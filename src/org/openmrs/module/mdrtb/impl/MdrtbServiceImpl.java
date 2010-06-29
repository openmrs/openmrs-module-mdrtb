package org.openmrs.module.mdrtb.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.db.MdrtbDAO;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.specimen.MdrtbCulture;
import org.openmrs.module.mdrtb.specimen.MdrtbCultureImpl;
import org.openmrs.module.mdrtb.specimen.MdrtbDst;
import org.openmrs.module.mdrtb.specimen.MdrtbDstImpl;
import org.openmrs.module.mdrtb.specimen.MdrtbSmear;
import org.openmrs.module.mdrtb.specimen.MdrtbSmearImpl;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimen;
import org.openmrs.module.mdrtb.specimen.MdrtbSpecimenImpl;
import org.openmrs.module.mdrtb.specimen.MdrtbTest;

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
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		
		// the id must refer to a valid obs, which is a smear, culture, or dst construct
		if (obs == null || !(obs.getConcept().equals(mdrtbFactory.getConceptSmearParent()) || obs.getConcept().equals(mdrtbFactory.getConceptCultureParent()) || obs.getConcept().equals(mdrtbFactory.getConceptDSTParent())) ) {
			throw new APIException ("Unable to delete specimen test: invalid test id " + testId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
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

	public MdrtbSmear getSmear(Integer obsId) {
		return getSmear(Context.getObsService().getObs(obsId));
	}
	
	public void saveSmear(MdrtbSmear smear) {
		if (smear == null) {
			log.warn("Unable to save smear: smear object is null");
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(smear.getTest() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	// TODO: get rid of this if I end up not using it!
	public void updateSmear(Integer smearId, MdrtbSmear smearUpdate) {
		MdrtbSmear smear;
		
		// first, get the smear that we are looking to update
		try {
			smear = getSmear(Context.getObsService().getObs(smearId));
		}
		catch (Exception e) {
			throw new APIException("Unable to get smear to update", e);
		}
		
		if (smear == null) {
			throw new APIException("Unable to get smear to update");
		}
			
		// perform the update and save the smear
		// TODO: explain better what an "update" means?
		updateSmearHelper(smear, smearUpdate);
		saveSmear(smear);
	}
	
	public MdrtbCulture createCulture(Encounter encounter) {		
		// first, get the specimen
		MdrtbSpecimen specimen = getSpecimen(encounter);
		
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addCulture();
	}
	
	public MdrtbCulture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new MdrtbCultureImpl(obs);
	}

	public MdrtbCulture getCulture(Integer obsId) {
		return getCulture(Context.getObsService().getObs(obsId));
	}
	
	public void saveCulture(MdrtbCulture culture) {
		if (culture == null) {
			log.warn("Unable to save culture: culture object is null");
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(culture.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) culture.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public MdrtbDst createDst(Encounter encounter) {		
		// first, get the specimen
		MdrtbSpecimen specimen = getSpecimen(encounter);
		
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst();
	}
	
	public MdrtbDst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new MdrtbDstImpl(obs);
	}

	public MdrtbDst getDst(Integer obsId) {
		return getDst(Context.getObsService().getObs(obsId));
	}
	
	public void saveDst(MdrtbDst dst) {
		if (dst == null) {
			log.warn("Unable to save dst: dst object is null");
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(dst.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
		
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return mdrtbFactory.getConceptSmearResult().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return mdrtbFactory.getConceptSmearMicroscopyMethod().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return mdrtbFactory.getConceptCultureResult().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return mdrtbFactory.getConceptCultureMethod().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return mdrtbFactory.getConceptDSTMethod().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return mdrtbFactory.getConceptTypeOfOrganism().getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return mdrtbFactory.getConceptSampleSource().getAnswers();
	}
	
	public Concept getConceptScanty() {
		return mdrtbFactory.getConceptScanty();
	}
	
	/**
	 * Utility functions
	 */
	
	// TODO: get rid of these if I end up not using them
	
	private void updateTestHelper(MdrtbTest oldTest, MdrtbTest newTest) {
		// update all the test values that are "settable"
		oldTest.setDateOrdered(newTest.getDateOrdered());
		oldTest.setDateReceived(newTest.getDateReceived());
		oldTest.setLab(newTest.getLab());
		oldTest.setResultDate(newTest.getResultDate());
		
	}
	
	private void updateSmearHelper(MdrtbSmear oldSmear, MdrtbSmear newSmear){
		// first, update all the common test parameters
		updateTestHelper(oldSmear, newSmear);
		
		// now, update everything else
		oldSmear.setResult(newSmear.getResult());
		oldSmear.setBacilli(newSmear.getBacilli());
		oldSmear.setMethod(newSmear.getMethod());		
	}
}
