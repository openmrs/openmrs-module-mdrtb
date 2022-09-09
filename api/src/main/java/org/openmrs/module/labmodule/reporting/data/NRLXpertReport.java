package org.openmrs.module.labmodule.reporting.data;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * LAB Report
 */
public class NRLXpertReport implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("labmodule.labNRLReport2");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("labmodule.labNRLReport2");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/NRL_Xpert"+ 
				(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	

	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	@SuppressWarnings({ "unused" })
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer)parameters.get("year");
		
		if (year == null) {
			//throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleasEnterAYear"));
			throw new IllegalArgumentException("Year is missing");
			
		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, null, null));
		
		Map<String,Object>pMap = context.getParameterValues();
		Set<String> keySet = pMap.keySet();
		
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	public ReportData evaluateReport(EvaluationContext context) {
		
		Integer year = (Integer) context.getParameterValue("year");
		Integer district=null,oblast=null,facility=null; 
	 	Date endDate = (Date)context.getParameterValue("endDate");
		Date startDate = (Date)context.getParameterValue("startDate");
		
		if(context.getParameterValue("district")!=null && context.getParameterValue("district")!="")
	 		district = (Integer) context.getParameterValue("district");
	 	if(context.getParameterValue("oblast")!=null && context.getParameterValue("oblast")!="")
		  	oblast = (Integer) context.getParameterValue("oblast");
	 	if(context.getParameterValue("facility")!=null && context.getParameterValue("facility")!="")
		  	facility =(Integer) context.getParameterValue("facility");
		
		Map<String,Date>dateMap = new HashMap<String, Date>();		
		Date q1StartDate;
		Date q2StartDate;
		Date q3StartDate;
		Date q4StartDate;
		Date q1EndDate;
		Date q2EndDate;
		Date q3EndDate;
		Date q4EndDate;
		
		dateMap = ReportUtil.getPeriodDates(year, "1", null);
		q1StartDate = dateMap.get("startDate"); 
		q1EndDate = dateMap.get("endDate");

		dateMap = ReportUtil.getPeriodDates(year, "2", null);
		q2StartDate = dateMap.get("startDate"); 
		q2EndDate = dateMap.get("endDate");
		
		dateMap = ReportUtil.getPeriodDates(year, "3", null);
		q3StartDate = dateMap.get("startDate"); 
		q3EndDate = dateMap.get("endDate");
		
		dateMap = ReportUtil.getPeriodDates(year, "4", null);
		q4StartDate = dateMap.get("startDate"); 
		q4EndDate = dateMap.get("endDate");
		Location location =Context.getService(TbService.class).getLocation(null, district, null);
		
		List<Location> locList = Context.getService(TbService.class).getLocationList(oblast, district, facility);
		if(locList==null)
			locList=Context.getLocationService().getAllLocations();
		
		if(location != null)
			context.addParameterValue("location", location.getName()); 
		else
			context.addParameterValue("location", " - "); 
		
		ReportDefinition report = new ReportDefinition(); 
		
		////////			XPERT MTB/RIF		//////////

		//Total
		Concept xpert = (Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT));

		String xpertTotalQ1 = Context.getService(TbService.class).getXpertWithMicroscopy(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertAllQ1", xpertTotalQ1);
		
		String xpertTotalQ2 = Context.getService(TbService.class).getXpertWithMicroscopy(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertAllQ2", xpertTotalQ2);
		
		String xpertTotalQ3 = Context.getService(TbService.class).getXpertWithMicroscopy(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertAllQ3", xpertTotalQ3);
		
		String xpertTotalQ4 = Context.getService(TbService.class).getXpertWithMicroscopy(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertAllQ4", xpertTotalQ4);

		String xpertTotalQz = Context.getService(TbService.class).getXpertWithMicroscopy(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertAllTotal", xpertTotalQz);
		
		//MT +ve
		String xpertMtPosQ1 = Context.getService(TbService.class).getXpertMTPositive(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertMtPositiveQ1", xpertMtPosQ1);
		
		String xpertMtPos2 = Context.getService(TbService.class).getXpertMTPositive(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertMtPositiveQ2", xpertMtPos2);
		
		String xpertMtPosQ3 = Context.getService(TbService.class).getXpertMTPositive(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertMtPositiveQ3", xpertMtPosQ3);
		
		String xpertMtPosQ4 = Context.getService(TbService.class).getXpertMTPositive(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtPositiveQ4", xpertMtPosQ4);

		String xpertMtPosTotal = Context.getService(TbService.class).getXpertMTPositive(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtPositiveTotal", xpertMtPosTotal);
		
		//MT -ve
		String xpertMtNegQ1 = Context.getService(TbService.class).getXpertMTNegative(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertMtNegativeQ1", xpertMtNegQ1);
		
		String xpertMtNeg2 = Context.getService(TbService.class).getXpertMTNegative(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertMtNegativeQ2", xpertMtNeg2);
		
		String xpertMtNegQ3 = Context.getService(TbService.class).getXpertMTNegative(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertMtNegativeQ3", xpertMtNegQ3);
		
		String xpertMtNegQ4 = Context.getService(TbService.class).getXpertMTNegative(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtNegativeQ4", xpertMtNegQ4);

		String xpertMtNegTotal = Context.getService(TbService.class).getXpertMTNegative(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtNegativeTotal", xpertMtNegTotal);

		//MT+, GX+ & Rif Sensitive
		String xpertMtPosXpertPosSensQ1 = Context.getService(TbService.class).getXpertPositiveMtPositiveSusceptible(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosSenstiveQ1", xpertMtPosXpertPosSensQ1);
		
		String xpertMtPosXpertPosSens2 = Context.getService(TbService.class).getXpertPositiveMtPositiveSusceptible(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosSenstiveQ2", xpertMtPosXpertPosSens2);
		
		String xpertMtPosXpertPosSensQ3 = Context.getService(TbService.class).getXpertPositiveMtPositiveSusceptible(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosSenstiveQ3", xpertMtPosXpertPosSensQ3);
		
		String xpertMtPosXpertPosSensQ4 = Context.getService(TbService.class).getXpertPositiveMtPositiveSusceptible(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosSenstiveQ4", xpertMtPosXpertPosSensQ4);

		String xpertMtPosXpertPosSensTotal = Context.getService(TbService.class).getXpertPositiveMtPositiveSusceptible(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosSenstiveTotal", xpertMtPosXpertPosSensTotal);
		
		//MT+, GX+ & Rif Resistant
		String xpertMtPosXpertPosResQ1 = Context.getService(TbService.class).getXpertPositiveMtPositiveResistant(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosResistantQ1", xpertMtPosXpertPosResQ1);
		
		String xpertMtPosXpertPosRes2 = Context.getService(TbService.class).getXpertPositiveMtPositiveResistant(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosResistantQ2", xpertMtPosXpertPosRes2);
		
		String xpertMtPosXpertPosResQ3 = Context.getService(TbService.class).getXpertPositiveMtPositiveResistant(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosResistantQ3", xpertMtPosXpertPosResQ3);
		
		String xpertMtPosXpertPosResQ4 = Context.getService(TbService.class).getXpertPositiveMtPositiveResistant(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosResistantQ4", xpertMtPosXpertPosResQ4);

		String xpertMtPosXpertPosResTotal = Context.getService(TbService.class).getXpertPositiveMtPositiveResistant(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtPosGXPosResistantTotal", xpertMtPosXpertPosResTotal);

		
		//MT-, GX+ & Rif Sensitive
		String xpertMtNegXpertPosSensQ1 = Context.getService(TbService.class).getXpertPositiveMtNegativeSusceptible(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosSenstiveQ1", xpertMtNegXpertPosSensQ1);
		
		String xpertMtNegXpertPosSens2 = Context.getService(TbService.class).getXpertPositiveMtNegativeSusceptible(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosSenstiveQ2", xpertMtNegXpertPosSens2);
		
		String xpertMtNegXpertPosSensQ3 = Context.getService(TbService.class).getXpertPositiveMtNegativeSusceptible(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosSenstiveQ3", xpertMtNegXpertPosSensQ3);
		
		String xpertMtNegXpertPosSensQ4 = Context.getService(TbService.class).getXpertPositiveMtNegativeSusceptible(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosSenstiveQ4", xpertMtNegXpertPosSensQ4);

		String xpertMtNegXpertPosSensTotal = Context.getService(TbService.class).getXpertPositiveMtNegativeSusceptible(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosSenstiveTotal", xpertMtNegXpertPosSensTotal);
	
		//MT-, GX+ & Rif Resistant
		String xpertMtNegXpertPosResQ1 = Context.getService(TbService.class).getXpertPositiveMtNegativeResistant(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosResistantQ1", xpertMtNegXpertPosResQ1);
		
		String xpertMtNegXpertPosRes2 = Context.getService(TbService.class).getXpertPositiveMtNegativeResistant(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosResistantQ2", xpertMtNegXpertPosRes2);
		
		String xpertMtNegXpertPosResQ3 = Context.getService(TbService.class).getXpertPositiveMtNegativeResistant(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosResistantQ3", xpertMtNegXpertPosResQ3);
		
		String xpertMtNegXpertPosResQ4 = Context.getService(TbService.class).getXpertPositiveMtNegativeResistant(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosResistantQ4", xpertMtNegXpertPosResQ4);

		String xpertMtNegXpertPosResTotal = Context.getService(TbService.class).getXpertPositiveMtNegativeResistant(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertMtNegGXPosResistantTotal", xpertMtNegXpertPosResTotal);

		//GX Negative
		//MT-, GX+ & Rif Resistant
		String xpertNegQ1 = Context.getService(TbService.class).getXpertNegative(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertNegativeQ1", xpertNegQ1 );
		
		String xpertNegQ2 = Context.getService(TbService.class).getXpertNegative(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertNegativeQ2", xpertNegQ2);
		
		String xpertNegQ3 = Context.getService(TbService.class).getXpertNegative(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertNegativeQ3", xpertNegQ3);
		
		String xpertNegQ4 = Context.getService(TbService.class).getXpertNegative(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertNegativeQ4", xpertNegQ4);

		String xpertNegTotal = Context.getService(TbService.class).getXpertNegative(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertNegativeTotal", xpertNegTotal);

		/////					XPERT ONLY			///////
		String xpertOnlyQ1 = Context.getService(TbService.class).getXpertOnly(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertOnlyQ1", xpertOnlyQ1 );
		
		String xpertOnlyQ2 = Context.getService(TbService.class).getXpertOnly(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertOnlyQ2", xpertOnlyQ2);
		
		String xpertOnlyQ3 = Context.getService(TbService.class).getXpertOnly(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertOnlyQ3", xpertOnlyQ3);
		
		String xpertOnlyQ4 = Context.getService(TbService.class).getXpertOnly(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertOnlyQ4", xpertOnlyQ4);

		String xpertOnlyTotal = Context.getService(TbService.class).getXpertOnly(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertOnlyTotal", xpertOnlyTotal);
		
		//XPERT Only Senstivie
		
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		String xpertOnlySensitiveQ1 = Context.getService(TbService.class).getXpertOnlyByResult(q1StartDate, q1EndDate, locList,detected);
		context.addParameterValue("xpertOnlySenQ1", xpertOnlySensitiveQ1 );
		
		String xpertOnlySensitiveQ2 = Context.getService(TbService.class).getXpertOnlyByResult(q2StartDate, q2EndDate, locList,detected);
		context.addParameterValue("xpertOnlySenQ2", xpertOnlySensitiveQ2);
		
		String xpertOnlySensitiveQ3 = Context.getService(TbService.class).getXpertOnlyByResult(q3StartDate, q3EndDate, locList,detected);
		context.addParameterValue("xpertOnlySenQ3", xpertOnlySensitiveQ3);
		
		String xpertOnlySensitiveQ4 = Context.getService(TbService.class).getXpertOnlyByResult(q4StartDate, q4EndDate, locList,detected);
		context.addParameterValue("xpertOnlySenQ4", xpertOnlySensitiveQ4);

		String xpertOnlySensitiveTotal = Context.getService(TbService.class).getXpertOnlyByResult(q1StartDate, q4EndDate, locList,detected);
		context.addParameterValue("xpertOnlySenTotal", xpertOnlySensitiveTotal);

		//XPERT Only Resistant
		
		Concept not_detected = Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED);
		
		String xpertOnlyResistantQ1 = Context.getService(TbService.class).getXpertOnlyByResult(q1StartDate, q1EndDate, locList,not_detected);
		context.addParameterValue("xpertOnlyResQ1", xpertOnlyResistantQ1 );
		
		String xpertOnlyResistantQ2 = Context.getService(TbService.class).getXpertOnlyByResult(q2StartDate, q2EndDate, locList,not_detected);
		context.addParameterValue("xpertOnlyResQ2", xpertOnlyResistantQ2);
		
		String xpertOnlyResistantQ3 = Context.getService(TbService.class).getXpertOnlyByResult(q3StartDate, q3EndDate, locList,not_detected);
		context.addParameterValue("xpertOnlyResQ3", xpertOnlyResistantQ3);
		
		String xpertOnlyResistantQ4 = Context.getService(TbService.class).getXpertOnlyByResult(q4StartDate, q4EndDate, locList,not_detected);
		context.addParameterValue("xpertOnlyResQ4", xpertOnlyResistantQ4);

		String xpertOnlyResistantTotal = Context.getService(TbService.class).getXpertOnlyByResult(q1StartDate, q4EndDate, locList,not_detected);
		context.addParameterValue("xpertOnlyResTotal", xpertOnlyResistantTotal);

		//XPERT only NEGATIVE
		//TODO: Verify that NEGATIVE is UNDETERMINED in Xpert
		Concept undetermined = Context.getService(TbService.class).getConcept(TbConcepts.UNDETERMINED);
		
		String xpertOnlyNegativeQ1 = Context.getService(TbService.class).getXpertOnlyByResult(q1StartDate, q1EndDate, locList,undetermined);
		context.addParameterValue("xpertOnlyNegQ1", xpertOnlyNegativeQ1 );
		
		String xpertOnlyNegativeQ2 = Context.getService(TbService.class).getXpertOnlyByResult(q2StartDate, q2EndDate, locList,undetermined);
		context.addParameterValue("xpertOnlyNegQ2", xpertOnlyNegativeQ2);
		
		String xpertOnlyNegativeQ3 = Context.getService(TbService.class).getXpertOnlyByResult(q3StartDate, q3EndDate, locList,undetermined);
		context.addParameterValue("xpertOnlyNegQ3", xpertOnlyNegativeQ3);
		
		String xpertOnlyNegativeQ4 = Context.getService(TbService.class).getXpertOnlyByResult(q4StartDate, q4EndDate, locList,undetermined);
		context.addParameterValue("xpertOnlyNegQ4", xpertOnlyNegativeQ4);

		String xpertOnlyNegativeTotal = Context.getService(TbService.class).getXpertOnlyByResult(q1StartDate, q4EndDate, locList,undetermined);
		context.addParameterValue("xpertOnlyNegTotal", xpertOnlyNegativeTotal);

		//Error
		String xpertOnlyErrorQ1 = Context.getService(TbService.class).getXpertError(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertErrorQ1", xpertOnlyErrorQ1 );
		
		String xpertOnlyErrorQ2 = Context.getService(TbService.class).getXpertError(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertErrorQ2", xpertOnlyErrorQ2);
		
		String xpertOnlyErrorQ3 = Context.getService(TbService.class).getXpertError(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertErrorQ3", xpertOnlyErrorQ3);
		
		String xpertOnlyErrorQ4 = Context.getService(TbService.class).getXpertError(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertErrorQ4", xpertOnlyErrorQ4);

		String xpertOnlyErrorTotal = Context.getService(TbService.class).getXpertError(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertErrorTotal", xpertOnlyErrorTotal);

		//No Results
		String xpertOnlyNoResultQ1 = Context.getService(TbService.class).getXpertNoResults(q1StartDate, q1EndDate, locList);
		context.addParameterValue("xpertNoResultsQ1", xpertOnlyNoResultQ1 );
		
		String xpertOnlyNoResultQ2 = Context.getService(TbService.class).getXpertNoResults(q2StartDate, q2EndDate, locList);
		context.addParameterValue("xpertNoResultsQ2", xpertOnlyNoResultQ2);
		
		String xpertOnlyNoResultQ3 = Context.getService(TbService.class).getXpertNoResults(q3StartDate, q3EndDate, locList);
		context.addParameterValue("xpertNoResultsQ3", xpertOnlyNoResultQ3);
		
		String xpertOnlyNoResultQ4 = Context.getService(TbService.class).getXpertNoResults(q4StartDate, q4EndDate, locList);
		context.addParameterValue("xpertNoResultsQ4", xpertOnlyNoResultQ4);

		String xpertOnlyNoResultTotal = Context.getService(TbService.class).getXpertNoResults(q1StartDate, q4EndDate, locList);
		context.addParameterValue("xpertNoResultsTotal", xpertOnlyNoResultTotal);

		
		CohortDefinition allLabResults = Cohorts.getAllLabResultDuring(startDate,endDate);
		CohortCrossTabDataSetDefinition d = new CohortCrossTabDataSetDefinition(); 
		d.addParameter(ReportingConstants.END_DATE_PARAMETER); 
		d.addColumn("total", allLabResults, null); 
		report.addDataSetDefinition("all", d, null); 
		ReportData results = null;
		try {
			results = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		} catch (EvaluationException e) {
			e.printStackTrace();
		} 
		return results;

		
	}

}