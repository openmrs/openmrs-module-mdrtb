package org.openmrs.module.mdrtb.web.dto;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.Form89;

public class SimpleForm89 extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -7985268200400994850L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	private Integer ageAtTB03Registration;
	
	private Integer yearOfTB03Registration;
	
	private Boolean isChildbearingAge;
	
	private Boolean isPulmonary;
	
	private String address;
	
	private String cityOfOrigin;
	
	private String clinicianNotes;
	
	private String cmacNumber;
	
	private String complication;
	
	private String countryOfOrigin;
	
	private String link;
	
	private String nameOfDoctor;
	
	private String otherDisease;
	
	private String otherMethodOfDetection;
	
	private String registrationNumber;
	
	private Date cmacDate;
	
	private Date dateOfBirth;
	
	private Date dateFirstSeekingHelp;
	
	private Date dateOfDecaySurvey;
	
	private Date dateOfReturn;
	
	private Date form89Date;
	
	private Concept cancer;
	
	private Concept circumstancesOfDetection;
	
	private Concept cnsdl;
	
	private Concept diabetes;
	
	private Concept anatomicalSite;
	
	private Concept ptbLocation;
	
	private Concept eptbLocation;
	
	private Concept eptbSite;
	
	private Concept hepatitis;
	
	private Concept htHeartDisease;
	
	private Concept ibc20;
	
	private Concept kidneyDisease;
	
	private Concept locationType;
	
	private Concept mentalDisorder;
	
	private Concept methodOfDetection;
	
	private Concept noDisease;
	
	private Concept placeOfCommission;
	
	private Concept placeOfDetection;
	
	private Concept populationCategory;
	
	private Concept pregnant;
	
	private Concept prescribedTreatment;
	
	private Concept presenceOfDecay;
	
	private Concept profession;
	
	private Concept ulcer;
	
	public SimpleForm89() {
	}
	
	public SimpleForm89(Form89 form89) {
		setEncounter(form89.getEncounter());
		setUuid(getEncounter().getUuid());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(form89.getPatientProgramId());
		setPatientProgramUuid(patientProgram.getUuid());
		setAddress(form89.getAddress());
		setAgeAtTB03Registration(form89.getAgeAtRegistration());
		setCancer(form89.getCancer());
		setCircumstancesOfDetection(form89.getCircumstancesOfDetection());
		setCityOfOrigin(form89.getCityOfOrigin());
		setClinicianNotes(form89.getClinicianNotes());
		setCmacDate(form89.getCmacDate());
		setCmacNumber(form89.getCmacNumber());
		setCnsdl(form89.getCnsdl());
		setComplication(form89.getComplication());
		setCountryOfOrigin(form89.getCountryOfOrigin());
		setDateFirstSeekingHelp(form89.getDateFirstSeekingHelp());
		setDateOfBirth(form89.getDateOfBirth());
		setDateOfDecaySurvey(form89.getDateOfDecaySurvey());
		setDateOfReturn(form89.getDateOfReturn());
		setDiabetes(form89.getDiabetes());
		setAnatomicalSite(form89.getAnatomicalSite());
		setPtbLocation(form89.getPtbLocation());
		setEptbLocation(form89.getEptbLocation());
		setEptbSite(form89.getEptbSite());
		setForm89Date(form89.getForm89Date());
		setHepatitis(form89.getHepatitis());
		setHtHeartDisease(form89.getHtHeartDisease());
		setIbc20(form89.getIbc20());
		setKidneyDisease(form89.getKidneyDisease());
		setLocationType(form89.getLocationType());
		setMentalDisorder(form89.getMentalDisorder());
		setMethodOfDetection(form89.getMethodOfDetection());
		setNameOfDoctor(form89.getNameOfDoctor());
		setIsChildbearingAge(form89.getIsChildbearingAge());
		setIsPulmonary(form89.getIsPulmonary());
		setLink(form89.getLink());
		setNoDisease(form89.getNoDisease());
		setOtherDisease(form89.getOtherDisease());
		setOtherMethodOfDetection(form89.getOtherMethodOfDetection());
		setPlaceOfCommission(form89.getPlaceOfCommission());
		setPlaceOfDetection(form89.getPlaceOfDetection());
		setPopulationCategory(form89.getPopulationCategory());
		setPregnant(form89.getPregnant());
		setPrescribedTreatment(form89.getPrescribedTreatment());
		setPresenceOfDecay(form89.getPresenceOfDecay());
		setProfession(form89.getProfession());
		setRegistrationNumber(form89.getRegistrationNumber());
		setUlcer(form89.getUlcer());
		setYearOfTB03Registration(form89.getYearOfTB03Registration());
	}
	
	/**
	 * Provide {@link SimpleForm89} representation of {@link Form89}
	 */
	public Form89 toForm() {
		Form89 form89 = new Form89();
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		form89.setPatientProgramId(patientProgram.getPatientProgramId());
		form89.setEncounter(getEncounter());
		form89.setCancer(getCancer());
		form89.setCircumstancesOfDetection(getCircumstancesOfDetection());
		form89.setCityOfOrigin(getCityOfOrigin());
		form89.setClinicianNotes(getClinicianNotes());
		form89.setCmacDate(getCmacDate());
		form89.setCmacNumber(getCmacNumber());
		form89.setCnsdl(getCnsdl());
		form89.setComplication(getComplication());
		form89.setCountryOfOrigin(getCountryOfOrigin());
		form89.setDateFirstSeekingHelp(getDateFirstSeekingHelp());
		form89.setDateOfDecaySurvey(getDateOfDecaySurvey());
		form89.setDateOfReturn(getDateOfReturn());
		form89.setDiabetes(getDiabetes());
		form89.setPtbLocation(getPtbLocation());
		form89.setEptbLocation(getEptbLocation());
		form89.setEptbSite(getEptbSite());
		form89.setForm89Date(getForm89Date());
		form89.setHepatitis(getHepatitis());
		form89.setHtHeartDisease(getHtHeartDisease());
		form89.setKidneyDisease(getKidneyDisease());
		form89.setLocationType(getLocationType());
		form89.setMentalDisorder(getMentalDisorder());
		form89.setMethodOfDetection(getMethodOfDetection());
		form89.setNameOfDoctor(getNameOfDoctor());
		form89.setNoDisease(getNoDisease());
		form89.setOtherDisease(getOtherDisease());
		form89.setOtherMethodOfDetection(getOtherMethodOfDetection());
		form89.setPlaceOfCommission(getPlaceOfCommission());
		form89.setPlaceOfDetection(getPlaceOfDetection());
		form89.setPopulationCategory(getPopulationCategory());
		form89.setPregnant(getPregnant());
		form89.setPrescribedTreatment(getPrescribedTreatment());
		form89.setPresenceOfDecay(getPresenceOfDecay());
		form89.setProfession(getProfession());
		form89.setUlcer(getUlcer());
		return form89;
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
	
	public Integer getAgeAtTB03Registration() {
		return ageAtTB03Registration;
	}
	
	public void setAgeAtTB03Registration(Integer ageAtTB03Registration) {
		this.ageAtTB03Registration = ageAtTB03Registration;
	}
	
	public Integer getYearOfTB03Registration() {
		return yearOfTB03Registration;
	}
	
	public void setYearOfTB03Registration(Integer yearOfTB03Registration) {
		this.yearOfTB03Registration = yearOfTB03Registration;
	}
	
	public Boolean getIsChildbearingAge() {
		return isChildbearingAge;
	}
	
	public void setIsChildbearingAge(Boolean isChildbearingAge) {
		this.isChildbearingAge = isChildbearingAge;
	}
	
	public Boolean getIsPulmonary() {
		return isPulmonary;
	}
	
	public void setIsPulmonary(Boolean isPulmonary) {
		this.isPulmonary = isPulmonary;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCityOfOrigin() {
		return cityOfOrigin;
	}
	
	public void setCityOfOrigin(String cityOfOrigin) {
		this.cityOfOrigin = cityOfOrigin;
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
	
	public String getComplication() {
		return complication;
	}
	
	public void setComplication(String complication) {
		this.complication = complication;
	}
	
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getNameOfDoctor() {
		return nameOfDoctor;
	}
	
	public void setNameOfDoctor(String nameOfDoctor) {
		this.nameOfDoctor = nameOfDoctor;
	}
	
	public String getOtherDisease() {
		return otherDisease;
	}
	
	public void setOtherDisease(String otherDisease) {
		this.otherDisease = otherDisease;
	}
	
	public String getOtherMethodOfDetection() {
		return otherMethodOfDetection;
	}
	
	public void setOtherMethodOfDetection(String otherMethodOfDetection) {
		this.otherMethodOfDetection = otherMethodOfDetection;
	}
	
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	
	public Date getCmacDate() {
		return cmacDate;
	}
	
	public void setCmacDate(Date cmacDate) {
		this.cmacDate = cmacDate;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public Date getDateFirstSeekingHelp() {
		return dateFirstSeekingHelp;
	}
	
	public void setDateFirstSeekingHelp(Date dateFirstSeekingHelp) {
		this.dateFirstSeekingHelp = dateFirstSeekingHelp;
	}
	
	public Date getDateOfDecaySurvey() {
		return dateOfDecaySurvey;
	}
	
	public void setDateOfDecaySurvey(Date dateOfDecaySurvey) {
		this.dateOfDecaySurvey = dateOfDecaySurvey;
	}
	
	public Date getDateOfReturn() {
		return dateOfReturn;
	}
	
	public void setDateOfReturn(Date dateOfReturn) {
		this.dateOfReturn = dateOfReturn;
	}
	
	public Date getForm89Date() {
		return form89Date;
	}
	
	public void setForm89Date(Date form89Date) {
		this.form89Date = form89Date;
	}
	
	public Concept getCancer() {
		return cancer;
	}
	
	public void setCancer(Concept cancer) {
		this.cancer = cancer;
	}
	
	public Concept getCircumstancesOfDetection() {
		return circumstancesOfDetection;
	}
	
	public void setCircumstancesOfDetection(Concept circumstancesOfDetection) {
		this.circumstancesOfDetection = circumstancesOfDetection;
	}
	
	public Concept getCnsdl() {
		return cnsdl;
	}
	
	public void setCnsdl(Concept cnsdl) {
		this.cnsdl = cnsdl;
	}
	
	public Concept getDiabetes() {
		return diabetes;
	}
	
	public void setDiabetes(Concept diabetes) {
		this.diabetes = diabetes;
	}
	
	public Concept getAnatomicalSite() {
		return anatomicalSite;
	}
	
	public void setAnatomicalSite(Concept anatomicalSite) {
		this.anatomicalSite = anatomicalSite;
	}
	
	public Concept getPtbLocation() {
		return ptbLocation;
	}
	
	public void setPtbLocation(Concept ptbLocation) {
		this.ptbLocation = ptbLocation;
	}
	
	public Concept getEptbLocation() {
		return eptbLocation;
	}
	
	public void setEptbLocation(Concept eptbLocation) {
		this.eptbLocation = eptbLocation;
	}
	
	public Concept getEptbSite() {
		return eptbSite;
	}
	
	public void setEptbSite(Concept eptbSite) {
		this.eptbSite = eptbSite;
	}
	
	public Concept getHepatitis() {
		return hepatitis;
	}
	
	public void setHepatitis(Concept hepatitis) {
		this.hepatitis = hepatitis;
	}
	
	public Concept getHtHeartDisease() {
		return htHeartDisease;
	}
	
	public void setHtHeartDisease(Concept htHeartDisease) {
		this.htHeartDisease = htHeartDisease;
	}
	
	public Concept getKidneyDisease() {
		return kidneyDisease;
	}
	
	public void setKidneyDisease(Concept kidneyDisease) {
		this.kidneyDisease = kidneyDisease;
	}
	
	public Concept getLocationType() {
		return locationType;
	}
	
	public void setLocationType(Concept locationType) {
		this.locationType = locationType;
	}
	
	public Concept getMentalDisorder() {
		return mentalDisorder;
	}
	
	public void setMentalDisorder(Concept mentalDisorder) {
		this.mentalDisorder = mentalDisorder;
	}
	
	public Concept getMethodOfDetection() {
		return methodOfDetection;
	}
	
	public void setMethodOfDetection(Concept methodOfDetection) {
		this.methodOfDetection = methodOfDetection;
	}
	
	public Concept getNoDisease() {
		return noDisease;
	}
	
	public void setNoDisease(Concept noDisease) {
		this.noDisease = noDisease;
	}
	
	public Concept getPlaceOfCommission() {
		return placeOfCommission;
	}
	
	public void setPlaceOfCommission(Concept placeOfCommission) {
		this.placeOfCommission = placeOfCommission;
	}
	
	public Concept getPlaceOfDetection() {
		return placeOfDetection;
	}
	
	public void setPlaceOfDetection(Concept placeOfDetection) {
		this.placeOfDetection = placeOfDetection;
	}
	
	public Concept getPopulationCategory() {
		return populationCategory;
	}
	
	public void setPopulationCategory(Concept populationCategory) {
		this.populationCategory = populationCategory;
	}
	
	public Concept getPregnant() {
		return pregnant;
	}
	
	public void setPregnant(Concept pregnant) {
		this.pregnant = pregnant;
	}
	
	public Concept getPrescribedTreatment() {
		return prescribedTreatment;
	}
	
	public void setPrescribedTreatment(Concept prescribedTreatment) {
		this.prescribedTreatment = prescribedTreatment;
	}
	
	public Concept getPresenceOfDecay() {
		return presenceOfDecay;
	}
	
	public void setPresenceOfDecay(Concept presenceOfDecay) {
		this.presenceOfDecay = presenceOfDecay;
	}
	
	public Concept getProfession() {
		return profession;
	}
	
	public void setProfession(Concept profession) {
		this.profession = profession;
	}
	
	public Concept getUlcer() {
		return ulcer;
	}
	
	public void setUlcer(Concept ulcer) {
		this.ulcer = ulcer;
	}
	
	public Concept getIbc20() {
		return ibc20;
	}
	
	public void setIbc20(Concept ibc20) {
		this.ibc20 = ibc20;
	}
}
