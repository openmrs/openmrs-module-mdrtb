package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.openmrs.Concept;

public class ContaminatedTubes implements Comparable<ContaminatedTubes>{

	Date repeatedDecontamination;
	Date growthDate;
	Concept contaminatedResult;
	Concept mtidTest;	
	Integer obsId=0;
	Integer obsGroupId=0;
	boolean voided = false;
	
	public Date getRepeatedDecontamination() {
		return repeatedDecontamination;
	}
	public void setRepeatedDecontamination(Date repeatedDecontamination) {
		this.repeatedDecontamination = repeatedDecontamination;
	}
	public Date getGrowthDate() {
		return growthDate;
	}
	public void setGrowthDate(Date growthDate) {
		this.growthDate = growthDate;
	}
	public Concept getContaminatedResult() {
		return contaminatedResult;
	}
	public void setContaminatedResult(Concept contaminatedResult) {
		this.contaminatedResult = contaminatedResult;
	}
	public Concept getMtidTest() {
		return mtidTest;
	}
	public void setMtidTest(Concept mtidTest) {
		this.mtidTest = mtidTest;
	}
	public Integer getObsId() {
		return obsId;
	}
	public void setObsId(Integer obsId) {
		this.obsId = obsId;
	}
	public Integer getObsGroupId() {
		return obsGroupId;
	}
	public void setObsGroupId(Integer obsGroupId) {
		this.obsGroupId = obsGroupId;
	}
	public boolean isVoided() {
		return voided;
	}
	public void setVoided(boolean voided) {
		this.voided = voided;
	}
	public int compareTo(ContaminatedTubes ct) {
		if(ct.getGrowthDate()==null && this.getGrowthDate()==null)
			return 0;
		else if(ct.getGrowthDate()==null)
			return 1;
		else if(this.getGrowthDate()==null)
			return -1;
		return this.getGrowthDate().compareTo(ct.getGrowthDate());
	}
		
}
