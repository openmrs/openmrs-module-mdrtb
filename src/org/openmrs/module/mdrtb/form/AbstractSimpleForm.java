package org.openmrs.module.mdrtb.form;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;


public abstract class AbstractSimpleForm implements SimpleForm {
	protected Encounter encounter;
	
	public AbstractSimpleForm() {
		this.encounter = new Encounter();
	}
	
	public AbstractSimpleForm(Patient patient) {
		this.encounter = new Encounter();
		this.encounter.setPatient(patient);
	}
	
	public AbstractSimpleForm(Encounter encounter) {
		this.encounter = encounter;
	}
	
	
	public Integer getId() {
		return this.encounter.getId();
	}
	
	public void setEncounter(Encounter encounter) {
	    this.encounter = encounter;
    }

	public Encounter getEncounter() {
	    return encounter;
    }
	
	public Person getProvider() {
		return encounter.getProvider();
	}
	
	public void setProvider(Person provider) {
		encounter.setProvider(provider);
	}
	
	public Patient getPatient() {
		return encounter.getPatient();
	}
	
	public void setPatient(Patient patient) {
		encounter.setPatient(patient);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setPerson(patient);
		}
	}
	
	public Date getEncounterDatetime() {
		return encounter.getEncounterDatetime();
	}

	public void setEncounterDatetime(Date date) {
		encounter.setEncounterDatetime(date);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setObsDatetime(date);
		}
	}
	
	public Location getLocation() {
		return encounter.getLocation();
	}
	
	public void setLocation(Location location) {
		encounter.setLocation(location);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setLocation(location);
		}
	}
	
	public String getClinicianNotes() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), encounter);
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}
	
	public void setClinicianNotes(String comments) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !StringUtils.equals(obs.getValueText(), comments)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module");
			}
				
			// now create the new Obs and add it to the encounter
			if(StringUtils.isNotBlank(comments)) {
				obs = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(comments);
				encounter.addObs(obs);
			}
		}
	}
	
	
}
