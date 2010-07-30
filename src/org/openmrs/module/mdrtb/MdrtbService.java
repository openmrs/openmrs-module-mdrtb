package org.openmrs.module.mdrtb;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.transaction.annotation.Transactional;


public interface MdrtbService extends OpenmrsService {
    
    @Transactional(readOnly=true)
    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws APIException ;
    
    public MdrtbFactory getMdrtbFactory();
    
    public void setMdrtbFactory(MdrtbFactory newMdrtbFactory);

    public List<MdrtbRegimenSuggestion> getStandardRegimens();

    public void setStandardRegimens(List<MdrtbRegimenSuggestion> standardRegimens);
    
    public List<Locale> getLocaleSetUsedInDB();
    
    public void setLocaleSetUsedInDB(List<Locale> localeSetUsedInDB);
    
    @Transactional(readOnly=true)
    public List<Location> getAllMdrtrbLocations(boolean includeRetired);
    
    @Transactional(readOnly=true)
    public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales);
    
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
     * Gets the patient chart for a specific patient
     */
    @Transactional(readOnly=true)
    public PatientChart getPatientChart(Integer patientId);
    
    /**
     * Gets the Mdrtb Programs for a specified patient
     */
    @Transactional(readOnly=true)
    public List<PatientProgram> getMdrtbPrograms(Integer patientId);
    
    /**
     * Saves a set of Mdrtb Programs
     */
    @Transactional
    public void saveMdrtbPrograms(List<PatientProgram> mdrtbPrograms);
    
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
     * Returns all the possible drug to display in a DST result, in the order we want to display them
     */
    public List<Concept> getPossibleDrugTypesToDisplay();
    
    /**
     * Returns the concept that represents a Scanty result
     */
    public Concept getConceptScanty();
    
    /**
     * Returns the concept that represents a Waiting For Test Results result
     */
    public Concept getConceptWaitingForTestResults();
    
    /**
     * Returns the concept that represents a DST Test Contaminated result
     */
    public Concept getConceptDstTestContaminated();
    
    /**
     * Returns the concept that represents Other Mycobacteria Non-Coded
     */
    public Concept getConceptOtherMycobacteriaNonCoded();
    
    /**
     * Returns the concept that represents Sputum
     */
    public Concept getConceptSputum();
    
    /**
     * Returns the concept that represents INTERMEDIATE TO TUBERCULOSIS DRUG
     */
    public Concept getConceptIntermediateToTuberculosisDrug();
    
    /**
     * Returns the concept that represents None
     */
    public Concept getConceptNone();
    
	/**
     * Check to see what color to associate with a given result concept
     */
    public String getColorForConcept(Concept concept);

    /**
     * Resets the color map cache to null to force cache reload
     * Auto generated method comment
     *
     */
    public void resetColorMapCache();
}

