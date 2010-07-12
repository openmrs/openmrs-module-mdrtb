package org.openmrs.module.mdrtb.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptWord;
import org.openmrs.Location;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.db.MdrtbDAO;

public class HibernateMdrtbDAO implements MdrtbDAO {

    protected static final Log log = LogFactory.getLog(HibernateMdrtbDAO.class);
    
    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;
    
    
    public void setSessionFactory(SessionFactory sessionFactory) { 
        this.sessionFactory = sessionFactory;
    }
        
    @SuppressWarnings("unchecked")
    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws DAOException {
        List<ConceptName> ret = new ArrayList<ConceptName>();
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(org.openmrs.ConceptName.class);
        crit.add(Expression.in("name", nameList));
        crit.add(Expression.eq("voided", false));
        if (loc != null)
            crit.add(Expression.eq("locale", loc.getLanguage()));
        ret = crit.list();
        //TODO:  fix this -- it SUCKS!!!!  (this is for hydration)
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
        
        if (removeDuplicates){
            Map<Concept, ConceptName> retNoDups = new LinkedHashMap<Concept, ConceptName>();
            ArrayList<ConceptName> newRet = new ArrayList<ConceptName>();
            for (ConceptName cnTmp : ret){
                if (!retNoDups.containsKey(cnTmp.getConcept()))
                    retNoDups.put(cnTmp.getConcept(), cnTmp);
            }
            newRet.addAll(retNoDups.values());
            return newRet;
        }
        
        return ret;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Location> getAllMdrtrbLocations(boolean includeRetired) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
        if (!includeRetired) {
            criteria.add(Expression.like("retired", false));
        }
        criteria.addOrder(org.hibernate.criterion.Order.asc("name"));
        return criteria.list();
    }
    
    
    /**
     * @see org.openmrs.api.db.ConceptDAO#getConceptWords(java.lang.String, java.util.List, boolean,
     *      java.util.List, java.util.List, java.util.List, java.util.List, org.openmrs.Concept,
     *      java.lang.Integer, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales) throws DAOException {
        
        //add the language-only portion of locale if its not in the list of locales already
        List<Locale> localesToAdd = new Vector<Locale>();
        for (Locale locale : locales) {
            Locale languageOnly = new Locale(locale.getLanguage());
            if (locales.contains(languageOnly) == false)
                localesToAdd.add(languageOnly);
        }
        
        locales.addAll(localesToAdd);
        
        //String locale = loc.getLanguage().substring(0, 2);        
        List<String> words = ConceptWord.getUniqueWords(phrase); //assumes getUniqueWords() removes quote(') characters.  (otherwise we would have a security leak)
        
        // these are the answers to restrict on
        List<Concept> answers = new Vector<Concept>();
        
        
        List<ConceptWord> conceptWords = new Vector<ConceptWord>();
        
        if (words.size() > 0 || !answers.isEmpty()) {
            
            Criteria searchCriteria = sessionFactory.getCurrentSession().createCriteria(ConceptWord.class, "cw1");
            searchCriteria.add(Expression.in("locale", locales));
            
            
            
            // Only restrict on answers if there are any
            if (!answers.isEmpty())
                searchCriteria.add(Expression.in("cw1.concept", answers));
            
            if (words.size() > 0) {
                Iterator<String> word = words.iterator();
                searchCriteria.add(Expression.like("word", word.next(), MatchMode.START));
                Conjunction junction = Expression.conjunction();
                while (word.hasNext()) {
                    String w = word.next();
                    
                    if (log.isDebugEnabled())
                        log.debug("Current word: " + w);
                    
                    DetachedCriteria crit = DetachedCriteria.forClass(ConceptWord.class).setProjection(
                        Property.forName("concept")).add(Expression.eqProperty("concept", "cw1.concept")).add(
                        Restrictions.like("word", w, MatchMode.START)).add(Expression.in("locale", locales));
                    junction.add(Subqueries.exists(crit));
                }
                searchCriteria.add(junction);
            }
            
            conceptWords = searchCriteria.list();
        }
        
        if (log.isDebugEnabled())
            log.debug("ConceptWords found: " + conceptWords.size());
        
        return conceptWords;
    }
    
}
