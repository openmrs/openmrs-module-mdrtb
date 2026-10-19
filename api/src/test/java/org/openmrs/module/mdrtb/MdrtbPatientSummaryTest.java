/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;

/**
 * Tests for the patient summary feature: {@link MdrtbService#getPatientSummary(Patient)} and
 * {@link PatientProgramSummary#getTreatmentOutcome()}.
 * <p>
 * Runs against the seed data loaded by {@link MdrtbTestBase}. Harry (id 1000) has a preferred name
 * "Harry James Potter", one identifier, no address and no observations, and is enrolled in two
 * non-voided programs: MDR-TB (enrolled 2022-08-01, still open) and DOTS (enrolled 2022-08-10,
 * completed 2009-06-25).
 */
public class MdrtbPatientSummaryTest extends MdrtbTestBase {
	
	private MdrtbService service;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
	}
	
	@Test
	public final void shouldReturnNullSummaryForNullPatient() {
		assertNull(service.getPatientSummary(null));
	}
	
	@Test
	public final void shouldCarryThePatientAndPreferredName() {
		PatientSummary summary = service.getPatientSummary(harry);
		
		assertEquals(harry, summary.getPatient());
		
		PersonName personName = summary.getPersonName();
		assertNotNull(personName);
		assertTrue(personName.getPreferred());
		assertEquals("Harry", personName.getGivenName());
		assertEquals("Potter", personName.getFamilyName());
	}
	
	@Test
	public final void shouldCarryNoAddressForHarryWhoHasNone() {
		PatientSummary summary = service.getPatientSummary(harry);
		
		assertNull(summary.getPatient().getPersonAddress());
	}
	
	@Test
	public final void shouldIncludeAllPatientIdentifiers() {
		PatientSummary summary = service.getPatientSummary(harry);
		
		Set<PatientIdentifier> identifiers = summary.getPatientIdentifiers();
		assertEquals(harry.getIdentifiers().size(), identifiers.size());
		assertEquals(1, identifiers.size());
		
		PatientIdentifier identifier = identifiers.iterator().next();
		assertEquals("987654321", identifier.getIdentifier());
		assertEquals(hogwartsIdType, identifier.getIdentifierType());
	}
	
	@Test
	public final void shouldIncludeBothOfHarrysNonVoidedPrograms() {
		PatientSummary summary = service.getPatientSummary(harry);
		
		List<Integer> programIds = programIdsOf(summary);
		assertEquals(2, programIds.size());
		assertTrue(programIds.containsAll(Arrays.asList(harryMdrProgram.getPatientProgramId(),
		    harryDotsProgram.getPatientProgramId())));
	}
	
	@Test
	public final void shouldReturnEmptyButNonNullObsAndLabTestListsWhenNothingIsRecorded() {
		PatientSummary summary = service.getPatientSummary(harry);
		
		for (PatientProgramSummary programSummary : summary.getPatientProgramSummaries()) {
			assertNotNull(programSummary.getObservations());
			assertTrue(programSummary.getObservations().isEmpty());
			assertNotNull(programSummary.getLabTests());
			assertTrue(programSummary.getLabTests().isEmpty());
		}
	}
	
	@Test
	public final void shouldScopeObservationsToTheEnclosingProgramWindow() {
		// Falls inside the still-open MDR-TB program (enrolled 2022-08-01) but after the DOTS
		// program window closed in 2009
		Obs obs = new Obs(harry.getPerson(), mdrStartDateConcept, new LocalDate(2022, 8, 15).toDate(), hogwarts);
		obs.setValueDatetime(new LocalDate(2022, 8, 15).toDate());
		Context.getObsService().saveObs(obs, null);
		
		PatientSummary summary = service.getPatientSummary(harry);
		
		assertTrue("obs dated 2022-08-15 should fall inside the open MDR-TB program",
		    containsObs(summaryFor(summary, harryMdrProgram).getObservations(), obs));
		assertFalse("obs dated 2022-08-15 should fall outside the closed DOTS program",
		    containsObs(summaryFor(summary, harryDotsProgram).getObservations(), obs));
	}
	
	@Test
	public final void shouldReturnNoProgramSummariesForAPatientEnrolledInNoProgram() {
		Patient patient = Context.getPatientService().savePatient(newHogwartsPatient());
		
		PatientSummary summary = service.getPatientSummary(patient);
		
		assertNotNull(summary.getPatientProgramSummaries());
		assertTrue(summary.getPatientProgramSummaries().isEmpty());
	}
	
	@Test
	public final void shouldReturnTheObsRecordedAgainstTheProgramsOutcomesConcept() {
		Obs startDateObs = new Obs();
		startDateObs.setConcept(mdrStartDateConcept);
		Obs outcomeObs = new Obs();
		outcomeObs.setConcept(noConcept);

		PatientProgramSummary programSummary = new PatientProgramSummary(programWithOutcomesConcept(noConcept),
		        new ArrayList<>(Arrays.asList(startDateObs, outcomeObs)), null);

		assertSame(outcomeObs, programSummary.getTreatmentOutcome());
	}
	
	@Test
	public final void shouldReturnNoTreatmentOutcomeWhenNoObsMatchesTheOutcomesConcept() {
		Obs startDateObs = new Obs();
		startDateObs.setConcept(mdrStartDateConcept);

		PatientProgramSummary programSummary = new PatientProgramSummary(programWithOutcomesConcept(noConcept),
		        new ArrayList<>(Arrays.asList(startDateObs)), null);

		assertNull(programSummary.getTreatmentOutcome());
	}
	
	private PatientProgram programWithOutcomesConcept(Concept outcomesConcept) {
		Program program = new Program();
		program.setName("In-memory program");
		program.setOutcomesConcept(outcomesConcept);
		
		PatientProgram patientProgram = new PatientProgram();
		patientProgram.setProgram(program);
		return patientProgram;
	}
	
	private Patient newHogwartsPatient() {
		Patient patient = new Patient();
		patient.setGender("F");
		patient.setBirthdate(new LocalDate(1981, 2, 13).toDate());
		patient.addName(new PersonName("Luna", null, "Lovegood"));
		
		PatientIdentifier identifier = new PatientIdentifier("LUNA-001", hogwartsIdType, hogwarts);
		identifier.setPreferred(true);
		patient.addIdentifier(identifier);
		return patient;
	}
	
	private PatientProgramSummary summaryFor(PatientSummary summary, PatientProgram patientProgram) {
		for (PatientProgramSummary programSummary : summary.getPatientProgramSummaries()) {
			if (patientProgram.getPatientProgramId().equals(programSummary.getPatientProgram().getPatientProgramId())) {
				return programSummary;
			}
		}
		throw new AssertionError("no summary found for patient program " + patientProgram.getPatientProgramId());
	}
	
	private boolean containsObs(List<Obs> observations, Obs target) {
		for (Obs obs : observations) {
			if (target.getObsId().equals(obs.getObsId())) {
				return true;
			}
		}
		return false;
	}
	
	private List<Integer> programIdsOf(PatientSummary summary) {
		List<Integer> programIds = new ArrayList<>();
		for (PatientProgramSummary programSummary : summary.getPatientProgramSummaries()) {
			programIds.add(programSummary.getPatientProgram().getPatientProgramId());
		}
		return programIds;
	}
}
