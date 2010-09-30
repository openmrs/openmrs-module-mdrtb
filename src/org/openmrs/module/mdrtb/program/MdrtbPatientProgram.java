package org.openmrs.module.mdrtb.program;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;


public class MdrtbPatientProgram {

	org.openmrs.module.programlocation.PatientProgram program;
	
	public MdrtbPatientProgram(PatientProgram program) {
		this.program = (org.openmrs.module.programlocation.PatientProgram) program;
	}
	
	public org.openmrs.module.programlocation.PatientProgram getPatientProgram() {
		return program;
	}
	
	public Integer getId() {
		return program.getId();
	}
	
	public Patient getPatient() {
		return program.getPatient();
	}
	
	public Date getDateEnrolled() {
		return this.program.getDateEnrolled();
	}
	
	public void setDateEnrolled(Date dateEnrolled) {
		program.setDateEnrolled(dateEnrolled);
	}
	
	public Location getLocation() {
		return this.program.getLocation();
	}
	
	public void setLocation(Location location) {
		program.setLocation(location);
	}
}
