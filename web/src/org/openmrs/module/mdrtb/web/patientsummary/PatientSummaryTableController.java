package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.openmrs.module.mdrtb.web.patientsummary.display.BacCellEditor;
import org.openmrs.module.mdrtb.web.patientsummary.display.DSTCellEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PatientSummaryTableController {

	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/mdrtb/mdrtbPatientSummaryTable")
	 public Map displayTable(HttpServletRequest request, ModelMap map){
		
		
		Calendar cal = Calendar.getInstance();
		
		cal.set(2003, 1, 1);
		Date startDate = cal.getTime();
		cal.set(2004, 1, 1);
		Date endDate = cal.getTime();
		
		
		PatientSummaryTable patientSummaryTable = new PatientSummaryTable(932, startDate, endDate);
		
		
		TableFacade tableFacade = TableFacadeFactory.createTableFacade("summaryTable", request);
		
		// pull out the column code to create the columns
		List<String> columns = new LinkedList<String>();
		for(PatientSummaryTableColumn column : patientSummaryTable.getPatientSummaryTableColumns()) {
			columns.add("dst." + column.getCode());
		}
		tableFacade.setColumnProperties(columns.toArray(new String [columns.size()]));		
		
		tableFacade.setItems(patientSummaryTable.getPatientSummaryTableRows());
	
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		HtmlRow row = table.getRow();
		
		row.getColumn("date").getCellRenderer().setCellEditor(new DateCellEditor("MM/yyyy"));
		row.getColumn("smear").getCellRenderer().setCellEditor(new BacCellEditor());
		row.getColumn("culture").getCellRenderer().setCellEditor(new BacCellEditor());
		
		// now set each DST to use the proper cell editor
		for(PatientSummaryTableColumn column : patientSummaryTable.getPatientSummaryTableColumns()) {
			row.getColumn("dst." + column.getCode()).getCellRenderer().setCellEditor(new DSTCellEditor());
		}
		
		map.put("patientSummaryTable", tableFacade.render());
		 
		return map; 
	 }
}
