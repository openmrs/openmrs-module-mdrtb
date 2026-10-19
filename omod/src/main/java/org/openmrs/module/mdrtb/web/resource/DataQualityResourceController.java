package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;
import org.openmrs.module.mdrtb.web.controller.reporting.DOTSDQController;
import org.openmrs.module.mdrtb.web.controller.reporting.MDRDQController;
import org.openmrs.module.mdrtb.web.dto.SimpleDQItem;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/dataquality", supportedClass = SimpleObject.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class DataQualityResourceController extends BaseReportResource<SimpleObject> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("identifier");
		description.addProperty("patientName");
		description.addProperty("gender");
		description.addProperty("tb03RegistrationDate");
		description.addProperty("ageAtTB03Registration");
		description.addProperty("dateOfBirth");
		description.addProperty("siteOfDisease");
		description.addProperty("dateFirstSeekingHelp");
		description.addProperty("dateOfReturn");
		description.addProperty("dateOfDecaySurvey");
		description.addProperty("cmacDate");
		description.addProperty("form89Date");
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
		description.addProperty("form89Uuid");
		return description;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String type = context.getRequest().getParameter("type");
		final Map<String, Object> params = processParams(context);
		if (params == null) {
			return new EmptySearchResult();
		}
		List<Location> locList = (List<Location>) params.get("locations");
		Integer year = (Integer) params.get("year");
		Integer quarter = (Integer) params.get("quarter");
		Integer month = (Integer) params.get("month");
		Integer month2 = (Integer) params.get("month2");
		SimpleObject report = new SimpleObject();
		// Fetch the respective report
		Map<String, Object> metrics;
		if ("dots".equalsIgnoreCase(type)) {
			metrics = DOTSDQController.getDOTSQualityMetrics(year, quarter, month, month2, locList);
		} else {
			metrics = MDRDQController.getMDRQualityMetrics(year, quarter, month, month2, locList);
		}
		for(Entry<String, Object> entry : metrics.entrySet()) {
			// Check if the object is a List<DQItem> boxed inside it
			if (entry.getValue() instanceof List) {
				List<DQItem> dqItems = (List<DQItem>) metrics.get(entry.getKey());
				List<SimpleDQItem> simpleDQItems = new ArrayList<>();
				for (DQItem item : dqItems) {
					simpleDQItems.add(new SimpleDQItem(item));
				}
				report.add(entry.getKey(), simpleDQItems);
			} else {
				report.add(entry.getKey(), entry.getValue());
			}
		}
		List<SimpleObject> list = new ArrayList<>();
		list.add(report);
		return new NeedsPaging<>(list, context);
	}
}
