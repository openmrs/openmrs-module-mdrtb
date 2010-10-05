package org.openmrs.module.mdrtb.status;

import org.openmrs.Encounter;


public interface VisitStatusRenderer {

	public String renderVisit(Encounter encounter);

}
