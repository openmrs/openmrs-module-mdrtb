package org.openmrs.module.mdrtb.status;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
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
		this.renderer = renderer;
	}
	
	@SuppressWarnings("unchecked")
	public Status calculate(PatientProgram program) {
		
		// create the Status
		LabResultsStatus status = new LabResultsStatus(program);
		
		// get the specimens for this patient program, because these will be used for multiple calculations
		List<Specimen> specimens = StatusUtil.getSpecimensForProgram(program);
		
		// get the control smear and diagnostic culture
		findControlSmearAndDiagnosticCulture(specimens, status);
		
		// determine any pending lab results
		status.addItem("pendingLabResults", findPendingLabResults(specimens));
		
		// determine the resistance profile
		StatusItem resistanceProfile = calculateResistanceProfile(specimens);
		status.addItem("drugResistanceProfile", resistanceProfile);
		
		// now use the resistance profile to determine the mdr-tb classification
		status.addItem("tbClassification", calculateTbClassication((List<Concept>) resistanceProfile.getValue()));
		
		// we want to to reverse the order of the specimens here so that first=most recent
		Collections.reverse(specimens);
		status.addItem("mostRecentSmear", findMostRecentSmear(specimens));
		status.addItem("mostRecentCulture", findMostRecentCulture(specimens));
		
		return status;
		
	}
	
	public void setRenderer(LabResultsStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
	public LabResultsStatusRenderer getRenderer() {
		return renderer;
	}
	
	/**
	 * Utility methods
	 */
	
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
	
	// this calculation is based upon guidelines in the WHO publication "Guidelines for the
	// programmatic management of drug-resistant tuberculosis, Emergency update 2008" (see section 4.2, p.20)
	private StatusItem calculateTbClassication(List<Concept> resistanceProfile) {
		
		MdrtbService mdrtbService = Context.getService(MdrtbService.class);
		
		StatusItem tbClassification = new StatusItem();
		TbClassification classification = null;
		
		// if there are no drugs in the resistance profile, we can't make any classications yet
		if (resistanceProfile.size() > 0) {
			if (resistanceProfile.size() == 1) {
				// if there's only one, it's mono resistance
				classification = TbClassification.MONO_RESISTANT_TB;
			} else {
				// if patient is resistance to 2 or more drugs, patient is at least poly-resistant
				classification = TbClassification.POLY_RESISTANT_TB;
				
				// if they are resistant to isoniazid and rifampicin, at least MDR-TB
				if (resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.ISONIAZID))
				        && resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.RIFAMPICIN))) {
					classification = TbClassification.MDR_TB;
					
					// if resistant to capreomycin, kanamycin, or amikacin, and any one fluorquinolone, the patient is XDR
					if ((resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.CAPREOMYCIN))
					        || resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.KANAMYCIN)) 
					        || resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.AMIKACIN)))
					        && (resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.LEVOFLOXACIN))
					        || resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.MOXIFLOXACIN))
					        || resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.OFLOXACIN)))) {

						classification = TbClassification.XDR_TB;
					}
				}
			}
		}
		
		// TODO: handle the determine/rendering of classication date
		
		tbClassification.setValue(classification);
		tbClassification.setDisplayString(renderer.renderTbClassification(classification));
		
		return tbClassification;
	}
	
	private StatusItem findPendingLabResults(List<Specimen> specimens) {
		StatusItem pendingLabResults = new StatusItem();
		
		List<Test> tests = new LinkedList<Test>();
		
		for (Specimen specimen : specimens) {
			for (Test test : specimen.getTests()) {
				if (test.getStatus() != TestStatus.COMPLETED) { // TODO: do I need to test if date equals "0" or something like that?
					tests.add(test);
				}
			}
		}
		
		pendingLabResults.setValue(tests);
		pendingLabResults.setDisplayString(renderer.renderPendingLabResults(tests));
		
		return pendingLabResults;
	}
	
	// diagnostic smear and culture defined as first smear and culture results from the specimens associated with the program
	private void findControlSmearAndDiagnosticCulture(List<Specimen> specimens, LabResultsStatus status) {

		Smear smear = findFirstCompletedSmearInList(specimens);
		StatusItem controlSmear = new StatusItem();
		controlSmear.setValue(smear);
		controlSmear.setDisplayString(renderer.renderSmear(smear));
		
		Culture culture = findFirstCompletedCultureInList(specimens);
		StatusItem diagnosticCulture = new StatusItem();
		diagnosticCulture.setValue(culture);
		diagnosticCulture.setDisplayString(renderer.renderCulture(culture));
		
		status.addItem("controlSmear", controlSmear);
		status.addItem("diagnosticCulture", diagnosticCulture);
    }
	
    private StatusItem findMostRecentSmear(List<Specimen> specimens) {
    	StatusItem mostRecentCompletedSmear = new StatusItem();
	
		Smear smear = findFirstCompletedSmearInList(specimens);
		mostRecentCompletedSmear.setValue(smear);
		mostRecentCompletedSmear.setDisplayString(renderer.renderSmear(smear));
		
		if (smear == null) {
			mostRecentCompletedSmear.addFlag(renderer.createNoSmearsFlag());
		}
			
		return mostRecentCompletedSmear;
    }
	
    private StatusItem findMostRecentCulture(List<Specimen> specimens) {
    	StatusItem mostRecentCompletedCulture = new StatusItem();
    	
		Culture culture = findFirstCompletedCultureInList(specimens);
		mostRecentCompletedCulture.setValue(culture);
		mostRecentCompletedCulture.setDisplayString(renderer.renderCulture(culture));
		
		if (culture == null) {
			mostRecentCompletedCulture.addFlag(renderer.createNoCulturesFlag());
		}
			
		return mostRecentCompletedCulture;
    }
    
	private Smear findFirstCompletedSmearInList(List<Specimen> specimens) {
		
		for (Specimen specimen : specimens) {
			List<Smear> smears = specimen.getSmears();
			
			if (smears != null && !smears.isEmpty()) {
				Collections.reverse(smears);
				for (Smear smear : smears) {
					if (smear.getResult() != null) {			
						return smear;
					}
				}
			}
		}
		
		// if we've got here, no matching smear
		return null;
	}
	
	private Culture findFirstCompletedCultureInList(List<Specimen> specimens) {
	
		for (Specimen specimen : specimens) {
			List<Culture> cultures = specimen.getCultures();
			
			if (cultures != null && !cultures.isEmpty()) {
				Collections.reverse(cultures);
				for (Culture culture : cultures) {
					if (culture.getResult() != null) {
						return culture;
					}
				}
			}
		}
		
		// if we've got to here, there is no completed culture for this patient
		return null;
	}
}
