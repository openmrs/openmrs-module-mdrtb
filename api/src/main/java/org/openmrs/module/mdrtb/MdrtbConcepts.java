package org.openmrs.module.mdrtb;

/**
 * This class defines all of the Concept names/mappings that are required/used by this module.
 * <p>
 * It holds constants only. Concept resolution (including caching) lives in
 * {@link org.openmrs.module.mdrtb.api.MdrtbServiceImpl#getConcept(String)} - call
 * <code>Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SOME_CONSTANT)</code>
 * instead of resolving concepts here.
 */
public class MdrtbConcepts {
	
	// General
	public static final String YES = "31b27f6a-0370-102d-b0e3-001ec94a0cc1"; // YES
	
	public static final String NO = "31b2803c-0370-102d-b0e3-001ec94a0cc1"; // NO
	
	// Vitals
	public static final String WEIGHT = "31c64f86-0370-102d-b0e3-001ec94a0cc1"; // WEIGHT
	
	public static final String PULSE = "31c64dd8-0370-102d-b0e3-001ec94a0cc1"; // PULSE
	
	public static final String TEMPERATURE = "31c64eb4-0370-102d-b0e3-001ec94a0cc1"; // TEMPERATURE
	
	public static final String RESPIRATORY_RATE = "31c7bd8a-0370-102d-b0e3-001ec94a0cc1"; // RESPIRATORY RATE
	
	public static final String SYSTOLIC_BLOOD_PRESSURE = "31c64c34-0370-102d-b0e3-001ec94a0cc1"; // SYSTOLIC BLOOD PRESSURE
	
	// MDR-TB Drugs
	public static final String TUBERCULOSIS_DRUGS = "31bef3d0-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS DRUGS
	
	public static final String ISONIAZID = "31afed04-0370-102d-b0e3-001ec94a0cc1"; // ISONIAZID
	
	public static final String RIFAMPICIN = "31b09a60-0370-102d-b0e3-001ec94a0cc1"; // RIFAMPICIN
	
	public static final String CAPREOMYCIN = "31b4a3ee-0370-102d-b0e3-001ec94a0cc1"; // CAPREOMYCIN
	
	public static final String KANAMYCIN = "31b4a8e4-0370-102d-b0e3-001ec94a0cc1"; // KANAMYCIN
	
	public static final String AMIKACIN = "31b49fca-0370-102d-b0e3-001ec94a0cc1"; // AMIKACIN
	
	public static final String CLOFAZIMINE = "31b4a4c0-0370-102d-b0e3-001ec94a0cc1"; // CLOFAZIMINE
	
	public static final String CYCLOSERINE = "31b4a592-0370-102d-b0e3-001ec94a0cc1"; // CYCLOSERINE
	
	public static final String ETHIONAMIDE = "31b4a664-0370-102d-b0e3-001ec94a0cc1"; // ETHIONAMIDE
	
	public static final String PROTHIONAMIDE = "31b4a740-0370-102d-b0e3-001ec94a0cc1"; // PROTHIONAMIDE
	
	public static final String GATIFLOXACIN = "31b4a812-0370-102d-b0e3-001ec94a0cc1"; // GATIFLOXACIN
	
	public static final String OFLOXACIN = "31b4a9b6-0370-102d-b0e3-001ec94a0cc1"; // OFLOXACIN
	
	public static final String P_AMINOSALICYLIC_ACID = "31b4aa88-0370-102d-b0e3-001ec94a0cc1"; // P-AMINOSALICYLIC ACID
	
	public static final String TERIZIDONE = "31b64c6c-0370-102d-b0e3-001ec94a0cc1"; // TERIZIDONE
	
	public static final String VIOMYCIN = "31bb3c54-0370-102d-b0e3-001ec94a0cc1"; // VIOMYCIN
	
	public static final String CLARITHROMYCIN = "31bb3e02-0370-102d-b0e3-001ec94a0cc1"; // CLARITHROMYCIN
	
	public static final String RIFABUTIN = "31bb3ed4-0370-102d-b0e3-001ec94a0cc1"; // RIFABUTIN
	
	public static final String STREPTOMYCIN = "31ae3504-0370-102d-b0e3-001ec94a0cc1"; // STREPTOMYCIN
	
	public static final String PYRAZINAMIDE = "31caeaa0-0370-102d-b0e3-001ec94a0cc1"; // PYRAZINAMIDE
	
	public static final String CIPROFLOXACIN = "31b0841c-0370-102d-b0e3-001ec94a0cc1"; // CIPROFLOXACIN
	
	public static final String ETHAMBUTOL = "31b08840-0370-102d-b0e3-001ec94a0cc1"; // ETHAMBUTOL
	
	public static final String LEVOFLOXACIN = "31b0907e-0370-102d-b0e3-001ec94a0cc1"; // LEVOFLOXACIN
	
	public static final String PYRIDOXINE = "31b0998e-0370-102d-b0e3-001ec94a0cc1"; // PYRIDOXINE
	
	public static final String MOXIFLOXACIN = "31b1e398-0370-102d-b0e3-001ec94a0cc1"; // MOXIFLOXACIN
	
	public static final String AMOXICILLIN_AND_LAVULANIC_ACID = "31ae749c-0370-102d-b0e3-001ec94a0cc1"; // AMOXICILLIN AND CLAVULANIC ACID
	
	public static final String THIOACETAZONE = "31b64b9a-0370-102d-b0e3-001ec94a0cc1"; // THIOACETAZONE
	
	public static final String BEDAQUILINE = "a60a046e-0739-4bd8-83b6-fe86e4771b48"; // BEDAQUILINE
	
	public static final String DELAMANID = "e1236682-8451-4c75-b4ba-44457d1ebf43"; // DELAMANID
	
	public static final String LINEZOLID = "b4d9a4cc-a6cd-431f-8915-5dd96e7cf678"; // LINEZOLID
	
	@Deprecated
	public static final String IMIPENEM = "8d53d5ad-5477-4997-aadb-5c661c51b5e4"; // IMIPENEM
	
	public static final String QUINOLONES = "31bef15a-0370-102d-b0e3-001ec94a0cc1"; // QUINOLONES
	
	// Drug-Related concepts
	public static final String CURRENT_MULTI_DRUG_RESISTANT_TUBERCULOSIS_TREATMENT_TYPE = "31ce35fc-0370-102d-b0e3-001ec94a0cc1"; // CURRENT MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT TYPE
	
	public static final String REASON_TUBERCULOSIS_TREATMENT_CHANGED_OR_STOPPED = "31b41c26-0370-102d-b0e3-001ec94a0cc1"; // REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED
	
	public static final String STANDARDIZED = "31ce2dbe-0370-102d-b0e3-001ec94a0cc1"; // STANDARDIZED
	
	public static final String EMPIRIC = "31ce2f12-0370-102d-b0e3-001ec94a0cc1"; // EMPIRIC
	
	public static final String INDIVIDUALIZED = "31ce3052-0370-102d-b0e3-001ec94a0cc1"; // INDIVIDUALIZED
	
	// Smear, Culture, and DSTs
	public static final String BACILLI = "31bf136a-0370-102d-b0e3-001ec94a0cc1"; // BACILLI
	
	public static final String COLONIES = "31bef4ac-0370-102d-b0e3-001ec94a0cc1"; // COLONIES
	
	public static final String COLONIES_IN_CONTROL = "31bef722-0370-102d-b0e3-001ec94a0cc1"; // COLONIES IN CONTROL
	
	public static final String CONCENTRATION = "31bef57e-0370-102d-b0e3-001ec94a0cc1"; // CONCENTRATION
	
	public static final String CULTURE_CONSTRUCT = "31bf10e0-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS CULTURE CONSTRUCT
	
	public static final String CULTURE_GROWTH = "b7a7ebe3-e298-4dd7-b711-8fe659a8ce2f"; // GROWTH
	
	public static final String CULTURE_METHOD = "31bf0d84-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS CULTURE METHOD
	
	public static final String CULTURE_RESULT = "31bf0f32-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS CULTURE RESULT
	
	public static final String DAYS_TO_POSITIVITY = "fbef36fd-8f0d-4bac-8b2d-0c6141c91382"; // DAYS TO POSITIVITY
	
	public static final String DIRECT_INDIRECT = "31bef7f4-0370-102d-b0e3-001ec94a0cc1"; // DIRECT/INDIRECT
	
	public static final String DST_CONSTRUCT = "31bf09b0-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS DRUG SENSITIVITY TEST CONSTRUCT
	
	public static final String DST_CONTAMINATED = "31bb6666-0370-102d-b0e3-001ec94a0cc1"; // DST CONTAMINATED
	
	public static final String DST_METHOD = "31bf023a-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS DRUG SENSITIVITY TEST METHOD
	
	public static final String DST_RESULT = "31befcf4-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS DRUG SENSITIVITY TEST RESULT
	
	public static final String INTERMEDIATE_TO_TB_DRUG = "31bef650-0370-102d-b0e3-001ec94a0cc1"; // INDETERMINATE TO TUBERCULOSIS DRUG
	
	public static final String MICROSCOPY_TEST_CONSTRUCT = "4f3a74ea-c5a8-49bc-a983-5410ed506f38"; // MICROSCOPY TEST CONSTRUCT
	
	public static final String OTHER_MYCOBACTERIA_NON_CODED = "31bef9a2-0370-102d-b0e3-001ec94a0cc1"; // OTHER MYCOBACTERIA NON-CODED
	
	public static final String RESISTANT_TO_TB_DRUG = "31b4bce4-0370-102d-b0e3-001ec94a0cc1"; // RESISTANT TO TUBERCULOSIS DRUG
	
	public static final String SUSCEPTIBLE_TO_TB_DRUG = "31bb4a82-0370-102d-b0e3-001ec94a0cc1"; // SUSCEPTIBLE TO TUBERCULOSIS DRUG
	
	public static final String SCANTY = "31bf100e-0370-102d-b0e3-001ec94a0cc1"; // SCANTY
	
	public static final String SPUTUM = "31b24d06-0370-102d-b0e3-001ec94a0cc1"; // SPUTUM
	
	public static final String SPUTUM_COLLECTION_DATE = "31bf080c-0370-102d-b0e3-001ec94a0cc1"; // SPUTUM COLLECTION DATE
	
	public static final String SAMPLE_SOURCE = "31bf065e-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS SAMPLE SOURCE
	
	public static final String SMEAR_CONSTRUCT = "31bf1518-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT
	
	public static final String SMEAR_METHOD = "31bf128e-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS SMEAR MICROSCOPY METHOD
	
	public static final String SMEAR_RESULT = "31bf1446-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS SMEAR RESULT
	
	public static final String TEST_DATE_ORDERED = "8a91ba44-c926-4bdc-802d-f1defe4f4e0f"; // TUBERCULOSIS TEST DATE ORDERED
	
	public static final String TEST_DATE_RECEIVED = "31bf073a-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS TEST DATE RECEIVED
	
	public static final String TEST_RESULT_DATE = "31bf0e60-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS TEST RESULT DATE
	
	public static final String TEST_START_DATE = "31bf030c-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS TEST START DATE
	
	public static final String TYPE_OF_ORGANISM = "31befc22-0370-102d-b0e3-001ec94a0cc1"; // TYPE OF ORGANISM
	
	public static final String TYPE_OF_ORGANISM_NON_CODED = "31bef8d0-0370-102d-b0e3-001ec94a0cc1"; // TYPE OF ORGANISM NON-CODED
	
	public static final String SCANNED_LAB_REPORT = "72d8e563-e141-4a5f-ab2e-2f7e3448bdc5"; // SCANNED LAB REPORT
	
	public static final String SPECIMEN_ID = "985a9b76-de2d-4182-b095-481700ce7c7c"; // TUBERCULOSIS SPECIMEN ID
	
	public static final String SPECIMEN_APPEARANCE = "31bb563a-0370-102d-b0e3-001ec94a0cc1"; // APPEARANCE OF SPECIMEN
	
	public static final String SPECIMEN_COMMENTS = "4a45b687-819f-452c-8b34-33ab6536d017"; // TUBERCULOSIS SPECIMEN COMMENTS
	
	public static final String WAITING_FOR_TEST_RESULTS = "31b9842c-0370-102d-b0e3-001ec94a0cc1"; // WAITING FOR TEST RESULTS
	
	// GeneXpert and HAIN Test
	public static final String GENEXPERT = "d7f6fb0b-241a-4ac8-993b-a2942c549955"; // GENEXPERT
	
	public static final String XPERT_CONSTRUCT = "6a6be4e0-9a56-4376-a8b3-9b6a9ec2d9bf"; // TUBERCULOSIS XPERT TEST CONSTRUCT
	
	public static final String MTB_RESULT = "731bdb67-f216-477f-85c2-8af92d999121"; // MTB RESULT
	
	public static final String RIFAMPICIN_RESULT = "12235c33-e627-4636-8b85-8643fadc622e"; // RIFAMPICIN RESULT
	
	public static final String DETECTED = "b24aeae2-6234-4e5d-95ec-97d03545a425"; // DETECTED
	
	public static final String NOT_DETECTED = "2710ad98-8f02-40a7-b0d4-8d0c6668dfa9"; // NOT DETECTED
	
	public static final String ERROR = "22c701fb-20e1-43c7-acf3-ea2faa7f7ba3"; // ERROR
	
	public static final String ERROR_CODE = "9ad2fd64-6ffb-4310-b1d8-0c129ae05b0f"; // ERROR CODE
	
	public static final String XPERT_MTB_BURDEN = "73ac78a7-6c49-490a-bb6e-ba94bc6d0479"; // XPERT MTB BURDEN
	
	public static final String XPERT_HIGH = "6b51dc02-1c23-419e-89cd-b9fcfd8f68f2"; // HIGH
	
	public static final String XPERT_MEDIUM = "f98e7ca5-058c-4031-887f-297a660b2f7e"; // MEDIUM
	
	public static final String XPERT_LOW = "288d86d6-74db-4260-9700-c9cbe8f92d81"; // LOW
	
	public static final String HAIN_TEST = "a32b1288-3740-4eec-8828-4be4fcc45ce6"; // HAIN TEST
	
	public static final String HAIN_CONSTRUCT = "d587d8cb-6f41-466d-9426-22c712783cd5"; // TUBERCULOSIS HAIN TEST CONSTRUCT
	
	public static final String HAIN2_CONSTRUCT = "3cbafe53-8630-446e-9905-b83c5c6fd04b"; // TUBERCULOSIS HAIN2 TEST CONSTRUCT
	
	public static final String ISONIAZID_RESULT = "9446085c-86ae-4e06-b571-f8a88217b472"; // ISONIAZID RESULT
	
	public static final String FLUOROQUINOLONE_RESULT = "200294f2-8b3e-4fb1-94fa-6755af9ed9c5"; // FLUOROQUINOLONE RESULT
	
	public static final String INJECTABLE_RESISTANCE = "eff9438b-f4c9-4b4c-aa82-1e5adc0fe09c"; // INJECTABLE RESISTANCE
	
	// Lab Results
	public static final String STRONGLY_POSITIVE = "31b4a240-0370-102d-b0e3-001ec94a0cc1"; // STRONGLY POSITIVE
	
	public static final String MODERATELY_POSITIVE = "488b58ff-64f5-4f8a-8979-fa79940b1594"; // MODERATELY POSITIVE
	
	public static final String WEAKLY_POSITIVE = "31b4a312-0370-102d-b0e3-001ec94a0cc1"; // WEAKLY POSITIVE
	
	public static final String POSITIVE = "31b0141e-0370-102d-b0e3-001ec94a0cc1"; // POSITIVE
	
	public static final String NEGATIVE = "31aff3c6-0370-102d-b0e3-001ec94a0cc1"; // NEGATIVE
	
	public static final String CONTAMINATED = "31b4a09c-0370-102d-b0e3-001ec94a0cc1"; // CONTAMINATED
	
	public static final String UNSATISFACTORY_SAMPLE = "31ccc80c-0370-102d-b0e3-001ec94a0cc1"; // UNSATISFACTORY SAMPLE
	
	public static final String LOWAFB = "bfa92f60-d34b-4ae5-acb6-bc7a705ae109"; // LOW AFB
	
	// MDR-TB Classification
	public static final String NEW = "f7b5bf49-cb07-4fca-8c15-93ba92249344"; // NEW
	
	public static final String PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY = "31c2d4be-0370-102d-b0e3-001ec94a0cc1"; // PREVIOUSLY TREATED WITH FIRST LINE DRUGS ONLY
	
	public static final String PREVIOUSLY_TREATED_SECOND_LINE_DRUGS = "31c2d3ec-0370-102d-b0e3-001ec94a0cc1"; // PREVIOUSLY TREATED WITH SECOND LINE DRUGS
	
	public static final String PATIENT_GROUP = "ae16bb6e-3d82-4e14-ab07-2018ee10d311"; // TUBERCULOSIS PATIENT TYPE
	
	public static final String CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE = "31c2d590-0370-102d-b0e3-001ec94a0cc1"; // CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE
	
	public static final String CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT = "31c2d31a-0370-102d-b0e3-001ec94a0cc1"; // CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT
	
	public static final String TREATMENT_AFTER_FAILURE = "TREATMENT AFTER FAILURE";
	
	public static final String TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT = "TREATMENT AFTER FAILURE OF FIRST TREATMENT MDR-TB PATIENT";
	
	public static final String TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT = "TREATMENT AFTER FAILURE OF RE-TREATMENT MDR-TB PATIENT";
	
	public static final String OTHER = "31c9eb78-0370-102d-b0e3-001ec94a0cc1"; // OTHER
	
	public static final String PATIENT_TRANSFERRED_IN = "e58da80f-3adf-4a4d-8a7a-43482f9fa5a5"; // PATIENT TRANSFERRED IN
	
	public static final String CANCELLED = "6ea6a201-0afa-4843-b0b6-212121c64f36"; // DIAGNOSIS CANCELLED
	
	// Custom classifications
	public static final String RELAPSE_AFTER_REGIMEN_1 = "31ce3b38-0370-102d-b0e3-001ec94a0cc1"; // RELAPSE AFTER REGIMEN 1
	
	public static final String RELAPSE_AFTER_REGIMEN_2 = "845257f4-642b-4f67-8c57-d82f8982c83c"; // RELAPSE AFTER REGIMEN 2
	
	public static final String DEFAULT_AFTER_REGIMEN_1 = "31b6b8aa-0370-102d-b0e3-001ec94a0cc1"; // DEFAULT AFTER REGIMEN 1
	
	public static final String DEFAULT_AFTER_REGIMEN_2 = "33741f7e-c104-44e5-a1d8-421c7b391ba5"; // DEFAULT AFTER REGIMEN 2
	
	public static final String FAILURE_AFTER_REGIMEN_1 = "31b5b644-0370-102d-b0e3-001ec94a0cc1"; // AFTER FAILURE REGIMEN 1
	
	public static final String FAILURE_AFTER_REGIMEN_2 = "31b4fb78-0370-102d-b0e3-001ec94a0cc1"; // AFTER FAILURE REGIMEN 2
	
	public static final String MDR_TB = "31c2d176-0370-102d-b0e3-001ec94a0cc1"; // MDR-TB
	
	public static final String XDR_TB = "31c2d09a-0370-102d-b0e3-001ec94a0cc1"; // XDR TB
	
	public static final String SUSPECTED_MDR_TB = "31c2d73e-0370-102d-b0e3-001ec94a0cc1"; // SUSPECTED MULTI-DRUG TUBERCULOSIS
	
	public static final String TB = "31ab3962-0370-102d-b0e3-001ec94a0cc1"; // TUBERCULOSIS
	
	public static final String RR_TB = "d78087db-6146-40d4-9dff-5b249e1b4eca"; // RR-TB
	
	public static final String PDR_TB = "16364087-fc02-485b-90c4-5e988830b031"; // PDR-TB
	
	public static final String PRE_XDR_TB = "9e263164-586f-47a1-824b-a1d205cc51fe"; // PRE-XDR
	
	public static final String MONO = "701b646d-a3e0-4556-9fbc-31d88e788464"; // MONO
	
	public static final String TDR_TB = "a1021d1d-338c-48bc-9f0b-b7c4b0f8ee5a"; // TDR-TB
	
	// Treatment Outcome
	public static final String MDR_TB_TREATMENT_OUTCOME = "31c2c834-0370-102d-b0e3-001ec94a0cc1"; // MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME
	
	public static final String CURED = "31b6bb34-0370-102d-b0e3-001ec94a0cc1"; // CURED
	
	public static final String DEFAULTED = "31b60f40-0370-102d-b0e3-001ec94a0cc1"; // DEFAULTED
	
	public static final String DEATH = "31b6b7d8-0370-102d-b0e3-001ec94a0cc1"; // DEATH
	
	public static final String TREATMENT_FAILED = "31b0e4ac-0370-102d-b0e3-001ec94a0cc1"; // TREATMENT FAILED
	
	public static final String TREATMENT_COMPLETE = "31b69906-0370-102d-b0e3-001ec94a0cc1"; // TREATMENT COMPLETE
	
	public static final String PATIENT_TRANSFERRED_OUT = "31b6b986-0370-102d-b0e3-001ec94a0cc1"; // PATIENT TRANSFERRED OUT
	
	public static final String ON_TREATMENT = "31c1d2e4-0370-102d-b0e3-001ec94a0cc1"; // STILL ON TREATMENT
	
	public static final String TB_TREATMENT_OUTCOME = "a690e0c4-3371-49b3-9d52-b390fca3dd90"; // TUBERCULOSIS TREATMENT OUTCOME
	
	public static final String LOST_TO_FOLLOWUP = "31c7bbdc-0370-102d-b0e3-001ec94a0cc1"; // LOST TO FOLLOW UP
	
	public static final String STARTED_SLD_TREATMENT = "33e55aee-a168-4082-9353-f8ca87dfe662"; // Started SLD Treatment
	
	public static final String TREATMENT_OUTCOME_DATE = "5060d5ce-df8e-4090-b09e-62e40a29201a"; // TREATMENT OUTCOME DATE
	
	// TB Type
	public static final String PULMONARY_TB = "31b6002c-0370-102d-b0e3-001ec94a0cc1"; // PULMONARY TUBERCULOSIS
	
	public static final String EXTRA_PULMONARY_TB = "31b5fe7e-0370-102d-b0e3-001ec94a0cc1"; // EXTRA-PULMONARY TUBERCULOSIS
	
	public static final String ANATOMICAL_SITE_OF_TB = "31b4c61c-0370-102d-b0e3-001ec94a0cc1"; // SITE OF TB DISEASE
	
	// Antiretrovirals (for HIV status section and HIV regimens)
	public static final String ANTIRETROVIRALS = "31b2955e-0370-102d-b0e3-001ec94a0cc1"; // ANTIRETROVIRAL DRUGS
	
	public static final String REASON_HIV_TREATMENT_STOPPED = "31b3d3b0-0370-102d-b0e3-001ec94a0cc1"; // REASON ANTIRETROVIRALS CHANGED OR STOPPED
	
	// HIV Co-infection
	public static final String COINFECTED_ARVS = "31c2cfc8-0370-102d-b0e3-001ec94a0cc1"; // COINFECTED AND ON ANTIRETROVIRALS
	
	public static final String CD4_COUNT = "31c94434-0370-102d-b0e3-001ec94a0cc1"; // CD4 COUNT
	
	public static final String RESULT_OF_HIV_TEST = "31b94ef8-0370-102d-b0e3-001ec94a0cc1"; // RESULT OF HIV TEST
	
	public static final String DATE_OF_HIV_TEST = "a8f2eacc-2c36-4ddd-b3b8-d80ecfb27bf3"; // DATE OF HIV TEST
	
	public static final String DATE_OF_ART_TREATMENT_START = "767fed8f-3e64-4567-a2c6-258444296787"; // DATE OF ART TREATMENT START
	
	public static final String DATE_OF_PCT_TREATMENT_START = "3458f801-1532-4055-b7f5-f3adf90ec7c7"; // DATE OF PCT TREATMENT START
	
	// Hospitalization states
	public static final String HOSPITALIZATION_WORKFLOW = "d3d3e1a7-7230-4085-af56-690977a479e5"; // HOSPITALIZATION WORKFLOW
	
	public static final String PATIENT_HOSPITALIZED = "31c095fa-0370-102d-b0e3-001ec94a0cc1"; // PATIENT HOSPITALIZED
	
	public static final String AMBULATORY = "31b66f1c-0370-102d-b0e3-001ec94a0cc1"; // AMBULATORY // legacy, has been retired
	
	// Other
	public static final String UNKNOWN = "cf82933b-3f3f-45e7-a5ab-5d31aaee3da3"; // UNKNOWN
	
	public static final String CLINICIAN_NOTES = "31b474e6-0370-102d-b0e3-001ec94a0cc1"; // CLINICIAN NOTES
	
	public static final String RETURN_VISIT_DATE = "31c6554e-0370-102d-b0e3-001ec94a0cc1"; // RETURN VISIT DATE
	
	public static final String TELEPHONE_NUMBER = "31b4b064-0370-102d-b0e3-001ec94a0cc1"; // TELEPHONE NUMBER
	
	public static final String NONE = "31b2a94a-0370-102d-b0e3-001ec94a0cc1"; // NONE
	
	// Legacy (only used by migration controller)
	public static final String CULTURE_STATUS = "31c2c5fa-0370-102d-b0e3-001ec94a0cc1"; // MULTI-DRUG RESISTANT TUBERCULOSIS CULTURE STATUS
	
	// Custom concepts for Tajikistan
	public static final String PREGNANT = "6462d5a2-0a83-4bad-98f8-95b36445dbb0"; // PREGNANT
	
	public static final String FIRST_LINE_DRUGS = "cb0f30f5-15d3-4eb4-b67e-0416cf940d88"; // FIRST LINE DRUGS
	
	public static final String SECOND_LINE_DRUGS = "8e6207d7-7ddc-4a2d-bf10-2f134e69e869"; // SECOND LINE DRUGS
	
	public static final String TEST_REFERRAL = "4f9b9703-95bf-49a3-b3f4-a10445f64c59"; // TEST REFERRAL
	
	public static final String MONTH_OF_TREATMENT = "0977d2a9-84a4-40dc-95b3-30d7c709cd92"; // MONTH OF TREATMENT
	
	public static final String CAUSE_OF_DEATH = "0f7abf6d-e0bb-46ce-aa69-5214b0d2a295"; // CAUSE OF DEATH
	
	public static final String DEATH_BY_TB = "61d7b45a-9d0f-4e78-a960-609e9a43ada1"; // DEATH BY TB
	
	public static final String DEATH_BY_TBHIV = "9e6bbc35-6d6e-4014-ac41-5152fd22c45f"; // DEATH BY TB/HIV
	
	public static final String DEATH_BY_OTHER_DISEASES = "7f3eda2c-9bcb-425e-bce4-30edd972a5a4"; // DEATH BY OTHER DISEASES
	
	public static final String AGE_AT_MDR_REGISTRATION = "b8135923-db22-4db5-b8c8-7d31a02b4cd3"; // AGE AT MDR REGISTRATION
	
	public static final String MDR_TREATMENT_START_DATE = "8abe1e01-f167-4618-9f8e-e21ac3dcdf14"; // DATE OF MDR TREATMENT START
	
	public static final String RESISTANCE_TYPE = "3f5a6930-5ead-4880-80ce-6ab79f4f6cb1"; // RESISTANCE TYPE
	
	public static final String TUBERCULOSIS_PATIENT_CATEGORY = "ebde5ed8-4717-472d-9172-599af069e94d"; // TUBERCULOSIS PATIENT CATEGORY
	
	public static final String REGIMEN_2_STANDARD = "88fbaf49-b1ba-4ca7-b96a-ef632f9b1ffb"; // REGIMEN 2 STANDARD
	
	public static final String REGIMEN_2_SHORT = "066ab5fd-55b1-4c42-84b4-21bb263f6eae"; // REGIMEN 2 SHORT
	
	public static final String REGIMEN_2_INDIVIDUALIZED = "368b276c-908a-421d-b76d-6460ef8d48aa"; // REGIMEN 2 INDIVIDUALIZED
	
	public static final String MDR_STATUS = "69abc246-13a9-4cbf-92be-83ac59a8938c"; // MDR-TB STATUS
	
	public static final String TREATMENT_LOCATION = "37b2cf33-aa3d-4638-95e1-2f886d7eb06c"; // TREATMENT LOCATION
	
	public static final String DATE_OF_MDR_CONFIRMATION = "c029cb31-867b-4e2e-b75d-e72ee584524a"; // DATE OF MDR CONFIRMATION
	
	public static final String RELAPSED = "53aaa4c7-49c0-4716-9af0-9098d35bacc5"; // PATIENT RELAPSED
	
	public static final String RELAPSE_MONTH = "79dff4a3-4093-4df6-8547-8a85e2569d4c"; // RELAPSE MONTH
	
	public static final String REGIMEN_2_REG_NUMBER = "6d79af1d-ae0c-4e68-8e0f-6cc84c4a9106"; // REGIMEN 2 REG NUMBER
	
	public static final String PATIENT_PROGRAM_ID = "97481839-6e94-40af-a98e-1bb01a285e54"; // PATIENT PROGRAM ID
	
	public static final String MDR_TB_PROGRAM = "31bd79ec-0370-102d-b0e3-001ec94a0cc1"; // MDR-TB PROGRAM
	
	public static final String DOTS_PROGRAM = "61b552ac-acd8-493f-84de-77d60bd2196f"; // DOTS PROGRAM
	
	public static final String FUNDING_SOURCE = "e56514ed-1b3b-4d2e-89f1-564fd6265ebe"; // FUNDING SOURCE
	
	public static final String PROJECT_HOPE = "5b70ffe6-3ed1-48f5-9793-d397e69780f7"; // PROJECT HOPE
	
	public static final String MSF = "010106ed-ee90-4d81-9b09-d5c274bd8157"; // MSF
	
	public static final String CM_DOSE = "59e4cba5-2343-482d-b08e-bb8b84890839"; // CAPREOMYCIN DOSE
	
	public static final String AM_DOSE = "f265c34d-614c-4af4-b279-fe885eafc6f4"; // AMIKACIN DOSE
	
	public static final String MFX_DOSE = "9e809189-f7b0-424a-a018-f6dbd5bf1127"; // MOXIFLOXACIN DOSE
	
	public static final String LFX_DOSE = "e5266c3f-e07e-4502-9e2b-892b00383ff9"; // LEVOFLOXACIN DOSE
	
	public static final String PTO_DOSE = "645117db-d6be-4dd9-b2d5-21c5fbf9249e"; // PROTHIONAMIDE DOSE
	
	public static final String CS_DOSE = "5be32a1d-295c-44d5-9f86-ec28e8e49777"; // CYCLOSERINE DOSE
	
	public static final String PAS_DOSE = "c3bd56cf-ac50-4d10-b6ac-3f4314cf0077"; // P-AMINOSALICYLIC DOSE
	
	public static final String Z_DOSE = "d7c20112-06f3-46dc-8bd5-ac271be894aa"; // PYRAZINAMIDE DOSE
	
	public static final String E_DOSE = "a28051b7-303c-40a8-a09f-137a40b50696"; // ETHAMBUTOL DOSE
	
	public static final String H_DOSE = "c20ff49c-06f8-4ce6-b5ff-5ec3cdd4a436"; // ISONIAZID DOSE
	
	public static final String LZD_DOSE = "4148d306-41df-4c3d-acad-61874fe84594"; // LINEZOLID DOSE
	
	public static final String CFZ_DOSE = "acb52757-8d76-45c5-97f9-e50cc28b2088"; // CLOFAZAMINE DOSE
	
	public static final String BDQ_DOSE = "46f5877e-b35a-4329-9842-c8e4d236223b"; // BEDAQUILINE DOSE
	
	public static final String DLM_DOSE = "0464131c-5758-4bcc-9c60-f9bd84aa9738"; // DELAMANID DOSE
	
	public static final String IMP_DOSE = "fef19b50-283b-437d-902f-7d1c5977ee4e"; // IMIPENEM DOSE
	
	public static final String AMX_DOSE = "78448ebb-79eb-41e0-a9fa-8f801da2b5ff"; // AMOXICLAV DOSE
	
	public static final String HR_DOSE = "3d9d4903-35fe-4dd3-be83-abe7fec6106c"; // HR DOSE
	
	public static final String HRZE_DOSE = "5eec397a-c366-4cf0-8f1d-d355e068d4b6"; // HRZE DOSE
	
	public static final String S_DOSE = "6f81017a-6454-4077-a830-17b6643a7b62"; // STREPTOMYCIN DOSE
	
	public static final String OTHER_DRUG_1_DOSE = "968b15e0-1c70-4347-af4c-7e8d9b374d90"; // OTHER DRUG 1 DOSE
	
	public static final String OTHER_DRUG_1_NAME = "975233be-8f88-417b-a54c-1dceedea6737"; // OTHER DRUG 1 NAME
	
	public static final String OTHER_DRUG_2_DOSE = "0cdd27ce-c469-466b-9d6e-0f1662ddb6e3"; // OTHER DRUG 2 DOSE
	
	public static final String SHORT_MDR_REGIMEN = "30eece8f-ed94-4241-b89c-2449bede927e"; // SHORT MDR REGIMEN
	
	public static final String STANDARD_MDR_REGIMEN = "c44661eb-3641-45c1-9fb2-f7ca87adf617"; // STANDARD MDR REGIMEN
	
	public static final String INDIVIDUAL_WITH_BEDAQUILINE = "c2358f1f-1d96-497a-849a-9f678b72c657"; // INDIVIDUAL WITH BEDAQUILINE
	
	public static final String INDIVIDUAL_WITH_DELAMANID = "7531b943-418c-4234-9f95-d18d1fccaa36"; // INDIVIDUAL WITH DELAMANID
	
	public static final String INDIVIDUAL_WITH_BEDAQUILINE_AND_DELAMANID = "dcf5702f-b9f7-4e0c-a502-884c6d5bdc5c"; // INDIVIDUAL WITH BEDAQUILINE AND DELAMANID
	
	public static final String INDIVIDUAL_WITH_CLOFAZIMIN_AND_LINEZOLID = "8236b21e-6504-4378-b95b-53f296d77a0d"; // INDIVIDUAL WITH CLOFAZIMIN AND LINEZOLID
	
	public static final String OTHER_MDRTB_REGIMEN = "79838176-04c9-419e-bf75-3658c220ff34"; // OTHER MDR-TB REGIMEN
	
	public static final String SLD_TREATMENT_REGIMEN = "f64d7ce4-c3b9-43a3-9614-308f1723e8f6"; // SLD TREATMENT REGIMEN
	
	public static final String SLD_REGIMEN_TYPE = "cd9e240f-7860-4e37-a0d2-b922cbcc62d3"; // SLD REGIMEN TYPE
	
	public static final String ADVERSE_EVENT = "7047f880-b929-42fc-81f7-b9dbba2d1b15"; // ADVERSE EVENT
	
	public static final String NAUSEA = "fc8c0da8-b043-4406-8758-5b8e68cd815f"; // NAUSEA
	
	public static final String DIARRHOEA = "2e4ee9a7-b433-43ab-a489-74b4bb5722d0"; // DIARRHOEA
	
	public static final String ARTHALGIA = "e5380772-f046-4976-86c6-956d8b8ab863"; // ARTHALGIA
	
	public static final String DIZZINESS = "8a4f3544-015e-4f96-aa49-16857195c34e"; // DIZZINESS
	
	public static final String HEADACHE = "f00389e2-c2c7-4447-b3b3-495f91f8a7af"; // HEADACHE
	
	public static final String SLEEP_DISTURBANCES = "720d9a37-63da-4974-8248-e208e0532859"; // SLEEP DISTURBANCES
	
	public static final String ELECTROLYTE_DISTURBANCES = "bf3810c5-13ed-44e2-befa-c71cbe3ab8bb"; // ELECTROLYTE DISTURBANCES
	
	public static final String ABDOMINAL_PAIN = "d74a3ee5-db18-41dc-88dc-452c32d8207f"; // ABDOMINAL PAIN
	
	public static final String ANOREXIA = "bce751d4-4e80-4cf2-bb0d-28df2e23ff6b"; // ANOREXIA
	
	public static final String GASTRITIS = "c8aff11d-7379-430f-bb35-671618327db5"; // GASTRITIS
	
	public static final String PERIPHERAL_NEUROPATHY = "463748a7-b7aa-4d94-8ef6-55b6d772064b"; // PERIPHERAL NEUROPATHY
	
	public static final String DEPRESSION = "f8ca1fed-e3de-4b0e-a88a-5c6e5aa5ddcd"; // DEPRESSION
	
	public static final String TINNITUS = "7b45cd6d-cfc9-43af-aeb1-c83e9858da8f"; // TINNITUS
	
	public static final String ALLERGIC_REACTION = "4a3cf15c-5794-40d7-ab34-093f67dcf33e"; // ALLERGIC REACTION
	
	public static final String RASH = "3f10519e-0803-44a9-ad95-b208b17e7d2d"; // RASH
	
	public static final String VISUAL_DISTURBANCES = "a59b6f82-f287-4edc-80d3-f4cb4cdedbba"; // VISUAL DISTURBANCES
	
	public static final String SEIZURES = "dab8309d-e9c9-4ac4-ac71-03a7b766c327"; // SEIZURES
	
	public static final String HYPOTHYROIDISM = "70331522-de68-4a8f-aa20-e08d006553ff"; // HYPOTHYROIDISM
	
	public static final String PSYCHOSIS = "3d2ef7bf-2728-4690-adda-13e62fe9b60f"; // PSYCHOSIS
	
	public static final String SUICIDAL_IDEATION = "e67968ce-054a-48aa-a8b5-6293d471a9a2"; // SUICIDAL IDEATION
	
	public static final String HEPATITIS_AE = "f2f2e18f-174c-4c42-b7bc-99e85ab2e02d"; // HEPATITIS (HEPATOTOXICITY)
	
	public static final String RENAL_FAILURE = "652c001f-d6ad-4c3d-a052-f1169d76b30c"; // RENAL FAILURE
	
	public static final String QT_PROLONGATION = "820fcc7d-e70a-4bbd-887c-ed3191a37ca1"; // QT PROLONGATION
	
	public static final String LAB_TEST_CONFIRMING_AE = "a2297d1c-9b74-41e9-92b6-fd5af8d5421e"; // LAB TEST CONFIRMING ADVERSE EVENT
	
	public static final String CLINICAL_SCREEN = "0cd36a99-8d39-45ec-ad7f-51313c77cdfc"; // CLINICAL SCREEN
	
	public static final String VISUAL_ACUITY = "a9595796-5e18-40ca-ba0d-ace42a3eb255"; // VISUAL ACUITY
	
	public static final String SIMPLE_HEARING_TEST = "0b89f97e-ceee-4782-8fdc-88bf4d8449a3"; // SIMPLE HEARING TEST
	
	public static final String AUDIOGRAM = "820eea06-1421-4df3-97b1-5e21639532e8"; // AUDIOGRAM
	
	public static final String NEURO_AND_PSYCHIATRIC_INVESTIGATION = "1f62f318-6ad3-46a8-a616-7b2d2f1de072"; // NEURO AND PSYCHIATRIC INVESTIGATION
	
	public static final String SERUM_CREATININE = "c7efbf6e-581e-4401-8e00-43fafb6cd8a7"; // SERUM CREATININE
	
	public static final String ALT = "ALT";
	
	public static final String AST = "AST";
	
	public static final String BILIRUBIN = "62c01166-6f2e-4c75-abd7-2c56eb31c261"; // BILIRUBIN
	
	public static final String ALKALINE_PHOSPHATASE = "3fd62922-5168-4589-b4ca-e365a2f99166"; // ALKALINE PHOSPHATASE
	
	public static final String YGT = "19842565-9739-4535-aa40-d6f36aa201c1"; // YGT
	
	public static final String ECG = "b59320fc-812f-4a9c-885f-d1547296440d"; // ECG
	
	public static final String LIPASE = "e93ef8cd-2d50-4c9f-9da4-363c739487d4"; // LIPASE
	
	public static final String AMYLASE = "82247581-c869-4815-a6da-5621b72851a7"; // AMYLASE
	
	public static final String POTASSIUM = "46c1bc26-dd2e-4eac-bae9-5d18d315b528"; // POTASSIUM
	
	public static final String MAGNESIUM = "af286828-eb90-46b1-9a91-9e757aa6869e"; // MAGNESIUM
	
	public static final String CALCIUM = "7b0a3c39-6fd9-4e12-9286-2cf6ecb920fd"; // CALCIUM
	
	public static final String ALBUMIN = "1822a750-996b-462b-9096-972738764c77"; // ALBUMIN
	
	public static final String CBC = "0637b682-c64e-4f35-8271-60f99c16e83d"; // COMPLETE BLOOD COUNT
	
	public static final String BLOOD_GLUCOSE = "aa73ce99-35f2-478f-9b6a-5b3740194143"; // BLOOD GLUCOSE
	
	public static final String THYROID_TEST = "ccea080b-2bbe-415c-bd20-0d40033f4264"; // THYROID TEST
	
	public static final String CLINICAL_SCREEN_DONE = "dd3142e4-237b-46e0-a5fe-7e5c68ee1dfe"; // CLINICAL SCREEN DONE
	
	public static final String VISUAL_ACUITY_DONE = "beb57526-334c-426a-af9b-9082be53f030"; // VISUAL ACUITY DONE
	
	public static final String SIMPLE_HEARING_TEST_DONE = "dc585a17-bfd5-4b44-aa7b-ff326abf45d0"; // SIMPLE HEARING TEST DONE
	
	public static final String AUDIOGRAM_DONE = "0e431270-5f1e-4b2e-ad08-bb15909ecd3c"; // AUDIOGRAM DONE
	
	public static final String NEURO_INVESTIGATION_DONE = "cb58ac47-7e2e-482d-b1ca-8f04641c9ad9"; // NEURO INVESTIGATION DONE
	
	public static final String CREATNINE_DONE = "1bccf186-0695-4fed-8dd4-937a2660c927"; // CREATININE DONE
	
	public static final String ALT_DONE = "7fea355e-0a14-4c64-af2f-4b6b96a7823d"; // ALT DONE
	
	public static final String AST_DONE = "076bd99e-869b-4253-a2fa-5c3697227615"; // AST DONE
	
	public static final String BILIRUBIN_DONE = "515f6786-1505-44a2-b216-2e22f31ab992"; // BILIRUBIN DONE
	
	public static final String ALKALINE_PHOSPHATASE_DONE = "56edff77-1933-4e62-845f-8399e3ade466"; // ALKALINE PHOSPHATASE DONE
	
	public static final String YGT_DONE = "a667469d-5eb1-470d-ad93-e37086f187af"; // YGT DONE
	
	public static final String ECG_DONE = "2d5df3ac-636f-4ad5-98c3-e4c7cf6f4782"; // ECG DONE
	
	public static final String LIPASE_DONE = "f8e5f193-0b6e-4ff3-91c8-84a3e06f7f75"; // LIPASE DONE
	
	public static final String AMYLASE_DONE = "99c62c8f-0885-401e-8aeb-8e41dd9f4a0b"; // AMYLASE DONE
	
	public static final String POTASSIUM_DONE = "c8328edf-30eb-429a-8c95-3713ebf8cd5f"; // POTASSIUM DONE
	
	public static final String MAGNESIUM_DONE = "6ef1bf66-4143-4290-8cf1-ae4d7a3aa911"; // MAGNESIUM DONE
	
	public static final String CALCIUM_DONE = "315531c6-bd48-4789-9c34-8a52d5f85f6a"; // CALCIUM DONE
	
	public static final String ALBUMIN_DONE = "a5228f9e-aaef-4835-8413-f6c4e89063aa"; // ALBUMIN DONE
	
	public static final String CBC_DONE = "09e6fee5-8d20-4fb8-b636-7e92335fa3b2"; // CBC DONE
	
	public static final String BLOOD_GLUCOSE_DONE = "3cee56ec-4841-43e3-a7fd-afdd4d1fea2e"; // BLOOD GLUCOSE DONE
	
	public static final String THYROID_TEST_DONE = "db064f00-5f8f-447f-ae4c-ccf46aabea1e"; // THYROID TEST DONE
	
	public static final String OTHER_TEST_DONE = "d3cadad5-bdd6-4a77-abc5-c752d9a56234"; // OTHER TEST DONE
	
	public static final String ADVERSE_EVENT_REGIMEN = "4fd9f2ad-0515-41c9-a2e5-468b315bc707"; // ADVERSE EVENT REGIMEN
	
	public static final String ADVERSE_EVENT_TYPE = "1051a25f-5609-40d1-9801-10c3b6fd74ab"; // ADVERSE EVENT TYPE
	
	public static final String SERIOUS = "937e3189-ebcb-4deb-a25b-836ef83b2727"; // SERIOUS
	
	public static final String OF_SPECIAL_INTEREST = "afd112d1-e7e8-4e21-81fa-f00490b4e0ef"; // OF SPECIAL INTEREST
	
	public static final String SAE_TYPE = "e31fb77b-3623-4c65-ac86-760a2248fc1b"; // SAE TYPE
	
	//public static final String HOSPITALIZATION = "HOSPITALIZATION";
	public static final String DISABILITY = "d6095301-08c1-4709-88b2-d6bfa30933cb"; // DISABILITY
	
	public static final String CONGENITAL_ANOMALY = "967d38cd-e0ff-4bfa-96d6-8fec245ff7af"; // CONGENITAL ANOMALY
	
	public static final String LIFE_THREATENING_EXPERIENCE = "75ad6edd-1f7f-409e-8554-616f46cd939a"; // LIFE THREATENING EXPERIENCE
	
	public static final String SPECIAL_INTEREST_EVENT_TYPE = "aa9cb2d0-a6d6-4fb7-bb02-9298235128b2"; // SPECIAL INTEREST EVENT TYPE
	
	public static final String MYELOSUPPRESSION = "fcf62a0c-3481-49f1-b2b0-78b7d5e86123"; // MYELOSUPPRESSION
	
	public static final String LACTIC_ACIDOSIS = "4b906909-58f6-4582-b092-474bacace127"; // LACTIC ACIDOSIS
	
	public static final String HYPOKALEMIA = "57a2bfde-d9ad-43aa-ba68-00858a0d83ae"; // HYPOKALAEMIA
	
	public static final String PANCREATITIS = "1af546ef-c7c3-437c-8dac-70b80e51a2d6"; // PANCREATITIS
	
	public static final String PHOSPHOLIPIDOSIS = "2598dc29-2e4b-49fb-bec6-db4cb6cde68f"; // PHOSPHOLIPIDOSIS
	
	public static final String YELLOW_CARD_DATE = "8d9a0671-a39c-4592-861d-2184531fbd8c"; // YELLOW CARD DATE
	
	public static final String SUSPECTED_DRUG = "1975615d-78d2-4a66-9c13-b513939c6160"; // SUSPECTED DRUG
	
	public static final String CAUSALITY_ASSESSMENT_RESULT_1 = "0d5228b4-5891-4740-9b13-1b8898ba4957"; // CAUSALITY ASSESSMENT RESULT 1
	
	public static final String CAUSALITY_ASSESSMENT_RESULT_2 = "dfad56a0-6f69-437b-bc50-28195039a9e2"; // CAUSALITY ASSESSMENT RESULT 2
	
	public static final String CAUSALITY_ASSESSMENT_RESULT_3 = "a04a5e94-a584-4f03-b0c5-ea7acf0a28d7"; // CAUSALITY ASSESSMENT RESULT 3
	
	public static final String CAUSALITY_DRUG_1 = "aaeb7e1e-e2ea-445d-8c86-6d5eff7d45ad"; // CAUSALITY DRUG 1
	
	public static final String CAUSALITY_DRUG_2 = "d5383d2c-ad69-489e-983d-938bc5356ecf"; // CAUSALITY DRUG 2
	
	public static final String CAUSALITY_DRUG_3 = "34c3a6f2-adf4-4c1a-9e47-7fd9dc4f093d"; // CAUSALITY DRUG 3
	
	public static final String DEFINITE = "4274a6a6-743e-4853-bce0-ab617f83bcf2"; // DEFINITE
	
	public static final String PROBABLE = "ba164852-9869-45e9-bdfc-7c773724ccab"; // PROBABLE
	
	public static final String POSSIBLE = "8b7e59d1-1db2-4a46-91ed-93a04243d94e"; // POSSIBLE
	
	public static final String SUSPECTED = "15a2d25c-45f9-4ba8-a185-5c7a5769cde6"; // SUSPECTED
	
	public static final String NOT_CLASSIFIED = "e81f9e85-995d-4ffa-a0da-edc10fe4a327"; // NOT CLASSIFIED
	
	public static final String ADVERSE_EVENT_ACTION = "d9cabe01-9c29-45b0-a071-0cd8d80fcb41"; // ADVERSE EVENT ACTION
	
	public static final String ADVERSE_EVENT_ACTION_2 = "7e97054b-cf92-49ec-9f68-54b095f5436e"; // ADVERSE EVENT ACTION 2
	
	public static final String ADVERSE_EVENT_ACTION_3 = "caa95b8f-86c3-4ec4-9397-933d880aba3e"; // ADVERSE EVENT ACTION 3
	
	public static final String ADVERSE_EVENT_ACTION_4 = "1d70fdcd-915d-4524-9ab5-1bdda1790508"; // ADVERSE EVENT ACTION 4
	
	public static final String ADVERSE_EVENT_ACTION_5 = "c0592626-62a8-48f6-93b0-1e4f8ff671f3"; // ADVERSE EVENT ACTION 5
	
	public static final String DOSE_NOT_CHANGED = "fd591b88-4071-4d68-8f5b-d9802036d877"; // DOSE NOT CHANGED
	
	public static final String DOSE_REDUCED = "a0ee5172-4912-40a0-846d-7aed4de3c6c2"; // DOSE REDUCED
	
	public static final String DRUG_INTERRUPTED = "dfcd8657-9c6c-401c-b0fa-a12506ae5971"; // DRUG INTERRUPTED
	
	public static final String DRUG_WITHDRAWN = "977f5abb-7cef-4344-a77d-cbde8461732f"; // DRUG WITHDRAWN
	
	public static final String ANCILLARY_DRUG_GIVEN = "633442c7-b32a-4681-8cf1-0bfe211fed2b"; // ANCILLARY DRUG GIVEN
	
	public static final String ADDITIONAL_EXAMINATION = "b2ff9394-9d71-453e-adc2-e53d0b91d2bc"; // ADDITIONAL EXAMINATION
	
	public static final String REQUIRES_ANCILLARY_DRUGS = "0b056d73-92e6-4d78-9948-5f14aa397127"; // REQUIRES ANCILLARY DRUGS
	
	public static final String REQUIRES_DOSE_CHANGE = "ac6e1e3c-f299-4cfb-bd9e-4c9679702db9"; // REQUIRES DOSE CHANGE
	
	public static final String ADVERSE_EVENT_OUTCOME = "adbed9a8-29a6-4adb-8a5b-60619fa02c19"; // ADVERSE EVENT OUTCOME
	
	public static final String RESOLVED = "417f1481-2783-4b70-9bba-6d8dc4d873e0"; // RESOLVED
	
	public static final String RESOLVED_WITH_SEQUELAE = "41b0f660-75ee-4d5a-b6a6-412e89330793"; // RESOLVED WITH SEQUELAE
	
	public static final String FATAL = "43897bbf-1a8d-4b5b-aa9b-8d8380e912c0"; // FATAL
	
	public static final String RESOLVING = "76fe4243-bac0-4a7b-8799-acdfe6affa13"; // RESOLVING
	
	public static final String NOT_RESOLVED = "97db90d6-c5f9-46b5-bae0-dfe8368673ae"; // NOT RESOLVED
	
	public static final String ADVERSE_EVENT_OUTCOME_DATE = "2e418adc-f3f2-4d49-bc4d-dcbe82dc5a17"; // ADVERSE EVENT OUTCOME DATE
	
	public static final String DRUG_RECHALLENGE = "dead117a-53c3-40f8-879b-40c170b68037"; // DRUG RECHALLENGE
	
	public static final String NO_RECHALLENGE = "82bf4caa-944a-4024-ab46-69aca67591ab"; // NO RECHALLENGE
	
	public static final String RECURRENCE_OF_EVENT = "0400d1fc-85b4-4808-89bf-cc2c9b88a3e3"; // RECURRENCE OF EVENT
	
	public static final String NO_RECURRENCE = "007798ee-ffeb-4191-8c58-16df6c125440"; // NO RECURRENCE
	
	public static final String UNKNOWN_RESULT = "be74692c-bc08-416c-8fac-d35a0b798605"; // UNKNOWN RESULT
	
	@Deprecated
	public static final String MEDDRA_CODE = "c213828e-3d30-46a4-9245-485c9f78c233"; // MEDDRA CODE
	
	public static final String SKIN_DISORDER = "a91b1a4e-c90f-446d-bcd2-4afcaff8ee3d"; // SKIN DISORDER
	
	public static final String MUSCULOSKELETAL_DISORDER = "a6a1098d-5064-4a25-ba81-a1b8cd913e28"; // MUSCULOSKELETAL DISORDER
	
	public static final String NEUROLOGICAL_DISORDER = "79bea1b6-8d57-4839-9456-8abf5ea058f6"; // NEUROLOGICAL DISORDER
	
	public static final String VISION_DISORDER = "959e1093-4b1d-4151-9089-9df3e284cbc1"; // VISION DISORDER
	
	public static final String HEARING_DISORDER = "c6e5327e-2ad5-4813-a2f2-107b13b8101b"; // HEARING DISORDER
	
	public static final String PSYCHIATRIC_DISORDER = "16059f2a-45ca-4208-8e0c-a8417746c47e"; // PSYCHIATRIC DISORDER
	
	public static final String GASTROINTESTINAL_DISORDER = "705ea4ff-9780-4865-928f-b651ea0cacbb"; // GASTROINTESTINAL DISORDER
	
	public static final String LIVER_DISORDER = "ffcb4db7-ba65-42fe-990a-8813564544f9"; // LIVER DISORDER
	
	public static final String METABOLIC_DISORDER = "4130c905-fde5-41bb-b363-8ef9551c8202"; // METABOLIC DISORDER
	
	public static final String ENDOCRINE_DISORDER = "75399647-a578-43af-81fa-b83dbaeb991e"; // ENDOCRINE DISORDER
	
	public static final String CARDIAC_DISORDER = "a2fd59aa-a304-490a-a496-4ec021b41a16"; // CARDIAC DISORDER
	
	// For DOTS Reports
	public static final String TB_CLINICAL_DIAGNOSIS = "e2e8dc4d-9ad2-45d4-9ae4-411388b725d3"; // TB CLINICAL DIAGNOSIS
	
	public static final String DOTS_TREATMENT_START_DATE = "7b1d2c97-de39-4f98-b896-b6c3e78cba1e"; // DATE OF DOTS TREATMENT START
	
	public static final String AGE_AT_DOTS_REGISTRATION = "b84fabbc-d296-4efa-8629-e393fb27e21f"; // AGE AT DOTS REGISTRATION
	
	public static final String TREATMENT_CENTER_FOR_IP = "ddf6e09c-f018-4048-a69f-436ff22308b5"; // TREATMENT CENTER FOR IP
	
	public static final String TREATMENT_CENTER_FOR_CP = "2cd70c1e-955d-428e-86cd-3efc5ecbcabd"; // TREATMENT CENTER FOR CP
	
	public static final String REGIMEN_1_NEW = "75d80cad-5279-4d78-9410-10001a29ae21"; // Regimen I - 6 months -2HRZE/4HR
	
	public static final String REGIMEN_1_RETREATMENT = "8b898a9c-e7a0-4cac-906c-9264be4e5fbf"; // REGIMEN 1 RETREATMENT
	
	public static final String DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME = "3d2e4053-26d7-4f86-b88a-93011ae1725f"; // DATE OF DEATH AFTER TREATMENT OUTCOME
	
	public static final String TB03_REGISTRATION_NUMBER = "ebd3205a-7ba6-44f8-8a31-3b14e3786fbc"; // REGISTRATION NUMBER ON TB03
	
	public static final String TB03_REGISTRATION_YEAR = "1ea8b390-bc3b-4dd0-9c27-8acc5177e217"; // YEAR OF REGISTRATION ON TB03
	
	public static final String LOCATION_TYPE = "290750ca-3d80-43b5-adbd-d6c12692572a"; // LOCATION TYPE
	
	public static final String PROFESSION = "1304ac7c-7acb-4df9-864d-fc911fc00028"; // PROFESSION
	
	public static final String POPULATION_CATEGORY = "955fa978-f0a6-4252-bd6d-22b16fba3c1e"; // POPULATION CATEGORY
	
	public static final String PLACE_OF_DETECTION = "e2a0dc12-9af9-4b2d-9b4f-d89463021560"; // PLACE OF DETECTION
	
	public static final String DATE_FIRST_SEEKING_HELP = "390a1b51-71f9-4fb8-b053-f01f4d06dbb6"; // DATE FIRST SEEKING HELP
	
	public static final String CIRCUMSTANCES_OF_DETECTION = "3d16500c-87be-431a-93e6-d517906bd20a"; // CIRCUMSTANCES OF DETECTION
	
	public static final String METHOD_OF_DETECTION = "207a0630-f0af-4208-9a81-326b8c37ebe2"; // METHOD OF DETECTION
	
	public static final String SITE_OF_EPTB = "06cd622c-ba03-45ec-a65f-96536a14aece"; // SITE OF EPTB
	
	public static final String LOCATION_OF_EPTB = "dfcaa03e-6d07-11ee-af2b-e86a64440f18"; // LOCATION OF EXTRA PULMONARY TB
	
	public static final String LOCATION_OF_PTB = "1ffd4121-4f4d-4016-b420-e8fe89c81c84"; // LOCATION OF PULMONARY TB
	
	public static final String PRESENCE_OF_DECAY = "cadd16ac-d717-4503-98de-794a1f26c219"; // PRESENCE OF DECAY
	
	public static final String DATE_OF_DECAY_SURVEY = "ffc5a747-4344-4327-ba66-46297d8c8ba4"; // DATE OF DECAY SURVEY
	
	public static final String DIABETES = "d1ee9e1a-c2a3-4548-86b5-49fe6268d45b"; // DIABETES
	
	public static final String CNSDL = "e564a30c-3c9f-464e-b3b7-20509a7f2b72"; // CNSDL
	
	public static final String HYPERTENSION_OR_HEART_DISEASE = "4fb94e2e-f366-451d-b5d7-29c4055b219b"; // HYPERTENSION OR HEART DISEASE
	
	public static final String ULCER = "41e61c2b-fcf6-4786-bb3c-6d05e57633d6"; // ULCER OF STOMACH OR DUODENUM
	
	public static final String MENTAL_DISORDER = "b60ced1d-e098-4da9-82ab-05b9e818096f"; // MENTAL DISEASE
	
	public static final String ICD20 = "75d2b8e3-8976-49f9-a6b3-dfe87c86b415"; // ICD B20.9
	
	public static final String CANCER = "25eead5d-73e7-4abd-aa48-5256001f6863"; // CANCER
	
	public static final String COMORBID_HEPATITIS = "0a1432dd-532b-4320-8396-1226ae148981"; // COMORBID HEPATITIS
	
	public static final String KIDNEY_DISEASE = "6d4349d3-06a0-4b4d-84e0-9457d27c52b2"; // KIDNEY DISEASE
	
	public static final String NO_DISEASE = "9ef2d44f-12cc-40ed-8882-224616212774"; // NO COMORBIDITY
	
	public static final String OTHER_DISEASE = "e91a356e-fa02-4b5a-b719-94039c5aaa42"; // OTHER CONCOMITANT DISEASE
	
	public static final String CENTRAL_COMMISSION_DATE = "65a4fb0d-503f-4ce5-961e-e55234ff2b88"; // DATE OF TB DIAGNOSIS IN CENTRAL COMMISSION
	
	public static final String CENTRAL_COMMISSION_NUMBER = "677e0452-e795-4299-8e57-ba68a49f0ad0"; // CMAC NUMBER
	
	public static final String PLACE_OF_CENTRAL_COMMISSION = "483e6ca8-293d-4d00-b71b-4464c093a71d"; // CMAC PLACE
	
	public static final String GENERAL_PRESCRIBED_TREATMENT = "a597b7df-a17f-4e91-ab6c-538cade307bf"; // GENERAL PRESCRIBED TREATMENT
	
	public static final String FORM89_DATE = "507da602-9e74-4f9f-a387-df0286734ec2"; // FORM89 DATE
	
	public static final String AGE_AT_FORM89_REGISTRATION = "2d7308a2-6d7e-4a1d-b271-8348bfe01782"; // FORM89 AGE
	
	public static final String MTB_POSITIVE = "962a52d7-51ff-4312-b2cb-6cdd0a5256aa"; // MTB POSITIVE
	
	public static final String MTB_NEGATIVE = "e91214ad-0d19-43b4-9586-609f7e818ebc"; // MTB NEGATIVE
	
	public static final String CONTACT_INVESTIGATION = "dabd5a72-02b0-4ce7-95b1-e8657b170dee"; // CONTACT INVESTIGATION
	
	public static final String NAME_OF_DOCTOR = "e2155e85-f730-4f6c-99a3-4b3a6b664b7a"; // NAME OF DOCTOR
	
	public static final String NAME_OF_IP_FACILITY = "c34c30ab-ae45-4004-9dc7-926d5d0ed862"; // NAME OF IP FACILITY
	
	public static final String NAME_OF_CP_FACILITY = "b4fb2f5a-2d8a-4bd7-a547-e6699aa6e592"; // NAME OF CP FACILITY
	
	public static final String DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE = "6004bc9f-22fd-4367-b4b2-0b84bd5b2b27"; // DOTS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE
	
	public static final String XRAY_DATE = "5539c878-497d-4a77-a173-e709e5b589f2"; // XRAY DATE
	
	public static final String WORKER = "6b32c9a7-164f-4dbd-a21a-363be348784d"; // WORKER
	
	public static final String GOVT_SERVANT = "67beee79-d8ef-4d97-a3e8-555bb88cd498"; // GOVERNMENT SERVANT
	
	public static final String STUDENT = "c54af629-5136-448d-8caa-75014923991c"; // STUDENT
	
	public static final String DISABLED = "79dbd582-a278-46b6-be2e-da002e9e392d"; // DISABLED
	
	public static final String UNEMPLOYED = "7635ade9-6d01-4116-afb8-18f530e5a0d0"; // UNEMPLOYED
	
	public static final String PHC_WORKER = "83ca0f48-e251-4bb1-8285-dc5b34082fc2"; // PHC WORKER
	
	public static final String PRIVATE_SECTOR = "PRIVATE SECTOR";
	
	public static final String MILITARY_SERVANT = "46e53ea3-3ec0-493d-8b4c-3d8fc5cfbccb"; // MILITARY SERVANT
	
	public static final String SCHOOLCHILD = "abe0f3c6-ebab-4d55-87a4-7a51e0a12533"; // SCHOOLCHILD
	
	public static final String TB_SERVICES_WORKER = "ffe1ad0d-cc47-4933-a8cc-f3557770be1d"; // TB SERVICES WORKER
	
	public static final String HOUSEWIFE = "07591214-cebf-4c34-9b90-dffe3fdef7ad"; // HOUSEWIFE
	
	public static final String PRESCHOOL_CHILD = "b03735a2-e537-4583-b69a-75bad77a5381"; // PRESCHOOL CHILD
	
	public static final String PENSIONER = "e4dc23da-746f-49e6-83dc-cbd34271118e"; // PENSIONER
	
	public static final String RESIDENT_OF_TERRITORY = "d0f575ab-e355-495c-853d-5336cd188611"; // RESIDENT OF TERRITORY
	
	public static final String RESIDENT_OTHER_TERRITORY = "60f4284f-9cbb-4b49-8b28-c7ca5101484d"; // RESIDENT OTHER TERRITORY
	
	public static final String FOREIGNER = "96ae1050-172f-440e-9214-449bb2e36984"; // FOREIGNER
	
	public static final String RESIDENT_SOCIAL_SECURITY_FACILITY = "0aa01478-c6b6-41d9-8a14-12d772303828"; // RESIDENT SOCIAL SECURITY FACILITY
	
	public static final String HOMELESS = "a046f495-25d8-4f3c-9d3b-d4b09981bc80"; // HOMELESS
	
	public static final String CONVICTED = "ebea61de-addc-4179-b17b-f3c4afbdd210"; // CONVICTED
	
	public static final String ON_REMAND = "e03b47c8-1554-4d1d-86bf-f09a17d53f65"; // ON REMAND
	
	public static final String CITY = "3fa634b6-88b6-418c-b3d5-7f4bf35ef879"; // CITY
	
	public static final String VILLAGE = "d412a97a-bf37-4a37-a26e-4adf9106de97"; // VILLAGE
	
	public static final String PHC_FACILITY = "3bcea2e6-14ee-4522-bc0b-e39759f3e1f0"; // PHC FACILITY
	
	public static final String OTHER_MEDICAL_FACILITY = "68c50fa1-aea3-4baa-a749-40539223bbdc"; // OTHER MEDICAL FACILITY
	
	public static final String PRIVATE_SECTOR_FACILITY = "9d082349-7e00-43ca-b79a-922b648f6874"; // PRIVATE SECTOR FACILITY
	
	public static final String TB_FACILITY = "dc286de6-8a6b-4cc1-aec1-d316d5290384"; // TB FACILITY
	
	public static final String SELF_REFERRAL = "1bdbd6e5-264a-45bd-a451-d538eaa525ba"; // SELF-REFERRAL
	
	public static final String BASELINE_EXAM = "e325c566-56d1-41ca-ab02-5cae7b827456"; // BASELINE EXAM
	
	public static final String POSTMORTERM_IDENTIFICATION = "e0454d97-330d-45c3-b406-f1b5bf4158ae"; // POSTMORTERM IDENTIFICATION
	
	//public static final String CONTACT  = "CONTACT";
	public static final String MIGRANT = "c5a59d21-aae6-4a15-9732-13a51ec58d26"; // MIGRANT
	
	public static final String COUNTRY_OF_ORIGIN = "04e4d0d6-d7a3-43c3-a920-7fb593539975"; // COUNTRY OF ORIGIN
	
	public static final String CITY_OF_ORIGIN = "03e90dc0-9aae-4017-aefc-87be6c16ea35"; // CITY OF ORIGIN
	
	public static final String DATE_OF_RETURN = "910d67fd-78db-4b93-aec7-303d03241941"; // DATE OF RETURN
	
	//public static final String FLUOROGRAPHY = "FLUOROGRAPHY";
	public static final String TUBERCULIN_TEST = "e513429f-b390-494d-86f8-1c5bed0f180f"; // TUBERCULIN TEST
	
	public static final String ZIEHLNELSEN = "676b7e98-d74b-4062-b499-7530402a6b6e"; // ZIEHLNELSEN
	
	public static final String FLURORESCENT_MICROSCOPY = "1063e358-8a6d-47a3-bfb7-3857546530e2"; // FLURORESCENT MICROSCOPY
	
	public static final String HISTOLOGY = "f470a709-d411-4535-904d-ba743db0af50"; // HISTOLOGY
	
	public static final String CULTURE_TEST = "6ffc466f-b088-487e-85ae-7c053e25cd9f"; // CULTURE TEST
	
	public static final String HAIN_1_DETECTION = "9c1159ff-aabd-435f-b96c-5cc7493a8110"; // HAIN 1 DETECTION
	
	public static final String HAIN_2_DETECTION = "addb813d-c274-4947-8d75-29f322bbe547"; // HAIN 2 DETECTION
	
	public static final String DST = "ef3aa16d-c8df-4d8c-9164-99e0e05499db"; // DST
	
	public static final String CXR_RESULT = "47a92a60-588e-4ee4-80a7-83ee919792b9"; // CXR RESULT
	
	public static final String OTHER_METHOD_OF_DETECTION = "56a27e54-4dd2-4fde-83d8-44761a3a4213"; // OTHER METHOD OF DETECTION
	
	public static final String FOCAL = "e9ecc46c-5c0c-4ae8-9480-105173f0bd1d"; // FOCAL
	
	public static final String INFILTRATIVE = "aa777c47-2dbd-47b3-a440-ccd5d531ce13"; // INFILTRATIVE
	
	public static final String DISSEMINATED = "c63610b8-09b3-49e4-8ec5-90ea8bb6567f"; // DISSEMINATED
	
	public static final String CAVERNOUS = "4395f061-a374-4f52-99ca-2beabc4b64d4"; // CAVERNOUS
	
	public static final String FIBROUS_CAVERNOUS = "19354709-be47-4f11-b27a-1a41bb55342d"; // FIBROUS CAVERNOUS
	
	public static final String CIRRHOTIC = "48a2fc8d-4b38-4787-a170-dee58dfd0316"; // CIRRHOTIC
	
	public static final String TB_PRIMARY_COMPLEX = "52c45523-db94-4f0d-af12-8df40015de3a"; // TB PRIMARY COMPLEX
	
	//public static final String MILITARY = "MILITARY";
	public static final String TUBERCULOMA = "1f411719-0731-4032-8a99-e89760604a18"; // TUBERCULOMA
	
	public static final String BRONCHUS = "6da0c77a-3f83-4191-bb53-43bde0effe60"; // BRONCHUS
	
	public static final String PLEVRITIS = "b07bc998-a91d-46e8-be18-a32a1f315711"; // PLEVRITIS
	
	public static final String OF_LYMPH_NODES = "80667bb0-ad83-4019-b460-bd480a2f549c"; // OF LYMPH NODES
	
	public static final String OSTEOARTICULAR = "f979dd67-566a-47ef-8d6c-64ea1b041108"; // OSTEOARTICULAR
	
	public static final String GENITOURINARY = "1b61d5a5-a3a5-490a-ad14-4c8916e21292"; // GENITOURINARY
	
	//public static final String OF_PERIPHERAL_LYMPH_NODES = "OF PERIPHERAL LYMPH NODES";
	public static final String ABDOMINAL = "8b54d18e-c525-4067-8be7-179ff384847e"; // ABDOMINAL
	
	public static final String TUBERCULODERMA = "9d9cdf9a-5462-486d-bb7c-8c7a35e79139"; // TUBERCULODERMA
	
	public static final String OCULAR = "e8b2e3b7-40f9-4c35-b098-0204c52be32a"; // OCULAR
	
	public static final String OF_CNS = "e05a9252-c2e4-48de-a335-8d65de02e41e"; // OF CNS
	
	public static final String OF_LIVER = "d46303ed-e0c3-437e-8501-a327620b96c1"; // OF LIVER
	
	public static final String COMPLICATION = "e194a5a1-5632-413f-8a7a-f93430978e2e"; // COMPLICATION
	
	public static final String OTHER_CAUSE_OF_DEATH = "bdac7716-d85b-4d12-a589-e04570644c26"; // OTHER CAUSE OF DEATH
	
	public static final String UNDETERMINED = "e0dc69e4-c4c0-46c1-b524-ed2bca46005e"; // UNDETERMINED
	
	public static final String DRUG_RESISTANCE_DURING_TREATMENT = "ccd094e6-ac27-418f-a30e-54e9a1bac362"; // DRUG RESISTANCE DURING TREATMENT
	
	public static final String DATE_OF_DRUG_RESISTANCE_DURING_TREATMENT = "61d48834-7268-4161-ae86-57b46fbce98d"; // DATE OF DRUG RESISTANCE DURING TREATMENT
	
	public static final String NAME_OF_TREATMENT_LOCATION = "59289c40-8f75-4cee-a5f2-90cbc66fa0e6"; // NAME OF TREATMENT LOCATION
	
	public static final String HOSPITAL = "888df023-7167-45ba-a71c-f040da19f50d"; // HOSPITAL
	
	// Contacts (potentially legacy?)
	public static final String CONTACT_KNOWN_OR_CURRENT_MDR_CASE = "PATIENT CONTACT IS KNOWN MDR-TB CASE";
	
	public static final String PATIENT_CONTACT_TB_TEST_RESULT = "PATIENT CONTACT TUBERCULOSIS TEST RESULT";
	
	public static final String SIMPLE_TB_TEST_RESULT = "SIMPLE TUBERCULOSIS TEST RESULT";
	
	public static final String SIMPLE_TB_TEST_TYPE = "SIMPLE TUBERCULOSIS TEST TYPE";
	
	public static final String TREATMENT_SUPPORTER_CURRENTLY_ACTIVE = "TREATMENT SUPPORTER IS CURRENTLY ACTIVE";
	
	/**
	 * New concepts added for v2.5 upgrade
	 */
	public static final String DURATION = "DURATION";
	
	public static final String DAYS = "DAYS";
	
	public static final String WEEKS = "WEEKS";
	
	public static final String MONTHS = "MONTHS";
	
	public static final String YEARS = "YEARS";
	
	public static final String DOSE_FREQUENCY = "DOSE FREQUENCY";
	
	public static final String EVERY_30_MIN = "53657024-f3fe-11ed-8b9d-00155d016f00"; // EVERY 30 MIN
	
	public static final String EVERY_EIGHT_HOURS = "53657079-f3fe-11ed-8b9d-00155d016f00"; // EVERY EIGHT HOURS
	
	public static final String EVERY_FIVE_HOURS = "53657085-f3fe-11ed-8b9d-00155d016f00"; // EVERY FIVE HOURS
	
	public static final String EVERY_FORTY_EIGHT_HOURS = "53657092-f3fe-11ed-8b9d-00155d016f00"; // EVERY FORTY-EIGHT HOURS
	
	public static final String EVERY_FOUR_HOURS = "536570a2-f3fe-11ed-8b9d-00155d016f00"; // EVERY FOUR HOURS
	
	public static final String EVERY_HOUR = "536570b4-f3fe-11ed-8b9d-00155d016f00"; // EVERY HOUR
	
	public static final String EVERY_SEVENTY_TWO_HOURS = "536570ca-f3fe-11ed-8b9d-00155d016f00"; // EVERY SEVENTY-TWO HOURS
	
	public static final String EVERY_SIX_HOURS = "536570e3-f3fe-11ed-8b9d-00155d016f00"; // EVERY SIX HOURS
	
	public static final String EVERY_TWENTY_FOUR_HOURS = "536570ff-f3fe-11ed-8b9d-00155d016f00"; // EVERY TWENTY-FOUR HOURS
	
	public static final String EVERY_TWO_HOURS = "5365711d-f3fe-11ed-8b9d-00155d016f00"; // EVERY TWO HOURS
	
	public static final String ONCE_DAILY = "cf3eeea7-f3fe-11ed-8b9d-00155d016f00"; // ONCE DAILY
	
	public static final String ONCE_DAILY_AT_BEDTIME = "cf3eef05-f3fe-11ed-8b9d-00155d016f00"; // ONCE DAILY, AT BEDTIME
	
	public static final String ONCE_DAILY_IN_THE_EVENING = "cf3eef11-f3fe-11ed-8b9d-00155d016f00"; // ONCE DAILY, IN THE EVENING
	
	public static final String ONCE_DAILY_IN_THE_MORNING = "cf3eef1e-f3fe-11ed-8b9d-00155d016f00"; // ONCE DAILY, IN THE MORNING
	
	public static final String ONE_TIME = "cf3eef2d-f3fe-11ed-8b9d-00155d016f00"; // ONE TIME
	
	public static final String THRICE_DAILY = "cf3eef40-f3fe-11ed-8b9d-00155d016f00"; // THRICE DAILY
	
	public static final String THRICE_DAILY_AFTER_MEALS = "cf3eef55-f3fe-11ed-8b9d-00155d016f00"; // THRICE DAILY, AFTER MEALS
	
	public static final String THRICE_DAILY_BEFORE_MEALS = "cf3eefc4-f3fe-11ed-8b9d-00155d016f00"; // THRICE DAILY, BEFORE MEALS
	
	public static final String THRICE_DAILY_WITH_MEALS = "cf3eefe0-f3fe-11ed-8b9d-00155d016f00"; // THRICE DAILY, WITH MEALS
	
	public static final String TWICE_DAILY = "cf3eeffe-f3fe-11ed-8b9d-00155d016f00"; // TWICE DAILY
	
	public static final String TWICE_DAILY_AFTER_MEALS = "cf3ef01f-f3fe-11ed-8b9d-00155d016f00"; // TWICE DAILY AFTER MEALS
	
	public static final String TWICE_DAILY_BEFORE_MEALS = "cf3ef042-f3fe-11ed-8b9d-00155d016f00"; // TWICE DAILY BEFORE MEALS
	
	public static final String TWICE_DAILY_WITH_MEALS = "cf3ef068-f3fe-11ed-8b9d-00155d016f00"; // TWICE DAILY WITH MEALS
	
	public static final String DOSING_UNIT = "ccba57c3-6d07-11ee-af2b-e86a64440f18"; // UNIT TYPE
	
	public static final String AMPULES = "AMPULE(S)";
	
	public static final String GRAMS = "2c933098-f6e6-11ed-8b9d-00155d016f00"; // GRAM(S)
	
	public static final String KILOGRAMS = "2c93301e-f6e6-11ed-8b9d-00155d016f00"; // KILOGRAM(S)
	
	public static final String LITRES = "2c933044-f6e6-11ed-8b9d-00155d016f00"; // LITRE(S)
	
	public static final String MILLIGRAMS = "2c933036-f6e6-11ed-8b9d-00155d016f00"; // MILLIGRAM(S)
	
	public static final String MILLILITRES = "2c933055-f6e6-11ed-8b9d-00155d016f00"; // MILLILITRE(S)
	
	public static final String TEASPOONS = "2c933069-f6e6-11ed-8b9d-00155d016f00"; // TEASPOON(S)
	
	public static final String TABLESPOONS = "2c93307f-f6e6-11ed-8b9d-00155d016f00"; // TABLESPOON(S)
	
	public static final String TABLETS = "d0fc804d-658e-4c48-b89f-4c7e0e703e05"; // FILM COATED TABLET
	
	public static final String SYRINGES = "2c9330d1-f6e6-11ed-8b9d-00155d016f00"; // SYRINGE(S)
	
	public static final String VIALS = "473f61ce-e8b6-4930-8c6e-80fa72d5f571"; // VIAL
	
	/* All TB Drugs for Resistance */
	public static final String AMIKACIN_RESISTANCE = "68e27ec8-37da-4ab8-b992-f90374275043"; // AMIKACIN RESISTANCE
	
	public static final String BEDAQUILINE_RESISTANCE = "7b41b1ae-410f-4b4a-adae-f7394311f49e"; // BEDAQUILINE RESISTANCE
	
	public static final String CAPREOMYCIN_RESISTANCE = "85209eef-f575-4a5f-9357-e8e39a1785cf"; // CAPREOMYCIN RESISTANCE
	
	public static final String CIPROFLOXACIN_RESISTANCE = "f82db553-afdf-4560-8ad5-5e0b2da0d7ae"; // CIPROFLOXACIN RESISTANCE
	
	public static final String CLARITHROMYCIN_RESISTANCE = "7723532f-5736-4fa3-9b51-39e10d9be47b"; // CLARITHROMYCIN RESISTANCE
	
	public static final String CLOFAZAMINE_RESISTANCE = "917e64f7-893a-4ef7-92e4-e6c1fa592300"; // CLOFAZAMINE RESISTANCE
	
	public static final String CYCLOSERINE_RESISTANCE = "9010317e-e348-4ac9-b73a-539edeca2ac0"; // CYCLOSERINE RESISTANCE
	
	public static final String DELAMANID_RESISTANCE = "92649f85-64d9-4ef0-b266-df583e3b6715"; // DELAMANID RESISTANCE
	
	public static final String ETHAMBUTOL_RESISTANCE = "8267241e-8c5d-4814-a1e0-e09e7f98399d"; // ETHAMBUTOL RESISTANCE
	
	public static final String ETHIONAMIDE_RESISTANCE = "899e13bf-b959-49e4-8ef4-c5644c0c4709"; // ETHIONAMIDE RESISTANCE
	
	public static final String GATIFLOXACIN_RESISTANCE = "ce390615-08fd-48da-9962-aa97214c47ca"; // GATIFLOXACIN RESISTANCE
	
	public static final String ISONIAZID_RESISTANCE = "3ba43856-00c1-46d0-9c39-875487bd1562"; // ISONIAZID RESISTANCE
	
	public static final String KANAMYCIN_RESISTANCE = "988fa8f7-c67d-44c8-99e8-fdd20a4b0050"; // KANAMYCIN RESISTANCE
	
	public static final String LEVOFLOXACIN_RESISTANCE = "8bf73e32-f75a-4242-9bcf-92c387ff264e"; // LEVOFLOXACIN RESISTANCE
	
	public static final String LINEZOLID_RESISTANCE = "5d758f40-a683-4779-8a49-e7320a7b863e"; // LINEZOLID RESISTANCE
	
	public static final String MOXIFLOXACIN_RESISTANCE = "a1930ed3-333f-4bdd-9c56-6b9791143e8a"; // MOXIFLOXACIN RESISTANCE
	
	public static final String OFLOXACIN_RESISTANCE = "b1c139e2-fc0e-46f0-af5b-93fdfabc42d9"; // OFLOXACIN RESISTANCE
	
	public static final String OTHER_RESISTANCE = "95b09185-3928-11ee-9784-e86a64440f18"; // OTHER RESISTANCE
	
	public static final String P_AMINOSALICY_RESISTANCE = "6371d4c7-b3c6-4d57-8d03-7a383b616fc7"; // P-AMINOSALICY RESISTANCE
	
	public static final String PROTHIONAMIDE_RESISTANCE = "044afaa8-5e59-4aae-9845-3141544d9957"; // PROTHIONAMIDE RESISTANCE
	
	public static final String PYRAZINAMIDE_RESISTANCE = "b877ac5a-7e03-4c38-bab5-6afe5ac76a74"; // PYRAZINAMIDE RESISTANCE
	
	public static final String RIFABUTIN_RESISTANCE = "998dea9c-21ba-42b3-bcaf-173fcb9ea78b"; // RIFABUTIN RESISTANCE
	
	public static final String RIFAMPICIN_RESISTANCE = "780eb765-8dcb-4edf-a808-fc643fe26b60"; // RIFAMPICIN RESISTANCE
	
	public static final String STREPTOMYCIN_RESISTANCE = "18876a12-3a06-450c-af3c-aaa774e902b6"; // STREPTOMYCIN RESISTANCE
	
	public static final String TERIZIDONE_RESISTANCE = "0a9528dd-fa14-4700-b682-ee218a0f741d"; // TERIZIDONE RESISTANCE
	
	public static final String THIOACETAZONE_RESISTANCE = "3892c5f2-3b71-4d34-a7d9-261dabcdbd5f"; // THIOACETAZONE RESISTANCE
	
	public static final String VIOMYCIN_RESISTANCE = "95b09178-3928-11ee-9784-e86a64440f18"; // VIOMYCIN RESISTANCE
}
