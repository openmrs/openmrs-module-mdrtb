package org.openmrs.module.mdrtb.web.controller.lab;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.web.controller.PortletController;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author engrmahmed14@gmail.com Registered as bean 'labTestOrderPortletController' in
 *         webModuleApplicationContext.xml and mapped to **&#47;patientLabTests.portlet there. Do
 *         NOT annotate this class with Controller/RequestMapping: that only worked upstream via
 *         DefaultAnnotationHandlerMapping, which Spring 5 removed. Without the explicit mapping the
 *         request falls through to the core portlet controller and the model arrives empty.
 */
public class LabTestOrderPortletController extends PortletController {
	
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		String patientId = request.getParameter("patientId");
		if (patientId != null) {
			int id = Integer.parseInt(patientId);
			Patient patient = Context.getPatientService().getPatient(id);
			JsonArray orderJsonArray = new JsonArray();
			if (patient != null) {
				List<LabTest> testList = Context.getService(LabTestService.class).getLabTests(patient, false);
				if (testList != null && !testList.isEmpty()) {
					for (LabTest labTest : testList) {
						JsonObject childJsonObject = new JsonObject();
						childJsonObject.addProperty("id", labTest.getTestOrderId());
						childJsonObject.addProperty("requiredSpecimen", labTest.getLabTestType().getRequiresSpecimen());
						childJsonObject.addProperty("testTypeName", labTest.getLabTestType().getName());
						childJsonObject.addProperty("encounterName", labTest.getOrder().getEncounter().getEncounterType()
						        .getName());
						SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
						String encounterDate = formatter.format(labTest.getOrder().getEncounter().getEncounterDatetime());
						childJsonObject.addProperty("encounterDate", encounterDate);
						childJsonObject.addProperty("labReferenceNumber", labTest.getLabReferenceNumber());
						childJsonObject.addProperty("testGroup", labTest.getLabTestType().getTestGroup().name());
						childJsonObject.addProperty("dateCreated", labTest.getDateCreated().toString());
						childJsonObject.addProperty("createdBy", labTest.getCreator().getUsername());
						childJsonObject.addProperty("encounterType", labTest.getOrder().getEncounter().getEncounterType()
						        .getName().toString());
						childJsonObject.addProperty("changedBy", (labTest.getChangedBy() == null) ? "" : labTest
						        .getChangedBy().getUsername());
						childJsonObject.addProperty("uuid", labTest.getUuid());
						List<LabTestAttribute> labTestAttribute = Context.getService(LabTestService.class)
						        .getLabTestAttributes(labTest.getTestOrderId());
						if (labTestAttribute != null && labTestAttribute.size() > 0) {
							childJsonObject.addProperty("resultFilled", Boolean.TRUE);
							childJsonObject.addProperty("resultDate", labTestAttribute.get(0).getDateCreated().toString());
						} else {
							childJsonObject.addProperty("resultFilled", Boolean.FALSE);
							childJsonObject.addProperty("resultDate", "");
						}
						orderJsonArray.add(childJsonObject);
					}
				}
				boolean anyTestRequireSample = false;
				for (JsonElement element : orderJsonArray) {
					anyTestRequireSample = ((JsonObject) element).get("requiredSpecimen").getAsBoolean();
					if (anyTestRequireSample == true)
						break;
				}
				model.put("testOrder", orderJsonArray);
				model.put("anyTestRequireSample", anyTestRequireSample);
			}
		}
		
	}
	
}
