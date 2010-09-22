package org.openmrs.module.mdrtb.web.pihhaiti;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpecimenMigrationController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Set<Concept> testConstructConcepts;
	private Map<String,Specimen> specimenMap = new HashMap<String,Specimen>();
	
    @RequestMapping("/module/mdrtb/pihhaiti/migration/migrate.form")
	public ModelAndView migrateSpecimenData() {
		
		ModelMap map = new ModelMap();
		
		initialize();
		
		// create all the new concepts/mappings/etc we need to add for the migration
		// TODO: NOTE THAT WE WILL WANT TO MAKE SURE WE KEEP CONCEPTS IN SYNC BETWEEN SERVERS/DICTIONARYS
		addAndUpdateConcepts();
		
		// add new global properties		
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("mdrtb.colorMap","1407:lightgrey|1408:lightcoral|1409:lightcoral|1410:lightcoral|2224:lightgrey|3047:khaki|664:lightgreen|703:lightcoral|2474:lightgreen|3017:khaki|1441:lightcoral|1107:none"));
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("mdrtb.locationToDisplayCodeMap","1:N/A|2:CANGE|3:HINCHE|4:N/A|5:MSLI"));
		
		// convert smears on Haiti forms
		convertSmearsOnIntakeAndFollowup();
		
		// migrate any existing Specimen Collection Encounters in the system
		// note: this needs to happen first
		migrateResultatsDeCrachetEncounters();
		
		// migrate any existing BAC and DST encounters in the system
		migrateBacAndDstEncounters();
		
		// clean up old concepts
		cleanUpConcepts();
		
		return new ModelAndView("/module/mdrtb/pihhaiti/specimenMigration",map);
	}
	
    @RequestMapping("/module/mdrtb/pihhaiti/migrate/voidEncounters.form")
	public ModelAndView voidEncounters() {
	
		// this migration controller is meant to run AFTER the specimen migration controller, to void all the BAC and DST encounters
		// (all data in these encounters should have been been migrated to new specimen encounters by the specimen migration controller)
		// for some reason I can't get this encounter voiding to work unless it is in a separate controller
		
		// fetch the bac and dst encounter types
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
	
	
		// now void all unused encounters
		// loop thru all the bac and dst encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			if (encounter.getAllObs().size() == 0) {
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
		 
		
		// retire Bacteriology and DST encounter-types
		Context.getEncounterService().retireEncounterType(Context.getEncounterService().getEncounterType("Bacteriology Result"), "retired as part of MDR-TB migration");
		Context.getEncounterService().retireEncounterType(Context.getEncounterService().getEncounterType("DST Result"), "retired as part of MDR-TB migration");
		
		
		 return new ModelAndView("/module/mdrtb/pihaiti/specimenMigration");
	}
    
	private void initialize() {
		testConstructConcepts = new HashSet<Concept>();
		testConstructConcepts.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT));
		testConstructConcepts.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT));
		testConstructConcepts.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT));
	}
	
	
	private void addAndUpdateConcepts() {
		
		// add concept mappings
		addConceptMapping("DRUG SENSITIVITY TEST CONTAMINATED","DST CONTAMINATED");
		addConceptMapping("WAITING FOR TEST RESULTS","WAITING FOR TEST RESULTS");
		addConceptMapping("LAB TEST REPORT, SCANNED","SCANNED LAB REPORT");
		addConceptMapping("APPEARANCE OF SPUTUM SPECIMEN","APPEARANCE OF SPECIMEN");
		addConceptMapping("SPUTUM","SPUTUM");
		addConceptMapping("STRONGLY POSITIVE","STRONGLY POSITIVE");
		addConceptMapping("MODERATELY POSITIVE", "MODERATELY POSITIVE");
		addConceptMapping("WEAKLY POSITIVE", "WEAKLY POSITIVE");
		addConceptMapping("POSITIVE", "POSITIVE");
		addConceptMapping("NEGATIVE", "NEGATIVE");
		addConceptMapping("CONTAMINATED", "CONTAMINATED");
		
		
		// add new concepts
		// NOTE: need to add french names as well!
		addConcept("TUBERCULOSIS SPECIMEN ID", "Specimen", "Text", "TUBERCULOSIS SPECIMEN ID", "org.openmrs.module.mdrtb");
		addConcept("TUBERCULOSIS TEST DATE ORDERED", "Question", "Date", "TUBERCULOSIS TEST DATE ORDERED", "org.openmrs.module.mdrtb"); // worry about there being two "Date" concept datatypes?
		addConcept("TUBERCULOSIS SPECIMEN COMMENTS", "Finding", "Text", "TUBERCULOSIS SPECIMEN COMMENTS", "org.openmrs.module.mdrtb");
		
		addConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT", "Misc", "N/A", "PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT", "org.openmrs.module.mdrtb"); 
		addConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT", "Test", "Coded", "PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT", "org.openmrs.module.mdrtb"); 
		
		// modify concepts
		
		// PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT
		Concept physicanSmearConstruct = Context.getConceptService().getConceptByName("PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT");
		physicanSmearConstruct.setSet(true);
		physicanSmearConstruct.getConceptSets().add(new ConceptSet(Context.getConceptService().getConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT"), new Double(1)));
		Context.getConceptService().saveConcept(physicanSmearConstruct);
		
		// PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT
		Concept physicanSmearResult = Context.getConceptService().getConceptByName("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT");
		addAnswerConcept(physicanSmearResult, Context.getConceptService().getConceptByName("POSITIVE"));
		addAnswerConcept(physicanSmearResult, Context.getConceptService().getConceptByName("NEGATIVE"));
		
		// TUBERCULOSIS SMEAR MICROSOPY CONSTRUCT
		Concept smearConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT);
		smearConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_ORDERED), new Double(smearConstruct.getConceptSets().size())));
		smearConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_START_DATE), new Double(smearConstruct.getConceptSets().size())));
		Context.getConceptService().saveConcept(smearConstruct);
		
		// TUBERCULOSIS CULTURE CONSTRUCT
		Concept cultureConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT);
		cultureConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM), new Double(cultureConstruct.getConceptSets().size())));
		cultureConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED), new Double(cultureConstruct.getConceptSets().size())));
		Context.getConceptService().saveConcept(cultureConstruct);
		
		// TUBERCULOSIS DRUG SENSITIVITY TEST RESULTS
		Concept dstResults = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_RESULT);
		dstResults.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONTAMINATED), new Double(dstResults.getConceptSets().size())));
		dstResults.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS), new Double(dstResults.getConceptSets().size())));
		Context.getConceptService().saveConcept(dstResults);
		
		// TUBERCULOSIS CULTURE RESULT
		Concept cultureResult  = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		addAnswerConcept(cultureResult, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		addAnswerConcept(cultureResult, Context.getConceptService().getConceptByName("POSITIVE"));
		
		// TUBERCULOSIS DRUG SENSITIVITY TEST METHOD
		Concept testMethod  = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_METHOD);
		addAnswerConcept(testMethod, Context.getConceptService().getConceptByName("OGAWA"));
		addAnswerConcept(testMethod, Context.getConceptService().getConceptByName("LOWENSTEIN-JENSEN"));
		
		// TUBERCULOSIS SMEAR RESULT
		Concept smearResult  = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		addAnswerConcept(smearResult, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		
		// THIOACETAZONE
		Concept thio = Context.getConceptService().getConceptByName("THIOACETAZONE");
		addAnswerConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG),thio);
		addAnswerConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG), thio);
		addAnswerConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG), thio);
		
		// Removed duplicate "referred by" mapping	
		Concept referred = Context.getConceptService().getConcept(3512);
		for(ConceptMap map : referred.getConceptMappings()) {
			if(map.getSourceCode().equals("REFERRED BY")) {
				referred.removeConceptMapping(map);
			}
		}
		
		// Removed duplicate "pulmonary" mapping	
		Concept pulmonary = Context.getConceptService().getConcept(831);
		for(ConceptMap map : pulmonary.getConceptMappings()) {
			if(map.getSourceCode().equals("PULMONARY TUBERCULOSIS")) {
				pulmonary.removeConceptMapping(map);
			}
		}
		Context.getConceptService().saveConcept(pulmonary);
	}
	
	private void addConcept(String name, String clazz, String datatype, String map, String mapSource) {
		Concept concept = new Concept();
		ConceptName conceptName = new ConceptName();
		ConceptMap conceptMap = new ConceptMap();
		Set<ConceptNameTag> conceptNameTags = new HashSet<ConceptNameTag>();
		conceptNameTags.add(Context.getConceptService().getConceptNameTagByName("default"));
		conceptNameTags.add(Context.getConceptService().getConceptNameTagByName("preferred"));
		
		conceptName.setName(name);
		conceptName.setLocale(Locale.ENGLISH);
		conceptName.setTags(conceptNameTags);
		concept.addName(conceptName);
			
		concept.setConceptClass(Context.getConceptService().getConceptClassByName(clazz));
		concept.setDatatype(Context.getConceptService().getConceptDatatypeByName(datatype));
		
		conceptMap.setSourceCode(map);
		conceptMap.setSource(Context.getConceptService().getConceptSourceByName(mapSource));
		concept.addConceptMapping(conceptMap);
		Context.getConceptService().saveConcept(concept);
		
	}
	
	private void addConceptMapping(String name, String mapping) {
		Concept concept = Context.getConceptService().getConceptByName(name);
		ConceptMap map = new ConceptMap();
		map.setSource(Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"));
		map.setSourceCode(mapping);
		concept.addConceptMapping(map);
		Context.getConceptService().saveConcept(concept);
	}
 	
	public void addAnswerConcept(Concept concept, Concept answerConcept) {
		ConceptAnswer answer = new ConceptAnswer();
		answer.setAnswerConcept(answerConcept);
		answer.setDateCreated(new Date());
		answer.setCreator(Context.getAuthenticatedUser());
		// TODO: is this the right way to generate a Uuid?
		answer.setUuid(UUID.randomUUID().toString()); 
		concept.addAnswer(answer);
		Context.getConceptService().saveConcept(concept);
	}
	
	// Note: this is a PIH_specific use case
	private void convertSmearsOnIntakeAndFollowup() {
		// fetch the encounter types associated with the Intake and Follow-up forms
		List<EncounterType> intakeOrFollowup = new LinkedList<EncounterType>();
		intakeOrFollowup.add(Context.getEncounterService().getEncounterType("MDR-TB Intake"));
		intakeOrFollowup.add(Context.getEncounterService().getEncounterType("MDR-TB Follow Up"));
		
		Concept smearConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT);
		Concept smearResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		Concept physicanSmearConstruct = Context.getConceptService().getConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT");
		Concept physicanSmearResult = Context.getConceptService().getConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT");
		
		
		// loop through all these encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, intakeOrFollowup, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			if (encounter.getPatient().isVoided()) {
				log.info("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating Intake or Follow Up encounter " + encounter.getEncounterId());
				
				// cycle through all encounters and change any smear constructs or smear results to "physician-reported" smear constructs or results
				for(Obs obs : encounter.getAllObs(false)) {
					if(smearResult.equals(obs.getConcept())) {
						log.info("Changing obs " + obs.getId() + " to type PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT");
						obs.setConcept(physicanSmearResult);
						Context.getEncounterService().saveEncounter(encounter);
					}
					else if(smearConstruct.equals(obs.getConcept())) {
						log.info("Changing obs " + obs.getId() + " to type PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT");
						obs.setConcept(physicanSmearConstruct);
						Context.getEncounterService().saveEncounter(encounter);
					}
				}
			}
		}
	}
	
	// Note: this is a PIH-specific use case
	private void migrateResultatsDeCrachetEncounters() {
					
		// first we need to create a new dummy form that we will use to keep track of these encounters so that we can redisplay them in 
		// a similar format in the new system
		Form dummyMSPPForm = new Form();
		dummyMSPPForm.setEncounterType(Context.getEncounterService().getEncounterType("Specimen Collection"));
		dummyMSPPForm.setName("Resultats de Crachat");
		dummyMSPPForm.setPublished(true);
		dummyMSPPForm.setVersion("1.0");
		Context.getFormService().saveForm(dummyMSPPForm);
		
		// get the form id and also save it in a global property
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("pihhaiti.dummyMSPPFormId", dummyMSPPForm.getId().toString()));
		
		// also, we need to modify the existing mdrtb forms list to reference this new form
		GlobalProperty forms = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.mdrtb_forms_list");
		forms.setPropertyValue("MDR-TB Rendezvous:html|MDR-TB Donnees de Base:html");
		Context.getAdministrationService().saveGlobalProperty(forms);
		
		// fetch the specimen collection encounter type
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		
		// loop thru all the specimen collection encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			if (encounter.getPatient().isVoided()) {
				log.info("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating resultats de crachet encounter " + encounter.getEncounterId());
			
				// pull all the smear results, and the appearance of the sputum specimen, out of the existing encounter
				Concept smearResultType = Context.getConceptService().getConceptByName("AFB SPUTUM SMEAR"); // NOTE: this is a PIH-specific concept
				Concept appearanceOfSpecimenType = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE); 
			
				List<Obs> smearResults = new LinkedList<Obs>();
				Obs appearanceOfSpecimen = null;
			  
				for(Obs obs : encounter.getAllObs()) {
					if(obs.getConcept().equals(smearResultType)) {
						smearResults.add(obs);
					}
					if(obs.getConcept().equals(appearanceOfSpecimenType)) {
						appearanceOfSpecimen = obs;
					}
				}
			
				// now create a new specimen for each result
				// (the assumption here is that each result is off a different specimen)
				for(Obs smearResult : smearResults) {		
					Specimen specimen = Context.getService(MdrtbService.class).createSpecimen(encounter.getPatient());
				
					// set the form id of the underlying encounter to the dummy MSPP form so that we can pull out this encounters
					((Encounter) specimen.getSpecimen()).setForm(dummyMSPPForm);
					
					// set the date created of the encounter underlying the specimen to the date created of the existing encounter
					// so that we can continue to group the specimens from this encounter together if need be
					((Encounter) specimen.getSpecimen()).setDateCreated(encounter.getDateCreated());
					
					
					// set the type to sputum
					specimen.setType(Context.getConceptService().getConceptByName("SPUTUM")); // NOTE: this is a PIH-specific concept
				
					// set the appearance if necessary
					if(appearanceOfSpecimen != null) {
						specimen.setAppearance(appearanceOfSpecimen.getValueCoded());
					}
				
					// set the date collected if it exists
					if(smearResult.getObsDatetime() != null) {
						specimen.setDateCollected(smearResult.getObsDatetime());
					}
					else {
						log.warn("No date collected recorded for encounter " + encounter.getId());
					}
				
					// set the provider
					specimen.setProvider(encounter.getProvider());
				
					// set the location
					specimen.setLocation(encounter.getLocation());
					
					// now create the smear for this specimen
					Smear smear = specimen.addSmear();
					
					// for these smears, the "lab" is just where the specimen was collected
					smear.setLab(encounter.getLocation());
					
					// now set the result
					smear.setResult(smearResult.getValueCoded());
					
					// set the result date based on the date of the original encounter
					smear.setResultDate(encounter.getEncounterDatetime());
					
					// then save the specimen
					Context.getService(MdrtbService.class).saveSpecimen(specimen);
					
					log.info("Added new specimen " + specimen.getId());
				}
			
				// void the existing encounter
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
		
		// now retire the old crachet form
		Form toRetire = Context.getFormService().getForm("Resultats de Crachat (MSPP)");
		Context.getFormService().retireForm(toRetire, "retired as part of mdr-tb migration");
		
	}
	
	public void migrateBacAndDstEncounters() {
		
		// fetch the bac and dst encounter types
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
	
		// loop thru all the bac and dst encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			// also void any encounters with no obs
			if (encounter.getPatient().isVoided()) {
				log.warn("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else if (encounter.getAllObs().size() == 0) {
				log.warn("Voiding encounter " + encounter.getId() + " because it has no non-voided obs");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating bac/dst results encounter " + encounter.getEncounterId());
			
				Boolean moveObsAndVoidEncounter = false;
				Specimen specimen = null;
			
				// first we need to figure out if we are going to need to create a new specimen for this encounter or not by checking if accession numbers
				for(Obs obs : encounter.getAllObs()) {
					if(obs.getAccessionNumber() != null && specimenMap.get(obs.getAccessionNumber().toUpperCase()) != null) {
						specimen = specimenMap.get(obs.getAccessionNumber().toUpperCase());
						moveObsAndVoidEncounter = true; // since this specimen already exists, we need to move all the obs to the encounter associated with the existing specimen
						break;
					}
				}
			
				// if the specimen is still null at this point, we need to create a new once
				if(!moveObsAndVoidEncounter) {
					specimen = createSpecimenFromEncounter(encounter);
				}
				
				// now make sure all accession numbers within this encounter map to this specimen
				for(Obs obs : encounter.getAllObs()) {
					if(!StringUtils.isEmpty(obs.getAccessionNumber())) {
						if(specimenMap.get(obs.getAccessionNumber().toUpperCase()) != null && specimenMap.get(obs.getAccessionNumber().toUpperCase()) != specimen) {
							log.warn("Specimen " + specimen.getId() + " and specimen " + specimenMap.get(obs.getAccessionNumber().toUpperCase()).getId() + " may be the same. They share the same accession number.");
						}
						else {
							specimenMap.put(obs.getAccessionNumber().toUpperCase(),specimen);
						}
					}
				}
			
			
				// now we need to iterate through all the test obs to pull out specific information to update specimen data
				for(Obs obs : encounter.getObsAtTopLevel(false)) {
					// check to see if this is a test construct				
					if(testConstructConcepts.contains(obs.getConcept())) {
						Obs colonies = null;
					
						log.info("Processing test with id " + obs.getId());
						
						// need to create a place to add any child obs to add to this test, so that we don't throw a concurrent access exception
						// while iterating through the group members; currently this is only used by the organism non-coded case;
						List<Obs> obsToAddToTest = new LinkedList<Obs>();
						
						// iterate through all the obs in the test
						for(Obs childObs : obs.getGroupMembers()) {	
							
							// TODO: do I want to ignore voided obs... get Group Members doesn't do so automatically
							
							log.info("Processing test obs " + childObs.getId() + " of concept type " + childObs.getConcept());
							
							// check to see if this is a the sample source obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE))) {
								// copy and void the existing sample source
								compareAndSetSampleSource(specimen, childObs);
								childObs.setVoided(true);
								//Context.getObsService().voidObs(childObs, "voided as part of mdr-tb migration");
							}	
					
							// check to see if this is a smear or culture result obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT)) || childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT))) {
								// set the date collected based on the value datetime of this obs, and reset the value datetime of this obs
								if(childObs.getValueDatetime() != null) {
									compareAndSetDateCollected(specimen, childObs);
									childObs.setValueDatetime(null);
								}
								// set the accession number on construct to the accession number on the result obs
								if(!StringUtils.isEmpty(childObs.getAccessionNumber())) {
									log.info("Setting accession number on obs " + obs.getId() + " to " + childObs.getAccessionNumber());
									obs.setAccessionNumber(childObs.getAccessionNumber());
								}
							}
					
							// see if this a dst with a sputum collection date on it
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPUTUM_COLLECTION_DATE))) {
								if(childObs.getValueDatetime() != null) {
									compareAndSetDateCollected(specimen, childObs);
								}
								// for some reason, the accession number for DST tests is stored in this construct
								if(!StringUtils.isEmpty(childObs.getAccessionNumber())) {
									log.info("Setting accession number on obs " + obs.getId() + " to " + childObs.getAccessionNumber());
									obs.setAccessionNumber(childObs.getAccessionNumber());
								}
								// for some reason, comments are stored on the sputum collection date; we need to move them to the DST construct
								if(StringUtils.isNotEmpty(childObs.getComment())) {
									obs.setComment(childObs.getComment());
								}
								// now set the sputum collection date obs as voided
								childObs.setVoided(true);
							}
							
							// change all DST contaminated to the proper type
							// NOTE: PIH Haiti specific functionality??
							if(obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT)) && childObs.getConcept().equals(Context.getConceptService().getConceptByName("CONTAMINATED"))) {
								childObs.setConcept(Context.getConceptService().getConceptByName("DRUG SENSITIVITY TEST CONTAMINATED"));
								log.info("Changing concept on obs " + obs.getConcept().getId() + " from CONTAMINATED to DRUG SENSITIVITY TEST CONTAMINATED");
							}
							
							// check to see if this is a colonies obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES))) {
								// only use the most recent colonies obs (to handle bug where colonies was being stored multiple times)
								if(compareAndUpdateColonies(childObs,colonies)) {
									log.warn("Encounter " + encounter.getId() + " has multiple colonies obs with different values. Using obs with most recent datetime.");
								}
							}
							
							// check to see if this is a organism-type non-coded obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED))) {
								// see if we can convert this to a coded type
								if(StringUtils.isNotBlank(childObs.getValueText())) {
									if(childObs.getValueText().equalsIgnoreCase("M. TUBERCULOSIS COMPLEX")) {	
										// if so, convert this to a coded obs
										changeOrganismTypeToCoded(obs, childObs, Context.getConceptService().getConceptByName("M. TUBERCULOSIS COMPLEX"));
										log.info("Changed type of organism non-coded to coded M. TUBERCULOSIS COMPLEX for test " + obs.getId());
									}
									else if(childObs.getValueText().equalsIgnoreCase("M. ABSCESSUS")) {
										changeOrganismTypeToCoded(obs, childObs, Context.getConceptService().getConceptByName("M. ABSCESSUS"));
										log.info("Changed type of organism non-coded obs to coded M. ABSCESSUS for test " + obs.getId());
									}
								}
							}
						}
						
						// now add any new obs that we've created during the looping
						if(obsToAddToTest.size() > 0) {
							for(Obs obsToAdd : obsToAddToTest) {
								obs.addGroupMember(obsToAdd);
							}
						}
					}
							
				}
				if(moveObsAndVoidEncounter) {
					log.info("Moving obs on encounter " + encounter.getId() + " to specimen encounter " + specimen.getId());
					for(Obs obs : encounter.getAllObs()) {
						obs.setEncounter((Encounter) specimen.getSpecimen());
					}
					// Note: moving the voiding of encounters to another controller afterwards to solve issue with all the obs getting voided;
				}
			
				Context.getService(MdrtbService.class).saveSpecimen(specimen);
			}
		}
	}
	
	private Specimen createSpecimenFromEncounter(Encounter encounter) {
		// change the encounter type to "specimen collection" encounter
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		
		// now instantiate a new specimen using this encounter
		Specimen specimen = new SpecimenImpl(encounter);
		
		// set the patient and provider of the specimen to the patient and provider of the underlying encounter
		// (not really needed, but just in case)
		specimen.setPatient(encounter.getPatient());
		specimen.setProvider(encounter.getProvider());
		specimen.setLocation(encounter.getLocation());
		
		// NOTE: PIH-HAITI specific functionality...
		// if the location on the initial encounter is a the MSLI, set the location to unknown (b/c specimen would never be COLLECTED at MSLI
		if(encounter.getLocation().getId() == 5) {
			specimen.setLocation(Context.getLocationService().getLocation(1));
		}
		
		return specimen;
	}
		
	private void compareAndSetSampleSource(Specimen specimen, Obs obs) {
		// nothing to do if no value for this obs
		if(obs.getValueCoded() == null) {
			return;
		}
		
		// fetch the obs on this specimen that holds the sample source
		Obs type = null;
		for(Obs obs2 : ((Encounter) specimen.getSpecimen()).getObsAtTopLevel(false)){
			if(obs2.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE))) {
				type = obs;
				break;
			}
		}
		
		if(type == null) {
			specimen.setType(obs.getValueCoded());
			return;
		}
		if(type.getValueCoded() != obs.getValueCoded()) {
			log.warn("Mismatched sample type on specimen " + specimen.getId() + "; using sample source with most recent obs date");
		}
		
		if(type.getObsDatetime() == null || (obs.getObsDatetime() != null && type.getObsDatetime().before(obs.getObsDatetime())) ) {
			type = Obs.newInstance(obs);
		}
	}
	
	private void compareAndSetDateCollected(Specimen specimen, Obs obs) {
		// nothing to do if no value for this obs
		if(obs.getValueDatetime() == null) {
			return;
		}
		
		Date datetime = ((Encounter) specimen.getSpecimen()).getEncounterDatetime();
		
		if(datetime == null) {
			datetime = obs.getValueDatetime();
			return;
		}
		
		if(datetime.before(obs.getValueDatetime())) {
			log.warn("Mismatched collection date on specimen " + specimen.getId() + "; using oldest date");
			return;
		}
		
		if(datetime.after(obs.getValueDatetime())) {
			log.warn("Mismatched collection date on specimen " + specimen.getId() + "; using oldest date");
			datetime = obs.getValueDatetime();
			return;
		}
		
	}
		
	// to fix an existing bug where colony obs were being stored in multiple
	private Boolean compareAndUpdateColonies(Obs source, Obs target) {
		Boolean returnValue = null;
		
		if(source == null) {
			return false;
		}
		
		if(target == null) {
			target = source;
			return false;
		}
		
		if(source.getValueNumeric() != target.getValueNumeric()) {
			returnValue = true;
		}
				
		if(target.getObsDatetime() == null || (source.getObsDatetime() != null && source.getObsDatetime().before(target.getObsDatetime()))) {
			log.info("Voiding colony observation " + target.getId());
			target.setVoided(true);
			//Context.getObsService().voidObs(target, "voided as part of mdr-tb migration");
			target = source;  // is this kosher?
		}
		else {
			log.info("Voiding colony observation " + target.getId());
			source.setVoided(true);
			//Context.getObsService().voidObs(source, "voided as part of mdr-tb migration");
		}
		
		return returnValue;
		
	}
	
	private void changeOrganismTypeToCoded(Obs test, Obs nonCoded, Concept type) {
		Boolean foundCoded = false;
		
		// cycle through all the obs on this test, find the *coded* type of organism and set it to the proper value
		for(Obs obs : test.getGroupMembers()) {
			if(obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM))) {
				obs.setValueCoded(type);
				foundCoded = true;
				break;
			}
		}
		
		// if we have found the coded obs, go ahead and voided the non-coded one
		if(foundCoded) {
			nonCoded.setVoided(true);
		}
		else {
			// we should never reach this case, but if for some reason there isn't a coded obs on this test, change the non-coded obs into a coded one
			nonCoded.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM));
			nonCoded.setValueCoded(type);
			nonCoded.setValueText(null);
		}
	}
	
	private void cleanUpConcepts() {
		// remove AFB smear and appearance of specimen from smear construct
		Concept smearConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT);
		Collection<ConceptSet> sets = smearConstruct.getConceptSets();
		
		Iterator<ConceptSet> i = sets.iterator();
		
		while(i.hasNext()) {
			ConceptSet set = i.next();
			if(set.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE))
					|| set.getConcept().equals(Context.getConceptService().getConceptByName("AFB SPUTUM SMEAR"))) {
					i.remove();
			}
		}
		
		Context.getConceptService().saveConcept(smearConstruct);
	}
}