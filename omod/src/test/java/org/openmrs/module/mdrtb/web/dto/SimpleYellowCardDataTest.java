package org.openmrs.module.mdrtb.web.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.reporting.pv.YellowCardData;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 * Unit test for {@link SimpleYellowCardData}: every value of {@link YellowCardData} must reach the
 * REST response unchanged. Runs without a database (see YellowCardDataTest in the api module for
 * the fakes).
 */
public class SimpleYellowCardDataTest {
	
	@Test
	public void shouldCopyEveryFieldOfTheCard() {
		YellowCardData card = buildCard();
		
		SimpleYellowCardData dto = new SimpleYellowCardData(card);
		
		assertEquals(card.getAdverseEventsFormUuid(), dto.getAdverseEventsFormUuid());
		assertEquals(Integer.valueOf(card.getCausalityIndex()), dto.getCausalityIndex());
		assertEquals(card.getPatientUuid(), dto.getPatientUuid());
		assertEquals(card.getPatientName(), dto.getPatientName());
		assertEquals(card.getAddress(), dto.getAddress());
		assertEquals(card.getOpenmrsIdentifier(), dto.getOpenmrsIdentifier());
		assertEquals(card.getDateOfBirth(), dto.getDateOfBirth());
		assertEquals(card.getGender(), dto.getGender());
		assertEquals(card.getOutcomeCode(), dto.getOutcomeCode());
		assertEquals(card.getOutcomeName(), dto.getOutcomeName());
		assertEquals(card.getRegimenTypeCode(), dto.getRegimenTypeCode());
		assertEquals(card.getRegimenTypeName(), dto.getRegimenTypeName());
		assertEquals(card.getDiagnosticSummary(), dto.getDiagnosticSummary());
		assertEquals(card.getClinicianNotes(), dto.getClinicianNotes());
		assertEquals(card.getSuspectedDrugName(), dto.getSuspectedDrugName());
		assertEquals(card.getSuspectedDrugsText(), dto.getSuspectedDrugsText());
		assertEquals(card.getOnsetDate(), dto.getOnsetDate());
		assertEquals(card.getCessationDate(), dto.getCessationDate());
		assertEquals(card.getAdverseEventName(), dto.getAdverseEventName());
		assertEquals(card.getSaeTypeCode(), dto.getSaeTypeCode());
		assertEquals(card.getSaeTypeName(), dto.getSaeTypeName());
		assertEquals(card.getAesiTypeCode(), dto.getAesiTypeCode());
		assertEquals(card.getAesiTypeName(), dto.getAesiTypeName());
		assertEquals(card.getRegimenDate(), dto.getRegimenDate());
		assertEquals(card.getRechallengeReproducedReaction(), dto.getRechallengeReproducedReaction());
		assertEquals(card.getCausalityCode(), dto.getCausalityCode());
		assertEquals(card.getCausalityName(), dto.getCausalityName());
		assertEquals(card.getReporterName(), dto.getReporterName());
		assertEquals(card.getReporterPlaceOfWork(), dto.getReporterPlaceOfWork());
		assertEquals(card.getReportDate(), dto.getReportDate());
		assertEquals(card.getWarnings(), dto.getWarnings());
		
		// sanity: the sample card really has values, so the checks above compare something
		assertEquals("Rahimov Ali", dto.getPatientName());
		assertEquals("RECOVERED_WITH_SEQUELAE", dto.getOutcomeCode());
		assertEquals("2026-09-01", dto.getCessationDate());
		assertEquals("HOSPITALIZATION", dto.getSaeTypeCode());
		assertEquals("CERTAIN", dto.getCausalityCode());
		assertEquals(Boolean.TRUE, dto.getRechallengeReproducedReaction());
		assertEquals("Saidova Malika", dto.getReporterName());
	}
	
	@Test
	public void shouldTurnMedicinesIntoNameLabelDoseObjects() {
		SimpleYellowCardData dto = new SimpleYellowCardData(buildCard());
		
		List<SimpleObject> rows = dto.getPrimaryDiseaseMedicines();
		
		assertEquals(2, rows.size());
		assertEquals("AMIKACIN", rows.get(0).get("name"));
		assertEquals("mdrtb.pv.amDose", rows.get(0).get("doseLabelCode"));
		assertEquals(3.0, (Double) rows.get(0).get("dose"), 0.0);
		assertEquals("Pretomanid", rows.get(1).get("name"));
		assertEquals("mdrtb.pv.otherDrug1Dose", rows.get(1).get("doseLabelCode"));
		assertNull(rows.get(1).get("dose"));
	}
	
	@Test
	public void shouldCopyWarningsIntoANewList() {
		YellowCardData card = buildCard();
		
		SimpleYellowCardData dto = new SimpleYellowCardData(card);
		
		assertNotSame(card.getWarnings(), dto.getWarnings());
		dto.getWarnings().add("CHANGED");
		assertTrue(!card.getWarnings().contains("CHANGED"));
	}
	
	@Test
	public void shouldKeepEmptyValuesEmpty() {
		Patient patient = new Patient(1);
		patient.setUuid("p");
		Encounter encounter = new Encounter(1);
		encounter.setUuid("e");
		encounter.setPatient(patient);
		
		AdverseEventsForm emptyForm = new EmptyAeForm(encounter);
		YellowCardData card = YellowCardData.fromAdverseEventsForm(emptyForm, new Lookups(null)).get(0);
		
		SimpleYellowCardData dto = new SimpleYellowCardData(card);
		
		assertNull(dto.getPatientName());
		assertNull(dto.getDateOfBirth());
		assertNull(dto.getOutcomeCode());
		assertNull(dto.getSuspectedDrugName());
		assertNull(dto.getRechallengeReproducedReaction());
		assertTrue(dto.getPrimaryDiseaseMedicines().isEmpty());
		assertEquals(Integer.valueOf(0), dto.getCausalityIndex());
		assertTrue(dto.getWarnings().containsAll(
		    Arrays.asList(YellowCardData.WARN_NO_CAUSALITY_DRUG, YellowCardData.WARN_NO_REGIMEN_FORM)));
	}
	
	// ---------------------------------------------------------------------------------------------
	
	private static YellowCardData buildCard() {
		Lookups lookups = new Lookups(new PatientIdentifierType(1));
		
		Patient patient = new Patient(10);
		patient.setUuid("patient-uuid");
		patient.addName(new PersonName("Ali", null, "Rahimov"));
		patient.setGender("M");
		patient.setBirthdate(date(1980, 3, 7));
		patient.addIdentifier(new PatientIdentifier("10004-6", lookups.idType, null));
		
		Location location = new Location(3);
		location.setName("City TB Centre");
		Encounter encounter = new Encounter(20);
		encounter.setUuid("ae-uuid");
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setEncounterDatetime(date(2026, 8, 4));
		
		Person provider = new Person(30);
		provider.addName(new PersonName("Malika", null, "Saidova"));
		
		FullAeForm form = new FullAeForm(encounter);
		form.drug = lookups.concept("AMIKACIN");
		form.result = lookups.concept("DEFINITE");
		form.outcome = lookups.concept("RESOLVED WITH SEQUELAE");
		form.sae = lookups.concept("HOSPITALIZATION WORKFLOW");
		form.aesi = lookups.concept("HEARING DISORDER");
		form.event = lookups.concept("TINNITUS");
		form.rechallenge = lookups.concept("RECURRENCE OF EVENT");
		form.provider = provider;
		
		lookups.regimen = new SampleRegimenForm(lookups.concept("SHORT MDR REGIMEN"));
		
		List<YellowCardData> cards = YellowCardData.fromAdverseEventsForm(form, lookups);
		assertEquals(1, cards.size());
		return cards.get(0);
	}
	
	private static Date date(int year, int month, int day) {
		return new GregorianCalendar(year, month - 1, day).getTime();
	}
	
	/** Concept lookups by name; concept ids are generated from the name. */
	private static class Lookups implements YellowCardData.Lookups {
		
		final PatientIdentifierType idType;
		
		RegimenForm regimen;
		
		Lookups(PatientIdentifierType idType) {
			this.idType = idType;
		}
		
		Concept concept(String name) {
			Concept concept = new Concept(Math.abs(name.hashCode()));
			concept.addName(new ConceptName(name, Locale.ENGLISH));
			return concept;
		}
		
		@Override
		public Concept getConcept(String lookup) {
			return concept(lookup);
		}
		
		@Override
		public Locale getLocale() {
			return Locale.ENGLISH;
		}
		
		@Override
		public RegimenForm getCurrentRegimenForm(Patient patient, Date onsetDate) {
			return regimen;
		}
		
		@Override
		public PatientIdentifierType getOpenmrsIdentifierType() {
			return idType;
		}
	}
	
	/** An AE form with nothing recorded. */
	private static class EmptyAeForm extends AdverseEventsForm {
		
		EmptyAeForm(Encounter encounter) {
			super(encounter);
		}
		
		@Override
		public Concept getCausalityDrug1() {
			return null;
		}
		
		@Override
		public Concept getCausalityDrug2() {
			return null;
		}
		
		@Override
		public Concept getCausalityDrug3() {
			return null;
		}
		
		@Override
		public Concept getCausalityAssessmentResult1() {
			return null;
		}
		
		@Override
		public Concept getCausalityAssessmentResult2() {
			return null;
		}
		
		@Override
		public Concept getCausalityAssessmentResult3() {
			return null;
		}
		
		@Override
		public Concept getActionOutcome() {
			return null;
		}
		
		@Override
		public Date getOutcomeDate() {
			return null;
		}
		
		@Override
		public Concept getAdverseEvent() {
			return null;
		}
		
		@Override
		public Concept getTypeOfSAE() {
			return null;
		}
		
		@Override
		public Concept getTypeOfSpecialEvent() {
			return null;
		}
		
		@Override
		public Concept getDrugRechallenge() {
			return null;
		}
		
		@Override
		public Date getYellowCardDate() {
			return null;
		}
		
		@Override
		public String getDiagnosticSummary() {
			return null;
		}
		
		@Override
		public String getComments() {
			return null;
		}
		
		@Override
		public String getSuspectedDrug() {
			return null;
		}
		
		@Override
		public Person getProvider() {
			return null;
		}
	}
	
	/** An AE form with one causality drug and most answers recorded. */
	private static class FullAeForm extends EmptyAeForm {
		
		Concept drug, result, outcome, sae, aesi, event, rechallenge;
		
		Person provider;
		
		FullAeForm(Encounter encounter) {
			super(encounter);
		}
		
		@Override
		public Concept getCausalityDrug1() {
			return drug;
		}
		
		@Override
		public Concept getCausalityAssessmentResult1() {
			return result;
		}
		
		@Override
		public Concept getActionOutcome() {
			return outcome;
		}
		
		@Override
		public Date getOutcomeDate() {
			return date(2026, 9, 1);
		}
		
		@Override
		public Concept getAdverseEvent() {
			return event;
		}
		
		@Override
		public Concept getTypeOfSAE() {
			return sae;
		}
		
		@Override
		public Concept getTypeOfSpecialEvent() {
			return aesi;
		}
		
		@Override
		public Concept getDrugRechallenge() {
			return rechallenge;
		}
		
		@Override
		public Date getYellowCardDate() {
			return date(2026, 9, 10);
		}
		
		@Override
		public String getDiagnosticSummary() {
			return "Audiogram, ";
		}
		
		@Override
		public String getComments() {
			return "notes";
		}
		
		@Override
		public String getSuspectedDrug() {
			return "Am";
		}
		
		@Override
		public Person getProvider() {
			return provider;
		}
	}
	
	/** Regimen form with amikacin 3 and an "other drug" without a dose. */
	private static class SampleRegimenForm extends RegimenForm {
		
		private final Concept type;
		
		SampleRegimenForm(Concept type) {
			super(new Encounter());
			this.type = type;
		}
		
		@Override
		public Concept getSldRegimenType() {
			return type;
		}
		
		@Override
		public Date getCouncilDate() {
			return date(2026, 5, 2);
		}
		
		@Override
		public Double getAmDose() {
			return 3.0;
		}
		
		@Override
		public String getOtherDrug1Name() {
			return "Pretomanid";
		}
		
		@Override
		public Double getOtherDrug1Dose() {
			return null;
		}
		
		// every other dose getter of RegimenForm reads obs through OpenMRS, so return null here
		@Override
		public Double getCmDose() {
			return null;
		}
		
		@Override
		public Double getMfxDose() {
			return null;
		}
		
		@Override
		public Double getLfxDose() {
			return null;
		}
		
		@Override
		public Double getPtoDose() {
			return null;
		}
		
		@Override
		public Double getCsDose() {
			return null;
		}
		
		@Override
		public Double getPasDose() {
			return null;
		}
		
		@Override
		public Double getZDose() {
			return null;
		}
		
		@Override
		public Double getEDose() {
			return null;
		}
		
		@Override
		public Double getHDose() {
			return null;
		}
		
		@Override
		public Double getLzdDose() {
			return null;
		}
		
		@Override
		public Double getCfzDose() {
			return null;
		}
		
		@Override
		public Double getBdqDose() {
			return null;
		}
		
		@Override
		public Double getDlmDose() {
			return null;
		}
		
		@Override
		public Double getImpDose() {
			return null;
		}
		
		@Override
		public Double getHrDose() {
			return null;
		}
		
		@Override
		public Double getHrzeDose() {
			return null;
		}
		
		@Override
		public Double getSDose() {
			return null;
		}
		
		@Override
		public Double getAmxDose() {
			return null;
		}
	}
}
