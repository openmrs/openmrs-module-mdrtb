package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link ReportDataResourceController}. Endpoint: GET
 * /rest/v1/mdrtb/reportdata
 */
public class ReportDataResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void search_shouldReturnReportDataForGivenYear() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnReportDataForGivenYearAndQuarter() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnReportDataForGivenYearAndMonth() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnReportDataFilteredByLocation() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultWhenYearParamIsMissing() {
		fail("Not yet implemented");
	}
}
