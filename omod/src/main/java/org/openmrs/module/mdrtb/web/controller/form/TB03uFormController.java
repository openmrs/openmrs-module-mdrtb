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
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
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
@RequestMapping("/module/mdrtb/form/tb03u.form")
public class TB03uFormController {
	
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
	
	@ModelAttribute("tb03u")
	public TB03uForm getTB03uForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId
	/* @RequestParam(required = false, value = "previousProgramId") Integer previousProgramId*/) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			MdrtbPatientProgram tbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			
			TB03uForm form = new TB03uForm(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			form.setEncounterDatetime(tbProgram.getDateEnrolled());
			form.setLocation(tbProgram.getLocation());
			form.setPatProgId(patientProgramId);
			
			if (tbProgram.getClassificationAccordingToPreviousTreatment() != null) {
				form.setRegistrationGroup(tbProgram.getClassificationAccordingToPreviousTreatment().getConcept());
			}
			if (tbProgram.getClassificationAccordingToPreviousDrugUse() != null) {
				form.setRegistrationGroupByDrug(tbProgram.getClassificationAccordingToPreviousDrugUse().getConcept());
			}
			return form;
		} else {
			return new TB03uForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showTB03uForm(@RequestParam(required = false, value = "returnUrl") String returnUrl,
	        @RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			TB03uForm tb03u = null;
			if (encounterId != -1) { //we are editing an existing encounter
				tb03u = new TB03uForm(Context.getEncounterService().getEncounter(encounterId));
			} else {
				try {
					tb03u = getTB03uForm(-1, patientProgramId);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//TB03Form tb03 = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
			Location location = tb03u.getLocation();
			String obName = location.getStateProvince();
			String distName = location.getCountyDistrict();
			String facName = location.getAddress4();
			
			Region ob = null;
			District dist = null;
			
			oblasts = Context.getService(MdrtbService.class).getRegions();
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
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(ob.getId());
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
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(dist.getId());
				if (facilities.size() == 0) { // Maybe it's for Dushanbe
					facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(ob.getId());
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
		} else if (district == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
		} else {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
			facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
			if (facilities.size() == 0) { // Maybe it's for Dushanbe
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(oblast));
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
		return new ModelAndView("/module/mdrtb/form/tb03u", model);
	}
	
	@SuppressWarnings({ "deprecation" })
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processTB03Form(@ModelAttribute("tb03u") TB03uForm tb03u, BindingResult errors,
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
		
		if (tb03u.getLocation() == null || !location.equals(tb03u.getLocation())) {
			tb03u.setLocation(location);
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(tb03u.getEncounter());
		
		//handle changes in workflows
		Concept outcome = tb03u.getTreatmentOutcome();
		Concept group = tb03u.getRegistrationGroup();
		Concept groupByDrug = tb03u.getRegistrationGroupByDrug();
		
		MdrtbPatientProgram tpp = getMdrtbPatientProgram(patientProgramId);
		PatientProgram pp = tpp.getPatientProgram();
		
		if (outcome != null) {
			ProgramWorkflow outcomeFlow = Context.getProgramWorkflowService().getWorkflow(pp.getProgram(), Context
			        .getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME).getName().toString());
			ProgramWorkflowState outcomeState = Context.getProgramWorkflowService().getState(outcomeFlow,
			    outcome.getName().toString());
			tpp.setOutcome(outcomeState);
			try {
				tpp.setDateCompleted(tb03u.getTreatmentOutcomeDate());
			}
			catch (Exception e) {}
		} else {
			tpp.setDateCompleted(null);
		}
		if (group != null) {
			Concept cat4Concept = Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT);
			ProgramWorkflow groupFlow = Context.getProgramWorkflowService().getWorkflow(pp.getProgram(),
			    cat4Concept.getName(Context.getLocale()).getName());
			ProgramWorkflowState groupState = Context.getProgramWorkflowService().getState(groupFlow,
			    group.getName(Context.getLocale()).getName());
			tpp.setClassificationAccordingToPreviousTreatment(groupState);
		}
		
		if (groupByDrug != null) {
			ProgramWorkflow groupByDrugFlow = Context.getProgramWorkflowService()
			        .getWorkflow(tpp.getPatientProgram().getProgram(), Context.getService(MdrtbService.class)
			                .getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE).getName().getName());
			ProgramWorkflowState groupByDrugState = Context.getProgramWorkflowService().getState(groupByDrugFlow,
			    groupByDrug.getName().getName());
			tpp.setClassificationAccordingToPreviousDrugUse(groupByDrugState);
		}
		
		Context.getProgramWorkflowService().savePatientProgram(tpp.getPatientProgram());
		
		//TX OUTCOME
		//PATIENT GROUP
		//PATIENT DEATH AND CAUSE OF DEATH
		if (outcome != null && outcome.getId().intValue() == Integer
		        .parseInt(Context.getAdministrationService().getGlobalProperty("mdrtb.outcome.died.conceptId"))) {
			Patient patient = tpp.getPatient();
			if (!patient.getDead()) {
				patient.setDead(new Boolean(true));
				patient.setCauseOfDeath(tb03u.getCauseOfDeath());
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
	public Collection<ConceptAnswer> getPossiblePatientCategories() {
		//return Context.getService(MdrtbService.class).getPossibleRegimens();
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getPossibleRegimens();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_2_STANDARD));
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_2_SHORT));
			concepts.add(ms.getConcept(MdrtbConcepts.REGIMEN_2_INDIVIDUALIZED));
			for (ConceptAnswer pws : bases) {
				for (Concept classification : concepts) {
					if (pws.getAnswerConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("groups")
	public ArrayList<ProgramWorkflowState> getPossiblePatientGroups() {
		//return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousTreatment();
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPreviousTreatment();
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
				for (Concept classification : concepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("bydrug")
	public ArrayList<ProgramWorkflowState> getPossibleResultsByDrugs() {
		/*return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousDrugUse();*/
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPreviousDrugUse();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.NEW));
			concepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY));
			concepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS));
			for (ProgramWorkflowState pws : states) {
				for (Concept classification : concepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.RESISTANCE_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.PDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.RR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.MDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.PRE_XDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.XDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.TDR_TB));
			concepts.add(ms.getConcept(MdrtbConcepts.NO));
			concepts.add(ms.getConcept(MdrtbConcepts.UNKNOWN));
			for (ConceptAnswer pws : bases) {
				for (Concept classification : concepts) {
					if (pws.getAnswerConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("outcomes")
	public ArrayList<ProgramWorkflowState> getPossibleTreatmentOutcomes() {
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class).getPossibleTbProgramOutcomes();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.CURED));
			concepts.add(ms.getConcept(MdrtbConcepts.TREATMENT_COMPLETED));
			concepts.add(ms.getConcept(MdrtbConcepts.DIED));
			concepts.add(ms.getConcept(MdrtbConcepts.TREATMENT_FAILED));
			concepts.add(ms.getConcept(MdrtbConcepts.LOST_TO_FOLLOWUP));
			concepts.add(ms.getConcept(MdrtbConcepts.STARTED_SLD_TREATMENT));
			concepts.add(ms.getConcept(MdrtbConcepts.CANCELLED));
			for (ProgramWorkflowState pws : states) {
				for (Concept classification : concepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("mdrstatuses")
	public Collection<ConceptAnswer> getPossibleMDRStatuses() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.MDR_STATUS);
	}
	
	@ModelAttribute("txlocations")
	public Collection<ConceptAnswer> getPossibleTxLocations() {
		
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.TREATMENT_LOCATION);
	}
	
	@ModelAttribute("basesfordiagnosis")
	public Collection<ConceptAnswer> getPossibleBasesForDiagnosis() {
		//return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.BASIS_FOR_TB_DIAGNOSIS);
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.METHOD_OF_DETECTION);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.GENEXPERT));
			concepts.add(ms.getConcept(MdrtbConcepts.HAIN_1_DETECTION));
			concepts.add(ms.getConcept(MdrtbConcepts.HAIN_2_DETECTION));
			concepts.add(ms.getConcept(MdrtbConcepts.DST));
			concepts.add(ms.getConcept(MdrtbConcepts.CONTACT_INVESTIGATION));
			concepts.add(ms.getConcept(MdrtbConcepts.TB_CLINICAL_DIAGNOSIS));
			for (ConceptAnswer pws : bases) {
				for (Concept classification : concepts) {
					if (pws.getAnswerConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("hivstatuses")
	public Collection<ConceptAnswer> getPossibleHivStatuses() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.RESULT_OF_HIV_TEST);
	}
	
	@ModelAttribute("relapses")
	public Collection<ConceptAnswer> getPossibleRelapses() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.RELAPSED);
	}
	
	@ModelAttribute("causes")
	public Collection<ConceptAnswer> getPossibleCausesOfDeath() {
		return Context.getService(MdrtbService.class).getPossibleConceptAnswers(MdrtbConcepts.CAUSE_OF_DEATH);
	}
	
}
