package org.openmrs.module.mdrtb.status;

import java.util.Collections;
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

	public static List<Regimen> getAntiretroviralRegimens(Patient patient) {
		
		if (patient == null) {
			return null;
		}
		
		return RegimenUtils.getAntiretroviralRegimenHistory(patient).getRegimenList();
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