package org.openmrs.module.mdrtb.status;

import org.openmrs.PatientProgram;


public class LabResultsStatus extends Status {

	public LabResultsStatus(PatientProgram program) {
	    super(program);
    }

	public StatusItem getCultureConversion() {
		return getItem("cultureConversion");
	}
	
	public StatusItem getDiagnosticSmear() {
		return getItem("diagnosticSmear");
	}
	
	public StatusItem getDiagnosticCulture() {
		return getItem("diagnosticCulture");
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
	
	public StatusItem getTbClassification() {
		return getItem("tbClassification");
	}

	public StatusItem getAnatomicalSite() {
		return getItem("anatomicalSite");
	}
}
