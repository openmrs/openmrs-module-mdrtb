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
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbSmearObj;
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.db.MdrtbDAO;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;

public class MdrtbServiceImpl  extends BaseOpenmrsService implements MdrtbService {

    protected final Log log = LogFactory.getLog(getClass());

    protected MdrtbDAO dao;
    
    private static MdrtbFactory mdrtbFactory;
    private static List<MdrtbRegimenSuggestion> standardRegimens = new ArrayList<MdrtbRegimenSuggestion>();
    private static List<Locale> localeSetUsedInDB = new ArrayList<Locale>();
    
    public void setMdrtbDAO(MdrtbDAO dao) {
        this.dao = dao;
    }
    
 public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws APIException{
     return dao.getOrderExtension(o, includeVoided);
 }
    
    public void purgeOrderException(OrderExtension oe) throws APIException{
            dao.purgeOrderException(oe);
    }
    
    public OrderExtension saveOrderExtension(OrderExtension oe) throws APIException{
        dao.saveOrderExtension(oe);
        return oe;
    }
    
    
    
    public  OrderExtension voidOrderExtension(OrderExtension oe) throws APIException{
        oe.setVoided(true);
        oe.setVoidReason(" ");
        oe.setVoidedBy(Context.getAuthenticatedUser());
        saveOrderExtension(oe);
        return oe;
    }
    
    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws APIException {
        return dao.getMdrtbConceptNamesByNameList(nameList, removeDuplicates, loc);
    }

    public MdrtbFactory getMdrtbFactory() {
        if (mdrtbFactory == null){
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

    public List<Location> getAllMdrtrbLocations(boolean includeRetired){
        return dao.getAllMdrtrbLocations(includeRetired);
    }
    
    public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales){
        return dao.getConceptWords(phrase, locales);
    }

    public MdrtbSmearObj getSmearObj(Integer obsId) {
	    // first, get the obs related to this obsId
    	Obs smearConstruct = Context.getObsService().getObs(obsId);
    	
    	// if this obs isn't of the proper type, return null
    	if (smearConstruct == null || smearConstruct.getConcept() == null || !(smearConstruct.getConcept().equals(mdrtbFactory.getConceptSmearParent()))) {
    		log.error("getSmearObj called with invalid Obs");
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
    		}
    		else if (obsConcept.equals(mdrtbFactory.getConceptDateReceived())) {
    			smear.setSmearDateReceived(obs);
    		}
    		else if (obsConcept.equals(mdrtbFactory.getConceptSmearMicroscopyMethod())) {
    			smear.setSmearMethod(obs);
    		}
    		else if (obsConcept.equals(mdrtbFactory.getConceptSmearResult())) {
    			smear.setSmearResult(obs);
    		}
    		else if (obsConcept.equals(mdrtbFactory.getConceptResultDate())) {
    			smear.setSmearResultDate(obs);
    		}
    		else if (obsConcept.equals(mdrtbFactory.getConceptSampleSource())) {
    			smear.setSource(obs);
    		}
    	}   
    	return smear;
    }
    
    public void updateSmearObj(MdrtbSmearObj smear) {
    	// first, make sure this object refers to an existing smear obs
    	if (smear == null || smear.getSmearParentObs() == null || smear.getSmearParentObs().getId() == null) {
    		throw new APIException("Unable to update smear object because object doesn't have valid id.");
    	}
    	
    	// now we need to get the existing smear object and compare to see if anything has changed
    	MdrtbSmearObj oldSmear = getSmearObj(smear.getSmearParentObs().getId());
    	
    	if (oldSmear == null) {
    		throw new APIException("Unable to update smear object because object doesn't have valid id.");
    	}
    	
    	// do the actual comparisons and updates
    	if(oldSmear.getBacilli().getValueNumeric() != smear.getBacilli().getValueNumeric()) {
    		// we actually set the oldSmear value here to the new value and save it (instead of just saving the new obs) to avoid lazy loading issues
    		oldSmear.getBacilli().setValueNumeric(smear.getBacilli().getValueNumeric());
    		Context.getObsService().saveObs(oldSmear.getBacilli(), "updated via mdr-tb specimen management ui");
    	}
    	if(oldSmear.getSmearDateReceived().getValueDatetime() != smear.getSmearDateReceived().getValueDatetime()) {
    		// we actually set the oldSmear value here to the new value and save it (instead of just saving the new obs) to avoid lazy loading issues
    		oldSmear.getSmearDateReceived().setValueDatetime(smear.getSmearDateReceived().getValueDatetime());
    		Context.getObsService().saveObs(oldSmear.getSmearDateReceived(), "updated via mdr-tb specimen management ui");
    	}
    	
    	// TODO: handle "smear method" once we figure this out
    	
    	if(oldSmear.getSmearResult().getValueCoded().getId() != smear.getSmearResult().getValueCoded().getId()) {
    		// we actually set the oldSmear value here to the new value and save it (instead of just saving the new obs) to avoid lazy loading issues
    		oldSmear.getSmearResult().getValueCoded().setId(smear.getSmearResult().getValueCoded().getId());
    		Context.getObsService().saveObs(oldSmear.getSmearResult(), "updated via mdr-tb specimen management ui");
    	}
    	if(oldSmear.getSmearResultDate().getValueDatetime() != smear.getSmearResultDate().getValueDatetime()) {
    		// we actually set the oldSmear value here to the new value and save it (instead of just saving the new obs) to avoid lazy loading issues
    		oldSmear.getSmearResultDate().setValueDatetime(smear.getSmearResultDate().getValueDatetime());
    		Context.getObsService().saveObs(oldSmear.getSmearResultDate(), "updated via mdr-tb specimen management ui");
    	}
    }
    
    public Collection<ConceptAnswer> getPossibleSmearResults() {
    	return mdrtbFactory.getConceptSmearResult().getAnswers();
    }
    
    // TODO: remove this if we end up not using it
    public Collection<ConceptAnswer> getPossibleSmearMethods() {
    	return mdrtbFactory.getConceptSmearMicroscopyMethod().getAnswers();
    }
}
