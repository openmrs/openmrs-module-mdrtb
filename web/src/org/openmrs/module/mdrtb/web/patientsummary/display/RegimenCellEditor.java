package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.openmrs.DrugOrder;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableRegimenElement;


public class RegimenCellEditor extends AbstractCellEditor {
	
	public Object getValue(Object item, String property, int rowCount) {
		
		// first, fetch the regimen element we are trying to display
		PatientSummaryTableRegimenElement regimenElement = (PatientSummaryTableRegimenElement) ItemUtils.getItemValue(item, property);
		
		// if there is a drug assigned to this element, it means this cell should get an "x"
		if (regimenElement != null && regimenElement.getRegimenComponent() != null) {
			HtmlBuilder html = new HtmlBuilder();
			html.span().style("color: red");
			
			// add the dosage as a tooltip
			DrugOrder drugOrder = regimenElement.getRegimenComponent().getDrugOrder();
			
			if (drugOrder.getDose() != null) {
				html.title("(" + drugOrder.getDose() + " " + drugOrder.getUnits() + ", " + drugOrder.getFrequency() + ")");
			}
			
			html.close().append("X").spanEnd();
			return html.toString();
		}
		else {
			return null;
		}
    }
	
}
