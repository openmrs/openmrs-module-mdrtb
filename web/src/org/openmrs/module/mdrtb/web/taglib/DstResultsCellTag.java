package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.DstResult;


public class DstResultsCellTag extends TagSupport {
	
    private static final long serialVersionUID = -1694368107155244376L;
    
	private final Log log = LogFactory.getLog(getClass());
	
    private List<DstResult> dstResults;
    
    public int doStartTag() {
   
    	String ret = null;
    	
    	if(dstResults != null && !dstResults.isEmpty()) {
    		String title = "";
    		Concept result = null;
    		
    		// first we need to build the title string (for the tooltip) and determine the result
    		for(DstResult dstResult : dstResults) {
    			
    			// need to ignore "none" results here
    			if(dstResult.getResult() != null && dstResult.getResult() != Context.getService(MdrtbService.class).getConceptNone()) {
    			
    			String concentration = "";
    		
    				if(dstResult.getConcentration() != null) {
    					concentration = dstResult.getConcentration().toString() + " - ";
    				}
    		
    				title = title + concentration + dstResult.getResult().getBestShortName(Context.getLocale()) + "<br>";
    
    				// TODO: figure out if this is the right rule if there are multiple results for the same drug
    				// right now I'm setting the result to intermediate (so it will pick up that color)
    				if(result == null) {
    					result = dstResult.getResult();
    				}
    				else if (!result.equals(dstResult.getResult())) {
    				    result = Context.getService(MdrtbService.class).getConceptIntermediateToTuberculosisDrug();
    				    break;
    				}
    			}
    		}
    		
    		String color = Context.getService(MdrtbService.class).getColorForConcept(result);
    		
    		ret = "<td class=\"chartCell\" title=\"" + title +
    				"\" style=\"width:30px;padding:0px;border:0px;margin:0px;background-color:" + color + ";\">&nbsp</td>";
    	}
    	else {
    		ret = "<td class=\"chartCell\"/>";
    	}
    		
    	try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting dst result tag", ex);
		}
     
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	dstResults = null;
    	return EVAL_PAGE;
    }
    
	public void setDstResults(List<DstResult> dstResults) {
	    this.dstResults = dstResults;
    }

	public List<DstResult> getDstResults() {
	    return dstResults;
    }
}
