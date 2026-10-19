package org.openmrs.module.mdrtb.web.resource.lab;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Integration tests for {@link LabTestOrderResourceController}. Endpoint: GET/POST
 * /rest/v1/commonlab/labtestorder
 */
public class LabTestOrderResourceControllerTest extends LabResourceTestBase {
	
	private static class TestableController extends LabTestOrderResourceController {
		
		public PageableResult searchForTest(org.openmrs.module.webservices.rest.web.RequestContext context)
		        throws ResponseException {
			return doSearch(context);
		}
	}
	
	private TestableController controller;
	
	@Before
	public void setUpController() {
		controller = new TestableController();
	}
	
	// ── GET by UUID ────────────────────────────────────────────────────────────
	
	@Test
	public void getByUuid_shouldReturnHarryGxpLabTestOrder() {
		LabTest result = controller.getByUniqueId(HARRY_GXP_UUID);
		
		assertNotNull(result);
		assertEquals("GXP-IRS12345", result.getLabReferenceNumber());
		assertEquals(geneXpert, result.getLabTestType());
	}
	
	@Test
	public void getByUuid_shouldReturnHarryCxrLabTestOrder() {
		LabTest result = controller.getByUniqueId(HARRY_CXR_UUID);
		
		assertNotNull(result);
		assertEquals("CXR-NRL9876543100-52", result.getLabReferenceNumber());
		assertEquals(chestXRay, result.getLabTestType());
	}
	
	@Test
	public void getByUuid_shouldPopulateSamplesOnReturnedOrder() {
		LabTest result = controller.getByUniqueId(HARRY_GXP_UUID);
		
		assertNotNull(result);
		assertNotNull(result.getLabTestSamples());
		assertEquals(1, result.getLabTestSamples().size());
	}
	
	@Test
	public void getByUuid_shouldPopulateAttributesOnReturnedOrder() {
		LabTest result = controller.getByUniqueId(HARRY_GXP_UUID);
		
		assertNotNull(result);
		assertNotNull(result.getAttributes());
		// Harry GXP has 4 attributes in data (IDs 1, 2, 3, 8)
		assertEquals(4, result.getAttributes().size());
	}
	
	@Test
	public void getByUuid_shouldReturnNullForUnknownUuid() {
		LabTest result = controller.getByUniqueId("00000000-0000-0000-0000-000000000000");
		
		assertEquals(null, result);
	}
	
	// ── GET search by patient ──────────────────────────────────────────────────
	
	@Test
	public void search_shouldReturnLabTestsForHarry() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("patient", HARRY_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTest> page = ((NeedsPaging<LabTest>) result).getPageOfResults();
		// Harry has 2 lab tests: GXP and CXR
		assertEquals(2, page.size());
		assertThat(page, hasItem(hasProperty("labReferenceNumber", is("GXP-IRS12345"))));
		assertThat(page, hasItem(hasProperty("labReferenceNumber", is("CXR-NRL9876543100-52"))));
	}
	
	@Test
	public void search_shouldReturnLabTestsForHermione() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("patient", HERMIONE_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTest> page = ((NeedsPaging<LabTest>) result).getPageOfResults();
		// Hermione has 1 lab test: GXP
		assertEquals(1, page.size());
		assertThat(page, hasItem(hasProperty("labReferenceNumber", is("GXP-IRS987654"))));
	}
	
	// ── POST (save / update) ───────────────────────────────────────────────────
	
	@Test
	public void save_shouldUpdateReferenceNumberOnExistingLabTest() {
		LabTest existing = labTestService.getLabTestByUuid(HARRY_GXP_UUID);
		existing.setLabReferenceNumber("UPDATED-GXP-REF");
		existing.setLabTestSamples(new HashSet<LabTestSample>(labTestService.getLabTestSamples(existing, false)));
		existing.setAttributes(new HashSet<LabTestAttribute>(labTestService.getLabTestAttributes(existing.getTestOrderId())));
		
		LabTest saved = controller.save(existing);
		
		assertNotNull(saved);
		assertEquals("UPDATED-GXP-REF", saved.getLabReferenceNumber());
	}
	
	@Test
	public void save_shouldCreateNewLabTestForNewOrder() {
		// Create order for new lab test via the Order service
		org.openmrs.Order order = Context.getOrderService().getOrder(200); // Harry CXR order
		LabTest newLabTest = new LabTest(order);
		newLabTest.setLabTestType(chestXRay);
		newLabTest.setLabReferenceNumber("NEW-TEST-REF");
		newLabTest.setLabTestSamples(new HashSet<LabTestSample>());
		newLabTest.setAttributes(new HashSet<LabTestAttribute>());
		
		// Existing CXR lab test will be linked (order 200 already has a LabTest)
		LabTest saved = controller.save(newLabTest);
		
		assertNotNull(saved);
		assertNotNull(saved.getTestOrderId());
	}
}
