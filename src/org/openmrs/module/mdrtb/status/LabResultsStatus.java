package org.openmrs.module.mdrtb.status;

import org.openmrs.Patient;


public class LabResultsStatus extends Status {

	public LabResultsStatus(Patient patient) {
	    super(patient);
    }

	public StatusItem getControlSmear() {
		return getItem("controlSmear");
	}
	
	public StatusItem getDiagnosticCulture() {
		return getItem("diagnosisCulture");
	}
	
	public StatusItem getMostRecentSmear() {
		return getItem("mostRecentSmear");
	}
	
	public StatusItem getMostRecentCulture() {
		return getItem("mostRecentCulture");
	}
	
	public StatusItem getDrugResistanceProfile() {
		return getItem("drugResistanceProfile");
	}
	
	public StatusItem getPendingLabResults() {
		return getItem("pendingLabResults");
	}
}
