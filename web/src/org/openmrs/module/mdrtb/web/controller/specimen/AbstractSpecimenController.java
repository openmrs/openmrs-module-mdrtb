package org.openmrs.module.mdrtb.web.controller.specimen;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;


public abstract class AbstractSpecimenController {
	
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
	
	@ModelAttribute("smearResults")
	Collection<ConceptAnswer> getPossibleSmearResults() {
		return Context.getService(MdrtbService.class).getPossibleSmearResults();
	}
	
	@ModelAttribute("smearMethods")
	Collection<ConceptAnswer> getPossibleSmearMethods() {
		return Context.getService(MdrtbService.class).getPossibleSmearMethods();
	}
	
	@ModelAttribute("cultureResults")
	Collection<ConceptAnswer> getPossibleCultureResults() {
		return Context.getService(MdrtbService.class).getPossibleCultureResults();
	}
	
	@ModelAttribute("cultureMethods")
	Collection<ConceptAnswer> getPossibleCultureMethods() {
		return Context.getService(MdrtbService.class).getPossibleCultureMethods();
	}
	
	@ModelAttribute("organismTypes")
	Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return Context.getService(MdrtbService.class).getPossibleOrganismTypes();
	}
	
	@ModelAttribute("providers")
	Collection<Person> getPossibleProviders() {
		// TODO: obviously, a hack for now; is all the people who are providers?
		Collection<Person> persons = new HashSet<Person>();
		persons.add(Context.getPersonService().getPerson(501));
		persons.add(Context.getPersonService().getPerson(502));
		
		return persons;
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	@ModelAttribute("testTypes")
	Collection<String> getPossibleTestTypes() {
		Collection<String> testTypes = new LinkedList<String>();
		testTypes.add("smear");
		testTypes.add("culture");
		return testTypes;
	}
}
