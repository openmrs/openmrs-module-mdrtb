package org.openmrs.module.mdrtb.web.resource.lab;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/commonlab/labtestattributetype", supportedClass = LabTestAttributeType.class, supportedOpenmrsVersions = { "2.0.*, 2.1.*, 2.2.*, 2.3.*, 2.8.*" })
public class LabTestAttributeTypeResourceController extends MetadataDelegatingCrudResource<LabTestAttributeType> {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	private LabTestService LabTestService = Context.getService(LabTestService.class);
	
	@Override
	public LabTestAttributeType getByUniqueId(String s) {
		return LabTestService.getLabTestAttributeTypeByUuid(s);
	}
	
	@Override
	public LabTestAttributeType newDelegate() {
		return new LabTestAttributeType();
	}
	
	@Override
	public LabTestAttributeType save(LabTestAttributeType labTestAttributeType) {
		return LabTestService.saveLabTestAttributeType(labTestAttributeType);
	}
	
	@Override
	public void purge(LabTestAttributeType labTestAttributeType, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String testTypeUuid = context.getRequest().getParameter("testTypeUuid");
		LabTestType labTestType = LabTestService.getLabTestTypeByUuid(testTypeUuid);
		List<LabTestAttributeType> attributeTypes = LabTestService.getLabTestAttributeTypes(labTestType, true);
		return new NeedsPaging<LabTestAttributeType>(attributeTypes, context);
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("display");
		if (representation instanceof DefaultRepresentation) {
			description.addProperty("name");
			description.addProperty("labTestType");
			description.addProperty("description");
			description.addProperty("sortWeight");
			description.addProperty("groupName");
			description.addProperty("multisetName");
			description.addProperty("maxOccurs");
			description.addProperty("datatypeClassname");
			description.addProperty("datatypeConfig");
			description.addProperty("preferredHandlerClassname");
			description.addProperty("handlerConfig");
			return description;
		} else if (representation instanceof FullRepresentation) {
			description.addProperty("name");
			description.addProperty("labTestType");
			description.addProperty("description");
			description.addProperty("sortWeight");
			description.addProperty("groupName");
			description.addProperty("multisetName");
			description.addProperty("maxOccurs");
			description.addProperty("datatypeClassname");
			description.addProperty("datatypeConfig");
			description.addProperty("preferredHandlerClassname");
			description.addProperty("handlerConfig");
			description.addProperty("creator");
			description.addProperty("dateCreated");
			description.addProperty("changedBy");
			description.addProperty("dateChanged");
			description.addProperty("retired");
			description.addProperty("dateRetired");
			description.addProperty("retiredBy");
			description.addProperty("retireReason");
			return description;
		} else if (representation instanceof RefRepresentation) {
			description.addProperty("name");
			description.addProperty("labTestType");
			description.addProperty("groupName");
			description.addProperty("multisetName");
			description.addProperty("datatypeClassname");
		}
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty("labTestType");
		description.addRequiredProperty("datatypeClassname");
		description.addProperty("description");
		description.addProperty("sortWeight");
		description.addProperty("maxOccurs");
		description.addProperty("datatypeConfig");
		description.addProperty("preferredHandlerClassname");
		description.addProperty("handlerConfig");
		description.addProperty("groupName");
		description.addProperty("multisetName");
		description.addProperty("hint");
		return description;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<LabTestAttributeType> list = LabTestService.getAllLabTestAttributeTypes(false);
		return new NeedsPaging<LabTestAttributeType>(list, context);
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @return description as Display
	 */
	@Override
	@PropertyGetter("display")
	public String getDisplayString(LabTestAttributeType labTestAttributeType) {
		if (labTestAttributeType == null)
			return "";
		return labTestAttributeType.getDescription();
	}
}
