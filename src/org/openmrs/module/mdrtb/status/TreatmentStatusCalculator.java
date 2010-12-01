package org.openmrs.module.mdrtb.status;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.module.mdrtb.MdrtbConstants.TreatmentState;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.regimen.Regimen;

public class TreatmentStatusCalculator implements StatusCalculator {

	TreatmentStatusRenderer renderer;
	
	public TreatmentStatusCalculator (TreatmentStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
    public Status calculate(MdrtbPatientProgram mdrtbProgram) { 	
    	
    	TreatmentStatus status = new TreatmentStatus(mdrtbProgram);
    	
    	TreatmentState treatmentState = null;
    
    	// get the list of regimens for this patient within the program period
    	List<StatusItem> regimenList = new LinkedList<StatusItem>();
    	
    	List<Regimen> regimensDuringProgram = mdrtbProgram.getMdrtbRegimensDuringProgram();
    	
    	if (regimensDuringProgram != null) {
    		for (Regimen regimen : regimensDuringProgram) {
	 	
    			// test if there is an active regimen that contains drug orders
    			if (regimen.isActive() && regimen.getDrugOrders() != null && regimen.getDrugOrders().size() > 0) {
    				treatmentState = TreatmentState.ON_TREATMENT;
    			}
	    	
    			StatusItem item = new StatusItem();
	    	
    			item.setValue(regimen);
    			item.setDisplayString(renderer.renderRegimen(regimen));
	    	
    			regimenList.add(item);
    		}
    	}
	    
	    // reverse the list so that the most current regimen is first
	    Collections.reverse(regimenList);
    
	    status.addItem("regimens", new StatusItem(regimenList));
	    
	    // if the treatment state has not been set to "On treatment" (i.e., if one of the regimens was active)
	    // set the state to not on treatment ONLY if the program is currently active (whether a treatment is active for
	    // a closed program doesn't make much sense)
	    // TODO: in the future, for closed programs this status should display some sort of summary for that program (never treated, treatment complete, etc)
	    if (treatmentState == null && mdrtbProgram.getActive()) {
	    	treatmentState = TreatmentState.NOT_ON_TREATMENT;
	    }
	 
	    StatusItem state = new StatusItem();
	    state.setValue(treatmentState);
	    state.setDisplayString(renderer.renderTreatmentState(treatmentState));
	    status.addItem("treatmentState", state);
	    
	    return status;
    }

    public TreatmentStatusRenderer getRenderer() {
    	return renderer;
    }

	
    public void setRenderer(TreatmentStatusRenderer renderer) {
    	this.renderer = renderer;
    }
}
