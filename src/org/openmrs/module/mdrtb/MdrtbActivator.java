/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.Activator;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class MdrtbActivator implements Activator, Runnable {

    private Log log = LogFactory.getLog(this.getClass());

    public void shutdown() {
    	log.info("Shutting down MDR-TB module.");
    	registerAddressTemplates();
    }

    public void startup() {
    	log.info("Starting up MDR-TB module.");
    	unregisterAddressTemplates();
    }

    public void run() {
	    // TODO Auto-generated method stub  
    }
    
    /**
	 * Sets up the Default Address Template
	 */
	public void registerAddressTemplates() {
		AddressTemplate at = new AddressTemplate();
		at.setDisplayName("Default Address Format");
		at.setCodeName("default");
		at.setCountry("default");
		Map<String, String> nameMappings = new HashMap<String, String>();
		nameMappings.put("cityVillage", "Location.cityVillage");
		nameMappings.put("address1", "PersonAddress.address1");
		at.setNameMappings(nameMappings);
		Map<String, String> sizeMappings = new HashMap<String, String>();
		sizeMappings.put("cityVillage", "20");
		sizeMappings.put("address1", "60");
		at.setSizeMappings(sizeMappings);
		Map<String, String> elementDefaults = new HashMap<String, String>();
		elementDefaults.put("country", "default");
		at.setElementDefaults(elementDefaults);
		at.setLineByLineFormat(Arrays.asList("cityVillage address1"));
		AddressSupport.getInstance().getLayoutTemplates().add(at);
	}
	
	/**
	 * Unregisters the Default Address Template
	 */
	public void unregisterAddressTemplates() {
		for (Iterator<AddressTemplate> i = AddressSupport.getInstance().getLayoutTemplates().iterator(); i.hasNext();) {
			AddressTemplate at = i.next();
			if ("defaultMdrtb".equals(at.getCodeName())) {
				i.remove();
			}
		}
	}
    
}
