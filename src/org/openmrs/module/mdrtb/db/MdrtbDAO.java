package org.openmrs.module.mdrtb.db;

import java.util.List;

import org.openmrs.ConceptName;
import org.openmrs.Order;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.OrderExtension;

public interface MdrtbDAO {

 public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws DAOException;
    
    public void purgeOrderException(OrderExtension oe) throws DAOException;
    
    public OrderExtension saveOrderExtension(OrderExtension oe) throws DAOException;
    
  
    public List<ConceptName> getMdrtbConceptsByEnglishNameList(List<String> nameList)  throws DAOException ;
    
}
