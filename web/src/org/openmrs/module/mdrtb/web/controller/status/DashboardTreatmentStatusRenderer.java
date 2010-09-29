package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.status.StatusUtil;
import org.openmrs.module.mdrtb.status.TreatmentStatusRenderer;


public class DashboardTreatmentStatusRenderer implements TreatmentStatusRenderer {

    public String renderRegimen(Regimen regimen) {
    	  	
    	DateFormat df = DateFormat.getDateInstance();
    	
    	// first we need to pull out all the drugs in this regimen
    	List<Concept> drugs = new LinkedList<Concept>();
    	for (RegimenComponent component : regimen.getComponents()) {
    		drugs.add(component.getDrug().getConcept());
    	}
    	
    	// sort the drug list
    	drugs = StatusUtil.sortDrugs(drugs);
    	
    	// get end reason, if there is one
    	String endReason = "";
    	if (regimen.getEndReason() != null) {
    		endReason = regimen.getEndReason().getDisplayString();
    	}
    	
	    String displayString = "<tr><td>" + DashboardStatusRendererUtil.renderDrugList(drugs) + "</td><td>" 
	    	+ df.format(regimen.getStartDate()) + "</td><td>" 
	    	+ (regimen.getEndDate() != null ? df.format(regimen.getEndDate()) : Context.getMessageSourceService().getMessage("mdrtb.present")) + "</td><td>"
	        + endReason + "</td><td>type</td></tr>";
	    
	    return displayString;
    }

}
