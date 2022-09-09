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
public class NRLReport implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("labmodule.labNRLReport1");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("labmodule.labNRLReport1");
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
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/NRL_Hain"+ 
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
		Concept rif = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept inh = Context.getService(TbService.class).getConcept(TbConcepts.ISONIAZID_RESISTANCE);
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT);
		
		////////			HAIN-1		//////////
		//Total
		String hain1TotalQ1 = Context.getService(TbService.class).getAllPatientTestedDuring(q1StartDate, q1EndDate,  locList,hain1);
		context.addParameterValue("hain1TotalQ1", hain1TotalQ1);
		
		String hain1TotalQ2 = Context.getService(TbService.class).getAllPatientTestedDuring(q2StartDate,  q2EndDate,  locList,hain1);
		context.addParameterValue("hain1TotalQ2", hain1TotalQ2);
		
		String hain1TotalQ3 = Context.getService(TbService.class).getAllPatientTestedDuring(q3StartDate,  q3EndDate,  locList,hain1);
		context.addParameterValue("hain1TotalQ3", hain1TotalQ3);
		
		String hain1TotalQ4 = Context.getService(TbService.class).getAllPatientTestedDuring(q4StartDate,  q4EndDate, locList,hain1);
		context.addParameterValue("hain1TotalQ4", hain1TotalQ4);
		
		String hain1TotalQz = Context.getService(TbService.class).getAllPatientTestedDuring(q1StartDate, q4EndDate, locList,hain1);
		context.addParameterValue("hain1Total", hain1TotalQz);
		
		//INH + RIF Sensitive
		String hain1SensitiveQ1 = Context.getService(TbService.class).getHainAllSensitive(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain1SensitiveHrQ1", hain1SensitiveQ1);
		
		String hain1SensitiveQ2 = Context.getService(TbService.class).getHainAllSensitive(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain1SensitiveHrQ2", hain1SensitiveQ2);
		
		String hain1SensitiveQ3 = Context.getService(TbService.class).getHainAllSensitive(q3StartDate,  q3EndDate,  locList);
		context.addParameterValue("hain1SensitiveHrQ3", hain1SensitiveQ3);
		
		String hain1SensitiveQ4 = Context.getService(TbService.class).getHainAllSensitive(q4StartDate,  q4EndDate, locList);
		context.addParameterValue("hain1SensitiveHrQ4", hain1SensitiveQ4);
		
		String hain1SensitiveTotal = Context.getService(TbService.class).getHainAllSensitive(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain1SensitiveHrTotal", hain1SensitiveTotal);
		
		//INH +  RIF Resistant
		String hain1ResistantQ1 = Context.getService(TbService.class).getHainAllResistant(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain1InhRifQ1", hain1ResistantQ1);

		String hain1ResistantQ2 = Context.getService(TbService.class).getHainAllResistant(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain1InhRifQ2", hain1ResistantQ2);

		String hain1ResistantQ3 = Context.getService(TbService.class).getHainAllResistant(q3StartDate,  q3EndDate,  locList);
		context.addParameterValue("hain1InhRifQ3", hain1ResistantQ3);

		String hain1ResistantQ4 = Context.getService(TbService.class).getHainAllResistant(q4StartDate,  q4EndDate, locList);
		context.addParameterValue("hain1InhRifQ4", hain1ResistantQ4);

		String hain1ResistantTotal = Context.getService(TbService.class).getHainAllResistant(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain1InhRifTotal", hain1ResistantTotal);
				
		//RIf Resistance Only
		String hain1RifResistantQ1 = Context.getService(TbService.class).getHainResistantByDrug(q1StartDate, q1EndDate,  rif, locList);
		context.addParameterValue("hain1RifQ1", hain1RifResistantQ1);

		String hain1RifResistantQ2 = Context.getService(TbService.class).getHainResistantByDrug(q2StartDate,  q2EndDate,  rif, locList);
		context.addParameterValue("hain1RifQ2", hain1RifResistantQ2);

		String hain1RifResistantQ3 = Context.getService(TbService.class).getHainResistantByDrug(q3StartDate,  q3EndDate,  rif, locList);
		context.addParameterValue("hain1RifQ3", hain1RifResistantQ3);

		String hain1RifResistantQ4 = Context.getService(TbService.class).getHainResistantByDrug(q4StartDate,  q4EndDate, rif, locList);
		context.addParameterValue("hain1RifQ4", hain1RifResistantQ4);

		String hain1RifResistantTotal = Context.getService(TbService.class).getHainResistantByDrug(q1StartDate, q4EndDate, rif, locList);
		context.addParameterValue("hain1RifTotal", hain1RifResistantTotal);

		//INH
		String hain1InhResistantQ1 = Context.getService(TbService.class).getHainResistantByDrug(q1StartDate, q1EndDate,  inh, locList);
		context.addParameterValue("hain1InhQ1", hain1InhResistantQ1);

		String hain1InhResistantQ2 = Context.getService(TbService.class).getHainResistantByDrug(q2StartDate,  q2EndDate,  inh, locList);
		context.addParameterValue("hain1InhQ2", hain1InhResistantQ2);

		String hain1InhResistantQ3 = Context.getService(TbService.class).getHainResistantByDrug(q3StartDate,  q3EndDate,  inh, locList);
		context.addParameterValue("hain1InhQ3", hain1InhResistantQ3);

		String hain1InhResistantQ4 = Context.getService(TbService.class).getHainResistantByDrug(q4StartDate,  q4EndDate, inh, locList);
		context.addParameterValue("hain1InhQ4", hain1InhResistantQ4);

		String hain1InhResistantTotal = Context.getService(TbService.class).getHainResistantByDrug(q1StartDate, q4EndDate, inh, locList);
		context.addParameterValue("hain1InhTotal", hain1InhResistantTotal );
		
		//INDEFINED
		String hain1IndefiniteQ1 = Context.getService(TbService.class).getHainAllIndefinite(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain1IndefiniteHrQ1", hain1IndefiniteQ1);
		
		String hain1IndefiniteQ2 = Context.getService(TbService.class).getHainAllIndefinite(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain1IndefiniteHrQ2", hain1IndefiniteQ2);
		
		String hain1IndefiniteQ3 = Context.getService(TbService.class).getHainAllIndefinite(q3StartDate,  q3EndDate,  locList);
		context.addParameterValue("hain1IndefiniteHrQ3", hain1IndefiniteQ3);
		
		String hain1IndefiniteQ4 = Context.getService(TbService.class).getHainAllIndefinite(q4StartDate,  q4EndDate, locList);
		context.addParameterValue("hain1IndefiniteHrQ4", hain1IndefiniteQ4);
		
		String hain1IndefiniteTotal = Context.getService(TbService.class).getHainAllIndefinite(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain1IndefiniteHrTotal", hain1IndefiniteTotal);
		
		//MTB NEGATIVE
		String hain1MtbQ1 = Context.getService(TbService.class).getHain1MTB(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain1MTBNegativeQ1", hain1MtbQ1);

		String hain1MtbQ2 = Context.getService(TbService.class).getHain1MTB(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain1MTBNegativeQ2", hain1MtbQ2);

		String hain1MtbQ3 = Context.getService(TbService.class).getHain1MTB(q3StartDate,  q3EndDate,  locList);
		context.addParameterValue("hain1MTBNegativeQ3", hain1MtbQ3);

		String hain1MtbQ4 = Context.getService(TbService.class).getHain1MTB(q4StartDate,  q4EndDate,locList);
		context.addParameterValue("hain1MTBNegativeQ4", hain1MtbQ4);

		String hain1MtbTotal = Context.getService(TbService.class).getHain1MTB(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain1MTBNegativeTotal",hain1MtbTotal );


		////////			HAIN-2		//////////
		//Total
		Concept hain2 = (Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT));

		String hain2TotalQ1 = Context.getService(TbService.class).getAllPatientTestedDuring(q1StartDate, q1EndDate,  locList, hain2);
		context.addParameterValue("hain2TotalQ1", hain2TotalQ1);
		
		String hain2TotalQ2 = Context.getService(TbService.class).getAllPatientTestedDuring(q2StartDate,  q2EndDate,  locList, hain2);
		context.addParameterValue("hain2TotalQ2", hain2TotalQ2);
		
		String hain2TotalQ3 = Context.getService(TbService.class).getAllPatientTestedDuring(q3StartDate,  q3EndDate,  locList, hain2);
		context.addParameterValue("hain2TotalQ3", hain2TotalQ3);
		
		String hain2TotalQ4 = Context.getService(TbService.class).getAllPatientTestedDuring(q4StartDate,  q4EndDate, locList, hain2);
		context.addParameterValue("hain2TotalQ4", hain2TotalQ4);

		String hain2TotalQz = Context.getService(TbService.class).getAllPatientTestedDuring(q1StartDate, q4EndDate, locList, hain2);
		context.addParameterValue("hain2Total", hain2TotalQz);
		
		//MTB NEGATIVE
		String hain2MtbQ1 = Context.getService(TbService.class).getHain2MTB(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain2MTBNegativeQ1", hain2MtbQ1);

		String hain2MtbQ2 = Context.getService(TbService.class).getHain2MTB(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain2MTBNegativeQ2", hain2MtbQ2);

		String hain2MtbQ3 = Context.getService(TbService.class).getHain2MTB(q3StartDate,  q3EndDate,  locList);
		context.addParameterValue("hain2MTBNegativeQ3", hain2MtbQ3);

		String hain2MtbQ4 = Context.getService(TbService.class).getHain2MTB(q4StartDate,  q4EndDate,locList);
		context.addParameterValue("hain2MTBNegativeQ4", hain2MtbQ4);

		String hain2MtbTotal = Context.getService(TbService.class).getHain2MTB(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain2MTBNegativeTotal",hain2MtbTotal );
		
		//Sensitive
		String hain2SensitiveQ1 = Context.getService(TbService.class).getHain2AllSensitive(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain2SensitiveQ1", hain2SensitiveQ1);

		String hain2SensitiveQ2 = Context.getService(TbService.class).getHain2AllSensitive(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain2SensitiveQ2", hain2SensitiveQ2);

		String hain2SensitiveQ3 = Context.getService(TbService.class).getHain2AllSensitive(q3StartDate,  q3EndDate,  locList);
		context.addParameterValue("hain2SensitiveQ3", hain2SensitiveQ3);

		String hain2SensitiveQ4 = Context.getService(TbService.class).getHain2AllSensitive(q4StartDate,  q4EndDate,  locList);
		context.addParameterValue("hain2SensitiveQ4", hain2SensitiveQ4);

		String hain2SensitiveTotal = Context.getService(TbService.class).getHain2AllSensitive(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain2SensitiveTotal", hain2SensitiveTotal);
		
		System.out.println("hain1TotalQz: " + hain1TotalQz);
		System.out.println("hain2TotalQz:: " + hain2TotalQz);
		
		//FLQ
		Concept mox = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		String hain2FlqQ1 = Context.getService(TbService.class).getHain2ResistanceByDrug(q1StartDate, q1EndDate,  locList, mox);
		context.addParameterValue("hain2FlqQ1", hain2FlqQ1);

		String hain2FlqQ2 = Context.getService(TbService.class).getHain2ResistanceByDrug(q2StartDate,  q2EndDate,  locList, mox);
		context.addParameterValue("hain2FlqQ2", hain2FlqQ2);

		String hain2FlqQ3 = Context.getService(TbService.class).getHain2ResistanceByDrug(q3StartDate,  q3EndDate,  locList, mox);
		context.addParameterValue("hain2FlqQ3", hain2FlqQ3);

		String hain2FlqQ4 = Context.getService(TbService.class).getHain2ResistanceByDrug(q4StartDate,  q4EndDate, locList, mox);
		context.addParameterValue("hain2FlqQ4", hain2FlqQ4);

		String hain2FlqTotal = Context.getService(TbService.class).getHain2ResistanceByDrug(q1StartDate, q4EndDate, locList, mox);
		context.addParameterValue("hain2FlqTotal", hain2FlqTotal);
		
		//FLQ + AG + CM
		Concept cm  = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		Concept emb  = Context.getService(TbService.class).getConcept(TbConcepts.E);
		
		String hain2AllQ1 = Context.getService(TbService.class).getHain2ResistanceFlqAgE(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain2AllQ1", hain2AllQ1);

		String hain2AllQ2 = Context.getService(TbService.class).getHain2ResistanceFlqAgE(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain2AllQ2", hain2AllQ2);

		String hain2AllQ3 = Context.getService(TbService.class).getHain2ResistanceFlqAgE(q3StartDate,  q3EndDate,  locList );
		context.addParameterValue("hain2AllQ3", hain2AllQ3);

		String hain2AllQ4 = Context.getService(TbService.class).getHain2ResistanceFlqAgE(q4StartDate,  q4EndDate, locList);
		context.addParameterValue("hain2AllQ4", hain2AllQ4);

		String hain2AllTotal = Context.getService(TbService.class).getHain2ResistanceFlqAgE(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain2AllTotal", hain2AllTotal);
		
		//FLQ + AG + CM - Indefinite
		String hain2IndefiniteQ1 = Context.getService(TbService.class).getHain2IndefiniteFlqAgE(q1StartDate, q1EndDate,  locList);
		context.addParameterValue("hain2IndefiniteQ1", hain2IndefiniteQ1);

		String hain2IndefiniteQ2 = Context.getService(TbService.class).getHain2IndefiniteFlqAgE(q2StartDate,  q2EndDate,  locList);
		context.addParameterValue("hain2IndefiniteQ2", hain2IndefiniteQ2);

		String hain2IndefiniteQ3 = Context.getService(TbService.class).getHain2IndefiniteFlqAgE(q3StartDate,  q3EndDate,  locList );
		context.addParameterValue("hain2IndefiniteQ3", hain2IndefiniteQ3);

		String hain2IndefiniteQ4 = Context.getService(TbService.class).getHain2IndefiniteFlqAgE(q4StartDate,  q4EndDate, locList);
		context.addParameterValue("hain2IndefiniteQ4", hain2IndefiniteQ4);

		String hain2IndefiniteTotal = Context.getService(TbService.class).getHain2IndefiniteFlqAgE(q1StartDate, q4EndDate, locList);
		context.addParameterValue("hain2IndefiniteTotal", hain2IndefiniteTotal);

		//FLQ + EMB
		String hain2FlqEmbQ1 = Context.getService(TbService.class).getHain2ResistanceFlqE(q1StartDate, q1EndDate,  locList);
		String hain2FlqEmbQ2 = Context.getService(TbService.class).getHain2ResistanceFlqE(q2StartDate,  q2EndDate,  locList);
		String hain2FlqEmbQ3 = Context.getService(TbService.class).getHain2ResistanceFlqE(q3StartDate,  q3EndDate,  locList);
		String hain2FlqEmbQ4 = Context.getService(TbService.class).getHain2ResistanceFlqE(q4StartDate,  q4EndDate, locList);
		String hain2FlqEmbTotal = Context.getService(TbService.class).getHain2ResistanceFlqE(q1StartDate, q4EndDate, locList);
		
		Integer iHainFlqEmbQ1 = Integer.valueOf(hain2FlqEmbQ1);
		Integer iHainFlqEmbQ2 = Integer.valueOf(hain2FlqEmbQ2);
		Integer iHainFlqEmbQ3 = Integer.valueOf(hain2FlqEmbQ3);
		Integer iHainFlqEmbQ4 = Integer.valueOf(hain2FlqEmbQ4);
		Integer iHainFlqEmbTotal = Integer.valueOf(hain2FlqEmbTotal);
		
		context.addParameterValue("hain2FlqEmbQ1", iHainFlqEmbQ1);
		context.addParameterValue("hain2FlqEmbQ2", iHainFlqEmbQ2);
		context.addParameterValue("hain2FlqEmbQ3", iHainFlqEmbQ3);
		context.addParameterValue("hain2FlqEmbQ4", iHainFlqEmbQ4);
		context.addParameterValue("hain2FlqEmbTotal", iHainFlqEmbTotal);
		
		//FLQ + AG/CM
		
		String hain2FlqCmQ1 = Context.getService(TbService.class).getHain2ResistanceFlqAg(q1StartDate, q1EndDate,  locList);
		String hain2FlqCmQ2 = Context.getService(TbService.class).getHain2ResistanceFlqAg(q2StartDate,  q2EndDate,  locList);
		String hain2FlqCmQ3 = Context.getService(TbService.class).getHain2ResistanceFlqAg(q3StartDate,  q3EndDate,  locList);
		String hain2FlqCmQ4 = Context.getService(TbService.class).getHain2ResistanceFlqAg(q4StartDate,  q4EndDate, locList);
		String hain2FlqCmTotal = Context.getService(TbService.class).getHain2ResistanceFlqAg(q1StartDate, q4EndDate, locList);		
		
		Integer iHainFlqCmQ1 = Integer.valueOf(hain2FlqCmQ1);
		Integer iHainFlqCmQ2 = Integer.valueOf(hain2FlqCmQ2);
		Integer iHainFlqCmQ3 = Integer.valueOf(hain2FlqCmQ3);
		Integer iHainFlqCmQ4 = Integer.valueOf(hain2FlqCmQ4);
		Integer iHainFlqCmTotal = Integer.valueOf(hain2FlqCmTotal);
		
		context.addParameterValue("hain2FlqCmQ1", iHainFlqCmQ1);
		context.addParameterValue("hain2FlqCmQ2", iHainFlqCmQ2);
		context.addParameterValue("hain2FlqCmQ3", iHainFlqCmQ3);
		context.addParameterValue("hain2FlqCmQ4", iHainFlqCmQ4);
		context.addParameterValue("hain2FlqCmTotal", iHainFlqCmTotal);
		
		//AG/CP
		String hain2CmQ1 = Context.getService(TbService.class).getHain2ResistanceByDrug(q1StartDate, q1EndDate,  locList, cm);
		String hain2CmQ2 = Context.getService(TbService.class).getHain2ResistanceByDrug(q2StartDate,  q2EndDate,  locList, cm);
		String hain2CmQ3 = Context.getService(TbService.class).getHain2ResistanceByDrug(q3StartDate,  q3EndDate,  locList, cm);
		String hain2CmQ4 = Context.getService(TbService.class).getHain2ResistanceByDrug(q4StartDate,  q4EndDate, locList, cm);
		String hain2CmTotal = Context.getService(TbService.class).getHain2ResistanceByDrug(q1StartDate, q4EndDate, locList, cm);		
		
		Integer iHain2CmQ1 =  Integer.valueOf(hain2CmQ1);
		Integer iHain2CmQ2 =  Integer.valueOf(hain2CmQ2);
		Integer iHain2CmQ3 =  Integer.valueOf(hain2CmQ3);
		Integer iHain2CmQ4 =  Integer.valueOf(hain2CmQ4);
		Integer iHain2CmTotal = Integer.valueOf(hain2CmTotal);
		
		context.addParameterValue("hain2CmQ1", iHain2CmQ1);
		context.addParameterValue("hain2CmQ2", iHain2CmQ2);
		context.addParameterValue("hain2CmQ3", iHain2CmQ3);
		context.addParameterValue("hain2CmQ4", iHain2CmQ4);
		context.addParameterValue("hain2CmTotal", iHain2CmTotal);
		
		
		//EMB
		String hain2EmbQ1 = Context.getService(TbService.class).getHain2ResistanceByDrug(q1StartDate, q1EndDate,  locList, emb);
		String hain2EmbQ2 = Context.getService(TbService.class).getHain2ResistanceByDrug(q2StartDate,  q2EndDate,  locList, emb);
		String hain2EmbQ3 = Context.getService(TbService.class).getHain2ResistanceByDrug(q3StartDate,  q3EndDate,  locList, emb);
		String hain2EmbQ4 = Context.getService(TbService.class).getHain2ResistanceByDrug(q4StartDate,  q4EndDate, locList, emb);
		String hain2EmbTotal = Context.getService(TbService.class).getHain2ResistanceByDrug(q1StartDate, q4EndDate, locList, emb);		
		
		Integer iHain2EmbQ1 =  Integer.valueOf(hain2EmbQ1);
		Integer iHain2EmbQ2 =  Integer.valueOf(hain2EmbQ2);
		Integer iHain2EmbQ3 =  Integer.valueOf(hain2EmbQ3);
		Integer iHain2EmbQ4 =  Integer.valueOf(hain2EmbQ4);
		Integer iHain2EmbTotal = Integer.valueOf(hain2EmbTotal);
		
		context.addParameterValue("hain2EmbQ1", iHain2EmbQ1);
		context.addParameterValue("hain2EmbQ2", iHain2EmbQ2);
		context.addParameterValue("hain2EmbQ3", iHain2EmbQ3);
		context.addParameterValue("hain2EmbQ4", iHain2EmbQ4);
		context.addParameterValue("hain2EmbTotal", iHain2EmbTotal);
		
		//AG/CP + EMB
		String hain2CmEmbQ1 = Context.getService(TbService.class).getHain2ResistanceAgE(q1StartDate, q1EndDate,  locList);
		String hain2CmEmbQ2 = Context.getService(TbService.class).getHain2ResistanceAgE(q2StartDate,  q2EndDate,  locList);
		String hain2CmEmbQ3 = Context.getService(TbService.class).getHain2ResistanceAgE(q3StartDate,  q3EndDate,  locList);
		String hain2CmEmbQ4 = Context.getService(TbService.class).getHain2ResistanceAgE(q4StartDate,  q4EndDate, locList);
		String hain2CmEmbTotal = Context.getService(TbService.class).getHain2ResistanceAgE(q1StartDate, q4EndDate, locList);
		
		Integer iHainCmEmbQ1 = Integer.valueOf(hain2CmEmbQ1);
		Integer iHainCmEmbQ2 = Integer.valueOf(hain2CmEmbQ2);
		Integer iHainCmEmbQ3 = Integer.valueOf(hain2CmEmbQ3);
		Integer iHainCmEmbQ4 = Integer.valueOf(hain2CmEmbQ4);
		Integer iHainCmEmbTotal = Integer.valueOf(hain2CmEmbTotal);
		
		context.addParameterValue("hain2CmEmbQ1", iHainCmEmbQ1);
		context.addParameterValue("hain2CmEmbQ2", iHainCmEmbQ2);
		context.addParameterValue("hain2CmEmbQ3", iHainCmEmbQ3);
		context.addParameterValue("hain2CmEmbQ4", iHainCmEmbQ4);
		context.addParameterValue("hain2CmEmbTotal", iHainCmEmbTotal);
		
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