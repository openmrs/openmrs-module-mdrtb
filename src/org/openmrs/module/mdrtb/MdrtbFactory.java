package org.openmrs.module.mdrtb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptSource;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsClassLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class MdrtbFactory {

    protected final Log log = LogFactory.getLog(getClass());
    
    private String STR_TB_SMEAR_RESULT;

    private String STR_TB_SAMPLE_SOURCE;

    private String STR_BACILLI;
    
    private String STR_RESULT_DATE;
    
    private String STR_DATE_RECEIVED;
    
    private String STR_TB_SMEAR_MICROSCOPY_METHOD; 
    
    private String STR_TB_CULTURE_RESULT;

    private String STR_COLONIES;

    private String STR_CULTURE_START_DATE;
  
    private String STR_TB_CULTURE_METHOD;

    private String STR_TYPE_OF_ORGANISM;

    private String STR_TYPE_OF_ORGANISM_NON_CODED;

    private String STR_DST_COMPLETE;

    private String STR_DST_METHOD;

    private String STR_DIRECT_INDIRECT;

    private String STR_COLONIES_IN_CONTROL;

    private String STR_CONCENTRATION;

    private String STR_DST_PARENT;

    private String STR_DST_RESULT_PARENT;

    private String STR_CULTURE_PARENT;

    private String STR_SMEAR_PARENT;

    private String STR_SPUTUM_COLLECTION_DATE;

    private String STR_TREATMENT_OUTCOME_PARENT;

    private String STR_CURED;

    private String STR_FAILED;

    private String STR_DEFAULTED;

    private String STR_DIED;

    private String STR_TRANSFERRED_OUT;
 
    private String STR_STILL_ON_TREATMENT;
  
    private String STR_CULTURE_STATUS_PARENT;
 
    private String STR_NOT_CONVERTED;
 
    private  String STR_CONVERTED;

    private  String STR_RECONVERTED;
  
    private  String STR_NONE;
 
    private String  STR_TUBERCULOSIS_PATIENT_STATUS_PARENT;

    private  String STR_ON_TREATMENT;

    private String  STR_SUSPENDED;

    private  String STR_WAITING_FOR_TREATMENT;
  
    private  String STR_TREATMENT_COMPLETE;
 
    private  String STR_CULTURE_CONVERSION;

    private  String STR_CULTURE_RECONVERSION;

    private  String STR_SMEAR_CONVERSION;

    private  String STR_SMEAR_RECONVERSION;

    private String STR_TREATMENT_START_DATE;

    private String STR_PREVIOUS_DRUG_USE_PARENT;

    private String STR_NEW_MDRTB_PATIENT;

    private String STR_PREVIOUSLY_TREATED_WITH_FIRST_LINE;

    private String STR_PREVIOUSLY_TREATED_WITH_SECOND_LINE;
 
    private String STR_PREVIOUS_TREATMENT_RESULT_PARENT;

    private String STR_RELAPSE;

    private String STR_TREATMENT_AFTER_DEFAULT;

    private String STR_TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT;

    private String STR_TREATMENT_AFTER_FAILURE_OF_RE_TREATMENT;

    private String STR_TRANSFER_IN;

    private String STR_OTHER_MDR_STATE;

    private String STR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT;

    private String STR_TUBERCULOSIS;
    
    private String STR_MDR_TUBERCULOSIS;
    
    private String STR_XDR_TUBERCULOSIS;

    private String STR_PULMONARY;

    private String STR_EXTRAPULMONARY;

    private String STR_EXTRAPULMONARY_LOCATION;

    private String STR_OTHER_MYCOBACTERIA_NONCODED;
 
    private String STR_HIV_STATUS;

    private String STR_CD4_COUNT;
 
    private String STR_CD4_PERCENT;

    private String STR_ALLERGY_COMMENT;
 
    private String STR_TREATMENT_PLAN_COMMENT;

    private String STR_PREV_TREATMENT_DURATION_IN_MONTHS;

    private String STR_PREV_REGISTRATION_NUM;

    private String STR_PREV_TREATMENT_CENTER;

    private String STR_REFERRED_BY;

    private String STR_TRANSFERRED_TO;

    private String STR_TRANSFERRED_FROM;

    private String STR_ON_ANTIRETROVIRALS;

    private String STR_NEXT_VISIT;

    private String STR_SCANTY;

    private String STR_PATIENT_CONTACT_TEST_RESULT_PARENT;

    private String STR_SIMPLE_TB_TEST_RESULT;

    private String STR_SIMPLE_TB_TEST_TYPE;

    private String STR_PHONE_NUMBER;

    private String STR_PATIENT_CONTACT_KNOWN_MDR_CASE;

    private String STR_SUSPECTED_MDR_TUBERCULOSIS;

    private String STR_CURRENT_TREATMENT_TYPE;

    private String STR_STANDARDIZED;

    private String STR_EMPIRIC;

    private String STR_INDIVIDUALIZED;

    private String STR_TREATMENT_STOP_DATE;

    private String STR_DST_RESULT;
    
    private String STR_SUSCEPTIBLE;
    
    private String STR_INTERMEDIATE;
    
    private String STR_RESISTANT;
    
    private String STR_ADVERSE_EFFECT_CONSTRUCT;
    
    private String STR_ADVERSE_EFFECT_MEDICATION;
    
    private String STR_ADVERSE_EFFECT_MEDICATION_NON_CODED;
    
    private String STR_ADVERSE_EFFECT;
    
    private String STR_ADVERSE_EFFECT_NON_CODED;
    
    private String STR_ADVERSE_EFFECT_DATE;
    
    private String STR_ADVERSE_EFFECT_ACTION_TAKEN;
    
    private String STR_TREATMENT_SUPPORTER_ACTIVE;
    
    private String STR_SPECIMEN_ID;
    
    private String STR_TEST_DATE_ORDERED;
    
    private String STR_TEST_COMMENTS;
    
    private String STR_SPECIMEN_COMMENTS;

    private Map<String, Concept> xmlConceptList = new HashMap<String, Concept>();
    


    private MdrtbFactory(){readXML();};
    
    private static MdrtbFactory uniqueInstance;

    public static final MdrtbFactory getInstance() {
    	if (uniqueInstance == null) {
    		uniqueInstance = new MdrtbFactory();
    	}
        return uniqueInstance;
    }
    
    private void readXML(){
    
        try { 
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream("mdrtbConcepts.xml");
	        Document doc = db.parse(in);
	        in.close();
	        log.warn("Loaded MDR Concepts from xml...");
	        doc.getDocumentElement().normalize();
	        Element concepts = doc.getDocumentElement();
	        
            NodeList nodeList = concepts.getElementsByTagName("STR_TB_SMEAR_RESULT");
            Node node = nodeList.item(0);
            this.STR_TB_SMEAR_RESULT= node.getFirstChild().getNodeValue();  
            nodeList = concepts.getElementsByTagName("STR_TB_SAMPLE_SOURCE");
            node = nodeList.item(0);
            this.STR_TB_SAMPLE_SOURCE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_BACILLI");
            node = nodeList.item(0);
            this.STR_BACILLI = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_RESULT_DATE");
            node = nodeList.item(0);
            this.STR_RESULT_DATE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DATE_RECEIVED");
            node = nodeList.item(0);
            this.STR_DATE_RECEIVED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TB_SMEAR_MICROSCOPY_METHOD");
            node = nodeList.item(0);
            this.STR_TB_SMEAR_MICROSCOPY_METHOD  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TB_CULTURE_RESULT");
            node = nodeList.item(0);
            this.STR_TB_CULTURE_RESULT  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_COLONIES");
            node = nodeList.item(0);
            this.STR_COLONIES  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_START_DATE");
            node = nodeList.item(0);
            this.STR_CULTURE_START_DATE  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TB_CULTURE_METHOD");
            node = nodeList.item(0);
            this.STR_TB_CULTURE_METHOD  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TYPE_OF_ORGANISM");
            node = nodeList.item(0);
            this.STR_TYPE_OF_ORGANISM  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TYPE_OF_ORGANISM_NON_CODED");
            node = nodeList.item(0);
            this.STR_TYPE_OF_ORGANISM_NON_CODED  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_COMPLETE");
            node = nodeList.item(0);
            this.STR_DST_COMPLETE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_METHOD");
            node = nodeList.item(0);
            this.STR_DST_METHOD = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DIRECT_INDIRECT");
            node = nodeList.item(0);
            this.STR_DIRECT_INDIRECT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_COLONIES_IN_CONTROL");
            node = nodeList.item(0);
            this.STR_COLONIES_IN_CONTROL = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CONCENTRATION");
            node = nodeList.item(0);
            this.STR_CONCENTRATION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SPUTUM_COLLECTION_DATE");
            node = nodeList.item(0);
            this.STR_SPUTUM_COLLECTION_DATE = node.getFirstChild().getNodeValue();    
            nodeList = concepts.getElementsByTagName("STR_DST_PARENT");
            node = nodeList.item(0);
            this.STR_DST_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_RESULT_PARENT");
            node = nodeList.item(0);
            this.STR_DST_RESULT_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SMEAR_PARENT");
            node = nodeList.item(0);
            this.STR_SMEAR_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_PARENT");
            node = nodeList.item(0);
            this.STR_CULTURE_PARENT = node.getFirstChild().getNodeValue();
            //workflows:
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_OUTCOME_PARENT");
            node = nodeList.item(0);
            this.STR_TREATMENT_OUTCOME_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CURED");
            node = nodeList.item(0);
            this.STR_CURED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_FAILED");
            node = nodeList.item(0);
            this.STR_FAILED  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DEFAULTED");
            node = nodeList.item(0);
            this.STR_DEFAULTED  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DIED");
            node = nodeList.item(0);
            this.STR_DIED  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TRANSFERRED_OUT");
            node = nodeList.item(0);
            this.STR_TRANSFERRED_OUT  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_STILL_ON_TREATMENT");
            node = nodeList.item(0);
            this.STR_STILL_ON_TREATMENT  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_STATUS_PARENT");
            node = nodeList.item(0);
            this.STR_CULTURE_STATUS_PARENT  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_NOT_CONVERTED");
            node = nodeList.item(0);
            this.STR_NOT_CONVERTED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CONVERTED");
            node = nodeList.item(0);
            this.STR_CONVERTED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_RECONVERTED");
            node = nodeList.item(0);
            this.STR_RECONVERTED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_NONE");
            node = nodeList.item(0);
            this.STR_NONE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TUBERCULOSIS_PATIENT_STATUS_PARENT");
            node = nodeList.item(0);
            this.STR_TUBERCULOSIS_PATIENT_STATUS_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ON_TREATMENT");
            node = nodeList.item(0);
            this.STR_ON_TREATMENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SUSPENDED");
            node = nodeList.item(0);
            this.STR_SUSPENDED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_WAITING_FOR_TREATMENT");
            node = nodeList.item(0);
            this.STR_WAITING_FOR_TREATMENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_COMPLETE");
            node = nodeList.item(0);
            this.STR_TREATMENT_COMPLETE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_CONVERSION");
            node = nodeList.item(0);
            this.STR_CULTURE_CONVERSION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_RECONVERSION");
            node = nodeList.item(0);
            this.STR_CULTURE_RECONVERSION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_START_DATE");
            node = nodeList.item(0);
            this.STR_TREATMENT_START_DATE = node.getFirstChild().getNodeValue();            
            nodeList = concepts.getElementsByTagName("STR_PREVIOUS_DRUG_USE_PARENT");
            node = nodeList.item(0);
            this.STR_PREVIOUS_DRUG_USE_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_NEW_MDRTB_PATIENT");
            node = nodeList.item(0);
            this.STR_NEW_MDRTB_PATIENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PREVIOUSLY_TREATED_WITH_FIRST_LINE");
            node = nodeList.item(0);
            this.STR_PREVIOUSLY_TREATED_WITH_FIRST_LINE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PREVIOUSLY_TREATED_WITH_SECOND_LINE");
            node = nodeList.item(0);
            this.STR_PREVIOUSLY_TREATED_WITH_SECOND_LINE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PREVIOUS_TREATMENT_RESULT_PARENT");
            node = nodeList.item(0);
            this.STR_PREVIOUS_TREATMENT_RESULT_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_RELAPSE");
            node = nodeList.item(0);
            this.STR_RELAPSE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_AFTER_DEFAULT");
            node = nodeList.item(0);
            this.STR_TREATMENT_AFTER_DEFAULT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT");
            node = nodeList.item(0);
            this.STR_TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_AFTER_FAILURE_OF_RE_TREATMENT");
            node = nodeList.item(0);
            this.STR_TREATMENT_AFTER_FAILURE_OF_RE_TREATMENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TRANSFER_IN");
            node = nodeList.item(0);
            this.STR_TRANSFER_IN = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_OTHER_MDR_STATE");
            node = nodeList.item(0);
            this.STR_OTHER_MDR_STATE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT");
            node = nodeList.item(0);
            this.STR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TUBERCULOSIS");
            node = nodeList.item(0);
            this.STR_TUBERCULOSIS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_MDR_TUBERCULOSIS");
            node = nodeList.item(0);
            this.STR_MDR_TUBERCULOSIS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_XDR_TUBERCULOSIS");
            node = nodeList.item(0);
            this.STR_XDR_TUBERCULOSIS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PULMONARY");
            node = nodeList.item(0);
            this.STR_PULMONARY = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_EXTRAPULMONARY");
            node = nodeList.item(0);
            this.STR_EXTRAPULMONARY = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_EXTRAPULMONARY_LOCATION");
            node = nodeList.item(0);
            this.STR_EXTRAPULMONARY_LOCATION = node.getFirstChild().getNodeValue();          
            nodeList = concepts.getElementsByTagName("STR_HIV_STATUS");
            node = nodeList.item(0);
            this.STR_HIV_STATUS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CD4_COUNT");
            node = nodeList.item(0);
            this.STR_CD4_COUNT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CD4_PERCENT");
            node = nodeList.item(0);
            this.STR_CD4_PERCENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ALLERGY_COMMENT");
            node = nodeList.item(0);
            this.STR_ALLERGY_COMMENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_PLAN_COMMENT");
            node = nodeList.item(0);
            this.STR_TREATMENT_PLAN_COMMENT = node.getFirstChild().getNodeValue();           
            nodeList = concepts.getElementsByTagName("STR_PREV_TREATMENT_DURATION_IN_MONTHS");
            node = nodeList.item(0);
            this.STR_PREV_TREATMENT_DURATION_IN_MONTHS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PREV_REGISTRATION_NUM");
            node = nodeList.item(0);
            this.STR_PREV_REGISTRATION_NUM = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PREV_TREATMENT_CENTER");
            node = nodeList.item(0);
            this.STR_PREV_TREATMENT_CENTER = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_REFERRED_BY");
            node = nodeList.item(0);
            this.STR_REFERRED_BY = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TRANSFERRED_TO");
            node = nodeList.item(0);
            this.STR_TRANSFERRED_TO = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TRANSFERRED_FROM");
            node = nodeList.item(0);
            this.STR_TRANSFERRED_FROM = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PHONE_NUMBER");
            node = nodeList.item(0);
            this.STR_PHONE_NUMBER = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ON_ANTIRETROVIRALS");
            node = nodeList.item(0);
            this.STR_ON_ANTIRETROVIRALS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_NEXT_VISIT");
            node = nodeList.item(0);
            this.STR_NEXT_VISIT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PATIENT_CONTACT_TEST_RESULT_PARENT");
            node = nodeList.item(0);
            this.STR_PATIENT_CONTACT_TEST_RESULT_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SIMPLE_TB_TEST_RESULT");
            node = nodeList.item(0);
            this.STR_SIMPLE_TB_TEST_RESULT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SIMPLE_TB_TEST_TYPE");
            node = nodeList.item(0);
            this.STR_SIMPLE_TB_TEST_TYPE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_PATIENT_CONTACT_KNOWN_MDR_CASE");
            node = nodeList.item(0);
            this.STR_PATIENT_CONTACT_KNOWN_MDR_CASE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SUSPECTED_MDR_TUBERCULOSIS");
            node = nodeList.item(0);
            this.STR_SUSPECTED_MDR_TUBERCULOSIS = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SMEAR_CONVERSION");
            node = nodeList.item(0);
            this.STR_SMEAR_CONVERSION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SMEAR_RECONVERSION");
            node = nodeList.item(0);
            this.STR_SMEAR_RECONVERSION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SCANTY");
            node = nodeList.item(0);
            this.STR_SCANTY = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_OTHER_MYCOBACTERIA_NONCODED");
            node = nodeList.item(0);
            this.STR_OTHER_MYCOBACTERIA_NONCODED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CURRENT_TREATMENT_TYPE");
            node = nodeList.item(0);
            this.STR_CURRENT_TREATMENT_TYPE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_STANDARDIZED");
            node = nodeList.item(0);
            this.STR_STANDARDIZED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_EMPIRIC");
            node = nodeList.item(0);
            this.STR_EMPIRIC = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_INDIVIDUALIZED");
            node = nodeList.item(0);
            this.STR_INDIVIDUALIZED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_STOP_DATE");
            node = nodeList.item(0);
            this.STR_TREATMENT_STOP_DATE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_RESISTANT");
            node = nodeList.item(0);
            this.STR_RESISTANT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SUSCEPTIBLE");
            node = nodeList.item(0);
            this.STR_SUSCEPTIBLE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_INTERMEDIATE");
            node = nodeList.item(0);
            this.STR_INTERMEDIATE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_RESULT");
            node = nodeList.item(0);
            this.STR_DST_RESULT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT_CONSTRUCT");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT_CONSTRUCT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT_MEDICATION");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT_MEDICATION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT_MEDICATION_NON_CODED");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT_MEDICATION_NON_CODED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT_NON_CODED");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT_NON_CODED = node.getFirstChild().getNodeValue();  
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT_DATE");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT_DATE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_ADVERSE_EFFECT_ACTION_TAKEN");
            node = nodeList.item(0);
            this.STR_ADVERSE_EFFECT_ACTION_TAKEN = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TREATMENT_SUPPORTER_ACTIVE");
            node = nodeList.item(0);
            this.STR_TREATMENT_SUPPORTER_ACTIVE = node.getFirstChild().getNodeValue();
            
            nodeList = concepts.getElementsByTagName("STR_SPECIMEN_ID");
            node = nodeList.item(0);
            this.STR_SPECIMEN_ID = node.getFirstChild().getNodeValue();
            
            nodeList = concepts.getElementsByTagName("STR_TEST_DATE_ORDERED");
            node = nodeList.item(0);
            this.STR_TEST_DATE_ORDERED = node.getFirstChild().getNodeValue();
            
            nodeList = concepts.getElementsByTagName("STR_TEST_COMMENTS");
            node = nodeList.item(0);
            this.STR_TEST_COMMENTS = node.getFirstChild().getNodeValue();
            
            nodeList = concepts.getElementsByTagName("STR_SPECIMEN_COMMENTS");
            node = nodeList.item(0);
            this.STR_SPECIMEN_COMMENTS = node.getFirstChild().getNodeValue();
            
            //pre-set concepts
            
            String[] allXMLNodes = {STR_TB_SAMPLE_SOURCE, 
                    STR_RESULT_DATE, 
                    STR_DATE_RECEIVED, 
                    STR_CULTURE_START_DATE,
                    STR_TYPE_OF_ORGANISM_NON_CODED, 
                    STR_TYPE_OF_ORGANISM, 
                    STR_COLONIES, 
                    STR_SMEAR_PARENT, 
                    STR_TB_SMEAR_RESULT, 
                    STR_BACILLI, 
                    STR_TB_SMEAR_MICROSCOPY_METHOD, 
                    STR_CULTURE_PARENT, 
                    STR_TB_CULTURE_RESULT, 
                    STR_TB_CULTURE_METHOD, 
                    STR_SCANTY, 
                    STR_DST_PARENT, 
                    STR_DST_COMPLETE, 
                    STR_DST_METHOD, 
                    STR_DST_RESULT, 
                    STR_DIRECT_INDIRECT, 
                    STR_SPUTUM_COLLECTION_DATE, 
                    STR_COLONIES_IN_CONTROL, 
                    STR_DST_RESULT_PARENT,
                    STR_RESISTANT, 
                    STR_SUSCEPTIBLE, 
                    STR_INTERMEDIATE, 
                    STR_CONCENTRATION, 
                    STR_TREATMENT_OUTCOME_PARENT, 
                    STR_CURED, 
                    STR_TREATMENT_COMPLETE, 
                    STR_FAILED, 
                    STR_DEFAULTED, 
                    STR_DIED, 
                    STR_TRANSFERRED_OUT, 
                    STR_STILL_ON_TREATMENT, 
                    STR_CULTURE_STATUS_PARENT, 
                    STR_NOT_CONVERTED, 
                    STR_CONVERTED, 
                    STR_RECONVERTED, 
                    STR_NONE, 
                    STR_TUBERCULOSIS_PATIENT_STATUS_PARENT, 
                    STR_ON_TREATMENT, 
                    STR_SUSPENDED, 
                    STR_WAITING_FOR_TREATMENT, 
                    STR_TREATMENT_COMPLETE, 
                    STR_CULTURE_CONVERSION, 
                    STR_CULTURE_RECONVERSION, 
                    STR_SMEAR_CONVERSION, 
                    STR_SMEAR_RECONVERSION, 
                    STR_PREVIOUS_DRUG_USE_PARENT, 
                    STR_NEW_MDRTB_PATIENT, 
                    STR_PREVIOUSLY_TREATED_WITH_FIRST_LINE, 
                    STR_PREVIOUSLY_TREATED_WITH_SECOND_LINE, 
                    STR_PREVIOUS_TREATMENT_RESULT_PARENT, 
                    STR_RELAPSE, 
                    STR_TREATMENT_AFTER_DEFAULT, 
                    STR_TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT, 
                    STR_TREATMENT_AFTER_FAILURE_OF_RE_TREATMENT, 
                    STR_TRANSFER_IN, 
                    STR_OTHER_MDR_STATE, 
                    STR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT, 
                    STR_TUBERCULOSIS, 
                    STR_MDR_TUBERCULOSIS, 
                    STR_XDR_TUBERCULOSIS, 
                    STR_SUSPECTED_MDR_TUBERCULOSIS, 
                    STR_HIV_STATUS,STR_CD4_COUNT, 
                    STR_CD4_PERCENT, 
                    STR_ON_ANTIRETROVIRALS, 
                    STR_TREATMENT_START_DATE, 
                    STR_PULMONARY, 
                    STR_EXTRAPULMONARY, 
                    STR_EXTRAPULMONARY_LOCATION, 
                    STR_ALLERGY_COMMENT, 
                    STR_TREATMENT_PLAN_COMMENT, 
                    STR_NEXT_VISIT, 
                    STR_PHONE_NUMBER, 
                    STR_OTHER_MYCOBACTERIA_NONCODED, 
                    STR_PREV_TREATMENT_DURATION_IN_MONTHS, 
                    STR_PREV_REGISTRATION_NUM, 
                    STR_PREV_TREATMENT_CENTER, 
                    STR_REFERRED_BY, 
                    STR_TRANSFERRED_TO,
                    STR_TRANSFERRED_FROM, 
                    STR_PATIENT_CONTACT_TEST_RESULT_PARENT, 
                    STR_SIMPLE_TB_TEST_RESULT,
                    STR_SIMPLE_TB_TEST_TYPE,
                    STR_PATIENT_CONTACT_KNOWN_MDR_CASE, 
                    STR_CURRENT_TREATMENT_TYPE, 
                    STR_STANDARDIZED,
                    STR_EMPIRIC, 
                    STR_INDIVIDUALIZED, 
                    STR_TREATMENT_STOP_DATE,
                    STR_ADVERSE_EFFECT_CONSTRUCT,
                    STR_ADVERSE_EFFECT_MEDICATION,
                    STR_ADVERSE_EFFECT_MEDICATION_NON_CODED,
                    STR_ADVERSE_EFFECT,
                    STR_ADVERSE_EFFECT_NON_CODED,
                    STR_ADVERSE_EFFECT_DATE,
                    STR_ADVERSE_EFFECT_ACTION_TAKEN,
                    STR_TREATMENT_SUPPORTER_ACTIVE,
                    STR_SPECIMEN_ID,
                    STR_TEST_DATE_ORDERED,
                    STR_TEST_COMMENTS,
                    STR_SPECIMEN_COMMENTS};

            
            List<String> stList = new ArrayList<String>();
            for (int i = 0; i < allXMLNodes.length; i++){
                stList.add(allXMLNodes[i]);
            }
            
            //TODO: remove these after a while:
            MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(1453), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "MULTIDRUG-RESISTANT TB TREATMENT START DATE");
            MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(730), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "CD4 PERCENT");
            MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(3024), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "TYPE OF ORGANISM");
            MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(3048), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "TUBERCULOSIS CULTURE CONSTRUCT"); 
            MdrtbUtil.repairMdrtrbConceptMapping(Context.getConceptService().getConcept(1565), "DIED - TB", "DIED - TB");
            
            
            //MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            ConceptService cService = Context.getConceptService();
            ConceptSource cs = cService.getConceptSourceByName("org.openmrs.module.mdrtb");
            List<ConceptMap> cms = cService.getConceptsByConceptSource(cs);
            for (ConceptMap cm: cms){
                Concept c = cm.getConcept();
                initializeEverythingAboutConcept(c);
                //System.out.println("Initializing MDRTB Metadata: Concept " + c + " " + c.getName().getName());
                xmlConceptList.put(cm.getSourceCode(), c);
            }
           
            
          //TODO: the following is for the concept switch from tuberculosis drug treatment start date to mdrtb drug treatment start date
            // once botwsana, haiti, pakistan have been upgraded, no worries.
            if (xmlConceptList.get(STR_TREATMENT_START_DATE) == null){
                Concept c = cService.getConceptByName(STR_TREATMENT_START_DATE);
                if (c != null)
                    xmlConceptList.put(STR_TREATMENT_START_DATE, c);
                else
                    throw new RuntimeException("Unable to load concept MULTIDRUG-RESISTANT TB TREATMENT START DATE");
            }
            
            if (xmlConceptList.get(STR_CD4_PERCENT) == null){
                Concept c = cService.getConceptByName(STR_CD4_PERCENT);
                if (c != null)
                    xmlConceptList.put(STR_CD4_PERCENT, c);
                else
                    throw new RuntimeException("Unable to load concept CD4%");
            }
            
            if (xmlConceptList.get(STR_DIED) == null){
                Concept c = cService.getConceptByName(STR_DIED);
                if (c != null)
                    xmlConceptList.put(STR_DIED, c);
                else {
                     throw new RuntimeException("Unable to load concept DIED - TB");
                }
            }
            
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    
    public Concept getConceptAdverseEffectConstruct(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT_CONSTRUCT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAdverseEffectMedication(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT_MEDICATION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAdverseEffectMedicationNonCoded(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT_MEDICATION_NON_CODED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAdverseEffect(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAdverseEffectNonCoded(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT_NON_CODED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAdverseEffectDate(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT_DATE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAdverseEffectActionTaken(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_ADVERSE_EFFECT_ACTION_TAKEN, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSmearMicroscopyMethod(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_TB_SMEAR_MICROSCOPY_METHOD, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptResultDate(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_RESULT_DATE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptBacilli(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_BACILLI, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptSampleSource(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_TB_SAMPLE_SOURCE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptTreatmentStopDate(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_TREATMENT_STOP_DATE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptDSTComplete(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_DST_COMPLETE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptDSTMethod(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_DST_METHOD, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptNone(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_NONE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }

    public Concept getConceptSputumCollectionDate(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_SPUTUM_COLLECTION_DATE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptDirectIndirect(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_DIRECT_INDIRECT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptColoniesInControl(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_COLONIES_IN_CONTROL, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptStandardized(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_STANDARDIZED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptEmpiric(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_EMPIRIC, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptIndividualized(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_INDIVIDUALIZED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptCurrentRegimenType(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_CURRENT_TREATMENT_TYPE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptScanty(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SCANTY, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptOtherMycobacteriaNonCoded(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_OTHER_MYCOBACTERIA_NONCODED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptDSTParent(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_DST_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptCultureParent(){
        Concept ret = null;
        ret =  this.getMDRTBConceptByKey(STR_CULTURE_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptCultureResult(){
        Concept ret = null;
        ret =  this.getMDRTBConceptByKey(STR_TB_CULTURE_RESULT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSmearResult(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TB_SMEAR_RESULT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptColonies(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_COLONIES, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }

    public Concept getConceptCultureStartDate(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_CULTURE_START_DATE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptCultureMethod(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TB_CULTURE_METHOD, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptSmearParent(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SMEAR_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptCultureConversion(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_CULTURE_CONVERSION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptCultureReconversion(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_CULTURE_RECONVERSION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSmearConversion(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SMEAR_CONVERSION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSmearReconversion(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SMEAR_RECONVERSION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    //STR_TYPE_OF_ORGANISM
    public Concept getConceptTypeOfOrganism(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TYPE_OF_ORGANISM, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptTypeOfOrganismNonCoded(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_TYPE_OF_ORGANISM_NON_CODED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    //STR_DST_RESULT_PARENT
    public Concept getConceptDSTResultParent(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(this.STR_DST_RESULT_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptDateReceived(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_DATE_RECEIVED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptTreatmentStartDate(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TREATMENT_START_DATE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }

    
    public Concept getConceptConcentration(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_CONCENTRATION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptPatientClassDrugUse(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PREVIOUS_DRUG_USE_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptPatientClassPrevTreatment(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PREVIOUS_TREATMENT_RESULT_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptTBCaseClassification(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptPulmonary(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PULMONARY, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptExtraPulmonary(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_EXTRAPULMONARY, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptExtraPulmonaryLocation(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_EXTRAPULMONARY_LOCATION, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
 
    public Concept getConceptHIVStatus(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_HIV_STATUS, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptCD4(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_CD4_COUNT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptContactTestResultParent(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PATIENT_CONTACT_TEST_RESULT_PARENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSimpleTBResult(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SIMPLE_TB_TEST_RESULT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSimpleTBTestType(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SIMPLE_TB_TEST_TYPE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
      
    public Concept getConceptNextVisit(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_NEXT_VISIT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptOnART(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_ON_ANTIRETROVIRALS, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptPhoneNumber(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PHONE_NUMBER, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptKnownMDRCase(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PATIENT_CONTACT_KNOWN_MDR_CASE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }

    public Concept getConceptCD4Percent(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_CD4_PERCENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
   
    public Concept getConceptTreatmentPlanComment(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TREATMENT_PLAN_COMMENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptAllergyComment(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_ALLERGY_COMMENT, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptPrevDuration(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PREV_TREATMENT_DURATION_IN_MONTHS, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptPrevRegNum(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PREV_REGISTRATION_NUM, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptPrevTreatmentCenter(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_PREV_TREATMENT_CENTER, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptPrevReferredBy(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_REFERRED_BY, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptStartDate(){
    	return getConceptCultureStartDate();
    }
    
    public Concept getConceptTransferredTo(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TRANSFERRED_TO, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptTransferredFrom(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TRANSFERRED_FROM, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    public Concept getConceptTreatmentSupporterActive(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TREATMENT_SUPPORTER_ACTIVE, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSuspectedMDR(){
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SUSPECTED_MDR_TUBERCULOSIS, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptDiedMDR() {
        Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_DIED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSpecimenID() {
    	Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SPECIMEN_ID, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptDateOrdered() {
    	Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TEST_DATE_ORDERED, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptTestComments() {
    	Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_TEST_COMMENTS, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    public Concept getConceptSpecimenComments() {
    	Concept ret = null;
        ret = this.getMDRTBConceptByKey(STR_SPECIMEN_COMMENTS, new Locale("en", "US"), this.xmlConceptList);
        return ret;
    }
    
    /**
     * The follow concepts are pulled using mdrtb concept mappings, not from the concept list this factory creates.
     * We can probably switch all the "getConcept" methods to work in this manner
     */
    
    public Concept getConceptSusceptibleToTuberculosisDrug() {
    	return Context.getConceptService().getConceptByMapping("SUSCEPTIBLE TO TUBERCULOSIS DRUG", "org.openmrs.module.mdrtb");
    }
    
    public Concept getConceptIntermediateToTuberculosisDrug() {
    	return Context.getConceptService().getConceptByMapping("INTERMEDIATE TO TUBERCULOSIS DRUG", "org.openmrs.module.mdrtb");
    }
    
    public Concept getConceptResistantToTuberculosisDrug() {
    	return Context.getConceptService().getConceptByMapping("RESISTANT TO TUBERCULOSIS DRUG", "org.openmrs.module.mdrtb");
    }
    
    public Concept getConceptWaitingForTestResults() {
    	return Context.getConceptService().getConceptByMapping("waitingForTestResults", "org.openmrs.module.mdrtb");
    }
    
    public Concept getConceptDstTestContaminated() {
    	return Context.getConceptService().getConceptByMapping("dstTestContaminated", "org.openmrs.module.mdrtb");
    }
    
    public Concept getConceptScannedLabReport() {
    	return Context.getConceptService().getConceptByMapping("scannedLabReport", "org.openmrs.module.mdrtb");
    }
    
    public Set<Concept> getMdrProgramOutcomeConcepts() {
    	Set<Concept> finalOutcomes = new HashSet<Concept>();
    	for (ProgramWorkflowState s : getStatesOutcomes()) {
    		if (!s.getConcept().isNamed(STR_STILL_ON_TREATMENT)) {
    			finalOutcomes.add(s.getConcept());
    		}
    	}
    	return finalOutcomes;
    }
    
    /**
     * 
     * Returns the mdrtb program name from global properties
     * 
     * @return
     */
    public Program getMDRTBProgram(){
        AdministrationService as = Context.getAdministrationService();
        String programString = as.getGlobalProperty("mdrtb.program_name");
        return Context.getProgramWorkflowService().getProgramByName(programString);
    }
    
    public ProgramWorkflow getOutcomeWorkflow(){
        Program program = this.getMDRTBProgram();
        return program.getWorkflowByName(this.STR_TREATMENT_OUTCOME_PARENT);
    }
    
    public ProgramWorkflow getPatientStatusWorkflow(){
        Program program = this.getMDRTBProgram();
        return program.getWorkflowByName(this.STR_TUBERCULOSIS_PATIENT_STATUS_PARENT);
    }
    
    public Set<ProgramWorkflowState> getStatesOutcomes(){
    	return getOutcomeWorkflow().getSortedStates();
    }
    
    public Set<ProgramWorkflowState> getStatesCultureStatus(){
        Program program = this.getMDRTBProgram();
        ProgramWorkflow pw = program.getWorkflowByName(this.STR_CULTURE_STATUS_PARENT);
        return  pw.getSortedStates();
    }
    public Set<ProgramWorkflowState> getStatesPatientStatus(){
        Program program = this.getMDRTBProgram();
        ProgramWorkflow pw = program.getWorkflowByName(this.STR_TUBERCULOSIS_PATIENT_STATUS_PARENT);
        return  pw.getSortedStates();
    }

    public String getSTR_ON_ANTIRETROVIRALS() {
        return STR_ON_ANTIRETROVIRALS;
    }

    public String getSTR_NEXT_VISIT() {
        return STR_NEXT_VISIT;
    }
    
    public String getSTR_TB_SMEAR_RESULT() {
        return STR_TB_SMEAR_RESULT;
    }

    public String getSTR_TB_SAMPLE_SOURCE() {
        return STR_TB_SAMPLE_SOURCE;
    }

    public String getSTR_BACILLI() {
        return STR_BACILLI;
    }

    public String getSTR_RESULT_DATE() {
        return STR_RESULT_DATE;
    }

    public String getSTR_DATE_RECEIVED() {
        return STR_DATE_RECEIVED;
    }

    public String getSTR_TB_SMEAR_MICROSCOPY_METHOD() {
        return STR_TB_SMEAR_MICROSCOPY_METHOD;
    }

    public String getSTR_TB_CULTURE_RESULT() {
        return STR_TB_CULTURE_RESULT;
    }

    public String getSTR_COLONIES() {
        return STR_COLONIES;
    }

    public String getSTR_CULTURE_START_DATE() {
        return STR_CULTURE_START_DATE;
    }

    public String getSTR_TB_CULTURE_METHOD() {
        return STR_TB_CULTURE_METHOD;
    }

    public String getSTR_TYPE_OF_ORGANISM() {
        return STR_TYPE_OF_ORGANISM;
    }

    public String getSTR_TYPE_OF_ORGANISM_NON_CODED() {
        return STR_TYPE_OF_ORGANISM_NON_CODED;
    }

    public String getSTR_DST_COMPLETE() {
        return STR_DST_COMPLETE;
    }

    public String getSTR_DST_METHOD() {
        return STR_DST_METHOD;
    }

    public String getSTR_DIRECT_INDIRECT() {
        return STR_DIRECT_INDIRECT;
    }

    public String getSTR_COLONIES_IN_CONTROL() {
        return STR_COLONIES_IN_CONTROL;
    }

    public String getSTR_CONCENTRATION() {
        return STR_CONCENTRATION;
    }

    public String getSTR_DST_PARENT() {
        return STR_DST_PARENT;
    }

    public String getSTR_DST_RESULT_PARENT() {
        return STR_DST_RESULT_PARENT;
    }

    public String getSTR_CULTURE_PARENT() {
        return STR_CULTURE_PARENT;
    }

    public String getSTR_SMEAR_PARENT() {
        return STR_SMEAR_PARENT;
    }

    public String getSTR_SPUTUM_COLLECTION_DATE() {
        return STR_SPUTUM_COLLECTION_DATE;
    }

    public String getSTR_TREATMENT_OUTCOME_PARENT() {
        return STR_TREATMENT_OUTCOME_PARENT;
    }

    public String getSTR_CURED() {
        return STR_CURED;
    }

    public String getSTR_FAILED() {
        return STR_FAILED;
    }

    public String getSTR_DEFAULTED() {
        return STR_DEFAULTED;
    }

    public String getSTR_DIED() {
        return STR_DIED;
    }

    public String getSTR_TRANSFERRED_OUT() {
        return STR_TRANSFERRED_OUT;
    }

    public String getSTR_STILL_ON_TREATMENT() {
        return STR_STILL_ON_TREATMENT;
    }

    public String getSTR_CULTURE_STATUS_PARENT() {
        return STR_CULTURE_STATUS_PARENT;
    }

    public String getSTR_NOT_CONVERTED() {
        return STR_NOT_CONVERTED;
    }

    public String getSTR_CONVERTED() {
        return STR_CONVERTED;
    }

    public String getSTR_RECONVERTED() {
        return STR_RECONVERTED;
    }

    public String getSTR_NONE() {
        return STR_NONE;
    }

    public String getSTR_TUBERCULOSIS_PATIENT_STATUS_PARENT() {
        return STR_TUBERCULOSIS_PATIENT_STATUS_PARENT;
    }

    public String getSTR_ON_TREATMENT() {
        return STR_ON_TREATMENT;
    }

    public String getSTR_SUSPENDED() {
        return STR_SUSPENDED;
    }

    public String getSTR_WAITING_FOR_TREATMENT() {
        return STR_WAITING_FOR_TREATMENT;
    }

    public String getSTR_TREATMENT_COMPLETE() {
        return STR_TREATMENT_COMPLETE;
    }

    public String getSTR_CULTURE_CONVERSION() {
        return STR_CULTURE_CONVERSION;
    }

    public String getSTR_CULTURE_RECONVERSION() {
        return STR_CULTURE_RECONVERSION;
    }
    
    public String getSTR_TREATMENT_START_DATE() {
        return STR_TREATMENT_START_DATE;
    }

    public String getSTR_PREVIOUS_DRUG_USE_PARENT() {
        return STR_PREVIOUS_DRUG_USE_PARENT;
    }

    public String getSTR_NEW_MDRTB_PATIENT() {
        return STR_NEW_MDRTB_PATIENT;
    }

    public String getSTR_PREVIOUSLY_TREATED_WITH_FIRST_LINE() {
        return STR_PREVIOUSLY_TREATED_WITH_FIRST_LINE;
    }

    public String getSTR_PREVIOUSLY_TREATED_WITH_SECOND_LINE() {
        return STR_PREVIOUSLY_TREATED_WITH_SECOND_LINE;
    }

    public String getSTR_PREVIOUS_TREATMENT_RESULT_PARENT() {
        return STR_PREVIOUS_TREATMENT_RESULT_PARENT;
    }

    public String getSTR_RELAPSE() {
        return STR_RELAPSE;
    }

    public String getSTR_TREATMENT_AFTER_DEFAULT() {
        return STR_TREATMENT_AFTER_DEFAULT;
    }

    public String getSTR_TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT() {
        return STR_TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT;
    }

    public String getSTR_TREATMENT_AFTER_FAILURE_OF_RE_TREATMENT() {
        return STR_TREATMENT_AFTER_FAILURE_OF_RE_TREATMENT;
    }

    public String getSTR_TRANSFER_IN() {
        return STR_TRANSFER_IN;
    }

    public String getSTR_OTHER_MDR_STATE() {
        return STR_OTHER_MDR_STATE;
    }

    public String getSTR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT() {
        return STR_TUBERCULOSIS_CASE_CLASSIFICATION_PARENT;
    }

    public String getSTR_TUBERCULOSIS() {
        return STR_TUBERCULOSIS;
    }

    public String getSTR_MDR_TUBERCULOSIS() {
        return STR_MDR_TUBERCULOSIS;
    }

    public String getSTR_XDR_TUBERCULOSIS() {
        return STR_XDR_TUBERCULOSIS;
    }

    public String getSTR_PULMONARY() {
        return STR_PULMONARY;
    }

    public String getSTR_EXTRAPULMONARY() {
        return STR_EXTRAPULMONARY;
    }

    public String getSTR_EXTRAPULMONARY_LOCATION() {
        return STR_EXTRAPULMONARY_LOCATION;
    }



    public String getSTR_HIV_STATUS() {
        return STR_HIV_STATUS;
    }

    public String getSTR_CD4_COUNT() {
        return STR_CD4_COUNT;
    }

    public String getSTR_CD4_PERCENT() {
        return STR_CD4_PERCENT;
    }

    public String getSTR_ALLERGY_COMMENT() {
        return STR_ALLERGY_COMMENT;
    }

    public String getSTR_TREATMENT_PLAN_COMMENT() {
        return STR_TREATMENT_PLAN_COMMENT;
    }


    public String getSTR_PREV_TREATMENT_DURATION_IN_MONTHS() {
        return STR_PREV_TREATMENT_DURATION_IN_MONTHS;
    }

    public String getSTR_PREV_REGISTRATION_NUM() {
        return STR_PREV_REGISTRATION_NUM;
    }

    public String getSTR_PREV_TREATMENT_CENTER() {
        return STR_PREV_TREATMENT_CENTER;
    }

    public String getSTR_REFERRED_BY() {
        return STR_REFERRED_BY;
    }

    public String getSTR_TRANSFERRED_TO() {
        return STR_TRANSFERRED_TO;
    }

    public String getSTR_TRANSFERRED_FROM() {
        return STR_TRANSFERRED_FROM;
    }

    public String getSTR_PATIENT_CONTACT_TEST_RESULT_PARENT() {
        return STR_PATIENT_CONTACT_TEST_RESULT_PARENT;
    }

    public String getSTR_SIMPLE_TB_TEST_RESULT() {
        return STR_SIMPLE_TB_TEST_RESULT;
    }

    public String getSTR_SIMPLE_TB_TEST_TYPE() {
        return STR_SIMPLE_TB_TEST_TYPE;
    }

    public String getSTR_PHONE_NUMBER() {
        return STR_PHONE_NUMBER;
    }

    public String getSTR_PATIENT_CONTACT_KNOWN_MDR_CASE() {
        return STR_PATIENT_CONTACT_KNOWN_MDR_CASE;
    }

    public String getSTR_SUSPECTED_MDR_TUBERCULOSIS() {
        return STR_SUSPECTED_MDR_TUBERCULOSIS;
    }

    public String getSTR_SCANTY() {
        return STR_SCANTY;
    }

    public String getSTR_OTHER_MYCOBACTERIA_NONCODED() {
        return STR_OTHER_MYCOBACTERIA_NONCODED;
    }

    public String getSTR_CURRENT_TREATMENT_TYPE() {
        return STR_CURRENT_TREATMENT_TYPE;
    }

    public String getSTR_STANDARDIZED() {
        return STR_STANDARDIZED;
    }

    public String getSTR_EMPIRIC() {
        return STR_EMPIRIC;
    }

    public String getSTR_INDIVIDUALIZED() {
        return STR_INDIVIDUALIZED;
    }


    
    public String getSTR_SMEAR_CONVERSION() {
        return STR_SMEAR_CONVERSION;
    }

    public String getSTR_SMEAR_RECONVERSION() {
        return STR_SMEAR_RECONVERSION;
    }

    public String getSTR_TREATMENT_STOP_DATE() {
        return STR_TREATMENT_STOP_DATE;
    }

    public String getSTR_DST_RESULT() {
        return STR_DST_RESULT;
    }

    public String getSTR_SUSCEPTIBLE() {
        return STR_SUSCEPTIBLE;
    }

    public String getSTR_INTERMEDIATE() {
        return STR_INTERMEDIATE;
    }

    public String getSTR_RESISTANT() {
        return STR_RESISTANT;
    }

    public String getSTR_ADVERSE_EFFECT_CONSTRUCT() {
        return STR_ADVERSE_EFFECT_CONSTRUCT;
    }

    public String getSTR_ADVERSE_EFFECT_MEDICATION() {
        return STR_ADVERSE_EFFECT_MEDICATION;
    }

    public String getSTR_ADVERSE_EFFECT_MEDICATION_NON_CODED() {
        return STR_ADVERSE_EFFECT_MEDICATION_NON_CODED;
    }

    public String getSTR_ADVERSE_EFFECT() {
        return STR_ADVERSE_EFFECT;
    }

    public String getSTR_ADVERSE_EFFECT_NON_CODED() {
        return STR_ADVERSE_EFFECT_NON_CODED;
    }

    public String getSTR_ADVERSE_EFFECT_DATE() {
        return STR_ADVERSE_EFFECT_DATE;
    }

    public String getSTR_ADVERSE_EFFECT_ACTION_TAKEN() {
        return STR_ADVERSE_EFFECT_ACTION_TAKEN;
    }
    
    public void setSTR_TREATMENT_STOP_DATE(String str_treatment_stop_date) {
        STR_TREATMENT_STOP_DATE = str_treatment_stop_date;
    }
    
    
    /**
     * Gets a map of all DSTs for a patient (the Obs is the parentObs) and sputumCollectionDate
     * 
     * @param patient
     * @return
     */
    public Map<Obs, Date> getDSTs(Patient patient){
        Map<Obs, Date> map = new LinkedHashMap<Obs,Date>();
        Map<Obs, Date> ret = new LinkedHashMap<Obs,Date>();
        Concept parentConcept = getConceptDSTParent();
        List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(patient, parentConcept);
        List<Date> dates = new ArrayList<Date>();
        if (obsList != null){
            for (Obs o:obsList){
                map.put(o, getSputumCollectionDateDST(o));
                dates.add(getSputumCollectionDateDST(o));
            }
        }

        Collections.sort(dates);
        for (Date d:dates){
            for (Map.Entry<Obs, Date> ent : map.entrySet()) {
                if (d.getTime() == ent.getValue().getTime())
                    ret.put(ent.getKey(), ent.getValue());
            }
        }
        return ret;
    }
    
    /**
     * 
     * Get cultures ordered by 
     * 
     * @param patient
     * @return
     */
      public Map<Obs, Date> getSmears(Patient patient){
          Map<Obs, Date> map = new LinkedHashMap<Obs,Date>();
          Map<Obs, Date> ret = new LinkedHashMap<Obs,Date>();
          Concept parentConcept = getConceptSmearParent();
          List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(patient, parentConcept);
          List<Date> dates = new ArrayList<Date>();
          if (obsList != null){
              for (Obs o:obsList){
                  Date dateTmp = getSputumCollectionDateSmear(o);
                  if (dateTmp != null){
                      map.put(o, dateTmp);
                      dates.add(dateTmp);
                  }
              }
          }
          if (dates.size() > 0)
          Collections.sort(dates);
          for (Date d:dates){
              for (Map.Entry<Obs, Date> ent : map.entrySet()) {
                  if (d.getTime() == ent.getValue().getTime())
                      ret.put(ent.getKey(), ent.getValue());
              }
          }
          return ret;
      }
      

    
    /**
     * 
     * Returns the sputumCollectionDate out of a DST
     * 
     * @param obs
     * @return
     */
    public Date getSputumCollectionDateDST(Obs obs){
        Date date = null;
        for (Obs o : obs.getGroupMembers()){
            Concept sputumCollectionConcept = this.getConceptSputumCollectionDate();
            if (o.getConcept().equals(sputumCollectionConcept)){
                date = o.getValueDatetime();
                break;
            }    
        }  
        return date;
    }
    
    
    
    /**
     * 
     * Returns the sputumCollectionDate out of a DST
     * 
     * @param obs
     * @return
     */
    public Date getSputumCollectionDateSmear(Obs obs){
        Date date = null;
        for (Obs o : obs.getGroupMembers()){
            Concept sputumCollectionConcept = this.getConceptSmearResult();
            if (o.getConcept().equals(sputumCollectionConcept)){
                date = o.getValueDatetime();
                break;
            }    
        }  
        return date;
    }
    


    
    
   
    
    
    

     /**
      * 
      * This is a scheduler routine for all patients;
      *
      */
     public void createCultureConversionsAllPatients(){
         PatientService ps = Context.getPatientService();
         ObsService os = Context.getObsService();
         
         List<Concept> cList = new ArrayList<Concept>();
         cList.add(this.getConceptCultureParent());
         List<Obs> oListTmp = os.getObservations(null, null, cList, null, null, null, null, null, null, null, null, false);
         List<Patient> pList = new ArrayList<Patient>();
         for (Obs o:oListTmp){
             if (!pList.contains(ps.getPatient(o.getPerson().getPersonId())))
                 pList.add(ps.getPatient(o.getPerson().getPersonId()));
         }
         for(Patient p : pList){
             MdrtbUtil.fixCultureConversions(p, this);
         }
     }
     
     
     
    
     
     /**
      * 
      * Sync the patient state for culture status with the culture conversion obs.
      * 
      * @param p
      * @param os
      * @param program
      */
     public void syncCultureStatus(Patient p, ObsService os){
         Program program = this.getMDRTBProgram();
         List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(p, program, null, null, null, null, false);
         Concept cCulture = this.getConceptCultureResult();
         //the patient must be enrolled in the mdrtb program for this to run:
         if (programs.size() > 0){
             
             Concept cc = this.getConceptCultureConversion();
             Concept rc = this.getConceptCultureReconversion();
             ProgramWorkflowService pws = Context.getProgramWorkflowService();
             
           //get the latest patientprogram:
             PatientProgram pp = programs.get(programs.size()-1);
             Set<ProgramWorkflowState> possibleStates = this.getStatesCultureStatus();
             
             //get all cc and rc obs
             List<Obs> ccs = os.getObservationsByPersonAndConcept(p, cc);
             List<Obs> rcs = os.getObservationsByPersonAndConcept(p, rc);
             Obs o = null;
             
             if (ccs != null && ccs.size() > 0){
                 o = ccs.get(0);
                 ccs.addAll(rcs);
                 for (Obs oTmp:ccs){
                     if (o.getValueDatetime().getTime() < oTmp.getValueDatetime().getTime())
                         o = oTmp;
                 }
             }

             if (o != null){
                 //this is the winning obs; change pp state accordingly, if necessary.
                 if (o.getConcept().equals(cc)){
                     
                     for (PatientState ps : pp.getStates()){
                         if (possibleStates.contains(ps.getState()) && !ps.getState().getConcept().equals(this.getMDRTBConceptByKey(STR_CONVERTED, new Locale("en", "US"), this.xmlConceptList))){
                             pp = transitionToStateNoErrorChecking(pp, ps.getState().getProgramWorkflow().getStateByName(this.STR_CONVERTED), o.getValueDatetime());
                             pws.savePatientProgram(pp);
                             break;
                         }
                     }
                     
                 } else {
                     
                     for (PatientState ps : pp.getStates()){
                         if (possibleStates.contains(ps.getState()) && !ps.getState().getConcept().equals(this.getMDRTBConceptByKey(STR_RECONVERTED, new Locale("en", "US"), this.xmlConceptList))){
                             pp = transitionToStateNoErrorChecking(pp, ps.getState().getProgramWorkflow().getStateByName(this.STR_RECONVERTED), o.getValueDatetime());
                             pws.savePatientProgram(pp);
                             break;
                         }
                     }

                 }
                 
             } else {

                 Program mdrtbProgram = this.getMDRTBProgram();

                 Map<Obs, Date> cultureObs = MdrtbUtil.getCultures(p, this);
                 
                 for (PatientProgram ppTmp:programs){
                     Set<PatientState> pss = ppTmp.getStates();
                     boolean found = false;
                     if (pss != null){
                         for (PatientState ps : pss){
                             if (ps.getState().getProgramWorkflow().getConcept().getConceptId() == mdrtbProgram.getWorkflowByName(this.STR_CULTURE_STATUS_PARENT).getConcept().getConceptId()){
                                 found = true;
                                
                                 break;
                                 
                             }    
                          }
                     }

                         //figure out if there are any cultures in the current program:
                         List<Obs> relevantCultures = new ArrayList<Obs>();
                         boolean allCulturesAreNegative = true;
                         for (Map.Entry<Obs, Date> e : cultureObs.entrySet()) {
                             
                             Date startDate = ppTmp.getDateEnrolled();
                             Date endDate = ppTmp.getDateCompleted();
                             if (endDate == null)
                                     endDate = new Date();
                             
                             Calendar startCal = Calendar.getInstance();
                             Calendar endCal = Calendar.getInstance();
                             startCal.setTime(startDate);
                             endCal.setTime(endDate);
                             startCal.add(Calendar.MONTH, -2);
                             endCal.add(Calendar.MONTH, 3);
                             
                             endDate = endCal.getTime();
                             startDate = startCal.getTime();
                             if (e.getValue().before(endDate) && e.getValue().after(startDate)){
                                 relevantCultures.add(e.getKey());
                                 if (!MdrtbUtil.isNegativeBacteriology(e.getKey(), cCulture, this))
                                     allCulturesAreNegative = false;
                             }    
                         }
                         
                         
                         if (relevantCultures.size() == 0 || !found || allCulturesAreNegative == true){
                              try {
                                 Set<ProgramWorkflow> pw = ppTmp.getProgram().getWorkflows();
                                     for (ProgramWorkflow pwTmp:pw){
                                    
                                         if (pwTmp.getStateByName(this.getSTR_NONE()) != null){
                                          
                                             ppTmp = transitionToStateNoErrorChecking(ppTmp,pwTmp.getStateByName(this.getSTR_NONE()), ppTmp.getDateEnrolled());
                                             break;
                                         }  
                                     }
                                     ppTmp = pws.savePatientProgram(ppTmp);
                                 } catch (Exception ex){
                                     log.warn("Could not set patient state of not converted for new mdrtb program enrollee", ex);
                                 }
                            
                         
                         }  else {
                             try {
                                 Set<ProgramWorkflow> pw = ppTmp.getProgram().getWorkflows();
                                     for (ProgramWorkflow pwTmp:pw){
                                         ProgramWorkflowState state = pwTmp.getStateByName(this.getSTR_NOT_CONVERTED());
                                         if (state != null){
                                             ppTmp = transitionToStateNoErrorChecking(ppTmp, state, ppTmp.getDateEnrolled());
                                             break;
                                         }  
                                     }
                                     ppTmp = pws.savePatientProgram(ppTmp);
                                 } catch (Exception ex){
                                     log.warn("Could not set patient state of not converted for new mdrtb program enrollee", ex);
                                 }
                             
                         }
                 }
                 
             }
         }
     }
     
    
     
     /**
      * 
      * Enroll a patient in the mdrtb program
      * 
      * @param p  Patient
      */
     public void enrollPatientInMDRTBProgram(Patient p, Date dateEnrolled) {
         Program prog = this.getMDRTBProgram();
         ProgramWorkflowService pws = Context.getProgramWorkflowService();
         PatientProgram pp = new PatientProgram();
         pp.setCreator(Context.getAuthenticatedUser());
         pp.setDateEnrolled(dateEnrolled);
         pp.setPatient(p);
         pp.setProgram(prog);
         pp.setVoided(false);
         
         try{
             pp = pws.savePatientProgram(pp);   
             try {
             Set<ProgramWorkflow> pw = pp.getProgram().getWorkflows();
                 for (ProgramWorkflow pwTmp:pw){
                     if (pwTmp.getStateByName(this.getSTR_NONE()) != null){
                         pp.transitionToState(pwTmp.getStateByName(this.getSTR_NONE()), dateEnrolled);
                         break;
                     }
                         
                 }
                 pp = pws.savePatientProgram(pp);
             } catch (Exception ex){
                 log.warn("Could not set patient state of not converted for new mdrtb program enrollee", ex);
             }

         } catch (Exception ex) {
             log.warn("Unable to enroll patient in mdrtb program", ex);
         }
     }
     

     
    
 
     

     
     /**
      * 
      * Gets culture conversion date for a patient
      * 
      * @param p
      * @return
      */
     public Date getSmearConversionDate(Patient p){
         Date ret = null;

         Map<Obs,Date> map = this.getSmears(p);
         Set<Obs> ObsSet = map.keySet();
         List<Obs> obsList = new ArrayList<Obs>();
         Concept cSmear = this.getConceptSmearResult();
         for (Obs obs:ObsSet)
             obsList.add(obs);
         //ensure that there is an original positive bacteriology;  if not, how can there be conversion?:
         int posOfOriginalPositiveBac = -1;
         for (int i = 0; i < obsList.size(); i++){
             Obs oTmp = obsList.get(i);
             if (!MdrtbUtil.isNegativeBacteriology(oTmp, cSmear, this)){
                 posOfOriginalPositiveBac = i;
                 break;
             }
         }
         if (obsList.size() > MdrtbUtil.lookupConversionNumberConsecutive() && posOfOriginalPositiveBac >= 0){
             for (int i = obsList.size()-1; i > posOfOriginalPositiveBac + 1; i--){
                 Obs o = obsList.get(i);
                 Date date = map.get(o);
                 if (MdrtbUtil.isNegativeBacteriology(o, cSmear, this)){
                     // if this smear is negative:
                       int k = i - 1;
                       Calendar calcutoff = Calendar.getInstance();
                       calcutoff.setTime(date);
                       
                       //NOTE: this is a little lazy.  Ideally, we'd make sure that the interval between any set of 
                       // negative cultures that result in a culture conversion is < 60 days.
                       //However, the logic below is a decent approximation -- sequences that could break this signal a very badly run TB program...
                       //patients should probably just be marked as default by hand, anyway.
                       
                       calcutoff.add(Calendar.DAY_OF_MONTH, - (MdrtbUtil.lookupConversionInterval()*MdrtbUtil.lookupConversionNumberConsecutive()));  //60 days = defaulted
                       
                       Calendar calThirty = Calendar.getInstance();
                       calThirty.setTime(date);
                       calThirty.add(Calendar.DAY_OF_MONTH, - (MdrtbUtil.lookupConversionInterval()));
                       Date testDate = date;
                     int numConsecutive = 1;  
                     try {
                         while (k > 0 && testDate.after(calcutoff.getTime())) {
                             Obs oInner = obsList.get(k);
                             Date dateInner = map.get(oInner);
                           
                             
                             if (!MdrtbUtil.isNegativeBacteriology(oInner,cSmear, this))
                                     break;
                             else {
                                 numConsecutive++; //number of consecutive negatives is now 2
                                 if (dateInner.before(calThirty.getTime()) || dateInner.getTime() == calThirty.getTime().getTime()){
                                     Obs oPrev = obsList.get(k-1);
                                     if (!MdrtbUtil.isNegativeBacteriology(oPrev, cSmear, this) && numConsecutive >= MdrtbUtil.lookupConversionNumberConsecutive()){
                                         ret = dateInner;
                                         break;   
                                     }
                                 }
                             }
                             testDate = dateInner;
                             k--;
                         }
                     } catch (Exception ex) {}
                 } 
                 if (ret != null)
                     break;
             }
         }
         return ret;
     }
     
     /**
      * returns a reconversion date, and cleans up false smear conversions
      */
      public Date getSmearReconversionDate(Patient p){
         
         Date ret = null;
         Map<Obs,Date> map = this.getSmears(p);
         List<Obs> oList = Context.getObsService().getObservationsByPersonAndConcept(p, this.getConceptSmearConversion());      
         Concept cSmear = this.getConceptSmearResult();
         
         if (oList != null){
             for (int i = oList.size()-1; i >=0 ; i--){
                 Obs o = oList.get(i);
                 boolean startLooking = false;
                 for (Map.Entry<Obs,Date> ent : map.entrySet()){
                     if (!startLooking && ent.getValue().after(o.getValueDatetime()))
                         startLooking = true;
                     if (startLooking && !MdrtbUtil.isNegativeBacteriology(ent.getKey(), cSmear, this)){
                         Calendar ccCal = Calendar.getInstance();                
                         ccCal.setTime(o.getValueDatetime());
                      
                         ccCal.add(Calendar.DAY_OF_MONTH, (MdrtbUtil.lookupConversionInterval() - 1));
                         if (ent.getValue().after(ccCal.getTime())){
                         //if more than 30 day difference from smear conversion date,a positive smear is reconversion
                            return ent.getValue();
                         } else {
                             // if less than 30 days, there was no smear conversion.
                            Context.getObsService().voidObs(o, "Voided by reconversion date routine -- positive smear within 30 days of conversion obs");
                            return null;
                         }
                     }  
                 }
             } 
         }
         return ret;
     }
      
      /**
       * 
       * Cleans out smear conversion and smear reconversion obs that no longer have smears that match by date and result
       * 
       * @param p
       */
      public void cleanSmearStatusObs(Patient p){
          ObsService os = Context.getObsService();
          Concept ccConcept = this.getConceptSmearConversion();
          Concept rcConcept = this.getConceptSmearReconversion();
          Map<Obs, Date> map = this.getSmears(p);
          
          List<Obs> ccObs = os.getObservationsByPersonAndConcept(p, ccConcept);
          List<Obs> rcObs = os.getObservationsByPersonAndConcept(p, rcConcept);
          
          //we need functions here that re-evaluate each existing cc obs:
          if (ccObs != null){
              for (Obs o : ccObs){
                  if (!isObsValidSmearconversion(o, p, map))
                   os.voidObs(o, "no longer a valid smear conversion");   
              }
          }
          map = this.getSmears(p);
          if (rcObs != null){
              for (Obs o : rcObs){
                  if (!isValidSmearReconversion(o, p, map, ccObs))
                  os.voidObs(o, "no longer a valid smear reconversion");
              }
          }  
      }
      
      
      /**
       * 
       * tests to see if 
       * 
       * @param o the obs to test
       * @param p the patient
       * @param map the results of this.getSmears(patient)
       * @return
       */
      public boolean isObsValidSmearconversion(Obs o, Patient p, Map<Obs,Date> map){
        //map is all smears
          int numberNeededInARow = MdrtbUtil.lookupConversionNumberConsecutive();
          boolean ret = false;
          Concept cSmear = this.getConceptSmearResult();
          List<Obs> obsList = new ArrayList<Obs>();      
          for (Obs obs:map.keySet())
              obsList.add(obs);
          
              //make sure there is still an original positive smear
              int posOfOriginalPositiveBac = -1;
                  for (int i = 0; i < obsList.size(); i++){
                      Obs oTmp = obsList.get(i);
                      if (!MdrtbUtil.isNegativeBacteriology(oTmp, cSmear, this)){
                          posOfOriginalPositiveBac = i;
                          //if the conversion date is before the first positive smear -- return false
                          if (o.getObsDatetime().before(oTmp.getObsDatetime()))
                              return false;
                          break;
                      }
                  }
               //if there are no positives   
               if (posOfOriginalPositiveBac == -1)
                   return false;
          
          //obsList = all smear obs
          if (obsList.size() > numberNeededInARow + 1){
              //get the smear obs that corresponds to the ccObs
              Obs ccObs = null;
              int pos = 0;
              for (Obs otmp : obsList){
                  if (map.get(otmp).getTime() == o.getValueDatetime().getTime()){
                      ccObs = otmp;
                      break;
                  }
                  pos ++;
              }
              if (ccObs == null || !MdrtbUtil.isNegativeBacteriology(ccObs, cSmear, this) || pos == 0 || MdrtbUtil.isNegativeBacteriology(obsList.get(pos -1) , cSmear, this) || pos + 1 >= obsList.size()){
                  return false;
              }
              //check if next X are negative (redundant, but quick)
              for (int k = 1; k < numberNeededInARow;k++){
                  if (!MdrtbUtil.isNegativeBacteriology(obsList.get(pos+k), cSmear, this))
                      return false;
              }     
              //now test the next 30 days:
              Calendar cal = Calendar.getInstance();
              cal.setTime(map.get(ccObs));
              cal.add(Calendar.DAY_OF_MONTH, MdrtbUtil.lookupConversionInterval());
              int count = 1;
              for (Obs oTmp : obsList){
                  if (map.get(oTmp).after(map.get(ccObs))){
                      count++;
                      if (map.get(oTmp).before(cal.getTime()) && !MdrtbUtil.isNegativeBacteriology(oTmp, cSmear, this)){
                          return false;
                      }
                      else if ((map.get(oTmp).after(cal.getTime()) || map.get(oTmp).getTime() == cal.getTime().getTime()) && MdrtbUtil.isNegativeBacteriology(oTmp, cSmear, this) && count >= numberNeededInARow){
                          return true;
                      }
                      else if ((map.get(oTmp).after(cal.getTime()) || map.get(oTmp).getTime() == cal.getTime().getTime()) && !MdrtbUtil.isNegativeBacteriology(oTmp, cSmear, this)){
                          return false;
                      }
                  }
              }  
          } else
              return false;
          return ret;
      }
      
      
      /**
       * 
       * Tests if an obs is a valid reconversion obs; assumes smear conversions have already been cleaned.
       * 
       * @param o the obs to test
       * @param p patient
       * @param map the results of this.getSmears(patient)
       * @param ccObs a list of all smear conversion obs
       * @return
       */
      public boolean isValidSmearReconversion(Obs o, Patient p, Map<Obs,Date> map, List<Obs> ccObs){
          boolean ret = false;
          Concept cSmear = this.getConceptSmearResult();
          if (ccObs == null || ccObs.size() == 0)
                  return false;
          Set<Obs> ObsSet = map.keySet();
          List<Obs> obsList = new ArrayList<Obs>();      
          for (Obs obs:ObsSet)
              obsList.add(obs);
          //obsList = all smear obs, and use the map values to get sputum collection dates.
          if (obsList.size() > 3){
              
              Obs rcObs = null;
              int pos = 0;
              for (Obs otmp : obsList){
                  if (map.get(otmp).getTime() == o.getValueDatetime().getTime()){
                      rcObs = otmp;
                      break;
                  }
                  pos ++;
              }
              if (rcObs == null || MdrtbUtil.isNegativeBacteriology(rcObs, cSmear, this) || pos < 3 || !MdrtbUtil.isNegativeBacteriology(obsList.get(pos -1), cSmear, this))
                  return false;

              //get previous smear, and see if its valid.
              for (int k = pos-1; k >= 0 ; k -- ){
                  Obs obsPrevious = obsList.get(k);
                  if (!MdrtbUtil.isNegativeBacteriology(obsPrevious, cSmear, this))
                      return false;
                  if (ccObs.contains(obsPrevious) && this.isObsValidSmearconversion(o, p, map)){
                      ret = true;
                      break;
                  }
              }
          }    
          return ret;
      }
      
      
      /**
       * 
       * Creates a smear conversion, reconversion, and cleans false obs for a patient;
       * 
       * @param p
       */
      public void fixSmearConversions(Patient p){
          ObsService os = Context.getObsService();
              this.cleanSmearStatusObs(p);
              Date date = null;
              Map<Obs, Date> map = this.getSmears(p);
              Concept ccConcept = this.getConceptSmearConversion();
                  if (ccConcept == null)
                      throw new RuntimeException("Smear Conversion Concept not loaded");
              Concept rcConcept = this.getConceptSmearReconversion();
                  if (rcConcept == null)
                      throw new RuntimeException("Smear Re - Conversion Concept not loaded");
              if (map != null && map.size() > 2){
                  date = this.getSmearConversionDate(p);
              }    
              if (date != null){
                  
                //test if the obs is already created:
                  if (ccConcept != null){
                      List<Obs> oList = os.getObservationsByPersonAndConcept(p, ccConcept);
                      boolean createObsTest = true;
                      if (oList != null){
                          for (Obs o:oList){
                              if (o.getValueDatetime().equals(date)){
                                  createObsTest = false;
                                  break;
                              }    
                          }
                      }
                      if (createObsTest){
                          for (Map.Entry<Obs,Date> e : map.entrySet()) {
                              Date dateInner = e.getValue();
                              if (dateInner.getTime() == date.getTime()){
                                  //TODO:  
                                  Obs oTmp = new Obs();
                                  oTmp.setConcept(ccConcept);
                                  oTmp.setCreator(Context.getAuthenticatedUser());
                                  oTmp.setDateCreated(new Date());
                                  oTmp.setEncounter(e.getKey().getEncounter());
                                  oTmp.setLocation(e.getKey().getLocation());
                                  oTmp.setObsDatetime(e.getKey().getObsDatetime());
                                  oTmp.setPerson(p);
                                  oTmp.setValueDatetime(date);
                                  oTmp.setVoided(false);
                                  
                                  try {
                                  log.info("Creating smear conversion Obs for patient " + p.getPatientId() + " for date " + oTmp.getValueDatetime());
                                  oTmp = os.saveObs(oTmp, "");
                                  } catch (Exception ex){
                                  log.warn("Failed to create smear conversion Obs for patient", ex);
                                  }
                                  break;
                              }    
                          }
                       
                      }
                  }
              }
              //smear reconversion:
              date = null;
              if (map != null && map.size() > 2)
                  date = this.getSmearReconversionDate(p);
              if (date != null){
                  if (rcConcept != null){
                      //test if the obs is already created:
                      List<Obs> oList = os.getObservationsByPersonAndConcept(p, rcConcept);
                      boolean createObsTest = true;
                      if (oList != null){
                          for (Obs o:oList){
                              if (o.getValueDatetime().equals(date)){
                                  createObsTest = false;
                                  break;
                              }    
                          }
                      }
                      
                      if (createObsTest){
                          for (Map.Entry<Obs,Date> e : map.entrySet()) {
                              Date dateInner = e.getValue();
                              if (dateInner.getTime() == date.getTime()){ 
                                  Obs oTmp = new Obs();
                                  oTmp.setConcept(rcConcept);
                                  oTmp.setCreator(Context.getAuthenticatedUser());
                                  oTmp.setDateCreated(new Date());
                                  oTmp.setEncounter(e.getKey().getEncounter());
                                  oTmp.setLocation(e.getKey().getLocation());
                                  oTmp.setObsDatetime(date);
                                  oTmp.setPerson(p);
                                  oTmp.setValueDatetime(date);
                                  oTmp.setVoided(false);
                                  
                                  try {
                                      log.info("Creating smear reconversion Obs for patient " + p.getPatientId() + " for date " + oTmp.getValueDatetime());
                                      oTmp = os.saveObs(oTmp, "");
                                  } catch (Exception ex){
                                      log.warn("Failed to create smear reconversion Obs for patient", ex);
                                  }
                                  break;
                              }    
                          }
                     
                      }
                  }
              }    
            
      }
      
      public Concept getMDRTBConceptByKey(String key, Locale loc, Map<String, Concept> xmlConceptList){
          try {
              Concept c = xmlConceptList.get(key);    
              return c;
          } catch (Exception ex) {
              throw new RuntimeException(ex);
          }
      }

    public Map<String, Concept> getXmlConceptList() {
        return xmlConceptList;
    }
    
    public void initializeEverythingAboutConcept(Concept c){
        if (c != null){
            for (ConceptName cns : c.getNames()){
                Collection<ConceptNameTag> tags = cns.getTags();
                for (ConceptNameTag cnTag:tags){
                    cnTag.getTag();
                }
            }
            Collection<ConceptAnswer> cas = c.getAnswers();
            if (cas != null){
                for (ConceptAnswer ca : cas){
                    Collection<ConceptName> cnsTmp = ca.getAnswerConcept().getNames();
                    for (ConceptName cn:cnsTmp){
                        Collection<ConceptNameTag> tags = cn.getTags();
                        for (ConceptNameTag cnTag:tags){
                            cnTag.getTag();
                        }
                    } 
                }
            }
        }
    }

    public void addKeyAndConceptToXmlConceptList(String conceptKey, Concept concept) {
        initializeEverythingAboutConcept(concept);
        this.xmlConceptList.put(conceptKey, concept);
    }
    
    /**
     * 
     * Utility method used to transition to a state, ignoring usual state change rules.
     * 
     * @param pp
     * @param programWorkflowState
     * @param onDate
     */
    public PatientProgram  transitionToStateNoErrorChecking(PatientProgram pp, ProgramWorkflowState programWorkflowState, Date onDate) {

        Set<PatientState> lastStates = pp.getStates();

        for (PatientState lastState : lastStates){
        
            
            if (lastState != null  && lastState.getState().getProgramWorkflow().getProgramWorkflowId().intValue() == programWorkflowState.getProgramWorkflow().getProgramWorkflowId().intValue() && !lastState.getVoided()) {
                
                    lastState.setEndDate(onDate);
                //if nonsensical: void
                if (lastState.getEndDate().getTime() <= lastState.getStartDate().getTime()){
                    lastState.setEndDate(onDate);
                    lastState.setDateVoided(new Date());
                    lastState.setVoided(true);
                    lastState.setVoidedBy(Context.getAuthenticatedUser());
                    lastState.setVoidReason("program states were edited such that this state observation was no longer valid"); 
                }
            }  
        }
        
        PatientState newState = new PatientState();
        newState.setPatientProgram(pp);
        newState.setState(programWorkflowState);
        newState.setStartDate(onDate);
        pp.getStates().add(newState);
        
  
        
        Set<Concept> outcomeConcepts = this.getMdrProgramOutcomeConcepts();
        if (outcomeConcepts.contains(programWorkflowState.getConcept())) {
            pp.setDateCompleted(onDate);
        }
        pp.setUuid(UUID.randomUUID().toString());
        Context.getProgramWorkflowService().savePatientProgram(pp);
        
        Concept diedConcept = this.getConceptDiedMDR();
        System.out.println("programWorkflowState.getConcept() " + programWorkflowState.getConcept());
        System.out.println("diedConcept " + diedConcept);
        if (programWorkflowState.getConcept().getConceptId().equals(diedConcept.getConceptId())) {
            Context.getPatientService().processDeath(pp.getPatient(), onDate, diedConcept, null);
        }
        
        return pp;
    }
    
}


