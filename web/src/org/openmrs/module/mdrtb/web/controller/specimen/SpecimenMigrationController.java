package org.openmrs.module.mdrtb.web.controller.specimen;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimenImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpecimenMigrationController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private MdrtbFactory mdrtbFactory;
	private Set<Concept> testConstructConcepts;
	
    @RequestMapping("/module/mdrtb/specimen/migrate.form")
	public ModelAndView migrateSpecimenData() {
		
		ModelMap map = new ModelMap();
		
		initialize();
		
		// migrate any existing Specimen Collection Encounters in the system
		//migrateResultatsDeCrachetEncounters();
		
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
			}
			else {
				log.info("Migrating resultats de crachet encounter " + encounter.getEncounterId());
			
				// pull all the smear results, and the appearance of the sputum specimen, out of the existing encounter
				Concept smearResultType = Context.getConceptService().getConceptByName("AFB SPUTUM SMEAR"); // NOTE: this is a PIH-specific concept
				Concept appearanceOfSpecimenType = mdrtbFactory.getConceptAppearanceOfSpecimen(); 
			
				List<Obs> smearResults = new LinkedList<Obs>();
				Obs appearanceOfSpecimen = null;
			
				for(Obs obs : encounter.getAllObs()) {
					if(obs.getConcept() == smearResultType) {
						smearResults.add(obs);
					}
					if(obs.getConcept() == appearanceOfSpecimenType) {
						appearanceOfSpecimen = obs;
					}
				}
			
				// now create a new specimen for each result
				// (the assumption here is that each result is off a different specimen)
				for(Obs smearResult : smearResults) {		
					MdrtbSpecimen specimen = Context.getService(MdrtbService.class).createSpecimen(encounter.getPatient());
				
					// set the type to sputum
					specimen.setType(Context.getConceptService().getConceptByName("SPUTUM")); // NOTE: this is a PIH-specific concept
				
					// set the appearance if necessary
					if(appearanceOfSpecimen != null) {
						specimen.setAppearanceOfSpecimen(appearanceOfSpecimen.getValueCoded());
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
					MdrtbSmear smear = specimen.addSmear();
					
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
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			if (encounter.getPatient().isVoided()) {
				log.info("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
			}
			else {
				log.info("Migrating bac/dst results encounter " + encounter.getEncounterId());
			}
			
			// now we need to iterate through all the test obs in the encounter and perform various modifications
			Obs type = null;
			Date collectionDate = null;
			Date collectionDateObsDate = null;
			
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if(testConstructConcepts.contains(obs.getConcept())) {
					// if this is a test, iterate through all the obs in the test
					Obs colonies = null;
					String accessionNumber = obs.getAccessionNumber();
					for(Obs childObs : obs.getGroupMembers()) {
						// set the accession number to same as parent
						childObs.setAccessionNumber(accessionNumber);
						
						// check to see if this is a the sample source obs
						if(childObs.getConcept() == mdrtbFactory.getConceptSampleSource()) {
							if(compareAndUpdateSampleSource(childObs,type)) {
								log.warn("Encounter " + encounter.getId() + " has multiple sample source obs with different values. Using obs with most recent datetime.");
							}
							// void the sample source on this test construct, as we are going to move it up to the specimen level
							Context.getObsService().voidObs(childObs, "voided as part of mdr-tb migration");
						}	
					
						// handle issues specific only to smears and cultures
						if(obs.getConcept() == mdrtbFactory.getConceptSmearParent() || obs.getConcept() == mdrtbFactory.getConceptCultureParent()) {
							
							// check to see if this is a smear or culture result obs
							if(childObs.getConcept() == mdrtbFactory.getConceptSmearResult() || childObs.getConcept() == mdrtbFactory.getConceptCultureResult()) {
								// check to see if there's a collection date stored in the value_datetime field of the result observation
								compareAndUpdateCollectionDate(childObs, collectionDate,collectionDateObsDate);
								
								// now set the existing value datetime to null, since we will no longer be storing the date collected here
								childObs.setValueDatetime(null);
							}
						}
						
						// handle issues specific to cultures
						if(obs.getConcept() == mdrtbFactory.getConceptCultureParent()) {
							// check to see if this is a colonies obs
							if(childObs.getConcept() == mdrtbFactory.getConceptColonies()) {
								// only use the most recent colonies obs (to handle bug where colonies was being stored multiple times)
								if(compareAndUpdateColonies(childObs,colonies)) {
									log.warn("Encounter " + encounter.getId() + " has multiple colonies obs with different values. Using obs with most recent datetime.");
								}
							}
						}
						
						// handle issues specific to DSTs
						if(obs.getConcept() == mdrtbFactory.getConceptDSTParent()) {
							// handle the date
							if(childObs.getConcept() == mdrtbFactory.getConceptSputumCollectionDate()) {
								// check to see if there's a date stored in the value_datetime field of the result observation
								compareAndUpdateCollectionDate(childObs, collectionDate,collectionDateObsDate);
								
								// now void the existing obs, since we aren't using it anymore
								Context.getObsService().voidObs(childObs, "voided as part of mdr-tb migration");
							}
							
							// change all contaminated to the proper type
							// NOTE: PIH Haiti specific functionality!
							if(childObs.getConcept() == Context.getConceptService().getConceptByName("CONTAMINATED")) {
								childObs.setConcept(Context.getConceptService().getConceptByName("DRUG SENSITIVITY TEST CONTAMINATED"));
							}
						}
					}
				}
			}
			
			// change the encounter type to "specimen collection" encounter
			encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
			
			// now instantiate a new specimen using this encounter
			MdrtbSpecimen specimen = new MdrtbSpecimenImpl(encounter);
			
			// set the patient and provider of the specimen to the patient and provider of the underlying encounter
			specimen.setPatient(encounter.getPatient());
			specimen.setProvider(encounter.getProvider());
			specimen.setLocation(encounter.getLocation());
			
			// NOTE: PIH-HAITI specific functionality...
			// if the location on the initial encounter is a the MSLI, set the location to unknown (b/c specimen would never be COLLECTED at MSLI
			if(encounter.getLocation().getId() == 5) {
				specimen.setLocation(Context.getLocationService().getLocation(1));
			}
			
			
			// set the sample source
			if(type != null) {
				specimen.setType(type.getValueCoded());
			}
			
			// set the date collected
			if(collectionDate != null) {
				specimen.setDateCollected(collectionDate);
			}

			// save the specimen
			Context.getService(MdrtbService.class).saveSpecimen(specimen);
		}
	}
	
	
	// this method compares the source object to the target object, and returns true if the
	// source and target are both not null, and their values aren't equal
	// it also sets the target object to the obs of the two with the most recent datetime 
	public Boolean compareAndUpdateSampleSource(Obs source, Obs target) {		
		Boolean returnValue = null;
		
		if(source == null) {
			return false;
		}
		
		if(target == null) {
			target = source;
			return false;
		}
		
		if(source.getValueCoded() != target.getValueCoded()) {
			returnValue = true;
		}
				
		if(target.getObsDatetime() == null || (source.getObsDatetime() != null && source.getObsDatetime().before(target.getObsDatetime()))) {
			target = source;
		}
		
		return returnValue;
	}
	
	// to fix an existing bug where colony obs were being stored in multiple
	public Boolean compareAndUpdateColonies(Obs source, Obs target) {
		Boolean returnValue = null;
		
		if(source == null) {
			Context.getObsService().voidObs(source, "voided as part of mdr-tb migration");
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
			Context.getObsService().voidObs(target, "voided as part of mdr-tb migration");
			target = source;
		}
		else {
			Context.getObsService().voidObs(source, "voided as part of mdr-tb migration");
		}
		
		return returnValue;
		
	}
	
	// to port collection date to the new format we are using
	public void compareAndUpdateCollectionDate(Obs source, Date collectionDate, Date collectionDateObsDate) {
		if(source.getValueDatetime() == null) {
			return;
		}
		
		if(collectionDate == null) {
			collectionDate = source.getValueDatetime();
			collectionDateObsDate = source.getObsDatetime();
		}
		
		if(collectionDate != source.getValueDatetime()) {
			log.warn("Encounter " + source.getEncounter().getId() + " has multiple date collected obs. Using obs with most recent obs_datetime.");
		}
		
		if(collectionDateObsDate == null || (source.getValueDatetime() != null && collectionDateObsDate.before(source.getValueDatetime()))) {
			collectionDate = source.getValueDatetime();
			collectionDateObsDate = source.getObsDatetime();
		}
	}
}