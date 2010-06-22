package org.openmrs.module.mdrtb.web.controller.specimen;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/mdrtb/specimen/delete.form")
public class SpecimenDeleteController {

	@RequestMapping(params="testId")
	public String deleteTest(@RequestParam("testId") Integer testId, @RequestParam("specimenId") Integer specimenId) {
	
		Context.getService(MdrtbService.class).deleteTest(testId);

		return "redirect:specimen.form?specimenId=" + specimenId;
	}
}
