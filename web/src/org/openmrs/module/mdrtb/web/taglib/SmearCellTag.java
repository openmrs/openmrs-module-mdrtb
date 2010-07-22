package org.openmrs.module.mdrtb.web.taglib;

import org.openmrs.module.mdrtb.specimen.MdrtbSmear;


public class SmearCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 6971553873382451091L;

    private MdrtbSmear smear;

    public int doStartTag() {
    	renderCell(smear, smear.getResult());
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	smear = null;
    	return EVAL_PAGE;
    }
    
    
	public void setSmear(MdrtbSmear smear) {
	    this.smear = smear;
    }

	public MdrtbSmear getSmear() {
	    return smear;
    }
}
