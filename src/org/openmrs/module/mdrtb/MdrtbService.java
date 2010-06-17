package org.openmrs.module.mdrtb;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
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
     * Initializes a specimen object, given a specimen object and a patient
     */
    public void initializeSpecimenObj(MdrtbSpecimenObj specimen, Patient patient);
    
    /**
     * Fetches a specimen sample obj given an encounter_id of the Specimen Collection type
     * 
     * @param obsId
     * @return
     */
    @Transactional(readOnly=true)
    public MdrtbSpecimenObj getSpecimenObj(Integer encounterId);
    
    /**
     * Saves or updates a specimen object
     */
    @Transactional
	public void saveSpecimenObj(MdrtbSpecimenObj specimen);
    
    /**
     * Initializes an encounter object, given a encounter object and an encounter
     */
    public void initializeSmearObj(MdrtbSmearObj smear, Encounter encounter);
    
    /**
     * Fetches a smear given the obs of a Tuberculosis Smear Test Construct
     * 
     * @param obsId
     * @return
     */
    @Transactional(readOnly=true)
    public MdrtbSmearObj getSmearObj(Integer obsId);
       
    /**
     * Updates a smear in the approriate obs construct
     */
    @Transactional
    public void saveSmearObj(MdrtbSmearObj smear);
    
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
     * Returns all the concepts that are possible coded answer for the Tuberculosis Sample Source concept
     */
    public Collection<ConceptAnswer> getPossibleSpecimenTypes();
}
