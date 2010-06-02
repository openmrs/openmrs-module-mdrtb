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


	public void setElements(Map<String,PatientSummaryTableElement> elementGroup) {
	    this.elements = elementGroup;
    }

	public Map<String,PatientSummaryTableElement> getElements() {
	    return elements;
    }
	
	public void addElement(String key, PatientSummaryTableElement element) {
		this.elements.put(key, element);
	}
	
	public PatientSummaryTableElement getElement(String key) {
		return this.elements.get(key);
	}
	
	public void removeElement(String key) {
		this.elements.remove(key);
	}
	
	public Boolean contains(String key) {
		return this.elements.containsKey(key);
	}
}
