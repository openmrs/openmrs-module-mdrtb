package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.PatientSummaryUtil;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Outputs a dataset for the GLC report
 */
@Controller
public class GLCListExportController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Location.class, new LocationEditor());
    }
	
    @RequestMapping("/module/mdrtb/reporting/glcReport")
    public void glcReport(HttpServletRequest request, HttpServletResponse response, ModelMap model,
    		@RequestParam("location") Location location) throws Exception {

    	Cohort c = PatientSummaryUtil.getCohort(location);
    	
    	List<String> columnList = new ArrayList<String>();
    	columnList.add(PatientSummaryUtil.FULL_NAME);
    	columnList.add(PatientSummaryUtil.AGE);
    	columnList.add(PatientSummaryUtil.GENDER);
    	columnList.add("hospitalizedDate");
    	columnList.add("ambulatoryDate");
    	columnList.add(PatientSummaryUtil.EMPIRIC_REGIMEN);
    	columnList.add(PatientSummaryUtil.EMPIRIC_REGIMEN_DATE);
    	columnList.add(PatientSummaryUtil.INDIVIDUALIZED_REGIMEN);
    	columnList.add(PatientSummaryUtil.INDIVIDUALIZED_REGIMEN_DATE);
    	columnList.add(PatientSummaryUtil.CURRENT_REGIMEN);
    	columnList.add(PatientSummaryUtil.CURRENT_REGIMEN_DATE);
    	columnList.add(PatientSummaryUtil.RESISTANCE_LIST);
    	columnList.add(PatientSummaryUtil.CLINICAL_PROGRESS);

    	PatientSummaryUtil.outputToExcel(response, c, columnList);
	}
}
