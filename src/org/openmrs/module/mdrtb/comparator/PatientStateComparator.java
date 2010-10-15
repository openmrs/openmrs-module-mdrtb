package org.openmrs.module.mdrtb.comparator;

import java.util.Comparator;

import org.openmrs.PatientState;


public class PatientStateComparator implements Comparator<PatientState> {

    public int compare(PatientState state1, PatientState state2) {
    	// I don't worry about null cases here, because the start date for a patient state should never be null
	    return state1.getStartDate().compareTo(state2.getStartDate());
    }

}
