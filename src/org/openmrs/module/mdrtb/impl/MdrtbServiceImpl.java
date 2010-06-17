package org.openmrs.module.mdrtb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbSmearObj;
import org.openmrs.module.mdrtb.MdrtbSpecimenObj;
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.db.MdrtbDAO;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;

public class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected MdrtbDAO dao;
	
	private static MdrtbFactory mdrtbFactory;
	
	private static List<MdrtbRegimenSuggestion> standardRegimens = new ArrayList<MdrtbRegimenSuggestion>();
	
	private static List<Locale> localeSetUsedInDB = new ArrayList<Locale>();
	
	public void setMdrtbDAO(MdrtbDAO dao) {
		this.dao = dao;
	}
	
	public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws APIException {
		return dao.getOrderExtension(o, includeVoided);
	}
	
	public void purgeOrderException(OrderExtension oe) throws APIException {
		dao.purgeOrderException(oe);
	}
	
	public OrderExtension saveOrderExtension(OrderExtension oe) throws APIException {
		dao.saveOrderExtension(oe);
		return oe;
	}
	
	public OrderExtension voidOrderExtension(OrderExtension oe) throws APIException {
		oe.setVoided(true);
		oe.setVoidReason(" ");
		oe.setVoidedBy(Context.getAuthenticatedUser());
		saveOrderExtension(oe);
		return oe;
	}
	
	public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)
	                                                                                                                    throws APIException {
		return dao.getMdrtbConceptNamesByNameList(nameList, removeDuplicates, loc);
	}
	
	public MdrtbFactory getMdrtbFactory() {
		if (mdrtbFactory == null) {
			this.setMdrtbFactory(MdrtbFactory.getInstance());
		}
		return mdrtbFactory;
	}
	
	public void setMdrtbFactory(MdrtbFactory newMdrtbFactory) {
		MdrtbServiceImpl.mdrtbFactory = newMdrtbFactory;
	}
	
	public List<MdrtbRegimenSuggestion> getStandardRegimens() {
		return standardRegimens;
	}
	
	public void setStandardRegimens(List<MdrtbRegimenSuggestion> standardRegimens) {
		MdrtbServiceImpl.standardRegimens.addAll(standardRegimens);
	}
	
	public List<Locale> getLocaleSetUsedInDB() {
		return localeSetUsedInDB;
	}
	
	public void setLocaleSetUsedInDB(List<Locale> localeSetUsedInDB) {
		MdrtbServiceImpl.localeSetUsedInDB.addAll(localeSetUsedInDB);
	}
	
	public List<Location> getAllMdrtrbLocations(boolean includeRetired) {
		return dao.getAllMdrtrbLocations(includeRetired);
	}
	
	public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales) {
		return dao.getConceptWords(phrase, locales);
	}
	
	public void initializeSpecimenObj(MdrtbSpecimenObj specimen, Patient patient) {
		// if either the specimen or the patient are null, we can't initialize this object
		if (specimen == null || patient == null) {
			log.error("Can't initialize specimen object if specimen or patient is null.");
			return;
		}
		
		// first, create a new Encounter, setting it to the proper patient and encounter type
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		
		// initialize all obs with the proper values
		Obs id = new Obs(patient, mdrtbFactory.getConceptSpecimenID(), null, null);
		id.setEncounter(encounter);
		
		Obs type = new Obs(patient, mdrtbFactory.getConceptSampleSource(), null, null);
		type.setEncounter(encounter);
		
		// add these new objects we have created to the specimen
		specimen.setEncounter(encounter);
		specimen.setId(id);
		specimen.setType(type);
	}
	
	public MdrtbSpecimenObj getSpecimenObj(Integer encounterId) {
		// fetch the encounter
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		
		// return null if there is no encounter, or if the encounter if of the wrong type
		if(encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"))) {
			log.error("Unable to fetch specimen obj: getSpecimenObj called with invalid encounter");
			return null;
		}
		
		// otherwise, create the specimen object
		MdrtbSpecimenObj specimen = new MdrtbSpecimenObj();
		
		// set the encounter
		specimen.setEncounter(encounter);
		
		// now we need to iterate through all the observations and set the proper variables
		for (Obs obs : encounter.getAllObs()) {
			Concept obsConcept = obs.getConcept();
			
			if (obsConcept.equals(mdrtbFactory.getConceptSpecimenID())) {
				specimen.setId(obs);
			}
			else if (obsConcept.equals(mdrtbFactory.getConceptSampleSource())) {
				specimen.setType(obs);
			}
			else if (obsConcept.equals(mdrtbFactory.getConceptSmearParent())) {
				specimen.addSmear(getSmearObj(obs.getId()));
			}
			
			// TODO: add cultures and dsts
		}
		
		return specimen;
	}
	
	public void saveSpecimenObj(MdrtbSpecimenObj specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen obj is null");
			return;
		}
				
		// we need to propagate all the encounter date and location to all the basic underlying obs
		// (note that we don't want the result constructs to have these values, however)
		// TODO: do we want to set obs.person as well?
		specimen.getId().setObsDatetime(specimen.getEncounter().getEncounterDatetime());
		specimen.getId().setLocation(specimen.getEncounter().getLocation());
		specimen.getType().setObsDatetime(specimen.getEncounter().getEncounterDatetime());
		specimen.getType().setLocation(specimen.getEncounter().getLocation());
				
		// now go ahead and save the encounter
		// TODO: do we need to handle voiding obs ourselves?
		Context.getEncounterService().saveEncounter(specimen.getEncounter());
		
		// TODO: need to save the underlying tests?
	}
	
	public void initializeSmearObj(MdrtbSmearObj smear, Encounter encounter) {
		// if either the smear or the encounter are null, we can't initialize the object
		if (smear == null || encounter == null) {
			log.error("Can't initialize smear object if smear or encounter is null.");
		}
		
		// initialize all the obs with the proper values
		Patient patient = encounter.getPatient();
		Obs smearParentObs = new Obs(patient,mdrtbFactory.getConceptSmearParent(),null,null);
		smearParentObs.setEncounter(encounter);
		
		Obs bacilli = new Obs(patient, mdrtbFactory.getConceptBacilli(), null, null);
		bacilli.setEncounter(encounter);
		
		Obs smearDateReceived = new Obs(patient, mdrtbFactory.getConceptDateReceived(),null,null);
		smearDateReceived.setEncounter(encounter);
		
		Obs smearMethod = new Obs(patient, mdrtbFactory.getConceptSmearMicroscopyMethod(), null, null);
		smearMethod.setEncounter(encounter);
		
		Obs smearResult = new Obs(patient, mdrtbFactory.getConceptSmearResult(),null,null);
		smearResult.setEncounter(encounter);
		
		Obs smearResultDate = new Obs(patient, mdrtbFactory.getConceptResultDate(), null, null);
		smearResultDate.setEncounter(encounter);
		
		// now add all the objects to the parent obs
		smearParentObs.addGroupMember(bacilli);
		smearParentObs.addGroupMember(smearDateReceived);
		smearParentObs.addGroupMember(smearMethod);
		smearParentObs.addGroupMember(smearResult);
		smearParentObs.addGroupMember(smearResultDate);
		
		// add these new objects we have created to the smear
		smear.setSmearResultDate(smearResultDate);
		smear.setBacilli(bacilli);
		smear.setSmearDateReceived(smearDateReceived);
		smear.setSmearMethod(smearMethod);
		smear.setSmearParentObs(smearParentObs);
		smear.setSmearResult(smearResult);
	}
	
	public MdrtbSmearObj getSmearObj(Integer obsId) {
		// first, get the obs related to this obsId
		Obs smearConstruct = Context.getObsService().getObs(obsId);
		
		// if this obs isn't of the proper type, return null
		if (smearConstruct == null || smearConstruct.getConcept() == null
		        || !(smearConstruct.getConcept().equals(mdrtbFactory.getConceptSmearParent()))) {
			log.error("Unable to fetch smear obj: getSmearObj called with invalid Obs");
			return null;
		}
		
		// otherwise create the new MdrtbSmearObj
		MdrtbSmearObj smear = new MdrtbSmearObj();
		
		// set the parent to this Obs
		smear.setSmearParentObs(smearConstruct);
		
		// now we need to iterate through all the observations and set the proper variables
		for (Obs obs : smearConstruct.getGroupMembers()) {
			Concept obsConcept = obs.getConcept();
			
			if (obsConcept.equals(mdrtbFactory.getConceptBacilli())) {
				smear.setBacilli(obs);
			} else if (obsConcept.equals(mdrtbFactory.getConceptDateReceived())) {
				smear.setSmearDateReceived(obs);
			} else if (obsConcept.equals(mdrtbFactory.getConceptSmearMicroscopyMethod())) {
				smear.setSmearMethod(obs);
			} else if (obsConcept.equals(mdrtbFactory.getConceptSmearResult())) {
				smear.setSmearResult(obs);
			} else if (obsConcept.equals(mdrtbFactory.getConceptResultDate())) {
				smear.setSmearResultDate(obs);
			} else if (obsConcept.equals(mdrtbFactory.getConceptSampleSource())) {
				smear.setSource(obs);
			}
		}
		return smear;
	}

	public void saveSmearObj(MdrtbSmearObj smear) {
		// first, make sure this object refers to an existing smear obs
		if (smear == null || smear.getSmearParentObs() == null) {
			throw new APIException("Unable to update smear object because no smear or construct obs is present.");
		}
		
		// need to properly set all the obs locations
		Location location = smear.getSmearParentObs().getLocation();
		smear.getBacilli().setLocation(location);
		smear.getSmearDateReceived().setLocation(location);
		smear.getSmearMethod().setLocation(location);
		smear.getSmearResult().setLocation(location);
		smear.getSmearResultDate().setLocation(location);
		
		//if any of the obs don't have a datetime set it to the current time
		if (smear.getBacilli().getObsDatetime() == null) {
			smear.getBacilli().setObsDatetime(new Date());
		}
		if (smear.getSmearDateReceived().getObsDatetime() == null) {
			smear.getSmearDateReceived().setObsDatetime(new Date());
		}
		if (smear.getSmearMethod().getObsDatetime() == null) {
			smear.getSmearMethod().setObsDatetime(new Date());
		}
		if (smear.getSmearResult().getObsDatetime() == null) {
			smear.getSmearResult().setObsDatetime(new Date());
		}
		if (smear.getSmearResultDate().getObsDatetime() == null) {
			smear.getSmearResultDate().setObsDatetime(new Date());
		}
		if (smear.getSmearParentObs().getObsDatetime() == null) {
			smear.getSmearParentObs().setObsDatetime(new Date());
		}
	
		// just save the parent obs, and everything should propogate (?)
		// TODO: does every one get voided and recreated even if it hasn't changed?
		Context.getObsService().saveObs(smear.getSmearParentObs(), "updated via mdr-tb module specimen tracking interface");
	}
	
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return mdrtbFactory.getConceptSmearResult().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return mdrtbFactory.getConceptSmearMicroscopyMethod().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return mdrtbFactory.getConceptSampleSource().getAnswers();
	}
}
