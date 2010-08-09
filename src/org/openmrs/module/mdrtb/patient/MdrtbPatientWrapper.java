package org.openmrs.module.mdrtb.patient;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartFactory;


// TODO: should you get specimens and regimens here as well?
// TODO: turn this into an interface
public class MdrtbPatientWrapper {
	
    private Patient patient;
    
    private PatientChart chart = null; // cache the patient chart
    
    public MdrtbPatientWrapper(Patient patient) {
    	this.patient = patient;
    }
    
	public List<PatientProgram> getMdrtbPrograms() {
		return Context.getProgramWorkflowService().getPatientPrograms(this.patient, Context.getService(MdrtbService.class).getMdrtbProgram(), null, null, null, null, false);
	 }
	
	
	public Boolean everEnrolledInMdrtbProgram() {
		return (getMdrtbPrograms().size() > 0);
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
		patientProgram.setProgram(Context.getService(MdrtbService.class).getMdrtbProgram());
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
