package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Date;
import java.util.Map;


public class PatientSummaryTableRow {

	Date date;
	
	PatientSummaryTableBacElement smear;
	
	PatientSummaryTableBacElement culture;
	
	Map<String,PatientSummaryTableDSTElement> dsts;
	
	public PatientSummaryTableRow() {
		// empty constructor
	}
	
	/*
	 * Getters and Setters
	 */
	
    public Date getDate() {
    	return date;
    }

	
    public void setDate(Date date) {
    	this.date = date;
    }

	
    public PatientSummaryTableBacElement getSmear() {
    	return smear;
    }

	
    public void setSmear(PatientSummaryTableBacElement smear) {
    	this.smear = smear;
    }

	
    public PatientSummaryTableBacElement getCulture() {
    	return culture;
    }

	
    public void setCulture(PatientSummaryTableBacElement culture) {
    	this.culture = culture;
    }

	
    public Map<String,PatientSummaryTableDSTElement> getDsts() {
    	return dsts;
    }

	
    public void setDsts(Map<String,PatientSummaryTableDSTElement> dsts) {
    	this.dsts = dsts;
    }


	
	
	
}
