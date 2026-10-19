package org.openmrs.module.mdrtb.reporting.pv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;

/**
 * Unit tests for {@link YellowCardData} - the rules that decide what is printed on the Yellow Card.
 * <p>
 * These tests need NO database and NO OpenMRS context. Everything YellowCardData normally asks
 * OpenMRS for is supplied by the small fakes at the bottom of this file:
 * <ul>
 * <li>{@link FakeLookups} - concepts, locale, regimen form and identifier type</li>
 * <li>{@link FakeAeForm} - an Adverse Events form whose answers are set directly in the test</li>
 * <li>{@link FakeRegimenForm} - a regimen form with doses set directly in the test</li>
 * </ul>
 * How to read a test: "given" builds the form, "when" calls {@link #cards()}, "then" checks the card.
 * <p>
 * Rules under test (agreed 2026-09-15): one card per filled causality drug; only 1:1 recorded values
 * are filled; an answer without an exact box on the card is never moved to a "close" box - it gets a
 * NOT_ON_CARD / OTHER code and a warning.
 */
@Ignore("Skipping this class until this feature is complete")
public class YellowCardDataTest {

	private static final String PATIENT_UUID = "patient-uuid-0001";

	private static final String FORM_UUID = "ae-encounter-uuid-0001";

	private FakeLookups lookups;

	private Patient patient;

	private PatientIdentifierType openmrsIdType;

	private Encounter encounter;

	private FakeAeForm form;

	private FakeRegimenForm regimen;

	@Before
	public void setUp() {
		lookups = new FakeLookups();

		openmrsIdType = new PatientIdentifierType(1);
		openmrsIdType.setName("OpenMRS ID");
		lookups.openmrsIdType = openmrsIdType;

		patient = new Patient(100);
		patient.setUuid(PATIENT_UUID);
		patient.addName(new PersonName("Ali", "Karimovich", "Rahimov"));
		patient.setGender("M");
		patient.setBirthdate(date(1980, 3, 7));
		PersonAddress address = new PersonAddress();
		address.setCountry("Tajikistan");
		address.setStateProvince("Dushanbe");
		address.setCountyDistrict("Ismoili Somoni");
		address.setCityVillage("Dushanbe");
		address.setAddress1("Rudaki 12");
		address.setAddress2("apt 5");
		address.setPreferred(true);
		patient.addAddress(address);
		PatientIdentifier id = new PatientIdentifier("10004-6", openmrsIdType, null);
		id.setPreferred(true);
		patient.addIdentifier(id);

		Location location = new Location(7);
		location.setName("City TB Centre Dushanbe");

		encounter = new Encounter(500);
		encounter.setUuid(FORM_UUID);
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setEncounterDatetime(date(2026, 8, 4));

		form = new FakeAeForm(encounter);
		form.causalityDrug1 = lookups.concept(MdrtbConcepts.AMIKACIN);
		form.causalityResult1 = lookups.concept(MdrtbConcepts.PROBABLE);

		regimen = new FakeRegimenForm();
		lookups.regimenForm = regimen;
	}

	// =============================================================================================
	// Number of cards (one per filled causality drug)
	// =============================================================================================

	@Test
	public void shouldReturnNoCardForNullForm() {
		assertTrue(YellowCardData.fromAdverseEventsForm(null, lookups).isEmpty());
	}

	@Test
	public void shouldReturnNoCardWhenFormHasNoEncounter() {
		FakeAeForm noEncounter = new FakeAeForm(null);
		assertTrue(YellowCardData.fromAdverseEventsForm(noEncounter, lookups).isEmpty());
	}

	@Test
	public void shouldReturnNoCardForVoidedEncounter() {
		encounter.setVoided(true);
		assertTrue(cards().isEmpty());
	}

	@Test
	public void shouldReturnNoCardWhenEncounterHasNoPatient() {
		encounter.setPatient(null);
		assertTrue(cards().isEmpty());
	}

	@Test
	public void shouldReturnOneCardWithIndexZeroAndWarningWhenNoCausalityDrugIsRecorded() {
		form.causalityDrug1 = null;
		form.causalityResult1 = null;

		List<YellowCardData> cards = cards();

		assertEquals(1, cards.size());
		YellowCardData card = cards.get(0);
		assertEquals(0, card.getCausalityIndex());
		assertNull(card.getSuspectedDrugName());
		assertNull(card.getCausalityCode());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_NO_CAUSALITY_DRUG));
	}

	@Test
	public void shouldReturnOneCardForOneCausalityDrug() {
		List<YellowCardData> cards = cards();

		assertEquals(1, cards.size());
		assertEquals(1, cards.get(0).getCausalityIndex());
		assertEquals("AMIKACIN", cards.get(0).getSuspectedDrugName());
		assertEquals(YellowCardData.CAUSALITY_PROBABLE, cards.get(0).getCausalityCode());
		assertFalse(cards.get(0).getWarnings().contains(YellowCardData.WARN_NO_CAUSALITY_DRUG));
	}

	@Test
	public void shouldReturnOneCardPerCausalityDrugEachWithItsOwnAssessment() {
		form.causalityDrug2 = lookups.concept(MdrtbConcepts.LINEZOLID);
		form.causalityResult2 = lookups.concept(MdrtbConcepts.DEFINITE);
		form.causalityDrug3 = lookups.concept(MdrtbConcepts.BEDAQUILINE);
		form.causalityResult3 = lookups.concept(MdrtbConcepts.POSSIBLE);

		List<YellowCardData> cards = cards();

		assertEquals(3, cards.size());
		assertCard(cards.get(0), 1, "AMIKACIN", YellowCardData.CAUSALITY_PROBABLE);
		assertCard(cards.get(1), 2, "LINEZOLID", YellowCardData.CAUSALITY_CERTAIN);
		assertCard(cards.get(2), 3, "BEDAQUILINE", YellowCardData.CAUSALITY_POSSIBLE);
	}

	@Test
	public void shouldSkipEmptyCausalitySlotAndKeepTheRealSlotNumber() {
		form.causalityDrug3 = lookups.concept(MdrtbConcepts.CLOFAZIMINE);
		form.causalityResult3 = lookups.concept(MdrtbConcepts.NOT_CLASSIFIED);

		List<YellowCardData> cards = cards();

		assertEquals(2, cards.size());
		assertCard(cards.get(0), 1, "AMIKACIN", YellowCardData.CAUSALITY_PROBABLE);
		assertCard(cards.get(1), 3, "CLOFAZIMINE", YellowCardData.CAUSALITY_UNCLASSIFIED);
	}

	@Test
	public void shouldIgnoreAssessmentWithoutDrug() {
		form.causalityResult2 = lookups.concept(MdrtbConcepts.DEFINITE); // drug 2 not filled

		assertEquals(1, cards().size());
	}

	@Test
	public void shouldRepeatSharedSectionsOnEveryCard() {
		form.causalityDrug2 = lookups.concept(MdrtbConcepts.LINEZOLID);
		form.adverseEvent = lookups.concept(MdrtbConcepts.TINNITUS);

		List<YellowCardData> cards = cards();

		for (YellowCardData card : cards) {
			assertEquals(FORM_UUID, card.getAdverseEventsFormUuid());
			assertEquals(PATIENT_UUID, card.getPatientUuid());
			assertEquals("Rahimov Ali Karimovich", card.getPatientName());
			assertEquals("TINNITUS", card.getAdverseEventName());
			assertEquals("2026-08-04", card.getOnsetDate());
		}
	}

	@Test
	public void shouldNotShareWarningListsBetweenCards() {
		form.causalityDrug2 = lookups.concept(MdrtbConcepts.LINEZOLID);
		form.causalityResult2 = lookups.concept(MdrtbConcepts.SUSPECTED);

		List<YellowCardData> cards = cards();

		assertNotSame(cards.get(0).getWarnings(), cards.get(1).getWarnings());
		assertFalse(cards.get(0).getWarnings().contains(YellowCardData.WARN_CAUSALITY_NOT_ON_CARD));
		assertTrue(cards.get(1).getWarnings().contains(YellowCardData.WARN_CAUSALITY_NOT_ON_CARD));
	}

	// =============================================================================================
	// Section 1 - patient information (fields 1-5)
	// =============================================================================================

	@Test
	public void shouldFillPatientNameAsFamilyGivenMiddle() {
		assertEquals("Rahimov Ali Karimovich", card().getPatientName());
	}

	@Test
	public void shouldSkipBlankNameParts() {
		patient.getPersonName().setMiddleName("  ");
		assertEquals("Rahimov Ali", card().getPatientName());
	}

	@Test
	public void shouldReturnNullNameWhenPatientHasNoName() {
		patient.removeName(patient.getPersonName());
		assertNull(card().getPatientName());
	}

	@Test
	public void shouldJoinAddressPartsAndSkipBlanks() {
		assertEquals("Tajikistan, Dushanbe, Ismoili Somoni, Dushanbe, Rudaki 12, apt 5", card().getAddress());

		PersonAddress address = patient.getPersonAddress();
		address.setCityVillage("");
		address.setAddress2(null);
		assertEquals("Tajikistan, Dushanbe, Ismoili Somoni, Rudaki 12", card().getAddress());
	}

	@Test
	public void shouldReturnNullAddressWhenAddressIsMissingOrEmpty() {
		PersonAddress address = patient.getPersonAddress();
		address.setCountry(null);
		address.setStateProvince(" ");
		address.setCountyDistrict(null);
		address.setCityVillage(null);
		address.setAddress1(null);
		address.setAddress2(null);
		assertNull(card().getAddress());

		patient.removeAddress(address);
		assertNull(card().getAddress());
	}

	@Test
	public void shouldFillOpenmrsIdentifier() {
		YellowCardData card = card();
		assertEquals("10004-6", card.getOpenmrsIdentifier());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_NO_OPENMRS_ID));
	}

	@Test
	public void shouldNotUseAnIdentifierOfAnotherType() {
		PatientIdentifierType dotsType = new PatientIdentifierType(2);
		dotsType.setName("DOTS");
		patient.getPatientIdentifier(openmrsIdType).setVoided(true);
		patient.addIdentifier(new PatientIdentifier("DOTS-999", dotsType, null));

		YellowCardData card = card();

		assertNull(card.getOpenmrsIdentifier());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_NO_OPENMRS_ID));
	}

	@Test
	public void shouldWarnWhenOpenmrsIdentifierTypeDoesNotExist() {
		lookups.openmrsIdType = null;

		YellowCardData card = card();

		assertNull(card.getOpenmrsIdentifier());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_NO_OPENMRS_ID));
	}

	@Test
	public void shouldFillDateOfBirthAsIsoDate() {
		assertEquals("1980-03-07", card().getDateOfBirth());
	}

	@Test
	public void shouldLeaveDateOfBirthEmptyWhenUnknown() {
		patient.setBirthdate(null);
		assertNull(card().getDateOfBirth());
	}

	@Test
	public void shouldFillGenderAsStored() {
		assertEquals("M", card().getGender());
		patient.setGender("F");
		assertEquals("F", card().getGender());
	}

	// =============================================================================================
	// Field 15 - outcome
	// =============================================================================================

	@Test
	public void shouldMapResolvedToRecoveryWithoutConsequences() {
		form.actionOutcome = lookups.concept(MdrtbConcepts.RESOLVED);

		YellowCardData card = card();

		assertEquals(YellowCardData.OUTCOME_RECOVERED_WITHOUT_SEQUELAE, card.getOutcomeCode());
		assertEquals("RESOLVED", card.getOutcomeName());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_OUTCOME_NOT_ON_CARD));
	}

	@Test
	public void shouldMapResolvedWithSequelaeToRecoveryWithConsequences() {
		form.actionOutcome = lookups.concept(MdrtbConcepts.RESOLVED_WITH_SEQUELAE);
		assertEquals(YellowCardData.OUTCOME_RECOVERED_WITH_SEQUELAE, card().getOutcomeCode());
	}

	@Test
	public void shouldNotTickAnyOutcomeBoxForOutcomesThatAreNotOnTheCard() {
		for (String outcome : Arrays.asList(MdrtbConcepts.FATAL, MdrtbConcepts.RESOLVING, MdrtbConcepts.NOT_RESOLVED)) {
			form.actionOutcome = lookups.concept(outcome);

			YellowCardData card = card();

			assertEquals(outcome, YellowCardData.OUTCOME_NOT_ON_CARD, card.getOutcomeCode());
			assertEquals(outcome, outcome, card.getOutcomeName());
			assertTrue(outcome, card.getWarnings().contains(YellowCardData.WARN_OUTCOME_NOT_ON_CARD));
		}
	}

	@Test
	public void shouldLeaveOutcomeEmptyWhenNotRecorded() {
		YellowCardData card = card();
		assertNull(card.getOutcomeCode());
		assertNull(card.getOutcomeName());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_OUTCOME_NOT_ON_CARD));
	}

	// =============================================================================================
	// Field 16 - regimen, laboratory summary, notes
	// =============================================================================================

	@Test
	public void shouldLookUpRegimenFormForThePatientAtTheOnsetDate() {
		card();
		assertEquals(1, lookups.regimenLookups.size());
		assertEquals(patient, lookups.regimenLookups.get(0).patient);
		assertEquals(date(2026, 8, 4), lookups.regimenLookups.get(0).date);
	}

	@Test
	public void shouldNotLookUpRegimenWhenOnsetDateIsMissing() {
		encounter.setEncounterDatetime(null);

		YellowCardData card = card();

		assertTrue(lookups.regimenLookups.isEmpty());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_NO_REGIMEN_FORM));
		assertNull(card.getOnsetDate());
	}

	@Test
	public void shouldWarnAndLeaveRegimenAndMedicinesEmptyWhenNoRegimenForm() {
		lookups.regimenForm = null;

		YellowCardData card = card();

		assertTrue(card.getWarnings().contains(YellowCardData.WARN_NO_REGIMEN_FORM));
		assertNull(card.getRegimenTypeCode());
		assertNull(card.getRegimenDate());
		assertTrue(card.getPrimaryDiseaseMedicines().isEmpty());
	}

	@Test
	public void shouldMapShortRegimen() {
		regimen.sldRegimenType = lookups.concept(MdrtbConcepts.SHORT_MDR_REGIMEN);

		YellowCardData card = card();

		assertEquals(YellowCardData.REGIMEN_SHORT_DR, card.getRegimenTypeCode());
		assertEquals("SHORT MDR REGIMEN", card.getRegimenTypeName());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_NO_REGIMEN_FORM));
	}

	@Test
	public void shouldMapEveryIndividualRegimenToIndividualizedRegimen() {
		for (String type : Arrays.asList(MdrtbConcepts.INDIVIDUAL_WITH_BEDAQUILINE, MdrtbConcepts.INDIVIDUAL_WITH_DELAMANID,
		    MdrtbConcepts.INDIVIDUAL_WITH_BEDAQUILINE_AND_DELAMANID, MdrtbConcepts.INDIVIDUAL_WITH_CLOFAZIMIN_AND_LINEZOLID)) {
			regimen.sldRegimenType = lookups.concept(type);

			YellowCardData card = card();

			assertEquals(type, YellowCardData.REGIMEN_INDIVIDUALIZED_DR, card.getRegimenTypeCode());
			assertFalse(type, card.getWarnings().contains(YellowCardData.WARN_REGIMEN_NOT_ON_CARD));
		}
	}

	@Test
	public void shouldNotTickARegimenBoxForRegimensThatAreNotOnTheCard() {
		for (String type : Arrays.asList(MdrtbConcepts.STANDARD_MDR_REGIMEN, MdrtbConcepts.OTHER_MDRTB_REGIMEN)) {
			regimen.sldRegimenType = lookups.concept(type);

			YellowCardData card = card();

			assertEquals(type, YellowCardData.REGIMEN_NOT_ON_CARD, card.getRegimenTypeCode());
			assertEquals(type, type, card.getRegimenTypeName());
			assertTrue(type, card.getWarnings().contains(YellowCardData.WARN_REGIMEN_NOT_ON_CARD));
		}
	}

	@Test
	public void shouldLeaveRegimenTypeEmptyWhenRegimenFormHasNoType() {
		regimen.sldRegimenType = null;

		YellowCardData card = card();

		assertNull(card.getRegimenTypeCode());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_REGIMEN_NOT_ON_CARD));
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_NO_REGIMEN_FORM));
	}

	@Test
	public void shouldFillRegimenDateFromCouncilDate() {
		regimen.councilDate = date(2026, 5, 2);
		assertEquals("2026-05-02", card().getRegimenDate());
	}

	@Test
	public void shouldTrimTrailingSeparatorOfDiagnosticSummary() {
		form.diagnosticSummary = "Clinical screen, Audiogram, ECG, ";
		assertEquals("Clinical screen, Audiogram, ECG", card().getDiagnosticSummary());
	}

	@Test
	public void shouldReturnNullDiagnosticSummaryWhenNothingWasDone() {
		form.diagnosticSummary = "";
		assertNull(card().getDiagnosticSummary());
		form.diagnosticSummary = " , ";
		assertNull(card().getDiagnosticSummary());
		form.diagnosticSummary = null;
		assertNull(card().getDiagnosticSummary());
	}

	@Test
	public void shouldCopyClinicianNotesAndSuspectedDrugTextAsEntered() {
		form.comments = "Tinnitus reported at month 3.";
		form.suspectedDrugText = "Am, Lzd";

		YellowCardData card = card();

		assertEquals("Tinnitus reported at month 3.", card.getClinicianNotes());
		assertEquals("Am, Lzd", card.getSuspectedDrugsText());
	}

	// =============================================================================================
	// Section 4 - medicines for the primary disease (from the regimen form)
	// =============================================================================================

	@Test
	public void shouldListOnlyDrugsWithADoseInRegimenSummaryOrder() {
		regimen.doses.put("Bdq", 4.0);
		regimen.doses.put("Am", 3.0);
		regimen.doses.put("Lzd", 0.5);

		List<YellowCardData.Medicine> medicines = card().getPrimaryDiseaseMedicines();

		assertEquals(3, medicines.size());
		assertMedicine(medicines.get(0), "AMIKACIN", "mdrtb.pv.amDose", 3.0);
		assertMedicine(medicines.get(1), "LINEZOLID", "mdrtb.pv.lzdDose", 0.5);
		assertMedicine(medicines.get(2), "BEDAQUILINE", "mdrtb.pv.bdqDose", 4.0);
	}

	@Test
	public void shouldListEveryRegimenDrugWithItsLabelCode() {
		String[][] expected = { { "Cm", "CAPREOMYCIN", "mdrtb.pv.cmDose" }, { "Am", "AMIKACIN", "mdrtb.pv.amDose" },
		        { "Mfx", "MOXIFLOXACIN", "mdrtb.pv.mfxDose" }, { "Lfx", "LEVOFLOXACIN", "mdrtb.pv.lfxDose" },
		        { "Pto", "PROTHIONAMIDE", "mdrtb.pv.ptoDose" }, { "Cs", "CYCLOSERINE", "mdrtb.pv.csDose" },
		        { "PAS", "P-AMINOSALICYLIC ACID", "mdrtb.pv.pasDose" }, { "Z", "PYRAZINAMIDE", "mdrtb.pv.zDose" },
		        { "E", "ETHAMBUTOL", "mdrtb.pv.eDose" }, { "H", "ISONIAZID", "mdrtb.pv.hDose" },
		        { "Lzd", "LINEZOLID", "mdrtb.pv.lzdDose" }, { "Cfz", "CLOFAZIMINE", "mdrtb.pv.cfzDose" },
		        { "Bdq", "BEDAQUILINE", "mdrtb.pv.bdqDose" }, { "Dlm", "DELAMANID", "mdrtb.pv.dlmDose" },
		        { "Imp", "IMIPENEM", "mdrtb.pv.impDose" }, { "HR", "HR", "mdrtb.pv.hrDose" },
		        { "HRZE", "HRZE", "mdrtb.pv.hrzeDose" }, { "S", "STREPTOMYCIN", "mdrtb.pv.sDose" },
		        { "Amx", "AMOXICILLIN AND CLAVULANIC ACID", "mdrtb.pv.amxDose" } };
		double dose = 1;
		for (String[] row : expected) {
			regimen.doses.put(row[0], dose++);
		}

		List<YellowCardData.Medicine> medicines = card().getPrimaryDiseaseMedicines();

		assertEquals(expected.length, medicines.size());
		for (int i = 0; i < expected.length; i++) {
			assertMedicine(medicines.get(i), expected[i][1], expected[i][2], (double) (i + 1));
		}
	}

	@Test
	public void shouldUseShortNameWhenDrugConceptIsMissingInTheDatabase() {
		lookups.missing.add(MdrtbConcepts.BEDAQUILINE);
		regimen.doses.put("Bdq", 4.0);

		assertMedicine(card().getPrimaryDiseaseMedicines().get(0), "Bdq", "mdrtb.pv.bdqDose", 4.0);
	}

	@Test
	public void shouldAddOtherDrugWithNameAndDoseAsLastRow() {
		regimen.doses.put("Bdq", 4.0);
		regimen.otherDrug1Name = "Pretomanid";
		regimen.otherDrug1Dose = 1.0;

		List<YellowCardData.Medicine> medicines = card().getPrimaryDiseaseMedicines();

		assertEquals(2, medicines.size());
		assertMedicine(medicines.get(1), "Pretomanid", "mdrtb.pv.otherDrug1Dose", 1.0);
	}

	@Test
	public void shouldAddOtherDrugWhenOnlyItsNameOrOnlyItsDoseIsFilled() {
		regimen.otherDrug1Name = "Pretomanid";
		assertMedicine(card().getPrimaryDiseaseMedicines().get(0), "Pretomanid", "mdrtb.pv.otherDrug1Dose", null);

		regimen.otherDrug1Name = null;
		regimen.otherDrug1Dose = 2.0;
		assertMedicine(card().getPrimaryDiseaseMedicines().get(0), null, "mdrtb.pv.otherDrug1Dose", 2.0);
	}

	@Test
	public void shouldNotAddOtherDrugWhenNameIsBlankAndDoseIsEmpty() {
		regimen.otherDrug1Name = "   ";
		assertTrue(card().getPrimaryDiseaseMedicines().isEmpty());
	}

	// =============================================================================================
	// Section 3 - dates, description, seriousness (33), special interest (34)
	// =============================================================================================

	@Test
	public void shouldFillOnsetDateFromEncounterDate() {
		assertEquals("2026-08-04", card().getOnsetDate());
	}

	@Test
	public void shouldNotShiftDatesLateInTheDay() {
		Calendar late = new GregorianCalendar(2026, Calendar.AUGUST, 4, 23, 59, 59);
		encounter.setEncounterDatetime(late.getTime());
		assertEquals("2026-08-04", card().getOnsetDate());
	}

	@Test
	public void shouldFillCessationDateOnlyWhenTheEventResolved() {
		form.outcomeDate = date(2026, 9, 1);

		form.actionOutcome = lookups.concept(MdrtbConcepts.RESOLVED);
		assertEquals("2026-09-01", card().getCessationDate());

		form.actionOutcome = lookups.concept(MdrtbConcepts.RESOLVED_WITH_SEQUELAE);
		assertEquals("2026-09-01", card().getCessationDate());

		for (String notEnded : Arrays.asList(MdrtbConcepts.FATAL, MdrtbConcepts.RESOLVING, MdrtbConcepts.NOT_RESOLVED)) {
			form.actionOutcome = lookups.concept(notEnded);
			assertNull(notEnded, card().getCessationDate());
		}

		form.actionOutcome = null;
		assertNull(card().getCessationDate());
	}

	@Test
	public void shouldFillAdverseEventDescriptionFromConceptName() {
		form.adverseEvent = lookups.concept(MdrtbConcepts.PERIPHERAL_NEUROPATHY);
		assertEquals("PERIPHERAL NEUROPATHY", card().getAdverseEventName());
	}

	@Test
	public void shouldUseTheLocaleFromLookupsForConceptNames() {
		Concept tinnitus = lookups.concept(MdrtbConcepts.TINNITUS);
		tinnitus.addName(new ConceptName("ШУМ В УШАХ", new Locale("ru")));
		form.adverseEvent = tinnitus;
		// only concepts that have a Russian name may be on the form: falling back to another
		// language goes through OpenMRS LocaleUtility, which needs a running OpenMRS
		form.causalityDrug1 = null;
		form.causalityResult1 = null;

		lookups.locale = new Locale("ru");

		assertEquals("ШУМ В УШАХ", card().getAdverseEventName());
	}

	@Test
	public void shouldMapEverySeriousnessAnswerThatHasABox() {
		Map<String, String> expected = new LinkedHashMap<>();
		expected.put(MdrtbConcepts.DEATH, YellowCardData.SAE_DEATH);
		expected.put(MdrtbConcepts.LIFE_THREATENING_EXPERIENCE, YellowCardData.SAE_LIFE_THREATENING);
		expected.put(MdrtbConcepts.DISABILITY, YellowCardData.SAE_DISABILITY);
		expected.put(MdrtbConcepts.CONGENITAL_ANOMALY, YellowCardData.SAE_CONGENITAL_ANOMALY);

		for (Map.Entry<String, String> entry : expected.entrySet()) {
			form.typeOfSAE = lookups.concept(entry.getKey());

			YellowCardData card = card();

			assertEquals(entry.getKey(), entry.getValue(), card.getSaeTypeCode());
			assertEquals(entry.getKey(), entry.getKey(), card.getSaeTypeName());
			assertFalse(entry.getKey(), card.getWarnings().contains(YellowCardData.WARN_SAE_NOT_ON_CARD));
			assertFalse(entry.getKey(), card.getWarnings().contains(YellowCardData.WARN_SAE_HOSPITALIZATION_NOT_SPLIT));
		}
	}

	@Test
	public void shouldFlagHospitalizationBecauseInitialAndProlongedCannotBeTold() {
		for (String hospitalization : Arrays.asList(MdrtbConcepts.HOSPITALIZATION_WORKFLOW,
		    MdrtbConcepts.PATIENT_HOSPITALIZED)) {
			form.typeOfSAE = lookups.concept(hospitalization);

			YellowCardData card = card();

			assertEquals(hospitalization, YellowCardData.SAE_HOSPITALIZATION, card.getSaeTypeCode());
			assertTrue(hospitalization, card.getWarnings().contains(YellowCardData.WARN_SAE_HOSPITALIZATION_NOT_SPLIT));
		}
	}

	@Test
	public void shouldNotTickSeriousnessForUnknownAnswer() {
		form.typeOfSAE = lookups.concept("SOME NEW SAE ANSWER");

		YellowCardData card = card();

		assertEquals(YellowCardData.SAE_NOT_ON_CARD, card.getSaeTypeCode());
		assertEquals("SOME NEW SAE ANSWER", card.getSaeTypeName());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_SAE_NOT_ON_CARD));
	}

	@Test
	public void shouldLeaveSeriousnessEmptyWhenNotRecorded() {
		YellowCardData card = card();
		assertNull(card.getSaeTypeCode());
		assertNull(card.getSaeTypeName());
	}

	@Test
	public void shouldMapEverySpecialInterestAnswerThatHasABox() {
		Map<String, String> expected = new LinkedHashMap<>();
		expected.put(MdrtbConcepts.PERIPHERAL_NEUROPATHY, YellowCardData.AESI_PERIPHERAL_NEUROPATHY);
		expected.put(MdrtbConcepts.PSYCHIATRIC_DISORDER, YellowCardData.AESI_PSYCHIATRIC_CNS);
		expected.put(MdrtbConcepts.HEARING_DISORDER, YellowCardData.AESI_OTOTOXICITY);
		expected.put(MdrtbConcepts.MYELOSUPPRESSION, YellowCardData.AESI_MYELOSUPPRESSION);
		expected.put(MdrtbConcepts.QT_PROLONGATION, YellowCardData.AESI_QT_PROLONGATION);
		expected.put(MdrtbConcepts.LACTIC_ACIDOSIS, YellowCardData.AESI_LACTIC_ACIDOSIS);
		expected.put(MdrtbConcepts.HEPATITIS_AE, YellowCardData.AESI_HEPATITIS);
		expected.put(MdrtbConcepts.HYPOTHYROIDISM, YellowCardData.AESI_HYPOTHYROIDISM);
		expected.put(MdrtbConcepts.HYPOKALEMIA, YellowCardData.AESI_HYPOKALEMIA);
		expected.put(MdrtbConcepts.PANCREATITIS, YellowCardData.AESI_PANCREATITIS);
		expected.put(MdrtbConcepts.PHOSPHOLIPIDOSIS, YellowCardData.AESI_PHOSPHOLIPIDOSIS);
		expected.put(MdrtbConcepts.RENAL_FAILURE, YellowCardData.AESI_SEVERE_RENAL_IMPAIRMENT);

		for (Map.Entry<String, String> entry : expected.entrySet()) {
			form.typeOfSpecialEvent = lookups.concept(entry.getKey());

			YellowCardData card = card();

			assertEquals(entry.getKey(), entry.getValue(), card.getAesiTypeCode());
			assertFalse(entry.getKey(), card.getWarnings().contains(YellowCardData.WARN_AESI_PRINTED_AS_OTHER));
		}
	}

	@Test
	public void shouldPrintVisionDisorderAsOtherNotAsOpticNerveDisorder() {
		form.typeOfSpecialEvent = lookups.concept(MdrtbConcepts.VISION_DISORDER);

		YellowCardData card = card();

		assertEquals(YellowCardData.AESI_OTHER, card.getAesiTypeCode());
		assertEquals("VISION DISORDER", card.getAesiTypeName());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_AESI_PRINTED_AS_OTHER));
	}

	@Test
	public void shouldLeaveSpecialInterestEmptyWhenNotRecorded() {
		YellowCardData card = card();
		assertNull(card.getAesiTypeCode());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_AESI_PRINTED_AS_OTHER));
	}

	// =============================================================================================
	// Field 44 - re-challenge
	// =============================================================================================

	@Test
	public void shouldAnswerYesWhenRechallengeReproducedTheEvent() {
		form.drugRechallenge = lookups.concept(MdrtbConcepts.RECURRENCE_OF_EVENT);
		assertEquals(Boolean.TRUE, card().getRechallengeReproducedReaction());
	}

	@Test
	public void shouldAnswerNoWhenRechallengeDidNotReproduceTheEvent() {
		form.drugRechallenge = lookups.concept(MdrtbConcepts.NO_RECURRENCE);
		assertEquals(Boolean.FALSE, card().getRechallengeReproducedReaction());
	}

	@Test
	public void shouldLeaveRechallengeEmptyWhenThereWasNoResult() {
		for (String answer : Arrays.asList(MdrtbConcepts.NO_RECHALLENGE, MdrtbConcepts.UNKNOWN_RESULT)) {
			form.drugRechallenge = lookups.concept(answer);
			assertNull(answer, card().getRechallengeReproducedReaction());
		}
		form.drugRechallenge = null;
		assertNull(card().getRechallengeReproducedReaction());
	}

	// =============================================================================================
	// Section 6 - causality
	// =============================================================================================

	@Test
	public void shouldMapEveryCausalityAnswerThatHasABox() {
		Map<String, String> expected = new LinkedHashMap<>();
		expected.put(MdrtbConcepts.DEFINITE, YellowCardData.CAUSALITY_CERTAIN);
		expected.put(MdrtbConcepts.PROBABLE, YellowCardData.CAUSALITY_PROBABLE);
		expected.put(MdrtbConcepts.POSSIBLE, YellowCardData.CAUSALITY_POSSIBLE);
		expected.put(MdrtbConcepts.NOT_CLASSIFIED, YellowCardData.CAUSALITY_UNCLASSIFIED);

		for (Map.Entry<String, String> entry : expected.entrySet()) {
			form.causalityResult1 = lookups.concept(entry.getKey());

			YellowCardData card = card();

			assertEquals(entry.getKey(), entry.getValue(), card.getCausalityCode());
			assertEquals(entry.getKey(), entry.getKey(), card.getCausalityName());
			assertFalse(entry.getKey(), card.getWarnings().contains(YellowCardData.WARN_CAUSALITY_NOT_ON_CARD));
		}
	}

	@Test
	public void shouldNotTickAnyCausalityBoxForSuspected() {
		form.causalityResult1 = lookups.concept(MdrtbConcepts.SUSPECTED);

		YellowCardData card = card();

		assertEquals(YellowCardData.CAUSALITY_NOT_ON_CARD, card.getCausalityCode());
		assertEquals("SUSPECTED", card.getCausalityName());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_CAUSALITY_NOT_ON_CARD));
	}

	@Test
	public void shouldLeaveCausalityEmptyWhenDrugHasNoAssessment() {
		form.causalityResult1 = null;

		YellowCardData card = card();

		assertEquals("AMIKACIN", card.getSuspectedDrugName());
		assertNull(card.getCausalityCode());
		assertFalse(card.getWarnings().contains(YellowCardData.WARN_CAUSALITY_NOT_ON_CARD));
	}

	// =============================================================================================
	// Section 7 - reporter
	// =============================================================================================

	@Test
	public void shouldFillReporterFromEncounterProviderLocationAndYellowCardDate() {
		Person provider = new Person(9);
		provider.addName(new PersonName("Malika", null, "Saidova"));
		form.provider = provider;
		form.yellowCardDate = date(2026, 9, 10);

		YellowCardData card = card();

		assertEquals("Saidova Malika", card.getReporterName());
		assertEquals("City TB Centre Dushanbe", card.getReporterPlaceOfWork());
		assertEquals("2026-09-10", card.getReportDate());
	}

	@Test
	public void shouldReturnNullReporterNameWhenProviderHasNoName() {
		form.provider = new Person(9);
		assertNull(card().getReporterName());
	}

	@Test
	public void shouldLeaveReporterEmptyWhenNotRecorded() {
		encounter.setLocation(null);

		YellowCardData card = card();

		assertNull(card.getReporterName());
		assertNull(card.getReporterPlaceOfWork());
		assertNull(card.getReportDate());
	}

	// =============================================================================================
	// Concept matching safety
	// =============================================================================================

	@Test
	public void shouldMatchConceptsByIdNotByObjectInstance() {
		Concept resolved = lookups.concept(MdrtbConcepts.RESOLVED);
		Concept sameConceptOtherSession = new Concept(resolved.getConceptId());
		sameConceptOtherSession.addName(new ConceptName("RESOLVED", Locale.ENGLISH));
		form.actionOutcome = sameConceptOtherSession;

		assertEquals(YellowCardData.OUTCOME_RECOVERED_WITHOUT_SEQUELAE, card().getOutcomeCode());
	}

	@Test
	public void shouldNotMatchAnythingWhenMdrtbConceptIsMissingInTheDatabase() {
		// the recorded answer exists, but getConcept(RESOLVED) finds nothing
		form.actionOutcome = lookups.concept(MdrtbConcepts.RESOLVED);
		lookups.missing.add(MdrtbConcepts.RESOLVED);

		YellowCardData card = card();

		assertEquals(YellowCardData.OUTCOME_NOT_ON_CARD, card.getOutcomeCode());
		assertTrue(card.getWarnings().contains(YellowCardData.WARN_OUTCOME_NOT_ON_CARD));
	}

	@Test
	public void shouldNotMatchConceptWithoutId() {
		Concept unsaved = new Concept();
		unsaved.addName(new ConceptName("DEATH", Locale.ENGLISH));
		form.typeOfSAE = unsaved;

		assertEquals(YellowCardData.SAE_NOT_ON_CARD, card().getSaeTypeCode());
	}

	@Test
	public void shouldProduceACardWithOnlyWarningsForAnAlmostEmptyForm() {
		patient.setBirthdate(null);
		patient.setGender(null);
		lookups.regimenForm = null;
		form.causalityDrug1 = null;
		form.causalityResult1 = null;

		YellowCardData card = card();

		assertNull(card.getDateOfBirth());
		assertNull(card.getOutcomeCode());
		assertNull(card.getSaeTypeCode());
		assertNull(card.getAesiTypeCode());
		assertNull(card.getCausalityCode());
		assertNull(card.getRechallengeReproducedReaction());
		assertTrue(card.getPrimaryDiseaseMedicines().isEmpty());
		assertTrue(card.getWarnings().containsAll(
		    Arrays.asList(YellowCardData.WARN_NO_REGIMEN_FORM, YellowCardData.WARN_NO_CAUSALITY_DRUG)));
	}

	// =============================================================================================
	// Codes are read by the Django front-end (utilities/yellow_card_util.py) - they must not change
	// =============================================================================================

	@Test
	public void codesSharedWithTheFrontEndShouldNotChange() {
		assertEquals("RECOVERED_WITHOUT_SEQUELAE", YellowCardData.OUTCOME_RECOVERED_WITHOUT_SEQUELAE);
		assertEquals("RECOVERED_WITH_SEQUELAE", YellowCardData.OUTCOME_RECOVERED_WITH_SEQUELAE);
		assertEquals("SHORT_REGIMEN_DR_TB", YellowCardData.REGIMEN_SHORT_DR);
		assertEquals("INDIVIDUALIZED_REGIMEN_DR_TB", YellowCardData.REGIMEN_INDIVIDUALIZED_DR);
		assertEquals("PATIENT_DEATH", YellowCardData.SAE_DEATH);
		assertEquals("LIFE_THREATENING", YellowCardData.SAE_LIFE_THREATENING);
		assertEquals("HOSPITALIZATION", YellowCardData.SAE_HOSPITALIZATION);
		assertEquals("DISABILITY", YellowCardData.SAE_DISABILITY);
		assertEquals("CONGENITAL_ANOMALY", YellowCardData.SAE_CONGENITAL_ANOMALY);
		assertEquals("PERIPHERAL_NEUROPATHY", YellowCardData.AESI_PERIPHERAL_NEUROPATHY);
		assertEquals("PSYCHIATRIC_CNS", YellowCardData.AESI_PSYCHIATRIC_CNS);
		assertEquals("OTOTOXICITY", YellowCardData.AESI_OTOTOXICITY);
		assertEquals("MYELOSUPPRESSION", YellowCardData.AESI_MYELOSUPPRESSION);
		assertEquals("QT_PROLONGATION", YellowCardData.AESI_QT_PROLONGATION);
		assertEquals("LACTIC_ACIDOSIS", YellowCardData.AESI_LACTIC_ACIDOSIS);
		assertEquals("HEPATITIS", YellowCardData.AESI_HEPATITIS);
		assertEquals("HYPOTHYROIDISM", YellowCardData.AESI_HYPOTHYROIDISM);
		assertEquals("HYPOKALEMIA", YellowCardData.AESI_HYPOKALEMIA);
		assertEquals("PANCREATITIS", YellowCardData.AESI_PANCREATITIS);
		assertEquals("PHOSPHOLIPIDOSIS", YellowCardData.AESI_PHOSPHOLIPIDOSIS);
		assertEquals("SEVERE_RENAL_IMPAIRMENT", YellowCardData.AESI_SEVERE_RENAL_IMPAIRMENT);
		assertEquals("OTHER", YellowCardData.AESI_OTHER);
		assertEquals("CERTAIN", YellowCardData.CAUSALITY_CERTAIN);
		assertEquals("PROBABLE", YellowCardData.CAUSALITY_PROBABLE);
		assertEquals("POSSIBLE", YellowCardData.CAUSALITY_POSSIBLE);
		assertEquals("UNCLASSIFIED", YellowCardData.CAUSALITY_UNCLASSIFIED);
		assertEquals("NOT_ON_CARD", YellowCardData.OUTCOME_NOT_ON_CARD);
		assertEquals("NOT_ON_CARD", YellowCardData.REGIMEN_NOT_ON_CARD);
		assertEquals("NOT_ON_CARD", YellowCardData.SAE_NOT_ON_CARD);
		assertEquals("NOT_ON_CARD", YellowCardData.CAUSALITY_NOT_ON_CARD);
	}

	@Test
	public void warningCodesShouldMatchTheMessageKeys() {
		// Each warning is shown as message mdrtb.yellowCard.warning.<CODE>; renaming a code breaks the label.
		List<String> codes = Arrays.asList(YellowCardData.WARN_OUTCOME_NOT_ON_CARD, YellowCardData.WARN_REGIMEN_NOT_ON_CARD,
		    YellowCardData.WARN_NO_REGIMEN_FORM, YellowCardData.WARN_SAE_HOSPITALIZATION_NOT_SPLIT,
		    YellowCardData.WARN_SAE_NOT_ON_CARD, YellowCardData.WARN_AESI_PRINTED_AS_OTHER,
		    YellowCardData.WARN_CAUSALITY_NOT_ON_CARD, YellowCardData.WARN_NO_CAUSALITY_DRUG,
		    YellowCardData.WARN_NO_OPENMRS_ID);
		assertEquals(Arrays.asList("OUTCOME_NOT_ON_CARD", "REGIMEN_NOT_ON_CARD", "NO_REGIMEN_FORM_AT_ONSET",
		    "SAE_HOSPITALIZATION_NOT_SPLIT", "SAE_NOT_ON_CARD", "AESI_PRINTED_AS_OTHER", "CAUSALITY_NOT_ON_CARD",
		    "NO_CAUSALITY_DRUG", "NO_OPENMRS_ID"), codes);
	}

	// =============================================================================================
	// helpers
	// =============================================================================================

	/** Builds the cards of the current form. */
	private List<YellowCardData> cards() {
		lookups.regimenLookups.clear();
		return YellowCardData.fromAdverseEventsForm(form, lookups);
	}

	/** The only card of the current form (the form has one causality drug in setUp). */
	private YellowCardData card() {
		List<YellowCardData> cards = cards();
		assertEquals("expected exactly one card", 1, cards.size());
		return cards.get(0);
	}

	private static void assertCard(YellowCardData card, int index, String drug, String causalityCode) {
		assertEquals(index, card.getCausalityIndex());
		assertEquals(drug, card.getSuspectedDrugName());
		assertEquals(causalityCode, card.getCausalityCode());
	}

	private static void assertMedicine(YellowCardData.Medicine medicine, String name, String labelCode, Double dose) {
		assertEquals(name, medicine.getName());
		assertEquals(labelCode, medicine.getDoseLabelCode());
		assertEquals(dose, medicine.getDose());
	}

	private static Date date(int year, int month, int day) {
		return new GregorianCalendar(year, month - 1, day).getTime();
	}

	// =============================================================================================
	// fakes
	// =============================================================================================

	/**
	 * Stands in for MdrtbService / Context. Every lookup string gets its own concept, named after the
	 * lookup string, with a stable id. Add a lookup to {@link #missing} to simulate a concept that does
	 * not exist in the database.
	 */
	static class FakeLookups implements YellowCardData.Lookups {

		private final Map<String, Concept> concepts = new HashMap<>();

		final List<String> missing = new ArrayList<>();

		Locale locale = Locale.ENGLISH;

		RegimenForm regimenForm;

		PatientIdentifierType openmrsIdType;

		final List<RegimenLookup> regimenLookups = new ArrayList<>();

		/** The concept for a lookup string (created on first use). */
		Concept concept(String lookup) {
			Concept concept = concepts.get(lookup);
			if (concept == null) {
				concept = new Concept(1000 + concepts.size());
				concept.addName(new ConceptName(lookup, Locale.ENGLISH));
				concepts.put(lookup, concept);
			}
			return concept;
		}

		@Override
		public Concept getConcept(String lookup) {
			return missing.contains(lookup) ? null : concept(lookup);
		}

		@Override
		public Locale getLocale() {
			return locale;
		}

		@Override
		public RegimenForm getCurrentRegimenForm(Patient patient, Date onsetDate) {
			regimenLookups.add(new RegimenLookup(patient, onsetDate));
			return regimenForm;
		}

		@Override
		public PatientIdentifierType getOpenmrsIdentifierType() {
			return openmrsIdType;
		}
	}

	static class RegimenLookup {

		final Patient patient;

		final Date date;

		RegimenLookup(Patient patient, Date date) {
			this.patient = patient;
			this.date = date;
		}
	}

	/** An Adverse Events form whose answers are plain fields (the real form reads obs through OpenMRS). */
	static class FakeAeForm extends AdverseEventsForm {

		Concept causalityDrug1, causalityDrug2, causalityDrug3;

		Concept causalityResult1, causalityResult2, causalityResult3;

		Concept actionOutcome, adverseEvent, typeOfSAE, typeOfSpecialEvent, drugRechallenge;

		Date outcomeDate, yellowCardDate;

		String diagnosticSummary, comments, suspectedDrugText;

		Person provider;

		FakeAeForm(Encounter encounter) {
			super(encounter);
		}

		@Override
		public Concept getCausalityDrug1() {
			return causalityDrug1;
		}

		@Override
		public Concept getCausalityDrug2() {
			return causalityDrug2;
		}

		@Override
		public Concept getCausalityDrug3() {
			return causalityDrug3;
		}

		@Override
		public Concept getCausalityAssessmentResult1() {
			return causalityResult1;
		}

		@Override
		public Concept getCausalityAssessmentResult2() {
			return causalityResult2;
		}

		@Override
		public Concept getCausalityAssessmentResult3() {
			return causalityResult3;
		}

		@Override
		public Concept getActionOutcome() {
			return actionOutcome;
		}

		@Override
		public Date getOutcomeDate() {
			return outcomeDate;
		}

		@Override
		public Concept getAdverseEvent() {
			return adverseEvent;
		}

		@Override
		public Concept getTypeOfSAE() {
			return typeOfSAE;
		}

		@Override
		public Concept getTypeOfSpecialEvent() {
			return typeOfSpecialEvent;
		}

		@Override
		public Concept getDrugRechallenge() {
			return drugRechallenge;
		}

		@Override
		public Date getYellowCardDate() {
			return yellowCardDate;
		}

		@Override
		public String getDiagnosticSummary() {
			return diagnosticSummary;
		}

		@Override
		public String getComments() {
			return comments;
		}

		@Override
		public String getSuspectedDrug() {
			return suspectedDrugText;
		}

		@Override
		public Person getProvider() {
			return provider;
		}
	}

	/**
	 * A regimen form. Doses are kept by the drug's short name as used in RegimenForm.getRegimenSummary():
	 * Cm, Am, Mfx, Lfx, Pto, Cs, PAS, Z, E, H, Lzd, Cfz, Bdq, Dlm, Imp, HR, HRZE, S, Amx.
	 */
	static class FakeRegimenForm extends RegimenForm {

		Concept sldRegimenType;

		Date councilDate;

		final Map<String, Double> doses = new HashMap<>();

		String otherDrug1Name;

		Double otherDrug1Dose;

		FakeRegimenForm() {
			super(new Encounter());
		}

		@Override
		public Concept getSldRegimenType() {
			return sldRegimenType;
		}

		@Override
		public Date getCouncilDate() {
			return councilDate;
		}

		@Override
		public Double getCmDose() {
			return doses.get("Cm");
		}

		@Override
		public Double getAmDose() {
			return doses.get("Am");
		}

		@Override
		public Double getMfxDose() {
			return doses.get("Mfx");
		}

		@Override
		public Double getLfxDose() {
			return doses.get("Lfx");
		}

		@Override
		public Double getPtoDose() {
			return doses.get("Pto");
		}

		@Override
		public Double getCsDose() {
			return doses.get("Cs");
		}

		@Override
		public Double getPasDose() {
			return doses.get("PAS");
		}

		@Override
		public Double getZDose() {
			return doses.get("Z");
		}

		@Override
		public Double getEDose() {
			return doses.get("E");
		}

		@Override
		public Double getHDose() {
			return doses.get("H");
		}

		@Override
		public Double getLzdDose() {
			return doses.get("Lzd");
		}

		@Override
		public Double getCfzDose() {
			return doses.get("Cfz");
		}

		@Override
		public Double getBdqDose() {
			return doses.get("Bdq");
		}

		@Override
		public Double getDlmDose() {
			return doses.get("Dlm");
		}

		@Override
		public Double getImpDose() {
			return doses.get("Imp");
		}

		@Override
		public Double getHrDose() {
			return doses.get("HR");
		}

		@Override
		public Double getHrzeDose() {
			return doses.get("HRZE");
		}

		@Override
		public Double getSDose() {
			return doses.get("S");
		}

		@Override
		public Double getAmxDose() {
			return doses.get("Amx");
		}

		@Override
		public String getOtherDrug1Name() {
			return otherDrug1Name;
		}

		@Override
		public Double getOtherDrug1Dose() {
			return otherDrug1Dose;
		}
	}
}
