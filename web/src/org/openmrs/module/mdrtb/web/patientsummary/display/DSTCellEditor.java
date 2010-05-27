package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableDSTElement;


public class DSTCellEditor extends AbstractCellEditor {

	public Object getValue(Object item, String property, int rowCount) {
		
		// first, fetch the DST element we are trying to display
		PatientSummaryTableDSTElement dst = (PatientSummaryTableDSTElement) ItemUtils.getItemValue(item, property);
		
		// if it's not empty, pull out the Obs that represents the result of the test
		if (dst != null && dst.getObs() != null) {
			for (Obs obs : dst.getObs().getGroupMembers()){
				
				int conceptId = obs.getConcept().getConceptId();
				
				if (conceptId == 2474 || conceptId == 3017 || conceptId == 1441) {
					
					HtmlBuilder html = new HtmlBuilder();
					html.span();
					if(conceptId == 1441 || conceptId == 3017){
						html.style("color: red"); // we should move these colors to global props, of course
					}
					else if(conceptId == 2474){
						html.style("color: green"); // we should move these colors to global props, of course
					}
					else{
						html.style("color: black"); // should never get here, but just in case
					}
					
					html.close().append(obs.getConcept().getBestShortName(Context.getLocale()));
					html.spanEnd();
					
					return html.toString();
										
					//return obs.getConcept().getBestShortName(Context.getLocale());
					
					// TODO: add handling of concentration
				}
			}
			// TODO: what to return if there is no result code--return "waiting for result"???
		}
			
		return null;
    }

}
