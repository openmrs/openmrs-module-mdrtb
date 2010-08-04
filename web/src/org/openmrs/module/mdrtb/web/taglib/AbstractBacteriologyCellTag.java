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
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;


public abstract class AbstractBacteriologyCellTag extends TagSupport {

    private static final long serialVersionUID = 6161336882128086538L;
    
	private final Log log = LogFactory.getLog(getClass());
	
	// TODO: extract this and make it configurable once we figure out how we want it done
	private static final String positiveColor="lightcoral";
	private static final String negativeColor="lightgreen";
	private static final String scantyColor="khaki";
	private static final String contaminatedColor="khaki";
	private static final String pendingColor="lightgray";
	
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
    		colorString = pendingColor;
    		
    		// loop through all the bacteriologies in this set and fetch the result for each of them
    		Set<Location> labs = new HashSet<Location> ();
    	
    		for(Bacteriology bac : bacs) {
    			if(bac != null) {		
    				
    				// update the lab string based on this bac
    				// only add this lab to the display list if we've yet to display it
					if(!labs.contains(bac.getLab())) {
						
						// add a slash if we have multiple labs
						if(StringUtils.isNotEmpty(labString)) {
							labString = labString.concat("/");
						}
						
						// add the lab name to the display string
						labString = labString.concat(Context.getService(MdrtbService.class).getDisplayCodeForLocation(bac.getLab()));
						
						// now mark that we've already handled this lab
						labs.add(bac.getLab());
					}
    			
    				// if there's a valid result....
    				if(bac.getResult() != null) {
    					
    					// fetch the scanty bacilli/colony number, if relevant
    					String scanty = getScantyResult(bac);
    					
    					// add a slash if we have multiple results
						if(StringUtils.isNotEmpty(resultString)) {
							resultString = resultString.concat("/");
						}
    					
    					// append the appropriate result to the result list
    					resultString = resultString.concat(bac.getResult().getBestShortName(Context.getLocale()).toString() + scanty);
    						
    					// append the appropriate title to the title list
    					titleString = titleString.concat(bac.getResult().getBestName(Context.getLocale()).toString() + " - " 
    						+ bac.getLab().getDisplayString() + "<br/>");
    					
    					// now figure the overall result for the purpose of determining the color of the cell
    					colorString = updateColorToDisplay(colorString, bac.getResult());
    				}
    				// if there isn't a result, add the status here
    				else {
    					titleString = titleString.concat(TestStatusRenderer.renderStandardStatus(bac) + " - " 
    						+ bac.getLab().getDisplayString() + "<br/>");
    				}
    			}		
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
    
    private static String getScantyResult(Bacteriology bac) {
    	// if this is a scanty result, we need to report the number of bacilli or colonies found with the result
		String scanty = "";
		if(bac.getResult().equals(Context.getService(MdrtbService.class).getConceptScanty())) {
			if("smear".equals(bac.getTestType())) {
				Smear smear = (Smear) bac;
				if(smear.getBacilli() != null) {
					scanty = smear.getBacilli().toString();
				}
			}
			else if ("culture".equals(bac.getTestType())) {
				Culture culture = (Culture) bac;
				if(culture.getColonies() != null) {
					scanty = culture.getColonies().toString();
				}
			}
		}
		return scanty;
    }
    
    private static String updateColorToDisplay(String color, Concept result) {
    	
    	ConceptService conceptService = Context.getConceptService();
    	
    	if(positiveColor.equals(color)) {
    		return positiveColor;
    	}
    	else {
    		// TODO: update this to use the new concept mapping service
			// set it to positive if that's what this result is
			if(result.equals(conceptService.getConceptByMapping("positive","org.openmrs.module.mdrtb"))
					|| result.equals(conceptService.getConceptByMapping("weaklyPositive","org.openmrs.module.mdrtb"))
					|| result.equals(conceptService.getConceptByMapping("moderatelyPositive","org.openmrs.module.mdrtb"))
					|| result.equals(conceptService.getConceptByMapping("stronglyPositive","org.openmrs.module.mdrtb"))) {
				return positiveColor;
			}
			// a scanty result overrides everything but a positive
			else if (result.equals(conceptService.getConceptByMapping("scanty","org.openmrs.module.mdrtb"))) {
				return scantyColor;
			}
			// a negative result overrides everything but a positive and a scanty
			else if (!scantyColor.equals(result) && result.equals(conceptService.getConceptByMapping("negative","org.openmrs.module.mdrtb"))) {
				return negativeColor;
			}
			// a contaminated result only overrides pending
			else if (!scantyColor.equals(result) && !negativeColor.equals(result) && result.equals(conceptService.getConceptByMapping("contaminated","org.openmrs.module.mdrtb"))) {
				return contaminatedColor;
			}
			else {
				return pendingColor;
			}
		} 
    }
}
