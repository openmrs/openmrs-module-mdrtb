package org.openmrs.module.mdrtb.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.pv.YellowCardData;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 * REST representation of one Yellow Card. All mapping rules live in {@link YellowCardData}; this class
 * only copies values so that the REST layer can serialize them.
 */
public class SimpleYellowCardData extends BaseOpenmrsData {

	private static final long serialVersionUID = 1L;

	private String adverseEventsFormUuid;

	private Integer causalityIndex;

	private String patientUuid;

	private String patientName;

	private String address;

	private String openmrsIdentifier;

	private String dateOfBirth;

	private String gender;

	private String outcomeCode;

	private String outcomeName;

	private String regimenTypeCode;

	private String regimenTypeName;

	private String diagnosticSummary;

	private String clinicianNotes;

	private String suspectedDrugName;

	private String suspectedDrugsText;

	private String onsetDate;

	private String cessationDate;

	private String adverseEventName;

	private String saeTypeCode;

	private String saeTypeName;

	private String aesiTypeCode;

	private String aesiTypeName;

	private String regimenDate;

	/** Each item: {name, doseLabelCode, dose} */
	private List<SimpleObject> primaryDiseaseMedicines = new ArrayList<>();

	private Boolean rechallengeReproducedReaction;

	private String causalityCode;

	private String causalityName;

	private String reporterName;

	private String reporterPlaceOfWork;

	private String reportDate;

	private List<String> warnings = new ArrayList<>();

	public SimpleYellowCardData() {
	}

	public SimpleYellowCardData(YellowCardData data) {
		adverseEventsFormUuid = data.getAdverseEventsFormUuid();
		causalityIndex = data.getCausalityIndex();
		patientUuid = data.getPatientUuid();
		patientName = data.getPatientName();
		address = data.getAddress();
		openmrsIdentifier = data.getOpenmrsIdentifier();
		dateOfBirth = data.getDateOfBirth();
		gender = data.getGender();
		outcomeCode = data.getOutcomeCode();
		outcomeName = data.getOutcomeName();
		regimenTypeCode = data.getRegimenTypeCode();
		regimenTypeName = data.getRegimenTypeName();
		diagnosticSummary = data.getDiagnosticSummary();
		clinicianNotes = data.getClinicianNotes();
		suspectedDrugName = data.getSuspectedDrugName();
		suspectedDrugsText = data.getSuspectedDrugsText();
		onsetDate = data.getOnsetDate();
		cessationDate = data.getCessationDate();
		adverseEventName = data.getAdverseEventName();
		saeTypeCode = data.getSaeTypeCode();
		saeTypeName = data.getSaeTypeName();
		aesiTypeCode = data.getAesiTypeCode();
		aesiTypeName = data.getAesiTypeName();
		regimenDate = data.getRegimenDate();
		for (YellowCardData.Medicine medicine : data.getPrimaryDiseaseMedicines()) {
			SimpleObject row = new SimpleObject();
			row.add("name", medicine.getName());
			row.add("doseLabelCode", medicine.getDoseLabelCode());
			row.add("dose", medicine.getDose());
			primaryDiseaseMedicines.add(row);
		}
		rechallengeReproducedReaction = data.getRechallengeReproducedReaction();
		causalityCode = data.getCausalityCode();
		causalityName = data.getCausalityName();
		reporterName = data.getReporterName();
		reporterPlaceOfWork = data.getReporterPlaceOfWork();
		reportDate = data.getReportDate();
		warnings = new ArrayList<>(data.getWarnings());
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {
	}

	public String getAdverseEventsFormUuid() {
		return adverseEventsFormUuid;
	}

	public void setAdverseEventsFormUuid(String adverseEventsFormUuid) {
		this.adverseEventsFormUuid = adverseEventsFormUuid;
	}

	public Integer getCausalityIndex() {
		return causalityIndex;
	}

	public void setCausalityIndex(Integer causalityIndex) {
		this.causalityIndex = causalityIndex;
	}

	public String getPatientUuid() {
		return patientUuid;
	}

	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOpenmrsIdentifier() {
		return openmrsIdentifier;
	}

	public void setOpenmrsIdentifier(String openmrsIdentifier) {
		this.openmrsIdentifier = openmrsIdentifier;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getOutcomeCode() {
		return outcomeCode;
	}

	public void setOutcomeCode(String outcomeCode) {
		this.outcomeCode = outcomeCode;
	}

	public String getOutcomeName() {
		return outcomeName;
	}

	public void setOutcomeName(String outcomeName) {
		this.outcomeName = outcomeName;
	}

	public String getRegimenTypeCode() {
		return regimenTypeCode;
	}

	public void setRegimenTypeCode(String regimenTypeCode) {
		this.regimenTypeCode = regimenTypeCode;
	}

	public String getRegimenTypeName() {
		return regimenTypeName;
	}

	public void setRegimenTypeName(String regimenTypeName) {
		this.regimenTypeName = regimenTypeName;
	}

	public String getDiagnosticSummary() {
		return diagnosticSummary;
	}

	public void setDiagnosticSummary(String diagnosticSummary) {
		this.diagnosticSummary = diagnosticSummary;
	}

	public String getClinicianNotes() {
		return clinicianNotes;
	}

	public void setClinicianNotes(String clinicianNotes) {
		this.clinicianNotes = clinicianNotes;
	}

	public String getSuspectedDrugName() {
		return suspectedDrugName;
	}

	public void setSuspectedDrugName(String suspectedDrugName) {
		this.suspectedDrugName = suspectedDrugName;
	}

	public String getSuspectedDrugsText() {
		return suspectedDrugsText;
	}

	public void setSuspectedDrugsText(String suspectedDrugsText) {
		this.suspectedDrugsText = suspectedDrugsText;
	}

	public String getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(String onsetDate) {
		this.onsetDate = onsetDate;
	}

	public String getCessationDate() {
		return cessationDate;
	}

	public void setCessationDate(String cessationDate) {
		this.cessationDate = cessationDate;
	}

	public String getAdverseEventName() {
		return adverseEventName;
	}

	public void setAdverseEventName(String adverseEventName) {
		this.adverseEventName = adverseEventName;
	}

	public String getSaeTypeCode() {
		return saeTypeCode;
	}

	public void setSaeTypeCode(String saeTypeCode) {
		this.saeTypeCode = saeTypeCode;
	}

	public String getSaeTypeName() {
		return saeTypeName;
	}

	public void setSaeTypeName(String saeTypeName) {
		this.saeTypeName = saeTypeName;
	}

	public String getAesiTypeCode() {
		return aesiTypeCode;
	}

	public void setAesiTypeCode(String aesiTypeCode) {
		this.aesiTypeCode = aesiTypeCode;
	}

	public String getAesiTypeName() {
		return aesiTypeName;
	}

	public void setAesiTypeName(String aesiTypeName) {
		this.aesiTypeName = aesiTypeName;
	}

	public String getRegimenDate() {
		return regimenDate;
	}

	public void setRegimenDate(String regimenDate) {
		this.regimenDate = regimenDate;
	}

	public List<SimpleObject> getPrimaryDiseaseMedicines() {
		return primaryDiseaseMedicines;
	}

	public void setPrimaryDiseaseMedicines(List<SimpleObject> primaryDiseaseMedicines) {
		this.primaryDiseaseMedicines = primaryDiseaseMedicines;
	}

	public Boolean getRechallengeReproducedReaction() {
		return rechallengeReproducedReaction;
	}

	public void setRechallengeReproducedReaction(Boolean rechallengeReproducedReaction) {
		this.rechallengeReproducedReaction = rechallengeReproducedReaction;
	}

	public String getCausalityCode() {
		return causalityCode;
	}

	public void setCausalityCode(String causalityCode) {
		this.causalityCode = causalityCode;
	}

	public String getCausalityName() {
		return causalityName;
	}

	public void setCausalityName(String causalityName) {
		this.causalityName = causalityName;
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public String getReporterPlaceOfWork() {
		return reporterPlaceOfWork;
	}

	public void setReporterPlaceOfWork(String reporterPlaceOfWork) {
		this.reporterPlaceOfWork = reporterPlaceOfWork;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
}
