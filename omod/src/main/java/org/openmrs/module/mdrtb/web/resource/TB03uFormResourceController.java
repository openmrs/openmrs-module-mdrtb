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
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03Form;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03uForm;
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
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03u", supportedClass = SimpleTB03uForm.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class TB03uFormResourceController extends DataDelegatingCrudResource<SimpleTB03uForm> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("patientProgramUuid");
		description.addProperty("relapseMonth");
		description.addProperty("clinicalNotes");
		description.addProperty("otherCauseOfDeath");
		description.addProperty("nameOfTreatmentLocation");
		description.addProperty("sldRegisterNumber");
		description.addProperty("weight");
		description.addProperty("mdrTreatmentStartDate");
		description.addProperty("artStartDate");
		description.addProperty("pctStartDate");
		description.addProperty("hivTestDate");
		description.addProperty("treatmentOutcomeDate");
		description.addProperty("dateOfDeathAfterTreatmentOutcome");
		description.addProperty("confirmationDate");
		description.addProperty("anatomicalSite", representation);
		description.addProperty("patientCategory", representation);
		description.addProperty("registrationGroup", representation);
		description.addProperty("hivStatus", representation);
		description.addProperty("causeOfDeath", representation);
		description.addProperty("treatmentOutcome", representation);
		description.addProperty("basisForDiagnosis", representation);
		description.addProperty("mdrStatus", representation);
		description.addProperty("registrationGroupByDrug", representation);
		description.addProperty("relapsed", representation);
		description.addProperty("resistanceType", representation);
		description.addProperty("treatmentLocation", representation);
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
			        .property("patientProgramUuid", new StringProperty())
			        .property("ageAtTB03Registration", new IntegerProperty())
			        .property("relapseMonth", new IntegerProperty()).property("clinicalNotes", new StringProperty())
			        .property("otherCauseOfDeath", new StringProperty())
			        .property("nameOfTreatmentLocation", new StringProperty())
			        .property("sldRegisterNumber", new StringProperty()).property("weight", new StringProperty())
			        .property("mdrTreatmentStartDate", new DateProperty()).property("artStartDate", new DateProperty())
			        .property("pctStartDate", new DateProperty()).property("hivTestDate", new DateProperty())
			        .property("treatmentOutcomeDate", new DateProperty())
			        .property("dateOfDeathAfterTreatmentOutcome", new DateProperty())
			        .property("anatomicalSite", new RefProperty("#/definitions/ConceptGet"))
			        .property("patientCategory", new RefProperty("#/definitions/ConceptGet"))
			        .property("registrationGroup", new RefProperty("#/definitions/ConceptGet"))
			        .property("hivStatus", new RefProperty("#/definitions/ConceptGet"))
			        .property("causeOfDeath", new RefProperty("#/definitions/ConceptGet"))
			        .property("treatmentOutcome", new RefProperty("#/definitions/ConceptGet"))
			        .property("basisForDiagnosis", new RefProperty("#/definitions/ConceptGet"))
			        .property("mdrStatus", new RefProperty("#/definitions/ConceptGet"))
			        .property("registrationGroupByDrug", new RefProperty("#/definitions/ConceptGet"))
			        .property("relapsed", new RefProperty("#/definitions/ConceptGet"))
			        .property("resistanceType", new RefProperty("#/definitions/ConceptGet"))
			        .property("treatmentOutcome", new RefProperty("#/definitions/ConceptGet"));
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
	public SimpleTB03uForm newDelegate() {
		return new SimpleTB03uForm();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleTB03Form simpleTB03Form) {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleTB03Form.getEncounter().getUuid());
		sb.append(" ");
		sb.append(simpleTB03Form.getPatientProgramUuid());
		return sb.toString();
	}
	
	@PropertySetter("obs")
	public static void setObs(SimpleTB03Form instance, Set<Obs> obs) {
		Encounter enc = instance.getEncounter();
		enc.getAllObs(true).clear();
		for (Obs o : obs)
			enc.addObs(o);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleTB03uForm> simpleTB03uForms = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_TB03U_MDRTB_INTAKE)) {
					TB03uForm tb03u = new TB03uForm(encounter);
					simpleTB03uForms.add(new SimpleTB03uForm(tb03u));
				}
			}
			return new NeedsPaging<>(simpleTB03uForms, context);
		}
		return null;
	}
	
	@Override
	public SimpleTB03uForm getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		SimpleTB03uForm simpleTB03uForm = new SimpleTB03uForm(new TB03uForm(encounter));
		return simpleTB03uForm;
	}
	
	@Override
	public SimpleTB03uForm save(SimpleTB03uForm delegate) throws ResponseException {
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
		TB03uForm tb03u = new TB03uForm(delegate.getEncounter());
		tb03u = formService.processTB03uForm(tb03u, null);
		return new SimpleTB03uForm(tb03u);
	}
	
	@Override
	protected void delete(SimpleTB03uForm delegate, String reason, RequestContext context) throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleTB03uForm delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
