package org.openmrs.module.mdrtb;


import java.util.Date;
import java.util.UUID;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * Class representing a TB Culture
 */
@Deprecated
public class MdrtbCultureObj {
    
    private Obs cultureResult = new Obs();
    private Obs source = new Obs();
    private Obs colonies = new Obs();
    private Obs cultureStartDate = new Obs();
    private Obs cultureResultsDate = new Obs();
    private Obs cultureDateReceived = new Obs();
    private Obs cultureMethod = new Obs();
    private Obs typeOfOrganism = new Obs();
    private Obs typeOfOrganismNonCoded = new Obs();
    private Obs cultureParentObs = new Obs();

    public MdrtbCultureObj(){}
    
    /**
     * This constructor sets up all of the Obs with the correct concepts
     * 
     * @param STR_TB_CULTURE_RESULT
     * @param STR_TB_SAMPLE_SOURCE
     * @param STR_COLONIES
     * @param STR_CULTURE_START_DATE
     * @param STR_RESULT_DATE
     * @param STR_DATE_RECEIVED
     * @param STR_TB_CULTURE_METHOD
     * @param STR_TYPE_OF_ORGANISM
     * @param STR_TYPE_OF_ORGANISM_NON_CODED
     */
    public MdrtbCultureObj(Patient patient, User user, MdrtbFactory mu){

        
        //STR_TB_CULTURE_RESULT
        cultureResult.setConcept(mu.getConceptCultureResult());  
        cultureResult.setVoided(false);
        cultureResult.setDateCreated(new Date());
        cultureResult.setPerson(patient);
        cultureResult.setCreator(user);
        cultureResult.setUuid(UUID.randomUUID().toString());
        if (cultureResult.getConcept() == null)
            throw new RuntimeException("cultureResult id is null");
        
        source.setConcept(mu.getConceptSampleSource());       
        source.setVoided(false);
        source.setDateCreated(new Date());
        source.setPerson(patient);
        source.setCreator(user);
        source.setUuid(UUID.randomUUID().toString());
        if (source.getConcept() == null)
            throw new RuntimeException("source id is null");
        
        colonies.setConcept(mu.getConceptColonies());        
        colonies.setVoided(false);
        colonies.setDateCreated(new Date());
        colonies.setPerson(patient);
        colonies.setCreator(user);
        colonies.setUuid(UUID.randomUUID().toString());
        if (colonies.getConcept() == null)
            throw new RuntimeException("colonies id is null");
        
        //STR_CULTURE_START_DATE
        cultureStartDate.setConcept(mu.getConceptCultureStartDate());       
        cultureStartDate.setVoided(false);
        cultureStartDate.setDateCreated(new Date());
        cultureStartDate.setPerson(patient);
        cultureStartDate.setCreator(user);
        cultureStartDate.setUuid(UUID.randomUUID().toString());
        if (cultureStartDate.getConcept() == null)
            throw new RuntimeException("cultureStartDate id is null");
       
        cultureResultsDate.setConcept(mu.getConceptResultDate());     
        cultureResultsDate.setVoided(false);
        cultureResultsDate.setDateCreated(new Date());
        cultureResultsDate.setPerson(patient);
        cultureResultsDate.setCreator(user);
        cultureResultsDate.setUuid(UUID.randomUUID().toString());
        if (cultureResultsDate.getConcept() == null)
            throw new RuntimeException("cultureResultsDate id is null");
        
        cultureDateReceived.setConcept(mu.getConceptDateReceived());
        cultureDateReceived.setVoided(false);
        cultureDateReceived.setDateCreated(new Date());
        cultureDateReceived.setPerson(patient);
        cultureDateReceived.setCreator(user);
        cultureDateReceived.setUuid(UUID.randomUUID().toString());
        if (cultureDateReceived.getConcept() == null)
            throw new RuntimeException("cultureDateReceived id is null");
      
        cultureMethod.setConcept( mu.getConceptCultureMethod());
        cultureMethod.setVoided(false);
        cultureMethod.setDateCreated(new Date());
        cultureMethod.setPerson(patient);
        cultureMethod.setCreator(user);
        cultureMethod.setUuid(UUID.randomUUID().toString());
        if (cultureMethod.getConcept() == null)
            throw new RuntimeException("cultureMethod id is null");
        
        typeOfOrganism.setConcept(mu.getConceptTypeOfOrganism());
        typeOfOrganism.setVoided(false);
        typeOfOrganism.setDateCreated(new Date());
        typeOfOrganism.setPerson(patient);
        typeOfOrganism.setCreator(user);
        typeOfOrganism.setUuid(UUID.randomUUID().toString());
        if (typeOfOrganism.getConcept() == null)
            throw new RuntimeException("typeOfOrganism id is null");
        
        typeOfOrganismNonCoded.setConcept(mu.getConceptTypeOfOrganismNonCoded());
        typeOfOrganismNonCoded.setVoided(false);
        typeOfOrganismNonCoded.setDateCreated(new Date());
        typeOfOrganismNonCoded.setPerson(patient);
        typeOfOrganismNonCoded.setCreator(user);
        typeOfOrganismNonCoded.setUuid(UUID.randomUUID().toString());
        if (typeOfOrganismNonCoded.getConcept() == null)
            throw new RuntimeException("typeOfOrganismNonCoded id is null");
        
        cultureParentObs.setConcept(mu.getConceptCultureParent());
        cultureParentObs.setVoided(false);
        cultureParentObs.setDateCreated(new Date());
        cultureParentObs.setPerson(patient);
        cultureParentObs.setCreator(user);
        cultureParentObs.setUuid(UUID.randomUUID().toString());
        if (cultureParentObs.getConcept() == null)
            throw new RuntimeException("cultureParentObs id is null");
    }
    
    public void setTypeOfOrganism(Obs o){
        typeOfOrganism = o;
    }
    
    public Obs getTypeOfOrganism(){
        return typeOfOrganism;
    }
    
    public void setTypeOfOrganismNonCoded(Obs o){
        typeOfOrganismNonCoded = o;
    }
    
    public Obs getTypeOfOrganismNonCoded(){
        return typeOfOrganismNonCoded;
    }
    
    public void setCultureResult(Obs o){
        cultureResult = o;
    }
    public Obs getCultureResult(){
        return cultureResult;
    }
    public void setSource(Obs o){
        source = o;
    }
    public Obs getSource(){
        return source;
    }
    public void setColonies(Obs o){
        colonies = o;
    }
    public Obs getColonies(){
        return colonies;
    }
    public void setCultureStartDate(Obs o){
        cultureStartDate = o;
    }
    public Obs getCultureStartDate(){
        return cultureStartDate;
    }
    public void setCultureResultsDate(Obs o){
        cultureResultsDate = o;
    }
    public Obs getCultureResultsDate(){
        return cultureResultsDate;
    }
    public void setCultureDateReceived(Obs o){
        cultureDateReceived = o;
    }
    public Obs getCultureDateReceived(){
        return cultureDateReceived;
    }
    public void setCultureMethod(Obs o){
        cultureMethod = o;
    }
    public Obs getCultureMethod(){
        return cultureMethod;
    }

    public Obs getCultureParentObs() {
        return cultureParentObs;
    }

    public void setCultureParentObs(Obs cultureParentObs) {
        this.cultureParentObs = cultureParentObs;
    }
    
    public void removeAllObs(){
        
        cultureResult = null;
        source = null;
        colonies = null;
        cultureStartDate = null;
        cultureResultsDate = null;
        cultureDateReceived = null;
        cultureMethod = null;
        typeOfOrganism = null;
        typeOfOrganismNonCoded = null;
        cultureParentObs = null;
        
    }
    
}
