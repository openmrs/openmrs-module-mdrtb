package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Date;
import java.util.Map;


public class PatientSummaryTableRow {

	private Date date;
	
	private PatientSummaryTableBacElement smear;
	
	private PatientSummaryTableBacElement culture;
	
	private Map<String,PatientSummaryTableDSTElement> dsts;
	
	private Map<String,PatientSummaryTableRegimenElement> regimens;
	
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

	public void setRegimens(Map<String,PatientSummaryTableRegimenElement> regimens) {
	    this.regimens = regimens;
    }

	public Map<String,PatientSummaryTableRegimenElement> getRegimens() {
	    return regimens;
    }


	
	
	
}
