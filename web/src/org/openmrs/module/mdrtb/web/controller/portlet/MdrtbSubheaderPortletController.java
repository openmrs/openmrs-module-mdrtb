package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.web.controller.PortletController;


public class MdrtbSubheaderPortletController extends PortletController {
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Patient patient = Context.getPatientService().getPatient((Integer) model.get("patientId"));
		model.put("patientPrograms", MdrtbUtil.getMdrtbPrograms(patient));
	}
}
