package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link DrugResistanceDuringTreatmentResourceController}. Endpoint: GET/POST
 * /rest/v1/mdrtb/drugresistance
 */
public class DrugResistanceDuringTreatmentResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnDrugResistanceFormForGivenUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnDrugResistanceFormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoDrugResistanceForms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewDrugResistanceDuringTreatmentForm() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveFormWithEncounterAndPatientProgram() {
		fail("Not yet implemented");
	}
}
