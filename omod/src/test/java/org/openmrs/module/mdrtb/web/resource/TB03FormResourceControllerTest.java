package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link TB03FormResourceController}. Endpoint: GET/POST /rest/v1/mdrtb/tb03
 */
public class TB03FormResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnTB03FormForGivenEncounterUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB03FormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoTB03Forms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnOnlyTB03EncounterTypes() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewTB03Form() {
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
