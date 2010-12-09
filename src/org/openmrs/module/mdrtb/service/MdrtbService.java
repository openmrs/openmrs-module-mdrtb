package org.openmrs.module.mdrtb.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.transaction.annotation.Transactional;


public interface MdrtbService extends OpenmrsService {
    
    @Transactional(readOnly=true)
    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws APIException ;
    
    @Deprecated
    public MdrtbFactory getMdrtbFactory();
    
    @Deprecated
    public void setMdrtbFactory(MdrtbFactory newMdrtbFactory);
    
    public List<Locale> getLocaleSetUsedInDB();
    
    public void setLocaleSetUsedInDB(List<Locale> localeSetUsedInDB);
    
    /**
     * @return all Locations which have non-voided Patient Programs associated with them
     */
    @Transactional(readOnly=true)
    public List<Location> getLocationsWithAnyProgramEnrollments();
    
    @Transactional(readOnly=true)
    public List<Location> getAllMdrtrbLocations(boolean includeRetired);
    
    @Transactional(readOnly=true)
    public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales);
    
    /**
     * Fetches a concept specified by a MdrtbConcepts mapping
     */
    public Concept getConcept(String... conceptMapping);
    
    /**
     * @return the Concept specified by the passed lookup string.  Checks MdrtbConcepts mapping, id, name, and uuid before returning null
     */
    public Concept findMatchingConcept(String lookup);
   
    /**
     * Resets the concept map cache
     */
    public void resetConceptMapCache();
    
  	/**
  	 * Returns all the mdrtb programs for a given patient
  	 */
    @Transactional(readOnly=true)
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
    @Transactional(readOnly=true)
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient);
	
    /**
     * Returns all the patient programs that fall within a specific date range
     */
    @Transactional(readOnly=true)
    public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
    
    /**
     * Return the specific MdrtbPatientProgram the patient was enrolled in on the specified date (if any)
     * (This assumes that a patient is only enrolled in one MDR-TB patient program at a time)
     */
    @Transactional(readOnly=true)
    public MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date);
    
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
    @Transactional(readOnly=true)
	public MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId);   
    
    /**
     * Creates a new specimen, associated with the given patient
     */
    public Specimen createSpecimen(Patient patient);
    
    /**
     * Fetches a specimen sample obj given an encounter of the Specimen Collection type
     */
    public Specimen getSpecimen(Encounter encounter);
    
    /**
     * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
     */
    public List<Specimen> getSpecimens(Patient patient);
    
    /**
     * Fetches all specimens for a patient within a certain date range
     */
    public List<Specimen> getSpecimens(Patient patient, Date startDate, Date endDate);
    
    /**
     * Deletes a specimen, referenced by specimen Id
     */
    public void deleteSpecimen(Integer patientId);
    
    /**
     * Saves or updates a specimen object
     */
    @Transactional
	public void saveSpecimen(Specimen specimen);
    
    /**
     * Deletes a smear, culture, or dst test
     */
    @Transactional 
    public void deleteTest(Integer testId);
    
    /**
     * Creates a new Smear, associated with the given encounter
     */
    public Smear createSmear(Encounter encounter);
    
    /**
     * Fetches a smear given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Smear getSmear(Obs obs);
       
    /**
     * Fetches a smear given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public Smear getSmear(Integer obsId);
    
    /**
     * Saves a smear in the approriate obs construct
     */
    @Transactional
    public void saveSmear(Smear smear);
     
    /**
     * Creates a new culture, associated with the given encounter
     */
    public Culture createCulture(Encounter encounter);
    
    /**
     * Fetches a culture given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Culture getCulture(Obs obs);
       
    /**
     * Fetches a culture given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public Culture getCulture(Integer obsId);
    
    /**
     * Saves a culture in the approriate obs construct
     */
    @Transactional
    public void saveCulture(Culture culture);
    
    
    /**
     * Creates a new dst, associated with the given encounter
     */
    public Dst createDst(Encounter encounter);
    
    /**
     * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Dst getDst(Obs obs);
       
    /**
     * Fetches a dst given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public Dst getDst(Integer obsId);
    
    /**
     * Saves a dst in the appropriate obs construct
     */
    @Transactional
    public void saveDst(Dst dst);
    
    
    /**
     * Deletes a dst result
     */
    @Transactional 
    public void deleteDstResult(Integer dstResultId);
    
    /**
     * Saves a scanned lab report in the appropriate obs constructs
     */
    @Transactional
    public void saveScannedLabReport(ScannedLabReport report);
    
    /**
     * Deletes a scanned lab report
     */
    @Transactional 
    public void deleteScannedLabReport(Integer reportId);
    
    /**
     * Handles exiting a patient from care
     */
    @Transactional
    public void exitFromCare(Patient patient, Date dateExited, Concept reasonForExit);
    
    /**
     * Gets the MDR-TB program
     */
    @Transactional(readOnly=true)
    public Program getMdrtbProgram();
    
    /**
     * Gets the possible providers
     */
    @Transactional(readOnly=true)
    public Collection<Person> getProviders();
    
    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Smear Test Result
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleSmearResults();
    
    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Smear Method concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleSmearMethods();
    
    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Culture Test Result
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleCultureResults();
    
    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Culture Method concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleCultureMethods();
    
    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Drug Sensitivity Test Method concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleDstMethods();
    
    /**
     * Returns all the concepts that are possible Dst results
     */
    @Transactional(readOnly=true)
    public Collection<Concept> getPossibleDstResults();
    
    /**
     * Returns all the concepts that are possible coded answered for Type of Organism concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleOrganismTypes();
    
    /**
     * Returns all the concepts that are possible coded answer for the Tuberculosis Sample Source concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleSpecimenTypes();
    
    /**
     * Returns all the concepts that are possible coded answers for the appearance of a sputum specimen
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleSpecimenAppearances();
    
    /**
     * Returns all possible TB Anatomical sites
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleAnatomicalSites();
    
    /**
     * @return all of the Drugs within the ConceptSets which match the conceptMapKeys
     */
    public List<Concept> getDrugsInSet(String... conceptMapKey);
    
    /**
     * @return all of the Drugs within the ConceptSet which match the conceptMapKeys
     */
    public List<Concept> getDrugsInSet(Concept concept);
  
    /**
     * Returns all the possible drugs to display in a DST result, in the order we want to display them
     */
    public List<Concept> getMdrtbDrugs();
    
    /**
     * Returns all the possible antiretrovirals
     */
    public List<Concept> getAntiretrovirals();
    
    /**
     * Returns all possible outcomes for the MDR-TB program
     */
    public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes();
    
    /**
     * Returns all possible MDR-TB previous drug use classifications
     */
    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse();
    
    /**
     * Returns all possible MDR-TB previous treatment classifications
     */
    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment();
    
	/**
     * Check to see what color to associate with a given result concept
     */
    public String getColorForConcept(Concept concept);

    /**
     * Resets the color map cache to null to force cache reload
     */
    public void resetColorMapCache();
    
    /**
     * Returns the display associated with a given location
     * Used to display location codes in the patient chart
     */
    public String getDisplayCodeForLocation(Location location);
    
    /**
     * Resets the display code for location to null to force cache relod
     */
    public void resetLocationToDisplayCodeCache();
}

