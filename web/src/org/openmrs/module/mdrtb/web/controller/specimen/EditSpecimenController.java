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
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/module/mdrtb/specimen/editSpecimen.form")
public class EditSpecimenController {
		
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
	
	@ModelAttribute("specimen")
	public MdrtbSpecimen getSpecimen(@RequestParam(required = false, value="encounterId") Integer encounterId, @RequestParam(required = false, value = "patientId") Integer patientId) {
		
		if(encounterId == null && patientId == null) {
			throw new RuntimeException("Must specify either a encounter Id or patient Id.");
		}
		
		MdrtbSpecimen specimen = null;
		
		if (encounterId != null) {
			specimen = Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(encounterId));
		}
		// create a new specimen object if needed
		if (specimen == null) {
			specimen = Context.getService(MdrtbService.class).createSpecimen(Context.getPatientService().getPatient(patientId));
		}
		
		return specimen;
	}
		
	@RequestMapping(method = RequestMethod.GET) 
	public String showSpecimen() {
		
		return "/module/mdrtb/specimen/editSpecimen";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("specimen") MdrtbSpecimen specimen, BindingResult result, SessionStatus status) {
		
		// TODO: add validation
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/mdrtb/specimen/editSpecimen");
		}
		
		// do the actual update
		Context.getService(MdrtbService.class).saveSpecimen(specimen);
		
		// clears the command object from the session
		status.setComplete();
		
		// TODO: this will become a different redirect
		return new ModelAndView("redirect:editSpecimen.form?encounterId=" + specimen.getSpecimenId());
		
	}
}
