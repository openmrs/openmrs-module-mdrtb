/**
 * 
 */
package org.openmrs.module.mdrtb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.openmrs.module.mdrtb.BaseLocation;
import org.openmrs.module.mdrtb.Country;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Region;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class HibernateMdrtbDAOTest extends MdrtbBase {
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getLocationsWithAnyProgramEnrollments()}.
	 */
	@Test
	public final void testGetLocationsWithAnyProgramEnrollments() {
		List<Location> list = dao.getLocationsWithAnyProgramEnrollments();
		assertTrue(list.contains(hogwarts));
		assertTrue(list.contains(diagonAlley));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getAllRayonsTJK()}.
	 */
	@Test
	public final void testGetAllRayonsTJK() {
		List<String> list = dao.getAllRayonsTJK();
		assertTrue(list.contains("Fayzobod District")); // Expect a district name
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getPatientIdentifierById(java.lang.Integer)}.
	 */
	@Test
	public final void testGetPatientIdentifierById() {
		Context.clearSession();
		PatientIdentifier identifier = dao.getPatientIdentifierById(1001);
		assertEquals(identifier.getPatient(), harry);
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#doPDF(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testDoPDF() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#countPDFRows()}.
	 */
	@Test
	@Ignore
	public final void testCountPDFRows() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#countPDFColumns()}.
	 */
	@Test
	@Ignore
	public final void testCountPDFColumns() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getPDFColumns()}.
	 */
	@Test
	@Ignore
	public final void testPDFColumns() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getPDFData(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testPDFRows() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#readTableData(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testReadTableData() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#unlockReport(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testUnlockReport() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#readReportStatus(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testReadReportStatus() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getEncountersByEncounterTypes(java.util.List)}.
	 */
	@Test
	public final void testGetEncountersByEncounterTypes() {
		List<String> encounterTypeNames = Arrays.asList(owlExam.getName(), transferIn.getName(), transferOut.getName());
		List<Encounter> list = dao.getEncountersByEncounterTypes(encounterTypeNames);
		assertTrue(list.size() > 1);
		Encounter encounter = Context.getEncounterService().getEncounter(100001);
		assertTrue(list.contains(encounter));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO#getEncountersByEncounterTypes(java.util.List, java.util.Date, java.util.Date, java.util.Date)}.
	 */
	@Test
	public final void testGetEncountersByEncounterTypesAndDates() {
		List<String> encounterTypeNames = Arrays.asList(owlExam.getName(), transferIn.getName(), transferOut.getName());
		LocalDate startDate = new LocalDate(2022, 8, 1);
		LocalDate endDate = new LocalDate(2022, 8, 8);
		List<Encounter> list = dao.getEncountersByEncounterTypes(encounterTypeNames, startDate.toDate(), endDate.toDate(),
		    null);
		assertEquals(4, list.size());
	}
	
	@Test
	public final void testGetLocationParent() {
		BaseLocation child = new BaseLocation(3, "Sughd Region", null);
		BaseLocation parent = dao.getAddressHierarchyLocationParent(child);
		assertEquals("Tajikistan", parent.getName());
	}
	
	@Test
	public final void testGetLocationsByHierarchyLevelCountry() {
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Country.HIERARCHY_LEVEL);
		assertEquals(2, list.size()); // Expect two countries
	}

	@Test
	public final void testGetLocationsByHierarchyLevelRegion() {
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Region.HIERARCHY_LEVEL);
		assertEquals(3, list.size()); // Expect three Tajik regions
	}

	@Test
	public final void testGetLocationsByHierarchyLevelDistrict() {
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(District.HIERARCHY_LEVEL);
		assertEquals(2, list.size()); // Expect two districts
	}

	@Test
	@Ignore
	public final void testGetLocationsByParent() {
	}
}
