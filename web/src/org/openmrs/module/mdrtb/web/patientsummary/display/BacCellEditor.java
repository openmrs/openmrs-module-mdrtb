package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.util.ItemUtils;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableBacElement;


public class BacCellEditor extends AbstractCellEditor {

	public Object getValue(Object item, String property, int rowCount) {
		
		// first, fetch the Bac element we are trying to display
		PatientSummaryTableBacElement bac = (PatientSummaryTableBacElement) ItemUtils.getItemValue(item, property);
		
		// if it's not empty, pull out the Obs that represents the result of the test
		if (bac != null && bac.getObs() != null) {
			for (Obs obs : bac.getObs().getGroupMembers()){
				if (obs.getConcept().getConceptId() == 3046 || obs.getConcept().getConceptId() == 3052) {
					return obs.getValueCoded().getBestName(Context.getLocale());
				}
			}
			// TODO: what to return if there is no result code--return "waiting for result"???
		}
			
		return null;
    }

}
