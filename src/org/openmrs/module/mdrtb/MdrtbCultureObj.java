package org.openmrs.module.mdrtb;


import java.util.Date;
import java.util.UUID;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * Class representing a TB Culture
 */
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
        this.cultureResult.setConcept(mu.getConceptCultureResult());  
        this.cultureResult.setVoided(false);
        this.cultureResult.setDateCreated(new Date());
        this.cultureResult.setPerson(patient);
        this.cultureResult.setCreator(user);
        this.cultureResult.setUuid(UUID.randomUUID().toString());
        
        this.source.setConcept(mu.getConceptSampleSource());       
        this.source.setVoided(false);
        this.source.setDateCreated(new Date());
        this.source.setPerson(patient);
        this.source.setCreator(user);
        this.source.setUuid(UUID.randomUUID().toString());
        
        this.colonies.setConcept(mu.getConceptColonies());        
        this.colonies.setVoided(false);
        this.colonies.setDateCreated(new Date());
        this.colonies.setPerson(patient);
        this.colonies.setCreator(user);
        this.colonies.setUuid(UUID.randomUUID().toString());
        
        //STR_CULTURE_START_DATE
        this.cultureStartDate.setConcept(mu.getConceptCultureStartDate());       
        this.cultureStartDate.setVoided(false);
        this.cultureStartDate.setDateCreated(new Date());
        this.cultureStartDate.setPerson(patient);
        this.cultureStartDate.setCreator(user);
        this.cultureStartDate.setUuid(UUID.randomUUID().toString());
       
        this.cultureResultsDate.setConcept(mu.getConceptResultDate());     
        this.cultureResultsDate.setVoided(false);
        this.cultureResultsDate.setDateCreated(new Date());
        this.cultureResultsDate.setPerson(patient);
        this.cultureResultsDate.setCreator(user);
        this.cultureResultsDate.setUuid(UUID.randomUUID().toString());
        
        this.cultureDateReceived.setConcept(mu.getConceptDateReceived());
        this.cultureDateReceived.setVoided(false);
        this.cultureDateReceived.setDateCreated(new Date());
        this.cultureDateReceived.setPerson(patient);
        this.cultureDateReceived.setCreator(user);
        this.cultureDateReceived.setUuid(UUID.randomUUID().toString());
      
        this.cultureMethod.setConcept( mu.getConceptCultureMethod());
        this.cultureMethod.setVoided(false);
        this.cultureMethod.setDateCreated(new Date());
        this.cultureMethod.setPerson(patient);
        this.cultureMethod.setCreator(user);
        this.cultureMethod.setUuid(UUID.randomUUID().toString());
        
        this.typeOfOrganism.setConcept(mu.getConceptTypeOfOrganism());
        this.typeOfOrganism.setVoided(false);
        this.typeOfOrganism.setDateCreated(new Date());
        this.typeOfOrganism.setPerson(patient);
        this.typeOfOrganism.setCreator(user);
        this.typeOfOrganism.setUuid(UUID.randomUUID().toString());
        
        this.typeOfOrganismNonCoded.setConcept(mu.getConceptTypeOfOrganismNonCoded());
        this.typeOfOrganismNonCoded.setVoided(false);
        this.typeOfOrganismNonCoded.setDateCreated(new Date());
        this.typeOfOrganismNonCoded.setPerson(patient);
        this.typeOfOrganismNonCoded.setCreator(user);
        this.typeOfOrganismNonCoded.setUuid(UUID.randomUUID().toString());
        
        this.cultureParentObs.setConcept(mu.getConceptCultureParent());
        this.cultureParentObs.setVoided(false);
        this.cultureParentObs.setDateCreated(new Date());
        this.cultureParentObs.setPerson(patient);
        this.cultureParentObs.setCreator(user);
        this.cultureParentObs.setUuid(UUID.randomUUID().toString());
    }
    
    public void setTypeOfOrganism(Obs o){
        this.typeOfOrganism = o;
    }
    
    public Obs getTypeOfOrganism(){
        return this.typeOfOrganism;
    }
    
    public void setTypeOfOrganismNonCoded(Obs o){
        this.typeOfOrganismNonCoded = o;
    }
    
    public Obs getTypeOfOrganismNonCoded(){
        return this.typeOfOrganismNonCoded;
    }
    
    public void setCultureResult(Obs o){
        this.cultureResult = o;
    }
    public Obs getCultureResult(){
        return this.cultureResult;
    }
    public void setSource(Obs o){
        this.source = o;
    }
    public Obs getSource(){
        return this.source;
    }
    public void setColonies(Obs o){
        this.colonies = o;
    }
    public Obs getColonies(){
        return this.colonies;
    }
    public void setCultureStartDate(Obs o){
        this.cultureStartDate = o;
    }
    public Obs getCultureStartDate(){
        return this.cultureStartDate;
    }
    public void setCultureResultsDate(Obs o){
        this.cultureResultsDate = o;
    }
    public Obs getCultureResultsDate(){
        return this.cultureResultsDate;
    }
    public void setCultureDateReceived(Obs o){
        this.cultureDateReceived = o;
    }
    public Obs getCultureDateReceived(){
        return this.cultureDateReceived;
    }
    public void setCultureMethod(Obs o){
        this.cultureMethod = o;
    }
    public Obs getCultureMethod(){
        return this.cultureMethod;
    }

    public Obs getCultureParentObs() {
        return cultureParentObs;
    }

    public void setCultureParentObs(Obs cultureParentObs) {
        this.cultureParentObs = cultureParentObs;
    }
    
}
