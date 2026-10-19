package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.pv.AdverseEventsRegisterData;
import org.openmrs.module.mdrtb.web.controller.reporting.AdverseEventsRegisterController;
import org.openmrs.module.mdrtb.web.dto.SimpleAdverseEventsRegisterData;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/adverseeventsregister", supportedClass = SimpleAdverseEventsRegisterData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class AdverseEventsDataResourceController extends BaseReportResource<SimpleAdverseEventsRegisterData> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("patientUuid");
		description.addProperty("patientIdentifier");
		description.addProperty("patientName");
		description.addProperty("birthdate");
		description.addProperty("onsetDate");
		description.addProperty("aeDescription");
		description.addProperty("diagnosticInvestigation");
		description.addProperty("serious");
		description.addProperty("ofSpecialInterest");
		description.addProperty("ancillaryDrugs");
		description.addProperty("doseChanged");
		description.addProperty("suspectedDrug");
		description.addProperty("suspectedDrugStartDate");
		description.addProperty("actionOutcome");
		description.addProperty("actionTaken");
		description.addProperty("txRegimen");
		description.addProperty("drugRechallenge");
		description.addProperty("yellowCardDate");
		description.addProperty("comments");
		description.addProperty("adverseEventsForm");
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
		List<AdverseEventsRegisterData> aeRegister = AdverseEventsRegisterController.getAdverseEventsRegister(year, quarter, month, month2, locList);
		List<SimpleAdverseEventsRegisterData> list = new ArrayList<>();
		for (AdverseEventsRegisterData aeData : aeRegister) {
			list.add(new SimpleAdverseEventsRegisterData(aeData));
		}
		return new NeedsPaging<>(list, context);
	}
}
