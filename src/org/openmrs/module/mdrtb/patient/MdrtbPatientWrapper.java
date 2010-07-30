package org.openmrs.module.mdrtb.patient;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartFactory;


public class MdrtbPatientWrapper {
	
    private Patient patient;
    
    private PatientChart chart = null; // cache the patient chart
  
    private Program mdrtbProgram; 
    
    public MdrtbPatientWrapper(Patient patient) {
    	this.patient = patient;
    	this.mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
    }
    
	public List<PatientProgram> getMdrtbPrograms() {
		return Context.getProgramWorkflowService().getPatientPrograms(this.patient, this.mdrtbProgram, null, null, null, null, false);
	 }
	
	
	public Boolean everEnrolledInMdrtbProgram() {
		return (Context.getProgramWorkflowService().getPatientPrograms(this.patient, this.mdrtbProgram, null, null, null, null, false).size() > 0);
    }
	
	public Boolean isEnrolledInMdrtbProgram() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void enrollInMdrtbProgram(Date dateEnrolled) {
		if(dateEnrolled == null) {
			throw new APIException("Can't enroll patient in program: date enrolled is null.");
		}
		else if (dateEnrolled.after(new Date())) {
			throw new APIException("Can't enroll patient in program: date enrolled cannot be in the future");
		}
		
		// go ahead and enroll the patient in the program
		// TODO: should I set the program workflow state to none?  see the MdrtbFactory method here?
		PatientProgram patientProgram = new PatientProgram();
		patientProgram.setPatient(this.patient);
		patientProgram.setProgram(this.mdrtbProgram);
		patientProgram.setDateEnrolled(dateEnrolled);
		Context.getProgramWorkflowService().savePatientProgram(patientProgram);
    }

	public PatientChart getChart() {
		
		if(chart == null) {
			PatientChartFactory factory = new PatientChartFactory();
			this.chart = factory.createPatientChart(this.patient);
		}
		
		return this.chart;
	}
	
    public Object getCultureConversionStatus() {
	    // TODO Auto-generated method stub
	    return null;
    }
}
