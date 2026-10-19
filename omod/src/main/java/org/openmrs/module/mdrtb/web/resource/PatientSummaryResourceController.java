package org.openmrs.module.mdrtb.web.resource;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.PatientSummary;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.web.dto.SimplePatientSummaryData;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/patientsummary", supportedClass = SimplePatientSummaryData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class PatientSummaryResourceController extends BaseReportResource<SimplePatientSummaryData> implements Searchable {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("patientUuid");
		description.addProperty("personName", rep);
		description.addProperty("personAddress", rep);
		description.addProperty("gender");
		description.addProperty("dateOfBirth");
		description.addProperty("patientIdentifiers", rep);
		description.addProperty("patientPrograms");
		return description;
	}
	
	@Override
	public SimplePatientSummaryData getByUniqueId(String patientUuid) {
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		if (patient == null) {
			return null;
		}
		PatientSummary patientSummary = Context.getService(MdrtbService.class).getPatientSummary(patient);
		return new SimplePatientSummaryData(patientSummary);
	}
}
