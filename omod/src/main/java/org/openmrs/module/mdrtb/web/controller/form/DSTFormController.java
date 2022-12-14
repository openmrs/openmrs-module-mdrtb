package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.DstResult;
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
@RequestMapping("/module/mdrtb/form/dst.form")
public class DSTFormController {
	
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
	
	@ModelAttribute("dst")
	public DSTForm getDSTForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
		if (pp.getProgram().getConcept().getId()
		        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId())) {
			mdr = true;
		}
		System.out.println(mdr ? "MDR" : "DOTS");
		// if no form is specified, create a new one
		if (encounterId == -1) {
			DSTForm form = null;
			if (!mdr) {
				TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
				
				form = new DSTForm(tbProgram.getPatient());
				
				// prepopulate the intake form with any program information
				//form.setEncounterDatetime(tbProgram.getDateEnrolled());
				form.setLocation(tbProgram.getLocation());
			}
			
			else {
				MdrtbPatientProgram mdrtbProgram = Context.getService(MdrtbService.class)
				        .getMdrtbPatientProgram(patientProgramId);
				
				form = new DSTForm(mdrtbProgram.getPatient());
				
				// prepopulate the intake form with any program information
				//form.setEncounterDatetime(mdrtbProgram.getDateEnrolled());
				form.setLocation(mdrtbProgram.getLocation());
			}
			return form;
		} else {
			return new DSTForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showDSTForm() {
		ModelMap map = new ModelMap();
		
		return new ModelAndView("/module/mdrtb/form/dst", map);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("dst") DSTForm dst, BindingResult errors,
	        @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "removeDstResult") String[] removeDstResults,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
		if (pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue()) {
			mdr = true;
		}
		
		else {
			mdr = false;
		}
		
		Context.getEncounterService().saveEncounter(dst.getEncounter());
		
		dst.setDi(new DstImpl(dst.getEncounter()));
		
		Context.getService(MdrtbService.class).evict(dst.di.getTest());
		Context.getService(MdrtbService.class).evict(dst.getEncounter());
		
		// hacky way to manually handle the addition of new dsts
		// note that we only add dsts that have a result and drug specified
		
		// remove dst results
		if (removeDstResults != null) {
			
			Set<String> removeDstResultSet = new HashSet<String>(Arrays.asList(removeDstResults));
			
			for (DstResult result : dst.getResults()) {
				if (result.getId() != null && removeDstResultSet.contains(result.getId())) {
					dst.removeResult(result);
				}
			}
		}
		
		int i = 1;
		while (i <= 30) {
			if (StringUtils.isNotEmpty(request.getParameter("addDstResult" + i + ".result"))
			        && StringUtils.isNotEmpty(request.getParameter("addDstResult" + i + ".drug"))) {
				// create the new result
				DstResult dstResult = dst.addResult();
				
				// pull the values from the request
				
				String resultType = request.getParameter("addDstResult" + i + ".result");
				String drug = request.getParameter("addDstResult" + i + ".drug");
				
				// although the DstResult obj should handle it, still a good idea to set the result before the drug because of the wonky way result/drugs are stored
				if (StringUtils.isNotBlank(resultType)) {
					dstResult.setResult(Context.getConceptService().getConcept(Integer.valueOf(resultType)));
				}
				if (StringUtils.isNotBlank(drug)) {
					dstResult.setDrug(Context.getConceptService().getConcept(Integer.valueOf(drug)));
				}
			}
			i++;
		}
		
		//Context.getService(MdrtbService.class).saveDst(dst.getDi());
		
		Context.getService(MdrtbService.class).saveDst(dst.getDi());
		status.setComplete();
		
		// clears the command object from the session
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			if (!mdr) {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl,
				    Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId).getPatient().getId(),
				    patientProgramId);
			}
			
			else {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl,
				    Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId).getPatient().getId(),
				    patientProgramId);
			}
		}
		
		return new ModelAndView(new RedirectView(returnUrl));
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
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getService(MdrtbService.class).getCultureLocations();
	}
	
	@ModelAttribute("dstResults")
	Collection<Concept> getPossibleDSTResults() {
		return Context.getService(MdrtbService.class).getPossibleDstResults();
	}
	
	@ModelAttribute("defaultDstDrugs")
	public List<List<Object>> getDefaultDstDrugs() {
		return MdrtbUtil.getDefaultDstDrugs();
	}
	
	@ModelAttribute("drugTypes")
	public Collection<Concept> getPossibleDrugTypes() {
		return Context.getService(MdrtbService.class).getMdrtbDrugs();
	}
	
}
