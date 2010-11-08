package org.openmrs.module.mdrtb.regimen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.order.RegimenSuggestion;
import org.openmrs.util.OpenmrsUtil;

/**
 * Utility method for use with Regimens
 */
public class RegimenUtils {

    protected static final Log log = LogFactory.getLog(RegimenUtils.class);

    /**
     * @return a Map from the Can Replace Concept Set to a List of supported RegimenSuggestions for that Set
     */
    public static Map<String, List<RegimenSuggestion>> getRegimenSuggestions() {
    	Map<String, List<RegimenSuggestion>> suggestions = new HashMap<String, List<RegimenSuggestion>>();
    	for (RegimenSuggestion rs : Context.getOrderService().getStandardRegimens()) {
    		String canReplace = ObjectUtil.nvlStr(rs.getCanReplace(), "");
    		List<RegimenSuggestion> l = suggestions.get(canReplace);
    		if (l == null) {
    			l = new ArrayList<RegimenSuggestion>();
    			suggestions.put(canReplace, l);
    		}
    		l.add(rs);
    	}
    	return suggestions;
    }
    
    /**
     * @return a List of Drugs whose Concept matches a Concept in the passed set
     */
    public static List<Drug> getDrugsWithGenericInSet(Concept drugSet) {
    	List<Drug> ret = new ArrayList<Drug>();
    	List<Drug> allDrugs = Context.getConceptService().getAllDrugs();
    	if (drugSet == null) {
    		return allDrugs;
    	}
    	List<Concept> generics = Context.getConceptService().getConceptsByConceptSet(drugSet);
    	for (Drug drug : allDrugs) {
    		if (generics.contains(drug.getConcept())) {
    			ret.add(drug);
    		}
    	}
    	return ret;
    }
    
    /**
     * TODO: Consider making this user-configurable
     * @return the List of all possible regimen types
     */
    public static List<RegimenType> getRegimenTypes() {
    	List<RegimenType> ret = new ArrayList<RegimenType>();
    	Map<String, List<RegimenSuggestion>> suggests = getRegimenSuggestions();
    	Set<Drug> addedDrugs = new HashSet<Drug>();
    	{
    		RegimenType type = new RegimenType("tb");
    		type.setDrugSet(getMdrtbService().getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS));
    		type.setDrugs(getDrugsWithGenericInSet(type.getDrugSet()));
    		addedDrugs.addAll(type.getDrugs());
    		type.setTypeQuestion(getMdrtbService().getConcept(MdrtbConcepts.CURRENT_MDRTB_TREATMENT_TYPE));
    		type.setReasonForStoppingQuestion(getMdrtbService().getConcept(MdrtbConcepts.REASON_TB_TX_STOPPED));
    		type.setSuggestions(suggests.get(MdrtbConcepts.TUBERCULOSIS_DRUGS[0]));
    		ret.add(type);
    	}
    	{
    		RegimenType type = new RegimenType("hiv");
    		type.setDrugSet(getMdrtbService().getConcept(MdrtbConcepts.ANTIRETROVIRALS));
    		type.setDrugs(getDrugsWithGenericInSet(type.getDrugSet()));
    		addedDrugs.addAll(type.getDrugs());
    		type.setSuggestions(suggests.get(MdrtbConcepts.ANTIRETROVIRALS[0]));
    		type.setReasonForStoppingQuestion(getMdrtbService().getConcept(MdrtbConcepts.REASON_HIV_TX_STOPPED));
    		ret.add(type);
    	}
    	{
    		RegimenType type = new RegimenType("other");
    		List<Drug> otherDrugs = getDrugsWithGenericInSet(null);
    		otherDrugs.removeAll(addedDrugs);
    		type.setDrugs(otherDrugs);
    		type.setReasonForStoppingQuestion(getMdrtbService().getConcept(MdrtbConcepts.REASON_TB_TX_STOPPED));
    		ret.add(type);
    	}
    	return ret;
    }
    
    /**
     * @return the full regimen history for the patient, categorized by type
     */
    public static Map<String, RegimenHistory> getRegimenHistory(Patient patient) {
    	Map<String, RegimenHistory> m = new LinkedHashMap<String, RegimenHistory>();

    	List<DrugOrder> orders = Context.getOrderService().getDrugOrdersByPatient(patient);
    	
    	// Add a RegimenHistory to the returned Map for each defined Regimen Type
    	for (RegimenType type : RegimenUtils.getRegimenTypes()) {
    		List<Concept> concepts = null;
    		if (type.getDrugSet() != null) {
    			concepts = getMdrtbService().getDrugsInSet(type.getDrugSet());
    		}
    		RegimenHistory history = new RegimenHistory(patient, type);
    		
    		// Add each DrugOrder that is appropriate for the Regimen Type, and remove it so that it is only found within one type
    		for (Iterator<DrugOrder> i = orders.iterator(); i.hasNext();) {
    			DrugOrder o = i.next();
    			if (concepts == null || concepts.contains(o.getConcept())) {
    				history.addDrugOrder(o);
    				i.remove();
    			}
    		}
    		
    		// Add each Observation that is appropriate for the Regimen Type
    		for (Obs o : Context.getObsService().getObservationsByPersonAndConcept(patient, type.getTypeQuestion())) {
    			history.addReasonForStarting(o);
    		}
    		
    		m.put(type.getName(), history);
    	}
    	return m;
    }
    
    /**
     * @param patient
     * @return the Tuberculosis Regimens for a Patient
     */
    public static RegimenHistory getTbRegimenHistory(Patient patient) {   	
    	return getRegimenHistory(patient).get("tb");
    }
    
    /**
     * @param patient
     * @return the HIV Regimens for a Patient
     */
    public static RegimenHistory getHivRegimenHistory(Patient patient) {
    	return getRegimenHistory(patient).get("hiv");	
    }
	
    /**
     * @return a String representation of the Generic drugs within this Regimen, separated by the passed String
     */
    public static String formatRegimenGenerics(Object regimen, String separator, String emptyCode) {
    	return formatConcepts(((Regimen)regimen).getUniqueGenerics(), separator, emptyCode);
    }
    
    /**
     * @return a String representation of the Generic drugs within the passed orders, separated by the passed String
     */
    public static String formatDrugOrders(Collection<DrugOrder> orders, String separator, String emptyCode) {
    	Set<Concept> s = new HashSet<Concept>();
    	for (DrugOrder d : orders) {
    		s.add(d.getConcept());
    	}
    	return formatConcepts(s, separator, emptyCode);
    }
    
    /**
     * @return a String representation of the Obs within the passed orders, separated by the passed String
     */
    public static String formatCodedObs(Collection<Obs> obs, String separator, String emptyCode) {
    	Set<Concept> s = new HashSet<Concept>();
    	for (Obs o : obs) {
    		if (o.getValueCoded() != null) {
    			s.add(o.getValueCoded());
    		}
    	}
    	return formatConcepts(s, separator, emptyCode);
    }
    
    /**
     * @return the formatted obs valueCoded
     */
    public static String formatCodedObs(Obs obs, String emptyCode) {
    	if (obs == null || obs.getValueCoded() == null) {
    		return emptyCode;
    	}
    	return formatCodedObs(Arrays.asList(obs), "", emptyCode);
    }
    
    /**
     * @return a String representation of the passed Concepts, using best short name, separated by the passed String
     */
    public static String formatConcepts(Collection<Concept> concepts, String separator, String emptyCode) {
    	if (concepts == null || concepts.isEmpty()) {
    		if (ObjectUtil.notNull(emptyCode)) {
    			return MessageUtil.translate(emptyCode);
    		}
    		return "";
    	}
    	List<String> shortNames = new ArrayList<String>();
    	for (Concept c : concepts) {
    		shortNames.add(c.getBestShortName(Context.getLocale()).getName());
    	}
    	Collections.sort(shortNames);
    	return OpenmrsUtil.join(shortNames, separator);
    }
    
    /**
     * @return the MdrtbService
     */
    private static MdrtbService getMdrtbService() {
    	return Context.getService(MdrtbService.class);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    

    

    

    
    /* here's the logic for what happens if there is a future regimen:
     * 
     * 
     *      //1.  if no drug order of a specific DrugOrder, (create the new order).  (OUTCOME1)
            
            //2.  if DrugOrder, for all DrugOrders: 
                //a.  if new start is before old start 
                    //NEW ORDER HAS STOP DATE -- create the older as is, and make adjustments:
                        //1.  if new end is before or equal to old start (create the new order)  (OUTCOME2)
                        //2.  if new end is after old start and ( before old end or old end is infinite) 
                           //if order is different, adjust the start date of the old order to the end date of the new (create the new order) (OUTCOME3)
                           //if order is same void old order(doesn't matter if old order has infinite stop date or not) (create the new order) (OUTCOME4)
                        //3. if end date is greater than or equal to old end -- void old order   (create the new order)  (OUTCOME5)
                    //NEW ORDER DOESN'T HAVE STOP DATE
                        //4. orders are different
                             //set end date of new to beginning of old and stop iterating over existing drug orders time sequence (create the new order, modified) (OUTCOME6)
                        //5. orders are the same
                            //delete the old order (create the new order) (OUTCOME7)
                //b. if start is the same 
                    // void existing (create the new order) (OUTCOME8)
            
                //c.  if start is after existing start
                    //1. if order is after old drug end  (create the new order) (OUTCOME9)
                    //2. if order is before old drug end or equal to old drug end or old drug end date is infinite
                        //if orders are the same update the old order with the new, taking the new end date value (Do not create new order) (OUTCOME10)
                        //if orders are different adjust the old to end on new start date (create the new order) (OUTCOME11)
                    
     * 
    public static void setRegimen(Patient patient, Date effectiveDate, Collection<DrugOrder> drugOrders, Concept reasonForChange, Encounter encounterForChange) {
        RegimenHistory history = getRegimenHistory(patient);
        Regimen regOnDate = history.getRegimenOnDate(effectiveDate);
        List<Regimen> regAfterDate = history.getRegimensAfter(effectiveDate);
        OrderService os = Context.getOrderService();
        if (encounterForChange != null){
            Context.getEncounterService().saveEncounter(encounterForChange);
            for (DrugOrder drugOrder : drugOrders) {
                drugOrder.setEncounter(encounterForChange);
            }
        }    
         
        if (!anyRegimens(regAfterDate)) {
            if (regOnDate == null || regOnDate.getDrugOrders().isEmpty()) {
                //case:  there is no existing regimen on the regimen start date, and there are no new regimens after this date
                // go ahead and create the regimen:
                for (DrugOrder drugOrder : drugOrders) {
                    Context.getOrderService().saveOrder(drugOrder);
                }
            } else {

                //case: there are still open orders and there are no new regimens after this date
                // first see what existing things we need to stop
                for (DrugOrder before : regOnDate.getDrugOrders()) {
                    //stop the old order only if it isn't exactly identical to a new order (excluding discontinued_date)
                    for (DrugOrder newOrder:drugOrders){
                        if (!before.getDiscontinued() && drugOrderMatchesDrugConcept(before, newOrder) && !regimenComponentIsTheSameAsDrugOrderExcludingDates(before, newOrder)){
                            discontinueOrder( before, effectiveDate, reasonForChange);
                        }    
                    }
                }
                // now see what new things to start (or extend)
                for (DrugOrder newOrder : drugOrders) {
                    
                    // create a new order if there isn't already an existing match, 
                        //or if there is (excluding discontinued date) you need to extend, or null the stop date
                    
               
                        boolean alreadyExists = false;
                        for (DrugOrder before : regOnDate.getDrugOrders()){
                            
                            if (!before.getDiscontinued() && regimenComponentIsTheSameAsDrugOrderExcludingDates(before, newOrder)){
                                alreadyExists = true;
                                before.setDiscontinuedDate(newOrder.getDiscontinuedDate());
                                before.setAutoExpireDate(newOrder.getAutoExpireDate());
                                before.setPrn(newOrder.getPrn());
                                before.setInstructions(newOrder.getInstructions());
                                os.saveOrder(before);
                                newOrder.setOrderId(before.getOrderId());
                                break;
                            }
                        }
                        if (!alreadyExists){
                            os.saveOrder(newOrder);
                        }
                }
            }
        } else { //there is a regimen change after the new drug order start date
            for (DrugOrder newOrder : drugOrders) {
                  boolean saveOrder = false;
                  boolean merged = false;
                  history = getRegimenHistory(patient);
                  List<DrugOrder> existingDrugOrders = getDrugOrdersInOrderByDrugOrConcept(history, newOrder);                                  
                  if (existingDrugOrders.size() == 0){
                          saveOrder = setSaveOrder(merged); //(OUTCOME1)
                  } else { 
                        for (DrugOrder before : existingDrugOrders){ 
                            if (newOrder.getStartDate().before(before.getStartDate())){ 
                                    if (newOrder.getDiscontinuedDate() != null){
                                        if (newOrder.getDiscontinuedDate().before(before.getStartDate()) || newOrder.getDiscontinuedDate().equals(before.getStartDate())){
                                            saveOrder = setSaveOrder(merged);//(OUTCOME2)
                                        } else if (newOrder.getDiscontinuedDate().after(before.getStartDate()) && (before.getDiscontinuedDate() == null || newOrder.getDiscontinuedDate().before(before.getDiscontinuedDate()))){
                                            if (!regimenComponentIsTheSameAsDrugOrderExcludingDates(before, newOrder)){
                                                //(OUTCOME3)
                                                before.setStartDate(newOrder.getDiscontinuedDate());
                                                os.saveOrder(before);
                                                saveOrder = setSaveOrder(merged);
                                            } else {
                                                //(OUTCOME4)    
                                                os.voidOrder(before, "overwritten");
                                                saveOrder = setSaveOrder(merged);
                                            }   
                                        } else if (before.getDiscontinuedDate() != null && (newOrder.getDiscontinuedDate().after(before.getDiscontinuedDate()) || newOrder.getDiscontinuedDate().equals(before.getDiscontinuedDate()))){
                                                //(OUTCOME5)
                                                os.voidOrder(before, "overwritten");
                                                saveOrder = setSaveOrder(merged);
                                        }
                                    } else {//new order has infinite end date
                                        if (!regimenComponentIsTheSameAsDrugOrderExcludingDates(before, newOrder)){
                                          //(OUTCOME6)
                                                newOrder.setDiscontinuedDate(before.getStartDate());
                                                saveOrder = setSaveOrder(merged);
                                                break;
                                        } else {
                                          //(OUTCOME7)
                                            os.voidOrder(before, "overwritten");
                                            saveOrder = setSaveOrder(merged);
                                        }
                                    }         
                            } else if (newOrder.getStartDate().equals(before.getStartDate())){ //b
                                //(OUTCOME8)
                                os.voidOrder(before, "overwritten");
                                saveOrder = setSaveOrder(merged);
                            } else { //c -- start date is after or equal to old end date
                                if (before.getDiscontinuedDate() != null && newOrder.getStartDate().after(before.getDiscontinuedDate()))//1
                                    //(OUTCOME9)
                                    saveOrder = setSaveOrder(merged);
                                    
                                if (before.getDiscontinuedDate() == null || newOrder.getStartDate().before(before.getDiscontinuedDate()) || newOrder.getStartDate().equals(before.getDiscontinuedDate())){//2
                                    if (regimenComponentIsTheSameAsDrugOrderExcludingDates(before, newOrder)){
                                      //(OUTCOME10)  
                                        before.setDiscontinuedDate(newOrder.getDiscontinuedDate());
                                        before.setAutoExpireDate(newOrder.getAutoExpireDate());
                                        before.setPrn(newOrder.getPrn());
                                        before.setInstructions(newOrder.getInstructions());
                                        os.saveOrder(before);
                                        saveOrder = false;
                                        newOrder.setOrderId(before.getOrderId());
                                        merged = true;
                                    } else {
                                      //(OUTCOME11)  
                                        before.setDiscontinuedDate(newOrder.getStartDate());
                                        os.saveOrder(before);
                                        saveOrder = setSaveOrder(merged);
                                    }
                                }
                            } 
                        }
                  }
                  if (saveOrder)
                      os.saveOrder(newOrder);
            }      
        }
    }
*/
    /**
     * Discontinues an order given a date and a reason, and saves it to the database if anything has changed.
     *  
     * @param order
     * @param effectiveDate
     * @param reason
     * 
     * @should change discontinued metadata if order is set to be discontinued after date
     * @should have no effect if order is discontinued before date
     */
    public static void discontinueOrder(Order order, Date date, Concept reason) {
        if (!order.isDiscontinuedRightNow()) {
            order.setDiscontinued(true);
            order.setDiscontinuedDate(date);
            order.setDiscontinuedReason(reason);
            Context.getOrderService().saveOrder(order);
        } else if (OpenmrsUtil.compareWithNullAsLatest(date, order.getDiscontinuedDate()) < 0) {
            order.setDiscontinued(true); // should already be true
            order.setDiscontinuedDate(date);
            order.setDiscontinuedReason(reason);
            Context.getOrderService().saveOrder(order);
        }
    }

    /**
     * Returns true if there are any non-empty regimens in this list
     * 
     * @param afterDate
     * @return
     */
    private static boolean anyRegimens(List<Regimen> regimenList) {
        for (Regimen reg : regimenList)
            if (!reg.getDrugOrders().isEmpty())
                return true;
        return false;
    }
    
    private static boolean regimenComponentIsTheSameAsDrugOrderExcludingDates(DrugOrder rc, DrugOrder doTmp){
        if (rc.getDrug() != null && doTmp.getDrug() != null && rc.getDrug().getDrugId().intValue() != doTmp.getDrug().getDrugId().intValue())
            return false;
        if (!OpenmrsUtil.nullSafeEquals(rc.getConcept(), doTmp.getConcept()))
            return false; 
        if (!OpenmrsUtil.nullSafeEquals(rc.getDose(), doTmp.getDose()))
            return false;
        if (!OpenmrsUtil.nullSafeEquals(rc.getFrequency(), doTmp.getFrequency()))
            return false;    
        if (!OpenmrsUtil.nullSafeEquals(rc.getUnits(), doTmp.getUnits()))
            return false;    
        return true;
    }
    
    
    private static boolean drugOrderMatchesDrugConcept(DrugOrder rc, DrugOrder doTmp){
        if (rc.getDrug() != null && 
                (doTmp.getDrug() != null && rc.getDrug().getConcept().equals(doTmp.getDrug().getConcept())
                ||
                (doTmp.getConcept() != null && rc.getDrug().getConcept().equals(doTmp.getConcept()))
                )
            )
            return true;
        if (doTmp.getDrug() != null && 
                (rc.getDrug() != null && doTmp.getDrug().getConcept().equals(rc.getDrug().getConcept())
                ||
                (rc.getConcept() != null && doTmp.getDrug().getConcept().equals(rc.getConcept()))
                )
            )
            return true;
        if (doTmp.getConcept() != null && rc.getConcept() != null && doTmp.getConcept().equals(rc.getConcept()))
            return true;
        return false;
    }
    
    /*newDrugOrder argument used to pass in the drug or the drug concept*/
    public static List<DrugOrder> getDrugOrdersInOrderByDrugOrConcept(RegimenHistory history, DrugOrder newDrugOrder){
            List<DrugOrder> ret = new ArrayList<DrugOrder>();
            List<Regimen> regList = history.getAllRegimens();
            
            for (Regimen regimen : regList){
                for (DrugOrder rc : regimen.getDrugOrders()){
                    if (drugOrderMatchesDrugConcept(rc, newDrugOrder)){
                        ret.add(rc);   
                    }
                }
            }
            return ret;
    }
    
    
    /** 
     * Strips the time component from a Calendar object, leaving only the date component
     */
    public static void stripTimeComponent(Calendar cal) {
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Strips the time component of a Date object, leaving only the date component
     */
    public static void stripTimeComponent(Date date) {
    	if (date == null) {
    		return;
    	}
    	else {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		stripTimeComponent(cal);
    		date = cal.getTime();
    	}
    }
    
    private static boolean setSaveOrder(boolean merged){
        if (!merged)
            return true;
        else 
            return false;
    }
    
    
}