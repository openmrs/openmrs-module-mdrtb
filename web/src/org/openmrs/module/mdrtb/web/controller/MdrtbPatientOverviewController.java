package org.openmrs.module.mdrtb.web.controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbContactPerson;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbPatient;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.MdrtbConstants.MdrtbPatientDashboardTabs;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenSuggestion;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenUtils;
import org.openmrs.module.web.extension.PatientDashboardTabExt;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

@Deprecated
public class MdrtbPatientOverviewController extends SimpleFormController {

    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(MdrtbPatientOverviewController.class);

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
     
              
    }
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
	            AdministrationService as = Context.getAdministrationService();
	            FormService fs = Context.getFormService();
	            ConceptService cs = Context.getConceptService();
	            String view = request.getParameter("view");
	            if (view != null)
	                map.put("view", view);
	            else
	                map.put("view", getDefaultMdrtbPatientDashboardTab());
	           
	            	// availableForms
	            String formsList = as.getGlobalProperty("mdrtb.mdrtb_forms_list");
	            List<Form> forms = fs.getAllForms();
	            List<Form> mdrtbForms = new ArrayList<Form>();
	            List<Form> htmlForms = new ArrayList<Form>();
	            for (StringTokenizer st = new StringTokenizer(formsList, "|"); st.hasMoreTokens();) {
	                String formName = st.nextToken().trim();
	                
	                for (Form form : forms) {
	                    if (formName.equals(form.getName().trim()))
	                        mdrtbForms.add(form);
	                    if (formName.contains(":html") && formName.replaceAll(":html", "").equals(form.getName().trim()))
	                        htmlForms.add(form);
	                }
	            }
	            map.put("htmlForms", htmlForms);
	            map.put("mdrtbForms", mdrtbForms);
	            
	           
	            
	//            List<Drug> tbDrugs = new ArrayList<Drug>();
	//            try {
	//                List<Concept> mdrtbDrugs = cs.getConceptsByConceptSet(MdrtbUtil.getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtb_drugs"), new Locale("en")));
	//                for (Concept c : mdrtbDrugs) {
	//                    List<Drug> drugs = cs.getDrugsByConcept(c);
	//                    tbDrugs.addAll(drugs);
	//                }
	//            } catch (Exception ex) {
	//                throw new RuntimeException(
	//                        "The global property mdrtb.mdrtb_drugs_concept did not return a valid concept name");
	//            }
	//            map.put("tbDrugs", tbDrugs);
	            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
	            MdrtbFactory mu = ms.getMdrtbFactory(); 
	            
	            List<MdrtbRegimenSuggestion> suggestions =  ms.getStandardRegimens();
	            map.put("standardRegimens", suggestions);
	            
	            List<Drug> firstLineDrugs = new ArrayList<Drug>();
	            List<Concept> mdrtbDrugs = new ArrayList<Concept>();
	            try {
	                mdrtbDrugs = cs.getConceptsByConceptSet(MdrtbUtil.getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.first_line_drugs"), new Locale("en"), mu));
	                for (Concept c : mdrtbDrugs) {
	                    List<Drug> drugs = cs.getDrugsByConcept(c);
	                    firstLineDrugs.addAll(drugs);
	                }
	            } catch (Exception ex) {
	                throw new RuntimeException("The global property mdrtb.first_line_drugs did not return a valid concept name; check your global property, or maybe you need to rebuild concept words? ", ex);
	            }
	            
	            
	            map.put("firstLineDrugs", firstLineDrugs);
	            map.put("firstLineConcepts", mdrtbDrugs);
	            
	            List<Drug> injectibleDrugs = new ArrayList<Drug>();
	            List<Concept> mdrtbDrugConceptsInj = new ArrayList<Concept>();
	            try {
	                mdrtbDrugConceptsInj = cs.getConceptsByConceptSet(MdrtbUtil.getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.injectible_drugs"), new Locale("en"), mu));
	                for (Concept c : mdrtbDrugConceptsInj) {
	                    List<Drug> drugs = cs.getDrugsByConcept(c);
	                    injectibleDrugs.addAll(drugs);
	                }
	            } catch (Exception ex) {
	                throw new RuntimeException(
	                        "The global property mdrtb.injectible_drugs did not return a valid concept name");
	            }
	            map.put("injectibleDrugs", injectibleDrugs);
	            map.put("injectibleConcepts", mdrtbDrugConceptsInj);
	            
	            List<Drug> quinolones = new ArrayList<Drug>();
	            List<Concept> mdrtbDrugQ = new ArrayList<Concept>();
	            try {
	                Concept quinolonesConcept = MdrtbUtil.getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.quinolones"), new Locale("en"), mu);
	                mdrtbDrugQ = cs.getConceptsByConceptSet(quinolonesConcept);
	                for (Concept c : mdrtbDrugQ) {
	                    List<Drug> drugs = cs.getDrugsByConcept(c);
	                    quinolones.addAll(drugs);
	                }
	            } catch (Exception ex) {
	                throw new RuntimeException(
	                        "The global property mdrtb.quinolones did not return a valid concept name");
	            }
	            map.put("quinolones", quinolones);
	            map.put("quinolonesConcepts", mdrtbDrugQ);
	            
	            List<Drug> secondLineDrugs = new ArrayList<Drug>();
	            List<Concept> mdrtbDrugConceptsSecondLine = new ArrayList<Concept>();
	            try {
	                
	                mdrtbDrugConceptsSecondLine = cs.getConceptsByConceptSet(MdrtbUtil.getMDRTBConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.other_second_line"), new Locale("en"), mu));
	                for (Concept c : mdrtbDrugConceptsSecondLine) {
	                    List<Drug> drugs = cs.getDrugsByConcept(c);
	                    secondLineDrugs.addAll(drugs);
	                }
	            } catch (Exception ex) {
	                throw new RuntimeException(
	                        "The global property mdrtb.other_second_line_drugs did not return a valid concept name");
	            }
	            map.put("secondLineDrugs", secondLineDrugs);
	            map.put("secondLineConcepts", mdrtbDrugConceptsSecondLine);
	            
	            //TODO:  Add another set of drug concepts here:
	            List<Drug> otherDrugs = new ArrayList<Drug>();
	            List<Concept> otherDrugConcepts = new ArrayList<Concept>();
	           
	            
	            /*try{
	                otherDrugConcepts = Context.getConceptService().getConceptsByConceptSet(Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtb_other_second_line")));
	                for (Concept c : otherDrugConcepts) {
	                    List<Drug> drugs = Context.getConceptService().getDrugs(c);
	                    otherDrugs.addAll(drugs);
	                }
	            } catch (Exception ex){
	                throw new RuntimeException(
	                "Error occurred loading up the list of non-MDRTB drugs");
	            }*/
	            
	            
	            map.put("otherDrugs", otherDrugs);
	            map.put("otherDrugsConcepts", otherDrugConcepts);
	            
	            
	            map.put("discontinueReasons", MdrtbUtil.getDiscontinueReasons(mu));
	            
	            String dateFormat = Context.getDateFormat().toPattern();
	            map.put("dateFormat", dateFormat);
	            
	            //workflow states:
	            
	            
	            map.put("cultureStates", mu.getStatesCultureStatus());
	            map.put("outcomeStates", mu.getStatesOutcomes());
	            map.put("patientStates", mu.getStatesPatientStatus());
	            map.put("standardized", mu.getConceptStandardized());
	            map.put("empiric", mu.getConceptEmpiric());
	            map.put("individualized", mu.getConceptIndividualized());
	            
	            //more
	            Concept drugUse = mu.getConceptPatientClassDrugUse();
	            Concept prevTreat = mu.getConceptPatientClassPrevTreatment();
	            Concept tbCaseClass = mu.getConceptTBCaseClassification();
	            Concept hivResultStatus = mu.getConceptHIVStatus();
	            
	            
	            try {
	                map.put("prevDrugUse",drugUse.getAnswers());
	            } catch (Exception ex){throw new RuntimeException("CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG is returning null -- verify that this concept has a concept mapping with source org.openmrs.module.mdrtb");}
	            
	            try {
	                map.put("prevTreatment", prevTreat.getAnswers());
	            } catch (Exception ex){throw new RuntimeException("CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT is returning null -- verify that this concept has a concept mapping with source org.openmrs.module.mdrtb");}
	            
	            try {
	                map.put("tbCaseClass",tbCaseClass.getAnswers());
	            } catch (Exception ex){throw new RuntimeException("TUBERCULOSIS CASE CLASSIFICATION is returning null -- verify that this concept has a concept mapping with source org.openmrs.module.mdrtb");}
	            
	            try {
	                map.put("hivStatuses", hivResultStatus.getAnswers());
	            } catch (Exception ex){throw new RuntimeException("RESULT OF HIV TEST is returning null -- verify that this concept has a concept mapping with source org.openmrs.module.mdrtb");}
	            
	            map.put("locations", ms.getAllMdrtrbLocations(false));
	            
	            MessageSourceAccessor msa = getMessageSourceAccessor();
	            map.put("daysOfWeek", "'" + msa.getMessage("mdrtb.sunday")+ "','" + msa.getMessage("mdrtb.monday")+ "','" + msa.getMessage("mdrtb.tuesday") + "','" + msa.getMessage("mdrtb.wednesday")+ "','" + msa.getMessage("mdrtb.thursday")+ "','" + msa.getMessage("mdrtb.friday")+ "','"
	                    + msa.getMessage("mdrtb.saturday")+ "','" + msa.getMessage("mdrtb.sun")+ "','" + msa.getMessage("mdrtb.mon")+ "','"+ msa.getMessage("mdrtb.tues")+ "','"+ msa.getMessage("mdrtb.wed")+ "','"+ msa.getMessage("mdrtb.thurs")+ "','"+ msa.getMessage("mdrtb.fri")+ "','" + msa.getMessage("mdrtb.sat") + "'");
	            map.put("monthsOfYear", "'" + msa.getMessage("mdrtb.january")+ "','"+ msa.getMessage("mdrtb.february")+ "','"+ msa.getMessage("mdrtb.march")+ "','"+ msa.getMessage("mdrtb.april")+ "','"+ msa.getMessage("mdrtb.may")+ "','"+ msa.getMessage("mdrtb.june")+ "','"+ msa.getMessage("mdrtb.july")+ "','"+ msa.getMessage("mdrtb.august")+ "','"
	                    + msa.getMessage("mdrtb.september")+ "','"+ msa.getMessage("mdrtb.october")+ "','"+ msa.getMessage("mdrtb.november")+ "','"+ msa.getMessage("mdrtb.december")+ "','"+ msa.getMessage("mdrtb.jan")+ "','"+ msa.getMessage("mdrtb.feb")+ "','"+ msa.getMessage("mdrtb.mar")+ "','"+ msa.getMessage("mdrtb.ap")+ "','"+ msa.getMessage("mdrtb.may")+ "','"
	                    + msa.getMessage("mdrtb.jun")+ "','"+ msa.getMessage("mdrtb.jul")+ "','"+ msa.getMessage("mdrtb.aug")+ "','"+ msa.getMessage("mdrtb.sept")+ "','"+ msa.getMessage("mdrtb.oct")+ "','"+ msa.getMessage("mdrtb.nov")+ "','"+ msa.getMessage("mdrtb.dec")+ "'");
	            
	            SortedSet<String> drugUnits = new TreeSet<String>();
	            List<Drug> drugs = cs.getAllDrugs();
	            for (Drug drug:drugs){
	                if (drug.getUnits() != null && !drug.getUnits().equals("") && !drugUnits.contains(drug.getUnits()))
	                    drugUnits.add(drug.getUnits());
	            }
	            map.put("drugUnits", drugUnits);
	            map.put("concentrationConceptId", mu.getConceptConcentration().getConceptId().intValue());
	
	            Concept c = mu.getConceptCurrentRegimenType();
	            map.put("stEmpInd", c);
	            map.put("stEmpIndAnswers", c.getAnswers(false));
	            
	           // add this list of tabs to display to the module map
	           List<HashMap<String,String>> tabs = getMdrtbPatientDashboardTabs(); 
	           map.put("tabs", tabs);
	        	   
	           mu = null;
        	}
        return map;
    }


    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object, BindException exceptions) throws Exception {       
        RedirectView rv = new RedirectView(getSuccessView());
        String patientId = request.getParameter("patientId");
        String action = request.getParameter("submit");
        MessageSourceAccessor msa = this.getMessageSourceAccessor();
        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        MdrtbFactory mu = ms.getMdrtbFactory();
        String view = getDefaultMdrtbPatientDashboardTab();

        if (action != null && msa.getMessage("mdrtb.saveneworders").equals(action)){
                String numberOfNewOrdersString = request.getParameter("numberOfNewOrders");
                Integer numberOfNewOrders = new Integer(0);
                try {
                numberOfNewOrders = Integer.parseInt(numberOfNewOrdersString);
                } catch (Exception ex){
                    log.warn("Error figuring out how many new drug orders were entered -- check the request parameter numberOfNewOrders", ex);
                }  
                String newDrugRoot = "newDrug_"; //the concept
                String newDrugSelect = "drugSelect_"; //the drug
                String doseRoot = "dose_";
                String unitsRoot = "units_";
                String perDayRoot = "perDay_";
                String perWeekRoot = "perWeek_";
                String asNeededRoot = "asNeeded_";
                String startDateRoot = "startDate_";
                String stopDateRoot = "stopDate_";
                String instructionsRoot = "instructions_";
                String regimenTypeRoot = "regimenType_";
  
                    for (int i = 1; i <= numberOfNewOrders; i++){
                        String newDrugSelectConcept = newDrugRoot+i;
                        String newDrug = newDrugSelect+i;
                        String dose = doseRoot+i;
                        String units = unitsRoot+i;
                        String perDay = perDayRoot+i;
                        String perWeek = perWeekRoot+i;
                        String asNeeded = asNeededRoot+i;
                        String startDate = startDateRoot+i;
                        String stopDate = stopDateRoot+i;
                        String instructions = instructionsRoot+i;
                        String regimenType = regimenTypeRoot + i;
                        
                        String newDrugParam = request.getParameter(newDrug);
                        String newDrugConceptParam = request.getParameter(newDrugSelectConcept);
                        if (newDrugConceptParam != null && !isNumeric(newDrugConceptParam)){
                            
                            String startDateParam = request.getParameter(startDate);
                            String endDateParam = request.getParameter(stopDate);
                            
                            SimpleDateFormat sdf = Context.getDateFormat();
                            Date startDateObj = new Date();//today
                            if (startDateParam != null && !startDateParam.equals(""))
                                startDateObj = sdf.parse(startDateParam);
                            Date endDateObj = null;//null = open order
                            if (endDateParam != null && !endDateParam.equals(""))
                                endDateObj = sdf.parse(endDateParam);
                            
                            List<MdrtbRegimenSuggestion> suggestions =  ms.getStandardRegimens();
                            for (MdrtbRegimenSuggestion mrs : suggestions){
                                if (mrs.getCodeName().equals(newDrugConceptParam)){
                                    List<DrugOrder> newDOs = MdrtbRegimenUtils.regimenSuggestionToDrugOrders(mrs, Context.getPatientService().getPatient(Integer.valueOf(patientId)), startDateObj, endDateObj);     
                                    Integer regTypeInt = null;
                                    try {
                                        regTypeInt = Integer.valueOf(mrs.getRegimenType());
                                    } catch (Exception ex){
                                        log.error("Invalid regimen type read in from standard regimen xml file.  Could not convert regimen type value to an Integer.");
                                    }
                                    MdrtbRegimenUtils.reconcileAndSaveDrugOrders(newDOs, regTypeInt, Context.getPatientService().getPatient(Integer.valueOf(patientId)), startDateObj);
                                    break;
                                } 
                            }
                            
                            
                        } else if ((newDrugParam != null && !newDrugParam.equals("")) || (newDrugConceptParam != null && !newDrugConceptParam.equals(""))){
                           
                                String doseParam = request.getParameter(dose);
                                String unitsParam = request.getParameter(units);
                                String perDayParam = request.getParameter(perDay);
                                String perWeekParam = request.getParameter(perWeek);
                                String asNeededParam = request.getParameter(asNeeded);
                                String startDateParam = request.getParameter(startDate);
                                String stopDateParam = request.getParameter(stopDate);
                                String instructionsParam = request.getParameter(instructions);
                                if (doseParam != null && unitsParam != null 
                                        && startDateParam != null && stopDateParam != null){
                                   
                                    SimpleDateFormat sdf = Context.getDateFormat();
                                    Date startDateObj = sdf.parse(startDateParam);
                                    Date dateTmp = null;
                                    try {
                                        Integer tmpInt = Integer.parseInt(stopDateParam);
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(startDateObj);
                                        cal.add(Calendar.SECOND, tmpInt*60*60*24);
                                        dateTmp = cal.getTime();
                                    } catch (Exception ex){
                                        try{
                                        dateTmp = sdf.parse(stopDateParam);
                                        } catch (Exception ex2) {}
                                          
                                    }
                                    
                                    DrugOrder newDO = new DrugOrder();
                                    newDO.setAutoExpireDate(dateTmp);
                                    newDO.setComplex(false);
                                    if (newDrugParam != null && !newDrugParam.equals("")){
                                        Drug drugToAdd = Context.getConceptService().getDrug(Integer.valueOf(newDrugParam));    
                                        newDO.setConcept(drugToAdd.getConcept());
                                        newDO.setDrug(drugToAdd);
                                    }
                                    else
                                    newDO.setConcept(Context.getConceptService().getConcept(Integer.valueOf(newDrugConceptParam)));   
                                    newDO.setCreator(Context.getAuthenticatedUser());
                                    newDO.setDateCreated(new Date());

                                    if (dateTmp != null && dateTmp.before(new Date())){
                                        newDO.setDiscontinuedDate(dateTmp);
                                        newDO.setDiscontinued(true);
                                    } else {
                                        newDO.setDiscontinued(false);
                                    }

                                    newDO.setDose(Double.valueOf(doseParam));
                                    newDO.setFrequency(Integer.valueOf(perDayParam)+"/day x " + Integer.valueOf(perWeekParam) + " days/week");
                                    newDO.setInstructions(instructionsParam);
                                    newDO.setOrderType(new OrderType(new Integer(OpenmrsConstants.ORDERTYPE_DRUG)));
                                    newDO.setPatient(Context.getPatientService().getPatient(Integer.valueOf(patientId)));
                                    newDO.setPrn(asNeededParam != null ? true : false);
                                    newDO.setUnits(unitsParam);
                                    newDO.setStartDate(startDateObj);
                                    
                                    List<DrugOrder> newDOs = new ArrayList<DrugOrder>();
                                    newDOs.add(newDO);
                                    Integer regTypeInt = null;
                                    try {
                                        regTypeInt = Integer.valueOf(request.getParameter(regimenType));
                                    } catch (Exception ex){
                                        log.error("Invalid regimen type read in from standard regimen xml file.  Could not convert regimen type value to an Integer.");
                                    }
                                    MdrtbRegimenUtils.reconcileAndSaveDrugOrders(newDOs, regTypeInt, Context.getPatientService().getPatient(Integer.valueOf(patientId)), startDateObj);
                                        
                                } 
                            } 
                            
                    }
                    
                    view = MdrtbConstants.MdrtbPatientDashboardTabs.REG.name();
        }
        
        if (action != null && msa.getMessage("mdrtb.save").equals(action)){
            
            
            
            String outcomeStateVal = request.getParameter("outcomeStatus");
            String patientStateVal = request.getParameter("patientStatus");
            String programStartDateString = request.getParameter("programstartdate");
            String treatmentStartDate = request.getParameter("treatmentstartdate");
            String outcomeStateDateString = request.getParameter("outcomeStatusDate");
            String patientStateDateString = request.getParameter("patientStatusDate");
            String healthCenterIdString = request.getParameter("healthcenter");
            String healthDistrictString = request.getParameter("healthdistrict");
            String accordingToPrevTreatConceptIdString = request.getParameter("prevtreatment");
            String accordingToPrevDrugConceptIdString = request.getParameter("prevdruguse");
            String allergyComment = request.getParameter("allergycomment");
            String treatmentComment = request.getParameter("treatmentcomment");
            String tbClassificationConceptIdString = request.getParameter("tbclassification");
            String pulmonaryInt = request.getParameter("pulmonaryradio");
            String tbLocationString = request.getParameter("tblocation");
            String hivStatusConceptIdString = request.getParameter("hivstatus");
            String dateOfHIVTestString = request.getParameter("dateofhivtest");
            String cd4count = request.getParameter("cdfourcount");
            String durationofprevioustreatment = request.getParameter("durationofprevioustreatment");
            String previousregistrationnumber = request.getParameter("previousregistrationnumber");
            String previoustreatmentcenter = request.getParameter("previoustreatmentcenter");
            String referredby = request.getParameter("referredby");
            String transferedto = request.getParameter("transferedto");
            String transferedfrom = request.getParameter("transferedfrom");
            String onartString = request.getParameter("onart");
            String treatmentSupporterIdString = request.getParameter("treatmentSupporterId");
            String detectionDateString = request.getParameter("detectionDate");
            
                    
                   

            
            
            SimpleDateFormat sdf = Context.getDateFormat();
               
                Date outcomeStateDate = null;
                Date patientStateDate = null;
                if (outcomeStateDateString != null && !outcomeStateDateString.equals("")){
                    try {
                    outcomeStateDate = sdf.parse(outcomeStateDateString);
                    } catch (Exception ex){}
                }
                if (patientStateDateString != null && !patientStateDateString.equals("")){
                    try {
                        patientStateDate = sdf.parse(patientStateDateString);
                    } catch (Exception ex){}
                }
                
            MdrtbPatient mp = (MdrtbPatient) object;
            ProgramWorkflowService pws = Context.getProgramWorkflowService();
            ConceptService cs = Context.getConceptService();
            ObsService os = Context.getObsService();
            PatientProgram pp = mp.getPatientProgram();
            boolean update = false;
            User me = Context.getAuthenticatedUser();
            PersonService perS = Context.getPersonService();
            AdministrationService as = Context.getAdministrationService();
            PatientService patSer = Context.getPatientService();
                ////////////////////////
            //////////////ProgramWorkflows/////////
            ///////////////////////////////////
                //outcome status
                if (pp != null){
                
                    ProgramWorkflow pw = pp.getProgram().getWorkflowByName(mu.getSTR_TREATMENT_OUTCOME_PARENT());
                    PatientState ps = pp.getCurrentState(pw);
                    if (outcomeStateVal != null && !outcomeStateVal.equals("")){
                        ProgramWorkflowState pxws = pw.getState(Integer.valueOf(outcomeStateVal));
                        if (ps == null || !ps.getState().equals(pxws) || (ps.getState().equals(pxws) && outcomeStateDate != null && outcomeStateDate.getTime() != ps.getStartDate().getTime())){
                        	if (outcomeStateDate == null) {
                        		outcomeStateDate = new Date();
                        	}
                            mu.transitionToStateNoErrorChecking(pp, pxws, outcomeStateDate);
                            update = true;
                        }
                    }
                    
                    //patient status
                    ProgramWorkflow pwTwo = pp.getProgram().getWorkflowByName(mu.getSTR_TUBERCULOSIS_PATIENT_STATUS_PARENT());
                    PatientState psTwo = pp.getCurrentState(pwTwo);
                    if (patientStateVal != null && !patientStateVal.equals("")){
                        ProgramWorkflowState pxwsTwo = pwTwo.getState(Integer.valueOf(patientStateVal));
                        if (psTwo == null || !psTwo.getState().equals(pxwsTwo) || (psTwo.getState().equals(pxwsTwo) && patientStateDate != null && patientStateDate.getTime() != psTwo.getStartDate().getTime())){
                            if (patientStateDate == null)
                                mu.transitionToStateNoErrorChecking(pp, pxwsTwo, new Date());
                            else
                                mu.transitionToStateNoErrorChecking(pp, pxwsTwo, patientStateDate); 
                            update = true;
                        }
                    }
                    
                    //program start date
                    if (programStartDateString != null && !programStartDateString.equals("")){
                            Date newProgStartDate = sdf.parse(programStartDateString);
                            if (pp.getDateEnrolled().getTime() != newProgStartDate.getTime()){
                                pp.setDateEnrolled(newProgStartDate);
                                update = true;
                            }
                    }    
                    if (update)
                    pws.savePatientProgram(pp);  
                    
                    
                    /////////////////////////////////
                    //////////OBS////////////////////
                    /////////////////////////////////
                    
                    
                    List<Person> pList = new ArrayList<Person>();
                    pList.add(mp.getPatient());
                    List<Concept> cList = new ArrayList<Concept>();
                    
                    Concept c = mu.getConceptTreatmentStartDate();
                    Concept prevDrugConcept = mu.getConceptPatientClassDrugUse();
                    Concept prevTreatmentConcept = mu.getConceptPatientClassPrevTreatment();
                    Concept treatmentCommentConcept = mu.getConceptTreatmentPlanComment();
                    Concept alergyCommentConcept = mu.getConceptAllergyComment();                   
                    Concept tbClassificationConcept = mu.getConceptTBCaseClassification();
                    Concept pulmonaryConcept = mu.getConceptPulmonary();
                    Concept extrapulmonaryConcept = mu.getConceptExtraPulmonary();
                    Concept extrapulmonaryLocationConcept = mu.getConceptExtraPulmonaryLocation();
                    Concept hivStatusConcept = mu.getConceptHIVStatus();
                    Concept cd4Concept = mu.getConceptCD4();
                    Concept cd4percentConcept = mu.getConceptCD4Percent();          
                    Concept durationOfPrevTreatment = mu.getConceptPrevDuration();
                    Concept prevRegNumber = mu.getConceptPrevRegNum();
                    Concept prevTreatmentCenter = mu.getConceptPrevTreatmentCenter();
                    Concept referredByConcept = mu.getConceptPrevReferredBy();
                    Concept transferredToConcept = mu.getConceptTransferredTo();
                    Concept transferredFromConcept = mu.getConceptTransferredFrom();                 
                    Concept onartConcept = mu.getConceptOnART();
   
                    
                    cList.add(onartConcept);
                    cList.add(prevDrugConcept);
                    cList.add(prevTreatmentConcept);
                    cList.add(c);
                    cList.add(treatmentCommentConcept);
                    cList.add(alergyCommentConcept);
                    cList.add(tbClassificationConcept);
                    cList.add(pulmonaryConcept);
                    cList.add(extrapulmonaryConcept);
                    cList.add(extrapulmonaryLocationConcept);
                    cList.add(hivStatusConcept);
                    cList.add(cd4Concept);
                    cList.add(cd4percentConcept);
                    cList.add(durationOfPrevTreatment);
                    cList.add(prevRegNumber);
                    cList.add(prevTreatmentCenter);
                    cList.add(referredByConcept);
                    cList.add(transferredToConcept);
                    cList.add(transferredFromConcept);
                    
                    
                        //doing the following so that the db only gets hit once in loading obs:
                    List<Obs> oMasterList = os.getObservations(pList, null, cList, null, null, null, null, null, null, null, null, false);

                    //treatment start date:
                    if (treatmentStartDate != null && !treatmentStartDate.equals("")){
                        Date newDate = sdf.parse(treatmentStartDate);
                   
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(c) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }                       
                        if (oTmp != null && oTmp.getValueDatetime().getTime() != newDate.getTime()){
                            oTmp.setValueDatetime(newDate);
                            oTmp = os.saveObs(oTmp, "new treatment start date");
                            for (Obs oInner : obs){
                                if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                    os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                    
                            }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(c);
                            oTmp.setCreator(me);
                            oTmp.setDateCreated(new Date());
                            for (Encounter enc:Context.getEncounterService().getEncountersByPatient(mp.getPatient())){
                                if (enc.getEncounterDatetime().getTime() == newDate.getTime()){
                                    oTmp.setEncounter(enc);
                                    oTmp.setLocation(enc.getLocation());
                                    break;
                                }   
                            }
                            if (oTmp.getLocation() == null)
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            oTmp.setObsDatetime(newDate);
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueDatetime(newDate);
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    }
                        //prevTreatment
                        
                    if (accordingToPrevTreatConceptIdString != null && !accordingToPrevTreatConceptIdString.equals("")){
                        Integer conceptAnswerId = Integer.valueOf(accordingToPrevTreatConceptIdString);
                   
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(prevTreatmentConcept) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }                      
                        if (oTmp != null && oTmp.getValueCoded() != null && conceptAnswerId.intValue() != oTmp.getValueCoded().getConceptId().intValue()){
                            oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                            oTmp = os.saveObs(oTmp, "new previous treatment status");
                            for (Obs oInner : obs){
                                if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                    os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                    
                            }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(prevTreatmentConcept);
                            oTmp.setCreator(me);
                            oTmp.setDateCreated(new Date());
                            if (mp.getLocation() != null)
                                oTmp.setLocation(mp.getLocation());
                            else 
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            if (mp.getPatientProgram() != null)
                                oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                            if (oTmp.getObsDatetime() == null)
                                oTmp.setObsDatetime(new Date());
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    }
                    
                        //prevDrug
                    if (accordingToPrevDrugConceptIdString != null && !accordingToPrevDrugConceptIdString.equals("")){
                        Integer conceptAnswerId = Integer.valueOf(accordingToPrevDrugConceptIdString);
                   
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(prevDrugConcept) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }                       
                        if (oTmp != null && oTmp.getValueCoded() != null && conceptAnswerId.intValue() != oTmp.getValueCoded().getConceptId().intValue()){
                            oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                            oTmp = os.saveObs(oTmp, "new previous drug status");
                            for (Obs oInner : obs){
                                if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                    os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                    
                            }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(prevDrugConcept);
                            oTmp.setCreator(me);
                            oTmp.setDateCreated(new Date());
                            if (mp.getLocation() != null)
                                oTmp.setLocation(mp.getLocation());
                            else 
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            if (mp.getPatientProgram() != null)
                                oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                            if (oTmp.getObsDatetime() == null)
                                oTmp.setObsDatetime(new Date());
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    }     
                    
                    //treatment comment
                    
                    if (treatmentComment != null && !treatmentComment.equals("")){
                   
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(treatmentCommentConcept) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }    
                        if (oTmp != null && oTmp.getValueText() != null && treatmentComment.compareTo(oTmp.getValueText()) != 0){
                            oTmp.setValueText(treatmentComment);
                            oTmp = os.saveObs(oTmp, "new treatment comment");
                            for (Obs oInner : obs){
                                if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime()){
                                    try{
                                        os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                    } catch (Exception ex){}
                                }    
                                    
                            }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(treatmentCommentConcept);
                            oTmp.setCreator(me);
                            oTmp.setDateCreated(new Date());
                            if (mp.getLocation() != null)
                                oTmp.setLocation(mp.getLocation());
                            else 
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            if (mp.getPatientProgram() != null)
                                oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                            if (oTmp.getObsDatetime() == null)
                                oTmp.setObsDatetime(new Date());
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueText(treatmentComment);
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    }
                    
                    //allergy comment:
                    
                    if (allergyComment != null && !allergyComment.equals("")){
                        
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(alergyCommentConcept) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }                       
                        if (oTmp != null && oTmp.getValueText() != null && allergyComment.compareTo(oTmp.getValueText()) != 0){
                            oTmp.setValueText(allergyComment);
                            oTmp =os.saveObs(oTmp, "new allergy comment");
                            for (Obs oInner : obs){
                                if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                    os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                    
                            }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(alergyCommentConcept);
                            oTmp.setCreator(Context.getAuthenticatedUser());
                            oTmp.setDateCreated(new Date());
                            if (mp.getLocation() != null)
                                oTmp.setLocation(mp.getLocation());
                            else 
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            if (mp.getPatientProgram() != null)
                                oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                            if (oTmp.getObsDatetime() == null)
                                oTmp.setObsDatetime(new Date());
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueText(allergyComment);
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    }
                    
                    //tbClassification
                    
                    if (tbClassificationConceptIdString  != null && !tbClassificationConceptIdString.equals("")){
                        Integer conceptAnswerId = Integer.valueOf(tbClassificationConceptIdString);
                        
                        Date classificationObsDatetime = null;
                        if (detectionDateString != null && !detectionDateString.equals(""))
                            try {
                            classificationObsDatetime = sdf.parse(detectionDateString);
                            } catch (Exception ex){}
                            
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(tbClassificationConcept) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }                        
                        if (oTmp != null){
                               boolean test = false;
                               //compare value coded answers
                               if (oTmp.getValueCoded() != null && (conceptAnswerId.intValue() != oTmp.getValueCoded().getConceptId().intValue())){
                                    oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                                    test = true;
                               }
                               //compare obs datetimes
                               if (classificationObsDatetime != null && (oTmp.getObsDatetime() == null || oTmp.getObsDatetime().getTime() != classificationObsDatetime.getTime())){
                                   oTmp.setObsDatetime(classificationObsDatetime);
                                   test = true;
                              }
                               if (test){
                                   oTmp =os.saveObs(oTmp, "new tb classification");
                                   for (Obs oInner : obs){
                                       if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                           os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                           
                                   }
                               }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(tbClassificationConcept);
                            oTmp.setCreator(me);
                            oTmp.setDateCreated(new Date());
                            if (mp.getLocation() != null)
                                oTmp.setLocation(mp.getLocation());
                            else 
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            if ( classificationObsDatetime != null)
                                oTmp.setObsDatetime(classificationObsDatetime);
                            else if (mp.getPatientProgram() != null)
                                oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                            if (oTmp.getObsDatetime() == null)
                                oTmp.setObsDatetime(new Date());
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    } 
                    
                    
                    //pullmonary and extrapullmonary   pulmonary = 0, extrapulmonary = 1;
                    
                    
                    if (pulmonaryInt != null){
                        if (Integer.valueOf(pulmonaryInt).intValue() == 0){
                            boolean found = false;
                            for (Obs o : oMasterList){
                            
                                if (o.getConcept().equals(pulmonaryConcept)){
                                    found = true;
                                }
                                if (o.getConcept().equals(extrapulmonaryConcept)){
                                    os.voidObs(o, "changing to tb type pulmonary");
                                } 
                                if (o.getConcept().equals(extrapulmonaryLocationConcept)){
                                    os.voidObs(o, "changing to tb type pulmonary");
                                }
                                
                            }
                            if (!found){
                                Obs oTmp = new Obs();
                                oTmp.setConcept(pulmonaryConcept);
                                oTmp.setValueNumeric(Integer.valueOf(1).doubleValue());
                                oTmp.setCreator(me);
                                oTmp.setDateCreated(new Date());
                                if (mp.getLocation() != null)
                                    oTmp.setLocation(mp.getLocation());
                                else 
                                    oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                if (mp.getPatientProgram() != null)
                                    oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                if (oTmp.getObsDatetime() == null)
                                    oTmp.setObsDatetime(new Date());
                                oTmp.setPerson(mp.getPatient());
                                oTmp.setVoided(false);
                                os.saveObs(oTmp, "");
                            }
                        }  else if (Integer.valueOf(pulmonaryInt).intValue() == 1){
                            boolean found = false;
                            for (Obs o : oMasterList){
                            
                                if (o.getConcept().equals(extrapulmonaryConcept) && o.isVoided() == false){
                                    found = true;
                                }
                                if (o.getConcept().equals(pulmonaryConcept)){
                                    os.voidObs(o, "changing to tb type extrapulmonary");
                                }   
                            }
                            if (!found){
                                Obs oTmp = new Obs();
                                oTmp.setConcept(extrapulmonaryConcept);
                                oTmp.setValueNumeric(Integer.valueOf(1).doubleValue());
                                oTmp.setCreator(me);
                                oTmp.setDateCreated(new Date());
                                if (mp.getLocation() != null)
                                    oTmp.setLocation(mp.getLocation());
                                else 
                                    oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                if (mp.getPatientProgram() != null)
                                    oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                if (oTmp.getObsDatetime() == null)
                                    oTmp.setObsDatetime(new Date());
                                oTmp.setPerson(mp.getPatient());
                                oTmp.setVoided(false);
                                os.saveObs(oTmp, "");
                            } 
                        }
                    }
                    
                    //tb location
                    
                        if (tbLocationString  != null && !tbLocationString.equals("")){
                        
                        List<Obs> obs = new ArrayList<Obs>();
                        for (Obs o:oMasterList){
                            if (o.getConcept().equals(extrapulmonaryLocationConcept) && o.isVoided() == false)
                            obs.add(o);
                        }
                        Obs oTmp = null;
                        if (obs.size() > 0){
                            oTmp = obs.get(obs.size()- 1);  
                            obs.remove(obs.size()- 1);
                        }                       
                        if (oTmp != null && oTmp.getValueText() != null && tbLocationString.compareTo(oTmp.getValueText()) != 0){
                            oTmp.setValueText(tbLocationString);
                            oTmp = os.saveObs(oTmp, "new tb location");
                            for (Obs oInner : obs){
                                if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                    os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                    
                            }
                        } 
                        if (oTmp == null){
                            //if there is no previous obs to edit:
                            oTmp = new Obs();
                            oTmp.setConcept(extrapulmonaryLocationConcept);
                            oTmp.setCreator(me);
                            oTmp.setDateCreated(new Date());
                            if (mp.getLocation() != null)
                                oTmp.setLocation(mp.getLocation());
                            else 
                                oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                            if (mp.getPatientProgram() != null)
                                oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                            if (oTmp.getObsDatetime() == null)
                                oTmp.setObsDatetime(new Date());
                            oTmp.setPerson(mp.getPatient());
                            oTmp.setValueText(tbLocationString);
                            oTmp.setVoided(false);
                            os.saveObs(oTmp, "");
                        }
                    }
                    
                    //hiv test result
                        
                        Date killCD4OnThisDate = null;
                        if (hivStatusConceptIdString   != null && !hivStatusConceptIdString.equals("")){
                            Integer conceptAnswerId = Integer.valueOf(hivStatusConceptIdString);
                            
                            Date hivTestDate = null;
                            try{
                                hivTestDate =sdf.parse(dateOfHIVTestString);
                            } catch (Exception ex){}
                            if (hivTestDate == null && mp.getPatientProgram() != null)
                                hivTestDate = mp.getPatientProgram().getDateEnrolled();
                            if (hivTestDate == null)
                                hivTestDate = new Date();

                       
                            List<Obs> obs = new ArrayList<Obs>();
                            for (Obs o:oMasterList){
                                if (o.getConcept().equals(hivStatusConcept) && o.isVoided() == false)
                                obs.add(o);
                            }
                            Obs oTmp = null;
                            if (obs.size() > 0){
                                oTmp = obs.get(obs.size()- 1);  
                                obs.remove(obs.size()- 1);
                            }                       
                            if (oTmp != null && oTmp.getValueCoded() != null && (conceptAnswerId.intValue() != oTmp.getValueCoded().getConceptId().intValue() || oTmp.getObsDatetime().getTime() != hivTestDate.getTime())){
                                if (!oTmp.getObsDatetime().equals(hivTestDate))
                                    killCD4OnThisDate = oTmp.getObsDatetime();
                                oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                                oTmp.setObsDatetime(hivTestDate);
                                oTmp = os.saveObs(oTmp, "new hiv Status");
                                for (Obs oInner : obs){
                                    if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                        try{
                                            os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                        } catch (Exception ex){}
                                        
                                }
                               
                            } 
                            if (oTmp == null){
                                //if there is no previous obs to edit:
                                oTmp = new Obs();
                                oTmp.setConcept(hivStatusConcept);
                                oTmp.setCreator(me);
                                oTmp.setDateCreated(new Date());
                                if (mp.getLocation() != null)
                                    oTmp.setLocation(mp.getLocation());
                                else 
                                    oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                oTmp.setObsDatetime(hivTestDate);
                                oTmp.setPerson(mp.getPatient());
                                oTmp.setValueCoded(cs.getConcept(conceptAnswerId));
                                oTmp.setVoided(false);
                                os.saveObs(oTmp, "");
                            }
                        }
                    
                    //cd4
                        
                        if (cd4count != null && !cd4count.equals("")){
                           if (cd4count.contains("%")){
                               Integer cd4Int = Integer.valueOf(cd4count.replace("%", "").trim());
                               boolean found = false;
                            
                               for (Obs o:oMasterList){
                                   if (o.getConcept().getConceptId().intValue() == cd4Concept.getConceptId().intValue() && o.isVoided() == false
                                           && ((mp.getHivStatus() != null && mp.getHivStatus().getObsDatetime() != null
                                                   && o.getObsDatetime() != null
                                                   && o.getObsDatetime().getTime() == mp.getHivStatus().getObsDatetime().getTime())
                                           || (killCD4OnThisDate != null && killCD4OnThisDate.getTime() == o.getObsDatetime().getTime())))
                                       os.voidObs(o, "Changing to cd4 percent from cd4 count");

                                   if (o.getConcept().getConceptId().intValue() == cd4percentConcept.getConceptId().intValue() 
                                           && o.getObsDatetime() != null
                                           && ((mp.getHivStatus() != null 
                                                   && mp.getHivStatus().getObsDatetime() != null
                                                   && o.getObsDatetime().getTime() == mp.getHivStatus().getObsDatetime().getTime() 
                                                   && o.getValueNumeric() != null 
                                                   && o.getValueNumeric().intValue() != cd4Int.intValue()) 
                                           || (killCD4OnThisDate != null && killCD4OnThisDate.getTime() == o.getObsDatetime().getTime()))){

                                           os.voidObs(o, "updating cd4 percent");
                                           
                                   }
                                   
                                   if (o.getConcept().getConceptId().intValue() == cd4percentConcept.getConceptId().intValue() 
                                           && ((mp.getHivStatus() != null 
                                                   && o.getObsDatetime().getTime() == mp.getHivStatus().getObsDatetime().getTime() 
                                                   && o.getValueNumeric() != null 
                                                   && o.getValueNumeric().intValue() == cd4Int.intValue()) 
                                           || (killCD4OnThisDate != null && killCD4OnThisDate.getTime() == o.getObsDatetime().getTime()))){

                                           found = true;
                                           
                                   }
                                       
                               }
                               if (!found){
                                   Obs oTmp = new Obs();
                                   oTmp.setConcept(cd4percentConcept);
                                   oTmp.setCreator(me);
                                   oTmp.setDateCreated(new Date());
                                   if (mp.getLocation() != null)
                                       oTmp.setLocation(mp.getLocation());
                                   else 
                                       oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                   if (mp.getHivStatus() != null)
                                       oTmp.setObsDatetime(mp.getHivStatus().getObsDatetime());
                                   else
                                       oTmp.setObsDatetime(new Date());
                                   oTmp.setPerson(mp.getPatient());
                                   oTmp.setValueNumeric(cd4Int.doubleValue());
                                   oTmp.setVoided(false);
                                   os.saveObs(oTmp, "");
                               }
                               
                           } else {
                               Integer cd4Int = Integer.valueOf(cd4count.trim());
                               boolean found = false;
                               for (Obs o:oMasterList){
                                   if (o.getConcept().getConceptId().intValue() == cd4percentConcept.getConceptId().intValue()  && o.isVoided() == false
                                           && ((mp.getHivStatus() != null 
                                           && o.getObsDatetime().getTime() == mp.getHivStatus().getObsDatetime().getTime())
                                           || (killCD4OnThisDate != null && killCD4OnThisDate.getTime() == o.getObsDatetime().getTime())))
                                       os.voidObs(o, "Changing to cd4 count from cd4 percent");
                                   
                                   if (o.getConcept().getConceptId().intValue() == cd4Concept.getConceptId().intValue() 
                                           && ((mp.getHivStatus() != null 
                                                   && o.getObsDatetime().getTime() == mp.getHivStatus().getObsDatetime().getTime() 
                                                   && o.getValueNumeric() != null 
                                                   && o.getValueNumeric().intValue() != cd4Int.intValue()) 
                                           ||(killCD4OnThisDate != null &&  killCD4OnThisDate.getTime() == o.getObsDatetime().getTime()))){

                                           os.voidObs(o, "updating cd4 count");
                                       
                                   }
                                   
                                   if (o.getConcept().getConceptId().intValue() == cd4Concept.getConceptId().intValue() 
                                           && ((mp.getHivStatus() != null 
                                                   && o.getObsDatetime().getTime() == mp.getHivStatus().getObsDatetime().getTime() 
                                                   && o.getValueNumeric() != null 
                                                   && o.getValueNumeric().intValue() == cd4Int.intValue()) 
                                           ||(killCD4OnThisDate != null &&  killCD4OnThisDate.getTime() == o.getObsDatetime().getTime()))){

                                           found = true;
                                       
                                   }
                                       
                               }
                               if (!found){
                                   Obs oTmp = new Obs();
                                   oTmp.setConcept(cd4Concept);
                                   oTmp.setCreator(me);
                                   oTmp.setDateCreated(new Date());
                                   if (mp.getLocation() != null)
                                       oTmp.setLocation(mp.getLocation());
                                   else 
                                       oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                   if (mp.getHivStatus() != null)
                                       oTmp.setObsDatetime(mp.getHivStatus().getObsDatetime());
                                   else
                                       oTmp.setObsDatetime(new Date());
                                   oTmp.setPerson(mp.getPatient());
                                   oTmp.setValueNumeric(cd4Int.doubleValue());
                                   oTmp.setVoided(false);
                                   os.saveObs(oTmp, "");
                               }
                               
                               
                           }
                            
                        }
                        
                   
                           
                           // Transferred From:
                           
                               if (transferedfrom  != null && !transferedfrom.equals("")){
                               
                               List<Obs> obs = new ArrayList<Obs>();
                               for (Obs o:oMasterList){
                                   if (o.getConcept().equals(transferredFromConcept) && o.isVoided() == false)
                                   obs.add(o);
                               }
                               Obs oTmp = null;
                               if (obs.size() > 0){
                                   oTmp = obs.get(obs.size()- 1);  
                                   obs.remove(obs.size()- 1);
                               }                        
                               if (oTmp != null && oTmp.getValueText() != null && transferedfrom.compareTo(oTmp.getValueText()) != 0){
                                   oTmp.setValueText(transferedfrom);
                                   oTmp = os.saveObs(oTmp, "new transferred from value");
                                   for (Obs oInner : obs){
                                       if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                           try{
                                               os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                           } catch (Exception ex){}
                                           
                                   }
                               } 
                               if (oTmp == null){
                                   //if there is no previous obs to edit:
                                   oTmp = new Obs();
                                   oTmp.setConcept(transferredFromConcept);
                                   oTmp.setCreator(me);
                                   oTmp.setDateCreated(new Date());
                                   if (mp.getLocation() != null)
                                       oTmp.setLocation(mp.getLocation());
                                   else 
                                       oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                   if (mp.getPatientProgram() != null)
                                       oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                   if (oTmp.getObsDatetime() == null)
                                       oTmp.setObsDatetime(new Date());
                                   oTmp.setPerson(mp.getPatient());
                                   oTmp.setValueText(transferedfrom);
                                   oTmp.setVoided(false);
                                   os.saveObs(oTmp, "");
                               }
                           }
 
                           
                           //  Transferred To
                               if (transferedto  != null && !transferedto.equals("")){
                                   
                                   List<Obs> obs = new ArrayList<Obs>();
                                   for (Obs o:oMasterList){
                                       if (o.getConcept().equals(transferredToConcept) && o.isVoided() == false)
                                       obs.add(o);
                                   }
                                   Obs oTmp = null;
                                   if (obs.size() > 0){
                                       oTmp = obs.get(obs.size()- 1);  
                                       obs.remove(obs.size()- 1);
                                   }                       
                                   if (oTmp != null && oTmp.getValueText() != null && transferedto.compareTo(oTmp.getValueText()) != 0){
                                       oTmp.setValueText(transferedto);
                                       oTmp =  os.saveObs(oTmp, "new transferred to value");
                                       for (Obs oInner : obs){
                                           if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                               try{
                                                   os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                               } catch (Exception ex){}
                                               
                                       }
                                   } 
                                   if (oTmp == null){
                                       //if there is no previous obs to edit:
                                       oTmp = new Obs();
                                       oTmp.setConcept(transferredToConcept);
                                       oTmp.setCreator(me);
                                       oTmp.setDateCreated(new Date());
                                       if (mp.getLocation() != null)
                                           oTmp.setLocation(mp.getLocation());
                                       else 
                                           oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                       if (mp.getPatientProgram() != null)
                                           oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                       if (oTmp.getObsDatetime() == null)
                                           oTmp.setObsDatetime(new Date());
                                       oTmp.setPerson(mp.getPatient());
                                       oTmp.setValueText(transferedto);
                                       oTmp.setVoided(false);
                                       os.saveObs(oTmp, "");
                                   }
                               }
                         
                              
                           //  Referred By
                               if (referredby  != null && !referredby.equals("")){
                                   
                                   List<Obs> obs = new ArrayList<Obs>();
                                   for (Obs o:oMasterList){
                                       if (o.getConcept().equals(referredByConcept) && o.isVoided() == false)
                                       obs.add(o);
                                   }
                                   Obs oTmp = null;
                                   if (obs.size() > 0){
                                       oTmp = obs.get(obs.size()- 1);  
                                       obs.remove(obs.size()- 1);
                                   }                       
                                   if (oTmp != null && oTmp.getValueText() != null && referredby.compareTo(oTmp.getValueText()) != 0){
                                       oTmp.setValueText(referredby);
                                       oTmp =  os.saveObs(oTmp, "new referred by value");
                                       for (Obs oInner : obs){
                                           if (oInner.getObsId() != oTmp.getObsId() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                               try{
                                                   os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                               } catch (Exception ex){}
                                               
                                       }
                                   } 
                                   if (oTmp == null){
                                       //if there is no previous obs to edit:
                                       oTmp = new Obs();
                                       oTmp.setConcept(referredByConcept);
                                       oTmp.setCreator(me);
                                       oTmp.setDateCreated(new Date());
                                       if (mp.getLocation() != null)
                                           oTmp.setLocation(mp.getLocation());
                                       else 
                                           oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                       if (mp.getPatientProgram() != null)
                                           oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                       if (oTmp.getObsDatetime() == null)
                                           oTmp.setObsDatetime(new Date());
                                       oTmp.setPerson(mp.getPatient());
                                       oTmp.setValueText(referredby);
                                       oTmp.setVoided(false);
                                       os.saveObs(oTmp, "");
                                   }
                               }

                              
                            //  Previous Treatment Center
                               if (previoustreatmentcenter  != null && !previoustreatmentcenter.equals("")){
                                   
                                   List<Obs> obs = new ArrayList<Obs>();
                                   for (Obs o:oMasterList){
                                       if (o.getConcept().equals(prevTreatmentCenter) && o.isVoided() == false)
                                       obs.add(o);
                                   }
                                   Obs oTmp = null;
                                   if (obs.size() > 0){
                                       oTmp = obs.get(obs.size()- 1);     
                                       obs.remove(obs.size()- 1);
                                   }    
                                   if (oTmp != null && oTmp.getValueText() != null && previoustreatmentcenter.compareTo(oTmp.getValueText()) != 0){
                                       oTmp.setValueText(previoustreatmentcenter);
                                       oTmp =  os.saveObs(oTmp, "new previous treatment center value");
                                       for (Obs oInner : obs){
                                           if (oInner.getObsId().intValue() != oTmp.getObsId().intValue() && oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime())
                                               try{
                                                   os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                               } catch (Exception ex){}
                                               
                                       }
                                   } 
                                   if (oTmp == null){
                                       //if there is no previous obs to edit:
                                       oTmp = new Obs();
                                       oTmp.setConcept(prevTreatmentCenter);
                                       oTmp.setCreator(me);
                                       oTmp.setDateCreated(new Date());
                                       if (mp.getLocation() != null)
                                           oTmp.setLocation(mp.getLocation());
                                       else 
                                           oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                       if (mp.getPatientProgram() != null)
                                           oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                       if (oTmp.getObsDatetime() == null)
                                           oTmp.setObsDatetime(new Date());
                                       oTmp.setPerson(mp.getPatient());
                                       oTmp.setValueText(previoustreatmentcenter);
                                       oTmp.setVoided(false);
                                       os.saveObs(oTmp, "");
                                   }
                               }

                              
                             //  Previous Registration Number
                               if (previousregistrationnumber  != null && !previousregistrationnumber.equals("")){
                                   
                                   List<Obs> obs = new ArrayList<Obs>();
                                   for (Obs o:oMasterList){
                                       if (o.getConcept().equals(prevRegNumber) && o.isVoided() == false)
                                       obs.add(o);
                                   }
                                   Obs oTmp = null;
                                   if (obs.size() > 0){
                                       oTmp = obs.get(obs.size()- 1);  
                                       obs.remove(obs.size()- 1);
                                   }                        
                                   if (oTmp != null && oTmp.getValueText() != null && previousregistrationnumber.compareTo(oTmp.getValueText()) != 0){
                                       oTmp.setValueText(previousregistrationnumber);
                                       oTmp =  os.saveObs(oTmp, "new previous registration number");
                                       for (Obs oInner : obs){
                                           if (oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime() && oInner.getObsId() != oTmp.getObsId())
                                               try{
                                                   os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                               } catch (Exception ex){}
                                               
                                       }
                                   } 
                                   if (oTmp == null){
                                       //if there is no previous obs to edit:
                                       oTmp = new Obs();
                                       oTmp.setConcept(prevRegNumber);
                                       oTmp.setCreator(me);
                                       oTmp.setDateCreated(new Date());
                                       if (mp.getLocation() != null)
                                           oTmp.setLocation(mp.getLocation());
                                       else 
                                           oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                       if (mp.getPatientProgram() != null)
                                           oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                       if (oTmp.getObsDatetime() == null)
                                           oTmp.setObsDatetime(new Date());
                                       oTmp.setPerson(mp.getPatient());
                                       oTmp.setValueText(previousregistrationnumber);
                                       oTmp.setVoided(false);
                                       os.saveObs(oTmp, "");
                                   }
                               }

                              
                              // Duration of previous treatment (in months)
                               

                               if (durationofprevioustreatment  != null && !durationofprevioustreatment.equals("")){
                                   Integer prevTreatment = Integer.valueOf(durationofprevioustreatment);
                                   
                                   List<Obs> obs = new ArrayList<Obs>();                                 

                                   for (Obs o:oMasterList){
                                       if (o.getConcept().getConceptId().equals(durationOfPrevTreatment.getConceptId())){
                                       obs.add(o);}
                                   }
                                   Obs oTmp = null;
                                   if (obs.size() > 0){
                                       oTmp = obs.get(obs.size()- 1);  
                                       obs.remove(obs.size()- 1);
                                   }                        
                                   if (oTmp != null && oTmp.getValueNumeric() != null && prevTreatment.intValue() != oTmp.getValueNumeric().intValue()){
                                       oTmp.setValueNumeric(prevTreatment.doubleValue());
                                       oTmp = os.saveObs(oTmp, "new duration of previous treatment value");
                                       for (Obs oInner : obs){
                                           if (oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime() && oInner.getObsId() != oTmp.getObsId())
                                               try{
                                                   os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                               } catch (Exception ex){}
                                               
                                       }
                                   } 
                                   if (oTmp == null){
                                       //if there is no previous obs to edit:
                                       oTmp = new Obs();
                                       oTmp.setConcept(durationOfPrevTreatment);
                                       oTmp.setCreator(me);
                                       oTmp.setDateCreated(new Date());
                                       if (mp.getLocation() != null)
                                           oTmp.setLocation(mp.getLocation());
                                       else 
                                           oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                       if (mp.getPatientProgram() != null)
                                           oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                       if (oTmp.getObsDatetime() == null)
                                           oTmp.setObsDatetime(new Date());
                                       oTmp.setPerson(mp.getPatient());
                                       oTmp.setValueNumeric(prevTreatment.doubleValue());                                    
                                       oTmp.setVoided(false);
                                       os.saveObs(oTmp, "");
                                   }
                               }
                               
                            //Duration of previous treatment ends here
                               
                            //onART    
                        
                               if (onartString  != null && !onartString.equals("")){
                                   Integer onartInt = Integer.valueOf(onartString);
                                   
                                   List<Obs> obs = new ArrayList<Obs>();
                                   for (Obs o:oMasterList){
                                       if (o.getConcept().getConceptId() == onartConcept.getConceptId())
                                       obs.add(o);
                                   }
                                   Obs oTmp = null;
                                   if (obs.size() > 0){
                                       oTmp = obs.get(obs.size()- 1);  
                                       obs.remove(obs.size()- 1);
                                   }                        
                                   if (oTmp != null && oTmp.getValueNumeric() != null && onartInt.intValue() != oTmp.getValueNumeric().intValue()){
                                       oTmp.setValueNumeric(onartInt.doubleValue());
                                       oTmp = os.saveObs(oTmp, "new on ART value");
                                       for (Obs oInner : obs){
                                           if (oInner.getObsDatetime().getTime() == oTmp.getObsDatetime().getTime() && oInner.getObsId() != oTmp.getObsId())
                                               try{
                                                   os.voidObs(oInner, "replaced by most current observation of value on this date.");
                                               } catch (Exception ex){}
                                               
                                       }
                                   } 
                                   if (oTmp == null){
                                       //if there is no previous obs to edit:
                                       oTmp = new Obs();
                                       oTmp.setConcept(onartConcept);
                                       oTmp.setCreator(me);
                                       oTmp.setDateCreated(new Date());
                                       if (mp.getLocation() != null)
                                           oTmp.setLocation(mp.getLocation());
                                       else 
                                           oTmp.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                                       if (mp.getPatientProgram() != null)
                                           oTmp.setObsDatetime(mp.getPatientProgram().getDateEnrolled());
                                       if (oTmp.getObsDatetime() == null)
                                           oTmp.setObsDatetime(new Date());
                                       oTmp.setPerson(mp.getPatient());
                                       oTmp.setValueNumeric(onartInt.doubleValue());
                                       oTmp.setVoided(false);
                                       os.saveObs(oTmp, "");
                                   }
                               }
                        
                    /////////////////////////
                    ///////PersonAttributes//
                    //////////////////////////
                    //Health Center
                    List<PersonAttribute> attList =   mp.getPatient().getActiveAttributes();
                    if (healthCenterIdString != null && !healthCenterIdString.equals("")){                 
                        PersonAttributeType pat = perS.getPersonAttributeTypeByName("Health Center");
                        boolean found = false;
                        for (PersonAttribute pa : attList){
                            if (pa.getAttributeType().equals(pat) && pa.getVoided() == false ){
                               found = true;
                               if (pa.getValue().compareTo(healthCenterIdString) != 0){
                                   pa.setValue(healthCenterIdString);
                                   pa.setChangedBy(me);
                                   pa.setDateChanged(new Date());
                                   patSer.savePatient(mp.getPatient());
                                   break;
                               }
                            } 
                        }
                        if (!found){
                            PersonAttribute paNew = new PersonAttribute();
                            paNew.setAttributeType(pat);
                            paNew.setCreator(me);
                            paNew.setDateCreated(new Date());
                            paNew.setPerson(mp.getPatient());
                            paNew.setValue(healthCenterIdString);
                            paNew.setVoided(false);
                            mp.getPatient().addAttribute(paNew);
                            Context.getPatientService().savePatient(mp.getPatient());
                        }
                    }
                    
                  //Health District
                    if (healthDistrictString != null){
                        PersonAttributeType pat = perS.getPersonAttributeTypeByName("Health District");
                        boolean found = false;
                        for (PersonAttribute pa : attList){
                            if (pa.getAttributeType().equals(pat) && pa.getVoided() == false ){
                               found = true;
                               if (pa.getValue().compareTo(healthDistrictString) != 0){
                                   pa.setValue(healthDistrictString);
                                   pa.setChangedBy(me);
                                   pa.setDateChanged(new Date());
                                   Context.getPatientService().savePatient(mp.getPatient());
                                   break;
                               }
                            } 
                        }
                        if (!found){
                            PersonAttribute paNew = new PersonAttribute();
                            paNew.setAttributeType(pat);
                            paNew.setCreator(me);
                            paNew.setDateCreated(new Date());
                            paNew.setPerson(mp.getPatient());
                            paNew.setValue(healthDistrictString);
                            paNew.setVoided(false);
                            mp.getPatient().addAttribute(paNew);
                            Context.getPatientService().savePatient(mp.getPatient());
                        }
                    }
                    
                    //////////////////////////
                    //treatment supporter
                    //////////////////////////
                    if (treatmentSupporterIdString != null && !treatmentSupporterIdString.equals("")){
                        Integer treatmentSupporterId = Integer.valueOf(treatmentSupporterIdString); 
                        
                        if (treatmentSupporterId.intValue() != 0){
                            try {
                            Person newTreatSup = perS.getPerson(treatmentSupporterId);
                            
                            Relationship r = new Relationship();
                            r.setCreator(me);
                            r.setDateCreated(new Date());
                            r.setPersonA(newTreatSup);
                            r.setPersonB(mp.getPatient());
                           
                            RelationshipType rt = perS.getRelationshipTypeByName(as.getGlobalProperty("mdrtb.treatment_supporter_relationship_type"));
                            r.setRelationshipType(rt);
                            r.setVoided(false);
                            perS.saveRelationship(r);
                            } catch (Exception ex){
                                log.error("Error creating treatment supporter relationship", ex);
                            }
                        }
                    }
                        
                            
                            //cleanup
                            MdrtbUtil.fixCultureConversions(mp.getPatient(), mu);

                }           
                view = MdrtbConstants.MdrtbPatientDashboardTabs.STATUS.name();
                mu = null;
        }   
        
        if (action != null && msa.getMessage("mdrtb.enroll").equals(action)){
            MdrtbPatient mp = (MdrtbPatient) object;
            String enrollmentDateString = request.getParameter("programEnrollmentDate");
            try {
            SimpleDateFormat sdf = Context.getDateFormat();
            mu.enrollPatientInMDRTBProgram(mp.getPatient(), sdf.parse(enrollmentDateString));
            } catch (Exception ex){
                log.warn("Failed to enroll patient in mdrtb program", ex);
            }
            view = MdrtbConstants.MdrtbPatientDashboardTabs.STATUS.name();  
            mu = null;
        }
      
        rv.addStaticAttribute("patientId", patientId);
        rv.addStaticAttribute("view", view);
        return new ModelAndView(rv); 
        
       
    }


    /**
     * This class returns the form backing object.  This can be a string, a boolean, or a normal
     * java pojo.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public Object formBackingObject(HttpServletRequest request) throws Exception { 
        
        if (Context.isAuthenticated()){
            MdrtbPatient mp = new MdrtbPatient();
            String patientId = request.getParameter("patientId");
            if (patientId != null){
                PatientService patientService = Context.getPatientService();
                ObsService os = Context.getObsService();    
                MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                MdrtbFactory mu = ms.getMdrtbFactory();
             //   try{
                    Patient patient = patientService.getPatient(Integer.valueOf(patientId));
                    patient.getIdentifiers();
                    mp.setPatient(patient);
                    Person person = Context.getPersonService().getPerson(patient.getPatientId());
                    
                    String codString = Context.getAdministrationService().getGlobalProperty("concept.causeOfDeath");
        			Concept causeOfDeathConcept = Context.getConceptService().getConcept(codString);           
  
                    Set<Concept> cSetTmp = new HashSet<Concept>();
                    for (Map.Entry<String, Concept> cn : mu.getXmlConceptList().entrySet()){
                        cSetTmp.add(cn.getValue());
                    }
                    cSetTmp.add(causeOfDeathConcept);
                    List<Concept> cListForObs = new ArrayList<Concept>(cSetTmp);
                    ArrayList<Person> pList = new ArrayList<Person>();
                    pList.add(person);
                    
                    List<Obs> obs = os.getObservations(pList, null, cListForObs, null, null, null, null, null, null, null, null, false);
                    
                    
                    mp.setObs(new LinkedHashSet<Obs>(obs));
                    mp.setGivenName(person.getGivenName());
                    mp.setMiddleName(person.getMiddleName());
                    mp.setFamilyName(person.getFamilyName());
                    mp.setFamilyNameTwo(person.getPersonName().getFamilyName2());
                    
                    List<Order> orders = Context.getOrderService().getOrdersByPatient(patient);
                    mp.setOrders(orders);
                    
                    if (Context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_ORDERS)) {
                        List<DrugOrder> drugOrderList = Context.getOrderService().getDrugOrdersByPatient(patient);
                        List<DrugOrder> futureDrugOrders = new ArrayList<DrugOrder>();
                        List<DrugOrder> currentDrugOrders = new ArrayList<DrugOrder>();
                        List<DrugOrder> discontinuedDrugOrders = new ArrayList<DrugOrder>();
                        for (Iterator<DrugOrder> iter = drugOrderList.iterator(); iter.hasNext(); ) {
                            DrugOrder next = iter.next();
                            if (next.isCurrent()) currentDrugOrders.add(next);
                            if (next.isFuture()) futureDrugOrders.add(next);
                            if (next.isDiscontinuedRightNow()) discontinuedDrugOrders.add(next); 
                            if (next.getDiscontinuedDate() == null && next.getAutoExpireDate() != null && next.getAutoExpireDate().before(new Date())) {
                                discontinuedDrugOrders.add(next);
                            }
                        }
                        mp.setCurrentDrugOrders(currentDrugOrders);
                        mp.setCompletedDrugOrders(discontinuedDrugOrders);
                        mp.setFutureDrugOrders(futureDrugOrders);
                    }
                    
                    // TODO: Compute resistance probability here
                    int probability = (int)(Math.random() * 100);
                    mp.getExtra().put("resistanceProbability", probability);
                    
                    List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
                    if (encs.size() > 0){
                       Encounter enc = encs.get(encs.size()-1);
                       mp.setLocation(enc.getLocation());
                       mp.setProvider(enc.getProvider());
                    }
                    //HTML forms support:
                    AdministrationService as = Context.getAdministrationService();
                    FormService fs = Context.getFormService();
                    List<Form> forms = fs.getAllForms();
                    String formsList = as.getGlobalProperty("mdrtb.mdrtb_forms_list");
                    for (StringTokenizer st = new StringTokenizer(formsList, "|"); st.hasMoreTokens();) {
                     String formName = st.nextToken().trim();
                        for (Form form : forms) {
                            if (formName.contains(":html") && formName.replaceAll(":html", "").equals(form.getName().trim())){
                                for (Encounter encTmp : encs){
                                    if (encTmp.getForm() != null && encTmp.getForm().getFormId().intValue() == form.getFormId().intValue())
                                        mp.getHtmlEncList().add(encTmp);
                                }
                            }
                                           

                        }

                    }
                    mp.sortHtmlEncListByEncounterDatetime();
                    
                    
                    
                    Concept cultureConversionConcept = mu.getConceptCultureConversion();
                    
                    if (cultureConversionConcept.getConceptId() != null){
                        List<Obs> oList = os.getObservationsByPersonAndConcept(patient, cultureConversionConcept); 
                        if (oList != null) {
                            if (oList.size() > 0){
                               //gets the latest 
                               Obs o = oList.get(0);
                               for (Obs oTmp : oList){
                                   if (o.getValueDatetime().getTime() < oTmp.getValueDatetime().getTime())
                                       o = oTmp;
                               }
                               mp.setCultureConversion(o);
                            }
                        }   
                    }
                    
                   Concept cultureReconversionConcept = mu.getConceptCultureReconversion();
                    
                    if (cultureReconversionConcept.getConceptId() != null){
                        List<Obs> oList = os.getObservationsByPersonAndConcept(patient, cultureReconversionConcept); 
                        if (oList != null && oList.size() > 0) {
                               //gets the latest 
                                Obs o = oList.get(0);
                                for (Obs oTmp : oList){
                                    if (o.getValueDatetime().getTime() < oTmp.getValueDatetime().getTime())
                                        o = oTmp;
                                }
                               mp.setCultureReconversion(o);
                        }
                    }
                    
                    //set program
                    Program program = mu.getMDRTBProgram();
                    List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(patient, program, null, null, null, null, false);
                    for (PatientProgram pp : pps){
                        if (pp.getDateCompleted() == null && pp.getProgram().equals(program) && !pp.getVoided()){
                            mp.setPatientProgram(pp);
                            break;
                        }    
                    }
                    if (mp.getPatientProgram() == null && pps.size() > 0){
                        mp.setPatientProgram(pps.get(pps.size()-1));
                    }
                    if (mp.getPatientProgram() != null){
                        Set<ProgramWorkflowState> pwsSet = mu.getStatesCultureStatus();
                        Set<PatientState> psSet = mp.getPatientProgram().getStates();
                        for (PatientState ps : psSet){
                            if (pwsSet.contains(ps.getState()) && ps.getEndDate() == null && !ps.getVoided()){
                                mp.setCultureStatus(ps);
                            }         
                        }
                    }
                    
                    //TODO: 
                    Concept c = mu.getConceptTreatmentStartDate();
                    List<Obs> obsTmp = os.getObservationsByPersonAndConcept(patient, c);
                    if (obsTmp.size() > 0)
                        mp.setTreatmentStartDate(obsTmp.get(obsTmp.size()-1));
                    
                    //set patientId
                    String piList = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_identifier_type");
                    if (piList == null || piList.equals(""))
                            mp.setPatientIdentifier(patient.getPatientIdentifier());
                    else {
                        try {
                            PatientIdentifier pi = new PatientIdentifier();
                            pi.setPreferred(false);
                            for (StringTokenizer st = new StringTokenizer(piList, "|"); st.hasMoreTokens(); ) {
                                String s = st.nextToken().trim();
                                PatientIdentifier piTmp = patient.getPatientIdentifier(s);
                                if (piTmp != null)
                                    pi = piTmp;
                                if (pi.getPreferred())
                                    break;                                
                            }   
                            if (pi.getPatient() != null)
                                mp.setPatientIdentifier(pi);
                            else   
                                mp.setPatientIdentifier(patient.getPatientIdentifier());
                        } catch (Exception ex){
                            mp.setPatientIdentifier(patient.getPatientIdentifier());
                        }
                    }
                    
                    
                    Concept drugUse = mu.getConceptPatientClassDrugUse();
                    Concept prevTreat = mu.getConceptPatientClassPrevTreatment();
                    Concept tbCaseClass = mu.getConceptTBCaseClassification();
                    Concept pulmonary = mu.getConceptPulmonary();
                    Concept extraPul = mu.getConceptExtraPulmonary();
                    Concept extraPulLoc = mu.getConceptExtraPulmonaryLocation();                   
                    Concept hivStatus = mu.getConceptHIVStatus();
                    Concept cd4 = mu.getConceptCD4();
                    Concept cd4percent = mu.getConceptCD4Percent();
                    Concept treatPlanComment = mu.getConceptTreatmentPlanComment();
                    Concept alergyComment = mu.getConceptAllergyComment();                   
                    Concept durationOfPrevTreatment = mu.getConceptPrevDuration();
                    Concept regNumber = mu.getConceptPrevRegNum();
                    Concept prevTreatmentCenter= mu.getConceptPrevTreatmentCenter();
                    Concept referredBy = mu.getConceptPrevReferredBy();
                    Concept transferredFrom = mu.getConceptTransferredFrom();
                    Concept transferredTo = mu.getConceptTransferredTo();                  
                    Concept onART = mu.getConceptOnART();
                    Concept nextVisit = mu.getConceptNextVisit();
                                        
                    //use already set obs list on patient:
                   for (Obs o : mp.getObs()){
                       if (o.getConcept().equals(drugUse) && (mp.getPatientClassDrugUse() == null || mp.getPatientClassDrugUse().getObsDatetime().before(o.getObsDatetime())))
                               mp.setPatientClassDrugUse(o);
                       if (o.getConcept().equals(prevTreat) && (mp.getPatientClassPrevTreatment() == null || mp.getPatientClassPrevTreatment().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPatientClassPrevTreatment(o);
                       if (o.getConcept().equals(tbCaseClass) && (mp.getTbCaseClassification() == null || mp.getTbCaseClassification().getObsDatetime().before(o.getObsDatetime())))
                           mp.setTbCaseClassification(o);
                       if (o.getConcept().equals(pulmonary) && (mp.getPulmonary() == null || mp.getPulmonary().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPulmonary(o);
                       if (o.getConcept().equals(extraPul) && (mp.getExtrapulmonary() == null || mp.getExtrapulmonary().getObsDatetime().before(o.getObsDatetime())))
                           mp.setExtrapulmonary(o);
                       if (o.getConcept().equals(extraPulLoc) && (mp.getTbLocation() == null || mp.getTbLocation().getObsDatetime().before(o.getObsDatetime())))
                           mp.setTbLocation(o);
                       
                       if (o.getConcept().equals(hivStatus) && (mp.getHivStatus() == null || mp.getHivStatus().getObsDatetime().before(o.getObsDatetime())))
                           mp.setHivStatus(o);
                       if (o.getConcept().getConceptId().intValue() == cd4.getConceptId().intValue() && (mp.getCd4() == null || mp.getCd4().getObsDatetime().before(o.getObsDatetime())))
                           mp.setCd4(o);
                       if (o.getConcept().getConceptId().intValue() == cd4percent.getConceptId().intValue() && (mp.getCd4percent() == null || mp.getCd4percent().getObsDatetime().before(o.getObsDatetime())))
                           mp.setCd4percent(o);
                      
                       
                       if (o.getConcept().equals(treatPlanComment) && (mp.getTreatmentPlanComment() == null || mp.getTreatmentPlanComment().getObsDatetime().before(o.getObsDatetime())))
                           mp.setTreatmentPlanComment(o);
                       if (o.getConcept().equals(alergyComment) && (mp.getAllergyComment() == null || mp.getAllergyComment().getObsDatetime().before(o.getObsDatetime())))
                           mp.setAllergyComment(o);
                       
                       if (o.getConcept().getConceptId().intValue() == durationOfPrevTreatment.getConceptId().intValue() && (mp.getDurationOfPreviousTreatment() == null || mp.getDurationOfPreviousTreatment().getObsDatetime().before(o.getObsDatetime())))
                           mp.setDurationOfPreviousTreatment(o);
                       if (o.getConcept().equals(regNumber) && (mp.getPreviousRegistrationNumber() == null || mp.getPreviousRegistrationNumber().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPreviousRegistrationNumber(o);
                       if (o.getConcept().getConceptId().intValue() == prevTreatmentCenter.getConceptId().intValue() && (mp.getPreviousTreatmentCenter() == null || mp.getPreviousTreatmentCenter().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPreviousTreatmentCenter(o);
                       if (o.getConcept().equals(referredBy) && (mp.getPatientReferredBy() == null || mp.getPatientReferredBy().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPatientReferredBy(o);
                       if (o.getConcept().equals(transferredFrom) && (mp.getPatientTransferredFrom() == null || mp.getPatientTransferredFrom().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPatientTransferredFrom(o);
                       if (o.getConcept().equals(transferredTo) && (mp.getPatientTransferredTo() == null || mp.getPatientTransferredTo().getObsDatetime().before(o.getObsDatetime())))
                           mp.setPatientTransferredTo(o);
                       
                       
                       if (o.getConcept().getConceptId().intValue() == onART.getConceptId().intValue() && (mp.getOnART() == null || mp.getOnART().getObsDatetime().before(o.getObsDatetime())))
                           mp.setOnART(o);
                       if (o.getConcept().getConceptId().intValue() == nextVisit.getConceptId().intValue() && (mp.getNextScheduledVisit() == null || mp.getNextScheduledVisit().getObsDatetime().before(o.getObsDatetime())))
                           mp.setNextScheduledVisit(o);
                       
                       if (o.getConcept().equals(causeOfDeathConcept)) {
                    	   mp.setCauseOfDeath(o);
                       }
                   
                   }
                   
                   //ART number
                   
                   String pitString = Context.getAdministrationService().getGlobalProperty("mdrtb.ART_identifier_type");
                   PatientIdentifierType pit = patientService.getPatientIdentifierTypeByName(pitString);
                   if (pit != null){
                       PatientIdentifier pi = patient.getPatientIdentifier(pit.getPatientIdentifierTypeId());
                       mp.setArtId(pi);
                   }
                   //health center
                   PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Health Center");
                   for (PersonAttribute pa: person.getAttributes()){
                       if (pa.getAttributeType().equals(pat)){
                           mp.setHealthCenter(pa);
                           break;
                       }    
                   }
                   //health district
                   PersonAttributeType patDist = Context.getPersonService().getPersonAttributeTypeByName("Health District");
                   for (PersonAttribute pa: person.getAttributes()){
                       if (pa.getAttributeType().equals(patDist)){
                           mp.setHealthDistrict(pa);
                           break;
                       }    
                   }
                   
                   //treatment supporter
                   PersonService ps = Context.getPersonService();
                   String rtString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_relationship_type");
                   RelationshipType rt = ps.getRelationshipTypeByName(rtString);
                   List<Relationship> treatmentSupporters = ps.getRelationships(null, mp.getPatient(), rt);
                   Person treatmentSupporter = null;
                   Date lastRelationshipDate = new Date(0);
                   for (Relationship r:treatmentSupporters){
                       Person personTmp = r.getPersonA();
                       if ((treatmentSupporter == null || r.getDateCreated().after(lastRelationshipDate)) && !r.isVoided()){
                           treatmentSupporter = personTmp;
                           r.getDateCreated();
                       }
                   }
                   
                   if (treatmentSupporter != null)
                       mp.setTreatmentSupporter(treatmentSupporter);
                   
                   
                   if (mp.getTreatmentSupporter() != null){
                       
                       Concept phoneConcept = mu.getConceptPhoneNumber();
                       List<Obs> oPhones = os.getObservationsByPersonAndConcept(mp.getTreatmentSupporter(), phoneConcept);
                       for (Obs o: oPhones){
                           if (!o.getVoided())
                               mp.setTreatmentSupporterPhone(o);
                       }
                       
                   }
                   
                   //contacts
                   
                   for (Relationship contact:ps.getRelationshipsByPerson(mp.getPatient())){                
                       //bi-directional:
                       Person contactTmp = null;
                       if (contact.getPersonB().getPersonId() != mp.getPatient().getPatientId())
                           contactTmp = contact.getPersonB();
                       if (contact.getPersonA().getPersonId() != mp.getPatient().getPatientId())
                           contactTmp = contact.getPersonA();
                           
                           if (contactTmp != null && !contact.getRelationshipType().equals(rt)){
                               MdrtbContactPerson mcp = new MdrtbContactPerson();
                               mcp.setPerson(contactTmp);
                               mcp.setRelationship(contact);
                               mcp.setIsPatient(mcp.getPerson().isPatient());
                               if (mcp.getPerson().isPatient()){
                                   List<PatientProgram> ppsTmp = Context.getProgramWorkflowService().getPatientPrograms(patientService.getPatient(contactTmp.getPersonId()), program, null, null, null, null, false);
                                   if (ppsTmp != null && ppsTmp.size() > 0)
                                       mcp.setIsTBPatient(true);
                               }

                               for (PersonAddress pa : contactTmp.getAddresses()){
                                   if (!pa.getVoided() && (mcp.getAddress() == null || (mcp.getAddress().getDateCreated().getTime() < pa.getDateCreated().getTime() && !mcp.getAddress().isPreferred()) || pa.isPreferred()))
                                       mcp.setAddress(pa);
                               }
                               
                               //contact person attribute -- contact ID
                               String contactAttributType = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_contact_id_attribute_type");
                               PersonAttributeType contactAttType = ps.getPersonAttributeTypeByName(contactAttributType);
                               if (contactAttType != null){
                                   for (PersonAttribute patt : contactTmp.getActiveAttributes()){
                                       if (!patt.isVoided() && patt.getAttributeType().equals(contactAttType) && (mcp.getMdrtbContactId() == null ||(mcp.getMdrtbContactId().getDateCreated().getTime() < patt.getDateCreated().getTime()))){
                                           mcp.setMdrtbContactId(patt);
                                       }     
                                   }
                               }
                               
                               //knownmdr
                               List<Obs> oKnownMDR = Context.getObsService().getObservationsByPersonAndConcept(mcp.getPerson(), mu.getConceptKnownMDRCase());
                               for (Obs mdr:oKnownMDR){
                                   if (!mdr.getVoided() && (mcp.getKnownMdrtbContact() == null || (mcp.getKnownMdrtbContact().getObsDatetime().getTime() < mdr.getObsDatetime().getTime())))
                                   mcp.setKnownMdrtbContact(mdr);
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
                                           if (oInner.getConcept().getConceptId() == mu.getConceptSimpleTBResult().getConceptId() && (mcp.getTestResult() == null || mcp.getTestResult().getObsDatetime().getTime() <= oInner.getObsDatetime().getTime())){
                                               mcp.setTestResult(oInner);
                                           }    
                                           if (oInner.getConcept().getConceptId() == mu.getConceptSimpleTBTestType().getConceptId() && (mcp.getTestType() == null || mcp.getTestType().getObsDatetime().getTime() <= oInner.getObsDatetime().getTime())){
                                               mcp.setTestType(oInner);
                                           }    
                                       }
                                   }
                               }

                               
                               mp.addContact(mcp);
                           }
                                                
                   }
                   
                   //resistant to these drugs concept list
                 
                   String redSt = as.getGlobalProperty("mdrtb.dst_color_coding_red");
                   String yellowSt = as.getGlobalProperty("mdrtb.dst_color_coding_yellow");
                   Concept red =  MdrtbUtil.getMDRTBConceptByName(redSt, new Locale("en", "US"), mu);
                   Concept yellow =  MdrtbUtil.getMDRTBConceptByName(yellowSt, new Locale("en", "US"), mu);
                   List<Concept> resistantConcepts = new ArrayList<Concept>();
                   resistantConcepts.add(red);
                   resistantConcepts.add(yellow);
                   //getResistantToDrugConcepts(Date minDate, Concept dstParent, Concept dstResultParent, List<Concept> dstDrugList, List<Concept> positiveConcepts, Patient patient, boolean considerOnlyLatestDst)
                   
                   Date dateTmp = new Date(0);
                   if (mp.getPatientProgram()!= null)
                       dateTmp = mp.getPatientProgram().getDateEnrolled();
                   mp.setResistanceDrugConcepts(MdrtbUtil.getResistantToDrugConcepts(dateTmp, mu.getConceptDSTParent(), mu.getConceptDSTResultParent(), MdrtbUtil.getDstDrugList(true, mu), resistantConcepts, patient, false));
                   
                   c = mu.getConceptCurrentRegimenType();
                   mp.setStEmpIndObs(os.getObservationsByPersonAndConcept(patient, c));
                   mu = null;
                //} catch (Exception ex){log.warn("formBackingObject passed invalid patientId", ex);}
                
            }
            
            return mp;
        } else return "";
        
    }
   
    protected boolean isNumeric(String str){
        String nums ="0123456789";
        for (int i = 0; i < str.length(); i++){
          char strChar = str.charAt(i);
          if (nums.indexOf(strChar) == -1){
                return false;
          }
        }
        return true;
    }
    
    /**
     * Utility functions
     */
    
    /**
     * Returns the mdrtb patient dashboard tabs to display by comparing available tabs against configuration property 
     */
    
    private List<HashMap<String,String>> getMdrtbPatientDashboardTabs(){
    	
    	// the list where we will store the tabs available
        List<HashMap<String,String>> availableTabs = new LinkedList<HashMap<String,String>>();
        // the list where we will store the actual tabs to display
        List<HashMap<String,String>> displayTabs = new LinkedList<HashMap<String,String>>();
        
        // first, get all the constant tabs defined by the module and add them to available list
        for (MdrtbPatientDashboardTabs dashboardTab : MdrtbPatientDashboardTabs.values()) {
     	   	HashMap<String,String> tab = new HashMap<String,String>();
        		tab.put("name", dashboardTab.name());
     	   		tab.put("id", dashboardTab.getId());  // the id to reference the code in Javascript
        		tab.put("messageCode", dashboardTab.getMessageCode());  // the spring:message code for the tag
        		availableTabs.add(tab);
        }
        
        // now pull out any extension point tabs that have been defined & that the user has privileges to see
        for (Extension extension:ModuleFactory.getExtensions("org.openmrs.mdrtb.mdrtbDashboardTab")) {
        	PatientDashboardTabExt patientDashboardTabExt = (PatientDashboardTabExt) extension;
        	
        	if(Context.hasPrivilege(patientDashboardTabExt.getRequiredPrivilege())){
        		HashMap<String,String> tab = new HashMap<String,String>();
        		tab.put("name", patientDashboardTabExt.getTabId());
        		tab.put("id", patientDashboardTabExt.getTabId());  // for extension points, name & id are the same
        		tab.put("messageCode", patientDashboardTabExt.getTabName());
        		availableTabs.add(tab);
        	}
        }
        
        // now get the global property that can be used to configure the display, and
        // loop thru the pipe-delimited values
        String[] globalPropTabs = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_dashboard_tab_conf").split("\\|");
        for (String globalPropTab : globalPropTabs) {
       
           // split the global property into the id and the show/hide spec
     	   String[] entry = globalPropTab.split(":");
  
     	   // now loop through the available tabs
     	   Iterator<HashMap<String,String>> availableTabsIterator = availableTabs.iterator();
     	   while(availableTabsIterator.hasNext()) {
     		   HashMap<String,String> tab = availableTabsIterator.next();
     		   
     		   if (StringUtils.equals(tab.get("name"), entry[0])){
 				   // if we've found a match, remove the tab from the available list
     			   availableTabsIterator.remove();
     			   // now, unless this configured as "hide", as this tab to the display list
 	        	   if ( ((entry.length) < 2) || !(StringUtils.equals(entry[1],"hide")) ){
 	        		   displayTabs.add(tab);
 	        	   }
 			   } 
     	   }
     	   // TODO: add a warning if this tab isn't found?
        }
        
        // now, since the default is to display any tabs that aren't explicitly hidden or listed in the conf, we need to
        // add all remaining tabs to the end of the display list
        displayTabs.addAll(availableTabs);
        
        return displayTabs;
        
    }
    
    /**
     * Returns the mdrtab patient dashboard tab to set as the default
     * (i.e. the first one listed in the patient_dashboard_tab_conf global property
     */
    private String getDefaultMdrtbPatientDashboardTab(){
    	String defaultTab = "";
    	
    	// iterate thru all the tabs listed in the configuration property and return the first one that isn't set to "hide"
    	String[] globalPropTabs = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_dashboard_tab_conf").split("\\|");
        for (String globalPropTab : globalPropTabs) {
          // split the global property into the id and the show/hide spec
     	   String[] entry = globalPropTab.split(":");
     	   
     	  if ( ((entry.length) < 2) || !(StringUtils.equals(entry[1],"hide")) ){
    		   defaultTab = entry[0];
    		   break;
    	   }  
        }
    	
    	if (StringUtils.isNotEmpty(defaultTab)) {
    		return defaultTab;
    	}
    	else {
    		// if no default has been defined, use the first tab listed in the MdrtbPatientDashboardTabs enum
    		return MdrtbConstants.MdrtbPatientDashboardTabs.values()[0].name();
    	}
    }
}



