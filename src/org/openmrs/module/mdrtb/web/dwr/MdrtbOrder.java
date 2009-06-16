package org.openmrs.module.mdrtb.web.dwr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;

public class MdrtbOrder {
    protected final Log log = LogFactory.getLog(getClass());
    
    public boolean voidOrder(int orderId, String voidReason) {
        try {
           Order o = Context.getOrderService().getOrder(orderId);
           Context.getOrderService().voidOrder(o, voidReason);
        } catch (Exception ex){
            return false;
        }
        return true;
    } 
    
    
    public boolean discontinueOrder(int orderId, String discontinueDate, int discontinueReasonConceptId) {
        try {
           ConceptService cs = Context.getConceptService();
           Concept discontinueConcept = cs.getConcept(discontinueReasonConceptId);
               if (discontinueConcept == null)
                   discontinueConcept = MdrtbUtil.getMDRTBConceptByName("OTHER NON-CODED", new Locale("en", "US"));
               if (discontinueConcept == null)
                   return false;
               Date discDate = null;
               SimpleDateFormat sdf = Context.getDateFormat();
               discDate = sdf.parse(discontinueDate);
               if (discDate == null)
                   return false;
               Order o = Context.getOrderService().getOrder(orderId);
               if (o != null && discDate.after(o.getStartDate())){
                   if (discDate.after(new Date()))
                       o.setAutoExpireDate(discDate);
                   Context.getOrderService().discontinueOrder(o, discontinueConcept , discDate);

                       
               }
                   
               else
                   return false;
        } catch (Exception ex){
            return false;
        }
        return true;
    } 
}
