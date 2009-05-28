package org.openmrs.module.mdrtb.extension.html;

import org.openmrs.module.Extension;

public class MdrtbPatientDashboardTabExt extends Extension {
    
    
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }
    
    public String getRequiredPrivilege() {
        return "";
    }

    public String getPortletUrl() {
        return "mdrtbDashboardPortlet";
    }

    public String getTabId() {
        return "mdrtb";
    }
 
    public String getTabName() {
        return "mdrtb.title";
    }
   
    
}
