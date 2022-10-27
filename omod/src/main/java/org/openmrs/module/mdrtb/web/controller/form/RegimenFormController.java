package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.web.util.MdrtbWebUtil;
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
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/module/mdrtb/form/regimen.form")
public class RegimenFormController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		
		// register binders 
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		
	}
	
	@ModelAttribute("regimenForm")
	public RegimenForm getRegimenForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId/*,
	                                                                                           @RequestParam(required = false, value = "previousProgramId") Integer previousProgramId*/)
	        throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException,
	        InvocationTargetException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			MdrtbPatientProgram tbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			
			RegimenForm form = new RegimenForm(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			//form.setEncounterDatetime(tbProgram.getDateEnrolled());
			form.setLocation(tbProgram.getLocation());
			form.setPatProgId(patientProgramId);
			
			return form;
		} else {
			return new RegimenForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showRegimenForm(@RequestParam(required = false, value = "returnUrl") String returnUrl,
	        /*@RequestParam(value="loc", required=false) String district,
	        @RequestParam(value="ob", required=false) String oblast,*/
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		/*   if(oblast==null)
		{*/
		RegimenForm regimenForm = null;
		if (encounterId != -1) { //we are editing an existing encounter
			regimenForm = new RegimenForm(Context.getEncounterService().getEncounter(encounterId));
		} else {
			try {
				regimenForm = getRegimenForm(-1, patientProgramId);
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		model.addAttribute("encounterId", encounterId);
		if (mode != null && mode.length() != 0) {
			model.addAttribute("mode", mode);
		}
		
		return new ModelAndView("/module/mdrtb/form/regimen", model);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processRegimenForm(@ModelAttribute("regimenForm") RegimenForm regimenForm, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        /* @RequestParam(required = true, value = "oblast") String oblastId,
	         @RequestParam(required = true, value = "district") String districtId,
	         @RequestParam(required = false, value = "facility") String facilityId,*/
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		System.out.println(regimenForm.getLocation());
		System.out.println(regimenForm.getProvider());
		System.out.println(regimenForm.getEncounterDatetime());
		
		if (regimenForm.getSldRegimenType() != null && regimenForm.getSldRegimenType().getId().intValue() != Context
		        .getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MDRTB_REGIMEN).getId().intValue()) {
			regimenForm.setOtherRegimen(null);
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(regimenForm.getEncounter());
		
		//handle changes in workflows
		
		status.setComplete();
		
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
		}
		
		returnUrl = MdrtbWebUtil.appendParameters(returnUrl,
		    Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId).getPatient().getId(),
		    patientProgramId);
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("tbProgram")
	public MdrtbPatientProgram getMdrtbPatientProgram(
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
	}
	
	@ModelAttribute("returnUrl")
	public String getReturnUrl(@RequestParam(required = false, value = "returnUrl") String returnUrl) {
		return returnUrl;
	}
	
	@ModelAttribute("providers")
	public Collection<Person> getProviders() {
		return Context.getService(MdrtbService.class).getProviders();
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("resistancetypes")
	public ArrayList<ConceptAnswer> getPossibleResistanceTypes() {
		/*return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.RESISTANCE_TYPE);*/
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.RESISTANCE_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PDR_TB));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RR_TB));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.MDR_TB));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PRE_XDR_TB));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.XDR_TB));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.TDR_TB));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NO));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.UNKNOWN));
			for (ConceptAnswer pws : bases) {
				for (Concept classification : classificationConcepts) {
					if (pws.getAnswerConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("cecOptions")
	public Collection<ConceptAnswer> getPossibleCMACPlace() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION);
	}
	
	@ModelAttribute("fundingSources")
	public Collection<ConceptAnswer> getPossibleFundingSources() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.FUNDING_SOURCE);
	}
	
	@ModelAttribute("sldregimens")
	public Collection<ConceptAnswer> getPossibleSLDRegimenrs() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.SLD_REGIMEN_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SHORT_MDR_REGIMEN));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.STANDARD_MDR_REGIMEN));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.INDIVIDUAL_WITH_BEDAQUILINE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.INDIVIDUAL_WITH_DELAMANID));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.INDIVIDUAL_WITH_BEDAQUILINE_AND_DELAMANID));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.INDIVIDUAL_WITH_CLOFAZIMIN_AND_LINEZOLID));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OTHER_MDRTB_REGIMEN));
			for (ConceptAnswer pws : bases) {
				for (Concept classification : classificationConcepts) {
					if (pws.getAnswerConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
}
