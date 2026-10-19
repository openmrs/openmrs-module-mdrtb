package org.openmrs.module.mdrtb.web.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MessageProperty;
import org.openmrs.module.mdrtb.api.MessagePropertyService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.api.MissingRequiredPropertyException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for {@link MessagePropertyResourceController}. Endpoint:
 * /ws/rest/v1/messageproperty
 * <p>
 * These call the controller methods directly rather than going through the dispatcher: the point is
 * the CRUD contract, not Spring's request mapping. The one thing that cannot be covered this way is
 * the {@code code:.+} regex that stops dotted codes being truncated as file extensions — that only
 * shows up in real routing, so it is worth one manual curl after deploying.
 */
public class MessagePropertyResourceControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String EN = "en";
	
	private MessagePropertyResourceController controller;
	
	private MessagePropertyService service;
	
	@Before
	public void setup() {
		controller = new MessagePropertyResourceController();
		service = Context.getService(MessagePropertyService.class);
		service.resetMessageCache();
	}
	
	private MessageProperty given(String lang, String code, String message) {
		return service.saveMessageProperty(new MessageProperty(lang, code, message));
	}
	
	private SimpleObject body(String lang, String code, String message) {
		SimpleObject object = new SimpleObject();
		if (lang != null) {
			object.add("lang", lang);
		}
		if (code != null) {
			object.add("code", code);
		}
		if (message != null) {
			object.add("message", message);
		}
		return object;
	}
	
	/*********************/
	/** GET ONE **/
	/*********************/
	
	@Test
	public void getMessageProperty_shouldReturnTheMessageForALanguageAndCode() {
		given(EN, "mdrtb.unknown", "Unknown");
		
		SimpleObject found = controller.getMessageProperty(EN, "mdrtb.unknown");
		
		assertEquals(EN, found.get("lang"));
		assertEquals("mdrtb.unknown", found.get("code"));
		assertEquals("Unknown", found.get("message"));
		assertEquals("mdrtb.unknown (en)", found.get("display"));
	}
	
	@Test
	public void getMessageProperty_shouldHandleDottedCodes() {
		given("ru", "mdrtb.tb03.gender.male", "Мужской");
		
		assertEquals("Мужской", controller.getMessageProperty("ru", "mdrtb.tb03.gender.male").get("message"));
	}
	
	@Test
	public void getMessageProperty_shouldReturnNotFoundForAnUnknownCode() {
		try {
			controller.getMessageProperty(EN, "mdrtb.noSuchCode");
			fail("expected ObjectNotFoundException");
		}
		catch (ObjectNotFoundException expected) {
			// 404 rather than an empty 200
		}
	}
	
	@Test
	public void getMessageProperty_shouldReturnNotFoundForAnUnknownLanguage() {
		given(EN, "mdrtb.unknown", "Unknown");
		try {
			controller.getMessageProperty("zz", "mdrtb.unknown");
			fail("expected ObjectNotFoundException");
		}
		catch (ObjectNotFoundException expected) {
			// the code exists, but not in that language
		}
	}
	
	/*********************/
	/** LIST AND FILTER **/
	/*********************/
	
	@Test
	public void getMessageProperties_shouldReturnEverythingWhenUnfiltered() {
		given(EN, "mdrtb.unknown", "Unknown");
		given("ru", "mdrtb.unknown", "Данных нет");
		
		assertEquals(2, controller.getMessageProperties(null, null).size());
	}
	
	@Test
	public void getMessageProperties_shouldFilterByLanguage() {
		given(EN, "mdrtb.unknown", "Unknown");
		given(EN, "mdrtb.yes", "Yes");
		given("ru", "mdrtb.unknown", "Данных нет");
		
		List<SimpleObject> results = controller.getMessageProperties("ru", null);
		
		assertEquals(1, results.size());
		assertEquals("Данных нет", results.get(0).get("message"));
	}
	
	@Test
	public void getMessageProperties_shouldFilterByCodeSubstringIgnoringCase() {
		given(EN, "mdrtb.tb03.gender.male", "Male");
		given(EN, "mdrtb.tb03.gender.female", "Female");
		given(EN, "mdrtb.unknown", "Unknown");
		
		assertEquals(2, controller.getMessageProperties(null, "gender").size());
		assertEquals(2, controller.getMessageProperties(null, "GENDER").size());
	}
	
	@Test
	public void getMessageProperties_shouldCombineBothFilters() {
		given(EN, "mdrtb.tb03.gender.male", "Male");
		given("ru", "mdrtb.tb03.gender.male", "Мужской");
		given("ru", "mdrtb.unknown", "Данных нет");
		
		List<SimpleObject> results = controller.getMessageProperties("ru", "gender");
		
		assertEquals(1, results.size());
		assertEquals("Мужской", results.get(0).get("message"));
	}
	
	@Test
	public void getMessageProperties_shouldReturnEmptyWhenNothingMatches() {
		given(EN, "mdrtb.unknown", "Unknown");
		
		assertTrue(controller.getMessageProperties("zz", null).isEmpty());
	}
	
	/*********************/
	/** POST / UPSERT **/
	/*********************/
	
	@Test
	public void saveMessageProperty_shouldCreateAndReport201WhenTheKeyIsNew() {
		ResponseEntity<SimpleObject> response = controller.saveMessageProperty(body(EN, "mdrtb.brandNew", "Brand new"));
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Brand new", response.getBody().get("message"));
		assertEquals("Brand new", service.getMessageProperty(EN, "mdrtb.brandNew").getMessage());
	}
	
	@Test
	public void saveMessageProperty_shouldUpdateAndReport200WhenTheKeyAlreadyExists() {
		given(EN, "mdrtb.unknown", "Unknown");
		
		ResponseEntity<SimpleObject> response = controller.saveMessageProperty(body(EN, "mdrtb.unknown", "Corrected"));
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Corrected", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
	}
	
	@Test
	public void saveMessageProperty_shouldMatchOnLanguageAndCodeFromTheBody() {
		given(EN, "mdrtb.unknown", "Unknown");
		given("ru", "mdrtb.unknown", "Данных нет");
		
		controller.saveMessageProperty(body("ru", "mdrtb.unknown", "Исправлено"));
		
		// the same code in another language must be left alone
		assertEquals("Исправлено", service.getMessageProperty("ru", "mdrtb.unknown").getMessage());
		assertEquals("Unknown", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
	}
	
	@Test
	public void saveMessageProperty_shouldMakeTheChangeVisibleToMessageResolution() {
		given(EN, "mdrtb.unknown", "Unknown");
		assertEquals("Unknown", service.getMessage("mdrtb.unknown"));
		
		controller.saveMessageProperty(body(EN, "mdrtb.unknown", "Corrected through REST"));
		
		// the cache has to be dropped by the save, or the endpoint would appear to do nothing
		assertEquals("Corrected through REST", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void saveMessageProperty_shouldAcceptAnEmptyMessage() {
		ResponseEntity<SimpleObject> response = controller.saveMessageProperty(body(EN, "mdrtb.blank", null));
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(service.getMessageProperty(EN, "mdrtb.blank"));
	}
	
	@Test
	public void saveMessageProperty_shouldRejectAMissingLanguage() {
		try {
			controller.saveMessageProperty(body(null, "mdrtb.unknown", "Unknown"));
			fail("expected MissingRequiredPropertyException");
		}
		catch (MissingRequiredPropertyException expected) {
			// lang is half the primary key
		}
	}
	
	@Test
	public void saveMessageProperty_shouldRejectAMissingCode() {
		try {
			controller.saveMessageProperty(body(EN, null, "Unknown"));
			fail("expected MissingRequiredPropertyException");
		}
		catch (MissingRequiredPropertyException expected) {
			// code is the other half
		}
	}
	
	/*********************/
	/** DELETE **/
	/*********************/
	
	@Test
	public void deleteMessageProperty_shouldRemoveTheRow() {
		given(EN, "mdrtb.unknown", "Unknown");
		
		controller.deleteMessageProperty(EN, "mdrtb.unknown");
		
		assertNull(service.getMessageProperty(EN, "mdrtb.unknown"));
	}
	
	@Test
	public void deleteMessageProperty_shouldOnlyRemoveTheGivenLanguage() {
		given(EN, "mdrtb.unknown", "Unknown");
		given("ru", "mdrtb.unknown", "Данных нет");
		
		controller.deleteMessageProperty("ru", "mdrtb.unknown");
		
		assertNull(service.getMessageProperty("ru", "mdrtb.unknown"));
		assertNotNull(service.getMessageProperty(EN, "mdrtb.unknown"));
	}
	
	@Test
	public void deleteMessageProperty_shouldReturnNotFoundWhenThereIsNothingToRemove() {
		try {
			controller.deleteMessageProperty(EN, "mdrtb.neverStored");
			fail("expected ObjectNotFoundException");
		}
		catch (ObjectNotFoundException expected) {
			// a mistyped code should not look like a successful delete
		}
	}
	
	@Test
	public void deleteMessageProperty_shouldMakeTheRemovalVisibleToMessageResolution() {
		given(EN, "mdrtb.unknown", "Unknown");
		assertEquals("Unknown", service.getMessage("mdrtb.unknown"));
		
		controller.deleteMessageProperty(EN, "mdrtb.unknown");
		
		// no row and no cache entry: resolution falls through and returns the code itself
		assertEquals("mdrtb.unknown", service.getMessage("mdrtb.unknown"));
	}
}
