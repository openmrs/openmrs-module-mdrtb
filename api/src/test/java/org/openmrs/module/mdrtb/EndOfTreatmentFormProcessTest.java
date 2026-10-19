package org.openmrs.module.mdrtb;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class EndOfTreatmentFormProcessTest extends MdrtbTestBase {
	
	@Ignore
	@Test
	public void process_shouldFailWhenEncounterHasNoLocation() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldFailWhenPatientProgramIsNotAttached() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldFailWhenPatientProgramDoesNotExist() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldFailWhenPatientProgramBelongsToAnotherPatient() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldFailForDotsProgramWithoutTB03() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldFailForMdrtbProgramWithoutTB03u() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldSaveForDotsProgramWithTB03() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldSaveForMdrtbProgramWithTB03u() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void process_shouldNotChangePatientProgramOrPatient() {
		// Until the outcome rule is agreed, saving an End of Treatment form must not close the
		// program, change its workflow states or mark the patient as dead.
		fail("Not yet implemented");
	}
}
