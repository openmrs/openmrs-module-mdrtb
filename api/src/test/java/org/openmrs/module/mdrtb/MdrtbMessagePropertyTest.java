/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MessagePropertyService;
import org.openmrs.module.mdrtb.api.dao.MessagePropertyDao;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Covers the database-backed message pipeline end to end: seeding {@code message_properties} from
 * the shipped bundles, the locale fallback chain, delegation to OpenMRS for codes this module does
 * not own, MessageFormat handling, and the cache.
 * <p>
 * Each test runs in a transaction that the OpenMRS test framework rolls back, so the table starts
 * empty every time. The service bean, however, is a singleton and its cache is not transactional,
 * which is why {@link #runBeforeEachTest()} resets it explicitly.
 * <p>
 * Tests that only need a handful of messages insert them straight through the DAO rather than
 * seeding all five bundles, which keeps the suite fast. The full seed is exercised by the
 * {@code seedMessageProperties_*} tests.
 */
public class MdrtbMessagePropertyTest extends MdrtbTestBase {
	
	private static final String EN = "en";
	
	private MessagePropertyService service;
	
	@Autowired
	private MessagePropertyDao messagePropertyDao;
	
	/** Seeding lives on the activator, so that is what these tests drive. */
	private MdrtbActivator activator;
	
	private Locale originalLocale;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MessagePropertyService.class);
		activator = new MdrtbActivator();
		originalLocale = Context.getLocale();
		// the bean is a singleton; do not let one test's cache leak into the next
		service.resetMessageCache();
	}
	
	@After
	public void runAfterEachTest() {
		Context.setLocale(originalLocale);
		service.resetMessageCache();
	}
	
	private void givenMessage(String lang, String code, String message) {
		messagePropertyDao.saveMessageProperty(new MessageProperty(lang, code, message));
		service.resetMessageCache();
	}
	
	@Test
	public void seedMessageProperties_shouldPopulateTheTableFromTheShippedBundles() {
		activator.seedMessageProperties();
		
		int stored = service.getAllMessageProperties().size();
		assertTrue("expected the five bundles to contribute several thousand rows, stored " + stored, stored > 1400);
		assertEquals("Unknown", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
		assertEquals("\u0414\u0430\u043d\u043d\u044b\u0445 \u043d\u0435\u0442",
		    service.getMessageProperty("ru", "mdrtb.unknown").getMessage());
		assertEquals("\u041d\u043e\u043c\u0430\u044a\u043b\u0443\u043c", service.getMessageProperty("tj", "mdrtb.unknown")
		        .getMessage());
		assertNull("the fr bundle was removed from the module", service.getMessageProperty("fr", "mdrtb.unknown"));
	}
	
	@Test
	public void seedMessageProperties_shouldInsertNothingOnASecondRun() {
		activator.seedMessageProperties();
		int afterFirstRun = service.getAllMessageProperties().size();
		
		activator.seedMessageProperties();
		
		assertTrue(afterFirstRun > 0);
		assertEquals("a second run must not add or duplicate rows", afterFirstRun, service.getAllMessageProperties().size());
	}
	
	@Test
	public void seedMessageProperties_shouldNotOverwriteRowsThatAlreadyExist() {
		// stands in for a translation an administrator has corrected directly in the database
		givenMessage(EN, "mdrtb.unknown", "Corrected in the database");
		
		activator.seedMessageProperties();
		
		assertEquals("Corrected in the database", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
		// ...while every other code in the bundle still gets inserted
		assertEquals("Yes", service.getMessageProperty(EN, "mdrtb.yes").getMessage());
	}
	
	@Test
	public void seedMessageProperties_shouldTopUpAPartiallyPopulatedTable() {
		// neither empty nor complete: seeding must still add the missing codes rather than deciding
		// there is nothing to do
		givenMessage(EN, "mdrtb.unknown", "Corrected in the database");
		givenMessage("ru", "mdrtb.unknown", "\u0418\u0441\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u043e");
		assertEquals(2, service.getAllMessageProperties().size());
		
		activator.seedMessageProperties();
		
		assertTrue(service.getAllMessageProperties().size() > 1400);
		assertEquals("Corrected in the database", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
		assertEquals("\u0418\u0441\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u043e",
		    service.getMessageProperty("ru", "mdrtb.unknown").getMessage());
	}
	
	@Test
	public void seedMessageProperties_shouldMakeMessagesResolvableStraightAway() {
		activator.seedMessageProperties();
		Context.setLocale(new Locale("ru"));
		
		assertEquals("\u0414\u0430\u043d\u043d\u044b\u0445 \u043d\u0435\u0442", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void getMessage_shouldReturnTheMessageForTheCurrentLocale() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		givenMessage("ru", "mdrtb.unknown", "Данных нет");
		Context.setLocale(new Locale("ru"));
		assertEquals("Данных нет", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void getMessage_shouldPreferAnExactLocaleMatchOverTheLanguageAlone() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		givenMessage("id_ID", "mdrtb.unknown", "Tidak diketahui");
		Context.setLocale(new Locale("id", "ID"));
		assertEquals("Tidak diketahui", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void getMessage_shouldFallBackFromARegionalLocaleToItsLanguage() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		givenMessage("fr", "mdrtb.unknown", "Inconnu");
		// fr_FR has no bundle, but fr does
		Context.setLocale(new Locale("fr", "FR"));
		assertEquals("Inconnu", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void getMessage_shouldFallBackToEnglishForAnUntranslatedLocale() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		Context.setLocale(new Locale("de", "DE"));
		assertEquals("Unknown", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void getMessage_shouldFallBackToEnglishWhenTheLocaleIsMissingOnlyThisCode() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		givenMessage(EN, "mdrtb.yes", "Yes");
		givenMessage("ru", "mdrtb.unknown", "Данных нет");
		Context.setLocale(new Locale("ru"));
		assertEquals("Данных нет", service.getMessage("mdrtb.unknown"));
		assertEquals("Yes", service.getMessage("mdrtb.yes"));
	}
	
	@Test
	public void getMessage_shouldDelegateToOpenmrsForCodesThisModuleDoesNotOwn() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		String code = "Patient.identifier";
		String fromOpenmrs = Context.getMessageSourceService().getMessage(code, null, null, Context.getLocale());
		
		if (fromOpenmrs != null && !code.equals(fromOpenmrs)) {
			assertEquals(fromOpenmrs, service.getMessage(code));
		} else {
			// core did not register the code in this test context
			assertEquals(code, service.getMessage(code));
		}
	}
	
	@Test
	public void getMessage_shouldPreferThisModulesMessageOverOpenmrsOwn() {
		String code = "Patient.identifier";
		givenMessage(EN, code, "Overridden by MDR-TB");
		assertEquals("Overridden by MDR-TB", service.getMessage(code));
	}
	
	@Test
	public void getMessage_shouldReturnTheCodeWhenNothingResolvesIt() {
		assertEquals("mdrtb.no.such.code.anywhere", service.getMessage("mdrtb.no.such.code.anywhere"));
	}
	
	@Test
	public void getMessage_shouldReturnTheDefaultMessageWhenNothingResolvesTheCode() {
		assertEquals("Fallback text", service.getMessage("mdrtb.no.such.code.anywhere", null, "Fallback text"));
	}
	
	@Test
	public void getMessage_shouldHandleBlankCodes() {
		assertEquals("", service.getMessage(null, null, null));
		assertEquals("", service.getMessage("  ", null, null));
		assertEquals("Fallback text", service.getMessage(null, null, "Fallback text"));
	}
	
	@Test
	public void getMessage_shouldSubstituteArgumentsWhenTheyArePresent() {
		givenMessage(EN, "mdrtb.converted", "Converted on {0}");
		assertEquals("Converted on 4 Aug 2026", service.getMessage("mdrtb.converted", new Object[] { "4 Aug 2026" }));
	}
	
	@Test
	public void getMessage_shouldSubstituteArgumentsIntoTheDefaultMessageToo() {
		assertEquals("Converted on 4 Aug 2026",
		    service.getMessage("mdrtb.no.such.code.anywhere", new Object[] { "4 Aug 2026" }, "Converted on {0}"));
	}
	
	@Test
	public void getMessage_shouldReturnTheRawMessageWhenThePatternIsMalformed() {
		givenMessage(EN, "mdrtb.malformed", "Broken {0 pattern");
		assertEquals("Broken {0 pattern", service.getMessage("mdrtb.malformed", new Object[] { "x" }));
	}
	
	@Test
	public void getMessage_shouldServeRepeatedLookupsFromTheCache() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		assertEquals("Unknown", service.getMessage("mdrtb.unknown"));
		// change the row behind the service's back; the DAO does not touch the cache
		MessageProperty persisted = service.getMessageProperty(EN, "mdrtb.unknown");
		persisted.setMessage("Changed underneath");
		messagePropertyDao.saveMessageProperty(persisted);
		assertEquals("the cache should still be serving the old value", "Unknown", service.getMessage("mdrtb.unknown"));
		service.resetMessageCache();
		assertEquals("Changed underneath", service.getMessage("mdrtb.unknown"));
	}
	
	@Test
	public void saveMessageProperty_shouldResetTheCacheSoTheChangeIsVisibleImmediately() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		// reading through the service pulls the whole table into the Hibernate session
		assertEquals("Unknown", service.getMessage("mdrtb.unknown"));
		MessageProperty unknown = new MessageProperty(EN, "mdrtb.unknown", "Saved through the service");
		service.saveMessageProperty(unknown);
		assertEquals("Saved through the service", service.getMessage("mdrtb.unknown"));
		assertEquals("Saved through the service", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
	}
	
	@Test
	public void saveMessageProperty_shouldInsertWhenTheRowDoesNotExistYet() {
		service.saveMessageProperty(new MessageProperty(EN, "mdrtb.brandNewCode", "Brand new"));
		assertEquals("Brand new", service.getMessageProperty(EN, "mdrtb.brandNewCode").getMessage());
		assertEquals("Brand new", service.getMessage("mdrtb.brandNewCode"));
	}
	
	@Test
	public void saveMessageProperty_shouldUpdateARowThatIsNotInTheSession() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		Context.flushSession();
		Context.clearSession();
		service.saveMessageProperty(new MessageProperty(EN, "mdrtb.unknown", "Updated while detached"));
		assertEquals("Updated while detached", service.getMessageProperty(EN, "mdrtb.unknown").getMessage());
	}
	
	@Test
	public void getMessageProperty_shouldReadStraightFromTheDatabase() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		MessageProperty persisted = service.getMessageProperty(EN, "mdrtb.unknown");
		assertNotNull(persisted);
		assertEquals(EN, persisted.getLang());
		assertEquals("mdrtb.unknown", persisted.getCode());
		assertEquals("Unknown", persisted.getMessage());
	}
	
	@Test
	public void getMessageProperty_shouldReturnNullForAnUnknownPair() {
		assertNull(service.getMessageProperty(EN, "mdrtb.no.such.code.anywhere"));
		assertNull(service.getMessageProperty("zz", "mdrtb.unknown"));
		assertNull(service.getMessageProperty(null, "mdrtb.unknown"));
	}
	
	@Test
	public void getAllMessageProperties_shouldReturnEveryLanguage() {
		givenMessage(EN, "mdrtb.unknown", "Unknown");
		givenMessage("ru", "mdrtb.unknown", "Данных нет");
		List<MessageProperty> all = service.getAllMessageProperties();
		assertEquals(2, all.size());
	}
	
	@Test
	public void messageProperty_shouldBeIdentifiedByLangAndCodeAlone() {
		MessageProperty one = new MessageProperty(EN, "mdrtb.unknown", "Unknown");
		MessageProperty sameKey = new MessageProperty(EN, "mdrtb.unknown", "A different translation");
		MessageProperty otherLang = new MessageProperty("ru", "mdrtb.unknown", "Unknown");
		assertEquals(one, sameKey);
		assertEquals(one.hashCode(), sameKey.hashCode());
		assertNotEquals(one, otherLang);
	}
}
