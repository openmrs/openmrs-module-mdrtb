package org.openmrs.module.mdrtb.web.patientsummary;

import org.openmrs.Obs;


public class PatientSummaryTableElement {
	private Obs obs;

	public PatientSummaryTableElement() {
		// generic constructor
	}
	
	public PatientSummaryTableElement(Obs obs) {
		setObs(obs);
	}
	
	/*
	 * Getters and Setters
	 */
	
    public Obs getObs() {
    	return obs;
    }

	
    public void setObs(Obs obs) {
    	this.obs = obs;
    }
}
