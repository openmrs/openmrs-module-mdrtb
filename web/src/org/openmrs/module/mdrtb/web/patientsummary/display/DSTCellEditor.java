package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.editor.AbstractCellEditor;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableDSTElement;


public class DSTCellEditor extends AbstractCellEditor {

	public Object getValue(Object item, String property, int rowCount) {
		
		// first, fetch the Bac element we are trying to display
		PatientSummaryTableDSTElement dst = (PatientSummaryTableDSTElement) ItemUtils.getItemValue(item, property);
		
		// if it's not empty, pull out the Obs that represents the result of the test
		if (dst != null && dst.getObs() != null) {
			for (Obs obs : dst.getObs().getGroupMembers()){
				if (obs.getConcept().getConceptId() == 2474 || obs.getConcept().getConceptId() == 3017 || obs.getConcept().getConceptId() == 1441) {
					return obs.getConcept().getBestShortName(Context.getLocale());
					
					// TODO: add handling of concentration
				}
			}
			// TODO: what to return if there is no result code--return "waiting for result"???
		}
			
		return null;
    }

}
