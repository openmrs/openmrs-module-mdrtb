package org.openmrs.module.mdrtb.status;

import java.util.HashMap;
import java.util.Map;


public class StatusMap {

	// TODO: make private if I actually use this?
	public Map<String,Status> map = new HashMap<String,Status>();
	
    public Map<String, Status> getMap() {
    	return map;
    }
	
    public void setMap(Map<String, Status> map) {
    	this.map = map;
    }
	
}
