package org.openmrs.module.mdrtb.web.patientsummary;

import org.openmrs.module.mdrtb.regimen.RegimenComponent;


public class PatientSummaryTableRegimenElement extends PatientSummaryTableElement{

	private RegimenComponent regimenComponent;
	
	public PatientSummaryTableRegimenElement() {
	}
	
	public PatientSummaryTableRegimenElement(RegimenComponent regimenComponent) {
		this.setRegimenComponent(regimenComponent);
    }

	/*
	 * Getters and Setters
	 */
	
	public void setRegimenComponent(RegimenComponent regimenComponent) {
	    this.regimenComponent = regimenComponent;
    }

	public RegimenComponent getRegimenComponent() {
	    return regimenComponent;
    }

}
