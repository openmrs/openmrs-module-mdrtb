package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link TB08DataResourceController}. Endpoint: GET /rest/v1/mdrtb/tb08report
 */
public class TB08DataResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void search_shouldReturnTB08ReportDataForGivenYear() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB08ReportDataForGivenYearAndQuarter() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB08ReportDataForGivenYearAndMonth() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnTB08ReportDataFilteredByLocation() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultWhenYearParamIsMissing() {
		fail("Not yet implemented");
	}
}
