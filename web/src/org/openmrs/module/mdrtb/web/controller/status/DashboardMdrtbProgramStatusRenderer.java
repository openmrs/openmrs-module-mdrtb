package org.openmrs.module.mdrtb.web.controller.status;

import java.util.Date;

import org.openmrs.module.mdrtb.status.MdrtbProgramStatusRenderer;


public class DashboardMdrtbProgramStatusRenderer implements MdrtbProgramStatusRenderer {

	// TODO: localize all this
	
    public String renderEnrollmentDateDisplayString(Date date) {
    	return date.toString();
    }

    public String renderMessageNoProgramEnrollmentDate() {
	    return "No MDR-TB program enrollment date specified";
    }

    public String renderMessagePatientNotEnrolledInProgram() {
        return "Patient not enrolled in program";
    }

	
}
