package org.openmrs.module.mdrtb.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.Order;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.HibernateOrderDAO;
import org.openmrs.module.mdrtb.OrderExtension;
import org.openmrs.module.mdrtb.db.MdrtbDAO;

public class HibernateMdrtbDAO implements MdrtbDAO {

    protected static final Log log = LogFactory.getLog(HibernateOrderDAO.class);
    
    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;
    
    
    public void setSessionFactory(SessionFactory sessionFactory) { 
        this.sessionFactory = sessionFactory;
    }
    
 @SuppressWarnings("unchecked")
 public List<OrderExtension> getOrderExtension(Order o, boolean includeVoided) throws DAOException{
     
     Criteria crit = sessionFactory.getCurrentSession().createCriteria(OrderExtension.class);
     
     if (includeVoided == false)
         crit.add(Expression.eq("voided", false));
     
     if (o != null)
         crit.add(Expression.eq("order", o));
     
     crit.addOrder( org.hibernate.criterion.Order.desc("dateCreated") );
     
     return crit.list();
 }
    
    public void purgeOrderException(OrderExtension oe) throws DAOException{
        sessionFactory.getCurrentSession().delete(oe);
    }
    
    public OrderExtension saveOrderExtension(OrderExtension oe) throws DAOException{ 
       sessionFactory.getCurrentSession().saveOrUpdate(oe);
        return oe;
    }
    
    @SuppressWarnings("unchecked")
    public List<ConceptName> getMdrtbConceptsByEnglishNameList(List<String> nameList)  throws DAOException {
        List<ConceptName> ret = new ArrayList<ConceptName>();
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(org.openmrs.ConceptName.class);
        crit.add(Expression.in("name", nameList));
        crit.add(Expression.eq("voided", false));
        ret = crit.list();
        //TODO:  fix this -- it SUCKS!!!!
        for (ConceptName cnTmp : ret){
          Collection<ConceptAnswer> cas = cnTmp.getConcept().getAnswers();
          if (cas != null){
              for (ConceptAnswer ca : cas){
                  Collection<ConceptName> cnsTmp = ca.getAnswerConcept().getNames();
                  for (ConceptName cn:cnsTmp){
                      Collection<ConceptNameTag> tags = cn.getTags();
                      for (ConceptNameTag cnTag:tags){
                          cnTag.getTag();
                      }
                  } 
              }
          }
        } 
        return ret;
    }
    
}
