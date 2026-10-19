package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link AdverseEventsFormResourceController}. Endpoint: GET/POST
 * /rest/v1/mdrtb/adverseevents
 */
public class AdverseEventsFormResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnAdverseEventsFormForGivenUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnAdverseEventsFormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoAdverseEventsForms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewAdverseEventsForm() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveFormWithEncounterAndPatientProgram() {
		fail("Not yet implemented");
	}
}
