package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.MdrtbConstants.TreatmentState;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.status.TreatmentStatusRenderer;


public class DashboardTreatmentStatusRenderer implements TreatmentStatusRenderer {

    public String renderRegimen(Regimen regimen) {
    	  	
    	DateFormat df = MdrtbConstants.DATE_FORMAT_DISPLAY;
    	
    	// first we need to pull out all the drugs in this regimen
    	List<Concept> generics = new ArrayList<Concept>(regimen.getUniqueGenerics());
    	
    	// sort the drug list
    	generics = MdrtbUtil.sortMdrtbDrugs(generics);
    	
    	// get end reason, if there is one
    	String endReason = "";
		for (Concept c : regimen.getEndReasons()) {
			endReason += ("".equals(endReason) ? "" : ", ") + c.getDisplayString();
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
