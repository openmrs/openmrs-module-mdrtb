package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.mdrtb.web.controller.MdrtbPatientOverviewController;
import org.openmrs.web.controller.PortletController;


public class MdrtbPatientHeaderPortletController extends PortletController {

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		// hack to just use the mdrtbPatientOverview controller at this point
		// (had to make formBackingObject in MdrtbPatientOverviewController visible to make this work)
		// TODO: eventually we want this to collect the data it needs by adding an MdrtbPatientWrapper instance
		// to the model instead
		
	
		/**
		MdrtbPatientOverviewController controller = new MdrtbPatientOverviewController();
		
		Object obj;
        try {
	        obj = controller.formBackingObject(request);
        }
        catch (Exception e) {
	       throw new RuntimeException(e);
        }
	
		model.put("obj", obj);
		*/
		
	}
}
