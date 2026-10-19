package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.reporting.custom.Form89Data;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Form89SingleExportController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/form89Single")
	public ModelAndView showRegimenOptions(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "yearSelected", required = false) Integer year,
	        @RequestParam(value = "quarterSelected", required = false) String quarter,
	        @RequestParam(value = "monthSelected", required = false) String month, ModelMap model) {
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
		} else if (district == null) {
			
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		} else {
			
			/*
			* if oblast is dushanbe, return both districts and facilities
			*/
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
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
		
		return new ModelAndView("/module/mdrtb/reporting/form89Single", model);
		
	}
	
	@SuppressWarnings({})
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/form89Single")
	public static String doForm89(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) {
		
		System.out.println("---POST-----");
		System.out.println("PARAMS:" + oblastId + " " + districtId + " " + facilityId + " " + year + " " + quarter + " "
		        + month);
		List<Location> locList = null;
		if (oblastId != null) {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		ArrayList<Form89Data> patientSet = getForm89PatientSet(year, quarterInt, monthInt, monthInt, locList);
		
		Collections.sort(patientSet);
		Integer num = patientSet.size();
		model.addAttribute("num", num);
		model.addAttribute("patientSet", patientSet);
		model.addAttribute("locale", Context.getLocale().toString());
		
		boolean reportStatus = Context.getService(MdrtbService.class).getReportArchived(oblastId, districtId, facilityId,
		    year, quarterInt, monthInt, "TB-03", ReportType.DOTSTB);
		
		System.out.println(reportStatus);
		model.addAttribute("oblast", oblastId);
		model.addAttribute("district", districtId);
		model.addAttribute("facility", facilityId);
		model.addAttribute("year", year);
		if (month != null && !month.isEmpty())
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if (quarter != null && !quarter.isEmpty())
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
		model.addAttribute("reportDate", Context.getDateTimeFormat().format(new Date()));
		model.addAttribute("reportStatus", reportStatus);
		return "/module/mdrtb/reporting/form89SingleResults";
	}
	
	public static ArrayList<Form89Data> getForm89PatientSet(Integer year, Integer quarter, Integer month, Integer month2, 
	        List<Location> locList) {
		List<Form89> f89List = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month, month2);
		
		ArrayList<Form89Data> patientSet = new ArrayList<>();
		SimpleDateFormat sdf = Context.getDateFormat();
		
		for (final Form89 f89 : f89List) {
			System.out.println("Processing: " + f89.getPatient().toString());
			Form89Data f89Data = new Form89Data();
			
			Patient patient = f89.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
			}
			f89Data.setPatient(patient);
			f89Data.setForm89(f89);
			
			//PATIENT IDENTIFIER
			String identifier = f89.getRegistrationNumber();
			f89Data.setIdentifier(identifier);
			
			//DATE OF TB03 REGISTRATION
			Date encDate = null;
			if (f89.getTB03() != null) {
				encDate = f89.getTB03().getEncounterDatetime();
				f89Data.setTb03RegistrationDate(sdf.format(encDate));
			}
			
			// TB03 Location
			f89Data.setTb03Location(f89.getTB03().getEncounter().getLocation());
			
			//FORMATTED DATE OF BIRTH
			if (patient.getBirthdate() != null)
				f89Data.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
			//AGE AT TB03 Registration
			Integer age = f89.getAgeAtRegistration();
			if (age != null)
				f89Data.setAgeAtTB03Registration(age);
			
			//SITE OF DISEASE (P/EP)
			Concept q = f89.getAnatomicalSite();
			
			if (q != null)
				f89Data.setSiteOfDisease(q.getName(Context.getLocale()).getName());
			if (f89.getForm89Date() != null) {
				f89Data.setForm89Date(sdf.format(f89.getForm89Date()));
			}
			if (f89.getDateFirstSeekingHelp() != null) {
				f89Data.setDateFirstSeekingHelp(sdf.format(f89.getDateFirstSeekingHelp()));
			}
			if (f89.getCmacDate() != null) {
				f89Data.setCmacDate(sdf.format(f89.getCmacDate()));
			}
			if (f89.getDateOfReturn() != null) {
				f89Data.setDateOfReturn(sdf.format(f89.getDateOfReturn()));
			}
			if (f89.getDateOfDecaySurvey() != null) {
				f89Data.setDateOfDecaySurvey(sdf.format(f89.getDateOfDecaySurvey()));
			}
			
			//DIAGNOSTIC RESULTS
			
			//DIAGNOSTIC SMEAR
			
			List<SmearForm> smears = f89.getSmears();
			SmearForm diagnosticSmear = null;
			
			if (smears != null && !smears.isEmpty()) {
				diagnosticSmear = smears.get(0);
			}
			
			if (diagnosticSmear != null) {
				if (diagnosticSmear.getSmearResult() != null) {
					f89Data.setDiagnosticSmearResult(diagnosticSmear.getSmearResult().getName(Context.getLocale()).getName());
				}
				if (diagnosticSmear.getEncounterDatetime() != null) {
					f89Data.setDiagnosticSmearDate(sdf.format(diagnosticSmear.getEncounterDatetime()));
				}
				
				f89Data.setDiagnosticSmearTestNumber(diagnosticSmear.getSpecimenId());
				
				Location loc = diagnosticSmear.getLocation();
				if (loc != null) {
					f89Data.setDiagnosticSmearLab(loc.getName());
				}
			}
			
			List<XpertForm> xperts = f89.getXperts();
			
			XpertForm firstXpert = null;
			
			if (xperts != null && !xperts.isEmpty())
				firstXpert = xperts.get(0);
			if (firstXpert != null) {
				if (firstXpert.getMtbResult() != null)
					f89Data.setXpertMTBResult(firstXpert.getMtbResult().getName(Context.getLocale()).getName());
				if (firstXpert.getRifResult() != null)
					f89Data.setXpertRIFResult(firstXpert.getRifResult().getName(Context.getLocale()).getName());
				if (firstXpert.getEncounterDatetime() != null)
					f89Data.setXpertTestDate(sdf.format(firstXpert.getEncounterDatetime()));
				
				f89Data.setXpertTestNumber(firstXpert.getSpecimenId());
				
				Location loc = firstXpert.getLocation();
				if (loc != null) {
					f89Data.setXpertLab(loc.getName());
				}
			}
			
			List<HAINForm> hains = f89.getHains();
			HAINForm firstHAIN = null;
			if (hains != null && !hains.isEmpty())
				firstHAIN = hains.get(0);
			
			if (firstHAIN != null) {
				if (firstHAIN.getMtbResult() != null)
					f89Data.setHainMTBResult(firstHAIN.getMtbResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getRifResult() != null)
					f89Data.setHainRIFResult(firstHAIN.getRifResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getInhResult() != null)
					f89Data.setHainINHResult(firstHAIN.getInhResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getEncounterDatetime() != null)
					f89Data.setHainTestDate(sdf.format(firstHAIN.getEncounterDatetime()));
				
				f89Data.setHainTestNumber(firstHAIN.getSpecimenId());
				
				Location loc = firstHAIN.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
						f89Data.setHainLab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
						f89Data.setHainLab(loc.getCountyDistrict());
					}
				}
			}
			
			List<HAIN2Form> hain2s = f89.getHain2s();
			
			HAIN2Form firstHAIN2 = null;
			
			if (hain2s != null && !hain2s.isEmpty())
				firstHAIN2 = hain2s.get(0);
			
			if (firstHAIN2 != null) {
				if (firstHAIN2.getMtbResult() != null)
					f89Data.setHain2MTBResult(firstHAIN2.getMtbResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getFqResult() != null)
					f89Data.setHain2FqResult(firstHAIN2.getFqResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getInjResult() != null)
					f89Data.setHain2InjResult(firstHAIN2.getInjResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getEncounterDatetime() != null)
					f89Data.setHain2TestDate(sdf.format(firstHAIN2.getEncounterDatetime()));
				
				f89Data.setHain2TestNumber(firstHAIN2.getSpecimenId());
				Location loc = firstHAIN2.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
						f89Data.setHain2Lab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
						f89Data.setHain2Lab(loc.getCountyDistrict());
					}
				}
			}
			patientSet.add(f89Data);
			
		}
		return patientSet;
	}
}
