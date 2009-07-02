package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;

public class MdrtbUtil {
    
    protected final Log log = LogFactory.getLog(getClass());

    public static Concept getMDRTBConceptByName(String conceptString, Locale loc){
        Concept ret = null;
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(loc);
        //first, if there's an exact match in the passed-in locale, that's what we want
        List<ConceptWord> conceptWords = Context.getConceptService().findConcepts(conceptString.trim(), locales, false, null, null, null, null);
        for (ConceptWord cw : conceptWords){
            Concept cTmp = cw.getConcept();
            for (ConceptName cns : cTmp.getNames(loc)){
                if (cns.getName().trim().equals(conceptString.trim())){
                    Collection<ConceptAnswer> cas = cTmp.getAnswers();
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
                    return cTmp;
                }  
            }
        }
        //second, check all other locales if still null 
        if (ret == null){
            
            
            locales = getLocalesFromDB();
            
            //first, if there's an exact match in the passed-in locale, that's what we want
            conceptWords = Context.getConceptService().findConcepts(conceptString.trim(), locales, false, null, null, null, null);
            for (ConceptWord cw : conceptWords){
                Concept cTmp = cw.getConcept();
                for (Locale localTmp :locales){
                    for (ConceptName cns : cTmp.getNames(localTmp)){
                        if (cns.getName().trim().equals(conceptString.trim())){
                            Collection<ConceptAnswer> cas = cTmp.getAnswers();
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
                            return cTmp;
                        }  
                    }  
                }
            }
            
        }
        return ret;
    }
    
    /*
     * faster method to be used by MdrtbFactory
     */
    public static Concept getMDRTBConceptByName(String conceptString, Locale loc, List<ConceptName> cnList){
        Concept ret = null;
        //first, if there's an exact match in the passed-in locale, that's what we want
             for (ConceptName cns : cnList){
                if (cns.getName().trim().equals(conceptString.trim()) && cns.getLocale().equals(loc)){
                    Collection<ConceptAnswer> cas = cns.getConcept().getAnswers();
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
                    return cns.getConcept();
                }  
            }
        //second, check all other locales if still null 
        if (ret == null){
            
            //first, if there's an exact match in the passed-in locale, that's what we want
                    for (ConceptName cns : cnList){
                        if (cns.getName().trim().equals(conceptString.trim())){
                            Collection<ConceptAnswer> cas = cns.getConcept().getAnswers();
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
                            return cns.getConcept();
                        }  
                    }  
        }
        return ret;
    }
    
    public static List<Locale> getLocalesFromDB(){
        List<Locale> locales = new ArrayList<Locale>();
        List<List<Object>> rows = Context.getAdministrationService().executeSQL("select distinct locale from concept_word", true);
        
        for (List<Object> row:rows){
            for (Object o : row){
                String oTmp = (String) o;
                if (oTmp != null && oTmp != "")
                    locales.add(new Locale(oTmp));
            }
        }
        return locales;
        
    }
    
    public static List<Concept>  getDiscontinueReasons() {
        
        Collection<ConceptAnswer> discontinueReasons = new HashSet<ConceptAnswer>();
        List<Concept> discontinueRes = new ArrayList<Concept>();
        try {
     
            Concept discontinueReasonsConcept =  getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.discontinue_drug_order_reasons"), new Locale("en", "US"));
            discontinueReasons = discontinueReasonsConcept.getAnswers(false);
            for (ConceptAnswer ca :discontinueReasons){
                discontinueRes.add(ca.getAnswerConcept());
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "The global property mdrtb.discontinue_drug_order_reasons did not return a valid concept set name");
        }
        return discontinueRes;
    }
    

    public static Concept getDefaultDiscontinueReason(){
        List<Concept> cList = getDiscontinueReasons();
        String defaultGPValue = Context.getAdministrationService().getGlobalProperty("mdrtb.default_discontinue_drug_order_reason");
        for (Concept c : cList){
            if (c.getBestName(new Locale("en", "US")) != null && c.getBestName(new Locale("en", "US")).getName().equals(defaultGPValue)){
                return c;
            }
        }
        throw new RuntimeException("global property default_discontinue_drug_order_reason not set to a concept that I can find.  Concept must be a concept answer of global property discontinue_drug_order_reasons.");
    }
    //TODO:  this is total crap:
    public static Location getDefaultLocation(Patient p){
        Location loc = null;
        List<Encounter> encList = Context.getEncounterService().getEncountersByPatient(p);
        if (!encList.isEmpty()){
            return encList.get(encList.size()-1).getLocation();
        }
        List<Location> locList = Context.getLocationService().getAllLocations(false);
        if (!locList.isEmpty())
            return locList.get(0);
        locList = Context.getLocationService().getAllLocations(true);
        if (!locList.isEmpty())
            return locList.get(0);
        if (loc == null)
            throw new RuntimeException("Unable to provide default location.  Does openmrs have any locations configured??");
        return loc;
    }
    
    public static String getMdrtbPatientIdentifier(Patient p){
        String ret = "";
        String piList = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_identifier_type");    
        Set<PatientIdentifier> identifiers = p.getIdentifiers();
        for (PatientIdentifier pi : identifiers){
            if (pi.getIdentifierType().getName().equals(piList)){
                return pi.getIdentifier();
            }
        }
        if (identifiers.size() > 0){
            for (PatientIdentifier pi : identifiers){
                return pi.getIdentifier();
            }
        } 
        return ret;
    }

    public static Obs getMostRecentObs(Integer conceptId, Patient p){
        Concept c = Context.getConceptService().getConcept(conceptId);
        List<Obs> oList = Context.getObsService().getObservationsByPersonAndConcept(p, c);
        if (oList.size() > 0)
            return oList.get(oList.size()-1);
        return null;
    }
}
