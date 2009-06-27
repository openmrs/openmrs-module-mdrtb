package org.openmrs.module.mdrtb.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MdrtbSpecimenRegistrationController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
    							    Object obj, BindException errors) throws Exception {

        if (Context.isAuthenticated()) {
        	
        	String encounterDateParam = request.getParameter("encounterDate");
        	Date encounterDate = null;
        	try {
        		encounterDate = Context.getDateFormat().parse(encounterDateParam);
        	}
        	catch (Exception e) {
        		log.error(e);
        		throw new IllegalArgumentException("You have specified an invalid encounter date");
        	}
        	
        	String providerParam = request.getParameter("provider");
        	User provider = null;
        	try {
        		provider = Context.getUserService().getUser(Integer.parseInt(providerParam));
        	}
        	catch (Exception e) {
        		log.error(e);
        		throw new IllegalArgumentException("You have specified an invalid provider");
        	}
        	
        	String locationParam = request.getParameter("location");
        	Location location = null;
        	try {
        		location = Context.getLocationService().getLocation(Integer.parseInt(locationParam));
        	}
        	catch (Exception e) {
        		log.error(e);
        		throw new IllegalArgumentException("You have specified an invalid location");
        	}
        	
        	String encounterTypeName = Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type");
        	EncounterType encounterType = null;
        	try {
        		encounterType = Context.getEncounterService().getEncounterType(encounterTypeName);
        	}
        	catch (Exception e) {
        		throw new IllegalArgumentException("You have configured an invalid encounter type in mdrtb.specimen_collection_encounter_type");
        	}

        	String orderTypeIdStr = Context.getAdministrationService().getGlobalProperty("mdrtb.lab_test_order_type");
        	OrderType orderType = null;
        	try {
        		orderType = Context.getOrderService().getOrderType(Integer.parseInt(orderTypeIdStr));
        	}
        	catch (Exception e) {
        		throw new IllegalArgumentException("You have configured an invalid order type in mdrtb.lab_test_order_type");
        	}
        	        	
        	String[] patientIds = request.getParameterValues("patientId");
        	List<Patient> patients = null;
        	try {
        		List<Integer> pIds = new ArrayList<Integer>();
        		for (String pId : patientIds) {
        			pIds.add(Integer.valueOf(pId));
        		}
        		patients = Context.getPatientSetService().getPatients(pIds);
        	}
        	catch (Exception e) {
        		log.error("Error parsing patientIds for submission." + e);
        		throw new IllegalArgumentException("One or more of the entered patients is invalid.");
        	}
        	
        	Concept dstConcept = MdrtbFactory.getInstance().getConceptDSTParent();
    		
        	// Create encounters for each patient
        	for (Patient p : patients) {
        		
        		Encounter e = new Encounter();
        		e.setPatient(p);
        		e.setEncounterDatetime(encounterDate);
        		e.setEncounterType(encounterType);
        		e.setLocation(location);
        		e.setProvider(provider);
        		
        		Order o = new Order();
        		o.setConcept(dstConcept);
        		o.setEncounter(e);
        		o.setOrderer(provider);
        		o.setOrderType(orderType);
        		o.setPatient(p);
        		o.setStartDate(encounterDate);
        		e.addOrder(o);
        		
        		Context.getEncounterService().saveEncounter(e);
        	}
        	
        }
        return new ModelAndView(new RedirectView(getSuccessView()));
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request){
    	return "";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map referenceData(HttpServletRequest request, Object obj, Errors errs) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dateFormat", Context.getDateFormat().toPattern());
        MessageSourceAccessor msa = getMessageSourceAccessor();
        StringBuilder days = new StringBuilder();
        for (String day : new String[] {"sunday","monday","tuesday","wednesday","thursday","friday","saturday",
        								"sun","mon","tues","wed","thurs","fri","sat"}) {
        	days.append("'"+msa.getMessage("mdrtb."+day)+"'");
        	if (!day.equals("sat")) {
        		days.append(",");
        	}
        }
        map.put("daysOfWeek", days.toString());
        
        StringBuilder months = new StringBuilder();
        for (String month : new String[] {"january","february","march","april","may","june",
				 "july","august","september","october","november","december",
				 "jan","feb","mar","ap","may","jun","jul","aug","sept","oct","nov","dec"}) {
        	
        	months.append("'"+msa.getMessage("mdrtb."+month)+"'");
			if (!month.equals("dec")) {
				months.append(",");
			}
		}
		map.put("monthsOfYear", months.toString());

        return map;
    }    
}
