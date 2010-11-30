package org.openmrs.module.mdrtb.web.controller.specimen;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/quickEntry.form")
public class SpecimenQuickEntryController extends AbstractSpecimenController {

	// used to specify if we are adding smears or cultures
	@ModelAttribute("testType")
	public String getTestType(@RequestParam(required = true, value = "testType") String testType) {
		return testType;
	}
	
	// used to specify the number of smear/cultures to add
	@ModelAttribute("numberOfTests")
	public Integer getNumberOfTests(@RequestParam(required = true, value="numberOfTests") Integer numberOfTests) {
		return numberOfTests;
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showQuickEntry(@RequestParam(required = true, value = "patientId") Integer patientId, ModelMap map) {
		map.put("patientId", patientId);
		return new ModelAndView("/module/mdrtb/specimen/specimenQuickEntry", map);
	}
	
		
	// since there are multiple objects (ie specimens) here we can't use spring binding easily
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@RequestParam(required = true, value = "patientId") Integer patientId,
	                                  @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                  @RequestParam(required = true, value = "testType") String testType,
		                              @RequestParam(required = true, value = "numberOfTests") Integer numberOfTests,
		                              @RequestParam(required = true, value = "location") Location location,
		                              @RequestParam(required = true, value = "lab") Location lab,
		                              @RequestParam(required = true, value = "provider") Person provider,
		                              HttpServletRequest request) throws ParseException {
										
		// cycle through all tests
		Integer i = 1;
		while (i < numberOfTests + 1) {
			// we are only adding results where a result has been specified
			if (StringUtils.isNotBlank(request.getParameter("result" + i))) {
				
				String dateCollected = request.getParameter("dateCollected" + i);
				String identifier = request.getParameter("identifier" + i);
				String type = request.getParameter("type" + i);
				String appearance = request.getParameter("appearance" + i);
				String result = request.getParameter("result" + i);
					
				// create the new specimen
				Specimen specimen = Context.getService(MdrtbService.class).createSpecimen(Context.getPatientService().getPatient(patientId));
				
				// add the specimen-specific parameters
				specimen.setLocation(location);
				specimen.setProvider(provider);
				
				SimpleDateFormat dateFormat = Context.getDateFormat();
		    	dateFormat.setLenient(false);
		    	specimen.setDateCollected(dateFormat.parse(dateCollected));
		    	
				
				if (StringUtils.isNotBlank(identifier)) {
					specimen.setIdentifier(identifier);		
				}
				if (StringUtils.isNotBlank(type)) {
					specimen.setType(Context.getConceptService().getConcept(Integer.valueOf(type)));
				}
				if (StringUtils.isNotBlank(appearance)) {
					specimen.setAppearance(Context.getConceptService().getConcept(Integer.valueOf(appearance)));
				}
				
				// now add the appropriate test
				Bacteriology bac;
				
				if (testType.equals("smear")) {
					bac = specimen.addSmear();
				}
				else if (testType.equals("culture")) {
					bac = specimen.addCulture();
				}
				else {
					throw new MdrtbAPIException("Invalid test type passed to quick entry controller.");
				}
				
				// now add the test-specific parameters
				bac.setLab(lab);
				bac.setResult(Context.getConceptService().getConcept(Integer.valueOf(result)));
				
				// save the specimen
				Context.getService(MdrtbService.class).saveSpecimen(specimen);
			}
			
			i++;
		}
		
		// redirect to the overview page
		return new ModelAndView("redirect:specimen.form?patientProgramId=" + patientProgramId);
	}
	
	
	
}
