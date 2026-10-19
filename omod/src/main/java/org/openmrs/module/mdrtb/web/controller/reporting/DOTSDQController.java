package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
import org.openmrs.module.mdrtb.form.custom.TransferOutForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
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
public class DOTSDQController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/dotsdq")
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
		
		return new ModelAndView("/module/mdrtb/reporting/dotsdq", model);
		
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/dotsdq")
	public static String doDQ(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) {
		
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		
		List<Location> locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		
		Map<String, Object> metrics = getDOTSQualityMetrics(year, quarterInt, monthInt, monthInt, locList);
		for (String key : metrics.keySet()) {
			model.addAttribute(key, metrics.get(key));
		}
		
		String oName = null;
		Region obl = Context.getService(MdrtbService.class).getRegion(oblastId);
		if (obl != null)
			oName = obl.getName();
		String dName = null;
		if (districtId != null) {
			District dist = Context.getService(MdrtbService.class).getDistrict(districtId);
			if (dist != null)
				dName = dist.getName();
		}
		String fName = null;
		if (facilityId != null) {
			Facility fac = Context.getService(MdrtbService.class).getFacility(facilityId);
			if (fac != null)
				fName = fac.getName();
		}
		model.addAttribute("oName", oName);
		model.addAttribute("dName", dName);
		model.addAttribute("fName", fName);
		model.addAttribute("oblast", oblastId);
		model.addAttribute("facility", facilityId);
		model.addAttribute("district", districtId);
		model.addAttribute("year", year);
		if (month != null && !month.isEmpty())
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if (quarter != null && !quarter.isEmpty())
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
		
		// TO CHECK WHETHER REPORT IS CLOSED OR NOT
		boolean reportStatus = Context.getService(MdrtbService.class).getReportArchived(oblastId, districtId, facilityId,
		    year, quarterInt, monthInt, "DOTSDQ", ReportType.DOTSTB);
		model.addAttribute("locale", Context.getLocale().toString());
		model.addAttribute("reportStatus", reportStatus);
		model.addAttribute("reportDate", Context.getDateTimeFormat().format(new Date()));
		return "/module/mdrtb/reporting/dotsdqResults";
	}
	
	public static Map<String, Object> getDOTSQualityMetrics(Integer year, Integer quarter, Integer month, Integer month2,
	        List<Location> locList) {
		Map<String, Object> map = new HashMap<>();
		List<DQItem> missingAge = new ArrayList<>();
		List<DQItem> missingPatientGroup = new ArrayList<>();
		List<DQItem> noForm89 = new ArrayList<>();
		List<DQItem> invalidForm89 = new ArrayList<>();
		List<DQItem> missingDiagnosticTests = new ArrayList<>();
		List<DQItem> notStartedTreatment = new ArrayList<>();
		List<DQItem> missingOutcomes = new ArrayList<>();
		List<DQItem> noDOTSId = new ArrayList<>();
		List<DQItem> noSite = new ArrayList<>();
		List<DQItem> noTifAfterTransferOut = new ArrayList<>();
		List<DQItem> noTofBeforeTransferIn = new ArrayList<>();
		List<DQItem> duplicateTB03 = new ArrayList<>();
		List<DQItem> duplicateForm89 = new ArrayList<>();
		List<DQItem> unlinkedTB03 = new ArrayList<>();
		List<Patient> errList = new ArrayList<>();
		
		Boolean errorFlag = Boolean.FALSE;
		Integer errorCount = 0;
		
		Date treatmentStartDate = null;
		Calendar tCal = null;
		Calendar nowCal = null;
		long timeDiff = 0;
		long diffInWeeks = 0;
		
		SmearForm diagnosticSmear = null;
		XpertForm firstXpert = null;
		HAINForm firstHAIN = null;
		CultureForm diagnosticCulture = null;
		Boolean eptb = Boolean.FALSE;
		Boolean child = Boolean.FALSE;
		DQItem dqi = null;
		
		Integer eptbConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB)
		        .getConceptId();
		
		List<TransferOutForm> tofList = Context.getService(MdrtbService.class).getTransferOutFormsFilled(locList, year,
		    quarter, month, month2);
		List<TransferInForm> tifList = Context.getService(MdrtbService.class).getTransferInFormsFilled(locList, year,
		    quarter, month, month2);
		List<TransferInForm> allTifs = null;
		HashMap<Integer, Integer> dupMap = new HashMap<>();
		HashMap<Integer, Integer> f89DupMap = new HashMap<>();
		
		int neww = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW).getId();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month, month2);
		
		dateMap.get("startDate");
		dateMap.get("endDate");
		Integer countNum = 0;
		
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month, month);
		for (TB03Form tf : tb03List) {
			//INIT
			treatmentStartDate = null;
			tCal = null;
			nowCal = null;
			timeDiff = 0;
			diffInWeeks = 0;
			diagnosticSmear = null;
			firstXpert = null;
			firstHAIN = null;
			diagnosticCulture = null;
			// patientList.clear();
			errorFlag = Boolean.FALSE;
			eptb = Boolean.FALSE;
			child = Boolean.FALSE;
			dqi = new DQItem();
			Patient patient = tf.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
			}
			dqi.setPatient(patient);
			dqi.setDateOfBirth(Context.getDateFormat().format(patient.getBirthdate()));
			
			//DUPLICATE TB03
			Integer patientProgramId = null;
			patientProgramId = tf.getPatientProgramId();
			Boolean found = Boolean.FALSE;
			if (patientProgramId != null && !dupMap.containsKey(patientProgramId)) {
				List<TB03Form> dupList = Context.getService(MdrtbService.class).getTB03FormsForProgram(patient,
				    patientProgramId);
				if (dupList != null) {
					if (dupList.size() > 1) {
						for (TB03Form form : dupList) {
							if (form.getPatientProgramId().intValue() == patientProgramId.intValue()) {
								dqi.addTb03Link(form.getLink());
								found = Boolean.TRUE;
								System.out.println("FOUND:" + tf.getPatientProgramId());
							}
						}
					}
				}
				if (found) {
					dupMap.put(patientProgramId, patientProgramId);
					errorFlag = Boolean.TRUE;
					duplicateTB03.add(dqi);
				}
			}
			
			else { //UNLINKED TB03
				String link = "";
				dqi.addTb03Link(link);
				unlinkedTB03.add(dqi);
				errorFlag = Boolean.TRUE;
			}
			
			//duplicate FORM89
			found = Boolean.FALSE;
			if (patientProgramId != null && !f89DupMap.containsKey(patientProgramId)) {
				List<Form89> formList = Context.getService(MdrtbService.class).getForm89FormsForProgram(patient,
				    patientProgramId);
				if (formList != null) {
					if (formList.size() > 1) {
						for (Form89 form : formList) {
							dqi.addForm89Link(form.getLink());
							found = Boolean.TRUE;
						}
					}
				}
				if (found) {
					f89DupMap.put(patientProgramId, patientProgramId);
					errorFlag = Boolean.TRUE;
					duplicateForm89.add(dqi);
				}
			}
			
			//Patient not new but has form89
			if (tf.getRegistrationGroup() != null) {
				if (tf.getRegistrationGroup().getConceptId() != neww) {
					List<Form89> formList = Context.getService(MdrtbService.class).getForm89FormsForProgram(patient,
					    patientProgramId);
					if (formList != null && !formList.isEmpty()) {
						errorFlag = Boolean.TRUE;
						invalidForm89.add(dqi);
					}
				}
			}
			if (tf.getAgeAtTB03Registration() == null) {
				missingAge.add(dqi);
				errorFlag = Boolean.TRUE;
			}
			//MISSING REGISTRATION GROUP
			if (tf.getRegistrationGroup() == null) {
				missingPatientGroup.add(dqi);
				errorFlag = Boolean.TRUE;
			}
			else if (tf.getRegistrationGroup().getId() == (Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.NEW).getId().intValue())) {
				List<Form89> f89 = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
				    tf.getPatient(), null, tf.getPatientProgramId(), year, quarter, month, month2);
				if (f89 == null || f89.isEmpty()) {
					noForm89.add(dqi);
					errorFlag = Boolean.TRUE;
				}
			}
			
			//NOT STARTED TREATMENT
			if (tf.getTreatmentStartDate() == null) {
				notStartedTreatment.add(dqi);
				errorFlag = Boolean.TRUE;
			} else {
				//MISSING OUTCOMES
				treatmentStartDate = tf.getTreatmentStartDate();
				tCal = new GregorianCalendar();
				tCal.setTime(treatmentStartDate);
				nowCal = new GregorianCalendar();
				timeDiff = nowCal.getTimeInMillis() - tCal.getTimeInMillis();
				diffInWeeks = MdrtbUtil.timeDiffInWeeks(timeDiff);
				if (diffInWeeks > 32) {
					if (tf.getTreatmentOutcome() == null) {
						missingOutcomes.add(dqi);
						errorFlag = Boolean.TRUE;
					}
				}
			}
			
			//NO SITE
			if (tf.getAnatomicalSite() == null) {
				noSite.add(dqi);
				errorFlag = Boolean.TRUE;
			} else {
				if (tf.getAnatomicalSite().getConceptId().intValue() == eptbConcept.intValue()) {
					eptb = Boolean.TRUE;
				}
			}
			if (tf.getAgeAtTB03Registration() != null) {
				Integer age = tf.getAgeAtTB03Registration();
				
				if (age > 14)
					child = Boolean.FALSE;
				else
					child = Boolean.TRUE;
			}
			
			//MISSING DIAGNOSTIC TESTS
			
			diagnosticSmear = TB03Util.getDiagnosticSmearForm(tf);
			firstXpert = TB03Util.getFirstXpertForm(tf);
			firstHAIN = TB03Util.getFirstHAINForm(tf);
			diagnosticCulture = TB03Util.getDiagnosticCultureForm(tf);
			if (diagnosticSmear == null && diagnosticCulture == null && firstXpert == null && firstHAIN == null
			        && eptb == Boolean.FALSE && child == Boolean.FALSE) {
				missingDiagnosticTests.add(dqi);
				errorFlag = Boolean.TRUE;
			}
			
			//MISSING DOTS ID
			if (tf.getPatientProgramId() != null) {
				
				TbPatientProgram program = Context.getService(MdrtbService.class).getTbPatientProgram(
				    tf.getPatientProgramId());
				if (program == null
				        || Context.getService(MdrtbService.class).getPatientProgramIdentifier(program.getPatientProgram()) == null) {
					noDOTSId.add(dqi);
					errorFlag = Boolean.TRUE;
				}
			}
			if (errorFlag) {
				errorCount++;
				errList.add(patient);
			}
			countNum++;
		}
		
		//PATIENT_TRANSFERRED_IN OUT BUT NO PATIENT_TRANSFERRED_IN IN
		//get latest transfer out with any of these locations for any patient
		//if no transferIn in list entered after that date for patient add error
		Boolean foundFlag = Boolean.FALSE;
		
		for (TransferOutForm tof : tofList) {
			Location tofLoc = tof.getLocation();
			Date tofDate = tof.getEncounterDatetime();
			Patient tofPatient = tof.getPatient();
			dqi = new DQItem();
			Patient patient = tof.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
			}
			// patientList.add(patient);
			dqi.setPatient(patient);
			dqi.setLocName(tofLoc.getDisplayString());
			dqi.setDateOfBirth(Context.getDateFormat().format(patient.getBirthdate()));
			foundFlag = Boolean.FALSE;
			errorFlag = Boolean.FALSE;
			allTifs = Context.getService(MdrtbService.class).getTransferInFormsFilledForPatient(patient);
			for (TransferInForm tif : allTifs) {
				if (tofLoc.equals(tif.getLocation()) && tofPatient.equals(tif.getPatient())) {
					if (tif.getEncounterDatetime().after(tofDate)) {
						foundFlag = Boolean.TRUE;
						break;
					}
				}
			}
			if (!foundFlag) {
				if (!errList.contains(patient)) {
					errorCount++;
					errList.add(patient);
				}
				noTifAfterTransferOut.add(dqi);
			}
		}
		
		//PATIENT_TRANSFERRED_IN In  BUT NO PATIENT_TRANSFERRED_IN Out
		//get latest transfer out with any of these locations for any patient
		//if no transferIn in list entered after that date for patient add error
		foundFlag = Boolean.FALSE;
		
		for (TransferInForm tif : tifList) {
			Location tifLoc = tif.getLocation();
			Date tifDate = tif.getEncounterDatetime();
			Patient tifPatient = tif.getPatient();
			dqi = new DQItem();
			Patient patient = tif.getPatient();
			
			if (patient == null || patient.getVoided()) {
				continue;
			}
			// patientList.add(patient);
			dqi.setPatient(patient);
			dqi.setLocName(tifLoc.getDisplayString());
			dqi.setDateOfBirth(Context.getDateFormat().format(patient.getBirthdate()));
			foundFlag = Boolean.FALSE;
			errorFlag = Boolean.FALSE;
			Context.getService(MdrtbService.class).getTransferOutFormsFilledForPatient(patient);
			for (TransferOutForm tof : tofList) {
				if (tifLoc.equals(tof.getLocation()) && tifPatient.equals(tof.getPatient())) {
					if (tof.getEncounterDatetime().before(tifDate)) {
						foundFlag = Boolean.TRUE;
						break;
					}
				}
			}
			if (!foundFlag) {
				if (!errList.contains(patient)) {
					errorCount++;
					errList.add(patient);
				}
				noTofBeforeTransferIn.add(dqi);
			}
		}
		
		Integer num = countNum;
		Integer errorPercentage = null;
		if (num == 0)
			errorPercentage = 0;
		else
			errorPercentage = (errorCount * 100) / num;
		map.put("num", num);
		map.put("duplicateTB03", duplicateTB03);
		map.put("duplicateForm89", duplicateForm89);
		map.put("unlinkedTB03", unlinkedTB03);
		map.put("missingForm89", noForm89);
		map.put("invalidForm89", invalidForm89);
		map.put("missingAge", missingAge);
		map.put("missingPatientGroup", missingPatientGroup);
		map.put("missingDiagnosticTests", missingDiagnosticTests);
		map.put("notStartedTreatment", notStartedTreatment);
		map.put("missingOutcomes", missingOutcomes);
		map.put("noDOTSId", noDOTSId);
		map.put("noSite", noSite);
		map.put("noTrasnferIn", noTifAfterTransferOut);
		map.put("noTransferOut", noTofBeforeTransferIn);
		map.put("errorCount", errorCount);
		map.put("errorPercentage", errorPercentage + "%");
		return map;
	}
}
