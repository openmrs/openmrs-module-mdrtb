package org.openmrs.module.mdrtb;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.ConceptName;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MdrtbService extends OpenmrsService {
    
    public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws APIException;
    
    public void purgeOrderException(OrderExtension oe) throws APIException;
    
    public OrderExtension saveOrderExtension(OrderExtension oe) throws APIException;
    
    public  OrderExtension voidOrderExtension(OrderExtension oe) throws APIException;

    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws APIException ;
    
    public MdrtbFactory getMdrtbFactory();

    public void setMdrtbFactory(MdrtbFactory newMdrtbFactory);

    public List<MdrtbRegimenSuggestion> getStandardRegimens();
    
    public void setStandardRegimens(List<MdrtbRegimenSuggestion> standardRegimens);
    
    public List<Locale> getLocaleSetUsedInDB();

    public void setLocaleSetUsedInDB(List<Locale> localeSetUsedInDB);
}
