package org.openmrs.module.mdrtb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Manual REST smoke test for the MDR-TB report/data endpoints, run by hand against a live, locally
 * running OpenMRS. This is NOT a JUnit test: it has a main() method, makes real HTTP calls, and is
 * never run by Maven/Surefire (it lives in src/main/java on purpose so it can't break the build -
 * the trade-off is that it is compiled into the shipped .omod). Before running, the target server
 * must have: the MDR-TB module deployed; the admin/Admin1234 login valid; Gson on the classpath
 * (already an omod dependency); and the hard-coded location/record UUIDs present with report data
 * for the current year and, for the quarterly checks, quarter 1 (monthly checks use the current
 * month). The exact endpoint, parameters and assertions of each check are documented on the
 * individual test method; server, credentials and UUIDs are the constants below. To run: execute
 * main() from your IDE (arguments are ignored), or from the command line put the omod classes plus
 * the Gson jar on the classpath, e.g. java -cp
 * "omod/target/classes:<m2>/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar"
 * org.openmrs.module.mdrtb.IntegrationTest (On Windows use ';' as the classpath separator.) Note:
 * "mvn exec:java" is not wired up in any pom. Behaviour: run() calls the 21 checks in order and is
 * fail-fast - the first check to fail throws an AssertionError (or IOException on a connection
 * problem) and stops the run; the rest do not execute. Nothing is printed on success (there is no
 * "all passed" message).
 */
public class IntegrationTest {
	
	/** Root of the OpenMRS REST API to hit. Change host/port here to target a different server. */
	private static final String BASE_URL = "http://localhost:8080/openmrs/ws/rest/v1";
	
	/** OpenMRS username for HTTP Basic auth. */
	private static final String USERNAME = "admin";
	
	/** OpenMRS password for HTTP Basic auth (plain text - externalise for real use). */
	private static final String PASSWORD = "Admin1234";
	
	/** Canonical 8-4-4-4-12 UUID shape used by {@link #assertUuid(JsonObject, String)}. */
	private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	
	/**
	 * Location UUID used by the reachability checks and several quarterly report checks. Must exist
	 * in the target DB.
	 */
	private static final String PRIMARY_LOCATION_UUID = "8cac8e81-6baf-4f6d-88e5-92dd794fded3";
	
	/**
	 * Location UUID used by the form89 / TB03 / TB07 / TB08 quarterly checks. Must exist in the
	 * target DB.
	 */
	private static final String SECONDARY_LOCATION_UUID = "82be00a0-894b-42aa-812f-428f23e9fd7a";
	
	/** Location UUID used by the TB08u quarterly check. Must exist in the target DB. */
	private static final String TB08U_LOCATION_UUID = "7a27b5db-f952-4396-989f-2623b2ea735d";
	
	/**
	 * Program entry point. Takes no arguments; builds an instance and runs every check. Exits
	 * normally (silently) if all pass, or terminates with an AssertionError / IOException stack
	 * trace on the first failure.
	 * 
	 * @param args ignored
	 * @throws IOException if the server cannot be reached
	 */
	public static void main(String[] args) throws IOException {
		new IntegrationTest().run();
	}
	
	/**
	 * Runs all 21 checks in order: first the HTTP-200 reachability checks, then the JSON content
	 * checks. Fail-fast - the first check that throws aborts the rest.
	 * 
	 * @throws IOException if a connection fails
	 */
	public void run() throws IOException {
		testForm8ReportEndpoint();
		testForm89ReportEndpoint();
		testTB03ReportEndpoint();
		testTB03uReportEndpoint();
		testTB07ReportEndpoint();
		testTB07uReportEndpoint();
		testTB08ReportEndpoint();
		testTB08uReportEndpoint();
		testTB03MissingReportEndpoint();
		testTB03uMissingReportEndpoint();
		testPatientListEndpoint();
		testForm8Report();
		testForm89Report();
		testTB03Report();
		testTB03uReport();
		testTB07Report();
		testTB07uReport();
		testTB08Report();
		testTB08uReport();
		testTransferOutReport();
		testRegimenReport();
	}
	
	/**
	 * Reachability: GET /mdrtb/form8report with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testForm8ReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/form8report", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/form89report with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testForm89ReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/form89report", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb03report with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB03ReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb03report", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb03ureport with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB03uReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb03ureport", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb07report with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB07ReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb07report", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb07ureport with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB07uReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb07ureport", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb08report with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB08ReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb08report", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb08ureport with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB08uReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb08ureport", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb03missingreport with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB03MissingReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb03missingreport", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/tb03umissingreport with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testTB03uMissingReportEndpoint() throws IOException {
		assertStatusOk("/mdrtb/tb03umissingreport", monthlyReportParameters());
	}
	
	/**
	 * Reachability: GET /mdrtb/patientlist with monthly params (PRIMARY location, current
	 * year+month); asserts HTTP 200.
	 */
	public void testPatientListEndpoint() throws IOException {
		assertStatusOk("/mdrtb/patientlist", monthlyReportParameters());
	}
	
	/**
	 * Content check: GET /mdrtb/form8report (PRIMARY location, current year, quarter 1). Asserts
	 * results[0].simpleForm8Table1Data.uuid through simpleForm8Table5aData.uuid are UUIDs, and
	 * results[0].simpleTB08Data.newAllDetected is greater than 0.
	 */
	public void testForm8Report() throws IOException {
		JsonObject form8 = getJson("/mdrtb/form8report", quarterlyReportParameters(PRIMARY_LOCATION_UUID));
		assertUuid(form8, "results[0].simpleForm8Table1Data.uuid");
		assertUuid(form8, "results[0].simpleForm8Table2Data.uuid");
		assertUuid(form8, "results[0].simpleForm8Table3Data.uuid");
		assertUuid(form8, "results[0].simpleForm8Table4Data.uuid");
		assertUuid(form8, "results[0].simpleForm8Table5aData.uuid");
		assertGreaterThan(form8, "results[0].simpleTB08Data.newAllDetected");
	}
	
	/**
	 * Content check: GET /mdrtb/form89report (SECONDARY location, current year, quarter 1). Asserts
	 * results[0].ageAtTB03Registration is present (not null).
	 */
	public void testForm89Report() throws IOException {
		JsonObject form89 = getJson("/mdrtb/form89report", quarterlyReportParameters(SECONDARY_LOCATION_UUID));
		assertPresent(form89, "results[0].ageAtTB03Registration");
	}
	
	/**
	 * Content check: GET /mdrtb/tb03report (SECONDARY location, current year, quarter 1). Asserts
	 * results[0].patientUuid is a UUID and results[0].identifier is present.
	 */
	public void testTB03Report() throws IOException {
		JsonObject tb03 = getJson("/mdrtb/tb03report", quarterlyReportParameters(SECONDARY_LOCATION_UUID));
		assertUuid(tb03, "results[0].patientUuid");
		assertPresent(tb03, "results[0].identifier");
	}
	
	/**
	 * Content check: GET /mdrtb/tb03ureport (PRIMARY location, current year, quarter 1). Asserts
	 * results[0].dotsYear is greater than 0 and results[0].identifierMDR is present.
	 */
	public void testTB03uReport() throws IOException {
		JsonObject tb03u = getJson("/mdrtb/tb03ureport", quarterlyReportParameters(PRIMARY_LOCATION_UUID));
		assertGreaterThan(tb03u, "results[0].dotsYear");
		assertPresent(tb03u, "results[0].identifierMDR");
	}
	
	/**
	 * Content check: GET /mdrtb/tb07report (SECONDARY location, current year, quarter 1). Asserts
	 * results[0].totalAll is at least 0 (i.e. the field exists and is numeric).
	 */
	public void testTB07Report() throws IOException {
		JsonObject tb07 = getJson("/mdrtb/tb07report", quarterlyReportParameters(SECONDARY_LOCATION_UUID));
		assertGreaterThanOrEqualTo(tb07, "results[0].totalAll");
	}
	
	/**
	 * Content check: GET /mdrtb/tb07ureport (PRIMARY location, current year, quarter 1). Asserts
	 * results[0].totalDetections is at least 0.
	 */
	public void testTB07uReport() throws IOException {
		JsonObject tb07u = getJson("/mdrtb/tb07ureport", quarterlyReportParameters(PRIMARY_LOCATION_UUID));
		assertGreaterThanOrEqualTo(tb07u, "results[0].totalDetections");
	}
	
	/**
	 * Content check: GET /mdrtb/tb08report (SECONDARY location, current year, quarter 1). Asserts
	 * results[0].newAllDetected is at least 0.
	 */
	public void testTB08Report() throws IOException {
		JsonObject tb08 = getJson("/mdrtb/tb08report", quarterlyReportParameters(SECONDARY_LOCATION_UUID));
		assertGreaterThanOrEqualTo(tb08, "results[0].newAllDetected");
	}
	
	/**
	 * Content check: GET /mdrtb/tb08ureport (TB08U location, current year, quarter 1). Asserts
	 * results[0].newTotalShort is at least 0.
	 */
	public void testTB08uReport() throws IOException {
		JsonObject tb08u = getJson("/mdrtb/tb08ureport", quarterlyReportParameters(TB08U_LOCATION_UUID));
		assertGreaterThanOrEqualTo(tb08u, "results[0].newTotalShort");
	}
	
	/**
	 * Content check: GET /mdrtb/transferout?q=bb167f6b-baf4-437a-93c1-376231fa78b4 (transfer-out
	 * search by the hard-coded query UUID, which must exist in the target DB). Asserts
	 * results[0].uuid is a UUID and results[0].encounter is present.
	 */
	public void testTransferOutReport() throws IOException {
		JsonObject transferOut = getJson("/mdrtb/transferout", parameters("q", "bb167f6b-baf4-437a-93c1-376231fa78b4"));
		assertUuid(transferOut, "results[0].uuid");
		assertPresent(transferOut, "results[0].encounter");
	}
	
	/**
	 * Content check: GET /mdrtb/transferout/777130b4-d711-49c1-be1e-e87fbecf17d6 (fetch one
	 * transfer-out resource by UUID, which must exist in the target DB). Asserts the top-level uuid
	 * is a UUID and encounter is present. NOTE: this method is named "Regimen" but actually hits
	 * the transfer-out resource - the name does not match the endpoint.
	 */
	public void testRegimenReport() throws IOException {
		JsonObject regimen = getJson("/mdrtb/transferout/777130b4-d711-49c1-be1e-e87fbecf17d6", new LinkedHashMap<>());
		assertUuid(regimen, "uuid");
		assertPresent(regimen, "encounter");
	}
	
	/**
	 * Query parameters for a monthly report: primary location, current year, current month
	 * (location, year, month).
	 */
	private Map<String, Object> monthlyReportParameters() {
		return reportParameters(PRIMARY_LOCATION_UUID, LocalDate.now().getYear(), "month", LocalDate.now().getMonthValue());
	}
	
	/**
	 * Query parameters for a quarterly report: given location, current year, and quarter fixed to 1
	 * (location, year, quarter).
	 * 
	 * @param location location UUID to filter by
	 */
	private Map<String, Object> quarterlyReportParameters(String location) {
		return reportParameters(location, LocalDate.now().getYear(), "quarter", 1);
	}
	
	/**
	 * Builds a report parameter map of location, year, and one period parameter.
	 * 
	 * @param location location UUID
	 * @param year report year
	 * @param periodName the period parameter name, e.g. "month" or "quarter"
	 * @param period the period value
	 */
	private Map<String, Object> reportParameters(String location, int year, String periodName, int period) {
		Map<String, Object> parameters = parameters("location", location);
		parameters.put("year", year);
		parameters.put(periodName, period);
		return parameters;
	}
	
	/** Creates a new insertion-ordered parameter map seeded with a single name/value pair. */
	private Map<String, Object> parameters(String name, Object value) {
		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(name, value);
		return parameters;
	}
	
	/**
	 * Reachability check: performs the GET and relies on {@link #getJson} throwing if the response
	 * is not HTTP 200 with a JSON object body. The parsed JSON is discarded.
	 */
	private void assertStatusOk(String endpoint, Map<String, Object> parameters) throws IOException {
		getJson(endpoint, parameters);
	}
	
	/**
	 * Performs an authenticated GET against BASE_URL + endpoint with the given query parameters and
	 * returns the parsed JSON object.
	 * 
	 * @throws AssertionError if the status is not HTTP 200 (message includes status and body) or
	 *             the body is not a JSON object
	 * @throws IOException if the connection itself fails
	 */
	private JsonObject getJson(String endpoint, Map<String, Object> parameters) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(buildUrl(endpoint, parameters)).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization",
		    "Basic " + Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8)));
		connection.setRequestProperty("Accept", "application/json");
		
		int status = connection.getResponseCode();
		String response = readResponse(status < HttpURLConnection.HTTP_BAD_REQUEST ? connection.getInputStream()
		        : connection.getErrorStream());
		if (status != HttpURLConnection.HTTP_OK) {
			throw new AssertionError(endpoint + " returned HTTP " + status + ": " + response);
		}
		JsonElement json = new JsonParser().parse(response);
		if (!json.isJsonObject()) {
			throw new AssertionError(endpoint + " did not return a JSON object");
		}
		return json.getAsJsonObject();
	}
	
	/**
	 * Assembles the full request URL: BASE_URL + endpoint plus a URL-encoded query string built
	 * from parameters (insertion order preserved).
	 */
	private String buildUrl(String endpoint, Map<String, Object> parameters) {
		StringBuilder url = new StringBuilder(BASE_URL).append(endpoint);
		if (!parameters.isEmpty()) {
			url.append('?');
			for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
				if (url.charAt(url.length() - 1) != '?') {
					url.append('&');
				}
				url.append(URLEncoder.encode(parameter.getKey(), StandardCharsets.UTF_8));
				url.append('=').append(URLEncoder.encode(String.valueOf(parameter.getValue()), StandardCharsets.UTF_8));
			}
		}
		return url.toString();
	}
	
	/**
	 * Reads an entire HTTP response body into a single string (UTF-8); returns "" for a null
	 * stream.
	 */
	private String readResponse(InputStream stream) throws IOException {
		if (stream == null) {
			return "";
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			return response.toString();
		}
	}
	
	/** Asserts the string at {@code path} matches {@link #UUID_REGEX}. */
	private void assertUuid(JsonObject json, String path) {
		String value = getValue(json, path).getAsString();
		if (!value.matches(UUID_REGEX)) {
			throw new AssertionError(path + " is not a UUID: " + value);
		}
	}
	
	/** Asserts the element at {@code path} exists and is not JSON null. */
	private void assertPresent(JsonObject json, String path) {
		JsonElement value = getValue(json, path);
		if (value.isJsonNull()) {
			throw new AssertionError(path + " must not be null");
		}
	}
	
	/** Asserts the integer at {@code path} is strictly greater than {@code minimum}. */
	private void assertGreaterThan(JsonObject json, String path) {
		if (getValue(json, path).getAsInt() <= 0) {
			throw new AssertionError(path + " must be greater than " + 0);
		}
	}
	
	/** Asserts the integer at {@code path} is greater than or equal to {@code minimum}. */
	private void assertGreaterThanOrEqualTo(JsonObject json, String path) {
		if (getValue(json, path).getAsInt() < 0) {
			throw new AssertionError(path + " must be at least " + 0);
		}
	}
	
	/**
	 * Resolves a value inside a JSON object using a tiny dot-path language. A path is dot-separated
	 * segments; each segment is a property name optionally followed by an array index in square
	 * brackets. For example "results[0].simpleTB08Data.newAllDetected" means: property results (a
	 * JSON array), element 0, then property simpleTB08Data, then property newAllDetected.
	 * 
	 * @throws AssertionError if any property is missing ("Missing response property: &lt;path&gt;")
	 *             or an indexed element does not exist
	 *             ("Missing response array item: &lt;path&gt;")
	 */
	private JsonElement getValue(JsonObject json, String path) {
		JsonElement current = json;
		for (String part : path.split("\\.")) {
			int arrayStart = part.indexOf('[');
			String property = arrayStart == -1 ? part : part.substring(0, arrayStart);
			if (!current.isJsonObject() || !current.getAsJsonObject().has(property)) {
				throw new AssertionError("Missing response property: " + path);
			}
			current = current.getAsJsonObject().get(property);
			if (arrayStart != -1) {
				int index = Integer.parseInt(part.substring(arrayStart + 1, part.length() - 1));
				if (!current.isJsonArray() || current.getAsJsonArray().size() <= index) {
					throw new AssertionError("Missing response array item: " + path);
				}
				current = current.getAsJsonArray().get(index);
			}
		}
		return current;
	}
}
