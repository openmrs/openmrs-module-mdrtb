package org.openmrs.module.mdrtb.web.controller.regimen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegimenViewController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		
		// register binders for location and program workflow state
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
		
	}
	
	@RequestMapping("/module/mdrtb/regimen/regimenView.form")
	public ModelAndView showRegimens(@RequestParam(required = true, value = "patientId") Integer patientId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        
	        ModelMap map) {
		
		Context.getPatientService().getPatient(patientId);
		MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		
		map.put("patientProgramId", program.getId());
		
		// add the patientId
		map.put("patientId", program.getPatient().getId());
		
		ArrayList<RegimenForm> forms = Context.getService(MdrtbService.class).getRegimenFormsForProgram(program.getPatient(),
		    program.getId());
		
		map.put("forms", forms);
		
		return new ModelAndView("/module/mdrtb/regimen/regimenView", map);
		
	}
	
}
