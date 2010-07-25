package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;


public class PatientChart {

	// Note: this map should be an ordered (Linked) map because we rely on the order in the implementation
	private Map<String,PatientChartRecord> records;
	
	private List<Concept> drugTypes;
	
	
	public PatientChart() {
		records = new LinkedHashMap<String,PatientChartRecord>();
	}
	

	public void setRecords(Map<String,PatientChartRecord> records) {
	    this.records = records;
    }

	public Map<String,PatientChartRecord> getRecords() {
	    return records;
    }

	public void setDrugTypes(List<Concept> drugTypes) {
	    this.drugTypes = drugTypes;
    }

	public List<Concept> getDrugTypes() {
	    return drugTypes;
    }
}
