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
package org.openmrs.module.mdrtb.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * WHO Form 05 Report
 */
public class WHOForm05 implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return "WHO Form 05";
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return "Quarterly report on MDR-TB detection and Category IV treatment start";
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", "Facility", Location.class));
		l.add(new Parameter("quarter", "Quarter", Integer.class));
		l.add(new Parameter("year", "Year", Integer.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML Report", "org/openmrs/module/mdrtb/reporting/data/output/WHOForm05.html"));
		return l;
	}

	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer)parameters.get("year");
		Integer quarter = (Integer)parameters.get("quarter");
		if (quarter == null) {
			throw new IllegalArgumentException("Please enter a quarter");
		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, null));
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	public ReportData evaluateReport(EvaluationContext context) {
		
		ReportDefinition report = new ReportDefinition();
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		
		// Set base cohort to patients assigned to passed location, if applicable
		CohortDefinition locationFilter = Cohorts.getLocationFilter(location);
		if (locationFilter != null) {
			report.setBaseCohortDefinition(locationFilter, null);
		}

		// Add the data set definitions
		CohortCrossTabDataSetDefinition labResultDsd = new CohortCrossTabDataSetDefinition();
		labResultDsd.addColumn("mdr", Cohorts.getMdrDetectionFilter(startDate, endDate), null);
		labResultDsd.addColumn("xdr", Cohorts.getXdrDetectionFilter(startDate, endDate), null);
		report.addDataSetDefinition("labDetections", labResultDsd, null);
		
		CohortCrossTabDataSetDefinition treatmentDsd = new CohortCrossTabDataSetDefinition();
		treatmentDsd.addRow("confirmed", Cohorts.getConfirmedMdrFilter(startDate, endDate), null);
		treatmentDsd.addRow("suspected", Cohorts.getSuspectedMdrFilter(startDate, endDate), null);
		treatmentDsd.addColumn("new", Cohorts.getNewCaseFilter(), null);
		treatmentDsd.addColumn("previousFirstLine", Cohorts.getPrevFirstLineCaseFilter(), null);
		treatmentDsd.addColumn("previousSecondLine", Cohorts.getPrevSecondLineCaseFilter(), null);
		report.addDataSetDefinition("startedTreatment", treatmentDsd, null);
		
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		return data;
	}
}