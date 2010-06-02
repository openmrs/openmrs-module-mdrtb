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
			for (Obs obs : dst.getObs().getGroupMembers()){
						
						int conceptId = obs.getConcept().getConceptId();
						
						if (conceptId == 2474 || conceptId == 3017 || conceptId == 1441) {
							
							if (conceptId == 1441 || conceptId == 3017) {
						html.style("background-color: red"); // we should move these colors to global props, of course
							}
							else if (conceptId == 2474) {
						html.style("background-color: green"); // we should move these colors to global props, of course
							}
					
					// now we will add the concentration, if it exists
					// TODO: refactor this so we aren't doing a double for-loop? could be written better!
					for (Obs obs2 : dst.getObs().getGroupMembers()) {
						if (obs2.getConcept().getConceptId() == 3016) {
							// check for empty concentration??
							html.title("(" + obs2.getValueAsString(Context.getLocale()) + " mcg/ml)"); // TODO: don't hardcode units?
						}
			}		
			}
		}
		}
		
		html.close();
		html.tdEnd();
		
		return html.toString();
	}	
}

	
