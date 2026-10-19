package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link TB03uFormResourceController}. Endpoint: GET/POST
 * /rest/v1/mdrtb/tb03u
 */
public class TB03uFormResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnTB03uFormForGivenEncounterUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB03uFormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoTB03uForms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnOnlyTB03uEncounterTypes() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewTB03uForm() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldAssignProviderFromCurrentUserWhenNoProviderSupplied() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldResolvePatientProgramIdFromUuid() {
		fail("Not yet implemented");
	}
}
