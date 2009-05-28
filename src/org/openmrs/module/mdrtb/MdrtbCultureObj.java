package org.openmrs.module.mdrtb;


import java.util.Date;
import java.util.Locale;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

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
    
    private ConceptService cs = Context.getConceptService();
    
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
    public MdrtbCultureObj(String STR_TB_CULTURE_RESULT, 
                           String STR_TB_SAMPLE_SOURCE,
                           String STR_COLONIES,
                           String STR_CULTURE_START_DATE,
                           String STR_RESULT_DATE,
                           String STR_DATE_RECEIVED, 
                           String STR_TB_CULTURE_METHOD, 
                           String STR_TYPE_OF_ORGANISM, 
                           String STR_TYPE_OF_ORGANISM_NON_CODED,
                           String STR_CULTURE_PARENT,
                           Patient patient,
                           User user){

        this.cultureResult.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TB_CULTURE_RESULT, new Locale("en", "US")));
        
        this.cultureResult.setVoided(false);
        this.cultureResult.setDateCreated(new Date());
        this.cultureResult.setPerson(patient);
        this.cultureResult.setCreator(user);
        
        this.source.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TB_SAMPLE_SOURCE, new Locale("en", "US")));       
        this.source.setVoided(false);
        this.source.setDateCreated(new Date());
        this.source.setPerson(patient);
        this.source.setCreator(user);
        
        this.colonies.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_COLONIES, new Locale("en", "US")));        
        this.colonies.setVoided(false);
        this.colonies.setDateCreated(new Date());
        this.colonies.setPerson(patient);
        this.colonies.setCreator(user);
        
        this.cultureStartDate.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_CULTURE_START_DATE, new Locale("en", "US")));       
        this.cultureStartDate.setVoided(false);
        this.cultureStartDate.setDateCreated(new Date());
        this.cultureStartDate.setPerson(patient);
        this.cultureStartDate.setCreator(user);
       
        this.cultureResultsDate.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_RESULT_DATE, new Locale("en", "US")));     
        this.cultureResultsDate.setVoided(false);
        this.cultureResultsDate.setDateCreated(new Date());
        this.cultureResultsDate.setPerson(patient);
        this.cultureResultsDate.setCreator(user);
        
        this.cultureDateReceived.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_DATE_RECEIVED, new Locale("en", "US")));
        this.cultureDateReceived.setVoided(false);
        this.cultureDateReceived.setDateCreated(new Date());
        this.cultureDateReceived.setPerson(patient);
        this.cultureDateReceived.setCreator(user);
      
        this.cultureMethod.setConcept( MdrtbUtil.getMDRTBConceptByName(STR_TB_CULTURE_METHOD, new Locale("en", "US")));
        this.cultureMethod.setVoided(false);
        this.cultureMethod.setDateCreated(new Date());
        this.cultureMethod.setPerson(patient);
        this.cultureMethod.setCreator(user);
        
        this.typeOfOrganism.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TYPE_OF_ORGANISM, new Locale("en", "US")));
        this.typeOfOrganism.setVoided(false);
        this.typeOfOrganism.setDateCreated(new Date());
        this.typeOfOrganism.setPerson(patient);
        this.typeOfOrganism.setCreator(user);
        
        this.typeOfOrganismNonCoded.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TYPE_OF_ORGANISM_NON_CODED, new Locale("en", "US")));
        this.typeOfOrganismNonCoded.setVoided(false);
        this.typeOfOrganismNonCoded.setDateCreated(new Date());
        this.typeOfOrganismNonCoded.setPerson(patient);
        this.typeOfOrganismNonCoded.setCreator(user);
        
        this.cultureParentObs.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_CULTURE_PARENT, new Locale("en", "US")));
        this.cultureParentObs.setVoided(false);
        this.cultureParentObs.setDateCreated(new Date());
        this.cultureParentObs.setPerson(patient);
        this.cultureParentObs.setCreator(user);
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
