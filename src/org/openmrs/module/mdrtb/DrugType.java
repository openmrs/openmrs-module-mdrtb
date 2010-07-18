package org.openmrs.module.mdrtb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;


/**
 * Simple object used store info about a DST drug type
 */

public class DrugType {

	protected final Log log = LogFactory.getLog(getClass());
	
	Concept drug;
	
	Double concentration;
	
	String key;

	public DrugType() {	
		// generic constructor
	}
	
	public DrugType(Concept drug) {
	    this.drug = drug;
	    this.concentration = null;
	    createKey();
    }
	
	public DrugType(Concept drug, Double concentration) {
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
	 * Implementation of equals method
	 */
    public boolean equals(Object obj) {
		if (obj instanceof DrugType) {
			DrugType drugType = (DrugType) obj;
			
			if (getKey() != null && drugType.getKey() != null) {
				return getKey().equals(drugType.getKey());
			}
		}
		
		// if key is null for either object, for equality the
		// two objects must be the same
		return this == obj;
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
