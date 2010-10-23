package org.openmrs.module.mdrtb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.service.MdrtbService;


public class CacheResetListener implements GlobalPropertyListener {

	protected final Log log = LogFactory.getLog(getClass());
	

    public boolean supportsPropertyName(String property) {   	
	    return property != null && (property.equals("mdrtb.colorMap") || property.equals("mdrtb.locationToDisplayCodeMap"));
    }
	
	
	public void globalPropertyChanged(GlobalProperty property) {
		if (property.getProperty() != null) {
			if (property.getProperty().equals("mdrtb.colorMap")) {
				Context.getService(MdrtbService.class).resetColorMapCache();
			}
			if (property.getProperty().equals("mdrtb.locationToDisplayCodeMap")) {
				Context.getService(MdrtbService.class).resetLocationToDisplayCodeCache();
			}
		}
	    
    }

    public void globalPropertyDeleted(String property) {
	    throw new MdrtbAPIException("Required global property has been deleted.");
    }

}
