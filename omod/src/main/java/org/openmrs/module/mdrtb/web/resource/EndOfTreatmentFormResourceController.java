package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;
import org.openmrs.module.mdrtb.web.dto.SimpleEndOfTreatmentForm;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

/**
 * REST endpoint for the End of Treatment form: GET/POST {base}/ws/rest/v1/mdrtb/endoftreatment.
 */
@Resource(name = RestConstants.VERSION_1 + "/mdrtb/endoftreatment", supportedClass = SimpleEndOfTreatmentForm.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class EndOfTreatmentFormResourceController extends DataDelegatingCrudResource<SimpleEndOfTreatmentForm> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("patientProgramUuid");
		// TODO (EoT fields): description.addProperty("<field>"[, representation for Concept fields]);
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation) {
			modelImpl.property("uuid", new StringProperty()).property("display", new StringProperty())
			        .property("encounter", new RefProperty("#/definitions/EncounterGet"))
			        .property("patientProgramUuid", new StringProperty());
		} else if (rep instanceof FullRepresentation) {
			modelImpl.property("uuid", new StringProperty()).property("display", new StringProperty())
			        .property("encounter", new RefProperty("#/definitions/EncounterGet"))
			        .property("patientProgramUuid", new StringProperty());
			// TODO (EoT fields): add each field (StringProperty / DateProperty / RefProperty ConceptGet).
		}
		return modelImpl;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("encounter", new RefProperty("#/definitions/EncounterCreate"))
		        .property("patientProgramUuid", new StringProperty()).required("encounter");
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		return getCREATEModel(rep);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("patientProgramUuid"); // optional
		description.addRequiredProperty("encounter");
		return description;
	}
	
	@Override
	public SimpleEndOfTreatmentForm newDelegate() {
		return new SimpleEndOfTreatmentForm();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleEndOfTreatmentForm simpleEndOfTreatmentForm) {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleEndOfTreatmentForm.getEncounter().getUuid());
		sb.append(" ");
		sb.append(simpleEndOfTreatmentForm.getPatientProgramUuid());
		return sb.toString();
	}
	
	@PropertySetter("obs")
	public static void setObs(SimpleEndOfTreatmentForm instance, Set<Obs> obs) {
		Encounter enc = instance.getEncounter();
		enc.getAllObs(true).clear();
		for (Obs o : obs)
			enc.addObs(o);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleEndOfTreatmentForm> simpleEndOfTreatmentForms = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_END_OF_TREATMENT)) {
					EndOfTreatmentForm endOfTreatmentForm = new EndOfTreatmentForm(encounter);
					simpleEndOfTreatmentForms.add(new SimpleEndOfTreatmentForm(endOfTreatmentForm));
				}
			}
			return new NeedsPaging<>(simpleEndOfTreatmentForms, context);
		}
		return null;
	}
	
	@Override
	public SimpleEndOfTreatmentForm getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		return new SimpleEndOfTreatmentForm(new EndOfTreatmentForm(encounter));
	}
	
	@Override
	public SimpleEndOfTreatmentForm save(SimpleEndOfTreatmentForm delegate) throws ResponseException {
		MdrtbFormServiceImpl formService = new MdrtbFormServiceImpl();
		// If no provider is supplied, then fetch the provider from current user by matching Provider Identifier with Username
		if (delegate.getEncounter().getEncounterProviders().isEmpty()) {
			Provider provider = Context.getProviderService().getProviderByIdentifier(
			    Context.getAuthenticatedUser().getUsername());
			if (provider != null) {
				delegate.getEncounter().addProvider(Context.getEncounterService().getEncounterRole(1), provider);
			}
		}
		// If the Patient Program ID obs has null value, then fetch from Patient Program's UUID
		Concept concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Set<Obs> obsAtTopLevel = delegate.getEncounter().getObsAtTopLevel(false);
		for (Obs obs : obsAtTopLevel) {
			if (obs.getConcept().equals(concept)) {
				PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(
				    delegate.getPatientProgramUuid());
				obs.setValueNumeric(patientProgram.getPatientProgramId().doubleValue());
				break;
			}
		}
		EndOfTreatmentForm endOfTreatmentForm = new EndOfTreatmentForm(delegate.getEncounter());
		endOfTreatmentForm = formService.processEndOfTreatmentForm(endOfTreatmentForm);
		return new SimpleEndOfTreatmentForm(endOfTreatmentForm);
	}
	
	@Override
	protected void delete(SimpleEndOfTreatmentForm delegate, String reason, RequestContext context) throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleEndOfTreatmentForm delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
