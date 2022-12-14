/**
 * 
 */
package org.openmrs.module.mdrtb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.Country;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class MdrtbServiceLocationTest extends MdrtbBase {
	
	MdrtbService service;
	
	LocalDate startDate = new LocalDate(2022, 8, 1);
	
	LocalDate endDate = new LocalDate(2022, 8, 31);
	
	LocalDate now = new LocalDate();
	
	Country tajikistan = new Country("Tajikistan", 1);
	Country scotland = new Country("Scotland", 2);
	
	Region dushanbe = new Region("Dushanbe", 3);
	Region khatlon = new Region("Khatlon Region", 4);
	Region republic = new Region("Republican Subordination", 5);
	
	District fayzobad = new District("Fayzobod District", 6);
	District nurobad = new District("Nurobod District", 7);
	District ferdowsi = new District("Ferdowsi District", 13);
	District mansur = new District("Shah Mansur District", 14);
	
	Facility fayzobodLab = new Facility("Fayzobod Central Testing Lab", 10);
	Facility dushanbeGeneralHospital = new Facility("Dushanbe General Hospital", 201);

	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
	}
	
	/**
	 * Test whether the service has initiated
	 */
	@Test
	public void testMdrtbService() {
		assertNotNull(service);
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getCountries()}.
	 */
	@Test
	public final void testGetCountries() {
		List<Country> list = service.getCountries();
		assertTrue(list.contains(tajikistan));
		assertTrue(list.contains(scotland));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegion(java.lang.Integer)}.
	 */
	@Test
	public final void testGetRegion() {
		assertEquals("Region mismatch", dushanbe, service.getRegion(dushanbe.getId()));
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegions()}.
	 */
	@Test
	public final void testGetRegions() {
		List<Region> list = service.getRegions();
		assertTrue(list.contains(dushanbe));
		assertTrue(list.contains(republic));
		assertTrue(list.contains(khatlon));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistrict(java.lang.Integer)}.
	 */
	@Test
	public final void testGetDistrict() {
		assertEquals("District mismatch", fayzobad, service.getDistrict(6));
		assertEquals("District mismatch", fayzobad, service.getDistrict("Fayzobod District"));
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistricts()}.
	 */
	@Test
	public final void testGetDistricts() {
		List<District> list = service.getDistricts();
		assertTrue(list.contains(fayzobad));
		assertTrue(list.contains(nurobad));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getDistrictsByParent(int)}.
	 */
	@Test
	public final void testGetDistrictsByParent() {
		List<District> list = service.getDistrictsByParent(republic.getId());
		assertTrue(list.contains(fayzobad));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getFacility(java.lang.Integer)}.
	 */
	@Test
	public final void testGetFacility() {
		assertEquals("Facility mismatch", fayzobodLab, service.getFacility(10));
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getFacilities()}.
	 */
	@Test
	public final void testGetFacilities() {
		List<Facility> list = service.getFacilitiesByParent(fayzobad.getId());
		assertTrue(list.contains(fayzobodLab));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocation(java.lang.Integer, java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	public final void testGetLocation() {
		Location actual = service.getLocation(republic.getId(), fayzobad.getId(), fayzobodLab.getId());
		Location expected = Context.getLocationService().getLocation(200);
		assertEquals("Location mismatch", expected, actual);
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationListForDushanbe(java.lang.Integer, java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationListForDushanbe() {
		ArrayList<Location> list = service.getLocationListForDushanbe(dushanbe.getId(), null, dushanbeGeneralHospital.getId());
		Location expected = Context.getLocationService().getLocation(dushanbeGeneralHospital.getId());
		assertTrue(list.contains(expected));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromDistrict(org.openmrs.module.mdrtb.District)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationsFromDistrict() {
		service.getLocationsFromDistrict(fayzobad);
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromFacility(org.openmrs.module.mdrtb.Facility)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationsFromFacility() {
		List<Location> list = service.getLocationsFromFacility(dushanbeGeneralHospital);
		Location expected = Context.getLocationService().getLocation(dushanbeGeneralHospital.getId());
		assertTrue(list.contains(expected));
	}
	
	/**
	 * Test method for
	 * {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getLocationsFromRegion(org.openmrs.module.mdrtb.Region)}.
	 */
	@Test
	@Ignore
	public final void testGetLocationsFromRegion() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegDistricts()}.
	 */
	@Test
	@Ignore
	public final void testGetRegDistricts() {
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
	 * Test method for {@link org.openmrs.module.mdrtb.service.MdrtbServiceImpl#getRegFacilities()}.
	 */
	@Test
	@Ignore
	public final void testGetRegFacilities() {
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
}
