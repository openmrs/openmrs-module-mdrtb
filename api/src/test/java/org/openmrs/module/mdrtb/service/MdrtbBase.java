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

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class MdrtbBase extends BaseModuleContextSensitiveTest {

	protected static final String DATA_XML = "MdrtbTestService-initialData.xml";

	protected Provider owais;

	protected Patient harry;
	
	protected PatientIdentifierType hogwartzIdType;

	protected Patient hermione;

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
		
		hogwartzIdType = Context.getPatientService().getPatientIdentifierType(5);

	}
}
