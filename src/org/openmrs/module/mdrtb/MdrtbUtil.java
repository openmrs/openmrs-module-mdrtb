package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptWord;
import org.openmrs.api.context.Context;

public class MdrtbUtil {

    
    public static Concept getMDRTBConceptByName(String conceptString, Locale loc){
        Concept ret = null;
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(loc);
        //first, if there's an exact match in the passed-in locale, that's what we want
        List<ConceptWord> conceptWords = Context.getConceptService().findConcepts(conceptString.trim(), locales, false, null, null, null, null);
        for (ConceptWord cw : conceptWords){
            Concept cTmp = cw.getConcept();
            if (cTmp.getName(loc).getName().equals(conceptString.trim())){
                return cTmp;
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
                    if (cTmp.getName(localTmp).getName().equals(conceptString.trim())){
                        return cTmp;
                    }   
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
            if (c.getName(new Locale("en", "US")) != null && c.getName(new Locale("en", "US")).getName().equals(defaultGPValue)){
                return c;
            }
        }
        throw new RuntimeException("global property default_discontinue_drug_order_reason not set to a concept that I can find.  Concept must be a concept answer of global property discontinue_drug_order_reasons.");
    }
    
    

}
