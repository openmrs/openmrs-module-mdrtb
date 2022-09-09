package org.openmrs.module.labmodule.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.labmodule.program.TbPatientProgram;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Dst;
import org.openmrs.module.labmodule.specimen.Dst1;
import org.openmrs.module.labmodule.specimen.Dst2;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.ScannedLabReport;
import org.openmrs.module.labmodule.specimen.Smear;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.Xpert;
import org.openmrs.module.labmodule.specimen.reporting.District;
import org.openmrs.module.labmodule.specimen.reporting.Facility;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.transaction.annotation.Transactional;
/*import org.openmrs.module.mdrtb.specimen.ScannedLabReport;*/


public interface TbService extends OpenmrsService {

    
    /**
     * @return all Locations which have non-voided Patient Programs associated with them
     */
    @Transactional(readOnly=true)
    public List<Location> getLocationsWithAnyProgramEnrollments();
    
    /**
     * Fetches a concept specified by a TbConcepts mapping
     */
    public Concept getConcept(String... conceptMapping);
    
    /**
     * Fetches a concept specified by a TbConcepts mapping
     */
    public Concept getConcept(String conceptMapping);
    
    /**
     * @return the Concept specified by the passed lookup string.  Checks TbConcepts mapping, id, name, and uuid before returning null
     */
    public Concept findMatchingConcept(String lookup);
   
    /**
     * Resets the concept map cache
     */
    public void resetConceptMapCache();
    
    /**
     * Gets all MDR-TB specific encounters for the given patient
     */
    @Transactional(readOnly=true)
    public List<Encounter> getTbEncounters(Patient patient);

    
    /**
     * Returns all the DOTS programs in the system
     */
    @Transactional(readOnly=true)
    public List<TbPatientProgram> getAllTbPatientPrograms();
    
   
    
    /**
     * Returns all the dots programs in the system that were active during a specific date range
     */
    @Transactional(readOnly=true)
    public List<TbPatientProgram> getAllTbPatientProgramsInDateRange(Date startDate, Date endDate);
    
  	
    /**
  	 * Returns all the dots programs for a given patient
  	 */
    @Transactional(readOnly=true)
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
    @Transactional(readOnly=true)
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient);
	
    /**
     * Returns all the patient programs for a given patient that fall within a specific date range
     */
    @Transactional(readOnly=true)
    public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
    
    /**
     * Return the specific MdrtbPatientProgram the patient was enrolled in on the specified date 
     * (This assumes that a patient is only enrolled in one MDR-TB patient program at a time)
     * 
     * If the date is before any program enrollments, it returns the first program enrollment
     * If the date is after all program enrollments, it returns the most recent program enrollment
     * If the date is between two program enrollments, it returns the later of the two
     */
    @Transactional(readOnly=true)
    public TbPatientProgram getTbPatientProgramOnDate(Patient patient, Date date);
    
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
    @Transactional(readOnly=true)
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId);   
    
    /**
     * Fetches a specimen sample obj given a specimen id
     */
    public Specimen getSpecimen(Integer specimedId);
    
    /**
     * Fetches a specimen sample obj given an encounter of the Specimen Collection type
     */
    public Specimen getSpecimen(Encounter encounter);
    
    /**
     * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
     */
    public List<Specimen> getSpecimens(Patient patient);
    
    /**
     * Fetches all specimens within a certain data range
     * 
     * @param patient: only include specimens associated with this patient
     * @param startDate: only include specimens with a date collected after (or equal to) this start date
     * @param endDate: only include specimens with a date collected before (or equal to) this end date
     * 
     * All parameters can be set to null
     */
    public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected);
    
    /**
     * Fetches all specimens within a certain data range and from a certain lab
     * 
     * @param patient: only include specimens associated with this patient
     * @param startDate: only include specimens with a date collected after (or equal to) this start date
     * @param endDate: only include specimens with a date collected before (or equal to) this end date
     * @param location: only include specimens collected from the specified location
     * 
     * All parameters can be set to null
     */
    public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected, Location locationCollected);
    


    /**
     * Fetches a specimen sample obj given a specimen id
     */
    public LabResult getLabResult(Integer labResultId);
    
    /**
     * Fetches a specimen sample obj given an encounter of the Specimen Collection type
     */
    public LabResult getLabResult(Encounter encounter);
    
    /**
     * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
     */
    public List<LabResult> getLabResults(Patient patient);
    
    /**
     * Fetches all specimens within a certain data range
     * 
     * @param patient: only include specimens associated with this patient
     * @param startDate: only include specimens with a date collected after (or equal to) this start date
     * @param endDate: only include specimens with a date collected before (or equal to) this end date
     * 
     * All parameters can be set to null
     */
    public List<LabResult> getLabResults(Patient patient, Date startDateCollected, Date endDateCollected);
    
    /**
     * Fetches all specimens within a certain data range and from a certain lab
     * 
     * @param patient: only include specimens associated with this patient
     * @param startDate: only include specimens with a date collected after (or equal to) this start date
     * @param endDate: only include specimens with a date collected before (or equal to) this end date
     * @param location: only include specimens collected from the specified location
     * 
     * All parameters can be set to null
     */
    public List<LabResult> getLabResults(Patient patient, Date startDateCollected, Date endDateCollected, Location locationCollected);
    
    public List<Oblast> getOblasts();
    public List<Facility> getFacilities();
    public List<District> getDistricts();/*

    public District getDistrictByPatientId(int patientId);
    public District getOblastByPatientId(int patientId);
    public District getFacilityByPatientId(int patientId);*/
    
    public Oblast getOblast(Integer oblastId);
    
    public List<Location> getLocationsFromOblastName(Oblast oblast);
    
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
     * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Dst getDst(Obs obs);
    
    /**
     * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Dst1 getDst1(Obs obs);
    
    public Dst1 getDst1(Integer obsId);
    /**
     * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Dst2 getDst2(Obs obs);
    
    public Dst2 getDst2(Integer obsId);
    /**
     * Fetches a dst given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public Dst getDst(Integer obsId);
    
    public Location getLocation(Integer o, Integer d, Integer f);

    public Location getAnyLocation(Integer o, Integer d, Integer f);

    public ArrayList<Location> getLocationList(Integer oblastId, Integer districtId, Integer facilityId);
	
    /**
     * Gets the DOTS program
     */
    @Transactional(readOnly=true)
    public Program getTbProgram();
    
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
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleMgitResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleSentResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleMtIdTest();

    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleLjResults();

    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getAllPlacesOfCulture();
    
    /**
     * Returns concepts that are Stable / Sensitive as coded answers for the H, R questions
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleR();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleH();
 
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleXpertMtbRif();
 
    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Culture Method concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleCultureMethods();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleCultureTypes();

    /**
     * Returns all the concepts that are possible coded answers for the Tuberculosis Drug Sensitivity Test Method concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleDstMethods();

    /**
     * Returns all the concepts that are possible coded answers for the Drug Sensitivity for a given Drug concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleDrugResistance();
    
    /**
     * Returns all the concepts that are possible coded answers for the Type of DST 
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleDstTypes();
    

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
     * Returns all the concepts that are possible coded answer for the Investigation Purpose concept
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleInvestigationPurposes();
    
    /**
     * Returns all the concepts that are possible coded answers for the appearance of a sputum specimen
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleSpecimenAppearances();
    
    /**
     * Returns all the concepts that are possible coded answers for the requesting facility type
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleRequestingFacilities();
    
    /**
     * Returns all the concepts that are possible coded answers for the microscopy results
     */
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleMicroscopyResults();
    
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
    public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes();
    
    /**
     * Returns all possible MDR-TB previous drug use classifications
     */
    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups();
    
    /**
     * Returns all possible MDR-TB previous treatment classifications
     */
    //public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment();
    
	/**
     * Check to see what color to associate with a given result concept
     */
    public String getColorForConcept(Concept concept);

    /**
     * Resets the color map cache to null to force cache reload
     */
    public void resetColorMapCache();
    
    /**
     * Fetches list of rayons. Assumes existence of AddressHierarchyModule
     */
    public List<String> getAllRayonsTJK();

	@Transactional(readOnly=true)
    public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRange(Date startDate,
			Date endDate);
	
	@Transactional(readOnly=true)
    public List<TbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate,
			Date endDate);
	
	 @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleMtbResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleRifResistanceResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleInhResistanceResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleXpertMtbBurdens();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleMoxResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleCmResults();
    
    @Transactional(readOnly=true)
    public Collection<ConceptAnswer> getPossibleEResults();
    
    /**
	 * Returns a specific MdrtbPatientProgram by id
	 */
    @Transactional(readOnly=true)
	public TbPatientProgram getMdrtbPatientProgram(Integer patientProgramId);   
    
    /**
     * Saves or updates a specimen object
     */
    @Transactional
	public void saveSpecimen(Specimen specimen);
    
    /**
     * Creates a new specimen, associated with the given patient
     */
    public Specimen createSpecimen(Patient patient);
    
    /**
     * Saves or updates a specimen object
     */
    @Transactional
	public int saveLabResult(LabResult labResult);
    
    /**
     * Creates a new specimen, associated with the given patient
     */
    public LabResult createLabResult(Patient patient);
   
    
    /**
     * Saves a xpert in the approriate obs construct
     */
    @Transactional
    public int saveXpert(Xpert xpert);
    
    /**
     * Saves a culture in the approriate obs construct
     */
    @Transactional
    public int saveCulture(Culture culture);
    
    /**
     * Saves a dst1 in the approriate obs construct
     */
    @Transactional
    public int saveDst1(Dst1 dst1);
    
    /**
     * Saves a dst2 in the approriate obs construct
     */
    @Transactional
    public int saveDst2(Dst2 dst2);

    /**
     * Saves a microscopy in the approriate obs construct
     */
    @Transactional
    public int saveMicroscopy(Microscopy microscopy);
    
    /**
     * Saves a microscopy in the approriate obs construct
     */
    @Transactional
    public void updateMicroscopy(Microscopy microscopy);
    
    /**
     * Creates a new Xpert, associated with the given encounter
     */
    public Xpert createXpert(Specimen specimen);
    
    /**
     * Fetches an xpert given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public Xpert getXpert(Obs obs);
       
    /**
     * Fetches an xpert given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public Xpert getXpert(Integer obsId);
    
    //HAIN
    
    /**
     * Saves a hain in the approriate obs construct
     */
    @Transactional
    public int saveHAIN(HAIN hain);
    
    /**
     * Saves a hain2 in the approriate obs construct
     */
    @Transactional
    public int saveHAIN2(HAIN2 hain2);
    
    /**
     * Creates a new Xpert, associated with the given encounter
     */
    public HAIN createHAIN(Specimen specimen);
    
    /**
     * Fetches an xpert given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public HAIN getHAIN(Obs obs);
       
    /**
     * Fetches an xpert given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public HAIN getHAIN(Integer obsId);
    
    /**
     * Fetches an xpert given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public Microscopy getMicroscopy(Integer obsId);
    
    /**
     * Creates a new microscopy, associated with the given encounter
     */
    public Microscopy createMicroscopy(LabResult labResult);
    
    /**
     * Creates a new Xpert, associated with the given encounter
     */
    public Xpert createXpert(LabResult labResult);
    
    /**
     * Creates a new Culture, associated with the given encounter
     */
    public Culture createCulture(LabResult labResult);
    
    /**
     * Creates a new HAIN, associated with the given encounter
     */
    public HAIN createHAIN(LabResult labResult);
    
    /**
     * Creates a new HAIN2, associated with the given encounter
     */
    public HAIN2 createHAIN2(LabResult labResult);
    
    /**
     * Creates a new Smear, associated with the given encounter
     */
    public Smear createSmear(LabResult labResult);
    
    /**
     * Saves a smear in the approriate obs construct
     */
    @Transactional
    public void saveSmear(Smear smear);
     
    /**
     * Creates a new culture, associated with the given encounter
     */
    public Culture createCulture(Specimen specimen);
        
    public Dst1 createDst1(Specimen specimen);
    
    /**
     * Creates a new dst, associated with the given encounter
     */
    public Dst createDst(Specimen specimen);

    /**
     * Creates a new Dst1, associated with the given encounter
     */
    public Dst1 createDst1(LabResult labResult); 
    
    /**
     * Creates a new Dst2, associated with the given encounter
     */
    public Dst2 createDst2(LabResult labResult);
 
    public Dst2 createDst2(Specimen specimen);
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
     * Deletes a specimen, referenced by specimen Id
     */
    public void deleteSpecimen(Integer patientId);
    
    /**
     * Deletes a smear, culture, or dst test
     */
    
    public Collection<Location> getPossibleLabs();
    
    @Transactional 
    public void deleteTest(Integer testId);
    
    public String getAllPatientTestedDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAllMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAllDiagnosticPatientTestedDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getDiagnosticMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAllFollowupPatientTestedDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getFollowupMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAllPositivePatientDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getPositiveDiagnosticPatientDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getPositiveFollowupPatientDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAllPositiveMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getDiagnosticPositiveMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getFollowupPositiveMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAllPHCResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getPositivePHCResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getRatioDiagnosticMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getRateFollowupMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getSalivaDiagnosticMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getAverageWeeklyLoadPerLabTechnician(Date startDate, Date endDate, List<Location> locList, int noOfWeeks);
    
    public String getAverageCultureInoculationDays(Date startDate, Date endDate, Concept inoculateDate, Concept growthDate, Concept conditionalConcept, Concept... codedValues);
    
    public String getAllPatientTestedDuring(Date startDate, Date endDate, List<Location> locList, Concept... concepts);
    
    public String getAllPatientByTestTypeDuring(Date startDate, Date endDate, List<Location> locList, Concept test);
    
    public String getCodedCohortByParentObs(Date startDate, Date endDate,Concept parent, Concept questionConcept, Concept... answers);

    public String getHainAllSensitive(Date startDate, Date endDate, List<Location> locList );
    
    public String getHainAllIndefinite(Date startDate, Date endDate, List<Location> locList );
    
    public String getHainAllResistant(Date startDate, Date endDate , List<Location> locList );
    
    public String getHainResistantByDrug(Date starDate, Date endDate, Concept drug , List<Location> locList);
    
    public String getHain1MTB(Date starDate, Date endDate, List<Location> locList );
    
    public String getHain2MTB(Date starDate, Date endDate, List<Location> locList );

    public String getHain2AllSensitive(Date starDate, Date endDate, List<Location> locList );
    
    public String getHain2ResistanceByDrug(Date starDate, Date endDate, List<Location> locList, Concept... drug);
    public String getHain2ResistanceFlqAgE(Date starDate, Date endDate, List<Location> locList);
    public String getHain2ResistanceFlqE(Date starDate, Date endDate, List<Location> locList);
    public String getHain2ResistanceFlqAg(Date starDate, Date endDate, List<Location> locList);
    public String getHain2ResistanceAgE(Date starDate, Date endDate, List<Location> locList);
    public String getHain2IndefiniteFlqAgE(Date starDate, Date endDate, List<Location> locList);
    
    public String getHain2AllResistant(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertWithMicroscopy(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertWithoutMicroscopy(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertMTPositive(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertMTNegative(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertPositiveMtPositiveSusceptible(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertPositiveMtPositiveResistant(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertPositiveMtNegativeSusceptible(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertPositiveMtNegativeResistant(Date starDate, Date endDate, List<Location> locList);
   
    public String getXpertNegative(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertOnly(Date starDate, Date endDate, List<Location> locList);
   
    public String getXpertOnlyByResult(Date starDate, Date endDate, List<Location> locList, Concept result);
    
    public String getXpertError(Date starDate, Date endDate, List<Location> locList);
    
    public String getXpertNoResults(Date starDate, Date endDate, List<Location> locList);
    
    public Collection<Location> getAllLocations();
    //public String generateReportFromQuery (String location, String year, String query, Boolean export);

    public List<Encounter> getAllPatientTestedAtLocationDuring(Date startDate, Date endDate, List<Location> locList);
    
    public String getCultureWithMicroscopy(Date starDate, Date endDate,Concept cultureType, Concept cultureResult, List<Location> locList);
    
    public String getCultureWithMicroscopyResult(Date starDate, Date endDate,Concept cultureType,Concept cultureResult,Concept micResult, List<Location> locList);
    
    public String getCultureWithMicroscopyResultPositive(Date starDate, Date endDate,Concept cultureType,Concept cultureResult, List<Location> locList);

    public String getDiagnosticCultureWithMicroscopy(Date starDate, Date endDate,Concept cultureType, Concept cultureResult, List<Location> locList);
    
    public String getDiagnosticCultureWithMicroscopyResult(Date starDate, Date endDate,Concept cultureType,Concept cultureResult,Concept micResult, List<Location> locList);
    
    public String getDiagnosticCultureWithMicroscopyResultPositive(Date starDate, Date endDate,Concept cultureType,Concept cultureResult, List<Location> locList);

	public List<Facility> getFacilities(Integer parentId);

	List<District> getDistricts(Integer parentId);
	
	 public Map<String, Object> getLocation(Location location,String type);
	 
	 public ArrayList<Location> getLocationHierarchy(Integer oblastId, Integer districtId, Integer facilityId);

}

