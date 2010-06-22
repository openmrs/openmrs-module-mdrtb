package org.openmrs.module.mdrtb.web.patientsummary;

import org.openmrs.Obs;


// TODO: Get rid of this, change MdrtbDst to implement PatientSummaryTableElement

public class PatientSummaryTableDSTElement extends PatientSummaryTableElement{

	private Obs obs;

	public PatientSummaryTableDSTElement() {
		// generic constructor
	}
	
	public PatientSummaryTableDSTElement(Obs obs) {
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
