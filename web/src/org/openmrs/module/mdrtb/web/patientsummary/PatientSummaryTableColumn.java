package org.openmrs.module.mdrtb.web.patientsummary;


public class PatientSummaryTableColumn {

	private String title;
	
	private String code;

	public PatientSummaryTableColumn() {
		// generic constructor
	}
	
	public PatientSummaryTableColumn(String code) {
		this.code = code;
	}
	
	public PatientSummaryTableColumn(String code, String title) {
		this.code = code;
		this.title = title;
	}
	
    public String getTitle() {
    	return title;
    }

	
    public void setTitle(String title) {
    	this.title = title;
    }

	
    public String getCode() {
    	return code;
    }

	
    public void setCode(String code) {
    	this.code = code;
    }
	
	
}
