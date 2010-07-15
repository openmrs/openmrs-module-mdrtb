package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;


public class PatientChartRecord {
	
	private List<MdrtbSpecimen> specimens;
	
	public PatientChartRecord() {
		this.specimens = new LinkedList<MdrtbSpecimen>();
	}
	
	public PatientChartRecord(List<MdrtbSpecimen> specimens) {
		this.specimens = specimens;
	}

	public void setSpecimens(List<MdrtbSpecimen> specimens) {
	    this.specimens = specimens;
    }

	public List<MdrtbSpecimen> getSpecimens() {
	    return specimens;
    }
	
}
