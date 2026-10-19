package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link TB07uDataResourceController}. Endpoint: GET
 * /rest/v1/mdrtb/tb07ureport
 */
public class TB07uDataResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void search_shouldReturnTB07uReportDataForGivenYear() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB07uReportDataForGivenYearAndQuarter() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB07uReportDataForGivenYearAndMonth() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB07uReportDataFilteredByLocation() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultWhenYearParamIsMissing() {
		fail("Not yet implemented");
	}
}
