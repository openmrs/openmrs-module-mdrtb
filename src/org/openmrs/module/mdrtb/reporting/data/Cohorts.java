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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.definition.DstResultCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbPatientProgramStateCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbProgramLocationCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbTreatmentStartedCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;

/**
 * Cohort methods
 */
public class Cohorts {
	
	public static Map<String, Integer> getDrugIds() {
		Map<String, Integer> drugIds = new HashMap<String, Integer>();
		drugIds.put("H", 656);
		drugIds.put("R", 767);
		drugIds.put("E", 745);
		drugIds.put("Z", 5829);
		drugIds.put("S", 438);
		drugIds.put("KANA", 1417);
		drugIds.put("ETHIO", 1414);
		drugIds.put("PAS", 1419);
		drugIds.put("CIPROX", 740);
		return drugIds;
	}
	
	/**
	 * @return the CohortDefinition for the Location
	 */
	
	
	public static CohortDefinition getLocationFilter(Location location, Date startDate, Date endDate) {
		if (location != null) {
			MdrtbProgramLocationCohortDefinition cd = new MdrtbProgramLocationCohortDefinition();
			cd.setLocation(location);
			cd.setStartDate(startDate);
			cd.setEndDate(endDate);
			
			return cd;
		}
		
		return null;
	}

	@SuppressWarnings("deprecation")
	public static CohortDefinition getStartedInStateDuring(Integer stateId, Date startDate, Date endDate) {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStartedOnOrAfter(startDate);
		cd.setStartedOnOrBefore(endDate);
		cd.setStates(Arrays.asList(Context.getProgramWorkflowService().getState(stateId)));
		return cd;
	}
	
	public static CohortDefinition getInStateFilter(Date onDate, ProgramWorkflow workflow, String stateName) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setOnDate(onDate);
		for (ProgramWorkflowState state : workflow.getStates()) {
			boolean match = state.getName() != null && state.getName().trim().equalsIgnoreCase(stateName);
			if (!match) {
				for (ConceptName cn : state.getConcept().getNames()) {
					match = match || cn.getName().trim().equalsIgnoreCase(stateName);
				}
			}
			if (match) {
				cd.setStates(Arrays.asList(state));
			}
		}
		return cd;
	}
	
	public static CohortDefinition getCuredDuringFilter(Date startDate, Date endDate) {
		CohortDefinition inState = getStartedInStateDuring(1, startDate, endDate);
		CohortDefinition hasObs = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, startDate, endDate, SetComparator.IN, 1746);
		return ReportUtil.getCompositionCohort("OR", inState, hasObs);
	}
	
	public static CohortDefinition getTreatmentCompletedDuringFilter(Date startDate, Date endDate) {
		CohortDefinition inState = getStartedInStateDuring(2, startDate, endDate);
		CohortDefinition hasObs = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, startDate, endDate, SetComparator.IN, 1714);
		return ReportUtil.getCompositionCohort("OR", inState, hasObs);
	}
	
	public static CohortDefinition getFailedDuringFilter(Date startDate, Date endDate) {
		CohortDefinition inState = getStartedInStateDuring(3, startDate, endDate);
		CohortDefinition hasObs = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, startDate, endDate, SetComparator.IN, 843);
		return ReportUtil.getCompositionCohort("OR", inState, hasObs);
	}
	
	public static CohortDefinition getDefaultedDuringFilter(Date startDate, Date endDate) {
		CohortDefinition inState = getStartedInStateDuring(4, startDate, endDate);
		CohortDefinition hasObs = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, startDate, endDate, SetComparator.IN, 1743);
		return ReportUtil.getCompositionCohort("OR", inState, hasObs);
	}
	
	public static CohortDefinition getDiedDuringFilter(Date startDate, Date endDate) {
		CohortDefinition inState = getStartedInStateDuring(5, startDate, endDate);
		CohortDefinition hasObs = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, startDate, endDate, SetComparator.IN, 1742);
		return ReportUtil.getCompositionCohort("OR", inState, hasObs);
	}
	
	public static CohortDefinition getTransferredDuringFilter(Date startDate, Date endDate) {
		CohortDefinition inState = getStartedInStateDuring(6, startDate, endDate);
		CohortDefinition hasObs = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, startDate, endDate, SetComparator.IN, 1744);
		return ReportUtil.getCompositionCohort("OR", inState, hasObs);
	}
	
	public static CohortDefinition getRelapsedDuringFilter(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 1568, startDate, endDate, SetComparator.IN, 6349);
	}
	
	// checked
	public static CohortDefinition getMdrDetectionFilter(Date startDate, Date endDate) {
		DstResultCohortDefinition mdrPats = new DstResultCohortDefinition();
		mdrPats.setIncludeMdr(true);
		mdrPats.setMinResultDate(startDate);
		mdrPats.setMaxResultDate(endDate);
		return mdrPats;
	}
	
	// checked
	public static CohortDefinition getXdrDetectionFilter(Date startDate, Date endDate) {
		DstResultCohortDefinition xdrPats = new DstResultCohortDefinition();
		xdrPats.setIncludeXdr(true);
		xdrPats.setMinResultDate(startDate);
		xdrPats.setMaxResultDate(endDate);
		return xdrPats;
	}
	
	// checked
	public static CohortDefinition getStartedTreatmentFilter(Date startDate, Date endDate) {
		MdrtbTreatmentStartedCohortDefinition startedTreatmentCohort = new MdrtbTreatmentStartedCohortDefinition();
		startedTreatmentCohort.setFromDate(startDate);
		startedTreatmentCohort.setToDate(endDate);
		return startedTreatmentCohort;
	}
	
	// checked
	public static CohortDefinition getConfirmedMdrFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition confirmed = new CompositionCohortDefinition();
		
		// NOTE: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		confirmed.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		confirmed.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		confirmed.setCompositionString("detectedWithMDR AND startedTreatment");
		return confirmed;
	}
	
	// checked
	public static CohortDefinition getSuspectedMdrFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition suspected = new CompositionCohortDefinition();	
		
		// NOTE: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		suspected.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		suspected.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		suspected.setCompositionString("(NOT detectedWithMDR) AND startedTreatment");
		return suspected;
	}
	
	// checked
	public static CohortDefinition getMdrtbPatientProgramStateFilter(Concept workflowConcept, List<Concept> stateConcepts, Date startDate, Date endDate) {
		MdrtbPatientProgramStateCohortDefinition cd = new MdrtbPatientProgramStateCohortDefinition();
		cd.setWorkflowConcept(workflowConcept);
		cd.setStateConcepts(stateConcepts);
		cd.setStartDate(startDate);
		cd.setEndDate(endDate);
		return cd;
	}
	
	// checked
	public static CohortDefinition getMdrtbPatientProgramStateFilter(Concept workflowConcept, Concept stateConcept, Date startDate, Date endDate) {
		List<Concept> stateConcepts = new ArrayList<Concept>();
		stateConcepts.add(stateConcept);
		
		return getMdrtbPatientProgramStateFilter(workflowConcept, stateConcepts, startDate, endDate);
	}
	
/**
	public static CohortDefinition getClassificationPreviousDrugUseFilter(Concept stateConcept, Date startDate, Date endDate) {
		MdrtbPatientProgramStateCohortDefinition newCase = new MdrtbPatientProgramStateCohortDefinition();
		newCase.setStartDate(startDate);
		newCase.setEndDate(endDate);
		newCase.setWorkflowConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
		newCase.addStateConcept(stateConcept);
		
		return newCase;
	}
	*/

	public static CohortDefinition getAnyPreviousTreatmentFilter() {
		CodedObsCohortDefinition newCase = new CodedObsCohortDefinition();
		newCase.setTimeModifier(TimeModifier.ANY);
		newCase.setQuestion(Context.getConceptService().getConcept(6371)); // TODO: Refactor this
		return newCase;
	}
	
	/**
	
	public static CohortDefinition getPrevFirstLineCaseFilter(Date startDate, Date endDate) {		
		CompositionCohortDefinition prevFirstLineCase = new CompositionCohortDefinition();	
		prevFirstLineCase.setName("MDR-TB: Previously treated with first-line drugs");
		prevFirstLineCase.addSearch("previouslyTreatedSecondLine", getPrevSecondLineCaseFilter(), null);
		prevFirstLineCase.addSearch("neverTreated", getNewCaseFilter(), null);
		prevFirstLineCase.setCompositionString("NOT (previouslyTreatedSecondLine OR neverTreated)");
		return prevFirstLineCase;
	}
	
	public static CohortDefinition getPrevSecondLineCaseFilter() {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 6371, null, null, SetComparator.IN, 2127);
	}
	
	*/
	
	public static CohortDefinition getPrevRelapseFilter() {
		CohortDefinition prevTx = getAnyPreviousTreatmentFilter();
		CodedObsCohortDefinition relapse = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, null, null, SetComparator.IN, 6349, 1746);
		return ReportUtil.getCompositionCohort("AND", prevTx, relapse);
	}
	
	public static CohortDefinition getPrevDefaultFilter() {
		CohortDefinition prevTx = getAnyPreviousTreatmentFilter();
		CodedObsCohortDefinition def = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, null, null, SetComparator.IN, 1743);
		return ReportUtil.getCompositionCohort("AND", prevTx, def);
	}
	
	public static CohortDefinition getPrevFailureCatIFilter() {	
		CodedObsCohortDefinition cat1 = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 6371, null, null, SetComparator.NOT_IN, 2126);
		CodedObsCohortDefinition failure = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, null, null, SetComparator.IN, 843);
		return ReportUtil.getCompositionCohort("AND", cat1, failure);
	}
	
	public static CohortDefinition getPrevFailureCatIIFilter() {
		CodedObsCohortDefinition cat2 = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 6371, null, null, SetComparator.IN, 2126);
		CodedObsCohortDefinition failure = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 1568, null, null, SetComparator.IN, 843);
		return ReportUtil.getCompositionCohort("AND", cat2, failure);
	}
	
	public static CohortDefinition getNewExtrapulmonaryFilter() {
		return null;
		
		/**
		CohortDefinition newCase = Cohorts.getNewCaseFilter();		
		CodedObsCohortDefinition extra = ReportUtil.getCodedObsCohort(TimeModifier.ANY, 992, null, null, SetComparator.IN, 1547);
		return ReportUtil.getCompositionCohort("AND", newCase, extra);
		*/
	}
	
	public static CohortDefinition getNewlyHospitalizedDuringPeriod(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3289, startDate, endDate, SetComparator.IN, 3389);
	}
	
	public static CohortDefinition getEverHospitalizedDuringPeriod(Date startDate, Date endDate) {
		CohortDefinition newlyHosptitalizedDuring = getNewlyHospitalizedDuringPeriod(startDate, endDate);
		CodedObsCohortDefinition hospitalizedAtStart = ReportUtil.getCodedObsCohort(TimeModifier.LAST, 3289, null, startDate, SetComparator.IN, 3389);
		return ReportUtil.getCompositionCohort("OR", newlyHosptitalizedDuring, hospitalizedAtStart);
	}
	
	public static CohortDefinition getMostRecentlyAmbulatoryByEnd(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST, 3289, null, endDate, SetComparator.IN, 1664);
	}
	
	public static CohortDefinition getMostRecentlySmearPositiveByDate(Date effectiveDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST, 3052, null, effectiveDate, SetComparator.IN, 703, 1408, 1409, 1410, 3047);
	}
	
	public static CohortDefinition getMostRecentlySmearNegativeByDate(Date effectiveDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST, 3052, null, effectiveDate, SetComparator.IN, 664);
	}
	
	public static CohortDefinition getHivPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3753, startDate, endDate, SetComparator.IN, 703);
	}
	
	public static CohortDefinition getNewlyHivPositive(Date startDate, Date endDate) {
		CohortDefinition byStart = getHivPositiveDuring(null, startDate);
		CohortDefinition byEnd = getHivPositiveDuring(null, endDate);
		return ReportUtil.minus(byEnd, byStart);
	}
	
	public static CohortDefinition getTransferredInDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 2536, startDate, endDate, SetComparator.IN, 1065);
	}
	
	public static CohortDefinition getAnySmearPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3052, startDate, endDate, SetComparator.IN, 703, 1408, 1409, 1410, 3047);
	}
	
	public static CohortDefinition getAllSmearNegativeDuring(Date startDate, Date endDate) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();	
		cd.addSearch("anyNegative", ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3052, startDate, endDate, SetComparator.IN, 664), null);
		cd.addSearch("anyPositive", getAnySmearPositiveDuring(startDate, endDate), null);
		cd.setCompositionString("anyNegative AND (NOT anyPositive)");
		return cd;
	}
	
	public static CohortDefinition getAnyCulturePositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3046, startDate, endDate, SetComparator.IN, 703, 1408, 1409, 1410, 3047);
	}
	
	public static CohortDefinition getAllCultureNegativeDuring(Date startDate, Date endDate) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();	
		cd.addSearch("anyNegative", ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3046, startDate, endDate, SetComparator.IN, 664), null);
		cd.addSearch("anyPositive", getAnyCulturePositiveDuring(startDate, endDate), null);
		cd.setCompositionString("anyNegative AND (NOT anyPositive)");
		return cd;
	}
	
	public static CohortDefinition getFirstCulturePositiveDuring(Date startDate, Date endDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o, (select person_id, concept_id, min(obs_datetime) as obs_datetime from obs where concept_id = 3046 group by person_id) d ");
		q.append("where		o.concept_id = 3046 ");
		q.append("and		o.obs_datetime = d.obs_datetime ");
		q.append("and		o.value_coded in (703, 1408, 1409, 1410, 3047) ");
		if (startDate != null) {
			q.append("and	o.obs_datetime >= '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime <= '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getInMdrProgramEverDuring(Date startDate, Date endDate) {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgram(1)));
		cd.setOnOrAfter(startDate);
		cd.setOnOrBefore(endDate);
		return cd;
	}
	
	public static CohortDefinition getPendingCulturesOnDate(Date effectiveDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = 3048 ");
		q.append("and		o.obs_id not in ");
		q.append("		(select obs_group_id from obs ");
		q.append("		 where concept_id = 3045 and value_datetime <= '" + DateUtil.formatDate(effectiveDate, "yyyy-MM-dd") + "') ");
		return new SqlCohortDefinition(q.toString());
	}
}