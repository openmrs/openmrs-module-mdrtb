package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * This method is to be used as the form backing object for adding a new bacteriology or DST in the 
 * mdrtb module.  In other words, this class represents everything that you would want to do with a sputum sample for MDRTB.
 */

public class MdrtbNewTestObj {
    private Patient patient;
    private List<MdrtbSmearObj> smears = new ArrayList<MdrtbSmearObj>();
    private List<MdrtbCultureObj> cultures = new ArrayList<MdrtbCultureObj>();
    private List<MdrtbDSTObj> dsts = new ArrayList<MdrtbDSTObj>();
    private List<Encounter> encounters = new ArrayList<Encounter>();



    private AdministrationService as = Context.getAdministrationService();
    
    /**
     * no-arg constructor
     */
    
    public MdrtbNewTestObj(){}
    /**
     * Constructor that adds all child Obs for X blank DSTs, cultures, and smears, where X is set by the global property
     * mdrtb.max_num_bacteriologies_or_dsts_to_add_at_once
     * @param STR_TB_SMEAR_RESULT
     * @param STR_TB_SAMPLE_SOURCE
     * @param STR_BACILLI
     * @param STR_RESULT_DATE
     * @param STR_DATE_RECEIVED
     * @param STR_TB_SMEAR_MICROSCOPY_METHOD
     * @param STR_TB_CULTURE_RESULT
     * @param STR_COLONIES
     * @param STR_CULTURE_START_DATE
     * @param STR_TB_CULTURE_METHOD
     * @param STR_TYPE_OF_ORGANISM
     * @param STR_TYPE_OF_ORGANISM_NON_CODED
     * @param STR_DST_COMPLETE
     * @param STR_DST_METHOD
     * @param STR_DIRECT_INDIRECT
     * @param STR_COLONIES_IN_CONTROL
     * @param STR_CONCENTRATION
     * @param STR_SMEAR_PARENT
     * @param STR_CULTURE_PARENT
     * @param STR_DST_PARENT
     * @param STR_DST_RESULT_PARENT
     */
    public MdrtbNewTestObj(Patient patient, User user, String view){
        
        String numNewTests = as.getGlobalProperty("mdrtb.max_num_bacteriologies_or_dsts_to_add_at_once");
        try{
            Integer maxNum = Integer.valueOf(numNewTests);
            for (int i = 0; i < maxNum; i++){
                MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                MdrtbFactory mu = ms.getMdrtbFactory();
                
                if (view.equals("DST")){
                    this.dsts.add(new MdrtbDSTObj(patient, user, mu));
                } else {
                    this.smears.add(new MdrtbSmearObj(patient, user, mu));
                    this.cultures.add(new MdrtbCultureObj( patient, user, mu));
                }
                
            }
        } catch (Exception ex){
            throw new RuntimeException("Unable to convert global property mdrtb.max_num_bacteriologies_or_dsts_to_add_at_once into an integer, or loading child Obs failed.");
        }
    }
 
    public void setSmears(List<MdrtbSmearObj> x){
        this.smears = x;
    }
    
    public List<MdrtbSmearObj> getSmears(){
        return this.smears;
    }
    
    public void setCultures(List<MdrtbCultureObj> x){
        this.cultures = x;
    }
    
    public List<MdrtbDSTObj> getDsts() {
        return dsts;
    }

    public void setDsts(List<MdrtbDSTObj> dsts) {
        this.dsts = dsts;
    }

    public List<MdrtbCultureObj> getCultures() {
        return cultures;
    }

    public void setPatient(Patient p){
        this.patient = p;
    }
    public Patient getPatient(){
        return this.patient;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }
    
    public void addSmear(MdrtbSmearObj mso){
        this.smears.add(mso);
    }
    public void addDST(MdrtbDSTObj mdo){
        this.dsts.add(mdo);
    }
    public void addCulture(MdrtbCultureObj mco){
        this.cultures.add(mco);
    }

}
