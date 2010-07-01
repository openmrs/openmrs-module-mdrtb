package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a DST result
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface MdrtbDstResult {

	/**
	 * Data points this interface provides access to:
	 * 
	 * drug: the drug this test refers to
	 * concentration: the concentration of the drug
	 * colonies: the colonies present (after testing?)
	 * result: coded result of the test
	 * 
	 */
	
	public Concept getDrug();
	public void setDrug(Concept drug);
	
	public Double getConcentration();
	public void setConcentration(Double concentration);
	
	public Concept getResult();
	public void setResult(Concept result);
	
	public Double getColonies();
	public void setColonies(Double colonies);
	
}
