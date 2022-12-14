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
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/module/mdrtb/form/smear.form")
@SessionAttributes("smear")
public class SmearFormController {
	
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
	
	@ModelAttribute("smear")
	public SmearForm getSmearForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
		if (pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue()) {
			mdr = true;
			System.out.println("mdr");
		}
		
		else {
			mdr = false;
			System.out.println("not mdr");
		}
		// if no form is specified, create a new one
		if (encounterId == -1) {
			SmearForm form = null;
			if (!mdr) {
				TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
				
				form = new SmearForm(tbProgram.getPatient());
				
				// prepopulate the intake form with any program information
				//form.setEncounterDatetime(tbProgram.getDateEnrolled());
				form.setLocation(tbProgram.getLocation());
			}
			
			else {
				MdrtbPatientProgram mdrtbProgram = Context.getService(MdrtbService.class)
				        .getMdrtbPatientProgram(patientProgramId);
				
				form = new SmearForm(mdrtbProgram.getPatient());
				
				// prepopulate the intake form with any program information
				//form.setEncounterDatetime(mdrtbProgram.getDateEnrolled());
				form.setLocation(mdrtbProgram.getLocation());
			}
			return form;
		} else {
			return new SmearForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showSmearForm(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		//ModelMap map = new ModelMap();
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			SmearForm smear = null;
			if (encounterId != -1) { //we are editing an existing encounter
				smear = new SmearForm(Context.getEncounterService().getEncounter(encounterId));
			} else {
				try {
					smear = getSmearForm(-1, patientProgramId);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
			Location location = smear.getLocation();
			if (location != null) {
				for (Region o : oblasts) {
					if (o.getName().equals(location.getStateProvince())) {
						model.addAttribute("oblastSelected", o.getId());
						districts = Context.getService(MdrtbService.class).getDistrictsByParent(o.getId());
						model.addAttribute("districts", districts);
						for (District d : districts) {
							if (d.getName().equals(location.getCountyDistrict())) {
								model.addAttribute("districtSelected", d.getId());
								facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
								if (facilities != null) {
									model.addAttribute("facilities", facilities);
									for (Facility f : facilities) {
										if (f.getName().equals(location.getRegion())) {
											model.addAttribute("facilitySelected", f.getId());
											break;
										}
									}
								}
								break;
							}
						}
						break;
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
		return new ModelAndView("/module/mdrtb/form/smear", model);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSmearForm(@ModelAttribute("smear") SmearForm smear, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		Location location = null;
		if (facilityId != null && facilityId.length() != 0)
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), Integer.parseInt(facilityId));
		else
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), null);
		
		if (location == null) { // && locations!=null && (locations.size()==0 || locations.size()>1)) {
			throw new MdrtbAPIException("Invalid Hierarchy Set selected");
		}
		
		if (smear.getLocation() == null || !location.equals(smear.getLocation())) {
			smear.setLocation(location);
		}
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		if (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId()
		        .equals(pp.getProgram().getConcept().getId())) {
			mdr = true;
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(smear.getEncounter());
		
		// clears the command object from the session
		status.setComplete();
		
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
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("smearresults")
	public ArrayList<ConceptAnswer> getSmearResults() {
		ArrayList<ConceptAnswer> answerArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getPossibleSmearResults();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> concepts = new HashSet<Concept>();
			concepts.add(ms.getConcept(MdrtbConcepts.LOWAFB));
			concepts.add(ms.getConcept(MdrtbConcepts.WEAKLY_POSITIVE));
			concepts.add(ms.getConcept(MdrtbConcepts.MODERATELY_POSITIVE));
			concepts.add(ms.getConcept(MdrtbConcepts.STRONGLY_POSITIVE));
			concepts.add(ms.getConcept(MdrtbConcepts.NEGATIVE));
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
	
}
