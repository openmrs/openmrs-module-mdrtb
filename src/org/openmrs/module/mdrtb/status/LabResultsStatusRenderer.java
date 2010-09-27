package org.openmrs.module.mdrtb.status;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Test;


public interface LabResultsStatusRenderer {

	public String renderMostRecentSmearDisplayString(Smear smear);

	public String renderMostRecentCultureDisplayString(Culture culture);

	public String renderPendingLabResults(List<Test> tests);

	public String renderDrugResistanceProfile(List<Concept> drugs);
	
	public StatusFlag createNoSmearsFlag();

	public StatusFlag createNoCulturesFlag();

}
