package org.openmrs.module.mdrtb.program;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;


public class MdrtbPatientProgram {

	org.openmrs.module.programlocation.PatientProgram program;
	
	public MdrtbPatientProgram(PatientProgram program) {
		this.program = (org.openmrs.module.programlocation.PatientProgram) program;
	}
	
	public org.openmrs.module.programlocation.PatientProgram getPatientProgram() {
		return program;
	}
	
	public Boolean getActive() {
		return program.getActive();
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
	
	public Date getDateCompleted() {
		return this.program.getDateCompleted();
	}
	
	public void setDateCompleted(Date dateCompleted) {
		program.setDateCompleted(dateCompleted);
	}
	
	public Location getLocation() {
		return this.program.getLocation();
	}
	
	public void setLocation(Location location) {
		program.setLocation(location);
	}
	
	public Concept getOutcome() {		
		Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
		PatientState state = program.getCurrentState(program.getProgram().getWorkflowByName(outcome.getName().getName()));
		
		if (state == null) {
			return null;
		}
		else {
			return state.getState().getConcept();
		}
	}
}
