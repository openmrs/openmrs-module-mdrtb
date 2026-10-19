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
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
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
@RequestMapping("/module/mdrtb/form/ae.form")
public class AdverseEventsFormController {
	
	private final Log log = LogFactory.getLog(getClass());
	
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
	
	@ModelAttribute("aeForm")
	public AdverseEventsForm getAdverseEventsForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId/*,
	                                                                                           @RequestParam(required = false, value = "previousProgramId") Integer previousProgramId*/)
	        throws SecurityException, IllegalArgumentException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			MdrtbPatientProgram tbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			
			AdverseEventsForm form = new AdverseEventsForm(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			form.setLocation(tbProgram.getLocation());
			form.setPatientProgramId(patientProgramId);
			return form;
		} else {
			return new AdverseEventsForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showAEForm(@RequestParam(required = false, value = "returnUrl") String returnUrl,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		AdverseEventsForm aeForm = null;
		if (encounterId != -1) { //we are editing an existing encounter
			aeForm = new AdverseEventsForm(Context.getEncounterService().getEncounter(encounterId));
		} else {
			try {
				aeForm = getAdverseEventsForm(-1, patientProgramId);
				model.addAttribute("aeForm", aeForm);
			}
			catch (Exception e) {
				log.warn(e.getMessage());
			}
		}
		model.addAttribute("encounterId", encounterId);
		if (mode != null && !mode.isEmpty()) {
			model.addAttribute("mode", mode);
		}
		return new ModelAndView("/module/mdrtb/form/ae", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processAEForm(@ModelAttribute("aeForm") AdverseEventsForm aeForm, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		System.out.println(aeForm.getLocation());
		System.out.println(aeForm.getProvider());
		System.out.println(aeForm.getEncounterDatetime());
		MdrtbFormServiceImpl formService = new MdrtbFormServiceImpl();
		aeForm = formService.processAdverseEventsForm(aeForm);
		
		//handle changes in workflows
		status.setComplete();
		map.clear();
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			return new ModelAndView(new RedirectView(request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form"));
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
	
	@ModelAttribute("aeOptions")
	public ArrayList<ConceptAnswer> getPossibleAdverseEvents() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EVENT)
		        .getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NAUSEA));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DIARRHOEA));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ARTHALGIA));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DIZZINESS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HEARING_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HEADACHE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SLEEP_DISTURBANCES));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ELECTROLYTE_DISTURBANCES));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ABDOMINAL_PAIN));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ANOREXIA));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.GASTRITIS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PERIPHERAL_NEUROPATHY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEPRESSION));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.TINNITUS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ALLERGIC_REACTION));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RASH));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.VISUAL_DISTURBANCES));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SEIZURES));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HYPOTHYROIDISM));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PSYCHOSIS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SUICIDAL_IDEATION));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HEPATITIS_AE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RENAL_FAILURE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.QT_PROLONGATION));
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
	
	@ModelAttribute("typeOptions")
	public Collection<ConceptAnswer> getPossibleEventType() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.ADVERSE_EVENT_TYPE).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SERIOUS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OF_SPECIAL_INTEREST));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OTHER));
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
	
	@ModelAttribute("saeOptions")
	public Collection<ConceptAnswer> getPossibleSAEType() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAE_TYPE)
		        .getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEATH));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HOSPITALIZATION_WORKFLOW));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DISABILITY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.CONGENITAL_ANOMALY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.LIFE_THREATENING_EXPERIENCE));
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
	
	@ModelAttribute("specialOptions")
	public Collection<ConceptAnswer> getPossibleSpecialType() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SPECIAL_INTEREST_EVENT_TYPE).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PERIPHERAL_NEUROPATHY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PSYCHIATRIC_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.VISUAL_DISTURBANCES));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HEARING_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.MYELOSUPPRESSION));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.QT_PROLONGATION));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.LACTIC_ACIDOSIS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HEPATITIS_AE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HYPOTHYROIDISM));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HYPOKALEMIA));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PANCREATITIS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PHOSPHOLIPIDOSIS));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RENAL_FAILURE));
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
	
	@ModelAttribute("cdOptions")
	public Collection<ConceptAnswer> getCausalityDrugOptions() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSALITY_DRUG_1).getAnswers();
	}
	
	@ModelAttribute("carOptions")
	public Collection<ConceptAnswer> getCausalityAssessmentOptions() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.CAUSALITY_ASSESSMENT_RESULT_1).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEFINITE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PROBABLE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.POSSIBLE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SUSPECTED));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NOT_CLASSIFIED));
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
	
	@ModelAttribute("actions")
	public Collection<ConceptAnswer> getActionOptions() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.ADVERSE_EVENT_ACTION).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DOSE_NOT_CHANGED));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DOSE_REDUCED));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DRUG_INTERRUPTED));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DRUG_WITHDRAWN));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ANCILLARY_DRUG_GIVEN));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ADDITIONAL_EXAMINATION));
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
	
	@ModelAttribute("outcomes")
	public Collection<ConceptAnswer> getActionOutcomes() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.ADVERSE_EVENT_OUTCOME).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RESOLVED));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RESOLVED_WITH_SEQUELAE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.FATAL));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RESOLVING));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NOT_RESOLVED));
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
	
	@ModelAttribute("meddraCodes")
	public ArrayList<ConceptAnswer> getMeddraCodeOptions() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MEDDRA_CODE)
		        .getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.SKIN_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.MUSCULOSKELETAL_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NEUROLOGICAL_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.VISION_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HEARING_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PSYCHIATRIC_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.GASTROINTESTINAL_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.LIVER_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.METABOLIC_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.ENDOCRINE_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.CARDIAC_DISORDER));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OTHER));
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
	
	@ModelAttribute("drugRechallenges")
	public ArrayList<ConceptAnswer> getRechallengeOptions() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DRUG_RECHALLENGE)
		        .getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NO_RECHALLENGE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RECURRENCE_OF_EVENT));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NO_RECURRENCE));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.UNKNOWN_RESULT));
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
	
	@ModelAttribute("regimens")
	public ArrayList<String> getRegimens(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		ArrayList<String> regimens = new ArrayList<>();
		MdrtbPatientProgram pp = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		List<RegimenForm> regimenList = Context.getService(MdrtbService.class).getRegimenFormsForProgram(pp.getPatient(),
		    patientProgramId);
		
		for (RegimenForm form : regimenList) {
			String s = form.getRegimenSummary();
			if (s != null) {
				regimens.add(s);
			}
		}
		
		return regimens;
	}
	
	@ModelAttribute("yesno")
	public ArrayList<ConceptAnswer> getYesNo() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.REQUIRES_ANCILLARY_DRUGS).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NO));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.YES));
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
