package org.openmrs.module.mdrtb.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
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
	// however, for now, I opted for cleaner code
	
	private LabResultsStatusRenderer renderer;
	
	public LabResultsStatusCalculator(LabResultsStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
	@SuppressWarnings("unchecked")
	public Status calculate(PatientProgram program) {
		
		MdrtbPatientProgram mdrtbProgram = new MdrtbPatientProgram(program);
		
		// create the Status
		LabResultsStatus status = new LabResultsStatus(program);
		
		// get the specimens for this patient program, because these will be used for multiple calculations
		List<Specimen> specimens = mdrtbProgram.getSpecimensDuringProgram();
		
		// get the control smear and diagnostic culture
		findDiagnosticSmearAndCulture(specimens, status);
		
		// determine any pending lab results
		findPendingLabResults(specimens, status);
		
		// determine the resistance profile
		StatusItem resistanceProfile = calculateResistanceProfile(specimens);
		status.addItem("drugResistanceProfile", resistanceProfile);
		
		// now use the resistance profile to determine the mdr-tb classification
		status.addItem("tbClassification", calculateTbClassication((List<Concept>) resistanceProfile.getValue()));
		
		// we want to to reverse the order of the specimens here so that first=most recent
		Collections.reverse(specimens);
		findMostRecentSmear(specimens, status);
		findMostRecentCulture(specimens, status);
		
		// calculate whether or not the culture has been converted
		status.addItem("cultureConversion", calculateCultureConversion(specimens));
		
		// figure out the anatomical site, if know
		status.addItem("anatomicalSite", findAnatomicalSite(mdrtbProgram));
		
		
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
		drugs = MdrtbUtil.sortMdrtbDrugs(drugs);
		
		resistanceProfile.setValue(drugs);
		resistanceProfile.setDisplayString(renderer.renderDrugResistanceProfile(drugs));
		
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
		
		// TODO: handle the determine/rendering of classification date
		
		tbClassification.setValue(classification);
		tbClassification.setDisplayString(renderer.renderTbClassification(classification));
		
		return tbClassification;
	}

	// defined as two consecutive negative cultures more than 30 days apart (with no positive smears in the meantime)
	// note that this method expects to work on a specimen list in reverse chronological order
	// TODO: should smears be included in this test?
	private StatusItem calculateCultureConversion(List<Specimen> specimens) {	
	
		// get a set of all concepts that represent positive results
		Set<Concept> positiveResults = MdrtbUtil.getPositiveResultConcepts();
			
		StatusItem cultureConversion = new StatusItem();
		List<Date> negativeCultureDates = new LinkedList<Date>();
	    
		// loop through all the specimens until we hit one with a positive smear or culture
	    for (Specimen specimen : specimens) {
	    	Boolean possibleConversion = calculateCultureConversionHelper(specimen, positiveResults);
	    
	    	if (possibleConversion != null) {
	    		if (!possibleConversion) {
	    			break;
	    		}
	    		else if (possibleConversion){
	    			negativeCultureDates.add(specimen.getDateCollected());
	    		}
	    	}
	    }
	    
	    // there need to be at least two negative cultures for this to be a conversion
	    if (negativeCultureDates.size() > 1) {
	    	
	    	// now compare to make sure that the conversions are at least 30 days apart
	    	Calendar lastNegativeCulture = Calendar.getInstance();
	    	Calendar firstNegativeCulture = Calendar.getInstance();
	    	
	    	lastNegativeCulture.setTime(negativeCultureDates.get(0));
	    	firstNegativeCulture.setTime(negativeCultureDates.get(negativeCultureDates.size() - 1));
	    	
	    	firstNegativeCulture.add(Calendar.DAY_OF_MONTH, 29);
	    	
	    	if (firstNegativeCulture.before(lastNegativeCulture)) {
	    		// we have a successful conversion
	    		cultureConversion.setValue(new Boolean(true));
	    		
	    		// determine what the conversion date should be reported as
	    		Collections.sort(negativeCultureDates, Collections.reverseOrder());
	    		for (Date date : negativeCultureDates) {
	    			if (date.after(firstNegativeCulture.getTime())) {
	    				cultureConversion.setDate(date);
	    			}
	    		}
	    		cultureConversion.setDisplayString(renderer.renderCultureConversion(cultureConversion));
	    		return cultureConversion;
	    	}
	    }
	    
	    // if we've got here, not converted
	    cultureConversion.setValue(new Boolean(false));
	    cultureConversion.setDisplayString(renderer.renderCultureConversion(cultureConversion));
	    
	    return cultureConversion;
    }
	
	// if this specimen has any positive smears/cultures, returns null
	// if not, and it has a negative culture, return the specimen collection date
	private Boolean calculateCultureConversionHelper(Specimen specimen, Set<Concept> positiveResults) {
				
		for (Smear smear : specimen.getSmears()) {
			if (positiveResults.contains(smear.getResult())) {
				return false;
			}
		}
		
		for (Culture culture : specimen.getCultures()) {
			if (positiveResults.contains(culture.getResult())) {
				return false;
			}
			else if (culture.getResult().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE))) {
				return true;
			}
		}
	
		// if we've gotten here, this specimen has no positive results, but no negative culture, so we haven't gotten any useful into from it
		return null;
	}
	
	private StatusItem findAnatomicalSite(MdrtbPatientProgram program) {
		StatusItem anatomicalSite = new StatusItem();
		
		anatomicalSite.setValue(program.getCurrentAnatomicalSiteDuringProrgram());
		anatomicalSite.setDisplayString(renderer.renderAnatomicalSite(anatomicalSite));
		
		return anatomicalSite;
	}
	
	private void findPendingLabResults(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem pendingLabResults = new StatusItem();
		
		List<StatusItem> tests = new LinkedList<StatusItem>();
		
		for (Specimen specimen : specimens) {
			for (Test test : specimen.getTests()) {
				if (test.getStatus() != TestStatus.COMPLETED) { // TODO: do I need to test if date equals "0" or something like that?
					tests.add(new StatusItem(test));
				}
			}
		}
		
		pendingLabResults.setValue(tests);
		renderer.renderPendingLabResults(pendingLabResults, status);
		
		status.addItem("pendingLabResults", pendingLabResults);
	}
	
	// diagnostic smear and culture defined as first smear and culture results from the specimens associated with the program
	private void findDiagnosticSmearAndCulture(List<Specimen> specimens, LabResultsStatus status) {

		Smear smear = findFirstCompletedSmearInList(specimens);
		StatusItem diagnosticSmear = new StatusItem();
		diagnosticSmear.setValue(smear);
		renderer.renderSmear(diagnosticSmear, status);
		
		Culture culture = findFirstCompletedCultureInList(specimens);
		StatusItem diagnosticCulture = new StatusItem();
		diagnosticCulture.setValue(culture);
		renderer.renderCulture(diagnosticCulture, status);
		
		status.addItem("diagnosticSmear", diagnosticSmear);
		status.addItem("diagnosticCulture", diagnosticCulture);
    }
	
    private void findMostRecentSmear(List<Specimen> specimens, LabResultsStatus status) {
    	StatusItem mostRecentCompletedSmear = new StatusItem();
	
		Smear smear = findFirstCompletedSmearInList(specimens);
		mostRecentCompletedSmear.setValue(smear);
		renderer.renderSmear(mostRecentCompletedSmear, status);
		
		status.addItem("mostRecentSmear", mostRecentCompletedSmear);
		
		/**
		if (smear == null) {
			mostRecentCompletedSmear.addFlag(renderer.createNoSmearsFlag());
		}
		*/

    }
	
    private void findMostRecentCulture(List<Specimen> specimens, LabResultsStatus status) {
    	StatusItem mostRecentCompletedCulture = new StatusItem();
    	
		Culture culture = findFirstCompletedCultureInList(specimens);
		mostRecentCompletedCulture.setValue(culture);
		renderer.renderCulture(mostRecentCompletedCulture, status);
		
		status.addItem("mostRecentCulture", mostRecentCompletedCulture);
		
		/**
		if (culture == null) {
			mostRecentCompletedCulture.addFlag(renderer.createNoCulturesFlag());
		}
		*/
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
