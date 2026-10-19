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
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Integration tests for {@link LabTestAttributeTypeResourceController}. Endpoint: GET/POST
 * /rest/v1/commonlab/labtestattributetype
 */
public class LabTestAttributeTypeResourceControllerTest extends LabResourceTestBase {
	
	// Expose protected doGetAll and doSearch
	private static class TestableController extends LabTestAttributeTypeResourceController {
		
		public PageableResult getAllForTest(RequestContext context) throws ResponseException {
			return doGetAll(context);
		}
		
		public PageableResult searchForTest(RequestContext context) throws ResponseException {
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
	public void getByUuid_shouldReturnCartridgeIdAttributeType() {
		LabTestAttributeType result = controller.getByUniqueId(CARTRIDGE_ID_ATTR_UUID);
		
		assertNotNull(result);
		assertEquals("Cartridge ID", result.getName());
		assertEquals(geneXpert, result.getLabTestType());
	}
	
	@Test
	public void getByUuid_shouldReturnRetiredAttributeTypeWhenRequested() {
		LabTestAttributeType result = controller.getByUniqueId(CAD4TB_SCORE_ATTR_UUID);
		
		assertNotNull(result);
		assertEquals("CAD4TB Score", result.getName());
		assertEquals(Boolean.TRUE, result.getRetired());
	}
	
	@Test
	public void getByUuid_shouldReturnNullForUnknownUuid() {
		LabTestAttributeType result = controller.getByUniqueId("00000000-0000-0000-0000-000000000000");
		
		assertEquals(null, result);
	}
	
	// ── GET all (non-retired) ──────────────────────────────────────────────────
	
	@Test
	public void getAll_shouldReturnOnlyNonRetiredAttributeTypes() throws ResponseException {
		PageableResult result = controller.getAllForTest(requestContext());
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestAttributeType> page = ((NeedsPaging<LabTestAttributeType>) result).getPageOfResults();
		// 5 non-retired attribute types (IDs 1-5); IDs 6 and 7 are retired
		assertEquals(5, page.size());
	}
	
	@Test
	public void getAll_shouldNotIncludeRetiredAttributeTypes() throws ResponseException {
		PageableResult result = controller.getAllForTest(requestContext());
		
		@SuppressWarnings("unchecked")
		List<LabTestAttributeType> page = ((NeedsPaging<LabTestAttributeType>) result).getPageOfResults();
		for (LabTestAttributeType attrType : page) {
			assertEquals("Retired attribute type should not appear in getAll", Boolean.FALSE, attrType.getRetired());
		}
	}
	
	// ── GET search by LabTestType ──────────────────────────────────────────────
	
	@Test
	public void search_shouldReturnAttributeTypesForGeneXpertTestType() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("testTypeUuid", GENEXPERT_TYPE_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestAttributeType> page = ((NeedsPaging<LabTestAttributeType>) result).getPageOfResults();
		// GeneXpert has 3 non-retired attribute types: Cartridge ID, MTB Result, RIF Result
		assertEquals(3, page.size());
		assertThat(page, hasItem(hasProperty("name", is("Cartridge ID"))));
		assertThat(page, hasItem(hasProperty("name", is("MTB Result"))));
		assertThat(page, hasItem(hasProperty("name", is("RIF Result"))));
	}
	
	@Test
	public void search_shouldReturnAttributeTypesForChestXRayTestType() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("testTypeUuid", CHEST_XRAY_TYPE_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestAttributeType> page = ((NeedsPaging<LabTestAttributeType>) result).getPageOfResults();
		// Chest X-Ray has 2 non-retired attribute types: CXR Result, Radiologist Remarks
		assertEquals(2, page.size());
		assertThat(page, hasItem(hasProperty("name", is("Chest X-Ray Result"))));
		assertThat(page, hasItem(hasProperty("name", is("Radiologist Remarks"))));
	}
	
	// ── POST (save / create) ───────────────────────────────────────────────────
	
	@Test
	public void create_shouldSaveNewLabTestAttributeType() {
		LabTestAttributeType newAttrType = new LabTestAttributeType();
		newAttrType.setName("New Attribute");
		newAttrType.setLabTestType(geneXpert);
		newAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		newAttrType.setMinOccurs(0);
		newAttrType.setSortWeight(10D);
		
		LabTestAttributeType saved = controller.save(newAttrType);
		
		assertNotNull(saved);
		assertNotNull(saved.getId());
		assertEquals("New Attribute", saved.getName());
	}
	
	@Test
	public void create_shouldPersistAttributeTypeWithLabTestTypeAssociation() {
		LabTestAttributeType newAttrType = new LabTestAttributeType();
		newAttrType.setName("Probe Serial Number");
		newAttrType.setLabTestType(geneXpert);
		newAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		newAttrType.setSortWeight(5D);
		
		LabTestAttributeType saved = controller.save(newAttrType);
		
		LabTestAttributeType fetched = controller.getByUniqueId(saved.getUuid());
		assertNotNull(fetched);
		assertEquals(geneXpert, fetched.getLabTestType());
	}
}
