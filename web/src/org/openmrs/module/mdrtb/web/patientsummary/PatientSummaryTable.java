package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;


public class PatientSummaryTable {

	private List<PatientSummaryTableRow> patientSummaryTableRows = new LinkedList<PatientSummaryTableRow>();
	
	private List<PatientSummaryTableColumn> patientSummaryTableColumns = new LinkedList<PatientSummaryTableColumn>();

	public PatientSummaryTable() {
		// empty constructor
	}
	
	// TODO == move this all into a factory method
	public PatientSummaryTable(int patientId, Date startDate, Date endDate) {
		
		// TODO: we need to handle "program enrollment" and other key dates for full columns
		
		// create all the column hashes which we will use to generate the table
		
		// get the hashes for all the columns
		// TODO: map the concept ids dynamically instead of hard-code!
		
		// Map all the Tuberculosis Smear Constructs by month
		Map<Date,Obs> smearHash = getDateToObsMap(patientId,3053);
		// Map all the Tuberculosis Culture Constructs by month
		Map<Date,Obs> cultureHash = getDateToObsMap(patientId,3048);
		// Map all the Drug Sensitivity Test Constructs by month
		Map<Date,Obs> dstHash = getDateToObsMap(patientId,3040);
		
		// now create the actual rows using the hash maps
		// first add the known columns
		getPatientSummaryTableColumns().add(new PatientSummaryTableColumn("date"));
		getPatientSummaryTableColumns().add(new PatientSummaryTableColumn("smear"));
		getPatientSummaryTableColumns().add(new PatientSummaryTableColumn("culture"));
		
		// now create the calendar that we are going to use
		Calendar cal = Calendar.getInstance();
		cal.setTime(resetAllButYearAndMonth(startDate));
		
		// iterate through the months
		while(cal.getTime().before(endDate)){
			Date date = cal.getTime();
			
			// create a new row for the table
			PatientSummaryTableRow row = new PatientSummaryTableRow();
			
			// the date row	
			row.setDate(date);
			
			// pull out the Smear and Culture constructs, if any, for this date
			row.setSmear(new PatientSummaryTableBacElement(smearHash.get(date))); // most of these will be null
			row.setCulture(new PatientSummaryTableBacElement(cultureHash.get(date))); // most of these will be null
			
			// DSTS are a bit more complex, as we need to create a hash of drug types to results
			Obs dstTestConstruct = dstHash.get(date);
			Map<String, PatientSummaryTableDSTElement> dsts = new HashMap<String, PatientSummaryTableDSTElement>();
		
			if (dstTestConstruct != null){
				for	(Obs obs : dstTestConstruct.getGroupMembers()) {
					// if this obs is a test result construct, we need to add it to our hash
					if (obs.getConcept().getId() == 3025){
						PatientSummaryTableDSTElement dst = new PatientSummaryTableDSTElement(obs);
					
						// TODO: we need to be able to handle "waiting on results" case
					
						// now loop through all the results to figure out where to hash it
						for (Obs result : obs.getGroupMembers()){
						
							// TODO: this could be handled more elegantly
							// TODO: ***we need to hash all concentrations for a certain drug type--multiple drug types in result***
							Integer resultConceptId = result.getConcept().getConceptId();
							if (resultConceptId == 2472 || resultConceptId == 3017 || resultConceptId == 1441){
								String dstName = result.getValueCoded().getBestName(Context.getLocale()).getName();
								// put it in the hash under the drug name
								dsts.put(dstName, dst);
								addPatientSummaryTableColumnIfNeeded(dstName); // create a new dst column if we haven't encountered this dst before
								break; // once we've identified the drug we are done... a single result construct shouldn't have more than one drug!
							}
						}
					}
				}
			}
			
			
			// set the DST hash
			row.setDsts(dsts);
			
			// add this row to the object
			getPatientSummaryTableRows().add(row);
			
			// go to the next month
			cal.add(Calendar.MONTH,1);
		}
	}
	
	/*;
	 * Utility Functions
	 */
	
	private Map<Date,Obs> getDateToObsMap(int patientId,int conceptId){
		// first, get all the obs for this patient/concept pair
		// TODO: fix the patient/person id mapping issue???
		List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(Context.getPersonService().getPerson(patientId), Context.getConceptService().getConcept(conceptId));
	
		// now create the map of dates to obs
		Map<Date,Obs> map = new HashMap<Date,Obs>();
		
		// IMPORTANT! needs to handle multiple obs within the same month--right now it just overwrite them, okay for prototype, but not for production
		// IMPORTANT! need to figure out exactly what date we want to use here... encounterDateTime? Obs DateTime?
		for (Obs obs : obsList){
			// put the obs in a hash based on the month and year of the obs
			map.put(resetAllButYearAndMonth(obs.getEncounter().getEncounterDatetime()), obs);
		}
		
		return map;
	}
	
	/*
	 * Adds a column to PatientSummaryTableColumns if a column with that code doesn't exist
	 */
	
	private void addPatientSummaryTableColumnIfNeeded(String code){
		for (PatientSummaryTableColumn column : getPatientSummaryTableColumns()) {
			if (StringUtils.equals(column.getCode(), "dsts." + code))
				return;
		}
		// if we've made it this far we need to add the column
		getPatientSummaryTableColumns().add(new PatientSummaryTableColumn("dsts." + code));
	}
	
	/*
	 * Given a Date, returns a Date which is equal to first day of that month and year at 00:00:00
	 */
	private Date resetAllButYearAndMonth(Date date){
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
	 * Getters and Setters
	 */
	
    public List<PatientSummaryTableRow> getPatientSummaryTableRows() {
    	return patientSummaryTableRows;
    }

	
    public void setPatientSummaryTableRows(List<PatientSummaryTableRow> patientSummaryTableRows) {
    	this.patientSummaryTableRows = patientSummaryTableRows;
    }

	public void setPatientSummaryTableColumns(List<PatientSummaryTableColumn> patientSummaryTableColumns) {
	    this.patientSummaryTableColumns = patientSummaryTableColumns;
    }

	public List<PatientSummaryTableColumn> getPatientSummaryTableColumns() {
	    return patientSummaryTableColumns;
    }
}
