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
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.web.dto.SimpleRegimenForm;
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
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/regimen", supportedClass = SimpleRegimenForm.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class RegimenFormResourceController extends DataDelegatingCrudResource<SimpleRegimenForm> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		description.addProperty("encounter", representation);
		description.addProperty("amDose");
		description.addProperty("amxDose");
		description.addProperty("bdqDose");
		description.addProperty("cfzDose");
		description.addProperty("cmDose");
		description.addProperty("csDose");
		description.addProperty("dlmDose");
		description.addProperty("eDose");
		description.addProperty("hDose");
		description.addProperty("hrDose");
		description.addProperty("hrzeDose");
		description.addProperty("impDose");
		description.addProperty("lfxDose");
		description.addProperty("lzdDose");
		description.addProperty("mfxDose");
		description.addProperty("otherDrug1Dose");
		description.addProperty("pasDose");
		description.addProperty("ptoDose");
		description.addProperty("sDose");
		description.addProperty("zDose");
		description.addProperty("councilDate");
		description.addProperty("clinicianNotes");
		description.addProperty("cmacNumber");
		description.addProperty("comment");
		description.addProperty("otherDrug1Name");
		description.addProperty("otherRegimen");
		description.addProperty("fundingSource", representation);
		description.addProperty("placeOfCommission", representation);
		description.addProperty("resistanceType", representation);
		description.addProperty("sldRegimen", representation);
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
			        .property("patientProgramUuid", new StringProperty()).property("amDose", new ObjectProperty())
			        .property("amxDose", new ObjectProperty()).property("bdqDose", new ObjectProperty())
			        .property("cfzDose", new ObjectProperty()).property("cmDose", new ObjectProperty())
			        .property("csDose", new ObjectProperty()).property("dlmDose", new ObjectProperty())
			        .property("eDose", new ObjectProperty()).property("hDose", new ObjectProperty())
			        .property("hrDose", new ObjectProperty()).property("hrzeDose", new ObjectProperty())
			        .property("impDose", new ObjectProperty()).property("lfxDose", new ObjectProperty())
			        .property("lzdDose", new ObjectProperty()).property("mfxDose", new ObjectProperty())
			        .property("otherDrug1Dose", new ObjectProperty()).property("pasDose", new ObjectProperty())
			        .property("ptoDose", new ObjectProperty()).property("sDose", new ObjectProperty())
			        .property("zDose", new ObjectProperty()).property("councilDate", new DateProperty())
			        .property("clinicianNotes", new StringProperty()).property("cmacNumber", new StringProperty())
			        .property("comment", new StringProperty()).property("otherDrug1Name", new StringProperty())
			        .property("otherRegimen", new StringProperty())
			        .property("fundingSource", new RefProperty("#/definitions/ConceptGet"))
			        .property("placeOfCommission", new RefProperty("#/definitions/ConceptGet"))
			        .property("resistanceType", new RefProperty("#/definitions/ConceptGet"))
			        .property("sldRegimen", new RefProperty("#/definitions/ConceptGet"));
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
	public SimpleRegimenForm newDelegate() {
		return new SimpleRegimenForm();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleRegimenForm simpleRegimenForm) {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleRegimenForm.getEncounter().getUuid());
		sb.append(" ");
		sb.append(simpleRegimenForm.getPatientProgramUuid());
		return sb.toString();
	}
	
	@PropertySetter("obs")
	public static void setObs(SimpleRegimenForm instance, Set<Obs> obs) {
		Encounter enc = instance.getEncounter();
		enc.getAllObs(true).clear();
		for (Obs o : obs)
			enc.addObs(o);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleRegimenForm> simpleRegimenForms = new ArrayList<>();
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_PV_REGIMEN)) {
					RegimenForm regimenForm = new RegimenForm(encounter);
					simpleRegimenForms.add(new SimpleRegimenForm(regimenForm));
				}
			}
			return new NeedsPaging<>(simpleRegimenForms, context);
		}
		return null;
	}
	
	@Override
	public SimpleRegimenForm getByUniqueId(String uuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		SimpleRegimenForm simpleRegimenForm = new SimpleRegimenForm(new RegimenForm(encounter));
		return simpleRegimenForm;
	}
	
	@Override
	public SimpleRegimenForm save(SimpleRegimenForm delegate) throws ResponseException {
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
		Double patientProgramId = null;
		for (Obs obs : obsAtTopLevel) {
			if (obs.getConcept().equals(concept)) {
				PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(
				    delegate.getPatientProgramUuid());
				patientProgramId = patientProgram.getPatientProgramId().doubleValue();
				obs.setValueNumeric(patientProgramId);
				break;
			}
		}
		RegimenForm regimenForm = new RegimenForm(delegate.getEncounter());
		regimenForm.setPatientProgramId(patientProgramId.intValue());
		regimenForm = formService.processRegimenForm(regimenForm);
		return new SimpleRegimenForm(regimenForm);
	}
	
	@Override
	protected void delete(SimpleRegimenForm delegate, String reason, RequestContext context) throws ResponseException {
		Context.getEncounterService().voidEncounter(delegate.getEncounter(), reason);
	}
	
	@Override
	public void purge(SimpleRegimenForm delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
