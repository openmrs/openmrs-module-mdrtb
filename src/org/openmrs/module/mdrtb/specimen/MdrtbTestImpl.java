package org.openmrs.module.mdrtb.specimen;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbConstants.MdrtbTestType;


public abstract class MdrtbTestImpl implements MdrtbTest {
	
	Obs test;  // the top-level obs that holds all the data for this smear
	
	MdrtbFactory mdrtbFactory;
	
	public MdrtbTestImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}
	
	/** implementing subclasses must override this method to define their type */
	public abstract MdrtbTestType getTestType();
	
	public Object getTest() {
		return this.test;
	}
	
	public String getId() {
		return this.test.getId().toString();
	}

	public String getStatus() {
		return calculateStatus();
	}
	
	public String getSpecimenId() {
		return this.test.getEncounter().getEncounterId().toString();
	}
	
	public Date getDateOrdered() {
		Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDateOrdered());
	    	
		if (obs == null) {
			return null;
	    }
	    else {
	    	return obs.getValueDatetime();
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
		return test.getLocation();
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

	 public void setDateOrdered(Date dateOrdered) {
	    Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDateOrdered());
			
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueDatetime().equals(dateOrdered)) {
				
			// void the existing obs if it exists
			voidObsIfNotNull(obs);
					
			// now create the new Obs and add it to the encounter
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptDateOrdered(), test.getObsDatetime(), test.getLocation());
			obs.setValueDatetime(dateOrdered);
			test.addGroupMember(obs);
		}	
	}
	    
	 public void setDateReceived(Date dateReceived) {
	    Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptDateReceived());
			
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueDatetime().equals(dateReceived)) {
				
			// void the existing obs if it exists
			voidObsIfNotNull(obs);
					
			// now create the new Obs and add it to the encounter
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptDateReceived(), test.getObsDatetime(), test.getLocation());
			obs.setValueDatetime(dateReceived);
			test.addGroupMember(obs);
		}
	 }

	 public void setLab(Location location) {
	    test.setLocation(location);
			
		// also propagate this location to the all the child obs
	    if(test.getGroupMembers() != null) {
	    	for(Obs obs : test.getGroupMembers()) {
	    		obs.setLocation(location);
	    	}
	    }
	 }

	 public void setResultDate(Date resultDate) {
	    Obs obs = getObsFromObsGroup(mdrtbFactory.getConceptResultDate());
			
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !obs.getValueDatetime().equals(resultDate)) {
				
			// void the existing obs if it exists
			voidObsIfNotNull(obs);
					
			// now create the new Obs and add it to the encounter
			obs = new Obs (test.getPerson(), mdrtbFactory.getConceptResultDate(), test.getObsDatetime(), test.getLocation());
			obs.setValueDatetime(resultDate);
			test.addGroupMember(obs);
		}
	 }

	    /**
	     * Protected methods used for interacting with the matching MdrSpecimenImpl
	     */

	    protected Obs getObs() {
	    	return test;
	    }
	    
	    /**
		 * Utility methods 
		 */
		
		/**
		 * Iterates through all the obs in the smear obs group and
		 * returns the first one that who concept matches the specified concept
		 * Returns null if obs not found
		 */
	    protected Obs getObsFromObsGroup(Concept concept) {
	    	if (test.getGroupMembers() != null) {
	    		for(Obs obs : test.getGroupMembers()) {
	    			if (obs.getConcept().equals(concept)) {
	    				return obs;
	    			}
	    		}
	    	}
	    	return null;
	    }
	    
	    /**
	     * Voids an obs if it isn't null
	     */
	    protected void voidObsIfNotNull(Obs obs) {
	    	if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
	    }
	    
	    
	    /**
	     * Determines the current status of this test by examines the
	     * values of the various date fields
	     * Auto generated method comment
	     * 
	     * @return
	     */
	    private String calculateStatus() {
	    	// TODO: determine the best way to localize all the text in this method

	    	if (getResultDate() != null) {
	    		return "Completed on " + getResultDate() + " at " + getLab();
	    	}
	    	else if (getDateReceived() != null) {
	    		return "Received by " + getLab() + " on " + getDateReceived();
	    	}
	    	else if (getDateOrdered() != null) {
	    		return "Ordered on " + getDateOrdered() + " from " + getLab();
	    	}
	    	else {
	    		return "Unknown";
	    	}
	    }
}
