package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;


public class PatientChartMonth {
	
	private List<MdrtbSpecimen> specimens;
	
	public PatientChartMonth() {
		specimens = new LinkedList<MdrtbSpecimen>();
	}

	public void setSpecimens(List<MdrtbSpecimen> specimens) {
	    this.specimens = specimens;
    }

	public List<MdrtbSpecimen> getSpecimens() {
	    return specimens;
    }
	
}
