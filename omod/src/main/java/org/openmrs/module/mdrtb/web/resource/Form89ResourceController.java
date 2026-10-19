package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.exception.ResourceNotFoundException;
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
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.web.dto.SimpleForm89;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/form89", supportedClass = SimpleForm89.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class Form89ResourceController extends DataDelegatingCrudResource<SimpleForm89> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("patientProgramUuid");
		description.addProperty("ageAtTB03Registration");
		description.addProperty("yearOfTB03Registration");
		description.addProperty("isChildbearingAge");
		description.addProperty("isPulmonary");
		description.addProperty("address");
		description.addProperty("cityOfOrigin");
		description.addProperty("clinicianNotes");
		description.addProperty("cmacNumber");
		description.addProperty("complication");
		description.addProperty("countryOfOrigin");
		description.addProperty("link");
		description.addProperty("nameOfDoctor");
		description.addProperty("otherDisease");
		description.addProperty("otherMethodOfDetection");
		description.addProperty("registrationNumber");
		description.addProperty("cmacDate");
		description.addProperty("dateOfBirth");
		description.addProperty("dateFirstSeekingHelp");
		description.addProperty("dateOfDecaySurvey");
		description.addProperty("dateOfReturn");
		description.addProperty("form89Date");
		description.addProperty("cancer", representation);
		description.addProperty("circumstancesOfDetection", representation);
		description.addProperty("cnsdl", representation);
		description.addProperty("diabetes", representation);
		description.addProperty("anatomicalSite", representation);
		description.addProperty("ptbLocation", representation);
		description.addProperty("eptbLocation", representation);
		description.addProperty("eptbSite", representation);
		description.addProperty("hepatitis", representation);
		description.addProperty("htHeartDisease", representation);
		description.addProperty("ibc20", representation);
		description.addProperty("kidneyDisease", representation);
		description.addProperty("locationType", representation);
		description.addProperty("mentalDisorder", representation);
		description.addProperty("methodOfDetection", representation);
		description.addProperty("noDisease", representation);
		description.addProperty("placeOfCommission", representation);
		description.addProperty("placeOfDetection", representation);
		description.addProperty("populationCategory", representation);
		description.addProperty("pregnant", representation);
		description.addProperty("prescribedTreatment", representation);
		description.addProperty("presenceOfDecay", representation);
		description.addProperty("profession", representation);
		description.addProperty("ulcer", representation);
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
			        .property("yearOfTB03Registration", new IntegerProperty())
			        .property("isChildbearingAge", new StringProperty()).property("isPulmonary", new StringProperty())
			        .property("address", new StringProperty()).property("cityOfOrigin", new StringProperty())
			        .property("clinicianNotes", new StringProperty()).property("cmacNumber", new StringProperty())
			        .property("complication", new StringProperty()).property("countryOfOrigin", new StringProperty())
			        .property("link", new StringProperty()).property("nameOfDoctor", new StringProperty())
			        .property("otherDisease", new StringProperty()).property("otherMethodOfDetection", new StringProperty())
			        .property("cmacDate", new DateProperty()).property("dateOfBirth", new DateProperty())
			        .property("dateFirstSeekingHelp", new DateProperty()).property("dateOfDecaySurvey", new DateProperty())
			        .property("dateOfReturn", new DateProperty()).property("form89Date", new DateProperty())
			        .property("cancer", new RefProperty("#/definitions/ConceptGet"))
			        .property("circumstancesOfDetection", new RefProperty("#/definitions/ConceptGet"))
			        .property("cnsdl", new RefProperty("#/definitions/ConceptGet"))
			        .property("diabetes", new RefProperty("#/definitions/ConceptGet"))
			        .property("anatomicalSite", new RefProperty("#/definitions/ConceptGet"))
			        .property("ptbLocation", new RefProperty("#/definitions/ConceptGet"))
			        .property("eptbLocation", new RefProperty("#/definitions/ConceptGet"))
			        .property("eptbSite", new RefProperty("#/definitions/ConceptGet"))
			        .property("hepatitis", new RefProperty("#/definitions/ConceptGet"))
			        .property("htHeartDisease", new RefProperty("#/definitions/ConceptGet"))
			        .property("ibc20", new RefProperty("#/definitions/ConceptGet"))
			        .property("kidneyDisease", new RefProperty("#/definitions/ConceptGet"))
			        .property("locationType", new RefProperty("#/definitions/ConceptGet"))
			        .property("mentalDisorder", new RefProperty("#/definitions/ConceptGet"))
			        .property("methodOfDetection", new RefProperty("#/definitions/ConceptGet"))
			        .property("noDisease", new RefProperty("#/definitions/ConceptGet"))
			        .property("placeOfCommission", new RefProperty("#/definitions/ConceptGet"))
			        .property("placeOfDetection", new RefProperty("#/definitions/ConceptGet"))
			        .property("populationCategory", new RefProperty("#/definitions/ConceptGet"))
			        .property("pregnant", new RefProperty("#/definitions/ConceptGet"))
			        .property("prescribedTreatment", new RefProperty("#/definitions/ConceptGet"))
			        .property("presenceOfDecay", new RefProperty("#/definitions/ConceptGet"))
			        .property("profession", new RefProperty("#/definitions/ConceptGet"))
			        .property("ulcer", new RefProperty("#/definitions/ConceptGet"));
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
	public SimpleForm89 newDelegate() {
		return new SimpleForm89();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleForm89 simpleForm89s) {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleForm89s.getEncounter().getUuid());
		sb.append(" ");
		sb.append(simpleForm89s.getPatientProgramUuid());
		return sb.toString();
	}
	
	@PropertySetter("obs")
	public static void setObs(SimpleForm89 instance, Set<Obs> obs) {
		Encounter enc = instance.getEncounter();
		enc.getAllObs(true).clear();
		for (Obs o : obs)
			enc.addObs(o);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleForm89> simpleForm89s = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_FORM89_TB_FOLLOWUP)) {
					Form89 form89 = new Form89(encounter);
					simpleForm89s.add(new SimpleForm89(form89));
				}
			}
			return new NeedsPaging<>(simpleForm89s, context);
		}
		return null;
	}
	
	@Override
	public SimpleForm89 getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		if (encounter == null) {
			throw new ResourceNotFoundException(uuid);
		}
		Form89 form89 = new Form89(encounter);
		form89.initTB03(form89.getPatientProgramId());
		SimpleForm89 simpleForm89 = new SimpleForm89(form89);
		return simpleForm89;
	}
	
	@Override
	public SimpleForm89 save(SimpleForm89 delegate) throws ResponseException {
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
		Integer patientProgramId = null;
		Set<Obs> obsAtTopLevel = delegate.getEncounter().getObsAtTopLevel(false);
		for (Obs obs : obsAtTopLevel) {
			if (obs.getConcept().equals(concept)) {
				PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(
				    delegate.getPatientProgramUuid());
				patientProgramId = patientProgram.getPatientProgramId();
				obs.setValueNumeric(patientProgramId.doubleValue());
				break;
			}
		}
		Form89 form89 = new Form89(delegate.getEncounter());
		form89.initTB03(patientProgramId);
		form89 = formService.processForm89(form89, null);
		return new SimpleForm89(form89);
	}
	
	@Override
	protected void delete(SimpleForm89 delegate, String reason, RequestContext context) throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleForm89 delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
