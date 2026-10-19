package org.openmrs.module.mdrtb.form.custom;

import java.util.*;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.DstResult;

public class DSTForm extends AbstractSimpleForm implements Comparable<DSTForm> {
	
	private static final String VOID_REASON = "voided by MDRTB module";
	
	public DstImpl di;
	
	private LabTest labTest;
	
	public DSTForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		LabTest dst = Context.getService(LabTestService.class).getDstLabTestOrder(this.encounter);
		di = new DstImpl(dst);
	}
	
	public DSTForm(Encounter encounter) {
		super(encounter);
		LabTest dst = Context.getService(LabTestService.class).getDstLabTestOrder(this.encounter);
		if (dst == null) {
			dst = Context.getService(LabTestService.class).createDstLabTestOrder(this.encounter);
		}
		di = new DstImpl(dst);
	}
	
	public DSTForm(Encounter encounter, LabTest labTest) {
		super(encounter);
		this.setLabTest(labTest);
		// The DST results live on the DST MGIT / DST LJ lab test for this encounter, not on the
		// COMMON TEST lab test passed here -- that one only supplies the shared specimen fields
		// (specimen id, program, month of treatment). Unlike DSTForm(Encounter) this never creates
		// an order, because a read path must not write. di stays null when the encounter has no DST
		// order, and the result accessors below tolerate that instead of throwing.
		LabTest dst = Context.getService(LabTestService.class).getDstLabTestOrder(encounter);
		if (dst != null) {
			di = new DstImpl(dst);
		}
	}
	
	public DstResult addResult() {
		return di.addResult();
	}
	
	public List<DstResult> getResults() {
		return di == null ? new ArrayList<>() : di.getResults();
	}
	
	@Deprecated
	public Map<Integer, List<DstResult>> getResultsMap() {
		return di == null ? new HashMap<Integer, List<DstResult>>() : di.getResultsMap();
	}
	
	public List<DstResult> getResultsList() {
		return di == null ? new ArrayList<>() : di.getResults();
	}
	
	public void removeResult(DstResult result) {
		di.removeResult(result);
	}
	
	public DstImpl getDi() {
		return di;
	}
	
	public void setDi(DstImpl di) {
		this.di = di;
	}
	
	public String getSpecimenId() {
		if (labTest != null) {
			LabTestSample sample = Context.getService(LabTestService.class).getMostRecentAcceptedSample(labTest);
			// A lab test can exist before any of its samples has been accepted, so the sample may be null
			if (sample != null && sample.getLabTestSampleId() != null) {
				return sample.getLabTestSampleId().toString();
			}
			// No accepted sample yet: fall back to the specimen ID recorded on the encounter
		}
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setSpecimenId(String id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !Objects.equals(obs.getValueText(), id)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.SPECIMEN_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(id);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getPatientProgramId() {
		if (labTest != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getCommonAttributeByTestAndName(labTest,
			    MdrtbConcepts.PATIENT_PROGRAM_ID);
			if (attribute != null) {
				try {
					return (Integer) attribute.getValue();
				}
				catch (ClassCastException e) {
					return Integer.parseInt(attribute.getValueReference());
				}
			}
		}
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public void setPatientProgramId(Integer id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || obs.getValueNumeric().intValue() != id) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_PROGRAM_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(id.doubleValue());
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getMonthOfTreatment() {
		if (labTest != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getCommonAttributeByTestAndName(labTest,
			    MdrtbConcepts.MONTH_OF_TREATMENT);
			if (attribute != null) {
				try {
					return (Integer) attribute.getValue();
				}
				catch (ClassCastException e) {
					return Integer.parseInt(attribute.getValueReference());
				}
			}
		}
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONTH_OF_TREATMENT), encounter);
		
		if (obs == null) {
			return 0;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public void setMonthOfTreatment(Integer month) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONTH_OF_TREATMENT), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && month == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || obs.getValueNumeric().intValue() != month) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (month != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.MONTH_OF_TREATMENT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(month.doubleValue());
				encounter.addObs(obs);
			}
		}
	}
	
	public int compareTo(DSTForm form) {
		if (this.getMonthOfTreatment() == null)
			return 1;
		if (form.getMonthOfTreatment() == null)
			return -1;
		return this.getMonthOfTreatment().compareTo(form.getMonthOfTreatment());
	}
	
	public String getLink() {
		return "/module/mdrtb/form/dst.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public LabTest getLabTest() {
		return labTest;
	}
	
	public void setLabTest(LabTest labTest) {
		this.labTest = labTest;
	}
}
