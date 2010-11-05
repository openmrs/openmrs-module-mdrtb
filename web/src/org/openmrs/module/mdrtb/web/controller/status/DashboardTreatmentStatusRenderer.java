package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.MdrtbConstants.TreatmentState;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.status.TreatmentStatusRenderer;


public class DashboardTreatmentStatusRenderer implements TreatmentStatusRenderer {

    public String renderRegimen(Regimen regimen) {
    	  	
    	DateFormat df = MdrtbConstants.dateFormatDisplay;
    	
    	// first we need to pull out all the drugs in this regimen
    	List<Concept> generics = new LinkedList<Concept>();
    	for (RegimenComponent component : regimen.getComponents()) {
    		// TODO: note that we are operating on generics, not the drug itself
    		generics.add(component.getGeneric());
    	}
    	
    	// sort the drug list
    	generics = MdrtbUtil.sortMdrtbDrugs(generics);
    	
    	// get end reason, if there is one
    	String endReason = "";
    	if (regimen.getEndReason() != null) {
    		endReason = regimen.getEndReason().getDisplayString();
    	}
    	
	    String displayString = "<tr><td><nobr>" + DashboardStatusRendererUtil.renderDrugList(generics) + "</nobr></td><td><nobr>" 
	    	+ df.format(regimen.getStartDate()) + "</nobr></td><td>" 
	    	+ (regimen.getEndDate() != null ? df.format(regimen.getEndDate()) : Context.getMessageSourceService().getMessage("mdrtb.present")) + "</nobr></td><td><nobr>"
	        + endReason + "</nobr></td></tr>";
	    
	    return displayString;
    }

    public String renderTreatmentState(TreatmentState state) {
	   if (state == TreatmentState.ON_TREATMENT) { 
		   return Context.getMessageSourceService().getMessage("mdrtb.onTreatment");
		   
	   }
	   else if (state == TreatmentState.NOT_ON_TREATMENT) {
		   return Context.getMessageSourceService().getMessage("mdrtb.notOnTreatment");	   
	   }
	   else {
		   return "";
	   }
    }

}
