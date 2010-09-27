package org.openmrs.module.mdrtb.status;

import java.util.Date;


public interface MdrtbProgramStatusRenderer {

	public String renderMessagePatientNotEnrolledInProgram();
	
	public String renderMessageNoProgramEnrollmentDate();
	
	public String renderEnrollmentDateDisplayString(Date date);
	
}
