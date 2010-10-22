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

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.reporting.PihHaitiQueryService;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.definition.DstResultCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * MOH Report which reports on MDR TB Patients within broader TB report
 */
public class MOHReport implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return "ZL MDR-TB Report";
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return "Report on MDR-TB treatment to be completed monthly and quarterly";
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", "Facility", Location.class));
		l.add(new Parameter("year", "Year of treatment start", Integer.class));
		l.add(new Parameter("quarter", "Quarter (optional)", Integer.class));
		l.add(new Parameter("month", "Month (optional)", Integer.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		return l;
	}
	
	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer) parameters.get("year");
		Integer quarter = (Integer) parameters.get("quarter");
		Integer month = (Integer) parameters.get("month");
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, month));
		
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	public ReportData evaluateReport(EvaluationContext context) {

		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");

		CohortDefinition inProgramAtStart = Cohorts.getInMdrProgramEverDuring(startDate, endDate);
		CohortDefinition atLocation = (location == null ? null : Cohorts.getLocationFilter(location));
		CohortDefinition newlyHospitalized = Cohorts.getNewlyHospitalizedDuringPeriod(startDate, endDate);
		CohortDefinition allHospitalized = Cohorts.getEverHospitalizedDuringPeriod(startDate, endDate);
		CohortDefinition startedTxDuring = Cohorts.getStartedTreatmentFilter(startDate, endDate);
		CohortDefinition startedTxByEnd = Cohorts.getStartedTreatmentFilter(null, endDate);
		CohortDefinition smearPositive = Cohorts.getMostRecentlySmearPositiveByDate(endDate);
		CohortDefinition smearNegative = Cohorts.getMostRecentlySmearNegativeByDate(endDate);
		CohortDefinition abandoned = Cohorts.getDefaultedDuringFilter(startDate, endDate);
		CohortDefinition died = Cohorts.getDiedDuringFilter(startDate, endDate);
		CohortDefinition newlyHivPositive = Cohorts.getNewlyHivPositive(startDate, endDate);
		CohortDefinition everHivPositive = Cohorts.getHivPositiveDuring(null, endDate);
		CohortDefinition transferredIn = Cohorts.getTransferredInDuring(startDate, endDate);
		CohortDefinition controlSmearNegative = Cohorts.getAllSmearNegativeDuring(startDate, endDate);
		CohortDefinition controlSmearPositive = Cohorts.getAnySmearPositiveDuring(startDate, endDate);
		CohortDefinition firstCulturePositive = Cohorts.getFirstCulturePositiveDuring(startDate, endDate);
		CohortDefinition resistantTests = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition ambulatory = Cohorts.getMostRecentlyAmbulatoryByEnd(startDate, endDate);
		CohortDefinition pendingCultures = Cohorts.getPendingCulturesOnDate(endDate);
		CohortDefinition controlCultureNegative = Cohorts.getAllCultureNegativeDuring(startDate, endDate);
		CohortDefinition controlCulturePositive = Cohorts.getAnyCulturePositiveDuring(startDate, endDate);
		CohortDefinition completedTx = Cohorts.getTreatmentCompletedDuringFilter(startDate, endDate);
		CohortDefinition curedTx = Cohorts.getCuredDuringFilter(startDate, endDate);
		CohortDefinition failedTx = Cohorts.getFailedDuringFilter(startDate, endDate);
		CohortDefinition relapsedTx = Cohorts.getRelapsedDuringFilter(startDate, endDate);

		ReportDefinition report = new ReportDefinition();
		report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("AND", inProgramAtStart, atLocation), null);
		
		CohortCrossTabDataSetDefinition s3 = new CohortCrossTabDataSetDefinition();
		report.addDataSetDefinition("III. Diagnostic / Traitement et Suivi des Patients MDR-TB", s3, null);
		s3.addRow("1.a Nombre de patients heberges pour traitement anti TB resistant - nouveaux heberges", newlyHospitalized, null);
		s3.addRow("1.b Nombre de patients heberges pour traitement anti TB resistant - total heberges", allHospitalized, null);
		s3.addRow("2.a Nombre de nouveaux cas mis cas sous tratement pour MDR-TB - TPM+", ReportUtil.getCompositionCohort("AND", startedTxDuring, smearPositive), null);
		s3.addRow("2.b Nombre de nouveaux cas mis cas sous tratement pour MDR-TB - TPM-", ReportUtil.getCompositionCohort("AND", startedTxDuring, smearNegative), null);
		s3.addRow("2.c Nombre de nouveaux cas mis cas sous tratement pour MDR-TB - Total", startedTxDuring, null);
		s3.addRow("3.  Nombre de patients ayant abandonne le traitement", abandoned, null);
		s3.addRow("4.  Nombre de patients decedes", died, null);
		s3.addRow("5.  Nombre de nouveaux patients sous traitement MDR-TB et VIH+", ReportUtil.getCompositionCohort("AND", startedTxByEnd, newlyHivPositive), null);
		s3.addRow("6.  Nombre de patients referes/recus pour evaluation MDR-TB", transferredIn, null);
		s3.addRow("7.  Nombre de patients davec crachats de controle negatifs", controlSmearNegative, null);
		s3.addRow("8.  Nombre de patients davec crachats de controle positives", controlSmearPositive, null);
		s3.addRow("9.  Nombre de cultures diagnostique postives", firstCulturePositive, null);
		s3.addRow("10. Nombre de nouveaux patients avec tests resistance confirmes", resistantTests, null);
		s3.addRow("11. Nombre total de patients en suivi non heberges", ambulatory, null);
		s3.addRow("12. Nombre de patients avec culture en cours", pendingCultures, null);
		s3.addRow("13. Nombre total de patients avec coinfection MDR-TB + HIV", everHivPositive, null);
		s3.addRow("14. Nombre de cultures de controle positives", controlCulturePositive, null);
		s3.addRow("15. Nombre de cultures de controle negatives", controlCultureNegative, null);
		s3.addRow("16. Nombre de patients ayant termine leur traitement MDR-TB avec success", ReportUtil.getCompositionCohort("OR", completedTx, curedTx), null);
		s3.addRow("17. Nombre de patients ayant echoue le traitement pour MDR-TB", failedTx, null);
		s3.addRow("18. Nombre de patients ayant repris le traitement MDR-TB au cours du mois", relapsedTx, null);
		
		CohortCrossTabDataSetDefinition s4 = new CohortCrossTabDataSetDefinition();
		report.addDataSetDefinition("IV. Repartition des patients selon leur profil de resistance", s4, null);
		Map<String, Cohort> profiles = PihHaitiQueryService.getResistanceProfiles(context, endDate);
		for (Map.Entry<String, Cohort> e : profiles.entrySet()) {
			DstResultCohortDefinition cd = new DstResultCohortDefinition();
			cd.setMaxResultDate(endDate);
			cd.setSpecificProfile(e.getKey());
			s4.addRow(e.getKey(), cd, null);
		}
		
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		return data;
	}
}