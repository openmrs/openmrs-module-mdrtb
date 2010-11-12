package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.reporting.data.Cohorts;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.util.StringUtils;

public class MdrtbCohortUtil {
    
	/**
	 * Utility method to return patients matching passed criteria
	 * @return Cohort
	 */
	public static Cohort getMdrPatients(String identifier, String name, String enrollment, Location location, List<ProgramWorkflowState> states) {
		
		Cohort cohort = Context.getPatientSetService().getAllPatients();
		
		MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
		
		Date now = new Date();
		Program mdrtbProgram = ms.getMdrtbProgram();
		
		if ("current".equals(enrollment)) {
			Cohort current = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, now, now);
			cohort = Cohort.intersect(cohort, current);
		}
		else {
			Cohort ever = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, null, null);
			if ("previous".equals(enrollment)) {
				Cohort current = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, now, now);
				Cohort previous = Cohort.subtract(ever, current);
				cohort = Cohort.intersect(cohort, previous);   			
			}
			else if ("never".equals(enrollment)) {
				cohort = Cohort.subtract(cohort, ever);
			}
			else {
				cohort = Cohort.intersect(cohort, ever);
			}	
		}
		
		if (StringUtils.hasText(name) || StringUtils.hasText(identifier)) {
			name = "".equals(name) ? null : name;
			identifier = "".equals(identifier) ? null : identifier;
			Cohort nameIdMatches = new Cohort(Context.getPatientService().getPatients(name, identifier, null, false));
			cohort = Cohort.intersect(cohort, nameIdMatches);
		}
		
		// If Location is specified, limit to patients at this Location
		if (location != null) {
			CohortDefinition lcd = Cohorts.getLocationFilter(location, now, now);
			Cohort locationCohort = Context.getService(CohortDefinitionService.class).evaluate(lcd, new EvaluationContext());
			cohort = Cohort.intersect(cohort, locationCohort);
		}
		
		if (states != null) {
			Cohort inStates = Context.getPatientSetService().getPatientsByProgramAndState(null, states, now, now);
			cohort = Cohort.intersect(cohort, inStates);
		}
		
		return cohort;
	}
}
