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
package org.openmrs.module.mdrtb.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class MdrtbBase extends BaseModuleContextSensitiveTest {

	protected static final String DATA_XML = "MdrtbTestService-initialData.xml";

	protected Provider owais;

	protected Patient harry, hermione;

	protected PatientIdentifierType hogwartsIdType;

	protected Location hogwarts, diagonAlley, leakyCauldron;

	protected EncounterType owlExam;

	protected EncounterType transferIn;

	protected EncounterType transferOut;
	
	protected Program mdrtbProgram;
	protected Program dotsProgram;

	protected Concept unknownConcept;
	protected Concept noConcept;
	protected Concept yesConcept;
	protected Concept mdrStartDateConcept;
	
	protected PatientProgram harryMdrProgram, harryDotsProgram;

	/**
	 * Initialize all data objects before each test
	 * 
	 * @throws Exception on Exception
	 */
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(DATA_XML);

		owais = Context.getProviderService().getProvider(300);

		harry = Context.getPatientService().getPatient(1000);
		hermione = Context.getPatientService().getPatient(2000);

		hogwartsIdType = Context.getPatientService().getPatientIdentifierType(5);

		hogwarts = Context.getLocationService().getLocation(101);
		diagonAlley = Context.getLocationService().getLocation(102);
		leakyCauldron = Context.getLocationService().getLocation(103);
		
		owlExam = Context.getEncounterService().getEncounterType(91);
		transferIn = Context.getEncounterService().getEncounterType(92);
		transferOut = Context.getEncounterService().getEncounterType(93);
		
		mdrtbProgram = Context.getProgramWorkflowService().getProgram(1);
		dotsProgram = Context.getProgramWorkflowService().getProgram(2);
		
		unknownConcept = Context.getConceptService().getConcept(1);
		noConcept = Context.getConceptService().getConcept(10);
		yesConcept = Context.getConceptService().getConcept(11);
		mdrStartDateConcept = Context.getConceptService().getConcept(436);
		
		harryMdrProgram = Context.getProgramWorkflowService().getPatientProgram(10001);
		harryDotsProgram = Context.getProgramWorkflowService().getPatientProgram(10002);
	}

	@Test
	public void shouldInitiateTestData() throws Exception {
		this.initTestData();
		assertNotNull(owais);
	}
}