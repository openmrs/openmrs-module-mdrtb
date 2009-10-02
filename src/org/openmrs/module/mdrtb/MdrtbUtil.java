package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;

public class MdrtbUtil {
    
    protected static final Log log = LogFactory.getLog(MdrtbUtil.class);

    public static Concept getMDRTBConceptByName(String conceptString, Locale loc){
        Concept ret = null;
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(loc);
        //first, if there's an exact match in the passed-in locale, that's what we want
        List<ConceptWord> conceptWords = Context.getConceptService().findConcepts(conceptString.trim(), locales, false, null, null, null, null);
        for (ConceptWord cw : conceptWords){
            Concept cTmp = cw.getConcept();
            if (!cTmp.isRetired()){
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
        }
        //second, check all other locales if still null 
        if (ret == null){
            
            
            locales = getLocalesFromDB();
            
            //first, if there's an exact match in the passed-in locale, that's what we want
            conceptWords = Context.getConceptService().findConcepts(conceptString.trim(), locales, false, null, null, null, null);
            for (ConceptWord cw : conceptWords){
                Concept cTmp = cw.getConcept();
                if (!cTmp.isRetired()){
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
                    
                    if (!cns.getConcept().isRetired()){
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
                        Concept cRet = cns.getConcept();
                        cRet = Context.getConceptService().getConcept(cRet.getConceptId());
                        return cRet;
                    }
                    
                }  
            }
        //second, check all other locales if still null 
        if (ret == null){
            
            //first, if there's an exact match in the passed-in locale, that's what we want
                    for (ConceptName cns : cnList){
                        if (cns.getName().trim().equals(conceptString.trim())){
                            
                            if (!cns.getConcept().isRetired()){
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
                                Concept cRet = cns.getConcept();
                                cRet = Context.getConceptService().getConcept(cRet.getConceptId());
                                return cRet;
                            }
                            
                        }  
                    }  
        }
        return ret;
    }
    
    public static List<Locale> getLocalesFromDB(){
        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        return ms.getLocaleSetUsedInDB(); 
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
    

    
   public static int lookupConversionInterval(){
       int ret = 30;
       String intervalString = Context.getAdministrationService().getGlobalProperty("mdrtb.conversion_definition_interval");
       try{
           ret = Integer.valueOf(intervalString.trim()).intValue();
       } catch (Exception ex){
           log.error("mdrtb.conversion_definition_interval is not set correctly, and the value can't be parsed to an integer.  Defaulting to WHO definition of 30", ex);
       }
       return ret;
   }
   
   public static int lookupConversionNumberConsecutive(){
       int ret = 2;
       String intervalString = Context.getAdministrationService().getGlobalProperty("mdrtb.conversion_definition_number");
       try{
           ret = Integer.valueOf(intervalString.trim()).intValue();
           if (ret < 2 )
               throw new RuntimeException("mdrtb.conversion_definition_number is not set to a valid number of consecutive bacteriologies.  Must be 2 or more.");
       } catch (Exception ex){
           log.error("mdrtb.conversion_definition_number is not set correctly, and the value can't be parsed to an integer.  Defaulting to WHO definition of 2", ex);
       }
       return ret;
   }
   
  
   /**
    * 
    * Tests to see if a culture is negative
    * Note:  THIS WORKS FOR BOTH SMEARS AND CULTURES -- JUST PASS IN THE RESULT CONCEPT FOR EITHER IN 2ND ARG.
    * 
    * @param parentObs
    * @return
    */
   public static boolean isNegativeBacteriology(Obs parentObs, Concept cResultConcept){

       String positiveConcepts = Context.getAdministrationService().getGlobalProperty("mdrtb.positive_culture_concepts");
       List<Concept> cList = new ArrayList<Concept>();
       for (StringTokenizer st = new StringTokenizer(positiveConcepts, "|"); st.hasMoreTokens(); ) {
           String s = st.nextToken().trim();
           Concept c = getMDRTBConceptByName(s, new Locale("en", "US"));
           if (c != null)
               cList.add(c);
       }
       //now, unpack parent obs:
       boolean negResult = true;
       for (Obs o : parentObs.getGroupMembers()){
           if (o.getConcept().equals(cResultConcept) && o.getValueCoded() != null && cList.contains(o.getValueCoded()) && !o.isVoided()){
               negResult = false;
               break; 
           }
           if (!negResult)
               break;
       }    
       return negResult;
   }
   
   /**
    * 
    * Utility method used to transition to a state, ignoring usual state change rules.
    * 
    * @param pp
    * @param programWorkflowState
    * @param onDate
    */
   public static PatientProgram  transitionToStateNoErrorChecking(PatientProgram pp, ProgramWorkflowState programWorkflowState, Date onDate) {

       Set<PatientState> lastStates = pp.getStates();

       for (PatientState lastState : lastStates){
       
           
           if (lastState != null  && lastState.getState().getProgramWorkflow().getProgramWorkflowId().intValue() == programWorkflowState.getProgramWorkflow().getProgramWorkflowId().intValue() && !lastState.getVoided()) {
               
                   lastState.setEndDate(onDate);
               //if nonsensical: void
               if (lastState.getEndDate().getTime() <= lastState.getStartDate().getTime()){
                   lastState.setEndDate(onDate);
                   lastState.setDateVoided(new Date());
                   lastState.setVoided(true);
                   lastState.setVoidedBy(Context.getAuthenticatedUser());
                   lastState.setVoidReason("program states were edited such that this state observation was no longer valid"); 
               }
           }  
       }
       

       
       PatientState newState = new PatientState();
       newState.setPatientProgram(pp);
       newState.setState(programWorkflowState);
       newState.setStartDate(onDate);
       pp.getStates().add(newState);
       return pp;
   }
   
   
   public static List<Concept> getDstDrugList(boolean removeDuplicates){
       String drugList = Context.getAdministrationService().getGlobalProperty("mdrtb.DST_drug_list");
       List<Concept> drugConceptList = new ArrayList<Concept>();
       try {
           Concept c =    MdrtbUtil.getMDRTBConceptByName(drugList, new Locale("en", "US"));
           if (c != null && c.isSet()){
              drugConceptList = Context.getConceptService().getConceptsByConceptSet(c);
           } else if (c != null){
              drugConceptList.add(c);
           } else if (c == null){
               //HERE:
               List<String> dstConceptList = new ArrayList<String>();
               for (StringTokenizer st = new StringTokenizer(drugList, "|"); st.hasMoreTokens(); ) {
                   dstConceptList.add(st.nextToken().trim());
               }
               MdrtbService ms = Context.getService(MdrtbService.class);
               List<ConceptName> names = ms.getMdrtbConceptNamesByNameList(dstConceptList, removeDuplicates, null);
               for (String name : dstConceptList){
                   for (ConceptName cName : names){
                       if (cName.getName().trim().compareTo(name.trim()) == 0 && !cName.getConcept().isRetired()){
                           drugConceptList.add(cName.getConcept());
                           break;
                       }    
                   }
               }
              
           }
           
       } catch (Exception ex){
           throw new RuntimeException("Unable to load drug concepts for DST tests.  Check your global properties values for 'mdrtb.DST_drug_list'.");
       }
       return drugConceptList;
   }
   
   


       public static List<Obs> getPositiveDSTResultObs(Date minDate, Concept dstParent, Concept dstResultParent, List<Concept> dstDrugList, List<Concept> positiveConcepts, Patient patient, boolean considerOnlyLatestDst){
           
           ObsService os = Context.getObsService();
           if (minDate == null)
               minDate = new Date(0);
           List<Obs> ret = new ArrayList<Obs>();
           List<Person> pList = new ArrayList<Person>();
           pList.add(patient);
           List<Concept> conceptList = new ArrayList<Concept>();
           conceptList.add(dstParent);
           List<String> stList = new ArrayList<String>();
           stList.add("obsDatetime");
           List<Obs> oList = os.getObservations(pList, null, conceptList, null, null, null, stList, null, null, minDate, null, false);
           Map<Concept, Obs> map = new LinkedHashMap<Concept, Obs>();
           
           if (considerOnlyLatestDst) {
               for (Concept drug : dstDrugList){ //for each drug
                   if (oList.size() > 0){
                       Obs oLatest = oList.get(0); //get the latest DST parent
                       for (Obs oInner : oLatest.getGroupMembers()){ //for all the DST inner obs
                       
                           if (oInner.getConcept().getConceptId().equals(dstResultParent.getConceptId())){ //if we hit a result parent
                               for (Obs o : oInner.getGroupMembers()){ 
                                   if (o.getValueCoded() != null && o.getValueCoded().getConceptId().equals(drug.getConceptId())){
                                       //add to map if latest:
                                       if (!map.containsKey(drug) || o.getObsDatetime().after(map.get(drug).getObsDatetime())
                                           || (map.get(drug).getObsDatetime().equals(o.getObsDatetime()) && positiveConcepts.contains(o.getValueCoded())))
                                           map.put(drug, o);
                                   }
                               }
                           }
                       
                       }
                   }
               } 
               
               
               
           } else {
               
               for (Concept drug : dstDrugList){ //for each drug 
                   for (Obs oLatest: oList){ //for each DST 
                       for (Obs oInner : oLatest.getGroupMembers()){
                           if (oInner.getConcept().getConceptId().equals(dstResultParent.getConceptId())){ //if we hit a result parent
                               for (Obs o : oInner.getGroupMembers()){ //iterate through group members
                               //match to the drug in the obs valueCoded
                                   if (o.getValueCoded() != null && o.getValueCoded().getConceptId().equals(drug.getConceptId())){
                                     //add to map if latest:
                                       if (!map.containsKey(drug) || o.getObsDatetime().after(map.get(drug).getObsDatetime())
                                           || (map.get(drug).getObsDatetime().equals(o.getObsDatetime()) && positiveConcepts.contains(o.getValueCoded())))
                                           map.put(drug, o); 
                                   }
                               }
                           }
                       }
                   }
               }
           }
           for (Object o: map.values()){
               Obs obs = (Obs) o;
               if (positiveConcepts.contains(obs.getConcept()))
                   ret.add(obs);
           }
           return ret;
       }
   
  
   /**
    * returns a list of concepts for each drug that the patient is restant to
    */
   public static List<Concept> getResistantToDrugConcepts(Date minDate, Concept dstParent, Concept dstResultParent, List<Concept> dstDrugList, List<Concept> positiveConcepts, Patient patient, boolean considerOnlyLatestDst){
       List<Concept> ret = new ArrayList<Concept>();
       
          List<Obs> dstRes = getPositiveDSTResultObs(minDate, dstParent, dstResultParent, dstDrugList, positiveConcepts, patient, considerOnlyLatestDst);
          for (Obs o: dstRes){
              if (!ret.contains(o.getValueCoded())){
                  ret.add(o.getValueCoded());
              }
          }
       return ret;
   }
   
   public static Concept getConceptFromMDRTBConceptMaps(String name){
       MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
       MdrtbFactory mu = ms.getMdrtbFactory();
       return mu.getMDRTBConceptByKey(name, new Locale("en", "US"), mu.getXmlConceptList());
   }
   
}
