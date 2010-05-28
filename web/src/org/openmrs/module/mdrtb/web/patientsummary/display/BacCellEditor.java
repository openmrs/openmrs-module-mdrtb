package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.util.ItemUtils;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableBacElement;


public class BacCellEditor extends AbstractCellEditor {

	public Object getValue(Object item, String property, int rowCount) {
		
		// first, fetch the Bac element we are trying to display
		PatientSummaryTableBacElement bac = (PatientSummaryTableBacElement) ItemUtils.getItemValue(item, property);
		
		// if it's not empty, pull out the Obs that represents the result of the test
		if (bac != null && bac.getObs() != null && bac.getObs().hasGroupMembers()) {
			for (Obs obs : bac.getObs().getGroupMembers()){
				
				if (obs.getConcept().getConceptId() == 3046 || obs.getConcept().getConceptId() == 3052) {
				
					int id = obs.getValueCoded().getConceptId();
					
					HtmlBuilder html = new HtmlBuilder();
					html.span();
					if(id == 1408 || id == 1409 || id == 1410 || id == 703){
						html.style("color: red"); // we should move these colors to global props, of course
					}
					else if(id == 664){
						html.style("color: green"); // we should move these colors to global props, of course
					}
					else {
						html.style("color: black");
					}
					html.close().append("[" + obs.getValueCoded().getBestShortName(Context.getLocale()) + "]");				
					html.spanEnd();
					
					return html.toString();
					
				//	return obs.getValueCoded().getBestName(Context.getLocale());
				}
			}
			// TODO: what to return if there is no result code--return "waiting for result"???
		}
			
		return null;
    }

}
