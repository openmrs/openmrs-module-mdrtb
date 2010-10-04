package org.openmrs.module.mdrtb.program;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;


public class MdrtbPatientProgram {

	org.openmrs.module.programlocation.PatientProgram program;
	
	public MdrtbPatientProgram() {
		this.program = new org.openmrs.module.programlocation.PatientProgram();
		this.program.setProgram(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")));
	}
	
	public MdrtbPatientProgram(PatientProgram program) {
		this.program = (org.openmrs.module.programlocation.PatientProgram) program;
	}
	
	public org.openmrs.module.programlocation.PatientProgram getPatientProgram() {
		return program;
	}
	
	public Boolean getActive() {
		return this.program.getActive();
	}
	
	public Integer getId() {
		return this.program.getId();
	}
	
	public Patient getPatient() {
		return this.program.getPatient();
	}
	
	public void setPatient(Patient patient) {
		this.program.setPatient(patient);
	}
	
	public Date getDateEnrolled() {
		return this.program.getDateEnrolled();
	}
	
	public void setDateEnrolled(Date dateEnrolled) {
		this.program.setDateEnrolled(dateEnrolled);
		
		// also update any of the start date of the classification states (if they exist)
		PatientState previousDrugState = getPatientState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
		if (previousDrugState != null) {
			previousDrugState.setStartDate(dateEnrolled);
		}
		
		PatientState previousTreatmentState = getPatientState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX));
		if (previousTreatmentState != null) {
			previousTreatmentState.setStartDate(dateEnrolled);
		}
	}
	
	public Date getDateCompleted() {
		return this.program.getDateCompleted();
	}
	
	public void setDateCompleted(Date dateCompleted) {
		this.program.setDateCompleted(dateCompleted);
		
		// also update the start date of the outcome state 
		PatientState outcome = getPatientState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME));
		if (outcome != null) {
			outcome.setStartDate(dateCompleted);
		}
	}
	
	public Location getLocation() {
		return this.program.getLocation();
	}
	
	public void setLocation(Location location) {
		this.program.setLocation(location);
	}
	
	public ProgramWorkflowState getOutcome() {		
		Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
		return getWorkflowState(outcome);
	}
	
	public void setOutcome (ProgramWorkflowState programOutcome) {
		// if the state hasn't changed, we don't need to bother doing the update
		ProgramWorkflowState currentOutcome = getOutcome();
		if ( (currentOutcome == null && programOutcome == null) || (currentOutcome != null && currentOutcome.equals(programOutcome)) ){
			return;
		}

		// otherwise, do the update
		Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
		
		// void any existing states tied to the the outcome workflow
		voidStates(outcome);
		
		// now add the new state
		PatientState outcomeState = new PatientState();
		outcomeState.setState(programOutcome);
		// the outcome state start date is always the completed date of the program
		outcomeState.setStartDate(program.getDateCompleted()); 
		this.program.getStates().add(outcomeState);	
	}
	
	public ProgramWorkflowState getClassificationAccordingToPreviousDrugUse() {		
		Concept previousDrug = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
		return getWorkflowState(previousDrug);
	}
	
	public void setClassificationAccordingToPreviousDrugUse (ProgramWorkflowState classification) {
		// if the state hasn't changed, we don't need to bother doing the update
		ProgramWorkflowState currentClassification = getClassificationAccordingToPreviousDrugUse();
		if ( (currentClassification == null && classification == null) || (currentClassification != null && currentClassification.equals(classification)) ){
			return;
		}

		// otherwise, do the update
		Concept previousDrug = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
		
		// void any existing states tied to the the outcome workflow
		voidStates(previousDrug);
		
		// now add the new state
		PatientState previousDrugState = new PatientState();
		previousDrugState.setState(classification);
		// the start date for the state should be the program enrollment date
		previousDrugState.setStartDate(program.getDateEnrolled()); 
		this.program.getStates().add(previousDrugState);	
	}
	
	public ProgramWorkflowState getClassificationAccordingToPreviousTreatment() {		
		Concept previousTreatment = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX);
		return getWorkflowState(previousTreatment);
	}
	
	public void setClassificationAccordingToPreviousTreatment (ProgramWorkflowState classification) {
		// if the state hasn't changed, we don't need to bother doing the update
		ProgramWorkflowState currentClassification = getClassificationAccordingToPreviousTreatment();
		if ( (currentClassification == null && classification == null) || (currentClassification != null && currentClassification.equals(classification)) ){
			return;
		}

		// otherwise, do the update
		Concept previousTreatment = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX);
		
		// void any existing states tied to the the outcome workflow
		voidStates(previousTreatment);
		
		// now add the new state
		PatientState previousTreatmentState = new PatientState();
		previousTreatmentState.setState(classification);
		// the start date for the state should be the program enrollment date
		previousTreatmentState.setStartDate(program.getDateEnrolled()); 
		this.program.getStates().add(previousTreatmentState);	
	}
	
	/**
	 * Utility functions
	 */
	
	
	/**
	 * Gets the current state for a workflow 
	 * 
	 * Note that this method operates under the assumption that there is only one non-voided
	 * state per workflow at any one time.  For a generic workflow, this would not be a valid
	 * assumption, but for the workflows we are working with, this should be true.
	 */
	private PatientState getPatientState (Concept workflowConcept) {
		for (PatientState state : this.program.getStates()) {
			if (state.getState().getProgramWorkflow().getConcept().equals(workflowConcept) && !state.getVoided()) {
				return state;
			}
		}
		
		return null;
	}
	
	private ProgramWorkflowState getWorkflowState(Concept workflowConcept) {
		PatientState state = getPatientState(workflowConcept);
		
		if (state == null) {
			return null;
		} 
		else {
			return state.getState();
		}
	}
	
	
	/**
	 * Voids all states related to a specific workflow
	 */
	private void voidStates (Concept workflowConcept) {
		for (PatientState state : this.program.getStates()) {
			if (state.getState().getProgramWorkflow().getConcept().equals(workflowConcept)) {
				state.setVoided(true);
				state.setVoidReason("voided by mdr-tb module");
			}
		}
	}
}
