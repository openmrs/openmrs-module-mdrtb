package org.openmrs.module.mdrtb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.db.MdrtbDAO;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.openmrs.module.mdrtb.specimen.MdrtbSmearImpl;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimenImpl;

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
	
	public MdrtbSpecimen createSpecimen(Patient patient) {
		// return null if the patient is null
		if(patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new MdrtbSpecimenImpl(patient);
	}
	
	public MdrtbSpecimen getSpecimen(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong type
		if(encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"))) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new MdrtbSpecimenImpl(encounter);
	}
	
	public void saveSpecimen(MdrtbSpecimen specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return;
		}
		
		// make sure getSpecimen returns the right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should an encounter)
		if(!(specimen.getSpecimen() instanceof Encounter)){
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
				
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter((Encounter) specimen.getSpecimen());
	}
	
	public MdrtbSmear createSmear(Encounter encounter) {		
		// first, get the specimen
		MdrtbSpecimen specimen = getSpecimen(encounter);
		
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addSmear();
	}
	
	public MdrtbSmear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new MdrtbSmearImpl(obs);
	}

	public void saveSmear(MdrtbSmear smear) {
		if (smear == null) {
			log.warn("Unable to save smear: smear object is null");
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(smear.getSmear() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getSmear(), "voided by Mdr-tb module specimen tracking UI");
		
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
