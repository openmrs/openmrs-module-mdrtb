package org.openmrs.module.mdrtb.web.resource.lab;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for lab resource controller integration tests. Loads LabService-initialData.xml and
 * authenticates before each test.
 * <p>
 * The extra {@code test-rest-context.xml} supplies {@code restService} and
 * {@code restHelperService} beans that {@code MainSubResourceController} needs — those controllers
 * are picked up by the broad {@code org.openmrs} component-scan in
 * {@code applicationContext-service.xml} but their dependencies are normally satisfied by the
 * web-layer context that isn't loaded during module integration tests.
 */
@ContextConfiguration(inheritLocations = true, locations = { "classpath:test-rest-context.xml" })
public abstract class LabResourceTestBase extends BaseModuleContextSensitiveTest {
	
	protected static final String DATA_XML = "LabService-initialData.xml";
	
	// Patient UUIDs
	protected static final String HARRY_UUID = "993c46d2-5007-45e8-9512-969300717761";
	
	protected static final String HERMIONE_UUID = "1f6959e5-d15a-4025-bb48-340ee9e2c58d";
	
	// Provider UUIDs
	protected static final String OWAIS_PROVIDER_UUID = "1a61a0b5-d271-4b00-a803-5cef8b06ba8f";
	
	protected static final String TAHIRA_PROVIDER_UUID = "449390a0-d338-4ff2-9b3f-61dc1617c2fe";
	
	// LabTestType UUIDs
	protected static final String UNKNOWN_TYPE_UUID = "ee9b140e-9a29-11e8-a296-40b034c3cfee";
	
	protected static final String GENEXPERT_TYPE_UUID = "4bf46c09-46e9-11e8-943c-40b034c3cfee";
	
	protected static final String CHEST_XRAY_TYPE_UUID = "a277edf4-46ea-11e8-943c-40b034c3cfee";
	
	// LabTestAttributeType UUIDs
	protected static final String CARTRIDGE_ID_ATTR_UUID = "ecf166e5-478e-11e8-943c-40b034c3cfee";
	
	protected static final String MTB_RESULT_ATTR_UUID = "ea22684f-478e-11e8-943c-40b034c3cfee";
	
	protected static final String RIF_RESULT_ATTR_UUID = "eb66655f-478e-11e8-943c-40b034c3cfee";
	
	protected static final String CXR_RESULT_ATTR_UUID = "efeb9339-538d-11e8-9c7c-40b034c3cfee";
	
	protected static final String RADIOLOGIST_REMARKS_ATTR_UUID = "f43de058-538d-11e8-9c7c-40b034c3cfee";
	
	protected static final String CAD4TB_SCORE_ATTR_UUID = "ed8b4caf-478e-11e8-943c-40b034c3cfee"; // retired
	
	protected static final String XRAY_FILM_ATTR_UUID = "ee261470-478e-11e8-943c-40b034c3cfee"; // retired
	
	// LabTest (order) UUIDs
	protected static final String HARRY_GXP_UUID = "d175e92e-47bf-11e8-943c-40b034c3cfee";
	
	protected static final String HARRY_CXR_UUID = "d23c2576-47bf-11e8-943c-40b034c3cfee";
	
	protected static final String HERMIONE_GXP_UUID = "d175e92e-dc93-11e8-d298-40b034c3cfee";
	
	// LabTestSample UUIDs
	protected static final String HARRY_SAMPLE_UUID = "f4bffc2f-5343-11e8-9c7c-40b034c3cfee";
	
	protected static final String HERMIONE_SAMPLE_UUID = "f40420f8-5346-11e8-9c7c-40b034c3cfee";
	
	// LabTestAttribute UUIDs
	protected static final String HARRY_CARTRIDGE_ID_ATTR_UUID = "2c9737d9-47c2-11e8-943c-40b034c3cfee";
	
	protected static final String HARRY_MTB_RESULT_ATTR_UUID = "2d9cc0d3-47c2-11e8-943c-40b034c3cfee";
	
	protected static final String HARRY_RIF_RESULT_ATTR_UUID = "2e45af47-47c2-11e8-943c-40b034c3cfee";
	
	protected static final String HARRY_CXR_RESULT_ATTR_UUID = "2efe1af7-47c2-11e8-943c-40b034c3cfee";
	
	protected static final String HERMIONE_CARTRIDGE_ID_ATTR_UUID = "b46ad728-51f0-11e8-b60d-080027ea421d";
	
	// Convenience references loaded in @Before
	protected LabTestService labTestService;
	
	protected LabTestType geneXpert;
	
	protected LabTestType chestXRay;
	
	protected LabTest harryGxp;
	
	protected LabTest harryCxr;
	
	protected LabTest hermioneGxp;
	
	protected LabTestSample harrySample;
	
	protected LabTestAttributeType cartridgeIdAttrType;
	
	protected LabTestAttributeType mtbResultAttrType;
	
	@Before
	public void setUpLabData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(DATA_XML);
		authenticate();
		
		labTestService = Context.getService(LabTestService.class);
		geneXpert = labTestService.getLabTestTypeByUuid(GENEXPERT_TYPE_UUID);
		chestXRay = labTestService.getLabTestTypeByUuid(CHEST_XRAY_TYPE_UUID);
		harryGxp = labTestService.getLabTestByUuid(HARRY_GXP_UUID);
		harryCxr = labTestService.getLabTestByUuid(HARRY_CXR_UUID);
		hermioneGxp = labTestService.getLabTestByUuid(HERMIONE_GXP_UUID);
		harrySample = labTestService.getLabTestSampleByUuid(HARRY_SAMPLE_UUID);
		cartridgeIdAttrType = labTestService.getLabTestAttributeTypeByUuid(CARTRIDGE_ID_ATTR_UUID);
		mtbResultAttrType = labTestService.getLabTestAttributeTypeByUuid(MTB_RESULT_ATTR_UUID);
	}
	
	protected RequestContext requestContext(String... params) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		for (int i = 0; i + 1 < params.length; i += 2) {
			request.addParameter(params[i], params[i + 1]);
		}
		RequestContext ctx = new RequestContext();
		ctx.setRequest(request);
		return ctx;
	}
}
