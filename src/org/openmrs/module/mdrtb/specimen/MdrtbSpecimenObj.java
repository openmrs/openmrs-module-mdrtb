package org.openmrs.module.mdrtb.specimen;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.mdrtb.MdrtbCultureObj;
import org.openmrs.module.mdrtb.MdrtbDSTObj;
import org.openmrs.module.mdrtb.MdrtbSmearObj;

/**
 * Represents a specimen and holds the results of the various tests that can be performed on it
 */
public class MdrtbSpecimenObj {
	
	// the id and type of the specimen
	private Obs id;
	private Obs type;
	
	// the encounter this specimen is associated with
	private Encounter encounter; 
	
	// TODO: these should be sorted lists?
	// the various tests associated with the specimen
	private List<MdrtbSmearObj> smears = new LinkedList<MdrtbSmearObj>();
	private List<MdrtbCultureObj> cultures = new LinkedList<MdrtbCultureObj>();
	private List<MdrtbDSTObj> dsts = new LinkedList<MdrtbDSTObj>();
	

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
    
    /**
     * Convenience methods for adding/removing smears, cultures, and DSTs
     */
    
    public void addSmear(MdrtbSmearObj smear) {
    	this.smears.add(smear);
    }
    
    public void removeSmear(MdrtbSmearObj smear) {
    	this.smears.remove(smear);
    }
    
    public void addCulture(MdrtbCultureObj culture) {
    	this.cultures.add(culture);
    }
    
    public void removeCulture(MdrtbCultureObj culture) {
    	this.cultures.remove(culture);
    }
    public void addDst(MdrtbDSTObj dst) {
    	this.dsts.add(dst);
    }
    
    public void removeDst(MdrtbDSTObj dst) {
    	this.dsts.remove(dst);
    }
}
