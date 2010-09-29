package org.openmrs.module.mdrtb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSet;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbConceptMap;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.db.MdrtbDAO;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenUtils;
import org.openmrs.module.mdrtb.patient.MdrtbPatientWrapper;
import org.openmrs.module.mdrtb.patient.MdrtbPatientWrapperImpl;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;

public class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected MdrtbDAO dao;
	
	private static MdrtbFactory mdrtbFactory;
	
	private MdrtbConceptMap conceptMap = new MdrtbConceptMap(); // TODO: should this be a bean?
	
	private List<MdrtbRegimenSuggestion> standardRegimens = null;
	
	private List<Locale> localeSetUsedInDB = null;
	
	
	// caches
	private Map<Integer,String> colorMapCache = null;
	
	private Map<Integer,String> locationToDisplayCodeCache = null;
	
	

	public void setMdrtbDAO(MdrtbDAO dao) {
		this.dao = dao;
	}
	
	public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)
	                                                                                                                    throws APIException {
		return dao.getMdrtbConceptNamesByNameList(nameList, removeDuplicates, loc);
	}
	
	public MdrtbFactory getMdrtbFactory() {
		if (mdrtbFactory == null) {
			this.setMdrtbFactory(MdrtbFactory.getInstance());
		}
		return mdrtbFactory;
	}
	
	public void setMdrtbFactory(MdrtbFactory newMdrtbFactory) {
		MdrtbServiceImpl.mdrtbFactory = newMdrtbFactory;
	}
	
	public List<MdrtbRegimenSuggestion> getStandardRegimens() {
		if(standardRegimens == null) {
			setStandardRegimens(MdrtbRegimenUtils.getMdrtbRegimenSuggestions());
		}
		
		return standardRegimens;
	}
	
	public void setStandardRegimens(List<MdrtbRegimenSuggestion> standardRegimens) {
		if(this.standardRegimens == null) {
			this.standardRegimens = new LinkedList<MdrtbRegimenSuggestion>();
		}
		
		this.standardRegimens.addAll(standardRegimens);
	}
	
	public List<Locale> getLocaleSetUsedInDB() {
		if(localeSetUsedInDB == null) {
			List<Locale> locales = new ArrayList<Locale>();
	        List<List<Object>> rows = Context.getAdministrationService().executeSQL("select distinct locale from concept_word", true);
	        
	        //get all used locales in ConceptWord table
	        for (List<Object> row:rows){
	            for (Object o : row){
	                String oTmp = (String) o;
	                if (oTmp != null && oTmp != "")
	                    locales.add(new Locale(oTmp));
	            }
	        }
	        
	        setLocaleSetUsedInDB(locales);
		}
		
		return localeSetUsedInDB;
	}
	
	public void setLocaleSetUsedInDB(List<Locale> localeSetUsedInDB) {
		if(this.localeSetUsedInDB == null) {
			this.localeSetUsedInDB = new LinkedList<Locale>();
		}
		
		this.localeSetUsedInDB.addAll(localeSetUsedInDB);
	}
	
	public List<Location> getAllMdrtrbLocations(boolean includeRetired) {
		return dao.getAllMdrtrbLocations(includeRetired);
	}
	
	public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales) {		
		return dao.getConceptWords(phrase, locales);
	}
	
	public Concept getConcept(String [] conceptMapping) {
		return conceptMap.lookup(conceptMapping);
	}
	
	public MdrtbPatientWrapper getMdrtbPatient(Integer patientId) {
		if(patientId == null) {
			log.error("Unable to create MdrtbPatient, patient is null");
			return null;
		}
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if(patient == null) {
			log.error("Unable to create MdrtbPatient, no Patient with patient ID " + patientId);
			return null;
		}
		else {
			return new MdrtbPatientWrapperImpl(patient);
		}
	}
    
	
	public Specimen createSpecimen(Patient patient) {
		// return null if the patient is null
		if(patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(patient);
	}
	
	public Specimen getSpecimen(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong type
		if(encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"))) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(encounter);
	}
	
	public List<Specimen> getSpecimens(Patient patient) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(specimenEncounterType);
		
		specimenEncounters = Context.getEncounterService().getEncounters(patient, null, null, null, null, specimenEncounterTypes, null, false);
		
		for(Encounter encounter : specimenEncounters) {
			specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDate, Date endDate) {
		
		if (startDate == null) {
			return null;
		}
		
		List<Specimen> specimens = getSpecimens(patient);
		List<Specimen> programSpecimens = new LinkedList<Specimen>();
		
		for (Specimen specimen : specimens) {
			if (endDate != null && specimen.getDateCollected().after(endDate)) {
				break;
			} else if (specimen.getDateCollected().after(startDate)) {
				programSpecimens.add(specimen);
			}
		}
		
		return programSpecimens;
	}
	 
	public void saveSpecimen(Specimen specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return;
		}
		
		// make sure getSpecimen returns the right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should an encounter)
		if(!(specimen.getSpecimen() instanceof Encounter)){
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
				
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter((Encounter) specimen.getSpecimen());
	}
	
	public void deleteSpecimen(Integer specimenId) {
		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		}
		else {
			Context.getEncounterService().voidEncounter(encounter, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		
		// the id must refer to a valid obs, which is a smear, culture, or dst construct
		if (obs == null || !(obs.getConcept().equals(this.getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))
				|| obs.getConcept().equals(this.getConcept(MdrtbConcepts.CULTURE_CONSTRUCT)) 
				|| obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_CONSTRUCT)) )) {
			throw new APIException ("Unable to delete specimen test: invalid test id " + testId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public Smear createSmear(Encounter encounter) {		
		// first, get the specimen
		Specimen specimen = getSpecimen(encounter);
		
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addSmear();
	}
	
	public Smear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new SmearImpl(obs);
	}

	public Smear getSmear(Integer obsId) {
		return getSmear(Context.getObsService().getObs(obsId));
	}
	
	public void saveSmear(Smear smear) {
		if (smear == null) {
			log.warn("Unable to save smear: smear object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(smear.getTest() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Culture createCulture(Encounter encounter) {		
		// first, get the specimen
		Specimen specimen = getSpecimen(encounter);
		
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addCulture();
	}
	
	public Culture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new CultureImpl(obs);
	}

	public Culture getCulture(Integer obsId) {
		return getCulture(Context.getObsService().getObs(obsId));
	}
	
	public void saveCulture(Culture culture) {
		if (culture == null) {
			log.warn("Unable to save culture: culture object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(culture.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) culture.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Dst createDst(Encounter encounter) {		
		// first, get the specimen
		Specimen specimen = getSpecimen(encounter);
		
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst();
	}
	
	public Dst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new DstImpl(obs);
	}

	public Dst getDst(Integer obsId) {
		return getDst(Context.getObsService().getObs(obsId));
	}
	
	public void saveDst(Dst dst) {
		if (dst == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(dst.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
		
	public void deleteDstResult(Integer dstResultId) {
		Obs obs = Context.getObsService().getObs(dstResultId);
		
		// the id must refer to a valid obs, which is a dst result
		if (obs == null || ! obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_RESULT)) ) {
			throw new APIException ("Unable to delete dst result: invalid dst result id " + dstResultId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(report.getScannedLabReport() instanceof Obs)) {
			throw new APIException("Not a valid scanned lab report implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), "voided by Mdr-tb module specimen tracking UI");
	}
	
	public void deleteScannedLabReport(Integer reportId) {
		Obs obs = Context.getObsService().getObs(reportId);
		
		// the id must refer to a valid obs, which is a scanned lab report
		if (obs == null || ! obs.getConcept().equals(this.getConcept(MdrtbConcepts.SCANNED_LAB_REPORT)) ) {
			throw new APIException ("Unable to delete scanned lab report: invalid report id " + reportId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
    public Program getMdrtbProgram() {
    	return Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
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
	
	public Collection<Concept> getPossibleDstResults() {
		List<Concept> results = new LinkedList<Concept>();
		results.add(this.getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		
		return results;
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
	
    public List<Concept> getPossibleDrugTypesToDisplay() {
    	
    	Concept tbDrugs = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS);
    	List<ConceptSet> drugSet = Context.getConceptService().getConceptSetsByConcept(tbDrugs);
    	
    	List<Concept> drugs = new LinkedList<Concept>();
    	
    	for (ConceptSet drug : drugSet) {
    		drugs.add(drug.getConcept());
    	}
    	
    	return drugs;
    	
    }
    
   
    
    public String getColorForConcept(Concept concept) {
    	if(concept == null) {
    		log.error("Cannot fetch color for null concept");
    		return "";
    	}
    	
    	// initialize the cache if need be
    	if(colorMapCache == null) {
    		colorMapCache = loadCache(Context.getAdministrationService().getGlobalProperty("mdrtb.colorMap"));
    	}
    	
    	String color = "";
    	
    	try {
    		color = colorMapCache.get(concept.getId());
    	}
    	catch(Exception e) {
    		log.error("Unable to get color for concept " + concept.getId());
    		color = "white";
    	}
    	
    	return color;
    }
	
    public void resetColorMapCache() {
    	colorMapCache = null;
    }
    
    // TODO: should this really run the display code through message.properties for localization (probably not, since locations are proper names?)
    public String getDisplayCodeForLocation(Location location) {
    	// initialize the cache if need be 
    	if(locationToDisplayCodeCache == null) {
    		locationToDisplayCodeCache = loadCache(Context.getAdministrationService().getGlobalProperty("mdrtb.locationToDisplayCodeMap"));
    	}
    	
    	return locationToDisplayCodeCache.get(location.getId());
    }
    
    
	/**
	 * Utility functions
	 */
	
    
    private Map<Integer,String> loadCache(String mapAsString) {
    	Map<Integer,String> map = new HashMap<Integer,String>();
    	
    	if(StringUtils.isNotBlank(mapAsString)) {    	
    		for(String mapping : mapAsString.split("\\|")) {
    			String[] mappingFields = mapping.split(":");
    			map.put(Integer.valueOf(mappingFields[0]), mappingFields[1]);
    		}
    	}
    	else {
    		// TODO: make this error catching a little more elegant?
    		throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
    	}
    	
    	return map;
    }
}
