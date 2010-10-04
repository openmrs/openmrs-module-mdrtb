package org.openmrs.module.mdrtb.web.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.status.LabResultsStatusCalculator;
import org.openmrs.module.mdrtb.status.Status;
import org.openmrs.module.mdrtb.status.StatusFlag;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.module.mdrtb.status.TreatmentStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardLabResultsStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardTreatmentStatusRenderer;
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
public class DashboardController {
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	@ModelAttribute("outcomes")
	Collection<ProgramWorkflowState> getOutcomes() {		
		return Context.getService(MdrtbService.class).getPossibleMdrtbProgramOutcomes();
	}
	
	@ModelAttribute("classificationsAccordingToPreviousDrugUse")
	Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousDrugUse() {		
		return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousDrugUse();
	}
	
	@ModelAttribute("classificationsAccordingToPreviousTreatment")
	Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousTreatment() {		
		return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousTreatment();
	}
	
	
	@ModelAttribute("status")
	public Map<String,Status> getStatusMap(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		
		if (patientProgramId == null) {
			// TODO: do something
		}
		
		Map<String,Status> statusMap = new HashMap<String,Status>();
		
		// get the program that we are operating on
		PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		
		// lab reports status
		Status labReportsStatus = new LabResultsStatusCalculator(new DashboardLabResultsStatusRenderer()).calculate(program);
		statusMap.put("labResultsStatus", labReportsStatus);
		
		// treatment status
		Status treatmentStatus = new TreatmentStatusCalculator(new DashboardTreatmentStatusRenderer()).calculate(program);
		statusMap.put("treatmentStatus", treatmentStatus);
		
		return statusMap;
		
	}
	
	@ModelAttribute("patientId")
	public Integer getPatientId(@RequestParam(required = true, value = "patientId") Integer patientId) {
		return patientId;
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showStatus(@ModelAttribute("status") Map<String,Status> statusMap, 
	                               @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId, ModelMap map) {

    	if (patientProgramId == null) {
			// TODO: do something
		}
		
    	// for now, we are just showing data from the most recent program
    	// TODO: expand this to handle multiple programs
    	PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
    	
    	if (program == null) {
    		// TODO: skip to an enroll-in-program status
    	}
    	else {
    		map.put("program", new MdrtbPatientProgram(program));
    	}
    	
    	// add any flags
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
