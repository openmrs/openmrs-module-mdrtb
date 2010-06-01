package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.HashMap;
import java.util.Map;


public class PatientSummaryTableElementGroup extends PatientSummaryTableElement {

	private Map<String,PatientSummaryTableElement> elements = new HashMap<String,PatientSummaryTableElement>();

	public PatientSummaryTableElementGroup() {
		// generic constructor
	}
	
	public PatientSummaryTableElementGroup(Map<String, PatientSummaryTableElement> elements) {
		this.elements = elements;
    }
	
	/*
	 * Getters and Setters
	 */


	public void setElementGroup(Map<String,PatientSummaryTableElement> elementGroup) {
	    this.elements = elementGroup;
    }

	public Map<String,PatientSummaryTableElement> getElementGroup() {
	    return elements;
    }
	
	public void addElement(String key, PatientSummaryTableElement element) {
		this.elements.put(key, element);
	}
	
	public void removeElement(String key) {
		this.elements.remove(key);
	}
}
