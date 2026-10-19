package org.openmrs.module.mdrtb.web.resource;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EndOfTreatmentFormResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnEndOfTreatmentFormForGivenEncounterUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void getByUuid_shouldReturnPatientProgramUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEndOfTreatmentFormsForGivenPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForPatientWithNoEndOfTreatmentForms() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultForUnknownPatientUuid() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnOnlyEndOfTreatmentEncounterTypes() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveEndOfTreatmentFormWhenTB03ExistsForDotsProgram() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldSaveEndOfTreatmentFormWhenTB03uExistsForMdrtbProgram() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldFailWhenDotsProgramHasNoTB03() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void create_shouldFailWhenMdrtbProgramHasNoTB03u() {
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
	
	@Ignore
	@Test
	public void delete_shouldVoidEndOfTreatmentEncounter() {
		fail("Not yet implemented");
	}
}
