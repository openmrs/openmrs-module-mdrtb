package org.openmrs.module.mdrtb.web.controller.regimen;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller manages the Regimen tab
 */
@Controller
public class MdrtbManageRegimenController {
	
	protected static final Log log = LogFactory.getLog(MdrtbManageRegimenController.class);
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
	}

    @RequestMapping("/module/mdrtb/regimen/manageRegimens")
    public void manageRegimens(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		ModelMap model) {
    	
    	model.addAttribute("patientId", patientId);
    	model.addAttribute("patientProgramId", patientProgramId);
    	
    	Patient p = Context.getPatientService().getPatient(patientId);
    	model.addAttribute("patient", p);

    	Map<String, RegimenHistory> m = RegimenUtils.getRegimenHistory(p);
    	model.addAttribute("regimenHistoryGroups", m);
    }
    
    @RequestMapping("/module/mdrtb/regimen/editRegimen")
    public void editRegimen(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		@RequestParam(required=true, value="type") String type,
    		@RequestParam(required=true, value="changeDate") Date changeDate,
    		ModelMap model) {
    	
    	model.addAttribute("patientId", patientId);
    	model.addAttribute("patientProgramId", patientProgramId);
    	model.addAttribute("type", type);
    	model.addAttribute("changeDate", changeDate);
    	
    	Patient p = Context.getPatientService().getPatient(patientId);
    	model.addAttribute("patient", p);
    }
    
    @RequestMapping("/module/mdrtb/regimen/saveRegimen")
    public String saveRegimen(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		@RequestParam(required=true, value="type") String type,
    		@RequestParam(required=true, value="startingChangeDate") Date startingChangeDate,
    		ModelMap model) {
    	
    	System.out.println("Hit the save Regimen page");

    	return "redirect:/module/mdrtb/regimen/manageRegimens.form?patientId="+patientId + "&patientProgramId="+patientProgramId;
    }    
}
