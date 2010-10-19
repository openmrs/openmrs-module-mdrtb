package org.openmrs.module.mdrtb.web.pihhaiti;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Custom controller to handle the Haiti-specific MSPP
 */
@Controller
@RequestMapping("/module/mdrtb/pihhaiti/MSPPForm.form")
public class MSPPFormController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		
		// register binders for concepts, locations, and persons
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
	}
	
	@ModelAttribute("person")
	public Person getMdrtbPatient(@RequestParam(required = true, value = "patientId") Integer patientId) {
		return Context.getPersonService().getPerson(patientId);
	}
	
	@ModelAttribute("msppForm")
	public MSPPForm getMSPPForm(@RequestParam(required = true, value = "patientId") Integer patientId,
	                            @RequestParam(required = true, value = "encounterId") Integer encounterId) {
		if (encounterId != -1) {
			return MSPPFormUtil.loadMSPPForm(encounterId);
		} else {
			// create a new form and initialize it
			MSPPForm form = new MSPPForm();
			form.initialize(Context.getPatientService().getPatient(patientId), true);
			return form;
		}
	}
	
	@ModelAttribute("providers")
	Collection<Person> getPossibleProviders() {
		return Context.getService(MdrtbService.class).getProviders();
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	@ModelAttribute("appearances")
	Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return Context.getService(MdrtbService.class).getPossibleSpecimenAppearances();
	}
	
	@ModelAttribute("smearResults")
	Collection<ConceptAnswer> getPossibleSmearResults() {
		return Context.getService(MdrtbService.class).getPossibleSmearResults();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(required = true, value = "encounterId") Integer encounterId, ModelMap map) {
		map.put("encounterId", encounterId);
		return new ModelAndView("/module/mdrtb/pihhaiti/MSPPForm", map);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("msppForm") MSPPForm form, BindingResult errors, SessionStatus status,
	                                  @RequestParam(required = true, value = "encounterId") Integer encounterId,
	                                  @RequestParam(required = true, value = "patientId") Integer patientId,
	                                  HttpServletRequest request, ModelMap map) {
		
		map.put("encounterId", encounterId);
		map.put("patientId", patientId);
		
		// first perform validation
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "provider", "mdrtb.specimen.errors.noProvider");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "mdrtb.specimen.errors.noLocation");
		
		if (errors.hasErrors()) {
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/pihhaiti/MSPPForm", map);
		}
		
		// now save all the specimens, but only if they have results data
		Form dummyMSPPForm = Context.getFormService().getForm(
		    Integer.valueOf(Context.getAdministrationService().getGlobalProperty("pihhaiti.dummyMSPPFormId")));
		
		Date dateCreated = null;
		Date resultDate = null;
		
		// get the result date off the first smear result so that we can copy it to all the rest
		if (form.getSpecimens().get(0) != null && form.getSpecimens().get(0).getSmears() != null
		        && form.getSpecimens().get(0).getSmears().get(0) != null) {
			resultDate = form.getSpecimens().get(0).getSmears().get(0).getResultDate();
		}
		
		for (Specimen specimen : form.getSpecimens()) {
			if (specimen.getSmears().get(0).getResult() != null) {
				// manually set the date created to make assure that all the specimens have the same date created
				// (so we can group them together)
				if (dateCreated != null) {
					((Encounter) specimen.getSpecimen()).setDateCreated(dateCreated);
				} else if (((Encounter) specimen.getSpecimen()).getDateCreated() == null) {
					dateCreated = new Date();
					((Encounter) specimen.getSpecimen()).setDateCreated(dateCreated);
				} else {
					dateCreated = ((Encounter) specimen.getSpecimen()).getDateCreated();
				}
				
				// set the lab of the smear to the location of the encounter
				// TODO: is this correct? is the smear always tested at the encounter location
				specimen.getSmears().get(0).setLab(specimen.getLocation());
				
				// set the result date
				specimen.getSmears().get(0).setResultDate(resultDate);
				
				// set the form of the underlying encounter to the dummy MSPP form so that we can pull out this encounters
				((Encounter) specimen.getSpecimen()).setForm(dummyMSPPForm);
				
				Context.getService(MdrtbService.class).saveSpecimen(specimen);
			}
		}
		
		// clears the command object from the session
		status.setComplete();
		
		return new ModelAndView("redirect:/module/mdrtb/visits/visits.form");
	}
}
