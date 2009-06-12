package org.openmrs.module.mdrtb.web.controller;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbOverviewObj;
import org.openmrs.module.mdrtb.propertyeditor.ObsEditor;
import org.openmrs.propertyeditor.ConceptClassEditor;
import org.openmrs.propertyeditor.ConceptDatatypeEditor;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonAttributeEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MdrtbPatientStatusAdminController extends SimpleFormController {

    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    @Override
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
        binder.registerCustomEditor(org.openmrs.PersonAttribute.class, 
                new PersonAttributeEditor());
    
    }
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {

        Map<String,Object> map = new HashMap<String,Object>();
        if (Context.isAuthenticated()){

        
            String dateFormat = Context.getDateFormat().toPattern();
            map.put("dateFormat", dateFormat);
            
            MessageSourceAccessor msa = getMessageSourceAccessor();
            map.put("daysOfWeek", "'" + msa.getMessage("mdrtb.sunday")+ "','" + msa.getMessage("mdrtb.monday")+ "','" + msa.getMessage("mdrtb.tuesday") + "','" + msa.getMessage("mdrtb.wednesday")+ "','" + msa.getMessage("mdrtb.thursday")+ "','" + msa.getMessage("mdrtb.friday")+ "','"
                    + msa.getMessage("mdrtb.saturday")+ "','" + msa.getMessage("mdrtb.sun")+ "','" + msa.getMessage("mdrtb.mon")+ "','"+ msa.getMessage("mdrtb.tues")+ "','"+ msa.getMessage("mdrtb.wed")+ "','"+ msa.getMessage("mdrtb.thurs")+ "','"+ msa.getMessage("mdrtb.fri")+ "','" + msa.getMessage("mdrtb.sat") + "'");
            map.put("monthsOfYear", "'" + msa.getMessage("mdrtb.january")+ "','"+ msa.getMessage("mdrtb.february")+ "','"+ msa.getMessage("mdrtb.march")+ "','"+ msa.getMessage("mdrtb.april")+ "','"+ msa.getMessage("mdrtb.may")+ "','"+ msa.getMessage("mdrtb.june")+ "','"+ msa.getMessage("mdrtb.july")+ "','"+ msa.getMessage("mdrtb.august")+ "','"
                    + msa.getMessage("mdrtb.september")+ "','"+ msa.getMessage("mdrtb.october")+ "','"+ msa.getMessage("mdrtb.november")+ "','"+ msa.getMessage("mdrtb.december")+ "','"+ msa.getMessage("mdrtb.jan")+ "','"+ msa.getMessage("mdrtb.feb")+ "','"+ msa.getMessage("mdrtb.mar")+ "','"+ msa.getMessage("mdrtb.ap")+ "','"+ msa.getMessage("mdrtb.may")+ "','"
                    + msa.getMessage("mdrtb.jun")+ "','"+ msa.getMessage("mdrtb.jul")+ "','"+ msa.getMessage("mdrtb.aug")+ "','"+ msa.getMessage("mdrtb.sept")+ "','"+ msa.getMessage("mdrtb.oct")+ "','"+ msa.getMessage("mdrtb.nov")+ "','"+ msa.getMessage("mdrtb.dec")+ "'");
            
            MdrtbFactory mu = new MdrtbFactory();
            map.put("outcomeStates", mu.getStatesOutcomes());
            map.put("patientStates", mu.getStatesPatientStatus());
            map.put("locations", Context.getLocationService().getAllLocations());
        }
        return map ;
    }


    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object, BindException exceptions) throws Exception {       
        
        if (Context.isAuthenticated()){
            ProgramWorkflowService pws = Context.getProgramWorkflowService();
            ObsService os = Context.getObsService();
            String action = request.getParameter("submit");
            PersonService perS = Context.getPersonService();
            MessageSourceAccessor msa = this.getMessageSourceAccessor();
            if (action.equals(msa.getMessage("submit"))){
                List<MdrtbOverviewObj> obj = (List<MdrtbOverviewObj>) object;
                String patientProgramEndDateRoot = "patientProgramEndDate_";
                String outcomeRoot = "outcome_";
                String stateRoot = "state_";
                String treatmentStartDateRoot = "treatmentStartDate_";
                String treatmentEndDateRoot = "treatmentEndDate_";
                String outcomeDateRoot = "outcomeStartDate_";
                String stateDateRoot = "stateStartDate_";
                String locationRoot = "healthCenter_";
                
                int i = 0;
                for (MdrtbOverviewObj moo : obj){
                    String patientProgramEndDateStringF = patientProgramEndDateRoot + i;
                    String outcomeStringF = outcomeRoot + i;
                    String stateStringF = stateRoot + i;
                    String treatmentStartStringF = treatmentStartDateRoot + i;
                    String treatmentEndStringF = treatmentEndDateRoot + i;
                    String outcomeDateStringF = outcomeDateRoot + i;
                    String stateDateStringF = stateDateRoot + i;
                    String locationStringF = locationRoot + i;
                    
                    String patientProgramEndDateString = request.getParameter(patientProgramEndDateStringF);
                    String outcomeString = request.getParameter(outcomeStringF);
                    String stateString = request.getParameter(stateStringF);
                    String treatmentStartString = request.getParameter(treatmentStartStringF);
                    String treatmentEndString = request.getParameter(treatmentEndStringF);
                    String outcomeDateString = request.getParameter(outcomeDateStringF);
                    String stateDateString = request.getParameter(stateDateStringF);
                    String locationString = request.getParameter(locationStringF);
                    
                    SimpleDateFormat sdf = Context.getDateFormat();
                    
                    Date programEndDate = null;
                    Date treatmentStartDate = null;
                    Date treatmentStopDate = null;
                    Date outcomeDate = null;
                    Date stateDate = null;
                    
                   try {
                       programEndDate = sdf.parse(patientProgramEndDateString);
                   } catch (Exception ex){}
                   try {
                       treatmentStartDate = sdf.parse(treatmentStartString);
                   } catch (Exception ex){}
                   try {
                       treatmentStopDate = sdf.parse(treatmentEndString);
                   } catch (Exception ex){}
                   try {
                       outcomeDate = sdf.parse(outcomeDateString);
                   } catch (Exception ex){}
                   try {
                       stateDate = sdf.parse(stateDateString);
                   } catch (Exception ex){}
                   
                   Integer outcomeWorkflowStateId = null;
                   Integer stateWorkflowStateId = null;
                   Integer locationId = null;
                   
                   try{
                       outcomeWorkflowStateId = Integer.valueOf(outcomeString);
                   } catch (Exception ex){}
                   try{
                       stateWorkflowStateId = Integer.valueOf(stateString);
                   } catch (Exception ex){}
                   try{
                       locationId = Integer.valueOf(locationString);
                   } catch (Exception ex){}
                   



                    
                   
                   //health center
                   
                   Patient patient = moo.getPatient();
                   if (locationString != null && !locationString.equals("")){
                       PersonAttributeType pat = perS.getPersonAttributeTypeByName("Health Center");
                       PersonAttribute pa = moo.getHealthCenter();
                       if (pa == null){
                           pa = new PersonAttribute();
                           pa.setAttributeType(pat);
                           pa.setCreator(Context.getAuthenticatedUser());
                           pa.setDateCreated(new Date());
                           pa.setPerson(patient);
                           pa.setValue(locationString);
                           pa.setVoided(false);
                         
                       }
                       pa.setValue(locationString);
                       patient.addAttribute(pa);
                       Context.getPatientService().savePatient(patient);
                   }
                   
                   
                   MdrtbFactory mu = new MdrtbFactory();
                   boolean update = false;
                   PatientProgram pp = moo.getPatientProgram();
                   ProgramWorkflow pw = pp.getProgram().getWorkflowByName(mu.getSTR_TREATMENT_OUTCOME_PARENT());
                   PatientState ps = pp.getCurrentState(pw);
                   if (outcomeWorkflowStateId != null){
                       ProgramWorkflowState pxws = pw.getState(outcomeWorkflowStateId);
                       if (ps == null || !ps.getState().equals(pxws) || (ps.getState().equals(pxws) && outcomeDate != null && outcomeDate.getTime() != ps.getStartDate().getTime())){
                           if (outcomeDate == null){
                               mu.transitionToStateNoErrorChecking(pp, pxws, new Date());
                               update = true;
                               
                           } else {
                               mu.transitionToStateNoErrorChecking(pp, pxws, outcomeDate);  
                               update = true;
                           }
                       }
                   }
                   
                   //patient status
                   ProgramWorkflow pwTwo = pp.getProgram().getWorkflowByName(mu.getSTR_TUBERCULOSIS_PATIENT_STATUS_PARENT());
                   PatientState psTwo = pp.getCurrentState(pwTwo);
                   if (stateWorkflowStateId != null){
                       ProgramWorkflowState pxwsTwo = pwTwo.getState(stateWorkflowStateId);
                       if (psTwo == null || !psTwo.getState().equals(pxwsTwo) || (psTwo.getState().equals(pxwsTwo) && stateDate != null && stateDate.getTime() != psTwo.getStartDate().getTime())){
                           if (stateDate == null)
                               mu.transitionToStateNoErrorChecking(pp, pxwsTwo, new Date());
                           else
                               mu.transitionToStateNoErrorChecking(pp, pxwsTwo, stateDate); 
                               update = true;
                       }
                   }
                   if (programEndDate != null){
                       moo.getPatientProgram().setDateCompleted(programEndDate);
                       moo.getPatientProgram().setChangedBy(Context.getAuthenticatedUser());
                       moo.getPatientProgram().setDateChanged(new Date());
                       update = true;
                   }
                   if (update)
                       pws.savePatientProgram(moo.getPatientProgram()); 
                    
                   
                   
                   Obs tsdObs = moo.getTreatmentStartDate();
                   Obs tstopObs = moo.getTreatmentStopDate();
//                 Date treatmentStartDate = null;
//                 Date treatmentStopDate = null;
                   if (treatmentStartDate != null){
                       tsdObs.setValueDatetime(treatmentStartDate);
                       if (locationId != null)
                           tsdObs.setLocation(Context.getLocationService().getLocation(locationId));
                       else {
                           tsdObs.setLocation(Context.getLocationService().getDefaultLocation());
                       }
                       os.saveObs(tsdObs, "");
                   }
                   
                   if (treatmentStopDate != null){
                       tstopObs.setValueDatetime(treatmentStopDate);
                       if (locationId != null)
                           tstopObs.setLocation(Context.getLocationService().getLocation(locationId));
                       else {
                           tstopObs.setLocation(Context.getLocationService().getDefaultLocation());
                       }
                       os.saveObs(tstopObs, "");
                   }
                   
                   
                   
                    
                    i ++;
                }
                
            }
        }      
        return new ModelAndView(new RedirectView(getSuccessView()));
    }


    /**
     * This class returns the form backing object.  This can be a string, a boolean, or a normal
     * java pojo.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception { 
       List<MdrtbOverviewObj> ret = new ArrayList<MdrtbOverviewObj>();
       if (Context.isAuthenticated()){
           MdrtbFactory mu = new MdrtbFactory();
           Program program = mu.getMDRTBProgram();
           ProgramWorkflowService progS = Context.getProgramWorkflowService();
           Collection<Program> cp = new HashSet<Program>();
           ObsService os = Context.getObsService();
           cp.add(program);
           Cohort c = Context.getPatientSetService().getPatientsInProgram(program, new Date(0), new Date());
           List<PatientProgram> ppList = progS.getPatientPrograms(c, cp);
           for (PatientProgram pp : ppList){
               MdrtbOverviewObj moo = new MdrtbOverviewObj(pp);
             //health center
               PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Health Center");
               for (PersonAttribute pa: pp.getPatient().getAttributes()){
                   if (pa.getAttributeType().equals(pat)){
                       moo.setHealthCenter(pa);
                       break;
                   }    
               } 
               if (moo.getHealthCenter() == null){
                   PersonAttribute paNew = new PersonAttribute();
                   paNew.setAttributeType(pat);
                   paNew.setCreator(Context.getAuthenticatedUser());
                   paNew.setDateCreated(new Date());
                   paNew.setPerson(pp.getPatient());
                   paNew.setVoided(false);
                   moo.setHealthCenter(paNew);
               }
               
               
               moo.setGivenName(pp.getPatient().getGivenName());
               moo.setMiddleName(pp.getPatient().getMiddleName());
               moo.setFamilyName(pp.getPatient().getFamilyName());
               //patient identifier
               String piList = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_identifier_type");
               Set<PatientIdentifier> identifiers = pp.getPatient().getIdentifiers();
               boolean found = false;
               for (PatientIdentifier pi : identifiers){
                   if (pi.getIdentifierType().getName().equals(piList)){
                       moo.setPatientIdentifier(pi);
                       found = true;
                       break;
                   }
               }
               if (!found && identifiers.size() > 0){
                   for (PatientIdentifier pi : identifiers){
                       moo.setPatientIdentifier(pi);
                       break;
                   }
               } 
               
               //outcome
               Set<ProgramWorkflowState> pwsSet = mu.getStatesOutcomes();
               Set<PatientState> psSet = pp.getStates();
               for (PatientState ps : psSet){
                   if (pwsSet.contains(ps.getState()) && ps.getEndDate() == null && !ps.getVoided()){
                       moo.setOutcome(ps);
                   }         
               }
               //status
               pwsSet = mu.getStatesPatientStatus();
               psSet = pp.getStates();
               for (PatientState ps : psSet){
                   if (pwsSet.contains(ps.getState()) && ps.getEndDate() == null && !ps.getVoided()){
                       moo.setStatus(ps);
                   }         
               }
               //treatment start date
               Concept tsdConcept = mu.getConceptTreatmentStartDate();
               List<Obs> oList = os.getObservationsByPersonAndConcept(pp.getPatient(), tsdConcept);
               if (oList.size() > 0){
                   moo.setTreatmentStartDate(oList.get(oList.size() -1));
               } else {
                   //needs location
                   Obs o = new Obs();
                   o.setConcept(tsdConcept);
                   o.setCreator(Context.getAuthenticatedUser());
                   o.setDateCreated(new Date());
                   o.setObsDatetime(new Date());
                   o.setPerson(pp.getPatient());
                   o.setVoided(false);
                   moo.setTreatmentStartDate(o);
               }
               //treatment stop date
               //copy treatment start date
               Concept tstopdConcept = mu.getConceptTreatmentStopDate();
               oList = os.getObservationsByPersonAndConcept(pp.getPatient(), tstopdConcept);
               if (oList.size() > 0){
                   moo.setTreatmentStopDate(oList.get(oList.size() -1));
               } else {
                   //needs location
                   Obs o = new Obs();
                   o.setConcept(tstopdConcept);
                   o.setCreator(Context.getAuthenticatedUser());
                   o.setDateCreated(new Date());
                   o.setObsDatetime(new Date());
                   o.setPerson(pp.getPatient());
                   o.setVoided(false);
                   moo.setTreatmentStopDate(o);
               }
               ret.add(moo);
           }
       } 
       return ret;
    
    }    
    
    
}
