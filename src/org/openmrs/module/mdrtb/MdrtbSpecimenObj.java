package org.openmrs.module.mdrtb;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Obs;

/**
 * Represents a specimen and holds the results of the various tests that can be performed on it
 */
public class MdrtbSpecimenObj {
	
	// the id and type of the specimen
	private Obs id;
	private Obs type;
	
	// the encounter this specimen is associated with
	private Encounter encounter; 
	
	// the various tests associated with the specimen
	private List<MdrtbSmearObj> smears;
	private List<MdrtbCultureObj> cultures;
	private List<MdrtbDSTObj> dsts;
	

	public MdrtbSpecimenObj () {
		//generic constructor
	}
	

	/*
	 * Getters and Setters
	 */
	
    public Obs getId() {
    	return id;
    }


	
    public void setId(Obs id) {
    	this.id = id;
    }


	
    public Obs getType() {
    	return type;
    }


	
    public void setType(Obs type) {
    	this.type = type;
    }


	
    public Encounter getEncounter() {
    	return encounter;
    }


	
    public void setEncounter(Encounter encounter) {
    	this.encounter = encounter;
    }


	
    public List<MdrtbSmearObj> getSmears() {
    	return smears;
    }


	
    public void setSmears(List<MdrtbSmearObj> smears) {
    	this.smears = smears;
    }


	
    public List<MdrtbCultureObj> getCultures() {
    	return cultures;
    }


	
    public void setCultures(List<MdrtbCultureObj> cultures) {
    	this.cultures = cultures;
    }


	
    public List<MdrtbDSTObj> getDsts() {
    	return dsts;
    }


	
    public void setDsts(List<MdrtbDSTObj> dsts) {
    	this.dsts = dsts;
    }
}
