package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.renderer.HtmlCellRendererImpl;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableDSTElement;


public class DSTCellRenderer extends HtmlCellRendererImpl {

	@Override
    public Object render(Object item, int rowcount) {
        String property = getColumn().getProperty();        
        HtmlBuilder html = new HtmlBuilder();
        html.td(2);
        html.width(getColumn().getWidth());
		
        // first, fetch the DST element we are trying to display
		PatientSummaryTableDSTElement dst = (PatientSummaryTableDSTElement) ItemUtils.getItemValue(item, property);
		
		// if it's not empty, pull out the Obs that represents the result of the test
		if (dst != null && dst.getObs() != null && dst.getObs().hasGroupMembers()) {
			
			// loop through the obs in the result group
			for (Obs obs : dst.getObs().getGroupMembers()){			
				int conceptId = obs.getConcept().getConceptId();
							
				// if resistance, set the color to red
				if (conceptId == 1441 || conceptId == 3017) {
					html.style("background-color: red"); // we should move these colors to global props, of course
					html.title("(" + Context.getConceptService().getConcept(conceptId).getBestShortName(Context.getLocale()).getName() + ")");
				}
				// we can assume that a result can't be both resistance and susceptible--that would be a data error
				else if (conceptId == 2474) {
					html.style("background-color: green"); // we should move these colors to global props, of course
					html.title("(" + Context.getConceptService().getConcept(conceptId).getBestShortName(Context.getLocale()).getName() + ")");
				}
			}
		}
		
		html.close();
		html.tdEnd();
		
		return html.toString();
	}	
}

	
