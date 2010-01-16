package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;

public class MdrtbBacteriologyWidgetController extends TagSupport {

    private static final long serialVersionUID = 14352344444L;
    private final Log log = LogFactory.getLog(getClass());
    private String obsGroupConcepts;
    private String concepts; //holds concepts for grey-bar obs
    private Collection<Obs> observations; //holds all observations
    private Boolean showDateHeader = true;
    private String cssClass;
    private Date fromDate;
    private Date toDate;
    private String columnHeaders = null;
    private String redList = "";
    private String greenList = "";
    private String headerDescTop;
    private String headerDescLeft;
    private boolean fillInBlankCells = false;
    private String stringForBlankCells;
    private String resultConceptList = "";
    private String programWorkflowStatesToNotShow = "";
    private String programNameReplacementString = "Program Start Date";
    private String scanty = "";



    public String getScanty() {
        return scanty;
    }

    public void setScanty(String scanty) {
        this.scanty = scanty;
    }

    public String getProgramWorkflowStatesToNotShow() {
        return programWorkflowStatesToNotShow;
    }

    public void setProgramWorkflowStatesToNotShow(
            String programWorkflowStatesToNotShow) {
        this.programWorkflowStatesToNotShow = programWorkflowStatesToNotShow;
    }

    public String getResultconceptList() {
        return resultConceptList;
    }

    public void setResultConceptList(String s) {
        this.resultConceptList = s;
    }

    public String getStringForBlankCells() {
        return this.stringForBlankCells;
    }

    public void setStringForBlankCells(String s) {
        this.stringForBlankCells = s;
    }

    public void setFillInBlankCells(boolean x) {
        this.fillInBlankCells = x;
    }

    public boolean getFillInBlankCells() {
        return this.fillInBlankCells;
    }

    public void setHeaderDescTop(String s) {
        this.headerDescTop = s;
    }

    public String getHeaderDescTop() {
        return this.headerDescTop;
    }

    public void setHeaderDescLeft(String s) {
        this.headerDescLeft = s;
    }

    public String getHeaderDescLeft() {
        return this.headerDescLeft;
    }

    public void setRedList(String s) {
        this.redList = s;
    }

    public void setGreenList(String s) {
        this.greenList = s;
    }

    public String getRedList() {
        return redList;
    }

    public String getGreenList() {
        return greenList;
    }

    public MdrtbBacteriologyWidgetController() {
    }

    public String getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(String s) {
        this.columnHeaders = s;
    }

    public int doStartTag() {

        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        MdrtbFactory mu = ms.getMdrtbFactory();
        StringBuilder ret = new StringBuilder();
        Locale loc = Context.getLocale();
        Locale locUS = new Locale("en");

        Set<Obs> encObs = new HashSet<Obs>();
        Set<String> obsGroupConcepts = new HashSet<String>();
        Set<Obs> headerObs = new HashSet<Obs>();
        SortedSet<Date> dates = new TreeSet<Date>();
        Set<String> greenSet = new HashSet<String>();
        Set<String> redSet = new HashSet<String>();
        Set<String> resultConceptListSet = new HashSet<String>();
        Patient patient = null;

        for (StringTokenizer st = new StringTokenizer(this.redList, "|"); st
                .hasMoreTokens();) {
            String redString = st.nextToken().trim();
            redSet.add(redString);
        }

        for (StringTokenizer st = new StringTokenizer(this.greenList, "|"); st
                .hasMoreTokens();) {
            String greenString = st.nextToken().trim();
            greenSet.add(greenString);
        }

        for (StringTokenizer st = new StringTokenizer(this.resultConceptList,
                "|"); st.hasMoreTokens();) {
            String tmp = st.nextToken().trim();
            resultConceptListSet.add(tmp);
        }

        for (StringTokenizer st = new StringTokenizer(this.obsGroupConcepts,
                "|"); st.hasMoreTokens();) {
            String tmp = st.nextToken().trim();
       
            Concept c = MdrtbUtil.getMDRTBConceptByName(tmp, new Locale("en", "US"), mu);
            if (c != null){
                obsGroupConcepts.add(c.getBestName(locUS).getName());
            }    
        }

        if (this.toDate == null)
            this.setToDate(new Date());
        if (this.fromDate == null)
            this.setFromDate(new Date(0));

        for (Obs ob : observations) {
            if (ob.isObsGrouping()
                    && this.doesSetContainSubString(ob.getConcept().getBestName(locUS).getName(), obsGroupConcepts)) {
                Set<Obs> tmpSet = ob.getGroupMembers();
                for (Obs oTmp : tmpSet) {
                    if (oTmp.getObsDatetime().getTime() >= fromDate.getTime()
                            && oTmp.getObsDatetime().getTime() <= toDate.getTime()
                            && oTmp.getVoided() == false
                            && this.doesSetContainSubString(oTmp.getConcept().getBestName(locUS).getName(),resultConceptListSet)) {
                        encObs.add(oTmp);
                        if (!dates.contains(oTmp.getObsDatetime()))
                            dates.add(oTmp.getObsDatetime());
                    }
                }

            }
            if (patient == null)
                patient = Context.getPatientService().getPatient(ob.getPerson().getPersonId());
        }


        // toss in the obs that are going to get grey rows:
        // if concept found: for all obs -- if matches concept, add date if not exists:
        // headerObs holds these going into the final routine.
        for (StringTokenizer st = new StringTokenizer(this.concepts, "|"); st
                .hasMoreTokens();) {
            String conceptString = st.nextToken().trim();
           
            Concept c = MdrtbUtil.getMDRTBConceptByName(conceptString, new Locale("en", "US"),mu);
            if (c != null) {
                for (Obs obx : observations) {
                    if (obx.getConcept().equals(c) && !obx.getVoided()){
                        headerObs.add(obx);
                        if (obx.getValueDatetime() != null){
                            if (!dates.contains(obx.getValueDatetime()))
                            dates.add(obx.getValueDatetime());
                        } else {
                            if (!dates.contains(obx.getObsDatetime()))
                            dates.add(obx.getObsDatetime());
                        }
                        
                    }
                }
            }
        }
        
        
        
        PatientProgram pp = null;
        String mdrtbProgram = Context.getAdministrationService().getGlobalProperty("mdrtb.program_name");
        Obs oProgramStart = new Obs(); 
        if (mdrtbProgram != null && !mdrtbProgram.equals("") && this.observations.size() > 0){

            Program program = Context.getProgramWorkflowService().getProgramByName(mdrtbProgram);
            List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(patient, program, null, null, null, null, false);
            for (PatientProgram ppTmp : pps){
                if (ppTmp.getDateCompleted() == null && ppTmp.getProgram().equals(program) && ppTmp.getVoided()== false){
                    //that's your pp
                    pp = ppTmp;
                    
                        //throw in non-persisted obs for program start date
                        
                    oProgramStart.setValueDatetime(pp.getDateEnrolled());
                    oProgramStart.setObsDatetime(pp.getDateEnrolled());
                    oProgramStart.setConcept(pp.getProgram().getConcept());
                    oProgramStart.setVoided(false);
                    oProgramStart.setDateCreated(new Date());
                    oProgramStart.setObsId(Integer.valueOf(0));
                        headerObs.add(oProgramStart);
                        if (oProgramStart.getValueDatetime() != null){
                            if (!dates.contains(oProgramStart.getValueDatetime()))
                            dates.add(oProgramStart.getValueDatetime());
                        } else {
                            if (!dates.contains(oProgramStart.getObsDatetime()))
                            dates.add(oProgramStart.getObsDatetime());
                        }
                        
                        //
                    break;
                }
            }
        }
        //psList is the list of patient states to use that gets iterated over.
        //
      //make exclude list:
        Set<String> doNotShowNames = new HashSet<String>();
        for (StringTokenizer st = new StringTokenizer(this.programWorkflowStatesToNotShow, "|"); st.hasMoreTokens(); ) {
            String s = st.nextToken().trim();
            if (s.length() > 2 && !doNotShowNames.contains(s))
                doNotShowNames.add(s);
        }  
        
        List<PatientState> psList = new ArrayList<PatientState>();
        Set<PatientState> psListUsedList = new HashSet<PatientState>();
        if (pp != null){
            for (PatientState state : pp.getStates()){

                if (state.getEndDate() == null && state.getVoided() == false && !doNotShowNames.contains(state.getState().getConcept().getBestName(Context.getLocale()).getName())){
                    psList.add(state);
                    if (!dates.contains(state.getStartDate()))
                        dates.add(state.getStartDate());
                }    
                    
            }
                
        }
        


        // setup left column items
        List<ConceptName> tests = new ArrayList<ConceptName>();
        if (this.columnHeaders != null) {
            tests = this.getColumnHeaderConceptNames(this.columnHeaders, locUS, mu);
        } else {
            tests = this.getConceptNamesFromSet(encObs, locUS);
        }

        
        /**
         * Here's where the table gets drawn
         */
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Context.getLocale());
        if (dates.size() > 0 && encObs.size() > 0) {
            ret.append("<table class='" + cssClass + "'>");
            if (this.headerDescLeft != null)
                ret.append("<tr><th style='font-size:120%'>" + this.headerDescLeft + "</th>");
            else
                ret.append("<tr><th>&nbsp;</th>");
            for (ConceptName cn : tests) {
                //TODO: lazy loading exception on the following line?
                //if so,
                //Concept cInner = Context.getConceptService().getConcept(cn.getConceptId());
                //then use cInner;
                ret.append("<th valign='top' style='font-size:120%'>" + cn.getConcept().getBestShortName(loc) + "</th>");
            }
            ret.append("</tr>");
            
            //used objects
            List<Obs> usedObs = new ArrayList<Obs>();

            
            
            //used to not re-print dates, which is annoying.
            Set<String> alreadyPrintedDates = new HashSet<String>();
            
            int countOfObjectsToShow = encObs.size() + headerObs.size() + psList.size();
            
            for (Date date : dates) {
                int count = 0;
                while ((doesSetContainObWithDateWithUsedCheck(encObs, date, usedObs)
                        || doesSetContainObWithDateWithUsedCheck(headerObs, date, usedObs) 
                        || doesSetContainStateWithDateWithUsedCheck(psList, date, psListUsedList))
                        && count <= countOfObjectsToShow) {
                    String dateOut = df.format(date);
                    if (!alreadyPrintedDates.contains(dateOut)){
                        alreadyPrintedDates.add(dateOut);
                        ret.append("<tr><th style='text-align: left' nowrap><b>" + dateOut + "</b></th>");
                    } else {
                        ret.append("<tr><td/></th>");
                    }
                    if (this.doesSetContainObWithDateWithUsedCheck(encObs, date, usedObs)) {
                        for (ConceptName test : tests) {
                            ret.append("<td>");
                            boolean emptyCellTest = true;
                            for (Obs o : encObs) {

                                if ((o.getValueCoded().getBestName(locUS)
                                        .equals(test) || o.getConcept()
                                        .getBestName(locUS).equals(test))
                                        && o.getObsDatetime().equals(date)
                                        && !arrayListContainsObs(o, usedObs)) {

                                    if (this.doesSetContainSubString(o
                                            .getValueCoded().getBestName(loc)
                                            .getName(), greenSet))
                                        ret = ret.insert(ret.length() - 1,
                                                " class='widgetGreen'  ");
                                    else if (this.doesSetContainSubString(o
                                            .getValueCoded().getBestName(loc)
                                            .getName(), redSet))
                                        ret = ret.insert(ret.length() - 1,
                                                " class='widgetRed'  ");
                                    else
                                        ret = ret.insert(ret.length() - 1,
                                                " class='widgetDefault'  ");
                                    String scantyAddition = "";
                                    if (o.getValueCoded().getBestName(locUS).getName().equals(this.scanty) && o.getValueNumeric() != null)
                                        scantyAddition = " (" + o.getValueNumeric().intValue() + ")";
                                    ret.append("<a class='widgetLinks' style='color:black' href='mdrtbEditTestContainer.form?ObsGroupId="
                                                    + o.getObsGroup().getObsId() + "'>"+ o.getValueCoded().getBestShortName(loc)+ scantyAddition +"</a>");
                                    emptyCellTest = false;
                                    usedObs.add(o);
                                    encObs.remove(o);
                                    break;

                                }
                            }
                            if (emptyCellTest && fillInBlankCells) {
                                if (stringForBlankCells == null)
                                    stringForBlankCells = " ";
                                ret.append(stringForBlankCells);
                            }
                            ret.append("</td>");
                        }
                    } else {
                        boolean test = false;
                        if ( doesSetContainObWithDateWithUsedCheck(headerObs, date, usedObs)){
                                //concept headers:
                                ret.append("<td style='font-size:120%' nowrap colspan='" + tests.size()
                                        + "' class='widgetHeaderRows'>");
                                for (Obs os : headerObs) {
                                    if (!arrayListContainsObs(os, usedObs) && (os.getObsDatetime().getTime() == date.getTime() && os.getValueDatetime() == null) || (os.getValueDatetime() != null && os.getValueDatetime().getTime() == date.getTime())) {
                                        if (!os.equals(oProgramStart))
                                            ret.append(os.getConcept().getBestName(loc).getName());
                                        else {
                                            ret.append(this.programNameReplacementString.toUpperCase());
                                        }    
                                        if (os.getValueCoded()!= null )
                                            ret.append(": " + os.getValueCoded().getBestName(loc).getName());
                                    
                                        usedObs.add(os);
                                        test = true;
                                        headerObs.remove(os);
                                        break;                              
                                    }
                                }
                         } else if (!test && doesSetContainStateWithDateWithUsedCheck(psList, date, psListUsedList)){
                                    //look for patient states
                                 ret.append("<td style='font-size:120%' nowrap colspan='" + tests.size()
                                     + "' class='widgetHeaderRows'>");
                                    for (PatientState ps : psList){
                                        if (ps.getStartDate().getTime() == date.getTime() && !psListUsedList.contains(ps) && ps.getEndDate() == null){
                                            ret.append(ps.getState().getProgramWorkflow().getConcept().getBestName(loc).getName() + ":<Br> " + ps.getState().getConcept().getBestName(loc).getName());
                                            psListUsedList.add(ps);
                                            psList.remove(ps);
                                            break;
                                        }  
                                    }    
                         }
                                ret.append("</td>");
                    }
                    ret.append("</tr>");
                count++;
                }//while
            }
            ret.append("</table>");
        } else {
            ret.append("<p>No Observations</p>");

        }

        try {
            JspWriter w = pageContext.getOut();
            w.println(ret);
        } catch (IOException ex) {
            log.error("Error while starting mdrtbObsTableWidget tag", ex);
        }
        return SKIP_BODY;
    }

    public String getConcepts() {
        return concepts;
    }

    public void setConcepts(String concepts) {
        if (concepts == null || concepts.length() == 0)
            return;
        this.concepts = concepts;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        if (cssClass == null || cssClass.length() == 0)
            return;
        this.cssClass = cssClass;
    }


    public Boolean getShowDateHeader() {
        return showDateHeader;
    }

    public void setShowDateHeader(Boolean showDateHeader) {
        if (showDateHeader == null)
            return;
        this.showDateHeader = showDateHeader;
    }

    public Collection<Obs> getObservations() {
        return observations;
    }

    public void setObservations(Collection<Obs> observations) {
        this.observations = observations;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        if (fromDate == null)
            return;
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        if (toDate == null)
            return;
        this.toDate = toDate;
    }


    public int doEndTag() {
        concepts = null;
        observations = null;
        cssClass = null;
        fromDate = null;
        toDate = null;
        columnHeaders = null;
        resultConceptList = null;
        obsGroupConcepts = null;
        showDateHeader = null;
        redList = null;
        greenList = null;
        headerDescTop = null;
        headerDescLeft = null;
        stringForBlankCells = null;
        resultConceptList = null;
        programWorkflowStatesToNotShow = null;  
        programNameReplacementString = null;
        scanty = null;
        return EVAL_PAGE;
    }

    private List<ConceptName> getColumnHeaderConceptNames(String nameList,
            Locale loc, MdrtbFactory mu) {
        List<ConceptName> tests = new ArrayList<ConceptName>();
        for (StringTokenizer st = new StringTokenizer(nameList, "|"); st
                .hasMoreTokens();) {
            String conceptString = st.nextToken().trim();
            Concept c = MdrtbUtil.getMDRTBConceptByName(conceptString, new Locale("en", "US"), mu);
            if (c != null) {
                if (tests.contains(c.getBestName(loc)) == false)
                    tests.add(c.getBestName(loc));
            }
        }
        return tests;
    }

    private List<ConceptName> getConceptNamesFromSet(Set<Obs> encObs, Locale loc) {
        List<ConceptName> tests = new ArrayList<ConceptName>();
        for (Obs o : encObs) {
            if (o.getValueCoded() != null
                    && tests.contains(o.getValueCoded().getBestName(loc)) == false)
                tests.add(o.getValueCoded().getBestName(loc));
        }
        return tests;
    }

    private boolean doesSetContainStateWithDateWithUsedCheck(List<PatientState> psSet,Date date,Set<PatientState> psSetUsedList){
        boolean test = false;
        for (PatientState o : psSet) {          
            if (o.getStartDate().getTime() == date.getTime() && !psSetUsedList.contains(o) && o.getEndDate() == null && o.getVoided() == false) {
                return true;
            }
        }
        
        
        return test;
    }
    
    private boolean doesSetContainObWithDateWithUsedCheck(Set<Obs> encObs, Date date, List<Obs> usedObs) {
        boolean test = false;
        for (Obs o : encObs) {
            
            if (((o.getValueDatetime() != null && o.getValueDatetime().getTime() == date.getTime()) || (o.getObsDatetime().getTime() == date.getTime() && o.getValueDatetime() == null) )  && !arrayListContainsObs(o, usedObs)) {
                return true;
            }
        }
        return test;
    }

    private boolean doesSetContainSubString(String str, Set<String> sSet) {
        boolean test = false;
        for (String sTest : sSet) {
            if (sTest.equals(str) || sTest.contains(str) || str.contains(sTest)) {
                return true;
            }
        }
        return test;
    }

    public String getObsGroupConcepts() {
        return obsGroupConcepts;
    }

    public void setObsGroupConcepts(String obsGroupConcepts) {
        this.obsGroupConcepts = obsGroupConcepts;
    }

    public String getResultConceptList() {
        return resultConceptList;
    }

    public String getProgramNameReplacementString() {
        return programNameReplacementString;
    }

    public void setProgramNameReplacementString(String programNameReplacementString) {
        this.programNameReplacementString = programNameReplacementString;
    }
    private boolean arrayListContainsObs(Obs o, List<Obs> list){
        boolean ret = false;
            for (Obs oInner:list){
                if (oInner.getObsId().intValue() == o.getObsId().intValue())
                    return true;
            }
        return ret;
    }
    
}
