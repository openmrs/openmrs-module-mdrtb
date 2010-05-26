package org.openmrs.module.mdrtb.web.patientsummary;


public class PatientSummaryTableColumn {

	private String name;
	
	private String code;

	public PatientSummaryTableColumn() {
		// generic constructor
	}
	
	public PatientSummaryTableColumn(String code) {
		this.code = code;
	}
	
    public String getName() {
    	return name;
    }

	
    public void setName(String name) {
    	this.name = name;
    }

	
    public String getCode() {
    	return code;
    }

	
    public void setCode(String code) {
    	this.code = code;
    }
	
	
}
