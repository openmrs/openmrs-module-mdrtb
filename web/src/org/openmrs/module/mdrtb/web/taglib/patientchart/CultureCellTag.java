package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.util.List;

import org.openmrs.module.mdrtb.specimen.Bacteriology;


public class CultureCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 6971553873382451091L;

    private List<Bacteriology> cultures;

    public int doStartTag() {
    	renderCell(cultures);
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	cultures = null;
    	return EVAL_PAGE;
    }


	
    public List<Bacteriology> getCultures() {
    	return cultures;
    }


	
    public void setCultures(List<Bacteriology> cultures) {
    	this.cultures = cultures;
    }


}
 
