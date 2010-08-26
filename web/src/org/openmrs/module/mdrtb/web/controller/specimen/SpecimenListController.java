package org.openmrs.module.mdrtb.web.controller.specimen;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/list.form")
public class SpecimenListController {

	protected final Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimenList(@RequestParam(required = true, value="patientId") int patientId, ModelMap map) {
				
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if(patient == null) {
			throw new MdrtbAPIException("Invalid patient id");
		}
		
		List<Specimen> specimens = Context.getService(MdrtbService.class).getSpecimens(patient);
		
		map.put("patientId", patientId);
		map.put("specimens", specimens);
		
		return new ModelAndView("/module/mdrtb/specimen/specimenList",map);
		
	}
	
}
