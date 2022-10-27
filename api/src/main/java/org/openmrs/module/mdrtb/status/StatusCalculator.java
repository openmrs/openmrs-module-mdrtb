package org.openmrs.module.mdrtb.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface StatusCalculator {

	static final Log log = LogFactory.getLog(StatusCalculator.class);

	// public Status calculate(Patient patient);
	
	// public Status calculate(PatientProgram program);
	
	// TODO: do we really want/need these
	//public List<Status> calculate(List<Patient> patients);

	// public List<Status> calculateFlagged(List<Patient> patients);

}
