package org.openmrs.module.mdrtb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

@Deprecated
public class MdrtbSpecimenTrackingController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
    							    Object obj, BindException errors) throws Exception {
    	
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
		
		List<Encounter> encounters = new ArrayList<Encounter>();
    	String encounterTypeName = Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type");
    	MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        MdrtbFactory mu = ms.getMdrtbFactory();
    	Concept dstConcept = mu.getConceptDSTParent();
    	try {
    		List<EncounterType> ec = Arrays.asList(Context.getEncounterService().getEncounterType(encounterTypeName));
    		List<Encounter> encs = Context.getEncounterService().getEncounters(null, null, null, null, null, ec, false);
    		for (Encounter e : encs) {
    			for (Order o : e.getOrders()) {
    				if (o.getConcept() != null && o.getConcept().getConceptId().equals(dstConcept.getConceptId())) {
    					encounters.add(e);
    				}
    			}
    		}
    	}
    	catch (Exception e) {
    		log.warn("You have configured an invalid encounter type in mdrtb.specimen_collection_encounter_type");
    	}
    	map.put("encounters", encounters);

        return map;
    }    
}
