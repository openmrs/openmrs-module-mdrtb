package org.openmrs.module.mdrtb;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAttribute;

public class MdrtbOverviewObj {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private Patient patient;
    private Obs treatmentStartDate; 
    private PatientProgram patientProgram;
    private PatientState outcome;
    private PatientState status;
    private PersonAttribute healthCenter;
    private Obs treatmentStopDate;
    private PatientIdentifier patientIdentifier;
    private String givenName;
    private String middleName;
    private String familyName;
    
    public MdrtbOverviewObj(PatientProgram prog){
        this.setPatient(prog.getPatient());
        this.setPatientProgram(prog);
    }
    
    
    
    public String getGivenName() {
        return givenName;
    }



    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }



    public String getMiddleName() {
        return middleName;
    }



    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }



    public String getFamilyName() {
        return familyName;
    }



    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }




    public Obs getTreatmentStopDate() {
        return treatmentStopDate;
    }



    public void setTreatmentStopDate(Obs treatmentStopDate) {
        this.treatmentStopDate = treatmentStopDate;
    }



    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    public Obs getTreatmentStartDate() {
        return treatmentStartDate;
    }
    public void setTreatmentStartDate(Obs treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }


    public PatientState getOutcome() {
        return outcome;
    }



    public void setOutcome(PatientState outcome) {
        this.outcome = outcome;
    }



    public PatientState getStatus() {
        return status;
    }



    public void setStatus(PatientState status) {
        this.status = status;
    }



    public PersonAttribute getHealthCenter() {
        return healthCenter;
    }
    public void setHealthCenter(PersonAttribute healthCenter) {
        this.healthCenter = healthCenter;
    }



    public PatientProgram getPatientProgram() {
        return patientProgram;
    }

    public void setPatientProgram(PatientProgram patientProgram) {
        this.patientProgram = patientProgram;
    }



    public PatientIdentifier getPatientIdentifier() {
        return patientIdentifier;
    }



    public void setPatientIdentifier(PatientIdentifier patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    
    
}
