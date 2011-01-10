package org.openmrs.module.mdrtb.specimen.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;


public class SpecimenReportingTools {

	protected static final Log log = LogFactory.getLog(SpecimenReportingTools.class);
	
	/**
	 * Returns all specimens collected within the start and end date parameters that have no smear, bacteriology, or dst results associated with them
	 */
	public static Map<Patient,List<Specimen>> getSpecimensWithNoResults(Date startDateCollected, Date endDateCollected) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Concept smearResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		Concept cultureResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		Concept intermediate = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG);
		Concept suceptible = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		// this query finds all the specimens that don't have any test results associated with them, by
		// fetching the set of encounters of the specimen collection type which are not voided and are not in the set of
		// encounters that have a not null obs of one of the following types: smear result, culture result, 
		// resistant to tb drug, intermediate to tb drug, or susceptible to tb drug
		
		// TODO: update this to include ... "x days old"
		
		String sql = "select patient_id, encounter_id from encounter where encounter_type='" + specimenEncounterType.getId() + "' and voided='0' and encounter.encounter_id " +
						(startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
						(endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
						"and encounter.encounter_id not in (select distinct encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " +
						"and obs.voided='0' and value_coded is not null and " +
						"(obs.concept_id='" + smearResult.getId() + "' or obs.concept_id='" + cultureResult.getId() + "' " +
						"or obs.concept_id='" + resistant.getId() + "' or obs.concept_id='" + intermediate.getId() + "' or obs.concept_id='" + suceptible.getId() + "') )";
	
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
	
		return convertQueryResultsToResultsMap(results);
	}
		

	public static Map<Patient,List<Specimen>> getSpecimensWithPositiveCultureButNoDstResults (Date startDateCollected, Date endDateCollected, Integer daysSinceCulture, Integer [] labIds) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// create all the parameters to use in the query
		Concept cultureResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		Concept intermediate = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG);
		Concept suceptible = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG);
		Concept waitingForTestResults = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS);
		
		String positiveResultConceptIds = convertIntegerArrayToString(MdrtbUtil.getPositiveResultConceptIds());
		String labIdString = convertIntegerArrayToString(labIds);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		// TODO: handle case where location == null
		
		// TODO: add a comment
		// TODO: exclude contaminated, because we will pick them up elsewhere?
		
		String sql = "select distinct encounter.patient_id, encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " + 
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " + 
					 (startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
					 (endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
					 "and value_coded in (" + positiveResultConceptIds +") and obs.concept_id='" + cultureResult.getId() + "' " +
					 (labIdString != null ? (" and obs.location_id in (" + labIdString + ") ") : "") + 
					 "and encounter.encounter_id not in " + 
					 "(select distinct encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " +
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " +
					 "and value_coded is not null and value_coded != '" + waitingForTestResults.getId() + "' " +
					 "and obs.concept_id in ('" + resistant.getId() + "','" + intermediate.getId() + "','" + suceptible.getId() + "'));";
		
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
	
		Map<Patient,List<Specimen>> resultMap = new TreeMap<Patient,List<Specimen>>(new PersonByNameComparator());
		
		Calendar dateToCompare = Calendar.getInstance(Context.getLocale());
		dateToCompare.add(Calendar.DAY_OF_YEAR, -daysSinceCulture);
		
		
		triage:
			for (List<Object> result : results) {
				Patient patient = Context.getPatientService().getPatient((Integer) result.get(0));
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen((Integer) result.get(1));
				
				// exclude specimens that have recent culture results
				for (Culture culture : specimen.getCultures()) {
					if (culture.getResultDate() != null && culture.getResultDate().after(dateToCompare.getTime())) {
						continue triage;
					}
				}
				if (!resultMap.containsKey(patient)) {
					resultMap.put(patient, new ArrayList<Specimen>());
				}
				resultMap.get(patient).add(specimen);
			}
		
		return resultMap;
	}
	
	
	public static Map<Patient,List<Specimen>> getSpecimensWithPositiveSmearButNoCultureResults (Date startDateCollected, Date endDateCollected, Integer daysSinceSmear, Integer [] labIds) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// create all the parameters to use in the query
		Concept smearResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		Concept cultureResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
	
		String positiveResultConceptIds = convertIntegerArrayToString(MdrtbUtil.getPositiveResultConceptIds());
		String labIdString = convertIntegerArrayToString(labIds);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		// TODO: handle case where location == null
		
		// TODO: add a comment
		// TODO: exclude contaminated, because we will pick them up elsewhere?
		
		String sql = "select distinct encounter.patient_id, encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " + 
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " + 
					 (startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
					 (endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
					 "and value_coded in (" + positiveResultConceptIds +") and obs.concept_id='" + smearResult.getId() + "' " +
					 (labIdString != null ? (" and obs.location_id in (" + labIdString + ") ") : "") + 
					 "and encounter.encounter_id not in " + 
					 "(select distinct encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " +
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " +
					 "and obs.concept_id='" + cultureResult.getId() + "');";
		
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
	
		Map<Patient,List<Specimen>> resultMap = new TreeMap<Patient,List<Specimen>>(new PersonByNameComparator());
		
		Calendar dateToCompare = Calendar.getInstance(Context.getLocale());
		dateToCompare.add(Calendar.DAY_OF_YEAR, -daysSinceSmear);
		
		triage:
			for (List<Object> result : results) {
				Patient patient = Context.getPatientService().getPatient((Integer) result.get(0));
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen((Integer) result.get(1));
				
				// exclude specimens that have recent culture results
				for (Smear smear : specimen.getSmears()) {
					if (smear.getResultDate() != null && smear.getResultDate().after(dateToCompare.getTime())) {
						continue triage;
					}
				}
				if (!resultMap.containsKey(patient)) {
					resultMap.put(patient, new ArrayList<Specimen>());
				}
				resultMap.get(patient).add(specimen);
			}
		
		return resultMap;
	}
	
	
	/**
	 * Returns all specimens collected within the start and end date parameters that have a test marked as "contaminated"
	 */
	public static Map<Patient,List<Specimen>> getSpecimensWithContaminatedTests(Date startDateCollected, Date endDateCollected, Integer [] labIds) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Concept smearResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		Concept cultureResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		Concept contaminated = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTAMINATED);
		Concept dstContaminated = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONTAMINATED);
		
		String labIdString = convertIntegerArrayToString(labIds);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		String sql = "select distinct encounter.patient_id, encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " + 
						"and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' " + 
						 (startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
						 (endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
						"and obs.voided='0' " + (labIdString != null ? (" and obs.location_id in (" + labIdString + ") ") : "") + 
						"and ( (concept_id in (" + smearResult.getId() + "," + cultureResult.getId()+ ") and value_coded='" + contaminated.getId() + "') " + 
						"or concept_id = '" + dstContaminated.getId() + "');";
		
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
		
		return convertQueryResultsToResultsMap(results);
	}
	
	
	/**
	 * Utility functions
	 */
	
	/**
	 * Takes a SQL query result and converts it into the format we wish to return... a map with patients as the key
	 * and the list of specimens associated with that patient as the value
	 */
	private static Map<Patient,List<Specimen>> convertQueryResultsToResultsMap(List<List<Object>> results) {
	
		Map<Patient,List<Specimen>> resultMap = new TreeMap<Patient,List<Specimen>>(new PersonByNameComparator());
		
		for (List<Object> result : results) {
			Patient patient = Context.getPatientService().getPatient((Integer) result.get(0));
			Specimen specimen = Context.getService(MdrtbService.class).getSpecimen((Integer) result.get(1));
			
			if (!resultMap.containsKey(patient)) {
				resultMap.put(patient, new ArrayList<Specimen>());
			}
			resultMap.get(patient).add(specimen);
		}
		
		return resultMap;
	}
	
	
	private static String convertIntegerArrayToString(Integer [] integers) {
		
		StringBuffer result = new StringBuffer(); 
	
		for (Integer i : integers) {
			result.append(i.toString() + ",");
		}
		result.deleteCharAt(result.length() - 1);  // delete the trailing comma
		
		return result.toString();
	}
	
	
	/**
	 * Returns all specimens collected within the start and end date parameters that have no smear, bacteriology, or dst results associated with them
	 */
	/**
	public static Map<Patient,List<Specimen>> getSpecimensWithNoResults(Date startDateCollected, Date endDateCollected) {
		
		Map<Patient,List<Specimen>> resultList = new HashMap<Patient,List<Specimen>>();
		Concept waitingForTestResults = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS);
		
		// first get all specimens
		List<Specimen> specimens = Context.getService(MdrtbService.class).getSpecimens(null, startDateCollected, endDateCollected);
		
		// loop through all the specimens within the date range
		search:
		for (Specimen specimen : specimens) {
			// see if there are any tests with results
			for (Test test  : specimen.getTests()) {
				if (test instanceof Bacteriology && ((Bacteriology) test).getResult() != null) {
					continue search;
				}
				else if (test instanceof Dst){
					Dst dst = (Dst) test;
					if (dst.getResults() != null) {
						for (DstResult result : dst.getResults()) {
							if (!result.getResult().equals(waitingForTestResults)) {
									continue search;
							}
						}
					}
				}
			}
			
			// if we've gotten here, this is a specimen with no results
			// so we need to push it on the map
			Patient patient = specimen.getPatient();
			if (!resultList.containsKey(patient)) {
				resultList.put(patient, new ArrayList<Specimen>());
			}
			resultList.get(patient).add(specimen);
		}
		
		return resultList;
	} **/

}
