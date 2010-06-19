package org.openmrs.module.mdrtb.web.controller.specimen;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/specimen.form")
public class ShowSpecimenController {

	@RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimen(@RequestParam(required = true, value = "encounterId") Integer encounterId, ModelMap map) {
		
		// fetch the specimen
		MdrtbSpecimen specimen = Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(encounterId));
		
		map.addAttribute("specimen", specimen);
		
		return new ModelAndView("/module/mdrtb/specimen/specimen", map);
	}
	
}
