package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.LinkedList;
import java.util.List;


public class PatientSummaryTable {

	private List<PatientSummaryTableRow> patientSummaryTableRows = new LinkedList<PatientSummaryTableRow>();
	
	private List<PatientSummaryTableColumn> patientSummaryTableColumns = new LinkedList<PatientSummaryTableColumn>();
	
	public PatientSummaryTable() {
		// empty constructor
	}
	
	
	/*
	 * Getters and Setters
	 */
	
    public List<PatientSummaryTableRow> getPatientSummaryTableRows() {
    	return patientSummaryTableRows;
    }

	
    public void setPatientSummaryTableRows(List<PatientSummaryTableRow> patientSummaryTableRows) {
    	this.patientSummaryTableRows = patientSummaryTableRows;
    }

	public void setPatientSummaryTableColumns(List<PatientSummaryTableColumn> patientSummaryTableColumns) {
	    this.patientSummaryTableColumns = patientSummaryTableColumns;
    }

	public List<PatientSummaryTableColumn> getPatientSummaryTableColumns() {
	    return patientSummaryTableColumns;
    }
}
