package org.openmrs.module.mdrtb.web.controller.specimen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.MdrtbCulture;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
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
@RequestMapping("/module/mdrtb/specimen/specimen.form")
public class SpecimenController extends AbstractSpecimenController {
	
protected final Log log = LogFactory.getLog(getClass());
	
	@ModelAttribute("smear")
	public MdrtbSmear getSmear(@RequestParam(required = false, value="smearId") Integer smearId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		MdrtbSmear smear = null;
		
		// only do something here if the smear id has been set
		if (smearId != null) {
			// smearId != -1 is means "this is a new smear"
			if (smearId != -1) {
				smear = Context.getService(MdrtbService.class).getSmear(smearId);
			}
			
			// create the new smear if needed
			if (smear == null) {
				smear = Context.getService(MdrtbService.class).createSmear(Context.getEncounterService().getEncounter(specimenId));
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return smear;
	}
	
	@ModelAttribute("culture")
	public MdrtbCulture getCulture(@RequestParam(required = false, value="cultureId") Integer cultureId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		MdrtbCulture culture = null;
		
		// only do something here if the culture id has been set
		if (cultureId != null) {
			// smearId != -1 is means "this is a new culture"
			if (cultureId != -1) {
				culture = Context.getService(MdrtbService.class).getCulture(cultureId);
			}
			
			// create the new smear if needed
			if (culture == null) {
				culture = Context.getService(MdrtbService.class).createCulture(Context.getEncounterService().getEncounter(specimenId));
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return culture;
	}
	
	@ModelAttribute("specimen")
	public MdrtbSpecimen getSpecimen(@RequestParam(required = true, value="specimenId") Integer specimenId) {
		return Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
	}
		
	
	@RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimen(ModelMap map) {
		return new ModelAndView("/module/mdrtb/specimen/specimen", map);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("specimen") MdrtbSpecimen specimen, @ModelAttribute("smear") MdrtbSmear smear, @ModelAttribute("culture") MdrtbCulture culture, BindingResult result, SessionStatus status) {
		
		// TODO: add validation
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/mdrtb/specimen/specimen");
		}
		
		// do the actual updates--there should only be one update per POST request!
		if(specimen != null) {
			Context.getService(MdrtbService.class).saveSpecimen(specimen);
		}
		else if(smear != null) {
			Context.getService(MdrtbService.class).saveSmear(smear);
		}
		else if(culture != null) {
			Context.getService(MdrtbService.class).saveCulture(culture);
		}
		
		// TODO: add cultures and Dsts
			
		// clears the command object from the session
		status.setComplete();
		
		// TODO: this will become a different redirect
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimen.getId());
		
	}
	
}
