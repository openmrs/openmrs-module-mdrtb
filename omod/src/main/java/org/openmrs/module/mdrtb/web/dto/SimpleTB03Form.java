package org.openmrs.module.mdrtb.web.dto;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.TB03Form;

public class SimpleTB03Form extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 2732274573256642676L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	private Integer ageAtTB03Registration;
	
	private String registrationNumber;
	
	private String address;
	
	private String clinicalNotes;
	
	private String nameOfIPFacility;
	
	private String nameOfCPFacility;
	
	private String otherCauseOfDeath;
	
	private Date treatmentStartDate;
	
	private Date artStartDate;
	
	private Date pctStartDate;
	
	private Date xrayDate;
	
	private Date hivTestDate;
	
	private Date treatmentOutcomeDate;
	
	private Date dateOfDeathAfterTreatmentOutcome;
	
	private Concept anatomicalSite;
	
	private Concept treatmentSiteIP;
	
	private Concept treatmentSiteCP;
	
	private Concept patientCategory;
	
	private Concept registrationGroup;
	
	private Concept hivStatus;
	
	private Concept resistanceType;
	
	private Concept causeOfDeath;
	
	private Concept treatmentOutcome;
	
	public SimpleTB03Form() {
	}
	
	public SimpleTB03Form(TB03Form tb03) {
		setEncounter(tb03.getEncounter());
		setUuid(getEncounter().getUuid());
		Integer patientProgramId = tb03.getPatientProgramId();
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		setPatientProgramUuid(patientProgram.getUuid());
		setRegistrationNumber(tb03.getRegistrationNumber());
		setAgeAtTB03Registration(tb03.getAgeAtTB03Registration());
		setAddress(tb03.getAddress());
		setClinicalNotes(tb03.getClinicianNotes());
		setNameOfIPFacility(tb03.getNameOfIPFacility());
		setNameOfCPFacility(tb03.getNameOfCPFacility());
		setOtherCauseOfDeath(tb03.getOtherCauseOfDeath());
		setTreatmentStartDate(tb03.getTreatmentStartDate());
		setArtStartDate(tb03.getArtStartDate());
		setPctStartDate(tb03.getPctStartDate());
		setXrayDate(tb03.getXrayDate());
		setHivTestDate(tb03.getHivTestDate());
		setTreatmentOutcomeDate(tb03.getTreatmentOutcomeDate());
		setDateOfDeathAfterTreatmentOutcome(tb03.getDateOfDeathAfterOutcome());
		setAnatomicalSite(tb03.getAnatomicalSite());
		setTreatmentSiteIP(tb03.getTreatmentSiteIP());
		setTreatmentSiteCP(tb03.getTreatmentSiteCP());
		setPatientCategory(tb03.getPatientCategory());
		setRegistrationGroup(tb03.getRegistrationGroup());
		setHivStatus(tb03.getHivStatus());
		setResistanceType(tb03.getResistanceType());
		setCauseOfDeath(tb03.getCauseOfDeath());
		setTreatmentOutcome(tb03.getTreatmentOutcome());
	}
	
	/**
	 * Provide {@link SimpleTB03Form} representation of {@link TB03Form}
	 */
	public TB03Form toForm() {
		TB03Form tb03 = new TB03Form();
		tb03.setEncounter(getEncounter());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		tb03.setPatientProgramId(patientProgram.getPatientProgramId());
		tb03.setClinicianNotes(getClinicalNotes());
		tb03.setNameOfIPFacility(getNameOfIPFacility());
		tb03.setNameOfCPFacility(getNameOfCPFacility());
		tb03.setOtherCauseOfDeath(getOtherCauseOfDeath());
		tb03.setTreatmentStartDate(getTreatmentStartDate());
		tb03.setArtStartDate(getArtStartDate());
		tb03.setPctStartDate(getPctStartDate());
		tb03.setXrayDate(getXrayDate());
		tb03.setHivTestDate(getHivTestDate());
		tb03.setTreatmentOutcomeDate(getTreatmentOutcomeDate());
		tb03.setDateOfDeathAfterOutcome(getDateOfDeathAfterTreatmentOutcome());
		tb03.setAnatomicalSite(getAnatomicalSite());
		tb03.setTreatmentSiteIP(getTreatmentSiteIP());
		tb03.setTreatmentSiteCP(getTreatmentSiteCP());
		tb03.setPatientCategory(getPatientCategory());
		tb03.setRegistrationGroup(getRegistrationGroup());
		tb03.setHivStatus(getHivStatus());
		tb03.setResistanceType(getResistanceType());
		tb03.setCauseOfDeath(getCauseOfDeath());
		tb03.setTreatmentOutcome(getTreatmentOutcome());
		return tb03;
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
	
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	
	public Integer getAgeAtTB03Registration() {
		return ageAtTB03Registration;
	}
	
	public void setAgeAtTB03Registration(Integer ageAtTB03Registration) {
		this.ageAtTB03Registration = ageAtTB03Registration;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getClinicalNotes() {
		return clinicalNotes;
	}
	
	public void setClinicalNotes(String clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}
	
	public String getNameOfIPFacility() {
		return nameOfIPFacility;
	}
	
	public void setNameOfIPFacility(String nameOfIPFacility) {
		this.nameOfIPFacility = nameOfIPFacility;
	}
	
	public String getNameOfCPFacility() {
		return nameOfCPFacility;
	}
	
	public void setNameOfCPFacility(String nameOfCPFacility) {
		this.nameOfCPFacility = nameOfCPFacility;
	}
	
	public String getOtherCauseOfDeath() {
		return otherCauseOfDeath;
	}
	
	public void setOtherCauseOfDeath(String otherCauseOfDeath) {
		this.otherCauseOfDeath = otherCauseOfDeath;
	}
	
	public Date getTreatmentStartDate() {
		return treatmentStartDate;
	}
	
	public void setTreatmentStartDate(Date treatmentStartDate) {
		this.treatmentStartDate = treatmentStartDate;
	}
	
	public Date getArtStartDate() {
		return artStartDate;
	}
	
	public void setArtStartDate(Date artStartDate) {
		this.artStartDate = artStartDate;
	}
	
	public Date getPctStartDate() {
		return pctStartDate;
	}
	
	public void setPctStartDate(Date pctStartDate) {
		this.pctStartDate = pctStartDate;
	}
	
	public Date getXrayDate() {
		return xrayDate;
	}
	
	public void setXrayDate(Date xrayDate) {
		this.xrayDate = xrayDate;
	}
	
	public Date getHivTestDate() {
		return hivTestDate;
	}
	
	public void setHivTestDate(Date hivTestDate) {
		this.hivTestDate = hivTestDate;
	}
	
	public Date getTreatmentOutcomeDate() {
		return treatmentOutcomeDate;
	}
	
	public void setTreatmentOutcomeDate(Date treatmentOutcomeDate) {
		this.treatmentOutcomeDate = treatmentOutcomeDate;
	}
	
	public Date getDateOfDeathAfterTreatmentOutcome() {
		return dateOfDeathAfterTreatmentOutcome;
	}
	
	public void setDateOfDeathAfterTreatmentOutcome(Date dateOfDeathAfterTreatmentOutcome) {
		this.dateOfDeathAfterTreatmentOutcome = dateOfDeathAfterTreatmentOutcome;
	}
	
	public Concept getAnatomicalSite() {
		return anatomicalSite;
	}
	
	public void setAnatomicalSite(Concept anatomicalSite) {
		this.anatomicalSite = anatomicalSite;
	}
	
	public Concept getTreatmentSiteIP() {
		return treatmentSiteIP;
	}
	
	public void setTreatmentSiteIP(Concept treatmentSiteIP) {
		this.treatmentSiteIP = treatmentSiteIP;
	}
	
	public Concept getTreatmentSiteCP() {
		return treatmentSiteCP;
	}
	
	public void setTreatmentSiteCP(Concept treatmentSiteCP) {
		this.treatmentSiteCP = treatmentSiteCP;
	}
	
	public Concept getPatientCategory() {
		return patientCategory;
	}
	
	public void setPatientCategory(Concept patientCategory) {
		this.patientCategory = patientCategory;
	}
	
	public Concept getRegistrationGroup() {
		return registrationGroup;
	}
	
	public void setRegistrationGroup(Concept registrationGroup) {
		this.registrationGroup = registrationGroup;
	}
	
	public Concept getHivStatus() {
		return hivStatus;
	}
	
	public void setHivStatus(Concept hivStatus) {
		this.hivStatus = hivStatus;
	}
	
	public Concept getResistanceType() {
		return resistanceType;
	}
	
	public void setResistanceType(Concept resistanceType) {
		this.resistanceType = resistanceType;
	}
	
	public Concept getCauseOfDeath() {
		return causeOfDeath;
	}
	
	public void setCauseOfDeath(Concept causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}
	
	public Concept getTreatmentOutcome() {
		return treatmentOutcome;
	}
	
	public void setTreatmentOutcome(Concept treatmentOutcome) {
		this.treatmentOutcome = treatmentOutcome;
	}
}
