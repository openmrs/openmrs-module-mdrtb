package org.openmrs.module.mdrtb.api;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderContext;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.*;
import org.openmrs.module.mdrtb.api.dao.MdrtbDao;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.exception.InvalidImplementationException;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.*;
import org.openmrs.module.mdrtb.lab.*;
import org.openmrs.module.mdrtb.program.*;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.specimen.*;
import org.openmrs.module.mdrtb.specimen.custom.*;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	private static final String VOID_MESSAGE = "voided by MDRTB module";
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	MdrtbDao dao;
	
	UserService userService;
	
	/**
	 * Cache of resolved concept IDs, keyed by the lookup string that was used to find them. Only the
	 * ID is cached, never the Concept object itself: a Concept loaded in one Hibernate session must
	 * not be handed out in another. Cleared by {@link #resetConceptMapCache()}.
	 */
	private final Map<String, Integer> conceptIdCache = new ConcurrentHashMap<>();
	
	Map<Integer, String> colorMapCache = null;
	
	@Override
	public void onStartup() {
		log.info("MDRTB Service starting up");
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(MdrtbDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/***********************/
	/** UTILITY FUNCTIONS **/
	/***********************/
	public String getColorForConcept(Concept concept) {
		if (concept == null) {
			log.error("Cannot fetch color for null concept");
			return "";
		}
		// initialize the cache if need be
		if (colorMapCache == null) {
			String colorMap = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_COLOR_MAP);
			colorMapCache = loadCache(colorMap);
		}
		String color = "";
		try {
			color = colorMapCache.get(concept.getId());
		}
		catch (Exception e) {
			log.error("Unable to get color for concept " + concept.getId());
			color = "white";
		}
		return color;
	}
	
	public void resetConceptMapCache() {
		conceptIdCache.clear();
	}
	
	public void resetColorMapCache() {
		this.colorMapCache = null;
	}
	
	private static Set<ProgramWorkflowState> getPossibleMdrtbWorkflowStates(Concept workflowConcept) {
		// get the mdrtb program via the name listed in global properties
		Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME));
		
		// get the workflow via the concept name
		for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
			if (workflow.getConcept().equals(workflowConcept)) {
				return workflow.getStates(false);
			}
		}
		return null;
	}
	
	/**
	 * Get the mdrtb program via the name listed in global properties
	 */
	private static Set<ProgramWorkflowState> getPossibleTbWorkflowStates(Concept workflowConcept) {
		// get the mdrtb program via the name listed in global properties
		String programName = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_PROGRAM_NAME);
		List<Program> allPrograms = Context.getProgramWorkflowService().getAllPrograms();
		for (Program program : allPrograms) {
			if (program.getName().equalsIgnoreCase(programName)) {
				for (ProgramWorkflow workflow : program.getAllWorkflows()) {
					if (workflow.getConcept().equals(workflowConcept)) {
						return workflow.getStates(false);
					}
				}
			}
		}
		// get the workflow via the concept name
		return null;
	}
	
	private Map<Integer, String> loadCache(String mapAsString) {
		Map<Integer, String> map = new HashMap<>();

		if (StringUtils.isNotBlank(mapAsString)) {
			for (String mapping : mapAsString.split("\\|")) {
				String[] mappingFields = mapping.split(":");

				Integer conceptId = null;

				// if this is a mapping code, need to convert it to the concept id
				if (!MdrtbUtil.isInteger(mappingFields[0])) {
					Concept concept = getConcept(mappingFields[0]);
					if (concept != null) {
						conceptId = concept.getConceptId();
					} else {
						throw new MdrtbAPIException("Invalid concept mapping value in the the colorMap global property.");
					}
				}
				// otherwise, assume this is a concept id
				else {
					conceptId = Integer.valueOf(mappingFields[0]);
				}

				map.put(conceptId, mappingFields[1]);
			}
		} else {
			throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
		}

		return map;
	}
	
	/**
	 * Resolves a concept from a lookup string. The lookup may be an MDR-TB concept mapping (see
	 * {@link MdrtbConcepts}), a concept UUID, or a concept name.
	 * <p>
	 * Results are cached per lookup string, so repeated calls do not hit the database. Call
	 * {@link #resetConceptMapCache()} after changing concept metadata.
	 *
	 * @param lookup mapping, UUID or name of the concept
	 * @return the matching concept, or null if nothing could be resolved (a warning is logged)
	 */
	public Concept getConcept(String lookup) {
		if (StringUtils.isBlank(lookup)) {
			return null;
		}
		Concept concept = null;
		Integer cachedId = conceptIdCache.get(lookup);
		if (cachedId != null) {
			// Re-fetch through the service so the Concept always belongs to the current session
			concept = Context.getConceptService().getConcept(cachedId);
			if (concept == null) {
				// The concept was deleted since it was cached; resolve it again
				conceptIdCache.remove(lookup);
			}
		}
		if (concept == null) {
			concept = resolveConcept(lookup);
			if (concept == null) {
				log.warn("Concept: " + lookup + " was not found!");
				return null;
			}
			conceptIdCache.put(lookup, concept.getConceptId());
		}
		// Initialize concept (the obsolete, dirty way)
		concept.getDatatype().getHl7Abbreviation();
		for (ConceptName cns : concept.getNames()) {
			Collection<ConceptNameTag> tags = cns.getTags();
			for (ConceptNameTag cnTag : tags) {
				cnTag.getTag();
			}
		}
		Collection<ConceptAnswer> cas = concept.getAnswers();
		if (cas != null) {
			for (ConceptAnswer ca : cas) {
				Collection<ConceptName> cnsTmp = ca.getAnswerConcept().getNames();
				for (ConceptName cn : cnsTmp) {
					Collection<ConceptNameTag> tags = cn.getTags();
					for (ConceptNameTag cnTag : tags) {
						cnTag.getTag();
					}
				}
			}
		}
		return concept;
	}
	
	/**
	 * Tries each resolution strategy in turn. Order matters: the more precise the strategy, the
	 * earlier it runs. Nothing here caches - {@link #getConcept(String)} owns the cache.
	 */
	private Concept resolveConcept(String lookup) {
		// MDR-TB module's own concept mappings
		Concept concept = findByMdrtbMapping(lookup);
		if (concept != null) {
			return concept;
		}
		// By UUID
		if (lookup.matches(MdrtbConstants.UUID_REGEX)) {
			concept = findByUuid(lookup);
			if (concept != null) {
				return concept;
			}
		}
		// Exact name
		List<Concept> namedMatches = getConceptsByNameQuietly(lookup);
		concept = findByName(lookup, namedMatches);
		if (concept != null) {
			return concept;
		}
		// Last resort, and only when nothing at all matched by name: partial name match
		if (namedMatches.isEmpty()) {
			return findByPartialName(lookup);
		}
		return null;
	}
	
	private List<Concept> getConceptsByNameQuietly(String lookup) {
		try {
			return Context.getConceptService().getConceptsByName(lookup, Locale.ENGLISH, false);
		}
		catch (Exception e) {
			log.debug("Name search failed for '" + lookup + "'", e);
			return Collections.emptyList();
		}
	}
	
	/**
	 * Finds a concept by name and then only accepts it if it also carries a concept mapping in the
	 * MDR-TB source ({@link MdrtbConstants#MDRTB_CONCEPT_MAPPING_CODE}) that points back at itself.
	 * This is the historic behaviour of MdrtbConcepts.lookup() and is kept for databases whose
	 * metadata was loaded with those mappings.
	 */
	private Concept findByMdrtbMapping(String lookup) {
		try {
			List<Concept> candidates = Context.getConceptService().getConceptsByName(lookup);
			Concept exactNameMatch = null;
			for (Concept c : candidates) {
				for (ConceptName cn : c.getNames()) {
					if (cn.getName().equalsIgnoreCase(lookup)) {
						exactNameMatch = c;
						break;
					}
				}
			}
			if (exactNameMatch != null) {
				candidates = Collections.singletonList(exactNameMatch);
			}
			for (Concept c : candidates) {
				List<Concept> mapped = Context.getConceptService().getConceptsByMapping(
				    String.valueOf(c.getConceptId()), MdrtbConstants.MDRTB_CONCEPT_MAPPING_CODE);
				for (Concept mapConcept : mapped) {
					if (mapConcept.getName(Context.getLocale()).equals(c.getName(Context.getLocale()))) {
						return c;
					}
				}
			}
		}
		catch (Exception e) {
			log.debug("No MDR-TB concept mapping found for '" + lookup + "'", e);
		}
		return null;
	}
	
	private Concept findByUuid(String uuid) {
		try {
			return Context.getConceptService().getConceptByUuid(uuid);
		}
		catch (Exception e) {
			log.debug("No concept found for uuid '" + uuid + "'", e);
			return null;
		}
	}
	
	/**
	 * Exact name match: first through the concept service, then through the English name search
	 * results, which must produce either a single result or an exact fully specified name.
	 */
	private Concept findByName(String lookup, List<Concept> namedMatches) {
		try {
			Concept concept = Context.getConceptService().getConceptByName(lookup);
			if (concept != null) {
				return concept;
			}
			// If there is exactly 1 object, then return it
			if (namedMatches.size() == 1) {
				return namedMatches.get(0);
			}
			// For more than 1 objects, match the exact name
			for (Concept c : namedMatches) {
				ConceptName fsn = c.getFullySpecifiedName(Locale.ENGLISH);
				if (fsn != null && fsn.getName().equalsIgnoreCase(lookup)) {
					return c;
				}
			}
		}
		catch (Exception e) {
			log.debug("No concept found by name '" + lookup + "'", e);
		}
		return null;
	}
	
	/**
	 * Fallback for names that are not stored exactly as written: matches any fully specified name
	 * that CONTAINS the lookup string. This can match the wrong concept, so every hit is logged at
	 * WARN level and the caller should treat it as a sign that the metadata needs fixing.
	 */
	private Concept findByPartialName(String lookup) {
		if (lookup.length() < 2) {
			log.warn("Partial name too short to fetch concept: " + lookup);
			return null;
		}
		// Refuse anything that could change the shape of the query below
		if (lookup.indexOf('\'') >= 0 || lookup.indexOf('%') >= 0 || lookup.indexOf('_') >= 0
		        || lookup.indexOf('\\') >= 0) {
			return null;
		}
		try {
			List<List<Object>> result = Context
			        .getAdministrationService()
			        .executeSQL(
			            "select c.uuid from concept c inner join concept_name cn on cn.concept_id = c.concept_id where c.retired = 0 and cn.voided = 0 and cn.concept_name_type = 'FULLY_SPECIFIED' and cn.name like '%"
			                    + lookup + "%' order by cn.concept_id", true);
			if (result.isEmpty()) {
				return null;
			}
			if (result.size() > 1) {
				log.warn("Concept '" + lookup + "' matched " + result.size()
				        + " concepts by partial name; using the first one. Fix the concept metadata for this lookup.");
			}
			String uuid = result.get(0).get(0).toString();
			if (!uuid.matches(MdrtbConstants.UUID_REGEX)) {
				return null;
			}
			Concept concept = findByUuid(uuid);
			if (concept != null) {
				log.warn("Concept '" + lookup + "' was resolved by PARTIAL name match to '"
				        + concept.getName(Locale.ENGLISH) + "' (id " + concept.getConceptId() + ")");
			}
			return concept;
		}
		catch (Exception e) {
			log.debug("No concept found by partial name '" + lookup + "'", e);
			return null;
		}
	}
	
	public Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion) {
		return this.getConcept(conceptQuestion).getAnswers();
	}
	
	/**
	 * @deprecated replace with Context.getProgramWorkflowService().getStateByUuid in all
	 *             implementations
	 */
	@Deprecated
	public PatientState getPatientState(Integer stateId) {
		if (stateId != null) {
			return (PatientState) dao.getSessionFactory().getCurrentSession()
			        .createQuery("from PatientState s where patientStateId = :psid").setInteger("psid", stateId)
			        .uniqueResult();
		}
		return null;
	}
	
	public Collection<Person> getProviders() {
		List<Provider> list = Context.getProviderService().getAllProviders();
		SortedSet<Person> people = new TreeSet<>();
		for (Provider provider : list) {
			people.add(provider.getPerson());
		}
		return people;
	}
	
	public List<Patient> getPatients(Collection<Integer> patientIds) {
		if (!patientIds.isEmpty()) {
			return patientIds.stream().map(id -> Context.getPatientService().getPatient(id)).collect(Collectors.toList());
		}
		return null;
	}
	
	public List<Patient> getAllPatientsWithRegimenForms() {
		ArrayList<Patient> pList = new ArrayList<>();
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = getEncounters(null, null, null, null, typeList);
		for (Encounter e : temp) {
			if (!pList.contains(e.getPatient())) {
				pList.add(e.getPatient());
			}
		}
		return pList;
	}
	
	public List<DrugOrder> getDrugOrders(Patient patient) {
		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
		List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
		List<DrugOrder> drugOrders = new ArrayList<>();
		for (Order order : orders) {
			if (order.isType(drugOrderType)) {
				drugOrders.add((DrugOrder) order);
			}
		}
		return null;
	}
	
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort cohort, Concept drugSet) {
		List<Concept> drugConcepts = null;
		if (drugSet != null) {
			List<ConceptSet> concepts = Context.getConceptService().getConceptSetsByConcept(drugSet);
			drugConcepts = new ArrayList<>();
			for (ConceptSet cs : concepts) {
				drugConcepts.add(cs.getConcept());
			}
		}

		Map<Integer, List<DrugOrder>> drugOrders = dao.getDrugOrders(cohort, drugConcepts);
		return drugOrders;
	}
	
	public DrugOrder saveDrugOrder(DrugOrder drugOrder) {
		OrderContext orderContext = new OrderContext();
		orderContext.setOrderType(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID));
		orderContext.setCareSetting(Context.getOrderService().getCareSetting(1)); // Outpatient care setting
		Context.getOrderService().saveOrder(drugOrder, orderContext);
		return drugOrder;
	}
	
	public List<Concept> getDrugsInSet(Concept concept) {
		List<Concept> drugs = new LinkedList<>();
		if (concept != null) {
			List<ConceptSet> drugSet = Context.getConceptService().getConceptSetsByConcept(concept);
			if (drugSet != null) {
				for (ConceptSet drug : drugSet) {
					drugs.add(drug.getConcept());
				}
			}
		}
		return drugs;
	}
	
	public List<Concept> getMdrtbDrugs() {
		return getDrugsInSet(getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS));
	}
	
	public List<Concept> getAntiretrovirals() {
		return getDrugsInSet(getConcept(MdrtbConcepts.ANTIRETROVIRALS));
	}
	
	public List<Concept> getAllDrugResistanceConcepts() {
		List<Concept> list = new ArrayList<>();
		list.add(getConcept(MdrtbConcepts.AMIKACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.BEDAQUILINE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CAPREOMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CIPROFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CLARITHROMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CLOFAZAMINE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CYCLOSERINE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.DELAMANID_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.ETHAMBUTOL_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.ETHIONAMIDE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.GATIFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.ISONIAZID_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.KANAMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.LEVOFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.LINEZOLID_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.MOXIFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.OFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.OTHER_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.P_AMINOSALICY_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.PROTHIONAMIDE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.PYRAZINAMIDE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.RIFABUTIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.STREPTOMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.TERIZIDONE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.THIOACETAZONE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.VIOMYCIN_RESISTANCE));
		return list;
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	                                                                                     Date startDate, Date endDate) {
		// Handle null location (i.e. assume all locations)
		if (locations == null) {
			locations = getEnrollmentLocations();
		}
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(),
				startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		programs.sort(new PatientProgramComparator());
		List<TbPatientProgram> tbPrograms = new LinkedList<>();
		TbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new TbPatientProgram(program);
			for (Location l : locations) {
				if (temp.getLocation() != null
						&& (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					tbPrograms.add(new TbPatientProgram(program));
					break;
				}
			}
		}
		return tbPrograms;
	}
	
	public Program getTbProgram() {
		List<Program> allPrograms = Context.getProgramWorkflowService().getAllPrograms();
		String programName = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_PROGRAM_NAME);
		for (Program program : allPrograms) {
			if (program.getName().equalsIgnoreCase(programName)) {
				return program;
			}
		}
		return null;
	}
	
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		} else if (patientProgramId == -1) {
			return null;
		} else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			if (program == null || !program.getProgram().equals(getTbProgram())) {
				throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program.");
			} else {
				return new TbPatientProgram(program);
			}
		}
	}
	
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient) {
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getTbProgram(),
				null, null, null, null, false);
		TbPatientProgram temp = null;
		// sort the programs so oldest is first and most recent is last
		programs.sort(new PatientProgramComparator());
		List<TbPatientProgram> tbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			// tbPrograms.add(new TbPatientProgram(program));
			temp = new TbPatientProgram(program);
			PatientIdentifier pid = getPatientMdrtbProgramIdentifier(new MdrtbPatientProgram(temp.getPatientProgram()));
			if (pid != null) {
				temp.setPatientIdentifier(pid);
			}
			tbPrograms.add(temp);
		}
		return tbPrograms;
	}
	
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<TbPatientProgram> programs = new LinkedList<>();
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if ((endDate == null || program.getDateEnrolled().before(endDate))
					&& (program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(
					startDate))) {
				programs.add(program);
			}
		}
		Collections.sort(programs);
		return programs;
	}
	
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient) {
		List<TbPatientProgram> programs = getTbPatientPrograms(patient);
		if (!programs.isEmpty()) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
				startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		programs.sort(new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms() {
		return getAllMdrtbPatientProgramsInDateRange(null, null);
	}
	
	//TODO: Redundant method. Remove it and use getAllMdrtbPatientProgramsInDateRange(Date, Date) only
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
				startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		programs.sort(new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	                                                                                           Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
				startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		programs.sort(new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		MdrtbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new MdrtbPatientProgram(program);

			for (Location l : locations) {
				if (temp.getLocation() != null
						&& (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					mdrtbPrograms.add(new MdrtbPatientProgram(program));
					break;
				}
			}
		}
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<MdrtbPatientProgram> programs = new LinkedList<>();

		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if ((endDate == null || program.getDateEnrolled().before(endDate))
					&& (program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(
					startDate))) {
				programs.add(program);
			}
		}
		Collections.sort(programs);
		return programs;
	}
	
	public MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date) {
		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
				return program;
			}
		}
		return null;
	}
	
	public MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		} else if (patientProgramId == -1) {
			return null;
		} else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			
			if (program == null || !program.getProgram().equals(getMdrtbProgram())) {
				return null;
				// throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			} else {
				return new MdrtbPatientProgram(program);
			}
		}
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient) {
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getMdrtbProgram(),
				null, null, null, null, false);
		// sort the programs so oldest is first and most recent is last
		programs.sort(new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		return mdrtbPrograms;
	}
	
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient) {
		List<MdrtbPatientProgram> programs = getMdrtbPatientPrograms(patient);
		if (!programs.isEmpty()) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept) {
        return TbUtil.getProgramWorkflowState(programWorkflowStateConcept);
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse() {
		return getPossibleMdrtbWorkflowStates(getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
		return getPossibleMdrtbWorkflowStates(getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT));
	}
	
	public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes() {
		return getPossibleTbWorkflowStates(getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME));
	}
	
	public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes() {
		return getPossibleMdrtbWorkflowStates(getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME));
	}
	
	public PatientIdentifier getPatientMdrtbProgramIdentifier(MdrtbPatientProgram mpp) {
		return getPatientProgramIdentifier(mpp.getPatientProgram());
	}
	
	public PatientIdentifier getPatientProgramIdentifier(PatientProgram pp) {
		Concept concept = pp.getProgram().getConcept();
		String idType = null;
		if (concept.equals(getConcept(MdrtbConcepts.MDR_TB_PROGRAM))) {
			idType = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE);
		} else if (concept.equals(getConcept(MdrtbConcepts.DOTS_PROGRAM))) {
			idType = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE);
		}
		List<PatientIdentifier> identifiers = pp.getPatient().getPatientIdentifiers(
		    Context.getPatientService().getPatientIdentifierTypeByName(idType));
		if (!identifiers.isEmpty()) {
			// If there are multiple identifiers, then look for the one that's closest to the patient program
			PatientIdentifier match = identifiers.get(0);
			long minDiff = Long.MAX_VALUE;
			for (PatientIdentifier identifier : identifiers) {
				long diff = Math.abs(identifier.getDateCreated().getTime() - pp.getDateEnrolled().getTime());
				if (diff < minDiff) {
					match = identifier;
					minDiff = diff;
				}
			}
			return match;
		}
		return pp.getPatient().getPatientIdentifier();
	}
	
	public Program getMdrtbProgram() {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME);
		return Context.getProgramWorkflowService().getProgramByName(globalProperty);
	}
	
	public ProgramWorkflow getProgramWorkflow(Program program, Integer conceptId) {
		for (ProgramWorkflow programWorkflow : program.getAllWorkflows()) {
			if (programWorkflow.getConcept().getConceptId().equals(conceptId)) {
				return programWorkflow;
			}
		}
		return null;
	}
	
	public ProgramWorkflowState getProgramWorkflowState(ProgramWorkflow programWorkflow, Integer conceptId)
	        throws APIException {
		for (ProgramWorkflowState s : programWorkflow.getStates()) {
			if (s.getConcept().getConceptId().equals(conceptId)) {
				return s;
			}
		}
		return null;
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups() {
		return getPossibleTbWorkflowStates(getConcept(MdrtbConcepts.PATIENT_GROUP));
	}
	
	public Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse() {
		return getPossibleTbWorkflowStates(getConcept(MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE));
	}
	
	// TODO: Urgent! No new cultures are being created without this method. How to create new cultures after upgrading to Lab module?
	public Culture createCulture(Specimen specimen, Culture culture) {
		/*
		Culture newCulture = specimen.addCulture();
		newCulture.copyMembersFrom(culture);
		return newCulture;
		*/
		return null;
	}
	
	public void saveCulture(Culture culture) {
		if (culture == null) {
			log.warn("Unable to save culture: culture object is null");
			return;
		}
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(culture.getTest() instanceof Obs)) {
			throw new InvalidImplementationException(Culture.class);
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) culture.getTest(), VOID_MESSAGE);
	}
	
	public Specimen createSpecimen(Patient patient) {
		// return null if the patient is null
		if (patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(patient);
	}
	
	public Specimen getSpecimen(Integer specimenId) {
		return getSpecimen(Context.getEncounterService().getEncounter(specimenId));
	}
	
	public Specimen getSpecimen(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong
		// type
		if (encounter == null || !encounter.getEncounterType().equals(MdrtbConstants.ET_SPECIMEN_COLLECTION)) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(encounter);
	}
	
	public List<Specimen> getSpecimens(Patient patient) {
		return getSpecimens(patient, null, null, null);
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDate, Date endDate) {
		return getSpecimens(patient, startDate, endDate, null);
	}
	
	public List<Specimen> getSpecimens(Patient patient, Integer programId) {
		List<Specimen> specimens = new LinkedList<>();
		List<Encounter> specimenEncounters = new LinkedList<>();

		// create the specific specimen encounter types
		List<EncounterType> specimenEncounterTypes = new LinkedList<>();
		specimenEncounterTypes.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		specimenEncounters = getEncountersByPatientAndTypes(patient, specimenEncounterTypes);
		Obs temp = null;
		for (Encounter encounter : specimenEncounters) {
			temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
			if (temp != null && temp.getValueNumeric() != null && temp.getValueNumeric().intValue() == programId)
				specimens.add(new SpecimenImpl(encounter));
		}

		Collections.sort(specimens);
		return specimens;
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected,
	                                   Location locationCollected) {
		List<Specimen> specimens = new LinkedList<>();
		List<Encounter> specimenEncounters = new LinkedList<>();

		// create the specific specimen encounter types
		List<EncounterType> specimenEncounterTypes = new LinkedList<>();
		specimenEncounterTypes.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);

		specimenEncounters = getEncounters(patient, locationCollected, startDateCollected, endDateCollected,
				specimenEncounterTypes);

		for (Encounter encounter : specimenEncounters) {
			specimens.add(new SpecimenImpl(encounter));
		}

		Collections.sort(specimens);
		return specimens;
	}
	
	public void saveSpecimen(Specimen specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return;
		}
		// make sure getSpecimen returns the right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should an encounter)
		if (!(specimen.getSpecimen() instanceof Encounter)) {
			throw new InvalidImplementationException(Specimen.class);
		}
		// We need the specimen encounters to potentially be viewable by a bacteriology
		// htmlform:
		Encounter enc = (Encounter) specimen.getSpecimen();
		String formIdWithWhichToViewEncounter = Context.getAdministrationService().getGlobalProperty(
		    MdrtbConstants.GP_MDRTB_FORM_ID_TO_ATTACH_TO_BACTERIOLOGY_ENTRY);
		try {
			if (formIdWithWhichToViewEncounter != null && !formIdWithWhichToViewEncounter.equals(""))
				enc.setForm(Context.getFormService().getForm(Integer.valueOf(formIdWithWhichToViewEncounter)));
		}
		catch (Exception ex) {
			log.error("Invalid formId found in global property.");
		}
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter(enc);
	}
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		if (obs == null
		        || !(obs.getConcept().equals(this.getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))
		                || obs.getConcept().equals(this.getConcept(MdrtbConcepts.CULTURE_CONSTRUCT)) || obs.getConcept()
		                .equals(this.getConcept(MdrtbConcepts.DST_CONSTRUCT)))) {
			throw new APIException("Unable to delete specimen test: invalid test id " + testId);
		} else {
			Context.getObsService().voidObs(obs, VOID_MESSAGE);
		}
	}
	
	public void deleteSpecimen(Integer specimenId) {
		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		} else {
			Context.getEncounterService().voidEncounter(encounter, VOID_MESSAGE);
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Encounter> getEncounters(Patient patient, Location location, Date start, Date end,
	        Collection<EncounterType> types) {
		return Context.getEncounterService().getEncounters(patient, location, start, end, null, types, null, null, null,
		    false);
	}
	
	public List<Encounter> getEncountersByPatientAndTypes(Patient patient, Collection<EncounterType> types) {
		return getEncounters(patient, null, null, null, types);
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate) {
		return dao.getEncountersByEncounterTypes(encounterTypeNames, startDate, endDate);
	}
	
	public List<Encounter> getEncountersWithNoProgram(EncounterType encounterType, Patient patient) {
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(encounterType);
		ArrayList<Encounter> encs = new ArrayList<>();
		List<Encounter> all = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : all) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e) == null) {
				encs.add(e);
			}
		}
		return encs;
	}
	
	public void addProgramIdToEncounter(Integer encounterId, Integer programId) {
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(programId);
		Encounter e = Context.getEncounterService().getEncounter(encounterId);
		Obs idObs = new Obs(pp.getPatient(), getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e.getEncounterDatetime(),
		        e.getLocation());
		idObs.setEncounter(e);
		idObs.setObsDatetime(e.getEncounterDatetime());
		idObs.setLocation(e.getLocation());
		idObs.setValueNumeric(programId.doubleValue());
		e.addObs(idObs);
		Context.getEncounterService().saveEncounter(e);
	}
	
	// TODO: There's no patient program ID in the patient_program table any more
	// FIXME: Removing this might cause bugs. Investigate
	@Deprecated
	public void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId) {
		//		String query = "UPDATE patient_program SET patient_identifier_id= " + patientIdenifierId
		//		        + " WHERE patient_program_id=" + patientProgramId + ";";
		//		Context.getAdministrationService().executeSQL(query, false);
	}
	
	public List<Encounter> getTbEncounters(Patient patient) {
		return getEncounters(patient, null, null, null, TbUtil.getTbEncounterTypes());
	}
	
	public List<Encounter> getMdrtbEncounters(Patient patient) {
		return getEncounters(patient, null, null, null, MdrtbUtil.getMdrtbEncounterTypes());
	}
	
	public Xpert getXpert(Integer obsId) {
		Obs obs = Context.getObsService().getObs(obsId);
		LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
		LabTest xpert = Context.getService(LabTestService.class).getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new XpertImpl(xpert);
	}
	
	public Smear getSmear(Obs obs) {
		LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
		LabTest smear = Context.getService(LabTestService.class).getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new SmearImpl(smear);
	}
	
	public void saveXpert(Xpert xpert) {
		if (xpert == null) {
			log.warn("Unable to save xpert: xpert object is null");
			return;
		}
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(xpert.getTest() instanceof Obs)) {
			throw new InvalidImplementationException(Xpert.class);
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) xpert.getTest(), VOID_MESSAGE);
	}
	
	public HAIN getHAIN(Obs obs) {
		LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
		LabTest hain = Context.getService(LabTestService.class).getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new HAINImpl(hain);
	}
	
	public void saveHAIN(HAIN hain) {
		if (hain == null) {
			log.warn("Unable to save hain: hain object is null");
			return;
		}
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(hain.getTest() instanceof Obs)) {
			throw new InvalidImplementationException(HAIN.class);
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain.getTest(), VOID_MESSAGE);
	}
	
	public HAIN2 getHAIN2(Obs obs) {
		LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
		LabTest hain = Context.getService(LabTestService.class).getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new HAIN2Impl(hain);
	}
	
	public void saveHAIN2(HAIN2 hain2) {
		if (hain2 == null) {
			log.warn("Unable to save hain: hain object is null");
			return;
		}
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		
		if (!(hain2.getTest() instanceof Obs)) {
			throw new InvalidImplementationException(HAIN2.class);
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain2.getTest(), VOID_MESSAGE);
	}
	
	// TODO: Urgent! No new DSTs are being created without this method. How to create new cultures after upgrading to Lab module?
	public Dst createDst(Specimen specimen, Dst dst) {
		/*
		Dst newDst = specimen.addDst();
		newDst.copyMembersFrom(dst);
		return newDst;
		*/
		return null;
	}
	
	public void saveDst(Dst dst) {
		if (dst == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(dst.getTest() instanceof Obs)) {
			throw new InvalidImplementationException(Dst.class);
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), VOID_MESSAGE);
	}
	
	/***************/
	/** LOCATIONS **/
	/***************/
	//TODO: Move all these location-specific methods to a spearte LocationService
	public Location getLocation(Integer regionId, Integer districtId, Integer facilityId) {
		if (regionId == null || districtId == null)
			return null;
		Region o = getRegion(regionId);
		District d = getDistrict(districtId);
		Facility f = null;
		if (facilityId != null) {
			f = getFacility(facilityId);
		}
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			boolean regionFlag = loc.getStateProvince().equalsIgnoreCase(o.getName());
			if (!regionFlag) {
				continue;
			}
			boolean districtFlag = loc.getCountyDistrict().equalsIgnoreCase(d.getName());
			if (!districtFlag) {
				continue;
			}
			boolean stateFlag = f == null || loc.getAddress4().equalsIgnoreCase(f.getName());
			if (districtFlag && stateFlag) {
				return loc;
			}
		}
		return null;
	}
	
	public List<Location> getLocations(Region region, District district, Facility facility) {
		if (region == null && district == null && facility == null) {
			return null;
		}
		List<Location> list = new ArrayList<>();
		if (facility != null) {
			Location facilityLocation = Context.getLocationService().getLocation(facility.getId());
			list.add(facilityLocation);
			list.addAll(facilityLocation.getChildLocations());
			return list;
		}
		if (district != null) {
			Location districtLocation = Context.getLocationService().getLocation(district.getId());
			list.add(districtLocation);
			list.addAll(districtLocation.getChildLocations());
			// Also add all children of this district
			for (Location child : districtLocation.getChildLocations()) {
				list.addAll(child.getChildLocations());
			}
			return list;
		}
		Location regionLocation = Context.getLocationService().getLocation(region.getId());
		list.add(regionLocation);
		list.addAll(regionLocation.getChildLocations());
		// Add children (districts)
		for (Location child : regionLocation.getChildLocations()) {
			list.addAll(child.getChildLocations());
			// Further add grand children (Facilities)
			for (Location grandChild : child.getChildLocations()) {
				list.addAll(grandChild.getChildLocations());
			}
		}
		return list;
	}
	
	@Deprecated
	public List<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId) {
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		List<Location> list = getLocations(region, district, facility);
		return list;
	}
	
	public List<String> getCountries() {
		List<String> countries = new ArrayList<>();
		for (String s : Locale.getISOCountries()) {
			Locale crunchifyObj = new Locale("", s);
			countries.add(crunchifyObj.getDisplayCountry());
		}
		return countries;
	}
	
	public List<Region> getRegions() {
		List<Region> oblasts = new ArrayList<>();
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.REGION);
		for (BaseLocation baseLocation : list) {
			oblasts.add(new Region(baseLocation));
		}
		return oblasts;
	}
	
	public Region getRegion(Integer regionId) {
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.REGION);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getId().equals(regionId)) {
				return new Region(baseLocation);
			}
		}
		return null;
	}
	
	public List<Location> getLocationsFromRegion(Region region) {
		List<Location> locationList = new ArrayList<>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getStateProvince() != null) {
				if (loc.getStateProvince().equals(region.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<Facility> getFacilities() {
		List<Facility> facilities = new ArrayList<>();
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.FACILITY);
		for (BaseLocation baseLocation : list) {
			facilities.add(new Facility(baseLocation));
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities() {
		List<Facility> facilityList = getFacilities();
		//TODO: Use location tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		List<Facility> labs = new ArrayList<>();
		if (labIds != null) {
			for (String id : labIds) {
				try {
					for (Facility f : facilityList) {
						if (f.getId().equals(Integer.parseInt(id))) {
							labs.add(f);
						}
					}
				}
				catch (Exception ignored) {}
			}
		}
		return facilityList;
	}
	
	public List<Facility> getFacilitiesByParent(Integer parentId) {
		List<Facility> facilities = new ArrayList<>();
		Location parent = Context.getLocationService().getLocation(parentId);
		Set<Location> childLocations = parent.getChildLocations();
		for (Location location : childLocations) {
			facilities.add(new Facility(new BaseLocation(location, LocationHierarchy.FACILITY)));
		}
		return facilities;
	}
	
	public List<Facility> getFacilitiesByTag(String tag) {
		LocationTag locationTag = Context.getLocationService().getLocationTagByName(tag);
		List<Location> locations = Context.getLocationService().getLocationsByTag(locationTag);
		List<Facility> facilities = new ArrayList<>();
		for (Location location : locations) {
			facilities.add(new Facility(new BaseLocation(location, LocationHierarchy.FACILITY)));
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities(Integer parentId) {
		List<Facility> facilities = getFacilitiesByParent(parentId);
		Set<Facility> filtered = new HashSet<>();
		//TODO: Use location tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		for (String id : labIds) {
			if (!StringUtils.isNumeric(id)) {
				continue;
			}
			for (Facility facility : facilities) {
				if (facility.getId().equals(Integer.parseInt(id))) {
					filtered.add(facility);
				}
			}
		}
		return new ArrayList<>(filtered);
	}
	
	public Facility getFacility(Integer facilityId) {
		List<Facility> facilities = getFacilities();
		for (Facility facility : facilities) {
			if (facility.getId().equals(facilityId)) {
				return facility;
			}
		}
		return null;
	}
	
	public List<District> getDistrictsByParent(Integer parentId) {
		List<District> districts = new ArrayList<>();
		Location parent = Context.getLocationService().getLocation(parentId);
		Set<Location> childLocations = parent.getChildLocations();
		for (Location location : childLocations) {
			districts.add(new District(new BaseLocation(location, LocationHierarchy.DISTRICT)));
		}
		return districts;
	}
	
	public List<District> getRegDistricts(Integer parentId) {
		List<District> districts = getDistrictsByParent(parentId);
		Set<District> filtered = new HashSet<>();
		//TODO: Use location tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		for (String id : labIds) {
			if (!StringUtils.isNumeric(id)) {
				continue;
			}
			for (District district : districts) {
				if (district.getId().equals(Integer.parseInt(id))) {
					filtered.add(district);
				}
			}
		}
		return new ArrayList<>(filtered);
	}
	
	public District getDistrict(Integer districtId) {
		Location location = Context.getLocationService().getLocation(districtId);
		return new District(new BaseLocation(location, LocationHierarchy.DISTRICT));
	}
	
	public District getDistrict(String districtName) {
		List<District> list = getDistricts();
		for (District district : list) {
			if (district.getName().equalsIgnoreCase(districtName)) {
				return district;
			}
		}
		return null;
	}
	
	public List<District> getDistricts() {
		List<District> districts = new ArrayList<>();
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.DISTRICT);
		for (BaseLocation baseLocation : list) {
			districts.add(new District(baseLocation));
		}
		return districts;
	}
	
	public List<District> getRegDistricts() {
		List<District> districts = getDistricts();
		Set<District> filtered = new HashSet<>();
		//TODO: Use tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		for (String id : labIds) {
			if (!StringUtils.isNumeric(id)) {
				continue;
			}
			for (District district : districts) {
				if (district.getId().equals(Integer.parseInt(id))) {
					filtered.add(district);
				}
			}
		}
		return new ArrayList<>(filtered);
	}
	
	public List<Location> getLocationsFromDistrict(District district) {
		List<Location> locationList = new ArrayList<>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getCountyDistrict() != null) {
				if (loc.getCountyDistrict().equals(district.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<Location> getEnrollmentLocations() {
		LocationTag enrollmentTag = Context.getLocationService().getLocationTagByName(
		    MdrtbConstants.ENROLLMENT_LOCATION_TAG_NAME);
		List<Location> list = Context.getLocationService().getLocationsByTag(enrollmentTag);
		return list;
	}
	
	public List<Location> getCultureLocations() {
		LocationTag enrollmentTag = Context.getLocationService().getLocationTagByName(
		    MdrtbConstants.CULTURE_LOCATION_TAG_NAME);
		List<Location> list = Context.getLocationService().getLocationsByTag(enrollmentTag);
		return list;
	}
	
	/**
	 * This should ideally be a recursive function. But for Tajikistan, we're limiting only to 3
	 * levels
	 */
	public List<Location> getLocationsInHierarchy(Location parent, boolean includeRetired) {
		List<Location> locList = new ArrayList<>();
		Set<Location> childLocations = parent.getChildLocations(includeRetired);
		if (childLocations.isEmpty()) {
			locList.add(parent);
		}
		// If there are child locations, fetch their list
		else {
			for (Location child : childLocations) {
				locList.add(child);
				// If there are grand children, fetch them too
				if (!child.getChildLocations().isEmpty()) {
					Set<Location> grandChildren = child.getChildLocations();
					for (Location grandChild : grandChildren) {
						locList.add(grandChild);
					}
				}
			}
		}
		return locList;
	}
	
	/*****************/
	/** ANSWER SETS **/
	/*****************/
	public Collection<ConceptAnswer> getPossibleMtbResults() {
		return this.getConcept(MdrtbConcepts.MTB_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return this.getConcept(MdrtbConcepts.RIFAMPICIN_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return this.getConcept(MdrtbConcepts.ISONIAZID_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleFqResistanceResults() {
		return this.getConcept(MdrtbConcepts.FLUOROQUINOLONE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInjResistanceResults() {
		return this.getConcept(MdrtbConcepts.INJECTABLE_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return this.getConcept(MdrtbConcepts.XPERT_MTB_BURDEN).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return this.getConcept(MdrtbConcepts.SMEAR_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return this.getConcept(MdrtbConcepts.SMEAR_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return this.getConcept(MdrtbConcepts.CULTURE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return this.getConcept(MdrtbConcepts.CULTURE_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return this.getConcept(MdrtbConcepts.DST_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return this.getConcept(MdrtbConcepts.TYPE_OF_ORGANISM).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return this.getConcept(MdrtbConcepts.SAMPLE_SOURCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return this.getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleAnatomicalSites() {
		return this.getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRegimens() {
		return this.getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleHIVStatuses() {
		return this.getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleResistanceTypes() {
		return this.getConcept(MdrtbConcepts.RESISTANCE_TYPE).getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		// Enable the line below and change the return type to ConceptAnswer
		// return this.getConcept(MdrtbConcepts.DST_RESULT).getAnswers();
		List<Concept> results = new LinkedList<>();
		results.add(this.getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		return results;
	}
	
	/***********/
	/** FORMS **/
	/***********/
	/*
	 * The form-list methods below all follow the same shape. Lab tests come first because the lab module is the system of record for anything entered
	 * after the upgrade, and every form class already prefers its LabTest over the encounter obs field by field.
	 * Encounters follow as a fallback for historical specimens entered through the old forms, which the lab module may have.
	 *
	 * Three filters decide what is included, and they are repeated in each method:
	 * - Program. Lab tests filter on labtest_test.patient_program_id, the foreign key. Encounters filter on the PATIENT PROGRAM ID obs, which is all an encounter has.
	 * - Method. A lab test is included only if it carries an attribute in this method's group. One COMMON TEST lab test can hold Smear, Xpert, HAIN, HAIN2 and Culture results at once, and its shared attributes (date, specimen number, lab) read the same for all of them.
	 * - Construct. An encounter is included only if it holds the obs group for this method. getDstForms is the exception and checks no construct, as it always has.
	 */
	public List<SmearForm> getSmearForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<SmearForm> smears = new ArrayList<>();
		LabTestService labTestService = Context.getService(LabTestService.class);
		// Encounters already covered by a lab test, so the encounter pass does not add them twice
		Set<Integer> labTestEncounterIds = new HashSet<>();
		// Search in Lab Tests
		List<LabTest> labTests = labTestService.getLabTests(tpp.getPatient(), labTestService.getCommonTestType(), tpp);
		for (LabTest labTest : labTests) {
			if (!labTestService.hasAttributeInGroup(labTest, MdrtbConstants.SMEAR_TEST_GROUP)) {
				continue;
			}
			// An order may legitimately have no encounter, and then there is no form to build
			if (labTest.getOrder() == null || labTest.getOrder().getEncounter() == null) {
				continue;
			}
			Encounter e = labTest.getOrder().getEncounter();
			SmearForm sf = new SmearForm(e, labTest);
			sf.setPatient(tpp.getPatient());
			smears.add(sf);
			labTestEncounterIds.add(e.getEncounterId());
		}
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (labTestEncounterIds.contains(e.getEncounterId())) {
				continue;
			}
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.SMEAR_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				// getValueNumeric() can be null on a malformed obs; intValue() on it would throw
				if (temp != null && temp.getValueNumeric() != null
				        && temp.getValueNumeric().intValue() == patientProgramId) {
					SmearForm sf = new SmearForm(e);
					sf.setPatient(tpp.getPatient());
					smears.add(sf);
				}
			}
		}
		Collections.sort(smears);
		return smears;
	}
	
	public List<CultureForm> getCultureForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<CultureForm> cultures = new ArrayList<>();
		LabTestService labTestService = Context.getService(LabTestService.class);
		// Encounters already covered by a lab test, so the encounter pass does not add them twice
		Set<Integer> labTestEncounterIds = new HashSet<>();
		// Search in Lab Tests
		List<LabTest> labTests = labTestService.getLabTests(tpp.getPatient(), labTestService.getCommonTestType(), tpp);
		for (LabTest labTest : labTests) {
			if (!labTestService.hasAttributeInGroup(labTest, MdrtbConstants.CULTURE_TEST_GROUP)) {
				continue;
			}
			// An order may legitimately have no encounter, and then there is no form to build
			if (labTest.getOrder() == null || labTest.getOrder().getEncounter() == null) {
				continue;
			}
			Encounter e = labTest.getOrder().getEncounter();
			CultureForm sf = new CultureForm(e, labTest);
			sf.setPatient(tpp.getPatient());
			cultures.add(sf);
			labTestEncounterIds.add(e.getEncounterId());
		}
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (labTestEncounterIds.contains(e.getEncounterId())) {
				continue;
			}
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.CULTURE_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				// getValueNumeric() can be null on a malformed obs; intValue() on it would throw
				if (temp != null && temp.getValueNumeric() != null
				        && temp.getValueNumeric().intValue() == patientProgramId) {
					CultureForm sf = new CultureForm(e);
					sf.setPatient(tpp.getPatient());
					cultures.add(sf);
				}
			}
		}
		Collections.sort(cultures);
		return cultures;
	}
	
	public List<XpertForm> getXpertForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<XpertForm> xperts = new ArrayList<>();
		LabTestService labTestService = Context.getService(LabTestService.class);
		// Encounters already covered by a lab test, so the encounter pass does not add them twice
		Set<Integer> labTestEncounterIds = new HashSet<>();
		// Search in Lab Tests
		List<LabTest> labTests = labTestService.getLabTests(tpp.getPatient(), labTestService.getCommonTestType(), tpp);
		for (LabTest labTest : labTests) {
			if (!labTestService.hasAttributeInGroup(labTest, MdrtbConstants.XPERT_TEST_GROUP)) {
				continue;
			}
			// An order may legitimately have no encounter, and then there is no form to build
			if (labTest.getOrder() == null || labTest.getOrder().getEncounter() == null) {
				continue;
			}
			Encounter e = labTest.getOrder().getEncounter();
			XpertForm sf = new XpertForm(e, labTest);
			sf.setPatient(tpp.getPatient());
			xperts.add(sf);
			labTestEncounterIds.add(e.getEncounterId());
		}
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (labTestEncounterIds.contains(e.getEncounterId())) {
				continue;
			}
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.XPERT_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				// getValueNumeric() can be null on a malformed obs; intValue() on it would throw
				if (temp != null && temp.getValueNumeric() != null
				        && temp.getValueNumeric().intValue() == patientProgramId) {
					XpertForm sf = new XpertForm(e);
					sf.setPatient(tpp.getPatient());
					xperts.add(sf);
				}
			}
		}
		Collections.sort(xperts);
		return xperts;
	}
	
	public List<HAINForm> getHAINForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAINForm> hains = new ArrayList<>();
		LabTestService labTestService = Context.getService(LabTestService.class);
		// Encounters already covered by a lab test, so the encounter pass does not add them twice
		Set<Integer> labTestEncounterIds = new HashSet<>();
		// Search in Lab Tests
		List<LabTest> labTests = labTestService.getLabTests(tpp.getPatient(), labTestService.getCommonTestType(), tpp);
		for (LabTest labTest : labTests) {
			if (!labTestService.hasAttributeInGroup(labTest, MdrtbConstants.HAIN_TEST_GROUP)) {
				continue;
			}
			// An order may legitimately have no encounter, and then there is no form to build
			if (labTest.getOrder() == null || labTest.getOrder().getEncounter() == null) {
				continue;
			}
			Encounter e = labTest.getOrder().getEncounter();
			HAINForm sf = new HAINForm(e, labTest);
			sf.setPatient(tpp.getPatient());
			hains.add(sf);
			labTestEncounterIds.add(e.getEncounterId());
		}
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (labTestEncounterIds.contains(e.getEncounterId())) {
				continue;
			}
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.HAIN_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				// getValueNumeric() can be null on a malformed obs; intValue() on it would throw
				if (temp != null && temp.getValueNumeric() != null
				        && temp.getValueNumeric().intValue() == patientProgramId) {
					HAINForm sf = new HAINForm(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public List<HAIN2Form> getHAIN2Forms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAIN2Form> hains = new ArrayList<>();
		LabTestService labTestService = Context.getService(LabTestService.class);
		// Encounters already covered by a lab test, so the encounter pass does not add them twice
		Set<Integer> labTestEncounterIds = new HashSet<>();
		// Search in Lab Tests
		List<LabTest> labTests = labTestService.getLabTests(tpp.getPatient(), labTestService.getCommonTestType(), tpp);
		for (LabTest labTest : labTests) {
			if (!labTestService.hasAttributeInGroup(labTest, MdrtbConstants.HAIN_2_TEST_GROUP)) {
				continue;
			}
			// An order may legitimately have no encounter, and then there is no form to build
			if (labTest.getOrder() == null || labTest.getOrder().getEncounter() == null) {
				continue;
			}
			Encounter e = labTest.getOrder().getEncounter();
			HAIN2Form sf = new HAIN2Form(e, labTest);
			sf.setPatient(tpp.getPatient());
			hains.add(sf);
			labTestEncounterIds.add(e.getEncounterId());
		}
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (labTestEncounterIds.contains(e.getEncounterId())) {
				continue;
			}
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.HAIN2_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				// getValueNumeric() can be null on a malformed obs; intValue() on it would throw
				if (temp != null && temp.getValueNumeric() != null
				        && temp.getValueNumeric().intValue() == patientProgramId) {
					HAIN2Form sf = new HAIN2Form(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	/**
	 * Looks for an Encounter in the given program against which a Specimen sample should exist, and
	 * returns all such encounters.
	 * <p>
	 * Unlike the five above, a DST specimen encounter is identified by being a specimen collection
	 * encounter in this program, not by holding a particular obs group, so no construct is checked.
	 */
	public List<DSTForm> getDstForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DSTForm> dsts = new ArrayList<>();
		LabTestService labTestService = Context.getService(LabTestService.class);
		// Encounters already covered by a lab test, so the encounter pass does not add them twice
		Set<Integer> labTestEncounterIds = new HashSet<>();
		// Search in Lab Tests
		List<LabTest> labTests = labTestService.getLabTests(tpp.getPatient(), labTestService.getCommonTestType(), tpp);
		for (LabTest labTest : labTests) {
			if (!labTestService.hasAttributeInGroup(labTest, MdrtbConstants.DST_TEST_GROUP)) {
				continue;
			}
			// An order may legitimately have no encounter, and then there is no form to build
			if (labTest.getOrder() == null || labTest.getOrder().getEncounter() == null) {
				continue;
			}
			Encounter e = labTest.getOrder().getEncounter();
			DSTForm sf = new DSTForm(e, labTest);
			sf.setPatient(tpp.getPatient());
			dsts.add(sf);
			labTestEncounterIds.add(e.getEncounterId());
		}
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		// Search for all Specimen collection encounters containing this patientProgramId
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (labTestEncounterIds.contains(e.getEncounterId())) {
				continue;
			}
			Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			// Add the DST form to list
			// getValueNumeric() can be null on a malformed obs; intValue() on it would throw
			if (temp != null && temp.getValueNumeric() != null && temp.getValueNumeric().intValue() == patientProgramId) {
				DSTForm sf = new DSTForm(e);
				sf.setPatient(tpp.getPatient());
				dsts.add(sf);
			}
		}
		return dsts;
	}
	
	public List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DrugResistanceDuringTreatmentForm> drdts = new ArrayList<>();
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_DRUG_RESISTANCE_DURING_TREATMENT);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
				DrugResistanceDuringTreatmentForm drdt = new DrugResistanceDuringTreatmentForm(e);
				drdt.setPatient(tpp.getPatient());
				drdts.add(drdt);
			}
		}
		Collections.sort(drdts);
		return drdts;
	}
	
	public TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient) {
		TB03Form ret = null;
		Integer encounterId = null;
		EncounterType intakeType = MdrtbConstants.ET_TB03_TB_INTAKE;
		String query = "select encounter_id from encounter where location_id=" + location.getId()
		        + " AND encounter_datetime <= '" + encounterDate + "' AND patient_id=" + patient.getId()
		        + " AND encounter_type=" + intakeType.getId() + " AND voided=0 ORDER BY encounter_datetime DESC";
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		if (result != null && !result.isEmpty()) {
			List<Object> resp = result.get(0);
			if (resp != null) {
				encounterId = (Integer) (resp.get(0));
			}
			
		}
		if (encounterId != null)
			ret = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
		return ret;
	}
	
	public List<TB03Form> getTB03FormsForProgram(Patient patient, Integer patientProgramId) {
		ArrayList<TB03Form> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == patientProgramId) {
				forms.add(new TB03Form(e));
			}
		}
		return forms;
	}
	
	public List<Form89> getForm89FormsForProgram(Patient patient, Integer patientProgramId) {
		ArrayList<Form89> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == patientProgramId) {
				forms.add(new Form89(e));
			}
		}
		return forms;
	}
	
	public List<Form89> getForm89FormsFilledForPatientProgram(Patient patient, Location location, Integer patientProgramId,
	                                                          Integer year, Integer quarter, Integer month, Integer month2) {
		ArrayList<Form89> forms = new ArrayList<>();
		Map<String, Date> dateMap = null;
		if (year != null && (quarter != null || month != null))
			dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = null;
		Date endDate = null;
		if (dateMap != null) {
			startDate = (dateMap.get("startDate"));
			endDate = (dateMap.get("endDate"));
		}
		Concept ppid = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		List<Encounter> temp = getEncounters(patient, location, startDate, endDate, typeList);
		if (temp != null) {
			for (Encounter e : temp) {
				Obs ppObs = MdrtbUtil.getObsFromEncounter(ppid, e);
				if (ppObs != null) {
					if (ppObs.getValueNumeric() != null
							&& (ppObs.getValueNumeric().intValue() == patientProgramId)) {
						forms.add(new Form89(e));
					}
				}
			}
		}
		return forms;
	}
	
	public RegimenForm getPreviousRegimenForm(Patient patient, List<Location> locactions, Date beforeDate) {
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		ArrayList<RegimenForm> forms = new ArrayList<>();
		if (locactions == null || locactions.isEmpty()) {
			temp = getEncounters(patient, null, null, beforeDate, typeList);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		} else {
			for (Location l : locactions) {
				temp = getEncounters(patient, l, null, beforeDate, typeList);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
		}
		if (!forms.isEmpty()) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		} else
			return null;
	}
	
	public RegimenForm getCurrentRegimenForm(Patient patient, Date beforeDate) {
		// RegimenForm form = null;
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		Date currentDate = null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(beforeDate.getTime());
		gc.add(Calendar.DATE, 1);
		currentDate = gc.getTime();
		ArrayList<RegimenForm> forms = new ArrayList<>();
		temp = getEncounters(patient, null, null, currentDate, typeList);
		for (Encounter e : temp) {
			forms.add(new RegimenForm(e));
		}
		if (!forms.isEmpty()) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		} else
			return null;
	}
	
	public List<TB03uForm> getTB03uFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month, Integer month2) {
		ArrayList<TB03uForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03uForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TB03uForm(e));
				}
			}
		}
		return forms;
	}
	
	public List<Form89> getForm89FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month, Integer month2) {
		ArrayList<Form89> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new Form89(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new Form89(e));
				}
			}
		}
		return forms;
	}
	
	public List<TB03Form> getTB03FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month, Integer month2) {
		ArrayList<TB03Form> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.add(Calendar.SECOND, -1);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		List<Encounter> temp = null;
		if (locations != null && !locations.isEmpty()) {
			for (Location l : locations) {
				temp = getEncounters(null, l, start.getTime(), end.getTime(), typeList);
				for (Encounter e : temp) {
					forms.add(new TB03Form(e));
				}
			}
		} else {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03Form(e));
			}
		}
		return forms;
	}
	
	public List<TransferInForm> getTransferInFormsFilled(List<Location> locations, Integer year, Integer quarter,
	                                                     Integer month, Integer month2) {
		ArrayList<TransferInForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TRANSFER_IN);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TransferInForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TransferInForm(e));
				}
			}
		}
		return forms;
	}
	
	public List<TransferInForm> getTransferInFormsFilledForPatient(Patient patient) {
		ArrayList<TransferInForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TRANSFER_IN);
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			forms.add(new TransferInForm(e));
		}
		return forms;
	}
	
	public List<TransferOutForm> getTransferOutFormsFilled(List<Location> locations, Integer year, Integer quarter,
	                                                       Integer month, Integer month2) {
		ArrayList<TransferOutForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		EncounterType eType = MdrtbConstants.ET_TRANSFER_OUT;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TransferOutForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TransferOutForm(e));
				}
			}
		}
		return forms;
	}
	
	public List<TransferOutForm> getTransferOutFormsFilledForPatient(Patient patient) {
		ArrayList<TransferOutForm> forms = new ArrayList<>();
		EncounterType eType = MdrtbConstants.ET_TRANSFER_OUT;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp;
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			forms.add(new TransferOutForm(e));
		}
		return forms;
	}
	
	public List<EndOfTreatmentForm> getEndOfTreatmentFormsForProgram(Patient patient, Integer patientProgramId) {
		ArrayList<EndOfTreatmentForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_END_OF_TREATMENT);
		Concept idConcept = getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		List<Encounter> temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == patientProgramId) {
				forms.add(new EndOfTreatmentForm(e));
			}
		}
		return forms;
	}
	
	public List<EndOfTreatmentForm> getEndOfTreatmentFormsFilled(List<Location> locations, Integer year, Integer quarter,
	                                                             Integer month, Integer month2) {
		ArrayList<EndOfTreatmentForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_END_OF_TREATMENT);
		List<Encounter> temp;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new EndOfTreatmentForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new EndOfTreatmentForm(e));
				}
			}
		}
		// TODO (EoT report): confirm with the report owner which date defines "in the period" -
		// the encounter date (used here, same as Transfer Out) or the End of Treatment date field
		// once it exists. Change this before the report goes live if it is the latter.
		return forms;
	}
	
	public TB03uForm getTB03uFormForProgram(Patient p, Integer patientProgId) {
		TB03uForm form = null;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);

		List<Encounter> temp = null;
		Concept idConcept = getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);

		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == patientProgId) {
				form = new TB03uForm(e);
				break;
			}
		}
		return form;
	}
	
	public ArrayList<RegimenForm> getRegimenFormsForProgram(Patient patient, Integer patientProgId) {
		ArrayList<RegimenForm> forms = new ArrayList<>();
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == patientProgId) {
				forms.add(new RegimenForm(e));
			}
		}
		// Collections.reverse(forms);
		Collections.sort(forms);
		return forms;
	}
	
	public List<TB03uForm> getTB03uFormsWithTreatmentStartedDuring(List<Location> locations, Integer year, Integer quarter,
	                                                               Integer month, Integer month2) {
		ArrayList<TB03uForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		List<Encounter> temp = null;

		// CHECK
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, null, null, typeList);
			for (Encounter e : temp) {
				Obs o = MdrtbUtil.getObsFromEncounter(
						Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE), e);
				if (o != null && o.getValueDatetime() != null
						&& (o.getValueDatetime().equals(startDate) || o.getValueDatetime().after(startDate))
						&& (o.getValueDatetime().equals(endDate) || o.getValueDatetime().before(endDate))) {
					forms.add(new TB03uForm(e));
				}
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, null, null, typeList);
				for (Encounter e : temp) {
					Obs o = MdrtbUtil.getObsFromEncounter(
							Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE), e);
					if (o != null && o.getValueDatetime() != null
							&& (o.getValueDatetime().equals(startDate) || o.getValueDatetime().after(startDate))
							&& (o.getValueDatetime().equals(endDate) || o.getValueDatetime().before(endDate))) {
						forms.add(new TB03uForm(e));
					}
				}
			}
		}
		return forms;
	}
	
	public List<TB03uForm> getTB03uFormsForProgram(Patient patient, Integer mdrtbProgramId) {
		ArrayList<TB03uForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getService(MdrtbService.class).getEncounters(patient, null, null, null, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == mdrtbProgramId) {
				forms.add(new TB03uForm(e));
			}
		}
		return forms;
	}
	
	public List<AdverseEventsForm> getAEFormsForProgram(Patient p, Integer patientProgId) {
		ArrayList<AdverseEventsForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_ADVERSE_EVENT);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
					&& idObs.getValueNumeric().intValue() == patientProgId) {
				forms.add(new AdverseEventsForm(e));
			}
		}
		Collections.sort(forms);
		return forms;
	}
	
	public List<AdverseEventsForm> getAEFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month, Integer month2) {
		ArrayList<AdverseEventsForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_ADVERSE_EVENT);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new AdverseEventsForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new AdverseEventsForm(e));
				}
			}
		}
		Collections.sort(forms);
		return forms;
	}
	
	public List<RegimenForm> getRegimenFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month) {
		ArrayList<RegimenForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
		}
		Collections.sort(forms);
		return forms;
	}
	
	public void evict(Object obj) {
		dao.evict(obj);
	}
	
	public Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate,
	        Date toDate) {
		Cohort patientsInStates = Context.getService(CohortQueryService.class).getPatientsInStates(stateList, fromDate,
		    toDate);
		List<PatientProgram> list = Context.getProgramWorkflowService().getPatientPrograms(patientsInStates,
		    Collections.singletonList(program));
		return new Cohort(list);
	}
	
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath) {
		// first call the main Patient Service process death method
		Context.getPatientService().processDeath(patient, deathDate, causeOfDeath, null);
		// if the most recent MDR-TB program is open, we need to close it
		MdrtbPatientProgram program = getMostRecentMdrtbPatientProgram(patient);
		if (program != null && program.getActive()) {
			program.setDateCompleted(deathDate);
			program.setOutcome(getProgramWorkflowState(Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.DEATH)));
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
		// if the patient is hospitalized, we need to end the hospitalization
		if (program != null && program.getCurrentlyHospitalized()) {
			program.closeCurrentHospitalization(deathDate);
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
	}
	
	public Smear createSmear(Specimen specimen) {
		return null;
	}
	
	public Smear createSmear(Specimen specimen, Smear smear) {
		return null;
	}
	
	public void saveSmear(Smear smear) {
		// Not implemented
	}
	
	/*******************/
	/** SAVED REPORTS **/
	/*******************/
	public ReportData getReportData(Integer id) throws APIException {
		return dao.getReportData(id);
	}
	
	public ReportData getReportDataByUuid(String uuid) throws APIException {
		return dao.getReportDataByUuid(uuid);
	}
	
	public ReportData saveReportData(ReportData reportData) throws APIException {
		return dao.saveReportData(reportData);
	}
	
	public void voidReportData(ReportData reportData, String reason) throws APIException {
		if (!reportData.getVoided()) {
			reportData.setVoided(true);
			reportData.setVoidReason(reason);
			reportData.setDateVoided(new Date());
			reportData.setVoidedBy(Context.getAuthenticatedUser());
			dao.saveReportData(reportData);
		}
	}
	
	public void unvoidReportData(ReportData reportData) throws APIException {
		if (reportData.getVoided()) {
			reportData.setVoided(false);
			reportData.setVoidReason("Previously voided with reason:" + reportData.getVoidReason());
			reportData.setDateVoided(null);
			reportData.setVoidedBy(null);
			dao.saveReportData(reportData);
		}
	}
	
	public List<ReportData> searchReportData(Location region, Location district, Location facility, Integer year,
	        Integer quarter, Integer month, String reportName, ReportType reportType) {
		return dao.searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
	}
	
	public boolean getReportArchived(Integer region, Integer district, Integer facility, Integer year, Integer quarter,
	        Integer month, String name, ReportType reportType) {
		return dao.getReportArchived(region, district, facility, year, quarter, month, name, reportType);
	}
	
	public List<String> readTableData(Integer regionId, Integer districtId, Integer facilityId, Integer year,
	        Integer quarter, Integer month, String name, ReportType reportType) {
		return dao.getReportDataAsList(regionId, districtId, facilityId, year, quarter, month, name, reportType);
	}
	
	public List<List<Object>> getReportsWithoutData(ReportType reportType) {
		return dao.getReports(reportType.toString());
	}
	
	public void lockReport(ReportData reportData) {
		if (reportData.getReportStatus() == ReportStatus.UNLOCKED) {
			try {
				dao.saveReportData(reportData);
				saveReportData(reportData);
			}
			catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
	}
	
	public void unlockReport(ReportData reportData) {
		if (reportData.getVoided()) {
			throw new APIException("Cannot unlock a voided report");
		}
		reportData.setReportStatus(ReportStatus.UNLOCKED);
		dao.saveReportData(reportData);
	}
	
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save DST: DST object is null");
			return;
		}
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(report.getScannedLabReport() instanceof Obs)) {
			throw new InvalidImplementationException(ScannedLabReport.class);
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), VOID_MESSAGE);
	}
	
	public PatientSummary getPatientSummary(Patient patient) {
		if (patient == null) {
			return null;
		}
		// Preferred name if the patient has one, otherwise the first name.
		PersonName personName = patient.getPerson().getNames().stream()
				.filter(PersonName::getPreferred)
				.findFirst()
				.orElse(patient.getPerson().getNames().stream().findFirst().orElse(null));
		Set<PatientIdentifier> patientIdentifiers = patient.getIdentifiers();

		List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, null, null, null, null, null, false);
		List<Obs> allObs = Context.getObsService().getObservationsByPerson(patient);

		LabTestType commonTestType = Context.getService(LabTestService.class).getCommonTestType();
		List<LabTest> allLabTests = Context.getService(LabTestService.class).getLabTests(patient, commonTestType);
		if (allLabTests.isEmpty()) {
			allLabTests = new ArrayList<>();
		}

		List<PatientProgramSummary> programSummaries = new  ArrayList<>();
		for (PatientProgram patientProgram : patientPrograms) {
			Date start = patientProgram.getDateEnrolled();
			Date end = patientProgram.getDateCompleted(); // null while the program is still open

			List<Obs> programObs = new ArrayList<>();
			for (Obs obs : allObs) {
				if (isWithinProgramWindow(obs.getObsDatetime(), start, end)) {
					programObs.add(obs);
				}
			}
			List<LabTest> progamLabTests = new  ArrayList<>();
			for (LabTest labTest : allLabTests) {
				Date targetDate;
				if (labTest.getOrder() != null && labTest.getOrder().getEncounter() != null) {
					targetDate = labTest.getOrder().getEncounter().getEncounterDatetime();
				} else {
					targetDate = labTest.getDateCreated();
				}
				if(isWithinProgramWindow(targetDate, start, end)) {
					progamLabTests.add(labTest);
				}
			}
			programSummaries.add(new PatientProgramSummary(patientProgram, programObs, progamLabTests));
		}
        return new PatientSummary(patient, personName, patientIdentifiers, programSummaries);
	}
	
	/**
	 * Returns true if the date lies within start and end (with null check
	 */
	private boolean isWithinProgramWindow(Date date, Date start, Date end) {
		if (date == null) {
			return false;
		}
		if (start != null && date.before(start)) {
			return false;
		}
		return end == null || !date.after(end);
	}
}
