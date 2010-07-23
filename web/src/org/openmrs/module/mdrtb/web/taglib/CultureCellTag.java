package org.openmrs.module.mdrtb.web.taglib;

import org.openmrs.module.mdrtb.specimen.Culture;


public class CultureCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 6971553873382451091L;

    private Culture culture;

    public int doStartTag() {
    	renderCell(culture, culture.getResult());
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	culture = null;
    	return EVAL_PAGE;
    }


	
    public Culture getCulture() {
    	return culture;
    }


	
    public void setCulture(Culture culture) {
    	this.culture = culture;
    }


}
 
