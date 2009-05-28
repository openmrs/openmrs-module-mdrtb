package org.openmrs.module.mdrtb.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbContactPerson;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbPatient;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MdrtbManageContactsController extends SimpleFormController {
    
    


    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
   
    @Override 
   protected void initBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) throws Exception {

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object obj, BindException errors)
            throws Exception {
        if (Context.isAuthenticated()) {
            String action = request.getParameter("submit");
            String view = "CONTACTS";
            String patientIdString = request.getParameter("patientId");
            if (patientIdString != null && !patientIdString.equals("")) {
                Integer patientId = Integer.valueOf(patientIdString);
                MessageSourceAccessor msa = this.getMessageSourceAccessor();
                if (action != null && msa.getMessage("mdrtb.save").equals(action)) {
                    MdrtbFactory mu = new MdrtbFactory();

                   
                    SimpleDateFormat sdf = Context.getDateFormat();
                    ConceptService cs = Context.getConceptService();
                    ObsService os = Context.getObsService();
                    PatientService ps = Context.getPatientService();
                    PersonService perS = Context.getPersonService();
                    EncounterService es = Context.getEncounterService();
                    LocationService ls = Context.getLocationService();
                    Location unknownLocation = ls
                            .getLocation("Unknown Location");

                    MdrtbPatient mp = (MdrtbPatient) obj;
                    List<MdrtbContactPerson> mcps = mp.getContacts();
                    for (MdrtbContactPerson mcp : mcps) {
                        Integer contactId = mcp.getPerson().getPersonId();
                        String testResultString = request.getParameter("testResult_" + contactId);
                        String testResultDateString = request.getParameter("testResultDate_" + contactId);
                        String testResultType = request.getParameter("testResultType_" + contactId);
                        String testResultAction = request.getParameter("testResultAction_" + contactId);

                        String deleteContactString = request
                                .getParameter("deleteContact_" + contactId);
                        if (deleteContactString != null
                                && !deleteContactString.equals("")) {
                            Integer testInt = Integer.valueOf(0);
                            try {
                                testInt = Integer.valueOf(deleteContactString);
                            } catch (Exception ex) {
                            }
                            if (testInt.intValue() > 0) {
                                Relationship r = perS.getRelationship(testInt);
                                perS.voidRelationship(r,
                                        "voided by mdr-tb contacts module");
                            }
                        } else if (testResultAction != null) {
                            if (testResultAction.equals("2")
                                    && testResultString != null
                                    && !testResultString.equals("")
                                    && testResultDateString != null
                                    && !testResultDateString.equals("")
                                    && testResultType != null
                                    && !testResultType.equals("")) {
                                // create new obs group:
                                Obs oParent = new Obs();
                                Integer testResultAnswer = Integer
                                        .valueOf(testResultString);
                                Integer testResultTypeAnswer = Integer
                                        .valueOf(testResultType);
                                oParent.setConcept(mu.getConceptContactTestResultParent());
                                oParent.setCreator(Context
                                        .getAuthenticatedUser());
                                oParent.setDateCreated(new Date());
                                // this might not be right, but it satisfies not
                                // null requirements:
                                Date testResultDate = sdf
                                        .parse(testResultDateString);
                                oParent.setObsDatetime(testResultDate);

                                if (mcp.getPerson().isPatient()){
                                    for (Encounter enc : es.getEncountersByPatient(ps.getPatient(mcp.getPerson().getPersonId()))) {
                                        if (enc.getEncounterDatetime().getTime() == testResultDate
                                                .getTime()) {
                                            oParent.setEncounter(enc);
                                            oParent.setLocation(enc.getLocation());
                                            break;
                                        }
                                    }
                                }
                                
                                if (oParent.getLocation() == null)
                                    oParent.setLocation(unknownLocation);

                                oParent.setPerson(mcp.getPerson());
                                oParent.setVoided(false);

                                Obs oResult = new Obs();
                                oResult.setConcept(mu.getConceptSimpleTBResult());
                                oResult.setCreator(Context.getAuthenticatedUser());
                                oResult.setDateCreated(new Date());
                                // this might not be right, but it satisfies not
                                // null requirements:
                                oResult.setLocation(oParent.getLocation());
                                oResult.setObsDatetime(testResultDate);
                                oResult.setValueDatetime(testResultDate);
                                oResult.setValueCoded(cs.getConcept(testResultAnswer));
                                oResult.setPerson(mcp.getPerson());
                                oResult.setVoided(false);
                                if (oParent.getEncounter() != null)
                                    oResult.setEncounter(oParent
                                                    .getEncounter());
                                oParent.addGroupMember(oResult);

                                Obs oType = new Obs();
                                oType.setConcept(mu.getConceptSimpleTBTestType());
                                oType
                                        .setCreator(Context
                                                .getAuthenticatedUser());
                                oType.setDateCreated(new Date());
                                // this might not be right, but it satisfies not
                                // null requirements:
                                oType.setLocation(oParent.getLocation());
                                oType.setObsDatetime(testResultDate);
                                oType.setValueCoded(cs
                                        .getConcept(testResultTypeAnswer));
                                oType.setPerson(mcp.getPerson());
                                oType.setVoided(false);
                                if (oParent.getEncounter() != null)
                                    oType.setEncounter(oParent.getEncounter());
                                oParent.addGroupMember(oType);

                                os.saveObs(oParent, "");

                            }

                            if (testResultAction.equals("1")
                                    && testResultString != null
                                    && !testResultString.equals("")
                                    && testResultDateString != null
                                    && !testResultDateString.equals("")
                                    && testResultType != null
                                    && !testResultType.equals("")) {
                                Obs testResult = mcp.getTestResult();
                                Obs testType = mcp.getTestType();
                                Integer testResultAnswer = Integer
                                        .valueOf(testResultString);
                                Integer testResultTypeInt = Integer
                                        .valueOf(testResultType);
                                boolean saveTestResult = false;
                                boolean saveTestType = false;
                                if (testResult.getValueCoded().getConceptId()
                                        .intValue() != testResultAnswer
                                        .intValue()) {
                                    testResult.setValueCoded(cs
                                            .getConcept(testResultAnswer));
                                    saveTestResult = true;
                                }
                                Date date = null;
                                try {
                                    date = sdf.parse(testResultDateString);
                                } catch (Exception ex) {
                                    log.error("Invalid date string, ignoring",
                                            ex);
                                }

                                if (date != null) {
                                    if (testResult.getValueDatetime().getTime() != date
                                            .getTime())
                                        testResult.setValueDatetime(date);
                                    testResult.setObsDatetime(date);
                                    testType.setObsDatetime(date);
                                    saveTestResult = true;
                                    saveTestType = true;
                                }
                                if (testType.getValueCoded().getConceptId()
                                        .intValue() != testResultTypeInt
                                        .intValue()) {
                                    testType.setValueCoded(cs
                                            .getConcept(testResultTypeInt));
                                    saveTestType = true;
                                }
                                if (saveTestType || saveTestResult) {
                                    Obs oParent = testResult.getObsGroup();
                                    os.saveObs(oParent, "");
                                }

                            }

                            if (testResultAction.equals("3")) {
                                Obs testResult = mcp.getTestResult();
                                Obs oParent = testResult.getObsGroup();
                                os.voidObs(oParent, "");
                            }
                        }
                        
                    }
                    
                    //add new contacts
                    
                    String newPersonCounterString = request.getParameter("newRowsToBeAdded");
                    Integer newPersonCounter = Integer.valueOf(newPersonCounterString);
                    
                    for (int i = 1; i <= newPersonCounter.intValue(); i++){
                    
                        String personIdString = request.getParameter("newRelationshipId_" + i);
                        String givenNameString = request.getParameter("newGivenName_" + i);
                        String familyNameString = request.getParameter("newFamilyName_" +  i);
                        String newRelationshipTypeString = request.getParameter("newRelationshipType_" + i);
                        String newGenderString = request.getParameter("newGender_" + i);
                        String newContactIdString = request.getParameter("newContactIdString_" + i);
                        String newKnownMDRString = request.getParameter("newKnownMDR_" + i);
                        
                        if (personIdString != null && !personIdString.equals("")
                                && newRelationshipTypeString != null && !newRelationshipTypeString.equals("")
                                && newGenderString != null && !newGenderString.equals("")){
                       

                            String relationshipKey = newRelationshipTypeString.substring(newRelationshipTypeString.length()-1, newRelationshipTypeString.length());
                            newRelationshipTypeString = newRelationshipTypeString.substring(0, newRelationshipTypeString.length()-1);

                                Integer personIdTmp;
                                personIdTmp = Integer.valueOf(personIdString);
                          
                        
                            Person contact = perS.getPerson(personIdTmp);
                            
                            boolean test = false; 
                            if (familyNameString != null && !familyNameString.equals(""))
                                    test = true;
                            if (givenNameString != null && !givenNameString.equals(""))
                                    test = true;
                            if (personIdTmp.intValue() == 0 && test && contact == null){
                                
                              
                                contact = new Person();
                                contact.setDead(false);
                                contact.setPersonVoided(false);
                                contact.setPersonDateCreated(new Date());
                                contact.setPersonCreator(Context.getAuthenticatedUser());
                                contact.setGender(newGenderString);
                                
                                PersonName pn = new PersonName();
                                pn.setCreator(Context.getAuthenticatedUser());
                                pn.setDateCreated(new Date());
                                if (familyNameString != null && !familyNameString.equals(""))
                                    pn.setFamilyName(familyNameString);
                                if (givenNameString != null && !givenNameString.equals(""))
                                    pn.setGivenName(givenNameString);
                                pn.setPerson(contact);
                                pn.setVoided(false);
                                pn.setPreferred(true);
                                contact.addName(pn);
                                                    
                           
                            }
                          
                            if (contact != null){
                               
                                
                                if (newContactIdString != null && !newContactIdString.equals("")){
                           
                                    String contactAttributType = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_contact_id_attribute_type");
                                    PersonAttributeType contactAttType = perS.getPersonAttributeTypeByName(contactAttributType);
                                    PersonAttribute pa = new PersonAttribute();
                                    pa.setAttributeType(contactAttType);
                                    pa.setCreator(Context.getAuthenticatedUser());
                                    pa.setDateCreated(new Date());
                                    pa.setPerson(contact);
                                    pa.setValue(newContactIdString);
                                    pa.setVoided(false);
                                    contact.addAttribute(pa);
                                }
                          
                                contact = perS.savePerson(contact);
                                
                                
                                  //add relationship  
                                  Integer relationshipTypeId = Integer.valueOf(newRelationshipTypeString); 
                                  Relationship r = new Relationship();
                                  r.setCreator(Context.getAuthenticatedUser());
                                  r.setDateCreated(new Date());
                                  if (relationshipKey.equals("A")){
                                      r.setPersonA(contact);
                                      r.setPersonB(mp.getPatient());
                                  } else {
                                      r.setPersonB(contact);
                                      r.setPersonA(mp.getPatient());
                                  }
                                  r.setVoided(false);
                                  r.setRelationshipType(perS.getRelationshipType(relationshipTypeId));
                                  perS.saveRelationship(r);
                                  
                                  
                                  
                                  //newKnownMDRString
                                  if (newKnownMDRString != null && !newKnownMDRString.equals("")){
                                      Integer newKnownMDR = Integer.valueOf(newKnownMDRString);
                                      Concept cKnown = mu.getConceptKnownMDRCase();
                                      List<Obs> oList = os.getObservationsByPersonAndConcept(contact, cKnown);
                                      if (oList != null && oList.size() > 0){
                                          Obs oInner = oList.get(oList.size() -1);
                                          oInner.setValueNumeric(newKnownMDR.doubleValue());
                                          os.saveObs(oInner, "");
                                      } else {
                                          Obs o = new Obs();
                                          o.setConcept(cKnown);
                                          o.setCreator(Context.getAuthenticatedUser());
                                          o.setDateCreated(new Date());
                                          o.setLocation(unknownLocation);
                                          o.setObsDatetime(new Date());
                                          o.setPerson(contact);
                                          o.setValueNumeric(newKnownMDR.doubleValue());
                                          o.setVoided(false);
                                          os.saveObs(o, "");
                                      }
                                  }    
                                  
                                  
                            }
                            
                        }
                            
                    }
                    mu = null;
                    RedirectView rv = new RedirectView("/openmrs/module/mdrtb/mdrtbManageContacts.form");
                    rv.addStaticAttribute("patientId", patientId);
                    rv.addStaticAttribute("view", view);
                    return new ModelAndView(rv);

                } else if (action != null && msa.getMessage("mdrtb.done").equals(action)) {
                   
                    RedirectView rv = new RedirectView(getSuccessView());
                    rv.addStaticAttribute("patientId", patientId);
                    rv.addStaticAttribute("view", view);
                    return new ModelAndView(rv);
                }
            }
        }
        return showForm(request, response, errors);
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request){
        
        if (Context.isAuthenticated()) {
            MdrtbPatient mp = new MdrtbPatient(); 
            PersonService ps = Context.getPersonService();
            String patientIdString = request.getParameter("patientId"); 
            mp.setPatient(Context.getPatientService().getPatient(Integer.valueOf(patientIdString)));
            String rtString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_relationship_type");
            RelationshipType rt = ps.getRelationshipTypeByName(rtString);
            MdrtbFactory mu = new MdrtbFactory();
            Program program = mu.getMDRTBProgram();
            for (Relationship contact:ps.getRelationshipsByPerson(mp.getPatient())){   
                //bi-directional:
                Person contactTmp = null;
                if (contact.getPersonB().getPersonId() != mp.getPatient().getPatientId())
                    contactTmp = contact.getPersonB();
                else if (contact.getPersonA().getPersonId() != mp.getPatient().getPatientId())
                    contactTmp = contact.getPersonA();
                    if (contactTmp != null && !contact.getRelationshipType().equals(rt)){
                        MdrtbContactPerson mcp = new MdrtbContactPerson();
                        contactTmp = ps.getPerson(contactTmp.getPersonId());
                        mcp.setPerson(contactTmp);
                        mcp.setRelationship(contact);
                        mcp.setIsPatient(contactTmp.isPatient());
                        if (mcp.getPerson().isPatient()){
                            List<PatientProgram> ppsTmp = Context.getProgramWorkflowService().getPatientPrograms(Context.getPatientService().getPatient(contactTmp.getPersonId()), program, null, null, null, null, false);
                            if (ppsTmp != null && ppsTmp.size() > 0)
                                mcp.setIsTBPatient(true);
                        }
                        for(PersonAddress pa : contactTmp.getAddresses()){
                            if (!pa.getVoided() && (mcp.getAddress() == null || (mcp.getAddress().getDateCreated().getTime() < pa.getDateCreated().getTime() && !mcp.getAddress().isPreferred()) || pa.isPreferred()))
                                mcp.setAddress(pa);
                        }
                        
                        
                        List<Obs> oListPhone = Context.getObsService().getObservationsByPersonAndConcept(mcp.getPerson(), mu.getConceptPhoneNumber());
                        for (Obs phone:oListPhone){
                            if (!phone.getVoided() && (mcp.getPhone() == null || (mcp.getPhone().getObsDatetime().getTime() < phone.getObsDatetime().getTime())))
                            mcp.setPhone(phone);
                        }

                        List<Obs> oList = Context.getObsService().getObservationsByPersonAndConcept(mcp.getPerson(), mu.getConceptContactTestResultParent());
                        if (oList != null && oList.size() > 0){
                            for (Obs o:oList){
                                for (Obs oInner : o.getGroupMembers()){
                                    oInner.getConcept().getName().getName();
                                    if (oInner.getConcept().getConceptId() == mu.getConceptSimpleTBResult().getConceptId() && (mcp.getTestResult() == null || mcp.getTestResult().getObsDatetime().getTime() <= oInner.getObsDatetime().getTime()))
                                        mcp.setTestResult(oInner);
                                    if (oInner.getConcept().getConceptId() == mu.getConceptSimpleTBTestType().getConceptId() && (mcp.getTestType() == null || mcp.getTestType().getObsDatetime().getTime() <= oInner.getObsDatetime().getTime()))
                                        mcp.setTestType(oInner);
                                }
                            }
                        }
                      //contact person attribute
                        String contactAttributType = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_contact_id_attribute_type");
                        PersonAttributeType contactAttType = ps.getPersonAttributeTypeByName(contactAttributType);
                        if (contactAttType != null){
                            for (PersonAttribute patt : contactTmp.getActiveAttributes()){
                                if (!patt.isVoided() && patt.getAttributeType().equals(contactAttType) && (mcp.getMdrtbContactId() == null ||(mcp.getMdrtbContactId().getDateCreated().getTime() < patt.getDateCreated().getTime()))){
                                    mcp.setMdrtbContactId(patt);
                                }     
                            }
                        }
                        //known mdr
                        List<Obs> oKnownMDR = Context.getObsService().getObservationsByPersonAndConcept(mcp.getPerson(), mu.getConceptKnownMDRCase());
                        for (Obs mdr:oKnownMDR){
                            if (!mdr.getVoided() && (mcp.getKnownMdrtbContact() == null || (mcp.getKnownMdrtbContact().getObsDatetime().getTime() < mdr.getObsDatetime().getTime())))
                            mcp.setKnownMdrtbContact(mdr);
                        }
                            
                        
                        mp.addContact(mcp);
   
                    }
                                         
            }
            
            ObsService os = Context.getObsService();
            //next visit:
            
            Concept nextVisit = mu.getConceptNextVisit();
  
            List<Obs> obsVisits = os.getObservationsByPersonAndConcept(mp.getPatient(), nextVisit);
       
            for (Obs o:obsVisits){         
                if ((mp.getNextScheduledVisit() == null || mp.getNextScheduledVisit().getObsDatetime().before(o.getObsDatetime()))){
                    mp.setNextScheduledVisit(o);
                }   
            }
            
                 
            Concept cultureConversionConcept = mu.getConceptCultureConverstion();
            
            if (cultureConversionConcept.getConceptId() != null){
                List<Obs> oListTmp = os.getObservationsByPersonAndConcept(mp.getPatient(), cultureConversionConcept); 
                if (oListTmp != null) {
                    if (oListTmp.size() > 0){
                       //gets the latest 
                       Obs o = oListTmp.get(0);
                       for (Obs oTmp : oListTmp){
                           if (o.getValueDatetime().getTime() < oTmp.getValueDatetime().getTime())
                               o = oTmp;
                       }
                       mp.setCultureConversion(o);
                    }
                }   
            }
            
           Concept cultureReconversionConcept = mu.getConceptCultureReconversion();
            
            if (cultureReconversionConcept.getConceptId() != null){
                List<Obs> oListTmp = os.getObservationsByPersonAndConcept(mp.getPatient(), cultureReconversionConcept); 
                if (oListTmp != null && oListTmp.size() > 0) {
                       //gets the latest 
                        Obs o = oListTmp.get(0);
                        for (Obs oTmp : oListTmp){
                            if (o.getValueDatetime().getTime() < oTmp.getValueDatetime().getTime())
                                o = oTmp;
                        }
                       mp.setCultureReconversion(o);
                }
            }
            
            //set program

            List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(mp.getPatient(), program, null, null, null, null, false);
            for (PatientProgram pp : pps){
                if (pp.getDateCompleted() == null && pp.getProgram().equals(program) && !pp.getVoided()){
                    mp.setPatientProgram(pp);
                    break;
                }    
            }
            
            if (mp.getPatientProgram() != null){
                Set<ProgramWorkflowState> pwsSet = mu.getStatesCultureStatus();
                Set<PatientState> psSet = mp.getPatientProgram().getStates();
                Date dateTmp = new Date(0);
                for (PatientState patientState : psSet){
                    if (pwsSet.contains(patientState.getState()) && patientState.getEndDate() == null && !patientState.getVoided()){
                        mp.setCultureStatus(patientState);
                    }         
                }
            }
            
            //TODO: programatically figure out treatment start date?
            Concept c = mu.getConceptTreatmentStartDate();
            List<Obs> obsTmp = os.getObservationsByPersonAndConcept(mp.getPatient(), c);
            if (obsTmp.size() > 0)
                mp.setTreatmentStartDate(obsTmp.get(obsTmp.size()-1));
            
            //set patientId
            String piList = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_identifier_type");
            if (piList == null || piList.equals(""))
                    mp.setPatientIdentifier(mp.getPatient().getPatientIdentifier());
            else {
                try {
                    PatientIdentifier pi = new PatientIdentifier();
                    pi.setPreferred(false);
                    for (StringTokenizer st = new StringTokenizer(piList, "|"); st.hasMoreTokens(); ) {
                        String s = st.nextToken().trim();
                        PatientIdentifier piTmp = mp.getPatient().getPatientIdentifier(s);
                        if (piTmp != null)
                            pi = piTmp;
                        if (pi.getPreferred())
                            break;                                
                    }   
                    if (pi.getPatient() != null)
                        mp.setPatientIdentifier(pi);
                    else   
                        mp.setPatientIdentifier(mp.getPatient().getPatientIdentifier());
                } catch (Exception ex){
                    mp.setPatientIdentifier(mp.getPatient().getPatientIdentifier());
                }
            }
            
          
            mp.setGivenName(mp.getPatient().getGivenName());
            mp.setMiddleName(mp.getPatient().getMiddleName());
            mp.setFamilyName(mp.getPatient().getFamilyName());
            mp.setFamilyNameTwo(mp.getPatient().getPersonName().getFamilyName2());
            mu = null;
            return mp;
        } else  return "";
        
    }

    @Override
    protected Map referenceData(HttpServletRequest request, Object obj, Errors errs) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            MdrtbFactory mu = new MdrtbFactory();
            PatientService ps = Context.getPatientService();
            AdministrationService as =  Context.getAdministrationService();
            map.put("tbResultConceptId", mu.getConceptSimpleTBResult().getConceptId());
            map.put("tbTypeConceptId", mu.getConceptSimpleTBTestType().getConceptId());
            map.put("relationshipTypes", Context.getPersonService().getAllRelationshipTypes());
            map.put("testResultResponses", mu.getConceptSimpleTBResult().getAnswers());
            map.put("testTypeResponses", mu.getConceptSimpleTBTestType().getAnswers());
            String dateFormat = Context.getDateFormat().toPattern();
            String relationshipTypesString = as.getGlobalProperty("mdrtb.patient_identifier_type_list");
            
            List<PatientIdentifierType>  pitList = new ArrayList<PatientIdentifierType>();
            for (StringTokenizer st = new StringTokenizer(relationshipTypesString, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim();
                PatientIdentifierType pitTmp = ps.getPatientIdentifierTypeByName(s);
                if (pitTmp != null)
                    pitList.add(pitTmp);
            }    
            map.put("patientIdentifierTypes", pitList);
            map.put("dateFormat", dateFormat);
            MessageSourceAccessor msa = getMessageSourceAccessor();
            map.put("daysOfWeek", "'" + msa.getMessage("mdrtb.sunday")+ "','" + msa.getMessage("mdrtb.monday")+ "','" + msa.getMessage("mdrtb.tuesday") + "','" + msa.getMessage("mdrtb.wednesday")+ "','" + msa.getMessage("mdrtb.thursday")+ "','" + msa.getMessage("mdrtb.friday")+ "','"
                    + msa.getMessage("mdrtb.saturday")+ "','" + msa.getMessage("mdrtb.sun")+ "','" + msa.getMessage("mdrtb.mon")+ "','"+ msa.getMessage("mdrtb.tues")+ "','"+ msa.getMessage("mdrtb.wed")+ "','"+ msa.getMessage("mdrtb.thurs")+ "','"+ msa.getMessage("mdrtb.fri")+ "','" + msa.getMessage("mdrtb.sat") + "'");
            map.put("monthsOfYear", "'" + msa.getMessage("mdrtb.january")+ "','"+ msa.getMessage("mdrtb.february")+ "','"+ msa.getMessage("mdrtb.march")+ "','"+ msa.getMessage("mdrtb.april")+ "','"+ msa.getMessage("mdrtb.may")+ "','"+ msa.getMessage("mdrtb.june")+ "','"+ msa.getMessage("mdrtb.july")+ "','"+ msa.getMessage("mdrtb.august")+ "','"
                    + msa.getMessage("mdrtb.september")+ "','"+ msa.getMessage("mdrtb.october")+ "','"+ msa.getMessage("mdrtb.november")+ "','"+ msa.getMessage("mdrtb.december")+ "','"+ msa.getMessage("mdrtb.jan")+ "','"+ msa.getMessage("mdrtb.feb")+ "','"+ msa.getMessage("mdrtb.mar")+ "','"+ msa.getMessage("mdrtb.ap")+ "','"+ msa.getMessage("mdrtb.may")+ "','"
                    + msa.getMessage("mdrtb.jun")+ "','"+ msa.getMessage("mdrtb.jul")+ "','"+ msa.getMessage("mdrtb.aug")+ "','"+ msa.getMessage("mdrtb.sept")+ "','"+ msa.getMessage("mdrtb.oct")+ "','"+ msa.getMessage("mdrtb.nov")+ "','"+ msa.getMessage("mdrtb.dec")+ "'");
            mu = null;
        }
        return map;
    }
    
}
