package org.openmrs.module.mdrtb.status;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.PatientProgram;
import org.openmrs.module.mdrtb.regimen.Regimen;

public class TreatmentStatusCalculator implements StatusCalculator {

	TreatmentStatusRenderer renderer;
	
	public TreatmentStatusCalculator (TreatmentStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
    public Status calculate(PatientProgram program) { 	
	   
    	TreatmentStatus status = new TreatmentStatus(program);
    	
    	List<StatusItem> regimenList = new LinkedList<StatusItem>();
    	
	    for (Regimen regimen : StatusUtil.getRegimensDuringProgram(program)) {
	
	    	StatusItem item = new StatusItem();
	    	
	    	item.setValue(regimen);
	    	item.setDisplayString(renderer.renderRegimen(regimen));
	    	
	    	regimenList.add(item);
	    }
	    
	    // reverse the list so that the most current regimen is first
	    Collections.reverse(regimenList);
    
	    StatusItem regimens = new StatusItem();
	    regimens.setValue(regimenList);
	    status.addItem("regimens", regimens);
	    
	    return status;
    }

    public TreatmentStatusRenderer getRenderer() {
    	return renderer;
    }

	
    public void setRenderer(TreatmentStatusRenderer renderer) {
    	this.renderer = renderer;
    }
}
