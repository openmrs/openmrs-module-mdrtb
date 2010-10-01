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
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.specimen.Specimen;


public class StatusUtil {
	
	public static List<MdrtbPatientProgram> getMdrtbPrograms(Patient patient) {
		
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, Context.getService(MdrtbService.class).getMdrtbProgram(), null, null, null, null, false);
		
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		
		return mdrtbPrograms;
	}
	
	public static MdrtbPatientProgram getMostRecentMdrtbProgram(Patient patient) {
		List<MdrtbPatientProgram> programs = getMdrtbPrograms(patient);
		
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
	
	public static Date getTreatmentStartDateDuringProgram(PatientProgram program) {
		Date startDate = null;
		List<Regimen> regimens = getRegimensDuringProgram(program);
			
		// TODO: confirm that regimen history sorts regimens in order
		// return the start date of the first regimen 
		if(regimens != null && regimens.size() > 0) {
			startDate = regimens.get(0).getStartDate();
		}
		
		// if no regimens, this will return null for a treatment start date	
		return startDate;
	}
	
	public static Date getTreatmentEndDateDuringProgram(PatientProgram program) {
		Date endDate = null;
		List<Regimen> regimens = getRegimensDuringProgram(program);
			
		// TODO: confirm that regimen history sorts regimens in order
		// return the end date of the last regimen 
		if(regimens != null && regimens.size() > 0) {
			endDate = regimens.get(regimens.size()-1).getEndDate();
		}
		
		// if no regimens, this will return null for a treatment end date	
		return endDate;
		
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