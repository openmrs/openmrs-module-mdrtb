package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.module.mdrtb.specimen.Specimen;


public class PatientChartRecord {
	
	private List<Specimen> specimens;
	
	public PatientChartRecord() {
		this.specimens = new LinkedList<Specimen>();
	}
	
	public PatientChartRecord(List<Specimen> specimens) {
		this.specimens = specimens;
	}

	public void setSpecimens(List<Specimen> specimens) {
	    this.specimens = specimens;
    }

	public List<Specimen> getSpecimens() {
	    return specimens;
    }
	
}
