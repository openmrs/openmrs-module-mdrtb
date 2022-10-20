package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.reporting.custom.TB03Data;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
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

public class TB03SingleExportController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/tb03Single")
	public ModelAndView showRegimenOptions(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "yearSelected", required = false) Integer year,
	        @RequestParam(value = "quarterSelected", required = false) String quarter,
	        @RequestParam(value = "monthSelected", required = false) String month, ModelMap model) {
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getOblasts();
			model.addAttribute("oblasts", oblasts);
		}
		
		else if (district == null) {
			
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
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
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		} else {
			
			/*
			* if oblast is dushanbe, return both districts and facilities
			*/
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
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
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilities(Integer.parseInt(district));
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
		List<Region> oblasts = Context.getService(MdrtbService.class).getOblasts();
		//drugSets =  ms.getMdrtbDrugs();
		
		
		
		model.addAttribute("locations", locations);
		model.addAttribute("oblasts", oblasts);*/
		return new ModelAndView("/module/mdrtb/reporting/tb03Single", model);
		
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/tb03Single")
	public static String doTB03(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		System.out.println("---POST-----");
		System.out.println(
		    "PARAMS:" + oblastId + " " + districtId + " " + facilityId + " " + year + " " + quarter + " " + month);
		
		ArrayList<Location> locList = null;
		if (oblastId != null) {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		
		ArrayList<TB03Data> patientSet = new ArrayList<TB03Data>();
		SimpleDateFormat sdf = Context.getDateFormat();
		SimpleDateFormat rdateSDF = Context.getDateTimeFormat();
		
		Integer regimenConceptId = null;
		Integer codId = null;
		//List<Obs> obsList = null;
		
		Concept reg1New = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_NEW);
		Concept reg1Rtx = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_RETREATMENT);
		
		for (TB03Form tf : tb03List) {
			
			TB03Data tb03Data = new TB03Data();
			tb03Data.setReg1New(Boolean.FALSE);
			tb03Data.setReg1Rtx(Boolean.FALSE);
			
			Patient patient = tf.getPatient();
			if (patient == null || patient.isVoided()) {
				continue;
				
			}
			tb03Data.setPatient(patient);
			
			//PATIENT IDENTIFIER
			String identifier = TB03Util.getRegistrationNumber(tf);
			tb03Data.setIdentifier(identifier);
			
			//DATE OF TB03 REGISTRATION
			Date encDate = tf.getEncounterDatetime();
			tb03Data.setTb03RegistrationDate(sdf.format(encDate));
			
			//FORMATTED DATE OF BIRTH
			if (patient.getBirthdate() != null)
				tb03Data.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
			//AGE AT TB03 Registration
			Integer age = tf.getAgeAtTB03Registration();//Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AGE_AT_DOTS_REGISTRATION);
			if (age != null)
				tb03Data.setAgeAtTB03Registration(age);
			
			//TX CENTER FOR IP
			Concept q = tf.getTreatmentSiteIP();
			
			if (q != null)
				tb03Data.setIntensivePhaseFacility(q.getName().getName());
			
			//TX CENTER FOR CP
			q = tf.getTreatmentSiteCP();
			
			if (q != null)
				tb03Data.setContinuationPhaseFacility(q.getName().getName());
			
			//DOTS TREATMENT REGIMEN
			q = tf.getPatientCategory();
			
			if (q != null)
				tb03Data.setTreatmentRegimen(q.getName().getName());
			
			//DATE OF TB03 TREATMENT START
			Date txStart = tf.getTreatmentStartDate();
			if (txStart != null)
				tb03Data.setTb03TreatmentStartDate(sdf.format(txStart));
			
			//SITE OF DISEASE (P/EP)
			q = tf.getAnatomicalSite();
			
			if (q != null)
				tb03Data.setSiteOfDisease(q.getName().getName());
			
			//HIV TEST RESULT
			q = tf.getHivStatus();
			
			if (q != null)
				tb03Data.setHivTestResult(q.getName().getName());
			
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
			if (q == null) {
				Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP), tf.getEncounter());
				q = obs.getValueCoded();
			}
			if (q != null) {
				tb03Data.setRegGroup(q.getConceptId());
			}
			
			//DIAGNOSTIC RESULTS
			
			//DIAGNOSTIC SMEAR
			SmearForm diagnosticSmear = TB03Util.getDiagnosticSmearForm(tf);
			if (diagnosticSmear != null) {
				if (diagnosticSmear.getSmearResult() != null) {
					tb03Data.setDiagnosticSmearResult(diagnosticSmear.getSmearResult().getName().getShortName());
				}
				if (diagnosticSmear.getEncounterDatetime() != null) {
					tb03Data.setDiagnosticSmearDate(sdf.format(diagnosticSmear.getEncounterDatetime()));
				}
				
				tb03Data.setDiagnosticSmearTestNumber(diagnosticSmear.getSpecimenId());
				
				Location loc = diagnosticSmear.getLocation();
				if (loc != null) {
					if (loc.getRegion() != null && loc.getRegion().length() != 0) {
						tb03Data.setDiagnosticSmearLab(loc.getRegion());
					}
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03Data.setDiagnosticSmearLab(loc.getCountyDistrict());
					}
				}
				
				System.out.println(tb03Data.getDiagnosticSmearResult() + "," + tb03Data.getDiagnosticSmearDate() + ","
				        + tb03Data.getDiagnosticSmearTestNumber());
			}
			
			XpertForm firstXpert = TB03Util.getFirstXpertForm(tf);
			if (firstXpert != null) {
				if (firstXpert.getMtbResult() != null)
					tb03Data.setXpertMTBResult(firstXpert.getMtbResult().getName().getShortName());
				if (firstXpert.getRifResult() != null)
					tb03Data.setXpertRIFResult(firstXpert.getRifResult().getName().getShortName());
				if (firstXpert.getEncounterDatetime() != null)
					tb03Data.setXpertTestDate(sdf.format(firstXpert.getEncounterDatetime()));
				
				tb03Data.setXpertTestNumber(firstXpert.getSpecimenId());
				
				Location loc = firstXpert.getLocation();
				if (loc != null) {
					if (loc.getRegion() != null && loc.getRegion().length() != 0) {
						tb03Data.setXpertLab(loc.getRegion());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03Data.setXpertLab(loc.getCountyDistrict());
					}
				}
			}
			
			HAINForm firstHAIN = TB03Util.getFirstHAINForm(tf);
			if (firstHAIN != null) {
				if (firstHAIN.getMtbResult() != null)
					tb03Data.setHainMTBResult(firstHAIN.getMtbResult().getName().getShortName());
				if (firstHAIN.getRifResult() != null)
					tb03Data.setHainRIFResult(firstHAIN.getRifResult().getName().getShortName());
				if (firstHAIN.getInhResult() != null)
					tb03Data.setHainINHResult(firstHAIN.getInhResult().getName().getShortName());
				if (firstHAIN.getEncounterDatetime() != null)
					tb03Data.setHainTestDate(sdf.format(firstHAIN.getEncounterDatetime()));
				
				tb03Data.setHainTestNumber(firstHAIN.getSpecimenId());
				
				Location loc = firstHAIN.getLocation();
				if (loc != null) {
					if (loc.getRegion() != null && loc.getRegion().length() != 0) {
						tb03Data.setHainLab(loc.getRegion());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03Data.setHainLab(loc.getCountyDistrict());
					}
				}
			}
			
			HAIN2Form firstHAIN2 = TB03Util.getFirstHAIN2Form(tf);
			if (firstHAIN2 != null) {
				if (firstHAIN2.getMtbResult() != null)
					tb03Data.setHain2MTBResult(firstHAIN2.getMtbResult().getName().getShortName());
				if (firstHAIN2.getFqResult() != null)
					tb03Data.setHain2FqResult(firstHAIN2.getFqResult().getName().getShortName());
				if (firstHAIN2.getInjResult() != null)
					tb03Data.setHain2InjResult(firstHAIN2.getInjResult().getName().getShortName());
				if (firstHAIN2.getEncounterDatetime() != null)
					tb03Data.setHain2TestDate(sdf.format(firstHAIN2.getEncounterDatetime()));
				
				tb03Data.setHain2TestNumber(firstHAIN2.getSpecimenId());
				Location loc = firstHAIN2.getLocation();
				if (loc != null) {
					if (loc.getRegion() != null && loc.getRegion().length() != 0) {
						tb03Data.setHain2Lab(loc.getRegion());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03Data.setHain2Lab(loc.getCountyDistrict());
					}
				}
			}
			
			CultureForm diagnosticCulture = TB03Util.getDiagnosticCultureForm(tf);
			if (diagnosticCulture != null) {
				if (diagnosticCulture.getCultureResult() != null)
					tb03Data.setCultureResult(diagnosticCulture.getCultureResult().getName().getShortName());
				if (diagnosticCulture.getEncounterDatetime() != null)
					tb03Data.setCultureTestDate(sdf.format(diagnosticCulture.getEncounterDatetime()));
				tb03Data.setCultureTestNumber(diagnosticCulture.getSpecimenId());
				
				Location loc = diagnosticCulture.getLocation();
				if (loc != null) {
					if (loc.getRegion() != null && loc.getRegion().length() != 0) {
						tb03Data.setCultureLab(loc.getRegion());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03Data.setCultureLab(loc.getCountyDistrict());
					}
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
						drugName = res.getDrug().getShortestName(Context.getLocale(), false).toString();
						result = res.getResult().getName().getShortName();
						tb03Data.getDstResults().put(drugName, result);
						
					}
				}
				System.out.println("-------");
			}
			
			//DRUG RESISTANCE
			
			q = tf.getResistanceType();//Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
			
			if (q != null)
				tb03Data.setDrugResistance(q.getName().getShortName());
			
			//FOLLOW-UP SMEARS
			
			//first check patient regimen
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
							tb03Data.setMonth2SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth2SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth2TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth2TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
								tb03Data.setMonth2TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 3);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth3SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth3SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth3TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth3TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
								tb03Data.setMonth3TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 5);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth5SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth5SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth5TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth5TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
								tb03Data.setMonth5TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 6);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth6SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth6SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth6TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth6TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
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
							tb03Data.setMonth3SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth3SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth3TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth3TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
								tb03Data.setMonth3TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 4);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth4SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth4SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth4TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth4TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
								tb03Data.setMonth4TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 5);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth5SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth5SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth5TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth5TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
								tb03Data.setMonth5TestLab(loc.getCountyDistrict());
							}
						}
					}
					
					followupSmear = TB03Util.getFollowupSmearForm(tf, 8);
					if (followupSmear != null) {
						if (followupSmear.getSmearResult() != null)
							tb03Data.setMonth8SmearResult(followupSmear.getSmearResult().getName().getShortName());
						if (followupSmear.getEncounterDatetime() != null)
							tb03Data.setMonth8SmearDate(sdf.format(followupSmear.getEncounterDatetime()));
						
						tb03Data.setMonth8TestNumber(followupSmear.getSpecimenId());
						Location loc = followupSmear.getLocation();
						if (loc != null) {
							if (loc.getRegion() != null && loc.getRegion().length() != 0) {
								tb03Data.setMonth8TestLab(loc.getRegion());
							}
							
							else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
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
				if (codId
				        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId()))
					tb03Data.setDiedOfTB(true);
				else
					tb03Data.setDiedOfTB(false);
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
		
		Integer num = patientSet.size();
		model.addAttribute("num", num);
		model.addAttribute("patientSet", patientSet);
		model.addAttribute("locale", Context.getLocale().toString());
		
		boolean reportStatus;// = Context.getService(MdrtbService.class).readReportStatus(report_oblast, location.getId(), year, report_quarter, report_month, "TB 03");
		
		reportStatus = Context.getService(MdrtbService.class).readReportStatus(oblastId, districtId, facilityId, year,
		    quarter, month, "TB-03", "DOTSTB");
		
		System.out.println(reportStatus);
		model.addAttribute("oblast", oblastId);
		model.addAttribute("district", districtId);
		model.addAttribute("facility", facilityId);
		model.addAttribute("year", year);
		if (month != null && month.length() != 0)
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if (quarter != null && quarter.length() != 0)
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
		model.addAttribute("reportDate", rdateSDF.format(new Date()));
		model.addAttribute("reportStatus", reportStatus);
		return "/module/mdrtb/reporting/tb03SingleResults";
	}
	
}
