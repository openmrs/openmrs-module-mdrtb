package org.openmrs.module.mdrtb.web.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.fail;

/**
 * Integration tests for {@link AdverseEventsDataResourceController}. Endpoint: GET
 * /rest/v1/mdrtb/adverseeventsregister
 */
public class AdverseEventsDataResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	@Test
	public void search_shouldReturnAdverseEventsRegisterDataForGivenYear() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnAdverseEventsRegisterDataForGivenYearAndQuarter() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnAdverseEventsRegisterDataForGivenYearAndMonth() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnAdverseEventsRegisterDataFilteredByLocation() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void search_shouldReturnEmptyResultWhenYearParamIsMissing() {
		fail("Not yet implemented");
	}
}
