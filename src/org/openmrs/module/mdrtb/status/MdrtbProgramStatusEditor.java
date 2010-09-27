package org.openmrs.module.mdrtb.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;



public class MdrtbProgramStatusEditor {

	public void updateEnrollmentDate(Patient patient, StatusItem enrollmentDate) {
		List<PatientProgram> mdrtbPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, Context.getService(MdrtbService.class).getMdrtbProgram(), null, null, null, null, false);
		Collections.sort(mdrtbPrograms, new PatientProgramComparator());

		if (mdrtbPrograms == null || mdrtbPrograms.isEmpty()) {
			// TODO: throw an exception?
			return;
		}
		
		PatientProgram program = mdrtbPrograms.get(mdrtbPrograms.size() - 1);
		
		if (enrollmentDate.getValue() instanceof Date) {
			program.setDateEnrolled((Date) enrollmentDate.getValue());
		}
		else {
			//bind dates
			SimpleDateFormat dateFormat = Context.getDateFormat();
	    	dateFormat.setLenient(false);
	    	try {
	            program.setDateEnrolled(dateFormat.parse((String) enrollmentDate.getValue()));
            }
            catch (ParseException e) {
	           
            }
		}
		
		Context.getProgramWorkflowService().savePatientProgram(program);
	}

	
	
	/**
     * Utility classes
     */
    
    private class PatientProgramComparator implements Comparator<PatientProgram> {

    	public int compare(PatientProgram program1, PatientProgram program2) {
    		if(program1 == null || program1.getDateEnrolled() == null) {
    			return 1;
    		} else if (program2 == null || program2.getDateEnrolled() == null) {
    			return -1;
    		} else {
    			return program1.getDateEnrolled().compareTo(program2.getDateEnrolled());
    		}
    	}
    }
}
