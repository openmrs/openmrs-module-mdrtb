package org.openmrs.module.mdrtb.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.status.VisitStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardVisitStatusRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/visits/visits.form")
public class VisitsController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView showVisits(@RequestParam(required = true, value="patientId") Integer patientId, 
	                               @RequestParam(required = true, value="patientProgramId") Integer patientProgramId, ModelMap map) {
		
		MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		
		map.put("visits", new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculate(program));
		
		map.put("patientId",patientId);
		map.put("patientProgramId", patientProgramId);
	
		return new ModelAndView("/module/mdrtb/visits/visits", map);
	}
}
