package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartFactory;
import org.openmrs.web.controller.PortletController;


public class PatientChartPortletController extends PortletController {
	
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(Integer.valueOf((String) model.get("patientProgramId")));
		
			PatientChart chart = new PatientChartFactory().createPatientChart(program);
			
			model.put("patientProgramId", program.getId());
			
			model.put("chart", chart);
	}

}
