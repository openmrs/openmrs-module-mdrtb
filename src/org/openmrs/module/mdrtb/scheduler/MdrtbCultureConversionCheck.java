package org.openmrs.module.mdrtb.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.scheduler.tasks.AbstractTask;

public class MdrtbCultureConversionCheck extends AbstractTask {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    public void execute(){
        Context.openSession();
        if (!Context.isAuthenticated())  
            authenticate();

        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        MdrtbFactory mu = ms.getMdrtbFactory();
        mu.createCultureConversionsAllPatients();
        Context.closeSession();
    }
   
}
