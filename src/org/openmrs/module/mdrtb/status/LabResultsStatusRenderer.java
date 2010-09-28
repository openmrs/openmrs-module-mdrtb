package org.openmrs.module.mdrtb.status;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Test;

public interface LabResultsStatusRenderer {
	
	public String renderSmear(Smear smear);
	
	public String renderCulture(Culture culture);

	public String renderPendingLabResults(List<Test> tests);
	
	public String renderDrugResistanceProfile(List<Concept> drugs);
	
	public String renderTbClassification(TbClassification classification);
	
	public StatusFlag createNoSmearsFlag();
	
	public StatusFlag createNoCulturesFlag();
	
}
