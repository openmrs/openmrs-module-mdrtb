package org.openmrs.module.mdrtb.web.controller.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
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
	
	private static Log log = LogFactory.getLog(TB03FormController.class);
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		
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
	        IllegalArgumentException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
			
			TB03Form form = new TB03Form(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			form.setEncounterDatetime(tbProgram.getDateEnrolled());
			form.setLocation(tbProgram.getLocation());
			form.setPatientProgramId(patientProgramId);
			
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
					log.warn(e.getMessage());
				}
			}
			
			//TB03Form tb03 = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
			Location location = tb03.getLocation();
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
				model.addAttribute("oblastSelected", ob);
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
				if (facilities.isEmpty()) { // Maybe it's for Dushanbe
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
			if (facilities.isEmpty()) { // Maybe it's for Dushanbe
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(oblast));
			}
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
			model.addAttribute("districtSelected", district);
			model.addAttribute("facilities", facilities);
		}
		model.addAttribute("encounterId", encounterId);
		if (mode != null && !mode.isEmpty()) {
			model.addAttribute("mode", mode);
		}
		return new ModelAndView("/module/mdrtb/form/tb03", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processTB03Form(@ModelAttribute("tb03") TB03Form tb03, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
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
		
		MdrtbFormServiceImpl formService = new MdrtbFormServiceImpl();
		tb03 = formService.processTB03Form(tb03, location);
		// clears the command object from the session
		status.setComplete();
		map.clear();
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
		}
		returnUrl = MdrtbWebUtil.appendParameters(returnUrl, tb03.getPatient().getPatientId(), tb03.getPatientProgramId());
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getPossibleRegimens();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<>();
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
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPatientGroups();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<>();
			concepts.add(ms.getConcept(MdrtbConcepts.NEW));
			concepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1));
			concepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2));
			concepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1));
			concepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2));
			concepts.add(ms.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1));
			concepts.add(ms.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2));
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
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<>();
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
		ArrayList<ConceptAnswer> answerArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE)
		        .getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<>();
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
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class).getPossibleTbProgramOutcomes();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<>();
			concepts.add(ms.getConcept(MdrtbConcepts.TREATMENT_COMPLETE));
			concepts.add(ms.getConcept(MdrtbConcepts.CURED));
			concepts.add(ms.getConcept(MdrtbConcepts.TREATMENT_FAILED));
			concepts.add(ms.getConcept(MdrtbConcepts.LOST_TO_FOLLOWUP));
			concepts.add(ms.getConcept(MdrtbConcepts.DEATH));
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
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH).getAnswers();
	}
}
