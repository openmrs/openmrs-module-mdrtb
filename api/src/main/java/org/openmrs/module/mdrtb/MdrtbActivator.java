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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.mdrtb.api.MessagePropertyService;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.*;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MdrtbActivator extends BaseModuleActivator {
	
	/***************************/
	private final Log log = LogFactory.getLog(this.getClass());
	
	public static final String SPECIMEN_TYPE_CONCEPT_UUID = "commonlabtest.specimenTypeConceptUuid";
	
	public static final String SPECIMEN_SITE_CONCEPT_UUID = "commonlabtest.specimenSiteConceptUuid";
	
	public static final String TEST_UNITS_CONCEPT_UUID = "commonlabtest.testunitsConceptUuid";
	
	public static final String UPLOAD_FILE_DIRECTORY = "commonlabtest.fileDirectory";
	
	public static final String UPLOAD_FILE_EXTENSIONS = "commonlabtest.fileExtensions";
	
	public static final String FILE_EXTENSIONS_NAMES = ".bmp ,.jpg ,.jpeg,.jfif,.GIF,.png,.bat,.BPG,.FLV,.AVI,.MOV,.M4P,.MPG,.WMV,.3gp,.RM,.SWF,.3GP,.ACT,.AIFF,.MP3,.WAV,.OGG,.FLAC,.AU,.RAW,.docx,.docm,.dotx,.docb,.dotm,.pdf";
	
	public static final String LAB_ORDER_TYPE_UUID = "commonlabtest.labOrderTypeUuid";
	
	public static final String MDRTB_TEST_TYPE_UUID = "commonlabtest.mdrtbTestTypeUuid";
	
	private void setGlobalProperty(AdministrationService service, String prop, String val, String desc) {
		GlobalProperty gp = service.getGlobalPropertyObject(prop);
		if (gp == null) {
			service.saveGlobalProperty(new GlobalProperty(prop, val, desc));
		} else if (StringUtils.isEmpty(gp.getPropertyValue())) {
			gp.setPropertyValue(val);
			service.saveGlobalProperty(gp);
		}
	}
	
	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		log.info("Starting up MDR-TB module.");
		contextRefreshed();
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down MDR-TB module.");
	}
	
	@Override
	public void contextRefreshed() {
		log.info("MDR-TB contextRefreshed: starting.");
		configureMdrtbGlobalProperties(Context.getAdministrationService());
		seedMessageProperties();
		log.info("MDR-TB contextRefreshed: finished.");
	}
	
	/**
	 * Loads message bundles into the {@code message_properties} table. The rule is
	 * upsert-without-update: a (lang, code) pair that is already stored is left as it is
	 */
	void seedMessageProperties() {
		MessagePropertyService service = Context.getService(MessagePropertyService.class);
		Map<String, Map<String, String>> bundles = new MdrtbMessages().loadBundles();
		if (bundles.isEmpty()) {
			log.error("Seeding message_properties: no message bundles could be read from the classpath.");
			return;
		}
		int codesInBundles = 0;
		for (Map.Entry<String, Map<String, String>> bundle : bundles.entrySet()) {
			codesInBundles += bundle.getValue().size();
			log.info("Seeding message_properties: bundle " + bundle.getKey() + " of size " + bundle.getValue().size());
		}

		// One time read from the table.
		Set<String> storedKeys = new HashSet<>();
		int storedBefore = 0;
		for (MessageProperty stored : service.getAllMessageProperties()) {
			storedKeys.add(primaryKeyOf(stored.getLang(), stored.getCode()));
			storedBefore++;
		}
		log.info("Seeding message_properties: table has " + storedBefore + " row(s); bundles has "
				+ codesInBundles + " codes.");

		// Keep the first insertion position but replace the value. In case of duplicate, the latest value is selected
		Map<String, MessageProperty> candidatesByKey = new LinkedHashMap<>();
		for (Map.Entry<String, Map<String, String>> bundle : bundles.entrySet()) {
			String lang = bundle.getKey();
			for (Map.Entry<String, String> entry : bundle.getValue().entrySet()) {
				String code = entry.getKey();
				String key = primaryKeyOf(lang, code);
				if (storedKeys.contains(key)) {
					continue;
				}
				MessageProperty superseded = candidatesByKey.put(key, new MessageProperty(lang, code, entry.getValue()));
			}
		}
		Set<MessageProperty> toInsert = new LinkedHashSet<>(candidatesByKey.values());
		if (toInsert.isEmpty()) {
			log.info("Seeding message_properties: nothing to insert.");
			return;
		}

		log.info("Seeding message_properties: inserting " + toInsert.size() + " new code(s).");
		int inserted = service.saveMessageProperties(toInsert);

		// Read back rather than trusting the insert count
		int storedAfter = service.getAllMessageProperties().size();
		if (storedAfter < storedBefore + inserted) {
			log.error("Seeding message_properties: expected at least " + (storedBefore + inserted)
			        + " row(s) after seeding but found " + storedAfter);
		}
		service.resetMessageCache();
	}
	
	/**
	 * Builds the key by which the {@code message_properties} primary key will actually compare two
	 * rows.
	 */
	private static String primaryKeyOf(String lang, String code) {
		String normalisedLang = lang == null ? "" : lang.toLowerCase(Locale.ROOT);
		String normalisedCode = code == null ? "" : code.toLowerCase(Locale.ROOT);
		return normalisedLang + "\n" + normalisedCode;
	}
	
	/**
	 * Registers every global property owned by the MDR-TB module with its default value and
	 * description. Properties are only created when missing, and only populated when their current
	 * value is empty; values already set in the database (e.g. by the production migration) are
	 * never overwritten. This replaces the previous approach of loading these properties through
	 * the ETL job's global_properties.xlsx resource. Package-private so that it can be exercised
	 * directly by unit tests.
	 */
	void configureMdrtbGlobalProperties(AdministrationService service) {
		// ---------- Lab test ----------
		String complexObsDirectory = service.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_COMPLEX_OBS_DIR);
		File dir = StringUtils.isBlank(complexObsDirectory) ? new File(OpenmrsUtil.getApplicationDataDirectory())
		        : OpenmrsUtil.getDirectoryInApplicationDataDirectory(complexObsDirectory);
		File path = new File(dir, "labTestFiles");
		if (!(path.exists() && path.isDirectory())) {
			try {
				path.mkdir();
			}
			catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		setGlobalProperty(service, UPLOAD_FILE_DIRECTORY, path.toString(), "");
		setGlobalProperty(service, SPECIMEN_TYPE_CONCEPT_UUID, "2da61322-bcc5-4c32-b412-1b1ef37f4a25",
		    "The UUID of a concept representing a group or set of different types of specimen, e.g. Saliva, Blood, Pus, etc.");
		setGlobalProperty(
		    service,
		    SPECIMEN_SITE_CONCEPT_UUID,
		    "31bf065e-0370-102d-b0e3-001ec94a0cc1",
		    "The UUID of a concept representing a group or set of anatomical source site from where the specimen is obtained, e.g. Bone, Tissue, etc.");
		setGlobalProperty(service, TEST_UNITS_CONCEPT_UUID, "5f21ab43-ec32-44b2-88e5-bc4ed2b93fba",
		    "The UUID of a concept representing a group or set of various measurement units (also used to measure dosage quantity).");
		setGlobalProperty(service, UPLOAD_FILE_EXTENSIONS, FILE_EXTENSIONS_NAMES, "");
		setGlobalProperty(service, LAB_ORDER_TYPE_UUID, "33ccfcc6-0370-102d-b0e3-001ec94a0cc1",
		    "The UUID of the Order type representing a Lab Test Order.");
		setGlobalProperty(service, MDRTB_TEST_TYPE_UUID, "4e81d04f-bdc0-11ed-9c1c-00155d694c4d",
		    "The UUID of the Order type representing a Lab Test Order.");
		
		// ---------- Programs ----------
		setGlobalProperty(service, MdrtbConstants.GP_MDRTB_PROGRAM_NAME, "MDR-TB PROGRAM",
		    "Name of the MDR-TB program. Must exactly match the name of a program defined under Administration > Programs.");
		setGlobalProperty(service, MdrtbConstants.GP_DOTS_PROGRAM_NAME, "DOTS Program",
		    "Name of the DOTS program. Must exactly match the name of a program defined under Administration > Programs.");
		
		// ---------- Patient identifier types ----------
		setGlobalProperty(service, MdrtbConstants.GP_OPENMRS_PRIMARY_IDENTIFIER_TYPE, "Регистрационный номер Дотc",
		    "Name of the primary patient identifier type to display for patients.");
		setGlobalProperty(service, MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE, "Регистрационный номер МЛУ",
		    "Name of the patient identifier type that holds the MDR-TB (МЛУ) registration number.");
		setGlobalProperty(service, MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE, "Регистрационный номер Дотc",
		    "Name of the patient identifier type that holds the DOTS registration number.");
		setGlobalProperty(service, MdrtbConstants.GP_PATIENT_IDENTIFIER_TYPE_LIST, "",
		    "Pipe delimited list of all patient identifier types associated with the MDR-TB program.");
		setGlobalProperty(service, MdrtbConstants.GP_PATIENT_IDENTIFIER_TYPE, "",
		    "Name of the patient identifier type shown in the patient identifiers portlet (legacy UI).");
		setGlobalProperty(
		    service,
		    MdrtbConstants.GP_FIXED_IDENTIFIER_LOCATION,
		    "",
		    "Takes as a parameter a location name. If this property is set, when creating patient identifiers, the patient "
		            + "identifier location is set to this value (and setting the location is not presented to the user as an option).");
		
		// ---------- Encounter types (values must match encounter type names) ----------
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_TB03, "TB03",
		    "Name of the encounter type used for TB03 (DOTS intake) encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_MDR, "TB03u - MDR",
		    "Name of the encounter type used for TB03u (MDR-TB intake) encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_XDR, "TB03u - XDR",
		    "Name of the encounter type used for TB03u (XDR-TB intake) encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_FORM_89, "Form 89",
		    "Name of the encounter type used for Form 89 (follow-up) encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_ADVERSE_EVENT, "Adverse Event",
		    "Name of the encounter type used for adverse event (pharmacovigilance) encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_LAB_RESULT, "Lab Result",
		    "Name of the encounter type used for lab result encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_RESISTANCE_DURING_TREATMENT,
		    "Resistance During Treatment",
		    "Name of the encounter type used for drug resistance during treatment encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_PV_REGIMEN, "PV Regimen",
		    "Name of the encounter type used for pharmacovigilance regimen encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_IN, "Transfer In",
		    "Name of the encounter type used for transfer in encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_OUT, "Transfer Out",
		    "Name of the encounter type used for transfer out encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_SPECIMEN_COLLECTION, "Specimen Collection",
		    "Name of the encounter type used for specimen collection encounters.");
		setGlobalProperty(service, MdrtbConstants.GP_ENCOUNTER_TYPE_END_OF_TREATMENT, "End of Treatment",
		    "Name of the encounter type used for End of Treatment encounters.");
		
		// ---------- Lab result form ids ----------
		setGlobalProperty(service, MdrtbConstants.GP_SMEAR_FORM_ID, "8", "Form id of the Smear lab result form.");
		setGlobalProperty(service, MdrtbConstants.GP_DST_FORM_ID, "11", "Form id of the DST lab result form.");
		setGlobalProperty(service, MdrtbConstants.GP_CULTURE_FORM_ID, "12", "Form id of the Culture lab result form.");
		setGlobalProperty(service, MdrtbConstants.GP_XPERT_FORM_ID, "14", "Form id of the Xpert lab result form.");
		setGlobalProperty(service, MdrtbConstants.GP_HAIN_FORM_ID, "15", "Form id of the Hain lab result form.");
		setGlobalProperty(service, MdrtbConstants.GP_MDRTB_FORM_ID_TO_ATTACH_TO_BACTERIOLOGY_ENTRY, "",
		    "If you would like for direct entry of a smear and culture to have a formId attached to the encounter, enter "
		            + "that here. This is useful if you want direct smear entry to show up in the cat-iv treatment card.");
		
		// ---------- Patient registration group concept ids ----------
		setGlobalProperty(service, MdrtbConstants.GP_NEW_CONCEPT_ID, "18",
		    "Concept id of the 'New' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_AFTER_RELAPSE1_CONCEPT_ID, "20",
		    "Concept id of the 'After relapse 1' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_AFTER_RELAPSE2_CONCEPT_ID, "430",
		    "Concept id of the 'After relapse 2' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_AFTER_FAILURE1_CONCEPT_ID, "17",
		    "Concept id of the 'After failure 1' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_AFTER_FAILURE2_CONCEPT_ID, "16",
		    "Concept id of the 'After failure 2' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_AFTER_DEFAULT1_CONCEPT_ID, "14",
		    "Concept id of the 'After default 1' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_AFTER_DEFAULT2_CONCEPT_ID, "431",
		    "Concept id of the 'After default 2' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OTHER_CONCEPT_ID, "19",
		    "Concept id of the 'Other' patient registration group concept.");
		setGlobalProperty(service, MdrtbConstants.GP_TRANSFER_IN_CONCEPT_ID, "15",
		    "Concept id of the 'Transfer in' patient registration group concept.");
		
		// ---------- Treatment outcome concept ids ----------
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_CURED_CONCEPT_ID, "26",
		    "Concept id of the 'Cured' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_TX_COMPLETED_CONCEPT_ID, "28",
		    "Concept id of the 'Treatment completed' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_TX_FAILURE_CONCEPT_ID, "27",
		    "Concept id of the 'Treatment failed' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID, "25",
		    "Concept id of the 'Died' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_LTFU_CONCEPT_ID, "29",
		    "Concept id of the 'Lost to follow-up' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_TRANSFER_OUT_CONCEPT_ID, "23",
		    "Concept id of the 'Transferred out' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_CANCELED_CONCEPT_ID, "280",
		    "Concept id of the 'Canceled' treatment outcome concept.");
		setGlobalProperty(service, MdrtbConstants.GP_OUTCOME_STARTED_SLD_CONCEPT_ID, "325",
		    "Concept id of the 'Started on second-line drugs' treatment outcome concept.");
		
		// ---------- Specimen reports ----------
		setGlobalProperty(service, MdrtbConstants.GP_SPECIMEN_REPORTS_DEFAULT_LAB, "47",
		    "Stores the default lab id for the specimen reports. You should never need to modify this parameter here, as "
		            + "it is updated via the specimen reports UI.");
		setGlobalProperty(service, MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_CULTURE, "14",
		    "Stores the 'days since culture' variable for the specimen reports. You should never need to modify this "
		            + "parameter here, as it is updated via the specimen reports UI.");
		setGlobalProperty(service, MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_SMEAR, "14",
		    "Stores the 'days since smear' variable for the specimen reports. You should never need to modify this "
		            + "parameter here, as it is updated via the specimen reports UI.");
		
		// ---------- Labs and DST ----------
		setGlobalProperty(service, MdrtbConstants.GP_LAB_ENTRY_IDS, "355|356|357|358|359|360|361|362|363",
		    "Pipe delimited list of address hierarchy entry ids representing labs.");
		setGlobalProperty(service, MdrtbConstants.GP_DEFAULT_DST_DRUGS,
		    "ISONIAZID|RIFAMPICIN|ETHAMBUTOL|PYRAZINAMIDE|STREPTOMYCIN",
		    "Pipe delimited list of default drugs to display in the specimen 'Add DST' list.");
		
		// ---------- Person attributes and relationships ----------
		setGlobalProperty(service, MdrtbConstants.GP_TX_SUPPORTER_RELATIONSHIP_TYPE,
		    "Treatment Supporter/Treatment Supportee",
		    "The relationship type used to describe the relationship from a treatment supporter (A) to a patient (B).");
		setGlobalProperty(service, MdrtbConstants.GP_TREATMENT_SUPPORTER_PERSON_ATTRIBUTE_TYPE, "Treatment Supporter",
		    "The person attribute type that corresponds to being a treatment supporter/acompanateur/DOTS worker.");
		setGlobalProperty(service, MdrtbConstants.GP_PATIENT_CONTACT_ID_ATTRIBUTE_TYPE, "MDR-TB Patient Contact ID Number",
		    "The person attribute type corresponding to a patient contact's ID number.");
		
		// ---------- Miscellaneous ----------
		setGlobalProperty(service, MdrtbConstants.GP_COLOR_MAP, "CONTAMINATED:lightgrey|UNSATISFACTORY SAMPLE:lightgrey|"
		        + "MODERATELY POSITIVE:lightcoral|STRONGLY POSITIVE:lightcoral|WEAKLY POSITIVE:lightcoral|"
		        + "WAITING FOR TEST RESULTS:lightgrey|SCANTY:khaki|NEGATIVE:lightgreen|POSITIVE:lightcoral|"
		        + "SUSCEPTIBLE TO TUBERCULOSIS DRUG:lightgreen|INDETERMINATE TO TUBERCULOSIS DRUG:khaki|"
		        + "RESISTANT TO TUBERCULOSIS DRUG:lightcoral|NONE:none",
		    "Pipe delimited list mapping concept names to color names and/or hex codes for display in the patient chart "
		            + "and (potentially) elsewhere.");
		setGlobalProperty(service, MdrtbConstants.GP_MDRTB_BIRT_REPORT_LIST, "",
		    "Pipe delimited list of all BIRT reports (by name) that correspond to the MDR-TB program.");
		setGlobalProperty(service, MdrtbConstants.GP_REGIMEN_TYPE_CONFIGURATION, "",
		    "XML configuration of the regimen types. When empty, the module falls back to the default "
		            + "RegimenTypeConfiguration.xml bundled inside the module.");
		setGlobalProperty(service, MdrtbConstants.GP_FIND_PATIENT_NUM_RESULTS, "20",
		    "Number of results to display when searching for a patient.");
		setGlobalProperty(service, MdrtbConstants.GP_DRUG_DOSE_UNITS, "g,mg,ml,tab(s)",
		    "Comma separated list of dose units available in the regimen editor.");
	}
}
