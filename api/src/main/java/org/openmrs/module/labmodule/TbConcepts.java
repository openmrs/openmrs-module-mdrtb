package org.openmrs.module.labmodule;

/**
 * This class defines all of the Concept Mappings that are required/used by this module
 * Note that mappings are defined as arrays in case we want to rename the mappings and temporarily support multiple mappings for a single concept
 */
public class TbConcepts {
	
	
	// LAB MODULE - LAB ENTRY
	public final static String [] LAB_NO = {"Laboratory investigation number"};
	public final static String [] INVESTIGATION_DATE = {"Investigation Date"};
	public final static String [] REQUEST_DATE = {"Date Request Lab"};
	
	//omar
	public final static String [] REFERRING_FACILITY= {"Referring Facility"};
	public final static String [] REFERRED_BY={"REFERRED BY"};
	public final static String [] REQUESTING_MEDICAL_FACILITY = {"Requesting medical facility"};
	public final static String [] PHC = {"PHC Facilitiy"};
	public final static String [] SALIVA = {"SALIVA"};
	public final static String [] INVESTIGATION_PURPOSE = {"Purpose of investigation"};
	public final static String [] NEW_CASE= {"Diagnostics - NA"};
	public final static String [] REPEAT_CASE= {"Diagnostics - Repeated"};
	
	
	public final static String [] BIOLOGICAL_SPECIMEN = {"Biological Specimen"};
	public final static String [] PERIPHERAL_LAB_NO = {"Peripheral laboratory investigation number"};
	public final static String [] PERIPHERAL_LAB = {"PERIPHERAL LAB"};
	public final static String [] PERIPHERAL_BIOLOGICAL_SPECIMEN = {"PERIPHERAL LAB BIOLOGICAL SPECIMEN"};
	public final static String [] MICROSCOPY_RESULT = {"Microscopy Result"};
	public final static String [] RESULT_DATE = {"Date of result"};
	public final static String [] YEAR = {"Year"};
	public final static String [] TB03 = {"tb03"};
	public final static String [] DATE_SAMPLE_SENT_TO_BACTERIOLOGICAL_LAB = {"Date of sending of biological sample to bacteriological laboratory"};
	public final static String [] RESULT_FROM_BACTERIOLOGICAL_LAB = {"Result of investigation in bacteriological laboratory"};
	public final static String [] SPECIMEN_COMMENTS =  {"TUBERCULOSIS SPECIMEN COMMENTS"};
	public final static String [] REGIONAL_LAB_NO = {"Regional Laboratory No."};
	public final static String [] STABLE = {"Stable"};
	public final static String [] SENSITIVE = {"Sensitive"};
	public final static String [] DATE_OBSERVED_GROWTH = {"Date of observed growth"};
	public final static String [] XPERT_MTB_RIF={"Xpert MTB/Rif"};
	public final static String [] MTB_NEGATIVE={"MTB NEGATIVE"};	
	public final static String [] CONTROL_TREATMENT_REGIMENT_I={"Treatment Control - CAT I, II, III"};
	public final static String [] CONTROL_TREATMENT_REGIMENT_II={"Treatment Control - CAT IV"};
	public final static String [] H = {"H"};
	public final static String [] R = {"R"};
	public final static String [] PLACE_OF_CULTURE = {"Place of Culture"};
	public final static String [] CULTURE_INOCULATION_DATE= {"Date of inoculation of culture"};
	public final static String [] LABORATORY_NO = {"Laboratory No"};
	public final static String [] MGIT_CULTURE = {"MGIT CULTURE RESULT"};
	public final static String [] MGIT = {"MGIT"};
	public final static String [] MT_ID_TEST = {"MT ID Test"};
	public final static String [] CULTURE_TYPE = {"Type of Culture Reported"};
	public final static String [] MT_POSITIVE = {"MT positive"};
	public final static String [] MT_NEGATIVE = {"MT negative"};
	public final static String [] MT_NOT_VALID = {"Not valid"};
	public final static String [] LAB_SPECIALIST_NAME = {"LAB SPECIALIST NAME"};
	public final static String [] SENT_TO_DST = {"SENT TO DST"};
	public final static String [] SENT_TO_DST_DATE = {"DATE OF SENDING TO DST"};
	/*public final static String [] OBLAST = {"Oblast"};
	public final static String [] DISTRICT = {"District"};
	*/public final static String [] SENT_TO_CULTURE = {"SENT TO CULTURE"};
	public final static String [] SENT_TO_CULTURE_DATE = {"DATE OF SENDING TO CULTURE"};
	
	
	public final static String [] CULTURE_RESULT_REPORTING_DATE= {"Date of Reporting culture result"};
	public final static String [] DATE_OF_MGIT_GROWTH =  {"Date of MGIT Growth Occurrence"};
	public final static String [] MGIT_RESULT_TEMPLATE=  {"MGIT RESULT TEMPLATE"};
	public final static String [] MGIT_NO_GROWTH=  {"No Growth"};
	public final static String [] MGIT_MT=  {"MT"};
	public final static String [] MGIT_PRO_GROWTH=  {"Pro-growth"};
	public final static String [] MGIT_HTM=  {"NMTB"};
	
	public final static String [] LJ_RESULT_TEMPLATE =  {"L-J RESULT TEMPLATE"};
	public final static String [] LJ =  {"L-J"};
	public final static String [] LJ_RESULT =  {"L-J Culture Result"};
	public final static String [] DATE_OF_LJ_GROWTH =  {"Date of L-J Growth Occurrence"};
	public final static String [] DATE_OF_REPEATED_DECONTAMINATION =  {"Date of repeated Decontamination"};
	public final static String [] DATE_OF_GROWTH_2nd_DECONTAMINATION =  {"Date of Growth 2nd decontamination"};
	public final static String [] CULTURE_RESULT_2nd_DECONTAMINATION =  {"Culture Result 2nd decontamination"};
	public final static String [] CONTAMINATED_TUBES_RESULT_TEMPLATE =  {"CONTAMINATED TUBES RESULT TEMPLATE"};
	
	//DST1
	public final static String [] PLACE_OF_DST1 = {"Place of DST1"};
	public final static String [] TYPE_OF_DEST_REPORTED= {"dst_type"};	
	public final static String [] DATE_DST1_RESULT_REPORTING= {"Date of Reporting 1st-line DST result"};
	public final static String [] SUSCEPTIBLE = {"SUSCEPTIBLE"};
	public final static String [] RESISTANT = {"RESISTANT"};
	public final static String [] DST_RESISTANCE= {"Drug Resistance"};
	public final static String [] DST_RESISTANCE_S= {"Streptomycin resistance"};
	public final static String [] DST_RESISTANCE_H= {"Isoniazid resistance"};
	public final static String [] DST_RESISTANCE_R= {"Rifampicin resistance"};
	public final static String [] DST_RESISTANCE_E= {"Ethambutol resistance"};
	public final static String [] DST_RESISTANCE_Z= {"Pyrazinamide resistance"};
	public final static String [] DST_RESISTANCE_LFX= {"LFX resistance"};

	//DST1 MGIT
	public final static String [] DST1_MGIT_CONSTRUCT= {"DST1 MGIT CONSTRUCT"};
	public final static String [] DATE_INOCULATION_DST1_MGIT= {"Date 1st-line DST inoculation on MGIT"};
	public final static String [] DATE_READING_DST1_MGIT= {"Date of reading 1st-line DST results on MGIT"};
	
	//DST1 LJ
	public final static String [] DST1_LJ_CONSTRUCT= {"DST1 LJ CONSTRUCT"};
	public final static String [] DATE_INOCULATION_DST1_LJ= {"Date of 1st-line DST inoculation on L-J"};
	public final static String [] DATE_READING_DST1_LJ= {"Date of reading 1st-line DST results on L-J"};
	
	//DST2
	public final static String [] DST2_MGIT_CONSTRUCT= {"DST2 MGIT CONSTRUCT"};
	public final static String [] DST2_LJ_CONSTRUCT= {"DST2 LJ CONSTRUCT"};
	public final static String [] PLACE_OF_DST2 = {"Place of DST2"};
	public final static String [] DATE_INOCULATION_DST2= {"Date of 2nd-line DST inoculation"};
	public final static String [] DATE_READING_DST2= {"Date of reading 2nd-line DST results"};
	public final static String [] DATE_REPORTING_DST2= {"Date of Reporting 2nd-line DST result"};
	
	public final static String [] OFX_R =  {"OFX resistance"};
	public final static String [] MOX_R =  {"MOX resistance"};
	public final static String [] LFX_R =  {"LFX resistance"};
	public final static String [] PTO_R =  {"PTO resistance"};
	public final static String [] LZD_R =  {"LZD resistance"};
	public final static String [] CFZ_R =  {"CFZ resistance"};
	public final static String [] BDQ_R =  {"BDQ resistance"};
	public final static String [] DLM_R =  {"DLM resistance"};
	public final static String [] PAS_R =  {"PAS resistance"};
	public final static String [] CM_R =   {"CM resistance"};
	public final static String [] KM_R =  {"KM resistance"};
	public final static String [] AM_R =  {"AM resistance"};
	
	public final static String [] QUINOLONES_RESISTANCE = {"QUINOLONES_RESISTANCE"};

	// LAB MODULE - LAB ENTRY - Investigation Purpose
	public final static String [] DIAGNOSTICS_NA = {"Diagnostics - NA"};
	public final static String [] DIAGNOSTICS_REPEATED = {"Diagnostics - Repeated"};
	public final static String [] TREATMENT_CONTROL_CAT_123 = {"Treatment Control - CAT I, II, III"};
	public final static String [] TREATMENT_CONTROL_CAT_4 = {"Treatment Control - CAT IV"};
	
	// LAB MODULE - MICROSCOPY
	public final static String [] MICROSCOPY_CONSTRUCT = {"MICROSCOPY TEST CONSTRUCT"};
	public final static String [] APPEARENANCE_SAMPLE_1 = {"Appearance sample 1"};
	public final static String [] APPEARENANCE_SAMPLE_2 = {"Appearance sample 2"};
	public final static String [] APPEARENANCE_SAMPLE_3 = {"Appearance sample 3"};
	public final static String [] DATE_SAMPLE_1 = {"Date sample 1"};
	public final static String [] DATE_SAMPLE_2 = {"Date sample 2"};
	public final static String [] DATE_SAMPLE_3 = {"Date sample 3"};
	public final static String [] RESULT_SAMPLE_1 = {"Result sample 1"};
	public final static String [] RESULT_SAMPLE_2 = {"Result sample 2"};
	public final static String [] RESULT_SAMPLE_3 = {"Result sample 3"};
	
	// HAIN 2
	public final static String [] HAIN_2_CONSTRUCT = {"HAIN 2 TEST CONSTRUCT"};
	public final static String [] MOX = {"MOX/OFX"};
	public final static String [] CM = {"Km/ Am/ Cm"};
	public final static String [] E = {"E"};
	
	
	// Vitals 
	public final static String [] WEIGHT = {"WEIGHT"};
	public final static String [] PULSE = {"PULSE"};
	public final static String [] TEMPERATURE = {"TEMPERATURE"};
	public final static String [] RESPIRATORY_RATE = {"RESPIRATORY RATE"};
	public final static String [] SYSTOLIC_BLOOD_PRESSURE = {"SYSTOLIC BLOOD PRESSURE"};
	
	// MDR-TB Drugs
	public final static String [] TUBERCULOSIS_DRUGS = {"TUBERCULOSIS DRUGS"};
	public final static String [] FIRST_LINE_DRUGS = {"FIRST LINE DRUGS"};
	public final static String [] SECOND_LINE_DRUGS = {"SECOND LINE DRUGS"};
	public final static String [] ISONIAZID = {"ISONIAZID"};
	public final static String [] RIFAMPICIN = {"RIFAMPICIN"};
	public final static String [] CAPREOMYCIN = {"CAPREOMYCIN"};
	public final static String [] KANAMYCIN = {"KANAMYCIN"};
	public final static String [] AMIKACIN = {"AMIKACIN"};
	public final static String [] CLOFAZIMINE = {"CLOFAZIMINE"};
	public final static String [] CYCLOSERINE = {"CYCLOSERINE"};
	public final static String [] ETHIONAMIDE = {"ETHIONAMIDE"};
	public final static String [] PROTHIONAMIDE = {"PROTHIONAMIDE"};
	public final static String [] GATIFLOXACIN = {"GATIFLOXACIN"};
	public final static String [] OFLOXACIN = {"OFLOXACIN"};
	public final static String [] P_AMINOSALICYLIC_ACID = {"P-AMINOSALICYLIC ACID "};
	public final static String [] TERIZIDONE = {"TERIZIDONE"};
	public final static String [] VIOMYCIN = {"VIOMYCIN"};
	public final static String [] CLARITHROMYCIN = {"CLARITHROMYCIN"};
	public final static String [] RIFABUTINE = {"RIFABUTINE"};
	public final static String [] STREPTOMYCIN = {"STREPTOMYCIN"};
	public final static String [] PYRAZINAMIDE = {"PYRAZINAMIDE"};
	public final static String [] CIPROFLOXACIN = {"CIPROFLOXACIN"};
	public final static String [] ETHAMBUTOL = {"ETHAMBUTOL"};
	public final static String [] LEVOFLOXACIN = {"LEVOFLOXACIN"};
	public final static String [] PYRIDOXINE = {"PYRIDOXINE"};
	public final static String [] MOXIFLOXACIN = {"MOXIFLOXACIN"};
	public final static String [] AMOXICILLIN_AND_LAVULANIC_ACID = {"AMOXICILLIN AND CLAVULANIC ACID"};
	public final static String [] THIOACETAZONE = {"THIOACETAZONE"};
	
	public final static String [] QUINOLONES = {"QUINOLONES"};

	// Drug-Related concepts
	public final static String [] CURRENT_MULTI_DRUG_RESISTANT_TUBERCULOSIS_TREATMENT_TYPE = {"CURRENT MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT TYPE"};
	public final static String [] REASON_TUBERCULOSIS_TREATMENT_CHANGED_OR_STOPPED = {"REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED"};
	public final static String [] STANDARDIZED = {"STANDARDIZED"};
	public final static String [] EMPIRIC = {"EMPIRIC"};
	public final static String [] INDIVIDUALIZED = {"INDIVIDUALIZED"};
	
    // Smear, Culture, and DSTs
    public final static String [] BACILLI = {"BACILLI"};
    public final static String [] COLONIES = {"COLONIES"};
    public final static String [] CULTURE_CONSTRUCT = {"TUBERCULOSIS CULTURE CONSTRUCT"};
    public final static String [] CULTURE_METHOD = {"TUBERCULOSIS CULTURE METHOD"};
    public final static String [] CULTURE_RESULT = {"TUBERCULOSIS CULTURE RESULT"};
    public final static String [] DIRECT_INDIRECT = {"DIRECT/INDIRECT"};
    public final static String [] DST_CONSTRUCT = {"TUBERCULOSIS DRUG SENSITIVITY TEST CONSTRUCT"};
    public final static String [] DST_METHOD = {"TUBERCULOSIS DRUG SENSITIVITY TEST METHOD"};
    public final static String [] DST_RESULT = {"TUBERCULOSIS DRUG SENSITIVITY TEST RESULT"};
    public final static String [] DST1_CONSTRUCT = {"DST1 CONSTRUCT"};
    public final static String [] DST2_CONSTRUCT = {"DST2 CONSTRUCT"};
    
    
    //XPERT AND HAIN CONCEPTS
    public final static String [] XPERT_CONSTRUCT = {"TUBERCULOSIS XPERT TEST CONSTRUCT"};
    public final static String [] HAIN_CONSTRUCT = {"TUBERCULOSIS HAIN TEST CONSTRUCT"};
    public final static String [] MTB_RESULT = {"MTB RESULT"};
    public final static String [] RIFAMPICIN_RESISTANCE = {"RIFAMPICIN RESISTANCE"};
    public final static String [] DETECTED = {"DETECTED"};
    public final static String [] NOT_DETECTED = {"NOT DETECTED"};
    public final static String [] UNDETERMINED= {"UNDETERMINED"};    
    public final static String [] ERROR = {"ERROR"};
    public final static String [] ERROR_CODE = {"ERROR CODE"};
    public final static String [] XPERT_MTB_BURDEN = {"XPERT MTB BURDEN"};
    public final static String [] XPERT_HIGH = {"HIGH"};
    public final static String [] XPERT_MEDIUM = {"MEDIUM"};
    public final static String [] XPERT_LOW = {"LOW"};
    public final static String [] ISONIAZID_RESISTANCE = {"ISONIAZID RESISTANCE"};
    public final static String [] POSITIVE_PLUS = {"Positive (+)"};
    public final static String [] TEST_NOT_DONE = {"TEST NOT DONE"};
    
    public final static String [] COLONIES_IN_CONTROL = {"COLONIES IN CONTROL"};
    public final static String [] CONCENTRATION = {"CONCENTRATION"};
    public final static String [] RESISTANT_TO_TB_DRUG = {"RESISTANT TO TUBERCULOSIS DRUG"};
    public final static String [] INTERMEDIATE_TO_TB_DRUG = {"INTERMEDIATE TO TUBERCULOSIS DRUG"};
    public final static String [] SUSCEPTIBLE_TO_TB_DRUG = {"SUSCEPTIBLE TO TUBERCULOSIS DRUG"};
    public final static String [] OTHER_MYCOBACTERIA_NON_CODED = {"OTHER MYCOBACTERIA NON-CODED"};
    public final static String [] SCANTY = {"SCANTY"};
    public final static String [] SPUTUM = {"SPUTUM"};
    public final static String [] INDUCED_SPUTUM = {"INDUCED SPUTUM"};
    public final static String [] SPUTUM_COLLECTION_DATE = {"SPUTUM COLLECTION DATE"};
    public final static String [] SAMPLE_SOURCE = {"TUBERCULOSIS SAMPLE SOURCE"};
    public final static String [] SMEAR_CONSTRUCT = {"TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT"};
    public final static String [] SMEAR_METHOD = {"TUBERCULOSIS SMEAR MICROSCOPY METHOD"};
    public final static String [] SMEAR_RESULT = {"TUBERCULOSIS SMEAR RESULT"};
    public final static String [] TEST_DATE_ORDERED = {"TUBERCULOSIS TEST DATE ORDERED"};
    public final static String [] TEST_DATE_RECEIVED = {"TUBERCULOSIS TEST DATE RECEIVED"};
    public final static String [] TEST_RESULT_DATE = {"TUBERCULOSIS TEST RESULT DATE"};
    public final static String [] TEST_START_DATE = {"TUBERCULOSIS TEST START DATE"};
    public final static String [] TYPE_OF_ORGANISM = {"TYPE OF ORGANISM"};
    public final static String [] TYPE_OF_ORGANISM_NON_CODED = {"TYPE OF ORGANISM NON-CODED"};
    public final static String [] SPECIMEN_ID = {"TUBERCULOSIS SPECIMEN ID"};
    public final static String [] SPECIMEN_APPEARANCE = {"APPEARANCE OF SPECIMEN"};
    
    public final static String [] WAITING_FOR_TEST_RESULTS = {"WAITING FOR TEST RESULTS"};
    public final static String [] DST_CONTAMINATED = {"DST CONTAMINATED"};
    public final static String [] SCANNED_LAB_REPORT = {"SCANNED LAB REPORT"};
    public final static String [] DAYS_TO_POSITIVITY = {"DAYS TO POSITIVITY"};
    public final static String [] MONTH_OF_TREATMENT = {"MONTH OF TREATMENT"};
    
    // Lab Results
    public final static String [] STRONGLY_POSITIVE = {"STRONGLY POSITIVE"};
    public final static String [] MODERATELY_POSITIVE = {"MODERATELY POSITIVE"};
    public final static String [] WEAKLY_POSITIVE = {"WEAKLY POSITIVE"};
    public final static String [] POSITIVE = {"POSITIVE"};
    public final static String [] NEGATIVE = {"NEGATIVE"};
    public final static String [] CONTAMINATED = {"CONTAMINATED"};
    public final static String [] UNSATISFACTORY_SAMPLE = {"UNSATISFACTORY SAMPLE"};
    
    // MDR-TB Classification
    //public final static String [] CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE = {"CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE"};
    public final static String [] PATIENT_GROUP = {"TUBERCULOSIS PATIENT TYPE"};
    public final static String [] NEW = {"NEW"};
    //public final static String [] PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY = {"PREVIOUSLY TREATED WITH FIRST LINE DRUGS ONLY"};
    //public final static String [] PREVIOUSLY_TREATED_SECOND_LINE_DRUGS = {"PREVIOUSLY TREATED WITH SECOND LINE DRUGS"};
    
    //public final static String [] CAT_4_CLASSIFICATION_PREVIOUS_TX = {"CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT"};
    //public final static String [] TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT = {"TREATMENT AFTER FAILURE OF FIRST TREATMENT MDR-TB PATIENT"};
    //public final static String [] TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT = {"TREATMENT AFTER FAILURE OF RE-TREATMENT MDR-TB PATIENT"};
    public final static String [] OTHER = {"OTHER NON-CODED"};
    public final static String [] RELAPSE = {"RELAPSE"};
    public final static String [] TRANSFER = {"TRANSFER"};
    public final static String [] TREATMENT_AFTER_FAILURE = {"TREATMENT AFTER FAILURE"};
    
    public final static String [] MDR_TB = {"MULTI-DRUG RESISTANT TUBERCULOSIS"};
    public final static String [] XDR_TB = {"EXTENSIVE DRUG RESISTANT TUBERCULOSIS"};
    public final static String [] SUSPECTED_MDR_TB = {"SUSPECTED MULTI-DRUG TUBERCULOSIS"};
    public final static String [] TB = {"TUBERCULOSIS"};
    
    // Treatment Outcome
    public final static String [] TB_TX_OUTCOME = {"TUBERCULOSIS TREATMENT OUTCOME"};
    public final static String [] CURED = {"CURED"};
    public final static String [] DEFAULTED = {"DEFAULTED"};
    public final static String [] DIED = {"DIED"};
    public final static String [] FAILED = {"FAILED"};
    public final static String [] TREATMENT_COMPLETE = {"TREATMENT COMPLETE"};
    public final static String [] PATIENT_TRANSFERRED_OUT = {"PATIENT TRANSFERRED OUT"};
    public final static String [] STILL_ON_TREATMENT = {"STILL ON TREATMENT"};
    public final static String [] CANCELLED = {"DIAGNOSIS CANCELLED"};
    public final static String [] LOST_TO_FOLLOWUP = {"LOST TO FOLLOWUP"};
    
    
    // TB Type
    public final static String [] PULMONARY_TB = {"PULMONARY TUBERCULOSIS"};
    public final static String [] EXTRA_PULMONARY_TB = {"EXTRA-PULMONARY TUBERCULOSIS"};
    public final static String [] ANATOMICAL_SITE_OF_TB = {"ANATOMICAL SITE OF TUBERCULOSIS"};

    // Antiretrovirals (for HIV status section and HIV regimens)
    public final static String [] ANTIRETROVIRALS = {"ANTIRETROVIRAL DRUGS"};
    public final static String [] REASON_HIV_TX_STOPPED = {"REASON ANTIRETROVIRALS CHANGED OR STOPPED"};
    
    // HIV Co-infection
    public final static String [] COINFECTED_ARVS = {"COINFECTED AND ON ANTIRETROVIRALS"};
    public final static String [] CD4_COUNT = {"CD4 COUNT"};
    public final static String [] RESULT_OF_HIV_TEST = {"RESULT OF HIV TEST"};
    
    // Hospitalization states
    public final static String [] HOSPITALIZATION_WORKFLOW = {"HOSPITALIZATION WORKFLOW"};
    public final static String [] HOSPITALIZED = {"HOSPITALIZED"};
    public final static String [] AMBULATORY = {"AMBULATORY"};   // legacy, has been retired
    
    // Other
    public final static String [] UNKNOWN = {"UNKNOWN"};
    public final static String [] CLINICIAN_NOTES = {"CLINICIAN NOTES"};
    public final static String [] RETURN_VISIT_DATE = {"RETURN VISIT DATE"};
    public final static String [] TELEPHONE_NUMBER = {"TELEPHONE NUMBER"};
    public final static String [] NONE = {"NONE"};
    

    // Contacts (potentially legacy?)
    public final static String [] PATIENT_CONTACT_IS_KNOWN_PRIOR_OR_CURRENT_MDR_TB_CASE = {"PATIENT CONTACT IS A KNOWN PRIOR OR CURRENT MDR-TB CASE"};
    public final static String [] PATIENT_CONTACT_TB_TEST_RESULT = {"PATIENT CONTACT TUBERCULOSIS TEST RESULT"};
    public final static String [] SIMPLE_TB_TEST_RESULT = {"SIMPLE TUBERCULOSIS TEST RESULT"};
    public final static String [] SIMPLE_TB_TEST_TYPE = {"SIMPLE TUBERCULOSIS TEST TYPE"};
    public final static String [] TREATMENT_SUPPORTER_CURRENTLY_ACTIVE = {"TREATMENT SUPPORTER IS CURRENTLY ACTIVE"};
    
    
    // Adverse Effects (potentially legacy?)
    public final static String [] ADVERSE_EFFECT = {"ADVERSE EFFECT"};
    public final static String [] ADVERSE_EFFECT_ACTION_TAKEN = {"ADVERSE EFFECT ACTION TAKEN, NON-CODED"};
    public final static String [] ADVERSE_EFFECT_CONSTRUCT = {"ADVERSE EFFECT CONSTRUCT"};
    public final static String [] ADVERSE_EFFECT_DATE = {"ADVERSE EFFECT DATE"};
    public final static String [] ADVERSE_EFFECT_MEDICATION = {"ADVERSE EFFECT MEDICATION"};
    public final static String [] ADVERSE_EFFECT_MEDICATION_NON_CODED = {"ADVERSE EFFECT MEDICATION NON-CODED"};
    public final static String [] ADVERSE_EFFECT_NON_CODED = {"ADVERSE EFFECT, NON-CODED"};
    
    
    // Legacy (only used by migration controller)
    public final static String [] CULTURE_STATUS = {"MULTI-DRUG RESISTANT TUBERCULOSIS CULTURE STATUS"};
    
    //NEW FOR DOTS REPORTS
    public final static String [] BASIS_FOR_TB_DIAGNOSIS = {"BASIS FOR TB DIAGNOSIS"};
    public final static String [] TB_CLINICAL_DIAGNOSIS = {"TB CLINICAL DIAGNOSIS"};
    public final static String [] CAUSE_OF_DEATH = {"CAUSE OF DEATH"};
    public final static String [] DEATH_BY_TB = {"DEATH BY TB"};
    public final static String [] DEATH_BY_TBHIV = {"DEATH BY TB/HIV"};
    public final static String [] DEATH_BY_OTHER_DISEASES = {"DEATH BY OTHER DISEASES"};
    public final static String [] DOTS_TREATMENT_START_DATE = {"DATE OF DOTS TREATMENT START"};
    
    
    
    //ADDED BY ALI
    public final static String[] CULTURE_MGIT_SPECIMEN = {"CULTURE MGIT"};
    public final static String[] CULTURE_LJ_SPECIMEN =  {"CULTURE L TH"};
    

}
