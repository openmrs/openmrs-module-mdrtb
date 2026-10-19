package org.openmrs.module.mdrtb;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;

import java.util.List;
import java.util.Set;

public class PatientSummary {
	
	private Patient patient;
	
	private PersonName personName;
	
	private Set<PatientIdentifier> patientIdentifiers;
	
	private List<PatientProgramSummary> patientProgramSummaries;
	
	public PatientSummary(Patient patient, PersonName personName, Set<PatientIdentifier> patientIdentifiers,
	    List<PatientProgramSummary> patientProgramSummaries) {
		this.patient = patient;
		this.personName = personName;
		this.patientIdentifiers = patientIdentifiers;
		this.patientProgramSummaries = patientProgramSummaries;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public PersonName getPersonName() {
		return personName;
	}
	
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}
	
	public Set<PatientIdentifier> getPatientIdentifiers() {
		return patientIdentifiers;
	}
	
	public void setPatientIdentifiers(Set<PatientIdentifier> patientIdentifiers) {
		this.patientIdentifiers = patientIdentifiers;
	}
	
	public List<PatientProgramSummary> getPatientProgramSummaries() {
		return patientProgramSummaries;
	}
	
	public void setPatientProgramSummaries(List<PatientProgramSummary> patientProgramSummaries) {
		this.patientProgramSummaries = patientProgramSummaries;
	}
}
