package org.openmrs.module.labmodule.specimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

public class Dst1Impl extends BacteriologyImpl implements Dst1 {

	Concept resistanceR = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_R);
	Concept resistanceH = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_H);
	Concept resistanceS = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_S);
	Concept resistanceE = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_E);
	Concept resistanceZ = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_Z);
	Concept resistanceLfx = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_LFX);
	
	//Dst1 Mgit
	Concept dst1MgitConstruct = Context.getService(TbService.class).getConcept(TbConcepts.DST1_MGIT_CONSTRUCT);
	Concept inoculationDateMgit = Context.getService(TbService.class).getConcept(TbConcepts.DATE_INOCULATION_DST1_MGIT);
	Concept readingDateMgit = Context.getService(TbService.class).getConcept(TbConcepts.DATE_READING_DST1_MGIT);


	//Dst1 l-j
	Concept dstLjConstruct = Context.getService(TbService.class).getConcept(TbConcepts.DST1_LJ_CONSTRUCT);
	Concept inoculationDateLj = Context.getService(TbService.class).getConcept(TbConcepts.DATE_INOCULATION_DST1_LJ);
	Concept readingDateLj = Context.getService(TbService.class).getConcept(TbConcepts.DATE_READING_DST1_LJ);
	
	public Dst1Impl() {
	}
	
	// set up a dst object, given an existing obs
	public Dst1Impl(Obs dst1) {
		
		if(dst1 == null || !(dst1.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.DST1_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize dst: invalid obs used for initialization.Obs Id"  + dst1.getId() );
		}
		
		test = dst1;
	}
	
	// create a new culture object, given an existing patient
	public Dst1Impl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.DST1_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public Integer getLabNo() {
	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.LABORATORY_NO), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return   obs.getValueNumeric().intValue();
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
	public Concept getResistanceR() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_R), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceR(Concept resistanceR) {
	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_R), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resistanceR == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(resistanceR == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_R), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(resistanceR);
	}

	@Override
	public Concept getResistanceH() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_H), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceH(Concept resistanceH) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_H), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resistanceH == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(resistanceH == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_H), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(resistanceH);		
	}

	@Override
	public Concept getResistanceS() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_S), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceS(Concept resistanceS) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_S), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resistanceS == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(resistanceS == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_S), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(resistanceS);		
	}

	@Override
	public Concept getResistanceE() {
	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_E), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceE(Concept resistanceE) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_E), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resistanceE == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(resistanceE == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_E), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(resistanceE);		

	}

	@Override
	public Concept getResistanceZ() {
	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_Z), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}

	}

	@Override
	public void setResistanceZ(Concept resistanceZ) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_Z), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resistanceZ == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(resistanceZ == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_Z), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(resistanceZ);		
	}

	@Override
	public List<Dst1Mgit> getMgitDsts() {
	List<Obs> obs  = TbUtil.getSameObsFromObsGroup(dst1MgitConstruct, test);
	    
		List<Dst1Mgit> mgits = new ArrayList<Dst1Mgit>();	
	    	
		if (obs == null) {
	    		return mgits;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				Dst1Mgit currentMgit;
				currentMgit = new Dst1Mgit();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(readingDateMgit.getConceptId())) 
					{
						currentMgit.setReadingDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(inoculationDateMgit.getConceptId())) 
					{
						currentMgit.setInoculationDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(resistanceE.getConceptId())) 
					{
						currentMgit.setResistanceE(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceH.getConceptId())) 
					{
						currentMgit.setResistanceH(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceR.getConceptId())) 
					{
						currentMgit.setResistanceR(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceS.getConceptId())) 
					{
						currentMgit.setResistanceS(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceZ.getConceptId())) 
					{
						currentMgit.setResistanceZ(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceLfx.getConceptId())) 
					{
						currentMgit.setResistanceLfx(currentOb.getValueCoded());
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
	public void setMgitDsts(List<Dst1Mgit> mgitDsts) {
		for(Dst1Mgit mgit : mgitDsts)
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
				obsToAddOrEdit = new Obs(test.getPerson(), dst1MgitConstruct ,test.getObsDatetime(), test.getLocation());
			}
			
			obsToAddOrEdit.setEncounter(test.getEncounter());
			
			// Fill values for the Concept Set
			Obs setObs;
			if (mgit.getInoculationDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(inoculationDateMgit, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), inoculationDateMgit,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(mgit.getInoculationDate());
			}
			if (mgit.getReadingDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(readingDateMgit, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), readingDateMgit,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(mgit.getReadingDate());
			}
			if (mgit.getResistanceE() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceE, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceE,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceE());
			}
			if (mgit.getResistanceH() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceH, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceH,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceH());
			}
			if (mgit.getResistanceR() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceR, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceR,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceR());
			}
			if (mgit.getResistanceS() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceS, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceS,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceS());
			}
			if (mgit.getResistanceZ() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceZ, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceZ,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceZ());
			}
			if (mgit.getResistanceLfx() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceLfx, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceLfx,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceLfx());
			}
			test.addGroupMember(obsToAddOrEdit);						
		}			
	}

	@Override
	public List<Dst1Lj> getLjDsts() {
		List<Obs> obs  = TbUtil.getSameObsFromObsGroup(dstLjConstruct, test);
	    
		List<Dst1Lj> ljs = new ArrayList<Dst1Lj>();	
	    	
		if (obs == null) {
	    		return ljs;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				Dst1Lj currentLj;
				currentLj = new Dst1Lj();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(readingDateLj.getConceptId())) 
					{
						currentLj.setReadingDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(inoculationDateLj.getConceptId())) 
					{
						currentLj.setInoculationDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(resistanceE.getConceptId())) 
					{
						currentLj.setResistanceE(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceH.getConceptId())) 
					{
						currentLj.setResistanceH(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceR.getConceptId())) 
					{
						currentLj.setResistanceR(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceS.getConceptId())) 
					{
						currentLj.setResistanceS(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceZ.getConceptId())) 
					{
						currentLj.setResistanceZ(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceLfx.getConceptId())) 
					{
						currentLj.setResistanceLfx(currentOb.getValueCoded());
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
	public void setLjDsts(List<Dst1Lj> ljDsts) {
		
		
		for(Dst1Lj lj : ljDsts)
		{			
			boolean obsExists=false;
			if (lj == null) {
				continue;
			}
			
			Obs parentMgit = null;
			// if this obs have not been created, and there is no data to add, do nothing
			if(lj.getObsId() >0 && lj.getObsGroupId() >0)
			{
				parentMgit = TbUtil.getObsFromObsGroup(lj.getObsGroupId(), test);
				
				if(parentMgit!=null)
					obsExists=true;
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(lj.isVoided()) {
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
				obsToAddOrEdit = new Obs(test.getPerson(), dstLjConstruct ,test.getObsDatetime(), test.getLocation());
			}
			
			obsToAddOrEdit.setEncounter(test.getEncounter());
			
			// Fill values for the Concept Set
			Obs setObs;
			if (lj.getInoculationDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(inoculationDateLj, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), inoculationDateLj,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(lj.getInoculationDate());
			}
			if (lj.getReadingDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(readingDateLj, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), readingDateLj,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(lj.getReadingDate());
			}
			if (lj.getResistanceE() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceE, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceE,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceE());
			}
			if (lj.getResistanceH() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceH, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceH,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceH());
			}
			if (lj.getResistanceR() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceR, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceR,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceR());
			}
			if (lj.getResistanceS() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceS, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceS,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceS());
			}
			if (lj.getResistanceZ() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceZ, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceZ,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceZ());
			}
			if (lj.getResistanceLfx() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceLfx, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceLfx,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceLfx());
			}
			test.addGroupMember(obsToAddOrEdit);						
		}		

	}

	@Override
	public Concept getDstType() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_DEST_REPORTED), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}

	}

	@Override
	public void setDstType(Concept dstType) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_DEST_REPORTED), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dstType == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(dstType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_DEST_REPORTED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(dstType);		

	}

	@Override
	public Date getReportingDate() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DATE_DST1_RESULT_REPORTING), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}

	}

	@Override
	public void setReportingDate(Date reportingDate) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DATE_DST1_RESULT_REPORTING), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && reportingDate == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(reportingDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DATE_DST1_RESULT_REPORTING), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueDatetime(reportingDate);		
		
	}

	@Override
	public String getTestType() {
		
		return "dst1";
	}

	@Override
	public String getComments() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST1_CONSTRUCT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
	}

	@Override
	public void setComments(String comments) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST1_CONSTRUCT), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && comments == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(comments == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST1_CONSTRUCT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setComment(comments);		

	}

	@Override
	public Concept getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResult(Concept result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Concept getLabLocation() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_DST1), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setLabLocation(Concept lab) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_DST1), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && lab == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(lab == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_DST1), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(lab);			
	}

	@Override
	public String getLabId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLabId(String labId) {
		// TODO Auto-generated method stub
		
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
		
	}
	*/

	@Override
	public Concept getResistanceLfx() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_LFX), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceLfx(Concept resistanceLfx) {
		
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_LFX), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resistanceLfx == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(resistanceLfx == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_LFX), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(resistanceLfx);	
		
	}
}
