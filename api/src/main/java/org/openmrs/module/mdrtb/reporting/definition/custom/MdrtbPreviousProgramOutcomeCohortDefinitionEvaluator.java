package org.openmrs.module.mdrtb.reporting.definition.custom;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbProgramClosedAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports = { MdrtbPreviousProgramOutcomeCohortDefinition.class }, order = 20)
public class MdrtbPreviousProgramOutcomeCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Default Constructor
	 */
	public MdrtbPreviousProgramOutcomeCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		MdrtbPreviousProgramOutcomeCohortDefinition cd = (MdrtbPreviousProgramOutcomeCohortDefinition) cohortDefinition;
		
		Cohort baseCohort = MdrtbUtil.getMdrPatients(null, null, null, null, null);
		
		Cohort resultCohort = new Cohort();
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(1900, 0, 1, 0, 0, 1);
		
		GregorianCalendar gc2 = new GregorianCalendar();
		gc2.setTime(cd.getFromDate());
		gc.add(Calendar.DATE, -1);
		
		// get all the program during the specified period
		Map<Integer, List<MdrtbPatientProgram>> mdrtbPatientProgramsMap = ReportUtil.getMdrtbPatientProgramsInDateRangeMap(
		    gc.getTime(), gc2.getTime());
		
		Collection<CohortMembership> memberships = baseCohort.getMemberships();
		for (CohortMembership membership : memberships) {
			int id = membership.getCohortMemberId();
			// first we need to find out what program(s) the patient was on during a given
			// time period
			List<MdrtbPatientProgram> programs = mdrtbPatientProgramsMap.get(id);
			
			// only continue if the patient was in a program during this time period
			if (programs != null && programs.size() != 0) {
				
				// by convention, operate on the most recent program during the time period
				// (there really should only ever be one program in a single period)
				MdrtbPatientProgram program = programs.get(programs.size() - 1);
				
				try {
					if (program.getOutcome() != null
					        && (program.getOutcome().getConcept() == Context.getService(MdrtbService.class).getConcept(
					            cd.getOutcome()))) {
						
						resultCohort.addMember(id);
					}
					
				}
				catch (Exception e) {
					log.debug(e.getMessage());
				}
				
			}
		}
		return new EvaluatedCohort(resultCohort, cohortDefinition, context);
	}
}
