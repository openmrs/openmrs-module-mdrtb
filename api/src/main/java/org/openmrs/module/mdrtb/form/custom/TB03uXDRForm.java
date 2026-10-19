package org.openmrs.module.mdrtb.form.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

public class TB03uXDRForm extends AbstractSimpleForm {
	
	private static final String VOID_REASON = "voided by MDRTB module";
	
	public TB03uXDRForm() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_TB03U_XDRTB_INTAKE);
	}
	
	public TB03uXDRForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_TB03U_XDRTB_INTAKE);
	}
	
	public TB03uXDRForm(Encounter encounter) {
		super(encounter);
	}
	
	public String getPatientName() {
		PersonName p = getPatient().getPersonName();
		
		return p.getFamilyName() + "," + p.getGivenName();
	}
	
	public String getTb03RegistrationNumber() {
		TB03uForm tf = getTb03u();
		if (tf != null)
			return tf.getTb03RegistrationNumber();
		else
			return null;
		
	}
	
	public Integer getTb03RegistrationYear() {
		TB03uForm tf = getTb03u();
		if (tf != null)
			return tf.getTb03RegistrationYear();
		else
			return null;
	}
	
	public Integer getAgeAtMDRRegistration() {
		TB03uForm tf = getTb03u();
		if (tf != null)
			return tf.getAgeAtMDRRegistration();
		else
			return null;
	}
	
	public String getSldRegisterNumber() {
		TB03uForm tf = getTb03u();
		if (tf != null)
			return tf.getSldRegisterNumber();
		else
			return null;
	}
	
	public void setSldRegisterNumber(String number) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_2_REG_NUMBER), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && number == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(number)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (number != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.REGIMEN_2_REG_NUMBER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(number);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getGender() {
		if (getPatient().getGender().equals("M"))
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.tb03.gender.male");
		if (getPatient().getGender().equals("F"))
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.tb03.gender.female");
		
		return "";
	}
	
	public Date getDateOfBirth() {
		return getPatient().getBirthdate();
	}
	
	public String getAddress() {
		
		PersonAddress pa = getPatient().getPersonAddress();
		if (pa == null)
			return null;
		String address = pa.getCountry() + "," + pa.getStateProvince() + "," + pa.getCountyDistrict();
		if (pa.getAddress1() != null && !pa.getAddress1().isEmpty()) {
			address += "," + pa.getAddress1();
			if (pa.getAddress2() != null && !pa.getAddress2().isEmpty())
				address += "," + pa.getAddress2();
		}
		
		return address;
	}
	
	public Concept getAnatomicalSite() {
		TB03Form tb03 = getTb03();
		if (tb03 != null)
			return tb03.getAnatomicalSite();
		else
			return null;
	}
	
	public Concept getRegistrationGroup() {
		if (getPatientProgramId() != null) {
			MdrtbPatientProgram tp = Context.getService(MdrtbService.class).getMdrtbPatientProgram(getPatientProgramId());
			ProgramWorkflowState pws = tp.getClassificationAccordingToPreviousTreatment();
			if (pws != null)
				return pws.getConcept();
			else
				return null;
		}
		
		else {
			return null;
		}
	}
	
	public void setRegistrationGroup(Concept group) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT),
		    encounter);
		
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
				    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT), encounter.getEncounterDatetime(),
				        encounter.getLocation());
				obs.setValueCoded(group);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getRegistrationGroupByDrug() {
		if (getPatientProgramId() != null) {
			MdrtbPatientProgram tp = Context.getService(MdrtbService.class).getMdrtbPatientProgram(getPatientProgramId());
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
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE),
		    encounter);
		
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
				    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE), encounter.getEncounterDatetime(),
				        encounter.getLocation());
				obs.setValueCoded(group);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getMdrStatus() {
		TB03uForm tf = getTb03u();
		if (tf != null)
			return tf.getMdrStatus();
		else
			return null;
	}
	
	public void setMdrStatus(Concept status) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_STATUS),
		    encounter);
		
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
				    MdrtbConcepts.MDR_STATUS), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(status);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getConfirmationDate() {
		TB03uForm tf = getTb03u();
		if (tf != null)
			return tf.getConfirmationDate();
		else
			return null;
	}
	
	public void setConfirmationDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_MDR_CONFIRMATION), encounter);
		
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
				    MdrtbConcepts.DATE_OF_MDR_CONFIRMATION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPatientCategory() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY), encounter);
		return obs == null ? null : obs.getValueCoded();
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
	
	public Date getMdrTreatmentStartDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE), encounter);
		return obs == null ? null : obs.getValueDatetime();
	}
	
	public void setMdrTreatmentStartDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE), encounter);
		
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
				    MdrtbConcepts.MDR_TREATMENT_START_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getTxLocation() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_LOCATION), encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setTxLocation(Concept cat) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_LOCATION), encounter);
		
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
				    MdrtbConcepts.TREATMENT_LOCATION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(cat);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getNameOfTxLocation() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_TREATMENT_LOCATION), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setNameOfTxLocation(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_TREATMENT_LOCATION), encounter);
		
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
				    MdrtbConcepts.NAME_OF_TREATMENT_LOCATION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getResistanceType() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE), encounter);
		return obs == null ? null : obs.getValueCoded();
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
	
	public Concept getBasisForDiagnosis() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION), encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setBasisForDiagnosis(Concept basis) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && basis == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(basis)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (basis != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.METHOD_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(basis);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getHivTestDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_HIV_TEST), encounter);
		return obs == null ? null : obs.getValueDatetime();
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
	
	public Concept getHivStatus() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST), encounter);
		return obs == null ? null : obs.getValueCoded();
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
		return obs == null ? null : obs.getValueDatetime();
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
		return obs == null ? null : obs.getValueDatetime();
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
	
	public Concept getTreatmentOutcome() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME), encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setTreatmentOutcome(Concept outcome) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME), encounter);
		
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
				    MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(outcome);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getTreatmentOutcomeDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_OUTCOME_DATE), encounter);
		return obs == null ? null : obs.getValueDatetime();
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
		/*Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME), encounter);*/
		
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
	
	public Concept getRelapsed() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSED),
		    encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setRelapsed(Concept rel) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSED),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && rel == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(rel)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			
			// now create the new Obs and add it to the encounter	
			if (rel != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.RELAPSED), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(rel);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getRelapseMonth() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_MONTH), encounter);
		return obs == null ? null : obs.getValueNumeric().intValue();
	}
	
	public void setRelapseMonth(Integer month) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_MONTH), encounter);
		
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
				    MdrtbConcepts.RELAPSE_MONTH), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(month.doubleValue());
				encounter.addObs(obs);
			}
		}
	}
	
	public String getCliniciansNotes() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), encounter);
		return obs == null ? null : obs.getValueText();
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
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
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
	
	public List<SmearForm> getSmears() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getSmearForms(getPatientProgramId());
		
	}
	
	public List<DrugResistanceDuringTreatmentForm> getDrdts() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getDrdtForms(getPatientProgramId());
		
	}
	
	public List<CultureForm> getCultures() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getCultureForms(getPatientProgramId());
		
	}
	
	public List<DSTForm> getDsts() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		return Context.getService(MdrtbService.class).getDstForms(getPatientProgramId());
		
	}
	
	public String getLink() {
		return "/module/mdrtb/form/tb03u.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public TB03Form getTb03() {
		TB03Form ret = null;
		Location location = getLocation();
		Date encounterDate = getEncounterDatetime();
		ret = Context.getService(MdrtbService.class).getClosestTB03Form(location, encounterDate, getPatient());
		return ret;
	}
	
	public TB03uForm getTb03u() {
		TB03uForm form = null;
		form = Context.getService(MdrtbService.class).getTB03uFormForProgram(getPatient(), getPatientProgramId());
		return form;
	}
	
	public Concept getCauseOfDeath() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH), encounter);
		return obs == null ? null : obs.getValueCoded();
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
	
	public String getOtherCauseOfDeath() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_CAUSE_OF_DEATH), encounter);
		return obs == null ? null : obs.getValueText();
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
}
