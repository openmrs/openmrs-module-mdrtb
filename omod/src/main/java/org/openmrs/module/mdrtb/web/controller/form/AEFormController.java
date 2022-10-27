package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.form.custom.AEForm;
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
@RequestMapping("/module/mdrtb/form/ae.form")
public class AEFormController {
	
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
	
	@ModelAttribute("aeForm")
	public AEForm getAEForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId/*,
	                                                                                           @RequestParam(required = false, value = "previousProgramId") Integer previousProgramId*/)
	        throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException,
	        InvocationTargetException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			MdrtbPatientProgram tbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			
			AEForm form = new AEForm(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			//form.setEncounterDatetime(tbProgram.getDateEnrolled());
			form.setLocation(tbProgram.getLocation());
			form.setPatProgId(patientProgramId);
			return form;
		} else {
			return new AEForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showAEForm(@RequestParam(required = false, value = "returnUrl") String returnUrl,
	        /*@RequestParam(value="loc", required=false) String district,
	        @RequestParam(value="ob", required=false) String oblast,*/
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		/*List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if(oblast==null)
		*/
		AEForm aeForm = null;
		if (encounterId != -1) { //we are editing an existing encounter
			aeForm = new AEForm(Context.getEncounterService().getEncounter(encounterId));
		} else {
			try {
				aeForm = getAEForm(-1, patientProgramId);
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
		
		return new ModelAndView("/module/mdrtb/form/ae", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processAEForm(@ModelAttribute("aeForm") AEForm aeForm, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        /* @RequestParam(required = true, value = "oblast") String oblastId,
	         @RequestParam(required = true, value = "district") String districtId,
	         @RequestParam(required = false, value = "facility") String facilityId,*/
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		System.out.println(aeForm.getLocation());
		System.out.println(aeForm.getProvider());
		System.out.println(aeForm.getEncounterDatetime());
		
		if (aeForm.getTypeOfEvent() != null && aeForm.getTypeOfEvent().getId().intValue() == Context
		        .getService(MdrtbService.class).getConcept(MdrtbConcepts.SERIOUS).getId().intValue()) {
			aeForm.setTypeOfSpecialEvent(null);
		} else if (aeForm.getTypeOfEvent() != null && aeForm.getTypeOfEvent().getId().intValue() == Context
		        .getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_SPECIAL_INTEREST).getId().intValue()) {
			aeForm.setTypeOfSAE(null);
		} else if (aeForm.getTypeOfEvent() == null) {
			aeForm.setTypeOfSpecialEvent(null);
			aeForm.setTypeOfSAE(null);
		}
		if (aeForm.getCausalityDrug1() == null) {
			aeForm.setCausalityAssessmentResult1(null);
		}
		if (aeForm.getCausalityDrug2() == null) {
			aeForm.setCausalityAssessmentResult2(null);
		}
		if (aeForm.getCausalityDrug3() == null) {
			aeForm.setCausalityAssessmentResult3(null);
		}
		if (aeForm.getActionOutcome() != null && (aeForm.getActionOutcome().getId().intValue() == Context
		        .getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_RESOLVED).getId().intValue()
		        || aeForm.getActionOutcome().getId().intValue() == Context.getService(MdrtbService.class)
		                .getConcept(MdrtbConcepts.RESOLVING).getId().intValue())) {
			aeForm.setOutcomeDate(null);
		}
		// save the actual update
		Context.getEncounterService().saveEncounter(aeForm.getEncounter());
		
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
	
	@ModelAttribute("aeOptions")
	public ArrayList<ConceptAnswer> getPossibleAdverseEvents() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.ADVERSE_EVENT);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.ADVERSE_EVENT_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.SAE_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DIED));
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.SPECIAL_INTEREST_EVENT_TYPE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		Collection<ConceptAnswer> ca = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.CAUSALITY_DRUG_1);
		return ca;
		
	}
	
	@ModelAttribute("carOptions")
	public Collection<ConceptAnswer> getCausalityAssessmentOptions() {
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.CAUSALITY_ASSESSMENT_RESULT_1);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.ADVERSE_EVENT_ACTION);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.ADVERSE_EVENT_OUTCOME);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.MEDDRA_CODE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.DRUG_RECHALLENGE);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
	public ArrayList<String> getRegimens(
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		ArrayList<String> regimens = new ArrayList<String>();
		MdrtbPatientProgram pp = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		ArrayList<RegimenForm> regimenList = Context.getService(MdrtbService.class)
		        .getRegimenFormsForProgram(pp.getPatient(), patientProgramId);
		
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
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class)
		        .getPossibleConceptAnswers(MdrtbConcepts.REQUIRES_ANCILLARY_DRUGS);
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
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
