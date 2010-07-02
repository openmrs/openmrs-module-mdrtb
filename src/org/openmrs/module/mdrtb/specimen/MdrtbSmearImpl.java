package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class MdrtbSmearImpl extends MdrtbTestImpl implements MdrtbSmear {
	
	public MdrtbSmearImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}

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
	public String getTestType() {
		return "smear";
	}
	
    public Integer getBacilli() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptBacilli());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
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

    public void setBacilli(Integer bacilli) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptBacilli());
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && bacilli == null) {
			return;
		}
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptBacilli(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(bacilli.doubleValue());
    }   

    public void setMethod(Concept method) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearMicroscopyMethod());
    	
    	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && method == null) {
			return;
		}
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptSmearMicroscopyMethod(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(method);
    }

    public void setResult(Concept result) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptSmearResult());
    	
    	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && result == null) {
			return;
		}
    	
    	// initialize the obs if we need to
		if (obs == null) {
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptSmearResult(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the data
		obs.setValueCoded(result);
    }
}

