package org.openmrs.module.mdrtb.status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.Patient;


abstract public class Status {

	Patient patient;
	
	Map<String,StatusItem> items = new HashMap<String,StatusItem>();
	
	List<StatusFlag> flags = new LinkedList<StatusFlag>();
	
	public Status(Patient patient) {
		this.patient = patient;
	}
	
    public Patient getPatient() {
    	return patient;
    }

    public void setPatient(Patient patient) {
    	this.patient = patient;
    }
	
    protected void addItem(String name, StatusItem item) {
		items.put(name, item);
	}
    
	protected StatusItem getItem(String name) {
		StatusItem item = items.get(name);
		
		if (item != null) {
			return item;
		} 
		else {
			return new StatusItem();
		}
	
	}
	
	public void addFlag(StatusFlag flag) {
		flags.add(flag);
	}
	
	public void removeFlag(StatusFlag flag) {
		flags.remove(flag);
	}
	
	public List<StatusFlag> getFlags() {
		
		List<StatusFlag> flags = new LinkedList<StatusFlag>();
		
		flags.addAll(this.flags);
		
		for (StatusItem item : this.items.values()) {
			flags.addAll(item.getFlags());
		}
		
		return flags;
	}
	
}
