package org.openmrs.module.mdrtb.regimen;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.reporting.definition.DstResultCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.DstResultCohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;


public class RegimenTest extends BaseModuleContextSensitiveTest{

	/**
	 * @see BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}

	/**
	 * @see {@link DstResultCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return confirmed mdrtb patients for the period", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnConfirmedMdrtbPatientsForThePeriod() throws Exception {
		
		
		System.out.println("Total all years: ");
	}

	
}
