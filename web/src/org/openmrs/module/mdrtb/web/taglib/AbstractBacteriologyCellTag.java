package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.BacteriologyResult;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;


public abstract class AbstractBacteriologyCellTag extends TagSupport {

    private static final long serialVersionUID = 6161336882128086538L;
    
	private final Log log = LogFactory.getLog(getClass());
	
    protected void renderCell(List<Bacteriology> bacs) {
    	
    	String titleString = "";
    	
    	String colorString = "";
    	
    	String labString = "";
    	
    	String resultString = "";
    	
    	String ret = "";
    	
    	if(bacs == null || bacs.isEmpty()) {
    		// handle the null case
    		ret = "<td style=\"padding:0px;border:0px;margin:0px;\"/>";
    	}
    	else {
    		
    		// since there is at least one bac here, we want the color of the cell to be gray, if nothing else
    		colorString = "gray";
    		
    		// loop through all the bacteriologies in this set and fetch the result for each of them
    		BacteriologyResult overallResult = null;
    		Set<Location> labs = new HashSet<Location> ();
    	
    		for(Bacteriology bac : bacs) {
    			if(bac != null) {		
    				// update the lab string based on this bac
    				// only add this lab to the display list if we've yet to display it
					if(!labs.contains(bac.getLab())) {
						// add a slash if we have multiple labs
						if(StringUtils.isNotEmpty(labString)) {
							labString = labString.concat(" - ");
						}
						
						// add the lab name to the display string
						labString = labString.concat(Context.getService(MdrtbService.class).getDisplayCodeForLocation(bac.getLab()));
						
						// now mark that we've already handled this lab
						labs.add(bac.getLab());
					}
    			
    				// if there's a valid result....
    				if(bac.getResult() != null) {
    					// now look at the result
    					BacteriologyResult result = Context.getService(MdrtbService.class).getBacteriologyResultForConcept(bac.getResult());
    					
    					// append the appropriate result to the result list
    					if(result != null) {
    						resultString = resultString.concat(Context.getMessageSourceService().getMessage(result.getDisplayCode()) + " ");
    					}
    						
    					// append the appropriate title to the title list
    					titleString = titleString.concat(bac.getResult().getBestName(Context.getLocale()).toString() + " - " 
    						+ bac.getLab().getDisplayString() + "<br/>");
    					
    					// now figure the overall result for the purpose of determining the color of the cell
    					overallResult = updateBacteriologyResult(overallResult, result);
    				}
    				// if there isn't a result, add the status here
    				else {
    					titleString = titleString.concat(TestStatusRenderer.renderStandardStatus(bac) + " - " 
    						+ bac.getLab().getDisplayString() + "<br/>");
    				}
    			}		
    		}
    		   		
    		// now set the color, based on the overall result
    		if(overallResult != null) {
    			colorString = overallResult.getColor();
    		}
    		
    		
    		// now create the actual string to render
    		// TODO: using the ../ is a little sketchy because it relies on directory structure not changing?
    		// TODO: this is operating on the assumption that all the bacs are from the same specimen
    		ret = "<td onmouseover=\"document.body.style.cursor = \'pointer\'\" onmouseout=\"document.body.style.cursor = \'default\'\" " +
    				"onclick=\"window.location = \'../specimen/specimen.form?specimenId=" + bacs.get(0).getSpecimenId() + "&testId=" + bacs.get(0).getId() +
    				"\'\" title=\"" + titleString + "\" style=\"text-align:center;font-style:bold;padding:0px;border:0px;margin:0px;background-color:" + 
    				colorString + ";\">&nbsp;" + resultString + " " + labString + "&nbsp;</td>";
    	}
    
    	try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting tag", ex);
		}
    }
    
    private static BacteriologyResult updateBacteriologyResult(BacteriologyResult overallResult, BacteriologyResult result) {
    	
    	if(overallResult == BacteriologyResult.POSITIVE) {
    		return BacteriologyResult.POSITIVE;
    	}
    	else {
			// set it to positive if that's what this result is
			if(result == BacteriologyResult.POSITIVE) {
				return BacteriologyResult.POSITIVE;
			}
			// a scanty result overrides everything but a positive
			else if (result == BacteriologyResult.SCANTY) {
				return BacteriologyResult.SCANTY;
			}
			// a negative result overrides everything but a positive and a scanty
			else if (result == BacteriologyResult.NEGATIVE && overallResult != BacteriologyResult.SCANTY) {
				return BacteriologyResult.NEGATIVE;
			}
			// a contaminated result only overrides pending
			else if (result == BacteriologyResult.CONTAMINATED && overallResult != BacteriologyResult.SCANTY && overallResult != BacteriologyResult.NEGATIVE) {
				return BacteriologyResult.CONTAMINATED;
			}
			else {
				return result;
			}
		} 
    }
}
