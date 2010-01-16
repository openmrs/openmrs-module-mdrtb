package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

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
import org.openmrs.module.mdrtb.impl.MdrtbServiceImpl;

public class MdrtbUtil {
    
    protected static final Log log = LogFactory.getLog(MdrtbUtil.class);

public static Concept getMDRTBConceptByName(String conceptString, Locale loc, MdrtbFactory mu){
        
        MdrtbService ms = Context.getService(MdrtbService.class);
        Concept ret = null;
        
        if (conceptString.contains("|") && conceptString.indexOf("|", 0) != conceptString.lastIndexOf("|"))
               return null;

        Map<String, Concept> xmlConceptList = mu.getXmlConceptList();
        ret = mu.getMDRTBConceptByKey(conceptString, loc, xmlConceptList);
        if (ret != null)
              return ret;
        
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
                        mu.addKeyAndConceptToXmlConceptList(conceptString, cTmp);
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
                                mu.addKeyAndConceptToXmlConceptList(conceptString, cTmp);
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
    
    public static List<Concept>  getDiscontinueReasons(MdrtbFactory mu) {
        
        Collection<ConceptAnswer> discontinueReasons = new HashSet<ConceptAnswer>();
        List<Concept> discontinueRes = new ArrayList<Concept>();
        try {
     
            Concept discontinueReasonsConcept =  getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.discontinue_drug_order_reasons"), new Locale("en", "US"), mu);
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
    

    public static Concept getDefaultDiscontinueReason(MdrtbFactory mu){
        List<Concept> cList = getDiscontinueReasons(mu);
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
   public static boolean isNegativeBacteriology(Obs parentObs, Concept cResultConcept, MdrtbFactory mu){

       String positiveConcepts = Context.getAdministrationService().getGlobalProperty("mdrtb.positive_culture_concepts");
       List<Concept> cList = new ArrayList<Concept>();
       for (StringTokenizer st = new StringTokenizer(positiveConcepts, "|"); st.hasMoreTokens(); ) {
           String s = st.nextToken().trim();
           Concept c = getMDRTBConceptByName(s, new Locale("en", "US"), mu);
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
   public static PatientProgram  transitionToStateNoErrorChecking(PatientProgram pp, ProgramWorkflowState programWorkflowState, Date onDate, MdrtbFactory mu) {

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
       
 
       
       Set<Concept> outcomeConcepts = mu.getMdrProgramOutcomeConcepts();
       if (outcomeConcepts.contains(programWorkflowState.getConcept())) {
    	   pp.setDateCompleted(onDate);
       }
       pp.setUuid(UUID.randomUUID().toString());
       Context.getProgramWorkflowService().savePatientProgram(pp);
       
       Concept diedConcept = mu.getConceptDiedMDR();
       if (programWorkflowState.getConcept().equals(diedConcept)) {
    	   Context.getPatientService().processDeath(pp.getPatient(), onDate, diedConcept, null);
       }
       
       return pp;
   }
   
   
   public static List<Concept> getDstDrugList(boolean removeDuplicates, MdrtbFactory mu){
       String drugList = Context.getAdministrationService().getGlobalProperty("mdrtb.DST_drug_list");
       List<Concept> drugConceptList = new ArrayList<Concept>();
       try {
           Concept c =    MdrtbUtil.getMDRTBConceptByName(drugList, new Locale("en", "US"), mu);
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
   
   public static Concept getConceptFromMDRTBConceptMaps(String name, MdrtbFactory mu){
       MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
       return mu.getMDRTBConceptByKey(name, new Locale("en", "US"), mu.getXmlConceptList());
   }
   
   
   /**
    * 
    * Creates a culture conversion, reconversion, and cleans false obs for a patient;
    * 
    * @param p
    */
   public static void fixCultureConversions(Patient p, MdrtbFactory mu){
       ObsService os = Context.getObsService();
           cleanCultureStatusObs(p, mu);
           Date date = null;
           Map<Obs, Date> map = getCultures(p, mu);
           
           Concept ccConcept = mu.getConceptCultureConversion();
           Concept rcConcept = mu.getConceptCultureReconversion();
           if (map != null && map.size() > 2)
               date = getCultureConversionDate(p, mu);
           if (date != null){
               
             //test if the obs is already created:
               if (ccConcept != null){
                   List<Obs> oList = os.getObservationsByPersonAndConcept(p, ccConcept);
                   boolean createObsTest = true;
                   if (oList != null){
                       for (Obs o:oList){
                           if (o.getValueDatetime().equals(date)){
                               createObsTest = false;
                               break;
                           }    
                       }
                   }
                   if (createObsTest){
                       for (Map.Entry<Obs,Date> e : map.entrySet()) {
                           Date dateInner = e.getValue();
                           if (dateInner.getTime() == date.getTime()){
                               //TODO:  
                               Obs oTmp = new Obs();
                               oTmp.setConcept(ccConcept);
                               oTmp.setCreator(Context.getAuthenticatedUser());
                               oTmp.setDateCreated(new Date());
                               oTmp.setEncounter(e.getKey().getEncounter());
                               oTmp.setLocation(e.getKey().getLocation());
                               oTmp.setObsDatetime(e.getKey().getObsDatetime());
                               oTmp.setPerson(p);
                               oTmp.setValueDatetime(date);
                               oTmp.setVoided(false);
                               
                               try {
                               log.info("Creating culture conversion Obs for patient " + p.getPatientId() + " for date " + oTmp.getValueDatetime());
                               oTmp = os.saveObs(oTmp, "");
                               } catch (Exception ex){
                               log.warn("Failed to create culture conversion Obs for patient", ex);
                               }
                               break;
                           }    
                       }
                    
                   }
               }
           }
           //culture reconversion:
           date = null;
           if (map != null && map.size() > 2)
               date = getReconversionDate(p, mu);
           if (date != null){
               if (rcConcept != null){
                   //test if the obs is already created:
                   List<Obs> oList = os.getObservationsByPersonAndConcept(p, rcConcept);
                   boolean createObsTest = true;
                   if (oList != null){
                       for (Obs o:oList){
                           if (o.getValueDatetime().equals(date)){
                               createObsTest = false;
                               break;
                           }    
                       }
                   }
                   
                   if (createObsTest){
                       for (Map.Entry<Obs,Date> e : map.entrySet()) {
                           Date dateInner = e.getValue();
                           if (dateInner.getTime() == date.getTime()){ 
                               Obs oTmp = new Obs();
                               oTmp.setConcept(rcConcept);
                               oTmp.setCreator(Context.getAuthenticatedUser());
                               oTmp.setDateCreated(new Date());
                               oTmp.setEncounter(e.getKey().getEncounter());
                               oTmp.setLocation(e.getKey().getLocation());
                               oTmp.setObsDatetime(date);
                               oTmp.setPerson(p);
                               oTmp.setValueDatetime(date);
                               oTmp.setVoided(false);
                               
                               try {
                                   log.info("Creating culture reconversion Obs for patient " + p.getPatientId() + " for date " + oTmp.getValueDatetime());
                                   oTmp = os.saveObs(oTmp, "");
                               } catch (Exception ex){
                                   log.warn("Failed to create reculture conversion Obs for patient", ex);
                               }
                               break;
                           }    
                       }
                  
                   }
               }
           }    
         mu.syncCultureStatus(p, os);
             //call smear cleaning here:
         mu.fixSmearConversions(p);
   }
   
   
   /**
    * 
    * Cleans out culture conversion and culture reconversion obs that no longer have cultures that match by date and result
    * 
    * @param p
    */
   public static void cleanCultureStatusObs(Patient p, MdrtbFactory mu){
       ObsService os = Context.getObsService();
       Concept ccConcept = mu.getConceptCultureConversion();
       Concept rcConcept = mu.getConceptCultureReconversion();
       Map<Obs, Date> map = getCultures(p, mu);
       
       List<Obs> ccObs = os.getObservationsByPersonAndConcept(p, ccConcept);
       List<Obs> rcObs = os.getObservationsByPersonAndConcept(p, rcConcept);
       
       //we need functions here that re-evaluate each existing cc obs:
       if (ccObs != null){
           for (Obs o : ccObs){
               if (!isObsValidCultureconversion(o, p, map, mu))
                os.voidObs(o, "no longer a valid culture conversion");   
           }
       }
       map = getCultures(p, mu);
       if (rcObs != null){
           for (Obs o : rcObs){
               if (!isValidReconversion(o, p, map, ccObs, mu))
               os.voidObs(o, "no longer a valid culture reconversion");
           }
       }  
   }
   
   /**
    * 
    * Get cultures ordered by 
    * 
    * @param patient
    * @return
    */
     public static Map<Obs, Date> getCultures(Patient patient, MdrtbFactory mu){
         Map<Obs, Date> map = new LinkedHashMap<Obs,Date>();
         Map<Obs, Date> ret = new LinkedHashMap<Obs,Date>();
         Concept parentConcept = mu.getConceptCultureParent();
         List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(patient, parentConcept);
         List<Date> dates = new ArrayList<Date>();
         if (obsList != null){
             for (Obs o:obsList){
                 Date dateTmp = getSputumCollectionDateCulture(o, mu);
                 if (dateTmp != null){
                     map.put(o, dateTmp);
                     dates.add(dateTmp);
                 }
             }
         }
         Collections.sort(dates);
         for (Date d:dates){
             for (Map.Entry<Obs, Date> ent : map.entrySet()) {
                 if (d.getTime() == ent.getValue().getTime())
                     ret.put(ent.getKey(), ent.getValue());
             }
         }
         return ret;
     }
     
     
     /**
      * 
      * Gets culture conversion date for a patient
      * 
      * @param p
      * @return
      */
     public static Date getCultureConversionDate(Patient p, MdrtbFactory mu){
         Date ret = null;

         Map<Obs,Date> map = getCultures(p, mu);
         Set<Obs> ObsSet = map.keySet();
         List<Obs> obsList = new ArrayList<Obs>();
         Concept cCulture = mu.getConceptCultureResult();
         for (Obs obs:ObsSet)
             obsList.add(obs);
         //ensure that there is an original positive bacteriology;  if not, how can there be conversion?:
         int posOfOriginalPositiveBac = -1;
         for (int i = 0; i < obsList.size(); i++){
             Obs oTmp = obsList.get(i);
             if (!MdrtbUtil.isNegativeBacteriology(oTmp, cCulture, mu)){
                 posOfOriginalPositiveBac = i;
                 break;
             }
         }
         if (obsList.size() > MdrtbUtil.lookupConversionNumberConsecutive() && posOfOriginalPositiveBac >= 0){
             for (int i = obsList.size()-1; i > posOfOriginalPositiveBac + 1; i--){
                 Obs o = obsList.get(i);
                 Date date = map.get(o);
                 if (MdrtbUtil.isNegativeBacteriology(o, cCulture, mu)){
                     // if this culture is negative:
                       int k = i - 1;
                       Calendar calcutoff = Calendar.getInstance();
                       calcutoff.setTime(date);

                       calcutoff.add(Calendar.DAY_OF_MONTH, - (MdrtbUtil.lookupConversionInterval()*MdrtbUtil.lookupConversionNumberConsecutive()));  //60 days = defaulted
                       
                       Calendar calThirty = Calendar.getInstance();
                       calThirty.setTime(date);
                       calThirty.add(Calendar.DAY_OF_MONTH, - (MdrtbUtil.lookupConversionInterval()));
                       Date testDate = date;
                     int numConsecutive = 1;  
                     try {
                         while (k > 0 && testDate.after(calcutoff.getTime())) {
                             Obs oInner = obsList.get(k);
                             Date dateInner = map.get(oInner);
                           
                             
                             if (!MdrtbUtil.isNegativeBacteriology(oInner,cCulture, mu))
                                     break;
                             else {
                                 numConsecutive++; //number of consecutive negatives is now 2
                                 if (dateInner.before(calThirty.getTime()) || dateInner.getTime() == calThirty.getTime().getTime()){
                                     Obs oPrev = obsList.get(k-1);
                                     if (!MdrtbUtil.isNegativeBacteriology(oPrev, cCulture, mu) && numConsecutive >= MdrtbUtil.lookupConversionNumberConsecutive()){
                                         ret = dateInner;
                                         break;   
                                     }
                                 }
                             }
                             testDate = dateInner;
                             k--;
                         }
                     } catch (Exception ex) {}
                 } 
                 if (ret != null)
                     break;
             }
         }
         return ret;
     }
     
     /**
      * returns a reconversion date, and cleans up false culture conversions
      */
      public static Date getReconversionDate(Patient p, MdrtbFactory mu){
         
         Date ret = null;
         Map<Obs,Date> map = getCultures(p, mu);
         List<Obs> oList = Context.getObsService().getObservationsByPersonAndConcept(p, mu.getConceptCultureConversion());      
         Concept cCulture = mu.getConceptCultureResult();
         
         if (oList != null){
             //for the last culture conversion:
             for (int i = oList.size()-1; i >= 0 ; i--){
                 Obs o = oList.get(i);
                 boolean startLooking = false;
                 //for all culture results (asc):
                 for (Map.Entry<Obs,Date> ent : map.entrySet()){
                     if (!startLooking && ent.getValue().after(o.getValueDatetime()))
                         startLooking = true;
                     if (startLooking && !MdrtbUtil.isNegativeBacteriology(ent.getKey(), cCulture, mu)){
                         Calendar ccCal = Calendar.getInstance();                
                         ccCal.setTime(o.getValueDatetime());
                         ccCal.add(Calendar.DAY_OF_MONTH, (MdrtbUtil.lookupConversionInterval() - 1));
                         if (ent.getValue().after(ccCal.getTime())){
                         //if more than 30 day difference from culture conversion date,a positive culture is reconversion
                            return ent.getValue();
                         } else {
                             // if less than 30 days, there was no culture conversion.
                            Context.getObsService().voidObs(o, "Voided by reconversion date routine -- positive culture within 30 days of conversion obs");
                            return null;
                         }
                     }  
                 }
             } 
         }
         return ret;
     }
   
      /**
       * 
       * Returns the sputumCollectionDate out of a DST
       * 
       * @param obs
       * @return
       */
      public static Date getSputumCollectionDateCulture(Obs obs, MdrtbFactory mu){
          Date date = null;
          for (Obs o : obs.getGroupMembers()){
              Concept sputumCollectionConcept = mu.getConceptCultureResult();
              if (o.getConcept().equals(sputumCollectionConcept)){
                  date = o.getValueDatetime();
                  break;
              }    
          }  
          return date;
      }
      
      /**
       * 
       * tests to see if 
       * 
       * @param o the obs to test
       * @param p the patient
       * @param map the results of this.getCultures(patient)
       * @return
       */
      public static boolean isObsValidCultureconversion(Obs o, Patient p, Map<Obs,Date> map, MdrtbFactory mu){
          //map is all cultures
          int numberNeededInARow = MdrtbUtil.lookupConversionNumberConsecutive();
          boolean ret = false;
          Concept cCulture = mu.getConceptCultureResult();
          List<Obs> obsList = new ArrayList<Obs>();      
          for (Obs obs:map.keySet())
              obsList.add(obs);
          
              //make sure there is still an original positive culture
              int posOfOriginalPositiveBac = -1;
                  for (int i = 0; i < obsList.size(); i++){
                      Obs oTmp = obsList.get(i);
                      if (!MdrtbUtil.isNegativeBacteriology(oTmp, cCulture, mu)){
                          posOfOriginalPositiveBac = i;
                          //if the conversion date is before the first positive culture -- return false
                          if (o.getObsDatetime().before(oTmp.getObsDatetime()))
                              return false;
                          break;
                      }
                  }
               //if there are no positives   
               if (posOfOriginalPositiveBac == -1)
                   return false;
          
          //obsList = all culture obs
          if (obsList.size() > numberNeededInARow + 1){
              //get the culture obs that corresponds to the ccObs
              Obs ccObs = null;
              int pos = 0;
              for (Obs otmp : obsList){
                  if (map.get(otmp).getTime() == o.getValueDatetime().getTime()){
                      ccObs = otmp;
                      break;
                  }
                  pos ++;
              }
              if (ccObs == null || !MdrtbUtil.isNegativeBacteriology(ccObs, cCulture, mu) || pos == 0 || MdrtbUtil.isNegativeBacteriology(obsList.get(pos -1) , cCulture, mu) || pos + 1 >= obsList.size()){
                  return false;
              }
              //check if next X are negative (redundant, but quick)
              for (int k = 1; k < numberNeededInARow;k++){
                  if (!MdrtbUtil.isNegativeBacteriology(obsList.get(pos+k), cCulture, mu))
                      return false;
              }     
              //now test the next 30 days:
              Calendar cal = Calendar.getInstance();
              cal.setTime(map.get(ccObs));
              cal.add(Calendar.DAY_OF_MONTH, MdrtbUtil.lookupConversionInterval());
              int count = 1;
              for (Obs oTmp : obsList){
                  if (map.get(oTmp).after(map.get(ccObs))){
                      count++;
                      if (map.get(oTmp).before(cal.getTime()) && !MdrtbUtil.isNegativeBacteriology(oTmp, cCulture, mu)){
                          return false;
                      }
                      else if ((map.get(oTmp).after(cal.getTime()) || map.get(oTmp).getTime() == cal.getTime().getTime()) && MdrtbUtil.isNegativeBacteriology(oTmp, cCulture, mu) && count >= numberNeededInARow){
                          return true;
                      }
                      else if ((map.get(oTmp).after(cal.getTime()) || map.get(oTmp).getTime() == cal.getTime().getTime()) && !MdrtbUtil.isNegativeBacteriology(oTmp, cCulture, mu)){
                          return false;
                      }
                  }
              }  
          } else
              return false;
          return ret;
      }
      
      /**
       * 
       * Tests if an obs is a valid reconversion obs; assumes culture conversions have already been cleaned.
       * 
       * @param o the obs to test
       * @param p patient
       * @param map the results of this.getCultures(patient)
       * @param ccObs a list of all culture conversion obs
       * @return
       */
      public static boolean isValidReconversion(Obs o, Patient p, Map<Obs,Date> map, List<Obs> ccObs, MdrtbFactory mu){
          boolean ret = false;
          Concept cCulture = mu.getConceptCultureResult();
          if (ccObs == null || ccObs.size() == 0)
                  return false;
          Set<Obs> ObsSet = map.keySet();
          List<Obs> obsList = new ArrayList<Obs>();      
          for (Obs obs:ObsSet)
              obsList.add(obs);
          //obsList = all culture obs, and use the map values to get sputum collection dates.
          if (obsList.size() > 3){
              
              Obs rcObs = null;
              int pos = 0;
              for (Obs otmp : obsList){
                  if (map.get(otmp).getTime() == o.getValueDatetime().getTime()){
                      rcObs = otmp;
                      break;
                  }
                  pos ++;
              }
              if (rcObs == null || MdrtbUtil.isNegativeBacteriology(rcObs, cCulture, mu) || pos < 3 || !MdrtbUtil.isNegativeBacteriology(obsList.get(pos -1), cCulture, mu))
                  return false;

              //get previous cultureconversionObs, and see if its valid.
              for (int k = pos-1; k >= 0 ; k -- ){
                  Obs obsPrevious = obsList.get(k);
                  if (!MdrtbUtil.isNegativeBacteriology(obsPrevious, cCulture, mu))
                      return false;
                  if (ccObs.contains(obsPrevious) && isObsValidCultureconversion(o, p, map, mu)){
                      ret = true;
                      break;
                  }
              }
          }    
          return ret;
      }
      
      
}
