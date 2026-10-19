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
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MessagePropertyService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests that {@link MdrtbActivator} registers all mdrtb.* global properties with sensible defaults
 * without overwriting values that administrators (or the production migration) have already set.
 */
public class MdrtbActivatorTest extends BaseModuleContextSensitiveTest {
	
	private MdrtbActivator activator;
	
	private AdministrationService administrationService;
	
	@Before
	public void setup() {
		activator = new MdrtbActivator();
		administrationService = Context.getAdministrationService();
	}
	
	@Test
	public void configureMdrtbGlobalProperties_shouldCreateMissingPropertiesWithDefaults() {
		activator.configureMdrtbGlobalProperties(administrationService);
		
		// Common lab test
		assertEquals("2da61322-bcc5-4c32-b412-1b1ef37f4a25",
		    administrationService.getGlobalProperty(MdrtbActivator.SPECIMEN_TYPE_CONCEPT_UUID));
		assertEquals("31bf065e-0370-102d-b0e3-001ec94a0cc1",
		    administrationService.getGlobalProperty(MdrtbActivator.SPECIMEN_SITE_CONCEPT_UUID));
		assertEquals("5f21ab43-ec32-44b2-88e5-bc4ed2b93fba",
		    administrationService.getGlobalProperty(MdrtbActivator.TEST_UNITS_CONCEPT_UUID));
		assertEquals(MdrtbActivator.FILE_EXTENSIONS_NAMES,
		    administrationService.getGlobalProperty(MdrtbActivator.UPLOAD_FILE_EXTENSIONS));
		assertEquals("33ccfcc6-0370-102d-b0e3-001ec94a0cc1",
		    administrationService.getGlobalProperty(MdrtbActivator.LAB_ORDER_TYPE_UUID));
		assertEquals("4e81d04f-bdc0-11ed-9c1c-00155d694c4d",
		    administrationService.getGlobalProperty(MdrtbActivator.MDRTB_TEST_TYPE_UUID));
		assertNotNull(administrationService.getGlobalProperty(MdrtbActivator.UPLOAD_FILE_DIRECTORY));
		
		// Programs
		assertEquals("MDR-TB PROGRAM", administrationService.getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME));
		assertEquals("DOTS Program", administrationService.getGlobalProperty(MdrtbConstants.GP_DOTS_PROGRAM_NAME));
		
		// Identifier types
		assertEquals("Регистрационный номер МЛУ",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE));
		assertEquals("Регистрационный номер Дотc",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE));
		
		// Encounter types
		assertEquals("TB03", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03));
		assertEquals("TB03u - MDR", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_MDR));
		assertEquals("TB03u - XDR", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_XDR));
		assertEquals("Form 89", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_FORM_89));
		assertEquals("Adverse Event",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_ADVERSE_EVENT));
		assertEquals("Lab Result", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_LAB_RESULT));
		assertEquals("Resistance During Treatment",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_RESISTANCE_DURING_TREATMENT));
		assertEquals("PV Regimen", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_PV_REGIMEN));
		assertEquals("Transfer In", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_IN));
		assertEquals("Transfer Out", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_OUT));
		assertEquals("Specimen Collection",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_SPECIMEN_COLLECTION));
		assertEquals("End of Treatment",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_END_OF_TREATMENT));
		
		// Lab result form ids
		assertEquals("8", administrationService.getGlobalProperty(MdrtbConstants.GP_SMEAR_FORM_ID));
		assertEquals("11", administrationService.getGlobalProperty(MdrtbConstants.GP_DST_FORM_ID));
		assertEquals("12", administrationService.getGlobalProperty(MdrtbConstants.GP_CULTURE_FORM_ID));
		assertEquals("14", administrationService.getGlobalProperty(MdrtbConstants.GP_XPERT_FORM_ID));
		assertEquals("15", administrationService.getGlobalProperty(MdrtbConstants.GP_HAIN_FORM_ID));
		
		// Registration group concept ids
		assertEquals("18", administrationService.getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID));
		assertEquals("20", administrationService.getGlobalProperty(MdrtbConstants.GP_AFTER_RELAPSE1_CONCEPT_ID));
		assertEquals("430", administrationService.getGlobalProperty(MdrtbConstants.GP_AFTER_RELAPSE2_CONCEPT_ID));
		assertEquals("17", administrationService.getGlobalProperty(MdrtbConstants.GP_AFTER_FAILURE1_CONCEPT_ID));
		assertEquals("16", administrationService.getGlobalProperty(MdrtbConstants.GP_AFTER_FAILURE2_CONCEPT_ID));
		assertEquals("14", administrationService.getGlobalProperty(MdrtbConstants.GP_AFTER_DEFAULT1_CONCEPT_ID));
		assertEquals("431", administrationService.getGlobalProperty(MdrtbConstants.GP_AFTER_DEFAULT2_CONCEPT_ID));
		assertEquals("19", administrationService.getGlobalProperty(MdrtbConstants.GP_OTHER_CONCEPT_ID));
		assertEquals("15", administrationService.getGlobalProperty(MdrtbConstants.GP_TRANSFER_IN_CONCEPT_ID));
		
		// Treatment outcome concept ids
		assertEquals("26", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_CURED_CONCEPT_ID));
		assertEquals("28", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_TX_COMPLETED_CONCEPT_ID));
		assertEquals("27", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_TX_FAILURE_CONCEPT_ID));
		assertEquals("25", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID));
		assertEquals("29", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_LTFU_CONCEPT_ID));
		assertEquals("23", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_TRANSFER_OUT_CONCEPT_ID));
		assertEquals("280", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_CANCELED_CONCEPT_ID));
		assertEquals("325", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_STARTED_SLD_CONCEPT_ID));
		
		// Specimen reports
		assertEquals("47", administrationService.getGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DEFAULT_LAB));
		assertEquals("14", administrationService.getGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_CULTURE));
		assertEquals("14", administrationService.getGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_SMEAR));
		
		// Labs and DST
		assertEquals("355|356|357|358|359|360|361|362|363",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS));
		assertEquals("ISONIAZID|RIFAMPICIN|ETHAMBUTOL|PYRAZINAMIDE|STREPTOMYCIN",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_DEFAULT_DST_DRUGS));
		
		// Person attributes and relationships
		assertEquals("Treatment Supporter/Treatment Supportee",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_TX_SUPPORTER_RELATIONSHIP_TYPE));
		assertEquals("Treatment Supporter",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_TREATMENT_SUPPORTER_PERSON_ATTRIBUTE_TYPE));
		assertEquals("MDR-TB Patient Contact ID Number",
		    administrationService.getGlobalProperty(MdrtbConstants.GP_PATIENT_CONTACT_ID_ATTRIBUTE_TYPE));
		
		// Display and configuration
		assertNotNull(administrationService.getGlobalProperty(MdrtbConstants.GP_COLOR_MAP));
		assertEquals("20", administrationService.getGlobalProperty(MdrtbConstants.GP_FIND_PATIENT_NUM_RESULTS));
		assertEquals("g,mg,ml,tab(s)", administrationService.getGlobalProperty(MdrtbConstants.GP_DRUG_DOSE_UNITS));
	}
	
	@Test
	public void configureMdrtbGlobalProperties_shouldNotOverwriteExistingValues() {
		administrationService.saveGlobalProperty(new GlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME,
		        "CUSTOM PROGRAM NAME"));
		administrationService.saveGlobalProperty(new GlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03, "CUSTOM TB03"));
		administrationService.saveGlobalProperty(new GlobalProperty(MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID, "9999"));
		
		activator.configureMdrtbGlobalProperties(administrationService);
		
		assertEquals("CUSTOM PROGRAM NAME", administrationService.getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME));
		assertEquals("CUSTOM TB03", administrationService.getGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03));
		assertEquals("9999", administrationService.getGlobalProperty(MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID));
	}
	
	@Test
	public void configureMdrtbGlobalProperties_shouldFillPropertiesWithEmptyValues() {
		administrationService.saveGlobalProperty(new GlobalProperty(MdrtbConstants.GP_DST_FORM_ID, ""));
		
		activator.configureMdrtbGlobalProperties(administrationService);
		
		assertEquals("11", administrationService.getGlobalProperty(MdrtbConstants.GP_DST_FORM_ID));
	}
	
	@Test
	public void configureMdrtbGlobalProperties_shouldBeIdempotent() {
		activator.configureMdrtbGlobalProperties(administrationService);
		activator.configureMdrtbGlobalProperties(administrationService);
		
		assertEquals("MDR-TB PROGRAM", administrationService.getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME));
	}
	
	/**
	 * The activator is what moves the shipped messages*.properties bundles into the
	 * message_properties table once liquibase has created it. Detailed coverage of seeding
	 * semantics, the locale fallback chain and the cache lives in {@link MdrtbMessagePropertyTest};
	 * this asserts the activator actually calls it and does so through the real classpath.
	 */
	@Test
	public void seedMessageProperties_shouldLoadTheBundlesIntoTheDatabase() {
		MessagePropertyService service = Context.getService(MessagePropertyService.class);
		
		activator.seedMessageProperties();
		
		assertNotNull(service.getMessageProperty("en", "mdrtb.unknown"));
		assertEquals("Unknown", service.getMessageProperty("en", "mdrtb.unknown").getMessage());
		assertEquals("Данных нет", service.getMessageProperty("ru", "mdrtb.unknown").getMessage());
	}
	
	@Test
	public void seedMessageProperties_shouldBeSafeToRunTwice() {
		activator.seedMessageProperties();
		activator.seedMessageProperties();
		
		assertEquals("Unknown", Context.getService(MessagePropertyService.class).getMessageProperty("en", "mdrtb.unknown")
		        .getMessage());
	}
}
