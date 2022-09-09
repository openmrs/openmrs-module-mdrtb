package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.openmrs.Concept;

public class MgitCulture implements Comparable<MgitCulture> {
	
	Date mgitGrowthDate;
	Concept mgitResult;
	Concept mtidTest;
	Integer obsId=0;
	Integer obsGroupId=0;
	boolean voided = false;
	
	public Date getMgitGrowthDate() {
		return mgitGrowthDate;
	}
	public void setMgitGrowthDate(Date mgitGrowthDate) {
		this.mgitGrowthDate = mgitGrowthDate;
	}
	public Concept getMgitResult() {
		return mgitResult;
	}
	public void setMgitResult(Concept mgitResult) {
		this.mgitResult = mgitResult;
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
	
	public int compareTo(MgitCulture mc) {
		if(mc.getMgitGrowthDate()==null && this.getMgitGrowthDate()==null)
			return 0;
		else if(mc.getMgitGrowthDate()==null)
			return 1;
		else if(this.getMgitGrowthDate()==null)
			return -1;
		return this.getMgitGrowthDate().compareTo(mc.getMgitGrowthDate());
	}
	
		
}
