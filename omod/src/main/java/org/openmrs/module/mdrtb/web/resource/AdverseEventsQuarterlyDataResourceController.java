package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.module.mdrtb.web.controller.reporting.AdverseEventsReportController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/adverseeventsquarterly", supportedClass = SimpleObject.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class AdverseEventsQuarterlyDataResourceController extends BaseReportResource<SimpleObject> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("patientUuid");
		description.addProperty("table1");
		description.addProperty("table2");
		description.addProperty("table3");
		description.addProperty("table4");
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
		List<Object> tables = AdverseEventsReportController.getPVDataTables(year, quarter, month, month2, locList);
		List<SimpleObject> list = new ArrayList<>();
		for (int i = 0; i < tables.size(); i++) {
			list.add(new SimpleObject().add("table" + (i + 1), tables.get(i)));
		}
		return new NeedsPaging<>(list, context);
	}
}
