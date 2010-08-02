package org.openmrs.module.mdrtb.specimen;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class SmearImpl extends TestImpl implements Smear {
	
	public SmearImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}

	// set up a smear object, given an existing obs
	public SmearImpl(Obs smear) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(smear == null || !(smear.getConcept().equals(mdrtbFactory.getConceptSmearParent()))) {
			throw new RuntimeException ("Cannot initialize smear: invalid obs used for initialization.");
		}
		
		test = smear;
	}
	
	// create a new smear object, given an existing patient
	public SmearImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create smear: encounter can not be null.");
		}
		
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs (encounter.getPatient(), mdrtbFactory.getConceptSmearParent(), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "smear";
	}
	
    public Integer getBacilli() {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptBacilli(), test);
    	
    	if (obs == null || obs.getValueNumeric() == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
    	}
    }
    
    public String getComments() {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptSmearResult(), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
      
    public Concept getMethod() {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptSmearMicroscopyMethod(), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public Concept getResult() {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptSmearResult(), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }

    public void setBacilli(Integer bacilli) {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptBacilli(), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && bacilli == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(bacilli == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
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

    public void setComments(String comments) {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptSmearResult(), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptSmearResult(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
    
    public void setMethod(Concept method) {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptSmearMicroscopyMethod(), test);
    	
    	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && method == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(method == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
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
    	Obs obs = MdrtbUtil.getObsFromObsGroup(mdrtbFactory.getConceptSmearResult(), test);
    	
    	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && result == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(result == null && StringUtils.isBlank(obs.getComment())) {  // we also need to make sure that there is no comment on the obs in this case
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
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

