package org.openmrs.module.mdrtb.form;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;


public class SimpleFollowUpForm extends AbstractSimpleForm {

	public SimpleFollowUpForm() {
		super();
		String property = Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type");
		this.encounter.setEncounterType(Context.getEncounterService().getEncounterType(property));		
		
	}
	
	public SimpleFollowUpForm(Patient patient) {
		super(patient);
		String property = Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type");
		this.encounter.setEncounterType(Context.getEncounterService().getEncounterType(property));		
	}
	
	public SimpleFollowUpForm(Encounter encounter) {
		super(encounter);
	}
}
