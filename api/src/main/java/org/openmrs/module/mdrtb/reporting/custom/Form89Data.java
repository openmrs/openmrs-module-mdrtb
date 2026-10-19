package org.openmrs.module.mdrtb.reporting.custom;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.api.MdrtbService;

public class Form89Data implements Comparable<Form89Data> {
	
	private Patient patient;
	
	private Form89 form89;
	
	private String identifier;
	
	private String tb03RegistrationDate;
	
	private Integer ageAtTB03Registration;
	
	private Location tb03Location;
	
	private String dateOfBirth;
	
	private String pulmonarySite;
	
	private String siteOfDisease;
	
	private String locationOfDisease;
	
	private String dateFirstSeekingHelp;
	
	private String dateOfReturn;
	
	private String dateOfDecaySurvey;
	
	private String cmacDate;
	
	private String form89Date;
	
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
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	
	public void setAgeAtTB03Registration(Integer ageAtTB03Registration) {
		this.ageAtTB03Registration = ageAtTB03Registration;
	}
	
	public Location getTb03Location() {
		return tb03Location;
	}
	
	public void setTb03Location(Location tb03Location) {
		this.tb03Location = tb03Location;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getPulmonarySite() {
		return pulmonarySite;
	}
	
	public void setPulmonarySite(String pulmonarySite) {
		this.pulmonarySite = pulmonarySite;
	}
	
	public String getSiteOfDisease() {
		return siteOfDisease;
	}
	
	public void setSiteOfDisease(String siteOfDisease) {
		this.siteOfDisease = siteOfDisease;
	}
	
	public String getLocationOfDisease() {
		return locationOfDisease;
	}
	
	public void setLocationOfDisease(String locationOfDisease) {
		this.locationOfDisease = locationOfDisease;
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
	
	public int compareTo(Form89Data o) {
		if (o.getIdentifier() == null || getIdentifier() == null)
			return 0;
		
		return identifier.compareTo(o.getIdentifier());
		
	}
	
	/**
	 * Returns the gender exactly as stored on the Patient, "M" or "F", and never a translated
	 * label. Translation of this field belongs to the consuming client alone: this getter used to
	 * resolve mdrtb.tb03.gender.male/female here, which put display text in the API payload and
	 * left the web app unable to render it in the user's own locale (a Russian client received the
	 * string the server had already chosen). The web templates now build the message key from this
	 * value.
	 * 
	 * @return "M", "F", or "" when no gender is recorded
	 */
	public String getGender() {
		if (patient == null || patient.getGender() == null) {
			return "";
		}
		return patient.getGender();
	}
	
	public void setGender(String gender) {
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
	
	public String getDiagnosticSmearLab() {
		return diagnosticSmearLab;
	}
	
	public void setDiagnosticSmearLab(String diagnosticSmearLab) {
		this.diagnosticSmearLab = diagnosticSmearLab;
	}
	
	public String getXpertLab() {
		return xpertLab;
	}
	
	public void setXpertLab(String xpertLab) {
		this.xpertLab = xpertLab;
	}
	
	public String getHainLab() {
		return hainLab;
	}
	
	public void setHainLab(String hainLab) {
		this.hainLab = hainLab;
	}
	
	public String getHain2Lab() {
		return hain2Lab;
	}
	
	public void setHain2Lab(String hain2Lab) {
		this.hain2Lab = hain2Lab;
	}
	
	public Form89 getForm89() {
		return form89;
	}
	
	public void setForm89(Form89 form89) {
		this.form89 = form89;
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
	
	public String getForm89Date() {
		return form89Date;
	}
	
	public void setForm89Date(String form89Date) {
		this.form89Date = form89Date;
	}
	
}
