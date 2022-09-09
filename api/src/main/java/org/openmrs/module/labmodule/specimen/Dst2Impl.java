package org.openmrs.module.labmodule.specimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

public class Dst2Impl extends BacteriologyImpl implements Dst2{
	
	//Constructs
	Concept dst2Construct = Context.getService(TbService.class).getConcept(TbConcepts.DST2_CONSTRUCT);	
	Concept dst2MgitConstruct = Context.getService(TbService.class).getConcept(TbConcepts.DST2_MGIT_CONSTRUCT);
	Concept dst2LjConstruct = Context.getService(TbService.class).getConcept(TbConcepts.DST2_LJ_CONSTRUCT);
	
	//Dates
	Concept dst2ReadingDate= Context.getService(TbService.class).getConcept(TbConcepts.DATE_READING_DST2);
	Concept dst2ReportingDate= Context.getService(TbService.class).getConcept(TbConcepts.DATE_REPORTING_DST2);
	Concept dst2InoculationDate= Context.getService(TbService.class).getConcept(TbConcepts.DATE_INOCULATION_DST2);
	
	//Resistances
	Concept resistanceOfx = Context.getService(TbService.class).getConcept(TbConcepts.OFX_R);
	Concept resistanceMox = Context.getService(TbService.class).getConcept(TbConcepts.MOX_R);
	Concept resistanceLfx = Context.getService(TbService.class).getConcept(TbConcepts.LFX_R);
	Concept resistancePto = Context.getService(TbService.class).getConcept(TbConcepts.PTO_R);
	Concept resistanceLzd = Context.getService(TbService.class).getConcept(TbConcepts.LZD_R);
	Concept resistanceCfz = Context.getService(TbService.class).getConcept(TbConcepts.CFZ_R);
	Concept resistanceBdq = Context.getService(TbService.class).getConcept(TbConcepts.BDQ_R);
	Concept resistanceDlm = Context.getService(TbService.class).getConcept(TbConcepts.DLM_R);
	Concept resistancePas = Context.getService(TbService.class).getConcept(TbConcepts.PAS_R);
	Concept resistanceCm = Context.getService(TbService.class).getConcept(TbConcepts.CM_R);
	Concept resistanceKm = Context.getService(TbService.class).getConcept(TbConcepts.KM_R);
	Concept resistanceAm = Context.getService(TbService.class).getConcept(TbConcepts.AM_R);
	
	// set up a dst object, given an existing obs
	public Dst2Impl(Obs dst2) {
		
		if(dst2 == null || !(dst2.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.DST2_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize dst: invalid obs used for initialization.Obs Id"  + dst2.getId() );
		}
		
		test = dst2;
	}
	
	// create a new culture object, given an existing patient
	public Dst2Impl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.DST2_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public List<Dst2Mgit> getMgitDsts() {
		List<Obs> obs  = TbUtil.getSameObsFromObsGroup(dst2MgitConstruct, test);
	    
		List<Dst2Mgit> mgits = new ArrayList<Dst2Mgit>();	
	    	
		if (obs == null) {
	    		return mgits;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				Dst2Mgit currentMgit;
				currentMgit = new Dst2Mgit();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(dst2ReadingDate.getConceptId())) 
					{
						currentMgit.setReadingDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(dst2InoculationDate.getConceptId())) 
					{
						currentMgit.setInoculationDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(resistanceAm.getConceptId())) 
					{
						currentMgit.setResistanceAm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceBdq.getConceptId())) 
					{
						currentMgit.setResistanceBdq(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceCfz.getConceptId())) 
					{
						currentMgit.setResistanceCfz(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceCm.getConceptId())) 
					{
						currentMgit.setResistanceCm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceDlm.getConceptId())) 
					{
						currentMgit.setResistanceDlm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceKm.getConceptId())) 
					{
						currentMgit.setResistanceKm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceLfx.getConceptId())) 
					{
						currentMgit.setResistanceLfx(currentOb.getValueCoded());
					}
					
					else if (currentOb.getConcept().getConceptId().equals(resistanceLzd.getConceptId())) 
					{
						currentMgit.setResistanceLzd(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceMox.getConceptId())) 
					{
						currentMgit.setResistanceMox(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceOfx.getConceptId())) 
					{
						currentMgit.setResistanceOfx(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistancePas.getConceptId())) 
					{
						currentMgit.setResistancePas(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistancePto.getConceptId())) 
					{
						currentMgit.setResistancePto(currentOb.getValueCoded());
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
	public void setMgitDsts(List<Dst2Mgit> mgitDsts) {
		for(Dst2Mgit mgit : mgitDsts)
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
				obsToAddOrEdit = new Obs(test.getPerson(), dst2MgitConstruct ,test.getObsDatetime(), test.getLocation());
			}
			
			obsToAddOrEdit.setEncounter(test.getEncounter());
			
			// Fill values for the Concept Set
			Obs setObs;
			if (mgit.getInoculationDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(dst2InoculationDate, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), dst2InoculationDate,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(mgit.getInoculationDate());
			}
			if (mgit.getReadingDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(dst2ReadingDate, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), dst2ReadingDate,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(mgit.getReadingDate());
			}
			//Am
			if (mgit.getResistanceAm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceAm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceAm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceAm());
			}
			//Bdq
			if (mgit.getResistanceBdq() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceBdq, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceBdq,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceBdq());
			}
	
			//Cfz	
			if (mgit.getResistanceCfz() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceCfz, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceCfz,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceCfz());
			}
			
			//Cfz	
			if (mgit.getResistanceCm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceCm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceCm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceCm());
			}
			
			//Dlm
			if (mgit.getResistanceDlm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceDlm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceDlm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceDlm());
			}
			
			//Km
			if (mgit.getResistanceKm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceKm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceKm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceKm());
			}

			//Lfx
			if (mgit.getResistanceLfx() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceLfx, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceLfx,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceLfx());
			}
			
			//Lzd
			if (mgit.getResistanceLzd() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceLzd, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceLzd,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceLzd());
			}
			
			//Mox
			if (mgit.getResistanceMox() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceMox, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceMox,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceMox());
			}
			
			//Ofx
			if (mgit.getResistanceOfx()!= null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceOfx, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceOfx,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistanceOfx());
			}

			//Pas
			if (mgit.getResistancePas()!= null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistancePas, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistancePas ,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistancePas());
			}
			
			//Pas
			if (mgit.getResistancePto()!= null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistancePto, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistancePto ,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(mgit.getResistancePto());
			}
			test.addGroupMember(obsToAddOrEdit);						
		}			

	}

	@Override
	public List<Dst2Lj> getLjDsts() {
		List<Obs> obs  = TbUtil.getSameObsFromObsGroup(dst2LjConstruct, test);
	    
		List<Dst2Lj> ljs = new ArrayList<Dst2Lj>();	
	    	
		if (obs == null) {
	    		return ljs;
    	}
    	//get members of the Set / Construct for MGIT
    	else 
    	{ 
    		for (Obs o: obs)
    		{
				Set<Obs> members = o.getGroupMembers();
				Dst2Lj currentLj;
				currentLj = new Dst2Lj();
				
				for (Obs currentOb : members) 
				{
					if (currentOb.getConcept().getConceptId().equals(dst2ReadingDate.getConceptId())) 
					{
						currentLj.setReadingDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(dst2InoculationDate.getConceptId())) 
					{
						currentLj.setInoculationDate(currentOb.getValueDatetime());
					} 
					else if (currentOb.getConcept().getConceptId().equals(resistanceAm.getConceptId())) 
					{
						currentLj.setResistanceAm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceBdq.getConceptId())) 
					{
						currentLj.setResistanceBdq(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceCfz.getConceptId())) 
					{
						currentLj.setResistanceCfz(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceCm.getConceptId())) 
					{
						currentLj.setResistanceCm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceDlm.getConceptId())) 
					{
						currentLj.setResistanceDlm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceKm.getConceptId())) 
					{
						currentLj.setResistanceKm(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceLfx.getConceptId())) 
					{
						currentLj.setResistanceLfx(currentOb.getValueCoded());
					}
					
					else if (currentOb.getConcept().getConceptId().equals(resistanceLzd.getConceptId())) 
					{
						currentLj.setResistanceLzd(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceMox.getConceptId())) 
					{
						currentLj.setResistanceMox(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistanceOfx.getConceptId())) 
					{
						currentLj.setResistanceOfx(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistancePas.getConceptId())) 
					{
						currentLj.setResistancePas(currentOb.getValueCoded());
					}
					else if (currentOb.getConcept().getConceptId().equals(resistancePto.getConceptId())) 
					{
						currentLj.setResistancePto(currentOb.getValueCoded());
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
	public void setLjDsts(List<Dst2Lj> ljDsts) {
		// TODO Auto-generated method stub
		for(Dst2Lj lj : ljDsts)
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
				obsToAddOrEdit = new Obs(test.getPerson(), dst2LjConstruct ,test.getObsDatetime(), test.getLocation());
			}
			
			obsToAddOrEdit.setEncounter(test.getEncounter());
			
			// Fill values for the Concept Set
			Obs setObs;
			if (lj.getInoculationDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(dst2InoculationDate, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), dst2InoculationDate,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(lj.getInoculationDate());
			}
			if (lj.getReadingDate() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(dst2ReadingDate, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), dst2ReadingDate,test.getObsDatetime(), test.getLocation());
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueDatetime(lj.getReadingDate());
			}
			//Am
			if (lj.getResistanceAm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceAm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceAm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceAm());
			}
			//Bdq
			if (lj.getResistanceBdq() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceBdq, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceBdq,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceBdq());
			}
	
			//Cfz	
			if (lj.getResistanceCfz() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceCfz, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceCfz,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceCfz());
			}
			
			//Cfz	
			if (lj.getResistanceCm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceCm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceCm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceCm());
			}
			
			//Dlm
			if (lj.getResistanceDlm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceDlm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceDlm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceDlm());
			}
			
			//Km
			if (lj.getResistanceKm() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceKm, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceKm,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceKm());
			}

			//Lfx
			if (lj.getResistanceLfx() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceLfx, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceLfx,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceLfx());
			}
			
			//Lzd
			if (lj.getResistanceLzd() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceLzd, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceLzd,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceLzd());
			}
			
			//Mox
			if (lj.getResistanceMox() != null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceMox, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceMox,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceMox());
			}
			
			//Ofx
			if (lj.getResistanceOfx()!= null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistanceOfx, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistanceOfx,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistanceOfx());
			}

			//Pas
			if (lj.getResistancePas()!= null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistancePas, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistancePas ,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistancePas());
			}
			
			//Pas
			if (lj.getResistancePto()!= null) {
				if(obsExists)
					setObs = TbUtil.getObsFromObsGroup(resistancePto, obsToAddOrEdit);
				else
					setObs = new Obs(test.getPerson(), resistancePto ,test.getObsDatetime(), test.getLocation());
				
				setObs.setEncounter(test.getEncounter());
				obsToAddOrEdit.addGroupMember(setObs);
				setObs.setValueCoded(lj.getResistancePto());
			}
			test.addGroupMember(obsToAddOrEdit);						
						
		}		
	}

	@Override
	public Concept getResistanceOfx() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceOfx, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceOfx(Concept ofx) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceOfx, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && ofx== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(ofx== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceOfx, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(ofx );
		
	}

	@Override
	public Concept getResistanceMox() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceMox, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}	}

	@Override
	public void setResistanceMox(Concept mox) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceMox, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mox== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(mox== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceMox, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(mox );
		
	}

	@Override
	public Concept getResistanceLfx() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceLfx, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceLfx(Concept lfx) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceLfx, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && lfx== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(lfx== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceLfx, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(lfx );
		
	}

	@Override
	public Concept getResistancePto() {
		Obs obs = TbUtil.getObsFromObsGroup(resistancePto, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}	
    }

	@Override
	public void setResistancePto(Concept pto) {
		Obs obs = TbUtil.getObsFromObsGroup(resistancePto, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && pto== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(pto== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistancePto, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(pto );
		
	}

	@Override
	public Concept getResistanceLzd() {
		 Obs obs = TbUtil.getObsFromObsGroup(resistanceLzd, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceLzd(Concept lzd) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceLzd, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && lzd== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(lzd== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceLzd, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(lzd );
		
	}

	@Override
	public Concept getResistanceCfz() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceCfz, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceCfz(Concept cfz) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceCfz, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cfz== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(cfz== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceCfz, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(cfz );
		
	}

	@Override
	public Concept getResistanceBdq() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceBdq, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceBdq(Concept bdq) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceDlm, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && bdq== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(bdq== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceBdq, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(bdq );
	}

	@Override
	public Concept getResistanceDlm() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceDlm, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceDlm(Concept dlm) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceDlm, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dlm== null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(dlm== null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceDlm, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(dlm );		
	}

	@Override
	public Concept getResistancePas() {
		Obs obs = TbUtil.getObsFromObsGroup(resistancePas, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistancePas(Concept pas) {
		Obs obs = TbUtil.getObsFromObsGroup(resistancePas, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && pas == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(pas  == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistancePas , test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(pas);		
	}

	@Override
	public Concept getResistanceCm() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceCm, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResistanceCm(Concept cm) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceCm, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cm == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(cm  == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceCm , test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(cm );		
					
	}

	@Override
	public Concept getResistanceKm() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceKm, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}	}

	@Override
	public void setResistanceKm(Concept km) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceKm, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && km == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(km == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceKm, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(km);		
		
	}

	@Override
	public Concept getResistanceAm() {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceAm, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}	
    	
	}

	@Override
	public void setResistanceAm(Concept am) {
		Obs obs = TbUtil.getObsFromObsGroup(resistanceAm, test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && am == null) {
			return;
		}

		// if we are trying to set the obs to null, simply void the obs
		if(am  == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), resistanceAm , test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(am );		
			
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
		Obs obs = TbUtil.getObsFromObsGroup(dst2ReportingDate, test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}

	}

	@Override
	public void setReportingDate(Date reportingDate) {
		Obs obs = TbUtil.getObsFromObsGroup(dst2ReportingDate, test);
    	
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
			obs = new Obs (test.getPerson(), dst2ReportingDate, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueDatetime(reportingDate);		
		
	}

	@Override
	public String getTestType() {
		return "dst2";
	}

	@Override
	public String getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComments(String comments) {
		// TODO Auto-generated method stub
		
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
	public Concept getLabLocation() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_DST2), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setLabLocation(Concept lab) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_DST2), test);
    	
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
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.PLACE_OF_DST2), test.getObsDatetime(), test.getLocation());
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

	/*public Integer getDistrictId() {
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
