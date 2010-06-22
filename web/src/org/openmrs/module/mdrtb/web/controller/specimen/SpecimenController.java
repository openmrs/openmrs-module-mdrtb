package org.openmrs.module.mdrtb.web.controller.specimen;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
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
@RequestMapping("/module/mdrtb/specimen/specimen.form")
public class SpecimenController {
	
protected final Log log = LogFactory.getLog(getClass());
	
	// TODO: pull these binders out into another file, following Spring docs?
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
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
		
		// bind a person id to an actual person
		binder.registerCustomEditor(Person.class, new PropertyEditorSupport() {
			public void setAsText(String person) {
				setValue(Context.getPersonService().getPerson(Integer.valueOf(person)));
			}
		});
	}
	
	@ModelAttribute("types")
	Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return Context.getService(MdrtbService.class).getPossibleSpecimenTypes();
	}
	
	@ModelAttribute("providers")
	Collection<Person> getPossibleProviders() {
		// obviously, a hack for now; is all the people who are providers?
		Collection<Person> persons = new HashSet<Person>();
		persons.add(Context.getPersonService().getPerson(501));
		persons.add(Context.getPersonService().getPerson(502));
		
		return persons;
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	
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
	
	@ModelAttribute("specimen")
	public MdrtbSpecimen getSpecimen(@RequestParam(required = false, value="specimenId") Integer specimenId, @RequestParam(required = false, value = "patientId") Integer patientId) {
		
		if(specimenId == null && patientId == null) {
			throw new RuntimeException("Must specify either a specimen Id or patient Id.");
		}
		
		MdrtbSpecimen specimen = null;
		
		if (specimenId != null) {
			specimen = Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
		}
		// create a new specimen object if needed
		if (specimen == null) {
			specimen = Context.getService(MdrtbService.class).createSpecimen(Context.getPatientService().getPatient(patientId));
		}
		
		return specimen;
	}
		
	
	@RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimen(@RequestParam(required = true, value = "specimenId") Integer specimenId, ModelMap map) {
		
		// fetch the specimen
		MdrtbSpecimen specimen = Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
		
		map.addAttribute("specimen", specimen);
		
		return new ModelAndView("/module/mdrtb/specimen/specimen", map);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("specimen") MdrtbSpecimen specimen, @ModelAttribute("smear") MdrtbSmear smear, BindingResult result, SessionStatus status) {
		
		// TODO: add validation
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/mdrtb/specimen/specimen");
		}
		
		// do the actual update
		if(smear != null) {
			Context.getService(MdrtbService.class).saveSmear(smear);
		}
		
		// TODO: add cultures and Dsts
			
		// clears the command object from the session
		status.setComplete();
		
		// TODO: this will become a different redirect
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimen.getSpecimenId());
		
	}
	
}
