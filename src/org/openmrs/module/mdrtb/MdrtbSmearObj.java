package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.UUID;

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

        smearResult.setConcept(mu.getConceptSmearResult()); 
        smearResult.setVoided(false);
        smearResult.setDateCreated(new Date());
        smearResult.setPerson(patient);
        smearResult.setCreator(user);
        smearResult.setUuid(UUID.randomUUID().toString());
        if (smearResult.getConcept() == null)
            throw new RuntimeException("smear result concept id is null");
        
        source.setConcept(mu.getConceptSampleSource() ); 
        source.setVoided(false);
        source.setDateCreated(new Date());
        source.setPerson(patient);
        source.setCreator(user);
        source.setUuid(UUID.randomUUID().toString());
        if (source.getConcept() == null)
            throw new RuntimeException("source concept id is null");
        
        bacilli.setConcept(mu.getConceptBacilli()); 
        bacilli.setVoided(false);
        bacilli.setDateCreated(new Date());
        bacilli.setPerson(patient);
        bacilli.setCreator(user);
        bacilli.setUuid(UUID.randomUUID().toString());
        if (bacilli.getConcept() == null)
            throw new RuntimeException("bacilli concept id is null");

        smearResultDate.setConcept(mu.getConceptResultDate()); 
        smearResultDate.setVoided(false);
        smearResultDate.setDateCreated(new Date());
        smearResultDate.setPerson(patient);
        smearResultDate.setCreator(user);
        smearResultDate.setUuid(UUID.randomUUID().toString());
        if (smearResult.getConcept() == null)
            throw new RuntimeException("smear result concept id is null");
        
        //STR_DATE_RECEIVED
        smearDateReceived.setConcept(mu.getConceptDateReceived());
        smearDateReceived.setVoided(false);
        smearDateReceived.setDateCreated(new Date());
        smearDateReceived.setPerson(patient);
        smearDateReceived.setCreator(user);
        smearDateReceived.setUuid(UUID.randomUUID().toString());
        if (smearDateReceived.getConcept() == null)
            throw new RuntimeException("ssmearDateReceived smear result concept id is null");
        
        //STR_TB_SMEAR_MICROSCOPY_METHOD
        smearMethod.setConcept(mu.getConceptSmearMicroscopyMethod());
        smearMethod.setVoided(false);
        smearMethod.setDateCreated(new Date());
        smearMethod.setPerson(patient);
        smearMethod.setCreator(user);
        smearMethod.setUuid(UUID.randomUUID().toString());
        if (smearMethod.getConcept() == null)
            throw new RuntimeException("smearMethod smear result concept id is null");
        
        smearParentObs.setVoided(false);
        smearParentObs.setDateCreated(new Date());
        smearParentObs.setConcept(mu.getConceptSmearParent());
        smearParentObs.setPerson(patient);
        smearParentObs.setCreator(user);
        smearParentObs.setUuid(UUID.randomUUID().toString());
        if (smearParentObs.getConcept() == null)
            throw new RuntimeException("smearParentObs smear result concept id is null");
    }
    
    public void setSmearParentObs(Obs o){
        smearParentObs = o;
    }
    
    public Obs getSmearParentObs(){
        return smearParentObs;
    }
    
    public void setSmearResult(Obs o){
        smearResult = o;
    }
    public Obs getSmearResult(){
        return smearResult;
    }
    public void setSource(Obs o){
        source = o;
    }
    public Obs getSource(){
        return source;
    }
    public void setBacilli(Obs o){
        bacilli = o;
    }
    public Obs getBacilli(){
        return bacilli;
    }
    public void setSmearResultDate(Obs o){
        smearResultDate = o;
    }
    public Obs getSmearResultDate(){
        return smearResultDate;
    }
    public void setSmearDateReceived(Obs o){
        smearDateReceived = o;
    }
    public Obs getSmearDateReceived(){
        return smearDateReceived;
    }
    public void setSmearMethod(Obs o){
        smearMethod = o;
    }
    public Obs getSmearMethod(){
        return smearMethod;
    }
    public void removeAllObs(){
        smearResult = null;
        source = null;
        bacilli = null;
        smearResultDate = null;
        smearDateReceived = null;
        smearMethod = null;
        smearParentObs = null;
    }
}
