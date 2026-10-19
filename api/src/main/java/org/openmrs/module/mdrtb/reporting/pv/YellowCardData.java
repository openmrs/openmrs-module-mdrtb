package org.openmrs.module.mdrtb.reporting.pv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;

/**
 * Data behind Yellow Card
 * One Adverse Events form can produce up to THREE cards: one for every filled causality pair
 * (CAUSALITY DRUG n + CAUSALITY ASSESSMENT RESULT n). When no causality drug is filled, a single
 * card is produced with {@code causalityIndex = 0} so that the event can still be printed.
 * RULES - read before changing anything here:
 * Only values that map 1:1 to a recorded observation are filled. Nothing is inferred. Yellow Card
 * questions that the system does not record (6-14, 17, 19-29, 35, 43, 45-47 and the "reported for
 * the first time" header) are NOT part of this class: the template prints them empty for hand-filling.</li>
 * Checkbox answers are returned as CODES (the constants below), never as translated text. A
 * recorded answer that has no exact box on the card returns the OTHER/UNMAPPED code, keeps its concept
 * name in the matching *Name field, and adds a warning to {@link #getWarnings()}. It is never silently
 * moved to a "close enough" box.</li>
 */
public class YellowCardData {

	/** Standard OpenMRS "OpenMRS ID" identifier type (field 3). */
	public static final String OPENMRS_ID_IDENTIFIER_TYPE_UUID = "8d793bee-c2cc-11de-8d13-0010c6dffd0f";

	private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

	// ---- Field 15: outcome of the adverse effect ----
	public static final String OUTCOME_RECOVERED_WITHOUT_SEQUELAE = "RECOVERED_WITHOUT_SEQUELAE";

	public static final String OUTCOME_RECOVERED_WITH_SEQUELAE = "RECOVERED_WITH_SEQUELAE";

	/** Outcome recorded (e.g. fatal, resolving, not resolved) but the card has no box for it. */
	public static final String OUTCOME_NOT_ON_CARD = "NOT_ON_CARD";

	// ---- Field 16: treatment regimen ----
	public static final String REGIMEN_SHORT_DR = "SHORT_REGIMEN_DR_TB";

	public static final String REGIMEN_INDIVIDUALIZED_DR = "INDIVIDUALIZED_REGIMEN_DR_TB";

	/** Regimen type recorded (e.g. standard MDR, other) but the card has no box for it. */
	public static final String REGIMEN_NOT_ON_CARD = "NOT_ON_CARD";

	// ---- Field 33: seriousness ----
	public static final String SAE_DEATH = "PATIENT_DEATH";

	public static final String SAE_LIFE_THREATENING = "LIFE_THREATENING";

	/** The system cannot tell initial from prolonged hospitalization: neither box is ticked. */
	public static final String SAE_HOSPITALIZATION = "HOSPITALIZATION";

	public static final String SAE_DISABILITY = "DISABILITY";

	public static final String SAE_CONGENITAL_ANOMALY = "CONGENITAL_ANOMALY";

	public static final String SAE_NOT_ON_CARD = "NOT_ON_CARD";

	// ---- Field 34: adverse events of special interest ----
	public static final String AESI_PERIPHERAL_NEUROPATHY = "PERIPHERAL_NEUROPATHY";

	public static final String AESI_PSYCHIATRIC_CNS = "PSYCHIATRIC_CNS";

	public static final String AESI_OTOTOXICITY = "OTOTOXICITY";

	public static final String AESI_MYELOSUPPRESSION = "MYELOSUPPRESSION";

	public static final String AESI_QT_PROLONGATION = "QT_PROLONGATION";

	public static final String AESI_LACTIC_ACIDOSIS = "LACTIC_ACIDOSIS";

	public static final String AESI_HEPATITIS = "HEPATITIS";

	public static final String AESI_HYPOTHYROIDISM = "HYPOTHYROIDISM";

	public static final String AESI_HYPOKALEMIA = "HYPOKALEMIA";

	public static final String AESI_PANCREATITIS = "PANCREATITIS";

	public static final String AESI_PHOSPHOLIPIDOSIS = "PHOSPHOLIPIDOSIS";

	public static final String AESI_SEVERE_RENAL_IMPAIRMENT = "SEVERE_RENAL_IMPAIRMENT";

	/**
	 * Printed in "Other (specify)" together with {@link #getAesiTypeName()}. Note: "Optic nerve disorder"
	 * has no concept of its own; VISION DISORDER is broader, so it is deliberately printed as OTHER.
	 */
	public static final String AESI_OTHER = "OTHER";

	// ---- Field 6 of section 6: causality ----
	public static final String CAUSALITY_CERTAIN = "CERTAIN";

	public static final String CAUSALITY_PROBABLE = "PROBABLE";

	public static final String CAUSALITY_POSSIBLE = "POSSIBLE";

	public static final String CAUSALITY_UNCLASSIFIED = "UNCLASSIFIED";

	/** e.g. SUSPECTED - no box is ticked. */
	public static final String CAUSALITY_NOT_ON_CARD = "NOT_ON_CARD";

	// ---- Warnings (shown on screen, not printed) ----
	public static final String WARN_OUTCOME_NOT_ON_CARD = "OUTCOME_NOT_ON_CARD";

	public static final String WARN_REGIMEN_NOT_ON_CARD = "REGIMEN_NOT_ON_CARD";

	public static final String WARN_NO_REGIMEN_FORM = "NO_REGIMEN_FORM_AT_ONSET";

	public static final String WARN_SAE_HOSPITALIZATION_NOT_SPLIT = "SAE_HOSPITALIZATION_NOT_SPLIT";

	public static final String WARN_SAE_NOT_ON_CARD = "SAE_NOT_ON_CARD";

	public static final String WARN_AESI_PRINTED_AS_OTHER = "AESI_PRINTED_AS_OTHER";

	public static final String WARN_CAUSALITY_NOT_ON_CARD = "CAUSALITY_NOT_ON_CARD";

	public static final String WARN_NO_CAUSALITY_DRUG = "NO_CAUSALITY_DRUG";

	public static final String WARN_NO_OPENMRS_ID = "NO_OPENMRS_ID";

	// ---------------------------------------------------------------------------------------------

	private String adverseEventsFormUuid;

	private int causalityIndex;

	private String patientUuid;

	// Section 1 - patient information
	private String patientName; // 1

	private String address; // 2

	private String openmrsIdentifier; // 3

	private String dateOfBirth; // 4

	private String gender; // 5 (M / F)

	private String outcomeCode; // 15

	private String outcomeName;

	private String regimenTypeCode; // 16

	private String regimenTypeName;

	private String diagnosticSummary; // 16 (laboratory / instrumental data)

	private String clinicianNotes; // 16

	// Section 2 - suspected medicine
	private String suspectedDrugName; // 18 (INN)

	private String suspectedDrugsText; // free text "Suspected drug(s)" of the AE form

	// Section 3 - adverse reaction
	private String onsetDate; // 30

	private String cessationDate; // 31

	private String adverseEventName; // 32

	private String saeTypeCode; // 33

	private String saeTypeName;

	private String aesiTypeCode; // 34

	private String aesiTypeName;

	// Section 4 - medicines for the primary disease (regimen form in force at onset)
	private String regimenDate;

	private List<Medicine> primaryDiseaseMedicines = new ArrayList<>();

	// Section 5
	private Boolean rechallengeReproducedReaction; // 44

	// Section 6
	private String causalityCode;

	private String causalityName;

	// Section 7 - reporter
	private String reporterName;

	private String reporterPlaceOfWork;

	private String reportDate; // Yellow card submission date

	private List<String> warnings = new ArrayList<>();

	/**
	 * One row of section 4, taken from the regimen form. {@code dose} is the number exactly as
	 * entered on the regimen form; its unit is given by the form label {@code doseLabelCode}
	 * (e.g. mdrtb.pv.bdqDose = "Bdq 100 mg").
	 */
	public static class Medicine {

		private final String name;

		private final String doseLabelCode;

		private final Double dose;

		public Medicine(String name, String doseLabelCode, Double dose) {
			this.name = name;
			this.doseLabelCode = doseLabelCode;
			this.dose = dose;
		}

		public String getName() {
			return name;
		}

		public String getDoseLabelCode() {
			return doseLabelCode;
		}

		public Double getDose() {
			return dose;
		}
	}

	/**
	 * Everything YellowCardData needs from a running OpenMRS. Production code uses {@link #CONTEXT}; unit
	 * tests pass their own implementation, so the mapping rules can be tested without a database.
	 */
	public interface Lookups {
		
		/** Same as MdrtbService.getConcept(lookup): a MdrtbConcepts constant -> Concept (null if unknown) */
		Concept getConcept(String lookup);
		
		/** Locale used for concept names */
		Locale getLocale();
		
		/** Same as MdrtbService.getCurrentRegimenForm(patient, date) */
		RegimenForm getCurrentRegimenForm(Patient patient, Date onsetDate);
		
		/** The "OpenMRS ID" identifier type, null when it does not exist */
		PatientIdentifierType getOpenmrsIdentifierType();
	}
	
	/** Lookups backed by the OpenMRS Context - used by the REST resource. */
	public static final Lookups CONTEXT = new Lookups() {
		
		@Override
		public Concept getConcept(String lookup) {
			return Context.getService(MdrtbService.class).getConcept(lookup);
		}
		
		@Override
		public Locale getLocale() {
			return Context.getLocale();
		}
		
		@Override
		public RegimenForm getCurrentRegimenForm(Patient patient, Date onsetDate) {
			return Context.getService(MdrtbService.class).getCurrentRegimenForm(patient, onsetDate);
		}
		
		@Override
		public PatientIdentifierType getOpenmrsIdentifierType() {
			return Context.getPatientService().getPatientIdentifierTypeByUuid(OPENMRS_ID_IDENTIFIER_TYPE_UUID);
		}
	};
	
	private YellowCardData() {
	}

	/**
	 * Builds the Yellow Card(s) for one Adverse Events form. Returns an empty list for a missing,
	 * voided or non-adverse-event encounter.
	 */
	public static List<YellowCardData> fromAdverseEventsForm(AdverseEventsForm aeForm) {
		return fromAdverseEventsForm(aeForm, CONTEXT);
	}
	
	/** Same as {@link #fromAdverseEventsForm(AdverseEventsForm)} with explicit lookups (unit tests). */
	public static List<YellowCardData> fromAdverseEventsForm(AdverseEventsForm aeForm, Lookups lookups) {
		List<YellowCardData> cards = new ArrayList<>();
		if (aeForm == null || aeForm.getEncounter() == null) {
			return cards;
		}
		Encounter encounter = aeForm.getEncounter();
		if (Boolean.TRUE.equals(encounter.getVoided()) || encounter.getPatient() == null) {
			return cards;
		}
		Concept[] drugs = { aeForm.getCausalityDrug1(), aeForm.getCausalityDrug2(), aeForm.getCausalityDrug3() };
		Concept[] results = { aeForm.getCausalityAssessmentResult1(), aeForm.getCausalityAssessmentResult2(),
		        aeForm.getCausalityAssessmentResult3() };
		for (int i = 0; i < drugs.length; i++) {
			if (drugs[i] != null) {
				cards.add(build(aeForm, i + 1, drugs[i], results[i], lookups));
			}
		}
		if (cards.isEmpty()) {
			YellowCardData card = build(aeForm, 0, null, null, lookups);
			card.warnings.add(WARN_NO_CAUSALITY_DRUG);
			cards.add(card);
		}
		return cards;
	}

	private static YellowCardData build(AdverseEventsForm aeForm, int causalityIndex, Concept drug, Concept causality,
	        Lookups lookups) {
		YellowCardData card = new YellowCardData();
		Encounter encounter = aeForm.getEncounter();
		Patient patient = encounter.getPatient();

		card.adverseEventsFormUuid = encounter.getUuid();
		card.causalityIndex = causalityIndex;
		card.patientUuid = patient.getUuid();

		// ---------------- Section 1 ----------------
		card.patientName = emptyToNull(joinNonEmpty(" ", patient.getFamilyName(), patient.getGivenName(),
		    patient.getMiddleName()));
		card.address = formatAddress(patient.getPersonAddress());
		card.openmrsIdentifier = getOpenmrsIdentifier(patient, lookups);
		if (card.openmrsIdentifier == null) {
			card.warnings.add(WARN_NO_OPENMRS_ID);
		}
		card.dateOfBirth = formatDate(patient.getBirthdate());
		card.gender = patient.getGender();

		// 15. Outcome
		Concept outcome = aeForm.getActionOutcome();
		card.outcomeName = conceptName(outcome, lookups);
		if (outcome != null) {
			if (is(outcome, MdrtbConcepts.RESOLVED, lookups)) {
				card.outcomeCode = OUTCOME_RECOVERED_WITHOUT_SEQUELAE;
			} else if (is(outcome, MdrtbConcepts.RESOLVED_WITH_SEQUELAE, lookups)) {
				card.outcomeCode = OUTCOME_RECOVERED_WITH_SEQUELAE;
			} else {
				card.outcomeCode = OUTCOME_NOT_ON_CARD;
				card.warnings.add(WARN_OUTCOME_NOT_ON_CARD);
			}
		}

		// 16. Treatment regimen: the regimen form in force at the onset date (same lookup the
		// quarterly AE report uses). Only the two DR-TB regimen boxes have an exact concept.
		RegimenForm regimenForm = null;
		if (aeForm.getEncounterDatetime() != null) {
			regimenForm = lookups.getCurrentRegimenForm(patient, aeForm.getEncounterDatetime());
		}
		if (regimenForm == null) {
			card.warnings.add(WARN_NO_REGIMEN_FORM);
		} else {
			Concept regimenType = regimenForm.getSldRegimenType();
			card.regimenTypeName = conceptName(regimenType, lookups);
			if (regimenType != null) {
				if (is(regimenType, MdrtbConcepts.SHORT_MDR_REGIMEN, lookups)) {
					card.regimenTypeCode = REGIMEN_SHORT_DR;
				} else if (is(regimenType, MdrtbConcepts.INDIVIDUAL_WITH_BEDAQUILINE, lookups)
				        || is(regimenType, MdrtbConcepts.INDIVIDUAL_WITH_DELAMANID, lookups)
				        || is(regimenType, MdrtbConcepts.INDIVIDUAL_WITH_BEDAQUILINE_AND_DELAMANID, lookups)
				        || is(regimenType, MdrtbConcepts.INDIVIDUAL_WITH_CLOFAZIMIN_AND_LINEZOLID, lookups)) {
					card.regimenTypeCode = REGIMEN_INDIVIDUALIZED_DR;
				} else {
					card.regimenTypeCode = REGIMEN_NOT_ON_CARD;
					card.warnings.add(WARN_REGIMEN_NOT_ON_CARD);
				}
			}
			card.regimenDate = formatDate(regimenForm.getCouncilDate());
			card.primaryDiseaseMedicines = getMedicines(regimenForm, lookups);
		}
		card.diagnosticSummary = trimTrailingSeparator(aeForm.getDiagnosticSummary());
		card.clinicianNotes = aeForm.getComments();

		// ---------------- Section 2 ----------------
		card.suspectedDrugName = conceptName(drug, lookups);
		card.suspectedDrugsText = aeForm.getSuspectedDrug();

		// ---------------- Section 3 ----------------
		card.onsetDate = formatDate(aeForm.getEncounterDatetime());
		// 31. Cessation = outcome date, but only when the outcome says the event actually ended
		if (OUTCOME_RECOVERED_WITHOUT_SEQUELAE.equals(card.outcomeCode)
		        || OUTCOME_RECOVERED_WITH_SEQUELAE.equals(card.outcomeCode)) {
			card.cessationDate = formatDate(aeForm.getOutcomeDate());
		}
		card.adverseEventName = conceptName(aeForm.getAdverseEvent(), lookups);

		// 33. Seriousness
		Concept sae = aeForm.getTypeOfSAE();
		card.saeTypeName = conceptName(sae, lookups);
		if (sae != null) {
			if (is(sae, MdrtbConcepts.DEATH, lookups)) {
				card.saeTypeCode = SAE_DEATH;
			} else if (is(sae, MdrtbConcepts.LIFE_THREATENING_EXPERIENCE, lookups)) {
				card.saeTypeCode = SAE_LIFE_THREATENING;
			} else if (is(sae, MdrtbConcepts.HOSPITALIZATION_WORKFLOW, lookups)
			        || is(sae, MdrtbConcepts.PATIENT_HOSPITALIZED, lookups)) {
				card.saeTypeCode = SAE_HOSPITALIZATION;
				card.warnings.add(WARN_SAE_HOSPITALIZATION_NOT_SPLIT);
			} else if (is(sae, MdrtbConcepts.DISABILITY, lookups)) {
				card.saeTypeCode = SAE_DISABILITY;
			} else if (is(sae, MdrtbConcepts.CONGENITAL_ANOMALY, lookups)) {
				card.saeTypeCode = SAE_CONGENITAL_ANOMALY;
			} else {
				card.saeTypeCode = SAE_NOT_ON_CARD;
				card.warnings.add(WARN_SAE_NOT_ON_CARD);
			}
		}

		// 34. Adverse event of special interest
		Concept aesi = aeForm.getTypeOfSpecialEvent();
		card.aesiTypeName = conceptName(aesi, lookups);
		if (aesi != null) {
			card.aesiTypeCode = mapAesi(aesi, lookups);
			if (AESI_OTHER.equals(card.aesiTypeCode)) {
				card.warnings.add(WARN_AESI_PRINTED_AS_OTHER);
			}
		}

		// ---------------- Section 5 ----------------
		// 44. Re-challenge: only the two answers that state a result are printed
		Concept rechallenge = aeForm.getDrugRechallenge();
		if (is(rechallenge, MdrtbConcepts.RECURRENCE_OF_EVENT, lookups)) {
			card.rechallengeReproducedReaction = Boolean.TRUE;
		} else if (is(rechallenge, MdrtbConcepts.NO_RECURRENCE, lookups)) {
			card.rechallengeReproducedReaction = Boolean.FALSE;
		}

		// ---------------- Section 6 ----------------
		card.causalityName = conceptName(causality, lookups);
		if (causality != null) {
			if (is(causality, MdrtbConcepts.DEFINITE, lookups)) {
				card.causalityCode = CAUSALITY_CERTAIN;
			} else if (is(causality, MdrtbConcepts.PROBABLE, lookups)) {
				card.causalityCode = CAUSALITY_PROBABLE;
			} else if (is(causality, MdrtbConcepts.POSSIBLE, lookups)) {
				card.causalityCode = CAUSALITY_POSSIBLE;
			} else if (is(causality, MdrtbConcepts.NOT_CLASSIFIED, lookups)) {
				card.causalityCode = CAUSALITY_UNCLASSIFIED;
			} else {
				card.causalityCode = CAUSALITY_NOT_ON_CARD;
				card.warnings.add(WARN_CAUSALITY_NOT_ON_CARD);
			}
		}

		// ---------------- Section 7 ----------------
		Person provider = aeForm.getProvider();
		if (provider != null) {
			card.reporterName = emptyToNull(joinNonEmpty(" ", provider.getFamilyName(), provider.getGivenName(),
			    provider.getMiddleName()));
		}
		Location location = encounter.getLocation();
		card.reporterPlaceOfWork = location == null ? null : location.getName();
		card.reportDate = formatDate(aeForm.getYellowCardDate());
		return card;
	}

	private static String mapAesi(Concept aesi, Lookups lookups) {
		if (is(aesi, MdrtbConcepts.PERIPHERAL_NEUROPATHY, lookups)) {
			return AESI_PERIPHERAL_NEUROPATHY;
		}
		if (is(aesi, MdrtbConcepts.PSYCHIATRIC_DISORDER, lookups)) {
			return AESI_PSYCHIATRIC_CNS;
		}
		if (is(aesi, MdrtbConcepts.HEARING_DISORDER, lookups)) {
			return AESI_OTOTOXICITY;
		}
		if (is(aesi, MdrtbConcepts.MYELOSUPPRESSION, lookups)) {
			return AESI_MYELOSUPPRESSION;
		}
		if (is(aesi, MdrtbConcepts.QT_PROLONGATION, lookups)) {
			return AESI_QT_PROLONGATION;
		}
		if (is(aesi, MdrtbConcepts.LACTIC_ACIDOSIS, lookups)) {
			return AESI_LACTIC_ACIDOSIS;
		}
		if (is(aesi, MdrtbConcepts.HEPATITIS_AE, lookups)) {
			return AESI_HEPATITIS;
		}
		if (is(aesi, MdrtbConcepts.HYPOTHYROIDISM, lookups)) {
			return AESI_HYPOTHYROIDISM;
		}
		if (is(aesi, MdrtbConcepts.HYPOKALEMIA, lookups)) {
			return AESI_HYPOKALEMIA;
		}
		if (is(aesi, MdrtbConcepts.PANCREATITIS, lookups)) {
			return AESI_PANCREATITIS;
		}
		if (is(aesi, MdrtbConcepts.PHOSPHOLIPIDOSIS, lookups)) {
			return AESI_PHOSPHOLIPIDOSIS;
		}
		if (is(aesi, MdrtbConcepts.RENAL_FAILURE, lookups)) {
			return AESI_SEVERE_RENAL_IMPAIRMENT;
		}
		return AESI_OTHER;
	}

	/** Section 4 rows: every drug that has a dose on the regimen form, in the order of the regimen summary. */
	private static List<Medicine> getMedicines(RegimenForm rf, Lookups lookups) {
		List<Medicine> list = new ArrayList<>();
		addMedicine(lookups, list, MdrtbConcepts.CAPREOMYCIN, "Cm", "mdrtb.pv.cmDose", rf.getCmDose());
		addMedicine(lookups, list, MdrtbConcepts.AMIKACIN, "Am", "mdrtb.pv.amDose", rf.getAmDose());
		addMedicine(lookups, list, MdrtbConcepts.MOXIFLOXACIN, "Mfx", "mdrtb.pv.mfxDose", rf.getMfxDose());
		addMedicine(lookups, list, MdrtbConcepts.LEVOFLOXACIN, "Lfx", "mdrtb.pv.lfxDose", rf.getLfxDose());
		addMedicine(lookups, list, MdrtbConcepts.PROTHIONAMIDE, "Pto", "mdrtb.pv.ptoDose", rf.getPtoDose());
		addMedicine(lookups, list, MdrtbConcepts.CYCLOSERINE, "Cs", "mdrtb.pv.csDose", rf.getCsDose());
		addMedicine(lookups, list, MdrtbConcepts.P_AMINOSALICYLIC_ACID, "PAS", "mdrtb.pv.pasDose", rf.getPasDose());
		addMedicine(lookups, list, MdrtbConcepts.PYRAZINAMIDE, "Z", "mdrtb.pv.zDose", rf.getZDose());
		addMedicine(lookups, list, MdrtbConcepts.ETHAMBUTOL, "E", "mdrtb.pv.eDose", rf.getEDose());
		addMedicine(lookups, list, MdrtbConcepts.ISONIAZID, "H", "mdrtb.pv.hDose", rf.getHDose());
		addMedicine(lookups, list, MdrtbConcepts.LINEZOLID, "Lzd", "mdrtb.pv.lzdDose", rf.getLzdDose());
		addMedicine(lookups, list, MdrtbConcepts.CLOFAZIMINE, "Cfz", "mdrtb.pv.cfzDose", rf.getCfzDose());
		addMedicine(lookups, list, MdrtbConcepts.BEDAQUILINE, "Bdq", "mdrtb.pv.bdqDose", rf.getBdqDose());
		addMedicine(lookups, list, MdrtbConcepts.DELAMANID, "Dlm", "mdrtb.pv.dlmDose", rf.getDlmDose());
		addMedicine(lookups, list, MdrtbConcepts.IMIPENEM, "Imp/Clm", "mdrtb.pv.impDose", rf.getImpDose());
		// Fixed-dose combinations have no single concept: the short name is used as is
		addMedicine(lookups, list, null, "HR", "mdrtb.pv.hrDose", rf.getHrDose());
		addMedicine(lookups, list, null, "HRZE", "mdrtb.pv.hrzeDose", rf.getHrzeDose());
		addMedicine(lookups, list, MdrtbConcepts.STREPTOMYCIN, "S", "mdrtb.pv.sDose", rf.getSDose());
		addMedicine(lookups, list, MdrtbConcepts.AMOXICILLIN_AND_LAVULANIC_ACID, "Amx/Clv", "mdrtb.pv.amxDose", rf.getAmxDose());
		String otherName = rf.getOtherDrug1Name();
		if (StringUtils.isNotBlank(otherName) || rf.getOtherDrug1Dose() != null) {
			list.add(new Medicine(otherName, "mdrtb.pv.otherDrug1Dose", rf.getOtherDrug1Dose()));
		}
		return list;
	}

	private static void addMedicine(Lookups lookups, List<Medicine> list, String conceptLookup, String shortName, String doseLabelCode,
	        Double dose) {
		if (dose == null) {
			return;
		}
		String name = null;
		if (conceptLookup != null) {
			name = conceptName(lookups.getConcept(conceptLookup), lookups);
		}
		list.add(new Medicine(name == null ? shortName : name, doseLabelCode, dose));
	}

	// ---------------------------------------------------------------------------------------------
	// helpers

	/** True when {@code concept} is the concept that {@code lookup} resolves to. Null-safe on both sides. */
	private static boolean is(Concept concept, String lookup, Lookups lookups) {
		if (concept == null || concept.getId() == null) {
			return false;
		}
		Concept target = lookups.getConcept(lookup);
		return target != null && concept.getId().equals(target.getId());
	}

	private static String conceptName(Concept concept, Lookups lookups) {
		if (concept == null) {
			return null;
		}
		ConceptName name = concept.getName(lookups.getLocale());
		return name == null ? null : name.getName();
	}

	private static String formatDate(Date date) {
		return date == null ? null : new SimpleDateFormat(ISO_DATE_FORMAT).format(date);
	}

	private static String getOpenmrsIdentifier(Patient patient, Lookups lookups) {
		PatientIdentifierType type = lookups.getOpenmrsIdentifierType();
		if (type == null) {
			return null;
		}
		PatientIdentifier identifier = patient.getPatientIdentifier(type);
		return identifier == null ? null : identifier.getIdentifier();
	}

	private static String formatAddress(PersonAddress pa) {
		if (pa == null) {
			return null;
		}
		String address = joinNonEmpty(", ", pa.getCountry(), pa.getStateProvince(), pa.getCountyDistrict(),
		    pa.getCityVillage(), pa.getAddress1(), pa.getAddress2());
		return address.isEmpty() ? null : address;
	}

	private static String joinNonEmpty(String separator, String... parts) {
		List<String> kept = new ArrayList<>();
		for (String part : parts) {
			if (StringUtils.isNotBlank(part)) {
				kept.add(part.trim());
			}
		}
		return StringUtils.join(kept, separator);
	}

	private static String emptyToNull(String text) {
		return StringUtils.isBlank(text) ? null : text;
	}
	
	/** getDiagnosticSummary() ends every item with ", " */
	private static String trimTrailingSeparator(String text) {
		if (text == null) {
			return null;
		}
		String trimmed = text.trim();
		if (trimmed.endsWith(",")) {
			trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
		}
		return trimmed.isEmpty() ? null : trimmed;
	}

	// ---------------------------------------------------------------------------------------------
	// getters

	public String getAdverseEventsFormUuid() {
		return adverseEventsFormUuid;
	}

	public int getCausalityIndex() {
		return causalityIndex;
	}

	public String getPatientUuid() {
		return patientUuid;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getAddress() {
		return address;
	}

	public String getOpenmrsIdentifier() {
		return openmrsIdentifier;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public String getOutcomeCode() {
		return outcomeCode;
	}

	public String getOutcomeName() {
		return outcomeName;
	}

	public String getRegimenTypeCode() {
		return regimenTypeCode;
	}

	public String getRegimenTypeName() {
		return regimenTypeName;
	}

	public String getDiagnosticSummary() {
		return diagnosticSummary;
	}

	public String getClinicianNotes() {
		return clinicianNotes;
	}

	public String getSuspectedDrugName() {
		return suspectedDrugName;
	}

	public String getSuspectedDrugsText() {
		return suspectedDrugsText;
	}

	public String getOnsetDate() {
		return onsetDate;
	}

	public String getCessationDate() {
		return cessationDate;
	}

	public String getAdverseEventName() {
		return adverseEventName;
	}

	public String getSaeTypeCode() {
		return saeTypeCode;
	}

	public String getSaeTypeName() {
		return saeTypeName;
	}

	public String getAesiTypeCode() {
		return aesiTypeCode;
	}

	public String getAesiTypeName() {
		return aesiTypeName;
	}

	public String getRegimenDate() {
		return regimenDate;
	}

	public List<Medicine> getPrimaryDiseaseMedicines() {
		return primaryDiseaseMedicines;
	}

	public Boolean getRechallengeReproducedReaction() {
		return rechallengeReproducedReaction;
	}

	public String getCausalityCode() {
		return causalityCode;
	}

	public String getCausalityName() {
		return causalityName;
	}

	public String getReporterName() {
		return reporterName;
	}

	public String getReporterPlaceOfWork() {
		return reporterPlaceOfWork;
	}

	public String getReportDate() {
		return reportDate;
	}

	public List<String> getWarnings() {
		return warnings;
	}
}
