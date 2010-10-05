package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.status.VisitStatusRenderer;


public class DashboardVisitStatusRenderer implements VisitStatusRenderer {
	
    public String renderVisit(Encounter encounter) {
    	DateFormat df = DateFormat.getDateInstance();
    	
    	String[] params = { df.format(encounter.getEncounterDatetime()), encounter.getLocation().getDisplayString()};
		return Context.getMessageSourceService().getMessage("mdrtb.visitStatus.visit", params,
		    "{0} at {1}", Context.getLocale());
    }

}
