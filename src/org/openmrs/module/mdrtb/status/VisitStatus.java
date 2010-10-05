package org.openmrs.module.mdrtb.status;

import org.openmrs.PatientProgram;


public class VisitStatus extends Status {

	public VisitStatus(PatientProgram program) {
	    super(program);
    }

	public StatusItem getIntakeVisits() {
		return getItem("intakeVisits");
	}
	
	public StatusItem getFollowUpVisits() {
		return getItem("followUpVisits");
	}
	
	public StatusItem getScheduledFollowUpVisits() {
		return getItem("scheduledFollowUpVisits");
	}
	
	public StatusItem getSpecimenCollectionVisits() {
		return getItem("specimenCollectionVisits");
	}
}
