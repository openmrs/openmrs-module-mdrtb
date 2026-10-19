package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;

public class SimpleDrugResistanceDuringTreatmentForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -7985268200400994850L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	private Concept drugResistance;
	
	public SimpleDrugResistanceDuringTreatmentForm() {
	}
	
	public SimpleDrugResistanceDuringTreatmentForm(DrugResistanceDuringTreatmentForm drugResistanceForm) {
		setEncounter(drugResistanceForm.getEncounter());
		setUuid(getEncounter().getUuid());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(
		    drugResistanceForm.getPatientProgramId());
		setPatientProgramUuid(patientProgram.getUuid());
		setDrugResistance(drugResistanceForm.getDrugResistanceDuringTreatment());
	}
	
	/**
	 * Provide {@link SimpleDrugResistanceDuringTreatmentForm} representation of
	 * {@link DrugResistanceDuringTreatmentForm}
	 */
	public DrugResistanceDuringTreatmentForm toForm() {
		DrugResistanceDuringTreatmentForm drugResistanceForm = new DrugResistanceDuringTreatmentForm();
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		drugResistanceForm.setPatientProgramId(patientProgram.getPatientProgramId());
		drugResistanceForm.setEncounter(getEncounter());
		drugResistanceForm.setDrugResistanceDuringTreatment(getDrugResistance());
		return drugResistanceForm;
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
	
	public Concept getDrugResistance() {
		return drugResistance;
	}
	
	public void setDrugResistance(Concept drugResistance) {
		this.drugResistance = drugResistance;
	}
}
