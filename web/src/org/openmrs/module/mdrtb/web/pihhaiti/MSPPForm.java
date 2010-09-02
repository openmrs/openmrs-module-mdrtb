package org.openmrs.module.mdrtb.web.pihhaiti;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.specimen.Specimen;

public class MSPPForm {
	
	List<Specimen> specimens;

	
	/**
	 * Generic construct
	 */
	public MSPPForm() {
		this.specimens = new LinkedList<Specimen>();
	}
	
	public MSPPForm(List<Specimen> specimens) {
		this.specimens = specimens;
		
		Collections.sort(this.specimens, new SpecimenComparator());
	}
	
	// initialize with empty obs
	public void initialize(Patient patient) {
		if(patient == null) {
			throw new MdrtbAPIException("Person cannot be null when initializing MSPPForm");
		}
		
		// add three empty specimens with a single smear
		for(int i = 1; i < 4; i++) {
			Specimen specimen = Context.getService(MdrtbService.class).createSpecimen(patient);
			specimen.addSmear();
			this.addSpecimen(specimen);
		}
	}
	
	public void addSpecimen(Specimen specimen) {
		if(specimen != null) {
			this.specimens.add(specimen);
			Collections.sort(specimens, new SpecimenComparator());
		}
	}
	
	public List<Specimen> getSpecimens() {
		return this.specimens;
	}
	
    public Location getLocation() {
    	if(specimens.size() == 0) {
    		return null;
    	} else {
    		return specimens.get(0).getLocation();
    	}
    }

    public void setLocation(Location location) {
    	for(Specimen specimen : this.getSpecimens()) {
    		specimen.setLocation(location);
    	}
    }

	
    public Person getProvider() {
    	if(specimens.size() == 0) {
    		return null;
    	} else {
    		return specimens.get(0).getProvider();
    	}
    }

	
    public void setProvider(Person provider) {
    	for(Specimen specimen : this.getSpecimens()) {
    		specimen.setProvider(provider);
    	}
    }

	
    public Date getDateCollected() {
    	if(specimens.size() == 0) {
    		return null;
    	} else {
    		return specimens.get(0).getDateCollected();
    	}
    }

	
    public void setDateCollected(Date dateCollected) {
    	for(Specimen specimen : this.getSpecimens()) {
    		specimen.setDateCollected(dateCollected);
    	}
    }
	
    public Concept getAppearance() {
    	if(specimens.size() == 0) {
    		return null;
    	}
    	
    	return specimens.get(0).getAppearance();
    }

	
    public void setAppearance(Concept appearance) {
    	for(Specimen specimen : this.getSpecimens()) {
    		specimen.setAppearance(appearance);
    	}
    }
    
    /**
     * Utility methods
     */
	
    // sort by encounter id
    private class SpecimenComparator implements Comparator<Specimen> {

        public int compare(Specimen specimen1, Specimen specimen2) {
        	if(specimen1 == null) {
        		return 1;
        	} 
        	if(specimen2 == null) {
        		return -1;
        	}
        	
        	// compare first by encounter date, and then by encounter id
        	if(specimen1.getDateCollected() == null) {
        		return 1;
        	} 
        	if(specimen2.getDateCollected() == null) {
        		return -1;
        	}
       
        	int dateCompare = specimen1.getDateCollected().compareTo(specimen2.getDateCollected());
        	
        	if(dateCompare != 0) {
        		return dateCompare;
        	}
        	
        	if(specimen1.getId() == null) {
        		return 1;
        	}
        	if(specimen2.getId() == null) {
        		return -1;
        	}
        	
        	return specimen1.getId().compareTo(specimen2.getId());
        }
    }
    
}
