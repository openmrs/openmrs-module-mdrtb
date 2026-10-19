package org.openmrs.module.mdrtb.form;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.mdrtb.MdrtbConstants;

public class SimpleFollowUpForm extends AbstractSimpleForm {
	
	public SimpleFollowUpForm() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
	}
	
	public SimpleFollowUpForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
	}
	
	public SimpleFollowUpForm(Encounter encounter) {
		super(encounter);
	}
}
