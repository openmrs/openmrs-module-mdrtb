package org.openmrs.module.mdrtb.web.patientsummary.display;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTable;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableFactory;
import org.openmrs.module.mdrtb.web.patientsummary.PatientSummaryTableField;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PatientSummaryTableController {
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/mdrtb/mdrtbPatientSummaryTable")
	 public Map displayTable(HttpServletRequest request, ModelMap map) throws Exception{

		Integer patientId = ServletRequestUtils.getIntParameter(request, "patientId");
		
		// TODO: test case if patientId has not been set--what is the default>?
		// hack, set default to patient 922
		if (patientId == null){
			patientId = 927;
		}
		
		// creating the summary table
		PatientSummaryTableFactory factory = new PatientSummaryTableFactory();
		PatientSummaryTable patientSummaryTable = factory.createPatientSummaryTable(patientId, null, Calendar.getInstance().getTime());
		
		// now start creating the html view
		TableFacade tableFacade = TableFacadeFactory.createTableFacade("patientSummaryTable", request); // custom style defined for this table in mdrtb.css
		
		// we want to display everything on one page
		tableFacade.setMaxRows(patientSummaryTable.getPatientSummaryTableRecords().size());
		
		// pull out the column code to create the columns
		List<String> columns = new LinkedList<String>();
		for(PatientSummaryTableField field : patientSummaryTable.getPatientSummaryTableFields()) {
			columns.add(field.getCode());
		}
		tableFacade.setColumnProperties(columns.toArray(new String [columns.size()]));		
		tableFacade.setItems(patientSummaryTable.getPatientSummaryTableRecords());
	
		// important to set view last for some reason
		tableFacade.setView(new CustomTableHtmlView());
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		
		table.getTableRenderer().setBorder("1px");
		table.getTableRenderer().setCellpadding("2px");
		table.getTableRenderer().setStyleClass("patientSummaryTable");	// defined in mdrtb.css
		
		HtmlRow displayRow = table.getRow();
		displayRow.getRowRenderer().setOddClass("patientSummaryTableOdd");
		displayRow.getRowRenderer().setEvenClass("patientSummaryTableEven");
		
		displayRow.setHighlighter(false);
		displayRow.setFilterable(false);
		displayRow.setSortable(false);
		
		displayRow.getColumn("date").getCellRenderer().setCellEditor(new DateCellEditor("MM/yyyy"));
		
		displayRow.getColumn("smear").setCellRenderer(new BacCellRenderer());
		displayRow.getColumn("culture").setCellRenderer(new BacCellRenderer());
		//displayRow.getColumn("smear").getCellRenderer().setCellEditor(new BacCellEditor());
		//displayRow.getColumn("culture").getCellRenderer().setCellEditor(new BacCellEditor());
		
		// now set each column to the proper title, and DST/regimen columns to use the proper cell editor and standard width
		for(PatientSummaryTableField field : patientSummaryTable.getPatientSummaryTableFields()) {
			displayRow.getColumn(field.getCode()).setTitle(field.getTitle());
			
			if (field.getCode().startsWith("dsts.")) {
				displayRow.getColumn(field.getCode()).setCellRenderer(new DSTCellRenderer());
				//displayRow.getColumn(column.getCode()).getCellRenderer().setCellEditor(new DSTCellEditor());
				displayRow.getColumn(field.getCode()).setWidth("30px");
			}
			
			if (field.getCode().startsWith("regimens.")) {
				displayRow.getColumn(field.getCode()).setCellRenderer(new RegimenCellRenderer());
				//displayRow.getColumn(column.getCode()).getCellRenderer().setCellEditor(new RegimenCellEditor());
				displayRow.getColumn(field.getCode()).setWidth("30px");
			}
		}
		
		map.put("patientSummaryTable", tableFacade.render());
		
		return map; 
	 }
}
