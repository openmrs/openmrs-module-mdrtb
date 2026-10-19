package org.openmrs.module.mdrtb.web.dto;

import com.google.common.collect.Lists;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.PatientProgramSummary;
import org.openmrs.module.mdrtb.PatientSummary;

import java.util.*;

public class SimplePatientSummaryData extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 111L;
	
	private String patientUuid;
	
	private PersonName personName;
	
	private PersonAddress personAddress;
	
	private String gender;
	
	private Date dateOfBirth;
	
	private Set<PatientIdentifier> patientIdentifiers;
	
	private List<SimplePatientProgramSummary> patientPrograms;
	
	public SimplePatientSummaryData(PatientSummary patientSummary) {
		Patient patient = patientSummary.getPatient();
		this.patientUuid = patient.getUuid();
		this.personName = patientSummary.getPersonName();
		this.personAddress = patient.getPersonAddress();
		this.gender = patient.getGender();
		this.dateOfBirth = patient.getBirthdate();
		this.patientIdentifiers = patientSummary.getPatientIdentifiers();
		patientPrograms = Lists.newArrayList();
		List<Map<String, String>> observations = new ArrayList<>();
		for (PatientProgramSummary programSummary : patientSummary.getPatientProgramSummaries()) {
			PatientProgram patientProgram = programSummary.getPatientProgram();
			for (Obs o : programSummary.getObservations()) {
				Map<String, String> obsMap = new HashMap<>();
				obsMap.put("concept", o.getConcept().getUuid());
				obsMap.put("encounter", o.getEncounter() != null ? o.getEncounter().getUuid() : null);
                obsMap.put("obsDatetime", o.getObsDatetime() != null ? o.getObsDatetime().toString() : null);
                obsMap.put("valueBoolean", o.getValueAsBoolean() != null ? String.valueOf(o.getValueAsBoolean()) : null);
                obsMap.put("valueCoded", o.getValueCoded() != null ? o.getValueCoded().getUuid() : null);
                obsMap.put("valueDate", o.getValueDate() != null ? String.valueOf(o.getValueDate()) : null);
                obsMap.put("valueDatetime", o.getValueDatetime() != null ? String.valueOf(o.getValueDatetime()) : null);
                obsMap.put("valueDrug", o.getValueDrug() != null ? o.getValueDrug().getUuid() : null);
                obsMap.put("valueText", o.getValueText());
                obsMap.put("valueNumeric", o.getValueNumeric() != null ? String.valueOf(o.getValueNumeric()) : null);
                obsMap.put("previousVersion", o.getPreviousVersion() != null ? o.getPreviousVersion().getUuid() : null);
                obsMap.put("valueAsString", o.getValueAsString(Context.getLocale()));
				observations.add(obsMap);
			}
			Obs outcome = programSummary.getTreatmentOutcome();
			SimplePatientProgramSummary ppSummary = new SimplePatientProgramSummary(
					patientUuid, patientProgram.getProgram().getName(), patientProgram.getUuid(),
					patientProgram.getDateEnrolled(), patientProgram.getDateCompleted(),
					(outcome != null ? outcome.getValueAsString(Context.getLocale()) : null),
					observations, programSummary.getLabTests()
			);
			patientPrograms.add(ppSummary);
		}
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
	
	public List<SimplePatientProgramSummary> getPatientPrograms() {
		return patientPrograms;
	}
	
	public void setPatientPrograms(List<SimplePatientProgramSummary> patientPrograms) {
		this.patientPrograms = patientPrograms;
	}
	
	public Set<PatientIdentifier> getPatientIdentifiers() {
		return patientIdentifiers;
	}
	
	public void setPatientIdentifiers(Set<PatientIdentifier> patientIdentifiers) {
		this.patientIdentifiers = patientIdentifiers;
	}
	
	public PersonName getPersonName() {
		return personName;
	}
	
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}
	
	public PersonAddress getPersonAddress() {
		return personAddress;
	}
	
	public void setPersonAddress(PersonAddress personAddress) {
		this.personAddress = personAddress;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
}
