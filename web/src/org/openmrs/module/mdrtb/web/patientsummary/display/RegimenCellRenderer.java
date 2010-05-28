package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.renderer.HtmlCellRendererImpl;
import org.openmrs.DrugOrder;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableRegimenElement;


public class RegimenCellRenderer  extends HtmlCellRendererImpl {

	@Override
    public Object render(Object item, int rowcount) {
        String property = getColumn().getProperty();
        HtmlBuilder html = new HtmlBuilder();
        html.td(2);
        html.width(getColumn().getWidth());
        
        // first, fetch the regimen element we are trying to display
		PatientSummaryTableRegimenElement regimenElement = (PatientSummaryTableRegimenElement) ItemUtils.getItemValue(item, property);
		
		// if there is a drug assigned to this element, it means this cell should get an "x"
		if (regimenElement != null && regimenElement.getRegimenComponent() != null) {
			html.style("background-color: green");  // define this dynamically, in a style sheet?
			
			// add the dosage as a tooltip
			DrugOrder drugOrder = regimenElement.getRegimenComponent().getDrugOrder();
			
			if (drugOrder.getDose() != null) {
				html.title("(" + drugOrder.getDose() + " " + drugOrder.getUnits() + ", " + drugOrder.getFrequency() + ")");
			}
		}
     
        html.close();
        html.tdEnd();
        
        return html.toString();
        
	}

}
