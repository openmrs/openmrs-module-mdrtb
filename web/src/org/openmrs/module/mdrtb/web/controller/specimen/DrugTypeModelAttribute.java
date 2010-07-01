package org.openmrs.module.mdrtb.web.controller.specimen;

import org.openmrs.Concept;


/**
 * Simple object used to put potential drug types into the model
 */
public class DrugTypeModelAttribute {

	Concept drug;
	
	Double concentration;

	public DrugTypeModelAttribute() {	
		// generic constructor
	}
	
	public DrugTypeModelAttribute(Concept drug) {
	    this.drug = drug;
	    this.concentration = null;
    }
	
	public DrugTypeModelAttribute(Concept drug, Double concentration) {
	    this.drug = drug;
	    this.concentration = concentration;
    }

	
    public Concept getDrug() {
    	return drug;
    }

	
    public void setDrug(Concept drug) {
    	this.drug = drug;
    }

	
    public Double getConcentration() {
    	return concentration;
    }

	
    public void setConcentration(Double concentration) {
    	this.concentration = concentration;
    }
	
	
	
}
