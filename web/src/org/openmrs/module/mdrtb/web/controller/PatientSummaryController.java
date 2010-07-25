package org.openmrs.module.mdrtb.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.patient.MdrtbPatientWrapper;
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
@RequestMapping("/module/mdrtb/summary/summary.form")
public class PatientSummaryController {
	
	protected final Log log = LogFactory.getLog(getClass());

	@ModelAttribute("mdrtbPatient")
	public MdrtbPatientWrapper getMdrtbPatient(@RequestParam(required = true, value="patientId") Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		return new MdrtbPatientWrapper(patient);
	} 
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
		
	}
	
	/*
	@ModelAttribute("mdrtbPrograms")
	public List<PatientProgram> getMdrtbPrograms(@RequestParam(required = true, value="patientId") Integer patientId) {
		return Context.getService(MdrtbService.class).getMdrtbPrograms(patientId);
	}
	
	@ModelAttribute("patientChart")
	public PatientChart getPatientChart(@RequestParam(required = true, value="patientId") Integer patientId) {
		return Context.getService(MdrtbService.class).getPatientChart(patientId);
	}
	*/
	
	
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView showPatientSummary(@RequestParam(required = true, value="patientId") Integer patientId, ModelMap map) {
		
		map.put("patientId",patientId);
		
		return new ModelAndView("/module/mdrtb/summary/patientSummary", map);
	}
	
	 @SuppressWarnings("unchecked")
	 @RequestMapping(method = RequestMethod.POST)
	 public ModelAndView processSubmit(@ModelAttribute("mdrtbPatient") MdrtbPatientWrapper mdrtbPatient,
	                                   BindingResult result, SessionStatus status, HttpServletRequest request,
	                                   @RequestParam(required = true, value="patientId") Integer patientId, ModelMap map) {
		 
		 map.put("patientId", patientId);
		 
		return new ModelAndView("/module/mdrtb/summary/patientSummary", map);
	 }
}
