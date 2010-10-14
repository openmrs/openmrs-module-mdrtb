package org.openmrs.module.mdrtb.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
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
		calendar.add(Calendar.DAY_OF_YEAR, -MdrtbConstants.NUMBER_OF_DAYS_PRIOR_TO_PROGRAM_ENROLLMENT_TO_INCLUDE_IN_PROGRAM_SUMMARY);
		
		return Context.getService(MdrtbService.class).getSpecimens(program.getPatient(), calendar.getTime(), program.getDateCompleted());
    }
	

	public static List<Regimen> getMdrtbRegimensDuringProgram(PatientProgram program) {
		
		if (program == null || program.getDateEnrolled() == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(program.getDateEnrolled());
		calendar.add(Calendar.DAY_OF_YEAR, -MdrtbConstants.NUMBER_OF_DAYS_PRIOR_TO_PROGRAM_ENROLLMENT_TO_INCLUDE_IN_PROGRAM_SUMMARY);
		
		return RegimenUtils.getMdrtbRegimenHistory(program.getPatient()).getRegimensBetweenDates(calendar.getTime(), 
			(program.getDateCompleted() != null ? program.getDateCompleted() : new Date()));
	}

	public static List<Regimen> getAntiretroviralRegimens(Patient patient) {
		
		if (patient == null) {
			return null;
		}
		
		return RegimenUtils.getAntiretroviralRegimenHistory(patient).getRegimenList();
	}
	
	public static List<Encounter> getMdrtbEncountersDuringProgram(PatientProgram program) {
	
		if (program == null || program.getDateEnrolled() == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(program.getDateEnrolled());
		calendar.add(Calendar.DAY_OF_YEAR, -MdrtbConstants.NUMBER_OF_DAYS_PRIOR_TO_PROGRAM_ENROLLMENT_TO_INCLUDE_IN_PROGRAM_SUMMARY);
		
		// only get MDR-TB specific encounters
		List<EncounterType> types = new LinkedList<EncounterType>();
		types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")));
    	types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")));
    	types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
    	
		
		return Context.getEncounterService().getEncounters(program.getPatient(), null, calendar.getTime(), program.getDateCompleted(), null, types, null, false);
	}
	
	public static Date getTreatmentStartDateDuringProgram(PatientProgram program) {
		Date startDate = null;
		List<Regimen> regimens = getMdrtbRegimensDuringProgram(program);
			
		// TODO: confirm that regimen history sorts regimens in order
		// return the start date of the first regimen 
		if(regimens != null && regimens.size() > 0) {
			startDate = regimens.get(0).getStartDate();
		
			// if the treatment start date is prior to the program start date, this is an invalid start date
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(program.getDateEnrolled());
			calendar.add(Calendar.DAY_OF_YEAR, -MdrtbConstants.NUMBER_OF_DAYS_PRIOR_TO_PROGRAM_ENROLLMENT_TO_INCLUDE_IN_PROGRAM_SUMMARY);
		
			if (startDate.before(calendar.getTime())) {
				startDate = null;
			}
		
		}
		
		// if no regimens, this will return null for a treatment start date	
		return startDate;
	}
	
	public static Date getTreatmentEndDateDuringProgram(PatientProgram program) {
		Date endDate = null;
		List<Regimen> regimens = getMdrtbRegimensDuringProgram(program);
			
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
	
	// given a list of concepts, sorts them in the same order as the list of MDR-TB drugs 
	// returns by getMdrtbDrugs(); all non-MDR-TB drug are ignored
	public static List<Concept> sortMdrtbDrugs(List<Concept> drugs) {
		return sortDrugs(drugs, Context.getService(MdrtbService.class).getMdrtbDrugs());
	}
	
	public static List<Concept> sortAntiretrovirals(List<Concept> drugs) {
		return sortDrugs(drugs, Context.getService(MdrtbService.class).getAntiretrovirals());
	}
	
	// give a list of drugs to sort and a drug list, sorts the first list so that the
	// drugs are in the same order as the second list; any drugs in the list to sort not
	// found in the drug list are discarded
	public static List<Concept> sortDrugs(List<Concept> drugsToSort, List<Concept> drugList) {
		List<Concept> sortedDrugs = new LinkedList<Concept>();
		
		for (Concept drug : drugList) {
			if (drugsToSort.contains(drug)) {
				sortedDrugs.add(drug);
			}
		}
		
		return sortedDrugs;
	}
}