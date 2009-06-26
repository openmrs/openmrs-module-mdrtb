package org.openmrs.module.mdrtb.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.scheduler.tasks.AbstractTask;

public class MdrtbCultureConversionCheck extends AbstractTask {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    public void execute(){
        Context.openSession();
        if (!Context.isAuthenticated())  
            authenticate();
  
        MdrtbFactory mu = MdrtbFactory.getInstance();
        mu.createCultureConversionsAllPatients();
        Context.closeSession();
    }
   
}
