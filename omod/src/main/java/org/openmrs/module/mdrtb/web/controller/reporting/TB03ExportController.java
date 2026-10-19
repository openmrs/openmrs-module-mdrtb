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
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.reporting.custom.TB03Data;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
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

@Controller
public class TB03ExportController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/tb03")
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
		} else {
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
		
		return new ModelAndView("/module/mdrtb/reporting/tb03", model);
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/tb03")
	public static String doTB03(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) {
		
		System.out.println("PARAMS:" + oblastId + " " + districtId + " " + facilityId + " " + year + " " + quarter + " "
		        + month);
		
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
		ArrayList<TB03Data> patientSet = getTB03PatientSet(year, quarterInt, monthInt, monthInt, locList);
		
		Integer num = patientSet.size();
		model.addAttribute("num", num);
		model.addAttribute("patientSet", patientSet);
		model.addAttribute("locale", Context.getLocale().toString());
		
		boolean reportStatus = Context.getService(MdrtbService.class).getReportArchived(oblastId, districtId, facilityId,
		    year, quarterInt, monthInt, "TB-03", ReportType.DOTSTB);
		
		//System.out.println(reportStatus);
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
		return "/module/mdrtb/reporting/tb03Results";
	}
	
	public static ArrayList<TB03Data> getTB03PatientSet(Integer year, Integer quarter, Integer month, Integer month2, List<Location> locList) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month, month2);
		
		ArrayList<TB03Data> patientSet = new ArrayList<>();
		SimpleDateFormat sdf = Context.getDateFormat();
		
		Integer regimenConceptId = null;
		Integer codId = null;
		
		Concept reg1New = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_NEW);
		Concept reg1Rtx = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_RETREATMENT);
		
		for (TB03Form tf : tb03List) {
			TB03Data tb03Data = new TB03Data();
			tb03Data.setReg1New(Boolean.FALSE);
			tb03Data.setReg1Rtx(Boolean.FALSE);
			Patient patient = tf.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
			}
			tb03Data.setPatient(patient);
			
			//PATIENT IDENTIFIER
			System.out.println("Processing: " + tf.getPatient().toString());
			String identifier = TB03Util.getRegistrationNumber(tf);
			tb03Data.setIdentifier(identifier);
			
			//DATE OF TB03 REGISTRATION
			Date encDate = tf.getEncounterDatetime();
			tb03Data.setTb03RegistrationDate(sdf.format(encDate));

			// TB03 Location
			tb03Data.setTb03Location(tf.getEncounter().getLocation());
			
			//FORMATTED DATE OF BIRTH
			if (patient.getBirthdate() != null)
				tb03Data.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
			//AGE AT TB03 Registration
			Integer age = tf.getAgeAtTB03Registration();
			if (age != null)
				tb03Data.setAgeAtTB03Registration(age);
			
			//TX CENTER FOR IP
			Concept q = tf.getTreatmentSiteIP();
			
			if (q != null)
				tb03Data.setIntensivePhaseFacility(q.getName(Context.getLocale()).getName());
			
			//TX CENTER FOR CP
			q = tf.getTreatmentSiteCP();
			
			if (q != null)
				tb03Data.setContinuationPhaseFacility(q.getName(Context.getLocale()).getName());
			
			//DOTS TREATMENT REGIMEN
			q = tf.getPatientCategory();
			
			if (q != null)
				tb03Data.setTreatmentRegimen(q.getName(Context.getLocale()).getName());
			
			//DATE OF TB03 TREATMENT START
			Date txStart = tf.getTreatmentStartDate();
			if (txStart != null)
				tb03Data.setTb03TreatmentStartDate(sdf.format(txStart));
			
			//SITE OF DISEASE (P/EP)
			q = tf.getAnatomicalSite();
			
			if (q != null)
				tb03Data.setSiteOfDisease(q.getName(Context.getLocale()).getName());
			
			//HIV TEST RESULT
			q = tf.getHivStatus();
			
			if (q != null)
				tb03Data.setHivTestResult(q.getName(Context.getLocale()).getName());
			
			//DATE OF HIV TEST
			Date hivTestDate = tf.getHivTestDate();
			if (hivTestDate != null)
				tb03Data.setHivTestDate(sdf.format(hivTestDate));

            //DATE OF ART START
			Date artStartDate = tf.getArtStartDate();
			if (artStartDate != null)
				tb03Data.setArtStartDate(sdf.format(artStartDate));
			
			//DATE OF CP START
			Date pctStartDate = tf.getPctStartDate();
			if (pctStartDate != null)
				tb03Data.setArtStartDate(sdf.format(pctStartDate));
			
			//REGISTRATION GROUP
			q = tf.getRegistrationGroup();
			
			if (q != null) {
				tb03Data.setRegGroup(q.getConceptId());
			}
			
			//DIAGNOSTIC RESULTS
			
			//DIAGNOSTIC SMEAR
			
			SmearForm diagnosticSmear = TB03Util.getDiagnosticSmearForm(tf);
			if (diagnosticSmear != null) {
				if (diagnosticSmear.getSmearResult() != null) {
					tb03Data.setDiagnosticSmearResult(diagnosticSmear.getSmearResult().getName(Context.getLocale())
					        .getName());
				}
				if (diagnosticSmear.getEncounterDatetime() != null) {
					tb03Data.setDiagnosticSmearDate(sdf.format(diagnosticSmear.getEncounterDatetime()));
				}
				
				tb03Data.setDiagnosticSmearTestNumber(diagnosticSmear.getSpecimenId());
				
				Location loc = diagnosticSmear.getLocation();
				if (loc != null) {
					tb03Data.setDiagnosticSmearLab(loc.getName());
				}
			}
			
			//DIAGNOSTIC XPERT
			XpertForm firstXpert = TB03Util.getFirstXpertForm(tf);
			if (firstXpert != null) {
				if (firstXpert.getMtbResult() != null)
					tb03Data.setXpertMTBResult(firstXpert.getMtbResult().getName(Context.getLocale()).getName());
				if (firstXpert.getRifResult() != null)
					tb03Data.setXpertRIFResult(firstXpert.getRifResult().getName(Context.getLocale()).getName());
				if (firstXpert.getEncounterDatetime() != null)
					tb03Data.setXpertTestDate(sdf.format(firstXpert.getEncounterDatetime()));
				
				tb03Data.setXpertTestNumber(firstXpert.getSpecimenId());
				
				Location loc = firstXpert.getLocation();
				if (loc != null) {
					if (loc.getName() != null && !loc.getName().isEmpty()) {
						tb03Data.setXpertLab(loc.getName());
					}
					
					else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
						tb03Data.setXpertLab(loc.getCountyDistrict());
					}
				}
			}
			
			//DIAGNOSTIC HAIN
			HAINForm firstHAIN = TB03Util.getFirstHAINForm(tf);
			if (firstHAIN != null) {
				if (firstHAIN.getMtbResult() != null)
					tb03Data.setHainMTBResult(firstHAIN.getMtbResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getRifResult() != null)
					tb03Data.setHainRIFResult(firstHAIN.getRifResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getInhResult() != null)
					tb03Data.setHainINHResult(firstHAIN.getInhResult().getName(Context.getLocale()).getName());
				if (firstHAIN.getEncounterDatetime() != null)
					tb03Data.setHainTestDate(sdf.format(firstHAIN.getEncounterDatetime()));
				
				tb03Data.setHainTestNumber(firstHAIN.getSpecimenId());
				
				Location loc = firstHAIN.getLocation();
				if (loc != null) {
					tb03Data.setHainLab(loc.getName());
				}
			}
			
			//HAIN2
			HAIN2Form firstHAIN2 = TB03Util.getFirstHAIN2Form(tf);
			if (firstHAIN2 != null) {
				if (firstHAIN2.getMtbResult() != null)
					tb03Data.setHain2MTBResult(firstHAIN2.getMtbResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getInjResult() != null)
					tb03Data.setHain2InjResult(firstHAIN2.getInjResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getFqResult() != null)
					tb03Data.setHain2FqResult(firstHAIN2.getFqResult().getName(Context.getLocale()).getName());
				if (firstHAIN2.getEncounterDatetime() != null)
					tb03Data.setHain2TestDate(sdf.format(firstHAIN2.getEncounterDatetime()));
				
				tb03Data.setHain2TestNumber(firstHAIN2.getSpecimenId());
				
				Location loc = firstHAIN2.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
						tb03Data.setHain2Lab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
						tb03Data.setHain2Lab(loc.getCountyDistrict());
					}
				}
			}
			
			//DIAGNOSTIC CULTURE
			CultureForm diagnosticCulture = TB03Util.getDiagnosticCultureForm(tf);
			if (diagnosticCulture != null) {
				if (diagnosticCulture.getCultureResult() != null)
					tb03Data.setCultureResult(diagnosticCulture.getCultureResult().getName(Context.getLocale()).getName());
				if (diagnosticCulture.getEncounterDatetime() != null)
					tb03Data.setCultureTestDate(sdf.format(diagnosticCulture.getEncounterDatetime()));
				tb03Data.setCultureTestNumber(diagnosticCulture.getSpecimenId());
				
				Location loc = diagnosticCulture.getLocation();
				if (loc != null) {
					tb03Data.setCultureLab(loc.getName());
				}
			}
			
			//DST
			Dst firstDst = TB03Util.getDiagnosticDST(tf);
			
			if (firstDst != null) {
				if (firstDst.getDateCollected() != null)
					tb03Data.setDstCollectionDate(sdf.format(firstDst.getDateCollected()));
				if (firstDst.getResultDate() != null)
					tb03Data.setDstResultDate(sdf.format(firstDst.getResultDate()));
				List<DstResult> resList = firstDst.getResults();
				String drugName = null;
				String result = null;
				for (DstResult res : resList) {
					if (res.getDrug() != null) {
						drugName = res.getDrug().getShortestName(Context.getLocale(), false).getName();
						result = res.getResult().getShortestName(Context.getLocale(), false).getName();
						tb03Data.getDstResults().put(drugName, result);
					}
				}
			}
			
			//DRUG RESISTANCE
			
			q = tf.getResistanceType();
			
			if (q != null)
				tb03Data.setDrugResistance(q.getName(Context.getLocale()).getName());
			
			//FOLLOW-UP SMEARS
			
			//first check patient regimen
			// Smear followupSmear = null;
			SmearForm followupSmear = null;
			q = tf.getPatientCategory();//
			
			if (q != null)
				regimenConceptId = q.getConceptId();
			
			//accordingly look for smears
			if (regimenConceptId != null) {
				if (regimenConceptId.equals(reg1New.getConceptId())) {
					
					tb03Data.setReg1New(Boolean.TRUE);
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 2);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth2SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth2SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth2TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth2TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth2TestLab(loc.getCountyDistrict());
							}
						}
						
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 3);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth3SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth3SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth3TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth3TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth3TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 5);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth5SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth5SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth5TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth5TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth5TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 6);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth6SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth6SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth6TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth6TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth6TestLab(loc.getCountyDistrict());
							}
						}
					}
				}
				
				else if (regimenConceptId.equals(reg1Rtx.getConceptId())) {
					tb03Data.setReg1Rtx(Boolean.TRUE);
					followupSmear = TB03Util.getFollowupSmearForm(tf, 3);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth3SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth3SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth3TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth3TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth3TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 4);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth4SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth4SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth4TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth4TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth4TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 5);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth5SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth5SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth5TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth5TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth5TestLab(loc.getCountyDistrict());
							}
						}
						
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 8);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth8SmearResult(followupSmear.getSmearResult().getName(Context.getLocale())
							        .getName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth8SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth8TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getAddress6() != null && !loc.getAddress6().isEmpty()) {
								tb03Data.setMonth8TestLab(loc.getAddress6());
							}
							
							else if (loc.getCountyDistrict() != null && !loc.getCountyDistrict().isEmpty()) {
								tb03Data.setMonth8TestLab(loc.getCountyDistrict());
							}
						}
					}
				}
			}
			
			//TX OUTCOME
			//CHECK CAUSE OF DEATH
			
			q = tf.getCauseOfDeath();//Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH);
			
			if (q != null) {
				codId = q.getConceptId();
                tb03Data.setDiedOfTB(codId
                        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId()));
			}
			
			else
				tb03Data.setDiedOfTB(false);
			
			q = tf.getTreatmentOutcome();
			if (q != null) {
				tb03Data.setTb03TreatmentOutcome(q.getConceptId());
				
				Date txOutcomeDate = tf.getTreatmentOutcomeDate();
				if (txOutcomeDate != null) {
					tb03Data.setTb03TreatmentOutcomeDate(sdf.format(txOutcomeDate));
				}
			}
			
			//NOTES
			
			String notes = tf.getClinicianNotes();
			
			if (notes != null)
				tb03Data.setNotes(notes);
			
			patientSet.add(tb03Data);
			
			q = null;
			
		}
		
		Collections.sort(patientSet);
		return patientSet;
	}
}
