package org.openmrs.module.mdrtb.web.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbCultureObj;
import org.openmrs.module.mdrtb.MdrtbDSTObj;
import org.openmrs.module.mdrtb.MdrtbDSTResultObj;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbNewTestObj;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbSmearObj;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.propertyeditor.ObsEditor;
import org.openmrs.propertyeditor.ConceptClassEditor;
import org.openmrs.propertyeditor.ConceptDatatypeEditor;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MdrtbEditTestContainerController extends SimpleFormController{
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    
    
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(org.openmrs.Obs.class,
                new ObsEditor());
        binder.registerCustomEditor(org.openmrs.Concept.class,
                new ConceptEditor());
        NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
        binder.registerCustomEditor(java.lang.Integer.class,
                new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.lang.Double.class,
                new CustomNumberEditor(java.lang.Double.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, 
                new CustomDateEditor(Context.getDateFormat(), true));
        binder.registerCustomEditor(org.openmrs.ConceptClass.class, 
                new ConceptClassEditor());
        binder.registerCustomEditor(org.openmrs.ConceptDatatype.class, 
                new ConceptDatatypeEditor());
        binder.registerCustomEditor(org.openmrs.Location.class, 
                new LocationEditor());
    }
    

    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
        if (Context.isAuthenticated()){
            String action = request.getParameter("action");
            if (action != null && action.equals("") == false)
                map.put("view", action);
            AdministrationService as = Context.getAdministrationService();
            ConceptService cs = Context.getConceptService();
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            MdrtbFactory mu = ms.getMdrtbFactory();
            
            map.put("testNames", this.getDSTTests(as, cs, mu));
            map.put("dstResults", this.getDSTRes(as, cs, mu));  
            map.put("organismTypes", this.getOrganismTypes(as, cs, mu));
            map.put("smearResults", this.getSmearRes(as, cs, mu));
            map.put("cultureResults", this.getCultureRes(as, cs, mu));
            
            Concept red =  MdrtbUtil.getMDRTBConceptByName(as.getGlobalProperty("mdrtb.dst_color_coding_red"), new Locale("en", "US"), mu);
            Concept yellow =  MdrtbUtil.getMDRTBConceptByName(as.getGlobalProperty("mdrtb.dst_color_coding_yellow"), new Locale("en", "US"), mu);
            Concept green =  MdrtbUtil.getMDRTBConceptByName(as.getGlobalProperty("mdrtb.dst_color_coding_green"), new Locale("en", "US"), mu);
            
            map.put("red", red.getBestName(Context.getLocale()).getName());
            map.put("yellow", yellow.getBestName(Context.getLocale()).getName());
            map.put("green", green.getBestName(Context.getLocale()).getName());
            
            
            map.put("none", mu.getConceptNone());
            Concept nonCodedConcept = mu.getConceptOtherMycobacteriaNonCoded();
            
            if (nonCodedConcept != null)
                map.put("OtherNonCodedId", nonCodedConcept.getConceptId());
            else
                map.put("OtherNonCodedId", 0);
            
            String anatLocList = as.getGlobalProperty("mdrtb.anatomical_locations_concept");
            Concept anatLocSet =    MdrtbUtil.getMDRTBConceptByName(anatLocList, new Locale("en", "US"), mu);
            List<Concept> anatomyRetList = new ArrayList<Concept>();
                if (anatLocSet == null)
                        throw new RuntimeException("Could not find concept for anatomical locations global property");
                Collection<ConceptAnswer> cons = anatLocSet.getAnswers(false);
                for (ConceptAnswer c : cons){
                    anatomyRetList.add(c.getAnswerConcept());
                }
                if (anatomyRetList == null || anatomyRetList.size() == 0)
                        log.warn("No concept answers found for anatomical concept.");
            map.put("anatSites", anatomyRetList);

            String cultureMethodList = as.getGlobalProperty("mdrtb.culture_method_concept");
            Concept cultureMethodsSet =    MdrtbUtil.getMDRTBConceptByName(cultureMethodList, new Locale("en", "US"), mu);
            List<Concept> cultureMethList = new ArrayList<Concept>();
                if (cultureMethodsSet == null)
                        throw new RuntimeException("Could not find concept for culture methods global property");
            Collection<ConceptAnswer> cMeth = cultureMethodsSet.getAnswers(false);
            for (ConceptAnswer c: cMeth){
                cultureMethList.add(c.getAnswerConcept());
            }
                if (cultureMethList == null || cultureMethList.size() == 0)
                        log.warn("No anwers found for culture methods concept");
            map.put("cultureMethods", cultureMethList);

            String smearMethodList = as.getGlobalProperty("mdrtb.smear_method_concept");
            Concept smearMethodsSet =    MdrtbUtil.getMDRTBConceptByName(smearMethodList, new Locale("en", "US"), mu);
            List<Concept> smearMethodsList = new ArrayList<Concept>();
                if (smearMethodsSet == null)
                    throw new RuntimeException("Could not find concept for smear methods global property");
            Collection<ConceptAnswer> sMeth = smearMethodsSet.getAnswers(false);
            for (ConceptAnswer c: sMeth){
                smearMethodsList.add(c.getAnswerConcept());
            }
                if (smearMethodsList == null || smearMethodsList.size() == 0)
                        log.warn("No answers found for smear methods concept");
            map.put("smearMethods", smearMethodsList);

            String dstMethodList = as.getGlobalProperty("mdrtb.DST_methods");
            Concept dstMethodsSet =    MdrtbUtil.getMDRTBConceptByName(dstMethodList, new Locale("en", "US"), mu);
            List<Concept> dstMethodsList = new ArrayList<Concept>();
                if (dstMethodsSet == null)
                    throw new RuntimeException("Could not find concept for DST methods global property");
            Collection<ConceptAnswer> dstMeth = dstMethodsSet.getAnswers(false);
            for (ConceptAnswer c: dstMeth){
                dstMethodsList.add(c.getAnswerConcept());
            }
                if (dstMethodsList == null || dstMethodsList.size() == 0)
                        log.warn("No answers found for dst methods concept");
            map.put("dstMethods", dstMethodsList);
            
            String locationsString = as.getGlobalProperty("mdrtb.lab_list"); 
            List<Location> locations = new ArrayList<Location>();
            for (StringTokenizer st = new StringTokenizer(locationsString, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim();
                Location locTmp = Context.getLocationService().getLocation(s);
                locations.add(locTmp);
            }
            if (locations.size() == 0)
                    locations = Context.getLocationService().getAllLocations();
            map.put("locations", locations);
            
            String dateFormat = Context.getDateFormat().toPattern();
            map.put("dateFormat", dateFormat);

            //these strings are formatted for expected input of date widget
            MessageSourceAccessor msa = getMessageSourceAccessor();
            map.put("daysOfWeek", "'" + msa.getMessage("mdrtb.sunday")+ "','" + msa.getMessage("mdrtb.monday")+ "','" + msa.getMessage("mdrtb.tuesday") + "','" + msa.getMessage("mdrtb.wednesday")+ "','" + msa.getMessage("mdrtb.thursday")+ "','" + msa.getMessage("mdrtb.friday")+ "','"
                    + msa.getMessage("mdrtb.saturday")+ "','" + msa.getMessage("mdrtb.sun")+ "','" + msa.getMessage("mdrtb.mon")+ "','"+ msa.getMessage("mdrtb.tues")+ "','"+ msa.getMessage("mdrtb.wed")+ "','"+ msa.getMessage("mdrtb.thurs")+ "','"+ msa.getMessage("mdrtb.fri")+ "','" + msa.getMessage("mdrtb.sat") + "'");
            map.put("monthsOfYear", "'" + msa.getMessage("mdrtb.january")+ "','"+ msa.getMessage("mdrtb.february")+ "','"+ msa.getMessage("mdrtb.march")+ "','"+ msa.getMessage("mdrtb.april")+ "','"+ msa.getMessage("mdrtb.may")+ "','"+ msa.getMessage("mdrtb.june")+ "','"+ msa.getMessage("mdrtb.july")+ "','"+ msa.getMessage("mdrtb.august")+ "','"
                    + msa.getMessage("mdrtb.september")+ "','"+ msa.getMessage("mdrtb.october")+ "','"+ msa.getMessage("mdrtb.november")+ "','"+ msa.getMessage("mdrtb.december")+ "','"+ msa.getMessage("mdrtb.jan")+ "','"+ msa.getMessage("mdrtb.feb")+ "','"+ msa.getMessage("mdrtb.mar")+ "','"+ msa.getMessage("mdrtb.ap")+ "','"+ msa.getMessage("mdrtb.may")+ "','"
                    + msa.getMessage("mdrtb.jun")+ "','"+ msa.getMessage("mdrtb.jul")+ "','"+ msa.getMessage("mdrtb.aug")+ "','"+ msa.getMessage("mdrtb.sept")+ "','"+ msa.getMessage("mdrtb.oct")+ "','"+ msa.getMessage("mdrtb.nov")+ "','"+ msa.getMessage("mdrtb.dec")+ "'");
            List<Role> roles = new Vector<Role>();
            roles.add(Context.getUserService().getRole("Provider"));
            List<User> providers = Context.getUserService().getUsers(null, roles, false);
            map.put("providers", providers);
            map.put("scantyId", mu.getConceptScanty().getConceptId());
            mu = null;
        }
        return map;
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object object,
            BindException exceptions) throws Exception {
        
        Patient patient = new Patient();
        String returnView = "BAC";
        String action = request.getParameter("submit");
        String retType = request.getParameter("retType");
        AdministrationService as = Context.getAdministrationService();
        EncounterService es = Context.getEncounterService();
        MessageSourceAccessor msa = getMessageSourceAccessor();
        UserService us = Context.getUserService();
        boolean clean = false;
        
        Integer numRowsShown = 1;
        String encString = "";
        encString = request.getParameter("encSelect");
        int pos = 0;
        pos = encString.indexOf("|");
        String encStringTmp = "";
        if (pos > 0)
        encStringTmp = encString.substring(0, pos);
        Integer encounterId = null;
        try { encounterId = Integer.parseInt(encStringTmp);
        } catch (Exception ex){log.info("Not able to parse encounterID, creating a new encounter for DST or bacteriology.");}
            //get the control object:
            MdrtbNewTestObj mnto = (MdrtbNewTestObj) object;
            if (mnto.getPatient() != null)
                patient = mnto.getPatient();
            boolean saveTest = false;
            
            //deal with the encounter & location & provider issue:
            
            Set<Encounter> encsToDelete = new HashSet<Encounter>();
            Encounter enc = new Encounter();
            if (encounterId != null){
                enc = Context.getEncounterService().getEncounter(encounterId);
            }

            fixEnc(enc);
            
            User defaultProvider = us.getUserByUsername(as
                    .getGlobalProperty("mdrtb.mdrtb_default_provider"));
            if (enc == null || enc.getEncounterId() == null){
            enc.setCreator(Context.getAuthenticatedUser());
            enc.setDateCreated(new Date());
            enc.setVoided(false);
            enc.setPatient(patient);
            enc.setProvider(defaultProvider);
            }
            
            
            Integer newProviderId = enc.getProvider().getPersonId();
            if (newProviderId == null)
                newProviderId = defaultProvider.getPersonId();
            try {
                String providerIdString = request.getParameter("provider_0");
                newProviderId = Integer.valueOf(providerIdString);
            } catch (Exception ex){}
        
                
                if (msa.getMessage("mdrtb.save").equals(action)) {
                    MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                    MdrtbFactory mu = ms.getMdrtbFactory();
                    for (int i = 0; i < numRowsShown; i ++){
                        MdrtbSmearObj mso = new MdrtbSmearObj();
                        MdrtbCultureObj mco = new MdrtbCultureObj();
                        if (retType.equals("smears")){
                        mso = mnto.getSmears().get(i);
                        if (enc.getEncounterType() == null)
                            enc.setEncounterType(es.getEncounterType(as.getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
                        if (enc.getEncounterType() == null)
                            throw new RuntimeException("EncounterType is null.  Make sure that your global properties for MDRTB encounter types are valid.");

                        }
                        if (retType.equals("cultures")){
                        mco = mnto.getCultures().get(i);
                        if (enc.getEncounterType() == null)
                            enc.setEncounterType(es.getEncounterType(as.getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
                        if (enc.getEncounterType() == null)
                            throw new RuntimeException("EncounterType is null.  Make sure that your global properties for MDRTB encounter types are valid.");
                        
                        }
                        
                        
                        //sputum collection date, a result,
                        if (mso.getSmearResult() != null && mso.getSmearResult().getValueCoded() != null 
                                && mso.getSmearResult().getObsDatetime() != null){

                                
                                Obs parentObs = mso.getSmearParentObs();
                                Encounter oldEnc = parentObs.getEncounter();
                                if (!oldEnc.equals(enc) || enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                  //we need to clone the old encounter, if provider is different
                                    if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                        Encounter newEnc = cloneEncounter(enc);
                                        resetEncOnAllObs(parentObs, newEnc);
                                        newEnc.setProvider(us.getUser(newProviderId));
                                        enc = newEnc;
                                    }
                                    oldEnc.removeObs(parentObs);
                                    encsToDelete.add(oldEnc);
                                    
                                }
                                parentObs.setObsDatetime(new Date());

                                
                                enc.addObs(parentObs);
                                
                                //encounter and obs location stuff:
                                if (parentObs.getLocation() == null && enc.getLocation() != null)
                                parentObs.setLocation(enc.getLocation());
                                else if (parentObs.getLocation() != null && enc.getLocation() == null)
                                    enc.setLocation(parentObs.getLocation());
                                else if (parentObs.getLocation() == null && enc.getLocation() == null){
                                    Location loc = Context.getLocationService().getLocation("Unknown Location");
                                    if (loc == null)
                                        throw new RuntimeException("Please create a default location called 'Unknown Location'");
                                    parentObs.setLocation(loc);
                                    enc.setLocation(loc);
                                }
                                if (enc.getEncounterDatetime() == null)
                                    enc.setEncounterDatetime(mso.getSmearResult().getObsDatetime());
                                
                                saveTest = true;
                                
                                //encounter and obs datetime stuff:
                                Date obsDate = new Date();
                                if (mso.getSmearResult().getObsDatetime() != null)
                                    obsDate = mso.getSmearResult().getObsDatetime();
                                parentObs.setObsDatetime(obsDate);

                                Obs smearResult = mso.getSmearResult();
                                smearResult.setValueDatetime(smearResult.getObsDatetime());
                                if (enc.getEncounterDatetime() == null)
                                    enc.setEncounterDatetime(smearResult.getValueDatetime());
                                smearResult.setLocation(parentObs.getLocation());
                                smearResult.setEncounter(enc);
                                parentObs.addGroupMember(smearResult);
                                
                               
                                
                                Obs bacilli = mso.getBacilli();
                                bacilli.setLocation(parentObs.getLocation());
                                bacilli.setEncounter(enc);
                                bacilli.setObsDatetime(obsDate);
                                if (smearResult.getValueCoded() != null 
                                        && smearResult.getValueCoded().getConceptId().intValue() != mu.getConceptScanty().getConceptId().intValue()
                                        && bacilli.getValueNumeric() != null){
                                    bacilli.setVoided(true);
                                    bacilli.setVoidedBy(Context.getAuthenticatedUser());
                                    bacilli.setDateVoided(new Date());
                                    bacilli.setValueNumeric(null);
                                } else if (bacilli.getValueNumeric() != null)
                                    smearResult.setValueNumeric(bacilli.getValueNumeric());
                                else 
                                    smearResult.setValueNumeric(null);
                                parentObs.addGroupMember(bacilli);

                                
                                Obs source = mso.getSource();
                                if (source.getValueCoded() != null){
                                    source.setLocation(parentObs.getLocation());
                                    source.setEncounter(enc);
                                    source.setObsDatetime(obsDate);
                                    parentObs.addGroupMember(source);
                                }
                                
                                Obs smearResultDate = mso.getSmearResultDate();
                                if (smearResultDate.getValueDatetime() != null){
                                    smearResultDate.setLocation(parentObs.getLocation());
                                    smearResultDate.setEncounter(enc);
                                    smearResultDate.setObsDatetime(obsDate);
                                    parentObs.addGroupMember(smearResultDate);
                                }
                                
                                Obs smearDateReceived = mso.getSmearDateReceived();
                                if (smearDateReceived.getValueDatetime() != null){
                                    smearDateReceived.setLocation(parentObs.getLocation());
                                    smearDateReceived.setEncounter(enc);
                                    smearDateReceived.setObsDatetime(obsDate);
                                    parentObs.addGroupMember(smearDateReceived);
                                }
                                
                                Obs smearMethod = mso.getSmearMethod();
                                if (smearMethod.getValueCoded() != null){
                                    smearMethod.setLocation(parentObs.getLocation());
                                    smearMethod.setEncounter(enc);
                                    smearMethod.setObsDatetime(obsDate);
                                    parentObs.addGroupMember(smearMethod);
                                }

                        }
                        
                        if (mco.getCultureResult() != null && mco.getCultureResult().getValueCoded() != null 
                                && mco.getCultureResult().getObsDatetime() != null){
                            
                            
                            Obs parentObs = mco.getCultureParentObs();
                            Encounter oldEnc = parentObs.getEncounter();
                            if (!oldEnc.equals(enc) || enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                //we need to clone the old encounter, if provider is different
                                  if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                      Encounter newEnc = cloneEncounter(enc);
                                      resetEncOnAllObs(parentObs, newEnc);
                                      newEnc.setProvider(us.getUser(newProviderId));
                                      enc = newEnc;
                                  }
                                  oldEnc.removeObs(parentObs);
                                  encsToDelete.add(oldEnc);
                                  
                              }
                            enc.addObs(parentObs);
                            if (parentObs.getLocation() == null && enc.getLocation() != null)
                            parentObs.setLocation(enc.getLocation());
                            else if (parentObs.getLocation() != null && enc.getLocation() == null)
                                enc.setLocation(parentObs.getLocation());
                            else if (parentObs.getLocation() == null && enc.getLocation() == null){
                                Location loc = Context.getLocationService().getLocation("Unknown Location");
                                if (loc == null)
                                    throw new RuntimeException("Please create a default location called 'Unknown Location'");
                                parentObs.setLocation(loc);
                                enc.setLocation(loc);
                            }
                            if (enc.getEncounterDatetime() == null)
                                enc.setEncounterDatetime(mco.getCultureResult().getObsDatetime());
                            saveTest = true;
                            
                          //encounter and obs datetime stuff:
                            Date obsDate = new Date();
                            if (mco.getCultureResult().getObsDatetime() != null)
                                obsDate = mco.getCultureResult().getObsDatetime();
                            parentObs.setObsDatetime(obsDate);
                          //now, add the child obs to the parent if their answers are not null:
                            Obs cultureResult = mco.getCultureResult();
                            cultureResult.setValueDatetime(cultureResult.getObsDatetime());
                            if (enc.getEncounterDatetime() == null)
                                enc.setEncounterDatetime(cultureResult.getValueDatetime());
                            cultureResult.setLocation(parentObs.getLocation());
                            cultureResult.setEncounter(enc);
                            parentObs.addGroupMember(cultureResult);
                           
                            
                            Obs colonies = mco.getColonies();
                            colonies.setLocation(parentObs.getLocation());
                            colonies.setEncounter(enc);
                            colonies.setObsDatetime(obsDate);
                            if (cultureResult.getValueCoded() != null 
                                    && cultureResult.getValueCoded().getConceptId().intValue() != mu.getConceptScanty().getConceptId().intValue()){
                                colonies.setVoided(true);
                                colonies.setVoidedBy(Context.getAuthenticatedUser());
                                colonies.setDateVoided(new Date());
                                colonies.setValueNumeric(null);
                                cultureResult.setValueNumeric(null);
                            } else if (colonies.getValueNumeric() != null)
                                cultureResult.setValueNumeric(colonies.getValueNumeric());
                            else 
                                cultureResult.setValueNumeric(null);
                            
                            parentObs.addGroupMember(colonies);
                            
                            Obs source = mco.getSource();
                            if (source.getValueCoded() != null){
                                source.setLocation(parentObs.getLocation());
                                source.setEncounter(enc);
                                source.setObsDatetime(obsDate);
                                parentObs.addGroupMember(source);
                            }
                            
                            Obs cultureStartDate = mco.getCultureStartDate();
                            if (cultureStartDate.getValueDatetime() != null){
                                cultureStartDate.setLocation(parentObs.getLocation());
                                cultureStartDate.setEncounter(enc);
                                cultureStartDate.setObsDatetime(obsDate);
                                parentObs.addGroupMember(cultureStartDate);
                                
                            }
                            
                            Obs cultureResultsDate = mco.getCultureResultsDate();
                            if (cultureResultsDate.getValueDatetime() != null){
                                cultureResultsDate.setLocation(parentObs.getLocation());
                                cultureResultsDate.setEncounter(enc);
                                cultureResultsDate.setObsDatetime(obsDate);
                                parentObs.addGroupMember(cultureResultsDate);
                            }
                            
                            Obs cultureDateReceived = mco.getCultureDateReceived();
                            if (cultureDateReceived.getValueDatetime() != null){
                                cultureDateReceived.setLocation(parentObs.getLocation());
                                cultureDateReceived.setEncounter(enc);
                                cultureDateReceived.setObsDatetime(obsDate);
                                parentObs.addGroupMember(cultureDateReceived);
                            }
                            
                            Obs cultureMethod = mco.getCultureMethod();
                            if (cultureMethod.getValueCoded() != null){
                                cultureMethod.setLocation(parentObs.getLocation());
                                cultureMethod.setEncounter(enc);
                                cultureMethod.setObsDatetime(obsDate);
                                parentObs.addGroupMember(cultureMethod);
                            }
                            
                            Obs typeOfOrganism = mco.getTypeOfOrganism();
                            if (typeOfOrganism.getValueCoded() != null){
                                typeOfOrganism.setLocation(parentObs.getLocation());
                                typeOfOrganism.setEncounter(enc);
                                typeOfOrganism.setObsDatetime(obsDate);
                                parentObs.addGroupMember(typeOfOrganism);
                            }
                            
                            Obs typeOfOrganismNonCoded = mco.getTypeOfOrganismNonCoded();
                            if (typeOfOrganismNonCoded.getValueText() != null){
                                typeOfOrganismNonCoded.setLocation(parentObs.getLocation());
                                typeOfOrganismNonCoded.setEncounter(enc);
                                typeOfOrganismNonCoded.setObsDatetime(obsDate);
                                parentObs.addGroupMember(typeOfOrganismNonCoded);
                            }
                           clean = true;
                        }
                    }

                    
                   
                    for (int i = 0; i < numRowsShown; i ++){
                        MdrtbDSTObj dst = new MdrtbDSTObj();
                        if (retType.equals("dsts")){
                            dst = mnto.getDsts().get(i);
                            returnView = "DST";
                            if (enc.getEncounterType() == null)
                                enc.setEncounterType(es.getEncounterType(as.getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
                            if (enc.getEncounterType() == null)
                                throw new RuntimeException("EncounterType is null.  Make sure that your global properties for MDRTB encounter types are valid.");

                        }    
                        
                    //figure out if its worth doing anything with this DST:
                        List<MdrtbDSTResultObj> dstResObs = dst.getDstResults();
                        boolean worthSaving = false;
                        for (MdrtbDSTResultObj res: dstResObs){
                                if (res.getDrug().getConcept() != null || res.getDrug().getObsId() != null){
                                    worthSaving = true;
                                break;
                                }
                        }

                        //we're requiring at least one drug with a response, and a sputum collection date
                        Obs parentObs = dst.getDstParentObs();
                        Encounter oldEnc = parentObs.getEncounter();
                        if (dst.getSputumCollectionDate().getValueDatetime() != null && worthSaving ){
                            
                            if (!oldEnc.equals(enc) || enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                //we need to clone the old encounter, if provider is different
                                  if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                      Encounter newEnc = cloneEncounter(enc);
                                      resetEncOnAllObs(parentObs, newEnc);
                                      newEnc.setProvider(us.getUser(newProviderId));
                                      enc = newEnc;
                                  }
                                  oldEnc.removeObs(parentObs);
                                  encsToDelete.add(oldEnc);
                                  
                            }
                            enc.addObs(parentObs);
                            if (parentObs.getLocation() == null && enc.getLocation() != null)
                            parentObs.setLocation(enc.getLocation());
                            else if (parentObs.getLocation() != null && enc.getLocation() == null)
                                enc.setLocation(parentObs.getLocation());
                            else if (parentObs.getLocation() == null && enc.getLocation() == null){
                                Location loc = Context.getLocationService().getLocation("Unknown Location");
                                if (loc == null)
                                    throw new RuntimeException("Please create a default location called 'Unknown Location'");
                                parentObs.setLocation(loc);
                                enc.setLocation(loc);
                            }
                            if (enc.getEncounterDatetime() == null)
                                enc.setEncounterDatetime(dst.getSputumCollectionDate().getValueDatetime());
                            saveTest = true;
                            
                          //encounter and obs datetime stuff:
                            Date obsDate = new Date();
                            if (dst.getSputumCollectionDate().getValueDatetime() != null)
                                obsDate = dst.getSputumCollectionDate().getValueDatetime();
                            else 
                                obsDate = dst.getDstResultsDate().getValueDatetime();
                            parentObs.setObsDatetime(obsDate);
                            
                            //create the obs tree before saving:
                            Obs sputumCollectionDate = dst.getSputumCollectionDate();
                            if (enc.getEncounterDatetime() == null)
                                enc.setEncounterDatetime(sputumCollectionDate.getValueDatetime());
                            sputumCollectionDate.setLocation(parentObs.getLocation());
                            sputumCollectionDate.setEncounter(enc);
                            sputumCollectionDate.setObsDatetime(sputumCollectionDate.getValueDatetime());
                            parentObs.addGroupMember(sputumCollectionDate);
                            
                            Obs typeOfOrganism = dst.getTypeOfOrganism();
                            if (typeOfOrganism.getValueCoded() != null){
                                typeOfOrganism.setLocation(parentObs.getLocation());
                                typeOfOrganism.setEncounter(enc);
                                typeOfOrganism.setObsDatetime(obsDate);
                                parentObs.addGroupMember(typeOfOrganism);
                            }
                            
                            Obs typeOfOrganismNonCoded = dst.getTypeOfOrganismNonCoded();
                            if (typeOfOrganismNonCoded.getValueText() != null){
                                typeOfOrganismNonCoded.setLocation(parentObs.getLocation());
                                typeOfOrganismNonCoded.setEncounter(enc);
                                typeOfOrganismNonCoded.setObsDatetime(obsDate);
                                parentObs.addGroupMember(typeOfOrganismNonCoded);
                            }
                            
                            Obs drugSensitivityTestComplete = dst.getDrugSensitivityTestComplete();
                            if (drugSensitivityTestComplete.getValueAsBoolean() != null){
                                drugSensitivityTestComplete.setLocation(parentObs.getLocation());
                                drugSensitivityTestComplete.setEncounter(enc);
                                drugSensitivityTestComplete.setObsDatetime(obsDate);
                                parentObs.addGroupMember(drugSensitivityTestComplete);
                            }
                            
                            Obs dstStartDate = dst.getDstStartDate();
                            if (dstStartDate.getValueDatetime() != null){
                                dstStartDate.setLocation(parentObs.getLocation());
                                dstStartDate.setEncounter(enc);
                                dstStartDate.setObsDatetime(obsDate);
                                parentObs.addGroupMember(dstStartDate);
                            }
                            
                            Obs dstResultsDate = dst.getDstResultsDate();
                            if (dstResultsDate.getValueDatetime() != null){
                                dstResultsDate.setLocation(parentObs.getLocation());
                                dstResultsDate.setEncounter(enc);
                                dstResultsDate.setObsDatetime(obsDate);
                                parentObs.addGroupMember(dstResultsDate);
                            }
                            
                            Obs dstDateReceived = dst.getDstDateReceived();
                            if (dstDateReceived.getValueDatetime() != null){
                                dstDateReceived.setLocation(parentObs.getLocation());
                                dstDateReceived.setEncounter(enc);
                                dstDateReceived.setObsDatetime(obsDate);
                                parentObs.addGroupMember(dstDateReceived);
                            }
                            
                            Obs source = dst.getSource();
                            if (source.getValueCoded() != null){
                                source.setLocation(parentObs.getLocation());
                                source.setEncounter(enc);
                                source.setObsDatetime(obsDate);
                                parentObs.addGroupMember(source);
                            }
                            
                            Obs dstMethod = dst.getDstMethod();
                            if (dstMethod.getValueCoded() != null){
                                dstMethod.setLocation(parentObs.getLocation());
                                dstMethod.setEncounter(enc);
                                dstMethod.setObsDatetime(obsDate);
                                parentObs.addGroupMember(dstMethod);
                            }
                            
                            
                            Obs directOrIndirect = dst.getDirectOrIndirect();
                            if (directOrIndirect.getValueAsBoolean()!= null){
                                directOrIndirect.setLocation(parentObs.getLocation());
                                directOrIndirect.setEncounter(enc);
                                directOrIndirect.setObsDatetime(obsDate);
                                parentObs.addGroupMember(directOrIndirect);
                            }
                            
                            Obs  coloniesInControl = dst.getColoniesInControl();
                            if (coloniesInControl.getValueNumeric()!= null){
                                coloniesInControl.setLocation(parentObs.getLocation());
                                coloniesInControl.setEncounter(enc);
                                coloniesInControl.setObsDatetime(obsDate);
                                parentObs.addGroupMember(coloniesInControl);
                            }
                            
                            
                            for (MdrtbDSTResultObj res: dstResObs){
                                if (res.getDrug().getConcept() != null && !res.getDrug().getConcept().getConceptId().equals(mu.getConceptNone().getConceptId())){
                                    
                                    Obs dstParentResultObs = res.getDstResultParentObs();
                                    dstParentResultObs.setLocation(parentObs.getLocation());
                                    dstParentResultObs.setEncounter(enc);
                                    dstParentResultObs.setObsDatetime(obsDate);
                                    parentObs.addGroupMember(dstParentResultObs);
                                    
                                   Obs drug = res.getDrug();
                                   if (drug.getConcept() != null){
                                       drug.setLocation(parentObs.getLocation());
                                       drug.setEncounter(enc);
                                       drug.setObsDatetime(obsDate);
                                       dstParentResultObs.addGroupMember(drug);
                                   }
                                   
                                   Obs colonies = res.getColonies();
                                   if (colonies.getValueNumeric() != null){
                                       colonies.setLocation(parentObs.getLocation());
                                       colonies.setEncounter(enc);
                                       colonies.setObsDatetime(obsDate);
                                       dstParentResultObs.addGroupMember(colonies);
                                   }
                                   
                                   Obs concentration = res.getConcentration();
                                   if (concentration.getValueNumeric() != null){
                                       concentration.setLocation(parentObs.getLocation());
                                       concentration.setEncounter(enc);
                                       concentration.setObsDatetime(obsDate);
                                       dstParentResultObs.addGroupMember(concentration);
                                   }
                                } else if (res.getDrug().getConcept() != null && res.getDrug().getConcept().getConceptId().equals(mu.getConceptNone().getConceptId()) && res.getDrug().getObsId() != null){
                                    Obs dstParentResultObs = res.getDstResultParentObs();
                                    dstParentResultObs.setVoided(true);
                                    dstParentResultObs.setVoidedBy(Context.getAuthenticatedUser());
                                    dstParentResultObs.setVoidReason("DST test nulled");
                                    dstParentResultObs.setDateVoided(new Date());
                                    
                                    Obs drug = res.getDrug();
                                    if (drug.getObsId() != null){
                                        //Integer tmp = drug.getObsId();
                                        //HERE
                                        //TODO: ooof. the following 5 lines suck:  we need to use sessions.getCurrentSession.evict(drug) to get at the saved value
//                                        Concept concept  =    MdrtbUtil.getMDRTBConceptByName("UNKNOWN", new Locale("en"));
//                                        if (concept == null)
//                                            concept =    MdrtbUtil.getMDRTBConceptByName("NONE", new Locale("en"));
//                                        if (concept == null)
//                                            throw new RuntimeException("Please create a generic concept called either UNKNOWN or NONE");
//                                        drug.setConcept(concept);

                                        drug.setVoided(true);
                                        drug.setVoidedBy(Context.getAuthenticatedUser());
                                        drug.setVoidReason("DST test nulled");
                                        drug.setDateVoided(new Date());
                                    }
                                    
                                    Obs colonies = res.getColonies();
                                    if (colonies.getObsId() != null){
                                        colonies.setVoided(true);
                                        colonies.setVoidedBy(Context.getAuthenticatedUser());
                                        colonies.setVoidReason("DST test nulled");
                                        colonies.setDateVoided(new Date());
                                    }
                                    
                                    Obs concentration = res.getConcentration();
                                    if (concentration.getObsId() != null){
                                        concentration.setVoided(true);
                                        concentration.setVoidedBy(Context.getAuthenticatedUser());
                                        concentration.setVoidReason("DST test nulled");
                                        concentration.setDateVoided(new Date());
                                    }
                                }
                                
                            }
                            List<MdrtbDSTResultObj> dstResObsTmp = dst.getDstResults();
                            boolean killParent = true;
                            for (MdrtbDSTResultObj res: dstResObsTmp){
                                    if (res.getDrug().getConcept() != null && !res.getDrug().getVoided()){
                                        killParent = false;
                                    break;
                                    }
                            }
                            if (killParent){
                                Context.getObsService().voidObs(parentObs, "DST had no results"); 
                            }
                        }
                    }
                    if (clean)
                        MdrtbUtil.fixCultureConversions(patient, mu);
                } 
                if (msa.getMessage("mdrtb.delete").equals(action)) {
                    Integer parentObsId = null;
                    if (retType.equals("smears")){
                        MdrtbSmearObj mso = mnto.getSmears().get(0);
                        parentObsId = mso.getSmearParentObs().getObsId();
                        
                        returnView = "BAC";
                    }    
                    if (retType.equals("cultures")){
                        MdrtbCultureObj mco = mnto.getCultures().get(0);
                        parentObsId = mco.getCultureParentObs().getObsId();
                        returnView = "BAC";
                        clean = true;
                    }
                    if (retType.equals("dsts")){
                        MdrtbDSTObj dst = mnto.getDsts().get(0);
                        parentObsId = dst.getDstParentObs().getObsId();
                        returnView = "DST";
                    }    
                    if (parentObsId  != null){
                        //TODO:  one of the mnto actions isn't necessary..., test and clean
                        Context.evictFromSession(mnto);
                        Context.evictFromSession(object);
                        mnto = null;
                        Obs parentObs = Context.getObsService().getObs(parentObsId);
                        Context.getObsService().voidObs(parentObs, "mdrtb");
                        saveTest = true;
                    }
                    if (clean){
                        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                        MdrtbFactory mu = ms.getMdrtbFactory();
                        MdrtbUtil.fixCultureConversions(patient, mu);
                    } 
                    
                }
                if (msa.getMessage("mdrtb.cancel").equals(action)) {
                    if (retType.equals("smears"))
                        returnView = "BAC";
                    if (retType.equals("cultures"))
                        returnView = "BAC";
                    if (retType.equals("dsts"))
                        returnView = "DST";  
                }
                if (saveTest){
                        //set uuids
                        fixEnc(enc);    
                        //for lazy loading
                        es.saveEncounter(enc);
                        for (Encounter oldEnc: encsToDelete){
                            //for lazy loading
                            oldEnc.getOrders();
                            Context.getEncounterService().saveEncounter(oldEnc);
                            if (oldEnc.getAllObs() == null || oldEnc.getAllObs().size() == 0){
                                Context.getEncounterService().voidEncounter(oldEnc, "no obs...");
                            }
                        }
                }     
                //cleanup culture conversion obs
                 
        RedirectView rv = new RedirectView(getSuccessView());
        rv.addStaticAttribute("patientId", patient.getPatientId());
        rv.addStaticAttribute("view", returnView);
        return new ModelAndView(rv);
    }
   
    
    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {

        
        if (Context.isAuthenticated()) {
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            MdrtbFactory mu = ms.getMdrtbFactory();
            String obsGroupId = request.getParameter("ObsGroupId");
            MdrtbNewTestObj mnto = new MdrtbNewTestObj();
            Obs parentObs = new Obs();
            User user = Context.getAuthenticatedUser();
            try {
                parentObs = Context.getObsService().getObs(Integer.valueOf(obsGroupId));
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Could not convert obsGroupId request parameter to an Integer.  You should only be able to access this page from the DST or bacteriology table widgets.",ex);
            }
            Set<Obs> levelTwoObs = parentObs.getGroupMembers();
            Encounter enc = parentObs.getEncounter();
            Patient patient = enc.getPatient();
            mnto.setPatient(patient);
            for (Encounter encs: Context.getEncounterService().getEncountersByPatient(patient)){
                mnto.getEncounters().add(encs);
            }
            // find out what we're editing:

            if (parentObs.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DST_PARENT()) && !parentObs.isVoided()) {
                // DST
                MdrtbDSTObj mdo = new MdrtbDSTObj();
                mdo.setDstParentObs(parentObs);
                List<Concept> drugConceptList = MdrtbUtil.getDstDrugList(false, mu);
                
                Set<MdrtbDSTResultObj> alreadyShownResults = new HashSet<MdrtbDSTResultObj>();
                
                for (Obs o : levelTwoObs){
                    if (!o.isVoided()){
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_COLONIES_IN_CONTROL())&& !o.getVoided())
                            mdo.setColoniesInControl(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DIRECT_INDIRECT())&& !o.getVoided())
                            mdo.setDirectOrIndirect(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DST_COMPLETE())&& !o.getVoided())
                            mdo.setDrugSensitivityTestComplete(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DATE_RECEIVED())&& !o.getVoided())
                            mdo.setDstDateReceived(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DST_METHOD())&& !o.getVoided())
                            mdo.setDstMethod(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_RESULT_DATE())&& !o.getVoided())
                            mdo.setDstResultsDate(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_CULTURE_START_DATE())&& !o.getVoided())
                            mdo.setDstStartDate(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_SAMPLE_SOURCE()) && !o.getVoided())
                            mdo.setSource(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_SPUTUM_COLLECTION_DATE()) && !o.getVoided())
                            mdo.setSputumCollectionDate(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TYPE_OF_ORGANISM()) && !o.getVoided())
                            mdo.setTypeOfOrganism(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TYPE_OF_ORGANISM_NON_CODED()) && !o.getVoided())
                            mdo.setTypeOfOrganismNonCoded(o);
                        if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DST_RESULT_PARENT()) && !o.getVoided()){
                           
                           
                            MdrtbDSTResultObj mdro = new MdrtbDSTResultObj();
                            
                                
                            Set<Obs> thirdLevelObs = o.getGroupMembers();
                            
                            for (Obs oInner : thirdLevelObs){
                                if (!oInner.isVoided()){
                                    if (oInner.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_COLONIES())&& !o.getVoided())
                                        mdro.setColonies(oInner);
                                    if (oInner.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_CONCENTRATION())&& !o.getVoided())
                                        mdro.setConcentration(oInner);
                                    
                                    //if in concept list, add obs 
                                    for (Concept drugConcept : drugConceptList){
                                        if (oInner.getValueCoded() != null && drugConcept.getConceptId().intValue() == oInner.getValueCoded().getConceptId().intValue()){
                                            mdro.setDrug(oInner);
                                            break;
                                        }
                                    }
                                }     
                            }
                            
                            if (mdro.getColonies() == null){
                                Obs colonies = new Obs();
                                colonies.setConcept( mu.getConceptColonies()); 
                                colonies.setVoided(false);
                                colonies.setDateCreated(new Date());
                                colonies.setPerson(patient);
                                colonies.setCreator(user);
                                colonies.setEncounter(enc);
                                colonies.setObsDatetime(parentObs.getObsDatetime());
                                mdro.setColonies(colonies);
                            }
                            if (mdro.getConcentration() == null){
                                Obs conc = new Obs();
                                conc.setConcept(  mu.getConceptConcentration()); 
                                conc.setVoided(false);
                                conc.setDateCreated(new Date());
                                conc.setPerson(patient);
                                conc.setCreator(user);
                                conc.setEncounter(enc);
                                conc.setObsDatetime(parentObs.getObsDatetime());
                                mdro.setConcentration(conc);
                            }
                            
                            if (mdro.getDrug() != null && !alreadyShownResults.contains(mdro)){
                                mdro.setDstResultParentObs(o);
                                mdo.getDstResults().add(mdro);
                                alreadyShownResults.add(mdro);
                            }  else {
                                log.error("HERE's a dst parent with some children but no attached drug obs:" + o.getObsId());
                            }
                        }        
                    }    
                }
                if (mdo.getColoniesInControl().getObsId() == null){
                    Obs cc = new Obs();
                    cc.setConcept( mu.getConceptColoniesInControl()); 
                    cc.setVoided(false);
                    cc.setDateCreated(new Date());
                    cc.setPerson(patient);
                    cc.setCreator(user);
                    cc.setEncounter(enc);
                    cc.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setColoniesInControl(cc);
                }
                if (mdo.getDirectOrIndirect().getObsId() == null){
                    Obs dInd = new Obs();
                    dInd.setConcept(   mu.getConceptDirectIndirect()); 
                    dInd.setVoided(false);
                    dInd.setDateCreated(new Date());
                    dInd.setPerson(patient);
                    dInd.setCreator(user);
                    dInd.setEncounter(enc);
                    dInd.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setDirectOrIndirect(dInd);
                }
                if (mdo.getDrugSensitivityTestComplete().getObsId() == null){
                    Obs dC = new Obs();
                    dC.setConcept(   mu.getConceptDSTComplete()); 
                    dC.setVoided(false);
                    dC.setDateCreated(new Date());
                    dC.setPerson(patient);
                    dC.setCreator(user);
                    dC.setEncounter(enc);
                    dC.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setDrugSensitivityTestComplete(dC);
                }
                if (mdo.getDstDateReceived().getObsId() == null){
                    Obs dR = new Obs();
                    dR.setConcept(   mu.getConceptDateReceived()); 
                    dR.setVoided(false);
                    dR.setDateCreated(new Date());
                    dR.setPerson(patient);
                    dR.setCreator(user);
                    dR.setEncounter(enc);
                    dR.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setDstDateReceived(dR);
                }
                if (mdo.getDstMethod().getObsId() == null){
                    Obs method = new Obs();
                    method.setConcept(   mu.getConceptDSTMethod()); 
                    method.setVoided(false);
                    method.setDateCreated(new Date());
                    method.setPerson(patient);
                    method.setCreator(user);
                    method.setEncounter(enc);
                    method.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setDstMethod(method);
                }
                if (mdo.getDstResultsDate().getObsId() == null){
                    Obs dRD = new Obs();
                 
                    dRD.setConcept(  mu.getConceptResultDate()); 
                    dRD.setVoided(false);
                    dRD.setDateCreated(new Date());
                    dRD.setPerson(patient);
                    dRD.setCreator(user);
                    dRD.setEncounter(enc);
                    dRD.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setDstResultsDate(dRD);
                }
                if (mdo.getDstStartDate().getObsId() == null){
                    Obs dSD = new Obs();
                    
                    dSD.setConcept(mu.getConceptCultureStartDate()); 
                    dSD.setVoided(false);
                    dSD.setDateCreated(new Date());
                    dSD.setPerson(patient);
                    dSD.setCreator(user);
                    dSD.setEncounter(enc);
                    dSD.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setDstStartDate(dSD);
                }
                if (mdo.getSource().getObsId() == null){
                    Obs source = new Obs();
                    
                    source.setConcept(mu.getConceptSampleSource()); 
                    source.setVoided(false);
                    source.setDateCreated(new Date());
                    source.setPerson(patient);
                    source.setCreator(user);
                    source.setEncounter(enc);
                    source.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setSource(source);
                }
                if (mdo.getSputumCollectionDate().getObsId() == null){
                    Obs sCD = new Obs();
                    
                    sCD.setConcept(mu.getConceptSputumCollectionDate()); 
                    sCD.setVoided(false);
                    sCD.setDateCreated(new Date());
                    sCD.setPerson(patient);
                    sCD.setCreator(user);
                    sCD.setEncounter(enc);
                    sCD.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setSputumCollectionDate(sCD);
                }
                if (mdo.getTypeOfOrganism().getObsId() == null){
                    Obs type = new Obs();
                    
                    type.setConcept(mu.getConceptTypeOfOrganism()); 
                    type.setVoided(false);
                    type.setDateCreated(new Date());
                    type.setPerson(patient);
                    type.setCreator(user);
                    type.setEncounter(enc);
                    type.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setTypeOfOrganism(type);
                }
                if (mdo.getTypeOfOrganismNonCoded().getObsId() == null){
                    Obs typeOther = new Obs();
                    
                    typeOther.setConcept(mu.getConceptTypeOfOrganismNonCoded()); 
                    typeOther.setVoided(false);
                    typeOther.setDateCreated(new Date());
                    typeOther.setPerson(patient);
                    typeOther.setCreator(user);
                    typeOther.setEncounter(enc);
                    typeOther.setObsDatetime(parentObs.getObsDatetime());
                    mdo.setTypeOfOrganismNonCoded(typeOther);
                }
                
                //lastly, add DST result obj for all non-used drugs:
                for (Concept c : drugConceptList){
                    //HERE
                    if (!mdoHasBeenCreatedForConcept(drugConceptList, c, mdo)){
                        MdrtbDSTResultObj mdro = new MdrtbDSTResultObj();
                        
                        Obs drug = new Obs();
                        drug.setDateCreated(new Date());
                        drug.setVoided(false);
                        drug.setValueCoded(c);
                        drug.setCreator(user);
                        drug.setPerson(patient);
                        mdro.setDrug(drug);
                        
                        Obs concentration = new Obs();
                        concentration.setDateCreated(new Date());
                        concentration.setVoided(false);
                        
                        concentration.setConcept(mu.getConceptConcentration());
                        concentration.setCreator(user);
                        concentration.setPerson(patient);
                        mdro.setConcentration(concentration);
                        
                        Obs colonies = new Obs();
                        
                        colonies.setConcept(mu.getConceptColonies()); 
                        colonies.setVoided(false);
                        colonies.setDateCreated(new Date());
                        colonies.setCreator(user);
                        colonies.setPerson(patient);
                        mdro.setColonies(colonies);
                        
                        Obs dstResultParentObs = new Obs();
                        
                        dstResultParentObs.setConcept(mu.getConceptDSTResultParent());
                        dstResultParentObs.setVoided(false);
                        dstResultParentObs.setDateCreated(new Date());
                        dstResultParentObs.setCreator(user);
                        dstResultParentObs.setPerson(patient);
                        mdro.setDstResultParentObs(dstResultParentObs);
                        
                        mdo.addDstResult(mdro);
                    }
                    
                }
                
                /*
                 * re-order DST tests according to global property:
                 */
                Set<MdrtbDSTResultObj> used = new HashSet<MdrtbDSTResultObj>();
                List<MdrtbDSTResultObj> newOrderList = new ArrayList<MdrtbDSTResultObj>();
                for (Concept drugConcept:drugConceptList){
                    for (MdrtbDSTResultObj oToOrder : mdo.getDstResults()){
                        
                        if (oToOrder.getDrug().getValueCoded().equals(drugConcept) && !used.contains(oToOrder)){
                            newOrderList.add(oToOrder);
                            used.add(oToOrder);
                        }
                    }
                }
                mdo.setDstResults(newOrderList);
                
                mnto.addDST(mdo);
            } else if (parentObs.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_SMEAR_PARENT()) && !parentObs.getVoided()) {
                // SMEAR
                MdrtbSmearObj mso = new MdrtbSmearObj();
                mso.setSmearParentObs(parentObs);
                for (Obs o : levelTwoObs){
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_BACILLI())&& !o.getVoided()){
                        mso.setBacilli(o);
                    }    
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DATE_RECEIVED())&& !o.getVoided())
                        mso.setSmearDateReceived(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_SMEAR_MICROSCOPY_METHOD())&& !o.getVoided())
                        mso.setSmearMethod(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_SMEAR_RESULT())&& !o.getVoided()){
                        mso.setSmearResult(o);
                    }
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_RESULT_DATE())&& !o.getVoided())
                        mso.setSmearResultDate(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_SAMPLE_SOURCE())&& !o.getVoided())
                        mso.setSource(o); 
                }
                if (mso.getBacilli().getObsId() == null){
                    Obs bacilli = new Obs();
                    
                    bacilli.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_BACILLI(), new Locale("en", "US"), mu)); 
                    bacilli.setVoided(false);
                    bacilli.setDateCreated(new Date());
                    bacilli.setPerson(patient);
                    bacilli.setCreator(user);
                    bacilli.setEncounter(enc);
                    bacilli.setObsDatetime(parentObs.getObsDatetime());
                    mso.setBacilli(bacilli);
                }
                if (mso.getSmearDateReceived().getObsId() == null){
                    Obs sDR = new Obs();
                    
                    sDR.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_DATE_RECEIVED(), new Locale("en", "US"), mu)); 
                    sDR.setVoided(false);
                    sDR.setDateCreated(new Date());
                    sDR.setPerson(patient);
                    sDR.setCreator(user);
                    sDR.setEncounter(enc);
                    sDR.setObsDatetime(parentObs.getObsDatetime());
                    mso.setSmearDateReceived(sDR);
                }
                if (mso.getSmearMethod().getObsId() == null){
                    Obs sMeth = new Obs();
                    
                    sMeth.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TB_SMEAR_MICROSCOPY_METHOD(), new Locale("en", "US"), mu)); 
                    sMeth.setVoided(false);
                    sMeth.setDateCreated(new Date());
                    sMeth.setPerson(patient);
                    sMeth.setCreator(user);
                    sMeth.setEncounter(enc);
                    sMeth.setObsDatetime(parentObs.getObsDatetime());
                    mso.setSmearMethod(sMeth);
                }
                if (mso.getSmearResult().getObsId() == null){
                    Obs sRes = new Obs();
                    
                    sRes.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TB_SMEAR_RESULT(), new Locale("en", "US"), mu)); 
                    sRes.setVoided(false);
                    sRes.setDateCreated(new Date());
                    sRes.setPerson(patient);
                    sRes.setCreator(user);
                    sRes.setEncounter(enc);
                    sRes.setObsDatetime(parentObs.getObsDatetime());
                    mso.setSmearResult(sRes);
                }
                if (mso.getSmearResultDate().getObsId() == null){
                    Obs sResDate = new Obs();
                    
                    sResDate.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_RESULT_DATE(), new Locale("en", "US"), mu)); 
                    sResDate.setVoided(false);
                    sResDate.setDateCreated(new Date());
                    sResDate.setPerson(patient);
                    sResDate.setCreator(user);
                    sResDate.setEncounter(enc);
                    sResDate.setObsDatetime(parentObs.getObsDatetime());
                    mso.setSmearResultDate(sResDate);
                }
                if (mso.getSource().getObsId() == null){
                    Obs source = new Obs();
                    
                    source.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TB_SAMPLE_SOURCE(), new Locale("en", "US"), mu)); 
                    source.setVoided(false);
                    source.setDateCreated(new Date());
                    source.setPerson(patient);
                    source.setCreator(user);
                    source.setEncounter(enc);
                    source.setObsDatetime(parentObs.getObsDatetime());
                    mso.setSource(source);
                }
                mnto.addSmear(mso);
                
            } else if (parentObs.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_CULTURE_PARENT())&& !parentObs.getVoided()) {
                // CULTURE
                MdrtbCultureObj mco = new MdrtbCultureObj();
                mco.setCultureParentObs(parentObs);
                for (Obs o : levelTwoObs){
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_SAMPLE_SOURCE())&& !o.getVoided())
                        mco.setSource(o); 
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_COLONIES())&& !o.getVoided())
                        mco.setColonies(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DATE_RECEIVED())&& !o.getVoided())
                        mco.setCultureDateReceived(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_CULTURE_METHOD())&& !o.getVoided())
                        mco.setCultureMethod(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TB_CULTURE_RESULT())&& !o.getVoided())
                        mco.setCultureResult(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_RESULT_DATE())&& !o.getVoided())
                        mco.setCultureResultsDate(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_CULTURE_START_DATE())&& !o.getVoided())
                        mco.setCultureStartDate(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TYPE_OF_ORGANISM())&& !o.getVoided())
                        mco.setTypeOfOrganism(o);
                    if (o.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_TYPE_OF_ORGANISM_NON_CODED()) && !o.getVoided())
                        mco.setTypeOfOrganismNonCoded(o);
                }
                if (mco.getColonies().getObsId() == null){
                    Obs colonies = new Obs();
                    
                    colonies.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_COLONIES(), new Locale("en", "US"), mu)); 
                    colonies.setVoided(false);
                    colonies.setDateCreated(new Date());
                    colonies.setPerson(patient);
                    colonies.setCreator(user);
                    colonies.setEncounter(enc);
                    colonies.setObsDatetime(parentObs.getObsDatetime());
                    mco.setColonies(colonies);
                }
                if (mco.getCultureDateReceived().getObsId() == null){
                    Obs dR = new Obs();
                    
                    dR.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_DATE_RECEIVED(), new Locale("en", "US"), mu)); 
                    dR.setVoided(false);
                    dR.setDateCreated(new Date());
                    dR.setPerson(patient);
                    dR.setCreator(user);
                    dR.setEncounter(enc);
                    dR.setObsDatetime(parentObs.getObsDatetime());
                    mco.setCultureDateReceived(dR);
                }
                if (mco.getCultureMethod().getObsId() == null){
                    Obs method = new Obs();
                    
                    method.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TB_CULTURE_METHOD(), new Locale("en", "US"), mu)); 
                    method.setVoided(false);
                    method.setDateCreated(new Date());
                    method.setPerson(patient);
                    method.setCreator(user);
                    method.setEncounter(enc);
                    method.setObsDatetime(parentObs.getObsDatetime());
                    mco.setCultureMethod(method);
                }
                if (mco.getCultureResult().getObsId() == null){
                    Obs cR = new Obs();
                    
                    cR.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TB_CULTURE_RESULT(), new Locale("en", "US"), mu)); 
                    cR.setVoided(false);
                    cR.setDateCreated(new Date());
                    cR.setPerson(patient);
                    cR.setCreator(user);
                    cR.setEncounter(enc);
                    cR.setObsDatetime(parentObs.getObsDatetime());
                    mco.setCultureResult(cR);
                }
                if (mco.getCultureResultsDate().getObsId() == null){
                    Obs cRD = new Obs();
                    
                    cRD.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_RESULT_DATE(), new Locale("en", "US"), mu)); 
                    cRD.setVoided(false);
                    cRD.setDateCreated(new Date());
                    cRD.setPerson(patient);
                    cRD.setCreator(user);
                    cRD.setEncounter(enc);
                    cRD.setObsDatetime(parentObs.getObsDatetime());
                    mco.setCultureResultsDate(cRD);
                }
                if (mco.getCultureStartDate().getObsId() == null){
                    Obs sD = new Obs();
                    
                    sD.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_CULTURE_START_DATE(), new Locale("en", "US"), mu)); 
                    sD.setVoided(false);
                    sD.setDateCreated(new Date());
                    sD.setPerson(patient);
                    sD.setCreator(user);
                    sD.setEncounter(enc);
                    sD.setObsDatetime(parentObs.getObsDatetime());
                    mco.setCultureStartDate(sD);
                }
                if (mco.getSource().getObsId() == null){
                    Obs source = new Obs();
                    
                    source.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TB_SAMPLE_SOURCE(), new Locale("en", "US"), mu)); 
                    source.setVoided(false);
                    source.setDateCreated(new Date());
                    source.setPerson(patient);
                    source.setCreator(user);
                    source.setEncounter(enc);
                    source.setObsDatetime(parentObs.getObsDatetime());
                    mco.setSource(source);
                }
                if (mco.getTypeOfOrganism().getObsId() == null){
                    Obs tO = new Obs();
                    
                    tO.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TYPE_OF_ORGANISM(), new Locale("en", "US"), mu)); 
                    tO.setVoided(false);
                    tO.setDateCreated(new Date());
                    tO.setPerson(patient);
                    tO.setCreator(user);
                    tO.setEncounter(enc);
                    tO.setObsDatetime(parentObs.getObsDatetime());
                    mco.setTypeOfOrganism(tO);
                } 
                if (mco.getTypeOfOrganismNonCoded().getObsId() == null){
                    Obs tON = new Obs();
                 
                    tON.setConcept(MdrtbUtil.getMDRTBConceptByName(mu.getSTR_TYPE_OF_ORGANISM_NON_CODED(), new Locale("en", "US"), mu)); 
                    tON.setVoided(false);
                    tON.setDateCreated(new Date());
                    tON.setPerson(patient);
                    tON.setCreator(user);
                    tON.setEncounter(enc);
                    tON.setObsDatetime(parentObs.getObsDatetime());
                    mco.setTypeOfOrganismNonCoded(tON);
                } 
                mnto.addCulture(mco);
            } else {
                throw new RuntimeException("The value for obsGroupId did not return either a smear, culture, or DST");
            }
            
            return mnto;
        } else
            return "";
       
    }

    private List<Concept> getDSTTests(AdministrationService as,
            ConceptService cs,MdrtbFactory mu) {
        String testList = as.getGlobalProperty("mdrtb.DST_drug_list");
        List<Concept> concepts = new ArrayList<Concept>();
        Concept dstTestConcept = MdrtbUtil.getMDRTBConceptByName(testList, new Locale("en", "US"), mu);
        if (dstTestConcept != null && dstTestConcept.isSet()) {
            Collection<ConceptAnswer> cas = dstTestConcept.getAnswers(false);
            for (ConceptAnswer c : cas) {
                concepts.add(c.getAnswerConcept());
            }
        } else if (dstTestConcept != null) {
            concepts.add(dstTestConcept);
        } else if (dstTestConcept == null) {
            for (StringTokenizer st = new StringTokenizer(testList, "|"); st
                    .hasMoreTokens();) {
                String drugName = st.nextToken().trim();
                List<ConceptWord> cw = cs.getConceptWords(drugName, MdrtbUtil.getLocalesFromDB(), false, null, null, null, null, null, null, null);
                for (ConceptWord c : cw) {
                    Concept concept = c.getConcept();
                    ConceptName cn = concept.getBestName(Context.getLocale());
                    if (cn.getName().equals(drugName))
                        concepts.add(cn.getConcept());
                }
            }
        }
        return concepts;
    }
    
    private List<Concept> getOrganismTypes(AdministrationService as, ConceptService cs, MdrtbFactory mu){
        List<Concept> res = new ArrayList<Concept>();
        String ots = as.getGlobalProperty("mdrtb.organism_type");
        Concept otsConcept = MdrtbUtil.getMDRTBConceptByName(ots, new Locale("en", "US"), mu);
        if (otsConcept != null){
            Collection<ConceptAnswer> cas =  otsConcept.getAnswers(false);
            if (cas.size() != 0){
                for (ConceptAnswer c : cas) {
                    res.add(c.getAnswerConcept());
                }
            } else res.add(otsConcept);
        } else {
            for (StringTokenizer st = new StringTokenizer(ots, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim();
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"), mu);
                if (c != null)
                res.add(c);
            }
        }
        return res;
    }
    
    private List<Concept> getDSTRes(AdministrationService as,
            ConceptService cs, MdrtbFactory mu) {
        List<Concept> dstRes = new ArrayList<Concept>();
        String dstResList = as.getGlobalProperty("mdrtb.DST_result_list");
        Concept dstResListConcept =  MdrtbUtil.getMDRTBConceptByName(dstResList, new Locale("en", "US"), mu);
        if (dstResListConcept != null && dstResListConcept.isSet()) {
            Collection<ConceptAnswer> cas = dstResListConcept.getAnswers(false);
            for (ConceptAnswer c : cas) {
                dstRes.add(c.getAnswerConcept());
            }
        } else if (dstResListConcept != null) {
            dstRes.add(dstResListConcept);
        } else if (dstResListConcept == null) {
            for (StringTokenizer st = new StringTokenizer(dstResList, "|"); st
                    .hasMoreTokens();) {
                String dstName = st.nextToken().trim();
                boolean found = false;
                List<ConceptWord> cw = cs.getConceptWords(dstName, MdrtbUtil.getLocalesFromDB(), false, null, null, null, null, null, null, null);
                for (ConceptWord c : cw) {
                    Concept concept = c.getConcept();
                    Collection<ConceptName> cnList = concept.getNames();
                    for (ConceptName cn : cnList){
                        if (cn.getName().equals(dstName) && (cn.getLocale().equals(Context.getLocale()) || cn.getLocale().equals(new Locale("en")))){
                            dstRes.add(concept);
                            found = true;
                            break;
                        }
                                
                    }
                    if (found)
                        break;
                }
            }
        }
        return dstRes;
    }
    
    
    private List<Concept> getSmearRes(AdministrationService as, ConceptService cs, MdrtbFactory mu){
        String smearResList = as.getGlobalProperty("mdrtb.smear_result_list");
        List<Concept> smearRes = new ArrayList<Concept>();
        Concept smearResListConcept = MdrtbUtil.getMDRTBConceptByName(smearResList, new Locale("en", "US"), mu);
        if (smearResListConcept != null){    
            Collection<ConceptAnswer> cas =  smearResListConcept.getAnswers(false);
            if (cas.size() != 0){
                for (ConceptAnswer c:cas){
                    smearRes.add(c.getAnswerConcept());
                }
            } else smearRes.add(smearResListConcept);
        } else {
            for (StringTokenizer st = new StringTokenizer(smearResList, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim();
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"), mu);
                if (c != null)
                smearRes.add(c);
            }
        }
        return smearRes;
    }
    
    private List<Concept> getCultureRes(AdministrationService as, ConceptService cs, MdrtbFactory mu){
        String cultureResList = as.getGlobalProperty("mdrtb.culture_result_list");
        List<Concept> cultureRes = new ArrayList<Concept>();
        Concept cultureResListConcept = MdrtbUtil.getMDRTBConceptByName(cultureResList, new Locale("en", "US"), mu);
        if (cultureResListConcept != null){
            Collection<ConceptAnswer> cas =  cultureResListConcept.getAnswers(false);
            if (cas.size() != 0){
                for (ConceptAnswer c:cas){
                    cultureRes.add(c.getAnswerConcept());
                }
            }
            else  cultureRes.add(cultureResListConcept);
        } else{
            for (StringTokenizer st = new StringTokenizer(cultureResList, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim();
                
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"), mu);
                if (c != null)
                cultureRes.add(c);
            }     
        }
        return cultureRes;
    }
    
    
   
    
    private Encounter cloneEncounter(Encounter src){
        Encounter enc = new Encounter();
        enc.setCreator(src.getCreator());
        enc.setDateCreated(new Date());
        enc.setEncounterDatetime(src.getEncounterDatetime());
        enc.setEncounterType(src.getEncounterType());
        enc.setLocation(src.getLocation());
        enc.setPatient(src.getPatient());
        enc.setVoided(false);
        return enc;
    }
    
    private void resetEncOnAllObs(Obs parentObs, Encounter newEnc){
        parentObs.setEncounter(newEnc);
        for (Obs o : parentObs.getGroupMembers()){
            o.setEncounter(newEnc);
            if (o.hasGroupMembers())
                resetEncOnAllObs(o, newEnc);
        }
    }
    
    private boolean mdoHasBeenCreatedForConcept(List<Concept> drugConceptList, Concept c, MdrtbDSTObj mdo){
        int neededCount = 0;
        for (Concept cTmp : drugConceptList)
            if (cTmp.getConceptId().intValue() == c.getConceptId().intValue())
                neededCount++;
                

        int createdCount = 0;
        for (MdrtbDSTResultObj mdro : mdo.getDstResults())
            if (mdro.getDrug() != null && mdro.getDrug().getValueCoded() != null 
                    && mdro.getDrug().getValueCoded().getConceptId() != null 
                    && mdro.getDrug().getValueCoded().getConceptId().intValue() == c.getConceptId().intValue())
                createdCount ++;
        
        if (neededCount > createdCount)
            return false;
        else 
            return true;
    }
    private static Encounter fixEnc(Encounter enc){
        
        if (enc.getAllObs() != null){
            for (Obs o : enc.getAllObs()){
                if (o.getUuid() == null)
                    o.setUuid(UUID.randomUUID().toString());
                if (o.getLocation() == null)
                    o.setLocation(enc.getLocation());
                if (o.hasGroupMembers()){
                    for (Obs oTmp : o.getGroupMembers()){
                        if (oTmp.getUuid() == null)
                            oTmp.setUuid(UUID.randomUUID().toString());
                        if (oTmp.getLocation() == null)
                            oTmp.setLocation(enc.getLocation());
                        if (oTmp.hasGroupMembers()){
                            for (Obs oInner : oTmp.getGroupMembers()){
                                if (oInner.getUuid() == null)
                                    oInner.setUuid(UUID.randomUUID().toString());
                                if (oInner.getLocation() == null)
                                    oInner.setLocation(enc.getLocation());
                            }
                        }
                    }
                }
            }
        }
        
        Set<Order> newOrders = enc.getOrders();
        if (newOrders.size() == 0)
            enc.setOrders(new HashSet<Order>());
        
        return enc;
    }
}
