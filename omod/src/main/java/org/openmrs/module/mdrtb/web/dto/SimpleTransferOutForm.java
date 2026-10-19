package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.TransferOutForm;

public class SimpleTransferOutForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -7985268200400994850L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	public SimpleTransferOutForm() {
	}
	
	public SimpleTransferOutForm(TransferOutForm transferOutForm) {
		setEncounter(transferOutForm.getEncounter());
		setUuid(getEncounter().getUuid());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(
		    transferOutForm.getPatientProgramId());
		setPatientProgramUuid(patientProgram.getUuid());
	}
	
	/**
	 * Provide {@link SimpleTransferOutForm} representation of
	 * {@link DrugResistanceDuringTreatmentForm}
	 */
	public TransferOutForm toForm() {
		TransferOutForm transferOutForm = new TransferOutForm();
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		transferOutForm.setPatientProgramId(patientProgram.getPatientProgramId());
		transferOutForm.setEncounter(getEncounter());
		return transferOutForm;
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
