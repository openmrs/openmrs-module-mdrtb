package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;

/**
 * An implementaton of a MdrtbCulture.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class MdrtbCultureImpl extends MdrtbTestImpl implements MdrtbCulture {

	public MdrtbCultureImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}
	
	// set up a smear object, given an existing obs
	public MdrtbCultureImpl(Obs culture) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(culture == null || !(culture.getConcept().equals(mdrtbFactory.getConceptCultureParent()))) {
			throw new RuntimeException ("Cannot initialize culture: invalid obs used for initialization.");
		}
		
		test = culture;
	}
	
	// create a new smear object, given an existing patient
	public MdrtbCultureImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), mdrtbFactory.getConceptCultureParent(), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "culture";
	}
	
    public Double getColonies() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptColonies());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric();
    	}
    }
    
    public Concept getMethod() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptCultureMethod());
    	
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
    
    public Concept getResult() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptCultureResult());
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public void setColonies(Double colonies) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptColonies());
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptColonies(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(colonies);
    }   

    public void setMethod(Concept method) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptCultureMethod());
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptCultureMethod(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(method);
    }
    
    public void setOrganismType(Concept organismType) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptTypeOfOrganism());
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptTypeOfOrganism(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(organismType);
    }

    public void setResult(Concept result) {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptCultureResult());
    	
    	// initialize the obs if we need to
		if (obs == null) {
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptCultureResult(), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the data
		obs.setValueCoded(result);
    }
}
