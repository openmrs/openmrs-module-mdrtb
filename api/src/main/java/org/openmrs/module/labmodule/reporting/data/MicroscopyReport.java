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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
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
import org.springframework.web.bind.annotation.ModelAttribute;

public class MicroscopyReport implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("labmodule.labReports");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("labmodule.report.description");
	}
	
	@ModelAttribute("labResults")
	public Collection<Oblast> getLabResults() {
		
		Location location = null;
		return Context.getService(TbService.class).getOblasts();
		
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
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/Microscopy" + 
			(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	

	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
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
	@SuppressWarnings("unchecked")
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
		
		List<Location> locList = Context.getService(TbService.class).getLocationList(oblast, district, facility);
		
		if(locList==null)
			locList=Context.getLocationService().getAllLocations();
		
		ReportDefinition report = new ReportDefinition(); 
		
		Map<String,Date>dateMap = ReportUtil.getPeriodDates(year, "1", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q1Test = Context.getService(TbService.class).getAllPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1Test", q1Test); 
		String q1DiagnosticTest = Context.getService(TbService.class).getAllDiagnosticPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1DiagnosticTest", q1DiagnosticTest); 
		String q1TreatmentControlTest = Context.getService(TbService.class).getAllFollowupPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1TreatmentControlTest", q1TreatmentControlTest); 
		String q1PositiveTest = Context.getService(TbService.class).getAllPositivePatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositiveTest", q1PositiveTest);
		
		if(q1PositiveTest.equals("0") || q1Test.equals("0"))
			context.addParameterValue("q1PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q1PositiveTest)/Float.parseFloat(q1Test))*100;
			context.addParameterValue("q1PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q1Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1Microscopy", q1Microscopy); 
		String q1PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositiveMicroscopyTest", q1PositiveMicroscopyTest);
		if(q1PositiveMicroscopyTest.equals("0") || q1Microscopy.equals("0"))
			context.addParameterValue("q1PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per = (Float.parseFloat(q1PositiveMicroscopyTest)/Float.parseFloat(q1Microscopy))*100;
			context.addParameterValue("q1PositiveMicroscopyPercentageTest", String.format("%.2f", per) + "%"); 
		}
		
		
		String q1PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositiveDiagnosticTest", q1PositiveDiagnosticTest); 
		if(q1PositiveDiagnosticTest.equals("0") || q1DiagnosticTest.equals("0"))
			context.addParameterValue("q1DiagnosticPercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q1PositiveDiagnosticTest)/Float.parseFloat(q1DiagnosticTest))*100;
			context.addParameterValue("q1DiagnosticPercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q1DiagnosticMicroscopyTest = Context.getService(TbService.class).getDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1DiagnosticMicroscopyTest", q1DiagnosticMicroscopyTest); 
		String q1PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositiveMicroscopyDiagnosticTest", q1PositiveMicroscopyDiagnosticTest);
		if(q1PositiveMicroscopyDiagnosticTest.equals("0") || q1DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q1PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q1PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q1DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q1PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		
		String q1PositiveTreatmentControlTest = Context.getService(TbService.class).getPositiveFollowupPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositiveTreatmentControlTest", q1PositiveTreatmentControlTest);
		if(q1PositiveTreatmentControlTest.equals("0") || q1TreatmentControlTest.equals("0"))
			context.addParameterValue("q1TreatmentControlPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q1PositiveTreatmentControlTest)/Float.parseFloat(q1TreatmentControlTest))*100;
			context.addParameterValue("q1TreatmentControlPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q1TreatmentControlMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1TreatmentControlMicroscopyTest", q1TreatmentControlMicroscopyTest); 
		String q1PositiveMicroscopyTreatmentControlTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositiveMicroscopyTreatmentControlTest", q1PositiveMicroscopyTreatmentControlTest);
		if(q1PositiveMicroscopyTreatmentControlTest.equals("0") || q1TreatmentControlMicroscopyTest.equals("0"))
			context.addParameterValue("q1PositiveMicroscopyPercentageTreatmentControlTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q1PositiveMicroscopyTreatmentControlTest)/Float.parseFloat(q1TreatmentControlMicroscopyTest))*100;
			context.addParameterValue("q1PositiveMicroscopyPercentageTreatmentControlTest", String.format("%.2f", per5) + "%"); 
		}
		String q1PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PHCTest", q1PHCTest);
		String q1PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1PositivePHCTest", q1PositivePHCTest);
		//Omar
		String q1DiagnosticRatio = "N/A"/*Context.getService(TbService.class).getRatioDiagnosticMicroscopyResultDuring(startDate, endDate, locList)*/;
		
		if(q1DiagnosticTest != null && (!q1DiagnosticTest.trim().equals("") && (!q1DiagnosticTest.trim().equals("0"))
				&& q1DiagnosticMicroscopyTest != null && (!q1DiagnosticMicroscopyTest.trim().equals("")) && (!q1DiagnosticMicroscopyTest.equalsIgnoreCase("0") )))
		{
			Float q1Multicipility ;
			q1Multicipility = (Float.parseFloat(q1DiagnosticMicroscopyTest)/Float.parseFloat(q1DiagnosticTest));
			q1DiagnosticRatio = String.format("%.2f", q1Multicipility);
		}
		context.addParameterValue("q1DiagnosticRatio", q1DiagnosticRatio);
		
		String q1TreatmentControlRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1TreatmentControlRatio", q1TreatmentControlRatio);
		String q1DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q1DiagnosticSaliva", q1DiagnosticSaliva);
		if(q1DiagnosticSaliva.equals("0") || q1DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q1MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q1DiagnosticSaliva)/Float.parseFloat(q1DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q1MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q1WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate,  endDate,  locList, 13);
		context.addParameterValue("q1WeeklyLoad", q1WeeklyLoad);
		
		dateMap = ReportUtil.getPeriodDates(year, "2", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q2Test = Context.getService(TbService.class).getAllPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2Test", q2Test); 
		String q2Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2Microscopy", q2Microscopy); 
		String q2DiagnosticTest = Context.getService(TbService.class).getAllDiagnosticPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2DiagnosticTest", q2DiagnosticTest); 
		String q2DiagnosticMicroscopyTest = Context.getService(TbService.class).getDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2DiagnosticMicroscopyTest", q2DiagnosticMicroscopyTest); 
		String q2TreatmentControlTest = Context.getService(TbService.class).getAllFollowupPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2TreatmentControlTest", q2TreatmentControlTest); 
		String q2TreatmentControlMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2TreatmentControlMicroscopyTest", q2TreatmentControlMicroscopyTest); 
		String q2PositiveTest = Context.getService(TbService.class).getAllPositivePatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositiveTest", q2PositiveTest); 
		if(q2PositiveTest.equals("0") || q2Test.equals("0"))
			context.addParameterValue("q2PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q2PositiveTest)/Float.parseFloat(q2Test))*100;
			context.addParameterValue("q2PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q2PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositiveDiagnosticTest", q2PositiveDiagnosticTest); 
		if(q2PositiveDiagnosticTest.equals("0") || q2DiagnosticTest.equals("0"))
			context.addParameterValue("q2DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q2PositiveDiagnosticTest)/Float.parseFloat(q2DiagnosticTest))*100;
			context.addParameterValue("q2DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q2PositiveTreatmentControlTest = Context.getService(TbService.class).getPositiveFollowupPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositiveTreatmentControlTest", q2PositiveTreatmentControlTest);
		if(q2PositiveTreatmentControlTest.equals("0") || q2TreatmentControlTest.equals("0"))
			context.addParameterValue("q2TreatmentControlPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q2PositiveTreatmentControlTest)/Float.parseFloat(q2TreatmentControlTest))*100;
			context.addParameterValue("q2TreatmentControlPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q2PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositiveMicroscopyTest", q2PositiveMicroscopyTest); 
		if(q2PositiveMicroscopyTest.equals("0") || q2Microscopy.equals("0"))
			context.addParameterValue("q2PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q2PositiveMicroscopyTest)/Float.parseFloat(q2Microscopy))*100;
			context.addParameterValue("q2PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q2PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositiveMicroscopyDiagnosticTest", q2PositiveMicroscopyDiagnosticTest);
		if(q2PositiveMicroscopyDiagnosticTest.equals("0") || q2DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q2PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q2PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q2DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q2PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q2PositiveMicroscopyTreatmentControlTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositiveMicroscopyTreatmentControlTest", q2PositiveMicroscopyTreatmentControlTest);
		if(q2PositiveMicroscopyTreatmentControlTest.equals("0") || q2TreatmentControlMicroscopyTest.equals("0"))
			context.addParameterValue("q2PositiveMicroscopyPercentageTreatmentControlTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q2PositiveMicroscopyTreatmentControlTest)/Float.parseFloat(q2TreatmentControlMicroscopyTest))*100;
			context.addParameterValue("q2PositiveMicroscopyPercentageTreatmentControlTest", String.format("%.2f", per5) + "%"); 
		}
		String q2PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PHCTest", q2PHCTest);
		String q2PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2PositivePHCTest", q2PositivePHCTest);
		String q2DiagnosticRatio = "N/A"/*Context.getService(TbService.class).getRatioDiagnosticMicroscopyResultDuring(startDate, endDate, locList)*/;
		if(q2DiagnosticTest != null && (!q2DiagnosticTest.trim().equals("") && (!q2DiagnosticTest.trim().equals("0"))
				&& q2DiagnosticMicroscopyTest != null && (!q2DiagnosticMicroscopyTest.trim().equals("")) && (!q2DiagnosticMicroscopyTest.equalsIgnoreCase("0") )))
		{
			Float q2Multicipility ;
			q2Multicipility = (Float.parseFloat(q2DiagnosticMicroscopyTest)/Float.parseFloat(q2DiagnosticTest));
			q2DiagnosticRatio = String.format("%.2f", q2Multicipility);
		}
		context.addParameterValue("q2DiagnosticRatio", q2DiagnosticRatio);
		String q2TreatmentControlRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2TreatmentControlRatio", q2TreatmentControlRatio);
		String q2DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q2DiagnosticSaliva", q2DiagnosticSaliva);
		if(q2DiagnosticSaliva.equals("0") || q2DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q2MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q2DiagnosticSaliva)/Float.parseFloat(q2DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q2MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q2WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate,  endDate,  locList, 13);
		context.addParameterValue("q2WeeklyLoad", q2WeeklyLoad);
		
		dateMap = ReportUtil.getPeriodDates(year, "3", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q3Test = Context.getService(TbService.class).getAllPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3Test", q3Test); 
		String q3Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3Microscopy", q3Microscopy); 
		String q3DiagnosticTest = Context.getService(TbService.class).getAllDiagnosticPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3DiagnosticTest", q3DiagnosticTest); 
		String q3DiagnosticMicroscopyTest = Context.getService(TbService.class).getDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3DiagnosticMicroscopyTest", q3DiagnosticMicroscopyTest); 
		String q3TreatmentControlTest = Context.getService(TbService.class).getAllFollowupPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3TreatmentControlTest", q3TreatmentControlTest); 
		String q3TreatmentControlMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3TreatmentControlMicroscopyTest", q3TreatmentControlMicroscopyTest); 
		String q3PositiveTest = Context.getService(TbService.class).getAllPositivePatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositiveTest", q3PositiveTest); 
		if(q3PositiveTest.equals("0") || q3Test.equals("0"))
			context.addParameterValue("q3PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q3PositiveTest)/Float.parseFloat(q3Test))*100;
			context.addParameterValue("q3PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q3PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositiveDiagnosticTest", q3PositiveDiagnosticTest); 
		if(q3PositiveDiagnosticTest.equals("0") || q3DiagnosticTest.equals("0"))
			context.addParameterValue("q3DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q3PositiveDiagnosticTest)/Float.parseFloat(q3DiagnosticTest))*100;
			context.addParameterValue("q3DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q3PositiveTreatmentControlTest = Context.getService(TbService.class).getPositiveFollowupPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositiveTreatmentControlTest", q3PositiveTreatmentControlTest);
		if(q3PositiveTreatmentControlTest.equals("0") || q3TreatmentControlTest.equals("0"))
			context.addParameterValue("q3TreatmentControlPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q3PositiveTreatmentControlTest)/Float.parseFloat(q3TreatmentControlTest))*100;
			context.addParameterValue("q3TreatmentControlPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q3PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositiveMicroscopyTest", q3PositiveMicroscopyTest); 
		if(q3PositiveMicroscopyTest.equals("0") || q3Microscopy.equals("0"))
			context.addParameterValue("q3PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q3PositiveMicroscopyTest)/Float.parseFloat(q3Microscopy))*100;
			context.addParameterValue("q3PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q3PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositiveMicroscopyDiagnosticTest", q3PositiveMicroscopyDiagnosticTest);
		if(q3PositiveMicroscopyDiagnosticTest.equals("0") || q3DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q3PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q3PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q3DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q3PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q3PositiveMicroscopyTreatmentControlTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositiveMicroscopyTreatmentControlTest", q3PositiveMicroscopyTreatmentControlTest);
		if(q3PositiveMicroscopyTreatmentControlTest.equals("0") || q3TreatmentControlMicroscopyTest.equals("0"))
			context.addParameterValue("q3PositiveMicroscopyPercentageTreatmentControlTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q3PositiveMicroscopyTreatmentControlTest)/Float.parseFloat(q3TreatmentControlMicroscopyTest))*100;
			context.addParameterValue("q3PositiveMicroscopyPercentageTreatmentControlTest", String.format("%.2f", per5) + "%"); 
		}
		String q3PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PHCTest", q3PHCTest);
		String q3PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3PositivePHCTest", q3PositivePHCTest);
		String q3DiagnosticRatio = "N/A"/*Context.getService(TbService.class).getRatioDiagnosticMicroscopyResultDuring(startDate, endDate, locList)*/;
		if(q3DiagnosticTest != null && (!q3DiagnosticTest.trim().equals("") && (!q3DiagnosticTest.trim().equals("0"))
				&& q3DiagnosticMicroscopyTest != null && (!q3DiagnosticMicroscopyTest.trim().equals("")) && (!q3DiagnosticMicroscopyTest.equalsIgnoreCase("0") )))
		{
			Float q3Multicipility ;
			q3Multicipility = (Float.parseFloat(q3DiagnosticMicroscopyTest)/Float.parseFloat(q3DiagnosticTest));
			q1DiagnosticRatio = String.format("%.2f", q3Multicipility);
		}		
		context.addParameterValue("q3DiagnosticRatio", q3DiagnosticRatio);
		String q3TreatmentControlRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3TreatmentControlRatio", q3TreatmentControlRatio);
		String q3DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q3DiagnosticSaliva", q3DiagnosticSaliva);
		if(q3DiagnosticSaliva.equals("0") || q3DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q3MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q3DiagnosticSaliva)/Float.parseFloat(q3DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q3MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q3WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate,  endDate,  locList, 13);
		context.addParameterValue("q3WeeklyLoad", q3WeeklyLoad);
		
		dateMap = ReportUtil.getPeriodDates(year, "4", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q4Test = Context.getService(TbService.class).getAllPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4Test", q4Test); 
		String q4Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4Microscopy", q4Microscopy); 
		String q4DiagnosticTest = Context.getService(TbService.class).getAllDiagnosticPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4DiagnosticTest", q4DiagnosticTest); 
		String q4DiagnosticMicroscopyTest = Context.getService(TbService.class).getDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4DiagnosticMicroscopyTest", q4DiagnosticMicroscopyTest); 
		String q4TreatmentControlTest = Context.getService(TbService.class).getAllFollowupPatientTestedDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4TreatmentControlTest", q4TreatmentControlTest); 
		String q4TreatmentControlMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4TreatmentControlMicroscopyTest", q4TreatmentControlMicroscopyTest); 
		String q4PositiveTest = Context.getService(TbService.class).getAllPositivePatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositiveTest", q4PositiveTest); 
		if(q4PositiveTest.equals("0") || q4Test.equals("0"))
			context.addParameterValue("q4PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q4PositiveTest)/Float.parseFloat(q4Test))*100;
			context.addParameterValue("q4PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q4PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositiveDiagnosticTest", q4PositiveDiagnosticTest); 
		if(q4PositiveDiagnosticTest.equals("0") || q4DiagnosticTest.equals("0"))
			context.addParameterValue("q4DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q4PositiveDiagnosticTest)/Float.parseFloat(q4DiagnosticTest))*100;
			context.addParameterValue("q4DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q4PositiveTreatmentControlTest = Context.getService(TbService.class).getPositiveFollowupPatientDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositiveTreatmentControlTest", q4PositiveTreatmentControlTest);
		if(q4PositiveTreatmentControlTest.equals("0") || q4TreatmentControlTest.equals("0"))
			context.addParameterValue("q4TreatmentControlPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q4PositiveTreatmentControlTest)/Float.parseFloat(q4TreatmentControlTest))*100;
			context.addParameterValue("q4TreatmentControlPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q4PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositiveMicroscopyTest", q4PositiveMicroscopyTest); 
		if(q4PositiveMicroscopyTest.equals("0") || q4Microscopy.equals("0"))
			context.addParameterValue("q4PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q4PositiveMicroscopyTest)/Float.parseFloat(q4Microscopy))*100;
			context.addParameterValue("q4PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q4PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositiveMicroscopyDiagnosticTest", q4PositiveMicroscopyDiagnosticTest);
		if(q4PositiveMicroscopyDiagnosticTest.equals("0") || q4DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q4PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q4PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q4DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q4PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q4PositiveMicroscopyTreatmentControlTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositiveMicroscopyTreatmentControlTest", q4PositiveMicroscopyTreatmentControlTest);
		if(q4PositiveMicroscopyTreatmentControlTest.equals("0") || q4TreatmentControlMicroscopyTest.equals("0"))
			context.addParameterValue("q4PositiveMicroscopyPercentageTreatmentControlTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q4PositiveMicroscopyTreatmentControlTest)/Float.parseFloat(q4TreatmentControlMicroscopyTest))*100;
			context.addParameterValue("q4PositiveMicroscopyPercentageTreatmentControlTest", String.format("%.2f", per5) + "%"); 
		}
		String q4PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PHCTest", q4PHCTest);
		String q4PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4PositivePHCTest", q4PositivePHCTest);
		String q4DiagnosticRatio = "N/A"/*Context.getService(TbService.class).getRatioDiagnosticMicroscopyResultDuring(startDate, endDate, locList)*/;
		if(q4DiagnosticTest != null && (!q4DiagnosticTest.trim().equals("") && (!q4DiagnosticTest.trim().equals("0"))
				&& q4DiagnosticMicroscopyTest != null && (!q4DiagnosticMicroscopyTest.trim().equals("")) && (!q4DiagnosticMicroscopyTest.equalsIgnoreCase("0") )))
		{
			Float q4Multicipility ;
			q4Multicipility = (Float.parseFloat(q4DiagnosticMicroscopyTest)/Float.parseFloat(q4DiagnosticTest));
			q4DiagnosticRatio = String.format("%.2f", q4Multicipility);
		}
		context.addParameterValue("q4DiagnosticRatio", q4DiagnosticRatio);
		String q4TreatmentControlRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4TreatmentControlRatio", q4TreatmentControlRatio);
		String q4DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("q4DiagnosticSaliva", q4DiagnosticSaliva);
		if(q4DiagnosticSaliva.equals("0") || q4DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q4MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q4DiagnosticSaliva)/Float.parseFloat(q4DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q4MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q4WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate,  endDate,  locList, 13);
		context.addParameterValue("q4WeeklyLoad", q4WeeklyLoad);
		
		Float totalTest = Float.parseFloat(q4Test) + Float.parseFloat(q3Test) + Float.parseFloat(q2Test) + Float.parseFloat(q1Test);
		context.addParameterValue("totalTest", String.format("%.0f", totalTest)); 
		Float allPositiveTest = Float.parseFloat(q4PositiveTest) + Float.parseFloat(q3PositiveTest) + Float.parseFloat(q2PositiveTest) + Float.parseFloat(q1PositiveTest);
		context.addParameterValue("allPositiveTest", String.format("%.0f", allPositiveTest)); 
		if(allPositiveTest == 0 || totalTest == 0)
			context.addParameterValue("allPercentageTest", "-"); 
		else{
			Float per = (allPositiveTest/totalTest)*100;
			context.addParameterValue("allPercentageTest", String.format("%.2f", per) + "%"); 
		}
		
		Float totalMicroscopy = Float.parseFloat(q4Microscopy) + Float.parseFloat(q3Microscopy) + Float.parseFloat(q2Microscopy) + Float.parseFloat(q1Microscopy);
		context.addParameterValue("totalMicroscopy", String.format("%.0f", totalMicroscopy));
		Float allPositiveMicroscopyTest = Float.parseFloat(q4PositiveMicroscopyTest) + Float.parseFloat(q3PositiveMicroscopyTest) + Float.parseFloat(q2PositiveMicroscopyTest) + Float.parseFloat(q1PositiveMicroscopyTest);
		context.addParameterValue("allPositiveMicroscopyTest", String.format("%.0f", allPositiveMicroscopyTest)); 
		if(allPositiveMicroscopyTest == 0 || totalMicroscopy == 0)
			context.addParameterValue("allPositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (allPositiveMicroscopyTest/totalMicroscopy)*100;
			context.addParameterValue("allPositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		Float totalDiagnosticTest = Float.parseFloat(q4DiagnosticTest) + Float.parseFloat(q3DiagnosticTest) + Float.parseFloat(q2DiagnosticTest) + Float.parseFloat(q1DiagnosticTest);
		context.addParameterValue("totalDiagnosticTest", String.format("%.0f", totalDiagnosticTest)); 
		Float allPositiveDiagnosticTest = Float.parseFloat(q4PositiveDiagnosticTest) + Float.parseFloat(q3PositiveDiagnosticTest) + Float.parseFloat(q2PositiveDiagnosticTest) + Float.parseFloat(q1PositiveDiagnosticTest);
		context.addParameterValue("allPositiveDiagnosticTest", String.format("%.0f", allPositiveDiagnosticTest)); 
		if(allPositiveDiagnosticTest == 0 || totalDiagnosticTest == 0)
			context.addParameterValue("allDiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (allPositiveDiagnosticTest/totalDiagnosticTest)*100;
			context.addParameterValue("allDiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		Float totalDiagnosticMicroscopyTest = Float.parseFloat(q4DiagnosticMicroscopyTest) + Float.parseFloat(q3DiagnosticMicroscopyTest) + Float.parseFloat(q2DiagnosticMicroscopyTest) + Float.parseFloat(q1DiagnosticMicroscopyTest);
		context.addParameterValue("totalDiagnosticMicroscopyTest", String.format("%.0f", totalDiagnosticMicroscopyTest)); 
		Float allPositiveMicroscopyDiagnosticTest = Float.parseFloat(q4PositiveMicroscopyDiagnosticTest) + Float.parseFloat(q3PositiveMicroscopyDiagnosticTest) + Float.parseFloat(q2PositiveMicroscopyDiagnosticTest) + Float.parseFloat(q1PositiveMicroscopyDiagnosticTest);
		context.addParameterValue("allPositiveMicroscopyDiagnosticTest", String.format("%.0f", allPositiveMicroscopyDiagnosticTest));
		if(allPositiveMicroscopyDiagnosticTest == 0 || totalDiagnosticMicroscopyTest == 0)
			context.addParameterValue("allPositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (allPositiveMicroscopyDiagnosticTest/totalDiagnosticMicroscopyTest)*100;
			context.addParameterValue("allPositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		
		Float totalTreatmentControlTest = Float.parseFloat(q4TreatmentControlTest) + Float.parseFloat(q3TreatmentControlTest) + Float.parseFloat(q2TreatmentControlTest) + Float.parseFloat(q1TreatmentControlTest);
		context.addParameterValue("totalTreatmentControlTest", String.format("%.0f", totalTreatmentControlTest)); 
		Float allPositiveTreatmentControlTest = Float.parseFloat(q4PositiveTreatmentControlTest) + Float.parseFloat(q3PositiveTreatmentControlTest) + Float.parseFloat(q2PositiveTreatmentControlTest) + Float.parseFloat(q1PositiveTreatmentControlTest);
		context.addParameterValue("allPositiveTreatmentControlTest", String.format("%.0f", allPositiveTreatmentControlTest));
		if(allPositiveTreatmentControlTest == 0 || totalTreatmentControlTest == 0)
			context.addParameterValue("allTreatmentControlPercentageTest", "-"); 
		else{
			Float per2 = (allPositiveTreatmentControlTest/totalTreatmentControlTest)*100;
			context.addParameterValue("allTreatmentControlPercentageTest", String.format("%.2f", per2) + "%"); 
		}	
		Float totalTreatmentControlMicroscopyTest = Float.parseFloat(q4TreatmentControlMicroscopyTest) + Float.parseFloat(q3TreatmentControlMicroscopyTest) + Float.parseFloat(q2TreatmentControlMicroscopyTest) + Float.parseFloat(q1TreatmentControlMicroscopyTest);
		context.addParameterValue("totalTreatmentControlMicroscopyTest", String.format("%.0f", totalTreatmentControlMicroscopyTest)); 
		Float allPositiveMicroscopyTreatmentControlTest = Float.parseFloat(q4PositiveMicroscopyTreatmentControlTest) + Float.parseFloat(q3PositiveMicroscopyTreatmentControlTest) + Float.parseFloat(q2PositiveMicroscopyTreatmentControlTest) + Float.parseFloat(q1PositiveMicroscopyTreatmentControlTest);
		context.addParameterValue("allPositiveMicroscopyTreatmentControlTest",  String.format("%.0f", allPositiveMicroscopyTreatmentControlTest));
		if(allPositiveMicroscopyTreatmentControlTest == 0|| totalTreatmentControlMicroscopyTest == 0)
			context.addParameterValue("allPositiveMicroscopyPercentageTreatmentControlTest",   "-"); 
		else{
			Float per5 = (allPositiveMicroscopyTreatmentControlTest/totalTreatmentControlMicroscopyTest)*100;
			context.addParameterValue("allPositiveMicroscopyPercentageTreatmentControlTest", String.format("%.2f", per5) + "%"); 
		}
		
		Float allPHCTest = Float.parseFloat(q4PHCTest) + Float.parseFloat(q3PHCTest) + Float.parseFloat(q2PHCTest) + Float.parseFloat(q1PHCTest);
		context.addParameterValue("allPHCTest",  String.format("%.0f",allPHCTest));
		Float allPositivePHCTest = Float.parseFloat(q4PositivePHCTest) + Float.parseFloat(q3PositivePHCTest) + Float.parseFloat(q2PositivePHCTest) + Float.parseFloat(q1PositivePHCTest);
		context.addParameterValue("allPositivePHCTest",  String.format("%.0f",allPositivePHCTest));
		
		dateMap = ReportUtil.getPeriodDates(year, null, null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String allDiagnosticRatio = "N/A"/*Context.getService(TbService.class).getRatioDiagnosticMicroscopyResultDuring(startDate, endDate, locList)*/;
		if(totalDiagnosticTest != null  && !(totalDiagnosticTest == 0) 
				&& totalDiagnosticMicroscopyTest != null && !(totalDiagnosticMicroscopyTest == 0) )
		{
			Float totalMulticipility ;
			totalMulticipility = ((totalDiagnosticMicroscopyTest)/(totalDiagnosticTest));
			allDiagnosticRatio = String.format("%.2f", totalMulticipility);
		}		
		context.addParameterValue("allDiagnosticRatio", allDiagnosticRatio);
		
		String allTreatmentControlRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate,  endDate,  locList);
		context.addParameterValue("allTreatmentControlRatio", allTreatmentControlRatio);
		String allDiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate,  endDate, locList);
		context.addParameterValue("allDiagnosticSaliva", allDiagnosticSaliva);
		if(allDiagnosticSaliva.equals("0") || totalDiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("allMicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(allDiagnosticSaliva)/totalDiagnosticMicroscopyTest)*100;
			context.addParameterValue("allMicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String totalWeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate,  endDate,  locList, 52);
		context.addParameterValue("totalWeeklyLoad", totalWeeklyLoad);
		
		if(context.getParameterValue("district")!=null && context.getParameterValue("district")!="")
	 		district = (Integer) context.getParameterValue("district");
		
		
		if (!locList.isEmpty()){
			List<CohortDefinition> cohortDefinitions = new ArrayList<CohortDefinition>();
			for(Location loc : locList)
				cohortDefinitions.add(Cohorts.getLocationFilter(loc, startDate, endDate));
				
			if(!cohortDefinitions.isEmpty()){
				report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("OR", cohortDefinitions), null);
			}
		}
		
	 CohortDefinition allLabResults = Cohorts.getAllLabResultDuring(startDate,endDate);
		
		CohortCrossTabDataSetDefinition d = new CohortCrossTabDataSetDefinition(); 
		d.addParameter(ReportingConstants.END_DATE_PARAMETER); 
		d.addColumn("total", allLabResults, null); 
		report.addDataSetDefinition("all", d, null); 
		   
		ReportData results = null;
		  
		try {
			results = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return results;
	}

}
