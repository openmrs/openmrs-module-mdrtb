package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.form.SimpleIntakeForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
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
@RequestMapping("/module/mdrtb/form/intake.form")
public class SimpleIntakeFormController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
		
		// register binders 
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		
	}
	
	@ModelAttribute("intake")
	public SimpleIntakeForm getIntakeForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	                                      @RequestParam(required = true, value = "patientId") Integer patientId,
	                                      @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		// if no form is specified, create a new one
		if (encounterId == -1) {
			SimpleIntakeForm form = new SimpleIntakeForm(Context.getPatientService().getPatient(patientId));
		
			// prepopulate the form with information that has been specified
			if (patientProgramId != null) {
				MdrtbPatientProgram program = new MdrtbPatientProgram(Context.getProgramWorkflowService().getPatientProgram(patientProgramId));
				form.setEncounterDatetime(program.getDateEnrolled());
				form.setLocation(program.getLocation());
			}
				
			return form;
		}
		else {
			return new SimpleIntakeForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}

	@ModelAttribute("patientId")
	public Integer getPatientId(@RequestParam(required = true, value = "patientId") Integer patientId) {
		return patientId;
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("redirect")
	public String getRedirect(@RequestParam(required = true, value = "redirect") String redirect) {
		return redirect;
	}
	
	@ModelAttribute("providers")
	public Collection<Person> getProviders() {
		return Context.getService(MdrtbService.class).getProviders();
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	@ModelAttribute("sites")
	public Collection<ConceptAnswer> getAnatomicalSites() {
		return Context.getService(MdrtbService.class).getPossibleAnatomicalSites();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showIntakeForm() {
		ModelMap map = new ModelMap();
		return new ModelAndView("/module/mdrtb/form/intake", map);	
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processIntakeForm (@ModelAttribute("intake") SimpleIntakeForm intake, BindingResult errors, 
	                                       @RequestParam(required = true, value = "patientId") Integer patientId,
	                                       @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                       SessionStatus status, HttpServletRequest request, ModelMap map) {

		// TODO: validate
		
		// save the actual update
		Context.getEncounterService().saveEncounter(intake.getEncounter());

		// clears the command object from the session
		status.setComplete();
		map.clear();

		// TODO: add a redirect handle here to handle proper redirect?
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + patientId + "&patientProgramId=" + patientProgramId);
	}
}
