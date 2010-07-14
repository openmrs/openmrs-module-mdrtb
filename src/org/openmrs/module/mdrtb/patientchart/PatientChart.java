package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;


public class PatientChart {

	private List<PatientChartMonth> months;
	
	
	public PatientChart() {
		months = new LinkedList<PatientChartMonth>();
	}
	

	public void setMonths(List<PatientChartMonth> months) {
	    this.months = months;
    }

	public List<PatientChartMonth> getMonths() {
	    return months;
    }
	
	
	
}
