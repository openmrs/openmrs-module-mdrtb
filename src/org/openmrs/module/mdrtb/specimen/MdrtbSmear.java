package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a smear
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface MdrtbSmear extends MdrtbTest {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * result: the Concept that represents the result of the smear
	 * bacilli: the bacilli count
	 * method: the Concept that represents the method used when performing the test
	 * 
	 */
	
	public Concept getResult();
	public void setResult(Concept result);
	
	public Double getBacilli();
	public void setBacilli(Double bacilli);
	
	public Concept getMethod();
	public void setMethod(Concept method);
}
