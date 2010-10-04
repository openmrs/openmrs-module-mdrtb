package org.openmrs.module.mdrtb.web.controller.program;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
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
public class ProgramEditController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
		
		// register binders for location and program workflow state
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
		
	}
	
	@ModelAttribute("program")
	public MdrtbPatientProgram getMdrtbPatientProgram(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		// if -1 has been specified, create a new patient program for spring to bind the results to
		if (patientProgramId == -1) {
			return new MdrtbPatientProgram();
		}
		// otherwise, load the program
		else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			return new MdrtbPatientProgram(program);
		}
	}
	
	@RequestMapping(value = "/module/mdrtb/program/programEdit.form", method = RequestMethod.POST)
	public ModelAndView processEdit(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors, 
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
	
	@RequestMapping(value = "/module/mdrtb/program/programClose.form", method = RequestMethod.POST)
	public ModelAndView processClose(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map) {
		  
		// TODO: validate
		// date should not be in future
		// must specify a treatment outcome
		   
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
				
		// clears the command object from the session
		status.setComplete();
		map.clear();
			
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + program.getPatient().getId() + "&patientProgramId=" + program.getId());
			
	}
	
	@RequestMapping(value = "/module/mdrtb/program/programEnroll.form", method = RequestMethod.POST)
	public ModelAndView processEnroll(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors, 
	                                  @RequestParam(required = true, value = "patientId") Integer patientId,
	                                  SessionStatus status, HttpServletRequest request, ModelMap map) {
		  
		// TODO: validate
		// date should not be in future
		   
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if (patient == null) {
			throw new RuntimeException ("Process enroll called with invalid patient id " + patientId);
		}
		
		// set the patient
		program.setPatient(patient);
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
				
		// clears the command object from the session
		status.setComplete();
		map.clear();
			
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + program.getPatient().getId() + "&patientProgramId=" + program.getId());
			
	}
	
}
