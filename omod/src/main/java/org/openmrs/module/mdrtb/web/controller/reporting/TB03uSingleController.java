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
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
import org.openmrs.module.mdrtb.reporting.custom.TB03uData;
import org.openmrs.module.mdrtb.reporting.custom.TB03uUtil;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
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

@SuppressWarnings({ "deprecation", "unused" })
@Controller
public class TB03uSingleController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/tb03uSingle")
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
		}
		
		else if (district == null) {
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
		}
		
		else {
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
				model.addAttribute("districtSelected", district);
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
		if (month != null && !month.isEmpty())
			model.addAttribute("monthSelected", month.replace("\"", ""));
		else
			model.addAttribute("monthSelected", "");
		
		if (quarter != null && !quarter.isEmpty())
			model.addAttribute("quarterSelected", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarterSelected", "");
		return new ModelAndView("/module/mdrtb/reporting/tb03uSingle", model);
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/tb03uSingle")
	public static String doTB03(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) {
		System.out.println("---POST-----");
		System.out.println("PARAMS:" + oblastId + " " + districtId + " " + facilityId + " " + year + " " + quarter + " "
		        + month);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		List<Location> locList = null;
		if (oblastId != null) {
			if (oblastId == 186) {
				locList = Context.getService(MdrtbService.class)
				        .getLocationListForDushanbe(oblastId, districtId, facilityId);
			} else {
				Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
				District district = Context.getService(MdrtbService.class).getDistrict(districtId);
				Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
				
				locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
			}
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		List<TB03uForm> tb03uList = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarterInt,
		    monthInt, monthInt);
		
		ArrayList<TB03uData> patientSet = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		Integer regimenConceptId = null;
		Integer codId = null;
		
		sdf.applyPattern("dd.MM.yyyy");
		SimpleDateFormat rdateSDF = new SimpleDateFormat();
		rdateSDF.applyPattern("dd.MM.yyyy HH:mm:ss");
		for (TB03uForm tf : tb03uList) {
			TB03uData tb03uData = new TB03uData();
			
			Patient patient = tf.getPatient();
			if (patient == null || patient.isVoided()) {
				continue;
				
			}
			tb03uData.setPatient(patient);
			
			//PATIENT IDENTIFIER
			String identifier = TB03Util.getRegistrationNumber(tf);
			tb03uData.setIdentifierMDR(identifier);
			
			String identifierDOTS = TB03Util.getRegistrationNumber(tf.getTb03());
			tb03uData.setIdentifierDOTS(identifierDOTS);
			
			Date encDate = tf.getEncounterDatetime();
			
			tb03uData.setTb03uRegistrationDate(sdf.format(encDate));
			tb03uData.setDotsYear(tf.getTb03RegistrationYear());
			
			Integer ageAtReg = tf.getAgeAtMDRRegistration();
			if (ageAtReg != null) {
				tb03uData.setAgeAtTB03uRegistration(ageAtReg);
			}
			
			if (patient.getBirthdate() != null)
				tb03uData.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
			//SITE OF DISEASE (P/EP)
			Concept q = tf.getAnatomicalSite();
			
			if (q != null)
				tb03uData.setSiteOfDisease(q.getName(Context.getLocale()).getName());
			
			//SLD Register Number
			String reg2Number = tf.getSldRegisterNumber();
			
			tb03uData.setReg2Number(reg2Number);
			
			//REGISTRATION GROUP
			q = tf.getRegistrationGroup();
			
			if (q != null) {
				tb03uData.setRegGroup(q.getConceptId());
			}
			
			//MDR STATUS
			q = tf.getMdrStatus();
			
			if (q != null) {
				tb03uData.setMdrtbStatus(q.getName(Context.getLocale()).getName());
			}
			
			//MDR CONF DATE
			Date confDate = tf.getConfirmationDate();
			if (confDate != null)
				tb03uData.setMdrConfDate(sdf.format(confDate));
			
			//MDR TREATMENT REGIMEN
			q = tf.getPatientCategory();
			
			if (q != null)
				tb03uData.setTreatmentRegimen(q.getName(Context.getLocale()).getName());
			
			//DATE OF MDR TREATMENT START
			Date txStartDate = tf.getMdrTreatmentStartDate();
			if (txStartDate != null)
				tb03uData.setTb03uTreatmentStartDate(sdf.format(txStartDate));
			
			//TREATMENT LOCATION
			q = tf.getTxLocation();
			
			if (q != null) {
				tb03uData.setTreatmentLocation(q.getName(Context.getLocale()).getName());
			}
			
			//DST
			Dst firstDst = TB03uUtil.getDiagnosticDST(tf);
			
			if (firstDst != null) {
				if (firstDst.getDateCollected() != null)
					tb03uData.setDstCollectionDate(sdf.format(firstDst.getDateCollected()));
				if (firstDst.getResultDate() != null)
					tb03uData.setDstResultDate(sdf.format(firstDst.getResultDate()));
				List<DstResult> resList = firstDst.getResults();
				String drugName = null;
				String result = null;
				for (DstResult res : resList) {
					if (res.getDrug() != null) {
						drugName = res.getDrug().getShortestName(Context.getLocale(), false).toString();
						result = res.getResult().getName(Context.getLocale()).getName();
						tb03uData.getDstResults().put(drugName, result);
						//System.out.println(drugName + "-" + result + " | " + res.getResult());
						
					}
				}
			}
			
			XpertForm firstXpert = TB03uUtil.getFirstXpertForm(tf);
			if (firstXpert != null) {
				if (firstXpert.getMtbResult() != null)
					tb03uData.setXpertMTBResult(firstXpert.getMtbResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstXpert.getRifResult() != null)
					tb03uData.setXpertRIFResult(firstXpert.getRifResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstXpert.getEncounterDatetime() != null)
					tb03uData.setXpertTestDate(sdf.format(firstXpert.getEncounterDatetime()));
				
				tb03uData.setXpertTestNumber(firstXpert.getSpecimenId());
				
				Location loc = firstXpert.getLocation();
				if (loc != null) {
					tb03uData.setXpertLab(loc.getCountyDistrict());
				}
			}
			
			HAINForm firstHAIN = TB03uUtil.getFirstHAINForm(tf);
			if (firstHAIN != null) {
				if (firstHAIN.getMtbResult() != null)
					tb03uData.setHainMTBResult(firstHAIN.getMtbResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getRifResult() != null)
					tb03uData.setHainRIFResult(firstHAIN.getRifResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getInhResult() != null)
					tb03uData.setHainINHResult(firstHAIN.getInhResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getEncounterDatetime() != null)
					tb03uData.setHainTestDate(sdf.format(firstHAIN.getEncounterDatetime()));
				
				tb03uData.setHainTestNumber(firstHAIN.getSpecimenId());
				
				Location loc = firstHAIN.getLocation();
				if (loc != null) {
					tb03uData.setHainLab(loc.getName());
				}
			}
			
			HAIN2Form firstHAIN2 = TB03uUtil.getFirstHAIN2Form(tf);
			if (firstHAIN2 != null) {
				if (firstHAIN2.getMtbResult() != null)
					tb03uData.setHain2MTBResult(firstHAIN2.getMtbResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstHAIN2.getInjResult() != null)
					tb03uData.setHain2InjResult(firstHAIN2.getInjResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstHAIN2.getFqResult() != null)
					tb03uData.setHain2FqResult(firstHAIN2.getFqResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getEncounterDatetime() != null)
					tb03uData.setHain2TestDate(sdf.format(firstHAIN2.getEncounterDatetime()));
				
				tb03uData.setHain2TestNumber(firstHAIN2.getSpecimenId());
				
				Location loc = firstHAIN2.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
						tb03uData.setHain2Lab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
						tb03uData.setHain2Lab(loc.getCountyDistrict());
					}
				}
			}
			
			//DRUG RESISTANCE
			q = tf.getResistanceType();
			
			if (q != null) {
				tb03uData.setDrugResistance(q.getName(Context.getLocale()).getName());
			}
			
			//DIAGNOSTIC METHOD
			q = tf.getBasisForDiagnosis();
			
			if (q != null) {
				tb03uData.setDiagnosticMethod(q.getName(Context.getLocale()).getName());
			}
			
			//HIV TEST RESULT
			q = tf.getHivStatus();
			
			if (q != null) {
				tb03uData.setHivTestResult(q.getName(Context.getLocale()).getName());
			}
			
			//DATE OF HIV TEST
			Date hivTestDate = tf.getHivTestDate();
			if (hivTestDate != null)
				tb03uData.setHivTestDate(sdf.format(hivTestDate));
			
			//DATE OF ART START
			Date artStartDate = tf.getArtStartDate();
			if (artStartDate != null)
				tb03uData.setArtStartDate(sdf.format(artStartDate));
			
			//DATE OF CP START
			Date pctStartDate = tf.getPctStartDate();
			if (pctStartDate != null)
				tb03uData.setCpStartDate(sdf.format(pctStartDate));
			
			//FOLLOW-UP SMEARS
			//accordingly look for smears
			
			SmearForm followupSmear = TB03uUtil.getFollowupSmearForm(tf, 0);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth0SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth0SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 1);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth1SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth1SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 2);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth2SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth2SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 3);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth3SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth3SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 4);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth4SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth4SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 5);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth5SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth5SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 6);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth6SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth6SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 7);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth7SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth7SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 8);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth8SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth8SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 9);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth9SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth9SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 10);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth10SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth10SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 11);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth11SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth11SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 12);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth12SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth12SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 15);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth15SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth15SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 18);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth18SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth18SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 21);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth21SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth21SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 24);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth24SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth24SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 27);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth27SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth27SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 30);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth30SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth30SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 33);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth33SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth33SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 36);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth36SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth36SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			//follow CULTURES
			CultureForm followupCulture = TB03uUtil.getFollowupCultureForm(tf, 0);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth0CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth0CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 1);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth1CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth1CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 2);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth2CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth2CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 3);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth3CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth3CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 4);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth4CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth4CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 5);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth5CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth5CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 6);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth6CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth6CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 7);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth7CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth7CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 8);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth8CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth8CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 9);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth9CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth9CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 10);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth10CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth10CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 11);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth11CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth11CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 12);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth12CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth12CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 15);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth15CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth15CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 18);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth18CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth18CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 21);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth21CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth21CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 24);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth24CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth24CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 27);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth27CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth27CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 30);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth30CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth30CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 33);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth33CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth33CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 36);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth36CultureResult(followupCulture.getCultureResult().getName(Context.getLocale())
					        .getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth36CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			q = tf.getCauseOfDeath();
			
			if (q != null) {
				codId = q.getConceptId();
                tb03uData.setDiedOfTB(codId
                        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId()));
			}
			
			else
				tb03uData.setDiedOfTB(false);
			q = tf.getRelapsed();
			
			if (q != null) {
				tb03uData.setRelapsed(q.getName(Context.getLocale()).getName());
			}
			
			Integer relMonth = tf.getRelapseMonth();
			
			if (relMonth != null) {
				tb03uData.setRelapseMonth(relMonth);
			}
			
			q = tf.getTreatmentOutcome();
			if (q != null) {
				tb03uData.setTb03uTreatmentOutcome(q.getConceptId());
				
				Date txOutcomeDate = tf.getTreatmentOutcomeDate();
				if (txOutcomeDate != null) {
					tb03uData.setTb03uTreatmentOutcomeDate(sdf.format(txOutcomeDate));
				}
			}
			
			//NOTES
			String notes = tf.getClinicianNotes();
			
			if (notes != null)
				tb03uData.setNotes(notes);
			
			regimenConceptId = null;
			codId = null;
			q = null;
			
			patientSet.add(tb03uData);
			
		}
		
		Collections.sort(patientSet);
		Integer num = patientSet.size();
		model.addAttribute("num", num);
		model.addAttribute("patientSet", patientSet);
		
		// TO CHECK WHETHER REPORT IS CLOSED OR NOT
		Integer report_oblast = null;
		Integer report_quarter = null;
		Integer report_month = null;
		
		boolean reportStatus = Context.getService(MdrtbService.class).getReportArchived(oblastId, districtId, facilityId,
		    year, quarterInt, monthInt, "TB-03u", ReportType.MDRTB);
		
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
		model.addAttribute("reportDate", rdateSDF.format(new Date()));
		model.addAttribute("reportStatus", reportStatus);
		
		return "/module/mdrtb/reporting/tb03uSingleResults";
	}
}
