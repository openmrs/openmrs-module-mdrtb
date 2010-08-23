package org.openmrs.module.mdrtb.web.controller.migration;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpecimenMigrationVoidEncountersController {
	
	@RequestMapping("/module/mdrtb/specimen/migrate/voidEncounters.form")
	public ModelAndView voidEncounters() {
	
		// this migration controller is meant to run AFTER the specimen migration controller, to void all the BAC and DST encounters
		// (all data in these encounters should have been been migrated to new specimen encounters by the specimen migration controller)
		// for some reason I can't get this encounter voiding to work unless it is in a separate controller
		
		// fetch the bac and dst encounter types
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
	
	
		// now void all unused encounters
		// loop thru all the bac and dst encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			if (encounter.getAllObs().size() == 0) {
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
		 
		 return new ModelAndView("/module/mdrtb/specimen/specimenMigration");
	}
	
}
