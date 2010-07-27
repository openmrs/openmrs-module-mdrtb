package org.openmrs.module.mdrtb.patientchart;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.Specimen;


public class PatientChartFactory {
	
	protected final Log log = LogFactory.getLog(getClass());

	public PatientChart createPatientChart(Patient patient) {
		PatientChart chart = new PatientChart();
		
		if (patient == null) {
			log.warn("Can't fetch patient chart, patient is null");
			return null;
		}
		
		// first, fetch all the specimens for this patient
		List<Specimen> specimens = Context.getService(MdrtbService.class).getSpecimens(patient);
		
		// the getSpecimen method should return the specimens sorted, but just in case it is changed
		Collections.sort(specimens);
		
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
			startDate.setTime(programs.get(0).getDateEnrolled());
		}
		
		// first, we want to get all specimens collected more than a month before treatment start date
		startDate.add(Calendar.MONTH, -1);
		chart.getRecords().put("PRIOR", new PatientChartRecord(getSpecimensBeforeDate(specimens,startDate)));
		
		// now add all the specimens collected in the month prior to treatment
		startDate.add(Calendar.MONTH, 1);
		chart.getRecords().put("BASELINE", new PatientChartRecord(getSpecimensBeforeDate(specimens,startDate)));
		
		// now go through the add all the other specimens
		startDate.add(Calendar.MONTH, 1);
		Integer iteration = 0;
		while(specimens.size() > 0) {
			chart.getRecords().put(iteration.toString(), new PatientChartRecord(getSpecimensBeforeDate(specimens,startDate)));
			startDate.add(Calendar.MONTH, 1);
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
		List<Concept> drugTypes = Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
		
		// in this set we will all the drug types we want to display
		List<Concept> drugTypesToDisplay = new LinkedList<Concept>();
		
		// get all the existing drugs in the specimen
		Map<String,PatientChartRecord> records = patientChart.getRecords();
		for(String key : records.keySet()) {
			for(Specimen specimen : records.get(key).getSpecimens()) {
				for(Dst dst : specimen.getDsts()) {
					for(DstResult dstResult : dst.getResults()) {	
						if(dstResult.getDrug() != null) {
							drugTypesToDisplay.add(dstResult.getDrug());
						}
					}
				}
			}
		}
	
		// now get all the first line drugs, because we want to display them no matter what
		// TODO: move this fetch to the Mdrtb Service?  Something else?
		Concept firstLineTBDrugs = Context.getService(MdrtbService.class).getMdrtbFactory().getConceptFirstLineTBDrugs();
		for(Concept drug : Context.getConceptService().getConceptsByConceptSet(firstLineTBDrugs)) {
			drugTypesToDisplay.add(drug);
		}
		
		
		// only keep the drug types that are in our drug types to display
		// note that we need to return this list (and not just the drugTypesToDisplay) because it holds the drugs in the proper order
		drugTypes.retainAll(drugTypesToDisplay);
		
		return drugTypes;
	}
}
