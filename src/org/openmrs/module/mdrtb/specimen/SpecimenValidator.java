package org.openmrs.module.mdrtb.specimen;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class SpecimenValidator implements Validator {

    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return Specimen.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
	    Specimen specimen = (Specimen) target;
	    
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateCollected", "mdrtb.specimen.errors.noDateCollected", "Please specify a date collected.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "provider", "mdrtb.specimen.errors.noCollector","Please specify who collected this sample.");
	    
	    if(specimen.getDateCollected() != null && specimen.getDateCollected().after(new Date())) {
	    	errors.rejectValue("dateCollected", "mdrtb.specimen.errors.dateCollectedInFuture", "The date collected cannot be in the future.");
	    }
	    
    }

}
