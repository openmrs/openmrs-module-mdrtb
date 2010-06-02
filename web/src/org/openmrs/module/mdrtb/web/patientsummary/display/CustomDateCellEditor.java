package org.openmrs.module.mdrtb.web.patientsummary.display;

import java.text.SimpleDateFormat;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.editor.AbstractCellEditor;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableDateElement;

// just need to modify the DateCellEditor to pull the actual date object out of the element and operate on that
public class CustomDateCellEditor extends AbstractCellEditor{

	SimpleDateFormat formatter;
	
	public CustomDateCellEditor(String format) {
		this.formatter = new SimpleDateFormat(format);
	}
	
	public Object getValue(Object item, String property, int rowcount) {
		PatientSummaryTableDateElement dateElement = (PatientSummaryTableDateElement) ItemUtils.getItemValue(item, property);	
		return formatter.format(dateElement.getDate());
    }
	
}
