package org.openmrs.module.mdrtb.web.controller.program;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.status.StatusUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProgramListController {
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations();
	}
	
	@RequestMapping("/module/mdrtb/program/programList.form")
	public ModelAndView showPrograms(@RequestParam(required = true, value = "patientId") Integer patientId) {
			
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if (patient == null) {
			throw new RuntimeException("Invalid patient id passed to ProgramListController.");
		}
		
		// first get all the MDR-TB programs for this patient
		List<MdrtbPatientProgram> programs = StatusUtil.getMdrtbPrograms(patient);
	
		// if there is exactly one patient program, we can just default to use that one and proceed directly to the dashboard
		if (programs.size() == 1) {
			return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientProgramId=" + programs.get(0).getId() 
				+ "&patientId=" + patientId);
		}
		else {
			Collections.reverse(programs);
			
			ModelMap map = new ModelMap();
			map.addAttribute("patientId", patientId);
			map.addAttribute("programs", programs);
			
			return new ModelAndView("/module/mdrtb/program/programList", map);
		}
	}
	
}
