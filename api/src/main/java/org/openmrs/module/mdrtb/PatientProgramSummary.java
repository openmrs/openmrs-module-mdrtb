package org.openmrs.module.mdrtb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.lab.LabTest;

import java.util.ArrayList;
import java.util.List;

public class PatientProgramSummary {

    private PatientProgram patientProgram;

    private List<Obs> observations = new ArrayList<>();

    private List<LabTest> labTests =  new ArrayList<>();

    public PatientProgramSummary(PatientProgram patientProgram, List<Obs> observations, List<LabTest> labTests) {
        this.patientProgram = patientProgram;
        if (observations != null) {
            this.observations = observations;
        }
        if (labTests != null) {
            this.labTests = labTests;
        }
    }

    public PatientProgram getPatientProgram() {
        return patientProgram;
    }

    public void setPatientProgram(PatientProgram patientProgram) {
        this.patientProgram = patientProgram;
    }

    public List<Obs> getObservations() {
        return observations;
    }

    public void setObservations(List<Obs> observations) {
        this.observations = observations;
    }

    public List<LabTest> getLabTests() {
        return labTests;
    }

    public void setLabTests(List<LabTest> labTests) {
        this.labTests = labTests;
    }

    /**
     * Each program has an associated Outcome concept. If an observation wos recorded in the set of obs in this
     * patient program object against the outcome concept, it will be returned.
     */
    public Obs getTreatmentOutcome() {
        Concept outcomesConcept = patientProgram.getProgram().getOutcomesConcept();
        Concept tbOutcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME);
        Concept mdrtbOutcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
        for (Obs obs : observations) {
            if (obs.getConcept() != null && outcomesConcept.equals(obs.getConcept())) {
                return obs;
            }
            if (obs.getConcept() != null && (tbOutcome.equals(obs.getConcept()) || mdrtbOutcome.equals(obs.getConcept()) )) {
                return obs;
            }
        }
        return null;
    }
}
