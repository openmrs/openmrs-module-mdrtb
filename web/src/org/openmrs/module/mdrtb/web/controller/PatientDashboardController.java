package org.openmrs.module.mdrtb.web.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.status.LabResultsStatusCalculator;
import org.openmrs.module.mdrtb.status.Status;
import org.openmrs.module.mdrtb.status.StatusFlag;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.module.mdrtb.status.StatusUtil;
import org.openmrs.module.mdrtb.web.controller.status.DashboardLabResultsStatusRenderer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/dashboard/dashboard.form")
public class PatientDashboardController {
	@ModelAttribute("status")
	public Map<String,Status> getStatusMap(@RequestParam(required = true, value = "patientId") String patientId) {
		
		Patient patient = Context.getPatientService().getPatient(Integer.valueOf(patientId));
		
		if (patient == null) {
			// TODO: do something
		}
		
		Map<String,Status> statusMap = new HashMap<String,Status>();
		
		// for now, just operate on the most recent program
		PatientProgram program = StatusUtil.getMostRecentMdrtbProgram(patient);
		
		// lab reports status
		Status labReportsStatus = new LabResultsStatusCalculator(new DashboardLabResultsStatusRenderer()).calculate(program);
		statusMap.put("labResultsStatus", labReportsStatus);
		
		return statusMap;
		
	}
	
	@ModelAttribute("patientId")
	public String getPatientId(@RequestParam(required = true, value = "patientId") String patientId) {
		return patientId;
	}
	
	
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showStatus(@ModelAttribute("status") Map<String,Status> statusMap, ModelMap map) {

		addFlags(statusMap, map);
		
		return new ModelAndView("/module/mdrtb/dashboard/dashboard", map);

	}
	
	
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
	public String updateStatus(@ModelAttribute("status") Map<String, Status> statusMap,  BindingResult errors, 
	                                  SessionStatus sessionStatus, HttpServletRequest request) {
		
		Map<String,String[]> parameterMap = request.getParameterMap();
		
		for (String parameter : parameterMap.keySet()) {
			String [] name = parameter.split("\\.");
			
			if(name.length > 1 && statusMap.get(name[0]) != null) {
				StatusItem item = new StatusItem();
				
				item.setValue(parameterMap.get(parameter)[0]);
				
				BeanWrapper wrapper = new BeanWrapperImpl(statusMap.get(name[0]));
				wrapper.setPropertyValue(name[1], item);
			}
		}
		
		return "redirect:/module/mdrtb/dashboard/dashboard.form";
		
	}	
	
	@SuppressWarnings("unchecked")
    private void addFlags(Map<String,Status> statusMap, ModelMap map) {
		
		// calculate flags
		List<StatusFlag> flags = new LinkedList<StatusFlag>();
		
		for (Status status: statusMap.values()) {
			if (status.getFlags() != null && !status.getFlags().isEmpty()) {
				flags.addAll(status.getFlags());
			}
		}
		
		map.put("flags", flags);
		
	}
}
