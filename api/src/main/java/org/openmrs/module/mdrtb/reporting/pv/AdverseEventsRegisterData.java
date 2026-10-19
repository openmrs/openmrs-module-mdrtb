package org.openmrs.module.mdrtb.reporting.pv;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

public class AdverseEventsRegisterData implements Comparable<AdverseEventsRegisterData> {
	
	private AdverseEventsForm aeForm;
	
	public AdverseEventsRegisterData(AdverseEventsForm aeForm) {
		this.aeForm = aeForm;
	}
	
	public Patient getPatient() {
		return aeForm.getPatient();
	}
	
	public String getIdentifier() {
		
		Integer patientProgramId = aeForm.getPatientProgramId();
		if (patientProgramId == null)
			return "";
		org.openmrs.PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		String id = null;
		if (pp != null) {
			PatientIdentifier pi = Context.getService(MdrtbService.class).getPatientProgramIdentifier(pp);
			id = pi == null ? null : pi.getIdentifier();
		}
		return id;
	}
	
	public AdverseEventsForm getAEForm() {
		return aeForm;
	}
	
	public void setAEForm(AdverseEventsForm aeForm) {
		this.aeForm = aeForm;
		
	}
	
	public int compareTo(AdverseEventsRegisterData aerd) {
		if (aerd.getAEForm() == null || getAEForm() == null)
			return 0;
		if (aerd.getAEForm().getEncounterDatetime() == null || this.getAEForm().getEncounterDatetime() == null)
			return 0;
		return this.getAEForm().getEncounterDatetime().compareTo(aerd.getAEForm().getEncounterDatetime());
	}
	
	public String getPatientName() {
		if (aeForm == null)
			return null;
		Patient p = aeForm.getPatient();
		String lastName = p.getFamilyName();
		String firstName = p.getGivenName();
		return lastName + ", " + firstName;
	}
	
	public String getBirthDate() {
		if (aeForm == null)
			return null;
		Patient p = aeForm.getPatient();
		Date dob = p.getBirthdate();
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(MdrtbConstants.RUSSIAN_DATE_FORMAT);
		return sdf.format(dob);
	}
	
	public String getOnsetDate() {
		if (aeForm == null)
			return null;
		Date od = aeForm.getEncounterDatetime();
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(MdrtbConstants.RUSSIAN_DATE_FORMAT);
		return sdf.format(od);
	}
	
	public String getAEDescription() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getAdverseEvent();
		if (q == null)
			return null;
		return q.getName().getName();
	}
	
	public String getDiagnosticInvestigation() {
		if (aeForm == null)
			return null;
		return aeForm.getDiagnosticSummary();
	}
	
	public String getSerious() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getTypeOfEvent();
		if (q == null)
			return null;
		if (q.getId() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SERIOUS).getId()) {
			return Context.getService(MessagePropertyService.class).getMessage(MdrtbConstants.MESSAGE_CODE_YES);
		} else {
			return Context.getService(MessagePropertyService.class).getMessage(MdrtbConstants.MESSAGE_CODE_NO);
		}
	}
	
	public String getOfSpecialInterest() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getTypeOfEvent();
		if (q == null)
			return null;
		if (q.getId() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_SPECIAL_INTEREST).getId()) {
			return Context.getService(MessagePropertyService.class).getMessage(MdrtbConstants.MESSAGE_CODE_YES);
		} else {
			return Context.getService(MessagePropertyService.class).getMessage(MdrtbConstants.MESSAGE_CODE_NO);
		}
	}
	
	public String getAncillaryDrugs() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getRequiresAncillaryDrugs();
		if (q == null)
			return null;
		return q.getName().getName();
	}
	
	public String getDoseChanged() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getRequiresDoseChange();
		if (q == null)
			return null;
		return q.getName().getName();
	}
	
	public String getSuspectedDrug() {
		String ret = "";
		if (aeForm == null)
			return null;
		Concept q = aeForm.getCausalityDrug1();
		if (q == null) {
			return null;
		}
		ret += q.getName().getName();
		q = aeForm.getCausalityDrug2();
		if (q != null) {
			ret += "/" + q.getName().getName();
			q = aeForm.getCausalityDrug3();
			if (q != null) {
				ret += "/" + q.getName().getName();
			}
		}
		return ret;
	}
	
	public String getSuspectedDrugStartDate() {
		String ret = "";
		if (aeForm == null)
			return null;
		
		Concept q = aeForm.getCausalityDrug1();
		if (q == null) {
			return null;
		}
		
		ret += q.getName().getName();
		
		q = aeForm.getCausalityDrug2();
		
		if (q != null) {
			ret += "/" + q.getName().getName();
			
			q = aeForm.getCausalityDrug3();
			
			if (q != null) {
				ret += "/" + q.getName().getName();
			}
		}
		return ret;
	}
	
	public String getActionOutcome() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getActionOutcome();
		if (q == null) {
			return null;
		}
		return q.getName().getName();
	}
	
	public String getActionTaken() {
		String at = "";
		if (aeForm == null)
			return null;
		Concept q = aeForm.getActionTaken();
		if (q != null) {
			at = q.getName().getName();
		}
		q = aeForm.getActionTaken2();
		if (q != null) {
			at += ", " + q.getName().getName();
		}
		q = aeForm.getActionTaken3();
		if (q != null) {
			at += ", " + q.getName().getName();
		}
		q = aeForm.getActionTaken4();
		if (q != null) {
			at += ", " + q.getName().getName();
		}
		return at;
	}
	
	public String getTxRegimen() {
		if (aeForm == null)
			return null;
		return aeForm.getTreatmentRegimenAtOnset();
	}
	
	public String getDrugRechallenge() {
		if (aeForm == null)
			return null;
		Concept q = aeForm.getDrugRechallenge();
		if (q == null) {
			return null;
		}
		return q.getName().getName();
	}
	
	public String getCausalityAssessment() {
		String ret = "";
		if (aeForm == null)
			return null;
		Concept q = aeForm.getCausalityAssessmentResult1();
		if (q == null) {
			return null;
		}
		ret += q.getName().getName();
		q = aeForm.getCausalityAssessmentResult2();
		if (q != null) {
			ret += "/" + q.getName().getName();
			
			q = aeForm.getCausalityAssessmentResult3();
			
			if (q != null) {
				ret += "/" + q.getName().getName();
			}
		}
		return ret;
	}
	
	public String getYellowCardDate() {
		if (aeForm == null)
			return null;
		Date ycd = aeForm.getYellowCardDate();
		if (ycd == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(MdrtbConstants.RUSSIAN_DATE_FORMAT);
		return sdf.format(ycd);
		
	}
	
	public String getComments() {
		if (aeForm == null)
			return null;
		return aeForm.getComments();
	}
}
