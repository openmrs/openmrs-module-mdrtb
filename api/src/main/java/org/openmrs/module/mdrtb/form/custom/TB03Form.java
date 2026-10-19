package org.openmrs.module.mdrtb.form.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

public class TB03Form extends AbstractSimpleForm implements Comparable<TB03Form> {
	
	private static final String VOID_REASON = "voided by MDRTB module";
	
	private String regNum = null;
	
	public TB03Form() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_TB03_TB_INTAKE);
		
	}
	
	public TB03Form(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_TB03_TB_INTAKE);
	}
	
	public TB03Form(Encounter encounter) {
		super(encounter);
	}
	
	public List<SmearForm> getSmears() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getSmearForms(getPatientProgramId());
		
	}
	
	public List<CultureForm> getCultures() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getCultureForms(getPatientProgramId());
		
	}
	
	public List<XpertForm> getXperts() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getXpertForms(getPatientProgramId());
		
	}
	
	public List<HAINForm> getHains() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getHAINForms(getPatientProgramId());
		
	}
	
	public List<HAIN2Form> getHain2s() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getHAIN2Forms(getPatientProgramId());
		
	}
	
	public List<DSTForm> getDsts() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getDstForms(getPatientProgramId());
		
	}
	
	public String getPatientName() {
		PersonName p = getPatient().getPersonName();
		return p.getFamilyName() + "," + p.getGivenName();
	}
	
	public Concept getAnatomicalSite() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter);
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setAnatomicalSite(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getAgeAtTB03Registration() {
		/*Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AGE_AT_DOTS_REGISTRATION), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueNumeric().intValue();
		}*/
		return MdrtbUtil.calculateAge(getPatient().getBirthdate(), getEncounterDatetime());
		
	}
	
	public String getGender() {
		if (encounter.getPatient().getGender().equals("M"))
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.tb03.gender.male");
		if (encounter.getPatient().getGender().equals("F"))
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.tb03.gender.female");
		return "";
	}
	
	public Date getDateOfBirth() {
		log.debug("DOB:" + encounter.getPatient().getBirthdate());
		return encounter.getPatient().getBirthdate();
	}
	
	public String getAddress() {
		PersonAddress pa = encounter.getPatient().getPersonAddress();
		if (pa == null) {
			return null;
		}
		String address = pa.getCountry() + "," + pa.getStateProvince() + "," + pa.getCountyDistrict();
		if (pa.getAddress1() != null && pa.getAddress1().length() != 0) {
			address += "," + pa.getAddress1();
			if (pa.getAddress2() != null && pa.getAddress2().length() != 0)
				address += "," + pa.getAddress2();
		}
		
		return address;
	}
	
	public Concept getTreatmentSiteIP() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setTreatmentSiteIP(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.TREATMENT_CENTER_FOR_IP), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getTreatmentSiteCP() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setTreatmentSiteCP(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.TREATMENT_CENTER_FOR_CP), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPatientCategory() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPatientCategory(Concept cat) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cat == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(cat)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (cat != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(cat);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getTreatmentStartDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DOTS_TREATMENT_START_DATE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setTreatmentStartDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DOTS_TREATMENT_START_DATE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DOTS_TREATMENT_START_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getRegistrationGroup() {
		if (getPatientProgramId() == null) {
			return null;
		}
		Integer programId = getPatientProgramId();
		ProgramWorkflowState pws = null;
		try {
			pws = Context.getService(MdrtbService.class).getTbPatientProgram(programId)
			        .getClassificationAccordingToPatientGroups();
		}
		catch (Exception ignored) {}
		if (pws == null)
			return null;
		return pws.getConcept();
	}
	
	public void setRegistrationGroup(Concept group) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && group == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(group)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (group != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_GROUP), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(group);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getRegistrationGroupByDrug() {
		Integer patientProgramId = getPatientProgramId();
		if (patientProgramId != null) {
			TbPatientProgram tp = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
			ProgramWorkflowState pws = tp.getClassificationAccordingToPreviousDrugUse();
			if (pws != null)
				return pws.getConcept();
			else
				return null;
		}
		
		else {
			return null;
		}
	}
	
	public void setRegistrationGroupByDrug(Concept group) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(
		        MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && group == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(group)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (group != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE), encounter.getEncounterDatetime(),
				        encounter.getLocation());
				obs.setValueCoded(group);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getHivStatus() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setHivStatus(Concept status) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && status == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(status)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (status != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.RESULT_OF_HIV_TEST), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(status);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getArtStartDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_ART_TREATMENT_START), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setArtStartDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_ART_TREATMENT_START), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_ART_TREATMENT_START), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getPctStartDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_PCT_TREATMENT_START), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setPctStartDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_PCT_TREATMENT_START), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_PCT_TREATMENT_START), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getXrayDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XRAY_DATE),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setXrayDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XRAY_DATE),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.XRAY_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getResistanceType() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setResistanceType(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && type == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.RESISTANCE_TYPE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getCauseOfDeath() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCauseOfDeath(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && type == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CAUSE_OF_DEATH), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getTreatmentOutcome() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setTreatmentOutcome(Concept outcome) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && outcome == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(outcome)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (outcome != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.TB_TREATMENT_OUTCOME), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(outcome);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getTreatmentOutcomeDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_OUTCOME_DATE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setTreatmentOutcomeDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_OUTCOME_DATE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.TREATMENT_OUTCOME_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getDateOfDeathAfterOutcome() {
		//Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME), encounter);
		List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(
		    Context.getPersonService().getPerson(getPatient().getId()),
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME));
		Obs obs = null;
		
		if (obsList != null && !obsList.isEmpty())
			obs = obsList.get(0);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setDateOfDeathAfterOutcome(Date date) {
		/*Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME), encounter);*/
		List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(
		    Context.getPersonService().getPerson(getPatient().getId()),
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME));
		
		Obs obs = null;
		
		if (obsList != null && !obsList.isEmpty())
			obs = obsList.get(0);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME), encounter.getEncounterDatetime(),
				        encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getCliniciansNotes() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
	}
	
	public void setCliniciansNotes(String notes) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && notes == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !Objects.equals(obs.getValueText(), notes)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (notes != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CLINICIAN_NOTES), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(notes);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getPatientProgramId() {
		Concept concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs obs = MdrtbUtil.getObsFromEncounter(concept, encounter);
		return obs == null ? null : obs.getValueNumeric().intValue();
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
			// void the existing obs if it exists, because openmrs doesn't void obs when saved via encounters
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
	
	public Date getHivTestDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_HIV_TEST), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setHivTestDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_HIV_TEST), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_HIV_TEST), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getNameOfIPFacility() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_IP_FACILITY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
		
	}
	
	public void setNameOfIPFacility(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_IP_FACILITY), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.NAME_OF_IP_FACILITY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getNameOfCPFacility() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_CP_FACILITY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
		
	}
	
	public void setNameOfCPFacility(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_CP_FACILITY), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.NAME_OF_CP_FACILITY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getOtherCauseOfDeath() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_CAUSE_OF_DEATH), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
		
	}
	
	public void setOtherCauseOfDeath(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_CAUSE_OF_DEATH), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.OTHER_CAUSE_OF_DEATH), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getLink() {
		return "/module/mdrtb/form/tb03.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public String getRegistrationNumber() {
		
		if (regNum != null) {
			return regNum;
		}
		String val = "";
		PatientIdentifier pi = null;
		Integer ppid = null;
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs idObs = MdrtbUtil.getObsFromEncounter(ppidConcept, getEncounter());
		if (idObs == null) {
			val = null;
		}
		
		else {
			ppid = idObs.getValueNumeric().intValue();
			PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(ppid);
			
			if (pp != null) {
				pi = Context.getService(MdrtbService.class).getPatientProgramIdentifier(pp);
				if (pi == null) {
					val = null;
				} else {
					val = pi.getIdentifier();
				}
			} else {
				val = null;
			}
		}
		if (val == null || val.isEmpty()) {
			// Context.getService(MessagePropertyService.class).getMessage("mdrtb.unassigned");
			val = null;
		}
		
		regNum = val;
		return val;
	}
	
	public int compareTo(TB03Form form) {
		if (this.getRegistrationNumber() == null)
			return 1;
		if (form.getRegistrationNumber() == null)
			return -1;
		
		return this.getRegistrationNumber().compareTo(form.getRegistrationNumber());
	}
}
