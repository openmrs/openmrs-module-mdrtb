package org.openmrs.module.mdrtb.status;

import org.openmrs.PatientProgram;


public class MdrtbProgramStatus extends Status {
	
	public MdrtbProgramStatus(PatientProgram program) {
	    super(program);
    }

	public StatusItem getEnrollmentDate() {
		return getItem("enrollmentDate");
	}
	
	public void setEnrollmentDate(StatusItem enrollmentDate) {		
		new MdrtbProgramStatusEditor().updateEnrollmentDate(this.program.getPatient(), enrollmentDate);
	}
	
}
