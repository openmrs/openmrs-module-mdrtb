package org.openmrs.module.mdrtb.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Just a hack redirect controller to stop people from going to the old home page by accident
 */

@Controller
public class MdrtbRedirectController {

	@RequestMapping("/module/mdrtb/mdrtbPatientOverview.form")
	public String redirect(@RequestParam(required=false, value="patientId") Integer patientId){
		
		if(patientId != null) {
			return "redirect:/module/mdrtb/program/programList.form?patientId=" + patientId;
		}
		
		return "redirect:/module/mdrtb/mdrtbIndex.form";
	}
}
