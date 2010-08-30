package org.openmrs.module.mdrtb.patient;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientProgram;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class MdrtbPatientValidator implements Validator {

	protected final Log log = LogFactory.getLog(getClass());
	
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return MdrtbPatientValidator.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
	    MdrtbPatientWrapper patient = (MdrtbPatientWrapper) target;
	    
	    // validate enrollment dates for all mdrtb programs
	    for(PatientProgram program : patient.getMdrtbPrograms()) {
	    	log.error("Testing program with date enrolledd " + program.getDateEnrolled());
	    	if(program.getDateEnrolled() == null || program.getDateEnrolled().toString().isEmpty()) {
	    		errors.reject("mdrtb.errors.noEnrollmentDate", "Please specify an enrollment date.");
	    	} else if(program.getDateEnrolled().after(new Date())) {
	    		errors.reject("mdrtb.errors.enrollmentDateInFuture", "The enrollment date must not be in the future.");
	    	}
	    }
    }
}
