package org.openmrs.module.mdrtb.web.controller.program;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/program/programEdit.form")
public class ProgramEditController {

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
		
		// register binders for locations
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@ModelAttribute("program")
	public MdrtbPatientProgram getMdrtbPatientProgram(@RequestParam(required = true, value = "patientProgramId") String patientProgramId) {
		
		PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(Integer.valueOf(patientProgramId));
    	
    	return new MdrtbPatientProgram(program);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult specimenErrors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map) {
		  
		// TODO: validate
		// date should not be in future
		   
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
				
		// clears the command object from the session
		status.setComplete();
		map.clear();
			
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + program.getPatient().getId() + "&patientProgramId=" + program.getId());
			
	}
	
}
