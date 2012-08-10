package org.openmrs.module.mdrtb.specimen.reporting;

import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.TestValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BacteriologyValidator implements Validator {

    public boolean supports(Class clazz) {
        return Bacteriology.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {

        // first call the existing test validator
        new TestValidator().validate(target, errors);

        // now do Bacteriology-specific testing
        Bacteriology bac = (Bacteriology) target;

        if (bac.getDateOrdered() == null && bac.getDateReceived() == null && bac.getResultDate() == null
                && bac.getStartDate() == null && bac.getResult() == null) {

            errors.reject("mdrtb.specimen.errors.noResult", "Please enter a value for at least one of the following: Date Ordered, Date Received, Start Date, Result Date, or Result.");
        }

    }
}
