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
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class MdrtbAddNewTestContainerController extends SimpleFormController  {

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
                    MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                    MdrtbFactory mu = ms.getMdrtbFactory();
                    if (enc.getEncounterType() == null)
                        enc.setEncounterType(es.getEncounterType(as.getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
                    if (enc.getEncounterType() == null)
                        throw new RuntimeException("EncounterType is null.  Make sure that your global properties for MDRTB encounter types are valid.");

                    for (int i = 0; i < numRowsShown; i ++){
                        MdrtbSmearObj mso = mnto.getSmears().get(i);
                        MdrtbCultureObj mco = mnto.getCultures().get(i);
                        
                        //sputum collection date, a result, and num colonies/bacilli are the required elements
                        if (mso.getSmearResult().getValueCoded() != null 
                                && mso.getSmearResult().getObsDatetime() != null){
   
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
                                    enc.setEncounterDatetime(mso.getSmearResult().getObsDatetime());
                              
                                
                                //encounter and obs datetime stuff:
                                Date obsDate = new Date();
                                if (mso.getSmearResult().getObsDatetime() != null)
                                    obsDate = mso.getSmearResult().getObsDatetime();
                                parentObs.setObsDatetime(obsDate);

                                Obs smearResult = mso.getSmearResult();
                                smearResult.setValueDatetime(smearResult.getObsDatetime());
                                if (enc.getEncounterDatetime() == null)
                                    enc.setEncounterDatetime(smearResult.getObsDatetime());
                                smearResult.setLocation(parentObs.getLocation());
                                smearResult.setEncounter(enc);
                                    //TODO:HERE -- obs Datetime should get set by jsp
                                    //smearResult.setObsDatetime(smearResult.getValueDatetime());
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
                                && mco.getCultureResult().getObsDatetime() != null){
                            
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
                                enc.setEncounterDatetime(mco.getCultureResult().getObsDatetime());
                     
                            
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
                            //TODO: add formID to obs, based on new global property
                            String formIdString = as.getGlobalProperty("mdrtb.formIdToAttachToBacteriologyEntry");
                            try {
                                 Integer formId = Integer.valueOf(formIdString);
                                 enc.setForm(Context.getFormService().getForm(formId));
                            } catch (Exception ex){}
                            
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
                MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                MdrtbFactory mu = ms.getMdrtbFactory();
                MdrtbUtil.fixCultureConversions(patient, mu);
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
 
            User user = Context.getAuthenticatedUser();
            Patient patient = new Patient();
            MdrtbNewTestObj mnto = new MdrtbNewTestObj();
            PatientService ps = Context.getPatientService();
            String patientId = request.getParameter("patientId");
            if (patientId != null) {
                try {
                    int id = Integer.valueOf(patientId);
                    patient = ps.getPatient(id);
                    String view = "";
                    if (request.getParameter("action") != null)
                        view = request.getParameter("action");
                    mnto = new MdrtbNewTestObj(patient,user, view);
                    mnto.setPatient(patient);
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(new Date());
//                    cal.add(Calendar.MONTH, -6);
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
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            MdrtbFactory mu = ms.getMdrtbFactory();
            
            //map.put("testNames", MdrtbUtil.getDstDrugList(false, mu));
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
            
            //HACK:
            //MdrtbUtil.getMDRTBConceptByName(STR_DST_COMPLETE, new Locale("en", "US"))

            Concept nonCodedConcept = mu.getConceptOtherMycobacteriaNonCoded();
            if (nonCodedConcept != null)
                map.put("OtherNonCodedId", nonCodedConcept.getConceptId());
            else
                map.put("OtherNonCodedId", 0);

            String anatLocList = as.getGlobalProperty("mdrtb.anatomical_locations_concept");
            
            Concept anatLocSet = MdrtbUtil.getMDRTBConceptByName(anatLocList, new Locale("en", "US"), mu);
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
            
            
            Concept cultureMethodsSet = MdrtbUtil.getMDRTBConceptByName(cultureMethodList, new Locale("en", "US"), mu);
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
            
            
            Concept smearMethodsSet = MdrtbUtil.getMDRTBConceptByName(smearMethodList, new Locale("en", "US"), mu);
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
            
            
            Concept dstMethodsSet = MdrtbUtil.getMDRTBConceptByName(dstMethodList, new Locale("en", "US"), mu);
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
        enc.setForm(src.getForm());
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
