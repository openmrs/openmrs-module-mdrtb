package org.openmrs.module.mdrtb.web.util;

import java.text.DateFormat;

import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;


public class TestStatusRenderer {

	public static String renderStandardStatus(Test test) {
		 // TODO: determine the best way to localize all the text in this method
		
		TestStatus status = test.getStatus();
	    DateFormat df = DateFormat.getDateInstance();
	    	
	    if (status == TestStatus.COMPLETED) {
	    	return "Completed on " + df.format(test.getResultDate()) + " at " + test.getLab();
	    }
	    else if (status == TestStatus.STARTED) {
	    	return "Started on " + df.format(test.getStartDate()) + " at " + test.getLab();
	    }
	    else if (status == TestStatus.RECEIVED) {
	    	return "Received by " + test.getLab() + " on " + df.format(test.getDateReceived());
	    }
	    else if (status == TestStatus.ORDERED) {
	    	return "Ordered on " + df.format(test.getDateOrdered()) + " from " + test.getLab();
	    }
	    else {
	    	return "Unknown";
	    }
	}
	
	public static String renderShortStatus(Test test) {
		
		TestStatus status = test.getStatus();
		
		if(status == TestStatus.ORDERED || status == TestStatus.RECEIVED || status == TestStatus.STARTED) {
			return "Pending";
		}
		else if (status == TestStatus.COMPLETED) {
			return "Complete";
		}
		else {
			return "Unknown";
		}
	}
}
