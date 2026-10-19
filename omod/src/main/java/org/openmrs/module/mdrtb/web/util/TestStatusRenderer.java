package org.openmrs.module.mdrtb.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

public class TestStatusRenderer {
	
	public static String renderStandardStatus(Test test) {
		
		TestStatus status = test.getStatus();
		DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
		
		if (status == TestStatus.COMPLETED) {
			String[] params = { test.getLab().getDisplayString(), df.format(test.getResultDate()) };
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.test.completed", params,
			    "Completed on {1} at {0}");
		} else if (status == TestStatus.STARTED) {
			String[] params = { test.getLab().getDisplayString(), df.format(test.getStartDate()) };
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.test.started", params,
			    "Started on {1} at {0}");
		} else if (status == TestStatus.RECEIVED) {
			String[] params = { test.getLab().getDisplayString(), df.format(test.getDateReceived()) };
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.test.received", params,
			    "Received by {0} at {1}");
		} else if (status == TestStatus.ORDERED) {
			String[] params = { test.getLab().getDisplayString(), df.format(test.getDateOrdered()) };
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.test.ordered", params,
			    "Ordered on {1} from {0}");
		} else {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.unknown");
		}
	}
	
	public static String renderShortStatus(Test test) {
		
		TestStatus status = test.getStatus();
		
		if (status == TestStatus.ORDERED || status == TestStatus.RECEIVED || status == TestStatus.STARTED) {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.pending");
		} else if (status == TestStatus.COMPLETED) {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.complete");
		} else {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.unknown");
		}
	}
	
	/**
	 * Renders a "short" status for a group of tests.
	 */
	public static String renderGroupShortStatus(List<Test> tests) {
		
		Boolean complete = false;
		Boolean pending = false;
		
		for (Test test : tests) {
			TestStatus status = test.getStatus();
			if (status == TestStatus.ORDERED || status == TestStatus.RECEIVED || status == TestStatus.STARTED) {
				pending = true;
			} else if (status == TestStatus.COMPLETED) {
				complete = true;
			}
		}
		
		if (complete && pending) {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.completePending");
		} else if (!complete && pending) {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.pending");
		} else if (complete && !pending) {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.complete");
		} else {
			return "";
		}
	}
}
