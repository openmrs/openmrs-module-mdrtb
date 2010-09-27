package org.openmrs.module.mdrtb.status;

import java.util.List;

import org.openmrs.Patient;


public interface StatusCalculator {

	public Status calculate(Patient patient);

	
	// TODO: do we reall want/need these
	public List<Status> calculate(List<Patient> patients);

	public List<Status> calculateFlagged(List<Patient> patients);

}
