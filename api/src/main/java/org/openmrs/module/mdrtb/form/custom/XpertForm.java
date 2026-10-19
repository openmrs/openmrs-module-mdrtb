package org.openmrs.module.mdrtb.form.custom;

import org.openmrs.Concept;
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
import org.openmrs.module.mdrtb.api.MessagePropertyService;

import java.util.Objects;

public class XpertForm extends AbstractSimpleForm implements Comparable<XpertForm> {
	
	private LabTest labTest;
	
	public XpertForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_SPECIMEN_COLLECTION);
	}
	
	public XpertForm(Encounter encounter) {
		super(encounter);
		this.encounter.setEncounterType(MdrtbConstants.ET_SPECIMEN_COLLECTION);
	}
	
	public XpertForm(Encounter encounter, LabTest labTest) {
		super(encounter);
		this.setLabTest(labTest);
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
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
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
				obs.setVoidReason("voided by MDRTB module");
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
	
	public Concept getMtbResult() {
		if (labTest != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(labTest,
			    MdrtbConcepts.MTB_RESULT);
			if (attribute != null) {
				return (Concept) attribute.getValue();
			}
		}
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null)
			obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			    obsgroup);
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setMtbResult(Concept result) {
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null) {
			obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			    obsgroup);
		} else {
			obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
		}
		
		if (obs == null && result == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(result)) {
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by MDRTB module");
			}
			
			// now create the new Obs and add it to the encounter	
			if (result != null) {
				//obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.MTB_RESULT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(result);
				obs.setObsGroup(obsgroup);
				obsgroup.addGroupMember(obs);
				encounter.addObs(obs);
				encounter.addObs(obsgroup);
			}
		}
	}
	
	public Concept getRifResult() {
		if (labTest != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(labTest,
			    MdrtbConcepts.RIFAMPICIN_RESULT);
			if (attribute != null) {
				return (Concept) attribute.getValue();
			}
		}
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null)
			obs = MdrtbUtil.getObsFromObsGroup(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESULT), obsgroup);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setRifResult(Concept result) {
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null) {
			obs = MdrtbUtil.getObsFromObsGroup(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESULT), obsgroup);
		} else {
			obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
		}
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && result == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(result)) {
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by MDRTB module");
			}
			
			// now create the new Obs and add it to the encounter	
			if (result != null) {
				//obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.RIFAMPICIN_RESULT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(result);
				obs.setObsGroup(obsgroup);
				obsgroup.addGroupMember(obs);
				encounter.addObs(obs);
				encounter.addObs(obsgroup);
				//encounter.
			}
		}
	}
	
	public Integer getPatientProgramId() {
		if (labTest != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(labTest,
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
				obs.setVoidReason("voided by MDRTB module");
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
				obs.setVoidReason("voided by MDRTB module");
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
	
	public String getResultString() {
		String ret = "";
		
		Concept mtbResult = getMtbResult();
		
		if (mtbResult == null) {
			return ret;
		}
		
		Concept rifResult = getRifResult();
		
		if (mtbResult.getId().intValue() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_POSITIVE)
		        .getId().intValue()) {
			ret = ret + Context.getService(MessagePropertyService.class).getMessage("mdrtb.positiveShort");
			
			if (rifResult != null
			        && rifResult.getId().intValue() == Context.getService(MdrtbService.class)
			                .getConcept(MdrtbConcepts.DETECTED).getId().intValue()) {
				ret += "/" + Context.getService(MessagePropertyService.class).getMessage("mdrtb.resistantShort");
			}
			
			else if (rifResult != null
			        && rifResult.getId().intValue() == Context.getService(MdrtbService.class)
			                .getConcept(MdrtbConcepts.NOT_DETECTED).getId().intValue()) {
				ret += "/" + Context.getService(MessagePropertyService.class).getMessage("mdrtb.sensitiveShort");
			}
			
			else if (rifResult != null
			        && rifResult.getId().intValue() == Context.getService(MdrtbService.class)
			                .getConcept(MdrtbConcepts.UNDETERMINED).getId().intValue()) {
				ret += "/" + Context.getService(MessagePropertyService.class).getMessage("mdrtb.indeterminateShort");
			}
		}
		
		else if (mtbResult.getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getId().intValue()) {
			ret = ret + Context.getService(MessagePropertyService.class).getMessage("mdrtb.negativeShort");
		}
		
		else if (mtbResult.getId().intValue() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR)
		        .getId().intValue()) {
			ret = ret + Context.getService(MessagePropertyService.class).getMessage("mdrtb.error");
		}
		return ret;
	}
	
	public int compareTo(XpertForm form) {
		if (this.getMonthOfTreatment() == null)
			return 1;
		if (form.getMonthOfTreatment() == null)
			return -1;
		return this.getMonthOfTreatment().compareTo(form.getMonthOfTreatment());
	}
	
	public String getLink() {
		return "/module/mdrtb/form/xpert.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public LabTest getLabTest() {
		return labTest;
	}
	
	public void setLabTest(LabTest labTest) {
		this.labTest = labTest;
	}
}
