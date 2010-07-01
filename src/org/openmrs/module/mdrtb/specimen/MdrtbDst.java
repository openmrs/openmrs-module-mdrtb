package org.openmrs.module.mdrtb.specimen;

import java.util.List;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a dst
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the dst data in obsgroups
 */
public interface MdrtbDst extends MdrtbTest {
	/**
	 * Data points this interface provides access to:
	 * 
	 * method: the Concept that represents the method used when performing the test
	 * direct: whether this was a direct or indirect test
	 * organism: the type of organism present
	 * coloniesInControl: the colonies in the control
	 * 
	 * results: returns a set of all the dst results for this test	
	 * 
	 */
	
	public Concept getMethod();
	public void setMethod(Concept method);
	
	public Boolean getDirect();
	public void setDirect(Boolean direct);
	
	public Concept getOrganismType();
	public void setOrganismType(Concept organismType);
	
	public Double getColoniesInControl();
	public void setColoniesInControl(Double coloniesInControl);

	public List<MdrtbDstResult> getResults();
	public MdrtbDstResult addResult();
}

