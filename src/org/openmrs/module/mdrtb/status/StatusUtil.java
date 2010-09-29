package org.openmrs.module.mdrtb.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.specimen.Specimen;


public class StatusUtil {
	
	public static List<PatientProgram> getMdrtbPrograms(Patient patient) {
		
		List<PatientProgram> mdrtbPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, Context.getService(MdrtbService.class).getMdrtbProgram(), null, null, null, null, false);
		
		 Collections.sort(mdrtbPrograms, new PatientProgramComparator());
		
		return mdrtbPrograms;
	}
	
	public static PatientProgram getMostRecentMdrtbProgram(Patient patient) {
		List<PatientProgram> programs = getMdrtbPrograms(patient);
		
		if (programs.size() > 0) {
			return programs.get(programs.size() - 1);
		} 
		else {
			return null;
		}
	}
	
	public static List<Specimen> getSpecimensForProgram(PatientProgram program) {
		
		if (program.getDateEnrolled() == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(program.getDateEnrolled());
		
		// TODO: do we need to fix this to be only one month before enrollment?  how do we define enrollment?
		calendar.add(Calendar.MONTH, -2);
		
		return Context.getService(MdrtbService.class).getSpecimens(program.getPatient(), calendar.getTime(), program.getDateCompleted());
    }
	
}