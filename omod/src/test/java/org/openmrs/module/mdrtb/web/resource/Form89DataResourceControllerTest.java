package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link Form89DataResourceController}. Endpoint: GET
 * /rest/v1/mdrtb/form89report
 */
public class Form89DataResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void search_shouldReturnForm89ReportDataForGivenYear() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnForm89ReportDataForGivenYearAndQuarter() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnForm89ReportDataForGivenYearAndMonth() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnForm89ReportDataFilteredByLocation() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultWhenYearParamIsMissing() {
		fail("Not yet implemented");
	}
}
