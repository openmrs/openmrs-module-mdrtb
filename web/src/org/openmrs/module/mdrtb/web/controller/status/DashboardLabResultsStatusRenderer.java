package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;
import org.openmrs.module.mdrtb.status.LabResultsStatusRenderer;
import org.openmrs.module.mdrtb.status.StatusFlag;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.web.WebConstants;

public class DashboardLabResultsStatusRenderer implements LabResultsStatusRenderer {
	
	public String renderSmear(Smear smear) {
		
		if (smear != null) {
			String[] params = { smear.getResult().getBestShortName(Context.getLocale()).toString(),
			        smear.getResultDate() != null ? DateFormat.getDateInstance().format(smear.getResultDate()) : "(N/A)",
			        smear.getLab() != null ? smear.getLab().getDisplayString() : "(N/A)" };
			
			return "<a href=\"/"
			        + WebConstants.WEBAPP_NAME
			        + "/module/mdrtb/specimen/specimen.form?specimenId="
			        + smear.getSpecimenId()
			        + "&testId="
			        + smear.getId()
			        + "\">"
			        + Context.getMessageSourceService().getMessage("mdrtb.smearFormatter", params,
			            "{0} on {1} at {2}", Context.getLocale()) + "</a>";
		} else {
			return "";
			//return Context.getMessageSourceService().getMessage("mdrtb.noSmears");
		}
	}
	
	public String renderCulture(Culture culture) {
		
		if (culture != null) {
			String[] params = {
			        culture.getResult().getBestShortName(Context.getLocale()).toString(),
			        culture.getResultDate() != null ? DateFormat.getDateInstance().format(culture.getResultDate()) : "(N/A)",
			        culture.getLab() != null ? culture.getLab().getDisplayString() : "(N/A)" };
			
			return "<a href=\"/"
			        + WebConstants.WEBAPP_NAME
			        + "/module/mdrtb/specimen/specimen.form?specimenId="
			        + culture.getSpecimenId()
			        + "&testId="
			        + culture.getId()
			        + "\">"
			        + Context.getMessageSourceService().getMessage("mdrtb.cultureFormatter", params,
			            "{0} on {1} at {2}", Context.getLocale()) + "</a>";
		} else {
			return "";
			//return Context.getMessageSourceService().getMessage("mdrtb.noCultures");
		}
	}
	
    public String renderDrugResistanceProfile(List<Concept> drugs) {
    	return DashboardStatusRendererUtil.renderDrugList(drugs);
    }
	
	public String renderPendingLabResults(List<Test> tests) {
		StringBuffer displayString = new StringBuffer();
		DateFormat df = DateFormat.getDateInstance();
		
		for (Test test : tests) {
			TestStatus status = test.getStatus();
			
			// kind of hacky to include the a href and tr/td here
			displayString.append("<tr><td><a href=\"/" + WebConstants.WEBAPP_NAME
			        + "/module/mdrtb/specimen/specimen.form?specimenId=" + test.getSpecimenId() + "&testId=" + test.getId()
			        + "\">");
			
			// get the test type and capitalize the first character
			String testType = Character.toUpperCase(test.getTestType().charAt(0)) + test.getTestType().substring(1);
			
			if (status == TestStatus.STARTED) {
				String[] params = { test.getLab().getDisplayString(), df.format(test.getStartDate()), testType };
				displayString.append(Context.getMessageSourceService().getMessage("mdrtb.labResultsStatus.started", params,
				    "{2} started on {1} at {0}", Context.getLocale()));
			} else if (status == TestStatus.RECEIVED) {
				String[] params = { test.getLab().getDisplayString(), df.format(test.getDateReceived()), testType };
				displayString.append(Context.getMessageSourceService().getMessage("mdrtb.labResultsStatus.received", params,
				    "{2} received by {0} at {1}", Context.getLocale()));
			} else if (status == TestStatus.ORDERED) {
				String[] params = { test.getLab().getDisplayString(), df.format(test.getDateOrdered()), testType };
				displayString.append(Context.getMessageSourceService().getMessage("mdrtb.labResultsStatus.ordered", params,
				    "{2} ordered on {1} from {0}", Context.getLocale()));
			} else {
				String[] params = { testType };
				displayString.append(Context.getMessageSourceService().getMessage("mdrtb.labResultsStatus.unknown", params,
				    "{0} with status unknown", Context.getLocale()));
			}
			
			displayString.append("</a></td></tr>");
		}
		
		return displayString.toString();
	}

    public String renderTbClassification(TbClassification classification) {
	   
    	StringBuffer displayString = new StringBuffer();
    	displayString.append(Context.getMessageSourceService().getMessage("mdrtb.confirmed") + " ");
	    
	    if (classification == TbClassification.MONO_RESISTANT_TB) {
	    	displayString.append(Context.getMessageSourceService().getMessage("mdrtb.monoResistantTb"));
	    } 
	    else if (classification == TbClassification.POLY_RESISTANT_TB){
	    	displayString.append(Context.getMessageSourceService().getMessage("mdrtb.polyResistantTb"));
	    } 
	    else if (classification == TbClassification.MDR_TB) {
	    	displayString.append(Context.getMessageSourceService().getMessage("mdrtb.mdrtb"));
	    }
	    else if (classification == TbClassification.XDR_TB) {
	    	displayString.append(Context.getMessageSourceService().getMessage("mdrtb.xdrtb"));
	    }
	    else {
	    	displayString = new StringBuffer();
	    	displayString.append(Context.getMessageSourceService().getMessage("mdrtb.none"));
	    }
	    
	    return displayString.toString();
    }
	
    public String renderCultureConversion(StatusItem cultureConversion) {
    DateFormat df = DateFormat.getDateInstance();
    	
	   if ( ((Boolean) cultureConversion.getValue()) == false) {
		   return Context.getMessageSourceService().getMessage("mdrtb.notConverted");
	   }
	   else {
		   String[] params = { df.format(cultureConversion.getDate()) };
		   return Context.getMessageSourceService().getMessage("mdrtb.converted", params,
			    "Converted on {0}", Context.getLocale());
	   }
    }
    
	public StatusFlag createNoSmearsFlag() {
		StatusFlag flag = new StatusFlag();
		flag.setMessage(Context.getMessageSourceService().getMessage("mdrtb.noSmearResults"));
		return flag;
	}
	
	public StatusFlag createNoCulturesFlag() {
		StatusFlag flag = new StatusFlag();
		flag.setMessage(Context.getMessageSourceService().getMessage("mdrtb.noCultureResults"));
		return flag;
	}

}
