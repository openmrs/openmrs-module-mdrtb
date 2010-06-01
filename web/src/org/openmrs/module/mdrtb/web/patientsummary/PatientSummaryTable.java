package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.LinkedList;
import java.util.List;


public class PatientSummaryTable {

	private List<PatientSummaryTableRecord> patientSummaryTableRecords = new LinkedList<PatientSummaryTableRecord>();
	
	private List<PatientSummaryTableField> patientSummaryTableFields = new LinkedList<PatientSummaryTableField>();
	
	public PatientSummaryTable() {
		// empty constructor
	}
	
	
	/*
	 * Getters and Setters
	 */
	
    public List<PatientSummaryTableRecord> getPatientSummaryTableRecords() {
    	return patientSummaryTableRecords;
    }

	
    public void setPatientSummaryTableRows(List<PatientSummaryTableRecord> patientSummaryTableRecords) {
    	this.patientSummaryTableRecords = patientSummaryTableRecords;
    }

	public void setPatientSummaryTableFields(List<PatientSummaryTableField> patientSummaryTableFields) {
	    this.patientSummaryTableFields = patientSummaryTableFields;
    }

	public List<PatientSummaryTableField> getPatientSummaryTableFields() {
	    return patientSummaryTableFields;
    }
}
