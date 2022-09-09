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
package org.openmrs.module.labmodule.reporting.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.MdrtbConstants.TbClassification;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.reporting.definition.AgeAtLabProgramEnrollmentCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.AgeAtLabProgramEnrollmentTJKCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.CompletedLabProgramEnrolledDuringTJKCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabBacBaselineResultTJKCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabBacResultAfterTreatmentStartedCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.labmodule.reporting.definition.LabDstResultCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabFLDTreatmentStartedCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabNoFollowupSmearTJKCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabPatientProgramStateCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabProgramClosedAfterTreatmentStartedCohortDefintion;
import org.openmrs.module.labmodule.reporting.definition.LabProgramLocationCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabProgramLocationCohortTJKDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabSLDTreatmentStartedCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabTJKConvertedInMonthForEnrollmentDuringCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabTJKPatientDistrictCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabTreatmentStartedCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabTxOutcomeExistsCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabTxStartDateExistsCohortDefinition;
import org.openmrs.module.labmodule.reporting.definition.LabTypeOfDiagnosisCohortDefinition;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;

/**
 * Cohort methods
 */
public class Cohorts {
	
	/**
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
	*/
	
	/**
	 * @return the CohortDefinition for the Location
	 */

	public static CohortDefinition getLocationFilter(Location location, Date startDate, Date endDate) {
		if (location != null) {
			LabProgramLocationCohortDefinition cd = new LabProgramLocationCohortDefinition();
			cd.setLocation(location);
			cd.setStartDate(startDate);
			cd.setEndDate(endDate);
			
			
			return cd;
		}
		
		return null;
	}
	
	//Copied from MDR-TB module (Omar)
	public static CohortDefinition getInMdrProgramEverDuring(Date startDate, Date endDate) {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"))));
		cd.setOnOrAfter(startDate);
		cd.setOnOrBefore(endDate);
		return cd;
	}
	
	public static CohortDefinition getHaveDiagnosticTypeDuring(Date startDate, Date endDate, String diagnosisType) {
		LabTypeOfDiagnosisCohortDefinition cd = new LabTypeOfDiagnosisCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		cd.setDiagnosisType(diagnosisType);
		return cd;
	}
	
	public static CohortDefinition getHaveTreatmentOutcome(Date startDate, Date endDate, String outcomeType) {
		LabTxOutcomeExistsCohortDefinition cd = new LabTxOutcomeExistsCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		cd.setOutcomeType(outcomeType);
		return cd;
	}
	
	public static CohortDefinition getHaveDOTSTxStartDate(Date startDate, Date endDate) {
		LabTxStartDateExistsCohortDefinition cd = new LabTxStartDateExistsCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		return cd;
	}
	
	
	public static CohortDefinition getLocationFilterTJK(String location, Date startDate, Date endDate) {
		if (location != null) {
			LabProgramLocationCohortTJKDefinition cd = new LabProgramLocationCohortTJKDefinition();
			cd.setLocation(location);
			cd.setStartDate(startDate);
			cd.setEndDate(endDate);
			
			
			return cd;
			
			
			
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static CohortDefinition getEnteredStateDuringFilter(Integer stateId, Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(Context.getProgramWorkflowService().getState(stateId), startDate, endDate);
	}
		
	public static CohortDefinition getEnteredStateDuringFilter(ProgramWorkflowState state, Date startDate, Date endDate) {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStartedOnOrAfter(startDate);
		cd.setStartedOnOrBefore(endDate);
		cd.setStates(Arrays.asList(state));
		return cd;
	}
	
	public static CohortDefinition getInStateDuringFilter(ProgramWorkflowState state, Date startDate, Date endDate) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setOnOrAfter(startDate);
		cd.setOnOrBefore(endDate);
		cd.setStates(Arrays.asList(state));
		return cd;
	}
	
	public static CohortDefinition getInStateFilter(ProgramWorkflowState state, Date onDate) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setOnDate(onDate);
		cd.setStates(Arrays.asList(state));
		return cd;
	}
	
	public static CohortDefinition getNotInStateDuringFilter(ProgramWorkflowState state, Date startDate, Date endDate) {
		return new InverseCohortDefinition(getInStateDuringFilter(state, startDate, endDate));
	}

	public static CohortDefinition getDotsPatientProgramStateFilter(Concept workflowConcept, List<Concept> stateConcepts, Date startDate, Date endDate) {
		LabPatientProgramStateCohortDefinition cd = new LabPatientProgramStateCohortDefinition();
		cd.setWorkflowConcept(workflowConcept);
		cd.setStateConcepts(stateConcepts);
		cd.setStartDate(startDate);
		cd.setEndDate(endDate);
		return cd;
	}
		
	public static CohortDefinition getDotsPatientProgramStateFilter(Concept workflowConcept, Concept stateConcept, Date startDate, Date endDate) {
		List<Concept> stateConcepts = new ArrayList<Concept>();
		if(stateConcept!=null)
			stateConcepts.add(stateConcept);
		
		return getDotsPatientProgramStateFilter(workflowConcept, stateConcepts, startDate, endDate);
	}
	
	public static CohortDefinition getCuredDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.CURED)), startDate, endDate);
	}
	
	public static CohortDefinition getTreatmentCompletedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_COMPLETE)), startDate, endDate);
	}
	
	public static CohortDefinition getFailedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.FAILED)), startDate, endDate);
	}
	
	public static CohortDefinition getDefaultedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.DEFAULTED)), startDate, endDate);	
	}
	
	public static CohortDefinition getDiedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.DIED)), startDate, endDate);
	}
	
	public static CohortDefinition getTransferredDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_TRANSFERRED_OUT)), startDate, endDate);
	}

	public static CohortDefinition getRelapsedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.RELAPSE)), startDate, endDate);

	}
	
	public static CohortDefinition getPolydrDetectionFilter(Date startDate, Date endDate) {
		LabDstResultCohortDefinition polydrPats = new LabDstResultCohortDefinition();
		polydrPats.setTbClassification(TbClassification.POLY_RESISTANT_TB);
		polydrPats.setMinResultDate(startDate);
		polydrPats.setMaxResultDate(endDate);
		return polydrPats;
	}
		
	
	public static CohortDefinition getMdrDetectionFilter(Date startDate, Date endDate) {
		LabDstResultCohortDefinition mdrPats = new LabDstResultCohortDefinition();
		mdrPats.setTbClassification(TbClassification.MDR_TB);
		mdrPats.setMinResultDate(startDate);
		mdrPats.setMaxResultDate(endDate);
		return mdrPats;
	}
	
	public static CohortDefinition getRRDetectionFilter(Date startDate, Date endDate) {
		LabDstResultCohortDefinition mdrPats = new LabDstResultCohortDefinition();
		mdrPats.setTbClassification(TbClassification.RR_TB);
		mdrPats.setMinResultDate(startDate);
		mdrPats.setMaxResultDate(endDate);
		return mdrPats;
	}
		
	public static CohortDefinition getXdrDetectionFilter(Date startDate, Date endDate) {
		LabDstResultCohortDefinition xdrPats = new LabDstResultCohortDefinition();
		xdrPats.setTbClassification(TbClassification.XDR_TB);
		xdrPats.setMinResultDate(startDate);
		xdrPats.setMaxResultDate(endDate);
		return xdrPats;
	}
	
	public static CohortDefinition getStartedTreatmentFilter(Date startDate, Date endDate) {
		LabTreatmentStartedCohortDefinition startedTreatmentCohort = new LabTreatmentStartedCohortDefinition();
		startedTreatmentCohort.setFromDate(startDate);
		startedTreatmentCohort.setToDate(endDate);
		return startedTreatmentCohort;
	}
	
	public static CohortDefinition getProgramClosedAfterTreatmentStartedFilter(Date startDate, Date endDate, Integer monthsFromTreatmentStart) {
		LabProgramClosedAfterTreatmentStartedCohortDefintion programClosedAfterTreatmentStartedCohort = new LabProgramClosedAfterTreatmentStartedCohortDefintion();
		programClosedAfterTreatmentStartedCohort.setFromDate(startDate);
		programClosedAfterTreatmentStartedCohort.setToDate(endDate);
		programClosedAfterTreatmentStartedCohort.setMonthsFromTreatmentStart(monthsFromTreatmentStart);
		return programClosedAfterTreatmentStartedCohort;
	}
	
	
	
	/*public static CohortDefinition getInDOTSProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition confirmed = new CompositionCohortDefinition();
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		confirmed.addSearch("inMdrProgram", getInDOTSProgramEverDuring(startDate, endDate), null);
		//confirmed.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		confirmed.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		confirmed.setCompositionString("inDOTSProgram AND startedTreatment");
		return confirmed;
	}*/
	
	public static CohortDefinition getInDOTSProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition confirmed = new CompositionCohortDefinition();
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		confirmed.addSearch("inDOTSProgram", getInDOTSProgramEverDuring(startDate, endDate), null);
		//confirmed.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		confirmed.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		confirmed.setCompositionString("inDOTSProgram AND startedTreatment");
		return confirmed;
	}
	
	/*public static CohortDefinition getSuspectedMdrInProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition suspected = new CompositionCohortDefinition();	
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		suspected.addSearch("inMdrProgram", getInDOTSProgramEverDuring(startDate, endDate), null);
		suspected.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		suspected.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		suspected.setCompositionString("inMdrProgram AND (NOT detectedWithMDR) AND startedTreatment");
		return suspected;
	}*/

	public static CohortDefinition getNewlyHospitalizedDuringPeriod(Date startDate, Date endDate) {	
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.HOSPITALIZED)), startDate, endDate);
	}
		
	public static CohortDefinition getEverHospitalizedDuringPeriod(Date startDate, Date endDate) {
		return getInStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.HOSPITALIZED)), startDate, endDate);
	}
	
	public static CohortDefinition getMostRecentlyAmbulatoryByEnd(Date startDate, Date endDate) {
		return getNotInStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.HOSPITALIZED)), startDate, endDate);
	}
	
	// TODO: figure out what obs to look for here--see ticket HATB-358
	public static CohortDefinition getHivPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 186, startDate, endDate, SetComparator.IN, Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE).getId());
	}
	
	// TODO: figure out what obs to look for here--see ticket HATB-358
	public static CohortDefinition getNewlyHivPositive(Date startDate, Date endDate) {
		CohortDefinition byStart = getHivPositiveDuring(null, startDate);
		CohortDefinition byEnd = getHivPositiveDuring(null, endDate);
		return ReportUtil.minus(byEnd, byStart);
	}
	
	public static CohortDefinition getTransferredInDuring(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(TbUtil.getProgramWorkflowState(Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER)), startDate, endDate);
	}
	
	public static CohortDefinition getMostRecentlySmearPositiveByDate(Date effectiveDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST, Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT).getId(), 
			null, effectiveDate, SetComparator.IN, TbUtil.getPositiveResultConceptIds());
	}
	
	public static CohortDefinition getAnySmearPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT).getId(),
			startDate, endDate, SetComparator.IN, TbUtil.getPositiveResultConceptIds());
	}
	
	public static CohortDefinition getMostRecentlySmearNegativeByDate(Date effectiveDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST, Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT).getId(), 
			null, effectiveDate, SetComparator.IN, Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE).getId());
	}
	
	public static CohortDefinition getAllSmearNegativeDuring(Date startDate, Date endDate) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();	
		cd.addSearch("anyNegative", ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT).getId(), 
			startDate, endDate, SetComparator.IN, Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE).getId()), null);
		cd.addSearch("anyPositive", getAnySmearPositiveDuring(startDate, endDate), null);
		cd.setCompositionString("anyNegative AND (NOT anyPositive)");
		return cd;
	}
	
	public static CohortDefinition getAnyCulturePositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT).getId(), 
			startDate, endDate, SetComparator.IN, TbUtil.getPositiveResultConceptIds());
	}
	
	public static CohortDefinition getAnyXpertOrHAINPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT).getId(), 
			startDate, endDate, SetComparator.IN, TbUtil.getPositiveResultConceptIds());
	}

	public static CohortDefinition getAllCultureNegativeDuring(Date startDate, Date endDate) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();	
		cd.addSearch("anyNegative", ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT).getId(),
			startDate, endDate, SetComparator.IN, Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE).getId()), null);
		cd.addSearch("anyPositive", getAnyCulturePositiveDuring(startDate, endDate), null);
		cd.setCompositionString("anyNegative AND (NOT anyPositive)");
		return cd;
	}
	
	public static CohortDefinition getAllPulmonaryEver() {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		
		q.append("and		o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.PULMONARY_TB).getId() + " ");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getAllExtraPulmonaryEver() {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		
		q.append("and		o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.EXTRA_PULMONARY_TB).getId() + " ");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getAllPulmonaryDuring(Date startDate, Date endDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		
		q.append("and		o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.PULMONARY_TB).getId() + " ");
		
		if (startDate != null) {
			q.append("and	o.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getAllExtraPulmonaryDuring(Date startDate, Date endDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		
		q.append("and		o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.EXTRA_PULMONARY_TB).getId() + " ");
		
		if (startDate != null) {
			q.append("and	o.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getFirstCulturePositiveDuring(Date startDate, Date endDate) {	
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o, (select person_id, concept_id, min(obs_datetime) as obs_datetime from obs where concept_id = "  + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT).getId() +  " group by person_id) d ");
		q.append("where		o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT).getId() + " ");
		q.append("and		o.obs_datetime = d.obs_datetime ");
		q.append("and		o.value_coded in (" + convertIntegerSetToString(TbUtil.getPositiveResultConceptIds()) + ") ");
		if (startDate != null) {
			q.append("and	o.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getInDOTSProgramEverDuring(Date startDate, Date endDate) {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		cd.setOnOrAfter(startDate);
		cd.setOnOrBefore(endDate);
		return cd;
	}
	
	public static CohortDefinition getEnrolledInDOTSProgramDuring(Date startDate, Date endDate) {
		ProgramEnrollmentCohortDefinition pd = new ProgramEnrollmentCohortDefinition();
		pd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		pd.setEnrolledOnOrAfter(startDate);
		pd.setEnrolledOnOrBefore(endDate);
		return pd;
	}

	public static CohortDefinition getAllNewSuspects(Date startDate, Date endDate) {
		ProgramEnrollmentCohortDefinition pd = new ProgramEnrollmentCohortDefinition();
		pd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		pd.setEnrolledOnOrAfter(startDate);
		pd.setEnrolledOnOrBefore(endDate);
		return pd;
	}

	public static CohortDefinition getEnrolledInMDRProgramDuring(Date startDate, Date endDate) {
		ProgramEnrollmentCohortDefinition pd = new ProgramEnrollmentCohortDefinition();
		pd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"))));
		pd.setEnrolledOnOrAfter(startDate);
		pd.setEnrolledOnOrBefore(endDate);
		return pd;
	}
	
	public static CohortDefinition getCompletedDOTSProgramsEnrolledDuring(Date startDate, Date endDate) {
		CompletedLabProgramEnrolledDuringTJKCohortDefinition pd = new CompletedLabProgramEnrolledDuringTJKCohortDefinition();
		pd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		pd.setEnrolledOnOrAfter(startDate);
		pd.setEnrolledOnOrBefore(endDate);
		return pd;
	}
	
	public static CohortDefinition getDOTSProgramCompletedDuring(Date startDate, Date endDate) {
		ProgramEnrollmentCohortDefinition pd = new ProgramEnrollmentCohortDefinition();
		pd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		if(startDate!=null)
			pd.setCompletedOnOrAfter(startDate);
		if(endDate!=null)
			pd.setCompletedOnOrBefore(endDate);
		return pd;
	}
	
	public static CohortDefinition getPendingCulturesOnDate(Date effectiveDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT) + " ");
	//	q.append("and       o.voided='0' ");
		q.append("and		o.obs_id not in ");
		q.append("		(select obs_group_id from obs ");
		q.append("		 where concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_RESULT_DATE) +" and value_datetime < '" + DateUtil.formatDate(effectiveDate, "yyyy-MM-dd") + "') ");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getDotsBacResultAfterTreatmentStart(Date startDate, Date endDate, Integer fromTreatmentMonth, Integer toTreatmentMonth, Result overallResult) {
		LabBacResultAfterTreatmentStartedCohortDefinition cd = new LabBacResultAfterTreatmentStartedCohortDefinition();
		cd.setFromDate(startDate);
		cd.setToDate(endDate);		
		cd.setFromTreatmentMonth(fromTreatmentMonth);
		cd.setToTreatmentMonth(toTreatmentMonth);
		cd.setOverallResult(overallResult);
		return cd;
	}
	
	public static CohortDefinition getDotsBacBaselineTJKResult(Date startDate, Date endDate, Integer fromTreatmentMonth, Integer toTreatmentMonth, org.openmrs.module.labmodule.reporting.definition.LabBacBaselineResultTJKCohortDefinition.Result overallResult) {
		LabBacBaselineResultTJKCohortDefinition cd = new LabBacBaselineResultTJKCohortDefinition();
		cd.setEnrolledOnOrAfter(startDate);
		cd.setEnrolledOnOrBefore(endDate);		
		cd.setFromTreatmentMonth(fromTreatmentMonth);
		cd.setToTreatmentMonth(toTreatmentMonth);
		cd.setOverallResult(overallResult);
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		return cd;
	}
	
	public static CohortDefinition getNoFollowupSmears(Date startDate, Date endDate) {
		LabNoFollowupSmearTJKCohortDefinition cd = new LabNoFollowupSmearTJKCohortDefinition();
		cd.setEnrolledOnOrAfter(startDate);
		cd.setEnrolledOnOrBefore(endDate);		
		
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		return cd;
	}
	
	public static CohortDefinition getConvertedInMonthEnrolledDuring(Date startDate, Date endDate, Integer treatmentMonth) {
		LabTJKConvertedInMonthForEnrollmentDuringCohortDefinition cd = new LabTJKConvertedInMonthForEnrollmentDuringCohortDefinition();
		cd.setEnrolledOnOrAfter(startDate);
		cd.setEnrolledOnOrBefore(endDate);		
		cd.setTreatmentMonth(treatmentMonth);
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		return cd;
	}
	
	public static CohortDefinition getAgeAtEnrollmentInDotsProgram(Date startDate, Date endDate, Integer minAge, Integer maxAge) {
		AgeAtLabProgramEnrollmentTJKCohortDefinition cd = new AgeAtLabProgramEnrollmentTJKCohortDefinition();
		cd.setEnrolledOnOrAfter(startDate);
		cd.setEnrolledOnOrBefore(endDate);		
		cd.setMinAge(minAge);
		cd.setMaxAge(maxAge);
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"))));
		return cd;
	}
	
	public static CohortDefinition getAgeAtEnrollmentInMdrtbProgram(Date startDate, Date endDate, Integer minAge, Integer maxAge) {
		AgeAtLabProgramEnrollmentCohortDefinition cd = new AgeAtLabProgramEnrollmentCohortDefinition();
		cd.setEnrolledOnOrAfter(startDate);
		cd.setEnrolledOnOrBefore(endDate);		
		cd.setMinAge(minAge);
		cd.setMaxAge(maxAge);
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"))));
		return cd;
	}
	
	/**
	 * @return the CohortDefinition for the Address and date 
	 */

	public static CohortDefinition getTreatmentStartAndAddressFilterTJK(String rayon, Date fromDate, Date toDate) {
		if (rayon != null) {
			LabTJKPatientDistrictCohortDefinition cd = new LabTJKPatientDistrictCohortDefinition();
			cd.setDistrict(rayon);
			cd.setFromDate(fromDate);
			cd.setToDate(toDate);
			
			return cd;
		}
		
		return null;
	}
	
	public static CohortDefinition getFLDTreatmentStartedFilter(Date fromDate, Date toDate) {
		
			LabFLDTreatmentStartedCohortDefinition cd = new LabFLDTreatmentStartedCohortDefinition();
			
			cd.setFromDate(fromDate);
			cd.setToDate(toDate);
			
			
			
			return cd;
		
	}
	
	public static CohortDefinition getSLDTreatmentStartedFilter(Date fromDate, Date toDate) {
		
		LabSLDTreatmentStartedCohortDefinition cd = new LabSLDTreatmentStartedCohortDefinition();
		
		cd.setFromDate(fromDate);
		cd.setToDate(toDate);
		
		
		return cd;
	
}
	
	
	public static CohortDefinition getPatientsWithDistict(Location l) {
		StringBuilder q = new StringBuilder();
		q.append("select 	person_id ");
		q.append("from		person_address ");
		q.append("where		county_district = '" + l.getCountyDistrict() + "'");
		
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getMalePatients() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setMaleIncluded(true);
		cd.setFemaleIncluded(false);
		cd.setUnknownGenderIncluded(false);
		
		return cd;
	}
	
	public static CohortDefinition getFemalePatients() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setMaleIncluded(false);
		cd.setFemaleIncluded(true);
		cd.setUnknownGenderIncluded(false);
		
		return cd;
	}
	
	/**
	public static CohortDefinition getAnyPreviousTreatmentFilter() {
		CodedObsCohortDefinition newCase = new CodedObsCohortDefinition();
		newCase.setTimeModifier(TimeModifier.ANY);
		newCase.setQuestion(Context.getConceptService().getConcept(6371)); // TODO: Refactor this
		return newCase;
	}
	
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
	
	*/
	
	
	/**
	 * Utility methods
	 */
	private static String convertIntegerSetToString(Integer [] set) {
		StringBuilder result = new StringBuilder();
		for (Integer integer : set) {
			result.append(integer + ",");
		}
		
		// remove the trailing comma
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	 public static String getAllLabPatientTestedDuring(Date startDate, Date endDate, List<Location> locList){
		
		 StringBuilder q = new StringBuilder();
			q.append("select distinct(e.patient_id)  ");
			q.append("from encounter e ");
			if(!locList.isEmpty())
				q.append(" , location l ");
			q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
					
			if (startDate != null) {
				q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
			}
			if (endDate != null) {
				q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
			}
			
			q.append("and e.voided = 0 ");
			
			if(!locList.isEmpty()){
				q.append("and e.location_id = l.location_id and ( ");
				
				for(int i = 0; i<locList.size(); i++){
					
					Location loc = locList.get(i);
					
					if(i != 0)
						q.append(" or ");
					
					q.append(" l.name = '" + loc.getName() + "'");
					
				}
				
				q.append(" ) and l.retired = 0");
			}
			
			return q.toString();
		}
	
	 public static CohortDefinition getAllLabResultDuring(Date startDate, Date endDate){
			StringBuilder q = new StringBuilder();
			q.append("select 	e.patient_id ");
			q.append("from		encounter e ");
			q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
					
			if (startDate != null) {
				q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
			}
			if (endDate != null) {
				q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
			}
			
			return new SqlCohortDefinition(q.toString());
		}
	 public static CohortDefinition getAllNewLabDuring(Date startDate, Date endDate){
			StringBuilder q = new StringBuilder();
			q.append("select 	e.patient_id ");
			q.append("from		encounter e ");
			q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
			q.append("and ");
					
			if (startDate != null) {
				q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
			}
			if (endDate != null) {
				q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
			}
			
			return new SqlCohortDefinition(q.toString());
		}
	 
	 //Omar
	 public static CohortDefinition getAllByTestType(Date startDate, Date endDate, Concept c, boolean viaLabModuleOnly)
	 {
		 	EncounterType labEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
			StringBuilder q = new StringBuilder();
			q.append("Select e.patient_id from encounter e");
			q.append(" inner join obs o on e.patient_id = o.person_id");
			if(viaLabModuleOnly)
				q.append(" 	and e.encounter_type = " + labEncounterType.getId());
			q.append(" and o.concept_id = " + c.getConceptId() );
					
			if (startDate != null) {
				q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
			}
			if (endDate != null) {
				q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
			}
			
			System.out.println(c.getName()+"----->"+q.toString());
			
			return new SqlCohortDefinition(q.toString());		
	 }
	 
	 //Omarby
	 public static CohortDefinition getAllSuspectsByLocation(Date startDate, Date endDate, Location location)
	 {
			StringBuilder q = new StringBuilder();
			q.append("select p.patient_id from patient p");
			q.append("inner join patient_identifier pi on p.patient_id = pi.patient_id and p.voided=0");
			q.append("inner join encounter e on e.patient_id = p.patient_id and e.encounter_type = '"+ Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
			if(location!= null)
				q.append("and e.locationId = " + location.getId());
			if (startDate != null) {
				q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
			}
			if (endDate != null) {
				q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
			}
			q.append("inner join patient_identifier_type pt on pi.identifier_type = pt.patient_identifier_type_id and pt.name = 'Suspect id'");
			
			return new SqlCohortDefinition(q.toString());		
	 }
	 
	 //Omar
	 /***
	  * 
	  * @param startDate
	  * @param endDate
	  * @param suspectType Takes either "new, "repeat" or "all" as possible
	  * @return
	  */
	 public static CohortDefinition getAllSuspectsByInvestigationPurpose(Date startDate, Date endDate, Concept suspectType, boolean viaLabModuleOnly)
	 {
		 	Concept investigationType=Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE);
			StringBuilder q = new StringBuilder();
			q.append("Select distinct e.patient_id ");
			q.append(" from encounter e");
			q.append(" inner join obs o on e.patient_id = o.person_id");
			q.append(" and o.concept_id= " + investigationType.getConceptId());
			q.append(" and o.value_coded = " + suspectType.getConceptId());
			if(viaLabModuleOnly)
				q.append(" and e.encounter_type = '"+ Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
			
			if (startDate != null) {
				q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
			}
			if (endDate != null) {
				q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
			}
			
			return new SqlCohortDefinition(q.toString());		
	 }
	 
	 //Simple Coded Cohort
	 public static CohortDefinition getSimpleCodedCohort(Date startDate, Date endDate, Concept questionConcept, Concept answer)
	 {
		 //TODO: Ignore voided here
		StringBuilder q = new StringBuilder();
		q.append("Select  obs.person_id from obs ");
		q.append(" where obs.concept_id = " + questionConcept.getConceptId());
		q.append(" and value_coded = " + answer.getConceptId());
		q.append("inner join encounter e on e.encounter_id = obs.encounter_id");
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		return new SqlCohortDefinition(q.toString());		
	 }
	 
	 public static CohortDefinition getCodedCohortByParentObs(Date startDate, Date endDate,Concept parent, Concept questionConcept, Concept answer)
	 {
		 //TODO: Ignore voided here
		 StringBuilder q = new StringBuilder();
		 q.append("	select parentObs.person_id ");
		 q.append("	 from obs childObs ");
		 q.append("	inner join ");
		 q.append("	 obs parentObs on childObs.obs_group_id = parentObs.obs_id and parentObs.concept_id = " + parent.getConceptId());
		 q.append("	and childObs.concept_id  = " + questionConcept.getConceptId());
		 if(answer != null)
			 q.append("	and childObs.value_coded  = " + answer.getConceptId());
		 q.append("	inner join encounter e on e.encounter_id = parentObs.encounter_id");
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		return new SqlCohortDefinition(q.toString());		
	 }
	 
	 public static CohortDefinition getCodedCohortByParentObs(Date startDate, Date endDate,Concept parent, Concept questionConcept, Concept... answers)
	 {
		 //TODO: Ignore voided here
		 StringBuilder q = new StringBuilder();
		 q.append("	select parentObs.person_id ");
		 q.append("	 from obs childObs ");
		 q.append("	inner join ");
		 q.append("	 obs parentObs on childObs.obs_group_id = parentObs.obs_id and parentObs.concept_id = " + parent.getConceptId());
		 q.append("	and childObs.concept_id  = " + questionConcept.getConceptId());
		 if(answers.length >0 )
		 {
			 q.append("	and childObs.value_coded  in( "); 
			 for(int i =0; i < answers.length; i ++)
			 {
				 q.append( answers[i].getConceptId());
				 if((i+1) < answers.length)
				 {
					 q.append(",");
				 }
			 }
			 q.append("	)");
		 }
		 q.append("	inner join encounter e on e.encounter_id = parentObs.encounter_id");
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		return new SqlCohortDefinition(q.toString());		
	 }
	 
	 public static CohortDefinition getCodedCohortByParentObs(Date startDate, Date endDate,Concept parent, Concept questionConcept, List<Concept> answers)
	 {
		 //TODO: Ignore voided here
		 StringBuilder q = new StringBuilder();
		 q.append("	select parentObs.person_id ");
		 q.append("	 from obs childObs ");
		 q.append("	inner join ");
		 q.append("	 obs parentObs on childObs.obs_group_id = parentObs.obs_id and parentObs.concept_id = " + parent.getConceptId());
		 q.append("	and childObs.concept_id  = " + questionConcept.getConceptId());
		 if(answers.size() >0 )
		 {
			 q.append("	and childObs.value_coded  in( "); 
			 for(int i =0; i < answers.size(); i ++)
			 {
				 q.append( answers.get(i).getConceptId());
				 if((i+1) < answers.size())
				 {
					 q.append(",");
				 }
			 }
			 q.append("	)");
		 }
		 q.append("	inner join encounter e on e.encounter_id = parentObs.encounter_id");
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		return new SqlCohortDefinition(q.toString());		
	 }
	 
	 public static CohortDefinition getSimpleCodedCohort(Date startDate, Date endDate, Concept questionConcept,Concept... answer)
	 {
		 //TODO: Ignore voided here
		StringBuilder q = new StringBuilder();
		q.append("Select  obs.person_id from obs ");
		q.append(" where obs.concept_id = " + questionConcept.getConceptId());
		q.append(" and value_coded in ("); 
				for(int i =0;i < answer.length; i ++){
					q.append(answer[i].getConceptId());
					if ((i+1)<answer.length) {
						q.append(",");
					}
				}
				q.append(" )");
		if (startDate != null) {
			q.append(" and	obs.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		return new SqlCohortDefinition(q.toString());		
	 }
	 //TB Culture Report
	 public static CohortDefinition getAllSubcultureByAnswer(Date startDate, Date endDate, Concept subcultureConstruct, Concept questionConcept, Concept... answerConcepts)
	 {
		StringBuilder q = new StringBuilder();
		q.append("Select o2.* from obs o1 inner join obs o2 ");
		q.append(" where o2.obs_group_id  = o1.obs_id ");
		q.append(" and o1.concept_id = " + subcultureConstruct.getConceptId());
		q.append(" and o2.concept_id = " + questionConcept.getConceptId());
		if(answerConcepts.length >0)
		{
			q.append(" and o2.value_coded in( " + answerConcepts );
			for(int i =0; i < answerConcepts.length; i ++)
			{
				q.append(answerConcepts[i].getConceptId());
				if ((i+1)<answerConcepts.length) {
					q.append(",");
				}
			}
			q.append(" )");
		}	
		q.append("inner join encounter e on e.encounter_id = o1.encounter_id");
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		return new SqlCohortDefinition(q.toString());		
	 }
	 
	 
	 
	 /////////////////////////////	LAB MODULE TAJIKISTAN /////////////////////
	 
	 //DST1
	 public static CohortDefinition getAnyResistance(Date startDate, Date endDate, Concept drug)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		List<Concept>drugs = new ArrayList<Concept>();
		drugs.add(drug);
		cd.setSpecificDrugs(drugs);
		cd.setTbClassification(TbClassification.ANY_RESISTANCE);
		return cd; 
	 }
	 
	 public static CohortDefinition getDst2Sensitive(Date startDate, Date endDate)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		cd.setSpecificDrugs(TbUtil.getDst2Drugs());
		cd.setTbClassification(TbClassification.ANY_RESISTANCE);
		return cd; 
	 }
	 
	 
	 public static CohortDefinition getDst1FullySensitive(Date startDate, Date endDate)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		cd.setTbClassification(TbClassification.FULLY_SENSITIVE);
		return cd; 
	 }

	 public static CohortDefinition getMonoResistance(Date startDate, Date endDate, Concept monoDrug)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);
		List<Concept>drugs = new ArrayList<Concept>();
		drugs.add(monoDrug);
		cd.setSpecificDrugs(drugs);

		cd.setTbClassification(TbClassification.MONO_RESISTANT_TB);
		return cd; 
	 }
	 
	 public static CohortDefinition getMDRResistance(Date startDate, Date endDate, List<Concept> otherDrugs)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);		
		cd.setSpecificDrugs(otherDrugs);
		cd.setTbClassification(TbClassification.MDR_TB);
		return cd; 
	 }

	 public static CohortDefinition getPolyResistance(Date startDate, Date endDate, List<Concept> drugs)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);		
		cd.setSpecificDrugs(drugs);
		cd.setTbClassification(TbClassification.POLY_RESISTANT_TB);
		return cd; 
	 }
	 
	 public static CohortDefinition getFlqResistance(Date startDate, Date endDate)
	 {
		LabDstResultCohortDefinition cd = new LabDstResultCohortDefinition();
		cd.setMinResultDate(startDate);
		cd.setMaxResultDate(endDate);		
		cd.setSpecificDrugs(TbUtil.getFLQs());
		cd.setTbClassification(TbClassification.ANY_RESISTANCE);
		return cd; 
	 }

	 
}