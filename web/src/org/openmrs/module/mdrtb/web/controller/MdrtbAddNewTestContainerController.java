package org.openmrs.module.mdrtb.web.controller;

import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbCultureObj;
import org.openmrs.module.mdrtb.MdrtbDSTObj;
import org.openmrs.module.mdrtb.MdrtbDSTResultObj;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbNewTestObj;
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
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MdrtbAddNewTestContainerController extends SimpleFormController  {

    protected final Log log = LogFactory.getLog(getClass());
    
    private String STR_TB_SMEAR_RESULT = "";
    private String STR_TB_SAMPLE_SOURCE = "";
    private String STR_BACILLI = "";
    private String STR_RESULT_DATE = "";
    private String STR_DATE_RECEIVED = "";
    private String STR_TB_SMEAR_MICROSCOPY_METHOD = "";
    private String STR_TB_CULTURE_RESULT = "";
    private String STR_COLONIES = "";
    private String STR_CULTURE_START_DATE = "";
    private String STR_TB_CULTURE_METHOD = "";
    private String STR_TYPE_OF_ORGANISM = "";
    private String STR_TYPE_OF_ORGANISM_NON_CODED = "";
    private String STR_DST_COMPLETE= "";
    private String STR_DST_METHOD= "";
    private String STR_DIRECT_INDIRECT= "";
    private String STR_COLONIES_IN_CONTROL= "";
    private String STR_CONCENTRATION = "";
    //ObsGroup concepts
    private String STR_DST_PARENT = "";
    private String STR_DST_RESULT_PARENT = "";
    private String STR_CULTURE_PARENT = "";
    private String STR_SMEAR_PARENT = "";
    private String STR_SPUTUM_COLLECTION_DATE = "";
    
 
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
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object object,
            BindException exceptions) throws Exception {
        String returnView = "BAC";
        boolean mdrTest = false;
        String action = request.getParameter("submit");
        AdministrationService as = Context.getAdministrationService();
        EncounterService es = Context.getEncounterService();
        MessageSourceAccessor msa = getMessageSourceAccessor();
        PatientService ps = Context.getPatientService();
        UserService us = Context.getUserService();
        
        String patientId = request.getParameter("patientId");
        Integer numRowsShown = Integer.valueOf(request.getParameter("numRowsShown"));
            if (numRowsShown == null || numRowsShown <1)
                    numRowsShown = 1;
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
        if (patientId != null && patientId.equals("") == false) {
            Patient patient = ps.getPatient(Integer.valueOf(patientId));
            //get the control object:
            MdrtbNewTestObj mnto = (MdrtbNewTestObj) object;
            if (!mnto.getPatient().equals(patient))
                throw new RuntimeException("patient passed in from form field vs. form backing object are different.");
     
            
            //deal with the encounter & location issue:
            Encounter enc = new Encounter();
            if (encounterId != null){
                enc = Context.getEncounterService().getEncounter(encounterId);
            }
            
            if (enc == null || enc.getEncounterId() == null){
            enc.setCreator(Context.getAuthenticatedUser());
            enc.setDateCreated(new Date());
            enc.setVoided(false);
            enc.setPatient(patient);
            enc.setProvider(us.getUserByUsername(as
                    .getGlobalProperty("mdrtb.mdrtb_default_provider")));
            }
            
            Set<Encounter> encsToSave = new HashSet<Encounter>();
            
        
                if (msa.getMessage("mdrtb.savebacteriology").equals(action)) {
                    MdrtbFactory mu = new MdrtbFactory();
                    if (enc.getEncounterType() == null)
                        enc.setEncounterType(es.getEncounterType(as.getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
                    if (enc.getEncounterType() == null)
                        throw new RuntimeException("EncounterType is null.  Make sure that your global properties for MDRTB encounter types are valid.");

                    for (int i = 0; i < numRowsShown; i ++){
                        MdrtbSmearObj mso = mnto.getSmears().get(i);
                        MdrtbCultureObj mco = mnto.getCultures().get(i);
                        
                        //sputum collection date, a result, and num colonies/bacilli are the required elements
                        if (mso.getSmearResult().getValueCoded() != null 
                                && mso.getSmearResult().getValueDatetime() != null){
   
                                Obs parentObs = mso.getSmearParentObs();
                                parentObs.setObsDatetime(new Date());
                                
                                
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
                                    enc.setEncounterDatetime(mso.getSmearResult().getValueDatetime());
                              
                                
                                //encounter and obs datetime stuff:
                                Date obsDate = new Date();
                                if (mso.getSmearResult().getValueDatetime() != null)
                                    obsDate = mso.getSmearResult().getValueDatetime();
                                parentObs.setObsDatetime(obsDate);

                                Obs smearResult = mso.getSmearResult();
                                if (enc.getEncounterDatetime() == null)
                                    enc.setEncounterDatetime(smearResult.getValueDatetime());
                                smearResult.setLocation(parentObs.getLocation());
                                smearResult.setEncounter(enc);
                                    smearResult.setObsDatetime(smearResult.getValueDatetime());
                                parentObs.addGroupMember(smearResult);
                                
                                if (smearResult.getValueCoded() != null && smearResult.getValueCoded().getConceptId().intValue() == mu.getConceptScanty().getConceptId().intValue()){
                                    Obs bacilli = mso.getBacilli();
                                    bacilli.setLocation(parentObs.getLocation());
                                    bacilli.setEncounter(enc);
                                    bacilli.setObsDatetime(obsDate);
                                    if (bacilli.getValueNumeric() != null)
                                        smearResult.setValueNumeric(bacilli.getValueNumeric());
                                    parentObs.addGroupMember(bacilli);
                                }
                                
                                
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
                                
                                //dealing with the possibility of different providers:
                                String providerString = "smear_provider_" + i;
                                String providerIdString = request.getParameter(providerString);
                                try{
                                   if (providerIdString != null && !providerIdString.equals("")){
                                       User provider = Context.getUserService().getUser(Integer.valueOf(providerIdString));
                                       if (provider.getPersonId().intValue() != enc.getProvider().getPersonId().intValue()){
                                           Encounter newEnc = cloneEncounter(enc);
                                           newEnc.addObs(parentObs);
                                           resetEncOnAllObs(parentObs, newEnc);
                                           newEnc.setProvider(provider);
                                           encsToSave.add(newEnc);
                                       } else {
                                           enc.addObs(parentObs);
                                           encsToSave.add(enc);
                                       }
                                   } else {
                                       enc.addObs(parentObs);
                                       encsToSave.add(enc);
                                   }
                                } catch (Exception ex){enc.addObs(parentObs); encsToSave.add(enc);}
                                
                        }
                        if (mco.getCultureResult().getValueCoded() != null 
                                && mco.getCultureResult().getValueDatetime() != null){
                            
                            Obs parentObs = mco.getCultureParentObs();
                            
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
                                enc.setEncounterDatetime(mco.getCultureResult().getValueDatetime());
                     
                            
                          //encounter and obs datetime stuff:
                            Date obsDate = new Date();
                            if (mco.getCultureResult().getValueDatetime() != null)
                                obsDate = mco.getCultureResult().getValueDatetime();
                            parentObs.setObsDatetime(obsDate);
                            
                          //now, add the child obs to the parent if their answers are not null:
                            Obs cultureResult = mco.getCultureResult();
                            if (enc.getEncounterDatetime() == null)
                                enc.setEncounterDatetime(cultureResult.getValueDatetime());
                            cultureResult.setLocation(parentObs.getLocation());
                            cultureResult.setEncounter(enc);
                            cultureResult.setObsDatetime(obsDate);
                            parentObs.addGroupMember(cultureResult);
                           
                            if (cultureResult.getValueCoded() != null && cultureResult.getValueCoded().getConceptId().intValue() == mu.getConceptScanty().getConceptId().intValue()){     
                                Obs colonies = mco.getColonies();
                                colonies.setLocation(parentObs.getLocation());
                                colonies.setEncounter(enc);
                                colonies.setObsDatetime(obsDate);
                                if (colonies.getValueNumeric() != null)
                                    cultureResult.setValueNumeric(colonies.getValueNumeric());
                                parentObs.addGroupMember(colonies);
                            }
                            
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
                          //dealing with the possibility of different providers:
                            String providerString = "culture_provider_" + i;
                            String providerIdString = request.getParameter(providerString);
                            try{
                               if (providerIdString != null && !providerIdString.equals("")){
                                   User provider = Context.getUserService().getUser(Integer.valueOf(providerIdString));
                                   if (provider.getPersonId().intValue() != enc.getProvider().getPersonId().intValue()){
                                       Encounter newEnc = cloneEncounter(enc);
                                       newEnc.addObs(parentObs);
                                       resetEncOnAllObs(parentObs, newEnc);
                                       newEnc.setProvider(provider);
                                       encsToSave.add(newEnc);
                                   } else {
                                       enc.addObs(parentObs);
                                       encsToSave.add(enc);
                                   }
                               } else {
                                   enc.addObs(parentObs);
                                   encsToSave.add(enc);
                               }
                            } catch (Exception ex){enc.addObs(parentObs); encsToSave.add(enc);}
                        }
                    }
                   
                    mdrTest = true;
                    mu = null;
                }  //bac  
    
                if (msa.getMessage("mdrtb.savedst").equals(action)) {
                    returnView = "DST";
                    if (enc.getEncounterType() == null)
                        enc.setEncounterType(es.getEncounterType(as.getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
                    if (enc.getEncounterType() == null)
                        throw new RuntimeException("EncounterType is null.  Make sure that your global properties for MDRTB encounter types are valid.");

                    for (int i = 0; i < numRowsShown; i ++){
                        MdrtbDSTObj dst = mnto.getDsts().get(i);
                        
                    //figure out if its worth doing anything with this DST:
                        List<MdrtbDSTResultObj> dstResObs = dst.getDstResults();
                        boolean worthSaving = false;
                        for (MdrtbDSTResultObj res: dstResObs){
                                if (res.getDrug().getConcept() != null){
                                worthSaving = true;
                                break;
                                }
                        }

                        //we're requiring at least one drug with a response, and a sputum collection date
                        if (worthSaving && dst.getSputumCollectionDate().getValueDatetime() != null){
                            Obs parentObs = dst.getDstParentObs();
                            
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
                                if (res.getDrug().getConcept() != null){
                                    
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
                                }
                                
                            }
                          //dealing with the possibility of different providers:
                            String providerString = "dst_provider_" + i;
                            String providerIdString = request.getParameter(providerString);
                            try{
                               if (providerIdString != null && !providerIdString.equals("")){
                                   User provider = Context.getUserService().getUser(Integer.valueOf(providerIdString));
                                   if (provider.getPersonId().intValue() != enc.getProvider().getPersonId().intValue()){
                                       Encounter newEnc = cloneEncounter(enc);
                                       newEnc.addObs(parentObs);
                                       resetEncOnAllObs(parentObs, newEnc);
                                       newEnc.setProvider(provider);
                                       encsToSave.add(newEnc);
                                   } else {
                                       enc.addObs(parentObs);
                                       encsToSave.add(enc);
                                   }
                               } else {
                                   enc.addObs(parentObs);
                                   encsToSave.add(enc);
                               }
                            } catch (Exception ex){enc.addObs(parentObs); encsToSave.add(enc);}
                           
                           
                        }
                    
                    }
                    
                } //DST

                
            for (Encounter encTmp : encsToSave){
                es.saveEncounter(encTmp);
            }
            if (mdrTest){
                MdrtbFactory mu = new MdrtbFactory();
                mu.fixCultureConversions(patient);
                mu = null;
            }
           
        }// patientId

        RedirectView rv = new RedirectView(getSuccessView());
        rv.addStaticAttribute("patientId", patientId);
        rv.addStaticAttribute("view", returnView);
        return new ModelAndView(rv);
    }


    /**
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception { 
     
        if (Context.isAuthenticated()) {
            this.readXML(request);
            User user = Context.getAuthenticatedUser();
            Patient patient = new Patient();
            MdrtbNewTestObj mnto = new MdrtbNewTestObj();
            PatientService ps = Context.getPatientService();
            String patientId = request.getParameter("patientId");
            if (patientId != null) {
                try {
                    int id = Integer.valueOf(patientId);
                    patient = ps.getPatient(id);
                    mnto = new MdrtbNewTestObj(STR_TB_SMEAR_RESULT, 
                            STR_TB_SAMPLE_SOURCE, 
                            STR_BACILLI,
                            STR_RESULT_DATE, 
                            STR_DATE_RECEIVED, 
                            STR_TB_SMEAR_MICROSCOPY_METHOD,
                            STR_TB_CULTURE_RESULT, 
                            STR_COLONIES, 
                            STR_CULTURE_START_DATE, 
                            STR_TB_CULTURE_METHOD, 
                            STR_TYPE_OF_ORGANISM, 
                            STR_TYPE_OF_ORGANISM_NON_CODED,
                            STR_DST_COMPLETE, 
                            STR_DST_METHOD, 
                            STR_DIRECT_INDIRECT, 
                            STR_COLONIES_IN_CONTROL,
                            STR_CONCENTRATION,
                            STR_SMEAR_PARENT,
                            STR_CULTURE_PARENT,
                            STR_DST_PARENT,
                            STR_DST_RESULT_PARENT,
                            STR_SPUTUM_COLLECTION_DATE,
                            patient,
                            user);
                    mnto.setPatient(patient);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.MONTH, -6);
                    //TODO: limit to last X months?
                    List<Encounter> encSet = Context.getEncounterService().getEncountersByPatient(patient);
                    mnto.setEncounters(encSet);
                }
                catch (NumberFormatException numberError) {
                    log.warn("Invalid userId supplied: '" + patientId + "'", numberError);
                }
                catch (ObjectRetrievalFailureException noUserEx) {
                    // continue
                }
            }
            return mnto;
        } else 
           return "";
        
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
            
            map.put("testNames", this.getDSTTests(as, cs));
            map.put("dstResults", this.getDSTRes(as, cs));  
            map.put("organismTypes", this.getOrganismTypes(as, cs));
            map.put("smearResults", this.getSmearRes(as, cs));
            map.put("cultureResults", this.getCultureRes(as, cs));
            
            Concept red =  MdrtbUtil.getMDRTBConceptByName(as.getGlobalProperty("mdrtb.dst_color_coding_red"), new Locale("en", "US"));
            Concept yellow =  MdrtbUtil.getMDRTBConceptByName(as.getGlobalProperty("mdrtb.dst_color_coding_yellow"), new Locale("en", "US"));
            Concept green =  MdrtbUtil.getMDRTBConceptByName(as.getGlobalProperty("mdrtb.dst_color_coding_green"), new Locale("en", "US"));         
            map.put("red", red.getName(Context.getLocale()).getName());
            map.put("yellow", yellow.getName(Context.getLocale()).getName());
            map.put("green", green.getName(Context.getLocale()).getName());
            
            //HACK:
            //MdrtbUtil.getMDRTBConceptByName(STR_DST_COMPLETE, new Locale("en", "US"))
            MdrtbFactory mu = new MdrtbFactory();
            Concept nonCodedConcept = mu.getConceptOtherMycobacteriaNonCoded();
            if (nonCodedConcept != null)
                map.put("OtherNonCodedId", nonCodedConcept.getConceptId());
            else
                map.put("OtherNonCodedId", 0);

            String anatLocList = as.getGlobalProperty("mdrtb.anatomical_locations_concept");
            
            Concept anatLocSet = MdrtbUtil.getMDRTBConceptByName(anatLocList, new Locale("en", "US"));
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
            
            
            Concept cultureMethodsSet = MdrtbUtil.getMDRTBConceptByName(cultureMethodList, new Locale("en", "US"));
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
            
            
            Concept smearMethodsSet = MdrtbUtil.getMDRTBConceptByName(smearMethodList, new Locale("en", "US"));
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
            
            
            Concept dstMethodsSet = MdrtbUtil.getMDRTBConceptByName(dstMethodList, new Locale("en", "US"));
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

    private List<Concept> getDSTTests(AdministrationService as,
            ConceptService cs) {
        String testList = as.getGlobalProperty("mdrtb.DST_drug_list");
        List<Concept> concepts = new ArrayList<Concept>();
        
        
        Concept dstTestConcept = MdrtbUtil.getMDRTBConceptByName(testList, new Locale("en", "US"));
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
                    ConceptName cn = concept.getName(new Locale("en"));
                    if (cn.getName().equals(drugName))
                        concepts.add(cn.getConcept());
                }
            }
        }
        return concepts;
    }
    
    private List<Concept> getOrganismTypes(AdministrationService as, ConceptService cs){
        List<Concept> res = new ArrayList<Concept>();
        String ots = as.getGlobalProperty("mdrtb.organism_type");
        
        Concept otsConcept = MdrtbUtil.getMDRTBConceptByName(ots, new Locale("en", "US"));
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
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"));
                if (c != null)
                res.add(c);
            }
        }
        return res;
    }
    
    private List<Concept> getDSTRes(AdministrationService as,
            ConceptService cs) {
        List<Concept> dstRes = new ArrayList<Concept>();
        String dstResList = as.getGlobalProperty("mdrtb.DST_result_list");
        Concept dstResListConcept =  MdrtbUtil.getMDRTBConceptByName(dstResList, new Locale("en", "US"));
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
    
    
    private List<Concept> getSmearRes(AdministrationService as, ConceptService cs){
        String smearResList = as.getGlobalProperty("mdrtb.smear_result_list");
        List<Concept> smearRes = new ArrayList<Concept>();
        Concept smearResListConcept = MdrtbUtil.getMDRTBConceptByName(smearResList, new Locale("en", "US"));
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
                
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"));
                if (c != null)
                smearRes.add(c);
            }
        }
        return smearRes;
    }
    
    private List<Concept> getCultureRes(AdministrationService as, ConceptService cs){
        String cultureResList = as.getGlobalProperty("mdrtb.culture_result_list");
        List<Concept> cultureRes = new ArrayList<Concept>();
        
        Concept cultureResListConcept = MdrtbUtil.getMDRTBConceptByName(cultureResList, new Locale("en", "US"));
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
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"));
                if (c != null)
                cultureRes.add(c);
            }     
        }
        return cultureRes;
    }

     
    
    private void readXML(HttpServletRequest request){
        //String httpBase = Context.getAdministrationService().getGlobalProperty("formentry.infopath_server_url");
        String httpBase = "http://localhost";
        if (httpBase.indexOf("/openmrs") > 0)
        httpBase = httpBase.substring(0, httpBase.indexOf("/openmrs"));
        String XMLlocation = httpBase + "/openmrs/moduleResources/mdrtb/mdrtbConcepts.xml";
                if (!XMLlocation.substring(10).contains(":"))
                XMLlocation = httpBase + Context.getAdministrationService().getGlobalProperty("mdrtb.webserver_port") + "/openmrs/moduleResources/mdrtb/mdrtbConcepts.xml";
        try{ 
            
        URL xmlURL = new URL(XMLlocation);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        log.info("xmlURL is set to " + xmlURL.toString());
        InputStream in = xmlURL.openStream();
        Document doc = db.parse(in);
        in.close();
        doc.getDocumentElement().normalize();
        Element concepts = doc.getDocumentElement();
            NodeList nodeList = concepts.getElementsByTagName("STR_TB_SMEAR_RESULT");
            Node node = nodeList.item(0);
            this.STR_TB_SMEAR_RESULT= node.getFirstChild().getNodeValue();  
            nodeList = concepts.getElementsByTagName("STR_TB_SAMPLE_SOURCE");
            node = nodeList.item(0);
            this.STR_TB_SAMPLE_SOURCE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_BACILLI");
            node = nodeList.item(0);
            this.STR_BACILLI = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_RESULT_DATE");
            node = nodeList.item(0);
            this.STR_RESULT_DATE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DATE_RECEIVED");
            node = nodeList.item(0);
            this.STR_DATE_RECEIVED = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TB_SMEAR_MICROSCOPY_METHOD");
            node = nodeList.item(0);
            this.STR_TB_SMEAR_MICROSCOPY_METHOD  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TB_CULTURE_RESULT");
            node = nodeList.item(0);
            this.STR_TB_CULTURE_RESULT  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_COLONIES");
            node = nodeList.item(0);
            this.STR_COLONIES  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_START_DATE");
            node = nodeList.item(0);
            this.STR_CULTURE_START_DATE  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TB_CULTURE_METHOD");
            node = nodeList.item(0);
            this.STR_TB_CULTURE_METHOD  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TYPE_OF_ORGANISM");
            node = nodeList.item(0);
            this.STR_TYPE_OF_ORGANISM  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_TYPE_OF_ORGANISM_NON_CODED");
            node = nodeList.item(0);
            this.STR_TYPE_OF_ORGANISM_NON_CODED  = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_COMPLETE");
            node = nodeList.item(0);
            this.STR_DST_COMPLETE = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_METHOD");
            node = nodeList.item(0);
            this.STR_DST_METHOD = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DIRECT_INDIRECT");
            node = nodeList.item(0);
            this.STR_DIRECT_INDIRECT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_COLONIES_IN_CONTROL");
            node = nodeList.item(0);
            this.STR_COLONIES_IN_CONTROL = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CONCENTRATION");
            node = nodeList.item(0);
            this.STR_CONCENTRATION = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SPUTUM_COLLECTION_DATE");
            node = nodeList.item(0);
            this.STR_SPUTUM_COLLECTION_DATE = node.getFirstChild().getNodeValue();    
            nodeList = concepts.getElementsByTagName("STR_DST_PARENT");
            node = nodeList.item(0);
            this.STR_DST_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_DST_RESULT_PARENT");
            node = nodeList.item(0);
            this.STR_DST_RESULT_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_SMEAR_PARENT");
            node = nodeList.item(0);
            this.STR_SMEAR_PARENT = node.getFirstChild().getNodeValue();
            nodeList = concepts.getElementsByTagName("STR_CULTURE_PARENT");
            node = nodeList.item(0);
            this.STR_CULTURE_PARENT = node.getFirstChild().getNodeValue();
        } catch (Exception ex){
            log.error("Could not read XML. Try accessing your server using the port number in the url.  Or, check the mdrtb.webserver_port global property.", ex);
        }
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
}
