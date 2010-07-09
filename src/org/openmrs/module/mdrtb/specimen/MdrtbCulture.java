package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a culture
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface MdrtbCulture extends MdrtbTest {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * result: the Concept that represents the result of the culture
	 * colonies: the # of colonies present
	 * method: the Concept that represents the method used when performing the test
	 * organism: the type of organism present
	 * 
	 */
	
	public Concept getResult();
	public void setResult(Concept result);
	
	public Integer getColonies();
	public void setColonies(Integer colonies);
	
	public Concept getMethod();
	public void setMethod(Concept method);
	
	public Concept getOrganismType();
	public void setOrganismType(Concept organismType);
}
