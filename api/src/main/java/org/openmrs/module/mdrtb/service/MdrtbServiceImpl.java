package org.openmrs.module.mdrtb.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.BaseLocation;
import org.openmrs.module.mdrtb.Country;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConceptMap;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.TbUtil;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.AEForm;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
import org.openmrs.module.mdrtb.form.custom.TransferOutForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO;
import org.openmrs.module.mdrtb.service.db.MdrtbDAO;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	private static final String UUID_REGEX = "^[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]+$";
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	protected HibernateMdrtbDAO dao;
	
	private MdrtbConceptMap conceptMap = new MdrtbConceptMap(); // TODO: should this be a bean?
	
	// caches
	private Map<Integer, String> colorMapCache = null;
	
	public void setDao(HibernateMdrtbDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see MdrtbService#getLocationsWithAnyProgramEnrollments()
	 */
	public List<Location> getLocationsWithAnyProgramEnrollments() {
		return dao.getLocationsWithAnyProgramEnrollments();
	}
	
	/**
	 * @see MdrtbService#getConcept(String)
	 */
	public Concept getConcept(String lookup) {
		if (ObjectUtil.notNull(lookup)) {
			// First try MDR-TB module's known concept mappings
			try {
				return conceptMap.lookup(lookup);
			}
			catch (Exception e) {}
			// Next try UUID
			if (lookup.length() == 36 && lookup.matches(UUID_REGEX)) {
				try {
					Concept c = Context.getConceptService().getConceptByUuid(lookup);
					if (c != null) {
						return c;
					}
				}
				catch (Exception e) {}
			}
			// Next try precise name
			try {
				Concept c = Context.getConceptService().getConceptByName(lookup);
				if (c != null) {
					return c;
				}
			}
			catch (Exception e) {}
		}
		log.warn("Concept: " + lookup + " was not found!");
		return null;
	}
	
	public void resetConceptMapCache() {
		this.conceptMap.resetCache();
	}
	
	public List<Encounter> getMdrtbEncounters(Patient patient) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, null,
		    MdrtbUtil.getMdrtbEncounterTypes(), null, false);
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms() {
		return getAllMdrtbPatientProgramsInDateRange(null, null);
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(), null,
		    endDate, startDate, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient) {
		
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getMdrtbProgram(),
		    null, null, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		
		return mdrtbPrograms;
	}
	
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient) {
		List<MdrtbPatientProgram> programs = getMdrtbPatientPrograms(patient);
		
		if (programs.size() > 0) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<MdrtbPatientProgram> programs = new LinkedList<MdrtbPatientProgram>();
		
		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if ((endDate == null || program.getDateEnrolled().before(endDate)) && (program.getDateCompleted() == null
			        || startDate == null || !program.getDateCompleted().before(startDate))) {
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
				// TODO: Figure out why this was throwing an exception before
				// throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			}
			else {
				return new MdrtbPatientProgram(program);
			}
		}
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
		if (encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"))) {
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
	
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected,
	        Location locationCollected) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(specimenEncounterType);
		
		specimenEncounters = Context.getEncounterService().getEncounters(patient, locationCollected, startDateCollected,
		    endDateCollected, null, specimenEncounterTypes, null, false);
		
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
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
		// We need the specimen encounters to potentially be viewable by a bacteriology
		// htmlform:
		Encounter enc = (Encounter) specimen.getSpecimen();
		String formIdWithWhichToViewEncounter = Context.getAdministrationService()
		        .getGlobalProperty("mdrtb.formIdToAttachToBacteriologyEntry");
		try {
			if (formIdWithWhichToViewEncounter != null && !formIdWithWhichToViewEncounter.equals(""))
				enc.setForm(Context.getFormService().getForm(Integer.valueOf(formIdWithWhichToViewEncounter)));
		}
		catch (Exception ex) {
			log.error("Invalid formId found in global property mdrtb.formIdToAttachToBacteriologyEntry");
		}
		
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter(enc);
	}
	
	public void deleteSpecimen(Integer specimenId) {
		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		} else {
			Context.getEncounterService().voidEncounter(encounter, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		
		// the id must refer to a valid obs, which is a smear, culture, or dst construct
		if (obs == null || !(obs.getConcept().equals(this.getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))
		        || obs.getConcept().equals(this.getConcept(MdrtbConcepts.CULTURE_CONSTRUCT))
		        || obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_CONSTRUCT)))) {
			throw new APIException("Unable to delete specimen test: invalid test id " + testId);
		} else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public Smear createSmear(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addSmear();
	}
	
	public Smear createSmear(Specimen specimen, Smear smear) {
		Smear newSmear = specimen.addSmear();
		newSmear.copyMembersFrom(smear);
		return newSmear;
	}
	
	public Smear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
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
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		
		if (!(smear.getTest() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Culture createCulture(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addCulture();
	}
	
	public Culture createCulture(Specimen specimen, Culture culture) {
		Culture newCulture = specimen.addCulture();
		newCulture.copyMembersFrom(culture);
		return newCulture;
	}
	
	public Culture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
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
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(culture.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) culture.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Dst createDst(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create dst: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst();
	}
	
	public Dst createDst(Specimen specimen, Dst dst) {
		Dst newDst = specimen.addDst();
		newDst.copyMembersFrom(dst);
		return newDst;
	}
	
	public Dst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
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
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(dst.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public void deleteDstResult(Integer dstResultId) {
		Obs obs = Context.getObsService().getObs(dstResultId);
		
		// the id must refer to a valid obs, which is a dst result
		if (obs == null || !obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_RESULT))) {
			throw new APIException("Unable to delete dst result: invalid dst result id " + dstResultId);
		} else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(report.getScannedLabReport() instanceof Obs)) {
			throw new APIException("Not a valid scanned lab report implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), "voided by Mdr-tb module specimen tracking UI");
	}
	
	public void deleteScannedLabReport(Integer reportId) {
		Obs obs = Context.getObsService().getObs(reportId);
		
		// the id must refer to a valid obs, which is a scanned lab report
		if (obs == null || !obs.getConcept().equals(this.getConcept(MdrtbConcepts.SCANNED_LAB_REPORT))) {
			throw new APIException("Unable to delete scanned lab report: invalid report id " + reportId);
		} else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath) {
		
		// first call the main Patient Service process death method
		Context.getPatientService().processDeath(patient, deathDate, causeOfDeath, null);
		
		// if the most recent MDR-TB program is open, we need to close it
		MdrtbPatientProgram program = getMostRecentMdrtbPatientProgram(patient);
		
		if (program != null && program.getActive()) {
			program.setDateCompleted(deathDate);
			program.setOutcome(
			    MdrtbUtil.getProgramWorkflowState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED)));
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
		
		// if the patient is hospitalized, we need to end the hospitalization
		if (program != null && program.getCurrentlyHospitalized()) {
			program.closeCurrentHospitalization(deathDate);
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
	}
	
	public Program getMdrtbProgram() {
		String globalProperty = Context.getAdministrationService().getGlobalProperty("mdrtb.program_name");
		return Context.getProgramWorkflowService().getProgramByName(globalProperty);
	}
	
	public Collection<Person> getProviders() {
		// TODO: this should be customizable, so that other installs can define there
		// own provider lists?
		// TODO: Use the Provider service instead
		Role provider = Context.getUserService().getRole("Provider");
		Collection<User> providers = Context.getUserService().getUsersByRole(provider);
		
		// add all the persons to a sorted set sorted by name
		SortedSet<Person> persons = new TreeSet<Person>(new PersonByNameComparator());
		
		for (User user : providers) {
			persons.add(user.getPerson());
		}
		
		return persons;
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
	
	public Collection<ConceptAnswer> getPossibleAnatomicalSites() {
		return this.getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
	}
	
	/**
	 * @return the List of Concepts that represent the Drugs within the passed Drug Set
	 */
	public List<Concept> getDrugsInSet(Concept concept) {
		List<Concept> drugs = new LinkedList<Concept>();
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
		return getDrugsInSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS));
	}
	
	public List<Concept> getAntiretrovirals() {
		return getDrugsInSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANTIRETROVIRALS));
	}
	
	public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes() {
		return getPossibleMdrtbWorkflowStates(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse() {
		return getPossibleMdrtbWorkflowStates(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
		return getPossibleMdrtbWorkflowStates(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT));
	}
	
	public String getColorForConcept(Concept concept) {
		if (concept == null) {
			log.error("Cannot fetch color for null concept");
			return "";
		}
		
		// initialize the cache if need be
		if (colorMapCache == null) {
			String colorMap = Context.getAdministrationService().getGlobalProperty("mdrtb.colorMap");
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
	
	public void resetColorMapCache() {
		this.colorMapCache = null;
	}
	
	/**
	 * Utility functions
	 */
	private Set<ProgramWorkflowState> getPossibleMdrtbWorkflowStates(Concept workflowConcept) {
		// get the mdrtb program via the name listed in global properties
		Program mdrtbProgram = Context.getProgramWorkflowService()
		        .getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
		
		// get the workflow via the concept name
		for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
			if (workflow.getConcept().equals(workflowConcept)) {
				return workflow.getStates(false);
			}
		}
		return null;
	}

	private Set<ProgramWorkflowState> getPossibleTbWorkflowStates(Concept workflowConcept) {
		// get the mdrtb program via the name listed in global properties
		String programName = Context.getAdministrationService().getGlobalProperty("dotsreports.program_name");
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
		Map<Integer, String> map = new HashMap<Integer, String>();
		
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
			// TODO: make this error catching a little more elegant?
			throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
		}
		
		return map;
	}
	
	public List<Specimen> getSpecimens(Patient patient, Integer programId) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(specimenEncounterType);
		
		specimenEncounters = Context.getEncounterService().getEncounters(patient, null, null, null, null,
		    specimenEncounterTypes, null, false);
		Obs temp = null;
		for (Encounter encounter : specimenEncounters) {
			temp = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
			if (temp != null && temp.getValueNumeric() != null && temp.getValueNumeric().intValue() == programId.intValue())
				specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
		
	}
	
	public Program getTbProgram() {
		List<Program> allPrograms = Context.getProgramWorkflowService().getAllPrograms();
		String programName = Context.getAdministrationService().getGlobalProperty("dotsreports.program_name");
		for (Program program : allPrograms) {
			if (program.getName().equalsIgnoreCase(programName)) {
				return program;
			}
		}
		return null;
	}
	
	public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes() {
		return getPossibleMdrtbWorkflowStates(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups() {
		return getPossibleTbWorkflowStates(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP));
	}
	
	public List<String> getAllRayonsTJK() {
		List<String> rayonList = null;
		
		return dao.getAllRayonsTJK();
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> tbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			tbPrograms.add(new MdrtbPatientProgram(program));
		}
		
		return tbPrograms;
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
			throw new APIException("Not a valid xpert implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) xpert.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Xpert createXpert(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addXpert();
	}
	
	public Xpert getXpert(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new XpertImpl(obs);
	}
	
	public Xpert getXpert(Integer obsId) {
		return getXpert(Context.getObsService().getObs(obsId));
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
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public HAIN createHAIN(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addHAIN();
	}
	
	public HAIN getHAIN(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new HAINImpl(obs);
	}
	
	public HAIN getHAIN(Integer obsId) {
		return getHAIN(Context.getObsService().getObs(obsId));
	}
	
	public HAIN2 getHAIN2(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new HAIN2Impl(obs);
	}
	
	public HAIN2 createHAIN2(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addHAIN2();
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
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain2.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public HAIN2 getHAIN2(Integer obsId) {
		return getHAIN2(Context.getObsService().getObs(obsId));
	}
	
	public Collection<ConceptAnswer> getPossibleMtbResults() {
		return this.getConcept(MdrtbConcepts.MTB_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return this.getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return this.getConcept(MdrtbConcepts.ISONIAZID_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleFqResistanceResults() {
		return this.getConcept(MdrtbConcepts.FQ_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInjResistanceResults() {
		return this.getConcept(MdrtbConcepts.INJ_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return this.getConcept(MdrtbConcepts.XPERT_MTB_BURDEN).getAnswers();
	}
	
	public Location getLocation(Integer oblastId, Integer districtId, Integer facilityId) {
		if (oblastId == null || districtId == null)
			return null;
		Region o = getOblast(oblastId);
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
			boolean stateFlag = f == null ? true : loc.getAddress4().equalsIgnoreCase(f.getName());
			if (regionFlag && districtFlag && stateFlag) {
				return loc;
			}
		}
		return null;
	}
	
	public List<Region> getOblasts() {
		List<Region> oblasts = new ArrayList<Region>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Region.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			oblasts.add(new Region(baseLocation));
		}
		return oblasts;
	}
	
	public List<Region> getOblasts(int parentId) {
		List<Region> oblasts = new ArrayList<Region>();
		BaseLocation parent = new District("", parentId, District.HIERARCHY_LEVEL);
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByParent(parent);
		for (BaseLocation baseLocation : list) {
			oblasts.add(new Region(baseLocation));
		}
		return oblasts;
	}
	
	public Region getOblast(Integer oblastId) {
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Region.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getId().equals(oblastId)) {
				return new Region(baseLocation);
			}
		}
		return null;
	}
	
	public List<Location> getLocationsFromOblastName(Region oblast) {
		List<Location> locationList = new ArrayList<Location>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getStateProvince() != null) {
				if (loc.getStateProvince().equals(oblast.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<Facility> getFacilities() {
		List<Facility> facilities = new ArrayList<Facility>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Facility.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			facilities.add(new Facility(baseLocation));
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities() {
		//TODO: horrible way to identify labs. Use location properties
		List<Facility> facilityList = getFacilities();
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty("mdrtb.lab_entry_ids");
		String labIds[] = labIdsProperty.split("\\|");
		List<Facility> labs = new ArrayList<Facility>();
		if (labIds != null) {
			for (String id : labIds) {
				try {
					for (Facility f : facilityList) {
						if (f.getId().equals(Integer.parseInt(id))) {
							labs.add(f);
						}
					}
				}
				catch (Exception e) {}
			}
		}
		return facilityList;
	}
	
	public List<Facility> getFacilities(int parentId) {
		List<Facility> facilities = new ArrayList<Facility>();
		BaseLocation parent = dao.getAddressHierarchyLocation(parentId);
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByParent(parent);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getLevelId().equals(Facility.HIERARCHY_LEVEL)) {
				facilities.add(new Facility(baseLocation));
			}
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities(int parentId) {
		List<Facility> facilities = getFacilities(parentId);
		Set<Facility> filtered = new HashSet<Facility>();
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty("mdrtb.lab_entry_ids");
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
		return new ArrayList<Facility>(filtered);
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
	
	public List<Location> getLocationsFromFacilityName(Facility facility) {
		List<Location> locationList = new ArrayList<Location>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getRegion() != null) {
				if (loc.getRegion().equals(facility.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<District> getDistricts(int parentId) {
		List<District> districts = new ArrayList<District>();
		BaseLocation parent = dao.getAddressHierarchyLocation(parentId);
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByParent(parent);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getLevelId().equals(District.HIERARCHY_LEVEL)) {
				districts.add(new District(baseLocation));
			}
		}
		return districts;
		
	}
	
	public List<District> getRegDistricts(int parentId) {
		List<District> districts = getDistricts(parentId);
		Set<District> filtered = new HashSet<District>();
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty("mdrtb.lab_entry_ids");
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
		return new ArrayList<District>(filtered);
	}
	
	public District getDistrict(Integer districtId) {
		return new District(dao.getAddressHierarchyLocation(districtId));
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
		List<District> districts = new ArrayList<District>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(District.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			districts.add(new District(baseLocation));
		}
		return districts;
	}
	
	public List<District> getRegDistricts() {
		List<District> districts = getDistricts();
		Set<District> filtered = new HashSet<District>();
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty("mdrtb.lab_entry_ids");
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
		return new ArrayList<District>(filtered);
	}
	
	public List<Location> getLocationsFromDistrictName(District district) {
		List<Location> locationList = new ArrayList<Location>();
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
		// TODO: Deal with these hard-coded strings
		List<Location> allLocations = Context.getLocationService().getAllLocations();
		List<Location> enrollmentLocations = new ArrayList<Location>();
		for (Location loc : allLocations) {
			String locName = loc.getName();
			if (!(locName.length() >= 2 && MdrtbUtil.areRussianStringsEqual(locName.substring(0, 2), "БЛ")
			        && !(locName.length() >= 2 && MdrtbUtil.areRussianStringsEqual(locName.substring(0, 2), "ГЦ"))
			        && !(locName.length() >= 4 && MdrtbUtil.areRussianStringsEqual(locName.substring(0, 4), "ОЦБТ"))
			        && !MdrtbUtil.areRussianStringsEqual(locName, "РЦЗНТ Душанбе")
			        && !MdrtbUtil.areRussianStringsEqual(locName, "НРЛ")
			        && !MdrtbUtil.areRussianStringsEqual(locName, "НЛОЗ")))
				enrollmentLocations.add(loc);
		}
		return enrollmentLocations;
	}
	
	public PatientIdentifier getPatientProgramIdentifier(MdrtbPatientProgram mpp) {
		return getGenPatientProgramIdentifier(mpp.getPatientProgram());
	}
	
	public PatientIdentifier getGenPatientProgramIdentifier(PatientProgram pp) {
		Integer id = null;
		String query = "select patient_id from patient_program where patient_program_id = " + pp.getPatientProgramId();
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		for (List<Object> temp : result) {
			for (int i = 0; i < temp.size(); i++) {
				Object value = temp.get(i);
				if (value != null) {
					id = (Integer) value;
				}
			}
		}
		if (id != null) {
			Patient patient = Context.getPatientService().getPatient(id);
			PatientIdentifier pi = patient.getPatientIdentifier();
			return pi;
		}
		return null;
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			tbPrograms.add(new TbPatientProgram(program));
		}
		
		return tbPrograms;
	}
	
	/* TODO: Is this method required? There's no patient program ID in the patient_program table any more */
	@Deprecated
	public void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId) {
//		String query = "UPDATE patient_program SET patient_identifier_id= " + patientIdenifierId
//		        + " WHERE patient_program_id=" + patientProgramId + ";";
//		Context.getAdministrationService().executeSQL(query, false);
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleRegimens() {
		return this.getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleHIVStatuses() {
		return this.getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleResistanceTypes() {
		return this.getConcept(MdrtbConcepts.RESISTANCE_TYPE).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion) {
		return this.getConcept(conceptQuestion).getAnswers();
	}
	
	public int countPDFRows() {
		return dao.countPDFRows();
	}
	
	public List<List<Integer>> getPDFRows(String reportType) {
		return dao.getPDFData(reportType);
	}
	
	public ArrayList<String> getPDFColumns() {
		return dao.getPDFColumns();
	}
	
	public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String date, String type) {
		dao.unlockReport(oblast, district, facility, year, quarter, month, name, date, type);
	}
	
	public void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String reportDate, String tableData, boolean reportStatus, String reportName, String reportType) {
		System.out.println("Impl -> Saving PDF");
		try {
			dao.doPDF(oblast, district, facility, year, quarter, month, reportDate, tableData, reportStatus, reportName,
			    reportType);
		}
		
		catch (Exception e) {
			System.out.println("caught in impl: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String type) {
		return dao.readReportStatus(oblast, district, facility, year, quarter, month, name, type);
	}
	
	public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String date, String reportType) {
		return dao.readTableData(oblast, district, facility, year, quarter, month, name, date, reportType);
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames) {
		return dao.getEncountersByEncounterTypes(encounterTypeNames);
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate) {
		return dao.getEncountersByEncounterTypes(encounterTypeNames, startDate, endDate, closeDate);
	}
	
	public List<SmearForm> getSmearForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<SmearForm> smears = new ArrayList<SmearForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT), e) != null) {
				// System.out.println("found SC");
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
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
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<CultureForm> cultures = new ArrayList<CultureForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT), e) != null) {
				// System.out.println("found SC");
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
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
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<XpertForm> xperts = new ArrayList<XpertForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), e) != null) {
				// System.out.println("found SC");
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
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
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAINForm> hains = new ArrayList<HAINForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), e) != null) {
				// System.out.println("found SC");
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
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
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAIN2Form> hains = new ArrayList<HAIN2Form>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT), e) != null) {
				// System.out.println("found SC");
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					HAIN2Form sf = new HAIN2Form(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public List<DSTForm> getDstForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DSTForm> dsts = new ArrayList<DSTForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT),
			    e) != null) {
				// System.out.println("found SC");
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					DSTForm sf = new DSTForm(e);
					sf.setPatient(tpp.getPatient());
					dsts.add(sf);
				}
			}
		}
		Collections.sort(dsts);
		return dsts;
	}
	
	public List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DrugResistanceDuringTreatmentForm> drdts = new ArrayList<DrugResistanceDuringTreatmentForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(Context.getEncounterService().getEncounterType("Resistance During Treatment"));
		List<Encounter> encs = Context.getEncounterService().getEncounters(tpp.getPatient(), null, null, null, null, et,
		    false);
		// System.out.println("Encs: " + encs.size());
		for (Encounter e : encs) {
			// if(MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT),
			// e)!=null) {
			// System.out.println("found SC");
			Obs temp = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
				DrugResistanceDuringTreatmentForm drdt = new DrugResistanceDuringTreatmentForm(e);
				drdt.setPatient(tpp.getPatient());
				drdts.add(drdt);
			}
			// }
		}
		Collections.sort(drdts);
		return drdts;
	}
	
	public List<Encounter> getEncountersWithNoProgramId(EncounterType et, Patient p) {
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(et);
		
		ArrayList<Encounter> encs = new ArrayList<Encounter>();
		List<Encounter> all = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		
		for (Encounter e : all) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e) == null) {
				encs.add(e);
			}
		}
		
		return encs;
	}
	
	public void addProgramIdToEncounter(Integer encounterId, Integer programId) {
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(programId);
		Encounter e = Context.getEncounterService().getEncounter(encounterId);
		Obs idObs = new Obs(pp.getPatient(),
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID),
		        e.getEncounterDatetime(), e.getLocation());
		idObs.setEncounter(e);
		idObs.setObsDatetime(e.getEncounterDatetime());
		idObs.setLocation(e.getLocation());
		idObs.setValueNumeric(programId.doubleValue());
		e.addObs(idObs);
		Context.getEncounterService().saveEncounter(e);
	}
	
	public ArrayList<TB03Form> getTB03FormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month) {
		
		ArrayList<TB03Form> forms = new ArrayList<TB03Form>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		Region o = null;
		if (!oblast.equals("") && location == null)
			o = Context.getService(MdrtbService.class).getOblast(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if (o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromOblastName(o);
		else if (location != null)
			locList.add(location);
		List<Encounter> temp = null;
		for (Location l : locList) {
			temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new TB03Form(e));
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<TB03Form> getTB03FormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month) {
		
		ArrayList<TB03Form> forms = new ArrayList<TB03Form>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		
		System.out.println("STR:" + startDate);
		System.out.println("END:" + endDate);
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList != null && locList.size() != 0) {
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new TB03Form(e));
				}
				
			}
		}
		
		else {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new TB03Form(e));
			}
		}
		
		return forms;
		
	}
	
	public ArrayList<TB03uForm> getTB03uFormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtbIntake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		Region o = null;
		if (!oblast.equals("") && location == null)
			o = Context.getService(MdrtbService.class).getOblast(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if (o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromOblastName(o);
		else if (location != null)
			locList.add(location);
		List<Encounter> temp = null;
		for (Location l : locList) {
			temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new TB03uForm(e));
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<TB03uForm> getTB03uFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtbIntake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new TB03uForm(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new TB03uForm(e));
				}
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<Form89> getForm89FormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		Region o = null;
		if (!oblast.equals("") && location == null)
			o = Context.getService(MdrtbService.class).getOblast(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if (o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromOblastName(o);
		else if (location != null)
			locList.add(location);
		List<Encounter> temp = null;
		for (Location l : locList) {
			temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new Form89(e));
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<Form89> getForm89FormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new Form89(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new Form89(e));
				}
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<Form89> getForm89FormsFilledForPatientProgram(Patient p, Location location, Integer patProgId,
	        Integer year, String quarter, String month) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		Map<String, Date> dateMap = null;
		
		if (year != null && (quarter != null || month != null))
			dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = null;
		Date endDate = null;
		
		if (dateMap != null) {
			startDate = (Date) (dateMap.get("startDate"));
			endDate = (Date) (dateMap.get("endDate"));
		}
		
		Concept ppid = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = Context.getEncounterService().getEncounters(p, location, startDate, endDate, null, typeList,
		    null, false);
		if (temp != null) {
			for (Encounter e : temp) {
				Obs ppObs = MdrtbUtil.getObsFromEncounter(ppid, e);
				if (ppObs != null) {
					if (ppObs.getValueNumeric() != null && (ppObs.getValueNumeric().intValue() == patProgId.intValue())) {
						forms.add(new Form89(e));
					}
					
				}
			}
		}
		return forms;
		
	}
	
	public ArrayList<TransferOutForm> getTransferOutFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<TransferOutForm> forms = new ArrayList<TransferOutForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_out_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new TransferOutForm(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new TransferOutForm(e));
				}
			}
		}
		
		return forms;
		
	}
	
	public ArrayList<TransferInForm> getTransferInFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<TransferInForm> forms = new ArrayList<TransferInForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_in_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new TransferInForm(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new TransferInForm(e));
				}
			}
		}
		
		return forms;
		
	}
	
	public ArrayList<TransferOutForm> getTransferOutFormsFilledForPatient(Patient p) {
		
		ArrayList<TransferOutForm> forms = new ArrayList<TransferOutForm>();
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_out_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		for (Encounter e : temp) {
			forms.add(new TransferOutForm(e));
		}
		
		return forms;
		
	}
	
	public ArrayList<TransferInForm> getTransferInFormsFilledForPatient(Patient p) {
		
		ArrayList<TransferInForm> forms = new ArrayList<TransferInForm>();
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_in_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		for (Encounter e : temp) {
			forms.add(new TransferInForm(e));
		}
		
		return forms;
		
	}
	
	public Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse() {
		Set<ProgramWorkflowState> temp = getPossibleTbWorkflowStates(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE));
		
		return temp;
	}
	
	public TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient) {
		TB03Form ret = null;
		Integer encounterId = null;
		EncounterType intakeType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
		String query = "select encounter_id from encounter where location_id=" + location.getId()
		        + " AND encounter_datetime <= '" + encounterDate + "' AND patient_id=" + patient.getId()
		        + " AND encounter_type=" + intakeType.getId() + " AND voided=0 ORDER BY encounter_datetime DESC";
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		if (result != null && result.size() > 0) {
			List<Object> resp = result.get(0);
			if (resp != null) {
				encounterId = (Integer) (resp.get(0));
			}
			
		}
		
		if (encounterId != null)
			ret = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
		
		return ret;
		
	}
	
	public List<Location> getCultureLocations() {
		ArrayList<Location> ret = new ArrayList<Location>();
		HashSet<Integer> locs = new HashSet<Integer>();
		
		String cultureLocIds = Context.getAdministrationService().getGlobalProperty("mdrtb.culturelabs");
		if (cultureLocIds == null || cultureLocIds.length() == 0) {
			return ret;
		}
		
		String[] locIds = cultureLocIds.split("\\|");
		
		if (locIds == null || locIds.length == 0) {
			return ret;
		}
		
		for (int i = 0; i < locIds.length; i++) {
			if (locIds[i].length() != 0) {
				locs.add(Integer.parseInt(locIds[i]));
			}
		}
		
		Collection<Location> allLocs = Context.getLocationService().getAllLocations(false);
		
		for (Location l : allLocs) {
			if (locs.contains(l.getId())) {
				ret.add(l);
			}
		}
		
		return ret;
	}
	
	public ArrayList<Location> getLocationList(Integer oblastId, Integer districtId, Integer facilityId) {
		
		ArrayList<Location> locList = new ArrayList<Location>();
		Location location = null;
		if (oblastId == null && districtId == null && facilityId == null)
			return null;
		
		if (districtId == null) { // means they stopped at oblast
			List<District> distList = getDistricts(oblastId);
			
			for (District d : distList) {
				location = getLocation(oblastId, d.getId(), null);
				if (location == null) {
					List<Facility> facs = getFacilities(d.getId().intValue());
					for (Facility f : facs) {
						location = getLocation(oblastId, d.getId(), f.getId());
						if (location != null) {
							locList.add(location);
						}
					}
				}
				
				else {
					locList.add(location);
				}
			}
		}
		
		else if (facilityId == null) {// means they stopped at district either a single district or a set of
		                                // facilities
			System.out.println("NULL FAC ID");
			location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, null);
			
			if (location == null) { // district that has a set of facilities under it
				System.out.println("NULL LOC");
				List<Facility> facs = Context.getService(MdrtbService.class).getFacilities(districtId.intValue());
				if (facs == null) {
					System.out.println("NULL FACS");
				} else {
					System.out.println("FACS LENGTH=" + facs.size());
				}
				for (Facility f : facs) {
					location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, f.getId());
					if (location != null) {
						locList.add(location);
					}
				}
			}
			
			else {
				System.out.println("NOT NULL LOC:" + location.getLocationId());
				locList.add(location);
			}
		}
		
		else { // single location
			
			location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, facilityId);
			System.out.println("SINGLE LOC:" + location.getLocationId());
			locList.add(location);
		}
		
		System.out.println("LOCS:" + locList.size());
		return locList;
	}
	
	public PatientIdentifier getPatientIdentifierById(Integer id) {
		
		return dao.getPatientIdentifierById(id);
	}
	
	public ArrayList<TB03uForm> getTB03uFormsFilledWithTxStartDateDuring(ArrayList<Location> locList, Integer year,
	        String quarter, String month) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtbIntake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		// CHECK
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, null, null, null, typeList, null, false);
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
		
		else {
			
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, null, null, null, typeList, null, false);
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
	
	public List<Country> getCountries() {
		//		AddressHierarchyLevel countryLevel = new AddressHierarchyLevel();
		//		countryLevel.setLevelId(1);
		//		List<AddressHierarchyEntry> list = getAddressService().getAddressHierarchyEntriesByLevel(countryLevel);
		//		
		//		for (AddressHierarchyEntry add : list) {
		//			Integer id = add.getId();
		//			String name = add.getName();
		//			
		//			ret.add(new Country(name, id));
		//		}
		List<Country> countries = new ArrayList<Country>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Country.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			countries.add(new Country(baseLocation.getName(), baseLocation.getId()));
		}
		return countries;
	}
	
	public ArrayList<TB03Form> getTB03FormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<TB03Form> forms = new ArrayList<TB03Form>();
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		System.out.println("TEMP: " + temp.size());
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new TB03Form(e));
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<Form89> getForm89FormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		System.out.println("TEMP: " + temp.size());
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new Form89(e));
			}
			
		}
		
		return forms;
		
	}
	
	public void evict(Object obj) {
		dao.evict(obj);
	}
	
	public TB03uForm getTB03uFormForProgram(Patient p, Integer patientProgId) {
		
		TB03uForm form = null;
		
		EncounterType eType = Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtbIntake_encounter_type"));
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				form = new TB03uForm(e);
				break;
			}
			
		}
		
		return form;
		
	}
	
	public ArrayList<RegimenForm> getRegimenFormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		
		EncounterType eType = Context.getEncounterService().getEncounterType("PV Regimen");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new RegimenForm(e));
			}
			
		}
		Collections.sort(forms);
		// Collections.reverse(forms);
		return forms;
		
	}
	
	public ArrayList<RegimenForm> getRegimenFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		
		System.out.println("STR:" + startDate);
		System.out.println("END:" + endDate);
		
		EncounterType eType = Context.getEncounterService().getEncounterType("PV Regimen");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		}
		
		else {
			
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
			
		}
		
		Collections.sort(forms);
		return forms;
		
	}
	
	public ArrayList<Patient> getAllPatientsWithRegimenForms() {
		ArrayList<Patient> pList = new ArrayList<Patient>();
		EncounterType eType = Context.getEncounterService().getEncounterType("PV Regimen");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = Context.getEncounterService().getEncounters(null, null, null, null, null, typeList, null,
		    false);
		
		for (Encounter e : temp) {
			if (!pList.contains(e.getPatient())) {
				pList.add(e.getPatient());
			}
		}
		
		return pList;
	}
	
	public RegimenForm getPreviousRegimenFormForPatient(Patient p, ArrayList<Location> locList, Date beforeDate) {
		RegimenForm form = null;
		EncounterType eType = Context.getEncounterService().getEncounterType("PV Regimen");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		List<Encounter> temp = null;
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(p, null, null, beforeDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		}
		
		else {
			
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(p, l, null, beforeDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
			
		}
		if (forms != null && forms.size() != 0) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		}
		
		else
			return null;
		
	}
	
	public RegimenForm getCurrentRegimenFormForPatient(Patient p, Date beforeDate) {
		// RegimenForm form = null;
		EncounterType eType = Context.getEncounterService().getEncounterType("PV Regimen");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		List<Encounter> temp = null;
		
		Date currentDate = null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(beforeDate.getTime());
		gc.add(gc.DATE, 1);
		currentDate = gc.getTime();
		
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		
		temp = Context.getEncounterService().getEncounters(p, null, null, currentDate, null, typeList, null, false);
		for (Encounter e : temp) {
			forms.add(new RegimenForm(e));
		}
		
		if (forms != null && forms.size() != 0) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		}
		
		else
			return null;
		
	}
	
	public ArrayList<AEForm> getAEFormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month) {
		
		ArrayList<AEForm> forms = new ArrayList<AEForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		
		System.out.println("STR:" + startDate);
		System.out.println("END:" + endDate);
		
		EncounterType eType = Context.getEncounterService().getEncounterType("Adverse Event");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
			for (Encounter e : temp) {
				forms.add(new AEForm(e));
			}
		}
		
		else {
			
			for (Location l : locList) {
				temp = Context.getEncounterService().getEncounters(null, l, startDate, endDate, null, typeList, null, false);
				for (Encounter e : temp) {
					forms.add(new AEForm(e));
				}
			}
		}
		
		Collections.sort(forms);
		return forms;
		
	}
	
	public ArrayList<AEForm> getAEFormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<AEForm> forms = new ArrayList<AEForm>();
		
		EncounterType eType = Context.getEncounterService().getEncounterType("Adverse Event");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new AEForm(e));
			}
			
		}
		Collections.sort(forms);
		return forms;
	}
	
	public ArrayList<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId) {
		
		ArrayList<Location> locList = new ArrayList<Location>();
		Location location = null;
		if (oblastId == null && districtId == null && facilityId == null)
			return null;
		
		if (districtId == null && facilityId == null) { // means they stopped at oblast??
			List<District> distList = getDistricts(oblastId);
			for (District d : distList) {
				location = getLocation(oblastId, d.getId(), null);
				if (location == null) {
					List<Facility> facs = getFacilities(d.getId().intValue());
					for (Facility f : facs) {
						location = getLocation(oblastId, d.getId(), f.getId());
						if (location != null) {
							locList.add(location);
						}
					}
				}
				else {
					locList.add(location);
				}
			}
		}
		else if (facilityId == null) {// means they stopped at district - so fetch all facility data
			location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, null);
			if (location == null) { // district that has a set of facilities under it
				List<Facility> facs = Context.getService(MdrtbService.class).getFacilities(districtId.intValue());
				for (Facility f : facs) {
					location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, f.getId());
					if (location != null) {
						locList.add(location);
					}
				}
			}
			else {
				locList.add(location);
			}
		}
		else if (districtId == null) { // they chose a facility so get all facilities with this name
			Facility fac = Context.getService(MdrtbService.class).getFacility(facilityId);
			if (fac == null)
				return null;
			String facName = fac.getName();
			List<Location> locs = Context.getLocationService().getAllLocations(false);
			for (Location l : locs) {
				if (MdrtbUtil.areRussianStringsEqual(l.getName(), facName)) {
					locList.add(l);
				}
			}
		}
		System.out.println("Dushanbe locations:" + locList.size());
		return locList;
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(Date startDate, Date endDate,
	        ArrayList<Location> locList) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		TbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new TbPatientProgram(program);
			
			for (Location l : locList) {
				if (temp.getLocation() != null
				        && (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					tbPrograms.add(new TbPatientProgram(program));
					break;
				}
			}
			
		}
		
		return tbPrograms;
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(Date startDate, Date endDate,
	        ArrayList<Location> locList) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		MdrtbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new MdrtbPatientProgram(program);
			
			for (Location l : locList) {
				if (temp.getLocation() != null
				        && (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					mdrtbPrograms.add(new MdrtbPatientProgram(program));
					break;
				}
			}
		}
		return mdrtbPrograms;
	}
	
	public ArrayList<TB03uForm> getTB03uFormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		EncounterType eType = Context.getEncounterService().getEncounterType("TB03u - MDR");
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		System.out.println("TEMP: " + temp.size());
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new TB03uForm(e));
			}
			
		}
		
		return forms;
		
	}
	
	public List<Encounter> getTbEncounters(Patient patient) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, null, TbUtil.getTbEncounterTypes(),
		    null, false);
	}
	
	public List<TbPatientProgram> getAllTbPatientPrograms() {
		return getAllTbPatientProgramsInDateRange(null, null);
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(), null,
		    endDate, startDate, null, false);
		
		// ADD BY ALI August 13th 2017
		TbPatientProgram temp = null;
		//
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			// tbPrograms.add(new TbPatientProgram(program));
			temp = new TbPatientProgram(program);
			PatientIdentifier pid = getPatientProgramIdentifier(new MdrtbPatientProgram(temp.getPatientProgram()));
			
			if (pid != null) {
				temp.setPatientIdentifier(pid);
			}
			tbPrograms.add(temp);
		}
		
		return tbPrograms;
	}
	
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient) {
		
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getTbProgram(), null,
		    null, null, null, false);
		
		// ADD BY ALI Aug 13th 2017
		TbPatientProgram temp = null;
		//
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			// tbPrograms.add(new TbPatientProgram(program));
			temp = new TbPatientProgram(program);
			PatientIdentifier pid = getPatientProgramIdentifier(new MdrtbPatientProgram(temp.getPatientProgram()));
			
			if (pid != null) {
				temp.setPatientIdentifier(pid);
			}
			tbPrograms.add(temp);
		}
		
		return tbPrograms;
	}
	
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient) {
		List<TbPatientProgram> programs = getTbPatientPrograms(patient);
		
		if (programs.size() > 0) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<TbPatientProgram> programs = new LinkedList<TbPatientProgram>();
		
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if ((endDate == null || program.getDateEnrolled().before(endDate)) && (program.getDateCompleted() == null
			        || startDate == null || !program.getDateCompleted().before(startDate))) {
				programs.add(program);
			}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	public TbPatientProgram getTbPatientProgramOnDate(Patient patient, Date date) {
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
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
				return null;
				// TODO: Figure out why this was throwing an exception before
				// throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			}
			else {
				return new TbPatientProgram(program);
			}
		}
	}
}
