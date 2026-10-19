package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link TransferOutFormResourceController}. Endpoint: GET/POST
 * /rest/v1/mdrtb/transferout
 */
public class TransferOutFormResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnTransferOutFormForGivenEncounterUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTransferOutFormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoTransferOutForms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveNewTransferOutForm() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveTransferOutFormWithEncounterAndPatientProgram() {
		fail("Not yet implemented");
	}
}
