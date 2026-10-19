package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.reporting.pv.YellowCardData;
import org.openmrs.module.mdrtb.web.dto.SimpleYellowCardData;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

/**
 * Yellow Card (Notification on Adverse Effects, Annex 4) - read only.
 * <p>
 * Usage:
 * <ul>
 * <li>GET mdrtb/yellowcard?formUuid={adverse events form (encounter) uuid} - the card(s) of one AE
 * form (one card per filled causality drug)</li>
 * <li>GET mdrtb/yellowcard?year=&amp;quarter=|month=[&amp;month2=]&amp;location= - the cards of
 * every AE form filled in the period, same parameters as mdrtb/tb03report</li>
 * </ul>
 * Mapping rules: see {@link YellowCardData}.
 */
@Resource(name = RestConstants.VERSION_1 + "/mdrtb/yellowcard", supportedClass = SimpleYellowCardData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class YellowCardDataResourceController extends BaseReportResource<SimpleYellowCardData> {
	
	public static final String PARAM_FORM_UUID = "formUuid";
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("adverseEventsFormUuid");
		description.addProperty("causalityIndex");
		description.addProperty("patientUuid");
		// Section 1
		description.addProperty("patientName");
		description.addProperty("address");
		description.addProperty("openmrsIdentifier");
		description.addProperty("dateOfBirth");
		description.addProperty("gender");
		description.addProperty("outcomeCode");
		description.addProperty("outcomeName");
		description.addProperty("regimenTypeCode");
		description.addProperty("regimenTypeName");
		description.addProperty("diagnosticSummary");
		description.addProperty("clinicianNotes");
		// Section 2
		description.addProperty("suspectedDrugName");
		description.addProperty("suspectedDrugsText");
		// Section 3
		description.addProperty("onsetDate");
		description.addProperty("cessationDate");
		description.addProperty("adverseEventName");
		description.addProperty("saeTypeCode");
		description.addProperty("saeTypeName");
		description.addProperty("aesiTypeCode");
		description.addProperty("aesiTypeName");
		// Section 4
		description.addProperty("regimenDate");
		description.addProperty("primaryDiseaseMedicines");
		// Section 5
		description.addProperty("rechallengeReproducedReaction");
		// Section 6
		description.addProperty("causalityCode");
		description.addProperty("causalityName");
		// Section 7
		description.addProperty("reporterName");
		description.addProperty("reporterPlaceOfWork");
		description.addProperty("reportDate");
		description.addProperty("warnings");
		return description;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		List<SimpleYellowCardData> list = new ArrayList<>();

		// 1) Cards of a single Adverse Events form
		String formUuid = context.getRequest().getParameter(PARAM_FORM_UUID);
		if (StringUtils.isNotBlank(formUuid)) {
			Encounter encounter = Context.getEncounterService().getEncounterByUuid(formUuid);
			if (encounter == null || Boolean.TRUE.equals(encounter.getVoided())
			        || MdrtbConstants.ET_ADVERSE_EVENT == null
			        || !MdrtbConstants.ET_ADVERSE_EVENT.equals(encounter.getEncounterType())) {
				return new EmptySearchResult();
			}
			addCards(list, new AdverseEventsForm(encounter));
			return new NeedsPaging<>(list, context);
		}

		// 2) Cards of all Adverse Events forms in a period / location (same parameters as TB03)
		final Map<String, Object> params = processParams(context);
		if (params == null) {
			return new EmptySearchResult();
		}
		List<Location> locList = (List<Location>) params.get("locations");
		Integer year = (Integer) params.get("year");
		Integer quarter = (Integer) params.get("quarter");
		Integer month = (Integer) params.get("month");
		Integer month2 = (Integer) params.get("month2");
		List<AdverseEventsForm> forms = Context.getService(MdrtbService.class).getAEFormsFilled(locList, year, quarter,
		    month, month2);
		for (AdverseEventsForm form : forms) {
			addCards(list, form);
		}
		return new NeedsPaging<>(list, context);
	}
	
	private void addCards(List<SimpleYellowCardData> list, AdverseEventsForm form) {
		for (YellowCardData card : YellowCardData.fromAdverseEventsForm(form)) {
			list.add(new SimpleYellowCardData(card));
		}
	}
}
