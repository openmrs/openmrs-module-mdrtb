package org.openmrs.module.mdrtb.web.pihhaiti;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A hacky class used to upgrade the specimen data in the Haiti Mdr-tb implementation to the new specimen tracking systems
 */

@Controller
public class SpecimenMigrationValidationController {

	protected final Log log = LogFactory.getLog(getClass());
	
	private Collection<Concept> testConstructConcepts;
	
	private Collection<Concept> smearConcepts; // valid concepts to be found within a smear construct
	
	private Collection<Concept> cultureConcepts; // valid concepts to be found within a culture construct
	
	private Collection<Concept> dstConcepts; // valid concepts to be found within a dst construct
	
	private Collection<Concept> dstResultConcepts; // valid concepts to be found with a dst result construct
	
	private Collection<Concept> dstResultTypes; // a subset of dstResultConcepts that just contains the concepts that are actual results
	
	private Collection<Concept> sampleSource;
	
	private Collection<Concept> smearResult;
	
	private Collection<Concept> smearMethod;
	
	private Collection<Concept> cultureResult;
	
	private Collection<Concept> cultureMethod;
	
	private Collection<Concept> typeOfOrganism;
	
	private Collection<Concept> dstMethod; 
	
	private Collection<Concept> dstDrugs;
	
	private MdrtbFactory mdrtbFactory;
	
	// TODO: do I want to use this in the end, or create a second handle method??
	private Boolean valid = true;
	
	private String results = "";
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/mdrtb/pihhaiti/validate.form") 
	public ModelAndView validateSpecimenData() {
		
		ModelMap map = new ModelMap();
		
		int patientsWithNoEnrollDate = 0;
		int patientsWithMoreThanOneEnrollDate = 0;
		int patientsWithSpecimenCollectionsBeforeEnrollDate = 0;
		
		initialize();

		for(Patient patient: Context.getPatientService().getAllPatients()) {
	
			// first, fetch all the specimens for this patient
			List<Specimen> specimens = Context.getService(MdrtbService.class).getSpecimens(patient);
			
			// now fetch the program start date
			// TODO: fix this
			Date startDate;
			Program mdrtb = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
			List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, mdrtb, null, null, null, null, false);
		
			if(programs.size() == 0) {
				patientsWithNoEnrollDate++;
			}
			else {
				if(programs.size() > 1) {
					patientsWithMoreThanOneEnrollDate++;
				}
				
				startDate = programs.get(0).getDateEnrolled();
			
				Boolean flag = false;
				
				for(Specimen specimen : specimens) {
					if(specimen.getDateCollected().before(startDate)) {
						results = results.concat("Patient " + patient.getId() + " has collection date " + specimen.getDateCollected() + " before enroll date " + startDate + ".</br>");	
						if(!flag) {
							patientsWithSpecimenCollectionsBeforeEnrollDate++;
							flag = true;
						}
					}
				}
			}
			
			results = results.concat("<br/>");
		}
		
		
		/**
		
		// loop thru all patients in the system
		for(Patient patient : Context.getPatientService().getAllPatients()) {
			results = results.concat("Integrity checking patient #" + patient.getId() +":<br/>");
			
			// now loop over all Bac and DST encounter for this patient			
			List<EncounterType> testEncounterTypes = new LinkedList<EncounterType>();
			testEncounterTypes.add(Context.getEncounterService().getEncounterType(5));
			testEncounterTypes.add(Context.getEncounterService().getEncounterType(6));
			
			for(Encounter encounter : Context.getEncounterService().getEncounters(patient, null, null, null, null, testEncounterTypes, null, false)) {
				validateTests(encounter);
			}
			
		// TODO: specifically handle the colonies issue
			
		}
		
		*/
		
		results = results.concat("Patients with no enroll date = " + patientsWithNoEnrollDate + "<br/>");
		results = results.concat("Patients with more than one enroll date = " + patientsWithMoreThanOneEnrollDate + "<br/>");
		results = results.concat("Patients with specimen collections before enroll date = " + patientsWithSpecimenCollectionsBeforeEnrollDate + "<br/>");
		
		map.put("results", results);
		
		return new ModelAndView("/module/mdrtb/pihhaiti/specimenMigrationValidation",map);
	}
	
	// TODO: do I need to check for doubles of anything--ie, too many of a obs type on a construct
	
	private void initialize() {
		mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		testConstructConcepts = new HashSet<Concept>();
		testConstructConcepts.add(mdrtbFactory.getConceptSmearParent());
		testConstructConcepts.add(mdrtbFactory.getConceptCultureParent());
		testConstructConcepts.add(mdrtbFactory.getConceptDSTParent());
		
		// TODO: what do we want to do with these:
		testConstructConcepts.add(mdrtbFactory.getConceptScannedLabReport());
		testConstructConcepts.add(mdrtbFactory.getConceptSmearConversion());
		testConstructConcepts.add(mdrtbFactory.getConceptSmearReconversion());
		testConstructConcepts.add(mdrtbFactory.getConceptCultureConversion());
		testConstructConcepts.add(mdrtbFactory.getConceptCultureReconversion());
		
		smearConcepts = Context.getConceptService().getConceptsByConceptSet(mdrtbFactory.getConceptSmearParent());
		// modify smearConcepts to pull out the AFB stuff...
		smearConcepts.remove(Context.getConceptService().getConcept(1444)); // AFB sputum smear (from PIH Crachet form)
		smearConcepts.remove(Context.getConceptService().getConcept(2488)); // AFB appearance of specimen (from PIH Crachet form)
		
		
		cultureConcepts = Context.getConceptService().getConceptsByConceptSet(mdrtbFactory.getConceptCultureParent());
	
		// have to add these manually since getConceptsByConceptSet returns all sub-child, but not other sets
		dstConcepts = new HashSet<Concept>();
		dstConcepts.add(mdrtbFactory.getConceptDSTResultParent());
		dstConcepts.add(mdrtbFactory.getConceptDSTComplete());
		dstConcepts.add(mdrtbFactory.getConceptSampleSource());
		dstConcepts.add(mdrtbFactory.getConceptSputumCollectionDate());
		dstConcepts.add(mdrtbFactory.getConceptDateReceived());
		dstConcepts.add(mdrtbFactory.getConceptStartDate());
		dstConcepts.add(mdrtbFactory.getConceptDSTMethod());
		dstConcepts.add(mdrtbFactory.getConceptTypeOfOrganism());
		dstConcepts.add(mdrtbFactory.getConceptTypeOfOrganismNonCoded());
		dstConcepts.add(mdrtbFactory.getConceptDirectIndirect());
		dstConcepts.add(mdrtbFactory.getConceptColoniesInControl());
		dstConcepts.add(mdrtbFactory.getConceptResultDate());
					
		dstResultConcepts = Context.getConceptService().getConceptsByConceptSet(mdrtbFactory.getConceptDSTResultParent());
		dstResultTypes = new HashSet<Concept>(dstResultConcepts);
		dstResultTypes.remove(mdrtbFactory.getConceptColonies());
		dstResultTypes.remove(mdrtbFactory.getConceptConcentration());
		
		// set all the concept answer sets
		sampleSource = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptSampleSource().getAnswers()) {
			sampleSource.add(ans.getAnswerConcept());
		}
		
		smearResult = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptSmearResult().getAnswers()) {
			smearResult.add(ans.getAnswerConcept());
		}
		
		smearMethod = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptSmearMicroscopyMethod().getAnswers()) {
			smearMethod.add(ans.getAnswerConcept());
		}
		
		typeOfOrganism = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptTypeOfOrganism().getAnswers()) {
			typeOfOrganism.add(ans.getAnswerConcept());
		}
		
		cultureResult = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptCultureResult().getAnswers()) {
			cultureResult.add(ans.getAnswerConcept());
		}
		
		cultureMethod = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptCultureMethod().getAnswers()) {
			cultureMethod.add(ans.getAnswerConcept());
		}
		
		dstMethod = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptDSTMethod().getAnswers()) {
			dstMethod.add(ans.getAnswerConcept());
		}
		
		dstDrugs = new HashSet<Concept>();
		for(ConceptAnswer ans : mdrtbFactory.getConceptSusceptibleToTuberculosisDrug().getAnswers()) {
			dstDrugs.add(ans.getAnswerConcept());
		}
		
	}
	
	private void validateTests(Encounter encounter) {
		
		Person patient = encounter.getPatient();
		
		// first check all the obs in the encounter for basic errors
		for(Obs obs : encounter.getAllObs(false)) {
			// make sure this obs has the same patient and provider as it's parent encounter
			if(obs.getPerson() != null && obs.getPerson() != patient) {
				results = results.concat("<span style=\"color:red\">The patient for obs " + obs.getId() + " does not match it's parent encounter.</span><br/>");
			}
		}
		
		// now iterate through only the top level obs
		for(Obs obs : encounter.getObsAtTopLevel(false)) {
			// make sure all the first-level obs are valid
			if(!testConstructConcepts.contains(obs.getConcept())){
				results = results.concat("<span style=\"color:red\">Invalid top-level obs of type " + obs.getConcept().getName() + " - " + obs.getConcept().getId() + "</span><br/>");
				valid = false;
			}
			// now make sure the test only contains valid obs
			if(obs.getConcept() == mdrtbFactory.getConceptSmearParent()) {
				validateSmear(obs);
			}
			else if (obs.getConcept() == mdrtbFactory.getConceptCultureParent()) {
				validateCulture(obs);
			}
			else if (obs.getConcept() == mdrtbFactory.getConceptDSTParent()) {
				validateDst(obs);
			}	
		}
		
		Concept sampleSource = null;
		Date collectionDate = null;
		// now make sure that all the sample sources associated with this encounter are the same, and that the collection dates are the same
		for(Obs obs : encounter.getAllObs(false)) {
			// check the sample sources
			if(obs.getConcept() == mdrtbFactory.getConceptSampleSource()) {
				if(sampleSource != null && obs.getValueCoded() != null && obs.getValueCoded() != sampleSource) {
					results = results.concat("<span style=\"color:red\">Mismatched sample source of types: " + sampleSource.getName() + " and " + obs.getValueCoded().getName() + "</span><br/>");
				}
				sampleSource = obs.getValueCoded();
			}
			// check the collection dates
			if(obs.getConcept() == mdrtbFactory.getConceptSmearParent() || obs.getConcept() == mdrtbFactory.getConceptCultureParent()) {
				if(collectionDate != null && obs.getValueDatetime() != null && collectionDate != obs.getValueDatetime()) {
					results = results.concat("<span style=\"color:red\">Mismatched collect dates: " + collectionDate + " and " + obs.getValueDatetime());
				}
				collectionDate = obs.getValueDatetime();
			}
		}
		if(collectionDate == null) {
			results = results.concat("<span style=\"color:red\"> No collection date for this sample</span><br/>");
		}
	}
	
	private void validateSmear(Obs obs) {	
		
		Collection<Concept> concepts = new HashSet<Concept>(smearConcepts);
		
		for(Obs childObs : obs.getGroupMembers()) {
			// make sure that all child obs are of the proper type for a smear
			if(!concepts.contains(childObs.getConcept())) {
				results = results.concat("<span style=\"color:red\">Invalid smear obs in set " + obs.getId() + " of type " + childObs.getConcept().getName() + " - " + childObs.getConcept().getId() + "</span><br/>");
				valid = false;
			}
			else {
				// remove the concept (so we can make sure that no concept appears twice)
				concepts.remove(childObs.getConcept());
			}
			
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptSampleSource() && !sampleSource.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid smear sample source as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptSmearResult() && !smearResult.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid smear result as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptSmearMicroscopyMethod() && !smearMethod.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid smear method as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
		}
		
		// TODO: next, make sure there aren't multiples of anything!
		// TODO: any other validation that needs to be done?
	}
	
	private void validateCulture(Obs obs) {
		Collection<Concept> concepts = new HashSet<Concept>(cultureConcepts);
		
		// TODO: remember that get group members includes voided obs!
		
		for(Obs childObs : obs.getGroupMembers()) {
			// make sure that all child obs are of the proper type for a culture
			if(!concepts.contains(childObs.getConcept())) {
				results = results.concat("<span style=\"color:red\">Invalid culture obs in set " + obs.getId() + " of type " + childObs.getConcept().getName() + " - " + childObs.getConcept().getId() + "</span><br/>");
				valid = false;
			}
			else {
				concepts.remove(childObs.getConcept());
			}
			
			// specific test to check the colonies issue
			if(childObs.getConcept() == mdrtbFactory.getConceptColonies() && childObs.getValueNumeric() != null) {
					results = results.concat("<span style=\"color:red\">Colony value: = " + childObs.getValueNumeric().toString() + "<br/></span>");
			}
		
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptSampleSource() && !sampleSource.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid smear sample source as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptCultureResult() && !cultureResult.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid culture result as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptCultureMethod() && !cultureMethod.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid culture method as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptTypeOfOrganism() && !typeOfOrganism.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid type of organism as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
		}
	}
	
	private void validateDst(Obs obs) {
		for(Obs childObs : obs.getGroupMembers()) {
			Collection<Concept> concepts = new HashSet<Concept>(dstConcepts);
			
			// make sure that all child obs are of the proper type for a dst
			if(!concepts.contains(childObs.getConcept())) {
				results = results.concat("<span style=\"color:red\">Invalid dst obs in set " + obs.getId() + " of type " + childObs.getConcept().getName() + " - " + childObs.getConcept().getId() + "</span><br/>");
				valid = false;
			}
			// now make sure that all the dst result sets only have valid obs as child
			if(childObs.getConcept() == mdrtbFactory.getConceptDSTResultParent()) {
				validateDstResult(childObs);
			}
			else {
				concepts.remove(childObs.getConcept());
			}
			
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptSampleSource() && !sampleSource.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid dst sample source as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptDSTMethod() && !dstMethod.contains(childObs.getValueCoded()) && childObs.getValueCoded() != null ) {
				results = results.concat("<span style=\"color:red\">Invalid dst method as answer to " + childObs.getId() + ":" + childObs.getConcept().getName() + " of type " + childObs.getValueCoded().getId() + " - " + childObs.getValueCoded().getName() + "</span><br/>");
				valid = false;
			}
			// make sure that any of the value-coded have only valid answer
			if(childObs.getConcept() == mdrtbFactory.getConceptTypeOfOrganism() && !typeOfOrganism.contains(childObs.getValueCoded()) ) {
				results = results.concat("<span style=\"color:red\">Invalid type of organism as answer to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
				valid = false;
			} 
		} 
	}
	
	private void validateDstResult(Obs obs) {
		for(Obs childObs : obs.getGroupMembers()) {
			Set<Concept> concepts = new HashSet<Concept>(dstResultConcepts);
			
			if(!concepts.contains(childObs.getConcept())) {
				results = results.concat("<span style=\"color:red\">Invalid dst results obs in set " + obs.getId() + " of type " + childObs.getConcept().getName() + " - " + childObs.getConcept().getId() + "</span><br/>");
				valid = false;
			}
			// if this one of the result types, remove all other types, because we can't have more than one result type per result--that wouldn't make sense
			else if(dstResultTypes.contains(childObs.getConcept())) {
				concepts.removeAll(dstResultTypes);
				
				// now test to make sure that the answer to the result type is valid
				if(!dstDrugs.contains(childObs.getValueCoded()) ) {
					results = results.concat("<span style=\"color:red\">Invalid dst drug type assigned to " + childObs.getId() + " of type " + childObs.getValueCoded().getName() + " - " + childObs.getValueCoded().getId() + "</span><br/>");
					valid = false;
				} 
			}
			else {
				concepts.remove(childObs.getConcept());
			}
		}
	}
}
