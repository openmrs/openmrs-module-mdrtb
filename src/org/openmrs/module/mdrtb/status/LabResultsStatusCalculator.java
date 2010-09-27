package org.openmrs.module.mdrtb.status;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;


public class LabResultsStatusCalculator implements StatusCalculator {

	// TODO: if there are performance issues, we can combine some of the calculation methods, 
	// to avoid iterating over specimens multiple times
	// however, I doubt that adds much overhead, so I opted for cleaner code
	
	private LabResultsStatusRenderer renderer;
	
	public LabResultsStatusCalculator(LabResultsStatusRenderer renderer) {
		this.setRenderer(renderer);
	}
	
    public Status calculate(Patient patient) {
 
    	// create the Status
    	LabResultsStatus status = new LabResultsStatus(patient);
    	
    	
    	// get the specimens for this patient, because these will be used for multiple calculations
    	List<Specimen> specimens = Context.getService(MdrtbService.class).getSpecimens(patient);
    	
    	// TODO: do control and diagnostic here?

    	status.addItem("pendingLabResults", findPendingLabResults(specimens));
    	status.addItem("drugResistanceProfile", calculateResistanceProfile(specimens));
    	
    	// we want to to reverse the order of the specimens here so that first=most recent
    	Collections.reverse(specimens);
    	status.addItem("mostRecentSmear", findFirstCompletedSmear(specimens));
    	status.addItem("mostRecentCulture", findFirstCompletedCulture(specimens));
    	
    	
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
    
    public void setRenderer(LabResultsStatusRenderer renderer) {
	    this.renderer = renderer;
    }

	public LabResultsStatusRenderer getRenderer() {
	    return renderer;
    }
    
	private StatusItem calculateResistanceProfile(List<Specimen> specimens) {
		StatusItem resistanceProfile = new StatusItem();
		
		List<Concept> drugs = new LinkedList<Concept>();
		
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		
		for (Specimen specimen : specimens) {
			for (Dst dst : specimen.getDsts()) {
				for (DstResult result : dst.getResults()) {
					if (resistant.equals(result.getResult())) {
						if (!drugs.contains(result.getDrug())) {
							drugs.add(result.getDrug());
						}
					}
				}
			}
		}
		
		// sort the drugs in the standard order
		List<Concept> drugTypes = Context.getService(MdrtbService.class).getPossibleDrugTypesToDisplay();
		List<Concept> sortedDrugs = new LinkedList<Concept>();
		
		for (Concept drug : drugTypes) {
			if (drugs.contains(drug)) {
				sortedDrugs.add(drug);
			}
		}
		
		resistanceProfile.setValue(sortedDrugs);
		resistanceProfile.setDisplayString(renderer.renderDrugResistanceProfile(sortedDrugs));
		
		return resistanceProfile;
	}
	
	private StatusItem findPendingLabResults(List<Specimen> specimens) {
		StatusItem pendingLabResults = new StatusItem();
		
		List<Test> tests = new LinkedList<Test>();
		
		for (Specimen specimen : specimens) {
			for (Test test : specimen.getTests()) {
				if (test.getStatus() != TestStatus.COMPLETED) {     // TODO: do I need to test if date equals "0" or something like that?
					tests.add(test);
				}
			}
		}
		
		pendingLabResults.setValue(tests);
		pendingLabResults.setDisplayString(renderer.renderPendingLabResults(tests));
		
		return pendingLabResults;
	}
	
    private StatusItem findFirstCompletedSmear(List<Specimen> specimens) {
    	
    	StatusItem mostRecentCompletedSmear = new StatusItem();
    	
    	for (Specimen specimen : specimens) {
    		List<Smear> smears = specimen.getSmears();
    		
    		if (smears != null && !smears.isEmpty()) {
    			Collections.reverse(smears);
    			for (Smear smear : smears) {
    				if (smear.getResult() != null) {
    					mostRecentCompletedSmear.setValue(smear);
    					mostRecentCompletedSmear.setDisplayString(renderer.renderMostRecentSmearDisplayString(smear));
    					
    					// TODO: see if we need to flag this smear
    					
    					return mostRecentCompletedSmear;
    				}
    			}
    		}
    	}
    	
    	// if we've got to here, there is no completed smear for this patient
    	mostRecentCompletedSmear.setValue(null);
    	mostRecentCompletedSmear.setDisplayString(renderer.renderMostRecentSmearDisplayString(null));
    	mostRecentCompletedSmear.addFlag(renderer.createNoSmearsFlag());
    	
    	return mostRecentCompletedSmear;
    }

    private StatusItem findFirstCompletedCulture(List<Specimen> specimens) {
    	
    	StatusItem mostRecentCompletedCulture = new StatusItem();
    	
    	for (Specimen specimen : specimens) {
    		List<Culture> cultures = specimen.getCultures();
    		
    		if (cultures != null && !cultures.isEmpty()) {
    			Collections.reverse(cultures);
    			for (Culture culture : cultures) {
    				if (culture.getResult() != null) {
    					mostRecentCompletedCulture.setValue(culture);
    					mostRecentCompletedCulture.setDisplayString(renderer.renderMostRecentCultureDisplayString(culture));
    					
    					// TODO: see if we need to flag this smear
    					
    					return mostRecentCompletedCulture;
    				}
    			}
    		}
    	}
    	
    	// if we've got to here, there is no completed smear for this patient
    	mostRecentCompletedCulture.setValue(null);
    	mostRecentCompletedCulture.setDisplayString(renderer.renderMostRecentCultureDisplayString(null));
    	mostRecentCompletedCulture.addFlag(renderer.createNoCulturesFlag());
    	
    	return mostRecentCompletedCulture;
    }
}
