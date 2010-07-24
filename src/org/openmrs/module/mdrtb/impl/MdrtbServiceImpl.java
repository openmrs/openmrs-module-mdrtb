package org.openmrs.module.mdrtb.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.db.MdrtbDAO;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartRecord;
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
	
	private static List<MdrtbRegimenSuggestion> standardRegimens = new ArrayList<MdrtbRegimenSuggestion>();
	
	private static List<Locale> localeSetUsedInDB = new ArrayList<Locale>();
	
	private Map<Integer,String> colorMapCache = null;
	
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
		return standardRegimens;
	}
	
	public void setStandardRegimens(List<MdrtbRegimenSuggestion> standardRegimens) {
		MdrtbServiceImpl.standardRegimens.addAll(standardRegimens);
	}
	
	public List<Locale> getLocaleSetUsedInDB() {
		return localeSetUsedInDB;
	}
	
	public void setLocaleSetUsedInDB(List<Locale> localeSetUsedInDB) {
		MdrtbServiceImpl.localeSetUsedInDB.addAll(localeSetUsedInDB);
	}
	
	public List<Location> getAllMdrtrbLocations(boolean includeRetired) {
		return dao.getAllMdrtrbLocations(includeRetired);
	}
	
	public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales) {
		return dao.getConceptWords(phrase, locales);
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
		if (obs == null || !(obs.getConcept().equals(mdrtbFactory.getConceptSmearParent()) || obs.getConcept().equals(mdrtbFactory.getConceptCultureParent()) || obs.getConcept().equals(mdrtbFactory.getConceptDSTParent())) ) {
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
		
		// the id must refer to a valid obs, which is a scanned lab report
		if (obs == null || ! obs.getConcept().equals(mdrtbFactory.getConceptDSTResultParent()) ) {
			throw new APIException ("Unable to delete scanned lab report: invalid report id " + dstResultId);
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
		if (obs == null || ! obs.getConcept().equals(mdrtbFactory.getConceptScannedLabReport()) ) {
			throw new APIException ("Unable to delete scanned lab report: invalid report id " + reportId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public PatientChart getPatientChart(Patient patient) {
		
		PatientChart chart = new PatientChart();
		
		if (patient == null) {
			log.warn("Can't fetch patient chart, patient is null");
			return null;
		}
		
		// first, fetch all the specimens for this patient
		List<Specimen> specimens = getSpecimens(patient);
		
		// the getSpecimen method should return the specimens sorted, but just in case it is changed
		Collections.sort(specimens);
		
		// now fetch the program start date
		Calendar startDate = Calendar.getInstance();
		
		Program mdrtb = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, mdrtb, null, null, null, null, false);
		
		if(programs == null || programs.size() == 0){
			if(specimens.size() > 0) {
				// set some sort of default date?
				// TODO: use collected date of this first specimen for now
				startDate.setTime(specimens.get(0).getDateCollected());
			}
		}
		else {
			// TODO: this is only temporary, not what we want to do long term, doesn't handle patients with more than one; baseline/prior
			startDate.setTime(programs.get(0).getDateEnrolled());
		}
		
		// first, we want to get all specimens collected more than a month before treatment start date
		startDate.add(Calendar.MONTH, -1);
		chart.getRecords().put("PRIOR", new PatientChartRecord(getSpecimensBeforeDate(specimens,startDate)));
		
		// now add all the specimens collected in the month prior to treatment
		startDate.add(Calendar.MONTH, 1);
		chart.getRecords().put("BASELINE", new PatientChartRecord(getSpecimensBeforeDate(specimens,startDate)));
		
		// now go through the add all the other specimens
		startDate.add(Calendar.MONTH, 1);
		Integer iteration = 0;
		while(specimens.size() > 0) {
			chart.getRecords().put(iteration.toString(), new PatientChartRecord(getSpecimensBeforeDate(specimens,startDate)));
			startDate.add(Calendar.MONTH, 1);
			iteration++;
		}
		
		return chart;
	}
	
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return getMdrtbFactory().getConceptSmearResult().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return getMdrtbFactory().getConceptSmearMicroscopyMethod().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return getMdrtbFactory().getConceptCultureResult().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return getMdrtbFactory().getConceptCultureMethod().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return getMdrtbFactory().getConceptDSTMethod().getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		List<Concept> results = new LinkedList<Concept>();
		results.add(getMdrtbFactory().getConceptSusceptibleToTuberculosisDrug());
		results.add(getMdrtbFactory().getConceptIntermediateToTuberculosisDrug());
		results.add(getMdrtbFactory().getConceptResistantToTuberculosisDrug());
		results.add(getMdrtbFactory().getConceptDstTestContaminated());
		results.add(getMdrtbFactory().getConceptWaitingForTestResults());
		
		return results;
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return getMdrtbFactory().getConceptTypeOfOrganism().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return getMdrtbFactory().getConceptSampleSource().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return getMdrtbFactory().getConceptAppearanceOfSpecimen().getAnswers();
	}
	
    public List<Concept> getPossibleDrugTypesToDisplay() {
    	// TODO: do we want to start pulling this from somewhere else?
    	String drugList = Context.getAdministrationService().getGlobalProperty("mdrtb.DST_drug_list");
    	
    	List<Concept> drugTypes = new LinkedList<Concept>();
    	
    	// we are simply making a list out of drugs, not drugs/concentration
    	for(String drugEntry : drugList.split("\\|")) {
    		String[] drugFields = drugEntry.split(":");
    		
    		Concept drug = Context.getConceptService().getConceptByName(drugFields[0]);
    		
    		if(!drugTypes.contains(drug)) {
    			drugTypes.add(drug);
    		}
    	}
    	
    	return drugTypes;
    	
    	// TODO: delete this stuff below when we are sure we don't need it
    	/** 
    	List<DrugType> drugTypes = new LinkedList<DrugType>();
    	
    	for(String drugEntry : drugList.split("\\|")) {
    		String[] drugFields = drugEntry.split(":");
    	   		
    		Concept drug = Context.getConceptService().getConceptByName(drugFields[0]);
    		if(drugFields.length == 1 || StringUtils.isEmpty(drugFields[1])) {
    			drugTypes.add(new DrugType(drug));
    		}
    		else {
    			drugTypes.add(new DrugType(drug, Double.valueOf(drugFields[1])));
    		}
    	}
    	
    	return drugTypes;
    	*/
    }
    
	public Concept getConceptScanty() {
		return getMdrtbFactory().getConceptScanty();
	}
	
	public Concept getConceptWaitingForTestResults() {
		return getMdrtbFactory().getConceptWaitingForTestResults();
	}
	
	public Concept getConceptDstTestContaminated() {
		return getMdrtbFactory().getConceptDstTestContaminated();
	}
	
	public Concept getConceptOtherMycobacteriaNonCoded() {
		return getMdrtbFactory().getConceptOtherMycobacteriaNonCoded();
	}
	
	public Concept getConceptSputum() {
		return getMdrtbFactory().getConceptSputum();
	}
	
	public Concept getConceptIntermediateToTuberculosisDrug() {
		return getMdrtbFactory().getConceptIntermediateToTuberculosisDrug();
	}
	
    public Concept getConceptNone() {
    	return getMdrtbFactory().getConceptNone();
    }
    
    public String getColorForConcept(Concept concept) {
    	
    	// initialize the cache if need be
    	if(colorMapCache == null) {
    		colorMapCache = new HashMap<Integer,String>();
    		
    		String colorMap = Context.getAdministrationService().getGlobalProperty("mdrtb.colorMap");
    	  	
        	if(colorMap != null) {    	
        		for(String mapping : colorMap.split("\\|")) {
        			String[] mappingFields = mapping.split(":");
        			colorMapCache.put(Integer.valueOf(mappingFields[0]), mappingFields[1]);
        		}
        	}
    	}
    	
    	return colorMapCache.get(concept.getId());
    }
	
    public void resetColorMapCache() {
    	colorMapCache = null;
    }
    
	/**
	 * Utility functions
	 */
	
	
	// IMPORTANT: the assumption this method makes is that list of specimens are ordered in descending date order
	// also, this method pulls all the specimens it returns off the list of specimens passed to it;
	// this method is intended to be use with the getPatientChart API method
	private List<Specimen> getSpecimensBeforeDate(List<Specimen> specimens, Calendar compareDate) {
		List<Specimen> results = new LinkedList<Specimen>();
		Calendar specimenDate = Calendar.getInstance();
		
		while(!specimens.isEmpty()) {
			specimenDate.setTime(specimens.get(0).getDateCollected());
			if(specimenDate.before(compareDate)) {
				results.add(specimens.get(0));
				specimens.remove(specimens.get(0));
			}
			else {
				// we don't need to keep checking since the the dates are in order
				break;
			}
		}
		
		return results;
	}
}
