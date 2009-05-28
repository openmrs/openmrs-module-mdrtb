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
    public MdrtbNewTestObj(String STR_TB_SMEAR_RESULT, 
                            String STR_TB_SAMPLE_SOURCE, 
                            String STR_BACILLI,
                            String STR_RESULT_DATE, 
                            String STR_DATE_RECEIVED, 
                            String STR_TB_SMEAR_MICROSCOPY_METHOD,
                            String STR_TB_CULTURE_RESULT, 
                            String STR_COLONIES, 
                            String STR_CULTURE_START_DATE, 
                            String STR_TB_CULTURE_METHOD, 
                            String STR_TYPE_OF_ORGANISM, 
                            String STR_TYPE_OF_ORGANISM_NON_CODED,
                            String STR_DST_COMPLETE, 
                            String STR_DST_METHOD, 
                            String STR_DIRECT_INDIRECT, 
                            String STR_COLONIES_IN_CONTROL,
                            String STR_CONCENTRATION,
                            String STR_SMEAR_PARENT,
                            String STR_CULTURE_PARENT,
                            String STR_DST_PARENT,
                            String STR_DST_RESULT_PARENT,
                            String STR_SPUTUM_COLLECTION_DATE,
                            Patient patient, 
                            User user){
        
        String numNewTests = as.getGlobalProperty("mdrtb.max_num_bacteriologies_or_dsts_to_add_at_once");
        try{
            Integer maxNum = Integer.valueOf(numNewTests);
            for (int i = 0; i < maxNum; i++){
                this.smears.add(new MdrtbSmearObj(STR_TB_SMEAR_RESULT, STR_TB_SAMPLE_SOURCE, STR_BACILLI, STR_RESULT_DATE, STR_DATE_RECEIVED, STR_TB_SMEAR_MICROSCOPY_METHOD, STR_SMEAR_PARENT, patient, user));
                this.cultures.add(new MdrtbCultureObj(STR_TB_CULTURE_RESULT, STR_TB_SAMPLE_SOURCE, STR_COLONIES, STR_CULTURE_START_DATE, STR_RESULT_DATE, STR_DATE_RECEIVED, STR_TB_CULTURE_METHOD, STR_TYPE_OF_ORGANISM, STR_TYPE_OF_ORGANISM_NON_CODED, STR_CULTURE_PARENT, patient, user));
                this.dsts.add(new MdrtbDSTObj(STR_DST_COMPLETE, STR_CULTURE_START_DATE, STR_RESULT_DATE, STR_DATE_RECEIVED, STR_TB_SAMPLE_SOURCE, STR_DST_METHOD, STR_TYPE_OF_ORGANISM, STR_TYPE_OF_ORGANISM_NON_CODED, STR_DIRECT_INDIRECT, STR_COLONIES_IN_CONTROL, STR_CONCENTRATION, STR_COLONIES, STR_DST_PARENT, STR_DST_RESULT_PARENT,STR_SPUTUM_COLLECTION_DATE, patient, user));
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
