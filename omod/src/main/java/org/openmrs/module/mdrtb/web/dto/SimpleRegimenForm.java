package org.openmrs.module.mdrtb.web.dto;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;

public class SimpleRegimenForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -7985268200400994850L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	private Double amDose;
	
	private Double amxDose;
	
	private Double bdqDose;
	
	private Double cfzDose;
	
	private Double cmDose;
	
	private Double csDose;
	
	private Double dlmDose;
	
	private Double eDose;
	
	private Double hDose;
	
	private Double hrDose;
	
	private Double hrzeDose;
	
	private Double impDose;
	
	private Double lfxDose;
	
	private Double lzdDose;
	
	private Double mfxDose;
	
	private Double otherDrug1Dose;
	
	private Double pasDose;
	
	private Double ptoDose;
	
	private Double sDose;
	
	private Double zDose;
	
	private Date councilDate;
	
	private String clinicianNotes;
	
	private String cmacNumber;
	
	private String comment;
	
	private String otherDrug1Name;
	
	private String otherRegimen;
	
	private Concept fundingSource;
	
	private Concept placeOfCommission;
	
	private Concept resistanceType;
	
	private Concept sldRegimen;
	
	public SimpleRegimenForm() {
	}
	
	public SimpleRegimenForm(RegimenForm regimenForm) {
		setEncounter(regimenForm.getEncounter());
		setUuid(getEncounter().getUuid());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(
		    regimenForm.getPatientProgramId());
		setPatientProgramUuid(patientProgram.getUuid());
		setAmDose(regimenForm.getAmDose());
		setAmxDose(regimenForm.getAmxDose());
		setBdqDose(regimenForm.getBdqDose());
		setCfzDose(regimenForm.getCfzDose());
		setClinicianNotes(regimenForm.getClinicianNotes());
		setCmacNumber(regimenForm.getCmacNumber());
		setCmDose(regimenForm.getCmDose());
		setComment(regimenForm.getComments());
		setCouncilDate(regimenForm.getCouncilDate());
		setCsDose(regimenForm.getCsDose());
		setDlmDose(regimenForm.getDlmDose());
		seteDose(regimenForm.getEDose());
		setFundingSource(regimenForm.getFundingSource());
		sethDose(regimenForm.getHDose());
		setHrDose(regimenForm.getHrDose());
		setHrzeDose(regimenForm.getHrzeDose());
		setImpDose(regimenForm.getImpDose());
		setLfxDose(regimenForm.getLfxDose());
		setLzdDose(regimenForm.getLzdDose());
		setMfxDose(regimenForm.getMfxDose());
		setOtherDrug1Name(regimenForm.getOtherDrug1Name());
		setOtherDrug1Dose(regimenForm.getOtherDrug1Dose());
		setOtherRegimen(regimenForm.getOtherRegimen());
		setPasDose(regimenForm.getPasDose());
		setPlaceOfCommission(regimenForm.getPlaceOfCommission());
		setPtoDose(regimenForm.getPtoDose());
		setResistanceType(regimenForm.getResistanceType());
		setsDose(regimenForm.getSDose());
		setSldRegimen(regimenForm.getSldRegimenType());
		setzDose(regimenForm.getZDose());
	}
	
	/**
	 * Provide {@link SimpleRegimenForm} representation of {@link RegimenForm}
	 */
	public RegimenForm toForm() {
		RegimenForm regimenForm = new RegimenForm();
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		regimenForm.setPatientProgramId(patientProgram.getPatientProgramId());
		regimenForm.setEncounter(getEncounter());
		regimenForm.setAmDose(getAmDose());
		regimenForm.setAmxDose(getAmxDose());
		regimenForm.setBdqDose(getBdqDose());
		regimenForm.setCfzDose(getCfzDose());
		regimenForm.setClinicianNotes(getClinicianNotes());
		regimenForm.setCmacNumber(getCmacNumber());
		regimenForm.setCmDose(getCmDose());
		regimenForm.setComments(getComment());
		regimenForm.setCouncilDate(getCouncilDate());
		regimenForm.setCsDose(getCsDose());
		regimenForm.setDlmDose(getDlmDose());
		regimenForm.setEDose(geteDose());
		regimenForm.setFundingSource(getFundingSource());
		regimenForm.setHDose(gethDose());
		regimenForm.setHrDose(getHrDose());
		regimenForm.setHrzeDose(getHrzeDose());
		regimenForm.setImpDose(getImpDose());
		regimenForm.setLfxDose(getLfxDose());
		regimenForm.setLzdDose(getLzdDose());
		regimenForm.setMfxDose(getMfxDose());
		regimenForm.setOtherDrug1Name(getOtherDrug1Name());
		regimenForm.setOtherDrug1Dose(getOtherDrug1Dose());
		regimenForm.setOtherRegimen(getOtherRegimen());
		regimenForm.setPasDose(getPasDose());
		regimenForm.setPlaceOfCommission(getPlaceOfCommission());
		regimenForm.setPtoDose(getPtoDose());
		regimenForm.setResistanceType(getResistanceType());
		regimenForm.setSDose(getsDose());
		regimenForm.setSldRegimenType(getSldRegimen());
		regimenForm.setZDose(getzDose());
		return regimenForm;
	}
	
	@Override
	public Integer getId() {
		return encounter.getEncounterId();
	}
	
	@Override
	public void setId(Integer id) {
		// Nope! Not gonna happen
	}
	
	public Encounter getEncounter() {
		return encounter;
	}
	
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	
	public String getPatientProgramUuid() {
		return patientProgramUuid;
	}
	
	public void setPatientProgramUuid(String patientProgramUuid) {
		this.patientProgramUuid = patientProgramUuid;
	}
	
	public Double getAmDose() {
		return amDose;
	}
	
	public void setAmDose(Double amDose) {
		this.amDose = amDose;
	}
	
	public Double getAmxDose() {
		return amxDose;
	}
	
	public void setAmxDose(Double amxDose) {
		this.amxDose = amxDose;
	}
	
	public Double getBdqDose() {
		return bdqDose;
	}
	
	public void setBdqDose(Double bdqDose) {
		this.bdqDose = bdqDose;
	}
	
	public Double getCfzDose() {
		return cfzDose;
	}
	
	public void setCfzDose(Double cfzDose) {
		this.cfzDose = cfzDose;
	}
	
	public Double getCmDose() {
		return cmDose;
	}
	
	public void setCmDose(Double cmDose) {
		this.cmDose = cmDose;
	}
	
	public Double getCsDose() {
		return csDose;
	}
	
	public void setCsDose(Double csDose) {
		this.csDose = csDose;
	}
	
	public Double getDlmDose() {
		return dlmDose;
	}
	
	public void setDlmDose(Double dlmDose) {
		this.dlmDose = dlmDose;
	}
	
	public Double geteDose() {
		return eDose;
	}
	
	public void seteDose(Double eDose) {
		this.eDose = eDose;
	}
	
	public Double gethDose() {
		return hDose;
	}
	
	public void sethDose(Double hDose) {
		this.hDose = hDose;
	}
	
	public Double getHrDose() {
		return hrDose;
	}
	
	public void setHrDose(Double hrDose) {
		this.hrDose = hrDose;
	}
	
	public Double getHrzeDose() {
		return hrzeDose;
	}
	
	public void setHrzeDose(Double hrzeDose) {
		this.hrzeDose = hrzeDose;
	}
	
	public Double getImpDose() {
		return impDose;
	}
	
	public void setImpDose(Double impDose) {
		this.impDose = impDose;
	}
	
	public Double getLfxDose() {
		return lfxDose;
	}
	
	public void setLfxDose(Double lfxDose) {
		this.lfxDose = lfxDose;
	}
	
	public Double getLzdDose() {
		return lzdDose;
	}
	
	public void setLzdDose(Double lzdDose) {
		this.lzdDose = lzdDose;
	}
	
	public Double getMfxDose() {
		return mfxDose;
	}
	
	public void setMfxDose(Double mfxDose) {
		this.mfxDose = mfxDose;
	}
	
	public Double getOtherDrug1Dose() {
		return otherDrug1Dose;
	}
	
	public void setOtherDrug1Dose(Double otherDrug1Dose) {
		this.otherDrug1Dose = otherDrug1Dose;
	}
	
	public Double getPasDose() {
		return pasDose;
	}
	
	public void setPasDose(Double pasDose) {
		this.pasDose = pasDose;
	}
	
	public Double getPtoDose() {
		return ptoDose;
	}
	
	public void setPtoDose(Double ptoDose) {
		this.ptoDose = ptoDose;
	}
	
	public Double getsDose() {
		return sDose;
	}
	
	public void setsDose(Double sDose) {
		this.sDose = sDose;
	}
	
	public Double getzDose() {
		return zDose;
	}
	
	public void setzDose(Double zDose) {
		this.zDose = zDose;
	}
	
	public Date getCouncilDate() {
		return councilDate;
	}
	
	public void setCouncilDate(Date councilDate) {
		this.councilDate = councilDate;
	}
	
	public String getClinicianNotes() {
		return clinicianNotes;
	}
	
	public void setClinicianNotes(String clinicianNotes) {
		this.clinicianNotes = clinicianNotes;
	}
	
	public String getCmacNumber() {
		return cmacNumber;
	}
	
	public void setCmacNumber(String cmacNumber) {
		this.cmacNumber = cmacNumber;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getOtherDrug1Name() {
		return otherDrug1Name;
	}
	
	public void setOtherDrug1Name(String otherDrug1Name) {
		this.otherDrug1Name = otherDrug1Name;
	}
	
	public String getOtherRegimen() {
		return otherRegimen;
	}
	
	public void setOtherRegimen(String otherRegimen) {
		this.otherRegimen = otherRegimen;
	}
	
	public Concept getFundingSource() {
		return fundingSource;
	}
	
	public void setFundingSource(Concept fundingSource) {
		this.fundingSource = fundingSource;
	}
	
	public Concept getPlaceOfCommission() {
		return placeOfCommission;
	}
	
	public void setPlaceOfCommission(Concept placeOfCommission) {
		this.placeOfCommission = placeOfCommission;
	}
	
	public Concept getResistanceType() {
		return resistanceType;
	}
	
	public void setResistanceType(Concept resistanceType) {
		this.resistanceType = resistanceType;
	}
	
	public Concept getSldRegimen() {
		return sldRegimen;
	}
	
	public void setSldRegimen(Concept sldRegimen) {
		this.sldRegimen = sldRegimen;
	}
}
