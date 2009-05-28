package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;


/**
 * Class representing a single row in a DST result
 */
public class MdrtbDSTResultObj {

    private Obs colonies = new Obs();
    private Obs concentration = new Obs();
    private Obs drug = new Obs();
    private Obs dstResultParentObs = new Obs();
    
    private ConceptService cs = Context.getConceptService();
    
    public MdrtbDSTResultObj(){}
    
    /**
     * Constructor to initialize the Obs
     * drug.valueCoded gets set to the drug concept, and then the user either sets the concept for the drug Obs 
     * to sensitive, intermediate, or resistant.
     * 
     * @param STR_DRUG
     * @param STR_CONCENTRATION
     * @param STR_COLONIES
     */
    public MdrtbDSTResultObj(Concept drugConcept,String STR_CONCENTRATION, String STR_COLONIES, String STR_DST_RESULT_PARENT, Patient patient, User user){
       
        

        drug.setDateCreated(new Date());
        drug.setVoided(false);
        drug.setValueCoded(drugConcept);
        drug.setCreator(user);
        drug.setPerson(patient);
        concentration.setDateCreated(new Date());
        concentration.setVoided(false);
        
        concentration.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_CONCENTRATION, new Locale("en", "US")));
        concentration.setCreator(user);
        concentration.setPerson(patient);
       
        colonies.setConcept( MdrtbUtil.getMDRTBConceptByName(STR_COLONIES, new Locale("en", "US"))); 
        colonies.setVoided(false);
        colonies.setDateCreated(new Date());
        colonies.setCreator(user);
        colonies.setPerson(patient);

        dstResultParentObs.setConcept(MdrtbUtil.getMDRTBConceptByName(STR_DST_RESULT_PARENT, new Locale("en", "US")));
        dstResultParentObs.setVoided(false);
        dstResultParentObs.setDateCreated(new Date());
        dstResultParentObs.setCreator(user);
        dstResultParentObs.setPerson(patient);
    }
    
    public void setColonies(Obs o){
        this.colonies = o;
    }
    public Obs getColonies(){
        return this.colonies;
    }

    public Obs getDrug() {
        return drug;
    }
    public void setDrug(Obs obs) {
        this.drug = obs;
    }
    public Obs getConcentration() {
        return concentration;
    }
    public void setConcentration(Obs concentration) {
        this.concentration = concentration;
    }
    
    public void setDrugConcept(Concept c){
        this.drug.setConcept(c);
    }
    
    public void setNumberOfColonies(Double x){
        this.colonies.setValueNumeric(x);
    }

    public void setConcentrationValue(Double x){
        this.concentration.setValueNumeric(x);
    }
    
    public void setEncounterForAllObs(Encounter enc){
        drug.setEncounter(enc);
        colonies.setEncounter(enc);
        concentration.setEncounter(enc);
    }

    public Obs getDstResultParentObs() {
        return dstResultParentObs;
    }

    public void setDstResultParentObs(Obs dstResultParentObs) {
        this.dstResultParentObs = dstResultParentObs;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof MdrtbDSTResultObj) {
            MdrtbDSTResultObj mdro = (MdrtbDSTResultObj) obj;
            
            return (this.colonies.equals(mdro.getColonies())
                    && this.concentration .equals(mdro.getConcentration())
                    && this.drug.equals(mdro.getDrug())
                    && this.dstResultParentObs .equals(mdro.getDstResultParentObs()));
        }
        
        // if personId is null for either object, for equality the
        // two objects must be the same
        return this == obj;
    }
    
}
