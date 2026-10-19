package org.openmrs.module.mdrtb.web.resource.lab;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestSampleStatus;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Integration tests for {@link LabTestSampleResourceController}. Endpoint: GET/POST
 * /rest/v1/commonlab/labtestsample
 */
public class LabTestSampleResourceControllerTest extends LabResourceTestBase {
	
	private static class TestableController extends LabTestSampleResourceController {
		
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
	public void getByUuid_shouldReturnHarrySampleForGivenUuid() {
		LabTestSample result = controller.getByUniqueId(HARRY_SAMPLE_UUID);
		
		assertNotNull(result);
		assertEquals("GXP-IRS12345-1", result.getSampleIdentifier());
		assertEquals(LabTestSampleStatus.PROCESSED, result.getStatus());
	}
	
	@Test
	public void getByUuid_shouldReturnHermioneSampleForGivenUuid() {
		LabTestSample result = controller.getByUniqueId(HERMIONE_SAMPLE_UUID);
		
		assertNotNull(result);
		assertEquals("GXP-IRS12345-2", result.getSampleIdentifier());
		assertEquals(LabTestSampleStatus.ACCEPTED, result.getStatus());
	}
	
	@Test
	public void getByUuid_shouldReturnNullForUnknownUuid() {
		LabTestSample result = controller.getByUniqueId("00000000-0000-0000-0000-000000000000");
		
		assertEquals(null, result);
	}
	
	// ── GET search ─────────────────────────────────────────────────────────────
	
	@Test
	public void search_shouldReturnSamplesForGivenLabOrder() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("laborder", HARRY_GXP_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestSample> page = ((NeedsPaging<LabTestSample>) result).getPageOfResults();
		// Harry GXP has 1 non-voided sample
		assertEquals(1, page.size());
		assertThat(page, hasItem(hasProperty("sampleIdentifier", is("GXP-IRS12345-1"))));
	}
	
	@Test
	public void search_shouldReturnSamplesForGivenPatient() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("patient", HARRY_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestSample> page = ((NeedsPaging<LabTestSample>) result).getPageOfResults();
		assertThat(page, hasItem(hasProperty("sampleIdentifier", is("GXP-IRS12345-1"))));
	}
	
	@Test
	public void search_shouldReturnSamplesBySampleIdentifier() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("sampleidentifier", "GXP-IRS12345-1"));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestSample> page = ((NeedsPaging<LabTestSample>) result).getPageOfResults();
		assertEquals(1, page.size());
		assertEquals("GXP-IRS12345-1", page.get(0).getSampleIdentifier());
	}
	
	@Test
	public void search_shouldReturnSamplesByCollector() throws ResponseException {
		PageableResult result = controller.searchForTest(requestContext("collector", OWAIS_PROVIDER_UUID));
		
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<LabTestSample> page = ((NeedsPaging<LabTestSample>) result).getPageOfResults();
		assertThat(page, hasItem(hasProperty("sampleIdentifier", is("GXP-IRS12345-1"))));
	}
	
	// ── POST (save / create) ───────────────────────────────────────────────────
	
	@Test
	public void create_shouldSaveNewLabTestSample() {
		LabTestSample newSample = new LabTestSample();
		newSample.setLabTest(harryGxp);
		newSample.setSampleIdentifier("NEW-HARRY-SPUTUM-2");
		newSample.setCollectionDate(new Date());
		newSample.setCollector(Context.getProviderService().getProviderByUuid(OWAIS_PROVIDER_UUID));
		newSample.setStatus(LabTestSampleStatus.ACCEPTED);
		// specimenType is NOT NULL in the schema — use concept 500 from the test dataset
		newSample.setSpecimenType(Context.getConceptService().getConcept(500));
		
		LabTestSample saved = controller.save(newSample);
		
		assertNotNull(saved);
		assertNotNull(saved.getId());
		assertEquals("NEW-HARRY-SPUTUM-2", saved.getSampleIdentifier());
	}
	
	@Test
	public void create_shouldPersistSampleLinkedToLabTest() {
		LabTestSample newSample = new LabTestSample();
		newSample.setLabTest(hermioneGxp);
		newSample.setSampleIdentifier("HERMIONE-SPUTUM-3");
		newSample.setCollectionDate(new Date());
		newSample.setCollector(Context.getProviderService().getProviderByUuid(OWAIS_PROVIDER_UUID));
		newSample.setStatus(LabTestSampleStatus.ACCEPTED);
		newSample.setSpecimenType(Context.getConceptService().getConcept(500));
		
		LabTestSample saved = controller.save(newSample);
		
		LabTestSample fetched = controller.getByUniqueId(saved.getUuid());
		assertNotNull(fetched);
		assertEquals("HERMIONE-SPUTUM-3", fetched.getSampleIdentifier());
		assertEquals(hermioneGxp, fetched.getLabTest());
	}
}
