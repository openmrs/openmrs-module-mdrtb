package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbConstants.MdrtbTestType;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */

public class MdrtbSmearImpl extends MdrtbTestImpl implements MdrtbSmear {
	

	// set up a smear object, given an existing obs
	public MdrtbSmearImpl(Obs smear) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(smear == null || !(smear.getConcept().equals(mdrtbFactory.getConceptSmearParent()))) {
			throw new RuntimeException ("Cannot initialize smear: invalid obs used for initialization.");
		}
		
		test = smear;
	}
	
	// create a new smear object, given an existing patient
	public MdrtbSmearImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create smear: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), mdrtbFactory.getConceptSmearParent(), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public MdrtbTestType getTestType() {
		return MdrtbTestType.SMEAR;
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

    public void setBacilli(Double bacilli) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptBacilli());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || obs.getValueNumeric() != bacilli) {
			
			// void the existing obs if it exists
			voidObsIfNotNull(obs);
				
			// now create the new Obs and add it to the encounter
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptBacilli(), test.getObsDatetime(), test.getLocation());
			obs.setValueNumeric(bacilli);
			test.addGroupMember(obs);
		}
    }

   

    public void setMethod(Concept method) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearMicroscopyMethod());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueCoded().equals(method)) {
			
			// void the existing obs if it exists
			voidObsIfNotNull(obs);
				
			// now create the new Obs and add it to the encounter
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptSmearMicroscopyMethod(), test.getObsDatetime(), test.getLocation());
			obs.setValueCoded(method);
			test.addGroupMember(obs);
		}
    }

    public void setResult(Concept result) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearResult());
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueCoded().equals(result)) {
			
			// void the existing obs if it exists
			voidObsIfNotNull(obs);
				
			// now create the new Obs and add it to the smear Obs
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptSmearResult(), test.getObsDatetime(), test.getLocation());
			obs.setValueCoded(result);
			test.addGroupMember(obs);
		}
    }
}

