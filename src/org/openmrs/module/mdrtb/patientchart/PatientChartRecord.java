package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;


public class PatientChartRecord {
	
	private List<PatientChartRecordComponent> components;
	
	public PatientChartRecord() {
		this.components = new LinkedList<PatientChartRecordComponent>();
	}
	
	public PatientChartRecord(List<PatientChartRecordComponent> specimens) {
		this.components = specimens;
	}

	public void setComponents(List<PatientChartRecordComponent> specimens) {
	    this.components = specimens;
    }

	public List<PatientChartRecordComponent> getComponents() {
	    return components;
    }
	
}
