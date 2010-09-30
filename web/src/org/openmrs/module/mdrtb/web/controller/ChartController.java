package org.openmrs.module.mdrtb.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.status.StatusUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/chart/chart.form")
public class ChartController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView showPatientSummary(@RequestParam(required = true, value="patientId") Integer patientId, ModelMap map) {
		
		// right now we are just displaying the chart for the most recent program
		Patient patient = Context.getPatientService().getPatient(patientId);
		PatientProgram program = StatusUtil.getMostRecentMdrtbProgram(patient);
		
		map.put("patientId",patientId);
		
		map.put("patientProgramId", program.getId());
	
		return new ModelAndView("/module/mdrtb/chart/chart", map);
	}
}
