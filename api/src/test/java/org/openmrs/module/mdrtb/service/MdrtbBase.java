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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class MdrtbBase extends BaseModuleWebContextSensitiveTest {
	
	protected static final String[] DATASETS = { "MdrtbTestService-initialData.xml", "concept.xml", "concept_answer.xml",
	        "concept_class.xml", "concept_datatype.xml", "concept_description.xml", "concept_map_type.xml",
	        "concept_name_tag_map.xml", "concept_name_tag.xml", "concept_name.xml", "concept_numeric.xml",
	        "concept_reference_map.xml", "concept_reference_source.xml", "concept_reference_term.xml", "concept_set.xml",
	        "concept_stop_word.xml", "address_hierarchy.xml" };

	protected Provider owais;
	
	protected Patient harry, hermione;
	
	protected PatientIdentifierType hogwartsIdType;
	
	protected Location hogwarts, diagonAlley, leakyCauldron;
	
	protected EncounterType owlExam;
	
	protected EncounterType transferIn;
	
	protected EncounterType transferOut;
	
	protected EncounterType specimen;
	
	protected Program mdrtbProgram;
	
	protected Program dotsProgram;
	
	protected Concept unknownConcept;
	
	protected Concept noConcept;
	
	protected Concept yesConcept;
	
	protected Concept mdrStartDateConcept;
	
	protected PatientProgram harryMdrProgram;
	
	protected PatientProgram hermioneMdrProgram;
	
	protected PatientProgram harryDotsProgram;
	
	protected PatientProgram hermioneDotsProgram;
	
	protected Encounter harrySpecimenEncounter;

	@Autowired
	protected HibernateMdrtbDAO dao;
	
	/**
	 * Initialize all data objects before each test
	 * 
	 * @throws Exception on Exception
	 */
	public void initTestData() throws Exception {
		// Super dirty hack. Create address_hierarchy tables
		String query = "CREATE TABLE IF NOT EXISTS address_hierarchy_level (address_hierarchy_level_id int(11) NOT NULL AUTO_INCREMENT, name varchar(160), parent_level_id int(11), address_field varchar(50), uuid char(38) NOT NULL, required tinyint(1) NOT NULL DEFAULT '0', PRIMARY KEY (address_hierarchy_level_id));";
		dao.getSessionFactory().getCurrentSession().createSQLQuery(query).executeUpdate();
		query = "CREATE TABLE IF NOT EXISTS address_hierarchy_entry ( address_hierarchy_entry_id int(11) NOT NULL AUTO_INCREMENT, name varchar(160) , level_id int(11) NOT NULL, parent_id int(11) , user_generated_id varchar(11) , latitude double , longitude double , elevation double , uuid char(38) NOT NULL, PRIMARY KEY (address_hierarchy_entry_id));";
		dao.getSessionFactory().getCurrentSession().createSQLQuery(query).executeUpdate();		

		initializeInMemoryDatabase();
		
		for (String dataFile : DATASETS) {
			executeDataSet(dataFile);
		}
		
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
		specimen = Context.getEncounterService().getEncounterType(94);
		
		mdrtbProgram = Context.getProgramWorkflowService().getProgram(1);
		dotsProgram = Context.getProgramWorkflowService().getProgram(2);
		
		unknownConcept = Context.getConceptService().getConcept(1);
		noConcept = Context.getConceptService().getConcept(10);
		yesConcept = Context.getConceptService().getConcept(11);
		mdrStartDateConcept = Context.getConceptService().getConcept(436);
		
		harryMdrProgram = Context.getProgramWorkflowService().getPatientProgram(10001);
		harryDotsProgram = Context.getProgramWorkflowService().getPatientProgram(10002);
		hermioneMdrProgram = Context.getProgramWorkflowService().getPatientProgram(10003);
		hermioneDotsProgram = Context.getProgramWorkflowService().getPatientProgram(10004);
		
		harrySpecimenEncounter = Context.getEncounterService().getEncounter(100008);
	}
	
	@Test
	public void shouldInitiateTestData() throws Exception {
		this.initTestData();
		assertNotNull(owais);
	}
}
