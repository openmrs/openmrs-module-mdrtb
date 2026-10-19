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
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.validator.ValidateUtil;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Integration tests for {@link LabTestAttributeResourceController}. Endpoint: GET/POST
 * /rest/v1/commonlab/labtestattribute The search for this resource takes {@code testOrderId}
 * (integer order ID, NOT UUID).
 */
public class LabTestAttributeResourceControllerTest extends LabResourceTestBase {
	
	private static class TestableController extends LabTestAttributeResourceController {
		
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
	public void getByUuid_shouldReturnHarryCartridgeIdAttribute() {
		LabTestAttribute result = controller.getByUniqueId(HARRY_CARTRIDGE_ID_ATTR_UUID);
		
		assertNotNull(result);
		assertEquals("1301", result.getValueReference());
		assertEquals(cartridgeIdAttrType, result.getAttributeType());
	}
	
	@Test
	public void getByUuid_shouldReturnHarryMtbResultAttribute() {
		LabTestAttribute result = controller.getByUniqueId(HARRY_MTB_RESULT_ATTR_UUID);
		
		assertNotNull(result);
		assertEquals("1138", result.getValueReference());
		assertEquals(mtbResultAttrType, result.getAttributeType());
	}
	
	@Test
	public void getByUuid_shouldReturnHarryCxrResultAttribute() {
		LabTestAttribute result = controller.getByUniqueId(HARRY_CXR_RESULT_ATTR_UUID);
		
		assertNotNull(result);
		// value_reference in data is "100"
		assertEquals("100", result.getValueReference());
	}
	
	@Test
	public void getByUuid_shouldReturnNullForUnknownUuid() {
		LabTestAttribute result = controller.getByUniqueId("00000000-0000-0000-0000-000000000000");
		
		assertEquals(null, result);
	}
	
	// ── GET search by testOrderId ──────────────────────────────────────────────
	
	@Test
	public void search_shouldReturnAllAttributesForHarryGxpOrder() throws ResponseException {
		// Order ID 100 = Harry's GXP order
		PageableResult result = controller.searchForTest(requestContext("testOrderId", "100"));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestAttribute> page = ((NeedsPaging<LabTestAttribute>) result).getPageOfResults();
		// Harry GXP has attributes: IDs 1 (Cartridge), 2 (MTB), 3 (RIF), 8 (CAD4TB)
		assertEquals(4, page.size());
		assertThat(page, hasItem(hasProperty("uuid", is(HARRY_CARTRIDGE_ID_ATTR_UUID))));
		assertThat(page, hasItem(hasProperty("uuid", is(HARRY_MTB_RESULT_ATTR_UUID))));
		assertThat(page, hasItem(hasProperty("uuid", is(HARRY_RIF_RESULT_ATTR_UUID))));
	}
	
	@Test
	public void search_shouldReturnAttributesForHarryCxrOrder() throws ResponseException {
		// Order ID 200 = Harry's CXR order
		PageableResult result = controller.searchForTest(requestContext("testOrderId", "200"));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestAttribute> page = ((NeedsPaging<LabTestAttribute>) result).getPageOfResults();
		// Harry CXR has attributes: IDs 4 (CXR Result), 5 (Radiologist Remarks)
		assertEquals(2, page.size());
		assertThat(page, hasItem(hasProperty("uuid", is(HARRY_CXR_RESULT_ATTR_UUID))));
	}
	
	@Test
	public void search_shouldReturnAttributesForHermioneGxpOrder() throws ResponseException {
		// Order ID 300 = Hermione's GXP order
		PageableResult result = controller.searchForTest(requestContext("testOrderId", "300"));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestAttribute> page = ((NeedsPaging<LabTestAttribute>) result).getPageOfResults();
		// Hermione GXP has attributes: IDs 6 (Cartridge), 7 (RIF Result)
		assertEquals(2, page.size());
		assertThat(page, hasItem(hasProperty("uuid", is(HERMIONE_CARTRIDGE_ID_ATTR_UUID))));
	}
	
	// ── POST (save / create) ───────────────────────────────────────────────────
	
	@Test
	public void create_shouldSaveNewLabTestAttribute() {
		LabTestAttribute newAttr = new LabTestAttribute();
		newAttr.setLabTest(harryGxp);
		newAttr.setAttributeType(cartridgeIdAttrType);
		newAttr.setValueReferenceInternal("NEWCARTRIDGE001");
		
		LabTestAttribute saved = controller.save(newAttr);
		
		assertNotNull(saved);
		assertNotNull(saved.getId());
	}
	
	@Test
	public void create_shouldPersistAttributeLinkedToLabTest() {
		LabTestAttribute newAttr = new LabTestAttribute();
		newAttr.setLabTest(hermioneGxp);
		newAttr.setAttributeType(mtbResultAttrType);
		// mtbResultAttrType uses ConceptDatatype — deserialize calls getConceptByUuid().
		// Concept 500 UUID is present in H2; disable thread-local validation so the
		// ConceptValidator (which requires coded answers) does not reject the in-memory concept.
		String concept500Uuid = "a8102d6d-c528-477a-80bd-acc38ebc6252";
		newAttr.setValueReferenceInternal(concept500Uuid);
		
		ValidateUtil.setDisableValidation(Boolean.TRUE);
		try {
			LabTestAttribute saved = controller.save(newAttr);
			assertNotNull(saved.getId());
			
			LabTestAttribute fetched = controller.getByUniqueId(saved.getUuid());
			assertNotNull(fetched);
			assertEquals(concept500Uuid, fetched.getValueReference());
			assertEquals(hermioneGxp, fetched.getLabTest());
		}
		finally {
			ValidateUtil.setDisableValidation(Boolean.FALSE);
		}
	}
}
