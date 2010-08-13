package org.openmrs.module.mdrtb.web.controller.patientchart;

import java.util.List;

import org.openmrs.module.mdrtb.specimen.Bacteriology;


public class SmearCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 6971553873382451091L;

    private List<Bacteriology> smears;

    public int doStartTag() {
    	renderCell(smears);
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	smears = null;
    	return EVAL_PAGE;
    }
    
    
	public void setSmears(List<Bacteriology> smears) {
	    this.smears = smears;
    }

	public List<Bacteriology> getSmears() {
	    return smears;
    }
}
