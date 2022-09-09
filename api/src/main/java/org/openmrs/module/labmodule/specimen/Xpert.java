package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a smear
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface Xpert extends Test {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * bacilli: the bacilli count
	 * method: the Concept that represents the method used when performing the test
	 * 
	 */
	
	public Concept getResult();
	public void setResult(Concept mtbResult);
	
	public Concept getRifResistance();
	public void setRifResistance(Concept rifResistance);
	
	public String getErrorCode();
	public void setErrorCode(String code);
	
	public Concept getMtbBurden();
	public void setMtbBurden(Concept mtbBurden);
	
	public Concept getSentToCulture();
	public void setSentToCulture(Concept cultureSent);
	
	public Concept getSentToDst();
	public void setSentToDst(Concept dstSent);
	
	public Date getSentToCultureDate();
	public void setSentToCultureDate(Date cultureSentDate);
	
	public Date getSentToDstDate();
	public void setSentToDstDate(Date dstSentDate);
	
}
