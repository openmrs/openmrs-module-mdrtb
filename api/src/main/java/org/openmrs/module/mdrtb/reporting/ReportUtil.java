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
package org.openmrs.module.mdrtb.reporting;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.reporting.data.Cohorts;
import org.openmrs.module.mdrtb.reporting.definition.DstResultCohortDefinition;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Utility methods
 */
public class ReportUtil {
	
	public static CodedObsCohortDefinition getCodedObsCohort(TimeModifier tm, Integer questionId, 
																  Date fromDate, Date toDate, SetComparator operator, 
																  Integer... answerIds) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(tm);
		cd.setQuestion(Context.getConceptService().getConcept(questionId));
		cd.setOnOrAfter(fromDate);
		cd.setOnOrBefore(toDate);
		cd.setOperator(operator);
		List<Concept> answers = new ArrayList<Concept>();
		for (Integer id : answerIds) {
			answers.add(Context.getConceptService().getConcept(id));
		}
		cd.setValueList(answers);
		return cd;
	}
	
	public static DstResultCohortDefinition getDstProfileCohort(String profile, Date effectiveDate) {
		DstResultCohortDefinition cd = new DstResultCohortDefinition();
		cd.setMaxResultDate(effectiveDate);
		cd.setSpecificProfile(profile);
		return cd;
	}
	
	public static CohortDefinition getCompositionCohort(Map<String, Mapped<? extends CohortDefinition>> entries, String operator) {
		if (entries.size() == 1) {
			return entries.values().iterator().next().getParameterizable();
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Mapped<? extends CohortDefinition>> cd : entries.entrySet()) {
			d.addSearch(cd.getKey(), cd.getValue().getParameterizable(), cd.getValue().getParameterMappings());
			if (s.length() > 0) {
				s.append(" " + operator + " ");
			}
			s.append(cd.getKey());
		}
		d.setCompositionString(s.toString());
		return d;
	}
	
	public static CohortDefinition getCompositionCohort(String operator, CohortDefinition... definitions) {
		if (definitions.length == 1) {
			return definitions[0];
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		int i = 1;
		for (CohortDefinition cd : definitions) {
			if (cd != null) {
				d.addSearch(""+i, cd, null);
				if (s.length() > 0) {
					s.append(" " + operator + " ");
				}
				s.append(i++);
			}
		}
		d.setCompositionString(s.toString());
		return d;
	}
	
	public static CohortDefinition minus(CohortDefinition base, CohortDefinition... toSubtract) {
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		d.addSearch("base", base, null);
		StringBuilder s = new StringBuilder("base AND NOT (");
		int i = 1;
		for (CohortDefinition cd : toSubtract) {
			d.addSearch(""+i, cd, null);
			if (i > 1) {
				s.append(" OR ");
			}
			s.append(i++);
		}
		s.append(")");
		d.setCompositionString(s.toString());
		return d;
	}
	
	/**
	 * @return a new EvaluationContext with the passed parameters and a number of useful additional parameters
	 */
	public static EvaluationContext constructContext(Map<String, Object> parameters) {
		EvaluationContext context = new EvaluationContext();
		
		if (parameters != null) {
			for (Map.Entry<String, Object> e : parameters.entrySet()) {
				context.addParameterValue(e.getKey(), e.getValue());
				// For any metadata parameters, add the metadata name so the renderer has access to it
				if (e.getValue() != null && e.getValue() instanceof OpenmrsMetadata) {
					context.addParameterValue(e.getKey() + "Name", ((OpenmrsMetadata)e.getValue()).getName());
				}
			}
		}
		
		context.addParameterValue("generationDate", new Date());
		context.addParameterValue("generatedBy", Context.getAuthenticatedUser().getPersonName().getFullName());
		return context;
	}
	
	/**
	 * Looks up a resource on the class path, and returns a RenderingMode based on it
	 * @throws UnsupportedEncodingException 
	 */
	public static RenderingMode renderingModeFromResource(String label, String resourceName) {
		InputStreamReader reader;
		
        try {
	        reader = new InputStreamReader(OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName), "UTF-8");    
        }
        catch (UnsupportedEncodingException e) {
	        throw new MdrtbAPIException("Error reading template from stream", e);
        }
        
		if (reader != null) {
			final ReportDesign design = new ReportDesign();
			ReportDesignResource resource = new ReportDesignResource();
			resource.setName("template");
			String extension = resourceName.substring(resourceName.lastIndexOf("."));
			resource.setExtension(extension);
			String contentType = "text/plain";
			for (ContentType type : ContentType.values()) {
				if (type.getExtension().equals(extension)) {
					contentType = type.getContentType();
				}
			}
			resource.setContentType(contentType);
			ReportRenderer renderer = null;
			try {
				resource.setContents(IOUtils.toByteArray(reader, "UTF-8"));
			}
			catch (Exception e) {
				throw new RuntimeException("Error reading template from stream", e);
			}
			
			design.getResources().add(resource);
			if ("xls".equals(extension)) {
				renderer = new ExcelTemplateRenderer() {
					public ReportDesign getDesign(String argument) {
						return design;
					}
				};
			}
			else {
				renderer = new TextTemplateRenderer() {
					public ReportDesign getDesign(String argument) {
						return design;
					}
				};
			}
			return new RenderingMode(renderer, label, extension, null);
		}
		return null;
	}
	
	public static Map<String, Date> getPeriodDates(Integer year, Integer quarter, Integer month) {
		
		// Validate input and construct start and end months
		int startMonth = 1;
		int endMonth = 12;
		
		
		// if the year is null, we don't have start and end dates, want to query from the beginning of time until today
		if (year == null && month == null && quarter == null) {
			Map<String, Date> periodDates = new HashMap<String, Date>();;
			periodDates.put("startDate", null);
			periodDates.put("endDate", new Date());
			
			return periodDates;
		}
		
		if (year == null || year < 1900 || year > 2100) {
			throw new IllegalArgumentException("Please enter a valid year");
		}
		
		if (quarter != null) {
			if (quarter < 1 || quarter > 4) {
				throw new IllegalArgumentException("Please enter a valid quarter (1-4)");
			}
			if (month != null) {
				throw new IllegalArgumentException("Please enter either a quarter or a month");
			}
			endMonth = quarter*3;
			startMonth = endMonth-2;
		}
		if (month != null) {
			if (month < 1 || month > 12) {
				throw new IllegalArgumentException("Please enter a valid month (1-12)");
			}
			startMonth = month;
			endMonth = month;
		}
		
		Map<String, Date> periodDates = new HashMap<String, Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, startMonth-1);
		c.set(Calendar.DATE, 1);
		periodDates.put("startDate", c.getTime());
		c.set(Calendar.MONTH, endMonth-1);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		periodDates.put("endDate", c.getTime());
		
		return periodDates;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getMdrtbOutcomesFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();
		
		Concept workflowConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
		
		CohortDefinition cured =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED), startDate, endDate);
		
		CohortDefinition complete =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETE), startDate, endDate);
		
		CohortDefinition failed =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILED), startDate, endDate);
	
		CohortDefinition defaulted =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULTED), startDate, endDate);
		
		CohortDefinition died =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED), startDate, endDate);
		
		CohortDefinition transferred =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_OUT), startDate, endDate);
		
		CohortDefinition stillEnrolled =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
	
		map.put("Cured", cured);
		map.put("TreatmentCompleted", complete);
		map.put("Failed", failed);
		map.put("Defaulted", defaulted);
		map.put("Died", died);
		map.put("TransferredOut", transferred);
		map.put("StillEnrolled", stillEnrolled);
		
		return map;
	}

	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getMdrtbPreviousDrugUseFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();

		Concept workflowConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
		
		CohortDefinition newPatient = Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW), startDate, endDate);
		
		CohortDefinition previousFirstLine = Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY), startDate, endDate);
		
		CohortDefinition previousSecondLine = Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS), startDate, endDate);
		
		CohortDefinition unknown = Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
		
		map.put("New", newPatient);
		map.put("PreviousFirstLine", previousFirstLine);
		map.put("PreviousSecondLine", previousSecondLine);
		map.put("Unknown", unknown);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getMdrtbPreviousTreatmentFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();
		
		Concept workflowConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX);
		
		CohortDefinition newPatient =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW), startDate, endDate);
		
		CohortDefinition relapse =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE), startDate, endDate);
		
		CohortDefinition afterDefault =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULTED), startDate, endDate);
	
		CohortDefinition failureCatI =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT), startDate, endDate);
		
		CohortDefinition failureCatII =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT), startDate, endDate);
		
		CohortDefinition transferred =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TRANSFER), startDate, endDate);
		
		CohortDefinition other =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER), startDate, endDate);
		
		CohortDefinition unknown =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
	
		map.put("New", newPatient);
		map.put("Relapse", relapse);
		map.put("AfterDefault", afterDefault);
		map.put("AfterFailureCategoryI", failureCatI);
		map.put("AfterFailureCategoryII", failureCatII);
		map.put("TransferredIn", transferred);
		map.put("Other", other);
		map.put("Unknown", unknown);
		
		return map;
	}
	
	/**
	 * Returns a map of patients ids to the list of active mdrtb patient programs for that patient during the given date range
	 */
	public static Map<Integer,List<MdrtbPatientProgram>> getMdrtbPatientProgramsInDateRangeMap(Date startDate, Date endDate) {
		
		Map<Integer,List<MdrtbPatientProgram>> mdrtbPatientProgramsMap = new HashMap<Integer,List<MdrtbPatientProgram>>();
		
		// get all the mdrtb patient programs
		for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getAllMdrtbPatientProgramsInDateRange(startDate, endDate)) {
			Integer patientId = program.getPatient().getId();
			
			// create a new entry for this patient if we don't already have it
			if (!mdrtbPatientProgramsMap.containsKey(patientId)) {
				mdrtbPatientProgramsMap.put(patientId, new ArrayList<MdrtbPatientProgram>());
			}
			
			// add the program
			mdrtbPatientProgramsMap.get(patientId).add(program);
		}
		
		return mdrtbPatientProgramsMap;
	}
}