package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;


public class Record {
	
	private List<RecordComponent> components;
	
	public Record() {
		this.components = new LinkedList<RecordComponent>();
	}
	
	public Record(List<RecordComponent> specimens) {
		this.components = specimens;
	}

	public void setComponents(List<RecordComponent> specimens) {
	    this.components = specimens;
    }

	public List<RecordComponent> getComponents() {
	    return components;
    }
	
}
