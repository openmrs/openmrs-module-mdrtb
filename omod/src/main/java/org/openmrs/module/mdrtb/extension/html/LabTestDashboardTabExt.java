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
package org.openmrs.module.mdrtb.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.mdrtb.MdrtbConfig;
import org.openmrs.module.web.extension.PatientDashboardTabExt;

/**
 * Adds the "Lab Tests" tab to the legacy patient dashboard. The tab renders the patientLabTests
 * portlet, which is backed by
 * {@link org.openmrs.module.mdrtb.web.controller.lab.LabTestOrderPortletController}.
 * <p>
 * This is the MDR-TB equivalent of CommonLabTestExt in openmrs-module-commonlabtest.
 */
public class LabTestDashboardTabExt extends PatientDashboardTabExt {
	
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTabName() {
		return "commonlabtest.title";
	}
	
	@Override
	public String getTabId() {
		return "commonlabtest";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return MdrtbConfig.VIEW_LAB_TEST_PRIVILEGE;
	}
	
	@Override
	public String getPortletUrl() {
		return "patientLabTests";
	}
}
