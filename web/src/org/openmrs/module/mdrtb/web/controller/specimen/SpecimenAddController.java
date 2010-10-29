package org.openmrs.module.mdrtb.web.controller.specimen;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenValidator;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.specimen.TestValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/add.form")
public class SpecimenAddController extends AbstractSpecimenController {

	// used to specify if we are adding a smear or culture when adding this specimen
	@ModelAttribute("testType")
	public String getTestType(@RequestParam(required = false, value = "testType") String testType) {
		return testType;
	}
	
	@ModelAttribute("specimen")
	public Specimen getSpecimen(@RequestParam(required = true, value = "patientId") Integer patientId) {
		Specimen specimen = Context.getService(MdrtbService.class).createSpecimen(Context.getPatientService().getPatient(patientId));
		
		// set the default type to "sputum"
		specimen.setType(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPUTUM));
		
		return specimen;
	}
		
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimenAdd(@RequestParam(required = true, value = "patientId") Integer patientId, ModelMap map) {
		map.put("patientId", patientId);
		return new ModelAndView("/module/mdrtb/specimen/specimenAdd", map);
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("specimen") Specimen specimen, BindingResult result, 
	                                  SessionStatus status, ModelMap map,
	                                  @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                  @RequestParam(required = false, value = "testType") String testType,
	                                  @RequestParam(required = false, value = "lab") Location lab,
	                                  @RequestParam(required = false, value = "resultDate") Date resultDate,
	                                  @RequestParam(required = false, value = "result") Concept resultConcept) {
			
		// add the smear/culture if necessary
		Bacteriology test = null;
		BindingResult testResult = null;
	
		if (testType != null) {
			
			if (testType.contentEquals("smear")) {
				test = specimen.addSmear();
			}
			else if (testType.contentEquals("culture")) {
				test = specimen.addCulture();
			}
			else {
				throw new MdrtbAPIException("Invalid test type " + testType + " passed to specimen add controller.");
			}
			
			test.setResult(resultConcept);
			test.setLab(lab);
			test.setResultDate(resultDate);
			
			// validate the smear/culture info
			testResult = new BeanPropertyBindingResult(test, "test");
			new TestValidator().validate(test, testResult);
		}
		
		// validate
		new SpecimenValidator().validate(specimen, result);
		
    	if (result.hasErrors() || (testResult != null && testResult.hasErrors())) {
			map.put("patientProgramId", patientProgramId);
			map.put("errors", result);
			map.put("testErrors",testResult);
			
			// we need to add the request parameters back into the map
			map.put("testType", testType);
			map.put("resultConcept", resultConcept);
			map.put("resultDate", resultDate);
			map.put("lab", lab);
			
			return new ModelAndView("/module/mdrtb/specimen/specimenAdd", map);
		}
		
		// save the specimen
		Context.getService(MdrtbService.class).saveSpecimen(specimen);
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// redirect to the new detail patient for this specimen
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimen.getId() + "&patientProgramId=" + patientProgramId, map);
		
	}
}
