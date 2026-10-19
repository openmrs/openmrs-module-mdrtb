package org.openmrs.module.mdrtb.form;

import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;

public interface SimpleForm {
	
	Integer getId();
	
	void setEncounter(Encounter encounter);
	
	Encounter getEncounter();
	
	Person getProvider();
	
	void setProvider(Person provider);
	
	Patient getPatient();
	
	void setPatient(Patient patient);
	
	Date getEncounterDatetime();
	
	void setEncounterDatetime(Date date);
	
	Location getLocation();
	
	void setLocation(Location location);
	
	String getWeight();
	
	void setWeight(String weight);
	
	String getPulse();
	
	void setPulse(String pulse);
	
	String getTemperature();
	
	void setTemperature(String temperature);
	
	String getSystolicBloodPressure();
	
	void setSystolicBloodPressure(String pressure);
	
	String getRespiratoryRate();
	
	void setRespiratoryRate(String rate);
	
	String getClinicianNotes();
	
	void setClinicianNotes(String comments);
}
