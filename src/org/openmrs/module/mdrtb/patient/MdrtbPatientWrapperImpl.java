package org.openmrs.module.mdrtb.patient;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartFactory;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.specimen.Specimen;

public class MdrtbPatientWrapperImpl implements MdrtbPatientWrapper {
	
    private Patient patient;
    
    private PatientChart chart = null; // cache the patient chart
    
    public MdrtbPatientWrapperImpl (Patient patient) {
    	this.patient = patient;
    }
    
    public Patient getPatient() {
    	return patient;
    }
    
	public List<PatientProgram> getMdrtbPrograms() {
		
		List<PatientProgram> mdrtbPrograms = Context.getProgramWorkflowService().getPatientPrograms(this.patient, Context.getService(MdrtbService.class).getMdrtbProgram(), null, null, null, null, false);
		
		// TODO: figure out a way to add sorting back in; unfortunately, sorting screws up spring bindings/validation
		// TODO: because spring expects the fields to come back in the same order on submission
		// Collections.sort(mdrtbPrograms, new PatientProgramComparator());
		
		return mdrtbPrograms;
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
		// TODO: do we want to set the patient in some specific states here? in the old MDR-TB factory all states are set to None?
		PatientProgram patientProgram = new PatientProgram();
		patientProgram.setPatient(this.patient);
		patientProgram.setProgram(Context.getService(MdrtbService.class).getMdrtbProgram());
		patientProgram.setDateEnrolled(dateEnrolled);
		Context.getProgramWorkflowService().savePatientProgram(patientProgram);
    }

	public PatientChart getChart() {
		
		if(chart == null) {
			PatientChartFactory factory = new PatientChartFactory();
			this.chart = factory.createPatientChart(this);
		}
		
		return this.chart;
	}

	public RegimenHistory getRegimenHistory() {
		return RegimenUtils.getRegimenHistory(this.patient);
	}
	
	public List<Regimen> getRegimens() {
		// first search for a regimen history
		RegimenHistory regimenHistory = getRegimenHistory();
		
		if(regimenHistory != null) {
			return regimenHistory.getRegimenList();
		}
		else {
			return null;
		}
	}
	
	public Date getTreatmentStartDate() {
		Date startDate = null;
		List<Regimen> regimens = getRegimens();
			
		// TODO: confirm that regimen history sorts regimens in order
		// return the start date of the first regimen 
		if(regimens != null && regimens.size() > 0) {
			startDate = regimens.get(0).getStartDate();
		}
		
		// if no regimens, this will return null for a treatment start date	
		return startDate;
	}
	
	public Date getTreatmentEndDate() {
		Date endDate = null;
		List<Regimen> regimens = getRegimens();
			
		// TODO: confirm that regimen history sorts regimens in order
		// return the end date of the last regimen 
		if(regimens != null && regimens.size() > 0) {
			endDate = regimens.get(regimens.size()-1).getEndDate();
		}
		
		// if no regimens, this will return null for a treatment end date	
		return endDate;
	}
	
    public List<Specimen> getSpecimens() {
	    return Context.getService(MdrtbService.class).getSpecimens(this.patient);
    }
    
    /**
     * Utility classes
     */
    
    private class PatientProgramComparator implements Comparator<PatientProgram> {

    	public int compare(PatientProgram program1, PatientProgram program2) {
    		if(program1 == null || program1.getDateEnrolled() == null) {
    			return 1;
    		} else if (program2 == null || program2.getDateEnrolled() == null) {
    			return -1;
    		} else {
    			return program1.getDateEnrolled().compareTo(program2.getDateEnrolled());
    		}
    	}
    }
}
