package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Date;


public class PatientSummaryTableDateElement extends PatientSummaryTableElement{
	
	private Date date;

	public PatientSummaryTableDateElement() {
		// generic constructor
	}
	
	public PatientSummaryTableDateElement(Date date) {
		this.date = date;
	}
	
	/*
	 * Getters and Setters
	 */
	
	public void setDate(Date date) {
	    this.date = date;
    }

	public Date getDate() {
	    return date;
    }
	
	

}
