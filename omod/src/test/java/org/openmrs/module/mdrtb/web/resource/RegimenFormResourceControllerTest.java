package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link RegimenFormResourceController}. Endpoint: GET/POST
 * /rest/v1/mdrtb/regimen
 */
public class RegimenFormResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnRegimenFormForGivenUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnRegimenFormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoRegimenForms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewRegimenForm() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveRegimenFormWithEncounterAndPatientProgram() {
		fail("Not yet implemented");
	}
}
