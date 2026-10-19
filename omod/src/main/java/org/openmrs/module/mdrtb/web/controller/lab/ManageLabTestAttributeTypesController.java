package org.openmrs.module.mdrtb.web.controller.lab;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/module/mdrtb/manageLabTestAttributeTypes.form")
public class ManageLabTestAttributeTypesController {
	
	/** Success form view name */
	private final String SUCCESS_FORM_VIEW = "/module/mdrtb/manageLabTestAttributeTypes";
	
	/** Logger for this class */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public String showLabTestAttributeTypes(@RequestParam(required = false) String save, ModelMap model) {
		List<LabTestAttributeType> list = Context.getService(LabTestService.class)
		        .getAllLabTestAttributeTypes(Boolean.FALSE);
		model.put("labTestAttributeTypes", list);
		model.addAttribute("status", save);
		return SUCCESS_FORM_VIEW;
	}
	
}
