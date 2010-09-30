package org.openmrs.module.mdrtb.status;

import org.openmrs.PatientProgram;


public class TreatmentStatus extends Status {

	public TreatmentStatus(PatientProgram program) {
	    super(program);
    }

	public StatusItem getTreatmentState() {
		return getItem("treatmentState");
	}
	
	public StatusItem getRegimens() {
		return getItem("regimens");
	}
	
}
