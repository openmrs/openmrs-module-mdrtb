package org.openmrs.module.mdrtb.web.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.web.dto.SimpleReportData;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/reportdata", supportedClass = SimpleReportData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class ReportDataResourceController extends DataDelegatingCrudResource<SimpleReportData> implements Searchable {
	
	private MdrtbService service = Context.getService(MdrtbService.class);
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("location");
		description.addProperty("reportName");
		description.addProperty("description");
		description.addProperty("year");
		description.addProperty("quarter");
		description.addProperty("month");
		description.addProperty("reportType");
		description.addProperty("reportStatus");
		description.addProperty("tableData");
		description.addProperty("dateCreated");
		description.addProperty("creator");
		description.addProperty("dateChanged");
		description.addProperty("changedBy");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = (ModelImpl) super.getGETModel(rep);
		modelImpl.property("uuid", new StringProperty()).property("display", new StringProperty())
		        .property("location", new RefProperty("#/definitions/LocationGet"))
		        .property("reportName", new StringProperty()).property("description", new StringProperty())
		        .property("year", new IntegerProperty()).property("quarter", new IntegerProperty())
		        .property("month", new IntegerProperty()).property("reportType", new StringProperty())
		        .property("reportStatus", new StringProperty()).property("tableData", new StringProperty());
		return modelImpl;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("location", new StringProperty()).property("reportName", new StringProperty())
		        .property("description", new StringProperty()).property("year", new IntegerProperty())
		        .property("quarter", new IntegerProperty()).property("month", new IntegerProperty())
		        .property("reportType", new StringProperty()).property("reportStatus", new StringProperty())
		        .property("tableData", new StringProperty());
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		return getCREATEModel(rep);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("location");
		description.addRequiredProperty("reportName");
		description.addRequiredProperty("tableData");
		description.addRequiredProperty("year");
		description.addProperty("month");
		description.addProperty("quarter");
		description.addProperty("reportType");
		description.addProperty("reportStatus");
		description.addProperty("description");
		return description;
	}
	
	@Override
	public SimpleReportData newDelegate() {
		SimpleReportData reportObject = new SimpleReportData();
		return reportObject;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleReportData reportObject) {
		StringBuilder sb = new StringBuilder();
		sb.append(reportObject.getUuid());
		sb.append(" ");
		sb.append(reportObject.getReportName());
		sb.append(" ");
		sb.append(reportObject.getYear());
		return sb.toString();
	}
	
	@Override
	public SimpleReportData getByUniqueId(String uuid) {
		ReportData reportData = service.getReportDataByUuid(uuid);
		if (reportData == null) {
			throw new ResourceNotFoundException(Context.getService(MessagePropertyService.class).getMessage(
			    "mdrtb.missingScannedLabReport"));
		}
		return new SimpleReportData(reportData, true);
	}
	
	@Override
	public SimpleReportData save(SimpleReportData delegate) throws ResponseException {
		// If the record exists, then we only want to update the Table data, nothing else.
		ReportData reportData = delegate.toReportData();
		ReportData existing = service.getReportDataByUuid(delegate.getUuid());
		if (existing != null) {
			try {
				existing.setTableData(reportData.getTableData());
				service.saveReportData(existing);
			}
			catch (IOException e) {
				log.error(e.getMessage());
			}
		} else {
			service.saveReportData(reportData);
		}
		return delegate;
	}
	
	@Override
	protected void delete(SimpleReportData delegate, String reason, RequestContext context) throws ResponseException {
		service.voidReportData(delegate.toReportData(), reason);
	}
	
	@Override
	public void purge(SimpleReportData delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String yearStr = context.getRequest().getParameter("year");
		String quarterStr = context.getRequest().getParameter("quarter");
		String monthStr = context.getRequest().getParameter("month");
		String regionUuid = context.getRequest().getParameter("region");
		String districtUuid = context.getRequest().getParameter("district");
		String facilityUuid = context.getRequest().getParameter("facility");
		String reportName = context.getRequest().getParameter("name");
		String reportTypeStr = context.getRequest().getParameter("type");
		ReportType reportType = reportTypeStr == null ? null : ReportType.valueOf(reportTypeStr.toUpperCase());
		// If conditions don't meet
		if (yearStr == null || regionUuid == null) {
			return new EmptySearchResult();
		}
		Location region = Context.getLocationService().getLocationByUuid(regionUuid);
		Location district = Context.getLocationService().getLocationByUuid(districtUuid);
		Location facility = Context.getLocationService().getLocationByUuid(facilityUuid);
		Integer year = Integer.parseInt(yearStr);
		Integer quarter = quarterStr == null ? null : Integer.parseInt(quarterStr);
		Integer month = monthStr == null ? null : Integer.parseInt(monthStr);
		List<ReportData> list = service.searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
		List<SimpleReportData> reportObjects = new ArrayList<>();
		for (ReportData reportData : list) {
			reportObjects.add(new SimpleReportData(reportData, false));
		}
		return new NeedsPaging<>(reportObjects, context);
	}
}
