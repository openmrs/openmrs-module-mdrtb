package org.openmrs.module.mdrtb.web.controller.lab;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LabTestAttributeTypeController {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	private final String SUCCESS_ADD_FORM_VIEW = "/module/mdrtb/addLabTestAttributeType";
	
	LabTestService LabTestService;
	
	@ModelAttribute("datatypes")
	public Collection<String> getDatatypes() {
		return CustomDatatypeUtil.getDatatypeClassnames();
	}
	
	@ModelAttribute("handlers")
	public Collection<String> getHandlers() {
		return CustomDatatypeUtil.getHandlerClassnames();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/addLabTestAttributeType.form")
	public String showForm(ModelMap model, @RequestParam(value = "error", required = false) String error,
	        @RequestParam(value = "uuid", required = false) String uuid) {
		LabTestService = Context.getService(LabTestService.class);
		LabTestAttributeType attributeType;
		if (uuid == null || uuid.equalsIgnoreCase("")) {
			attributeType = new LabTestAttributeType();
		} else {
			attributeType = LabTestService.getLabTestAttributeTypeByUuid(uuid);
			List<LabTestAttribute> labTestAttributes = LabTestService.getLabTestAttributes(attributeType, Boolean.FALSE);
			if (labTestAttributes.size() > 0) {
				model.addAttribute("available", Boolean.TRUE);
			} else {
				model.addAttribute("available", Boolean.FALSE);
			}
		}
		model.addAttribute("listTestType", LabTestService.getAllLabTestTypes(Boolean.FALSE));
		model.addAttribute("attributeType", attributeType);
		model.addAttribute("error", error);
		return SUCCESS_ADD_FORM_VIEW;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/addLabTestAttributeType.form")
	public String onSubmit(ModelMap model, HttpSession httpSession,
	        @ModelAttribute("anyRequestObject") Object anyRequestObject, HttpServletRequest request,
	        @ModelAttribute("attributeType") LabTestAttributeType attributeType, BindingResult result) {
		LabTestService = Context.getService(LabTestService.class);
		String status = "";
		try {
			if (result.hasErrors()) {
				status = "Invalid Lab Test Type concept Id entered";
				model.addAttribute("error", status);
				if (attributeType.getLabTestAttributeTypeId() == null) {
					return "redirect:addLabTestAttributeType.form";
				} else {
					return "redirect:addLabTestAttributeType.form?uuid=" + attributeType.getUuid();
				}
			} else {
				LabTestService.saveLabTestAttributeType(attributeType);
				StringBuilder subString = new StringBuilder();
				subString.append("Lab Test Attribute with Uuid :");
				subString.append(attributeType.getUuid());
				subString.append(" is  saved!");
				status = subString.toString();
			}
		}
		catch (Exception e) {
			status = "could not save Lab Test Attribute Type";
			e.printStackTrace();
			model.addAttribute("error", status);
			if (attributeType.getLabTestAttributeTypeId() == null) {
				return "redirect:addLabTestAttributeType.form";
			} else {
				return "redirect:addLabTestAttributeType.form?uuid=" + attributeType.getUuid();
			}
		}
		model.addAttribute("save", status);
		return "redirect:manageLabTestAttributeTypes.form";
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/retirelabtestattributetype.form")
	public String onRetire(ModelMap model, HttpSession httpSession, HttpServletRequest request,
	        @RequestParam("uuid") String uuid, @RequestParam("retireReason") String retireReason) {
		LabTestService = Context.getService(LabTestService.class);
		LabTestAttributeType attributeType = Context.getService(LabTestService.class).getLabTestAttributeTypeByUuid(uuid);
		String status = "";
		if (Context.getAuthenticatedUser() == null) {
			return "redirect:../../login.htm";
		}
		if (Context.getAuthenticatedUser() == null) {
			return "redirect:../../login.htm";
		}
		try {
			LabTestService.retireLabTestAttributeType(attributeType, retireReason);
			StringBuilder subString = new StringBuilder();
			subString.append("Lab Test Attribute with Uuid :");
			subString.append(attributeType.getUuid());
			subString.append(" is  retired!");
			status = subString.toString();
		}
		catch (Exception e) {
			status = "could not retire Lab Test Attribute Type";
			e.printStackTrace();
			model.addAttribute("error", status);
			if (attributeType.getLabTestAttributeTypeId() == null) {
				return "redirect:addLabTestAttributeType.form";
			} else {
				return "redirect:addLabTestAttributeType.form?uuid=" + attributeType.getUuid();
			}
		}
		model.addAttribute("save", status);
		return "redirect:manageLabTestAttributeTypes.form";
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/deletelabtestattributetype.form")
	public String onDelete(ModelMap model, HttpSession httpSession, HttpServletRequest request,
	        @RequestParam("uuid") String uuid) {
		LabTestService = Context.getService(LabTestService.class);
		LabTestAttributeType attributeType = LabTestService.getLabTestAttributeTypeByUuid(uuid);
		String status;
		if (Context.getAuthenticatedUser() == null) {
			return "redirect:../../login.htm";
		}
		try {
			LabTestService.deleteLabTestAttributeType(attributeType, true);
			StringBuilder subString = new StringBuilder();
			subString.append("Lab Test Attribute with Uuid :");
			subString.append(attributeType.getUuid());
			subString.append(" is permanently deleted!");
			status = subString.toString();
		}
		catch (Exception exception) {
			// status = exception.getLocalizedMessage();
			status = "could not delete Lab Test Attribute Type";
			exception.printStackTrace();
			model.addAttribute("error", status);
			return "redirect:addLabTestAttributeType.form?uuid=" + attributeType.getUuid();
		}
		model.addAttribute("save", status);
		return "redirect:manageLabTestAttributeTypes.form";
	}
}
