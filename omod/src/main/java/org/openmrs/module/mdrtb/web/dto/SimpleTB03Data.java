package org.openmrs.module.mdrtb.web.dto;

import java.util.Map;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.module.mdrtb.reporting.custom.TB03Data;

public class SimpleTB03Data extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private String patientUuid;
	
	private String identifier;
	
	private PersonName personName;
	
	private String gender;
	
	private PersonAddress personAddress;
	
	private String tb03RegistrationDate;
	
	private String tb03Facility;
	
	private Integer ageAtTB03Registration;
	
	private String dateOfBirth;
	
	private String intensivePhaseFacility;
	
	private String continuationPhaseFacility;
	
	private String treatmentRegimen;
	
	private String tb03TreatmentStartDate;
	
	private String siteOfDisease;
	
	private Integer regGroup;
	
	private String hivTestResult;
	
	private String hivTestDate;
	
	private String artStartDate;
	
	private String cpStartDate;
	
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
	
	private String cultureResult;
	
	private String cultureTestDate;
	
	private String cultureTestNumber;
	
	private String cultureLab;
	
	private String drugResistance;
	
	private String month2SmearResult;
	
	private String month2SmearDate;
	
	private String month2TestNumber;
	
	private String month2TestLab;
	
	private String month3SmearResult;
	
	private String month3SmearDate;
	
	private String month3TestNumber;
	
	private String month3TestLab;
	
	private String month4SmearResult;
	
	private String month4SmearDate;
	
	private String month4TestNumber;
	
	private String month4TestLab;
	
	private String month5SmearResult;
	
	private String month5SmearDate;
	
	private String month5TestNumber;
	
	private String month5TestLab;
	
	private String month6SmearResult;
	
	private String month6SmearDate;
	
	private String month6TestNumber;
	
	private String month6TestLab;
	
	private String month8SmearResult;
	
	private String month8SmearDate;
	
	private String month8TestNumber;
	
	private String month8TestLab;
	
	private Integer tb03TreatmentOutcome;
	
	private String tb03TreatmentOutcomeDate;
	
	private Boolean diedOfTB;
	
	private String notes;
	
	private Boolean reg1New;
	
	private Boolean reg1Rtx;
	
	private String dstCollectionDate;
	
	private String dstResultDate;
	
	private Map<String, String> dstResults;
	
	public SimpleTB03Data(TB03Data tb03Data) {
		patientUuid = tb03Data.getPatient().getUuid();
		personName = tb03Data.getPatient().getPersonName();
		personAddress = tb03Data.getPatient().getPersonAddress();
		gender = tb03Data.getGender();
		identifier = tb03Data.getIdentifier();
		tb03RegistrationDate = tb03Data.getTb03RegistrationDate();
		tb03Facility = null;
		if (tb03Data.getTb03Location() != null) {
			tb03Facility = tb03Data.getTb03Location().getName();
		}
		ageAtTB03Registration = tb03Data.getAgeAtTB03Registration();
		dateOfBirth = tb03Data.getDateOfBirth();
		intensivePhaseFacility = tb03Data.getIntensivePhaseFacility();
		continuationPhaseFacility = tb03Data.getContinuationPhaseFacility();
		treatmentRegimen = tb03Data.getTreatmentRegimen();
		tb03TreatmentStartDate = tb03Data.getTb03TreatmentStartDate();
		siteOfDisease = tb03Data.getSiteOfDisease();
		regGroup = tb03Data.getRegGroup();
		hivTestResult = tb03Data.getHivTestResult();
		hivTestDate = tb03Data.getHivTestDate();
		artStartDate = tb03Data.getArtStartDate();
		cpStartDate = tb03Data.getCpStartDate();
		diagnosticSmearResult = tb03Data.getDiagnosticSmearResult();
		diagnosticSmearTestNumber = tb03Data.getDiagnosticSmearTestNumber();
		diagnosticSmearDate = tb03Data.getDiagnosticSmearDate();
		diagnosticSmearLab = tb03Data.getDiagnosticSmearLab();
		xpertMTBResult = tb03Data.getXpertMTBResult();
		xpertRIFResult = tb03Data.getXpertRIFResult();
		xpertTestDate = tb03Data.getXpertTestDate();
		xpertTestNumber = tb03Data.getXpertTestNumber();
		xpertLab = tb03Data.getXpertLab();
		hainMTBResult = tb03Data.getHainMTBResult();
		hainINHResult = tb03Data.getHainINHResult();
		hainRIFResult = tb03Data.getHainRIFResult();
		hainTestDate = tb03Data.getHainTestDate();
		hainTestNumber = tb03Data.getHainTestNumber();
		hainLab = tb03Data.getHainLab();
		hain2MTBResult = tb03Data.getHain2MTBResult();
		hain2InjResult = tb03Data.getHain2InjResult();
		hain2FqResult = tb03Data.getHain2FqResult();
		hain2TestDate = tb03Data.getHain2TestDate();
		hain2TestNumber = tb03Data.getHain2TestNumber();
		hain2Lab = tb03Data.getHain2Lab();
		cultureResult = tb03Data.getCultureResult();
		cultureTestDate = tb03Data.getCultureTestDate();
		cultureTestNumber = tb03Data.getCultureTestNumber();
		cultureLab = tb03Data.getCultureLab();
		drugResistance = tb03Data.getDrugResistance();
		month2SmearResult = tb03Data.getMonth2SmearResult();
		month2SmearDate = tb03Data.getMonth2SmearDate();
		month2TestNumber = tb03Data.getMonth2TestNumber();
		month2TestLab = tb03Data.getMonth2TestLab();
		month3SmearResult = tb03Data.getMonth3SmearResult();
		month3SmearDate = tb03Data.getMonth3SmearDate();
		month3TestNumber = tb03Data.getMonth3TestNumber();
		month3TestLab = tb03Data.getMonth3TestLab();
		month4SmearResult = tb03Data.getMonth4SmearResult();
		month4SmearDate = tb03Data.getMonth4SmearDate();
		month4TestNumber = tb03Data.getMonth4TestNumber();
		month4TestLab = tb03Data.getMonth4TestLab();
		month5SmearResult = tb03Data.getMonth5SmearResult();
		month5SmearDate = tb03Data.getMonth5SmearDate();
		month5TestNumber = tb03Data.getMonth5TestNumber();
		month5TestLab = tb03Data.getMonth5TestLab();
		month6SmearResult = tb03Data.getMonth6SmearResult();
		month6SmearDate = tb03Data.getMonth6SmearDate();
		month6TestNumber = tb03Data.getMonth6TestNumber();
		month6TestLab = tb03Data.getMonth6TestLab();
		month8SmearResult = tb03Data.getMonth8SmearResult();
		month8SmearDate = tb03Data.getMonth8SmearDate();
		month8TestNumber = tb03Data.getMonth8TestNumber();
		month8TestLab = tb03Data.getMonth8TestLab();
		tb03TreatmentOutcome = tb03Data.getTb03TreatmentOutcome();
		tb03TreatmentOutcomeDate = tb03Data.getTb03TreatmentOutcomeDate();
		diedOfTB = tb03Data.getDiedOfTB();
		notes = tb03Data.getNotes();
		reg1New = tb03Data.getReg1New();
		reg1Rtx = tb03Data.getReg1Rtx();
		dstCollectionDate = tb03Data.getDstCollectionDate();
		dstResultDate = tb03Data.getDstResultDate();
		dstResults = tb03Data.getDstResults();
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer id) {
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	public PersonName getPersonName() {
		return personName;
	}
	
	public void setPersonName(PersonName patientName) {
		this.personName = patientName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public PersonAddress getPersonAddress() {
		return personAddress;
	}
	
	public void setResidentialAddress(PersonAddress personAddress) {
		this.personAddress = personAddress;
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
	
	public String getTb03Facility() {
		return tb03Facility;
	}
	
	public void setTb03Facility(String tb03Facility) {
		this.tb03Facility = tb03Facility;
	}
	
	public Integer getAgeAtTB03Registration() {
		return ageAtTB03Registration;
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
	
	public String getIntensivePhaseFacility() {
		return intensivePhaseFacility;
	}
	
	public void setIntensivePhaseFacility(String intensivePhaseFacility) {
		this.intensivePhaseFacility = intensivePhaseFacility;
	}
	
	public String getContinuationPhaseFacility() {
		return continuationPhaseFacility;
	}
	
	public void setContinuationPhaseFacility(String continuationPhaseFacility) {
		this.continuationPhaseFacility = continuationPhaseFacility;
	}
	
	public String getTreatmentRegimen() {
		return treatmentRegimen;
	}
	
	public void setTreatmentRegimen(String treatmentRegimen) {
		this.treatmentRegimen = treatmentRegimen;
	}
	
	public String getTb03TreatmentStartDate() {
		return tb03TreatmentStartDate;
	}
	
	public void setTb03TreatmentStartDate(String tb03TreatmentStartDate) {
		this.tb03TreatmentStartDate = tb03TreatmentStartDate;
	}
	
	public String getSiteOfDisease() {
		return siteOfDisease;
	}
	
	public void setSiteOfDisease(String siteOfDisease) {
		this.siteOfDisease = siteOfDisease;
	}
	
	public Integer getRegGroup() {
		return regGroup;
	}
	
	public void setRegGroup(Integer regGroup) {
		this.regGroup = regGroup;
	}
	
	public String getHivTestResult() {
		return hivTestResult;
	}
	
	public void setHivTestResult(String hivTestResult) {
		this.hivTestResult = hivTestResult;
	}
	
	public String getHivTestDate() {
		return hivTestDate;
	}
	
	public void setHivTestDate(String hivTestDate) {
		this.hivTestDate = hivTestDate;
	}
	
	public String getArtStartDate() {
		return artStartDate;
	}
	
	public void setArtStartDate(String artStartDate) {
		this.artStartDate = artStartDate;
	}
	
	public String getCpStartDate() {
		return cpStartDate;
	}
	
	public void setCpStartDate(String cpStartDate) {
		this.cpStartDate = cpStartDate;
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
	
	public String getCultureResult() {
		return cultureResult;
	}
	
	public void setCultureResult(String cultureResult) {
		this.cultureResult = cultureResult;
	}
	
	public String getCultureTestDate() {
		return cultureTestDate;
	}
	
	public void setCultureTestDate(String cultureTestDate) {
		this.cultureTestDate = cultureTestDate;
	}
	
	public String getCultureTestNumber() {
		return cultureTestNumber;
	}
	
	public void setCultureTestNumber(String cultureTestNumber) {
		this.cultureTestNumber = cultureTestNumber;
	}
	
	public String getCultureLab() {
		return cultureLab;
	}
	
	public void setCultureLab(String cultureLab) {
		this.cultureLab = cultureLab;
	}
	
	public String getDrugResistance() {
		return drugResistance;
	}
	
	public void setDrugResistance(String drugResistance) {
		this.drugResistance = drugResistance;
	}
	
	public String getMonth2SmearResult() {
		return month2SmearResult;
	}
	
	public void setMonth2SmearResult(String month2SmearResult) {
		this.month2SmearResult = month2SmearResult;
	}
	
	public String getMonth2SmearDate() {
		return month2SmearDate;
	}
	
	public void setMonth2SmearDate(String month2SmearDate) {
		this.month2SmearDate = month2SmearDate;
	}
	
	public String getMonth2TestNumber() {
		return month2TestNumber;
	}
	
	public void setMonth2TestNumber(String month2TestNumber) {
		this.month2TestNumber = month2TestNumber;
	}
	
	public String getMonth2TestLab() {
		return month2TestLab;
	}
	
	public void setMonth2TestLab(String month2TestLab) {
		this.month2TestLab = month2TestLab;
	}
	
	public String getMonth3SmearResult() {
		return month3SmearResult;
	}
	
	public void setMonth3SmearResult(String month3SmearResult) {
		this.month3SmearResult = month3SmearResult;
	}
	
	public String getMonth3SmearDate() {
		return month3SmearDate;
	}
	
	public void setMonth3SmearDate(String month3SmearDate) {
		this.month3SmearDate = month3SmearDate;
	}
	
	public String getMonth3TestNumber() {
		return month3TestNumber;
	}
	
	public void setMonth3TestNumber(String month3TestNumber) {
		this.month3TestNumber = month3TestNumber;
	}
	
	public String getMonth3TestLab() {
		return month3TestLab;
	}
	
	public void setMonth3TestLab(String month3TestLab) {
		this.month3TestLab = month3TestLab;
	}
	
	public String getMonth4SmearResult() {
		return month4SmearResult;
	}
	
	public void setMonth4SmearResult(String month4SmearResult) {
		this.month4SmearResult = month4SmearResult;
	}
	
	public String getMonth4SmearDate() {
		return month4SmearDate;
	}
	
	public void setMonth4SmearDate(String month4SmearDate) {
		this.month4SmearDate = month4SmearDate;
	}
	
	public String getMonth4TestNumber() {
		return month4TestNumber;
	}
	
	public void setMonth4TestNumber(String month4TestNumber) {
		this.month4TestNumber = month4TestNumber;
	}
	
	public String getMonth4TestLab() {
		return month4TestLab;
	}
	
	public void setMonth4TestLab(String month4TestLab) {
		this.month4TestLab = month4TestLab;
	}
	
	public String getMonth5SmearResult() {
		return month5SmearResult;
	}
	
	public void setMonth5SmearResult(String month5SmearResult) {
		this.month5SmearResult = month5SmearResult;
	}
	
	public String getMonth5SmearDate() {
		return month5SmearDate;
	}
	
	public void setMonth5SmearDate(String month5SmearDate) {
		this.month5SmearDate = month5SmearDate;
	}
	
	public String getMonth5TestNumber() {
		return month5TestNumber;
	}
	
	public void setMonth5TestNumber(String month5TestNumber) {
		this.month5TestNumber = month5TestNumber;
	}
	
	public String getMonth5TestLab() {
		return month5TestLab;
	}
	
	public void setMonth5TestLab(String month5TestLab) {
		this.month5TestLab = month5TestLab;
	}
	
	public String getMonth6SmearResult() {
		return month6SmearResult;
	}
	
	public void setMonth6SmearResult(String month6SmearResult) {
		this.month6SmearResult = month6SmearResult;
	}
	
	public String getMonth6SmearDate() {
		return month6SmearDate;
	}
	
	public void setMonth6SmearDate(String month6SmearDate) {
		this.month6SmearDate = month6SmearDate;
	}
	
	public String getMonth6TestNumber() {
		return month6TestNumber;
	}
	
	public void setMonth6TestNumber(String month6TestNumber) {
		this.month6TestNumber = month6TestNumber;
	}
	
	public String getMonth6TestLab() {
		return month6TestLab;
	}
	
	public void setMonth6TestLab(String month6TestLab) {
		this.month6TestLab = month6TestLab;
	}
	
	public String getMonth8SmearResult() {
		return month8SmearResult;
	}
	
	public void setMonth8SmearResult(String month8SmearResult) {
		this.month8SmearResult = month8SmearResult;
	}
	
	public String getMonth8SmearDate() {
		return month8SmearDate;
	}
	
	public void setMonth8SmearDate(String month8SmearDate) {
		this.month8SmearDate = month8SmearDate;
	}
	
	public String getMonth8TestNumber() {
		return month8TestNumber;
	}
	
	public void setMonth8TestNumber(String month8TestNumber) {
		this.month8TestNumber = month8TestNumber;
	}
	
	public String getMonth8TestLab() {
		return month8TestLab;
	}
	
	public void setMonth8TestLab(String month8TestLab) {
		this.month8TestLab = month8TestLab;
	}
	
	public Integer getTb03TreatmentOutcome() {
		return tb03TreatmentOutcome;
	}
	
	public void setTb03TreatmentOutcome(Integer tb03TreatmentOutcome) {
		this.tb03TreatmentOutcome = tb03TreatmentOutcome;
	}
	
	public String getTb03TreatmentOutcomeDate() {
		return tb03TreatmentOutcomeDate;
	}
	
	public void setTb03TreatmentOutcomeDate(String tb03TreatmentOutcomeDate) {
		this.tb03TreatmentOutcomeDate = tb03TreatmentOutcomeDate;
	}
	
	public Boolean getDiedOfTB() {
		return diedOfTB;
	}
	
	public void setDiedOfTB(Boolean diedOfTB) {
		this.diedOfTB = diedOfTB;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Boolean getReg1New() {
		return reg1New;
	}
	
	public void setReg1New(Boolean reg1New) {
		this.reg1New = reg1New;
	}
	
	public Boolean getReg1Rtx() {
		return reg1Rtx;
	}
	
	public void setReg1Rtx(Boolean reg1Rtx) {
		this.reg1Rtx = reg1Rtx;
	}
	
	public String getDstCollectionDate() {
		return dstCollectionDate;
	}
	
	public void setDstCollectionDate(String dstCollectionDate) {
		this.dstCollectionDate = dstCollectionDate;
	}
	
	public String getDstResultDate() {
		return dstResultDate;
	}
	
	public void setDstResultDate(String dstResultDate) {
		this.dstResultDate = dstResultDate;
	}
	
	public Map<String, String> getDstResults() {
		return dstResults;
	}
	
	public void setDstResults(Map<String, String> dstResults) {
		this.dstResults = dstResults;
	}
}
