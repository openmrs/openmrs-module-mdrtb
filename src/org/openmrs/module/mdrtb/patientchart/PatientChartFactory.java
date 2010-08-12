package org.openmrs.module.mdrtb.patientchart;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.patient.MdrtbPatientWrapper;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenUtil;


public class PatientChartFactory {
	
	protected final Log log = LogFactory.getLog(getClass());

	// TODO: should this operate on an MDR-TB patient wrapper?
	public PatientChart createPatientChart(MdrtbPatientWrapper patient) {
		PatientChart chart = new PatientChart();
		
		if (patient == null) {
			log.warn("Can't fetch patient chart, patient is null");
			return null;
		}
		
		// first, fetch all the specimens for this patient
		List<Specimen> specimens = patient.getSpecimens();
		
		// the getSpecimen method should return the specimens sorted, but just in case it is changed
		Collections.sort(specimens);
		
		// now we group the specimens by day
		// TODO: make this configurable so that we don't always do this?
		SpecimenUtil.groupSpecimensByDay(specimens);
		
		// also get the regimen history for the patient
		RegimenHistory regimenHistory = patient.getRegimenHistory();
		
		// calculate all the state change components we need to display on the chart
		List<RecordComponent> stateChangeRecordComponents = createAllStateChangeRecordComponents(patient);
		
		// get the treatment start date to use as the chart start date
		Date chartStartDate = patient.getTreatmentStartDate();
		
		// get the treatment end date
		Date treatmentEndDate = patient.getTreatmentEndDate();
		
		// if there is no treatment end date, but there was a start date, (i.e, treatment is active) set the treatment end date to today
		if(treatmentEndDate == null && chartStartDate != null) {
			treatmentEndDate = new Date();
		}
		
		Calendar recordStartDate = Calendar.getInstance();
		
		// if there is no treatment start date, set the first record to a month after the collected date of the first specimen
		// (so that the first specimen should show up in the baseline row)
		if(chartStartDate == null) {
			chartStartDate = specimens.get(0).getDateCollected();
			recordStartDate.setTime(chartStartDate);
			recordStartDate.add(Calendar.MONTH, 1);
		}
		else {
			recordStartDate.setTime(chartStartDate);
		}

		// first, we want to get all specimens collected more than a month the chart start date (for the prior row)
		recordStartDate.add(Calendar.MONTH, -1);  // set the periodStartDate one month back and use it as the endDate parameter to createRecordComponents
		Record record = createChartRecord(specimens, stateChangeRecordComponents, regimenHistory, null, recordStartDate);
		chart.getRecords().put("PRIOR", record);
		
		// now add all the specimens collected in the month prior to treatment (for the baseline row)
		Calendar recordEndDate = (Calendar) recordStartDate.clone();
		recordEndDate.add(Calendar.MONTH, 1); // create an end date one month after the startDate
		recordEndDate.add(Calendar.DATE, -1); // set the end date back one day, so that the records don't overlap by a day
		record = createChartRecord(specimens, stateChangeRecordComponents, regimenHistory, recordStartDate, recordEndDate);
		chart.getRecords().put("BASELINE", record);
		
		
		// now go through the add all the other specimens
		recordStartDate.add(Calendar.MONTH, 1);
		recordEndDate.add(Calendar.MONTH, 1);
		Integer iteration = 0;
		// loop until we are out of specimens, or until we've passed the treatment end date, whatever is later
		while(specimens.size() > 0 || (treatmentEndDate != null && (recordEndDate.getTime()).before(treatmentEndDate))) {
			record = createChartRecord(specimens, stateChangeRecordComponents, regimenHistory, recordStartDate, recordEndDate);
			chart.getRecords().put(iteration.toString(), record);
			
			// increment
			recordStartDate.add(Calendar.MONTH, 1);
			recordEndDate.add(Calendar.MONTH, 1);
			iteration++;
		}
		
		// add the possible drug types
		List<Concept> drugTypes = getDrugTypesForChart(chart);
		
		chart.setDrugTypes(drugTypes);
		
		return chart;
	}
	
	 /**
	 * Utility functions
	 */
	
	/**
	 * Creates a record for the patient chart in the specified range
	 */
	private Record createChartRecord(List<Specimen> specimens, List<RecordComponent> stateChangeRecordComponents, RegimenHistory regimenHistory, Calendar recordStartDate, Calendar recordEndDate) {
		// get the specimen components for this period
		List<RecordComponent> components = createSpecimenRecordComponents(specimens, regimenHistory, recordStartDate, recordEndDate);
		
		// add the state change components for this period
		components.addAll(getStateChangeRecordComponentsBeforeDate(stateChangeRecordComponents, recordEndDate));
		
		// sort the components
		Collections.sort(components);
		
		// if there are no components, simply gather all the regimens the patient was on during this time period
		// and put it on a specimen component
		if(components.size() == 0) {
			List<Regimen> regimens = regimenHistory.getRegimensBetweenDates((recordStartDate != null ? recordStartDate.getTime() : null), recordEndDate.getTime());
			components.add(new SpecimenRecordComponent(null, regimens));
		}
		
		// create a new record using those components
		return new Record(components);
	}
	
	
	/**
	 * Assembles all the specimen components for a record
	 */
	private List<RecordComponent> createSpecimenRecordComponents(List<Specimen> specimens, RegimenHistory regimenHistory, Calendar startDate, Calendar endDate) {
		
		List<RecordComponent> components = new LinkedList<RecordComponent>();
		
		// get all the specimens to include in this record, i.e. all specimens collected during this time period
		List<Specimen> specimensToAdd = getSpecimensBeforeDate(specimens, endDate);
		
		Date dateCounter;
		if(startDate != null) {
			dateCounter = startDate.getTime();
		}
		else {
			// if the startdate is null (i.e., this is the "prior" case) set start date to the collection date of the first specimen
			if(specimensToAdd != null && specimensToAdd.size() > 0) {
				dateCounter = specimensToAdd.get(0).getDateCollected();
			}
			else {
				// otherwise, return an empty set
				return components;
			}
		}
		
		// now create a new record component for each specimen
		if(specimensToAdd.size() > 0) {
			ListIterator<Specimen> i = specimensToAdd.listIterator();
			
			while (i.hasNext()) {
				Specimen specimenToAdd = i.next();
				List<Regimen> regimens;
			
				// fetch the regimens that we want to display in this component
				if(!i.hasNext()) {
					regimens = regimenHistory.getRegimensBetweenDates(dateCounter,endDate.getTime()); 
				}
				else{
					regimens = regimenHistory.getRegimensBetweenDates(dateCounter,specimenToAdd.getDateCollected());
					dateCounter = specimenToAdd.getDateCollected();
				}
						
				// add the components
				components.add(new SpecimenRecordComponent(specimenToAdd, regimens));
			}
		}
				
		return components;
	}
	
	/**
	 * Assembles all state change components for the entire chart
	 */
	private List<RecordComponent> createAllStateChangeRecordComponents(MdrtbPatientWrapper patient) {
		List<RecordComponent> stateChangeRecordComponents = new LinkedList<RecordComponent>();
		
		// the only state we are worried about at this point is the treatment start date
		// TODO: localize
		stateChangeRecordComponents.add(new StateChangeRecordComponent(patient.getTreatmentStartDate(), "TREATMENT START DATE"));
		
		Collections.sort(stateChangeRecordComponents);
		
		return stateChangeRecordComponents;
	}
	
	// IMPORTANT: the assumption this method makes is that list is in descending date order
	// also, this method pulls all the components it returns off the list of component passed to it;
	// this method is intended to be use with the getPatientChart API method
	private List<RecordComponent> getStateChangeRecordComponentsBeforeDate(List<RecordComponent> components, Calendar compareDate) {
		List<RecordComponent> results = new LinkedList<RecordComponent>();
		Calendar stateChangeDate = Calendar.getInstance();
		
		while(!components.isEmpty()) {
			stateChangeDate.setTime(components.get(0).getDate());
			if(stateChangeDate.before(compareDate)) {
				results.add(components.get(0));
				components.remove(components.get(0));
			}
			else {
				// we don't need to keep checking since the the dates are in order
				break;
			}
		}
		
		return results;
	}
	
	// IMPORTANT: the assumption this method makes is that list of specimens are ordered in descending date order
	// also, this method pulls all the specimens it returns off the list of specimens passed to it;
	// this method is intended to be use with the getPatientChart API method
	private List<Specimen> getSpecimensBeforeDate(List<Specimen> specimens, Calendar compareDate) {
		List<Specimen> results = new LinkedList<Specimen>();
		Calendar specimenDate = Calendar.getInstance();
		
		while(!specimens.isEmpty()) {
			specimenDate.setTime(specimens.get(0).getDateCollected());
			if(specimenDate.before(compareDate)) {
				results.add(specimens.get(0));
				specimens.remove(specimens.get(0));
			}
			else {
				// we don't need to keep checking since the the dates are in order
				break;
			}
		}
		
		return results;
	}
	
	/**
	 * Retrieves the drug types to display in the chart from global property
	 */
	private List<Concept> getDrugTypesForChart(PatientChart patientChart) {
		// get all the possible drug types to display--this method also returns them in the order we want to display them
		return Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
	}
}
