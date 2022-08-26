package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Oblast;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/*import org.openmrs.module.mdrtbdrugforecast.DrugCount;
import org.openmrs.module.mdrtbdrugforecast.MdrtbDrugStock;
import org.openmrs.module.mdrtbdrugforecast.MdrtbUtil;
import org.openmrs.module.mdrtbdrugforecast.MdrtbConcepts;
import org.openmrs.module.mdrtbdrugforecast.drugneeds.DrugForecastUtil;
import org.openmrs.module.mdrtbdrugforecast.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtbdrugforecast.regimen.Regimen;
import org.openmrs.module.mdrtbdrugforecast.regimen.RegimenUtils;
import org.openmrs.module.mdrtbdrugforecast.reporting.definition.MdrtbDrugForecastTreatmentStartedCohortDefinition;
import org.openmrs.module.mdrtbdrugforecast.reporting.definition.MdrtbDrugForecastTreatmentStartedOnDrugCohortDefinition;
import org.openmrs.module.mdrtbdrugforecast.service.MdrtbDrugForecastService;
import org.openmrs.module.mdrtbdrugforecast.status.TreatmentStatusCalculator;
import org.openmrs.module.mdrtbdrugforecast.web.controller.status.DashboardTreatmentStatusRenderer;*/
import org.springframework.web.servlet.ModelAndView;

@Controller

public class MissingTb03Controller {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
        binder.registerCustomEditor(Concept.class, new ConceptEditor());
        binder.registerCustomEditor(Location.class, new LocationEditor());
    }
        
    
    @RequestMapping(method=RequestMethod.GET, value="/module/mdrtb/reporting/missingTb03")
    public ModelAndView showRegimenOptions(@RequestParam(value="loc", required=false) String district,
			@RequestParam(value="ob", required=false) String oblast,
			@RequestParam(value="yearSelected", required=false) Integer year,
			@RequestParam(value="quarterSelected", required=false) String quarter,
			@RequestParam(value="monthSelected", required=false) String month,
			ModelMap model) {
    	
    	List<Oblast> oblasts;
        List<Facility> facilities;
        List<District> districts;
    	
    	if(oblast==null) {
    		oblasts = Context.getService(MdrtbService.class).getOblasts();
    		model.addAttribute("oblasts", oblasts);
    	}
    	 
    	
    	else if(district==null)
         { 
    		//DUSHANBE
    		if(Integer.parseInt(oblast)==186) {
    			oblasts = Context.getService(MdrtbService.class).getOblasts();
    			districts= Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
    			District d = districts.get(0);
    			facilities = Context.getService(MdrtbService.class).getFacilities(d.getId());
    			model.addAttribute("oblastSelected", oblast);
    			model.addAttribute("oblasts", oblasts);
           	 	model.addAttribute("districts", districts);
           	 	model.addAttribute("facilities", facilities);
           	 	model.addAttribute("dushanbe", 186);
    		}
    		
    		else {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class)
						.getDistricts(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
    		}
         }
    	
         else
         {
        	 /*
      		 * if oblast is dushanbe, return both districts and facilities
      		 */
     		if(Integer.parseInt(oblast)==186) {
     			oblasts = Context.getService(MdrtbService.class).getOblasts();
     			districts= Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
     			District d = districts.get(0);
     			facilities = Context.getService(MdrtbService.class).getFacilities(d.getId());
     			model.addAttribute("oblastSelected", oblast);
     			model.addAttribute("oblasts", oblasts);
            	 	model.addAttribute("districts", districts);
            	 	model.addAttribute("facilities", facilities);
            	 	model.addAttribute("dushanbe", 186);
     		}
     		
     		else {
        	 
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class)
						.getDistricts(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class)
						.getFacilities(Integer.parseInt(district));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("districtSelected", district);
				model.addAttribute("facilities", facilities);
     		}
         }
    	
    	 model.addAttribute("yearSelected", year);
    	 model.addAttribute("monthSelected", month);
    	 model.addAttribute("quarterSelected", quarter);
       
        /*List<Location> locations = Context.getLocationService().getAllLocations(false);// Context.getLocationService().getAllLocations();//ms = (MdrtbDrugForecastService) Context.getService(MdrtbDrugForecastService.class);
        List<Oblast> oblasts = Context.getService(MdrtbService.class).getOblasts();
        //drugSets =  ms.getMdrtbDrugs();
        
       

        model.addAttribute("locations", locations);
        model.addAttribute("oblasts", oblasts);*/
    	 return new ModelAndView("/module/mdrtb/reporting/missingTb03", model);
    
    }
    
    
    @SuppressWarnings("unused")
	@RequestMapping(method=RequestMethod.POST, value="/module/mdrtb/reporting/missingTb03")
    public static String doDQ(
    		@RequestParam("district") Integer districtId,
    		@RequestParam("oblast") Integer oblastId,
    		@RequestParam("facility") Integer facilityId,
            @RequestParam(value="year", required=true) Integer year,
            @RequestParam(value="quarter", required=false) String quarter,
            @RequestParam(value="month", required=false) String month,
            ModelMap model) throws EvaluationException {
    	
    //	
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	
    	/*ArrayList<Person> patientList = new ArrayList<Person>();
    	ArrayList<Concept> conceptQuestionList = new ArrayList<Concept>();
    	ArrayList<Concept> conceptAnswerList = new ArrayList<Concept>();
    	
    	List<Obs> obsList = null;*/
    	
    	List<DQItem> missingTB03 = new ArrayList<DQItem>();
    	
    	List<Patient> errList = new ArrayList<Patient>();
    	
    	Boolean errorFlag = Boolean.FALSE;
    	Integer errorCount = 0;
    	
    	Date treatmentStartDate = null;
    	Calendar tCal = null;
    	Calendar nowCal = null;
    	long timeDiff = 0;
    	double diffInWeeks = 0;
    	
    	
	    DQItem dqi = null;
	    
	    
	    
    	sdf.applyPattern("dd.MM.yyyy");
    	SimpleDateFormat rdateSDF = new SimpleDateFormat();
    	rdateSDF.applyPattern("dd.MM.yyyy HH:mm:ss");
    	
    	//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
    	
    	ArrayList<Location> locList = null;
    	if(oblastId!=null) {
    		if(oblastId.intValue()==186) {
    			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId,districtId,facilityId);
    		}
    		else {
    			locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
    		}
    	}
    	
    	ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
    	
    
    	
    	
    	
    	
    	Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));
    	Integer countNum = 0;
    	
    	for (TB03Form  tf : tb03List) {
    		
    		 //INIT
    	    treatmentStartDate = null;
    		tCal = null;
    		nowCal = null;
    		timeDiff = 0;
    		diffInWeeks = 0;
    		
    	   // patientList.clear();
    	    errorFlag = Boolean.FALSE;
    	 
    		
    		dqi = new DQItem();
    	    Patient patient = tf.getPatient();//Context.getPatientService().getPatient(i);
    	    
    	    if(patient==null || patient.isVoided()) {
    	    	continue;
    	    }
    	    
    	    if(patient.getGender().equals("F") && Context.getLocale().equals("ru")) {
    	    	patient.setGender(Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female"));
    	    }
    	   // patientList.add(patient);
    	    dqi.setPatient(patient);
    	    dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
    	}

    	
    	TbPatientProgram temp = null;
    	
    	List<TbPatientProgram> progList = Context.getService(MdrtbService.class).getAllTbPatientProgramsEnrolledInDateRangeAndLocations(startDate, endDate, locList);
    	//Integer countNum = 0;
    	Boolean matched = Boolean.FALSE;
    	if(progList!=null) {
    		for(TbPatientProgram p : progList) {
    			matched = Boolean.FALSE;
    			dqi = new DQItem();
        	    Patient patient = p.getPatient();//Context.getPatientService().getPatient(i);
        	    
        	    if(patient==null || patient.isVoided()) {
        	    	continue;
        	    }
        	    
        	    for(TB03Form t3f : tb03List) {
        	    	Patient tempPat = t3f.getPatient();
        	    	
        	    	if(tempPat.getId().intValue()==patient.getId().intValue()) {
        	    		matched = Boolean.TRUE;
        	    		break;
        	    	}
        	    	
        	    	
        	    }
        	    
        	    if(!matched)
        	    	countNum++;
        	    
        	    if(patient.getGender().equals("F") && Context.getLocale().equals("ru")) {
        	    	patient.setGender(Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female"));
        	    }
        	   // patientList.add(patient);
        	    dqi.setPatient(patient);
        	    dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
    			
    			ArrayList<TB03Form> x = Context.getService(MdrtbService.class).getTB03FormsForProgram(patient, p.getId());
    		
    			if(x==null || x.size()==0) {
    				//errorFlag = Boolean.TRUE;
    				missingTB03.add(dqi);
    				
    				if(!errList.contains(patient)) {
        				errorCount++;
        				errList.add(patient);
        			}
    				
    			
    			}	
    		}
    		
    	}
    	
    	Integer num = countNum;// + tofList.size();
    	
    	
    	Integer errorPercentage = null;
    	if(num==0)
    		errorPercentage = 0;
    	else
    		errorPercentage = (errorCount*100)/num;
    	
    	String oName = null;
    	Oblast obl = Context.getService(MdrtbService.class).getOblast(oblastId);
    	if(obl!=null)
    		oName = obl.getName();
    	
    	String dName = null;
    	if(districtId!=null) {
    		District dist = Context.getService(MdrtbService.class).getDistrict(districtId);
    		if(dist!=null)
    			dName = dist.getName();
    	}
    	
    	String fName = null;
    	if(facilityId!=null) {
    		Facility fac = Context.getService(MdrtbService.class).getFacility(facilityId);
    		if(fac!=null)
    			fName = fac.getName();
    	}
    	
    	model.addAttribute("num", num);
    	model.addAttribute("missingTB03", missingTB03);
    	
    	model.addAttribute("errorCount", new Integer(errorCount));
    	model.addAttribute("errorPercentage", errorPercentage.toString() + "%");
    	model.addAttribute("oName", oName);
    	model.addAttribute("dName", dName);
    	model.addAttribute("fName", fName);
    	
    	
    	
    	
    	model.addAttribute("locale", Context.getLocale().toString());
    	
    	// TO CHECK WHETHER REPORT IS CLOSED OR NOT
    	/*Integer report_oblast = null; Integer report_quarter = null; Integer report_month = null;
		if(new PDFHelper().isInt(oblast)) { report_oblast = Integer.parseInt(oblast); }
		if(new PDFHelper().isInt(quarter)) { report_quarter = Integer.parseInt(quarter); }
		if(new PDFHelper().isInt(month)) { report_month = Integer.parseInt(month); }*/
		
    	boolean reportStatus = Context.getService(MdrtbService.class).readReportStatus(oblastId, districtId, facilityId, year, quarter, month, "DOTSDQ", "DOTSTB");
    	
    	model.addAttribute("oblast", oblastId);
    	model.addAttribute("facility", facilityId);
    	model.addAttribute("district", districtId);
    	model.addAttribute("year", year);
    	if(month!=null && month.length()!=0)
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if(quarter!=null && quarter.length()!=0)
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
    	model.addAttribute("reportDate", rdateSDF.format(new Date()));
    	model.addAttribute("reportStatus", reportStatus);
        return "/module/mdrtb/reporting/missingTb03Results";
    }
    
    
  
    
}
