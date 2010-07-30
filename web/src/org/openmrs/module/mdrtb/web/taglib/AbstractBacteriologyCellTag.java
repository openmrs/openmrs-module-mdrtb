package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;


public abstract class AbstractBacteriologyCellTag extends TagSupport {

    private static final long serialVersionUID = 6161336882128086538L;
    
	private final Log log = LogFactory.getLog(getClass());
   
    protected void renderCell(Test test, Concept result) {
    	String ret = null;
    		
    	if(test == null) {
    		// handle the null case
    		ret = "<td style=\"padding:0px;border:0px;margin:0px;\"/>";
    	}
    	else {
    		
    		String title= "";
    		
    		String content = "";
    		
    		String color;
    		
    		// if we have a result, configure the cell to display the result
    		if(result != null) {
    			title = "Result: " + result.getBestName(Context.getLocale()) + "<br/>Lab: " + test.getLab().getName();
    			content = result.getBestShortName(Context.getLocale()).getName();
    			color = Context.getService(MdrtbService.class).getColorForConcept(result);
    			
    			if (color == null) {
    				color = "black";
    			}
    		}
    
    		// otherwise, configure the cell to display the status 
    		else {
    			title = "Status: " + TestStatusRenderer.renderStandardStatus(test) + "<br/>Lab: " + test.getLab().getName();
    			color = "grey";  // probably want to get rid of hardcoding the color
    		}
    		
    		// TODO: using the ../ is a little sketchy because it relies on directory structure not changing?
    		ret = "<td onmouseover=\"document.body.style.cursor = \'pointer\'\" onmouseout=\"document.body.style.cursor = \'default\'\" " +
    				"onclick=\"window.location = \'../specimen/specimen.form?specimenId=" + test.getSpecimenId() + "&testId=" + test.getId() +
    				"\'\" title=\"" + title + "\" style=\"text-align:center;font-style:bold;padding:0px;border:0px;margin:0px;background-color:" + 
    				color + ";\">&nbsp;" + content + "&nbsp;</td>";
    	}
    
    		
    	try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting tag", ex);
		}
    }
}
