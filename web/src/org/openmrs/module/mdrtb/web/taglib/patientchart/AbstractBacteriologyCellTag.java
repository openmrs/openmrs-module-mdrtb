package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;


public abstract class AbstractBacteriologyCellTag extends TagSupport {

    private static final long serialVersionUID = 1L;
    
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
    		// initialize the rankings used by calculate what color to display in the cell
    		Map<Concept,Integer> resultRankings = null;
    		
    		// used to track the "overall" result, which determines how we decide what color to display
    		Concept overallResult = null;
    		
    		// used to keep track of the locations we have already displayed
    		Set<Location> labs = new HashSet<Location> ();
    		
    		// loop through all the bacteriologies in this set and fetch the result for each of them
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
    					// TODO: some error handling here if we get a result we don't expect?
    					if(overallResult == null) {
    						overallResult = bac.getResult();
    					}
    					else {
    						// initialize this here so that we only bother doing it if we have more than one result for a cell
    						if(resultRankings == null) {
    							resultRankings = initializeRankings();
    						}
    						
    						// replace the overall result if this result is ranked higher (i.e., the rank number is lower)
    						if(resultRankings.get(overallResult) > resultRankings.get(bac.getResult())) {
    							overallResult = bac.getResult();
    						}
    						
    					}
    				}
    				// if there isn't a result, add the status here
    				else {
    					titleString = titleString.concat(TestStatusRenderer.renderStandardStatus(bac) + " - " 
    						+ bac.getLab().getDisplayString() + "<br/>");
    				}
    			}		
    		}
    		
    		// set the color based on the overall result
    		if(overallResult != null) {
    			colorString = Context.getService(MdrtbService.class).getColorForConcept(overallResult);
    		}
    		else {
    			// if there is some sort of bac here, set the default color to gray (if there is no actual result)
				colorString = "lightgray";  // TODO: make this configurable?
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
    
    // defines the rankings that determine precedent when decided what color the cell should be
    // TODO: move this into a global property?
    private static Map<Concept,Integer> initializeRankings() {
    
    	Map<Concept,Integer> resultRankings = new HashMap<Concept,Integer> ();
    	
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.STRONGLY_POSITIVE), 1);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MODERATELY_POSITIVE), 2);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSITIVE), 3);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WEAKLY_POSITIVE), 4);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCANTY), 5);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE), 6);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTAMINATED), 7);
    	resultRankings.put(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS), 8);
    
    	return resultRankings;
    }
    
    private static String getScantyResult(Bacteriology bac) {
    	// if this is a scanty result, we need to report the number of bacilli or colonies found with the result
		String scanty = "";
		if(bac.getResult().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCANTY))) {
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
}
