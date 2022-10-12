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
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
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
@RequestMapping("/module/mdrtb/form/tb03.form")
public class TB03FormController {
	
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
	
	@ModelAttribute("tb03")
	public TB03Form getTB03Form(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
			
			TB03Form form = new TB03Form(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			form.setEncounterDatetime(tbProgram.getDateEnrolled());
			form.setLocation(tbProgram.getLocation());
			form.setPatProgId(patientProgramId);
			
			if (tbProgram.getClassificationAccordingToPatientGroups() != null) {
				form.setRegistrationGroup(tbProgram.getClassificationAccordingToPatientGroups().getConcept());
			}
			if (tbProgram.getClassificationAccordingToPreviousDrugUse() != null) {
				form.setRegistrationGroupByDrug(tbProgram.getClassificationAccordingToPreviousDrugUse().getConcept());
			}
			return form;
		} else {
			return new TB03Form(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showTB03Form(@RequestParam(required = false, value = "returnUrl") String returnUrl,
	        @RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			TB03Form tb03 = null;
			if (encounterId != -1) { //we are editing an existing encounter
				tb03 = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
			} else {
				try {
					tb03 = getTB03Form(-1, patientProgramId);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//TB03Form tb03 = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
			Location location = tb03.getLocation();
			String obName = location.getStateProvince();
			String distName = location.getCountyDistrict();
			String facName = location.getAddress4();
			
			Region ob = null;
			District dist = null;
			
			oblasts = Context.getService(MdrtbService.class).getOblasts();
			model.addAttribute("oblasts", oblasts);
			if (obName != null) {
				for (Region o : oblasts) {
					if (o.getName().equalsIgnoreCase(obName)) {
						ob = o;
						break;
					}
				}
			}
			if (ob != null) {
				model.addAttribute("oblastSelected", ob);
				districts = Context.getService(MdrtbService.class).getDistricts(ob.getId());
				model.addAttribute("districts", districts);
				if (distName != null) {
					for (District d : districts) {
						if (d.getName().equalsIgnoreCase(distName)) {
							dist = d;
							break;
						}
					}
					if (dist != null) {
						model.addAttribute("districtSelected", dist.getId());
					}
				}
			}
			if (dist != null) {
				facilities = Context.getService(MdrtbService.class).getFacilities(dist.getId());
				if (facilities.size() == 0) { // Maybe it's for Dushanbe
					facilities = Context.getService(MdrtbService.class).getFacilities(ob.getId());
				}
				model.addAttribute("facilities", facilities);
				if (facName != null) {
					for (Facility f : facilities) {
						if (f.getName().equalsIgnoreCase(facName)) {
							model.addAttribute("facilitySelected", f.getId());
							break;
						}
					}
				}
			}
		}
		else if (district == null) {
			oblasts = Context.getService(MdrtbService.class).getOblasts();
			districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
		} else {
			oblasts = Context.getService(MdrtbService.class).getOblasts();
			districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
			facilities = Context.getService(MdrtbService.class).getFacilities(Integer.parseInt(district));
			if (facilities.size() == 0) { // Maybe it's for Dushanbe
				facilities = Context.getService(MdrtbService.class).getFacilities(Integer.parseInt(oblast));
			}
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
			model.addAttribute("districtSelected", district);
			model.addAttribute("facilities", facilities);
		}
		model.addAttribute("encounterId", encounterId);
		if (mode != null && mode.length() != 0) {
			model.addAttribute("mode", mode);
		}
		return new ModelAndView("/module/mdrtb/form/tb03", model);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processTB03Form(@ModelAttribute("tb03") TB03Form tb03, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		// perform validation and check for errors
		/*if (tb03 != null) {
			new SimpleFormValidator().validate(tb03, errors);
		}*/
		
		/*if (errors.hasErrors()) {
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/form/intake", map);
		}*/
		
		Location location = null;
		if (StringUtils.isNotBlank(facilityId)) {
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), Integer.parseInt(facilityId));
		} else if (StringUtils.isNotBlank(districtId)) {
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), null);
		}
		if (location == null) {
			throw new MdrtbAPIException("Invalid Hierarchy Set selected");
		}
		
		if (tb03.getLocation() == null || !location.equals(tb03.getLocation())) {
			tb03.setLocation(location);
		}
		
		if (tb03.getCauseOfDeath() != null && tb03.getCauseOfDeath().getId().intValue() != Context
		        .getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_OTHER_DISEASES).getId().intValue()) {
			tb03.setOtherCauseOfDeath(null);
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(tb03.getEncounter());
		
		//handle changes in workflows
		Concept outcome = tb03.getTreatmentOutcome();
		Concept group = tb03.getRegistrationGroup();
		Concept groupByDrug = tb03.getRegistrationGroupByDrug();
		
		TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		Program program = tpp.getPatientProgram().getProgram();
		
		try {
			ProgramWorkflow outcomeFlow = Context.getService(MdrtbService.class).getProgramWorkflow(program, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME).getId());
			ProgramWorkflowState outcomeState = Context.getService(MdrtbService.class).getProgramWorkflowState(outcomeFlow, outcome.getId());
			tpp.setOutcome(outcomeState);
			tpp.setDateCompleted(tb03.getTreatmentOutcomeDate());
		}
		catch (Exception e) {
			tpp.setDateCompleted(null);
		}
		
		try {
			ProgramWorkflow groupFlow = Context.getService(MdrtbService.class).getProgramWorkflow(program, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP).getId());
			ProgramWorkflowState groupState = Context.getService(MdrtbService.class).getProgramWorkflowState(groupFlow, group.getId());
			tpp.setClassificationAccordingToPatientGroups(groupState);
		}
		catch (Exception e) {}
		
		try {
			ProgramWorkflow groupByDrugFlow = Context.getService(MdrtbService.class).getProgramWorkflow(program, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE).getId());
			ProgramWorkflowState groupByDrugState = Context.getService(MdrtbService.class).getProgramWorkflowState(groupByDrugFlow, groupByDrug.getId());
			tpp.setClassificationAccordingToPreviousDrugUse(groupByDrugState);
		}
		catch (Exception e) {}
		
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(tpp.getPatientProgram().getPatientProgramId());
		Context.getProgramWorkflowService().savePatientProgram(pp);
		
		//TX OUTCOME
		//PATIENT GROUP
		//PATIENT DEATH AND CAUSE OF DEATH
		if (outcome != null && (outcome.getId().intValue() == Integer
		        .parseInt(Context.getAdministrationService().getGlobalProperty("mdrtb.outcome.died.conceptId")))) {
			Patient patient = tpp.getPatient();
			if (!patient.getDead()) {
				patient.setDead(new Boolean(true));
				patient.setCauseOfDeath(tb03.getCauseOfDeath());
			}
			Context.getPatientService().savePatient(patient);
		}
		// clears the command object from the session
		status.setComplete();
		
		/*if(programModified) {
			System.out.println("saving program");
			Context.getProgramWorkflowService().savePatientProgram(pp);
		}*/
		
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
		}
		returnUrl = MdrtbWebUtil.appendParameters(returnUrl,
		    Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId).getPatient().getId(),
		    patientProgramId);
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("tbProgram")
	public TbPatientProgram getTbPatientProgram(
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
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
	
	@ModelAttribute("sites")
	public Collection<ConceptAnswer> getAnatomicalSites() {
		return Context.getService(MdrtbService.class).getPossibleAnatomicalSites();
	}
	
	@ModelAttribute("iptxsites")
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites() {
		return Context.getService(MdrtbService.class).getPossibleIPTreatmentSites();
	}
	
	@ModelAttribute("cptxsites")
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites() {
		return Context.getService(MdrtbService.class).getPossibleCPTreatmentSites();
	}
	
	@ModelAttribute("categories")
	public ArrayList<ConceptAnswer> getPossiblePatientCategories() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getPossibleRegimens();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_1_NEW));
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_1_RETREATMENT));
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_2_STANDARD));
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_2_SHORT));
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_2_INDIVIDUALIZED));
			for (ConceptAnswer pws : bases) {
				for (Concept c : concepts) {
					if (pws.getAnswerConcept().getId().equals(c.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("groups")
	public ArrayList<ProgramWorkflowState> getPossiblePatientGroups() {
		//return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPatientGroups();
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPatientGroups();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.NEW));
			concepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1));
			concepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2));
			concepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1));
			concepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2));
			concepts.add(ms.getConcept(MdrtbConcepts.AFTER_FAILURE_REGIMEN_1));
			concepts.add(ms.getConcept(MdrtbConcepts.AFTER_FAILURE_REGIMEN_2));
			concepts.add(ms.getConcept(MdrtbConcepts.OTHER));
			for (ProgramWorkflowState pws : states) {
				for (Concept c : concepts) {
					if (pws.getConcept().getId().equals(c.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("bydrug")
	public ArrayList<ProgramWorkflowState> getPossibleResultsByDrugs() {
		// return Context.getService(MdrtbService.class).getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.NEW));
			concepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS));
			for (ProgramWorkflowState pws : states) {
				for (Concept c : concepts) {
					if (pws.getConcept().getId().equals(c.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("hivstatuses")
	public Collection<ConceptAnswer> getPossibleHIVStatuses() {
		return Context.getService(MdrtbService.class).getPossibleHIVStatuses();
	}
	
	@ModelAttribute("resistancetypes")
	public ArrayList<ConceptAnswer> getPossibleResistanceTypes() {
		//return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.RESISTANCE_TYPE);
		ArrayList<ConceptAnswer> answerArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.RESISTANCE_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.MONO));
			concepts.add(ms.getConcept(MdrtbConcepts.PDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.RR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.MDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.PRE_XDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.XDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.TDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.NO));
			concepts.add(ms.getConcept(MdrtbConcepts.UNKNOWN));
			for (ConceptAnswer pws : bases) {
				for (Concept c : concepts) {
					if (pws.getAnswerConcept().getId().equals(c.getId())) {
						answerArray.add(pws);
					}
				}
			}
		}
		return answerArray;
	}
	
	@ModelAttribute("outcomes")
	public ArrayList<ProgramWorkflowState> getPossibleTreatmentOutcomes() {
		//Context.getService(MdrtbService.class).getPossibleTbProgramOutcomes();
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class).getPossibleTbProgramOutcomes();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.TREATMENT_COMPLETED));
			concepts.add(ms.getConcept(MdrtbConcepts.CURED));
			concepts.add(ms.getConcept(MdrtbConcepts.TREATMENT_FAILED));
			concepts.add(ms.getConcept(MdrtbConcepts.LOST_TO_FOLLOWUP));
			concepts.add(ms.getConcept(MdrtbConcepts.DIED));
			for (ProgramWorkflowState pws : states) {
				for (Concept c : concepts) {
					if (pws.getConcept().getId().equals(c.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("causes")
	public Collection<ConceptAnswer> getPossibleCausesOfDeath() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.CAUSE_OF_DEATH);
	}
}
