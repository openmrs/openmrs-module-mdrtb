package org.openmrs.module.mdrtb.web.patientsummary;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;


public class PatientSummaryTable {

	private List<PatientSummaryTableRow> patientSummaryTableRows = new LinkedList<PatientSummaryTableRow>();
	
	private PatientSummaryTableHeader patientSummaryTableHeader;

	public PatientSummaryTable() {
		// empty constructor
	}
	
	public PatientSummaryTable(int patientId, Date startDate, Date endDate) {
		
		// first, generator the header row for the table
		// TODO!
		
		// create all the column hashes which we will use to generate the table
		
		// get the hashes for all the columns
		// TODO: pull in the right concept ids!!
		Map<Date,Obs> smearHash = getDateToObsMap(patientId,12345);
		Map<Date,Obs> cultureHash = getDateToObsMap(patientId,12345);
		Map<Date,Obs> astHash = getDateToObsMap(patientId,12345);
		
		// now create the actual rows using the hash maps
		Calendar cal = Calendar.getInstance();
		cal.setTime(resetAllButYearAndMonth(startDate));
		
		// iterate through the months
		while(cal.getTime().before(endDate)){
			Date date = cal.getTime();
			
			PatientSummaryTableRow row = new PatientSummaryTableRow();
			
			// TODO: this will need to formatting using SimpleDateFormatter
			row.setDate(date);
			row.setSmear(new PatientSummaryTableBacElement(smearHash.get(date)));
			row.setCulture(new PatientSummaryTableBacElement(cultureHash.get(date)));
			row.setAst(new PatientSummaryTableDSTElement(astHash.get(date)));
			
			cal.add(Calendar.MONTH,1);
		}
	}
	
	/*
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

	
    public PatientSummaryTableHeader getPatientSummaryTableHeader() {
    	return patientSummaryTableHeader;
    }

	
    public void setPatientSummaryTableHeader(PatientSummaryTableHeader patientSummaryTableHeader) {
    	this.patientSummaryTableHeader = patientSummaryTableHeader;
    }
	
	
}
