package org.openmrs.module.labmodule.web.dwr;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.module.labmodule.specimen.reporting.District;
import org.openmrs.module.labmodule.specimen.reporting.Facility;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.openmrs.web.dwr.PatientListItem;

public class LabFindlocation {
    protected final Log log = LogFactory.getLog(getClass());
    
    public Collection getDistricts(int parentId) {
        
        Collection<Object> LocationList = new Vector<Object>();

        Integer userId = -1;
        if (Context.isAuthenticated())
            userId = Context.getAuthenticatedUser().getUserId();
        
        Collection<District> locList = Context.getService(TbService.class).getDistricts(parentId);
        LocationList = new Vector<Object>(locList.size());
        LocationList.addAll(locList);
        
        return LocationList;
    }
    
 public Collection getfacilities(int parentId) {
        
        Collection<Object> LocationList = new Vector<Object>();

        Integer userId = -1;
        if (Context.isAuthenticated())
            userId = Context.getAuthenticatedUser().getUserId();
        
        Collection<Facility> locList = Context.getService(TbService.class).getFacilities(parentId);            
        LocationList = new Vector<Object>(locList.size());
        LocationList.addAll(locList);
        return LocationList;
    }
    
 public Collection getAllDistricts() {
     
     Collection<Object> LocationList = new Vector<Object>();

     Integer userId = -1;
     if (Context.isAuthenticated())
         userId = Context.getAuthenticatedUser().getUserId();
     
     Collection<District> locList = Context.getService(TbService.class).getDistricts();
     LocationList = new Vector<Object>(locList.size());
     LocationList.addAll(locList);
     
     return LocationList;
 }
 
public Collection getAllFacilities() {
     
     Collection<Object> LocationList = new Vector<Object>();

     Integer userId = -1;
     if (Context.isAuthenticated())
         userId = Context.getAuthenticatedUser().getUserId();
     
     Collection<Facility> locList = Context.getService(TbService.class).getFacilities();
     LocationList = new Vector<Object>(locList.size());
     LocationList.addAll(locList);
     
     return LocationList;
 }

public Collection getAllOblasts() {
    
    Collection<Object> LocationList = new Vector<Object>();

    Integer userId = -1;
    if (Context.isAuthenticated())
        userId = Context.getAuthenticatedUser().getUserId();
    
    Collection<Oblast> locList = Context.getService(TbService.class).getOblasts();
    LocationList = new Vector<Object>(locList.size());
    LocationList.addAll(locList);
    
    return LocationList;
}

public Map<String, Object> getlocation(String name) {
    
    Integer userId = -1;
    if (Context.isAuthenticated())
        userId = Context.getAuthenticatedUser().getUserId();
    Location loc=Context.getLocationService().getLocation(name);
    Map<String, Object> location = Context.getService(TbService.class).getLocation(loc, "");
    return location;
}

}
