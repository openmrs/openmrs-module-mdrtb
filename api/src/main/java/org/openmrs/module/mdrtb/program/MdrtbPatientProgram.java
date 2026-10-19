package org.openmrs.module.mdrtb.program;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.TbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.comparator.PatientStateComparator;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.TB03uXDRForm;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class MdrtbPatientProgram implements Comparable<MdrtbPatientProgram>, Validator {
	
	private PatientProgram program;
	
	private PatientIdentifier patientIdentifier;
	
	public MdrtbPatientProgram() {
		this.program = new PatientProgram();
		this.program.setProgram(Context.getProgramWorkflowService().getProgramByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME)));
	}
	
	public MdrtbPatientProgram(PatientProgram program) {
		this.program = program;
	}
	
	public PatientProgram getPatientProgram() {
		return program;
	}
	
	public Boolean getActive() {
		return this.program.getActive();
	}
	
	public Boolean getActive(Date onDate) {
		return this.program.getActive(onDate);
	}
	
	public Integer getId() {
		return this.program.getId();
	}
	
	public Patient getPatient() {
		return this.program.getPatient();
	}
	
	public void setPatient(Patient patient) {
		this.program.setPatient(patient);
	}
	
	public Date getDateEnrolled() {
		return this.program.getDateEnrolled();
	}
	
	public void setDateEnrolled(Date dateEnrolled) {
		this.program.setDateEnrolled(dateEnrolled);
		
		// also update any of the start date of the classification states (if they exist)
		PatientState previousDrugState = getPatientState(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
		if (previousDrugState != null) {
			previousDrugState.setStartDate(dateEnrolled);
		}
		
		PatientState previousTreatmentState = getPatientState(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT));
		if (previousTreatmentState != null) {
			previousTreatmentState.setStartDate(dateEnrolled);
		}
	}
	
	public Date getDateCompleted() {
		return this.program.getDateCompleted();
	}
	
	public void setDateCompleted(Date dateCompleted) {
		this.program.setDateCompleted(dateCompleted);
		
		// also update the start date of the outcome state 
		PatientState outcome = getPatientState(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME));
		if (outcome != null) {
			outcome.setStartDate(dateCompleted);
		}
	}
	
	public Location getLocation() {
		return this.program.getLocation();
	}
	
	public void setLocation(Location location) {
		this.program.setLocation(location);
	}
	
	public ProgramWorkflowState getOutcome() {
		Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
		return getPatientWorkflowState(outcome);
	}
	
	public void setOutcome(ProgramWorkflowState programOutcome) {
		// if the state hasn't changed, we don't need to bother doing the update
		ProgramWorkflowState currentOutcome = getOutcome();
		if ((currentOutcome == null && programOutcome == null)
		        || (currentOutcome != null && currentOutcome.equals(programOutcome))) {
			return;
		}
		
		// otherwise, do the update
		Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
		
		// void any existing states tied to the the outcome workflow
		voidStates(outcome);
		
		// now add the new state, if one has been specified
		if (programOutcome != null) {
			PatientState outcomeState = new PatientState();
			outcomeState.setState(programOutcome);
			// the outcome state start date is always the completed date of the program
			outcomeState.setStartDate(program.getDateCompleted());
			this.program.getStates().add(outcomeState);
		}
	}
	
	public ProgramWorkflowState getClassificationAccordingToPreviousDrugUse() {
		Concept previousDrug = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
		return getPatientWorkflowState(previousDrug);
	}
	
	public void setClassificationAccordingToPreviousDrugUse(ProgramWorkflowState classification) {
		// first make sure that this program workflow state is valid
		if (classification != null
		        && !Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousDrugUse()
		                .contains(classification)) {
			throw new MdrtbAPIException(classification
			        + " is not a valid state for Classification According To Previous Drug Use workflow");
		}
		
		// if the state hasn't changed, we don't need to bother doing the update
		ProgramWorkflowState currentClassification = getClassificationAccordingToPreviousDrugUse();
		if ((currentClassification == null && classification == null)
		        || (currentClassification != null && currentClassification.equals(classification))) {
			return;
		}
		
		// otherwise, do the update
		Concept previousDrug = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
		
		// void any existing states tied to the the outcome workflow
		voidStates(previousDrug);
		
		// now add the new state, if one has been specified
		if (classification != null) {
			PatientState previousDrugState = new PatientState();
			previousDrugState.setState(classification);
			// the start date for the state should be the program enrollment date
			previousDrugState.setStartDate(program.getDateEnrolled());
			this.program.getStates().add(previousDrugState);
		}
	}
	
	public ProgramWorkflowState getClassificationAccordingToPreviousTreatment() {
		Concept previousTreatment = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT);
		return getPatientWorkflowState(previousTreatment);
	}
	
	public void setClassificationAccordingToPreviousTreatment(ProgramWorkflowState classification) {
		// first make sure that the program workflow state is valid
		if (classification != null
		        && !Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousTreatment()
		                .contains(classification)) {
			throw new MdrtbAPIException(classification
			        + " is not a valid state for Classification According To Previous Treatment workflow");
		}
		
		// if the state hasn't changed, we don't need to bother doing the update
		ProgramWorkflowState currentClassification = getClassificationAccordingToPreviousTreatment();
		if ((currentClassification == null && classification == null)
		        || (currentClassification != null && currentClassification.equals(classification))) {
			return;
		}
		
		// otherwise, do the update
		Concept previousTreatment = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT);
		
		// void any existing states tied to the the outcome workflow
		voidStates(previousTreatment);
		
		// now add the new state, if one has been specified
		if (classification != null) {
			PatientState previousTreatmentState = new PatientState();
			previousTreatmentState.setState(classification);
			// the start date for the state should be the program enrollment date
			previousTreatmentState.setStartDate(program.getDateEnrolled());
			this.program.getStates().add(previousTreatmentState);
		}
	}
	
	public ProgramWorkflowState getCurrentHospitalizationState() {
		Concept hospitalizationWorkflow = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.HOSPITALIZATION_WORKFLOW);
		return getCurrentPatientWorkflowState(hospitalizationWorkflow);
	}
	
	public Boolean getCurrentlyHospitalized() {
		Concept hospitalizationWorkflow = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.HOSPITALIZATION_WORKFLOW);
		ProgramWorkflowState currentState = getCurrentPatientWorkflowState(hospitalizationWorkflow);
		
		if (currentState == null) {
			return false;
		} else {
			return currentState.getConcept().equals(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED));
		}
	}
	
	public void closeCurrentHospitalization(Date dischargeDate) {
		
		PatientState currentState = getCurrentPatientState(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.HOSPITALIZATION_WORKFLOW));
		
		// if the current state is hospitalized, we need to close it
		Concept hospitalized = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED);
		if (currentState != null && currentState.getState() != null
		        && currentState.getState().getConcept().equals(hospitalized)) {
			currentState.setEndDate(dischargeDate);
		}
	}
	
	public List<PatientState> getAllHospitalizations() {
		List<PatientState> states = getAllPatientStates(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOSPITALIZATION_WORKFLOW),
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED));
		
		states.sort(Collections.reverseOrder(new PatientStateComparator()));
		return states;
	}
	
	public void addHospitalization(Date admissionDate, Date dischargeDate) {
		PatientState hospitalization = new PatientState();
		hospitalization.setState(Context.getService(MdrtbService.class).getProgramWorkflowState(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED)));
		hospitalization.setStartDate(admissionDate);
		hospitalization.setEndDate(dischargeDate);
		this.program.getStates().add(hospitalization);
	}
	
	public void removeHospitalization(PatientState hospitalizations) {
		// void this state
		hospitalizations.setVoided(true);
		hospitalizations.setVoidReason("voided by mdr-tb module");
	}
	
	/**
	 * Returns true/false whether the passed date falls within this program period
	 */
	public boolean isDateDuringProgram(Date date) {
		if (date == null) {
			return false;
		}
		
		if ((getPreviousProgramDateCompleted() != null && date.before(getPreviousProgramDateCompleted()))
		        || (!isMostRecentProgram() && date.after(program.getDateCompleted()))) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Methods that get certain patient data during the time of this program
	 */
	
	public List<Specimen> getSpecimensDuringProgram() {
		
		if (program.getDateEnrolled() == null) {
			return null;
		}
		
		return Context.getService(MdrtbService.class).getSpecimens(program.getPatient(), getPreviousProgramDateCompleted(),
		    (!isMostRecentProgram() ? program.getDateCompleted() : new Date()));
	}
	
	public List<Specimen> getSpecimensDuringProgramObs() {
		
		if (program.getId() == null) {
			return null;
		}
		
		return Context.getService(MdrtbService.class).getSpecimens(program.getPatient(), program.getId());
	}
	
	public List<Regimen> getMdrtbRegimensDuringProgram() {
		
		if (program.getDateEnrolled() == null) {
			return null;
		}
		
		return RegimenUtils.getTbRegimenHistory(program.getPatient()).getRegimensDuring(getPreviousProgramDateCompleted(),
		    (!isMostRecentProgram() && program.getDateCompleted() != null ? program.getDateCompleted() : new Date()));
	}
	
	public List<Encounter> getMdrtbEncountersDuringProgram() {
		
		if (program.getDateEnrolled() == null) {
			return null;
		}
		return Context.getService(MdrtbService.class).getEncounters(program.getPatient(), null,
		    getPreviousProgramDateCompleted(), (!isMostRecentProgram() ? program.getDateCompleted() : new Date()),
		    TbUtil.getTbEncounterTypes());
	}
	
	public List<Encounter> getMdrtbEncountersDuringProgramObs() {
		
		if (program.getDateEnrolled() == null) {
			return null;
		}
		
		List<Encounter> encs = Context.getService(MdrtbService.class).getMdrtbEncounters(program.getPatient());
		ArrayList<Encounter> ret = new ArrayList<>();
		Obs temp = null;
		for (Encounter encounter : encs) {
			temp = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
			if (temp != null && temp.getValueNumeric() != null && temp.getValueNumeric().intValue() == getId())
				ret.add(encounter);
		}
		return ret;
	}
	
	public Concept getCurrentAnatomicalSiteDuringProgram() {
		if (program.getDateEnrolled() == null) {
			return null;
		}
		
		Concept[] anatomicalSiteConcept = { Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.ANATOMICAL_SITE_OF_TB) };
		Person[] person = { program.getPatient() };
		
		List<Obs> anatomicalSites = Context.getObsService().getObservations(Arrays.asList(person), null,
		    Arrays.asList(anatomicalSiteConcept), null, null, null, null, null, null, getPreviousProgramDateCompleted(),
		    (!isMostRecentProgram() ? program.getDateCompleted() : new Date()), false);
		
		if (!anatomicalSites.isEmpty()) {
			return anatomicalSites.get(0).getValueCoded();
		} else {
			return null;
		}
	}
	
	public Date getTreatmentStartDateDuringProgram() {
		Date startDate = null;
		List<Regimen> regimens = getMdrtbRegimensDuringProgram();
		if (regimens != null && !regimens.isEmpty()) {
			startDate = regimens.get(0).getStartDate();
			Date previousProgramDateCompleted = getPreviousProgramDateCompleted();
			if (previousProgramDateCompleted != null && startDate.before(previousProgramDateCompleted)) {
				startDate = null;
			}
			
		}
		return startDate;
	}
	
	public Date getTreatmentEndDateDuringProgram() {
		Date endDate = null;
		List<Regimen> regimens = getMdrtbRegimensDuringProgram();
		if (regimens != null && !regimens.isEmpty()) {
			endDate = regimens.get(regimens.size() - 1).getEndDate();
		}
		return endDate;
	}
	
	/**
	 * Returns true if this is the most recent program for this patient
	 */
	private Boolean isMostRecentProgram() {
		List<MdrtbPatientProgram> programs = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(
		    this.program.getPatient());
		
		if (!programs.isEmpty()) {
			return this.equals(programs.get(programs.size() - 1));
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the end date of the program before the previous program (Returns null if this is the
	 * first program)
	 */
	private Date getPreviousProgramDateCompleted() {
		MdrtbPatientProgram previousProgram = getPreviousProgram();
		return (previousProgram != null ? previousProgram.getDateCompleted() : null);
	}
	
	/**
	 * Get the program directly before this program (Returns null if this is the first program)
	 */
	private MdrtbPatientProgram getPreviousProgram() {
		
		List<MdrtbPatientProgram> mdrtbPatientPrograms = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(
		    this.program.getPatient());
		ListIterator<MdrtbPatientProgram> programs = mdrtbPatientPrograms.listIterator();
		
		while (programs.hasNext()) {
			if (programs.next().equals(this)) {
				programs.previous();
				if (programs.hasPrevious()) {
					return programs.previous();
				} else {
					return null;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the state for a workflow Note that this method operates under the assumption that there
	 * is only one non-voided state per workflow at any one time. For a generic workflow, this would
	 * not be a valid assumption, but for the Classification and Outcome workflows we are working
	 * with, this should be true.
	 */
	private PatientState getPatientState(Concept workflowConcept) {
		for (PatientState state : this.program.getStates()) {
			if (state.getState().getProgramWorkflow().getConcept().equals(workflowConcept) && !state.getVoided()) {
				return state;
			}
		}
		
		return null;
	}
	
	private ProgramWorkflowState getPatientWorkflowState(Concept workflowConcept) {
		PatientState state = getPatientState(workflowConcept);
		
		if (state == null) {
			return null;
		} else {
			return state.getState();
		}
	}
	
	/**
	 * Gets all the non-voided PatientStates for a specificed workflow This is used for workflows
	 * like Hospitalization State which will have more than one non-voided states
	 */
	private List<PatientState> getAllPatientStates(Concept workflowConcept, Concept patientStateConcept) {
		
		List<PatientState> states = new LinkedList<>();
		
		for (PatientState state : this.program.getStates()) {
			if (state.getState().getConcept().equals(patientStateConcept)
			        && state.getState().getProgramWorkflow().getConcept().equals(workflowConcept) && !state.getVoided()) {
				states.add(state);
			}
		}
		
		return states;
	}
	
	/**
	 * Gets the current state for a workflow This method is meant to operate on workflows like the
	 * the Hospitalization Workflow that we will be using to state changes over time
	 */
	
	private PatientState getCurrentPatientState(Concept workflowConcept) {
		for (PatientState state : this.program.getStates()) {
			// this assumes that there is only one active state per workflow
			if (state.getActive() && state.getState().getProgramWorkflow().getConcept().equals(workflowConcept)) {
				return state;
			}
		}
		
		return null;
	}
	
	private ProgramWorkflowState getCurrentPatientWorkflowState(Concept workflowConcept) {
		PatientState state = getCurrentPatientState(workflowConcept);
		
		if (state == null) {
			return null;
		} else {
			return state.getState();
		}
		
	}
	
	/**
	 * Voids all states related to a specific workflow
	 */
	private void voidStates(Concept workflowConcept) {
		for (PatientState state : this.program.getStates()) {
			if (state.getState().getProgramWorkflow().getConcept().equals(workflowConcept)) {
				state.setVoided(true);
				state.setVoidReason("voided by mdr-tb module");
			}
		}
	}
	
	/****** CUSTOM METHODS *****/
	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}
	
	public void setPatientIdentifier(PatientIdentifier patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}
	
	public TB03uForm getTb03u() {
		TB03uForm tb03u = null;
		List<Encounter> encounters = getMdrtbEncountersDuringProgramObs();
		
		if (encounters != null) {
			for (Encounter encounter : encounters) {
				// create a new status item for this encounter
				
				// now place the visit in the appropriate "bucket"
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_TB03U_MDRTB_INTAKE)) {
					tb03u = new TB03uForm(encounter);
					break;
				}
			}
		}
		
		return tb03u;
	}
	
	public TB03uXDRForm getTb03uXDR() {
		TB03uXDRForm tb03ux = null;
		List<Encounter> encounters = getMdrtbEncountersDuringProgramObs();
		
		if (encounters != null) {
			for (Encounter encounter : encounters) {
				// create a new status item for this encounter
				
				// now place the visit in the appropriate "bucket"
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_TB03U_XDRTB_INTAKE)) {
					tb03ux = new TB03uXDRForm(encounter);
					break;
				}
			}
		}
		
		return tb03ux;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return MdrtbPatientProgram.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		MdrtbPatientProgram program = (MdrtbPatientProgram) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateEnrolled", "mdrtb.program.errors.noDateEnrolled",
		    "Please specify an enrollment date.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "mdrtb.program.errors.noLocation",
		    "Please specify an enrollment location.");
		
		// make sure, if the program is closed, that it must have an outcome
		if (program.getDateCompleted() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "outcome", "mdrtb.program.errors.noOutcome",
			    "Please specify an outcome.");
		}
		
		// make sure, if an outcome has been set, that it has a date completed
		if (program.getOutcome() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateCompleted", "mdrtb.program.errors.NoDateCompleted",
			    "Please specify a date completed.");
		}
		
		// make sure the program enrollment date is not in the future
		if (program.getDateEnrolled() != null && program.getDateEnrolled().after(new Date())) {
			errors.rejectValue("dateEnrolled", "mdrtb.program.errors.dateEnrolledInFuture",
			    "The date enrolled cannot be in the future.");
		}
		
		// make sure the program completion date is not in the future
		if (program.getDateCompleted() != null && program.getDateCompleted().after(new Date())) {
			errors.rejectValue("dateEnrolled", "mdrtb.program.errors.dateCompletedInFuture",
			    "The date completed cannot be in the future.");
		}
		
		// make sure that the enrollment date is before the date completed
		if (program.getDateCompleted() != null && program.getDateEnrolled() != null
		        && program.getDateCompleted().before(program.getDateEnrolled())) {
			errors.rejectValue("dateCompleted", "mdrtb.program.errors.dateCompletedBeforeDateEnrolled",
			    "The program completion date cannot be before the enrollment date.");
		}
		
		if (program.getDateEnrolled() != null) {
			// make sure this program doesn't overlap with any other programs
			// fetch all the programs between the date enrolled and date completed
			for (MdrtbPatientProgram existingProgram : Context.getService(MdrtbService.class)
			        .getMdrtbPatientProgramsInDateRange(program.getPatient(), program.getDateEnrolled(),
			            program.getDateCompleted())) {
				// the only program allowed during the current period is this new program
				if (!program.equals(existingProgram)) {
					errors.reject("mdrtb.program.errors.programOverlap",
					    "This program overlaps with an existing MDR-TB program enrollment for this patient.");
					break;
				}
			}
		}
	}
	
	/**
	 * Equality test The two programs are considered equal if the underlying patient programs are
	 * equal
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof MdrtbPatientProgram)) {
			return false;
		} else {
			return program.equals(((MdrtbPatientProgram) obj).getPatientProgram());
		}
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patientIdentifier == null) ? 0 : patientIdentifier.hashCode());
		result = prime * result + ((program == null) ? 0 : program.hashCode());
		return result;
	}
	
	/**
	 * Implementation of comparable method
	 */
	public int compareTo(MdrtbPatientProgram programToCompare) {
		
		if (this.getDateEnrolled() == null)
			return 1;
		
		else if (programToCompare.getDateEnrolled() == null)
			return 0;
		
		return this.getDateEnrolled().compareTo(programToCompare.getDateEnrolled());
	}
}
