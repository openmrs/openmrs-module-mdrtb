/**
 * 
 */
package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.pv.AdverseEventsRegisterData;

/**
 * 
 */
public class SimpleAdverseEventsRegisterData extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 264292209987810577L;
	
	private String patientUuid;
	
	private String patientIdentifier;
	
	private String patientName;
	
	private String birthdate;
	
	private String onsetDate;
	
	private String aeDescription;
	
	private String diagnosticInvestigation;
	
	private String serious;
	
	private String ofSpecialInterest;
	
	private String ancillaryDrugs;
	
	private String doseChanged;
	
	private String suspectedDrug;
	
	private String suspectedDrugStartDate;
	
	private String actionOutcome;
	
	private String actionTaken;
	
	private String txRegimen;
	
	private String drugRechallenge;
	
	private String yellowCardDate;
	
	private String comments;
	
	private SimpleAdverseEventsForm adverseEventsForm;
	
	public SimpleAdverseEventsRegisterData(AdverseEventsRegisterData registerData) {
		setPatientUuid(registerData.getPatient().getUuid());
		setPatientIdentifier(registerData.getIdentifier());
		setPatientName(registerData.getPatientName());
		setBirthdate(registerData.getBirthDate());
		setOnsetDate(registerData.getOnsetDate());
		setAeDescription(registerData.getAEDescription());
		setDiagnosticInvestigation(registerData.getDiagnosticInvestigation());
		setSerious(registerData.getSerious());
		setOfSpecialInterest(registerData.getOfSpecialInterest());
		setAncillaryDrugs(registerData.getAncillaryDrugs());
		setDoseChanged(registerData.getDoseChanged());
		setSuspectedDrug(registerData.getSuspectedDrug());
		setSuspectedDrugStartDate(registerData.getSuspectedDrugStartDate());
		setActionOutcome(registerData.getActionOutcome());
		setActionTaken(registerData.getActionTaken());
		setTxRegimen(registerData.getTxRegimen());
		setDrugRechallenge(registerData.getDrugRechallenge());
		setYellowCardDate(registerData.getYellowCardDate());
		setComments(registerData.getComments());
		adverseEventsForm = new SimpleAdverseEventsForm(registerData.getAEForm());
	}
	
	@Override
	public Integer getId() {
		return null;
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
	
	public String getPatientIdentifier() {
		return patientIdentifier;
	}
	
	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}
	
	public String getPatientName() {
		return patientName;
	}
	
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public String getBirthdate() {
		return birthdate;
	}
	
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	
	public String getOnsetDate() {
		return onsetDate;
	}
	
	public void setOnsetDate(String onsetDate) {
		this.onsetDate = onsetDate;
	}
	
	public String getAeDescription() {
		return aeDescription;
	}
	
	public void setAeDescription(String aeDescription) {
		this.aeDescription = aeDescription;
	}
	
	public String getDiagnosticInvestigation() {
		return diagnosticInvestigation;
	}
	
	public void setDiagnosticInvestigation(String diagnosticInvestigation) {
		this.diagnosticInvestigation = diagnosticInvestigation;
	}
	
	public String getSerious() {
		return serious;
	}
	
	public void setSerious(String serious) {
		this.serious = serious;
	}
	
	public String getOfSpecialInterest() {
		return ofSpecialInterest;
	}
	
	public void setOfSpecialInterest(String ofSpecialInterest) {
		this.ofSpecialInterest = ofSpecialInterest;
	}
	
	public String getAncillaryDrugs() {
		return ancillaryDrugs;
	}
	
	public void setAncillaryDrugs(String ancillaryDrugs) {
		this.ancillaryDrugs = ancillaryDrugs;
	}
	
	public String getDoseChanged() {
		return doseChanged;
	}
	
	public void setDoseChanged(String doseChanged) {
		this.doseChanged = doseChanged;
	}
	
	public String getSuspectedDrug() {
		return suspectedDrug;
	}
	
	public void setSuspectedDrug(String suspectedDrug) {
		this.suspectedDrug = suspectedDrug;
	}
	
	public String getSuspectedDrugStartDate() {
		return suspectedDrugStartDate;
	}
	
	public void setSuspectedDrugStartDate(String suspectedDrugStartDate) {
		this.suspectedDrugStartDate = suspectedDrugStartDate;
	}
	
	public String getActionOutcome() {
		return actionOutcome;
	}
	
	public void setActionOutcome(String actionOutcome) {
		this.actionOutcome = actionOutcome;
	}
	
	public String getActionTaken() {
		return actionTaken;
	}
	
	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}
	
	public String getTxRegimen() {
		return txRegimen;
	}
	
	public void setTxRegimen(String txRegimen) {
		this.txRegimen = txRegimen;
	}
	
	public String getDrugRechallenge() {
		return drugRechallenge;
	}
	
	public void setDrugRechallenge(String drugRechallenge) {
		this.drugRechallenge = drugRechallenge;
	}
	
	public String getYellowCardDate() {
		return yellowCardDate;
	}
	
	public void setYellowCardDate(String yellowCardDate) {
		this.yellowCardDate = yellowCardDate;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public SimpleAdverseEventsForm getAdverseEventsForm() {
		return adverseEventsForm;
	}
	
	public void setAdverseEventsForm(SimpleAdverseEventsForm adverseEventsForm) {
		this.adverseEventsForm = adverseEventsForm;
	}
}
