package org.openmrs.module.mdrtb.web.resource.lab;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTestGroup;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Integration tests for {@link LabTestTypeResourceController}. Endpoint: GET/POST
 * /rest/v1/commonlab/labtesttype
 */
public class LabTestTypeResourceControllerTest extends LabResourceTestBase {
	
	// Subclass to expose the protected doGetAll method
	private static class TestableController extends LabTestTypeResourceController {
		
		public PageableResult getAllForTest(RequestContext context) throws ResponseException {
			return doGetAll(context);
		}
	}
	
	private TestableController controller;
	
	@Before
	public void setUpController() {
		controller = new TestableController();
	}
	
	// ── GET by UUID ────────────────────────────────────────────────────────────
	
	@Test
	public void getByUuid_shouldReturnGeneXpertTypeForGivenUuid() {
		LabTestType result = controller.getByUniqueId(GENEXPERT_TYPE_UUID);
		
		assertNotNull(result);
		assertEquals("GeneXpert Test", result.getName());
		assertEquals("GXP", result.getShortName());
		assertEquals(LabTestGroup.BACTERIOLOGY, result.getTestGroup());
	}
	
	@Test
	public void getByUuid_shouldReturnChestXRayTypeForGivenUuid() {
		LabTestType result = controller.getByUniqueId(CHEST_XRAY_TYPE_UUID);
		
		assertNotNull(result);
		assertEquals("Chest X-Ray", result.getName());
		assertEquals("CXR", result.getShortName());
		assertEquals(LabTestGroup.RADIOLOGY, result.getTestGroup());
	}
	
	@Test
	public void getByUuid_shouldReturnNullForUnknownUuid() {
		LabTestType result = controller.getByUniqueId("00000000-0000-0000-0000-000000000000");
		
		assertEquals(null, result);
	}
	
	// ── GET all ────────────────────────────────────────────────────────────────
	
	@Test
	public void getAll_shouldReturnAllNonRetiredLabTestTypes() throws ResponseException {
		PageableResult result = controller.getAllForTest(requestContext());
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestType> page = ((NeedsPaging<LabTestType>) result).getPageOfResults();
		// Data has 4 types (IDs 1-4), none retired
		assertEquals(4, page.size());
	}
	
	@Test
	public void getAll_shouldIncludeGeneXpertAndChestXRayTypes() throws ResponseException {
		PageableResult result = controller.getAllForTest(requestContext());
		
		@SuppressWarnings("unchecked")
		List<LabTestType> page = ((NeedsPaging<LabTestType>) result).getPageOfResults();
		assertThat(page, hasItem(hasProperty("name", is("GeneXpert Test"))));
		assertThat(page, hasItem(hasProperty("name", is("Chest X-Ray"))));
	}
	
	// ── POST (save / create) ───────────────────────────────────────────────────
	
	@Test
	public void create_shouldSaveNewLabTestType() {
		LabTestType newType = new LabTestType();
		newType.setName("AFB Smear Microscopy");
		newType.setShortName("AFB");
		newType.setTestGroup(LabTestGroup.BACTERIOLOGY);
		newType.setRequiresSpecimen(Boolean.TRUE);
		// referenceConcept is NOT NULL in the schema — use concept 500 from the test dataset
		newType.setReferenceConcept(Context.getConceptService().getConcept(500));
		
		LabTestType saved = controller.save(newType);
		
		assertNotNull(saved);
		assertNotNull(saved.getId());
		assertEquals("AFB Smear Microscopy", saved.getName());
	}
	
	@Test
	public void create_shouldPersistLabTestTypeToDatabase() {
		LabTestType newType = new LabTestType();
		newType.setName("Spirometry Test");
		newType.setShortName("SPT");
		newType.setTestGroup(LabTestGroup.OTHER);
		newType.setRequiresSpecimen(Boolean.FALSE);
		newType.setReferenceConcept(Context.getConceptService().getConcept(500));
		
		LabTestType saved = controller.save(newType);
		assertNotNull(saved.getId());
		
		// Verify it can be retrieved by UUID
		LabTestType fetched = controller.getByUniqueId(saved.getUuid());
		assertNotNull(fetched);
		assertEquals("Spirometry Test", fetched.getName());
	}
}
