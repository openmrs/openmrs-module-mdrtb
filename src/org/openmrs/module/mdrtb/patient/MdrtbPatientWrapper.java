package org.openmrs.module.mdrtb.patient;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.specimen.Specimen;

/**
 * An interface that provides access to the MDR-TB-specific elements of a patient
 */
public interface MdrtbPatientWrapper {

	/**
	 * Return the patient this wrapper wraps
	 */
	public Patient getPatient();
	
	/**
	 * Returns all mdr-tb patient programs associated with this patient
	 */
	public List<PatientProgram> getMdrtbPrograms();
	
	/**
	 * Return whether or not the patient has ever been enrolled in the MDR-TB program
	 */
	public Boolean everEnrolledInMdrtbProgram();
	
	/**
	 * Returns whether or not the patient is currently enrolled in the MDR-TB program
	 */
	public Boolean isEnrolledInMdrtbProgram();
	
	/**
	 * Enrolls the patient in the MDR-TB program at the specified data
	 */
	public void enrollInMdrtbProgram(Date dateEnrolled);
	
	/**
	 * Returns the mdrtb patient chart for this patient
	 */
	public PatientChart getChart();
	
	/**
	 * Returns the mdrtb regimen history for this patient
	 */
	public RegimenHistory getRegimenHistory();
	
	/**
	 * Returns the list of all mdrtb regimens for the patinent
	 * returns null if no regimens
	 */
	public List<Regimen> getRegimens();
	
	/**
	 * Returns the list of specimens associated with this patient 
	 */
	public List<Specimen> getSpecimens();
	
	/**
	 * Gets the mdrtb treatment start date for this patient
	 * (Defined as the date of the first TB drug order)
	 * TODO: add methods to get treatment start date by program?
	 */
	public Date getTreatmentStartDate();
	
	/**
	 * Gets the mdrtb treatment stop date for this patient
	 * (Defined as the date of the end of the last TB order; returns null if there is an open order... i.e., treatment is ongoing)
	 */
	public Date getTreatmentEndDate();
	
}
