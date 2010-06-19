package org.openmrs.module.mdrtb.specimen;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;

/**
 * Interface that defines how to interaction with a specimen
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the specimen data in obsgroups
 */
public interface MdrtbSmear {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * patient: the patient this smear is associated with
	 * result: the Concept that represents the result of the smear
	 * bacilli: the bacilli of the smear
	 * method: the Concept that represents method used when performing the test
	 * resultDate: the date of the result
	 * TODO: dateReceived: the date the specimen was received (by the lab?)
	 * lab: the lab that performed the smear
	 * 
	 */
	
	public List<Object> getSmear();
	public String getSmearId(); 
	public String getSpecimenId();
	
	public Concept getResult();
	public void setResult(Concept result);
	
	public Double getBacilli();
	public void setBacilli(Double bacilli);
	
	public Concept getMethod();
	public void setMethod(Concept method);
	
	public Date getResultDate();
	public void setResultDate(Date resultDate);
	
	public Date getDateReceived();
	public void setDateReceived(Date dateReceived);
	
	public Location getLab();
	public void setLab(Location location);
}
