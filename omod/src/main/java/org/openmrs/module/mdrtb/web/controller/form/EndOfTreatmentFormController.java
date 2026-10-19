package org.openmrs.module.mdrtb.web.controller.form;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Legacy (JSP) controller for the End of Treatment form, same shape as TransferOutController. Works for both DOTS (TB03) and MDR-TB (TB03u) programs.
 */
@Controller
@RequestMapping("/module/mdrtb/form/endOfTreatment.form")
@SessionAttributes("endOfTreatment")
public class EndOfTreatmentFormController {
	
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
	
	@ModelAttribute("endOfTreatment")
	public EndOfTreatmentForm getEndOfTreatmentForm(
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException {
		// if no form is specified, create a new one
		if (encounterId == -1) {
			EndOfTreatmentForm form;
			if (!isMdrtbProgram(patientProgramId)) {
				TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
				form = new EndOfTreatmentForm(tbProgram.getPatient());
				form.setLocation(tbProgram.getLocation());
			} else {
				MdrtbPatientProgram mdrtbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(
				    patientProgramId);
				form = new EndOfTreatmentForm(mdrtbProgram.getPatient());
				form.setLocation(mdrtbProgram.getLocation());
			}
			form.setPatientProgramId(patientProgramId);
			return form;
		} else {
			return new EndOfTreatmentForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showEndOfTreatmentForm(
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		model.addAttribute("encounterId", encounterId);
		if (mode != null && !mode.isEmpty()) {
			model.addAttribute("mode", mode);
		}
		return new ModelAndView("/module/mdrtb/form/endOfTreatment", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processEndOfTreatmentForm(@ModelAttribute("endOfTreatment") EndOfTreatmentForm eot,
	        BindingResult errors, @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		// make sure the form is attached to the program it was opened from
		eot.setPatientProgramId(patientProgramId);
		
		MdrtbFormServiceImpl formService = new MdrtbFormServiceImpl();
		eot = formService.processEndOfTreatmentForm(eot);
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			if (!isMdrtbProgram(patientProgramId)) {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl, Context.getService(MdrtbService.class)
				        .getTbPatientProgram(patientProgramId).getPatient().getId(), patientProgramId);
			} else {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl, Context.getService(MdrtbService.class)
				        .getMdrtbPatientProgram(patientProgramId).getPatient().getId(), patientProgramId);
			}
		}
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	/** True when the patient program is the MDR-TB program (TB03u), false for DOTS (TB03) */
	private boolean isMdrtbProgram(Integer patientProgramId) {
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		return pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue();
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("returnUrl")
	public String getReturnUrl(@RequestParam(required = false, value = "returnUrl") String returnUrl) {
		return returnUrl;
	}
	
	@ModelAttribute("providers")
	public Collection<Person> getProviders() {
		return Context.getService(MdrtbService.class).getProviders();
	}
}
