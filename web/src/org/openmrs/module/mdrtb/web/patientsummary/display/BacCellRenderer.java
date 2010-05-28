package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.renderer.HtmlCellRendererImpl;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableBacElement;


public class BacCellRenderer extends HtmlCellRendererImpl{

	@Override
    public Object render(Object item, int rowcount) {
        String property = getColumn().getProperty();
        HtmlBuilder html = new HtmlBuilder();
        html.td(2);
        html.width(getColumn().getWidth());
        
        // first, fetch the Bac element we are trying to display
		PatientSummaryTableBacElement bac = (PatientSummaryTableBacElement) ItemUtils.getItemValue(item, property);
		
		// if it's not empty, pull out the Obs that represents the result of the test
		if (bac != null && bac.getObs() != null && bac.getObs().hasGroupMembers()) {
			for (Obs obs : bac.getObs().getGroupMembers()){
				
				if (obs.getConcept().getConceptId() == 3046 || obs.getConcept().getConceptId() == 3052) {
				
					int id = obs.getValueCoded().getConceptId();
					
					if(id == 1408 || id == 1409 || id == 1410 || id == 703){
						html.style("background-color: red"); // we should move these colors to global props, or perhaps a style sheet?
					}
					else if(id == 664){
						html.style("background-color: green"); // we should move these colors to global props, or perhaps a style sheet?
					}
					
					html.title(obs.getValueCoded().getBestName(Context.getLocale()).getName());				
					
				//	return obs.getValueCoded().getBestName(Context.getLocale());
				}
			}
			// TODO: what to return if there is no result code--return "waiting for result"???
		}
		
		html.close();
		html.tdEnd();
		
		return html.toString();
	}	
}
