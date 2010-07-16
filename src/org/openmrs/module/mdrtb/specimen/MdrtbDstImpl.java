package org.openmrs.module.mdrtb.specimen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	Map<String,MdrtbDstResult> resultsMap = null; // TODO: we need to cache the results map... do we need to worry about timing it out? 
	
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

	public MdrtbDstResult addResult() {
		// create a new obs for the result, set to the proper values
		Obs resultObs = new Obs(this.test.getPerson(), mdrtbFactory.getConceptDSTResultParent(), this.test.getObsDatetime(), this.test.getLocation());
		resultObs.setEncounter(this.test.getEncounter());
		
		// add the result to this obs group
		this.test.addGroupMember(resultObs);
		
		// now create and return a new DstResult
		return new MdrtbDstResultImpl(resultObs);
	}
	
    public Integer getColoniesInControl() {
    	Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptColoniesInControl());
    	
    	if (obs == null || obs.getValueNumeric() == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
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

    public List<MdrtbDstResult> getResults() {
    	List<MdrtbDstResult> results = new LinkedList<MdrtbDstResult>();
		
		// iterate through all the obs groups, create dst results from them, and add them to the list
		if(test.getGroupMembers() != null) {
			for(Obs obs : test.getGroupMembers()) {
				if (obs.getConcept().equals(mdrtbFactory.getConceptDSTResultParent())) {
					results.add(new MdrtbDstResultImpl(obs));
				}
			}
		}
		return results;
    }
    
    // Note this is created ONCE per instantiation, for performance reasons, so if underlying drugs change, this will be inaccurate
    public Map<String,MdrtbDstResult> getResultsMap() {  
    
    	if (resultsMap == null) {
    		resultsMap = new HashMap<String,MdrtbDstResult>();
    	
    		// map the results based on a key created by concatenating the string representation of the drug concept id and the
    		// string representation of the concentration
    		for(MdrtbDstResult result : getResults()) {
    			if(result.getConcentration() != null) {
    				resultsMap.put((result.getDrug().getId()).toString() + "|" + result.getConcentration().toString(), result);
    			}
    			else {
    				resultsMap.put((result.getDrug().getId()).toString(), result);
    			}
    		}
    	}
    	
    	return resultsMap;
    }
    
    public void setColoniesInControl(Integer coloniesInControl) {
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
		obs.setValueNumeric(coloniesInControl.doubleValue());
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
