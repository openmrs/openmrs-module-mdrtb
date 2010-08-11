package org.openmrs.module.mdrtb.patient;

import java.util.Date;

import org.openmrs.PatientProgram;


public class MdrtbPatientProgramImpl implements MdrtbPatientProgram{

	PatientProgram program;
	
	public MdrtbPatientProgramImpl() {
	}
	
	public MdrtbPatientProgramImpl(PatientProgram program) {
		this.program = program;
	}
	
	
    public Date getDateEnrolled() {
    	return program.getDateEnrolled();
    }

}
