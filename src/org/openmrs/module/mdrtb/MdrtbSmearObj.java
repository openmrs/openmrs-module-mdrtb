package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.Locale;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

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
    
    private ConceptService cs = Context.getConceptService();
  
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
    public MdrtbSmearObj(String STR_TB_SMEAR_RESULT,
                        String STR_TB_SAMPLE_SOURCE,
                        String STR_BACILLI,
                        String STR_RESULT_DATE,
                        String STR_DATE_RECEIVED,
                        String STR_TB_SMEAR_MICROSCOPY_METHOD,
                        String STR_SMEAR_PARENT, Patient patient, User user){
        
 
        this.smearResult.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TB_SMEAR_RESULT, new Locale("en", "US"))); 
        this.smearResult.setVoided(false);
        this.smearResult.setDateCreated(new Date());
        this.smearResult.setPerson(patient);
        this.smearResult.setCreator(user);
        
        this.source.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TB_SAMPLE_SOURCE, new Locale("en", "US"))); 
        this.source.setVoided(false);
        this.source.setDateCreated(new Date());
        this.source.setPerson(patient);
        this.source.setCreator(user);
        
        this.bacilli.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_BACILLI, new Locale("en", "US"))); 
        this.bacilli.setVoided(false);
        this.bacilli.setDateCreated(new Date());
        this.bacilli.setPerson(patient);
        this.bacilli.setCreator(user);

        this.smearResultDate.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_RESULT_DATE, new Locale("en", "US"))); 
        this.smearResultDate.setVoided(false);
        this.smearResultDate.setDateCreated(new Date());
        this.smearResultDate.setPerson(patient);
        this.smearResultDate.setCreator(user);
        
       
        this.smearDateReceived.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_DATE_RECEIVED, new Locale("en", "US")));
        this.smearDateReceived.setVoided(false);
        this.smearDateReceived.setDateCreated(new Date());
        this.smearDateReceived.setPerson(patient);
        this.smearDateReceived.setCreator(user);
        
       
        this.smearMethod.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_TB_SMEAR_MICROSCOPY_METHOD, new Locale("en", "US")));
        this.smearMethod.setVoided(false);
        this.smearMethod.setDateCreated(new Date());
        this.smearMethod.setPerson(patient);
        this.smearMethod.setCreator(user);
        this.smearParentObs.setVoided(false);
        this.smearParentObs.setDateCreated(new Date());
        
        
        this.smearParentObs.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_SMEAR_PARENT, new Locale("en", "US")));
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
