package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;

/**
 * REST representation of {@link EndOfTreatmentForm}. The link to the patient's treatment flow is carried as patientProgramUuid (stored on the encounter as the PATIENT_PROGRAM_ID obs).
 */
public class SimpleEndOfTreatmentForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 4319608452931487115L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	// TODO (EoT fields): add form fields here.
	
	public SimpleEndOfTreatmentForm() {
	}
	
	public SimpleEndOfTreatmentForm(EndOfTreatmentForm endOfTreatmentForm) {
		setEncounter(endOfTreatmentForm.getEncounter());
		setUuid(getEncounter().getUuid());
		Integer patientProgramId = endOfTreatmentForm.getPatientProgramId();
		if (patientProgramId != null) {
			PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			if (patientProgram != null) {
				setPatientProgramUuid(patientProgram.getUuid());
			}
		}
		// TODO (EoT fields): copy each form field from endOfTreatmentForm.
	}
	
	/**
	 * Provide {@link EndOfTreatmentForm} representation of {@link SimpleEndOfTreatmentForm}
	 */
	public EndOfTreatmentForm toForm() {
		EndOfTreatmentForm endOfTreatmentForm = new EndOfTreatmentForm();
		endOfTreatmentForm.setEncounter(getEncounter());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		endOfTreatmentForm.setPatientProgramId(patientProgram.getPatientProgramId());
		// TODO (EoT fields): copy each form field back to endOfTreatmentForm.
		return endOfTreatmentForm;
	}
	
	@Override
	public Integer getId() {
		return encounter.getEncounterId();
	}
	
	@Override
	public void setId(Integer id) {
		// Nope! Not gonna happen
	}
	
	public Encounter getEncounter() {
		return encounter;
	}
	
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	
	public String getPatientProgramUuid() {
		return patientProgramUuid;
	}
	
	public void setPatientProgramUuid(String patientProgramUuid) {
		this.patientProgramUuid = patientProgramUuid;
	}
}
