package org.openmrs.module.mdrtb.web.controller.specimen;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/editSmear.form")
public class EditSmearController  {

    protected final Log log = LogFactory.getLog(getClass());
	
    // TODO: add validator
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	
    	// bind dates
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
    
    	// bind a concept id to an actual concept
		binder.registerCustomEditor(Concept.class, new PropertyEditorSupport() {
			public void setAsText(String type) {
				setValue(Context.getConceptService().getConcept(Integer.valueOf(type)));
			}
		});
		
		// bind a location id to an actual location
		binder.registerCustomEditor(Location.class, new PropertyEditorSupport() {
			public void setAsText(String location) {
				setValue(Context.getLocationService().getLocation(Integer.valueOf(location)));
			}
		});
    }
    
    @ModelAttribute("results")
    Collection<ConceptAnswer> getPossibleSmearResults() {
    	return Context.getService(MdrtbService.class).getPossibleSmearResults();
    }
    
    @ModelAttribute("methods")
    Collection<ConceptAnswer> getPossibleSmearMethods() {
    	return Context.getService(MdrtbService.class).getPossibleSmearMethods();
    }
    
    @ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
    
    @ModelAttribute("smear")
    public MdrtbSmear getSmear(@RequestParam(required = false, value="obsId") Integer obsId, @RequestParam(required = false, value="encounterId") Integer encounterId) {
    	if (obsId == null && encounterId == null) {
    		throw new RuntimeException("Must specify either an obs Id or encounter Id");
    	}
    	
    	MdrtbSmear smear = null;
    	
    	// if we have an obs, fetch the smear
    	if (obsId != null) {
    		smear = Context.getService(MdrtbService.class).getSmear(Context.getObsService().getObs(obsId));
    	}
    	
    	
    	// create a new smear object if needed
    	if (smear == null) {
    		smear = Context.getService(MdrtbService.class).createSmear(Context.getEncounterService().getEncounter(encounterId));
    	}
    	
    	return smear;
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public String showSmear() {
		
		return "/module/mdrtb/specimen/editSmear";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("smear") MdrtbSmear smear, BindingResult result, SessionStatus status) {
		
		// TODO: add validation
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/mdrtb/specimen/editSmear");
		}
		
		// do the actual update
		Context.getService(MdrtbService.class).saveSmear(smear);
		
		// clears the command object from the session
		status.setComplete();
		
		// TODO: this will become a different redirect
		return new ModelAndView("redirect:specimen.form?encounterId=" + smear.getSpecimenId());
		
	}
	
}
