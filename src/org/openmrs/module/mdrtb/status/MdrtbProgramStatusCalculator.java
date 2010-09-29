package org.openmrs.module.mdrtb.status;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;


public class MdrtbProgramStatusCalculator implements StatusCalculator {

	private MdrtbProgramStatusRenderer renderer;
	
	public MdrtbProgramStatusCalculator(MdrtbProgramStatusRenderer renderer) {
		this.setRenderer(renderer);
	}
	
    public Status calculate(PatientProgram program) {

    	// create the status element to return
    	MdrtbProgramStatus status = new MdrtbProgramStatus(program);
    	
    	// first set the most recent mdrtb program associated with this person
    	List<PatientProgram> mdrtbPrograms = Context.getProgramWorkflowService().getPatientPrograms(program.getPatient(), Context.getService(MdrtbService.class).getMdrtbProgram(), null, null, null, null, false);
		Collections.sort(mdrtbPrograms, new PatientProgramComparator());
		
		if (mdrtbPrograms == null || mdrtbPrograms.isEmpty()) {
			StatusFlag flag = new StatusFlag();
			flag.setMessage(renderer.renderMessagePatientNotEnrolledInProgram());  		
			status.addFlag(flag);
			return status;
		}
		
		//PatientProgram program = mdrtbPrograms.get(mdrtbPrograms.size() - 1);
		
    	// set enrollment date
    	StatusItem enrollmentDate = new StatusItem();
    	Date date = program.getDateEnrolled();
    	
    	enrollmentDate.setValue(date);
    	
    	if(date != null) {
    		enrollmentDate.setDisplayString(renderer.renderEnrollmentDateDisplayString(date));  
    	}
    	else {
    		StatusFlag flag = new StatusFlag();
    		flag.setMessage(renderer.renderMessageNoProgramEnrollmentDate());  
    		enrollmentDate.addFlag(flag);
    	}

    	// bogus parameter just test this out
    	if (date.before(new Date())) {
    		StatusFlag flag = new StatusFlag();
    		flag.setMessage("Program enrollment date must be in the future");
    		enrollmentDate.addFlag(flag);
    	}
    	
    	status.addItem("enrollmentDate", enrollmentDate);
    	
    	return status;
    }

    public List<Status> calculate(List<Patient> patients) {
	    // TODO Auto-generated method stub
	    return null;
    }
    
    public List<Status> calculateFlagged(List<Patient> patients) {
    	return null;
    }
    
    public void setRenderer(MdrtbProgramStatusRenderer renderer) {
	    this.renderer = renderer;
    }

	public MdrtbProgramStatusRenderer getRenderer() {
	    return renderer;
    }

}
