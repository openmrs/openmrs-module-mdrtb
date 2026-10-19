/**
 * 
 */
package org.openmrs.module.mdrtb;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;

import static org.junit.Assert.*;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class MdrtbServiceLocationTest extends MdrtbTestBase {
	
	MdrtbService service;
	
	LocalDate startDate = new LocalDate(2022, 8, 1);
	
	LocalDate endDate = new LocalDate(2022, 8, 31);
	
	LocalDate now = new LocalDate();
	
	Region dushanbe;
	
	Region khatlon;
	
	Region republic;
	
	District fayzobad;
	
	District tojikobod;
	
	District ferdowsi;
	
	District mansur;
	
	Facility fayzobodLab;
	
	Facility dushanbeGeneralHospital;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
		
		dushanbe = new Region(new BaseLocation(Context.getLocationService().getLocation(50), LocationHierarchy.REGION));
		khatlon = new Region(new BaseLocation(Context.getLocationService().getLocation(52), LocationHierarchy.REGION));
		republic = new Region(new BaseLocation(Context.getLocationService().getLocation(51), LocationHierarchy.REGION));
		
		fayzobad = new District(new BaseLocation(Context.getLocationService().getLocation(75), LocationHierarchy.DISTRICT));
		tojikobod = new District(new BaseLocation(Context.getLocationService().getLocation(74), LocationHierarchy.DISTRICT));
		ferdowsi = new District(new BaseLocation(Context.getLocationService().getLocation(72), LocationHierarchy.DISTRICT));
		mansur = new District(new BaseLocation(Context.getLocationService().getLocation(70), LocationHierarchy.DISTRICT));
		
		fayzobodLab = new Facility(new BaseLocation(Context.getLocationService().getLocation(200),
		        LocationHierarchy.FACILITY));
		dushanbeGeneralHospital = new Facility(new BaseLocation(Context.getLocationService().getLocation(201),
		        LocationHierarchy.FACILITY));
	}
	
	@Test
	public void testMdrtbService() {
		assertNotNull(service);
	}
	
	@Test
	public final void testGetRegion() {
		assertEquals("Region mismatch", dushanbe, service.getRegion(dushanbe.getId()));
	}
	
	@Test
	public final void testGetRegions() {
		List<Region> list = service.getRegions();
		assertTrue(list.contains(dushanbe));
		assertTrue(list.contains(republic));
		assertTrue(list.contains(khatlon));
	}
	
	@Test
	public final void testGetDistrict() {
		assertEquals("District mismatch", fayzobad, service.getDistrict(75));
		assertEquals("District mismatch", fayzobad, service.getDistrict("Fayzobod"));
	}
	
	@Test
	public final void testGetDistricts() {
		List<District> list = service.getDistricts();
		assertTrue(list.contains(fayzobad));
		assertTrue(list.contains(tojikobod));
	}
	
	@Test
	public final void testGetDistrictsByParent() {
		List<District> list = service.getDistrictsByParent(republic.getId());
		assertTrue(list.contains(fayzobad));
	}
	
	@Test
	public final void testGetFacility() {
		Facility facility = service.getFacility(200);
		assertEquals("Facility mismatch", fayzobodLab, facility);
	}
	
	@Test
	public final void testGetFacilitiesByParent() {
		List<Facility> list = service.getFacilitiesByParent(fayzobad.getId());
		assertFalse(list.isEmpty());
	}
	
	@Test
	public final void testGetLocation() {
		Location actual = service.getLocation(republic.getId(), fayzobad.getId(), fayzobodLab.getId());
		Location expected = Context.getLocationService().getLocation(200);
		assertEquals("Location mismatch", expected, actual);
	}
	
	@Test
	@Ignore
	public final void testGetLocationListForDushanbe() {
		fail("Not yet implemented");
	}
	
	@Test
	public final void testGetLocationsFromDistrict() {
		List<Location> list = service.getLocationsFromDistrict(fayzobad);
		Location expected = Context.getLocationService().getLocation(fayzobodLab.getId());
		assertTrue(list.contains(expected));
	}
	
	@Test
	public final void testGetLocationsFromRegion() {
		List<Location> list = service.getLocationsFromRegion(republic);
		Location expected = Context.getLocationService().getLocation(fayzobodLab.getId());
		assertTrue("Expected location missing from region", list.contains(expected));
	}
	
	@Test
	public final void testGetRegDistricts() {
		Context.getAdministrationService().setGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS,
		    String.valueOf(fayzobad.getId()));
		List<District> list = service.getRegDistricts();
		assertTrue(list.contains(fayzobad));
	}
	
	@Test
	public final void testGetRegFacilities() {
		Context.getAdministrationService().setGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS,
		    String.valueOf(fayzobodLab.getId()));
		List<Facility> list = service.getRegFacilities();
		assertTrue(list.contains(fayzobodLab));
	}
}
