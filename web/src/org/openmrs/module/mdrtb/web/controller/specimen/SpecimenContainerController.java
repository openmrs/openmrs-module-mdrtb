package org.openmrs.module.mdrtb.web.controller.specimen;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/mdrtb/specimen/specimenContainer.form")
public class SpecimenContainerController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String showSpecimenContainer() {
		return "/module/mdrtb/specimen/specimenContainer";
	}
}
