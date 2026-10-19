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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

/**
 * Reads this module's {@code messages*.properties} bundles.
 * OpenMRS itself does the equivalent in {@code DefaultMessageSourceServiceImpl}, which loads
 * {@code messages.properties} through {@code OpenmrsClassLoader} at construction time. We use the
 * same class loader so that the bundles are found whether the module is running as a packaged omod,
 * as an exploded directory, or straight off {@code target/classes} in a unit test.
 */
public class MdrtbMessages {
	
	private static final Log log = LogFactory.getLog(MdrtbMessages.class);
	
	/** Language recorded for the unsuffixed {@code messages.properties} bundle. */
	public static final String DEFAULT_LANG = "en";
	
	static final String BUNDLE_PREFIX = "messages";
	
	static final String BUNDLE_SUFFIX = ".properties";
	
	/**
	 * Bundles we know ship with this module, matching the {@code <messages>} blocks in config.xml.
	 * {@link #discoverLanguages()} normally finds these (and any newly added bundle) by inspecting
	 * the classpath; this list is the fallback for the cases where that inspection cannot run, and
	 * it also guarantees a stable ordering.
	 */
	public static final List<String> KNOWN_LANGUAGES = Collections.unmodifiableList(Arrays.asList(DEFAULT_LANG, "ru",
	    "tj"));
	
	/** Matches messages.properties and messages_<lang>.properties, capturing the language. */
	private static final Pattern BUNDLE_PATTERN = Pattern.compile("^" + BUNDLE_PREFIX + "(?:_([A-Za-z0-9_]+))?"
	        + Pattern.quote(BUNDLE_SUFFIX) + "$");
	
	/**
	 * Supplies a single message from storage on a cache miss. Implemented by
	 * {@code MdrtbServiceImpl} over {@code MdrtbDao}; kept as an interface so that this class never
	 * touches Hibernate and stays unit testable without a database.
	 */
	public interface MessageStore {

		/**
		 * @return the message for this exact language and code, or null when it is not stored
		 */
		String find(String lang, String code);
	}

	/**
	 * Cache of individual messages, populated on demand
	 * Two deliberate differences from the concept cache. First it is a {@link ConcurrentHashMap}:
	 * entries are added by whichever request thread first asks for a code, and a plain HashMap
	 * mutated that way is a data race. Second, misses are cached too — see {@link #NOT_STORED} —
	 * because the JSPs resolve roughly 170 codes that belong to OpenMRS core rather than to this
	 * module, and without negative caching every one of them would hit the database on every page.
	 */
	private final ConcurrentHashMap<String, Map<String, String>> cache = new ConcurrentHashMap<>();

	/**
	 * Marker for "we asked the database for this and it is not there". ConcurrentHashMap forbids
	 * null values, and we need to tell "not looked up yet" apart from "looked up, does not exist".
	 * Compared by identity, so it can never collide with a real message.
	 */
	private static final String NOT_STORED = "mdrtb.message.notStored";

	private MessageStore store;

	/**
	 * Injected by {@code MdrtbServiceImpl}. Until it is set, {@link #resolve} finds nothing and the
	 * service falls back to OpenMRS's message sources.
	 */
	public void setStore(MessageStore store) {
		this.store = store;
	}

	/**
	 * Resolves a code for a locale, filling the cache as it goes.
	 * <p>
	 * Walks {@link #candidateLanguages(Locale)} and returns the first stored message. Every language
	 * tried is remembered, hit or miss, so a repeat lookup of the same code costs a couple of map
	 * reads and no query.
	 *
	 * @param code the message code
	 * @param locale the locale to resolve for; may be null, treated as English
	 * @param arguments MessageFormat arguments; formatting is applied only when this is non-empty,
	 *            which is what keeps apostrophes intact in unparameterised messages
	 * @return the resolved message, or null when this module does not define the code in any
	 *         language on the fallback chain
	 */
	public String resolve(String code, Locale locale, Object[] arguments) {
		if (code == null || code.trim().isEmpty()) {
			return null;
		}
		for (String lang : candidateLanguages(locale)) {
			String message = lookup(lang, code);
			if (message != null) {
				return format(message, arguments, locale);
			}
		}
		return null;
	}

	/**
	 * Reads one exact (lang, code) pair through the cache.
	 *
	 * @return the message, or null when it is not stored for that language
	 */
	String lookup(String lang, String code) {
		Map<String, String> messages = cache.get(lang);
		if (messages == null) {
			Map<String, String> created = new ConcurrentHashMap<>();
			Map<String, String> raced = cache.putIfAbsent(lang, created);
			messages = raced == null ? created : raced;
		}

		String cached = messages.get(code);
		if (cached != null) {
			// identity comparison: only our own sentinel instance means "known to be absent"
			return cached.equals(NOT_STORED) ? null : cached;
		}

		if (store == null) {
			return null;
		}
		String stored;
		try {
			stored = store.find(lang, code);
		}
		catch (Exception e) {
			// a failed read must not take the page down, and must not be cached as a miss
			log.error("Unable to read message " + code + " for language " + lang, e);
			return null;
		}
		messages.put(code, stored == null ? NOT_STORED : stored);
		return stored;
	}

	/**
	 * Reads the cache without falling through to the store. For tests and diagnostics.
	 *
	 * @return the cached message, or null when the pair has not been looked up yet or is cached as
	 *         absent
	 */
	String peek(String lang, String code) {
		Map<String, String> messages = cache.get(lang);
		String cached = messages == null ? null : messages.get(code);
		return Objects.equals(cached, NOT_STORED) ? null : cached;
	}

	/**
	 * @return true when this pair has been looked up and recorded as not present in storage
	 */
	boolean isCachedAsMissing(String lang, String code) {
		Map<String, String> messages = cache.get(lang);
		return messages != null && Objects.equals(messages.get(code), NOT_STORED);
	}

	/**
	 * Discards everything cached so far. Call after editing message_properties directly.
	 */
	public void resetCache() {
		cache.clear();
	}

	/**
	 * @return how many messages are currently cached, counting negative entries; for tests and
	 *         diagnostics
	 */
	public int getCachedCount() {
		int total = 0;
		for (Map<String, String> messages : cache.values()) {
			total += messages.size();
		}
		return total;
	}

	/**
	 * ISO 639 renamed three languages in 1989, but {@link Locale} still reports the pre-1989 codes
	 * for backwards compatibility: {@code new Locale("id").getLanguage()} returns {@code "in"}, not
	 * {@code "id"}. Bundle file names use the modern codes (messages_id_ID.properties), so both
	 * spellings have to be tried or Indonesian would silently fall through to English.
	 */
	private static final Map<String, String> MODERN_LANGUAGE_CODES;

	static {
		Map<String, String> modern = new HashMap<>();
		modern.put("in", "id"); // Indonesian
		modern.put("iw", "he"); // Hebrew
		modern.put("ji", "yi"); // Yiddish
		MODERN_LANGUAGE_CODES = Collections.unmodifiableMap(modern);
	}

	/**
	 * Builds the lookup chain for a locale, most specific first: the full locale, then the language
	 * alone, then English. Each level is tried with the modern ISO 639 language code before the
	 * legacy one that {@link Locale} actually stores.
	 *
	 * @return the languages to try, in order; never null, never empty, and always ending in
	 *         {@link #DEFAULT_LANG}
	 */
	public static List<String> candidateLanguages(Locale locale) {
		List<String> candidates = new ArrayList<>(5);
		if (locale != null) {
			String legacyLanguage = locale.getLanguage();
			String modernLanguage = modernLanguageCode(legacyLanguage);
			String full = locale.toString();

			if (!full.isEmpty()) {
				if (!modernLanguage.equals(legacyLanguage) && full.startsWith(legacyLanguage)) {
					// "in_ID" as Locale renders it becomes "id_ID" as the bundle names it
					addIfAbsent(candidates, modernLanguage + full.substring(legacyLanguage.length()));
				}
				addIfAbsent(candidates, full);
			}
			addIfAbsent(candidates, modernLanguage);
			addIfAbsent(candidates, legacyLanguage);
		}
		addIfAbsent(candidates, DEFAULT_LANG);
		return candidates;
	}

	/**
	 * @return the modern ISO 639 code for a language, or the input unchanged when it is not one of
	 *         the three renamed languages
	 */
	private static String modernLanguageCode(String language) {
		if (language == null || language.isEmpty()) {
			return "";
		}
		String modern = MODERN_LANGUAGE_CODES.get(language);
		return modern == null ? language : modern;
	}

	private static void addIfAbsent(List<String> candidates, String candidate) {
		if (candidate != null && !candidate.isEmpty() && !candidates.contains(candidate)) {
			candidates.add(candidate);
		}
	}

	/**
	 * Applies MessageFormat only when there is something to substitute. Doing it unconditionally
	 * would swallow the single quotes that appear throughout the bundles.
	 */
	public static String format(String message, Object[] arguments, Locale locale) {
		if (message == null || arguments == null || arguments.length == 0) {
			return message;
		}
		try {
			return new MessageFormat(message, locale == null ? Locale.ENGLISH : locale).format(arguments);
		}
		catch (IllegalArgumentException e) {
			// a malformed pattern should not take a page down
			return message;
		}
	}

	/**
	 * Loads every bundle this module ships.
	 * 
	 * @return language code to (code to message) map, sorted by language for predictable logging.
	 *         Languages whose bundle cannot be read are omitted rather than failing the whole load,
	 *         so that one malformed file cannot stop the module from starting.
	 */
	public Map<String, Map<String, String>> loadBundles() {
		Map<String, Map<String, String>> bundles = new TreeMap<>();
		for (String lang : discoverLanguages()) {
			Map<String, String> messages = loadBundle(lang);
			if (!messages.isEmpty()) {
				bundles.put(lang, messages);
			}
		}
		if (bundles.isEmpty()) {
			log.warn("No mdrtb message bundles could be read from the classpath; "
			        + "message_properties will not be seeded.");
		}
		return bundles;
	}
	
	/**
	 * Loads a single bundle.
	 * 
	 * @param lang language code, e.g. {@code en}, {@code ru}, {@code id_ID}
	 * @return code to message map, empty when the bundle is absent or unreadable
	 */
	public Map<String, String> loadBundle(String lang) {
		Map<String, String> messages = new LinkedHashMap<>();
		String resource = resourceNameFor(lang);
		InputStream stream = null;
		try {
			stream = openResource(resource);
			if (stream == null) {
				log.debug("No message bundle found on the classpath for " + resource);
				return messages;
			}
			OrderedProperties properties = new OrderedProperties();
			// OpenmrsUtil.loadProperties reads the stream as UTF-8 and closes it, which is what
			// core does for messages.properties. The bundles here are UTF-8, not ISO-8859-1.
			OpenmrsUtil.loadProperties(properties, stream);
			for (Map.Entry<String, String> entry : properties.inFileOrder().entrySet()) {
				String code = entry.getKey();
				String value = entry.getValue();
				if (code.length() > MessageProperty.MAX_CODE_LENGTH) {
					log.warn("Skipping message code longer than " + MessageProperty.MAX_CODE_LENGTH + " characters in "
					        + resource + ": " + code);
					continue;
				}
				messages.put(code, value == null ? "" : value);
			}
		}
		catch (Exception e) {
			log.error("Unable to read message bundle " + resource, e);
		}
		finally {
			closeQuietly(stream);
		}
		return messages;
	}

	/**
	 * {@link Properties} extends Hashtable, so iterating it loses the order the keys appeared in the
	 * file. That order matters: when two keys collide — exactly, or only in case once they reach a
	 * case-insensitive database collation — the rule is that the later definition wins, and "later"
	 * is only meaningful if the file order survives the load.
	 * <p>
	 * {@code Properties.load} calls {@code put} once per entry as it parses, so recording those
	 * calls in a LinkedHashMap captures file order. Re-putting an existing key keeps its original
	 * position and replaces the value, which is the last-one-wins behaviour Properties itself has
	 * for exact duplicate keys.
	 */
	private static class OrderedProperties extends Properties {

		private static final long serialVersionUID = 1L;

		private final Map<String, String> ordered = new LinkedHashMap<>();

		@Override
		public synchronized Object put(Object key, Object value) {
			if (key instanceof String && (value == null || value instanceof String)) {
				ordered.put((String) key, (String) value);
			}
			return super.put(key, value);
		}

		Map<String, String> inFileOrder() {
			return ordered;
		}
	}
	
	/**
	 * Works out which bundles are actually present. The classpath location of
	 * {@code messages.properties} is inspected and its siblings enumerated, which handles both a
	 * packaged jar and an exploded {@code target/classes} directory. Anything found is unioned with
	 * {@link #KNOWN_LANGUAGES} so that a failed inspection still seeds the shipped bundles.
	 * 
	 * @return language codes in a stable order, default language first
	 */
	Set<String> discoverLanguages() {
		Set<String> languages = new LinkedHashSet<>(KNOWN_LANGUAGES);
		try {
			URL url = resolveResource(resourceNameFor(DEFAULT_LANG));
			if (url != null) {
				languages.addAll("jar".equals(url.getProtocol()) ? languagesInJar(url) : languagesInDirectory(url));
			}
		}
		catch (Exception e) {
			log.warn("Could not enumerate message bundles on the classpath; falling back to the known list", e);
		}
		return languages;
	}
	
	private Set<String> languagesInDirectory(URL url) throws Exception {
		Set<String> languages = new LinkedHashSet<>();
		File bundle = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
		File directory = bundle.getParentFile();
		if (directory == null || !directory.isDirectory()) {
			return languages;
		}
		String[] names = directory.list();
		if (names == null) {
			return languages;
		}
		for (String name : names) {
			addIfBundle(languages, name);
		}
		return languages;
	}
	
	private Set<String> languagesInJar(URL url) throws IOException {
		Set<String> languages = new LinkedHashSet<>();
		URLConnection connection = url.openConnection();
		if (!(connection instanceof java.net.JarURLConnection)) {
			return languages;
		}
		JarFile jar = ((java.net.JarURLConnection) connection).getJarFile();
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			// only consider bundles at the root of the jar, which is where they are packaged
			if (name.indexOf('/') < 0) {
				addIfBundle(languages, name);
			}
		}
		return languages;
	}
	
	private void addIfBundle(Set<String> languages, String fileName) {
		Matcher matcher = BUNDLE_PATTERN.matcher(fileName);
		if (matcher.matches()) {
			String lang = matcher.group(1);
			languages.add(lang == null ? DEFAULT_LANG : lang);
		}
	}
	
	/**
	 * @param lang language code
	 * @return the classpath resource name for that language's bundle
	 */
	static String resourceNameFor(String lang) {
		if (lang == null || lang.isEmpty() || DEFAULT_LANG.equals(lang)) {
			return BUNDLE_PREFIX + BUNDLE_SUFFIX;
		}
		return BUNDLE_PREFIX + "_" + lang + BUNDLE_SUFFIX;
	}
	
	/**
	 * Opens this module's copy of a bundle.
	 *
	 * @see #resolveResource(String)
	 */
	InputStream openResource(String name) {
		URL url = resolveResource(name);
		if (url == null) {
			return null;
		}
		try {
			return url.openStream();
		}
		catch (IOException e) {
			log.error("Unable to open message bundle " + url, e);
			return null;
		}
	}

	/**
	 * Locates <em>this module's</em> copy of a bundle, which is emphatically not the same thing as
	 * the first {@code messages.properties} on the classpath.
	 * <p>
	 * OpenMRS core ships its own {@code messages.properties} (and {@code messages_ru}, {@code _fr},
	 * and a dozen more) at the root of openmrs-api.jar — that is the file
	 * {@code DefaultMessageSourceServiceImpl} loads. Because class loading delegates to the parent
	 * first, a plain {@code getResource("messages.properties")} at runtime returns core's bundle of
	 * ~3000 UI strings, not ours. Seeding from that would fill message_properties with OpenMRS's own
	 * labels and none of the module's.
	 * <p>
	 * So instead of trusting resolution order, every candidate is enumerated and the one that lives
	 * in the same jar (or the same {@code target/classes} directory) as this class is selected. That
	 * is unambiguous whether the module runs as a packaged omod, as an expanded directory, or off
	 * {@code target/classes} in a unit test — and it does not depend on surefire happening to order
	 * the classpath the opposite way round from production.
	 * <p>
	 * Package-private so tests can override it.
	 *
	 * @param name bundle file name, e.g. {@code messages_ru.properties}
	 * @return the URL of this module's copy, or null when the module does not ship that bundle
	 */
	URL resolveResource(String name) {
		String ourCodeSource = codeSourceLocation();
		List<URL> candidates = allResources(name);

		if (ourCodeSource != null) {
			for (URL candidate : candidates) {
				if (candidate.toString().contains(ourCodeSource)) {
					return candidate;
				}
			}
		}

		if (candidates.isEmpty()) {
			return null;
		}
		// Nothing matched our own code source. Refuse rather than guess: OpenMRS core and a dozen
		// other modules ship a messages.properties at the root of their jars, so "the first one on
		// the classpath" is very likely somebody else's. Seeding nothing is recoverable; seeding
		// another module's UI strings into message_properties is not.
		log.error("Could not identify this module's copy of " + name + " among " + candidates.size()
		        + " classpath candidates " + candidates + ". Refusing to seed from an unidentified bundle.");
		return null;
	}

	/**
	 * @return every classpath entry with the given name, in resolution order, never null
	 */
	private List<URL> allResources(String name) {
		List<URL> urls = new ArrayList<>();
		for (ClassLoader loader : candidateClassLoaders()) {
			if (loader == null) {
				continue;
			}
			try {
				Enumeration<URL> found = loader.getResources(name);
				while (found.hasMoreElements()) {
					URL url = found.nextElement();
					if (!urls.contains(url)) {
						urls.add(url);
					}
				}
			}
			catch (Exception e) {
				log.debug("Class loader " + loader + " could not enumerate " + name, e);
			}
		}
		return urls;
	}

	/**
	 * OpenMRS's class loader can see every module's jars, so it is tried first; this class's own
	 * loader is the fallback for a plain unit test where OpenmrsClassLoader is not initialised.
	 */
	private List<ClassLoader> candidateClassLoaders() {
		List<ClassLoader> loaders = new ArrayList<>(2);
		try {
			loaders.add(OpenmrsClassLoader.getInstance());
		}
		catch (Exception e) {
			log.debug("OpenmrsClassLoader unavailable; using the local class loader only", e);
		}
		loaders.add(MdrtbMessages.class.getClassLoader());
		return loaders;
	}

	/**
	 * @return the jar or directory this class was loaded from, as a string suitable for matching
	 *         against a resource URL, or null when it cannot be determined
	 */
	private String codeSourceLocation() {
		try {
			ProtectionDomain domain = MdrtbMessages.class.getProtectionDomain();
			CodeSource source = domain == null ? null : domain.getCodeSource();
			URL location = source == null ? null : source.getLocation();
			return location == null ? null : location.toString();
		}
		catch (Exception e) {
			log.debug("Could not determine the code source of " + MdrtbMessages.class, e);
			return null;
		}
	}
	
	private static void closeQuietly(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (IOException ignored) {
				// nothing useful to do here
			}
		}
	}
}
