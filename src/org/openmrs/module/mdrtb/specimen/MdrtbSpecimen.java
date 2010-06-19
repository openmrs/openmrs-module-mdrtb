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
 * the messiness of storing the specimen data in nested obsgroups
 */
public interface MdrtbSpecimen {

	/**
	 * Data points this interface provides access to
	 * 
	 * id: the id code of the specimen
	 * type: the concept that represents the type of the specimen (sputum, etc...)
	 * patient: the patient associated with the specimen
	 * location: the location where the specimen was collected
	 * provider: who collected the specimen
	 * dateCollected: the date the specimen was collected
	 * 
	 * smears: a list of the smears associated with the specimen
	 * cultures: a list of the cultures associated with the specimen
	 * dsts: a list of the DSTS associated with the specimen
	 * 
	 * This interface also defines a "getSpecimen" method, intended to be used
	 * by a service to retrieve the underlying object or objects that need to be saved
	 * to persist this specimen, and a "getSpecimenID", used to retrieve whatever ID
	 * is used to reference the specimen (in the standard implementation, it's the 
	 * id of the underlying encounter)
	 */

	public List<Object> getSpecimen();
	public String getSpecimenId();      
	
	public String getIdentifier();
	public void setIdentifier(String id);
	
	public Concept getType();
	public void setType(Concept type);
	
	public Patient getPatient();
	public void setPatient(Patient patient);
	
	public Location getLocation();
	public void setLocation(Location location);
	
	public Person getProvider();
	public void setProvider(Person provider);
	
	public Date getDateCollected();
	public void setDateCollected(Date dateCollected);
	
	public List<MdrtbSmear> getSmears();
	public void setSmears(List<MdrtbSmear> smears);
	public MdrtbSmear addSmear();
	public void removeSmear(MdrtbSmear smear);
	
	public List<MdrtbCulture> getCultures();
	public void setCultures(List<MdrtbCulture> cultures);
	public void addCulture(MdrtbCulture culture);
	public void removeCulture(MdrtbCulture culture);
	
	public List<MdrtbDst> getDsts();
	public void setDsts(List<MdrtbDst> dsts);
	public void addDst(MdrtbDst dst);
	public void removeDst(MdrtbDst dst);
	
}
