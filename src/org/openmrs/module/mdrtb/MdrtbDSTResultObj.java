package org.openmrs.module.mdrtb;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;


/**
 * Class representing a single row in a DST result
 */
public class MdrtbDSTResultObj {

    private Obs colonies;
    private Obs concentration;
    private Obs drug;
    private Obs dstResultParentObs;
    
    
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
    public MdrtbDSTResultObj(Concept drugConcept,Patient patient, User user, MdrtbFactory mu){
       
        drug = new Obs();
        concentration = new Obs();
        colonies = new Obs();
        dstResultParentObs = new Obs();

        drug.setDateCreated(new Date());
        drug.setVoided(false);
        drug.setValueCoded(drugConcept);
        drug.setCreator(user);
        drug.setPerson(patient);
        concentration.setDateCreated(new Date());
        concentration.setVoided(false);
        
        concentration.setConcept(mu.getConceptConcentration());
        concentration.setCreator(user);
        concentration.setPerson(patient);
       
        colonies.setConcept( mu.getConceptColonies()); 
        colonies.setVoided(false);
        colonies.setDateCreated(new Date());
        colonies.setCreator(user);
        colonies.setPerson(patient);

        dstResultParentObs.setConcept(mu.getConceptDSTResultParent());
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
