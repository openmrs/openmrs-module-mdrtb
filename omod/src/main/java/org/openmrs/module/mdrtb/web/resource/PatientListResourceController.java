package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.web.controller.reporting.PatientListContoller;
import org.openmrs.module.mdrtb.web.dto.SimpleDataObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/patientlist", supportedClass = SimpleDataObject.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class PatientListResourceController extends BaseReportResource<SimpleDataObject> implements Searchable {
	
	private static final String ALL_CASES_ENROLLED = "allenrolled";
	
	private static final String DOTS_CAES_BY_REGISTRATION_GROUP = "dotsbyregistrationgroup";
	
	private static final String DOTS_CASES_BY_ANATOMICAL_SITE = "dotsbyanatomicalsite";
	
	private static final String DOTS_CASES_BY_DRUG_RESISTANCE = "dotsbydrugresistance";
	
	private static final String DOTS_CASES_BY_REGISTRATION_GROUP_AND_BACTERIOLOGICAL_STATUS = "dotsbyregistrationgroupandbacteriologicalstatus";
	
	private static final String MDR_XDR_PATIENTS = "mdrxdrpatients";
	
	private static final String MDR_XDR_PATIENTS_WITH_NO_TREATMENT = "mdrxdrwithnotreatment";
	
	private static final String MDR_SUCCESSFUL_TREATMENT_OUTCOME = "mdrsuccessfultreatmentoutcome";
	
	private static final String WOMEN_OF_CHILD_BEARING_AGE = "womenofchildbearingage";
	
	private static final String MEN_OF_CONSCRIPT_AGE = "menofconscriptage";
	
	private static final String WITH_CONCOMITANT_DISEASE = "withconcomitantdisease";
	
	private static final String WITH_CANCER = "withcancer";
	
	private static final String DETECTED_FROM_CONTACT = "detectedfromcontact";
	
	private static final String WITH_COPD = "withcopd";
	
	private static final String WITH_HYPERTENSION = "withhypertension";
	
	private static final String WITH_ULCER = "withulcer";
	
	private static final String WITH_MENTAL_DISORDER = "withmentaldisorder";
	
	private static final String WITH_HIV = "withhiv";
	
	private static final String WITH_HEPATITIS = "withhepatitis";
	
	private static final String WITH_KIDNEY_DISEASE = "withkidneydisease";
	
	private static final String WITH_OTHER_DISEASE = "withotherdisease";
	
	private static final String BY_SOC_PROF_STATUS = "bysocprofstatus";
	
	private static final String BY_POPULATION_CATEGORY = "bypopulationcategory";
	
	private static final String BY_DWELLING = "bydwelling";
	
	private static final String BY_PLACES_OF_DETECTION = "byplacesofdetection";
	
	private static final String BY_CIRCUMSTANCES_OF_DETECTION = "bycircumstancesofdetection";
	
	private static final String BY_METHOD_OF_DETECTION = "bymethodofdetection";
	
	private static final String BY_PULMONARY_LOCATION = "bypulmonarylocation";
	
	private static final String BY_EXTRA_PULMONARY_LOCATION = "byextrapulmonarylocation";
	
	private static final String DRTB_CASES = "drtbcases";
	
	private static final String DRTB_PATIENTS_WITHOUT_TREATMENT = "drtbpatientswithouttreatment";
	
	private static final String DRTB_PATIENTS_WITH_SUCCESSFUL_TREATMENT = "drtbpatientswithsuccessfultreatment";
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("stringData");
		description.addProperty("integerData");
		description.addProperty("floatData");
		return description;
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String yearStr = context.getRequest().getParameter("year");
		String quarterStr = context.getRequest().getParameter("quarter");
		String monthStr = context.getRequest().getParameter("month");
		String month2Str = context.getRequest().getParameter("month2");
		String locationUuid = context.getRequest().getParameter("location");
		String listName = context.getRequest().getParameter("listname");
		// If conditions don't meet
		if (listName == null || yearStr == null || locationUuid == null) {
			return new EmptySearchResult();
		}
		// Get location by UUID
		Location parent = Context.getLocationService().getLocationByUuid(locationUuid);
		// Get all child locations
		List<Location> locList = Context.getService(MdrtbService.class).getLocationsInHierarchy(parent, true);
		Integer year = Integer.parseInt(yearStr);
		Integer quarter = quarterStr == null ? null : Integer.parseInt(quarterStr);
		Integer month = monthStr == null ? null : Integer.parseInt(monthStr);
		Integer month2 = month2Str == null ? null : Integer.parseInt(month2Str);
		List<SimpleDataObject> tableData = new ArrayList<>();
		String htmlTable = null;
		switch (listName.toLowerCase()) {
			case ALL_CASES_ENROLLED:
				htmlTable = PatientListContoller.getAllCasesEnrolledTable(locList, year, quarter, month, month2, true);
				break;
			case DOTS_CAES_BY_REGISTRATION_GROUP:
				htmlTable = PatientListContoller.getDotsCasesByRegistrationGroupTable(locList, year, quarter, month, month2, true);
				break;
			case DOTS_CASES_BY_ANATOMICAL_SITE:
				htmlTable = PatientListContoller.getDotsCasesByAnatomicalSiteTable(locList, year, quarter, month, month2, true);
				break;
			case DOTS_CASES_BY_DRUG_RESISTANCE:
				htmlTable = PatientListContoller.getDotsCasesByDrugResistanceTable(locList, year, quarter, month, month2, true);
				break;
			case DOTS_CASES_BY_REGISTRATION_GROUP_AND_BACTERIOLOGICAL_STATUS:
				htmlTable = PatientListContoller.getDotsPulmonaryCasesByRegisrationGroupAndBacteriologicalStatusTable(
				    locList, year, quarter, month, month2, true);
				break;
			case MDR_XDR_PATIENTS:
				htmlTable = PatientListContoller.getMdrXdrPatientsTable(locList, year, quarter, month, month2, true);
				break;
			case MDR_XDR_PATIENTS_WITH_NO_TREATMENT:
				htmlTable = PatientListContoller.getMdrXdrPatientsWithNoTreatmentTable(locList, year, quarter, month, month2, true);
				break;
			case MDR_SUCCESSFUL_TREATMENT_OUTCOME:
				htmlTable = PatientListContoller.getMdrSuccessfulTreatmentOutcomeTable(locList, year, quarter, month, month2, true);
				break;
			case WOMEN_OF_CHILD_BEARING_AGE:
				htmlTable = PatientListContoller.getWomenOfChildbearingAgeTable(locList, year, quarter, month, month2, true);
				break;
			case MEN_OF_CONSCRIPT_AGE:
				htmlTable = PatientListContoller.getMenOfConscriptAgeTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_CONCOMITANT_DISEASE:
				htmlTable = PatientListContoller.getCasesWithConcamitantDiseasesTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_CANCER:
				htmlTable = PatientListContoller.getCasesWithCancerTable(locList, year, quarter, month, month2, true);
				break;
			case DETECTED_FROM_CONTACT:
				htmlTable = PatientListContoller.getCasesDetectedFromContactTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_COPD:
				htmlTable = PatientListContoller.getCasesWithCopdTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_HYPERTENSION:
				htmlTable = PatientListContoller.getCasesWithHypertensionTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_ULCER:
				htmlTable = PatientListContoller.getCasesWithUlcerTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_MENTAL_DISORDER:
				htmlTable = PatientListContoller.getCasesWithMentalDisorderTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_HIV:
				htmlTable = PatientListContoller.getCasesWithHivTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_HEPATITIS:
				htmlTable = PatientListContoller.getCasesWithHepatitisTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_KIDNEY_DISEASE:
				htmlTable = PatientListContoller.getCasesWithKidneyDiseaseTable(locList, year, quarter, month, month2, true);
				break;
			case WITH_OTHER_DISEASE:
				htmlTable = PatientListContoller.getCasesWithOtherDiseaseTable(locList, year, quarter, month, month2, true);
				break;
			case BY_SOC_PROF_STATUS:
				htmlTable = PatientListContoller.getCasesBySocProfStatusTable(locList, year, quarter, month, month2, true);
				break;
			case BY_POPULATION_CATEGORY:
				htmlTable = PatientListContoller.getCasesByPopulationCategoryTable(locList, year, quarter, month, month2, true);
				break;
			case BY_DWELLING:
				htmlTable = PatientListContoller.getCasesByDwellingTable(locList, year, quarter, month, month2, true);
				break;
			case BY_PLACES_OF_DETECTION:
				htmlTable = PatientListContoller.getCasesByPlaceOfDetectionTable(locList, year, quarter, month, month2, true);
				break;
			case BY_CIRCUMSTANCES_OF_DETECTION:
				htmlTable = PatientListContoller
				        .getCasesByCircumstancesOfDetectionTable(locList, year, quarter, month, month2, true);
				break;
			case BY_METHOD_OF_DETECTION:
				htmlTable = PatientListContoller.getCasesByMethodOfDetectionTable(locList, year, quarter, month, month2, true);
				break;
			case BY_PULMONARY_LOCATION:
				htmlTable = PatientListContoller.getCasesByPulmonaryLocationTable(locList, year, quarter, month, month2, true);
				break;
			case BY_EXTRA_PULMONARY_LOCATION:
				htmlTable = PatientListContoller.getCasesByExtraPulmonaryLocationTable(locList, year, quarter, month, month2, true);
				break;
			case DRTB_CASES:
				htmlTable = PatientListContoller.getDrtbCasesTable(locList, year, quarter, month, month2, true);
				break;
			case DRTB_PATIENTS_WITHOUT_TREATMENT:
				htmlTable = PatientListContoller.getDrTbCasesWithSuccessfulTreatmentTable(locList, year, quarter, month, month2,
				    true);
				break;
			case DRTB_PATIENTS_WITH_SUCCESSFUL_TREATMENT:
				htmlTable = PatientListContoller.getDrTbCasesWithSuccessfulTreatmentTable(locList, year, quarter, month, month2,
				    true);
				break;
			default:
				break;
		}
		tableData.add(new SimpleDataObject(htmlTable, null, null));
		return new NeedsPaging<>(tableData, context);
	}
}
