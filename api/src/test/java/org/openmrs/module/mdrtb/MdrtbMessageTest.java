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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link MdrtbMessages}. These deliberately avoid an OpenMRS context: the
 * loader is the one piece of the message pipeline that only touches the classpath, and keeping it
 * testable in isolation is why it is a separate class from {@link MdrtbActivator}.
 * <p>
 * The subclass below pins resource resolution to the plain class loader so the tests do not depend
 * on {@code OpenmrsClassLoader} being initialised. The production path through
 * {@code OpenmrsClassLoader} is exercised by {@link MdrtbMessagePropertyTest}, which runs inside a
 * real context.
 */
public class MdrtbMessageTest {
	
	/** Resolves resources with the plain class loader only. */
	private static class ClasspathBundleLoader extends MdrtbMessages {
		
		@Override
		InputStream openResource(String name) {
			return MdrtbMessageTest.class.getClassLoader().getResourceAsStream(name);
		}
		
		@Override
		URL resolveResource(String name) {
			return MdrtbMessageTest.class.getClassLoader().getResource(name);
		}
	}
	
	private MdrtbMessages loader;
	
	@Before
	public void setup() {
		loader = new ClasspathBundleLoader();
	}
	
	@Test
	public void resourceNameFor_shouldMapDefaultLanguageToTheUnsuffixedBundle() {
		assertEquals("messages.properties", MdrtbMessages.resourceNameFor("en"));
		assertEquals("messages.properties", MdrtbMessages.resourceNameFor(null));
		assertEquals("messages.properties", MdrtbMessages.resourceNameFor(""));
	}
	
	@Test
	public void resourceNameFor_shouldSuffixEveryOtherLanguage() {
		assertEquals("messages_ru.properties", MdrtbMessages.resourceNameFor("ru"));
		assertEquals("messages_tj.properties", MdrtbMessages.resourceNameFor("tj"));
		assertEquals("messages_id_ID.properties", MdrtbMessages.resourceNameFor("id_ID"));
	}
	
	@Test
	public void loadBundle_shouldReadTheEnglishBundle() {
		Map<String, String> messages = loader.loadBundle("en");
		
		assertFalse(messages.isEmpty());
		assertEquals("Unknown", messages.get("mdrtb.unknown"));
		assertEquals("Yes", messages.get("mdrtb.yes"));
		// a sanity floor rather than an exact count, so that adding a key does not break the build
		assertTrue("expected the English bundle to hold well over a thousand keys, found " + messages.size(),
		    messages.size() > 1400);
	}
	
	@Test
	public void loadBundle_shouldReadNonLatinBundlesAsUtf8() {
		// this is the assertion that catches the bundles being read as ISO-8859-1
		assertEquals("Данных нет", loader.loadBundle("ru").get("mdrtb.unknown"));
		assertEquals("Номаълум", loader.loadBundle("tj").get("mdrtb.unknown"));
	}
	
	@Test
	public void loadBundle_shouldPreserveMessageFormatPlaceholdersAndApostrophes() {
		Map<String, String> messages = loader.loadBundle("en");
		
		assertEquals("Converted on {0}", messages.get("mdrtb.converted"));
		assertEquals("Please specify this patient's date of death.", messages.get("mdrtb.dateOfDeath.errors.required"));
	}
	
	@Test
	public void loadBundle_shouldReturnEmptyForAnUnknownLanguage() {
		assertTrue(loader.loadBundle("zz_ZZ").isEmpty());
	}
	
	@Test
	public void loadBundle_shouldSkipCodesLongerThanTheColumnAllows() {
		final StringBuilder longCode = new StringBuilder();
		for (int i = 0; i < MessageProperty.MAX_CODE_LENGTH + 1; i++) {
			longCode.append('x');
		}
		final String contents = "mdrtb.ok=fine\n" + longCode + "=too long\n";
		
		MdrtbMessages stubbed = new MdrtbMessages() {
			
			@Override
			InputStream openResource(String name) {
				return new ByteArrayInputStream(contents.getBytes(Charset.forName("UTF-8")));
			}
		};
		Map<String, String> messages = stubbed.loadBundle("en");
		
		assertEquals(1, messages.size());
		assertEquals("fine", messages.get("mdrtb.ok"));
		assertNull(messages.get(longCode.toString()));
	}
	
	@Test
	public void loadBundle_shouldReturnEmptyRatherThanThrowWhenTheResourceIsMissing() {
		MdrtbMessages stubbed = new MdrtbMessages() {
			
			@Override
			InputStream openResource(String name) {
				return null;
			}
		};
		
		assertNotNull(stubbed.loadBundle("en"));
		assertTrue(stubbed.loadBundle("en").isEmpty());
	}
	
	@Test
	public void discoverLanguages_shouldFindEveryBundleShippedWithTheModule() {
		Set<String> languages = loader.discoverLanguages();
		
		assertTrue(languages.containsAll(MdrtbMessages.KNOWN_LANGUAGES));
		assertTrue(languages.contains("en"));
		assertTrue(languages.contains("ru"));
		assertTrue(languages.contains("tj"));

	}
	
	@Test
	public void discoverLanguages_shouldFallBackToTheKnownListWhenTheClasspathCannotBeInspected() {
		MdrtbMessages stubbed = new MdrtbMessages() {
			
			@Override
			URL resolveResource(String name) {
				return null;
			}
		};
		
		assertEquals(MdrtbMessages.KNOWN_LANGUAGES.size(), stubbed.discoverLanguages().size());
		assertTrue(stubbed.discoverLanguages().containsAll(MdrtbMessages.KNOWN_LANGUAGES));
	}
	
	@Test
	public void loadBundles_shouldReturnOneEntryPerShippedLanguage() {
		Map<String, Map<String, String>> bundles = loader.loadBundles();
		
		assertEquals(MdrtbMessages.KNOWN_LANGUAGES.size(), bundles.size());
		for (String lang : MdrtbMessages.KNOWN_LANGUAGES) {
			assertNotNull("no bundle loaded for " + lang, bundles.get(lang));
			assertFalse("empty bundle loaded for " + lang, bundles.get(lang).isEmpty());
		}
		assertEquals("Unknown", bundles.get("en").get("mdrtb.unknown"));
		assertEquals("Данных нет", bundles.get("ru").get("mdrtb.unknown"));
		assertEquals("Номаълум", bundles.get("tj").get("mdrtb.unknown"));
	}
	
	@Test
	public void loadBundles_shouldOmitLanguagesWhoseBundleIsEmpty() {
		MdrtbMessages stubbed = new MdrtbMessages() {
			
			@Override
			public Map<String, String> loadBundle(String lang) {
				return "en".equals(lang) ? super.loadBundle(lang) : new java.util.HashMap<String, String>();
			}
			
			@Override
			InputStream openResource(String name) {
				return MdrtbMessageTest.class.getClassLoader().getResourceAsStream(name);
			}
			
			@Override
			URL resolveResource(String name) {
				return MdrtbMessageTest.class.getClassLoader().getResource(name);
			}
		};
		
		Map<String, Map<String, String>> bundles = stubbed.loadBundles();

		assertEquals(1, bundles.size());
		assertTrue(bundles.containsKey("en"));
	}

	/*********************/
	/** CACHE           **/
	/*********************/

	/** Stands in for message_properties, and counts how often it is asked. */
	private static class CountingStore implements MdrtbMessages.MessageStore {

		private final Map<String, String> rows = new java.util.HashMap<>();

		private int queries = 0;

		void put(String lang, String code, String message) {
			rows.put(lang + '|' + code, message);
		}

		@Override
		public String find(String lang, String code) {
			queries++;
			return rows.get(lang + '|' + code);
		}
	}

	private MdrtbMessages cachingLoader(CountingStore store) {
		MdrtbMessages messages = new ClasspathBundleLoader();
		messages.setStore(store);
		return messages;
	}

	@Test
	public void resolve_shouldReadThroughToTheStoreOnce() {
		CountingStore store = new CountingStore();
		store.put("en", "mdrtb.unknown", "Unknown");
		MdrtbMessages messages = cachingLoader(store);

		assertEquals("Unknown", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));
		assertEquals("Unknown", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));
		assertEquals("Unknown", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));

		assertEquals("the store should have been consulted exactly once", 1, store.queries);
	}

	@Test
	public void resolve_shouldCacheOneCodeAtATimeRatherThanTheWholeTable() {
		CountingStore store = new CountingStore();
		store.put("en", "mdrtb.unknown", "Unknown");
		store.put("en", "mdrtb.yes", "Yes");
		MdrtbMessages messages = cachingLoader(store);

		messages.resolve("mdrtb.unknown", Locale.ENGLISH, null);

		assertEquals(1, messages.getCachedCount());
		assertEquals("Unknown", messages.peek("en", "mdrtb.unknown"));
		assertNull("an unrequested code must not be pulled in", messages.peek("en", "mdrtb.yes"));
	}

	@Test
	public void resolve_shouldCacheMissesSoUnknownCodesAreNotQueriedTwice() {
		// the JSPs resolve about 170 codes that belong to OpenMRS core rather than to this module;
		// without negative caching each one would hit the database on every page render
		CountingStore store = new CountingStore();
		MdrtbMessages messages = cachingLoader(store);

		assertNull(messages.resolve("Person.gender.male", Locale.ENGLISH, null));
		int afterFirst = store.queries;
		assertNull(messages.resolve("Person.gender.male", Locale.ENGLISH, null));

		assertTrue(afterFirst > 0);
		assertEquals("a repeat lookup must not query again", afterFirst, store.queries);
		assertTrue(messages.isCachedAsMissing("en", "Person.gender.male"));
	}

	@Test
	public void resolve_shouldWalkTheLocaleChainAndCacheEveryLanguageItTried() {
		CountingStore store = new CountingStore();
		store.put("en", "mdrtb.unknown", "Unknown");
		MdrtbMessages messages = cachingLoader(store);

		// ru has no row, so it falls through to en; both outcomes are remembered
		assertEquals("Unknown", messages.resolve("mdrtb.unknown", new Locale("ru"), null));
		int afterFirst = store.queries;
		assertEquals("Unknown", messages.resolve("mdrtb.unknown", new Locale("ru"), null));

		assertEquals(afterFirst, store.queries);
		assertTrue(messages.isCachedAsMissing("ru", "mdrtb.unknown"));
		assertEquals("Unknown", messages.peek("en", "mdrtb.unknown"));
	}

	@Test
	public void resolve_shouldPreferTheMoreSpecificLanguage() {
		CountingStore store = new CountingStore();
		store.put("en", "mdrtb.unknown", "Unknown");
		store.put("ru", "mdrtb.unknown", "Данных нет");
		MdrtbMessages messages = cachingLoader(store);

		assertEquals("Данных нет", messages.resolve("mdrtb.unknown", new Locale("ru"), null));
		assertEquals("Unknown", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));
	}

	@Test
	public void resetCache_shouldSendTheNextLookupBackToTheStore() {
		CountingStore store = new CountingStore();
		store.put("en", "mdrtb.unknown", "Unknown");
		MdrtbMessages messages = cachingLoader(store);
		messages.resolve("mdrtb.unknown", Locale.ENGLISH, null);

		store.put("en", "mdrtb.unknown", "Corrected");
		assertEquals("still cached", "Unknown", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));

		messages.resetCache();

		assertEquals(0, messages.getCachedCount());
		assertEquals("Corrected", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));
	}

	@Test
	public void resolve_shouldNotCacheAMissWhenTheStoreFailed() {
		// a transient database error must not be remembered as "this code does not exist"
		final boolean[] fail = { true };
		MdrtbMessages messages = new ClasspathBundleLoader();
		messages.setStore(new MdrtbMessages.MessageStore() {

			@Override
			public String find(String lang, String code) {
				if (fail[0]) {
					throw new IllegalStateException("database is down");
				}
				return "en".equals(lang) ? "Unknown" : null;
			}
		});

		assertNull(messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));
		assertEquals("a failed read must not be cached", 0, messages.getCachedCount());

		fail[0] = false;
		assertEquals("Unknown", messages.resolve("mdrtb.unknown", Locale.ENGLISH, null));
	}

	@Test
	public void resolve_shouldReturnNullWhenNoStoreHasBeenWiredUp() {
		assertNull(new ClasspathBundleLoader().resolve("mdrtb.unknown", Locale.ENGLISH, null));
	}

	@Test
	public void resolve_shouldIgnoreBlankCodes() {
		CountingStore store = new CountingStore();
		MdrtbMessages messages = cachingLoader(store);

		assertNull(messages.resolve(null, Locale.ENGLISH, null));
		assertNull(messages.resolve("   ", Locale.ENGLISH, null));
		assertEquals(0, store.queries);
	}

	/*********************/
	/** LOCALE CHAIN    **/
	/*********************/

	@Test
	public void candidateLanguages_shouldGoFromMostToLeastSpecificAndEndInEnglish() {
		assertEquals(Arrays.asList("fr_FR", "fr", "en"), MdrtbMessages.candidateLanguages(new Locale("fr", "FR")));
		assertEquals(Arrays.asList("ru", "en"), MdrtbMessages.candidateLanguages(new Locale("ru")));
		assertEquals(Arrays.asList("en_GB", "en"), MdrtbMessages.candidateLanguages(Locale.UK));
		assertEquals(Collections.singletonList("en"), MdrtbMessages.candidateLanguages(Locale.ENGLISH));
		assertEquals(Collections.singletonList("en"), MdrtbMessages.candidateLanguages(null));
		assertEquals(Collections.singletonList("en"), MdrtbMessages.candidateLanguages(new Locale("")));
	}

	/*********************/
	/** MESSAGE FORMAT  **/
	/*********************/

	@Test
	public void format_shouldSubstituteArgumentsWhenTheyArePresent() {
		assertEquals("Converted on 4 Aug 2026",
		    MdrtbMessages.format("Converted on {0}", new Object[] { "4 Aug 2026" }, Locale.ENGLISH));
	}

	@Test
	public void format_shouldLeaveApostrophesAloneWhenThereAreNoArguments() {
		// MessageFormat eats single quotes, so it must not run on unparameterised messages
		String withApostrophe = "Please specify this patient's date of death.";

		assertEquals(withApostrophe, MdrtbMessages.format(withApostrophe, null, Locale.ENGLISH));
		assertEquals(withApostrophe, MdrtbMessages.format(withApostrophe, new Object[0], Locale.ENGLISH));
	}

	@Test
	public void format_shouldReturnTheRawMessageWhenThePatternIsMalformed() {
		assertEquals("Broken {0 pattern",
		    MdrtbMessages.format("Broken {0 pattern", new Object[] { "x" }, Locale.ENGLISH));
	}
}
