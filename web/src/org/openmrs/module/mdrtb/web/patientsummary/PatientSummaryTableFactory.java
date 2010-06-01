package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;

public class PatientSummaryTableFactory {
	
	private static Log log = LogFactory.getLog(Patient.class);
	
	public PatientSummaryTableFactory() {
		// generic constructor
	}
	
	public PatientSummaryTable createPatientSummaryTable(Integer patientId, Date startDate, Date endDate) {
		
		// TODO: we need to handle "program enrollment" and other key dates for full columns
		
		// create all the fields hashes which we will use to generate the table
		// TODO: map the concept ids dynamically instead of hard-code!
		
		// define a new table
		PatientSummaryTable table = new PatientSummaryTable();
		
		// create all the fields that the table will use
		createFields(table);
		
		// now create all the hash mappings we are going to use for table creation
		// Map all the Tuberculosis Smear Constructs by month
		Map<Date, Obs> smearHash = getDateToObsMap(patientId, 3053);
		// Map all the Tuberculosis Culture Constructs by month
		Map<Date, Obs> cultureHash = getDateToObsMap(patientId, 3048);
		// Map all the Drug Sensitivity Test Constructs by month
		Map<Date, Obs> dstHash = getDateToObsMap(patientId, 3040);
		
		// now create the records using the hashs we have created
		createRecords(table, patientId, startDate, endDate, smearHash, cultureHash, dstHash);
		
		return table;
	}
	
	private void createFields(PatientSummaryTable table) {
		// first add the date and bacs
		// TODO: change this to retrieve localized names for date, smear and culture
		table.getPatientSummaryTableFields().add(new PatientSummaryTableField("elements.date", "Date"));
		table.getPatientSummaryTableFields().add(new PatientSummaryTableField("elements.smear", "Smear"));
		table.getPatientSummaryTableFields().add(new PatientSummaryTableField("elements.culture", "Culture"));
		
		// now add the dsts
		List<Integer> dstIds = new LinkedList<Integer>();
		initializeDSTs(dstIds);
		for (Integer dstId : dstIds) {
			String dstName = Context.getConceptService().getConcept(dstId).getBestShortName(Context.getLocale()).getName();
			;
			table.getPatientSummaryTableFields().add(
			    new PatientSummaryTableField("elements.dsts.elements." + dstName, dstName));
		}
		
		// now add the potential drugs that can be ordered
		List<Integer> drugIds = new LinkedList<Integer>();
		initializeDrugs(drugIds);
		for (Integer drugId : drugIds) {
			String drugName = Context.getConceptService().getConcept(drugId).getBestShortName(Context.getLocale()).getName();
			;
			table.getPatientSummaryTableFields().add(
			    new PatientSummaryTableField("elements.regimens.elements." + drugName, drugName));
		}
	}
	
	private Map<Date, Obs> getDateToObsMap(int patientId, int conceptId) {
		// first, get all the obs for this patient/concept pair
		// TODO: fix the patient/person id mapping issue??? is it even an issue?
		List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(
		    Context.getPersonService().getPerson(patientId), Context.getConceptService().getConcept(conceptId));
		
		// now create the map of dates to obs
		Map<Date, Obs> map = new HashMap<Date, Obs>();
		
		// IMPORTANT! needs to handle multiple obs within the same month--right now it just overwrite them, okay for prototype, but not for production
		// IMPORTANT! need to figure out exactly what date we want to use here... encounterDateTime? Obs DateTime?
		for (Obs obs : obsList) {
			// put the obs in a hash based on the month and year of the obs
			map.put(resetAllButYearAndMonth(obs.getEncounter().getEncounterDatetime()), obs);
		}
		
		return map;
	}
	
	private void createRecords(PatientSummaryTable table, Integer patientId, Date startDate, Date endDate,
	                           Map<Date, Obs> smearHash, Map<Date, Obs> cultureHash, Map<Date, Obs> dstHash) {
		// now create the actual rows using the hash maps
		
		// if we haven't been given a specific start date, take the first date that we have info for
		if (startDate == null) {
			startDate = calculateStartDate(smearHash.keySet(), cultureHash.keySet(), dstHash.keySet());
		}
		
		// create the calendar that we are going to use
		Calendar cal = Calendar.getInstance();
		cal.setTime(resetAllButYearAndMonth(startDate));
		
		// iterate through the months
		while (cal.getTime().before(endDate)) {
			Date date = cal.getTime();
			
			// TODO: ? break bacs, dsts, and regimens into separate functions for readability purposes?
			
			// create a new row for the table
			PatientSummaryTableRecord record = new PatientSummaryTableRecord();
			
			// the date row	
			record.addElement("date", new PatientSummaryTableDateElement(date));
			
			// pull out the Smear and Culture constructs, if any, for this date
			
			record.addElement("smear", new PatientSummaryTableBacElement(smearHash.get(date))); // most of these will be null
			record.addElement("culture", new PatientSummaryTableBacElement(cultureHash.get(date))); // most of these will be null
			
			// DSTS are a bit more complex, as we need to create a hash of drug types to results
			Obs dstTestConstruct = dstHash.get(date);
			PatientSummaryTableElementGroup dsts = new PatientSummaryTableElementGroup();
			
			if (dstTestConstruct != null) {
				for (Obs obs : dstTestConstruct.getGroupMembers()) {
					// if this obs is a test result construct, we need to add it to our hash
					if (obs.getConcept().getId() == 3025) {
						PatientSummaryTableDSTElement dst = new PatientSummaryTableDSTElement(obs);
						
						// TODO: we need to be able to handle "waiting on results" case
						
						// now loop through all the results to figure out where to hash it
						for (Obs result : obs.getGroupMembers()) {
							
							// TODO: this could be handled more elegantly
							// TODO: ***we need to hash all concentrations for a certain drug type--multiple drug types in result***
							Integer resultConceptId = result.getConcept().getConceptId();
							if (resultConceptId == 2474 || resultConceptId == 3017 || resultConceptId == 1441) {
								String dstName = result.getValueCoded().getBestShortName(Context.getLocale()).getName();
								// put it in the hash under the drug name
								dsts.addElement(dstName, dst);
								//addPatientSummaryTableColumnIfNeeded(dstName,table); // create a new dst column if we haven't encountered this dst before
								break; // once we've identified the drug we are done... a single result construct shouldn't have more than one drug!
							}
						}
					}
				}
			}
			
			// set the DST hash
			record.addElement("dsts", dsts);
			
			// now create the regimen information for a certain date
			PatientSummaryTableElementGroup regimens = new PatientSummaryTableElementGroup();
			
			// get the patient's regimen history
			RegimenHistory regimenHistory = RegimenUtils
			        .getRegimenHistory(Context.getPatientService().getPatient(patientId));
			// get the regimen history on this date
			// TODO: should regimen finder use last day of month instead of first day of month for better accurate?
			Regimen regimen = regimenHistory.getRegimen(date);
			
			if (regimen != null) {
				for (RegimenComponent component : regimen.getComponents()) {
					String drugName = component.getDrug().getConcept().getBestShortName(Context.getLocale()).getName();
					PatientSummaryTableRegimenElement drug = new PatientSummaryTableRegimenElement(component);
					regimens.addElement(drugName, drug);
				}
			}
			
			// add regimens to the row
			record.addElement("regimens", regimens);
			
			// add this row to the object
			table.getPatientSummaryTableRecords().add(record);
			
			// go to the next month
			cal.add(Calendar.MONTH, 1);
		}
	}
	
	/*
	 * Utility Functions
	 */

	// returns the earliest date we have any data from
	private static Date calculateStartDate(Set<Date> smearDateSet, Set<Date> cultureDateSet, Set<Date> dstDateSet) {
		
		List<Date> minDates = new ArrayList<Date>();
		minDates.add(Collections.min(smearDateSet));
		minDates.add(Collections.min(cultureDateSet));
		minDates.add(Collections.min(dstDateSet));
		
		return Collections.min(minDates);
	}
	
	/*
	 * Given a Date, returns a Date which is equal to first day of that month and year at 00:00:00
	 */
	private static Date resetAllButYearAndMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/*
	 * Initialize the DSTs (this of course will be done some other way than hard-coded concept ids in the end)
	 */
	private static void initializeDSTs(List<Integer> dstIds) {
		
		// ugly hack until I figure out where I want to pull the DSTs from
		
		dstIds.add(656);
		dstIds.add(745);
		dstIds.add(438);
		dstIds.add(1417);
		dstIds.add(1411);
		dstIds.add(1414);
		dstIds.add(740);
		dstIds.add(767);
		dstIds.add(5829);
		dstIds.add(755);
		dstIds.add(1406);
		dstIds.add(1412);
		dstIds.add(1413);
		dstIds.add(2459);
		dstIds.add(2460);
		dstIds.add(1419);
		
	}
	
	/*
	 * Initialize the DSTs (this of course will be done some other way than hard-coded concept ids in the end)
	 */
	private static void initializeDrugs(List<Integer> dstIds) {
		
		// ugly hack until I figure out where I want to pull the DSTs from
		
		dstIds.add(656);
		dstIds.add(745);
		dstIds.add(438);
		dstIds.add(1417);
		dstIds.add(1411);
		dstIds.add(1414);
		dstIds.add(740);
		dstIds.add(767);
		dstIds.add(5829);
		dstIds.add(755);
		dstIds.add(1406);
		dstIds.add(1412);
		dstIds.add(1413);
		dstIds.add(2459);
		dstIds.add(2460);
		dstIds.add(1419);
		
	}
	
	/*
	 * Adds a column to PatientSummaryTableColumns if a column with that code doesn't exist
	 */

	/* private static void addPatientSummaryTableColumnIfNeeded(String name, PatientSummaryTable table){
		for (PatientSummaryTableColumn column : table.getPatientSummaryTableColumns()) {
			if (StringUtils.equals(column.getCode(), "dsts." + name))
				return;
		}
		// if we've made it this far we need to add the column
		table.getPatientSummaryTableColumns().add(new PatientSummaryTableColumn("dsts." + name, name));
	} */
}
