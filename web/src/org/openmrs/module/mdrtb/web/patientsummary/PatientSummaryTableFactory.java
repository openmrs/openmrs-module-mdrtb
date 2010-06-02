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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;
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
		
		// now create the records for the table
		createRecords(table, patientId, startDate, endDate);
		
		return table;
	}
	
	/*
	 * Private methods
	 */

	private void createFields(PatientSummaryTable table) {
		
		// TODO: I can get rid of this if I don't use the "get concept by name" functionality
		MdrtbFactory factory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		// first add the date and bacs
		// TODO: change this to retrieve localized names for date, smear and culture
		table.getPatientSummaryTableFields().add(new PatientSummaryTableField("elements.date", "Date"));
		table.getPatientSummaryTableFields().add(new PatientSummaryTableField("elements.smear", "Smear"));
		table.getPatientSummaryTableFields().add(new PatientSummaryTableField("elements.culture", "Culture"));
		
		// now add the dst fields
		// TODO: want to abstract this in some way
		// TODO: should we handle the idea that this could be a reference to a single concept set
		// TODO: do we have to use the special mdrtb get concept by name method?
		// TODO: should I even bother mapping from name to concept and then back to name?
		
		String drugList = Context.getAdministrationService().getGlobalProperty("mdrtb.DST_drug_list");
		
		for (String drug : drugList.split("\\|")) {
			
			// TODO: can we map an array to two variables?
			String[] entry = drug.split(":");
			String name = entry[0];
			Double concentration = null;
			if (entry.length > 1) {
				// we want to convert this to a double so that the string representation of this concentration 
				// is identical to the string representation that will be return by obs.getValueAsString();
				concentration = Double.valueOf(entry[1]);
			}
			
			// TODO: do I need to use this, or will basic core concept by name work
			Concept concept = MdrtbUtil.getMDRTBConceptByName(name, Context.getLocale(), factory);
			
			if (concept != null) {
				String dstName = concept.getBestShortName(Context.getLocale()).getName();
				
				if (concentration != null && concentration != 0) {
					table.getPatientSummaryTableFields().add(
					    new PatientSummaryTableField("elements.dsts.elements." + dstName
					            + concentration.toString().replace(".", "e"), dstName + "<br/>" + concentration)); // TODO: hacky to include <br> html here, fix!
					
				} else {
					table.getPatientSummaryTableFields().add(
					    new PatientSummaryTableField("elements.dsts.elements." + dstName, dstName));
				}
			}
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
	
	private void createRecords(PatientSummaryTable table, Integer patientId, Date startDate, Date endDate) {
		
		// first all the hash mappings we are going to use for table creation
		// Map all the Tuberculosis Smear Constructs by month
		Map<Date, Obs> smearHash = getDateToObsMap(patientId, 3053);
		// Map all the Tuberculosis Culture Constructs by month
		Map<Date, Obs> cultureHash = getDateToObsMap(patientId, 3048);
		// Map all the Drug Sensitivity Test Constructs by month
		Map<Date, Obs> dstHash = getDateToObsMap(patientId, 3040);
		
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
			
			// create a new record for the table
			PatientSummaryTableRecord record = new PatientSummaryTableRecord();
			
			// add the date field	
			record.addElement("date", new PatientSummaryTableDateElement(date));
			
			// add the the Smear and Culture constructs, if any, for this date
			record.addElement("smear", new PatientSummaryTableBacElement(smearHash.get(date))); // most of these will be null
			record.addElement("culture", new PatientSummaryTableBacElement(cultureHash.get(date))); // most of these will be null
			
			// add the DSTs, if any, for this date
			record.addElement("dsts", createDSTElement(dstHash.get(date)));
			
			// add regimens, if any, for this date
			record.addElement("regimens", createRegimenElement(date, patientId));
			
			// add this row to the object
			table.getPatientSummaryTableRecords().add(record);
			
			// go to the next month
			cal.add(Calendar.MONTH, 1);
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
	
	private PatientSummaryTableElement createDSTElement(Obs dstTestConstruct) {
		// add the DSTs for this date; these are a bit more complex, as we need to create a map of drug types to results
		PatientSummaryTableElementGroup dsts = new PatientSummaryTableElementGroup();
		
		if (dstTestConstruct != null) {
			
			for (Obs obs : dstTestConstruct.getGroupMembers()) {
				// if this obs is a test result construct, we need to add it to our dst map
				if (obs.getConcept().getId() == 3025) {
					String dstName = "";
					String concentration = "";
					
					PatientSummaryTableDSTElement dst = new PatientSummaryTableDSTElement(obs);
					
					// TODO: we need to be able to handle "waiting on results" case
					
					// loop through all the results to figure out which drug this result is for
					for (Obs result : obs.getGroupMembers()) {
						
						// TODO: this could be handled more elegantly
						// search for a concept that holds a drug name, and then retrieve that drug name
						Integer resultConceptId = result.getConcept().getConceptId();
						if (resultConceptId == 2474 || resultConceptId == 3017 || resultConceptId == 1441) {
							dstName = result.getValueCoded().getBestShortName(Context.getLocale()).getName();
						} else if (resultConceptId == 3016) {
							concentration = result.getValueAsString(Context.getLocale());
						}
					}
					// put it in the hash under the drug name and concentration, if available
					if (StringUtils.isNotEmpty(concentration)) {
						dsts.addElement(dstName + concentration.replace(".", "e"), dst);
					} else {
						dsts.addElement(dstName, dst);
					}
				}
			}
		}
		return dsts;
	}
	
	private PatientSummaryTableElement createRegimenElement(Date date, Integer patientId) {
		PatientSummaryTableElementGroup regimens = new PatientSummaryTableElementGroup();
		
		// get the patient's regimen history
		RegimenHistory regimenHistory = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId));
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		
		Regimen regimen = null;
		
		// iterate backwards through the month until we find a regimen
		// (i.e., fetch the most recent regimen, if any, from this month)
		// TODO: time consuming, because this will test every day of the month for a regimen
		// create better util/mdrtb functions to handle this. or perhaps they already exist?
		while (cal.getTime().after(date)) {
			regimen = regimenHistory.getRegimen(cal.getTime());
			if (regimen != null) {
				break;
			}
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
			
		if (regimen != null) {
			for (RegimenComponent component : regimen.getComponents()) {
				String drugName = component.getDrug().getConcept().getBestShortName(Context.getLocale()).getName();
				PatientSummaryTableRegimenElement drug = new PatientSummaryTableRegimenElement(component);
				regimens.addElement(drugName, drug);
			}
		}
		
		// add regimens to the record
		return regimens;
	}
	
	/*
	 * Private Utility Methods
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
