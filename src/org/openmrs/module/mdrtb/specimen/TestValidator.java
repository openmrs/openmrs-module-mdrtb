package org.openmrs.module.mdrtb.specimen;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class TestValidator implements Validator {

	@SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return Test.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
	    Test test = (Test) target;
	    
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lab", "mdrtb.specimen.errors.noLab", "Please specify a laboratory.");
	    
	   // various tests on the dates
	    Date ordered = test.getDateOrdered();
	    Date received = test.getDateReceived();
	    Date started = test.getStartDate();
	    Date completed = test.getResultDate();
	    Date today = new Date();
	    
	    // make sure none of the dates are in the future
	    if (ordered != null && ordered.after(today)) {
	    	errors.rejectValue("dateOrdered", "mdrtb.specimen.errors.dateOrderedInFuture", "The date ordered cannot be in the future.");
	    }
	    if (received != null && received.after(today)) {
	    	errors.rejectValue("dateReceived", "mdrtb.specimen.errors.dateReceivedInFuture", "The date received cannot be in the future.");
	    }
	    if (started != null && started.after(today)) {
	    	errors.rejectValue("startDate", "mdrtb.specimen.errors.dateStartedInFuture", "The date started cannot be in the future.");
	    }
	    if (completed != null && completed.after(today)) {
	    	errors.rejectValue("resultDate", "mdrtb.specimen.errors.dateCompletedInFuture", "The date completed cannot be in the future.");
	    }
	    
    }
}
