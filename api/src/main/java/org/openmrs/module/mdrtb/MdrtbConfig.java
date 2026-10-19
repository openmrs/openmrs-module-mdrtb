/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb;

import org.springframework.stereotype.Component;

/**
 * Contains module's config.
 */
@Component("mdrtb.MdrtbConfig")
public class MdrtbConfig {
	
	public final static String MODULE_PRIVILEGE = "Edit DOTS-MDR Data";
	
	public static final String ADD_LAB_TEST_METADATA_PRIVILEGE = "Add LabTest Metadata";
	
	public static final String EDIT_LAB_TEST_METADATA_PRIVILEGE = "Edit LabTest Metadata";
	
	public static final String DELETE_LAB_TEST_METADATA_PRIVILEGE = "Delete LabTest Metadata";
	
	public static final String VIEW_LAB_TEST_METADATA_PRIVILEGE = "View LabTest Metadata";
	
	public static final String ADD_LAB_TEST_SAMPLE_PRIVILEGE = "Add LabTest Samples";
	
	public static final String EDIT_LAB_TEST_SAMPLE_PRIVILEGE = "Edit LabTest Samples";
	
	public static final String VIEW_LAB_TEST_SAMPLE_PRIVILEGE = "View LabTest Samples";
	
	public static final String DELETE_LAB_TEST_SAMPLE_PRIVILEGE = "Delete LabTest Samples";
	
	public static final String ADD_LAB_TEST_PRIVILEGE = "Add LabTest Orders";
	
	public static final String EDIT_LAB_TEST_PRIVILEGE = "Edit LabTest Orders";
	
	public static final String VIEW_LAB_TEST_PRIVILEGE = "View LabTest Orders";
	
	public static final String DELETE_LAB_TEST_PRIVILEGE = "Delete LabTest Orders";
	
	public static final String ADD_LAB_RESULT_PRIVILEGE = "Add LabTest Results";
	
	public static final String EDIT_LAB_RESULT_PRIVILEGE = "Edit LabTest Results";
	
	public static final String VIEW_LAB_RESULT_PRIVILEGE = "View LabTest Results";
	
	public static final String DELETE_LAB_RESULT_PRIVILEGE = "Delete LabTest Results";
	
	public static final Boolean AUTO_VOID_REJECTED_SAMPLES = Boolean.TRUE;
}
