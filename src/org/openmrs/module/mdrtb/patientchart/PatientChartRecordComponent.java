package org.openmrs.module.mdrtb.patientchart;

import java.util.List;

import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.specimen.Specimen;


public class PatientChartRecordComponent {

	private Specimen specimen;
	
	private List<Regimen> regimens;
	
	public PatientChartRecordComponent() {}
	
	public PatientChartRecordComponent(Specimen specimen) {
		this.specimen = specimen;
	}
	
	public PatientChartRecordComponent(Specimen specimen, List<Regimen> regimens) {
		this.specimen = specimen;
		this.regimens = regimens;
	}

	public void setSpecimen(Specimen specimen) {
	    this.specimen = specimen;
    }

	public Specimen getSpecimen() {
	    return specimen;
    }

	public void setRegimens(List<Regimen> regimen) {
	    this.regimens = regimen;
    }

	public List<Regimen> getRegimens() {
	    return regimens;
    }
	
	
	
}
