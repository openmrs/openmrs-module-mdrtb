package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;


/**
 * Class representing a single row in a DST result
 */

@Deprecated
public class MdrtbDSTResultObj {

    private Obs colonies = new Obs();
    private Obs concentration = new Obs();
    private Obs drug = new Obs();
    private Obs dstResultParentObs = new Obs();
    
    
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

        this.drug.setDateCreated(new Date());
        this.drug.setVoided(false);
        this.drug.setValueCoded(drugConcept);
        this.drug.setCreator(user);
        this.drug.setPerson(patient);
        this.drug.setUuid(UUID.randomUUID().toString());
        
        this.concentration.setDateCreated(new Date());
        this.concentration.setVoided(false);
        this.concentration.setConcept(mu.getConceptConcentration());
        this.concentration.setCreator(user);
        this.concentration.setPerson(patient);
        this.concentration.setUuid(UUID.randomUUID().toString());
       
        this.colonies.setConcept( mu.getConceptColonies()); 
        this.colonies.setVoided(false);
        this.colonies.setDateCreated(new Date());
        this.colonies.setCreator(user);
        this.colonies.setPerson(patient);
        this.colonies.setUuid(UUID.randomUUID().toString());

        this.dstResultParentObs.setConcept(mu.getConceptDSTResultParent());
        this.dstResultParentObs.setVoided(false);
        this.dstResultParentObs.setDateCreated(new Date());
        this.dstResultParentObs.setCreator(user);
        this.dstResultParentObs.setPerson(patient);
        this.dstResultParentObs.setUuid(UUID.randomUUID().toString());
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
