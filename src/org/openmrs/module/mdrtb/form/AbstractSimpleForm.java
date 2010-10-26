package org.openmrs.module.mdrtb.form;

import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;


public abstract class AbstractSimpleForm implements SimpleForm {
	protected Encounter encounter;
	
	public AbstractSimpleForm() {
		this.encounter = new Encounter();
	}
	
	public AbstractSimpleForm(Patient patient) {
		this.encounter = new Encounter();
		this.encounter.setPatient(patient);
	}
	
	public AbstractSimpleForm(Encounter encounter) {
		this.encounter = encounter;
	}
	
	
	public Integer getId() {
		return this.encounter.getId();
	}
	
	public void setEncounter(Encounter encounter) {
	    this.encounter = encounter;
    }

	public Encounter getEncounter() {
	    return encounter;
    }
	
	public Person getProvider() {
		return encounter.getProvider();
	}
	
	public void setProvider(Person provider) {
		encounter.setProvider(provider);
	}
	
	public Patient getPatient() {
		return encounter.getPatient();
	}
	
	public void setPatient(Patient patient) {
		encounter.setPatient(patient);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setPerson(patient);
		}
	}
	
	public Date getEncounterDatetime() {
		return encounter.getEncounterDatetime();
	}

	public void setEncounterDatetime(Date date) {
		encounter.setEncounterDatetime(date);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setObsDatetime(date);
		}
	}
	
	public Location getLocation() {
		return encounter.getLocation();
	}
	
	public void setLocation(Location location) {
		encounter.setLocation(location);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setLocation(location);
		}
	}
	
}
