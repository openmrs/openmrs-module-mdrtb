package org.openmrs.module.mdrtb;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;

public class MdrtbConstants {
	
	public static final String moduleName = "mdrtb";
	
	public static final String UUID_REGEX = "^[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]+$";;
	
	public static final String DATE_FORMAT_DISPLAY = "dd/MMM/yyyy";
	
	public static final String RUSSIAN_DATE_FORMAT = "yyyy.MM.dd";
	
	public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String SQL_DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	public static final String PATIENT_CHART_REGIMEN_CELL_COLOR = "lightblue";
	
	public static final String ROLES_TO_REDIRECT_GLOBAL_PROPERTY = moduleName + ".roles_to_redirect_from_openmrs_homepage";
	
	public enum TbClassification {
		MONO_RESISTANT_TB, POLY_RESISTANT_TB, MDR_TB, XDR_TB, RIF_RESISTANT_TB, PRE_XDR_TB
	}
	
	public enum TreatmentState {
		NOT_ON_TREATMENT, ON_TREATMENT
	}
	
	public static final String ENROLLMENT_LOCATION_TAG_NAME = "Admission Location";
	
	public static final String CULTURE_LOCATION_TAG_NAME = "Culture Lab";
	
	// The Concept Map name-space
	public static final String MDRTB_CONCEPT_MAPPING_CODE = "org.openmrs.module.mdrtb";
	
	public static final String LOCATION_ATTRIBUTE_TYPE_LEVEL = "LEVEL";
	
	/*******************************/
	/** START - Global Properties **/
	
	public static final String GP_MDRTB_PROGRAM_NAME = "mdrtb.programName";
	
	public static final String GP_DOTS_PROGRAM_NAME = "mdrtb.dotsProgramName";
	
	public static final String GP_OPENMRS_PRIMARY_IDENTIFIER_TYPE = "mdrtb.primaryPatientIdentifierType";
	
	public static final String GP_MDRTB_IDENTIFIER_TYPE = "mdrtb.mdrtbPatientIdentifierType";
	
	public static final String GP_DOTS_IDENTIFIER_TYPE = "mdrtb.dotsPatientIdentifierType";
	
	public static final String GP_COLOR_MAP = "mdrtb.colorMap";
	
	public static final String GP_MDRTB_BIRT_REPORT_LIST = "mdrtb.birt_report_list";
	
	public static final String GP_TX_SUPPORTER_RELATIONSHIP_TYPE = "mdrtb.treatmentSupporterRelationshipType";
	
	public static final String GP_PATIENT_CONTACT_ID_ATTRIBUTE_TYPE = "mdrtb.patientContactIdAttributeType";
	
	//TODO: instead of list of drugs, use concept set
	@Deprecated
	public static final String GP_DEFAULT_DST_DRUGS = "mdrtb.defaultDstDrugs";
	
	public static final String GP_REGIMEN_TYPE_CONFIGURATION = "mdrtb.regimenTypeConfiguration";
	
	public static final String GP_FIXED_IDENTIFIER_LOCATION = "mdrtb.fixedIdentifierLocation";
	
	public static final String GP_COHORT_BUILDER_DRUG_SETS = "cohortBuilder.drugSets";
	
	public static final String GP_COHORT_COHORT_BUILDER_SHORTCUTS = "cohort.cohortBuilder.shortcuts";
	
	@Deprecated
	public static final String GP_SPECIMEN_REPORTS_DEFAULT_LAB = "mdrtb.specimenReports.defaultLabId";
	
	public static final String GP_SPECIMEN_REPORTS_DAYS_SINCE_CULTURE = "mdrtb.specimenReports.daysSinceCulture";
	
	public static final String GP_SPECIMEN_REPORTS_DAYS_SINCE_SMEAR = "mdrtb.specimenReports.daysSinceSmear";
	
	/** Concepts **/
	public static final String GP_NEW_CONCEPT_ID = "mdrtb.new.conceptId";
	
	public static final String GP_AFTER_RELAPSE1_CONCEPT_ID = "mdrtb.afterRelapse1.conceptId";
	
	public static final String GP_AFTER_RELAPSE2_CONCEPT_ID = "mdrtb.afterRelapse2.conceptId";
	
	public static final String GP_AFTER_FAILURE1_CONCEPT_ID = "mdrtb.afterFailure1.conceptId";
	
	public static final String GP_AFTER_FAILURE2_CONCEPT_ID = "mdrtb.afterFailure2.conceptId";
	
	public static final String GP_AFTER_DEFAULT1_CONCEPT_ID = "mdrtb.afterDefault1.conceptId";
	
	public static final String GP_AFTER_DEFAULT2_CONCEPT_ID = "mdrtb.afterDefault2.conceptId";
	
	public static final String GP_OTHER_CONCEPT_ID = "mdrtb.other.conceptId";
	
	public static final String GP_OUTCOME_STARTED_SLD_CONCEPT_ID = "mdrtb.outcome.sld2.conceptId";
	
	public static final String GP_OUTCOME_TRANSFER_OUT_CONCEPT_ID = "mdrtb.outcome.transferout.conceptId";
	
	public static final String GP_OUTCOME_CANCELED_CONCEPT_ID = "mdrtb.outcome.canceled.conceptId";
	
	public static final String GP_OUTCOME_LTFU_CONCEPT_ID = "mdrtb.outcome.ltfu.conceptId";
	
	public static final String GP_OUTCOME_DIED_CONCEPT_ID = "mdrtb.outcome.died.conceptId";
	
	public static final String GP_OUTCOME_TX_FAILURE_CONCEPT_ID = "mdrtb.outcome.txFailure.conceptId";
	
	public static final String GP_OUTCOME_TX_COMPLETED_CONCEPT_ID = "mdrtb.outcome.txCompleted.conceptId";
	
	public static final String GP_OUTCOME_CURED_CONCEPT_ID = "mdrtb.outcome.cured.conceptId";
	
	public static final String GP_TRANSFER_IN_CONCEPT_ID = "mdrtb.transferIn.conceptId";
	
	/** Forms **/
	public static final String GP_DST_FORM_ID = "mdrtb.dst.formId";
	
	public static final String GP_HAIN_FORM_ID = "mdrtb.hain.formId";
	
	public static final String GP_CULTURE_FORM_ID = "mdrtb.culture.formId";
	
	public static final String GP_SMEAR_FORM_ID = "mdrtb.smear.formId";
	
	public static final String GP_XPERT_FORM_ID = "mdrtb.xpert.formId";
	
	public static final String GP_MDRTB_FORM_ID_TO_ATTACH_TO_BACTERIOLOGY_ENTRY = "mdrtb.formIdToAttachToBacteriologyEntry";
	
	/** Encounters **/
	public static final String GP_ENCOUNTER_TYPE_ADVERSE_EVENT = "mdrtb.encounterType.adverseEvent";
	
	public static final String GP_ENCOUNTER_TYPE_TRANSFER_IN = "mdrtb.encounterType.transferIn";
	
	public static final String GP_ENCOUNTER_TYPE_TRANSFER_OUT = "mdrtb.encounterType.transferOut";
	
	public static final String GP_ENCOUNTER_TYPE_SPECIMEN_COLLECTION = "mdrtb.encounterType.specimenCollection";
	
	public static final String GP_ENCOUNTER_TYPE_FORM_89 = "mdrtb.encounterType.form89";
	
	public static final String GP_ENCOUNTER_TYPE_TB03 = "mdrtb.encounterType.tb03";
	
	public static final String GP_ENCOUNTER_TYPE_TB03U_MDR = "mdrtb.encounterType.tb03u.mdr";
	
	public static final String GP_ENCOUNTER_TYPE_TB03U_XDR = "mdrtb.encounterType.tb03u.xdr";
	
	public static final String GP_ENCOUNTER_TYPE_LAB_RESULT = "mdrtb.encounterType.labResult";
	
	public static final String GP_ENCOUNTER_TYPE_RESISTANCE_DURING_TREATMENT = "mdrtb.encounterType.resistanceDuringTreatment";
	
	public static final String GP_ENCOUNTER_TYPE_PV_REGIMEN = "mdrtb.encounterType.pvRegimen";
	
	public static final String GP_ENCOUNTER_TYPE_END_OF_TREATMENT = "mdrtb.encounterType.endOfTreatment";
	
	@Deprecated
	public static final String GP_TREATMENT_SUPPORTER_PERSON_ATTRIBUTE_TYPE = "mdrtb.treatmentSupporterPersonAttributeType";
	
	@Deprecated
	public static final String GP_PATIENT_IDENTIFIER_TYPE_LIST = "mdrtb.patient_identifier_type_list";
	
	@Deprecated
	public static final String GP_LAB_ENTRY_IDS = "mdrtb.lab_entry_ids";
	
	/** Properties referenced only from JSP views (legacy UI) **/
	public static final String GP_FIND_PATIENT_NUM_RESULTS = "mdrtb.findPatientNumResults";
	
	public static final String GP_DRUG_DOSE_UNITS = "mdrtb.drugDoseUnits";
	
	public static final String GP_PATIENT_IDENTIFIER_TYPE = "mdrtb.patient_identifier_type";
	
	/** END - Global Properties **/
	/*****************************/
	
	/************************/
	/** START - Common Lab **/
	public static final String DST_MGIT_TEST_NAME = "DST MGIT";
	
	public static final String DST_LJ_TEST_NAME = "DST LJ";
	
	public static final String XPERT_TEST_GROUP = "XPERT";
	
	public static final String CULTURE_TEST_GROUP = "CULTURE";
	
	public static final String HAIN_TEST_GROUP = "HAIN";
	
	public static final String HAIN_2_TEST_GROUP = "HAIN2";
	
	public static final String SMEAR_TEST_GROUP = "SMEAR";
	
	public static final String DST_TEST_GROUP = "DST";
	
	/** END - Common Lab **/
	/**********************/
	
	/***************************/
	/** START - Message Codes **/
	public static final String MESSAGE_CODE_NO = "mdrtb.no";
	
	public static final String MESSAGE_CODE_YES = "mdrtb.yes";
	
	/** END - Message Codes **/
	/*************************/
	
	/*****************************/
	/** START - Encounter Types **/
	public static EncounterType ET_ADVERSE_EVENT = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_ADVERSE_EVENT));
	
	public static EncounterType ET_TRANSFER_IN = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_TRANSFER_IN));
	
	public static EncounterType ET_TRANSFER_OUT = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_TRANSFER_OUT));
	
	public static EncounterType ET_SPECIMEN_COLLECTION = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_SPECIMEN_COLLECTION));
	
	public static EncounterType ET_FORM89_TB_FOLLOWUP = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_FORM_89));
	
	public static EncounterType ET_TB03_TB_INTAKE = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_TB03));
	
	public static EncounterType ET_TB03U_MDRTB_INTAKE = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_TB03U_MDR));
	
	public static EncounterType ET_TB03U_XDRTB_INTAKE = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_TB03U_XDR));
	
	public static EncounterType ET_LAB_RESULT = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_LAB_RESULT));
	
	public static EncounterType ET_DRUG_RESISTANCE_DURING_TREATMENT = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_RESISTANCE_DURING_TREATMENT));
	
	public static EncounterType ET_PV_REGIMEN = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_PV_REGIMEN));
	
	public static EncounterType ET_END_OF_TREATMENT = Context.getEncounterService().getEncounterType(
	    Context.getAdministrationService().getGlobalProperty(GP_ENCOUNTER_TYPE_END_OF_TREATMENT));
	
	/** END - Encounter Types **/
}
