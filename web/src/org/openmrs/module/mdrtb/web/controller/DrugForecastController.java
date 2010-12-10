package org.openmrs.module.mdrtb.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.drugneeds.DrugForecastUtil;
import org.openmrs.propertyeditor.ConceptEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("cohort")
public class DrugForecastController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true));
        binder.registerCustomEditor(Concept.class, new ConceptEditor());
    }
        
    
    @RequestMapping(method=RequestMethod.GET, value="/module/mdrtb/drugforecast/simpleUsage")
    public void showSimpleForecastOptions(ModelMap model) {
        Cohort cohort = Context.getPatientSetService().getAllPatients();
        cohort.setDescription("All patients in system");
        List<Concept> drugSets = new ArrayList<Concept>();
        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        {
        	Concept tb = Context.getConceptService().getConceptByName("TUBERCULOSIS TREATMENT DRUGS");
            tb.getConceptSets();
            drugSets.add(tb);
        }

        model.addAttribute(cohort);
        model.addAttribute("drugSets", drugSets);
    }
    
    
    @RequestMapping(method=RequestMethod.POST, value="/module/mdrtb/drugforecast/simpleUsage")
    public String doSimpleForecast(
            @ModelAttribute Cohort cohort,
            @RequestParam("drugSet") Concept drugSet,
            @RequestParam(value="fromDate", required=false) Date fromDate,
            @RequestParam(value="toDate", required=false) Date toDate,
            ModelMap model) {

        if (fromDate == null)
            fromDate = new Date();
        if (toDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            cal.add(Calendar.DAY_OF_MONTH, 30);
            toDate = cal.getTime();
        }
        if (fromDate.compareTo(toDate) > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            toDate = cal.getTime();
        }
        
        Map<Drug, Double> usage = DrugForecastUtil.simpleDrugNeedsCalculation(cohort, drugSet, fromDate, toDate);
        
        int duration = DrugForecastUtil.daysFrom(fromDate, toDate);
        
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Map.Entry<Drug, Double> e : usage.entrySet()) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("drug", e.getKey());
            row.put("totalUsage", DrugForecastUtil.formatNicely(e.getValue()));
            row.put("dailyUsage", DrugForecastUtil.formatNicely(duration <= 0 ? 0.0 : e.getValue()/duration));
            data.add(row);
        }
        
        model.put("drugSet", drugSet);
        model.put("fromDate", fromDate);
        model.put("toDate", toDate);
        model.put("duration", duration);
        model.put("usage", data);
        return "/module/mdrtb/drugforecast/simpleUsageResults";
    }

    
    @RequestMapping(method=RequestMethod.GET, value="/module/mdrtb/drugforecast/patientsTakingDrugs")
    public void showCountPatientsOnDrugsOptions(ModelMap model) {
        Cohort cohort = Context.getPatientSetService().getAllPatients();
        cohort.setDescription("All patients in system");
        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        List<Concept> drugSets = new ArrayList<Concept>();
        {
        	Concept tb = Context.getConceptService().getConceptByName("TUBERCULOSIS TREATMENT DRUGS");
            tb.getConceptSets();
            drugSets.add(tb);
        }
        model.addAttribute(cohort);
        model.addAttribute("drugSets", drugSets);
    }
    
    
    @RequestMapping(method=RequestMethod.POST, value="/module/mdrtb/drugforecast/patientsTakingDrugs")
    public String doCountPatientsOnDrugs(
            @ModelAttribute Cohort cohort,
            @RequestParam("method") String method,
            @RequestParam("drugSet") Concept drugSet,
            @RequestParam(value="onDate", required=false) Date onDate,
            ModelMap model) {

        if (onDate == null)
            onDate = new Date();

        model.put("drugSet", drugSet);
        model.put("onDate", onDate);
        model.put("method", method);

        if (method.equals("drug")) {
            Map<Drug, Integer> data = DrugForecastUtil.countPatientsTakingDrugs(cohort, drugSet, onDate);
            model.put("patientsTaking", data);
        } else { // generic
            Map<Concept, Integer> data = DrugForecastUtil.countPatientsTakingGenericDrugs(cohort, drugSet, onDate);
            model.put("patientsTaking", data);
        }
        
        return "/module/mdrtb/drugforecast/patientsTakingDrugsResults";
    }
}
