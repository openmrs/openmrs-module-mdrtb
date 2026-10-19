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

import java.util.Objects;

public class HAINForm extends AbstractSimpleForm implements Comparable<HAINForm> {
	
	private static final String VOID_MESSAGE = "voided by MDRTB module";
	
	private LabTest labTest;
	
	public HAINForm() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_SPECIMEN_COLLECTION);
	}
	
	public HAINForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_SPECIMEN_COLLECTION);
	}
	
	public HAINForm(Encounter encounter) {
		super(encounter);
	}
	
	public HAINForm(Encounter encounter, LabTest labTest) {
		super(encounter);
		this.setLabTest(labTest);
	}
	
	public String getSpecimenId() {
		if (getLabTest() != null) {
			LabTestSample sample = Context.getService(LabTestService.class).getMostRecentAcceptedSample(getLabTest());
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
				obs.setVoidReason(VOID_MESSAGE);
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
		if (getLabTest() != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getHainAttributeByTestAndName(
			    getLabTest(), MdrtbConcepts.MTB_RESULT);
			if (attribute != null) {
				return (Concept) attribute.getValue();
			}
		}
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null)
			obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			    obsgroup);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setMtbResult(Concept result) {
		log.debug("result" + result);
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null) {
			obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			    obsgroup);
		} else {
			obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.HAIN_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
		}
		
		// if this obs have not been created, and there is no data to add, do nothing
		log.debug("Obs Group:" + obsgroup);
		if (obs == null && result == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(result)) {
			log.debug("new obs or value change");
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				log.debug("not null obs");
				//				obsgroup.setVoided(true);
				//				obsgroup.setVoidReason("voided by MDRTB module");
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			// now create the new Obs and add it to the encounter	
			if (result != null) {
				log.debug("creating new obs");
				//obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.MTB_RESULT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(result);
				obs.setObsGroup(obsgroup);
				obsgroup.addGroupMember(obs);
				encounter.addObs(obs);
				encounter.addObs(obsgroup);
				//encounter.
			}
		}
	}
	
	public Concept getRifResult() {
		if (getLabTest() != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getHainAttributeByTestAndName(
			    getLabTest(), MdrtbConcepts.RIFAMPICIN_RESULT);
			if (attribute != null) {
				return (Concept) attribute.getValue();
			}
		}
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null)
			obs = MdrtbUtil.getObsFromObsGroup(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESULT), obsgroup);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setRifResult(Concept result) {
		log.debug("result" + result);
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null) {
			obs = MdrtbUtil.getObsFromObsGroup(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESULT), obsgroup);
		} else {
			obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.HAIN_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
		}
		
		// if this obs have not been created, and there is no data to add, do nothing
		log.debug("Obs Group:" + obsgroup);
		if (obs == null && result == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(result)) {
			log.debug("new obs or value change");
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				log.debug("not null obs");
				//				obsgroup.setVoided(true);
				//				obsgroup.setVoidReason("voided by MDRTB module");
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			// now create the new Obs and add it to the encounter	
			if (result != null) {
				log.debug("creating new obs");
				//obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
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
	
	public Concept getInhResult() {
		if (getLabTest() != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(
			    getLabTest(), MdrtbConcepts.ISONIAZID_RESULT);
			if (attribute != null) {
				return (Concept) attribute.getValue();
			}
		}
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null)
			obs = MdrtbUtil.getObsFromObsGroup(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID_RESULT), obsgroup);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setInhResult(Concept result) {
		log.debug("result" + result);
		Obs obsgroup = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter);
		Obs obs = null;
		
		if (obsgroup != null) {
			obs = MdrtbUtil.getObsFromObsGroup(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID_RESULT), obsgroup);
			
		}
		
		else {
			obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.HAIN_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
		}
		
		// if this obs have not been created, and there is no data to add, do nothing
		log.debug("Obs Group:" + obsgroup);
		if (obs == null && result == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(result)) {
			log.debug("new obs or value change");
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				log.debug("not null obs");
				//				obsgroup.setVoided(true);
				//				obsgroup.setVoidReason("voided by MDRTB module");
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			// now create the new Obs and add it to the encounter	
			if (result != null) {
				log.debug("creating new obs");
				//obsgroup = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.ISONIAZID_RESULT), encounter.getEncounterDatetime(), encounter.getLocation());
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
		if (getLabTest() != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(
			    getLabTest(), MdrtbConcepts.PATIENT_PROGRAM_ID);
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
				obs.setVoidReason(VOID_MESSAGE);
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
		if (getLabTest() != null) {
			LabTestAttribute attribute = Context.getService(LabTestService.class).getCommonAttributeByTestAndName(
			    getLabTest(), MdrtbConcepts.MONTH_OF_TREATMENT);
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
				obs.setVoidReason(VOID_MESSAGE);
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
	
	public int compareTo(HAINForm form) {
		if (this.getMonthOfTreatment() == null)
			return 1;
		if (form.getMonthOfTreatment() == null)
			return -1;
		return this.getMonthOfTreatment().compareTo(form.getMonthOfTreatment());
	}
	
	public String getLink() {
		return "/module/mdrtb/form/hain.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public LabTest getLabTest() {
		return labTest;
	}
	
	public void setLabTest(LabTest labTest) {
		this.labTest = labTest;
	}
}
