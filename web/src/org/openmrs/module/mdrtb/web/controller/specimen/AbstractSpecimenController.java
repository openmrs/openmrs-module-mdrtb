package org.openmrs.module.mdrtb.web.controller.specimen;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.User;
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
				if(StringUtils.isNotEmpty(type)) {
					setValue(Context.getConceptService().getConcept(Integer.valueOf(type)));
				}
				else {
					setValue(null);
				}
			}
		});
		
		// bind a location id to an actual location
		binder.registerCustomEditor(Location.class, new PropertyEditorSupport() {
			public void setAsText(String location) {
				if(StringUtils.isNotEmpty(location)) {
					setValue(Context.getLocationService().getLocation(Integer.valueOf(location)));
				}
				else {
					setValue(null);
				}
			}	
		});
		
		// bind a person id to an actual person
		binder.registerCustomEditor(Person.class, new PropertyEditorSupport() {
			public void setAsText(String person) {
				if(StringUtils.isNotEmpty(person)) {
					setValue(Context.getPersonService().getPerson(Integer.valueOf(person)));
				}
				else {
					setValue(null);
				}
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
	
	@ModelAttribute("dstMethods")
	Collection<ConceptAnswer> getPossibleDstMethods() {
		return Context.getService(MdrtbService.class).getPossibleDstMethods();
	}
	
	@ModelAttribute("dstResults")
	Collection<Concept> getPossibleDstResults() {
		return Context.getService(MdrtbService.class).getPossibleDstResults();
	}
	
	@ModelAttribute("organismTypes")
	Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return Context.getService(MdrtbService.class).getPossibleOrganismTypes();
	}
	
	@ModelAttribute("providers")
	Collection<Person> getPossibleProviders() {
		// TODO: this should be customizable, so that other installs can define there own provider lists?
		Role provider = Context.getUserService().getRole("Provider");
		Collection<User> providers = Context.getUserService().getUsersByRole(provider);
			
		// add all the persons to a sorted set sorted by family name
		SortedSet<Person> persons = new TreeSet<Person>(new Comparator<Person>() {
			public int compare(Person person1, Person person2) {
			    return person1.getPersonName().getFamilyName().compareTo(person2.getPersonName().getFamilyName());
		    }
		});
		
		for(User user : providers) {
			persons.add(user.getPerson());
		}
		
		return persons;
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	@ModelAttribute("appearances")
	Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return Context.getService(MdrtbService.class).getPossibleSpecimenAppearances();
	}
	
	@ModelAttribute("testTypes")
	Collection<String> getPossibleTestTypes() {
		Collection<String> testTypes = new LinkedList<String>();
		testTypes.add("smear");
		testTypes.add("culture");
		testTypes.add("dst");
		return testTypes;
	}
	
	@ModelAttribute("drugTypes")
	Collection<Concept> getPossibleDrugTypes() {
		return Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
	}
	
	@ModelAttribute("scanty")
	Concept getConceptScanty() {
		return Context.getService(MdrtbService.class).getConceptScanty();
	}
	
	@ModelAttribute("waitingForTestResult")
	Concept getWaitingForTestResultConcept() {
		return Context.getService(MdrtbService.class).getConceptWaitingForTestResults();
	}
	
	@ModelAttribute("dstTestContaminated")
	Concept getDstTestContaiminated() {
		return Context.getService(MdrtbService.class).getConceptDstTestContaminated();
	}
	
	@ModelAttribute("otherMycobacteriaNonCoded")
	Concept getOtherMycobacteriaNonCoded() {
		return Context.getService(MdrtbService.class).getConceptOtherMycobacteriaNonCoded();
	}	
 }


