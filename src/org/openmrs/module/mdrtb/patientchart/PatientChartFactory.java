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
		
		// get the treatment start date to use as the chart start date
		Date chartStartDate = patient.getTreatmentStartDate();
		
		// get the treatment end date
		Date treatmentEndDate = patient.getTreatmentEndDate();
		
		// if there is no treatment end date, but there was a start date, (i.e, treatment is active) set the treatment end date to today
		if(treatmentEndDate == null && chartStartDate != null) {
			treatmentEndDate = new Date();
		}
		
		// if there is no treatment start date, set the chart start date to the collected date of the first specimen
		if(chartStartDate == null) {
			chartStartDate = specimens.get(0).getDateCollected();
		}
		
		// now set the start date for the 1st record in the chart
		Calendar recordStartDate = Calendar.getInstance();
		recordStartDate.setTime(chartStartDate);
	
		// first, we want to get all specimens collected more than a month the chart start date (for the prior row)
		recordStartDate.add(Calendar.MONTH, -1);  // set the periodStartDate one month back and use it as the endDate parameter to createRecordComponents
		chart.getRecords().put("PRIOR", new PatientChartRecord(createRecordComponents(specimens, regimenHistory, null, recordStartDate)));
		
		// now add all the specimens collected in the month prior to treatment (for the baseline row)
		Calendar recordEndDate = (Calendar) recordStartDate.clone();
		recordEndDate.add(Calendar.MONTH, 1); // create an end date one month after the startDate
		recordEndDate.add(Calendar.DATE, -1); // set the end date back one day, so that the records don't overlap by a day
		chart.getRecords().put("BASELINE", new PatientChartRecord(createRecordComponents(specimens, regimenHistory, recordStartDate, recordEndDate)));
		
		// now go through the add all the other specimens
		recordStartDate.add(Calendar.MONTH, 1);
		recordEndDate.add(Calendar.MONTH, 1);
		Integer iteration = 0;
		// loop until we are out of specimens, or until we've passed the treatment end date, whatever is later
		while(specimens.size() > 0 || (treatmentEndDate != null && (recordEndDate.getTime()).before(treatmentEndDate))) {
			chart.getRecords().put(iteration.toString(), new PatientChartRecord(createRecordComponents(specimens, regimenHistory, recordStartDate, recordEndDate)));
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
	 * Assembles all the components for a record
	 */
	private List<PatientChartRecordComponent> createRecordComponents(List<Specimen> specimens, RegimenHistory regimenHistory, Calendar startDate, Calendar endDate) {
		
		List<PatientChartRecordComponent> components = new LinkedList<PatientChartRecordComponent>();
		
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
				components.add(new PatientChartRecordComponent(specimenToAdd, regimens));
			}
		}
		// if there are no specimens, simply gather all the regimens the patient was on during this time period
		else {
			List<Regimen> regimens = regimenHistory.getRegimensBetweenDates(dateCounter, endDate.getTime());
			components.add(new PatientChartRecordComponent(null, regimens));
		}
		
		return components;
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
	
	private List<Concept> getDrugTypesForChart(PatientChart patientChart) {
		// get all the possible drug types to display--this method also returns them in the order we want to display them
		return Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
	}
}
