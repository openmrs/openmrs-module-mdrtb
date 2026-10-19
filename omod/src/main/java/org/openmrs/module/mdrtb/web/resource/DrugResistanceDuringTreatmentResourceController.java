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
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.web.dto.SimpleDrugResistanceDuringTreatmentForm;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/drugresistance", supportedClass = SimpleDrugResistanceDuringTreatmentForm.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class DrugResistanceDuringTreatmentResourceController extends DataDelegatingCrudResource<SimpleDrugResistanceDuringTreatmentForm> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("patientProgramUuid");
		description.addProperty("drugResistance", representation);
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = (ModelImpl) super.getGETModel(rep);
		modelImpl.property("uuid", new StringProperty()).property("display", new StringProperty())
		        .property("encounter", new RefProperty("#/definitions/EncounterGet"))
		        .property("patientProgramUuid", new StringProperty())
		        .property("drugResistance", new RefProperty("#/definitions/ConceptGet"));
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
	public SimpleDrugResistanceDuringTreatmentForm newDelegate() {
		return new SimpleDrugResistanceDuringTreatmentForm();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleDrugResistanceDuringTreatmentForm drugResistanceForms) {
		StringBuilder sb = new StringBuilder();
		sb.append(drugResistanceForms.getEncounter().getUuid());
		sb.append(" ");
		sb.append(drugResistanceForms.getPatientProgramUuid());
		return sb.toString();
	}
	
	@PropertySetter("obs")
	public static void setObs(SimpleDrugResistanceDuringTreatmentForm instance, Set<Obs> obs) {
		Encounter enc = instance.getEncounter();
		enc.getAllObs(true).clear();
		for (Obs o : obs)
			enc.addObs(o);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleDrugResistanceDuringTreatmentForm> drugResistanceForms = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_DRUG_RESISTANCE_DURING_TREATMENT)) {
					DrugResistanceDuringTreatmentForm drugResistanceForm = new DrugResistanceDuringTreatmentForm(encounter);
					drugResistanceForms.add(new SimpleDrugResistanceDuringTreatmentForm(drugResistanceForm));
				}
			}
			return new NeedsPaging<>(drugResistanceForms, context);
		}
		return null;
	}
	
	@Override
	public SimpleDrugResistanceDuringTreatmentForm getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		SimpleDrugResistanceDuringTreatmentForm simpleForm89 = new SimpleDrugResistanceDuringTreatmentForm(
		        new DrugResistanceDuringTreatmentForm(encounter));
		return simpleForm89;
	}
	
	@Override
	public SimpleDrugResistanceDuringTreatmentForm save(SimpleDrugResistanceDuringTreatmentForm delegate)
	        throws ResponseException {
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
		DrugResistanceDuringTreatmentForm drugResistanceForm = new DrugResistanceDuringTreatmentForm(delegate.getEncounter());
		drugResistanceForm = formService.processDrugResistanceDuringTreatmentForm(drugResistanceForm);
		return new SimpleDrugResistanceDuringTreatmentForm(drugResistanceForm);
	}
	
	@Override
	protected void delete(SimpleDrugResistanceDuringTreatmentForm delegate, String reason, RequestContext context)
	        throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleDrugResistanceDuringTreatmentForm delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
