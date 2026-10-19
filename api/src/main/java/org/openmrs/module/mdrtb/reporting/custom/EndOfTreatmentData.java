package org.openmrs.module.mdrtb.reporting.custom;

import org.openmrs.Patient;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;

/**
 * One row of the End of Treatment report: one filled {@link EndOfTreatmentForm} together with the patient details the report shows next to it. Same role as TB03Data / Form89Data.
 */
public class EndOfTreatmentData implements Comparable<EndOfTreatmentData> {
	
	private Patient patient;
	
	private EndOfTreatmentForm endOfTreatmentForm;
	
	/** Registration number of the patient program (TB03 or TB03u), used for sorting */
	private String identifier;
	
	// TODO (EoT report): add report columns here (see class comment).
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public EndOfTreatmentForm getEndOfTreatmentForm() {
		return endOfTreatmentForm;
	}
	
	public void setEndOfTreatmentForm(EndOfTreatmentForm endOfTreatmentForm) {
		this.endOfTreatmentForm = endOfTreatmentForm;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public int compareTo(EndOfTreatmentData o) {
		if (o.getIdentifier() == null || getIdentifier() == null)
			return 0;
		return identifier.compareTo(o.getIdentifier());
	}
}
