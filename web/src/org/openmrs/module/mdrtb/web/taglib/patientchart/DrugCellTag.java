package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.util.OpenmrsUtil;


public class DrugCellTag extends TagSupport {

    private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());
	
	private Concept drug;
	
	private List<Regimen> regimens;
	
	private String style;
	
	  public int doStartTag() {
		 
	    	String ret = null;
	    	
	    	String colorString = "";
	    	
	    	StringBuffer titleString = new StringBuffer();
	    	
	    	if(regimens != null) {
	    		for(Regimen regimen : regimens) {
	    			RegimenComponent component = regimen.getRegimenComponentByDrugConcept(drug);
	    			if(component != null) {
	    				updateTitle(titleString, component);
	    				// TODO: make this a global prop, make sure it's in sync with color in dstresultscell
	    				colorString="skyblue";
	    			}
	    		}
	    	}
	    	
	    	if(StringUtils.isNotBlank(colorString)) {
	    		ret = "<td class=\"chartCell\" title=\"" + titleString.toString() + "\" style=\"background-color:" + colorString + "\"/>";
	    	}
	    	else {
	    		ret = "<td class=\"chartCell\"/>";
	    	}
	    		
	    	try {
				JspWriter w = pageContext.getOut();
				w.println(ret);
			}
			catch (IOException ex) {
				log.error("Error while starting drug cell tag", ex);
			}
	     
	    	return SKIP_BODY;
	    }
	
	public int doEndTag() {
	    	this.drug = null;
	    	this.regimens = null;
	    	return EVAL_PAGE;
	 }
	
	/**
	 * Utility methods
	 */

	/**
	 * Adds the details of the specified regimen component (i.e. drug order) to the
	 * title (which can be rendered in a tooltip)
	 */
	private static void updateTitle(StringBuffer titleString, RegimenComponent component) {
		
		// if the component is null or a drug order is missing, do nothing--protect against null pointer crashing everything
		if(component == null || component.getDrugOrder() == null || component.getDrug() == null) {
			return;
		}
		
		SimpleDateFormat dateFormat = OpenmrsUtil.getDateFormat(Context.getLocale());
		
		titleString.append("<nobr>");
		titleString.append(component.getDrug().getName());
		titleString.append(" ");
		titleString.append(component.getDrugOrder().getDose());
		titleString.append(" ");
		titleString.append(component.getDrugOrder().getUnits());
		titleString.append("</nobr><br/><nobr>");
		titleString.append(component.getDrugOrder().getFrequency());
		titleString.append("</nobr><br/><nobr>");
		titleString.append(dateFormat.format(component.getStartDate()));
		titleString.append("</nobr>");
		
		if(component.getStopDate() != null) {
			titleString.append("<br/><nobr>");
			titleString.append(dateFormat.format(component.getStopDate()));
			titleString.append("</nobr>");
		}
		
		if(component.getStopReason() != null) {
			titleString.append("<br/><nobr>");
			titleString.append(component.getStopReason().getBestName(Context.getLocale()));
			titleString.append("</nobr>");
		}
		
		titleString.append("<br/>");
	}

	
	
	public void setDrug(Concept drug) {
	    this.drug = drug;
    }

	public Concept getDrug() {
	    return drug;
    }

	public void setRegimens(List<Regimen> regimens) {
	    this.regimens = regimens;
    }

	public List<Regimen> getRegimens() {
	    return regimens;
    }

	public void setStyle(String style) {
	    this.style = style;
    }

	public String getStyle() {
	    return style;
    }
}
