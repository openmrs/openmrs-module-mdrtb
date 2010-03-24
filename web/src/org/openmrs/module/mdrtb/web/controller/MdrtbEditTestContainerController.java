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
import org.openmrs.EncounterType;
import org.openmrs.Form;
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
            
            //map.put("testNames", this.getDSTTests(as, cs, mu));
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
            
            
            String obsGroupId = request.getParameter("ObsGroupId");
            Obs o = Context.getObsService().getObs(Integer.valueOf(obsGroupId));
            List<Encounter> encSet = Context.getEncounterService().getEncountersByPatient(Context.getPatientService().getPatient(o.getPersonId()));
            map.put("encounters", encSet);
            mu = null;
        }
        return map;
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,HttpServletResponse response, Object object,BindException exceptions) throws Exception {

            String returnView = "BAC";
            String action = request.getParameter("submit");
            String retType = request.getParameter("retType");
            MessageSourceAccessor msa = getMessageSourceAccessor();
            MdrtbNewTestObj mnto = (MdrtbNewTestObj) object;
            Patient patient = mnto.getPatient();
    
            if (msa.getMessage("mdrtb.cancel").equals(action)) {
                if (retType.equals("smears"))
                    returnView = "BAC";
                if (retType.equals("cultures"))
                    returnView = "BAC";
                if (retType.equals("dsts"))
                    returnView = "DST";  
                RedirectView rv = new RedirectView(getSuccessView());
                rv.addStaticAttribute("patientId", patient.getPatientId());
                rv.addStaticAttribute("view", returnView);
                return new ModelAndView(rv);
            }
            
            AdministrationService as = Context.getAdministrationService();
            EncounterService es = Context.getEncounterService();
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
            try { 
                encounterId = Integer.parseInt(encStringTmp);
            } catch (Exception ex){
                log.info("Not able to parse encounterID, creating a new encounter for DST or bacteriology.");
            }
     
            boolean saveTest = false;
            
            //deal with the encounter & location & provider issue:
            
            Set<Encounter> encsToDelete = new HashSet<Encounter>();
            Encounter enc = mnto.getPrimaryEncounter();
            if (encounterId != null && !encounterId.equals(enc.getEncounterId())){
                enc = Context.getEncounterService().getEncounter(encounterId);
            }

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
                        MdrtbSmearObj mso = null;
                        MdrtbCultureObj mco = null;
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
                        if (mso != null && mso.getSmearResult() != null && mso.getSmearResult().getValueCoded() != null 
                                && mso.getSmearResult().getObsDatetime() != null){
                                Date obsDate = mso.getSmearResult().getObsDatetime(); 
                                
                                Obs parentObs = mso.getSmearParentObs();
                                Encounter oldEnc = parentObs.getEncounter();
                                if (!oldEnc.getEncounterId().equals(enc.getEncounterId())){
                                  //we need to clone the old encounter, if provider is different
//                                    if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
//                                        Encounter newEnc = cloneEncounter(enc);
//                                        resetEncOnAllObs(parentObs, newEnc);
//                                        newEnc.setProvider(us.getUser(newProviderId));
//                                        enc = newEnc;
//                                    }
                                    oldEnc = removeObsFromOldEnc(oldEnc, parentObs.getObsId());
                                    encsToDelete.add(oldEnc);
                                    
                                }
                                if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                    enc.setProvider(Context.getUserService().getUser(newProviderId));
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
                                
                                saveTest = true;
                                
                                
                                enc.setEncounterDatetime(obsDate);
                                parentObs.setObsDatetime(obsDate);

                                Obs smearResult = mso.getSmearResult();
                                smearResult.setValueDatetime(smearResult.getObsDatetime());
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
                                    setLocationEncounterAndObsDate(source, parentObs.getLocation(), enc, obsDate);
                                    parentObs.addGroupMember(source);
                                }
                                
                                Obs smearResultDate = mso.getSmearResultDate();
                                if (smearResultDate.getValueDatetime() != null){
                                    setLocationEncounterAndObsDate(smearResultDate, parentObs.getLocation(), enc, obsDate);
                                    parentObs.addGroupMember(smearResultDate);
                                }
                                
                                Obs smearDateReceived = mso.getSmearDateReceived();
                                if (smearDateReceived.getValueDatetime() != null){
                                    setLocationEncounterAndObsDate(smearDateReceived, parentObs.getLocation(), enc, obsDate);
                                    parentObs.addGroupMember(smearDateReceived);
                                }
                                
                                Obs smearMethod = mso.getSmearMethod();
                                if (smearMethod.getValueCoded() != null){
                                    setLocationEncounterAndObsDate(smearMethod, parentObs.getLocation(), enc, obsDate);
                                    parentObs.addGroupMember(smearMethod);
                                }

                        }
                        
                        if (mco != null && mco.getCultureResult() != null && mco.getCultureResult().getValueCoded() != null 
                                && mco.getCultureResult().getObsDatetime() != null){
                            
                            Date obsDate = mco.getCultureResult().getObsDatetime();
                            Obs parentObs = mco.getCultureParentObs();
                            Encounter oldEnc = parentObs.getEncounter();
                            //lazy loading:
                            enc.getProvider();
                            if (!oldEnc.getEncounterId().equals(enc.getEncounterId())){
                                //we need to clone the old encounter, if provider is different
//                                  if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
//                                      Encounter newEnc = cloneEncounter(enc);
//                                      resetEncOnAllObs(parentObs, newEnc);
//                                      newEnc.setProvider(us.getUser(newProviderId));
//                                      enc = newEnc;
//                                  }
                                  oldEnc = removeObsFromOldEnc(oldEnc, parentObs.getObsId());
                                  encsToDelete.add(oldEnc);
                              }
                            if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                enc.setProvider(Context.getUserService().getUser(newProviderId));
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
                            enc.setEncounterDatetime(mco.getCultureResult().getObsDatetime());
                            saveTest = true;
                         
                            parentObs.setObsDatetime(obsDate);
                          //now, add the child obs to the parent if their answers are not null:
                            Obs cultureResult = mco.getCultureResult();
                            cultureResult.setValueDatetime(cultureResult.getObsDatetime());
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
                                setLocationEncounterAndObsDate(source, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(source);
                            }
                            
                            Obs cultureStartDate = mco.getCultureStartDate();
                            if (cultureStartDate.getValueDatetime() != null){
                                setLocationEncounterAndObsDate(cultureStartDate, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(cultureStartDate);
                                
                            }
                            
                            Obs cultureResultsDate = mco.getCultureResultsDate();
                            if (cultureResultsDate.getValueDatetime() != null){
                                setLocationEncounterAndObsDate(cultureResultsDate, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(cultureResultsDate);
                            }
                            
                            Obs cultureDateReceived = mco.getCultureDateReceived();
                            if (cultureDateReceived.getValueDatetime() != null){
                                setLocationEncounterAndObsDate(cultureDateReceived, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(cultureDateReceived);
                            }
                            
                            Obs cultureMethod = mco.getCultureMethod();
                            if (cultureMethod.getValueCoded() != null){
                                setLocationEncounterAndObsDate(cultureMethod, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(cultureMethod);
                            }
                            
                            Obs typeOfOrganism = mco.getTypeOfOrganism();
                            if (typeOfOrganism.getValueCoded() != null){
                                setLocationEncounterAndObsDate(typeOfOrganism, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(typeOfOrganism);
                            }
                            
                            Obs typeOfOrganismNonCoded = mco.getTypeOfOrganismNonCoded();
                            if (typeOfOrganismNonCoded.getValueText() != null){
                                setLocationEncounterAndObsDate(typeOfOrganismNonCoded, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(typeOfOrganismNonCoded);
                            }
                           clean = true;
                        }
                        Context.evictFromSession(mso);
                        Context.evictFromSession(mco);
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
                            Date obsDate = dst.getSputumCollectionDate().getValueDatetime();
                            if (!oldEnc.getEncounterId().equals(enc.getEncounterId())){
                                //we need to clone the old encounter, if provider is different
//                                  if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
//                                      Encounter newEnc = cloneEncounter(enc);
//                                      resetEncOnAllObs(parentObs, newEnc);
//                                      newEnc.setProvider(us.getUser(newProviderId));
//                                      enc = newEnc;
//                                  }
                                  oldEnc = removeObsFromOldEnc(oldEnc, parentObs.getObsId());
                                  encsToDelete.add(oldEnc);
                                  
                            }
                            if (enc.getProvider().getPersonId().intValue() != newProviderId.intValue()){
                                enc.setProvider(Context.getUserService().getUser(newProviderId));
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

                            enc.setEncounterDatetime(dst.getSputumCollectionDate().getValueDatetime());
                            saveTest = true;

                            parentObs.setObsDatetime(obsDate);
                            
                            //create the obs tree before saving:
                            Obs sputumCollectionDate = dst.getSputumCollectionDate();
                            sputumCollectionDate.setLocation(parentObs.getLocation());
                            sputumCollectionDate.setEncounter(enc);
                            sputumCollectionDate.setObsDatetime(sputumCollectionDate.getValueDatetime());
                            parentObs.addGroupMember(sputumCollectionDate);
                            
                            Obs typeOfOrganism = dst.getTypeOfOrganism();
                            if (typeOfOrganism.getValueCoded() != null){
                                setLocationEncounterAndObsDate(typeOfOrganism, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(typeOfOrganism);
                            }
                            
                            Obs typeOfOrganismNonCoded = dst.getTypeOfOrganismNonCoded();
                            if (typeOfOrganismNonCoded.getValueText() != null){
                                setLocationEncounterAndObsDate(typeOfOrganismNonCoded, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(typeOfOrganismNonCoded);
                            }
                            
                            Obs drugSensitivityTestComplete = dst.getDrugSensitivityTestComplete();
                            if (drugSensitivityTestComplete.getValueAsBoolean() != null){
                                setLocationEncounterAndObsDate(drugSensitivityTestComplete, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(drugSensitivityTestComplete);
                            }
                            
                            Obs dstStartDate = dst.getDstStartDate();
                            if (dstStartDate.getValueDatetime() != null){
                                setLocationEncounterAndObsDate(dstStartDate, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(dstStartDate);
                            }
                            
                            Obs dstResultsDate = dst.getDstResultsDate();
                            if (dstResultsDate.getValueDatetime() != null){
                                setLocationEncounterAndObsDate(dstResultsDate, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(dstResultsDate);
                            }
                            
                            Obs dstDateReceived = dst.getDstDateReceived();
                            if (dstDateReceived.getValueDatetime() != null){
                                setLocationEncounterAndObsDate(dstDateReceived, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(dstDateReceived);
                            }
                            
                            Obs source = dst.getSource();
                            if (source.getValueCoded() != null){
                                setLocationEncounterAndObsDate(source, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(source);
                            }
                            
                            Obs dstMethod = dst.getDstMethod();
                            if (dstMethod.getValueCoded() != null){
                                setLocationEncounterAndObsDate(dstMethod, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(dstMethod);
                            }
                            
                            
                            Obs directOrIndirect = dst.getDirectOrIndirect();
                            if (directOrIndirect.getValueAsBoolean()!= null){
                                setLocationEncounterAndObsDate(directOrIndirect, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(directOrIndirect);
                            }
                            
                            Obs  coloniesInControl = dst.getColoniesInControl();
                            if (coloniesInControl.getValueNumeric()!= null){
                                setLocationEncounterAndObsDate(coloniesInControl, parentObs.getLocation(), enc, obsDate);
                                parentObs.addGroupMember(coloniesInControl);
                            }
                            
                            
                            for (MdrtbDSTResultObj res: dstResObs){
                                if (res.getDrug().getConcept() != null && !res.getDrug().getConcept().getConceptId().equals(mu.getConceptNone().getConceptId())){
                                    
                                    Obs dstParentResultObs = res.getDstResultParentObs();
                                    setLocationEncounterAndObsDate(dstParentResultObs, parentObs.getLocation(), enc, obsDate);
                                    parentObs.addGroupMember(dstParentResultObs);
                                    
                                   Obs drug = res.getDrug();
                                   if (drug.getConcept() != null){
                                       setLocationEncounterAndObsDate(drug, parentObs.getLocation(), enc, obsDate);
                                       dstParentResultObs.addGroupMember(drug);
                                   }
                                   
                                   Obs colonies = res.getColonies();
                                   if (colonies.getValueNumeric() != null){
                                       setLocationEncounterAndObsDate(colonies, parentObs.getLocation(), enc, obsDate);
                                       dstParentResultObs.addGroupMember(colonies);
                                   }
                                   
                                   Obs concentration = res.getConcentration();
                                   if (concentration.getValueNumeric() != null){
                                       setLocationEncounterAndObsDate(concentration, parentObs.getLocation(), enc, obsDate);
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
                        Context.evictFromSession(dst);
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
                if (saveTest){
                        //set uuids
                        Context.evictFromSession(mnto);
                        Context.evictFromSession(object);
                        object = null;
                        fixEnc(enc, patient);   
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
        //TODO:  attach concentrations to empty concentration obs that correspond to no results
        
        if (Context.isAuthenticated()) {
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            MdrtbFactory mu = ms.getMdrtbFactory();
            String obsGroupId = request.getParameter("ObsGroupId");
            MdrtbNewTestObj mnto = new MdrtbNewTestObj();
            Obs parentObs = null;
            User user = Context.getAuthenticatedUser();
            
            Integer parentObsInt = Integer.valueOf(obsGroupId);
            Encounter enc = null;
            Encounter encTmp = Context.getObsService().getObs(parentObsInt).getEncounter();
            if (encTmp == null){
                parentObs = Context.getObsService().getObs(parentObsInt);
                enc = new Encounter();
                enc.addObs(parentObs);
            }  else {  
                Integer encounterId = encTmp.getEncounterId();
                Context.evictFromSession(encTmp);
                encTmp = null;
                enc = Context.getEncounterService().getEncounter(encounterId);
                mnto.setPrimaryEncounter(enc);
                for (Obs oTmp : enc.getAllObs()){
                    if (oTmp.getObsId().equals(parentObsInt))
                        parentObs = oTmp;
                }
            }
            if (parentObs == null || enc == null)
                throw new RuntimeException("Could not load parent obs for this bacteriology or DST.");
            
            Set<Obs> levelTwoObs = parentObs.getGroupMembers();
            Patient patient = Context.getPatientService().getPatient(parentObs.getPersonId());
            mnto.setPatient(patient);

            if (parentObs.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_DST_PARENT()) && !parentObs.isVoided()) {
                // DST
                MdrtbDSTObj mdo = new MdrtbDSTObj(patient, user, mu);
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
                            
                            if (mdro.getColonies() == null || (mdro.getColonies().getConcept() == null)){
                                Obs colonies = createNewObs(mu.getConceptColonies(), patient, user, enc, parentObs.getObsDatetime());
                                mdro.setColonies(colonies);
                            }
                            if (mdro.getConcentration() == null || (mdro.getConcentration().getConcept() == null)){
                                Obs conc = createNewObs(mu.getConceptConcentration(), patient, user, enc, parentObs.getObsDatetime());
                                mdro.setConcentration(conc);
                            }
                            
                            if (mdro.getDrug() != null && !alreadyShownResults.contains(mdro)){
                                mdro.setDstResultParentObs(o);
                                //TODO:  HERE -- replace blank mdros 
                                removeBlankMdroIfNeeded(mdo, mdro);
                                mdo.getDstResults().add(mdro);
                                alreadyShownResults.add(mdro);
                            }  else {
                                log.error("HERE's a dst parent with some children but no attached drug obs:" + o.getObsId());
                            }
                        }        
                    }    
                }
                if (mdo.getColoniesInControl().getObsId() == null){
                    Obs cc = createNewObs(mu.getConceptColoniesInControl(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setColoniesInControl(cc);
                }
                if (mdo.getDirectOrIndirect().getObsId() == null){
                    Obs dInd = createNewObs(mu.getConceptDirectIndirect(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setDirectOrIndirect(dInd);
                }
                if (mdo.getDrugSensitivityTestComplete().getObsId() == null){
                    Obs dC = createNewObs(mu.getConceptDSTComplete(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setDrugSensitivityTestComplete(dC);
                }
                if (mdo.getDstDateReceived().getObsId() == null){
                    Obs dR = createNewObs(mu.getConceptDateReceived(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setDstDateReceived(dR);
                }
                if (mdo.getDstMethod().getObsId() == null){
                    Obs method = createNewObs(mu.getConceptDSTMethod(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setDstMethod(method);
                }
                if (mdo.getDstResultsDate().getObsId() == null){
                    Obs dRD = createNewObs(mu.getConceptResultDate(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setDstResultsDate(dRD);
                }
                if (mdo.getDstStartDate().getObsId() == null){
                    Obs dSD = createNewObs(mu.getConceptCultureStartDate(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setDstStartDate(dSD);
                }
                if (mdo.getSource().getObsId() == null){
                    Obs source = createNewObs(mu.getConceptSampleSource(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setSource(source);
                }
                if (mdo.getSputumCollectionDate().getObsId() == null){
                    Obs sCD = createNewObs(mu.getConceptSputumCollectionDate(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setSputumCollectionDate(sCD);
                }
                if (mdo.getTypeOfOrganism().getObsId() == null){
                    Obs type = createNewObs(mu.getConceptTypeOfOrganism(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setTypeOfOrganism(type);
                }
                if (mdo.getTypeOfOrganismNonCoded().getObsId() == null){
                    Obs typeOther = createNewObs(mu.getConceptTypeOfOrganismNonCoded(), patient, user, enc, parentObs.getObsDatetime());
                    mdo.setTypeOfOrganismNonCoded(typeOther);
                }
                
//                //lastly, add DST result obj for all non-used drugs:
//                for (Concept c : drugConceptList){
//                    //HERE
//                    if (!mdoHasBeenCreatedForConcept(drugConceptList, c, mdo) ){
//                        MdrtbDSTResultObj mdro = new MdrtbDSTResultObj();
//                        
//                        Obs drug = createNewObs(null, patient, user, enc, parentObs.getObsDatetime());
//                        drug.setValueCoded(c);
//                        mdro.setDrug(drug);
//                        
//                        Obs concentration = createNewObs(mu.getConceptConcentration(), patient, user, enc, parentObs.getObsDatetime());
//                        mdro.setConcentration(concentration);
//                        
//                        Obs colonies = createNewObs(mu.getConceptColonies(), patient, user, enc, parentObs.getObsDatetime());
//                        mdro.setColonies(colonies);
//                        
//                        Obs dstResultParentObs = createNewObs(mu.getConceptDSTResultParent(), patient, user, enc, parentObs.getObsDatetime());
//                        mdro.setDstResultParentObs(dstResultParentObs);
//                        
//                        mdo.addDstResult(mdro);
//                    }
//                    
//                }
                
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
                MdrtbSmearObj mso = new MdrtbSmearObj(patient, user, mu);
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
                    Obs bacilli = createNewObs(mu.getConceptBacilli(), patient, user, enc, parentObs.getObsDatetime());
                    mso.setBacilli(bacilli);
                }
                if (mso.getSmearDateReceived().getObsId() == null){
                    Obs sDR = createNewObs(mu.getConceptDateReceived(), patient, user, enc, parentObs.getObsDatetime());
                    mso.setSmearDateReceived(sDR);
                }
                if (mso.getSmearMethod().getObsId() == null){
                    Obs sMeth = createNewObs(mu.getConceptSmearMicroscopyMethod(), patient, user, enc, parentObs.getObsDatetime());
                    mso.setSmearMethod(sMeth);
                }
                if (mso.getSmearResult().getObsId() == null){
                    Obs sRes = createNewObs(mu.getConceptSmearResult(), patient, user, enc, parentObs.getObsDatetime());
                    mso.setSmearResult(sRes);
                }
                if (mso.getSmearResultDate().getObsId() == null){
                    Obs sResDate = createNewObs(mu.getConceptResultDate(), patient, user, enc, parentObs.getObsDatetime());
                    mso.setSmearResultDate(sResDate);
                }
                if (mso.getSource().getObsId() == null){
                    Obs source = createNewObs(mu.getConceptSampleSource(), patient, user, enc, parentObs.getObsDatetime());
                    mso.setSource(source);
                }
                mnto.addSmear(mso);
                
            } else if (parentObs.getConcept().getBestName(new Locale("en")).getName().equals(mu.getSTR_CULTURE_PARENT())&& !parentObs.getVoided()) {
                // CULTURE
                MdrtbCultureObj mco = new MdrtbCultureObj(patient, user, mu);
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
                    Obs colonies = createNewObs(mu.getConceptColonies(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setColonies(colonies);
                }
                if (mco.getCultureDateReceived().getObsId() == null){
                    Obs dR = createNewObs(mu.getConceptDateReceived(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setCultureDateReceived(dR);
                }
                if (mco.getCultureMethod().getObsId() == null){
                    Obs method = createNewObs(mu.getConceptCultureMethod(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setCultureMethod(method);
                }
                if (mco.getCultureResult().getObsId() == null){
                    Obs cR = createNewObs(mu.getConceptCultureResult(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setCultureResult(cR);
                }
                if (mco.getCultureResultsDate().getObsId() == null){
                    Obs cRD = createNewObs(mu.getConceptResultDate(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setCultureResultsDate(cRD);
                }
                if (mco.getCultureStartDate().getObsId() == null){
                    Obs sD = createNewObs(mu.getConceptCultureStartDate(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setCultureStartDate(sD);
                }
                if (mco.getSource().getObsId() == null){
                    Obs source = createNewObs(mu.getConceptSampleSource(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setSource(source);
                }
                if (mco.getTypeOfOrganism().getObsId() == null){
                    Obs tO = createNewObs(mu.getConceptTypeOfOrganism(), patient, user, enc, parentObs.getObsDatetime());
                    mco.setTypeOfOrganism(tO);
                } 
                if (mco.getTypeOfOrganismNonCoded().getObsId() == null){
                    Obs tON = createNewObs(mu.getConceptTypeOfOrganismNonCoded(), patient, user, enc, parentObs.getObsDatetime());
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
        enc.setUuid(UUID.randomUUID().toString());
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
        for (MdrtbDSTResultObj mdro : mdo.getDstResults()){
            if (mdro.getDrug() != null && mdro.getDrug().getValueCoded() != null 
                    && mdro.getDrug().getValueCoded().getConceptId() != null 
                    && mdro.getDrug().getValueCoded().getConceptId().intValue() == c.getConceptId().intValue())
                createdCount ++;
        }
        if (neededCount > createdCount)
            return false;
        else 
            return true;
    }
    
    private static Encounter fixEnc(Encounter enc, Patient patient){   
        if (enc.getAllObs() != null){
            for (Obs o : enc.getAllObs()){
                if (o.getConcept() == null){
                    enc.removeObs(o);
                    continue;
                }    
                if (o.getUuid() == null)
                    o.setUuid(UUID.randomUUID().toString());
                if (o.getLocation() == null)
                    o.setLocation(enc.getLocation());
                if (o.getDateCreated() == null)
                    o.setDateCreated(new Date());
                if (o.getCreator() == null)
                    o.setCreator(Context.getAuthenticatedUser());
                o.setPerson(patient);
                if (o.hasGroupMembers()){
                    for (Obs oTmp : o.getGroupMembers()){
                        if (oTmp.getConcept() == null){
                            enc.removeObs(oTmp);
                            continue;
                        }
                        if (oTmp.getUuid() == null)
                            oTmp.setUuid(UUID.randomUUID().toString());
                        if (oTmp.getLocation() == null)
                            oTmp.setLocation(enc.getLocation());
                        if (oTmp.getDateCreated() == null)
                            oTmp.setDateCreated(new Date());
                        if (oTmp.getCreator() == null)
                            oTmp.setCreator(Context.getAuthenticatedUser());
                        oTmp.setPerson(patient);
                        if (oTmp.hasGroupMembers()){
                            for (Obs oInner : oTmp.getGroupMembers()){
                                if (oInner.getConcept() == null){
                                    oTmp.removeGroupMember(oInner);
                                    continue;
                                }
                                if (oInner.getUuid() == null)
                                    oInner.setUuid(UUID.randomUUID().toString());
                                if (oInner.getLocation() == null)
                                    oInner.setLocation(enc.getLocation());
                                if (oInner.getDateCreated() == null)
                                    oInner.setDateCreated(new Date());
                                if (oInner.getCreator() == null)
                                    oInner.setCreator(Context.getAuthenticatedUser());
                                oInner.setPerson(patient);
                            }
                        }
                    }
                }
            }
        }
        
        Set<Order> newOrders = enc.getOrders();
        if (newOrders.size() == 0)
            enc.setOrders(new HashSet<Order>());
        
        if (enc.getEncounterType() != null){
            enc.getEncounterType();
            EncounterType et = Context.getEncounterService().getEncounterType(enc.getEncounterType().getEncounterTypeId());
            enc.setEncounterType(et);
        }
        if (enc.getForm() != null){
            enc.getForm();
            Form form = Context.getFormService().getForm(enc.getForm().getFormId());
            enc.setForm(form);
        }    
        
        return enc;
    }
    

    private Obs createNewObs(Concept c, Patient patient, User user, Encounter enc, Date obsDatetime){
          Obs o = new Obs();
          o.setConcept(c); 
          o.setVoided(false);
          o.setDateCreated(new Date());
          o.setPerson(patient);
          o.setCreator(user);
          o.setEncounter(enc);
          o.setObsDatetime(obsDatetime);
          o.setUuid(UUID.randomUUID().toString());
          return o;
    }
    
    private void setLocationEncounterAndObsDate(Obs o, Location location, Encounter enc, Date obsDate){
        o.setLocation(location);
        o.setEncounter(enc);
        o.setObsDatetime(obsDate);
    }
    
    private Encounter removeObsFromOldEnc(Encounter oldEnc, Integer parentObsToRemove){
       //oldEnc.removeObs(parentObs);
        Integer id = oldEnc.getEncounterId();
        Context.evictFromSession(oldEnc);
        Encounter enc = Context.getEncounterService().getEncounter(id);
        for (Obs o : enc.getAllObs()){
            if (o.getObsId().equals(parentObsToRemove))
                enc.removeObs(o);
        }
        return enc;
    }    
    
    private static void removeBlankMdroIfNeeded(MdrtbDSTObj mdo, MdrtbDSTResultObj mdro){
        for (MdrtbDSTResultObj mdroTmp : mdo.getDstResults()){
            if (mdroTmp.getDrug() != null && mdroTmp.getDrug().getConcept() == null && 
                    mdroTmp.getDrug().getValueCoded() != null &&
                    mdroTmp.getDrug().getValueCoded().getConceptId().equals(mdro.getDrug().getValueCoded().getConceptId())){
                mdo.getDstResults().remove(mdroTmp);
                break;
            }
        }
    }
}
