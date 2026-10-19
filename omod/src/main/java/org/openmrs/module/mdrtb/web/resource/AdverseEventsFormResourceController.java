package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.web.dto.SimpleAdverseEventsForm;
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
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/adverseevents", supportedClass = SimpleAdverseEventsForm.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class AdverseEventsFormResourceController extends DataDelegatingCrudResource<SimpleAdverseEventsForm> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("patientProgramUuid");
		description.addProperty("actionTakenSummary", representation);
		description.addProperty("clinicianNotes", representation);
		description.addProperty("comments", representation);
		description.addProperty("diagnosticSummary", representation);
		description.addProperty("facility", representation);
		description.addProperty("link", representation);
		description.addProperty("suspectedDrug", representation);
		description.addProperty("treatmentRegimenAtOnset", representation);
		description.addProperty("outcomeDate", representation);
		description.addProperty("yellowCardDate", representation);
		description.addProperty("actionOutcome", representation);
		description.addProperty("actionTaken", representation);
		description.addProperty("actionTaken2", representation);
		description.addProperty("actionTaken3", representation);
		description.addProperty("actionTaken4", representation);
		description.addProperty("actionTaken5", representation);
		description.addProperty("advereEvent", representation);
		description.addProperty("albuminDone", representation);
		description.addProperty("alkalinePhosphateDone", representation);
		description.addProperty("altDone", representation);
		description.addProperty("amylaseDone", representation);
		description.addProperty("astDone", representation);
		description.addProperty("audiogramDone", representation);
		description.addProperty("bilirubinDone", representation);
		description.addProperty("bloodGlucoseDone", representation);
		description.addProperty("calciumDone", representation);
		description.addProperty("casualityAssessmentResult", representation);
		description.addProperty("casualityAssessmentResult2", representation);
		description.addProperty("casualityAssessmentResult3", representation);
		description.addProperty("casualityDrug", representation);
		description.addProperty("casualityDrug2", representation);
		description.addProperty("casualityDrug3", representation);
		description.addProperty("cbcDone", representation);
		description.addProperty("clinicalScreenDone", representation);
		description.addProperty("diagnosticInvestigation", representation);
		description.addProperty("drugRechallenge", representation);
		description.addProperty("ecgDone", representation);
		description.addProperty("lipaseDone", representation);
		description.addProperty("magnesiumDone", representation);
		description.addProperty("neuroInvestigationDone", representation);
		description.addProperty("otherTestDone", representation);
		description.addProperty("potassiumDone", representation);
		description.addProperty("requiresAncillaryDrugs", representation);
		description.addProperty("requiresDoseChange", representation);
		description.addProperty("serumCreatinineDone", representation);
		description.addProperty("simpleHearingTestDone", representation);
		description.addProperty("thyroidTestDone", representation);
		description.addProperty("typeOfEvent", representation);
		description.addProperty("typeOfSAE", representation);
		description.addProperty("typeOfSpecialEvent", representation);
		description.addProperty("visualAcuityDone", representation);
		description.addProperty("ygtDone", representation);
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
			        .property("actionTakenSummary", new StringProperty()).property("clinicianNotes", new StringProperty())
			        .property("comments", new StringProperty()).property("diagnosticSummary", new StringProperty())
			        .property("facility", new StringProperty()).property("link", new StringProperty())
			        .property("suspectedDrug", new StringProperty())
			        .property("treatmentRegimenAtOnset", new StringProperty()).property("outcomeDate", new DateProperty())
			        .property("yellowCardDate", new DateProperty())
			        .property("actionOutcome", new RefProperty("#/definitions/ConceptGet"))
			        .property("actionTaken", new RefProperty("#/definitions/ConceptGet"))
			        .property("actionTaken2", new RefProperty("#/definitions/ConceptGet"))
			        .property("actionTaken3", new RefProperty("#/definitions/ConceptGet"))
			        .property("actionTaken4", new RefProperty("#/definitions/ConceptGet"))
			        .property("actionTaken5", new RefProperty("#/definitions/ConceptGet"))
			        .property("advereEvent", new RefProperty("#/definitions/ConceptGet"))
			        .property("albuminDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("alkalinePhosphateDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("altDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("amylaseDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("astDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("audiogramDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("bilirubinDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("bloodGlucoseDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("calciumDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("casualityAssessmentResult", new RefProperty("#/definitions/ConceptGet"))
			        .property("casualityAssessmentResult2", new RefProperty("#/definitions/ConceptGet"))
			        .property("casualityAssessmentResult3", new RefProperty("#/definitions/ConceptGet"))
			        .property("casualityDrug", new RefProperty("#/definitions/ConceptGet"))
			        .property("casualityDrug2", new RefProperty("#/definitions/ConceptGet"))
			        .property("casualityDrug3", new RefProperty("#/definitions/ConceptGet"))
			        .property("cbcDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("clinicalScreenDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("diagnosticInvestigation", new RefProperty("#/definitions/ConceptGet"))
			        .property("drugRechallenge", new RefProperty("#/definitions/ConceptGet"))
			        .property("ecgDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("lipaseDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("magnesiumDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("neuroInvestigationDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("otherTestDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("potassiumDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("requiresAncillaryDrugs", new RefProperty("#/definitions/ConceptGet"))
			        .property("requiresDoseChange", new RefProperty("#/definitions/ConceptGet"))
			        .property("serumCreatinineDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("simpleHearingTestDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("thyroidTestDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("typeOfEvent", new RefProperty("#/definitions/ConceptGet"))
			        .property("typeOfSAE", new RefProperty("#/definitions/ConceptGet"))
			        .property("typeOfSpecialEvent", new RefProperty("#/definitions/ConceptGet"))
			        .property("visualAcuityDone", new RefProperty("#/definitions/ConceptGet"))
			        .property("ygtDone", new RefProperty("#/definitions/ConceptGet"));
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
	public SimpleAdverseEventsForm newDelegate() {
		return new SimpleAdverseEventsForm();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleAdverseEventsForm simpleForm89s) {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleForm89s.getEncounter().getUuid());
		sb.append(" ");
		sb.append(simpleForm89s.getPatientProgramUuid());
		return sb.toString();
	}
	
	@PropertySetter("obs")
	public static void setObs(SimpleAdverseEventsForm instance, Set<Obs> obs) {
		Encounter enc = instance.getEncounter();
		enc.getAllObs(true).clear();
		for (Obs o : obs)
			enc.addObs(o);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleAdverseEventsForm> simpleAeForms = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			Collection<EncounterType> types = new ArrayList<>();
			types.add(MdrtbConstants.ET_ADVERSE_EVENT);
			List<Encounter> encs = Context.getService(MdrtbService.class).getEncountersByPatientAndTypes(patient, types);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_ADVERSE_EVENT)) {
					AdverseEventsForm aeForm = new AdverseEventsForm(encounter);
					simpleAeForms.add(new SimpleAdverseEventsForm(aeForm));
				}
			}
			return new NeedsPaging<>(simpleAeForms, context);
		}
		return null;
	}
	
	@Override
	public SimpleAdverseEventsForm getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		SimpleAdverseEventsForm simpleAeForm = new SimpleAdverseEventsForm(new AdverseEventsForm(encounter));
		return simpleAeForm;
	}
	
	@Override
	public SimpleAdverseEventsForm save(SimpleAdverseEventsForm delegate) throws ResponseException {
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
		AdverseEventsForm aeForm = new AdverseEventsForm(delegate.getEncounter());
		aeForm = formService.processAdverseEventsForm(aeForm);
		return new SimpleAdverseEventsForm(aeForm);
	}
	
	@Override
	protected void delete(SimpleAdverseEventsForm delegate, String reason, RequestContext context) throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleAdverseEventsForm delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
