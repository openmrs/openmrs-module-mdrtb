package org.openmrs.module.mdrtb.web.dwr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.OrderExtensionService;

public class MdrtbOrder {
    protected final Log log = LogFactory.getLog(getClass());
    
    public boolean voidOrder(int orderId, String voidReason) {
        try {
           Order o = Context.getOrderService().getOrder(orderId);
           Context.getOrderService().voidOrder(o, voidReason);
           
           OrderExtensionService oes = (OrderExtensionService)Context.getService(OrderExtensionService.class);
           List<OrderExtension> oeS = oes.getOrderExtension(o, false);
           for (OrderExtension oe : oeS){
               oes.voidOrderExtension(oe);
           }
           
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
               Date discDate = new Date();
               SimpleDateFormat sdf = Context.getDateFormat();
               discDate = sdf.parse(discontinueDate);
               if (discDate == null)
                   return false;
           Context.getOrderService().discontinueOrder(Context.getOrderService().getOrder(orderId), discontinueConcept , discDate);
        } catch (Exception ex){
            return false;
        }
        return true;
    } 
}
