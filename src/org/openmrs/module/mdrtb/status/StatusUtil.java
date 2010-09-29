package org.openmrs.module.mdrtb.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
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
	
	public static List<Specimen> getSpecimensDuringProgram(PatientProgram program) {
		
		if (program == null || program.getDateEnrolled() == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(program.getDateEnrolled());
		
		// TODO: do we need to fix this to be only one month before enrollment?  how do we define enrollment?
		calendar.add(Calendar.MONTH, -2);
		
		return Context.getService(MdrtbService.class).getSpecimens(program.getPatient(), calendar.getTime(), program.getDateCompleted());
    }
	

	public static List<Regimen> getRegimensDuringProgram(PatientProgram program) {
		
		if (program == null || program.getDateEnrolled() == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(program.getDateEnrolled());
		
		// TODO: do we need to fix this to be only one month before enrollment?  how do we define enrollment?
		calendar.add(Calendar.MONTH, -2);
		
		return RegimenUtils.getRegimenHistory(program.getPatient()).getRegimensBetweenDates(calendar.getTime(), 
			(program.getDateCompleted() != null ? program.getDateCompleted() : new Date()));
	}
	
	// returns a list of concepts that represent a positive result for a smear or culture
	public static Set<Concept> getPositiveResultConcepts() {
		MdrtbService service = Context.getService(MdrtbService.class);
		
		// create a list of all concepts that represent positive results
		Set<Concept> positiveResults = new HashSet<Concept>();
		positiveResults.add(service.getConcept(MdrtbConcepts.STRONGLY_POSITIVE));
		positiveResults.add(service.getConcept(MdrtbConcepts.MODERATELY_POSITIVE));
		positiveResults.add(service.getConcept(MdrtbConcepts.WEAKLY_POSITIVE));
		positiveResults.add(service.getConcept(MdrtbConcepts.POSITIVE));
		positiveResults.add(service.getConcept(MdrtbConcepts.SCANTY));
		
		return positiveResults;
	}
	
	public static List<Concept> sortDrugs(List<Concept> drugs) {
		List<Concept> drugTypes = Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
		List<Concept> sortedDrugs = new LinkedList<Concept>();
		
		for (Concept drug : drugTypes) {
			if (drugs.contains(drug)) {
				sortedDrugs.add(drug);
			}
		}
		
		return sortedDrugs;
	}
}