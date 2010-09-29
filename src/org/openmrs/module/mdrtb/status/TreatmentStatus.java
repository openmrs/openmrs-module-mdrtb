package org.openmrs.module.mdrtb.status;

import org.openmrs.PatientProgram;


public class TreatmentStatus extends Status {

	public TreatmentStatus(PatientProgram program) {
	    super(program);
    }

	public StatusItem getTreatmentStatus() {
		return getItem("treatmentStatus");
	}
	
	public StatusItem getCurrentRegimen() {
		return getItem("getCurrentRegimen");
	}
	
	public StatusItem getInitialRegimen() {
		return getItem("getInitialRegimen");
	}
}
