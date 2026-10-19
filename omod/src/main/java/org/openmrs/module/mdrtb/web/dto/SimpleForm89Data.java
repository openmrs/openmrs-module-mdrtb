package org.openmrs.module.mdrtb.web.dto;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.module.mdrtb.reporting.custom.Form89Data;

public class SimpleForm89Data extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private String patientUuid;
	
	private String form89Uuid;
	
	private String identifier;
	
	private PersonName personName;
	
	private PersonAddress personAddress;
	
	private String gender;
	
	private String tb03RegistrationDate;
	
	private String tb03Facility;
	
	private Integer ageAtTB03Registration;
	
	private String dateOfBirth;
	
	private String siteOfDisease;
	
	private String dateFirstSeekingHelp;
	
	private String dateOfReturn;
	
	private String dateOfDecaySurvey;
	
	private String cmacDate;
	
	private Date form89Date;
	
	private String diagnosticSmearResult;
	
	private String diagnosticSmearTestNumber;
	
	private String diagnosticSmearDate;
	
	private String diagnosticSmearLab;
	
	private String xpertMTBResult;
	
	private String xpertRIFResult;
	
	private String xpertTestDate;
	
	private String xpertTestNumber;
	
	private String xpertLab;
	
	private String hainMTBResult;
	
	private String hainINHResult;
	
	private String hainRIFResult;
	
	private String hainTestDate;
	
	private String hainTestNumber;
	
	private String hainLab;
	
	private String hain2MTBResult;
	
	private String hain2InjResult;
	
	private String hain2FqResult;
	
	private String hain2TestDate;
	
	private String hain2TestNumber;
	
	private String hain2Lab;
	
	private Concept locationType;
	
	private Concept pregnant;
	
	private Concept profession;
	
	private Concept populationCategory;
	
	private Concept placeOfDetection;
	
	private Concept circumstancesOfDetection;
	
	private Concept methodOfDetection;
	
	private Concept anatomicalSite;
	
	private Concept eptbSite;
	
	private Concept ptbLocation;
	
	private Concept eptbLocation;
	
	private Concept presenceOfDecay;
	
	private Concept diabetes;
	
	private Concept cnsdl;
	
	private Concept htHeartDisease;
	
	private Concept ulcer;
	
	private Concept mentalDisorder;
	
	private Concept ibc20;
	
	private Concept cancer;
	
	private Concept hepatitis;
	
	private Concept kidneyDisease;
	
	private Concept noDisease;
	
	private String otherDisease;
	
	private String cmacNumber;
	
	private Concept prescribedTreatment;
	
	private String nameOfDoctor;
	
	private String countryOfOrigin;
	
	private Concept placeOfCommission;
	
	private String cityOfOrigin;
	
	private String otherMethodOfDetection;
	
	private String complication;
	
	public SimpleForm89Data(Form89Data form89Data) {
		this.patientUuid = form89Data.getPatient().getUuid();
		this.personName = form89Data.getPatient().getPersonName();
		this.personAddress = form89Data.getPatient().getPersonAddress();
		this.gender = form89Data.getGender();
		this.locationType = form89Data.getForm89().getLocationType();
		this.pregnant = form89Data.getForm89().getPregnant();
		this.profession = form89Data.getForm89().getProfession();
		this.populationCategory = form89Data.getForm89().getPopulationCategory();
		this.placeOfDetection = form89Data.getForm89().getPlaceOfDetection();
		this.circumstancesOfDetection = form89Data.getForm89().getCircumstancesOfDetection();
		this.methodOfDetection = form89Data.getForm89().getMethodOfDetection();
		this.anatomicalSite = form89Data.getForm89().getAnatomicalSite();
		this.ptbLocation = form89Data.getForm89().getPtbLocation();
		this.eptbSite = form89Data.getForm89().getEptbSite();
		this.eptbLocation = form89Data.getForm89().getEptbLocation();
		this.presenceOfDecay = form89Data.getForm89().getPresenceOfDecay();
		this.diabetes = form89Data.getForm89().getDiabetes();
		this.cnsdl = form89Data.getForm89().getCnsdl();
		this.htHeartDisease = form89Data.getForm89().getHtHeartDisease();
		this.ulcer = form89Data.getForm89().getUlcer();
		this.mentalDisorder = form89Data.getForm89().getMentalDisorder();
		this.ibc20 = form89Data.getForm89().getIbc20();
		this.cancer = form89Data.getForm89().getCancer();
		this.hepatitis = form89Data.getForm89().getHepatitis();
		this.kidneyDisease = form89Data.getForm89().getKidneyDisease();
		this.noDisease = form89Data.getForm89().getNoDisease();
		this.otherDisease = form89Data.getForm89().getOtherDisease();
		this.cmacNumber = form89Data.getForm89().getCmacNumber();
		this.prescribedTreatment = form89Data.getForm89().getPrescribedTreatment();
		this.nameOfDoctor = form89Data.getForm89().getNameOfDoctor();
		this.countryOfOrigin = form89Data.getForm89().getCountryOfOrigin();
		this.placeOfCommission = form89Data.getForm89().getPlaceOfCommission();
		this.cityOfOrigin = form89Data.getForm89().getCityOfOrigin();
		this.otherMethodOfDetection = form89Data.getForm89().getOtherMethodOfDetection();
		this.complication = form89Data.getForm89().getComplication();
		this.form89Uuid = form89Data.getForm89().getEncounter().getUuid();
		this.identifier = form89Data.getIdentifier();
		this.tb03RegistrationDate = form89Data.getTb03RegistrationDate();
		this.ageAtTB03Registration = form89Data.getAgeAtTB03Registration();
		this.tb03Facility = null;
		if (form89Data.getTb03Location() != null) {
			this.tb03Facility = form89Data.getTb03Location().getName();
		}
		this.dateOfBirth = form89Data.getDateOfBirth();
		this.siteOfDisease = form89Data.getSiteOfDisease();
		this.dateFirstSeekingHelp = form89Data.getDateFirstSeekingHelp();
		this.dateOfReturn = form89Data.getDateOfReturn();
		this.dateOfDecaySurvey = form89Data.getDateOfDecaySurvey();
		this.cmacDate = form89Data.getCmacDate();
		this.form89Date = form89Data.getForm89().getForm89Date();
		this.diagnosticSmearResult = form89Data.getDiagnosticSmearResult();
		this.diagnosticSmearTestNumber = form89Data.getDiagnosticSmearTestNumber();
		this.diagnosticSmearDate = form89Data.getDiagnosticSmearDate();
		this.diagnosticSmearLab = form89Data.getDiagnosticSmearLab();
		this.xpertMTBResult = form89Data.getXpertMTBResult();
		this.xpertRIFResult = form89Data.getXpertRIFResult();
		this.xpertTestDate = form89Data.getXpertTestDate();
		this.xpertTestNumber = form89Data.getXpertTestNumber();
		this.xpertLab = form89Data.getXpertLab();
		this.hainMTBResult = form89Data.getHainMTBResult();
		this.hainINHResult = form89Data.getHainINHResult();
		this.hainRIFResult = form89Data.getHainRIFResult();
		this.hainTestDate = form89Data.getHainTestDate();
		this.hainTestNumber = form89Data.getHainTestNumber();
		this.hainLab = form89Data.getHainLab();
		this.hain2MTBResult = form89Data.getHain2MTBResult();
		this.hain2InjResult = form89Data.getHain2InjResult();
		this.hain2FqResult = form89Data.getHain2FqResult();
		this.hain2TestDate = form89Data.getHain2TestDate();
		this.hain2TestNumber = form89Data.getHain2TestNumber();
		this.hain2Lab = form89Data.getHain2Lab();
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer integer) {
		// Not gonna happen
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	public String getForm89Uuid() {
		return form89Uuid;
	}
	
	public void setForm89Uuid(String form89Uuid) {
		this.form89Uuid = form89Uuid;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public PersonName getPersonName() {
		return personName;
	}
	
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}
	
	public PersonAddress getPersonAddress() {
		return personAddress;
	}
	
	public void setPersonAddress(PersonAddress personAddress) {
		this.personAddress = personAddress;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getTb03RegistrationDate() {
		return tb03RegistrationDate;
	}
	
	public void setTb03RegistrationDate(String tb03RegistrationDate) {
		this.tb03RegistrationDate = tb03RegistrationDate;
	}
	
	public Integer getAgeAtTB03Registration() {
		return ageAtTB03Registration;
	}
	
	public String getTb03Facility() {
		return tb03Facility;
	}
	
	public void setTb03Facility(String tb03Facility) {
		this.tb03Facility = tb03Facility;
	}
	
	public void setAgeAtTB03Registration(Integer ageAtTB03Registration) {
		this.ageAtTB03Registration = ageAtTB03Registration;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getSiteOfDisease() {
		return siteOfDisease;
	}
	
	public void setSiteOfDisease(String siteOfDisease) {
		this.siteOfDisease = siteOfDisease;
	}
	
	public String getDateFirstSeekingHelp() {
		return dateFirstSeekingHelp;
	}
	
	public void setDateFirstSeekingHelp(String dateFirstSeekingHelp) {
		this.dateFirstSeekingHelp = dateFirstSeekingHelp;
	}
	
	public String getDateOfReturn() {
		return dateOfReturn;
	}
	
	public void setDateOfReturn(String dateOfReturn) {
		this.dateOfReturn = dateOfReturn;
	}
	
	public String getDateOfDecaySurvey() {
		return dateOfDecaySurvey;
	}
	
	public void setDateOfDecaySurvey(String dateOfDecaySurvey) {
		this.dateOfDecaySurvey = dateOfDecaySurvey;
	}
	
	public String getCmacDate() {
		return cmacDate;
	}
	
	public void setCmacDate(String cmacDate) {
		this.cmacDate = cmacDate;
	}
	
	public Date getForm89Date() {
		return form89Date;
	}
	
	public void setForm89Date(Date form89Date) {
		this.form89Date = form89Date;
	}
	
	public String getDiagnosticSmearResult() {
		return diagnosticSmearResult;
	}
	
	public void setDiagnosticSmearResult(String diagnosticSmearResult) {
		this.diagnosticSmearResult = diagnosticSmearResult;
	}
	
	public String getDiagnosticSmearTestNumber() {
		return diagnosticSmearTestNumber;
	}
	
	public void setDiagnosticSmearTestNumber(String diagnosticSmearTestNumber) {
		this.diagnosticSmearTestNumber = diagnosticSmearTestNumber;
	}
	
	public String getDiagnosticSmearDate() {
		return diagnosticSmearDate;
	}
	
	public void setDiagnosticSmearDate(String diagnosticSmearDate) {
		this.diagnosticSmearDate = diagnosticSmearDate;
	}
	
	public String getDiagnosticSmearLab() {
		return diagnosticSmearLab;
	}
	
	public void setDiagnosticSmearLab(String diagnosticSmearLab) {
		this.diagnosticSmearLab = diagnosticSmearLab;
	}
	
	public String getXpertMTBResult() {
		return xpertMTBResult;
	}
	
	public void setXpertMTBResult(String xpertMTBResult) {
		this.xpertMTBResult = xpertMTBResult;
	}
	
	public String getXpertRIFResult() {
		return xpertRIFResult;
	}
	
	public void setXpertRIFResult(String xpertRIFResult) {
		this.xpertRIFResult = xpertRIFResult;
	}
	
	public String getXpertTestDate() {
		return xpertTestDate;
	}
	
	public void setXpertTestDate(String xpertTestDate) {
		this.xpertTestDate = xpertTestDate;
	}
	
	public String getXpertTestNumber() {
		return xpertTestNumber;
	}
	
	public void setXpertTestNumber(String xpertTestNumber) {
		this.xpertTestNumber = xpertTestNumber;
	}
	
	public String getXpertLab() {
		return xpertLab;
	}
	
	public void setXpertLab(String xpertLab) {
		this.xpertLab = xpertLab;
	}
	
	public String getHainMTBResult() {
		return hainMTBResult;
	}
	
	public void setHainMTBResult(String hainMTBResult) {
		this.hainMTBResult = hainMTBResult;
	}
	
	public String getHainINHResult() {
		return hainINHResult;
	}
	
	public void setHainINHResult(String hainINHResult) {
		this.hainINHResult = hainINHResult;
	}
	
	public String getHainRIFResult() {
		return hainRIFResult;
	}
	
	public void setHainRIFResult(String hainRIFResult) {
		this.hainRIFResult = hainRIFResult;
	}
	
	public String getHainTestDate() {
		return hainTestDate;
	}
	
	public void setHainTestDate(String hainTestDate) {
		this.hainTestDate = hainTestDate;
	}
	
	public String getHainTestNumber() {
		return hainTestNumber;
	}
	
	public void setHainTestNumber(String hainTestNumber) {
		this.hainTestNumber = hainTestNumber;
	}
	
	public String getHainLab() {
		return hainLab;
	}
	
	public void setHainLab(String hainLab) {
		this.hainLab = hainLab;
	}
	
	public String getHain2MTBResult() {
		return hain2MTBResult;
	}
	
	public void setHain2MTBResult(String hain2mtbResult) {
		hain2MTBResult = hain2mtbResult;
	}
	
	public String getHain2InjResult() {
		return hain2InjResult;
	}
	
	public void setHain2InjResult(String hain2InjResult) {
		this.hain2InjResult = hain2InjResult;
	}
	
	public String getHain2FqResult() {
		return hain2FqResult;
	}
	
	public void setHain2FqResult(String hain2FqResult) {
		this.hain2FqResult = hain2FqResult;
	}
	
	public String getHain2TestDate() {
		return hain2TestDate;
	}
	
	public void setHain2TestDate(String hain2TestDate) {
		this.hain2TestDate = hain2TestDate;
	}
	
	public String getHain2TestNumber() {
		return hain2TestNumber;
	}
	
	public void setHain2TestNumber(String hain2TestNumber) {
		this.hain2TestNumber = hain2TestNumber;
	}
	
	public String getHain2Lab() {
		return hain2Lab;
	}
	
	public void setHain2Lab(String hain2Lab) {
		this.hain2Lab = hain2Lab;
	}
	
	public Concept getLocationType() {
		return locationType;
	}
	
	public void setLocationType(Concept locationType) {
		this.locationType = locationType;
	}
	
	public Concept getPregnant() {
		return pregnant;
	}
	
	public void setPregnant(Concept pregnant) {
		this.pregnant = pregnant;
	}
	
	public Concept getProfession() {
		return profession;
	}
	
	public void setProfession(Concept profession) {
		this.profession = profession;
	}
	
	public Concept getPopulationCategory() {
		return populationCategory;
	}
	
	public void setPopulationCategory(Concept populationCategory) {
		this.populationCategory = populationCategory;
	}
	
	public Concept getPlaceOfDetection() {
		return placeOfDetection;
	}
	
	public void setPlaceOfDetection(Concept placeOfDetection) {
		this.placeOfDetection = placeOfDetection;
	}
	
	public Concept getCircumstancesOfDetection() {
		return circumstancesOfDetection;
	}
	
	public void setCircumstancesOfDetection(Concept circumstancesOfDetection) {
		this.circumstancesOfDetection = circumstancesOfDetection;
	}
	
	public Concept getMethodOfDetection() {
		return methodOfDetection;
	}
	
	public void setMethodOfDetection(Concept methodOfDetection) {
		this.methodOfDetection = methodOfDetection;
	}
	
	public Concept getAnatomicalSite() {
		return anatomicalSite;
	}
	
	public void setAnatomicalSite(Concept anatomicalSite) {
		this.anatomicalSite = anatomicalSite;
	}
	
	public Concept getEptbSite() {
		return eptbSite;
	}
	
	public void setEptbSite(Concept eptbSite) {
		this.eptbSite = eptbSite;
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
	
	public Concept getPresenceOfDecay() {
		return presenceOfDecay;
	}
	
	public void setPresenceOfDecay(Concept presenceOfDecay) {
		this.presenceOfDecay = presenceOfDecay;
	}
	
	public Concept getDiabetes() {
		return diabetes;
	}
	
	public void setDiabetes(Concept diabetes) {
		this.diabetes = diabetes;
	}
	
	public Concept getCnsdl() {
		return cnsdl;
	}
	
	public void setCnsdl(Concept cnsdl) {
		this.cnsdl = cnsdl;
	}
	
	public Concept getHtHeartDisease() {
		return htHeartDisease;
	}
	
	public void setHtHeartDisease(Concept htHeartDisease) {
		this.htHeartDisease = htHeartDisease;
	}
	
	public Concept getUlcer() {
		return ulcer;
	}
	
	public void setUlcer(Concept ulcer) {
		this.ulcer = ulcer;
	}
	
	public Concept getMentalDisorder() {
		return mentalDisorder;
	}
	
	public void setMentalDisorder(Concept mentalDisorder) {
		this.mentalDisorder = mentalDisorder;
	}
	
	public Concept getIbc20() {
		return ibc20;
	}
	
	public void setIbc20(Concept ibc20) {
		this.ibc20 = ibc20;
	}
	
	public Concept getCancer() {
		return cancer;
	}
	
	public void setCancer(Concept cancer) {
		this.cancer = cancer;
	}
	
	public Concept getHepatitis() {
		return hepatitis;
	}
	
	public void setHepatitis(Concept hepatitis) {
		this.hepatitis = hepatitis;
	}
	
	public Concept getKidneyDisease() {
		return kidneyDisease;
	}
	
	public void setKidneyDisease(Concept kidneyDisease) {
		this.kidneyDisease = kidneyDisease;
	}
	
	public Concept getNoDisease() {
		return noDisease;
	}
	
	public void setNoDisease(Concept noDisease) {
		this.noDisease = noDisease;
	}
	
	public String getOtherDisease() {
		return otherDisease;
	}
	
	public void setOtherDisease(String otherDisease) {
		this.otherDisease = otherDisease;
	}
	
	public String getCmacNumber() {
		return cmacNumber;
	}
	
	public void setCmacNumber(String cmacNumber) {
		this.cmacNumber = cmacNumber;
	}
	
	public Concept getPrescribedTreatment() {
		return prescribedTreatment;
	}
	
	public void setPrescribedTreatment(Concept prescribedTreatment) {
		this.prescribedTreatment = prescribedTreatment;
	}
	
	public String getNameOfDoctor() {
		return nameOfDoctor;
	}
	
	public void setNameOfDoctor(String nameOfDoctor) {
		this.nameOfDoctor = nameOfDoctor;
	}
	
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}
	
	public Concept getPlaceOfCommission() {
		return placeOfCommission;
	}
	
	public void setPlaceOfCommission(Concept placeOfCommission) {
		this.placeOfCommission = placeOfCommission;
	}
	
	public String getCityOfOrigin() {
		return cityOfOrigin;
	}
	
	public void setCityOfOrigin(String cityOfOrigin) {
		this.cityOfOrigin = cityOfOrigin;
	}
	
	public String getOtherMethodOfDetection() {
		return otherMethodOfDetection;
	}
	
	public void setOtherMethodOfDetection(String otherMethodOfDetection) {
		this.otherMethodOfDetection = otherMethodOfDetection;
	}
	
	public String getComplication() {
		return complication;
	}
	
	public void setComplication(String complication) {
		this.complication = complication;
	}
}
