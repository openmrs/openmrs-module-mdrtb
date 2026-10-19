package org.openmrs.module.mdrtb.web.controller.command;

import org.openmrs.Patient;
import org.openmrs.PersonAddress;

/**
 * Command object for adding/editing a patient
 */
public class PatientCommand {
	
	private Patient patient;
	
	private PersonAddress address;
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public PersonAddress getAddress() {
		return address;
	}
	
	public void setAddress(PersonAddress address) {
		this.address = address;
	}
}
