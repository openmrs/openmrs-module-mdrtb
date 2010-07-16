package org.openmrs.module.mdrtb.web.taglib;

import org.openmrs.module.mdrtb.specimen.MdrtbCulture;


public class CultureCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 6971553873382451091L;

    private MdrtbCulture culture;

    public int doStartTag() {
    	renderCell(culture.getResult());
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	culture = null;
    	return EVAL_PAGE;
    }


	
    public MdrtbCulture getCulture() {
    	return culture;
    }


	
    public void setCulture(MdrtbCulture culture) {
    	this.culture = culture;
    }


}
 
