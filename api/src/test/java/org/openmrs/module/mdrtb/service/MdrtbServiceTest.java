/**
 * 
 */
package org.openmrs.module.mdrtb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.Country;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;

/**
 * @author owais.hussain@esquaredsystems.com
 *
 */
public class MdrtbServiceTest extends MdrtbBase {

	MdrtbService service;
	
	LocalDate startDate = new LocalDate(2022, 8, 1);
	LocalDate endDate = new LocalDate(2022, 8, 31);
	LocalDate now = new LocalDate();

	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
	}
	
	/**
	 * Test whether the service has initiated
	 */
	@Test
	public void testMdrtbService() {
		assertNotNull(service);
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getConcept(java.lang.String)}.
	 */
	@Test
	public final void testFindMatchingConceptWithExactString() {
		Concept concept = service.getConcept("DATE OF MDR TREATMENT START");
		assertNotNull(concept);
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllMdrtbPatientProgramsInDateRange(java.util.Date, java.util.Date)}.
	 */
	@Test
	public final void testGetAllMdrtbPatientProgramsInDateRange() {
		List<MdrtbPatientProgram> list = service.getAllMdrtbPatientProgramsEnrolledInDateRange(startDate.toDate(), endDate.toDate());
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientPrograms(org.openmrs.Patient)}.
	 */
	@Test
	public final void testGetMdrtbPatientPrograms() {
		List<MdrtbPatientProgram> list = service.getMdrtbPatientPrograms(harry);
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMostRecentMdrtbPatientProgram(org.openmrs.Patient)}.
	 */
	@Test
	public final void testGetMostRecentMdrtbPatientProgram() {
		MdrtbPatientProgram program = service.getMostRecentMdrtbPatientProgram(harry);
		assertTrue(program.getId() == new MdrtbPatientProgram(harryMdrProgram).getId());
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientProgramsInDateRange(org.openmrs.Patient, java.util.Date, java.util.Date)}.
	 */
	@Test
	public final void testGetMdrtbPatientProgramsInDateRange() {
		List<MdrtbPatientProgram> list = service.getMdrtbPatientProgramsInDateRange(harry, startDate.toDate(), endDate.toDate());
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientProgramOnDate(org.openmrs.Patient, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbPatientProgramOnDate() {
		MdrtbPatientProgram program = service.getMdrtbPatientProgramOnDate(harry, startDate.toDate());
		assertTrue(program.equals(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientProgram(java.lang.Integer)}.
	 */
	@Test
	public final void testGetMdrtbPatientProgram() {
		MdrtbPatientProgram program = service.getMdrtbPatientProgram(10001);
		assertTrue(program.equals(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createSpecimen(org.openmrs.Patient)}.
	 */
	@Test
	public final void testCreateSpecimen() {
		Specimen specimen = service.createSpecimen(harry);
		assertNotNull(specimen);
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimens(org.openmrs.Patient, java.util.Date, java.util.Date, org.openmrs.Location)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimensPatientDateDateLocation() {
		List<Specimen> list = service.getSpecimens(harry, startDate.toDate(), endDate.toDate(), hogwarts);
		assertTrue(list.contains(new SpecimenImpl(harrySpecimenEncounter)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveSpecimen(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	public final void testSaveSpecimen() {
		now = new LocalDate();
		Encounter encounter = new Encounter();
		encounter.setPatient(hermione);
		encounter.setEncounterDatetime(now.toDate());
		encounter.setEncounterType(specimen);
		Specimen specimenEncounter = new SpecimenImpl(encounter);
		service.saveSpecimen(specimenEncounter);
		List<Encounter> list = Context.getEncounterService().getEncountersByPatient(hermione);
		boolean exists = false;
		for (Encounter e : list) {
			if (e.getEncounterType().equals(specimen) && e.getEncounterDatetime().equals(now.toDate())) {
				exists = true;
			}
		}
		assertTrue(exists);
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#deleteTest(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void shouldNOTDeleteTest() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#processDeath(org.openmrs.Patient, java.util.Date, org.openmrs.Concept)}.
	 */
	@Test
	@Ignore
	public final void testProcessDeath() {
		Concept causeOfDeath = Context.getConceptService().getConcept(27);
		service.processDeath(harry, now.toDate(), causeOfDeath);
		MdrtbPatientProgram mdrtbPatientProgram = new MdrtbPatientProgram(Context.getProgramWorkflowService().getPatientProgram(harryMdrProgram.getId()));

		// Test if patient is labeled as dead
		Concept died = Context.getConceptService().getConcept(71);
		assertEquals(died, mdrtbPatientProgram.getOutcome().getConcept());		
		
		// Test if the MdrTB program is deactivated
		assertFalse(mdrtbPatientProgram.getActive());
		
		// TODO: Test if hospitalization status has changed
		// Concept hospitalizationWorkflow = Context.getConceptService().getConcept(MdrtbConcepts.HOSPITALIZATION_WORKFLOW[0]);
		// ProgramWorkflowState hospitalizationState = mdrtbPatientProgram.getCurrentHospitalizationState();
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getProviders()}.
	 */
	@Test
	@Ignore
	public final void testGetProviders() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getColorForConcept(org.openmrs.Concept)}.
	 */
	@Test
	public final void testGetColorForConcept() {
		Concept contaminated = Context.getConceptService().getConcept(47);
		String color = service.getColorForConcept(contaminated);
		assertEquals(color, "lightgrey");
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllRayonsTJK()}.
	 */
	@Test
	@Ignore
	public final void testGetAllRayonsTJK() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllMdrtbPatientProgramsEnrolledInDateRange(java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetAllMdrtbPatientProgramsEnrolledInDateRange() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveXpert(org.openmrs.module.mdrtb.specimen.custom.Xpert)}.
	 */
	@Test
	@Ignore
	public final void testSaveXpert() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createXpert(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testCreateXpert() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getXpert(org.openmrs.Obs)}.
	 */
	@Test
	@Ignore
	public final void testGetXpertObs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getXpert(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetXpertInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveHAIN(org.openmrs.module.mdrtb.specimen.custom.HAIN)}.
	 */
	@Test
	@Ignore
	public final void testSaveHAIN() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createHAIN(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testCreateHAIN() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getHAIN(org.openmrs.Obs)}.
	 */
	@Test
	@Ignore
	public final void testGetHAINObs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getHAIN(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetHAINInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getHAIN2(org.openmrs.Obs)}.
	 */
	@Test
	@Ignore
	public final void testGetHAIN2Obs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createHAIN2(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testCreateHAIN2() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveHAIN2(org.openmrs.module.mdrtb.specimen.custom.HAIN2)}.
	 */
	@Test
	@Ignore
	public final void testSaveHAIN2() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getHAIN2(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetHAIN2Integer() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleMtbResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleMtbResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleRifResistanceResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleRifResistanceResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleInhResistanceResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleInhResistanceResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleFqResistanceResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleFqResistanceResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleInjResistanceResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleInjResistanceResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleXpertMtbBurdens()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleXpertMtbBurdens() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getOblasts()}.
	 */
	@Test
	@Ignore
	public final void testGetOblasts() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getOblast(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetOblast() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromOblastName(org.openmrs.module.mdrtb.Region)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationsFromOblastName() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getFacilities()}.
	 */
	@Test
	@Ignore
	public final void testGetFacilities() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegFacilities()}.
	 */
	@Test
	@Ignore
	public final void testGetRegFacilities() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocation(java.lang.Integer, java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetLocation() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getFacilities(int)}.
	 */
	@Test
	@Ignore
	public final void testGetFacilitiesInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegFacilities(int)}.
	 */
	@Test
	@Ignore
	public final void testGetRegFacilitiesInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getFacility(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetFacility() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromFacilityName(org.openmrs.module.mdrtb.Facility)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationsFromFacilityName() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistricts(int)}.
	 */
	@Test
	@Ignore
	public final void testGetDistrictsInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegDistricts(int)}.
	 */
	@Test
	@Ignore
	public final void testGetRegDistrictsInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistrict(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetDistrictInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistrict(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetDistrictString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistricts()}.
	 */
	@Test
	@Ignore
	public final void testGetDistricts() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegDistricts()}.
	 */
	@Test
	@Ignore
	public final void testGetRegDistricts() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromDistrictName(org.openmrs.module.mdrtb.District)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationsFromDistrictName() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getEnrollmentLocations()}.
	 */
	@Test
	@Ignore
	public final void testGetEnrollmentLocations() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPatientProgramIdentifier(org.openmrs.module.mdrtb.program.MdrtbPatientProgram)}.
	 */
	@Test
	@Ignore
	public final void testGetPatientProgramIdentifier() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getGenPatientProgramIdentifier(org.openmrs.PatientProgram)}.
	 */
	@Test
	@Ignore
	public final void testGetGenPatientProgramIdentifier() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllTbPatientProgramsEnrolledInDateRange(java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetAllTbPatientProgramsEnrolledInDateRange() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#addIdentifierToProgram(java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testAddIdentifierToProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleIPTreatmentSites()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleIPTreatmentSites() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleCPTreatmentSites()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleCPTreatmentSites() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleRegimens()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleRegimens() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleHIVStatuses()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleHIVStatuses() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleResistanceTypes()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleResistanceTypes() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleConceptAnswers(java.lang.String[])}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleConceptAnswers() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#countPDFRows()}.
	 */
	@Test
	@Ignore
	public final void testCountPDFRows() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#countPDFColumns()}.
	 */
	@Test
	@Ignore
	public final void testCountPDFColumns() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#PDFRows(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testPDFRows() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#PDFColumns()}.
	 */
	@Test
	@Ignore
	public final void testPDFColumns() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#unlockReport(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testUnlockReport() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#doPDF(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testDoPDF() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#readReportStatus(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testReadReportStatus() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#readTableData(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testReadTableData() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getEncountersByEncounterTypes(java.util.List)}.
	 */
	@Test
	@Ignore
	public final void testGetEncountersByEncounterTypesListOfString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getEncountersByEncounterTypes(java.util.List, java.util.Date, java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetEncountersByEncounterTypesListOfStringDateDateDate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSmearForms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetSmearForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCultureForms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetCultureForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getXpertForms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetXpertForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getHAINForms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetHAINForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getHAIN2Forms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetHAIN2Forms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDstForms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetDstForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDrdtForms(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetDrdtForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getEncountersWithNoProgramId(org.openmrs.EncounterType, org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetEncountersWithNoProgramId() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#addProgramIdToEncounter(java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testAddProgramIdToEncounter() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03FormsFilled(org.openmrs.Location, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03FormsFilledLocationStringIntegerStringString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03FormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03FormsFilledArrayListOfLocationIntegerStringString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03uFormsFilled(org.openmrs.Location, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03uFormsFilledLocationStringIntegerStringString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03uFormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03uFormsFilledArrayListOfLocationIntegerStringString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getForm89FormsFilled(org.openmrs.Location, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetForm89FormsFilledLocationStringIntegerStringString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getForm89FormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetForm89FormsFilledArrayListOfLocationIntegerStringString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getForm89FormsFilledForPatientProgram(org.openmrs.Patient, org.openmrs.Location, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetForm89FormsFilledForPatientProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTransferOutFormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTransferOutFormsFilled() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTransferInFormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTransferInFormsFilled() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTransferOutFormsFilledForPatient(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetTransferOutFormsFilledForPatient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTransferInFormsFilledForPatient(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetTransferInFormsFilledForPatient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleDOTSClassificationsAccordingToPreviousDrugUse()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleDOTSClassificationsAccordingToPreviousDrugUse() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getClosestTB03Form(org.openmrs.Location, java.util.Date, org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetClosestTB03Form() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCultureLocations()}.
	 */
	@Test
	@Ignore
	public final void testGetCultureLocations() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationList(java.lang.Integer, java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationList() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPatientIdentifierById(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetPatientIdentifierById() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03uFormsFilledWithTxStartDateDuring(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03uFormsFilledWithTxStartDateDuring() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCountries()}.
	 */
	@Test
	@Ignore
	public final void testGetCountries() {
		List<Country> list = service.getCountries();
		Country england = new Country("England", 1);
		assertTrue(list.contains(england));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getOblasts(int)}.
	 */
	@Test
	@Ignore
	public final void testGetOblastsInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03FormsForProgram(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03FormsForProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getForm89FormsForProgram(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetForm89FormsForProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#evict(java.lang.Object)}.
	 */
	@Test
	@Ignore
	public final void testEvict() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03uFormForProgram(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03uFormForProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegimenFormsForProgram(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetRegimenFormsForProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegimenFormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetRegimenFormsFilled() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllPatientsWithRegimenForms()}.
	 */
	@Test
	@Ignore
	public final void testGetAllPatientsWithRegimenForms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPreviousRegimenFormForPatient(org.openmrs.Patient, java.util.ArrayList, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetPreviousRegimenFormForPatient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCurrentRegimenFormForPatient(org.openmrs.Patient, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetCurrentRegimenFormForPatient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAEFormsFilled(java.util.ArrayList, java.lang.Integer, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testGetAEFormsFilled() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAEFormsForProgram(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetAEFormsForProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationListForDushanbe(java.lang.Integer, java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationListForDushanbe() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllTbPatientProgramsEnrolledInDateRangeAndLocations(java.util.Date, java.util.Date, java.util.ArrayList)}.
	 */
	@Test
	@Ignore
	public final void testGetAllTbPatientProgramsEnrolledInDateRangeAndLocations() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(java.util.Date, java.util.Date, java.util.ArrayList)}.
	 */
	@Test
	@Ignore
	public final void testGetAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTB03uFormsForProgram(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetTB03uFormsForProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTbEncounters(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetTbEncounters() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllTbPatientPrograms()}.
	 */
	@Test
	@Ignore
	public final void testGetAllTbPatientPrograms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllTbPatientProgramsInDateRange(java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetAllTbPatientProgramsInDateRange() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTbPatientPrograms(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetTbPatientPrograms() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMostRecentTbPatientProgram(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetMostRecentTbPatientProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTbPatientProgramsInDateRange(org.openmrs.Patient, java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetTbPatientProgramsInDateRange() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTbPatientProgramOnDate(org.openmrs.Patient, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetTbPatientProgramOnDate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTbPatientProgram(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetTbPatientProgram() {
		fail("Not yet implemented"); // TODO
	}

}
