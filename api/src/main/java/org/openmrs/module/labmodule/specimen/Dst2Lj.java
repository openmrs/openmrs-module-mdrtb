package org.openmrs.module.labmodule.specimen;

import java.util.Date;
import org.openmrs.Concept;

public class Dst2Lj implements Comparable<Dst2Lj> {

	Date inoculationDate;
	Date readingDate;
	
	Concept resistanceOfx;
	Concept resistanceMox;
	Concept resistanceLfx;
	Concept resistancePto;
	Concept resistanceLzd;
	Concept resistanceCfz;
	Concept resistanceBdq;
	Concept resistanceDlm;
	Concept resistancePas;
	Concept resistanceCm;
	Concept resistanceKm;
	Concept resistanceAm;

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
	public Concept getResistanceOfx() {
		return resistanceOfx;
	}
	public void setResistanceOfx(Concept resistanceOfx) {
		this.resistanceOfx = resistanceOfx;
	}
	public Concept getResistanceMox() {
		return resistanceMox;
	}
	public void setResistanceMox(Concept resistanceMox) {
		this.resistanceMox = resistanceMox;
	}
	public Concept getResistanceLfx() {
		return resistanceLfx;
	}
	public void setResistanceLfx(Concept resistanceLfx) {
		this.resistanceLfx = resistanceLfx;
	}
	public Concept getResistancePto() {
		return resistancePto;
	}
	public void setResistancePto(Concept resistancePto) {
		this.resistancePto = resistancePto;
	}
	public Concept getResistanceLzd() {
		return resistanceLzd;
	}
	public void setResistanceLzd(Concept resistanceLzd) {
		this.resistanceLzd = resistanceLzd;
	}
	public Concept getResistanceCfz() {
		return resistanceCfz;
	}
	public void setResistanceCfz(Concept resistanceCfz) {
		this.resistanceCfz = resistanceCfz;
	}
	public Concept getResistanceBdq() {
		return resistanceBdq;
	}
	public void setResistanceBdq(Concept resistanceBdq) {
		this.resistanceBdq = resistanceBdq;
	}
	public Concept getResistanceDlm() {
		return resistanceDlm;
	}
	public void setResistanceDlm(Concept resistanceDlm) {
		this.resistanceDlm = resistanceDlm;
	}
	public Concept getResistancePas() {
		return resistancePas;
	}
	public void setResistancePas(Concept resistancePas) {
		this.resistancePas = resistancePas;
	}
	public Concept getResistanceCm() {
		return resistanceCm;
	}
	public void setResistanceCm(Concept resistanceCm) {
		this.resistanceCm = resistanceCm;
	}
	public Concept getResistanceKm() {
		return resistanceKm;
	}
	public void setResistanceKm(Concept resistanceKm) {
		this.resistanceKm = resistanceKm;
	}
	public Concept getResistanceAm() {
		return resistanceAm;
	}
	public void setResistanceAm(Concept resistanceAm) {
		this.resistanceAm = resistanceAm;
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
	
	public int compareTo(Dst2Lj dm) {
		if(dm.getInoculationDate()==null && this.getInoculationDate()==null)
			return 0;
		else if(dm.getInoculationDate()==null)
			return 1;
		else if(this.getInoculationDate()==null)
			return -1;
		return this.getInoculationDate().compareTo(dm.getInoculationDate());
	}
	
}
