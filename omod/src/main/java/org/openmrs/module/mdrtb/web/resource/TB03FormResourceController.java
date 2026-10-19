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
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03Form;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03", supportedClass = SimpleTB03Form.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class TB03FormResourceController extends DataDelegatingCrudResource<SimpleTB03Form> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("patientProgramUuid");
		description.addProperty("registrationNumber");
		description.addProperty("ageAtTB03Registration");
		description.addProperty("address");
		description.addProperty("clinicalNotes");
		description.addProperty("nameOfIPFacility");
		description.addProperty("nameOfCPFacility");
		description.addProperty("otherCauseOfDeath");
		description.addProperty("treatmentStartDate");
		description.addProperty("artStartDate");
		description.addProperty("pctStartDate");
		description.addProperty("xrayDate");
		description.addProperty("hivTestDate");
		description.addProperty("treatmentOutcomeDate");
		description.addProperty("dateOfDeathAfterTreatmentOutcome");
		description.addProperty("anatomicalSite", representation);
		description.addProperty("treatmentSiteIP", representation);
		description.addProperty("treatmentSiteCP", representation);
		description.addProperty("patientCategory", representation);
		description.addProperty("registrationGroup", representation);
		description.addProperty("hivStatus", representation);
		description.addProperty("resistanceType", representation);
		description.addProperty("causeOfDeath", representation);
		description.addProperty("treatmentOutcome", representation);
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
			        .property("registrationNumber", new StringProperty()).property("address", new StringProperty())
			        .property("clinicalNotes", new StringProperty()).property("nameOfIPFacility", new StringProperty())
			        .property("nameOfCPFacility", new StringProperty()).property("otherCauseOfDeath", new StringProperty())
			        .property("treatmentStartDate", new DateProperty()).property("artStartDate", new DateProperty())
			        .property("pctStartDate", new DateProperty()).property("xrayDate", new DateProperty())
			        .property("hivTestDate", new DateProperty()).property("treatmentOutcomeDate", new DateProperty())
			        .property("dateOfDeathAfterTreatmentOutcome", new DateProperty())
			        .property("anatomicalSite", new RefProperty("#/definitions/ConceptGet"))
			        .property("treatmentSiteIP", new RefProperty("#/definitions/ConceptGet"))
			        .property("treatmentSiteCP", new RefProperty("#/definitions/ConceptGet"))
			        .property("patientCategory", new RefProperty("#/definitions/ConceptGet"))
			        .property("registrationGroup", new RefProperty("#/definitions/ConceptGet"))
			        .property("hivStatus", new RefProperty("#/definitions/ConceptGet"))
			        .property("resistanceType", new RefProperty("#/definitions/ConceptGet"))
			        .property("causeOfDeath", new RefProperty("#/definitions/ConceptGet"))
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
	public SimpleTB03Form newDelegate() {
		return new SimpleTB03Form();
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
		List<SimpleTB03Form> simpleTB03Forms = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_TB03_TB_INTAKE)) {
                    try {
                        TB03Form tb03 = new TB03Form(encounter);
                        simpleTB03Forms.add(new SimpleTB03Form(tb03));
                    } catch (Exception ignored) {
                    }
                }
			}
			return new NeedsPaging<>(simpleTB03Forms, context);
		}
		return null;
	}
	
	@Override
	public SimpleTB03Form getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		return new SimpleTB03Form(new TB03Form(encounter));
	}
	
	@Override
	public SimpleTB03Form save(SimpleTB03Form delegate) throws ResponseException {
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
		TB03Form tb03 = new TB03Form(delegate.getEncounter());
		tb03 = formService.processTB03Form(tb03, null);
		return new SimpleTB03Form(tb03);
	}
	
	@Override
	protected void delete(SimpleTB03Form delegate, String reason, RequestContext context) throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleTB03Form delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
