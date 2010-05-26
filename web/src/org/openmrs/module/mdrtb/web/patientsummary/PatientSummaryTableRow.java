package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Date;


public class PatientSummaryTableRow {

	Date date;
	
	PatientSummaryTableBacElement smear;
	
	PatientSummaryTableBacElement culture;
	
	PatientSummaryTableDSTElement ast;
	
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

	
    public PatientSummaryTableDSTElement getAst() {
    	return ast;
    }

	
    public void setAst(PatientSummaryTableDSTElement ast) {
    	this.ast = ast;
    }
	
	
	
}
