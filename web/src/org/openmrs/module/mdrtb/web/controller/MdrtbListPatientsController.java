package org.openmrs.module.mdrtb.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.api.context.Context;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.openmrs.util.OpenmrsUtil;
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
	    			@RequestParam(required=false, value="identifier") 		String identifier,
	    			@RequestParam(required=false, value="name") 			String name,
	    			@RequestParam(required=false, value="locations") 		List<Location> locations,
	    			@RequestParam(required=false, value="locationMethod") 	PatientLocationMethod locationMethod,
	    			@RequestParam(required=false, value="states") 			List<ProgramWorkflowState> states
	            ) {
    	
    	model.addAttribute("identifier", identifier);
    	model.addAttribute("name", name);
    	model.addAttribute("locations", locations);
    	model.addAttribute("locationMethod", locationMethod);
    	model.addAttribute("states", states);
    	
    	List<Patient> patients = new ArrayList<Patient>();
    	Cohort keep = null;
    	
    	if (StringUtils.hasText(name) || StringUtils.hasText(identifier)) {
    		name = "".equals(name) ? null : name;
    		identifier = "".equals(identifier) ? null : identifier;
    		patients = Context.getPatientService().getPatients(name, identifier, null, false);
    	}
    	else {
    		if (locations != null || states != null) {
    			patients = Context.getPatientService().getAllPatients();
    		}
    	}
    	keep = new Cohort(patients);
    	
    	if (locations != null) {
    		
    		if (locationMethod == null) {
    			locationMethod = PatientLocationMethod.PATIENT_HEALTH_CENTER;
    		}
    		Map<Location, Cohort> locationCache = new HashMap<Location, Cohort>();
    		String unknownLocationName = Context.getAdministrationService().getGlobalProperty("mdrtb.unknownLocationName");
    		
    		Cohort locationCohort = new Cohort();
    		for (Location l : locations) {
    			locationCohort = Cohort.union(locationCohort, getLocationCohort(l, locationMethod, locationCache));
    			if (l.getName().equals(unknownLocationName)) {
    				Cohort noLocations = new Cohort(patients);
    				for (Location ul : Context.getLocationService().getAllLocations()) {
    					noLocations = Cohort.subtract(noLocations, getLocationCohort(ul, locationMethod, locationCache));
    				}
    				locationCohort = Cohort.union(locationCohort, noLocations);
    			}
    		}
    		
    		keep = Cohort.intersect(keep, locationCohort);
    	}
    	
    	if (states != null) {
    		Date now = new Date();
    		Cohort inStates = Context.getPatientSetService().getPatientsByProgramAndState(null, states, now, now);
    		keep = Cohort.intersect(keep, inStates);
    	}
    	
    	Set<Integer> patientIds = new HashSet<Integer>();
    	for (Iterator<Patient> i = patients.iterator(); i.hasNext();) {
    		Patient p = i.next();
    		if (!keep.contains(p.getPatientId())) {
    			i.remove();
    		}
    		else {
    			patientIds.add(p.getPatientId());
    		}
    	}
    	
    	model.addAttribute("patientIds", OpenmrsUtil.join(patientIds, ","));
    	model.addAttribute("patients", patients);
    }
    
    /**
     * Utility method to return a list of patients at a given location
     */
    protected Cohort getLocationCohort(Location location, PatientLocationMethod locationMethod, Map<Location, Cohort> cache) {
    	if (cache.containsKey(location)) {
    		return cache.get(location);
    	}
    	Cohort c = Context.getPatientSetService().getPatientsHavingLocation(location, locationMethod);
    	cache.put(location, c);
    	return c;
    }
}
