/**
 * 
 */
package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class HibernateMdrtbDAOTest extends MdrtbTestBase {
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
	}
	
	@Test
	public final void testGetLocationsWithAnyProgramEnrollments() {
		List<Location> list = dao.getLocationsWithAnyProgramEnrollments();
		assertTrue(list.contains(hogwarts));
		assertTrue(list.contains(diagonAlley));
	}
	
	@Test
	public final void testGetPatientIdentifierById() {
		Context.clearSession();
		PatientIdentifier identifier = dao.getPatientIdentifierById(1001);
		assertEquals(identifier.getPatient(), harry);
	}
	
	@Test
	public final void testGetEncountersByEncounterTypes() {
		List<String> encounterTypeNames = Arrays.asList(owlExam.getName(), transferIn.getName(), transferOut.getName());
		List<Encounter> list = dao.getEncountersByEncounterTypes(encounterTypeNames);
		assertTrue(list.size() > 1);
		Encounter encounter = Context.getEncounterService().getEncounter(100001);
		assertTrue(list.contains(encounter));
	}
	
	@Test
	public final void testGetEncountersByEncounterTypesAndDates() {
		List<String> encounterTypeNames = Arrays.asList(owlExam.getName(), transferIn.getName(), transferOut.getName());
		LocalDate startDate = new LocalDate(2022, 8, 1);
		LocalDate endDate = new LocalDate(2022, 8, 8);
		List<Encounter> list = dao.getEncountersByEncounterTypes(encounterTypeNames, startDate.toDate(), endDate.toDate());
		assertEquals(4, list.size());
	}
	
	@Test
	public final void testGetLocationsByHierarchyLevelRegion() {
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.REGION);
		assertEquals(3, list.size()); // Expect three Tajik regions
	}
	
	@Test
	public final void testGetLocationsByHierarchyLevelDistrict() {
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.DISTRICT);
		assertEquals(2, list.size()); // Expect two districts
	}
	
	@Test
	@Ignore
	public final void testGetLocationsByParent() {
	}
}
