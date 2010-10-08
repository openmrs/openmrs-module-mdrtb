package org.openmrs.module.mdrtb.web.controller.specimen;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class AbstractSpecimenController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
		
		// register binders for concepts, locations, and persons
		binder.registerCustomEditor(Concept.class, new ConceptEditor()); 
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
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
			
		// add all the persons to a sorted set sorted by name
		SortedSet<Person> persons = new TreeSet<Person>(new PersonByNameComparator()); 
		
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
		return Context.getService(MdrtbService.class).getMdrtbDrugs();
	}
	
	@ModelAttribute("scanty")
	Concept getConceptScanty() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCANTY);
	}
	
	@ModelAttribute("waitingForTestResult")
	Concept getWaitingForTestResultConcept() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS);
	}
	
	@ModelAttribute("dstTestContaminated")
	Concept getDstTestContaiminated() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONTAMINATED);
	}
	
	@ModelAttribute("otherMycobacteriaNonCoded")
	Concept getOtherMycobacteriaNonCoded() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MYCOBACTERIA_NON_CODED);
	}	
 }


