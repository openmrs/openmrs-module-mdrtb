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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.Activator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MdrtbActivator implements Activator, Runnable {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void shutdown() {
		log.info("Shutting down MDR-TB module.");
		unregisterAddressTemplates();
	}
	
	public void startup() {
		log.info("Starting up MDR-TB module.");
		registerAddressTemplates();
		configureGlobalProperties();
		performCustomMigrations();
	}
	
	public void run() {
		// TODO Auto-generated method stub  
	}
	
	/**
	 * Sets up the Default Address Template
	 */
	public void registerAddressTemplates() {
		
		log.info("Registering default address format.");
		
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
			if ("default".equals(at.getCodeName())) {
				i.remove();
			}
		}
	}
	
	/**
	 * Configures any global properties that need to be configured
	 */
	private void configureGlobalProperties() {
		// configure the primary patient identifier type if needed
		if (StringUtils.isBlank(Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType"))) {
			List<PatientIdentifierType> types = Context.getPatientService().getAllPatientIdentifierTypes();
			// just pick the first identifier to use as the primary
			if(types.size() > 0) {
				GlobalProperty primaryIdType = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.primaryPatientIdentifierType");
				primaryIdType.setPropertyValue(types.get(0).getName());
				Context.getAdministrationService().saveGlobalProperty(primaryIdType);
			}
		}
	}
	
	/**
	 * Perform any custom migrations required due to changes in the data model
	 */
	private void performCustomMigrations() {
		migrateClinicianNotes();
	}
	
	/**
	 *  We changed the concept in the core metadata that is mapped to the mdrtb mapping CLINICIAN NOTES;
	 *  make sure we migrate all underlying observations 
	 */
	private void migrateClinicianNotes() {	
		// try to fetch old note concept by uuid
		Concept oldNotesConcept = Context.getConceptService().getConceptByUuid("31bbf216-0370-102d-b0e3-001ec94a0cc1");

		// try the same with the new notes concept by uuid
		Concept newNotesConcept = Context.getConceptService().getConceptByUuid("31b474e6-0370-102d-b0e3-001ec94a0cc1");
		
		// double check that we've found a old notes concept and a new notes concept, to make sure that we don't fetch all obs by accident,
		// or set obs to have a concept of null!
		if (oldNotesConcept != null && newNotesConcept != null) {
			List<Concept> oldNotesList = new ArrayList<Concept>();
			oldNotesList.add(oldNotesConcept);
			
			// we only want to update mdr-tb module encounters
			EncounterType intake = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
			EncounterType followup = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
			
			for (Obs obs : Context.getObsService().getObservations(null, null, oldNotesList, null, null, null, null, null, null, null, null, false)) {
				// only update mdr-tb encounters that HAVEN'T been created with a custom form
				if (obs.getEncounter().getEncounterType().equals(intake) || obs.getEncounter().getEncounterType().equals(followup) 
						&& obs.getEncounter().getForm() == null && obs.getValueCoded() == null) {
					obs.setConcept(newNotesConcept);
					Context.getObsService().saveObs(obs, "migrated to new CLINICIAN NOTES concept");
				}
			}
		}
	}
}
