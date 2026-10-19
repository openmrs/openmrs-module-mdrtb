package org.openmrs.module.mdrtb.form.custom;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;

/**
 * End of Treatment form 
 * Filled inside a patient's treatment flow once treatment is complete. It belongs to one patient program, linked through the PATIENT_PROGRAM_ID obs (the REST layer exposes it as patientProgramUuid). 
 * A TB03 form (DOTS program) or TB03u form (MDR-TB program) must already exist for that program.
 */
public class EndOfTreatmentForm extends AbstractSimpleForm {
	
	private static final String VOID_REASON = "voided by MDRTB module";
	
	public EndOfTreatmentForm() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_END_OF_TREATMENT);
	}
	
	public EndOfTreatmentForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_END_OF_TREATMENT);
	}
	
	public EndOfTreatmentForm(Encounter encounter) {
		super(encounter);
	}
	
	public Integer getPatientProgramId() {
		Concept concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs obs = MdrtbUtil.getObsFromEncounter(concept, encounter);
		return obs == null || obs.getValueNumeric() == null ? null : obs.getValueNumeric().intValue();
	}
	
	public void setPatientProgramId(Integer id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || id == null || obs.getValueNumeric().intValue() != id) {
			// void the existing obs if it exists, because openmrs doesn't void obs when saved via encounters
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			// now create the new Obs and add it to the encounter
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_PROGRAM_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(id.doubleValue());
				encounter.addObs(obs);
			}
		}
	}
	
	// TODO (EoT fields): add getter/setter pairs for the form fields here (see class comment).
	
	public String getLink() {
		return "/module/mdrtb/form/endOfTreatment.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
}
