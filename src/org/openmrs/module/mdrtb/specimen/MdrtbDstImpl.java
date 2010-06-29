package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;

/**
 * An implementation of a MdrtbDst.  This wraps an ObsGroup and provides access to dst
 * data within the obsgroup.
 */
public class MdrtbDstImpl extends MdrtbTestImpl implements MdrtbDst {
	
	public MdrtbDstImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}
	
	// set up a dst object, given an existing obs
	public MdrtbDstImpl(Obs dst) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(dst == null || !(dst.getConcept().equals(mdrtbFactory.getConceptDSTParent()))) {
			throw new RuntimeException ("Cannot initialize dst: invalid obs used for initialization.");
		}
		
		test = dst;
	}
	
	// create a new culture object, given an existing patient
	public MdrtbDstImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), mdrtbFactory.getConceptDSTParent(), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "dst";
	}

    public Double getColoniesInControl() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptColoniesInControl());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric();
    	}
    }

    public Boolean getDirect() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDirectIndirect());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueAsBoolean();
    	}
    }

    public Concept getMethod() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDSTMethod());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }

    public Concept getOrganismType() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptTypeOfOrganism());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }

    public void setColoniesInControl(Double coloniesInControl) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptColoniesInControl());
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && coloniesInControl == null) {
			return;
		}
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptColoniesInControl(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(coloniesInControl);
    }

    public void setDirect(Boolean direct) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDirectIndirect());
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && direct == null) {
			return;
		}
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptDirectIndirect(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		// TODO: is this the proper way to cast from Boolean to Double?
		obs.setValueNumeric(direct ? 1.0 : 0.0);
    }

    public void setMethod(Concept method) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDSTMethod());
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && method == null) {
			return;
		}
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptDSTMethod(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(method);
    }


    public void setOrganismType(Concept organismType) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptTypeOfOrganism());
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptTypeOfOrganism(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(organismType);
    }
	
	

}
