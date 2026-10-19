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
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
import org.openmrs.module.mdrtb.specimen.DstImpl;
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
import org.openmrs.module.mdrtb.api.MessagePropertyService;

@SuppressWarnings("null")
@Controller
public class PatientListContoller {
	
	private static final String RETURN_LINK = "/module/mdrtb/reporting/patientListsResults";
	
	static final String OPEN_TR = "<tr>";
	
	static final String CLOSE_TR = "</tr>";
	
	static final String CLOSE_TD = "</td>";
	
	static final String ALIGN_LEFT_TAG = "<td style=\"padding:0.25rem;\" align=\"left\">";
	
	static final String OPEN_CLOSE_TD = "<td></td>";
	
	static final String OPEN_H4 = "<h4>";
	
	static final String CLOSE_H4 = "</h4>";
	
	static final String OPEN_TABLE = "<table border=\"1\">";
	
	static final String CLOSE_TABLE = "</table>";
	
	static final String BR_TAG = "<br/>";
	
	static final String OBLAST = "oblast";
	
	static final String DISTRICTS = "districts";
	
	static final String OBLASTS2 = "oblasts";
	
	static final String FACILITIES = "facilities";
	
	static final String OBLAST_SELECTED = "oblastSelected";
	
	static final String DISTRICT_SELECTED = "districtSelected";
	
	static final String DISTRICT = "district";
	
	static final String FACILITY = "facility";
	
	static final String YEAR = "year";
	
	static final String MONTH = "month";
	
	static final String QUARTER = "quarter";
	
	static final String MDRTB_SERIAL_NUMBER = "mdrtb.serialNumber";
	
	static final String MDRTB_TB_03_REGISTRATION_NUMBER = "mdrtb.tb03.registrationNumber";
	
	static final String MDRTB_TB_03_DATE_OF_REGISTRATION = "mdrtb.tb03.dateOfRegistration";
	
	static final String MDRTB_TB_03_TREATMENT_START_DATE = "mdrtb.tb03.treatmentStartDate";
	
	static final String MDRTB_TB_03_TREATMENT_SITE_IP = "mdrtb.tb03.treatmentSiteIP";
	
	static final String MDRTB_TB_03_NAME = "mdrtb.tb03.name";
	
	static final String MDRTB_TB_03_GENDER = "mdrtb.tb03.gender";
	
	static final String MDRTB_TB_03_DATE_OF_BIRTH = "mdrtb.tb03.dateOfBirth";
	
	static final String MDRTB_TB_03_AGE_AT_REGISTRATION = "mdrtb.tb03.ageAtRegistration";
	
	static final String MDRTB_TB_03_TB_LOCALIZATION = "mdrtb.tb03.tbLocalization";
	
	static final String MDRTB_LISTS_CASE_DEFINITION = "mdrtb.lists.caseDefinition";
	
	static final String MDRTB_LISTS_MICROSCOPY = "mdrtb.lists.microscopy";
	
	static final String MDRTB_XPERT = "mdrtb.xpert";
	
	static final String MDRTB_HAIN_1 = "mdrtb.hain";
	
	static final String MDRTB_HAIN_2 = "mdrtb.hain2";
	
	static final String MDRTB_CULTURE = "mdrtb.culture";
	
	static final String MDRTB_LISTS_DRUG_RESISTANCE = "mdrtb.lists.drugResistance";
	
	static final String MDRTB_LISTS_RESISTANT_TO = "mdrtb.lists.resistantTo";
	
	static final String MDRTB_LISTS_SENSITIVE_TO = "mdrtb.lists.sensitiveTo";
	
	static final String MDRTB_HIV_STATUS = "mdrtb.hivStatus";
	
	static final String MDRTB_LISTS_OUTCOME = "mdrtb.outcome";
	
	static final String MDRTB_LISTS_END_OF_TREATMENT_DATE = "mdrtb.lists.endOfTreatmentDate";
	
	static final String MDRTB_LISTS_REREGISRATION_NUMBER = "mdrtb.lists.reregisrationNumber";
	
	static final String MDRTB_RESULT = "mdrtb.result";
	
	static final String MDRTB_LISTS_INH_SHORT = "mdrtb.lists.inhShort";
	
	static final String MDRTB_LISTS_RIF_SHORT = "mdrtb.lists.rifShort";
	
	static final String MDRTB_LISTS_INJECTABLES_SHORT = "mdrtb.lists.injectablesShort";
	
	static final String MDRTB_LISTS_QUIN_SHORT = "mdrtb.lists.quinShort";
	
	static final String MDRTB_ALL_CASES_ENROLLED = "mdrtb.allCasesEnrolled";
	
	static final String MDRTB_LISTS_PULMONARY_SHORT = "mdrtb.lists.pulmonaryShort";
	
	static final String MDRTB_LISTS_EXTRAPULMONARY_SHORT = "mdrtb.lists.extrapulmonaryShort";
	
	static final String MDRTB_NEGATIVE_SHORT = "mdrtb.negativeShort";
	
	static final String MDRTB_POSITIVE_SHORT = "mdrtb.positiveShort";
	
	static final String MDRTB_RESISTANT_SHORT = "mdrtb.resistantShort";
	
	static final String MDRTB_SENSITIVE_SHORT = "mdrtb.sensitiveShort";
	
	static final String MDRTB_LISTS_GROWTH = "mdrtb.lists.growth";
	
	static final String MDRTB_NUMBER_OF_RECORDS = "mdrtb.numberOfRecords";
	
	static final String MDRTB_DOTS_CASES_BY_REGISTRATION_GROUP = "mdrtb.dotsCasesByRegistrationGroup";
	
	static final String MDRTB_LISTS_NEW = "mdrtb.lists.new";
	
	static final String MDRTB_LISTS_RELAPSES = "mdrtb.lists.relapses";
	
	static final String MDRTB_TB_03_LTFU = "mdrtb.tb03.ltfu";
	
	static final String MDRTB_TB_03_FAILURE = "mdrtb.forms.failure";
	
	static final String MDRTB_LISTS_TRANSFER_IN = "mdrtb.lists.transferIn";
	
	static final String MDRTB_TB_03_TRANSFER_FROM = "mdrtb.tb03.transferFrom";
	
	static final String MDRTB_LISTS_DATE_OF_TRANSFER = "mdrtb.lists.dateOfTransfer";
	
	static final String MDRTB_TB_03_OTHER = "mdrtb.tb03.other";
	
	static final String MDRTB_DOTS_CASES_BY_ANATOMICAL_SITE = "mdrtb.dotsCasesByAnatomicalSite";
	
	static final String MDRTB_PULMONARY = "mdrtb.pulmonary";
	
	static final String MDRTB_EXTRAPULMONARY = "mdrtb.extrapulmonary";
	
	static final String MDRTB_BY_DRUG_RESISTANCE = "mdrtb.byDrugResistance";
	
	static final String MDRTB_LISTS_LOCALIZATION = "mdrtb.lists.localization";
	
	static final String MDRTB_LISTS_DRUG_NAMES = "mdrtb.lists.drugNames";
	
	static final String MDRTB_SENSITIVE = "mdrtb.sensitive";
	
	static final String MDRTB_DOTS_PULMONARY_CASES_BY_REGISRATION_GROUP_AND_BAC_STATUS = "mdrtb.dotsPulmonaryCasesByRegisrationGroupAndBacStatus";
	
	static final String MDRTB_LISTS_NEW_PULMONARY_BAC_POSITIVE = "mdrtb.lists.newPulmonaryBacPositive";
	
	static final String MDRTB_LISTS_NEW_PULMONARY_BAC_NEGATIVE = "mdrtb.lists.newPulmonaryBacNegative";
	
	static final String MDRTB_LISTS_RELAPSE_PULMONARY_BAC_POSITIVE = "mdrtb.lists.relapsePulmonaryBacPositive";
	
	static final String MDRTB_LISTS_RELAPSE_PULMONARY_BAC_NEGATIVE = "mdrtb.lists.relapsePulmonaryBacNegative";
	
	static final String MDRTB_LISTS_RETREATMENT_PULMONARY_BAC_POSITIVE = "mdrtb.lists.retreatmentPulmonaryBacPositive";
	
	static final String MDRTB_LISTS_RETREATMENT_PULMONARY_BAC_NEGATIVE = "mdrtb.lists.retreatmentPulmonaryBacNegative";
	
	static final String MDRTB_LISTS_TRANSFER_IN_PULMONARY_BAC_POSITIVE = "mdrtb.lists.transferInPulmonaryBacPositive";
	
	static final String MDRTB_LISTS_TRANSFER_IN_PULMONARY_BAC_NEGATIVE = "mdrtb.lists.transferInPulmonaryBacNegative";
	
	static final String MDRTB_MDR_XDR_PATIENTS_NO_TREATMENT = "mdrtb.mdrXdrPatientsNoTreatment";
	
	static final String MDRTB_MDRTB = "mdrtb.mdrtb";
	
	static final String MDRTB_XDRTB = "mdrtb.xdrtb";
	
	static final String MDRTB_MDR_SUCCESSFUL_TREATMENT_OUTCOME = "mdrtb.mdrSuccessfulTreatmentOutcome";
	
	static final String MDRTB_MDR_SUCCESSFUL_TREATMENT = "mdrtb.mdrSuccessfulTreatment";
	
	static final String MDRTB_MDR_XDR_PATIENTS = "mdrtb.mdrXdrPatients";
	
	static final String MDRTB_WOMEN_OF_CHILDBEARING_AGE = "mdrtb.womenOfChildbearingAge";
	
	static final String MDRTB_MEN_OF_CONSCRIPT_AGE = "mdrtb.menOfConscriptAge";
	
	static final String MDRTB_WITH_CONCOMITANT_DISEASE = "mdrtb.withConcomitantDisease";
	
	static final String MDRTB_WITH_DIABETES = "mdrtb.withDiabetes";
	
	static final String MDRTB_WITH_CANCER = "mdrtb.withCancer";
	
	static final String MDRTB_WITH_COPD = "mdrtb.withCOPD";
	
	static final String MDRTB_WITH_HYPERTENSION = "mdrtb.withHypertension";
	
	static final String MDRTB_WITH_ULCER = "mdrtb.withUlcer";
	
	static final String MDRTB_WITH_MENTAL_DISORDER = "mdrtb.withMentalDisorder";
	
	static final String MDRTB_WITH_HIV = "mdrtb.withHIV";
	
	static final String MDRTB_WITH_HEPATITIS = "mdrtb.withHepatitis";
	
	static final String MDRTB_WITH_KIDNEY_DISEASE = "mdrtb.withKidneyDisease";
	
	static final String MDRTB_WITH_OTHER_DISEASE = "mdrtb.withOtherDisease";
	
	static final String MDRTB_DETECTED_FROM_CONTACT = "mdrtb.detectedFromContact";
	
	static final String MDRTB_BY_SOC_PROF_STATUS = "mdrtb.bySocProfStatus";
	
	static final String MDRTB_BY_POP_CATEGORY = "mdrtb.byPopCategory";
	
	static final String MDRTB_FORM_89_COUNTRY_OF_ORIGIN = "mdrtb.form89.countryOfOrigin";
	
	static final String MDRTB_BY_DWELLING = "mdrtb.byDwelling";
	
	static final String MDRTB_LISTS_CITY = "mdrtb.lists.city";
	
	static final String MDRTB_LISTS_VILLAGE = "mdrtb.lists.village";
	
	static final String MDRTB_BY_PLACE_OF_DETECTION = "mdrtb.byPlaceOfDetection";
	
	static final String MDRTB_BY_CIRCUMSTANCES_OF_DETECTION = "mdrtb.byCircumstancesOfDetection";
	
	static final String MDRTB_FORM_89_CITY_OF_ORIGIN = "mdrtb.form89.cityOfOrigin";
	
	static final String MDRTB_FORM_89_DATE_OF_RETURN = "mdrtb.form89.dateOfReturn";
	
	static final String MDRTB_BY_METHOD_OF_DETECTION = "mdrtb.byMethodOfDetection";
	
	static final String MDRTB_BY_PULMONARY_LOCATION = "mdrtb.byPulmonaryLocation";
	
	static final String MDRTB_BY_EXTRA_PULMONARY_LOCATION = "mdrtb.byExtraPulmonaryLocation";
	
	static final String MDRTB_DR_TB_PATIENTS = "mdrtb.drTbPatients";
	
	static final String MDRTB_TB_03_U_REGISTRATION_NUMBER = "mdrtb.tb03uRegistrationNumber";
	
	static final String MDRTB_TB_03_U_DATE = "mdrtb.tb03uDate";
	
	static final String MDRTB_TB_03_TREATMENT_REGIMEN = "mdrtb.tb03.treatmentRegimen";
	
	static final String MDRTB_TB_03_U_CHANGE_OF_REGIMEN = "mdrtb.tb03u.changeOfRegimen";
	
	static final String MDRTB_DR_TB_PATIENTS_NO_TREATMENT = "mdrtb.drTbPatientsNoTreatment";
	
	static final String MDRTB_LISTS_NO_TX_REASON = "mdrtb.lists.noTxReason";
	
	static final String MDRTB_DR_TB_PATIENTS_SUCCESSFUL_TREATMENT = "mdrtb.drTbPatientsSuccessfulTreatment";
	
	/**
	 * Message key prefix for a gender label. The stored code ("M" / "F") is appended to build the
	 * key, and the bare prefix is itself a key meaning "unspecified". This is the same family the
	 * web app's report templates use, so the Patient List reads the same as every other register;
	 * the older mdrtb.tb03.gender.male/female keys hold single letters instead.
	 */
	static final String MDRTB_GENDER_PREFIX = "mdrtb.gender.";
	
	static final String MDRTB_UNASSIGNED = "mdrtb.unassigned";
	
	static final String MDRTB_VIEW = "mdrtb.view";
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/patientLists")
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
			model.addAttribute(OBLASTS2, oblasts);
		}
		
		else if (district == null) {
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute(OBLAST_SELECTED, oblast);
				model.addAttribute(OBLASTS2, oblasts);
				model.addAttribute(DISTRICTS, districts);
				model.addAttribute(FACILITIES, facilities);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				model.addAttribute(OBLAST_SELECTED, oblast);
				model.addAttribute(OBLASTS2, oblasts);
				model.addAttribute(DISTRICTS, districts);
			}
		} else {
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute(OBLAST_SELECTED, oblast);
				model.addAttribute(OBLASTS2, oblasts);
				model.addAttribute(DISTRICT_SELECTED, district);
				model.addAttribute(DISTRICTS, districts);
				model.addAttribute(FACILITIES, facilities);
			} else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
				model.addAttribute(OBLAST_SELECTED, oblast);
				model.addAttribute(OBLASTS2, oblasts);
				model.addAttribute(DISTRICTS, districts);
				model.addAttribute(DISTRICT_SELECTED, district);
				model.addAttribute(FACILITIES, facilities);
			}
		}
		model.addAttribute("yearSelected", year);
		model.addAttribute("monthSelected", month);
		model.addAttribute("quarterSelected", quarter);
		return new ModelAndView("/module/mdrtb/reporting/patientLists", model);
	}
	
	/* All Cases Enrolled */
	@RequestMapping("/module/mdrtb/reporting/allCasesEnrolled")
	public String allCasesEnrolled(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		String oName = "";
		if (oblastId != null) {
			oName = Context.getService(MdrtbService.class).getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = Context.getService(MdrtbService.class).getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = Context.getService(MdrtbService.class).getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		model.addAttribute("listName", getMessage(MDRTB_ALL_CASES_ENROLLED));
		
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		List<Location> locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getAllCasesEnrolledTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
	}
	
	public static String getAllCasesEnrolledTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		Collections.sort(tb03s);
		StringBuilder report = new StringBuilder();
		
		//NEW CASES 
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_START_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_SITE_IP)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TB_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		//report += ALIGN_LEFT_TAG + getMessage("mdrtb.tb03.tbLocalization") + CLOSE_TD;
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_MICROSCOPY)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_XPERT)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_1)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_2)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_CULTURE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_RESISTANCE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RESISTANT_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_SENSITIVE_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_HIV_STATUS)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_OUTCOME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_END_OF_TREATMENT_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_REREGISRATION_NUMBER)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		report.append(OPEN_TR);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INH_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RIF_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INJECTABLES_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_QUIN_SHORT)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		
		report.append(CLOSE_TR);
		
		int i = 0;
		Person p;
		for (TB03Form tf : tb03s) {
			System.out.println("Processing: Patient#" + tf.getPatient().getPatientIdentifier());
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report.append(OPEN_TR);
			report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(Context.getDateFormat().format(tf.getEncounterDatetime())).append(CLOSE_TD);
			if (tf.getTreatmentStartDate() != null)
				report.append(ALIGN_LEFT_TAG).append(Context.getDateFormat().format(tf.getTreatmentStartDate()))
				        .append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			if (tf.getTreatmentSiteIP() != null) {
				report.append(ALIGN_LEFT_TAG).append(tf.getTreatmentSiteIP().getName().getName()).append(CLOSE_TD);
			} else
				report.append(OPEN_CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(p.getFamilyName()).append(",").append(p.getGivenName()).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getGender(p)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(Context.getDateFormat().format(p.getBirthdate())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
			
			if (tf.getAnatomicalSite() != null) {
				Integer asId = tf.getAnatomicalSite().getConceptId();
				if (asId.intValue() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB)
				        .getConceptId().intValue()) {
					report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_PULMONARY_SHORT)).append(CLOSE_TD);
				} else if (asId.intValue() == Context.getService(MdrtbService.class)
				        .getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB).getConceptId().intValue()) {
					report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_EXTRAPULMONARY_SHORT)).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				//report += ALIGN_LEFT_TAG + tf.getAnatomicalSite().getName().getName().charAt(0) + CLOSE_TD;
			} else
				report.append(OPEN_CLOSE_TD);
			if (tf.getRegistrationGroup() != null)
				report.append(ALIGN_LEFT_TAG).append(tf.getRegistrationGroup().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			//SMEAR
			List<SmearForm> smears = tf.getSmears();
			if (smears != null && !smears.isEmpty()) {
				SmearForm ds = smears.get(0);
				if (ds.getSmearResult() != null) {
					if (ds.getSmearResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					} else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (Integer conc : concs) {
							if (conc.intValue() == ds.getSmearResult().getConceptId().intValue()) {
								report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_POSITIVE_SHORT)).append(CLOSE_TD);
								break;
							}
							
						}
					}
				} else {
					report.append(OPEN_CLOSE_TD);
				}
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && !xperts.isEmpty()) {
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				if (mtb == null) {
					report.append(OPEN_CLOSE_TD);
				} else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage(MDRTB_POSITIVE_SHORT);
						if (res != null) {
							int resId = res.getConceptId();
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_RESISTANT_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							} else if (resId == Context.getService(MdrtbService.class)
							        .getConcept(MdrtbConcepts.NOT_DETECTED).getConceptId()) {
								xr += "/" + getMessage(MDRTB_SENSITIVE_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							} else {
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
						} else {
							report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
						}
					} else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					} else {
						report.append(OPEN_CLOSE_TD);
					}
				}
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 1
			List<HAINForm> hains = tf.getHains();
			if (hains != null && !hains.isEmpty()) {
				HAINForm h = hains.get(0);
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getShortestName(Context.getLocale(), false).getName())
					        .append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getShortestName(Context.getLocale(), false).getName())
					        .append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (rh != null) {
					report.append(ALIGN_LEFT_TAG).append(rh.getShortestName(Context.getLocale(), false).getName())
					        .append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
			} else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tf.getHain2s();
			if (hain2s != null && !hain2s.isEmpty()) {
				HAIN2Form h = hain2s.get(0);
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getShortestName(Context.getLocale(), false).getName())
					        .append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getShortestName(Context.getLocale(), false).getName())
					        .append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				if (fq != null) {
					report.append(ALIGN_LEFT_TAG).append(fq.getShortestName(Context.getLocale(), false).getName())
					        .append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
			} else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && !cultures.isEmpty()) {
				CultureForm dc = cultures.get(0);
				if (dc.getCultureResult() != null) {
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					} else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_GROWTH)).append(CLOSE_TD);
					} else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (Integer conc : concs) {
							if (conc.intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_POSITIVE_SHORT)).append(CLOSE_TD);
								break;
							}
						}
					}
				} else {
					report.append(OPEN_CLOSE_TD);
				}
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report.append(ALIGN_LEFT_TAG)
				        .append(tf.getResistanceType().getShortestName(Context.getLocale(), false).getName())
				        .append(CLOSE_TD);
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getSensitiveDrugs(tf.getDsts())).append(CLOSE_TD);
			if (tf.getHivStatus() != null) {
				report.append(ALIGN_LEFT_TAG)
				        .append(tf.getHivStatus().getShortestName(Context.getLocale(), false).getName()).append(CLOSE_TD);
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			if (tf.getTreatmentOutcome() != null) {
				report.append(ALIGN_LEFT_TAG)
				        .append(tf.getTreatmentOutcome().getShortestName(Context.getLocale(), false).getName())
				        .append(CLOSE_TD);
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			if (tf.getTreatmentOutcomeDate() != null) {
				report.append(ALIGN_LEFT_TAG).append(Context.getDateFormat().format(tf.getTreatmentOutcomeDate()))
				        .append(CLOSE_TD);
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//OTHER NUMBER
			report.append(ALIGN_LEFT_TAG).append(getReRegistrationNumber(tf)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
			report.append(CLOSE_TR);
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* DOTS Cases by Registration Group */
	
	@RequestMapping("/module/mdrtb/reporting/dotsCasesByRegistrationGroup")
	public String dotsCasesByRegistrationGroup(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage(MDRTB_DOTS_CASES_BY_REGISTRATION_GROUP));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsCasesByRegistrationGroupTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDotsCasesByRegistrationGroupTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		Collections.sort(tb03s);
		
		//NEW CASES 
		Concept newConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP);
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_NEW)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//Relapse
		
		Concept relapse1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		Concept relapse2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_RELAPSES)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//AfterDefault
		Concept default1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		Concept default2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_TB_03_LTFU)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == default2Concept.getId().intValue())) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//AfterFailure
		Concept failure1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1);
		Concept failure2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_TB_03_FAILURE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//Transfer In
		Concept transferInConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_TRANSFER_IN)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TRANSFER_FROM)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DATE_OF_TRANSFER)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getTransferFrom(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getTransferFromDate(tf)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		
		//OTHER CASES 
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER);
		report.append(OPEN_H4).append(getMessage(MDRTB_TB_03_OTHER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == otherConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		return report.toString();
	}
	
	/* DOTS Cases by Anatomical Site */
	
	@RequestMapping("/module/mdrtb/reporting/dotsCasesByAnatomicalSite")
	public String dotsCasesByAnatomicalSite(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage(MDRTB_DOTS_CASES_BY_ANATOMICAL_SITE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsCasesByAnatomicalSiteTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDotsCasesByAnatomicalSiteTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		Collections.sort(tb03s);
		
		//NEW CASES 
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
		Concept pulmonaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB);
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_PULMONARY)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == pulmonaryConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationGroup(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//Relapse
		
		Concept epConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_EXTRAPULMONARY)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == epConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationGroup(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* By Drug Resistance */
	
	@RequestMapping("/module/mdrtb/reporting/byDrugResistance")
	public String byDrugResistance(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage(MDRTB_BY_DRUG_RESISTANCE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsCasesByDrugResistanceTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDotsCasesByDrugResistanceTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		Collections.sort(tb03s);
		
		StringBuilder report = new StringBuilder();
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONO);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// RIF
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RR_TB);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// POLY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PDR_TB);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// MDR
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// PRE_XDR_TB
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRE_XDR_TB);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// XDR_TB
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// TDR
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TDR_TB);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// UNKNOWN
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNKNOWN);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		// NO
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO);
		report.append(OPEN_H4).append(getMessage(MDRTB_SENSITIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_LOCALIZATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_NAMES)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getSiteOfDisease(tf)).append(CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* DOTS Pulmonary cases by Registration Group and Bacteriological Status */
	
	@RequestMapping("/module/mdrtb/reporting/dotsPulmonaryCasesByRegisrationGroupAndBacStatus")
	public String dotsPulmonaryCasesByRegisrationGroupAndBacStatus(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage(MDRTB_DOTS_PULMONARY_CASES_BY_REGISRATION_GROUP_AND_BAC_STATUS));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsPulmonaryCasesByRegisrationGroupAndBacteriologicalStatusTable(locList, year, quarterInt,
		    monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDotsPulmonaryCasesByRegisrationGroupAndBacteriologicalStatusTable(List<Location> locList,
	        Integer year, Integer quarter, Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		Collections.sort(tb03s);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP);
		Concept siteConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
		Concept pulConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB);
		Concept newConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW);
		
		//NEW CASES + Positive
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_NEW_PULMONARY_BAC_POSITIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Obs temp2;
		Person p;
		int i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						i++;
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
					}
				}
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//NEW CASES + Negative
		
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_NEW_PULMONARY_BAC_NEGATIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						i++;
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
					}
				}
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		//Relapse + positive
		Concept relapse1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		Concept relapse2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_RELAPSE_PULMONARY_BAC_POSITIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
					}
				}
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		//Relapse + negative
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_RELAPSE_PULMONARY_BAC_NEGATIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
					}
				}
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//Retreament - Negative
		Concept default1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		Concept default2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		Concept failure1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1);
		Concept failure2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2);
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_RETREATMENT_PULMONARY_BAC_POSITIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationGroup(tf)).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
					}
				}
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_RETREATMENT_PULMONARY_BAC_NEGATIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationGroup(tf)).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
					}
				}
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//Transfer In
		Concept transferInConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_TRANSFER_IN_PULMONARY_BAC_POSITIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
						
					}
				}
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		//Transfer In
		
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_TRANSFER_IN_PULMONARY_BAC_NEGATIVE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report.append(OPEN_TR);
						report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
						report.append(renderPerson(p, true));
						report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
						report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
						report.append(CLOSE_TR);
						
					}
				}
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* MDR-XDR Patients with no Treatment */
	
	@RequestMapping("/module/mdrtb/reporting/mdrXdrPatientsNoTreatment")
	public String mdrXdrPatientsNoTreatment(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_MDR_XDR_PATIENTS_NO_TREATMENT));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMdrXdrPatientsWithNoTreatmentTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getMdrXdrPatientsWithNoTreatmentTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month,
		    month2);
		
		//NEW CASES 
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept treatmentStartDate = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.MDR_TREATMENT_START_DATE);
		Concept mdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB);
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_MDRTB)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Obs temp2;
		Person p;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(treatmentStartDate, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == mdr.getId().intValue()
			        && (temp2 == null || temp2.getValueDatetime() == null)) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, false));
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		
		Concept xdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_XDRTB)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(treatmentStartDate, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == xdr.getId().intValue()
			        && (temp2 == null || temp2.getValueDatetime() == null)) {
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, false));
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* MDR Successful Treatment Outcome */
	
	@RequestMapping("/module/mdrtb/reporting/mdrSuccessfulTreatmentOutcome")
	public String mdrSuccessfulTreatmentOutcome(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_MDR_SUCCESSFUL_TREATMENT_OUTCOME));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMdrSuccessfulTreatmentOutcomeTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getMdrSuccessfulTreatmentOutcomeTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
		Concept curedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED);
		Concept txCompleted = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETE);
		
		//NEW CASES 
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_MDR_SUCCESSFUL_TREATMENT)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == curedConcept.getId().intValue() || temp.getValueCoded()
			                .getId().intValue() == txCompleted.getId().intValue())) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, false));
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* MDR-XDR Patients */
	
	@RequestMapping("/module/mdrtb/reporting/mdrXdrPatients")
	public String mdrXdrPatients(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_MDR_XDR_PATIENTS));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMdrXdrPatientsTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getMdrXdrPatientsTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept mdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB);
		
		//NEW CASES 
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_MDRTB)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == mdr.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, false));
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		
		//EP
		Concept xdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_XDRTB)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == xdr.getId().intValue()) {
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
				report.append(renderPerson(p, false));
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Women of Child-bearing Age */
	
	@RequestMapping("/module/mdrtb/reporting/womenOfChildbearingAge")
	public String womenOfChildbearingAge(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WOMEN_OF_CHILDBEARING_AGE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getWomenOfChildbearingAgeTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getWomenOfChildbearingAgeTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> forms = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		Collections.sort(forms);
		
		//NEW CASES 
		StringBuilder report = new StringBuilder();
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		//Obs temp = null;
		Person p;
		int i = 0;
		
		for (TB03Form tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			if (tf.getPatient().getGender().equals("F")) {
				
				Integer age = tf.getAgeAtTB03Registration();
				
				if (age != null && age >= 15 && age <= 49) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					if (tf.getRegistrationGroup() != null)
						report.append(ALIGN_LEFT_TAG).append(tf.getRegistrationGroup().getName().getName()).append(CLOSE_TD);
					else
						report.append(OPEN_CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Men of Conscript Age */
	
	@RequestMapping("/module/mdrtb/reporting/menOfConscriptAge")
	public String menOfConscriptAge(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_MEN_OF_CONSCRIPT_AGE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMenOfConscriptAgeTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getMenOfConscriptAgeTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		Collections.sort(tb03List);
		
		//NEW CASES 
		StringBuilder report = new StringBuilder();
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_CASE_DEFINITION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		//Obs temp = null;
		Person p;
		int i = 0;
		for (TB03Form tf : tb03List) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			if (tf.getPatient().getGender().equals("M")) {
				
				Integer age = tf.getAgeAtTB03Registration();
				
				if (age != null && age >= 18 && age <= 27) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					if (tf.getRegistrationGroup() != null)
						report.append(ALIGN_LEFT_TAG).append(tf.getRegistrationGroup().getName().getName()).append(CLOSE_TD);
					else
						report.append(OPEN_CLOSE_TD);
					
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Concomitant Disease */
	
	@RequestMapping("/module/mdrtb/reporting/withConcomitantDisease")
	public String withConcomitantDisease(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_CONCOMITANT_DISEASE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithConcamitantDiseasesTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithConcamitantDiseasesTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		Collections.sort(tb03List);
		ArrayList<Form89> forms = new ArrayList<>();
		Concept regGroup;
		Form89 f89;
		StringBuilder report = new StringBuilder();
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			f89.setTB03(tb03);
			forms.add(f89);
		}
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_DIABETES)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_CANCER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_COPD)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_HYPERTENSION)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_ULCER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_MENTAL_DISORDER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ICD20);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_HIV)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				Concept c = tf.getIbc20();
				
				//temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (c != null && (c.getConceptId().intValue() == yes.getConceptId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_HEPATITIS)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_KIDNEY_DISEASE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		report.append(BR_TAG);
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE);
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_OTHER_DISEASE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && temp.getValueCoded() != null
				        && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, true));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Cancer */
	
	@RequestMapping("/module/mdrtb/reporting/withCancer")
	public String withCancer(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_CANCER));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithCancerTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithCancerTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_CANCER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
		}
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases detected from Contact Tracing */
	
	@RequestMapping("/module/mdrtb/reporting/detectedFromContact")
	public String detectedFromContact(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_DETECTED_FROM_CONTACT));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesDetectedFromContactTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesDetectedFromContactTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Collections.sort(forms);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION);
		Concept fromContact = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_DETECTED_FROM_CONTACT)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == fromContact.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with COPD */
	
	@RequestMapping("/module/mdrtb/reporting/withCOPD")
	public String withCOPD(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_COPD));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithCopdTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithCopdTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_COPD)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
		}
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Hypertension */
	
	@RequestMapping("/module/mdrtb/reporting/withHypertension")
	public String withHypertension(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_HYPERTENSION));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithHypertensionTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithHypertensionTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_HYPERTENSION)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Ulcer */
	
	@RequestMapping("/module/mdrtb/reporting/withUlcer")
	public String withUlcer(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_ULCER));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithUlcerTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithUlcerTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_ULCER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Mental Disorder */
	
	@RequestMapping("/module/mdrtb/reporting/withMentalDisorder")
	public String withMentalDisorder(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_MENTAL_DISORDER));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithMentalDisorderTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithMentalDisorderTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_MENTAL_DISORDER)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with HIV */
	
	@RequestMapping("/module/mdrtb/reporting/withHIV")
	public String withHIV(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_HIV));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithHivTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithHivTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ICD20);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_HIV)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Hepatitis */
	
	@RequestMapping("/module/mdrtb/reporting/withHepatitis")
	public String withHepatitis(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_HEPATITIS));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithHepatitisTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithHepatitisTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_HEPATITIS)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Kidney Disease */
	
	@RequestMapping("/module/mdrtb/reporting/withKidneyDisease")
	public String withKidneyDisease(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_KIDNEY_DISEASE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithKidneyDiseaseTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithKidneyDiseaseTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_KIDNEY_DISEASE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases with Other Disease */
	
	@RequestMapping("/module/mdrtb/reporting/withOtherDisease")
	public String withOtherDisease(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_WITH_OTHER_DISEASE));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithOtherDiseaseTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesWithOtherDiseaseTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month,
		    month2);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		StringBuilder report = new StringBuilder();
		report.append(OPEN_H4).append(getMessage(MDRTB_WITH_OTHER_DISEASE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Obs temp;
		Person p;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report.append(OPEN_TR);
					report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
					report.append(renderPerson(p, false));
					report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
					report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
					report.append(CLOSE_TR);
				}
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* Cases by Soc Prof. Status */
	
	@RequestMapping("/module/mdrtb/reporting/bySocProfStatus")
	public String bySocProfStatus(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_SOC_PROF_STATUS));
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesBySocProfStatusTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
	}
	
	public static String getCasesBySocProfStatusTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		StringBuilder report = new StringBuilder();
		Concept status;
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION);
		ArrayList<Form89> workerList = new ArrayList<>();
		ArrayList<Form89> govtList = new ArrayList<>();
		ArrayList<Form89> studentList = new ArrayList<>();
		ArrayList<Form89> disabledList = new ArrayList<>();
		ArrayList<Form89> unemployedList = new ArrayList<>();
		ArrayList<Form89> phcList = new ArrayList<>();
		ArrayList<Form89> militaryList = new ArrayList<>();
		ArrayList<Form89> schoolList = new ArrayList<>();
		ArrayList<Form89> tbWorkerList = new ArrayList<>();
		ArrayList<Form89> privateList = new ArrayList<>();
		ArrayList<Form89> housewifeList = new ArrayList<>();
		ArrayList<Form89> preschoolList = new ArrayList<>();
		ArrayList<Form89> pensionerList = new ArrayList<>();
		
		//category
		Concept workerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WORKER);
		int workerId = workerConcept.getConceptId();
		Concept govtConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GOVT_SERVANT);
		int govtId = govtConcept.getConceptId();
		Concept studentConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.STUDENT);
		int studentId = studentConcept.getConceptId();
		Concept disabledConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISABLED);
		int disabledId = disabledConcept.getConceptId();
		Concept unemployedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNEMPLOYED);
		int unemployedId = unemployedConcept.getConceptId();
		Concept phcConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_WORKER);
		int phcId = phcConcept.getConceptId();
		Concept militaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		int militaryId = militaryConcept.getConceptId();
		Concept schoolConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCHOOLCHILD);
		int schoolId = schoolConcept.getConceptId();
		Concept tbWorkerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_SERVICES_WORKER);
		int tbWorkerId = tbWorkerConcept.getConceptId();
		Concept privateConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR);
		int privateId = privateConcept.getConceptId();
		Concept housewifeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOUSEWIFE);
		int housewifeId = housewifeConcept.getConceptId();
		Concept preschoolConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESCHOOL_CHILD);
		int preschoolId = preschoolConcept.getConceptId();
		Concept pensionerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PENSIONER);
		int pensionerId = pensionerConcept.getConceptId();
		
		int statusId;
		Form89 f89;
		Concept regGroup;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			status = f89.getProfession();
			
			if (status == null)
				continue;
			
			f89.setTB03(tb03);
			
			statusId = status.getConceptId();
			
			if (statusId == workerId)
				workerList.add(f89);
			else if (statusId == govtId)
				govtList.add(f89);
			else if (statusId == studentId)
				studentList.add(f89);
			else if (statusId == disabledId)
				disabledList.add(f89);
			else if (statusId == unemployedId)
				unemployedList.add(f89);
			else if (statusId == phcId)
				phcList.add(f89);
			else if (statusId == militaryId)
				militaryList.add(f89);
			else if (statusId == schoolId)
				schoolList.add(f89);
			else if (statusId == tbWorkerId)
				tbWorkerList.add(f89);
			else if (statusId == privateId)
				privateList.add(f89);
			else if (statusId == housewifeId)
				housewifeList.add(f89);
			else if (statusId == preschoolId)
				preschoolList.add(f89);
			else if (statusId == pensionerId)
				pensionerList.add(f89);
		}
		
		//WORKER
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WORKER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Person p;
		int i = 0;
		for (Form89 tf : workerList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(workerList.size());
		report.append(BR_TAG);
		
		//GOVT SERVANT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GOVT_SERVANT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : govtList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(govtList.size());
		report.append(BR_TAG);
		
		//STUDENT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.STUDENT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : studentList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(studentList.size());
		report.append(BR_TAG);
		
		//DISABLED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISABLED);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : disabledList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(disabledList.size());
		report.append(BR_TAG);
		
		//UNEMPLOYED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNEMPLOYED);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : unemployedList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(unemployedList.size());
		report.append(BR_TAG);
		
		//PHC WORKER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_WORKER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : phcList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(phcList.size());
		report.append(BR_TAG);
		
		//MILITARY SERVANT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : militaryList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(militaryList.size());
		report.append(BR_TAG);
		
		//SCHOOLCHILD
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCHOOLCHILD);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : schoolList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(schoolList.size());
		report.append(BR_TAG);
		
		//TB SERVICES WORKER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_SERVICES_WORKER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : tbWorkerList) {
			
			TB03Form tb03;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(tbWorkerList.size());
		report.append(BR_TAG);
		
		//PRIVATE SECTOR WORKER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : privateList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(privateList.size());
		report.append(BR_TAG);
		
		//HOUSEWIFE
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOUSEWIFE);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : housewifeList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(housewifeList.size());
		report.append(BR_TAG);
		
		//PRE-SCHOOL CHILD
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESCHOOL_CHILD);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : preschoolList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(preschoolList.size());
		report.append(BR_TAG);
		
		//PENSIONER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PENSIONER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : pensionerList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(pensionerList.size());
		report.append(BR_TAG);
		return report.toString();
	}
	
	/* Cases by Population Category */
	
	@RequestMapping("/module/mdrtb/reporting/byPopCategory")
	public String byPopulationCategory(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_POP_CATEGORY));
		
		System.out.println("GETTING FORM89 LIST");
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByPopulationCategoryTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByPopulationCategoryTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		StringBuilder report = new StringBuilder();
		Concept category = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY);
		
		ArrayList<Form89> thisList = new ArrayList<>();
		ArrayList<Form89> otherList = new ArrayList<>();
		ArrayList<Form89> foreignerList = new ArrayList<>();
		ArrayList<Form89> welfareList = new ArrayList<>();
		ArrayList<Form89> homelessList = new ArrayList<>();
		ArrayList<Form89> prisonerList = new ArrayList<>();
		ArrayList<Form89> investigationList = new ArrayList<>();
		
		//CATEGORY
		Concept thisConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OF_TERRITORY);
		int thisId = thisConcept.getConceptId();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OTHER_TERRITORY);
		int otherId = otherConcept.getConceptId();
		Concept foreignerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOREIGNER);
		int foreignerId = foreignerConcept.getConceptId();
		Concept welfareConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.RESIDENT_SOCIAL_SECURITY_FACILITY);
		int welfareId = welfareConcept.getConceptId();
		Concept homelessConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOMELESS);
		int homelessId = homelessConcept.getConceptId();
		Concept prisonerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONVICTED);
		int prisonerId = prisonerConcept.getConceptId();
		Concept investigationConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ON_REMAND);
		int investigationId = investigationConcept.getConceptId();
		
		int catId;
		Form89 f89;
		Concept regGroup;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			category = f89.getPopulationCategory();
			
			if (category == null)
				continue;
			
			f89.setTB03(tb03);
			catId = category.getConceptId();
			
			if (catId == thisId)
				thisList.add(f89);
			else if (catId == otherId)
				otherList.add(f89);
			else if (catId == foreignerId)
				foreignerList.add(f89);
			else if (catId == welfareId)
				welfareList.add(f89);
			else if (catId == homelessId)
				homelessList.add(f89);
			else if (catId == prisonerId)
				prisonerList.add(f89);
			else if (catId == investigationId)
				investigationList.add(f89);
		}
		
		//RESIDENT_OF_TERRITORY
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OF_TERRITORY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Person p;
		int i = 0;
		for (Form89 tf : thisList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(thisList.size());
		report.append(BR_TAG);
		
		//RESIDENT_OTHER_TERRITORY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OTHER_TERRITORY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : otherList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(otherList.size());
		report.append(BR_TAG);
		
		//FOREIGNER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOREIGNER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_FORM_89_COUNTRY_OF_ORIGIN)).append(CLOSE_TD);
		
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : foreignerList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(tf.getCountryOfOrigin()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(foreignerList.size());
		report.append(BR_TAG);
		
		//RESIDENT_SOCIAL_SECURITY_FACILITY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_SOCIAL_SECURITY_FACILITY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : welfareList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(welfareList.size());
		report.append(BR_TAG);
		
		//HOMELESS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOMELESS);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : homelessList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(homelessList.size());
		report.append(BR_TAG);
		
		//CONVICTED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONVICTED);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : prisonerList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(prisonerList.size());
		report.append(BR_TAG);
		
		//ON_REMAND
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ON_REMAND);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : investigationList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(investigationList.size());
		return report.toString();
	}
	
	/* Cases by Dwelling */
	
	@RequestMapping("/module/mdrtb/reporting/byDwelling")
	public String byDwelling(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_DWELLING));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByDwellingTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByDwellingTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		Collections.sort(tb03List);
		
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE);
		Concept type;
		StringBuilder report = new StringBuilder();
		
		ArrayList<Form89> cityList = new ArrayList<>();
		ArrayList<Form89> villageList = new ArrayList<>();
		//PLACE
		Concept cityConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY);
		int cityId = cityConcept.getConceptId();
		Concept villageConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.VILLAGE);
		int villageId = villageConcept.getConceptId();
		
		int typeId;
		Form89 f89;
		Concept regGroup;
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			type = f89.getLocationType();
			
			if (type == null)
				continue;
			
			f89.setTB03(tb03);
			
			typeId = type.getConceptId();
			
			if (typeId == cityId) {
				cityList.add(f89);
			}
			
			else if (typeId == villageId) {
				villageList.add(f89);
			}
			
		}
		
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY);
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_CITY)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Person p;
		int i = 0;
		for (Form89 tf : cityList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(cityList.size());
		report.append(BR_TAG);
		
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.VILLAGE);
		report.append(OPEN_H4).append(getMessage(MDRTB_LISTS_VILLAGE)).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : villageList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(villageList.size());
		return report.toString();
	}
	
	/* Cases by Places of Detection */
	
	@RequestMapping("/module/mdrtb/reporting/byPlaceOfDetection")
	public String byPlaceOfDetection(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_PLACE_OF_DETECTION));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByPlaceOfDetectionTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByPlaceOfDetectionTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		Collections.sort(tb03List);
		
		ArrayList<Form89> tbList = new ArrayList<>();
		ArrayList<Form89> privateList = new ArrayList<>();
		ArrayList<Form89> phcList = new ArrayList<>();
		ArrayList<Form89> otherList = new ArrayList<>();
		
		Concept circSite;
		
		StringBuilder report = new StringBuilder();
		//PLACE
		Concept tbConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_FACILITY);
		int tbId = tbConcept.getConceptId();
		Concept privateConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR_FACILITY);
		int privateId = privateConcept.getConceptId();
		Concept phcConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_FACILITY);
		int phcId = phcConcept.getConceptId();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MEDICAL_FACILITY);
		int otherId = otherConcept.getConceptId();
		
		int circId;
		Form89 f89;
		Concept regGroup;
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			circSite = f89.getPlaceOfDetection();
			
			if (circSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			circId = circSite.getConceptId();
			
			if (circId == tbId)
				tbList.add(f89);
			else if (circId == privateId)
				privateList.add(f89);
			else if (circId == phcId)
				phcList.add(f89);
			else if (circId == otherId)
				otherList.add(f89);
		}
		
		//TB FACILITY
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_FACILITY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Person p;
		int i = 0;
		for (Form89 tf : tbList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(tbList.size());
		report.append(BR_TAG);
		
		//PHC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_FACILITY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : phcList) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(phcList.size());
		report.append(BR_TAG);
		
		//Private Sector
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR_FACILITY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : privateList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(privateList.size());
		report.append(BR_TAG);
		
		//OTHER MED FAC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MEDICAL_FACILITY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : otherList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(otherList.size());
		return report.toString();
	}
	
	/* Cases by Circumstances of Detection */
	
	@RequestMapping("/module/mdrtb/reporting/byCircumstancesOfDetection")
	public String byCircumstancesOfDetection(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_CIRCUMSTANCES_OF_DETECTION));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByCircumstancesOfDetectionTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByCircumstancesOfDetectionTable(List<Location> locList, Integer year, Integer quarterInt,
	        Integer monthInt, Integer month2Int, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarterInt,
		    monthInt, month2Int);
		Collections.sort(tb03List);
		
		ArrayList<Form89> selfRefList = new ArrayList<>();
		ArrayList<Form89> baselineExamList = new ArrayList<>();
		ArrayList<Form89> postmortemList = new ArrayList<>();
		ArrayList<Form89> contactList = new ArrayList<>();
		ArrayList<Form89> migrantList = new ArrayList<>();
		
		Concept circSite = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION);
		
		StringBuilder report = new StringBuilder();
		//CIRCUMSTANCES
		Concept selfRefConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SELF_REFERRAL);
		int selfRefId = selfRefConcept.getConceptId();
		Concept baselineExamConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BASELINE_EXAM);
		int baselineExamId = baselineExamConcept.getConceptId();
		Concept postmortemConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.POSTMORTERM_IDENTIFICATION);
		int postMortemId = postmortemConcept.getConceptId();
		Concept contactConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		int contactId = contactConcept.getConceptId();
		Concept migrantConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MIGRANT);
		int migrantId = migrantConcept.getConceptId();
		
		int circId;
		Form89 f89;
		Concept regGroup;
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			circSite = f89.getCircumstancesOfDetection();
			
			if (circSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			circId = circSite.getConceptId();
			
			if (circId == selfRefId) {
				selfRefList.add(f89);
			}
			
			else if (circId == baselineExamId) {
				baselineExamList.add(f89);
			}
			
			else if (circId == postMortemId) {
				postmortemList.add(f89);
			}
			
			else if (circId == contactId) {
				contactList.add(f89);
			}
			
			else if (circId == migrantId) {
				migrantList.add(f89);
			}
			
		}
		
		//SELF_REFERRAL
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SELF_REFERRAL);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Person p;
		int i = 0;
		for (Form89 tf : selfRefList) {
			
			TB03Form tb03;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(selfRefList.size());
		report.append(BR_TAG);
		
		//BASELINE_EXAM
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BASELINE_EXAM);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : baselineExamList) {
			
			TB03Form tb03;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(baselineExamList.size());
		report.append(BR_TAG);
		
		//POSTMORTERM_IDENTIFICATION
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSTMORTERM_IDENTIFICATION);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : postmortemList) {
			
			TB03Form tb03;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(postmortemList.size());
		//CONTACT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : contactList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(contactList.size());
		report.append(BR_TAG);
		
		//MIGRANT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MIGRANT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_FORM_89_CITY_OF_ORIGIN)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_FORM_89_DATE_OF_RETURN)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : migrantList) {
			
			TB03Form tb03;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(tf.getCityOfOrigin()).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(renderDate(tf.getDateOfReturn())).append(CLOSE_TD);
				
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(migrantList.size());
		return report.toString();
	}
	
	/* Cases by Method of Detection */
	
	@RequestMapping("/module/mdrtb/reporting/byMethodOfDetection")
	public String byMethodOfDetection(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_METHOD_OF_DETECTION));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByMethodOfDetectionTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByMethodOfDetectionTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		ArrayList<Form89> fluorographyList = new ArrayList<>();
		ArrayList<Form89> genexpertList = new ArrayList<>();
		ArrayList<Form89> microscopyList = new ArrayList<>();
		ArrayList<Form89> tuberculinList = new ArrayList<>();
		ArrayList<Form89> hainList = new ArrayList<>();
		ArrayList<Form89> cultureList = new ArrayList<>();
		ArrayList<Form89> histologyList = new ArrayList<>();
		ArrayList<Form89> cxrList = new ArrayList<>();
		ArrayList<Form89> otherList = new ArrayList<>();
		
		Concept method = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION);
		
		StringBuilder report = new StringBuilder();
		//METHOD
		Concept fluorographyConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		int fluorographyId = fluorographyConcept.getConceptId();
		Concept genexpertConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENEXPERT);
		int genexpertId = genexpertConcept.getConceptId();
		Concept tuberculinConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULIN_TEST);
		int tuberculinId = tuberculinConcept.getConceptId();
		Concept hainConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_TEST);
		int hainId = hainConcept.getConceptId();
		Concept cultureConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_TEST);
		int cultureId = cultureConcept.getConceptId();
		Concept histologyConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HISTOLOGY);
		int histologyId = histologyConcept.getConceptId();
		Concept cxrConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CXR_RESULT);
		int cxrId = cxrConcept.getConceptId();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER);
		int otherId = otherConcept.getConceptId();
		
		int methodId;
		Form89 f89;
		Concept regGroup;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			regGroup = tb03.getRegistrationGroup();
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			if (fList == null || fList.size() != 1) {
				continue;
			}
			f89 = fList.get(0);
			method = f89.getMethodOfDetection();
			if (method == null)
				continue;
			f89.setTB03(tb03);
			methodId = method.getConceptId();
			if (methodId == fluorographyId)
				fluorographyList.add(f89);
			else if (methodId == genexpertId)
				genexpertList.add(f89);
			else if (methodId == tuberculinId)
				tuberculinList.add(f89);
			else if (methodId == hainId)
				hainList.add(f89);
			else if (methodId == cultureId)
				cultureList.add(f89);
			else if (methodId == histologyId)
				histologyList.add(f89);
			else if (methodId == cxrId)
				cxrList.add(f89);
			else if (methodId == otherId)
				otherList.add(f89);
		}
		
		//FLUOROGRAPHY
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		Person p;
		int i = 0;
		for (Form89 tf : fluorographyList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(fluorographyList.size());
		report.append(BR_TAG);
		
		//GENEXPERT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENEXPERT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : genexpertList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(genexpertList.size());
		report.append(BR_TAG);
		
		//FLURORESCENT_MICROSCOPY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : microscopyList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(microscopyList.size());
		report.append(BR_TAG);
		
		//TUBERCULIN_TEST
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULIN_TEST);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : tuberculinList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(tuberculinList.size());
		report.append(BR_TAG);
		
		//HAIN_TEST
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_TEST);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : hainList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(hainList.size());
		report.append(BR_TAG);
		
		//CULTURE_TEST
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_TEST);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : cultureList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(cultureList.size());
		report.append(BR_TAG);
		
		//HISTOLOGY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HISTOLOGY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : histologyList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(histologyList.size());
		report.append(BR_TAG);
		
		//CXR_RESULT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CXR_RESULT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : cxrList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(cxrList.size());
		report.append(BR_TAG);
		
		//OTHER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		
		for (Form89 tf : otherList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(otherList.size());
		return report.toString();
	}
	
	/* Cases by Pulmonary Location */
	
	@RequestMapping("/module/mdrtb/reporting/byPulmonaryLocation")
	public String byPulmonaryLocation(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage(MDRTB_BY_PULMONARY_LOCATION));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByPulmonaryLocationTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByPulmonaryLocationTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		ArrayList<Form89> focalList = new ArrayList<>();
		ArrayList<Form89> infilList = new ArrayList<>();
		ArrayList<Form89> disList = new ArrayList<>();
		ArrayList<Form89> cavList = new ArrayList<>();
		ArrayList<Form89> fibCavList = new ArrayList<>();
		ArrayList<Form89> cirrList = new ArrayList<>();
		ArrayList<Form89> priCompList = new ArrayList<>();
		ArrayList<Form89> miliaryList = new ArrayList<>();
		ArrayList<Form89> tuberculomaList = new ArrayList<>();
		ArrayList<Form89> bronchiList = new ArrayList<>();
		Concept anatomicalSite;
		
		StringBuilder report = new StringBuilder();
		//PULMONARY
		Concept fibroCavConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FIBROUS_CAVERNOUS);
		int fibroCavId = fibroCavConcept.getConceptId();
		Concept miliaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		int miliaryId = miliaryConcept.getConceptId();
		Concept focalConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOCAL);
		int focalId = focalConcept.getConceptId();
		Concept infiltrativeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INFILTRATIVE);
		int infiltrativeId = infiltrativeConcept.getConceptId();
		Concept disseminatedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISSEMINATED);
		int disseminatedId = disseminatedConcept.getConceptId();
		Concept cavernousConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAVERNOUS);
		int cavernousId = cavernousConcept.getConceptId();
		Concept cirrhoticConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRRHOTIC);
		int cirrhoticId = cirrhoticConcept.getConceptId();
		Concept primaryComplexConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_PRIMARY_COMPLEX);
		int primaryComplexId = primaryComplexConcept.getConceptId();
		Concept tuberculomaConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOMA);
		int tuberculomaId = tuberculomaConcept.getConceptId();
		Concept bronchiConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BRONCHUS);
		int bronchiId = bronchiConcept.getConceptId();
		
		int pulId;
		Form89 f89;
		Concept regGroup;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			
			f89 = fList.get(0);
			
			anatomicalSite = f89.getAnatomicalSite();
			
			if (anatomicalSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			pulId = anatomicalSite.getConceptId();
			
			if (pulId == focalId)
				focalList.add(f89);
			else if (pulId == infiltrativeId)
				infilList.add(f89);
			else if (pulId == disseminatedId)
				disList.add(f89);
			else if (pulId == cavernousId)
				cavList.add(f89);
			else if (pulId == fibroCavId)
				fibCavList.add(f89);
			else if (pulId == cirrhoticId)
				cirrList.add(f89);
			else if (pulId == primaryComplexId)
				priCompList.add(f89);
			else if (pulId == miliaryId)
				miliaryList.add(f89);
			else if (pulId == tuberculomaId)
				tuberculomaList.add(f89);
			else if (pulId == bronchiId)
				bronchiList.add(f89);
		}
		
		// FOCAL
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOCAL);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		Person p;
		int i = 0;
		for (Form89 tf : focalList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(focalList.size());
		report.append(BR_TAG);
		
		// INFILTRATIVE
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INFILTRATIVE);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : infilList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(infilList.size());
		report.append(BR_TAG);
		
		// DISSEMINATED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISSEMINATED);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : disList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(disList.size());
		report.append(BR_TAG);
		
		// CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAVERNOUS);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : cavList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(cavList.size());
		report.append(BR_TAG);
		
		// FIBROUS_CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FIBROUS_CAVERNOUS);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : fibCavList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(fibCavList.size());
		report.append(BR_TAG);
		
		// CIRRHOTIC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRRHOTIC);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : cirrList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(cirrList.size());
		report.append(BR_TAG);
		
		// TB_PRIMARY_COMPLEX
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_PRIMARY_COMPLEX);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : priCompList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(priCompList.size());
		report.append(BR_TAG);
		
		// MILITARY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : miliaryList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(miliaryList.size());
		report.append(BR_TAG);
		
		// TUBERCULOMA
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOMA);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : tuberculomaList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(tuberculomaList.size());
		report.append(BR_TAG);
		
		// BRONCHUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BRONCHUS);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : bronchiList) {
			
			TB03Form tb03;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
				
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(bronchiList.size());
		report.append(BR_TAG);
		return report.toString();
	}
	
	/* Cases by Extra-Pulmonary Location */
	
	@RequestMapping("/module/mdrtb/reporting/byExtraPulmonaryLocation")
	public String byExtraPulmonaryLocation(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage(MDRTB_BY_EXTRA_PULMONARY_LOCATION));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByExtraPulmonaryLocationTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getCasesByExtraPulmonaryLocationTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, Integer month2, boolean restfulLink) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		
		ArrayList<Form89> plevlList = new ArrayList<>();
		ArrayList<Form89> ofLymphList = new ArrayList<>();
		ArrayList<Form89> osteoList = new ArrayList<>();
		ArrayList<Form89> uroList = new ArrayList<>();
		ArrayList<Form89> periLymphList = new ArrayList<>();
		ArrayList<Form89> abdList = new ArrayList<>();
		ArrayList<Form89> skinList = new ArrayList<>();
		ArrayList<Form89> eyeList = new ArrayList<>();
		ArrayList<Form89> cnsList = new ArrayList<>();
		ArrayList<Form89> liverList = new ArrayList<>();
		
		Concept ptbLocation;
		
		StringBuilder report = new StringBuilder();
		//PULMONARY
		Concept plevConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLEVRITIS);
		int plevId = plevConcept.getConceptId();
		Concept ofLymphConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int ofLymphId = ofLymphConcept.getConceptId();
		Concept osteoConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OSTEOARTICULAR);
		int osteoId = osteoConcept.getConceptId();
		Concept uroConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENITOURINARY);
		int uroId = uroConcept.getConceptId();
		Concept periLymphConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int periLymphId = periLymphConcept.getConceptId();
		Concept abdConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ABDOMINAL);
		int abdId = abdConcept.getConceptId();
		Concept skinConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULODERMA);
		int skinId = skinConcept.getConceptId();
		Concept eyeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OCULAR);
		int eyeId = eyeConcept.getConceptId();
		Concept cnsConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_CNS);
		int cnsId = cnsConcept.getConceptId();
		Concept liverConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LIVER);
		int liverId = liverConcept.getConceptId();
		
		int pulId;
		Form89 f89;
		Concept regGroup;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				continue;
			}
			regGroup = tb03.getRegistrationGroup();
			if (regGroup == null || regGroup.getConceptId() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				continue;
			}
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null, null);
			if (fList == null || fList.size() != 1) {
				continue;
			}
			f89 = fList.get(0);
			ptbLocation = f89.getEptbLocation();
			if (ptbLocation == null)
				continue;
			f89.setTB03(tb03);
			pulId = ptbLocation.getConceptId();
			if (pulId == plevId)
				plevlList.add(f89);
			else if (pulId == ofLymphId)
				ofLymphList.add(f89);
			else if (pulId == osteoId)
				osteoList.add(f89);
			else if (pulId == uroId)
				uroList.add(f89);
			else if (pulId == periLymphId)
				periLymphList.add(f89);
			else if (pulId == abdId)
				abdList.add(f89);
			else if (pulId == skinId)
				skinList.add(f89);
			else if (pulId == eyeId)
				eyeList.add(f89);
			else if (pulId == cnsId)
				cnsList.add(f89);
			else if (pulId == liverId)
				liverList.add(f89);
		}
		
		// FOCAL
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLEVRITIS);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		Person p;
		int i = 0;
		for (Form89 tf : plevlList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(plevlList.size());
		report.append(BR_TAG);
		
		// INFILTRATIVE
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : ofLymphList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(ofLymphList.size());
		report.append(BR_TAG);
		
		// DISSEMINATED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OSTEOARTICULAR);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : osteoList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(osteoList.size());
		report.append(BR_TAG);
		
		// CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENITOURINARY);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : uroList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(uroList.size());
		report.append(BR_TAG);
		
		// FIBROUS_CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : periLymphList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(periLymphList.size());
		report.append(BR_TAG);
		
		// CIRRHOTIC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ABDOMINAL);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : abdList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(abdList.size());
		report.append(BR_TAG);
		
		// TB_PRIMARY_COMPLEX
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULODERMA);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : skinList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(skinList.size());
		report.append(BR_TAG);
		
		// MILITARY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OCULAR);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : eyeList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(eyeList.size());
		report.append(BR_TAG);
		
		// TUBERCULOMA
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_CNS);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : cnsList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(cnsList.size());
		report.append(BR_TAG);
		
		// BRONCHUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LIVER);
		report.append(OPEN_H4).append(q.getName().getName()).append(CLOSE_H4);
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(CLOSE_TR);
		
		i = 0;
		for (Form89 tf : liverList) {
			TB03Form tb03;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report.append(OPEN_TR);
				report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tb03)).append(CLOSE_TD);
				report.append(renderPerson(p, true));
				report.append(ALIGN_LEFT_TAG).append(age).append(CLOSE_TD);
				report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
				report.append(CLOSE_TR);
			}
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(liverList.size());
		report.append(BR_TAG);
		return report.toString();
	}
	
	/* DR-TB Patients */
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatients")
	public String drTbPatients(@RequestParam(DISTRICT) Integer districtId, @RequestParam(OBLAST) Integer oblastId,
	        @RequestParam(FACILITY) Integer facilityId, @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		model.addAttribute("listName", getMessage(MDRTB_DR_TB_PATIENTS));
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDrtbCasesTable(locList, year, quarterInt, monthInt, monthInt, false);
		
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDrtbCasesTable(List<Location> locList, Integer year, Integer quarter, Integer month,
	        Integer month2, boolean restfulLink) {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		
		List<TB03uForm> tb03us = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month,
		    month2);
		
		Collections.sort(tb03us);
		
		StringBuilder report = new StringBuilder();
		
		//NEW CASES 
		
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_XPERT)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_1)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_2)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_CULTURE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_RESISTANCE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RESISTANT_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_SENSITIVE_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_HIV_STATUS)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_OUTCOME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_END_OF_TREATMENT_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_U_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_U_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_REGIMEN)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_START_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_U_CHANGE_OF_REGIMEN)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_START_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_OUTCOME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_END_OF_TREATMENT_DATE)).append(CLOSE_TD);
		report.append(CLOSE_TR);
		
		report.append(OPEN_TR);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INH_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RIF_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INJECTABLES_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_QUIN_SHORT)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		
		report.append(CLOSE_TR);
		TB03Form tf;
		RegimenForm rf;
		int i = 0;
		Person p;
		for (TB03uForm tuf : tb03us) {
			if (tuf.getPatient() == null || tuf.getPatient().getVoided())
				continue;
			
			tf = tuf.getTb03();
			
			if (tf == null)
				continue;
			
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report.append(OPEN_TR);
			report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(p.getFamilyName()).append(",").append(p.getGivenName()).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getGender(p)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(sdf.format(p.getBirthdate())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && !xperts.isEmpty()) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report.append(OPEN_CLOSE_TD);
				}
				
				else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage(MDRTB_POSITIVE_SHORT);
						
						if (res != null) {
							int resId = res.getConceptId();
							
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_RESISTANT_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
							
							else if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_SENSITIVE_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							} else {
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
						} else {
							report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
						}
					} else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					} else {
						report.append(OPEN_CLOSE_TD);
					}
				}
			} else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 1	
			List<HAINForm> hains = tf.getHains();
			if (hains != null && !hains.isEmpty()) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (rh != null) {
					report.append(ALIGN_LEFT_TAG).append(rh.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tf.getHain2s();
			if (hain2s != null && !hain2s.isEmpty()) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (fq != null) {
					report.append(ALIGN_LEFT_TAG).append(fq.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && !cultures.isEmpty()) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_GROWTH)).append(CLOSE_TD);
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (Integer conc : concs) {
							if (conc.intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_POSITIVE_SHORT)).append(CLOSE_TD);
								break;
							}
						}
					}
				}
				
				else {
					report.append(OPEN_CLOSE_TD);
				}
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null)
				report.append(ALIGN_LEFT_TAG).append(tf.getResistanceType().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getSensitiveDrugs(tf.getDsts())).append(CLOSE_TD);
			
			if (tf.getHivStatus() != null)
				report.append(ALIGN_LEFT_TAG).append(tf.getHivStatus().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			if (tf.getTreatmentOutcome() != null)
				report.append(ALIGN_LEFT_TAG).append(tf.getTreatmentOutcome().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			if (tf.getTreatmentOutcomeDate() != null)
				report.append(ALIGN_LEFT_TAG).append(sdf.format(tf.getTreatmentOutcomeDate())).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			report.append(ALIGN_LEFT_TAG).append(TB03Util.getRegistrationNumber(tuf)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(sdf.format(tuf.getEncounterDatetime())).append(CLOSE_TD);
			
			if (tuf.getPatientCategory() != null)
				report.append(ALIGN_LEFT_TAG).append(tuf.getPatientCategory().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			if (tuf.getMdrTreatmentStartDate() != null)
				report.append(ALIGN_LEFT_TAG).append(sdf.format(tuf.getMdrTreatmentStartDate())).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			rf = getFirstRegimenChangeForPatient(tuf.getPatient(), tuf.getPatientProgramId());
			
			if (rf != null) {
				if (rf.getSldRegimenType() != null)
					report.append(ALIGN_LEFT_TAG).append(rf.getSldRegimenType().getName().getName()).append(CLOSE_TD);
				else
					report.append(OPEN_CLOSE_TD);
				if (rf.getCouncilDate() != null)
					report.append(ALIGN_LEFT_TAG).append(sdf.format(rf.getCouncilDate())).append(CLOSE_TD);
				else
					report.append(OPEN_CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			if (tuf.getTreatmentOutcome() != null)
				report.append(ALIGN_LEFT_TAG).append(tuf.getTreatmentOutcome().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			if (tuf.getTreatmentOutcomeDate() != null)
				report.append(ALIGN_LEFT_TAG).append(sdf.format(tuf.getTreatmentOutcomeDate())).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
			report.append(CLOSE_TR);
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* DR-TB Patients without Treatment */
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatientsNoTreatment")
	public String drTbPatientsNoTreatment(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		model.addAttribute("listName", getMessage(MDRTB_DR_TB_PATIENTS_NO_TREATMENT));
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		
		String report = getDrTbPatientsWithoutTreatmentTable(locList, year, quarterInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDrTbPatientsWithoutTreatmentTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month, boolean restfulLink) {
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month);
		Collections.sort(tb03s);
		
		StringBuilder report = new StringBuilder();
		//NEW CASES 
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_XPERT)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_1)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_2)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_CULTURE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_RESISTANCE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RESISTANT_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_SENSITIVE_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_HIV_STATUS)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_OUTCOME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_END_OF_TREATMENT_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_NO_TX_REASON)).append(CLOSE_TD);
		report.append(CLOSE_TR);
		
		report.append(OPEN_TR);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INH_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RIF_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INJECTABLES_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_QUIN_SHORT)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		
		report.append(CLOSE_TR);
		
		//RegimenForm rf = null;
		
		int noId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO).getConceptId();
		int unknownId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNKNOWN).getConceptId();
		int monoId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONO).getConceptId();
		
		int i = 0;
		Person p;
		Concept resist;
		int resistId;
		TB03Form tutf;
		boolean found;
		
		for (TB03Form tf : tb03s) {
			found = false;
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			resist = tf.getResistanceType();
			
			if (resist != null) {
				resistId = resist.getConceptId();
				if (resistId == noId || resistId == unknownId || resistId == monoId) {
					continue;
				}
				
			}
			
			else {
				
				continue;
			}
			
			//find mdrtb program with TB03 same as this form
			List<MdrtbPatientProgram> mdrtbPrograms = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(
			    tf.getPatient());
			if (mdrtbPrograms != null && !mdrtbPrograms.isEmpty()) {
				for (MdrtbPatientProgram mpp : mdrtbPrograms) {
					TB03uForm tuf = mpp.getTb03u();
					if (tuf != null) {
						tutf = tuf.getTb03();
						if (tutf != null) {
							if (!tutf.getEncounter().getVoided()
							        && (tutf.getEncounter().getEncounterId().intValue() == tf.getEncounter()
							                .getEncounterId().intValue())) {
								found = true;
								break;
							}
							
						}
						
					}
					
				}
				
			}
			
			//if program found, skip loop
			if (found)
				continue;
			
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report.append(OPEN_TR);
			report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tf)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(p.getFamilyName()).append(",").append(p.getGivenName()).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getGender(p)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(sdf.format(p.getBirthdate())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(tf.getAgeAtTB03Registration()).append(CLOSE_TD);
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && !xperts.isEmpty()) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report.append(OPEN_CLOSE_TD);
				}
				
				else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage(MDRTB_POSITIVE_SHORT);
						
						if (res != null) {
							int resId = res.getConceptId();
							
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_RESISTANT_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
							
							else if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_SENSITIVE_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
							
							else {
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
						}
						
						else {
							report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
						}
					}
					
					else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					}
					
					else {
						report.append(OPEN_CLOSE_TD);
					}
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 1	
			List<HAINForm> hains = tf.getHains();
			if (hains != null && !hains.isEmpty()) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (rh != null) {
					report.append(ALIGN_LEFT_TAG).append(rh.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tf.getHain2s();
			if (hain2s != null && !hain2s.isEmpty()) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (fq != null) {
					report.append(ALIGN_LEFT_TAG).append(fq.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && !cultures.isEmpty()) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_GROWTH)).append(CLOSE_TD);
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (Integer conc : concs) {
							if (conc.intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_POSITIVE_SHORT)).append(CLOSE_TD);
								break;
							}
							
						}
					}
				}
				
				else {
					report.append(OPEN_CLOSE_TD);
				}
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report.append(ALIGN_LEFT_TAG).append(tf.getResistanceType().getName().getName()).append(CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tf.getDsts())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getSensitiveDrugs(tf.getDsts())).append(CLOSE_TD);
			
			if (tf.getHivStatus() != null) {
				report.append(ALIGN_LEFT_TAG).append(tf.getHivStatus().getName().getName()).append(CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			if (tf.getTreatmentOutcome() != null) {
				report.append(ALIGN_LEFT_TAG).append(tf.getTreatmentOutcome().getName().getName()).append(CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			if (tf.getTreatmentOutcomeDate() != null) {
				report.append(ALIGN_LEFT_TAG).append(sdf.format(tf.getTreatmentOutcomeDate())).append(CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//////////////////////////////////////
			
			report.append(OPEN_CLOSE_TD);
			
			report.append(ALIGN_LEFT_TAG).append(getPatientLink(tf.getPatient(), restfulLink)).append(CLOSE_TD);
			report.append(CLOSE_TR);
			
		}
		
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	/* DR-TB Patients with Successful Treatment */
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatientsSuccessfulTreatment")
	public String drTbPatientsSuccessfulTreatment(@RequestParam(DISTRICT) Integer districtId,
	        @RequestParam(OBLAST) Integer oblastId, @RequestParam(FACILITY) Integer facilityId,
	        @RequestParam(value = YEAR, required = true) Integer year,
	        @RequestParam(value = QUARTER, required = false) String quarter,
	        @RequestParam(value = MONTH, required = false) String month, ModelMap model) {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		model.addAttribute(OBLAST, oName);
		model.addAttribute(DISTRICT, dName);
		model.addAttribute(FACILITY, fName);
		model.addAttribute(YEAR, year);
		model.addAttribute(MONTH, month);
		model.addAttribute(QUARTER, quarter);
		model.addAttribute("listName", getMessage(MDRTB_DR_TB_PATIENTS_SUCCESSFUL_TREATMENT));
		
		List<Location> locList;
		if (oblastId == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDrTbCasesWithSuccessfulTreatmentTable(locList, year, quarterInt, monthInt, monthInt, false);
		model.addAttribute("report", report);
		return RETURN_LINK;
		
	}
	
	public static String getDrTbCasesWithSuccessfulTreatmentTable(List<Location> locList, Integer year, Integer quarterInt,
	        Integer monthInt, Integer month2Int, boolean restfulLink) {
		List<TB03uForm> tb03us = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarterInt,
		    monthInt, month2Int);
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		Collections.sort(tb03us);
		
		StringBuilder report = new StringBuilder();
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report.append(OPEN_TABLE);
		report.append(OPEN_TR);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_SERIAL_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_U_REGISTRATION_NUMBER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_U_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_NAME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_GENDER)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_DATE_OF_BIRTH)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_AGE_AT_REGISTRATION)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_XPERT)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_1)).append(CLOSE_TD);
		report.append("<td align=\"center\" colspan=\"3\">").append(getMessage(MDRTB_HAIN_2)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_CULTURE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_DRUG_RESISTANCE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RESISTANT_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_SENSITIVE_TO)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_HIV_STATUS)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_REGIMEN)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_START_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_U_CHANGE_OF_REGIMEN)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_TB_03_TREATMENT_START_DATE)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_OUTCOME)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_END_OF_TREATMENT_DATE)).append(CLOSE_TD);
		
		report.append(CLOSE_TR);
		
		report.append(OPEN_TR);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INH_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_RIF_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_RESULT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_INJECTABLES_SHORT)).append(CLOSE_TD);
		report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_QUIN_SHORT)).append(CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		report.append(OPEN_CLOSE_TD);
		
		report.append(CLOSE_TR);
		
		RegimenForm rf;
		int curedId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED).getConceptId();
		int txCompId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETE).getConceptId();
		int i = 0;
		Person p;
		for (TB03uForm tuf : tb03us) {
			
			if (tuf.getPatient() == null || tuf.getPatient().getVoided())
				continue;
			
			if (tuf.getTreatmentOutcome() == null
			        || (tuf.getTreatmentOutcome().getConceptId() != curedId && tuf.getTreatmentOutcome().getConceptId() != txCompId))
				continue;
			
			//tuf = Context.getService(MdrtbService.class).getTB03uFormForProgram(tf.getPatient(), tf.getPatientProgramId());
			
			i++;
			p = Context.getPersonService().getPerson(tuf.getPatient().getId());
			report.append(OPEN_TR);
			report.append(ALIGN_LEFT_TAG).append(i).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getRegistrationNumber(tuf)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(sdf.format(tuf.getEncounterDatetime())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(p.getFamilyName()).append(",").append(p.getGivenName()).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getGender(p)).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(sdf.format(p.getBirthdate())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(tuf.getAgeAtMDRRegistration()).append(CLOSE_TD);
			
			//XPERT
			List<XpertForm> xperts = tuf.getXperts();
			if (xperts != null && !xperts.isEmpty()) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report.append(OPEN_CLOSE_TD);
				}
				
				else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage(MDRTB_POSITIVE_SHORT);
						
						if (res != null) {
							int resId = res.getConceptId();
							
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_RESISTANT_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
							
							else if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED)
							        .getConceptId()) {
								xr += "/" + getMessage(MDRTB_SENSITIVE_SHORT);
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
							
							else {
								report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
							}
						}
						
						else {
							report.append(ALIGN_LEFT_TAG).append(xr).append(CLOSE_TD);
						}
					}
					
					else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					}
					
					else {
						report.append(OPEN_CLOSE_TD);
					}
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 1	
			List<HAINForm> hains = tuf.getHains();
			if (hains != null && !hains.isEmpty()) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (rh != null) {
					report.append(ALIGN_LEFT_TAG).append(rh.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tuf.getHain2s();
			if (hain2s != null && !hain2s.isEmpty()) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report.append(ALIGN_LEFT_TAG).append(res.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (ih != null) {
					report.append(ALIGN_LEFT_TAG).append(ih.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (fq != null) {
					report.append(ALIGN_LEFT_TAG).append(fq.getName().getName()).append(CLOSE_TD);
				} else {
					report.append(OPEN_CLOSE_TD);
				}
				
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			//CULTURE
			List<CultureForm> cultures = tuf.getCultures();
			if (cultures != null && !cultures.isEmpty()) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_NEGATIVE_SHORT)).append(CLOSE_TD);
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_LISTS_GROWTH)).append(CLOSE_TD);
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (Integer conc : concs) {
							if (conc.intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report.append(ALIGN_LEFT_TAG).append(getMessage(MDRTB_POSITIVE_SHORT)).append(CLOSE_TD);
								break;
							}
							
						}
					}
				}
				
				else {
					report.append(OPEN_CLOSE_TD);
				}
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			//Drug Resistance
			if (tuf.getResistanceType() != null) {
				report.append(ALIGN_LEFT_TAG).append(tuf.getResistanceType().getName().getName()).append(CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			report.append(ALIGN_LEFT_TAG).append(getResistantDrugs(tuf.getDsts())).append(CLOSE_TD);
			report.append(ALIGN_LEFT_TAG).append(getSensitiveDrugs(tuf.getDsts())).append(CLOSE_TD);
			
			if (tuf.getHivStatus() != null) {
				report.append(ALIGN_LEFT_TAG).append(tuf.getHivStatus().getName().getName()).append(CLOSE_TD);
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
			}
			
			if (tuf.getPatientCategory() != null)
				report.append(ALIGN_LEFT_TAG).append(tuf.getPatientCategory().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			if (tuf.getMdrTreatmentStartDate() != null)
				report.append(ALIGN_LEFT_TAG).append(sdf.format(tuf.getMdrTreatmentStartDate())).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			rf = getFirstRegimenChangeForPatient(tuf.getPatient(), tuf.getPatientProgramId());
			
			if (rf != null) {
				if (rf.getSldRegimenType() != null) {
					report.append(ALIGN_LEFT_TAG).append(rf.getSldRegimenType().getName().getName()).append(CLOSE_TD);
				}
				
				else {
					report.append(OPEN_CLOSE_TD);
				}
				
				if (rf.getCouncilDate() != null) {
					report.append(ALIGN_LEFT_TAG).append(sdf.format(rf.getCouncilDate())).append(CLOSE_TD);
				}
				
				else {
					report.append(OPEN_CLOSE_TD);
				}
			}
			
			else {
				report.append(OPEN_CLOSE_TD);
				report.append(OPEN_CLOSE_TD);
			}
			
			if (tuf.getTreatmentOutcome() != null)
				report.append(ALIGN_LEFT_TAG).append(tuf.getTreatmentOutcome().getName().getName()).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			if (tuf.getTreatmentOutcomeDate() != null)
				report.append(ALIGN_LEFT_TAG).append(sdf.format(tuf.getTreatmentOutcomeDate())).append(CLOSE_TD);
			else
				report.append(OPEN_CLOSE_TD);
			
			report.append(ALIGN_LEFT_TAG).append(getPatientLink(tuf.getPatient(), restfulLink)).append(CLOSE_TD);
			report.append(CLOSE_TR);
		}
		report.append(CLOSE_TABLE);
		report.append(getMessage(MDRTB_NUMBER_OF_RECORDS)).append(": ").append(i);
		return report.toString();
	}
	
	////////////////////// UTILITY FUNCTIONS //////////////////////////
	private static String renderPerson(Person p, boolean gender) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		String ret = "";
		ret += ALIGN_LEFT_TAG + p.getFamilyName() + "," + p.getGivenName() + CLOSE_TD;
		
		if (gender) {
			// Was a second, independent copy of the same two-bucket logic, mapping every non-"M"
			// code to female and throwing on a null gender. One lookup now serves both call sites.
			ret += ALIGN_LEFT_TAG + getGender(p) + CLOSE_TD;
		}
		ret += ALIGN_LEFT_TAG + dateFormat.format(p.getBirthdate()) + CLOSE_TD;
		return ret;
		
	}
	
	private static String renderDate(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		return dateFormat.format(date);
	}
	
	private static String getRegistrationNumber(TB03Form form) {
		String val;
		val = form.getRegistrationNumber();
		if (val == null || val.isEmpty()) {
			val = getMessage(MDRTB_UNASSIGNED);
		}
		return val;
	}
	
	private static String getRegistrationNumber(TB03uForm form) {
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs idObs = MdrtbUtil.getObsFromEncounter(ppidConcept, form.getEncounter());
		if (idObs != null) {
			Integer ppid = idObs.getValueNumeric().intValue();
			PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(ppid);
			if (pp != null) {
				PatientIdentifier pi = Context.getService(MdrtbService.class).getPatientProgramIdentifier(pp);
				
				if (pi != null) {
					return pi.getIdentifier();
				}
			}
		}
		String val = getMessage(MDRTB_UNASSIGNED);
		return (val != null && !val.isEmpty()) ? val : null;
	}
	
	public static String getPatientLink(Patient patient, boolean restfulLink) {
		String link = "<a href=\"" + "../program/enrollment.form?patientId=" + patient.getId() + "\" target=\"_blank\">"
		        + getMessage(MDRTB_VIEW) + "</a>";
		if (restfulLink) {
			link = "<a href=\"" + "../patient/" + patient.getUuid() + "/enrolledprograms\" target=\"_blank\">"
			        + getMessage(MDRTB_VIEW) + "</a>";
		}
		return link;
	}
	
	/**
	 * The translated gender label for the Patient List.
	 * <p>
	 * This report is rendered to HTML here on the server and returned as stringData, so unlike
	 * every other register - where the REST resource returns the stored "M" / "F" and the web app
	 * translates - the lookup has to happen in this renderer.
	 * <p>
	 * Every code gets its own key rather than being sorted into two buckets. The previous version
	 * returned the male label for anything that was not exactly "F", so an unrecorded gender was
	 * reported as male, and threw on a null gender.
	 */
	public static String getGender(Person p) {
		if (p == null || p.getGender() == null) {
			return getMessage(MDRTB_GENDER_PREFIX);
		}
		return getMessage(MDRTB_GENDER_PREFIX + p.getGender());
	}
	
	public static String getTransferFrom(TB03Form tf) {
		TransferInForm tif = getTransferInForm(tf);
		if (tif != null) {
			return tif.getLocation().toString();
		}
		return "";
	}
	
	public static String getTransferFromDate(TB03Form tf) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		TransferInForm tif = getTransferInForm(tf);
		if (tif != null) {
			return dateFormat.format(tif.getEncounterDatetime());
		}
		
		else {
			return "";
		}
	}
	
	public static TransferInForm getTransferInForm(TB03Form tf) {
		TransferInForm tif = null;
		
		Integer ppid = tf.getPatientProgramId();
		TbPatientProgram tpp = Context.getService(MdrtbService.class).getTbPatientProgram(ppid);
		tpp.getDateEnrolled();
		Date endDate = tpp.getDateCompleted();
		
		List<TransferInForm> allTifs = Context.getService(MdrtbService.class).getTransferInFormsFilledForPatient(
		    tf.getPatient());
		
		for (TransferInForm temp : allTifs) {
			if (tf.getEncounterDatetime().equals(temp.getEncounterDatetime())) {
				tif = temp;
				break;
			} else if (tf.getEncounterDatetime().before(temp.getEncounterDatetime())) {
				if (endDate != null && temp.getEncounterDatetime().before(endDate)) {
					tif = temp;
					break;
				} else if (endDate == null) {
					tif = temp;
					break;
				}
			}
		}
		return tif;
	}
	
	public static String getSiteOfDisease(TB03Form tf) {
		if (tf.getAnatomicalSite() != null) {
			return tf.getAnatomicalSite().getName().getName();
		} else {
			return "";
		}
	}
	
	public static String getRegistrationGroup(TB03Form tf) {
		if (tf.getRegistrationGroup() != null) {
			return tf.getRegistrationGroup().getName().getName();
		}
		return "";
	}
	
	public static String getResistantDrugs(List<DSTForm> dsts) {
		String drugs = "";
		if (!dsts.isEmpty()) {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			if (dst != null) {
				String display = dst.getResistantDrugs();
				return display.replace(",", BR_TAG);
			}
		}
		return drugs;
	}
	
	public static String getSensitiveDrugs(List<DSTForm> dsts) {
		String drugs = "";
		if (!dsts.isEmpty()) {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			if (dst != null) {
				String display = dst.getSensitiveDrugs();
				return display.replace(",", BR_TAG);
			}
		}
		return drugs;
	}
	
	public static String getReRegistrationNumber(TB03Form tf) {
		String ret = "";
		Integer ppid = tf.getPatientProgramId();
		if (ppid == null)
			return ret;
		Patient p = tf.getPatient();
		MdrtbService ms = Context.getService(MdrtbService.class);
		List<TbPatientProgram> tpps = ms.getTbPatientPrograms(p);
		if (tpps == null || tpps.size() <= 1) {
			return ret;
		}
		Collections.sort(tpps);
		int numPrograms = tpps.size();
		int index = 0;
		int foundIndex = -1;
		for (TbPatientProgram tpp : tpps) {
			if (tpp == null || tpp.getId() == null)
				continue;
			if (tpp.getId().intValue() == ppid.intValue()) {
				foundIndex = index;
				break;
			}
			index++;
		}
		if (foundIndex != -1) {
			if (foundIndex + 1 < numPrograms) {
				if (tpps.get(foundIndex + 1).getPatientIdentifier() != null)
					return tpps.get(foundIndex + 1).getPatientIdentifier().getIdentifier();
			}
		}
		return ret;
	}
	
	private static RegimenForm getFirstRegimenChangeForPatient(Patient p, Integer patientProgramId) {
		List<RegimenForm> forms = Context.getService(MdrtbService.class).getRegimenFormsForProgram(p, patientProgramId);
		if (forms == null)
			return null;
		if (forms.size() >= 2) {
			return forms.get(1);
		}
		return null;
	}
	
	private static String getMessage(String code) {
		return Context.getService(MessagePropertyService.class).getMessage(code);
	}
}
