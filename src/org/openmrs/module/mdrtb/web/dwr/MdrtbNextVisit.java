package org.openmrs.module.mdrtb.web.dwr;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;

public class MdrtbNextVisit {
    protected final Log log = LogFactory.getLog(getClass());
    
    public boolean setNextVisitDate(String newDate, int patientId, int locationId){
        Date ret = new Date();
        MdrtbFactory mu = new MdrtbFactory();
        
        Concept nextVisitConcept = mu.getConceptNextVisit();
        Person p = Context.getPersonService().getPerson(patientId);
        SimpleDateFormat sdf = Context.getDateFormat();
        
        try {
           Location location = Context.getLocationService().getLocation(locationId);
           ret = sdf.parse(newDate); 
           Obs oNew = new Obs();
           oNew.setConcept(nextVisitConcept);
           oNew.setCreator(Context.getAuthenticatedUser());
           oNew.setDateCreated(new Date());
           oNew.setLocation(location);
           if (oNew.getLocation() == null)
               oNew.setLocation(Context.getLocationService().getLocation("Unknown Location"));
           oNew.setObsDatetime(new Date());
           oNew.setPerson(p);
           oNew.setValueDatetime(ret);
           oNew.setVoided(false);
           Context.getObsService().saveObs(oNew, "");
        }
        catch (Exception ex){
            log.error("DWR Error saving next visit date",ex);
            return false;
        }

        return true;
    }
}
