package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.module.mdrtb.status.VisitStatus;
import org.openmrs.module.mdrtb.status.VisitStatusRenderer;
import org.openmrs.web.WebConstants;


public class DashboardVisitStatusRenderer implements VisitStatusRenderer {
	
    public void renderVisit(StatusItem visit, VisitStatus status) {
    	
    	Encounter encounter = (Encounter) visit.getValue();
    	
    	DateFormat df = DateFormat.getDateInstance();
    	
    	String[] params = { df.format(encounter.getEncounterDatetime()), encounter.getLocation().getDisplayString()};
    	
    	visit.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.visitStatus.visit", params,
		    "{0} at {1}", Context.getLocale()));
    	
    	// now determine where to link to
    	// if there is a form linked to this encounter, assume it is an HTML Form Entry form
    	if(encounter.getForm() != null) {
    		visit.setLink("/" + WebConstants.WEBAPP_NAME + "/module/htmlformentry/htmlFormEntry.form?personId=" + encounter.getPatientId() 
    			+ "&formId=" + encounter.getForm().getId() + "&encounterId=" + encounter.getId() + 
    			"&mode=VIEW&returnUrl=" + "/" + WebConstants.WEBAPP_NAME 
    			+ "/module/mdrtb/dashboard/dashboard.form?patientProgramId=" + status.getPatientProgram().getId() 
    			+ "&patientId=" + encounter.getPatientId());
    	}
    	// otherwise, create the link based on the encounter type of the visit
    	else {
    	
    		EncounterType type = encounter.getEncounterType();
    	
    		if (type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")))) {
    			// TODO: add proper link
    		}
    		else if (type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")))) {
    			// TODO: add proper link
    		}
    		else if(type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {
    			visit.setLink("/" + WebConstants.WEBAPP_NAME + "/module/mdrtb/specimen/specimen.form?specimenId=" + encounter.getId()
    							+ "&patientProgramId=" + status.getPatientProgram().getId());
    		}
    		else {
    			throw new MdrtbAPIException("Invalid encounter type passed to Dashboard visit status renderer.");
    		}
    	}
    } 
}
