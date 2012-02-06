package org.openmrs.module.mdrtb.web.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
     	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true));
    }  
    
    @RequestMapping("/module/mdrtb/mdrtbListPatients")
    public void listPatients(ModelMap model,
    				@RequestParam(required=false, value="displayMode") 			String displayMode,
	    			@RequestParam(required=false, value="identifier") 			String identifier,
	    			@RequestParam(required=false, value="name") 				String name,
	    			@RequestParam(required=false, value="enrolledOnOrAfter") 	Date enrolledOnOrAfter,
	    			@RequestParam(required=false, value="enrolledOnOrBefore") 	Date enrolledOnOrBefore,
	    			@RequestParam(required=false, value="enrollment")			String enrollment,
	    			@RequestParam(required=false, value="location") 			Location location,
	    			@RequestParam(required=false, value="states") 				List<ProgramWorkflowState> states
	            ) {
    	
    	model.addAttribute("displayMode", displayMode);
    	model.addAttribute("identifier", identifier);
    	model.addAttribute("name", name);
    	model.addAttribute("enrolledOnOrAfter", enrolledOnOrAfter);
    	model.addAttribute("enrolledOnOrBefore", enrolledOnOrBefore);
    	model.addAttribute("enrollment", enrollment);
    	model.addAttribute("location", location);;
    	model.addAttribute("states", states);
    	
    	if (StringUtils.hasText(displayMode)) {
    		Cohort cohort = MdrtbUtil.getMdrPatients(identifier, name, enrolledOnOrAfter, enrolledOnOrBefore, enrollment, location, states);
        	model.addAttribute("patientIds", cohort.getCommaSeparatedPatientIds());
        	model.addAttribute("patients", Context.getPatientSetService().getPatients(cohort.getMemberIds()));
    	}
    	
    	MdrtbService svc = Context.getService(MdrtbService.class);
    	
    	Program mdrtbProgram = svc.getMdrtbProgram();
    	model.addAttribute("mdrProgram", mdrtbProgram);
    	
    	List<Location> patientLocations = svc.getLocationsWithAnyProgramEnrollments();
    	model.addAttribute("patientLocations", patientLocations);
    }
}
