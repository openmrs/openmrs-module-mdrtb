package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link Form89ResourceController}. Endpoint: GET/POST /rest/v1/mdrtb/form89
 */
public class Form89ResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnForm89ForGivenUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnForm89FormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoForm89s() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewForm89() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveForm89WithEncounterAndPatientProgram() {
		fail("Not yet implemented");
	}
}
