package org.openmrs.module.mdrtb.web.controller.attribute;

import org.openmrs.Concept;


/**
 * Simple object used to put potential drug types into the model
 */

public class DrugTypeModelAttribute implements Comparable<DrugTypeModelAttribute> {

	Concept drug;
	
	Double concentration;
	
	String key;

	public DrugTypeModelAttribute() {	
		// generic constructor
	}
	
	public DrugTypeModelAttribute(Concept drug) {
	    this.drug = drug;
	    this.concentration = null;
	    createKey();
    }
	
	public DrugTypeModelAttribute(Concept drug, Double concentration) {
	    this.drug = drug;
	    this.concentration = concentration;
	    createKey();
    }

	
    public Concept getDrug() {
    	return drug;
    }

	
    public void setDrug(Concept drug) {
    	this.drug = drug;
    	createKey();
    }

	
    public Double getConcentration() {
    	return concentration;
    }

	
    public void setConcentration(Double concentration) {
    	this.concentration = concentration;
    	createKey();
    }

	
    public String getKey() {
    	return key;
    }
    
    /**
	 * Implementation of comparable method
	 */
	public int compareTo(DrugTypeModelAttribute drugTypeToCompare) {
		return this.getKey().compareTo(drugTypeToCompare.getKey());
	}
    
    /**
     * Utility methods
     */
	
    private void createKey() {
    	if (this.concentration != null) {
    		this.key = this.drug.getId().toString() + "|" + this.concentration.toString();
    	}
    	else {
    		this.key = this.drug.getId().toString();
    	}
    }
}
