package org.openmrs.module.mdrtb;

import java.text.SimpleDateFormat;

import org.openmrs.api.context.Context;

public class MdrtbConstants {
    private static final String moduleName = "mdrtb";
    
    public static final SimpleDateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("dd/MMM/yyyy", Context.getLocale());
    public static final String PATIENT_CHART_REGIMEN_CELL_COLOR = "lightblue";
    
    public static final String MDRTB_PROGRAM_GLOBAL_PROPERTY = moduleName + ".program_name";
    public static final String ROLES_TO_REDIRECT_GLOBAL_PROPERTY = moduleName + ".roles_to_redirect_from_openmrs_homepage";
    public static final String MDRTB_PATIENT_IDENTIFIER_TYPES = moduleName + ".patient_identifier_type_list";    
    
    public static enum TbClassification {MONO_RESISTANT_TB, POLY_RESISTANT_TB, MDR_TB, XDR_TB}; // TODO: add suspected mdr-tb?
    
    public static enum TreatmentState {NOT_ON_TREATMENT, ON_TREATMENT};
    
    public static enum MdrtbPatientDashboardTabs{
    	STATUS("status","mdrtb.status"),
    	FORM("formEntry","mdrtb.formentry"),
    	REG("patientRegimen","mdrtb.patientregimen"),
    	BAC("BAC","mdrtb.bacteriologies"),
    	DST("DST","mdrtb.dsts"),
    	CONTACTS("contacts","mdrtb.contacts");
    	
    	String id; // the id to reference the code in Javascript
    	String messageCode;  // the spring:message code for the tag
    	
    	MdrtbPatientDashboardTabs(String id, String messageCode){
    		this.id = id;
    		this.messageCode = messageCode;
    	}
    	public String getId(){
    		return id;
    	}
    	public String getMessageCode(){
    		return messageCode;
    	}
    }
}



