package org.openmrs.module.mdrtb.web.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.status.HivStatusCalculator;
import org.openmrs.module.mdrtb.status.LabResultsStatusCalculator;
import org.openmrs.module.mdrtb.status.Status;
import org.openmrs.module.mdrtb.status.StatusFlag;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.module.mdrtb.status.TreatmentStatusCalculator;
import org.openmrs.module.mdrtb.status.VisitStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardHivStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardLabResultsStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardTreatmentStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardVisitStatusRenderer;
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
public class MdrtbDashboardController {
	
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
	
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showStatus(@RequestParam(required = false, value = "patientId") Integer patientId,
	                               @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId, ModelMap map) {

    	MdrtbPatientProgram program = null;
    	
    	// if there is no patient program selected, we want to show the most recent program
    	if (patientProgramId == null) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			
			if (patient == null) {
				throw new MdrtbAPIException("Invalid patient id passed to dashboard controller");
			}
			else {
				program = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(patient);
			}
			
    	}
    	// fetch the program that is being requested
    	else {
    		program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
    	}
    		
    	if (program == null) {
    		// if the patient has no program, redirect to the enroll-in-program
    		map.clear();
    		return new ModelAndView("redirect:/module/mdrtb/program/showEnroll.form?patientId=" + patientId); 
    	}
    	else {
    		map.put("program", program);
    	}

    	// add the patient program ID
    	map.put("patientProgramId", program.getId());
    	
    	// add the patientId
    	map.put("patientId", program.getPatient().getId());
    	
    	// now add the status items
		Map<String,Status> statusMap = new HashMap<String,Status>();
	
		// lab reports status
		Status labReportsStatus = new LabResultsStatusCalculator(new DashboardLabResultsStatusRenderer()).calculate(program);
		statusMap.put("labResultsStatus", labReportsStatus);
		
		// treatment status
		Status treatmentStatus = new TreatmentStatusCalculator(new DashboardTreatmentStatusRenderer()).calculate(program);
		statusMap.put("treatmentStatus", treatmentStatus);
		
		// visits status
		Status visitStatus = new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculate(program);
		statusMap.put("visitStatus", visitStatus);
		
		// hiv status
		Status hivStatus = new HivStatusCalculator(new DashboardHivStatusRenderer()).calculate(program.getPatient());
		statusMap.put("hivStatus", hivStatus);
		
		map.put("status", statusMap);
    	
    	// add any flags
		addFlags(statusMap, map);
		
		return new ModelAndView("/module/mdrtb/dashboard/dashboard", map);

	}
    
    // TODO: this most likely will not be needed... it is not currently used
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
