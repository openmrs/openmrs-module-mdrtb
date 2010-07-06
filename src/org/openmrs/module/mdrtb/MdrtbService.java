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
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.specimen.MdrtbCulture;
import org.openmrs.module.mdrtb.specimen.MdrtbDst;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
import org.springframework.transaction.annotation.Transactional;


public interface MdrtbService extends OpenmrsService {
    
    @Transactional
    public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws APIException;
    
    @Transactional
    public void purgeOrderException(OrderExtension oe) throws APIException;
    
    @Transactional
    public OrderExtension saveOrderExtension(OrderExtension oe) throws APIException;
    
    @Transactional
    public  OrderExtension voidOrderExtension(OrderExtension oe) throws APIException;
    
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
    public MdrtbSpecimen createSpecimen(Patient patient);
    
    /**
     * Fetches a specimen sample obj given an encounter of the Specimen Collection type
     */
    public MdrtbSpecimen getSpecimen(Encounter encounter);
    
    /**
     * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
     */
    public List<MdrtbSpecimen> getSpecimens(Patient patient);
    
    /**
     * Deletes a specimen, referenced by specimen Id
     */
    public void deleteSpecimen(Integer patientId);
    
    /**
     * Saves or updates a specimen object
     */
    @Transactional
	public void saveSpecimen(MdrtbSpecimen specimen);
    
    /**
     * Deletes a smear, culture, or dst test
     */
    @Transactional 
    public void deleteTest(Integer testId);
    
    /**
     * Creates a new Smear, associated with the given encounter
     */
    public MdrtbSmear createSmear(Encounter encounter);
    
    /**
     * Fetches a smear given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public MdrtbSmear getSmear(Obs obs);
       
    /**
     * Fetches a smear given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public MdrtbSmear getSmear(Integer obsId);
    
    /**
     * Saves a smear in the approriate obs construct
     */
    @Transactional
    public void saveSmear(MdrtbSmear smear);
    
    
    // TODO: get rid of this if I end up not using it
    /**
     * Updates a smear 
     * 
     * @param smearId the reference to the smear to update
     * @param smear the new values for the smear
     */
    @Transactional
    public void updateSmear(Integer smearId, MdrtbSmear smear);
    
    /**
     * Creates a new culture, associated with the given encounter
     */
    public MdrtbCulture createCulture(Encounter encounter);
    
    /**
     * Fetches a culture given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public MdrtbCulture getCulture(Obs obs);
       
    /**
     * Fetches a culture given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public MdrtbCulture getCulture(Integer obsId);
    
    /**
     * Saves a culture in the approriate obs construct
     */
    @Transactional
    public void saveCulture(MdrtbCulture culture);
    
    
    /**
     * Creates a new dst, associated with the given encounter
     */
    public MdrtbDst createDst(Encounter encounter);
    
    /**
     * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obs
     * @return
     */
    public MdrtbDst getDst(Obs obs);
       
    /**
     * Fetches a dst given the obs_id of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     */
    public MdrtbDst getDst(Integer obsId);
    
    /**
     * Saves a dst in the approriate obs construct
     */
    @Transactional
    public void saveDst(MdrtbDst dst);
    
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
     * Returns the concept that represents a Scanty result
     */
    public Concept getConceptScanty();
    
    public Concept getConceptWaitingForTestResults();
    
    public Concept getConceptDstTestContaminated();
}
