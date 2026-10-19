package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.custom.TB03Data;
import org.openmrs.module.mdrtb.web.controller.reporting.TB03ExportController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03Data;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03report", supportedClass = SimpleTB03Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class TB03DataResourceController extends BaseReportResource<SimpleTB03Data> implements Searchable {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("patientUuid");
		description.addProperty("personName");
		description.addProperty("personAddress");
		description.addProperty("gender");
		description.addProperty("identifier");
		description.addProperty("tb03RegistrationDate");
		description.addProperty("tb03Facility");
		description.addProperty("ageAtTB03Registration");
		description.addProperty("dateOfBirth");
		description.addProperty("intensivePhaseFacility");
		description.addProperty("continuationPhaseFacility");
		description.addProperty("treatmentRegimen");
		description.addProperty("tb03TreatmentStartDate");
		description.addProperty("siteOfDisease");
		description.addProperty("regGroup");
		description.addProperty("hivTestResult");
		description.addProperty("hivTestDate");
		description.addProperty("artStartDate");
		description.addProperty("cpStartDate");
		description.addProperty("diagnosticSmearResult");
		description.addProperty("diagnosticSmearTestNumber");
		description.addProperty("diagnosticSmearDate");
		description.addProperty("diagnosticSmearLab");
		description.addProperty("xpertMTBResult");
		description.addProperty("xpertRIFResult");
		description.addProperty("xpertTestDate");
		description.addProperty("xpertTestNumber");
		description.addProperty("xpertLab");
		description.addProperty("hainMTBResult");
		description.addProperty("hainINHResult");
		description.addProperty("hainRIFResult");
		description.addProperty("hainTestDate");
		description.addProperty("hainTestNumber");
		description.addProperty("hainLab");
		description.addProperty("hain2MTBResult");
		description.addProperty("hain2InjResult");
		description.addProperty("hain2FqResult");
		description.addProperty("hain2TestDate");
		description.addProperty("hain2TestNumber");
		description.addProperty("hain2Lab");
		description.addProperty("cultureResult");
		description.addProperty("cultureTestDate");
		description.addProperty("cultureTestNumber");
		description.addProperty("cultureLab");
		description.addProperty("drugResistance");
		description.addProperty("month2SmearResult");
		description.addProperty("month2SmearDate");
		description.addProperty("month2TestNumber");
		description.addProperty("month2TestLab");
		description.addProperty("month3SmearResult");
		description.addProperty("month3SmearDate");
		description.addProperty("month3TestNumber");
		description.addProperty("month3TestLab");
		description.addProperty("month4SmearResult");
		description.addProperty("month4SmearDate");
		description.addProperty("month4TestNumber");
		description.addProperty("month4TestLab");
		description.addProperty("month5SmearResult");
		description.addProperty("month5SmearDate");
		description.addProperty("month5TestNumber");
		description.addProperty("month5TestLab");
		description.addProperty("month6SmearResult");
		description.addProperty("month6SmearDate");
		description.addProperty("month6TestNumber");
		description.addProperty("month6TestLab");
		description.addProperty("month8SmearResult");
		description.addProperty("month8SmearDate");
		description.addProperty("month8TestNumber");
		description.addProperty("month8TestLab");
		description.addProperty("tb03TreatmentOutcome");
		description.addProperty("tb03TreatmentOutcomeDate");
		description.addProperty("diedOfTB");
		description.addProperty("notes");
		description.addProperty("reg1New");
		description.addProperty("reg1Rtx");
		description.addProperty("dstCollectionDate");
		description.addProperty("dstResultDate");
		description.addProperty("dstResults");
		return description;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		final Map<String, Object> params = processParams(context);
		if (params == null) {
			return new EmptySearchResult();
		}
		List<Location> locList = (List<Location>) params.get("locations");
		Integer year = (Integer) params.get("year");
		Integer quarter = (Integer) params.get("quarter");
		Integer month = (Integer) params.get("month");
		Integer month2 = (Integer) params.get("month2");
		List<TB03Data> patientSet = TB03ExportController.getTB03PatientSet(year, quarter, month, month2, locList);
		List<SimpleTB03Data> list = new ArrayList<>();
		for (TB03Data tb03Data : patientSet) {
			list.add(new SimpleTB03Data(tb03Data));
		}
		return new NeedsPaging<>(list, context);
	}
}
