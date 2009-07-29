package org.openmrs.module.mdrtb.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptName;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.db.MdrtbDAO;

public class MdrtbServiceImpl  extends BaseOpenmrsService implements MdrtbService {

    protected final Log log = LogFactory.getLog(getClass());

    protected MdrtbDAO dao;
    
    public void setMdrtbDAO(MdrtbDAO dao) {
        this.dao = dao;
    }
    
 public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws APIException{
     return dao.getOrderExtension(o, includeVoided);
 }
    
    public void purgeOrderException(OrderExtension oe) throws APIException{
            dao.purgeOrderException(oe);
    }
    
    public OrderExtension saveOrderExtension(OrderExtension oe) throws APIException{
        dao.saveOrderExtension(oe);
        return oe;
    }
    
    
    
    public  OrderExtension voidOrderExtension(OrderExtension oe) throws APIException{
        oe.setVoided(true);
        oe.setVoidReason(" ");
        oe.setVoidedBy(Context.getAuthenticatedUser());
        saveOrderExtension(oe);
        return oe;
    }
    
    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws APIException {
        return dao.getMdrtbConceptNamesByNameList(nameList, removeDuplicates, loc);
    }
    
}
