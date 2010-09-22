package org.openmrs.module.mdrtb.web.pihhaiti;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;

public class MSPPFormUtil {
	
	protected final static Log log = LogFactory.getLog(MSPPFormUtil.class);
	
	public MSPPFormUtil() {
	}
	
	// given an encounter, load the data from that encounter into a MSSPForm object
	public static MSPPForm loadMSPPForm(Integer encounterId) {
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		
		if (encounter == null) {
			return null;
		}
		
		if (!encounter.getEncounterType().equals(Context.getEncounterService().getEncounterType("Specimen Collection"))) {
			throw new MdrtbAPIException("Encounter is of invalid type to create MSPP Form");
		}
		
		if (encounter.getForm() == null
		        || !encounter.getForm().getId().equals(
		            Integer.valueOf(Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId")))) {
			throw new MdrtbAPIException("Encounter is not linked to dummy MSPP Form");
		}
		
		// if we've passed all the checks, create the new form
		MSPPForm form = new MSPPForm();
		
		// first, add this encounter as a specimen specimen
		form.addSpecimen(Context.getService(MdrtbService.class).getSpecimen(encounter));
		
		// now find the other relevant encounters
		List<Encounter> encounters = Context.getEncounterService().getEncounters(
		    encounter.getPatient(),
		    encounter.getLocation(),
		    null,
		    null,
		    Arrays.asList(Context.getFormService().getForm(
		        Integer.valueOf(Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId")))),
		    Arrays.asList(Context.getEncounterService().getEncounterType("Specimen Collection")), null, false);
		
		// the key rule here is that if the encounters have the same datetime created, they are from the same form
		for (Encounter e : encounters) {
			if (e != null && !e.equals(encounter) && e.getDateCreated().equals(encounter.getDateCreated())
			        && e.getProvider().equals(encounter.getProvider())) {
				form.addSpecimen(Context.getService(MdrtbService.class).getSpecimen(e));
			}
		}
		
		// finish initialization by adding empty specimens, as necessary, until the form contains three
		form.initialize(encounter.getPatient(), false);
		
		return form;
	}
	
	/**
	 * Given a patient, return all the MSPP encounters for that patient
	 */
	public static List<Encounter> getMSPPEncounters(Patient patient) {
		
		// if there is no pihhaiti.dummyMSPPFormId defined, return null
		if (Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId") == null) {
			return null;
		}
		
		// first, get all the possible encounters
		List<Encounter> encounters = Context.getEncounterService().getEncounters(
		    patient, null, null, null,
		    Arrays.asList(Context.getFormService().getForm(
		        Integer.valueOf(Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId")))),
		    Arrays.asList(Context.getEncounterService().getEncounterType("Specimen Collection")), null, false);
		
		// sort by encounter date
		Collections.sort(encounters, new Comparator<Encounter>(){
			public int compare(Encounter e1, Encounter e2) {
		    	if(e1 == null) {
		    		return 1;
		    	}
		    	if(e2 == null) {
		    		return -1;
		    	}	
		    	return e1.getEncounterDatetime().compareTo(e2.getEncounterDatetime());
		    }
		});
		
		// now pull off any duplicates
		Set<Date> creationDates = new HashSet<Date>();
		Iterator<Encounter> i = encounters.iterator();
		
		while(i.hasNext()) {
			Encounter encounter = i.next();
			Date dateCreated = encounter.getDateCreated();

			// if we already have a date with the same date created, pull it off the set, as we only want one encounter from each set
			if(creationDates.contains(dateCreated)) {
				i.remove();
			} else {
				creationDates.add(dateCreated);
			}
		}
		
		return encounters;
	}
	
}
