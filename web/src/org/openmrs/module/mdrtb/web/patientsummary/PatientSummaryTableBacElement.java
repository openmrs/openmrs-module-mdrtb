package org.openmrs.module.mdrtb.web.patientsummary;

import org.openmrs.Obs;


public class PatientSummaryTableBacElement extends PatientSummaryTableElement{

	private Obs obs;

	public PatientSummaryTableBacElement() {
		// generic constructor
	}
	
	public PatientSummaryTableBacElement(Obs obs) {
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
