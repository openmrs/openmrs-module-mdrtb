package org.openmrs.module.labmodule.specimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

/**
 * An implementaton of a MdrtbCulture.  This wraps an ObsGroup and provides access to culture
 * data within the obsgroup.
 */
public class CultureImpl extends BacteriologyImpl implements Culture {

	public CultureImpl() {
	}
	
	// set up a culture object, given an existing obs
	public CultureImpl(Obs culture) {
		
		if(culture == null || !(culture.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize culture: invalid obs used for initialization.");
		}
		
		test = culture;
	}
	
	// create a new culture object, given an existing patient
	public CultureImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "culture";
	}
	
    public Integer getColonies() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.COLONIES), test);
    	
    	if (obs == null || obs.getValueNumeric() == null) {
    		return null;
    	}
    	else {
    		return (obs.getValueNumeric()).intValue();
    	}
    }
    
    public String getComments() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
    
    public Integer getDaysToPositivity() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DAYS_TO_POSITIVITY), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
    	}
    }
    
    public Concept getMethod() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_METHOD), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public Concept getOrganismType() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public String getOrganismTypeNonCoded() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueText();
    	}
    }
    
    public void setColonies(Integer colonies) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.COLONIES), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && colonies == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(colonies == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.COLONIES), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(colonies.doubleValue());
    }   

    public void setComments(String comments) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
    
    public void setDaysToPositivity(Integer daysToPositivity) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DAYS_TO_POSITIVITY), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && daysToPositivity == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(daysToPositivity == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DAYS_TO_POSITIVITY), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(daysToPositivity.doubleValue());
    }   
    
    public void setMethod(Concept method) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_METHOD), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && method == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(method == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_METHOD), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(method);
    }
    
    public void setOrganismType(Concept organismType) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(organismType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(organismType);
    }
    
    public void setOrganismTypeNonCoded(String organismType) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(organismType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM_NON_CODED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueText(organismType);
    }

	@Override
	public Date getInoculationDate() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_INOCULATION_DATE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
	}

	@Override
	public void setInoculationDate(Date inoculationDate) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_INOCULATION_DATE), test);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && inoculationDate == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(inoculationDate)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(inoculationDate != null) {
				obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_INOCULATION_DATE), test.getObsDatetime(), test.getLocation());
				obs.setValueDatetime(inoculationDate);
				obs.setEncounter(test.getEncounter());
				test.addGroupMember(obs);
			}
		
		}
	}

	@Override
	public Concept getMgitResult() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setMgitResult(Concept mgit) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mgit == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(mgit == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(mgit);		
	}

	@Override
	public Concept getMtIdTest() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setMtIdTest(Concept mtId) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mtId == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(mtId == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(mtId);	
	}

	@Override
	public Concept getTypeOfCulture() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_TYPE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setTypeOfCulture(Concept cultureType) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_TYPE), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cultureType == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(cultureType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_TYPE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(cultureType);		
	}

	@Override
	public Date getCultureResultReportingDate() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT_REPORTING_DATE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
	}

	@Override
	public void setCultureResultReportingDate(Date reportingDate) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT_REPORTING_DATE), test);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && reportingDate == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(reportingDate)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(reportingDate != null) {
				obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT_REPORTING_DATE), test.getObsDatetime(), test.getLocation());
				obs.setValueDatetime(reportingDate);
				obs.setEncounter(test.getEncounter());
				test.addGroupMember(obs);
			}
		
		}
	}

	@Override
	public Integer getLabNo() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.LABORATORY_NO), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
    	}
	}

	@Override
	public void setLabNo(Integer labNo) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.LABORATORY_NO), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && labNo == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(labNo == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.LABORATORY_NO), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(labNo.doubleValue());
		
	}

	@Override
	public Concept getPlaceOfCulture() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_CULTURE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setPlaceOfCulture(Concept place) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_CULTURE), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && place == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(place == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_CULTURE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(place);		
		
	}

	@Override
	public List<MgitCulture> getMgitCultures() {
		//These are constructs for tests ie Mgit1, Mgit2, MgitN
		Concept mgitConstruct = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_RESULT_TEMPLATE);
		Concept mgitResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		Concept mtidTest = Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST);
		Concept growthDate = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_MGIT_GROWTH);
		
		List<Obs> obs  = TbUtil.getSameObsFromObsGroup(mgitConstruct, test);
	    
		List<MgitCulture> mgits = new ArrayList<MgitCulture>();	
	    	
		if (obs == null) {
	    		return mgits;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				MgitCulture currentMgit;
				currentMgit = new MgitCulture();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(mgitResult.getConceptId())) 
					{
						currentMgit.setMgitResult(currentOb.getValueCoded());
					} 
					else if (currentOb.getConcept().getConceptId().equals(mtidTest.getConceptId())) 
					{
						currentMgit.setMtidTest(currentOb.getValueCoded());
					} 
					else if (currentOb.getConcept().getConceptId().equals(growthDate.getConceptId())) 
					{
						currentMgit.setMgitGrowthDate(currentOb.getValueDatetime());
					}
					if(currentMgit.getObsId() < 1)
					{
						currentMgit .setObsId(currentOb.getObsId());
					}
					if(currentMgit.getObsGroupId() < 1)
					{
						currentMgit .setObsGroupId(currentOb.getObsGroup().getObsId());
					}
				}
				
				mgits.add(currentMgit);
			}	    		
    	}	    	
		return mgits;
	}

	@Override
	public void setMgitCultures(List<MgitCulture> mgits) {
		
		Concept mgitConstruct = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_RESULT_TEMPLATE);
		Concept mgitResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		Concept mtidTest = Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST);
		Concept growthDate = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_MGIT_GROWTH);

		for(MgitCulture mgit : mgits)
		{			
			boolean obsExists=false;
			if (mgit == null) {
				continue;
			}
			
			Obs parentMgit = null;
			// if this obs have not been created, and there is no data to add, do nothing
			if(mgit.getObsId() >0 && mgit.getObsGroupId() >0)
			{
				parentMgit = TbUtil.getObsFromObsGroup(mgit.getObsGroupId(), test);
				
				if(parentMgit!=null)
					obsExists=true;
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(mgit.isVoided()) {
				if(parentMgit != null)
				{
					parentMgit.setVoided(true);
					for(Obs o : parentMgit.getGroupMembers())
					{
						o.setVoided(true);
						o.setVoidReason("omar voided this");
					}
					parentMgit.setVoidReason("omar voided this");
					continue;					
				}
			}
			
			Obs obsToAddOrEdit;
			if(obsExists)
			{
				obsToAddOrEdit = parentMgit;
			}
			else
			{
				obsToAddOrEdit = new Obs(test.getPerson(), mgitConstruct,test.getObsDatetime(), test.getLocation());
			}
			
			obsToAddOrEdit.setEncounter(test.getEncounter());
			
			// Fill values for the Concept Set
			Obs setObs;
			if (mgit.getMgitResult() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(mgitResult, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), mgitResult,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getMgitResult());
			}
			if (mgit.getMtidTest() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(mtidTest, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), mtidTest,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getMtidTest());
			}
			if (mgit.getMgitGrowthDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(growthDate, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), growthDate,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(mgit.getMgitGrowthDate());
			}
			test.addGroupMember(obsToAddOrEdit);						
		}		
	}
	
	public void voidSubculture(Obs parent, Obs child)
	{
		TbUtil.voidObsInEncounter(child, test.getEncounter(), "Voided because deleted from Lab module");
	}

	@Override
	public List<LjCulture> getLjCultures() {
		
		//These are constructs for tests ie Lj1, Lj2, LjN
		Concept ljConstruct = Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT_TEMPLATE);
		Concept ljResult = Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT);
		Concept mtidTest = Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST);
		Concept growthDate = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_LJ_GROWTH);
		
		List<Obs> obs  = TbUtil.getSameObsFromObsGroup(ljConstruct, test);
	    
		List<LjCulture> ljs = new ArrayList<LjCulture>();	
	    	
		if (obs == null) {
	    		return ljs;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				LjCulture currentLj;
				currentLj = new LjCulture();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(ljResult.getConceptId())) 
					{
						currentLj.setLjResult(currentOb.getValueCoded());
					} 
					else if (currentOb.getConcept().getConceptId().equals(mtidTest.getConceptId())) 
					{
						currentLj.setMtIdTest(currentOb.getValueCoded());
					} 
					else if (currentOb.getConcept().getConceptId().equals(growthDate.getConceptId())) 
					{
						currentLj.setLjGrowthDate(currentOb.getValueDatetime());
					}
					if(currentLj.getObsId() < 1)
					{
						currentLj .setObsId(currentOb.getObsId());
					}
					if(currentLj.getObsGroupId() < 1)
					{
						currentLj .setObsGroupId(currentOb.getObsGroup().getObsId());
					}
				}
				
				ljs.add(currentLj);
			}	    		
    	}	    	
		return ljs;
	}

	@Override
	public void setLjCultures(List<LjCulture> ljs) {
		for(LjCulture currentLj : ljs)
		{			
			if (currentLj == null) {
				continue;
			}
			
			Obs parentLj = null;
			// if this obs have not been created, and there is no data to add, do nothing
			if(currentLj.getObsId() >0 && currentLj.getObsGroupId() >0)
			{
				parentLj = TbUtil.getObsFromObsGroup(currentLj.getObsGroupId(), test);				
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(currentLj.isVoided()) {
				if(parentLj != null)
				{
					parentLj.setVoided(true);
					for(Obs o : parentLj.getGroupMembers())
					{
						o.setVoided(true);
						o.setVoidReason("omar voided this");
					}
					parentLj.setVoidReason("omar voided this");
					continue;					
				}
			}
			
			if (parentLj == null) {
				Obs newObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT_TEMPLATE),test.getObsDatetime(), test.getLocation());
				newObs.setEncounter(test.getEncounter());
				
				// Fill values for the Concept Set
				Obs setObs;
				if (currentLj.getLjResult() != null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueCoded(currentLj.getLjResult());
				}
				if (currentLj.getMtIdTest()!= null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueCoded(currentLj.getMtIdTest());
				}
				if (currentLj.getLjGrowthDate() != null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_LJ_GROWTH),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueDatetime(currentLj.getLjGrowthDate());
				}
				test.addGroupMember(newObs);
			}			
		}		
		
	}

	@Override
	public List<ContaminatedTubes> getContaminatedTubes() {
		//These are constructs for tests ie Test1, Test2, TestN
		Concept contaminationConstruct = Context.getService(TbService.class).getConcept(TbConcepts.CONTAMINATED_TUBES_RESULT_TEMPLATE);
		Concept cultureResult = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT_2nd_DECONTAMINATION);
		Concept mtidTest = Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST);
		Concept contaminationDate = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_REPEATED_DECONTAMINATION);
		Concept growthDate = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_GROWTH_2nd_DECONTAMINATION);
		
		List<Obs> obs  = TbUtil.getSameObsFromObsGroup(contaminationConstruct, test);
	    
		List<ContaminatedTubes> tubes = new ArrayList<ContaminatedTubes>();	
	    	
		if (obs == null) {
	    		return tubes;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				ContaminatedTubes currentTube;
				currentTube = new ContaminatedTubes();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(cultureResult.getConceptId())) 
					{
						currentTube.setContaminatedResult(currentOb.getValueCoded());
					} 
					else if (currentOb.getConcept().getConceptId().equals(mtidTest.getConceptId())) 
					{
						currentTube.setMtidTest(currentOb.getValueCoded());
					} 
					else if (currentOb.getConcept().getConceptId().equals(growthDate.getConceptId())) 
					{
						currentTube.setGrowthDate(currentOb.getValueDatetime());
					}
					else if (currentOb.getConcept().getConceptId().equals(contaminationDate.getConceptId())) 
					{
						currentTube.setRepeatedDecontamination(currentOb.getValueDatetime());
					}
					if(currentTube.getObsId() < 1)
					{
						currentTube .setObsId(currentOb.getObsId());
					}
					if(currentTube.getObsGroupId() < 1)
					{
						currentTube .setObsGroupId(currentOb.getObsGroup().getObsId());
					}
				}
				
				tubes.add(currentTube);
			}	    		
    	}	    	
		return tubes;
	}

	@Override
	public void setContaminatedTubes(List<ContaminatedTubes> tubes) {
		for(ContaminatedTubes currentTube : tubes)
		{			
			if (currentTube == null) {
				continue;
			}
			
			Obs parentTube = null;
			// if this obs have not been created, and there is no data to add, do nothing
			if(currentTube.getObsId() >0 && currentTube.getObsGroupId() >0)
			{
				parentTube = TbUtil.getObsFromObsGroup(currentTube.getObsGroupId(), test);				
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(currentTube.isVoided()) {
				if(parentTube != null)
				{
					parentTube.setVoided(true);
					for(Obs o : parentTube.getGroupMembers())
					{
						o.setVoided(true);
						o.setVoidReason("omar voided this");
					}
					parentTube.setVoidReason("omar voided this");
					continue;					
				}
			}
			
			if (parentTube == null) {
				Obs newObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CONTAMINATED_TUBES_RESULT_TEMPLATE),test.getObsDatetime(), test.getLocation());
				newObs.setEncounter(test.getEncounter());
				
				// Fill values for the Concept Set
				Obs setObs;
				if (currentTube.getContaminatedResult() != null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT_2nd_DECONTAMINATION),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueCoded(currentTube.getContaminatedResult());
				}
				if (currentTube.getMtidTest() != null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueCoded(currentTube.getMtidTest());
				}
				if (currentTube.getGrowthDate() != null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_GROWTH_2nd_DECONTAMINATION),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueDatetime(currentTube.getGrowthDate());
				}
				if (currentTube.getRepeatedDecontamination() != null) {
					setObs = new Obs(test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_REPEATED_DECONTAMINATION),test.getObsDatetime(), test.getLocation());
					setObs.setEncounter(test.getEncounter());
					newObs.addGroupMember(setObs);
					setObs.setValueDatetime(currentTube.getRepeatedDecontamination());
				}
				test.addGroupMember(newObs);
			}			
		}		
	}
	
	public int compareTo(Culture mc) {
		if(mc.getCultureResultReportingDate()==null && this.getCultureResultReportingDate()==null)
			return 0;
		else if(mc.getCultureResultReportingDate()==null)
			return 1;
		else if(this.getCultureResultReportingDate()==null)
			return -1;
		return this.getCultureResultReportingDate().compareTo(mc.getCultureResultReportingDate());
	}
/*
	public Integer getDistrictId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDistrictId(Integer districtId) {
		// TODO Auto-generated method stub
		
	}

	public Integer getOblastId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOblastId(Integer OblastId) {
		// TODO Auto-generated method stub
		
	}*/
}
