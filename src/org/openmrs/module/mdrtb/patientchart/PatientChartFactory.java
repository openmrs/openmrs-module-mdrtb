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
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenUtil;


public class PatientChartFactory {
	
	protected final Log log = LogFactory.getLog(getClass());

	// TODO: should this operate on an MDR-TB patient wrapper?
	public PatientChart createPatientChart(Patient patient) {
		PatientChart chart = new PatientChart();
		
		if (patient == null) {
			log.warn("Can't fetch patient chart, patient is null");
			return null;
		}
		
		// first, fetch all the specimens for this patient
		List<Specimen> specimens = Context.getService(MdrtbService.class).getSpecimens(patient);
		
		// also get the regimen history for the patient
		RegimenHistory regimenHistory = RegimenUtils.getRegimenHistory(patient);
		
		// the getSpecimen method should return the specimens sorted, but just in case it is changed
		Collections.sort(specimens);
		
		// now we group the specimens by day
		// TODO: make this configurable so that we don't always do this?
		SpecimenUtil.groupSpecimensByDay(specimens);
		
		// now fetch the program start date
		Calendar startDate = Calendar.getInstance();
		
		Program mdrtb = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, mdrtb, null, null, null, null, false);
		
		if(programs == null || programs.size() == 0){
			if(specimens.size() > 0) {
				// set some sort of default date?
				// TODO: use collected date of this first specimen for now
				startDate.setTime(specimens.get(0).getDateCollected());
			}
		}
		else {
			// TODO: this is only temporary, not what we want to do long term, doesn't handle patients with more than one; baseline/prior
			// TODO: this should be driven from the a getDateEnrolled method on the MdrtbPatientWrapper?
			startDate.setTime(programs.get(0).getDateEnrolled());
		}
		
		// first, we want to get all specimens collected more than a month before treatment start date
		Calendar endDate = (Calendar) startDate.clone();
		endDate.add(Calendar.MONTH, 1);
		chart.getRecords().put("PRIOR", new PatientChartRecord(createRecordComponents(specimens, regimenHistory, startDate, endDate)));
		
		// now add all the specimens collected in the month prior to treatment
		startDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.MONTH, 1);
		chart.getRecords().put("BASELINE", new PatientChartRecord(createRecordComponents(specimens, regimenHistory, startDate, endDate)));
		
		// now go through the add all the other specimens
		startDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.MONTH, 1);
		Integer iteration = 0;
		while(specimens.size() > 0) {
			chart.getRecords().put(iteration.toString(), new PatientChartRecord(createRecordComponents(specimens, regimenHistory, startDate, endDate)));
			startDate.add(Calendar.MONTH, 1);
			endDate.add(Calendar.MONTH, 1);
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
				
		//log.error("Testing " + startDate.getTime() + "and " + endDate.getTime());
		
		List<PatientChartRecordComponent> components = new LinkedList<PatientChartRecordComponent>();
		
		// get all the specimens to include in this record, i.e. all specimens collected during this time period
		List<Specimen> specimensToAdd = getSpecimensBeforeDate(specimens, endDate);
		
		// now create a new record component for each specimen
		if(specimensToAdd.size() > 0) {
			Date dateCounter = startDate.getTime();
			
			ListIterator<Specimen> i = specimensToAdd.listIterator();
			
			while (i.hasNext()) {
				Specimen specimenToAdd = i.next();
				List<Regimen> regimens;
			
				// fetch the regimens that we want to display in this component
				if(!i.hasNext()) {
					regimens = regimenHistory.getRegimensBetweenDates(dateCounter,endDate.getTime()); 
					//log.error("Regimens between " + dateCounter + " and " + endDate.getTime() + " = " + regimens);
				}
				else{
					regimens = regimenHistory.getRegimensBetweenDates(dateCounter,specimenToAdd.getDateCollected());
					//log.error("Regimens between " + dateCounter + " and " + specimenToAdd.getDateCollected() + " = " + regimens);
					dateCounter = specimenToAdd.getDateCollected();
				}
						
				// add the components
				components.add(new PatientChartRecordComponent(specimenToAdd, regimens));
			}
		}
		// if there are no specimens, simply gather all the regimens the patient was on during this time period
		else {
			List<Regimen> regimens = regimenHistory.getRegimensBetweenDates(startDate.getTime(), endDate.getTime());
			//log.error("Regimens between " + startDate.getTime() + " and " + endDate.getTime() + " = " + regimens);
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
