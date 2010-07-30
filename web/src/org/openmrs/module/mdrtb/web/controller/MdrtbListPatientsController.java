package org.openmrs.module.mdrtb.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbCohortUtil;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MdrtbListPatientsController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
     	binder.registerCustomEditor(Location.class, new LocationEditor());
     	binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
    }  
    
    @RequestMapping("/module/mdrtb/mdrtbListPatients")
    public void listPatients(ModelMap model,
    				@RequestParam(required=false, value="displayMode") 		String displayMode,
	    			@RequestParam(required=false, value="identifier") 		String identifier,
	    			@RequestParam(required=false, value="name") 			String name,
	    			@RequestParam(required=false, value="enrollment")		String enrollment,
	    			@RequestParam(required=false, value="locations") 		List<Location> locations,
	    			@RequestParam(required=false, value="locationMethod") 	PatientLocationMethod locationMethod,
	    			@RequestParam(required=false, value="states") 			List<ProgramWorkflowState> states
	            ) {
    	
    	model.addAttribute("displayMode", displayMode);
    	model.addAttribute("identifier", identifier);
    	model.addAttribute("name", name);
    	model.addAttribute("enrollment", enrollment);
    	model.addAttribute("locations", locations);
    	model.addAttribute("locationMethod", locationMethod);
    	model.addAttribute("states", states);
    	
    	if (StringUtils.hasText(displayMode)) {
    		Cohort cohort = MdrtbCohortUtil.getMdrPatients(identifier, name, enrollment, locations, locationMethod, states);
        	model.addAttribute("patientIds", cohort.getCommaSeparatedPatientIds());
        	model.addAttribute("patients", Context.getPatientSetService().getPatients(cohort.getMemberIds()));
    	}
    }
}
