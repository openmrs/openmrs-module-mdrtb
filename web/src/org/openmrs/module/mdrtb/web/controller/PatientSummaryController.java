package org.openmrs.module.mdrtb.web.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartRecord;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PatientSummaryController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/mdrtb/summary/summary.form")
	ModelAndView showPatientChart(@RequestParam(required = false, value="patientId") Integer patientId, ModelMap map) {
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		PatientChart patientChart = Context.getService(MdrtbService.class).getPatientChart(patient);
		
		map.put("patientId",patientId);
		map.put("drugTypes", getDrugTypes(patientChart));
		map.put("records",patientChart.getRecords());
		
		return new ModelAndView("/module/mdrtb/summary/patientSummary", map);
	}

	List<Concept> getDrugTypes(PatientChart patientChart) {
		List<Concept> drugTypes = Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
		
		// in this set we will store all the existing drug types in the set of specimens
		List<Concept> existingDrugTypes = new LinkedList<Concept>();
		
		// get all the existing drugs in the specimen
		Map<String,PatientChartRecord> records = patientChart.getRecords();
		for(String key : records.keySet()) {
			for(Specimen specimen : records.get(key).getSpecimens()) {
				for(Dst dst : specimen.getDsts()) {
					for(DstResult dstResult : dst.getResults()) {	
						if(dstResult.getDrug() != null) {
							existingDrugTypes.add(dstResult.getDrug());
						}
					}
				}
			}
		}
		
		// only retain the drugs that exist within the patient specimen
		drugTypes.retainAll(existingDrugTypes);
		
		return drugTypes;
	}
}
