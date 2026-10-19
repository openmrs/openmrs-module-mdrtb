package org.openmrs.module.mdrtb.web.controller.specimen.reporting;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.status.LabResultsStatusCalculator;
import org.openmrs.module.mdrtb.status.Status;
import org.openmrs.module.mdrtb.web.controller.status.DashboardLabResultsStatusRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LabResultsReportsController {
	
	@RequestMapping("/module/mdrtb/specimen/labResultsReports.form")
	public ModelAndView showReports(ModelMap map) {
		
		List<Status> status = new ArrayList<>();
		
		for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getAllMdrtbPatientPrograms()) {
			if (!program.getPatient().getVoided()) {
				status.add(new LabResultsStatusCalculator(new DashboardLabResultsStatusRenderer()).calculate(program));
			}
		}
		
		map.put("labResultsStatus", status);
		
		return new ModelAndView("/module/mdrtb/specimen/labResultsReports", map);
	}
}
