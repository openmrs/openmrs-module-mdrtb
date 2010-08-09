package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.module.mdrtb.regimen.Regimen;


public class DrugCellTag extends TagSupport {

    private static final long serialVersionUID = 6323202853285105042L;

	private final Log log = LogFactory.getLog(getClass());
	
	private Concept drug;
	
	private List<Regimen> regimens;

	  public int doStartTag() {
		   
		  	//log.error("Drug = " + drug + " Regimens = " + regimens);
		  
	    	String ret = null;
	    	
	    	String colorString = "";
	    	
	    	if(regimens != null) {
	    		for(Regimen regimen : regimens) {
	    			if(regimen.containsDrugConcept(drug)) {
	    				colorString="gray";
	    			}
	    		}
	    	}
	    	
	    	if(StringUtils.isNotBlank(colorString)) {
	    		ret = "<td class=\"chartCell\" style=\"background-color:" + colorString + "\"/>";
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
}
