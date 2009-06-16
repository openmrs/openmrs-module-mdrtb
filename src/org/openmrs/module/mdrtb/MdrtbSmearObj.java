package org.openmrs.module.mdrtb;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * A class representing a smear result.  Contains all Obs for all observations containined in a smear result.
 */

public class MdrtbSmearObj {
    private Obs smearResult = new Obs();
    private Obs source = new Obs();
    private Obs bacilli = new Obs();
    private Obs smearResultDate = new Obs();
    private Obs smearDateReceived = new Obs();
    private Obs smearMethod = new Obs();
    private Obs smearParentObs = new Obs();
    
  
    public MdrtbSmearObj() {}
    
    /**
     * Constructor that initializes this class with all of the concepts attached to Obs
     * 
     * @param STR_TB_SMEAR_RESULT
     * @param STR_TB_SAMPLE_SOURCE
     * @param STR_BACILLI
     * @param STR_RESULT_DATE
     * @param STR_DATE_RECEIVED
     * @param STR_TB_SMEAR_MICROSCOPY_METHOD
     */
    public MdrtbSmearObj(Patient patient, User user, MdrtbFactory mu){

        this.smearResult.setConcept(mu.getConceptSmearResult()); 
        this.smearResult.setVoided(false);
        this.smearResult.setDateCreated(new Date());
        this.smearResult.setPerson(patient);
        this.smearResult.setCreator(user);
        
        this.source.setConcept(mu.getConceptSampleSource() ); 
        this.source.setVoided(false);
        this.source.setDateCreated(new Date());
        this.source.setPerson(patient);
        this.source.setCreator(user);
        
        this.bacilli.setConcept(mu.getConceptBacilli()); 
        this.bacilli.setVoided(false);
        this.bacilli.setDateCreated(new Date());
        this.bacilli.setPerson(patient);
        this.bacilli.setCreator(user);

        this.smearResultDate.setConcept(mu.getConceptResultDate()); 
        this.smearResultDate.setVoided(false);
        this.smearResultDate.setDateCreated(new Date());
        this.smearResultDate.setPerson(patient);
        this.smearResultDate.setCreator(user);
        
        //STR_DATE_RECEIVED
        this.smearDateReceived.setConcept(mu.getConceptDateReceived());
        this.smearDateReceived.setVoided(false);
        this.smearDateReceived.setDateCreated(new Date());
        this.smearDateReceived.setPerson(patient);
        this.smearDateReceived.setCreator(user);
        
        //STR_TB_SMEAR_MICROSCOPY_METHOD
        this.smearMethod.setConcept(mu.getConceptSmearMicroscopyMethod());
        this.smearMethod.setVoided(false);
        this.smearMethod.setDateCreated(new Date());
        this.smearMethod.setPerson(patient);
        this.smearMethod.setCreator(user);
        this.smearParentObs.setVoided(false);
        this.smearParentObs.setDateCreated(new Date());
    
        //STR_SMEAR_PARENT
        this.smearParentObs.setConcept(mu.getConceptSmearParent());
        this.smearParentObs.setPerson(patient);
        this.smearParentObs.setCreator(user);
    }
    
    public void setSmearParentObs(Obs o){
        this.smearParentObs = o;
    }
    
    public Obs getSmearParentObs(){
        return this.smearParentObs;
    }
    
    public void setSmearResult(Obs o){
        this.smearResult = o;
    }
    public Obs getSmearResult(){
        return this.smearResult;
    }
    public void setSource(Obs o){
        this.source = o;
    }
    public Obs getSource(){
        return this.source;
    }
    public void setBacilli(Obs o){
        this.bacilli = o;
    }
    public Obs getBacilli(){
        return this.bacilli;
    }
    public void setSmearResultDate(Obs o){
        this.smearResultDate = o;
    }
    public Obs getSmearResultDate(){
        return this.smearResultDate;
    }
    public void setSmearDateReceived(Obs o){
        this.smearDateReceived = o;
    }
    public Obs getSmearDateReceived(){
        return this.smearDateReceived;
    }
    public void setSmearMethod(Obs o){
        this.smearMethod = o;
    }
    public Obs getSmearMethod(){
        return this.smearMethod;
    }
    
}
