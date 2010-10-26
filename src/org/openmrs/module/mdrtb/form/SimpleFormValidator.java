package org.openmrs.module.mdrtb.form;

import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class SimpleFormValidator implements Validator {
	
	@SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return Specimen.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {   
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "encounterDatetime", "mdrtb.form.errors.noEncounterDatetime", "Please specify a visit date.");
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "mdrtb.form.errors.noLocation", "Please specify a location.");
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "provider", "mdrtb.form.errors.noProvider", "Please specify a provider");
    }
}
