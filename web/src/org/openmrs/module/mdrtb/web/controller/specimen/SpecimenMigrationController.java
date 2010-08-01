package org.openmrs.module.mdrtb.web.controller.specimen;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
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
	
	private MdrtbFactory mdrtbFactory;
	private Set<Concept> testConstructConcepts;
	private Map<String,Specimen> specimenMap = new HashMap<String,Specimen>();
	
    @RequestMapping("/module/mdrtb/specimen/migrate.form")
	public ModelAndView migrateSpecimenData() {
		
		ModelMap map = new ModelMap();
		
		initialize();
		
		// migrate any existing Specimen Collection Encounters in the system
		// note: this needs t happen first
	//	migrateResultatsDeCrachetEncounters();
		
		// migrate any existing BAC and DST encounters in the system
		migrateBacAndDstEncounters();
		
		return new ModelAndView("/module/mdrtb/specimen/specimenMigration",map);
	}
	
	public void initialize() {
		mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();

		testConstructConcepts = new HashSet<Concept>();
		testConstructConcepts.add(mdrtbFactory.getConceptSmearParent());
		testConstructConcepts.add(mdrtbFactory.getConceptCultureParent());
		testConstructConcepts.add(mdrtbFactory.getConceptDSTParent());
	}
	
	// Note: this is a PIH-specific use case
	public void migrateResultatsDeCrachetEncounters() {
					
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
				Concept appearanceOfSpecimenType = mdrtbFactory.getConceptAppearanceOfSpecimen(); 
			
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
					
					// then save the specimen
					Context.getService(MdrtbService.class).saveSpecimen(specimen);
					
					log.info("Added new specimen " + specimen.getId());
				}
			
				// void the existing encounter
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
	}
	
	public void migrateBacAndDstEncounters() {
		
		// fetch the bac and dst encounter types
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
			
		// loop thru all the bac and dst encounters
/**		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			// also void any encounters with no obs
			if (encounter.getPatient().isVoided()) {
				log.info("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else if (encounter.getAllObs().size() == 0) {
				log.info("Voiding encounter " + encounter.getId() + " because it has no non-voided obs");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating bac/dst results encounter " + encounter.getEncounterId());
			
				Boolean moveObsAndVoidEncounter = false;
				Specimen specimen = null;
			
				// first we need to figure out if we are going to need to create a new specimen for this encounter or not by checking if accession numbers
				for(Obs obs : encounter.getAllObs()) {
					if(obs.getAccessionNumber() != null && specimenMap.get(obs.getAccessionNumber()) != null) {
						specimen = specimenMap.get(obs.getAccessionNumber());
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
						if(specimenMap.get(obs.getAccessionNumber()) != null && specimenMap.get(obs.getAccessionNumber()) != specimen) {
							log.warn("Specimen " + specimen.getId() + " and specimen " + specimenMap.get(obs.getAccessionNumber()).getId() + " may be the same. They share the same accession number.");
						}
						else {
							specimenMap.put(obs.getAccessionNumber(),specimen);
						}
					}
				}
			
			
				// now we need to iterate through all the test obs
				for(Obs obs : encounter.getObsAtTopLevel(false)) {
					// check to see if this is a test construct				
					if(testConstructConcepts.contains(obs.getConcept())) {
						Obs colonies = null;
					
						log.info("Processing test with id " + obs.getId());
						
						// iterate through all the obs in the test
						for(Obs childObs : obs.getGroupMembers()) {	
							
							// TODO: do I want to ignore voided obs... get Group Members doesn't do so automatically
							
							log.info("Processing test obs " + childObs.getId() + " of concept type " + childObs.getConcept());
							
							// check to see if this is a the sample source obs
							if(childObs.getConcept().equals(mdrtbFactory.getConceptSampleSource())) {
								// copy and void the existing sample source
								compareAndSetSampleSource(specimen, childObs);
								childObs.setVoided(true);
								//Context.getObsService().voidObs(childObs, "voided as part of mdr-tb migration");
							}	
					
							// check to see if this is a smear or culture result obs, or a sputum collection date
							if(childObs.getConcept().equals(mdrtbFactory.getConceptSmearResult()) || childObs.getConcept().equals(mdrtbFactory.getConceptCultureResult())) {
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
							if(childObs.getConcept().equals(mdrtbFactory.getConceptSputumCollectionDate())) {
								if(childObs.getValueDatetime() != null) {
									compareAndSetDateCollected(specimen, childObs);
									childObs.setVoided(true);
									//Context.getObsService().voidObs(childObs, "voided as part of mdr-tb migration");
								}
								// for some reason, the accession number for DST tests is stored in this construct
								if(!StringUtils.isEmpty(childObs.getAccessionNumber())) {
									log.info("Setting accession number on obs " + obs.getId() + " to " + childObs.getAccessionNumber());
									obs.setAccessionNumber(childObs.getAccessionNumber());
								}
							}
							
							// change all DST contaminated to the proper type
							// NOTE: PIH Haiti specific functionality??
							if(obs.getConcept().equals(mdrtbFactory.getConceptDSTParent()) && childObs.getConcept().equals(Context.getConceptService().getConceptByName("CONTAMINATED"))) {
								childObs.setConcept(Context.getConceptService().getConceptByName("DRUG SENSITIVITY TEST CONTAMINATED"));
								log.warn("Changing concept on obs " + obs.getConcept().getId() + " from CONTAMINATED to DRUG SENSITIVITY TEST CONTAMINATED");
							}
							
							// check to see if this is a colonies obs
							if(childObs.getConcept().equals(mdrtbFactory.getConceptColonies())) {
								// only use the most recent colonies obs (to handle bug where colonies was being stored multiple times)
								if(compareAndUpdateColonies(childObs,colonies)) {
									log.warn("Encounter " + encounter.getId() + " has multiple colonies obs with different values. Using obs with most recent datetime.");
								}
							}
						}
					}
							
				}
				if(moveObsAndVoidEncounter) {
					log.info("Moving obs on encounter " + encounter.getId() + " to specimen encounter " + specimen.getId());
					for(Obs obs : encounter.getAllObs()) {
					obs.setEncounter((Encounter) specimen.getSpecimen());
					}
					// Note: moving the voiding of encounters to afterwards to solve issue with all the obs getting voided
					//Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
				}
			
				Context.getService(MdrtbService.class).saveSpecimen(specimen);
			}
		}
	*/
		// now void all unused encounters
		// loop thru all the bac and dst encounters
		
		// TODO: might need to pull this into a separate call if I can't get this to work properly
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			if (encounter.getAllObs().size() == 0) {
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
	}
	
	
	public Specimen createSpecimenFromEncounter(Encounter encounter) {
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
		
	
	
	
	public void compareAndSetSampleSource(Specimen specimen, Obs obs) {
		// nothing to do if no value for this obs
		if(obs.getValueCoded() == null) {
			return;
		}
		
		// fetch the obs on this specimen that holds the sample source
		Obs type = null;
		for(Obs obs2 : ((Encounter) specimen.getSpecimen()).getObsAtTopLevel(false)){
			if(obs2.getConcept().equals(mdrtbFactory.getConceptSampleSource())) {
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
	
	public void compareAndSetDateCollected(Specimen specimen, Obs obs) {
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
	public Boolean compareAndUpdateColonies(Obs source, Obs target) {
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
}