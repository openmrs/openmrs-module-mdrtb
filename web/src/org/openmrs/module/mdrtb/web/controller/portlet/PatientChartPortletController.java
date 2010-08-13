package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.web.controller.PortletController;


public class PatientChartPortletController extends PortletController {
	
	/**
	 * Adds mdrtbPatientWrapper object to the model, if necessary
	 */
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
			// only add the Mdrtb Patient Header if it's not already in the map
			if(!model.containsKey("mdrtbPatient")) {
				model.put("mdrtbPatient", Context.getService(MdrtbService.class).getMdrtbPatient((Integer) model.get("patientId")));
			}
	}

}
