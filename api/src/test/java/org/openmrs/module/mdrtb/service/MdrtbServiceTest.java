/**
 * 
 */
package org.openmrs.module.mdrtb.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ProgramWorkflowDAO;
import org.openmrs.api.db.hibernate.HibernateConceptDAO;
import org.openmrs.api.db.hibernate.HibernateEncounterDAO;
import org.openmrs.api.db.hibernate.HibernateObsDAO;
import org.openmrs.api.db.hibernate.HibernateOrderDAO;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO;

/**
 * @author owais.hussain@esquaredsystems.com
 *
 */
public class MdrtbServiceTest extends MdrtbBase {

	@InjectMocks
	MdrtbServiceImpl service = new MdrtbServiceImpl();

	@Mock
	HibernateMdrtbDAO dao;

	@Mock
	HibernateConceptDAO conceptDao;

	@Mock
	HibernateOrderDAO orderDao;

	@Mock
	HibernateEncounterDAO encounterDao;

	@Mock
	HibernateObsDAO obsDao;
	
	@Mock
	ProgramWorkflowDAO programWorkflowDao;

	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * Test whether the service has initiated
	 */
	@Test
	public void setupMdrtbService() {
		assertNotNull(service);
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#findMatchingConcept(java.lang.String)}.
	 */
	@Test
	public final void testFindMatchingConceptWithExactString() {
		when(conceptDao.getConceptsByMapping(anyString(), anyString(), anyBoolean())).thenReturn(Arrays.asList(mdrStartDateConcept));
		Concept concept = service.findMatchingConcept("DATE OF MDR TREATMENT START");
		assertNotNull(concept);
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAllMdrtbPatientProgramsInDateRange(java.util.Date, java.util.Date)}.
	 */
	@Test
	public final void testGetAllMdrtbPatientProgramsInDateRange() {
		Date now = new Date();
		List<PatientProgram> patientPrograms = Arrays.asList(harryMdrProgram, harryDotsProgram);
		when(programWorkflowDao.getPatientPrograms(harry, mdrtbProgram, now, now, now, now, false)).thenReturn(patientPrograms);
		List<MdrtbPatientProgram> list = service.getAllMdrtbPatientProgramsEnrolledInDateRange(now, now);
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientPrograms(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbPatientPrograms() {
		Date now = new Date();
		List<PatientProgram> patientPrograms = Arrays.asList(harryMdrProgram);
		when(programWorkflowDao.getPatientPrograms(harry, mdrtbProgram, now, now, now, now, false)).thenReturn(patientPrograms);
		List<MdrtbPatientProgram> list = service.getMdrtbPatientPrograms(harry);
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMostRecentMdrtbPatientProgram(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetMostRecentMdrtbPatientProgram() {
		MdrtbPatientProgram program = service.getMostRecentMdrtbPatientProgram(harry);
		assertTrue(program.getId() == harry.getId());
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientProgramsInDateRange(org.openmrs.Patient, java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbPatientProgramsInDateRange() {
		LocalDate start = new LocalDate(2022, 8, 1);
		LocalDate end = new LocalDate(2022, 8, 31);
		List<MdrtbPatientProgram> list = service.getMdrtbPatientProgramsInDateRange(harry, start.toDate(), end.toDate());
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientProgramOnDate(org.openmrs.Patient, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbPatientProgramOnDate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbPatientProgram(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbPatientProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createSpecimen(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testCreateSpecimen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimen(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimenInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimen(org.openmrs.Encounter)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimenEncounter() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimens(org.openmrs.Patient)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimensPatient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimens(org.openmrs.Patient, java.util.Date, java.util.Date)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimensPatientDateDate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimens(org.openmrs.Patient, java.util.Date, java.util.Date, org.openmrs.Location)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimensPatientDateDateLocation() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveSpecimen(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testSaveSpecimen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#deleteSpecimen(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testDeleteSpecimen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#deleteTest(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testDeleteTest() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createSmear(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testCreateSmearSpecimen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createSmear(org.openmrs.module.mdrtb.specimen.Specimen, org.openmrs.module.mdrtb.specimen.Smear)}.
	 */
	@Test
	@Ignore
	public final void testCreateSmearSpecimenSmear() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSmear(org.openmrs.Obs)}.
	 */
	@Test
	@Ignore
	public final void testGetSmearObs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSmear(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetSmearInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveSmear(org.openmrs.module.mdrtb.specimen.Smear)}.
	 */
	@Test
	@Ignore
	public final void testSaveSmear() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createCulture(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testCreateCultureSpecimen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createCulture(org.openmrs.module.mdrtb.specimen.Specimen, org.openmrs.module.mdrtb.specimen.Culture)}.
	 */
	@Test
	@Ignore
	public final void testCreateCultureSpecimenCulture() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCulture(org.openmrs.Obs)}.
	 */
	@Test
	@Ignore
	public final void testGetCultureObs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCulture(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetCultureInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveCulture(org.openmrs.module.mdrtb.specimen.Culture)}.
	 */
	@Test
	@Ignore
	public final void testSaveCulture() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createDst(org.openmrs.module.mdrtb.specimen.Specimen)}.
	 */
	@Test
	@Ignore
	public final void testCreateDstSpecimen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#createDst(org.openmrs.module.mdrtb.specimen.Specimen, org.openmrs.module.mdrtb.specimen.Dst)}.
	 */
	@Test
	@Ignore
	public final void testCreateDstSpecimenDst() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDst(org.openmrs.Obs)}.
	 */
	@Test
	@Ignore
	public final void testGetDstObs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDst(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetDstInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveDst(org.openmrs.module.mdrtb.specimen.Dst)}.
	 */
	@Test
	@Ignore
	public final void testSaveDst() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#deleteDstResult(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testDeleteDstResult() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#saveScannedLabReport(org.openmrs.module.mdrtb.specimen.ScannedLabReport)}.
	 */
	@Test
	@Ignore
	public final void testSaveScannedLabReport() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#deleteScannedLabReport(java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testDeleteScannedLabReport() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#processDeath(org.openmrs.Patient, java.util.Date, org.openmrs.Concept)}.
	 */
	@Test
	@Ignore
	public final void testProcessDeath() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbProgram()}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbProgram() {
		fail("Not yet implemented"); // TODO
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
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleSmearResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleSmearResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleSmearMethods()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleSmearMethods() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleCultureResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleCultureResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleCultureMethods()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleCultureMethods() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleDstMethods()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleDstMethods() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleDstResults()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleDstResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleOrganismTypes()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleOrganismTypes() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleSpecimenTypes()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleSpecimenTypes() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleSpecimenAppearances()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleSpecimenAppearances() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleAnatomicalSites()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleAnatomicalSites() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDrugsInSet(java.lang.String[])}.
	 */
	@Test
	@Ignore
	public final void testGetDrugsInSetStringArray() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDrugsInSet(org.openmrs.Concept)}.
	 */
	@Test
	@Ignore
	public final void testGetDrugsInSetConcept() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getMdrtbDrugs()}.
	 */
	@Test
	@Ignore
	public final void testGetMdrtbDrugs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getAntiretrovirals()}.
	 */
	@Test
	@Ignore
	public final void testGetAntiretrovirals() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleMdrtbProgramOutcomes()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleMdrtbProgramOutcomes() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleClassificationsAccordingToPreviousDrugUse()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleClassificationsAccordingToPreviousDrugUse() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleClassificationsAccordingToPreviousTreatment()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleClassificationsAccordingToPreviousTreatment() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getColorForConcept(org.openmrs.Concept)}.
	 */
	@Test
	@Ignore
	public final void testGetColorForConcept() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#resetColorMapCache()}.
	 */
	@Test
	@Ignore
	public final void testResetColorMapCache() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getSpecimens(org.openmrs.Patient, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetSpecimensPatientInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getTbProgram()}.
	 */
	@Test
	@Ignore
	public final void testGetTbProgram() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleTbProgramOutcomes()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleTbProgramOutcomes() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getPossibleClassificationsAccordingToPatientGroups()}.
	 */
	@Test
	@Ignore
	public final void testGetPossibleClassificationsAccordingToPatientGroups() {
		fail("Not yet implemented"); // TODO
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
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromOblastName(org.openmrs.module.mdrtb.Oblast)}.
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
		fail("Not yet implemented"); // TODO
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
