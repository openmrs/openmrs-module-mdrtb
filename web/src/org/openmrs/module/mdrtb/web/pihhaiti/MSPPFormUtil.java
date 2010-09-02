package org.openmrs.module.mdrtb.web.pihhaiti;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;


public class MSPPFormUtil {

	public MSPPFormUtil() {
	}
	
	// given an encounter, load the data from that encounter into a MSSPForm object
	public static MSPPForm loadMSPPForm(Integer encounterId) {
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		
		if(encounter == null) {
			return null;
		}
		
		if(!encounter.getEncounterType().equals(Context.getEncounterService().getEncounterType("Specimen Collection"))) {
			throw new MdrtbAPIException("Encounter is of invalid type to create MSPP Form");
		}
		
		if(encounter.getForm() == null || !encounter.getForm().getId().equals(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId")))) {
			throw new MdrtbAPIException("Encounter is not linked to dummy MSPP Form");
		}
		
		// if we've passed all the checks, create the new form
		MSPPForm form = new MSPPForm();
		
		// first, add this encounter as a specimen specimen
		form.addSpecimen(Context.getService(MdrtbService.class).getSpecimen(encounter));
		
		// now find the other relevant encounters
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounter.getPatient(), encounter.getLocation(), null, null, 
				Arrays.asList(Context.getFormService().getForm(Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId"))), 
				Arrays.asList(Context.getEncounterService().getEncounterType("Specimen Collection")), null, false);
		
		// the key rule here is that if the encounters have the same datetime created, they are from the same form
		for(Encounter e : encounters) {
			if(e != null && e.getDateCreated().equals(encounter.getDateCreated()) && e.getProvider().equals(encounter.getProvider())){
				form.addSpecimen(Context.getService(MdrtbService.class).getSpecimen(e));
			}
		}
		
		return form;
	}
}
