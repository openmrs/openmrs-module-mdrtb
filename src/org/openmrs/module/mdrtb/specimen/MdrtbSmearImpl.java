package org.openmrs.module.mdrtb.specimen;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */

public class MdrtbSmearImpl implements MdrtbSmear {

	Obs smear;  // the top-level obs that holds all the data for this smear
	
	MdrtbFactory mdrtbFactory;
	
	public MdrtbSmearImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}
	
	// set up a smear object, given an existing obs
	public MdrtbSmearImpl(Obs smear) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(smear == null || !(smear.getConcept().equals(mdrtbFactory.getConceptSmearParent()))) {
			throw new RuntimeException ("Cannot initialize smear: invalid obs used for initialization.");
		}
		
		this.smear = smear;
	}
	
	// create a new smear object, given an existing patient
	public MdrtbSmearImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create smear: encounter can not be null.");
		}
		
		this.smear = new Obs (encounter.getPatient(), mdrtbFactory.getConceptSmearParent(), encounter.getEncounterDatetime(), null);
	}
	
	public List<Object> getSmear() {
		List<Object> list = new LinkedList<Object>();
		list.add(this.smear);
		return list;
	}
	
	public String getSmearId() {
		return this.smear.getId().toString();
	}
	
	public String getSpecimenId() {
		return this.smear.getEncounter().getEncounterId().toString();
	}
	
    public Double getBacilli() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptBacilli());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric();
    	}
    }

    public Date getDateReceived() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDateReceived());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
    }

    public Location getLab() {
	   return smear.getLocation();
    }

    public Concept getMethod() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearMicroscopyMethod());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public Concept getResult() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearResult());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }

    public Date getResultDate() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptResultDate());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
    }

    public void setBacilli(Double bacilli) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptBacilli());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || obs.getValueNumeric() != bacilli) {
			
			// void the existing obs if it exists
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			obs = new Obs (smear.getPerson(), mdrtbFactory.getConceptBacilli(), smear.getObsDatetime(), smear.getLocation());
			obs.setValueNumeric(bacilli);
			smear.addGroupMember(obs);
		}
    }

    public void setDateReceived(Date dateReceived) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDateReceived());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueDatetime().equals(dateReceived)) {
			
			// void the existing obs if it exists
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			obs = new Obs (smear.getPerson(), mdrtbFactory.getConceptDateReceived(), smear.getObsDatetime(), smear.getLocation());
			obs.setValueDatetime(dateReceived);
			smear.addGroupMember(obs);
		}
    }

    public void setLab(Location location) {
    	smear.setLocation(location);
		
		// also propagate this location to the all the child obs
    	if(smear.getGroupMembers() != null) {
    		for(Obs obs : smear.getGroupMembers()) {
    			obs.setLocation(location);
    		}
    	}
    }

    public void setMethod(Concept method) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearMicroscopyMethod());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueCoded().equals(method)) {
			
			// void the existing obs if it exists
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			obs = new Obs (smear.getPerson(), mdrtbFactory.getConceptSmearMicroscopyMethod(), smear.getObsDatetime(), smear.getLocation());
			obs.setValueCoded(method);
			smear.addGroupMember(obs);
		}
    }


    public void setResult(Concept result) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearResult());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueCoded().equals(result)) {
			
			// void the existing obs if it exists
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the smear Obs
			obs = new Obs (smear.getPerson(), mdrtbFactory.getConceptSmearResult(), smear.getObsDatetime(), smear.getLocation());
			obs.setValueCoded(result);
			smear.addGroupMember(obs);
		}
    }

    public void setResultDate(Date resultDate) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptResultDate());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueDatetime().equals(resultDate)) {
			
			// void the existing obs if it exists
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			obs = new Obs (smear.getPerson(), mdrtbFactory.getConceptResultDate(), smear.getObsDatetime(), smear.getLocation());
			obs.setValueDatetime(resultDate);
			smear.addGroupMember(obs);
		}
    }

    /**
     * Protected methods used for interacting with the matching MdrSpecimenImpl
     */

    protected Obs getObs() {
    	return smear;
    }
    
    /**
	 * Utility methods 
	 */
	
	/**
	 * Iterates through all the obs in the smear obs group and
	 * returns the first one that who concept matches the specified concept
	 * Returns null if obs not found
	 */
    private Obs getObsFromObsGroup(Concept concept) {
    	if (smear.getGroupMembers() != null) {
    		for(Obs obs : smear.getGroupMembers()) {
    			if (obs.getConcept().equals(concept)) {
    				return obs;
    			}
    		}
    	}
    	return null;
    }
    
}

