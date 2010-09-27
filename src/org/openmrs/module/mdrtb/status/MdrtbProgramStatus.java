package org.openmrs.module.mdrtb.status;

import org.openmrs.Patient;


public class MdrtbProgramStatus extends Status {
	
	public MdrtbProgramStatus(Patient patient) {
	    super(patient);
    }

	public StatusItem getEnrollmentDate() {
		return getItem("enrollmentDate");
	}
	
	public void setEnrollmentDate(StatusItem enrollmentDate) {		
		new MdrtbProgramStatusEditor().updateEnrollmentDate(this.patient, enrollmentDate);
	}
	
}
