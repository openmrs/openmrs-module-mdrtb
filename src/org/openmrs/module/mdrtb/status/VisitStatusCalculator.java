package org.openmrs.module.mdrtb.status;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;


public class VisitStatusCalculator implements StatusCalculator {

	private VisitStatusRenderer renderer;
	
	public VisitStatusCalculator(VisitStatusRenderer renderer) {
		this.setRenderer(renderer);
	}
	
	// TODO: flags to add:
	// if there is more than one intake encounter?
	// if there is no scheduled follow up?
	
	
    public Status calculate(PatientProgram program) {
	   // create the new status
    	VisitStatus status = new VisitStatus(program);
    	
    	EncounterType intakeType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
    	EncounterType followUpType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
    	EncounterType specimenType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
    	
    	
    	// where we will store the various visits
    	List<StatusItem> intakeVisits = new LinkedList<StatusItem>();
    	List<StatusItem> followUpVisits = new LinkedList<StatusItem>();
    	List<StatusItem> scheduledFollowUpVisits = new LinkedList<StatusItem>();
    	List<StatusItem> specimenCollectionVisits = new LinkedList<StatusItem>();
    	
    	
    	// get all the encounters during the program
    	List<Encounter> encounters = StatusUtil.getMdrtbEncountersDuringProgram(program);
   
    	for (Encounter encounter : encounters) {
    		// create a new status item for this encounter
    		StatusItem visit = new StatusItem();
    		visit.setValue(encounter);
    		visit.setDate(encounter.getEncounterDatetime());
    		renderer.renderVisit(visit, status);
    	
    		// now place the visit in the appropriate "bucket"
    		if (encounter.getEncounterType().equals(intakeType)) {
    			intakeVisits.add(visit);
    		}
    		else if (encounter.getEncounterType().equals(specimenType)) {
    			specimenCollectionVisits.add(visit);
    		}
    		else if (encounter.getEncounterType().equals(followUpType)) {
    			if (encounter.getEncounterDatetime().after(new Date())) {
    				scheduledFollowUpVisits.add(visit);
    			}
    			else {
    				followUpVisits.add(visit);
    			}
    		}
    	}
    	
    	// add all the lists to the main status 
    	status.addItem("intakeVisits", new StatusItem(intakeVisits));
    	status.addItem("specimenCollectionVisits", new StatusItem(specimenCollectionVisits));
    	status.addItem("scheduledFollowUpVisits", new StatusItem(scheduledFollowUpVisits));
    	status.addItem("followUpVisits", new StatusItem(followUpVisits));
    	
    	// now handle adding the links that we should use for the new intake and follow-up visits
    	// (the logic to determine these links is basically delegated to the renderer
    	StatusItem newIntakeVisit = new StatusItem();
    	renderer.renderNewIntakeVisit(newIntakeVisit, status);
    	status.addItem("newIntakeVisit", newIntakeVisit);
    	
     	StatusItem newFollowUpVisit = new StatusItem();
    	renderer.renderNewFollowUpVisit(newFollowUpVisit, status);
    	status.addItem("newFollowUpVisit", newFollowUpVisit);
    	
    	return status;
    }


	public void setRenderer(VisitStatusRenderer renderer) {
	    this.renderer = renderer;
    }


	public VisitStatusRenderer getRenderer() {
	    return renderer;
    }

}
