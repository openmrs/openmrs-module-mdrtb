package org.openmrs.module.mdrtb.status;

import org.openmrs.PatientProgram;


public interface StatusCalculator {

	public Status calculate(PatientProgram program);
	
	// TODO: do we really want/need these
	//public List<Status> calculate(List<Patient> patients);

	// public List<Status> calculateFlagged(List<Patient> patients);

}
