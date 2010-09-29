package org.openmrs.module.mdrtb.status;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.module.mdrtb.patient.MdrtbPatientWrapper;
import org.openmrs.module.mdrtb.patient.MdrtbPatientWrapperImpl;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;

public class TreatmentStatusCalculator implements StatusCalculator {

	TreatmentStatusRenderer renderer;
	
	public TreatmentStatusCalculator (TreatmentStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
    public Status calculate(PatientProgram program) { 	
	    //MdrtbPatientWrapper mdrtbPatient = new MdrtbPatientWrapperImpl(patient);
	    //RegimenHistory regimenHistory = mdrtbPatient.getRegimenHistory();
    	
    	TreatmentStatus status = new TreatmentStatus(program);
	    
	    // get the current regimen
	   // status.addItem("currentRegimen", findCurrentRegimen(regimenHistory));
	    
	    return status;
    }

	public List<Status> calculate(List<Patient> patients) {
	    // TODO Auto-generated method stub
	    return null;
    }

    public List<Status> calculateFlagged(List<Patient> patients) {
	    // TODO Auto-generated method stub
	    return null;
    }

    public TreatmentStatusRenderer getRenderer() {
    	return renderer;
    }

	
    public void setRenderer(TreatmentStatusRenderer renderer) {
    	this.renderer = renderer;
    }

    /**
     * Utility methods
     */
    
    private StatusItem findCurrentRegimen(RegimenHistory regimenHistory) {
    	StatusItem currentRegimen = new StatusItem();
    	List<Concept> drugs = new LinkedList<Concept>();
    	
	    Regimen regimen = regimenHistory.getRegimen(new Date());
	    
	    for (RegimenComponent component : regimen.getComponents()) {
	    	drugs.add(component.getDrug().getConcept());
	    }
	    
	    currentRegimen.setValue(drugs);
	    
	    return currentRegimen;
    }
}
