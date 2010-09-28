package org.openmrs.module.mdrtb.status;

import org.openmrs.Patient;


public class TreatmentStatus extends Status {

	public TreatmentStatus(Patient patient) {
	    super(patient);
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
