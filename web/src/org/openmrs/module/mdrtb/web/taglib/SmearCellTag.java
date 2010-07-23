package org.openmrs.module.mdrtb.web.taglib;

import org.openmrs.module.mdrtb.specimen.Smear;


public class SmearCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 6971553873382451091L;

    private Smear smear;

    public int doStartTag() {
    	renderCell(smear, smear.getResult());
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	smear = null;
    	return EVAL_PAGE;
    }
    
    
	public void setSmear(Smear smear) {
	    this.smear = smear;
    }

	public Smear getSmear() {
	    return smear;
    }
}
