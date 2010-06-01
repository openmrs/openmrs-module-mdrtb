package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.HashMap;
import java.util.Map;



public class PatientSummaryTableRecord {

	private Map<String,PatientSummaryTableElement> elements = new HashMap<String,PatientSummaryTableElement>();

	
	//private Date date;
	
	//private PatientSummaryTableBacElement smear;
	
	//private PatientSummaryTableBacElement culture;
	
	//private Map<String,PatientSummaryTableDSTElement> dsts;
	
	//private Map<String,PatientSummaryTableRegimenElement> regimens;
	
	public PatientSummaryTableRecord() {
		// empty constructor
	}

	
	/*
	 * Getters and Setters
	 */

	public void setElements(Map<String,PatientSummaryTableElement>elements) {
	    this.elements = elements;
    }

	public Map<String,PatientSummaryTableElement> getElements() {
	    return elements;
    }
	
	public void addElement(String key, PatientSummaryTableElement element) {
		this.elements.put(key, element);
	}
	
	public void removeElement(String key) {
		this.elements.remove(key);
	}
}
