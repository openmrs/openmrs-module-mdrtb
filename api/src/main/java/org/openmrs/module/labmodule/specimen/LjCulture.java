package org.openmrs.module.labmodule.specimen;

import java.util.Date;
import org.openmrs.Concept;

public class LjCulture implements Comparable<LjCulture>{

	Date ljGrowthDate;
	Concept ljResult;
	Concept mtIdTest;
	Integer obsId=0;
	Integer obsGroupId=0;
	boolean voided = false;
	
	public Date getLjGrowthDate() {
		return ljGrowthDate;
	}
	public void setLjGrowthDate(Date ljGrowthDate) {
		this.ljGrowthDate = ljGrowthDate;
	}
	public Concept getLjResult() {
		return ljResult;
	}
	public void setLjResult(Concept ljResult) {
		this.ljResult = ljResult;
	}
	public Concept getMtIdTest() {
		return mtIdTest;
	}
	public void setMtIdTest(Concept mtIdTest) {
		this.mtIdTest = mtIdTest;
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
	public int compareTo(LjCulture lj) {
		if(lj.getLjGrowthDate()==null && this.getLjGrowthDate()==null)
			return 0;
		else if(lj.getLjGrowthDate()==null)
			return 1;
		else if(this.getLjGrowthDate()==null)
			return -1;
		return this.getLjGrowthDate().compareTo(lj.getLjGrowthDate());
	}
	
	
}
