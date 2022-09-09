package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.openmrs.Concept;

public class Dst1Lj implements Comparable<Dst1Lj> {
	Date inoculationDate;
	Date readingDate;
	
	Concept resistanceR;
	Concept resistanceH;
	Concept resistanceS;
	Concept resistanceE;
	Concept resistanceZ;
	Concept resistanceLfx;
	
	public Concept getResistanceLfx() {
		return resistanceLfx;
	}
	public void setResistanceLfx(Concept resistanceLfx) {
		this.resistanceLfx = resistanceLfx;
	}

	Integer obsId=0;
	Integer obsGroupId=0;
	boolean voided = false;
	
	public Date getInoculationDate() {
		return inoculationDate;
	}
	public void setInoculationDate(Date inoculationDate) {
		this.inoculationDate = inoculationDate;
	}
	public Date getReadingDate() {
		return readingDate;
	}
	public void setReadingDate(Date readingDate) {
		this.readingDate = readingDate;
	}
	public Concept getResistanceR() {
		return resistanceR;
	}
	public void setResistanceR(Concept resistanceR) {
		this.resistanceR = resistanceR;
	}
	public Concept getResistanceH() {
		return resistanceH;
	}
	public void setResistanceH(Concept resistanceH) {
		this.resistanceH = resistanceH;
	}
	public Concept getResistanceS() {
		return resistanceS;
	}
	public void setResistanceS(Concept resistanceS) {
		this.resistanceS = resistanceS;
	}
	public Concept getResistanceE() {
		return resistanceE;
	}
	public void setResistanceE(Concept resistanceE) {
		this.resistanceE = resistanceE;
	}
	public Concept getResistanceZ() {
		return resistanceZ;
	}
	public void setResistanceZ(Concept resistanceZ) {
		this.resistanceZ = resistanceZ;
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

	public int compareTo(Dst1Lj dm) {
		if(dm.getInoculationDate()==null && this.getInoculationDate()==null)
			return 0;
		else if(dm.getInoculationDate()==null)
			return 1;
		else if(this.getInoculationDate()==null)
			return -1;
		return this.getInoculationDate().compareTo(dm.getInoculationDate());
	}
	
}
