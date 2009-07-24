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
import org.openmrs.ConceptNameTag;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbUtil;

public class MdrtbDSTWidgetController extends TagSupport {

    private static final long serialVersionUID = 14352344444L;
    private final Log log = LogFactory.getLog(getClass());
    private String obsGroupConcepts;
    private String obsGroupConceptDST;
    private String sputumCollectionDateConcept;
    private String concepts;
    private Collection<Obs> observations;
    private Boolean showDateHeader = true;
    private String cssClass;
    private Date fromDate;
    private Date toDate;
    private String columnHeaders = null;
    private String redList = "";
    private String greenList = "";
    private String yellowList ="";
    private String headerDescTop;
    private String headerDescLeft;
    private boolean fillInBlankCells = false;
    private String stringForBlankCells;
    private String resultConceptList = "";
    private String concentrationLabel = "";
    private int concentrationConceptId = 0;
    
    
    public String getConcentrationLabel() {
        return concentrationLabel;
    }


    public void setConcentrationLabel(String concentrationLabel) {
        this.concentrationLabel = concentrationLabel;
    }


    public MdrtbDSTWidgetController() {}
    
    
    public int doStartTag() {
        MdrtbFactory mu = MdrtbFactory.getInstance();
        StringBuilder ret = new StringBuilder();
        Locale loc = Context.getLocale();
        Locale locUS = new Locale("en");

        Set<String> obsGroupConcepts = new HashSet<String>();
        Set<Obs> headerObs = new HashSet<Obs>();
        List<Date> dates = new ArrayList<Date>();
        Set<String> greenSet = new HashSet<String>();
        Set<String> redSet = new HashSet<String>();
        Set<String> yellowSet = new HashSet<String>();
        Set<String> resultConceptListSet = new HashSet<String>();
        List<Obs> colIndex = new ArrayList<Obs>();
        
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
        
        for (StringTokenizer st = new StringTokenizer(this.yellowList, "|"); st
            .hasMoreTokens();) {
            String yellowString = st.nextToken().trim();
            yellowSet.add(yellowString);
        }
        
        for (StringTokenizer st = new StringTokenizer(this.resultConceptList,
                "|"); st.hasMoreTokens();) {
            String tmp = st.nextToken().trim();
            resultConceptListSet.add(tmp);
        }
        
        for (StringTokenizer st = new StringTokenizer(this.obsGroupConcepts,
                "|"); st.hasMoreTokens();) {
            String tmp = st.nextToken().trim();
           
            Concept c =  MdrtbUtil.getMDRTBConceptByName(tmp, new Locale("en", "US"), mu.getXmlConceptNameList());
            if (c != null){
                c = Context.getConceptService().getConcept(c.getConceptId());
                Collection<ConceptName> cnsTmp = c.getNames();
                for (ConceptName cn:cnsTmp){
                    Collection<ConceptNameTag> tags = cn.getTags();
                    for (ConceptNameTag cnTag:tags){
                        cnTag.getTag();
                    }
                }
                obsGroupConcepts.add(c.getBestName(locUS).getName());
                
            }    
        }
        
        if (this.toDate == null)
            this.setToDate(new Date());
        if (this.fromDate == null)
            this.setFromDate(new Date(0));
        
        for (Obs ob : observations) {
            if (ob.isObsGrouping()
                    && ob.getConcept().getBestName(locUS).getName().equals(this.obsGroupConceptDST)) {
                Set<Obs> tmpSet = ob.getGroupMembers();
                boolean dateAdded = false;
                for (Obs oTmp : tmpSet) {
                    if (this.doesSetContainSubString(oTmp.getConcept().getBestName(locUS).getName(), obsGroupConcepts) 
                            && oTmp.isObsGrouping()
                            && oTmp.getObsDatetime().getTime() >= fromDate.getTime()
                            && oTmp.getObsDatetime().getTime() <= toDate.getTime()
                            && !oTmp.isVoided()){
                            if (!dateAdded){
                            dates.add(oTmp.getObsDatetime());
                            dateAdded = true;
                            }
                            colIndex.add(ob);
                            
                    }
                }
        
            }
        }
        //to clarify, what we have now is all relevant dates in dates and TUBERCULOSIS DRUG SENSITIVITY TEST RESULT obs in encObs
        // toss in the header concepts:
        
        // toss in the header concepts:
        // if concept found: for all obs -- if matches concept:
        for (StringTokenizer st = new StringTokenizer(this.concepts, "|"); st
                .hasMoreTokens();) {
            String conceptString = st.nextToken().trim();
            Concept c =  MdrtbUtil.getMDRTBConceptByName(conceptString, new Locale("en", "US"), mu.getXmlConceptNameList());
            
            if (c != null) {
                c = Context.getConceptService().getConcept(c.getConceptId());
                for (Obs obx : observations) {
                    if (obx.getConcept().equals(c) && !obx.getVoided()){
                        if (obx.getValueDatetime() != null){
                            headerObs.add(obx);
                            dates.add(obx.getValueDatetime());
                        } else {
                            headerObs.add(obx);
                            dates.add(obx.getObsDatetime());
                        }
                        
                    }
                }
            }
        }

        //TODO: sort dates
        
       SortedSet<Date> ssDates = new TreeSet<Date>();
       for (Date d:dates){
           if (!ssDates.contains(d))
               ssDates.add(d);
       }
       List<Date> datesOrdered = new ArrayList<Date>();
       for (Date dateInner : ssDates){
           for (Date d : dates){
               if (dateInner.equals(d))
                   datesOrdered.add(d);
           }
       }
       
       
       dates = datesOrdered; 
       
       
        // TODO: allow columnHeaders to be a conceptSet and return all children
        List<ConceptName> tests = new ArrayList<ConceptName>();
        if (this.columnHeaders != null) 
            tests = this.getColumnHeaderConceptNames(this.columnHeaders, loc);

        /**
         * Here's where the table gets drawn
         */
        
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, loc);
        if (dates.size() > 0) {
            
            //Draw the Table header
            
            ret.append("<table class='" + cssClass + "'>");
            if (this.headerDescTop != null)
                ret.append("<tr><Th>&nbsp;</th><th colspan='" + dates.size() + "'>" + this.headerDescTop + "</th></tr>");
            ret.append("<tr><th>&nbsp;</th>");
            for (Date date : dates) {
                String dateOut = df.format(date);
                ret.append("<th valign='top' nowrap>" + dateOut + "</th>");
            }
            ret.append("</tr>");
                    
            //end table header
            
            Set<Obs> usedObs = new HashSet<Obs>();
            Set<Integer> headerPos = new TreeSet<Integer>(); 
            
            for (ConceptName test : tests) {
                Integer colIndexCount = 0;
                
                //create the row header for each test
                ret.append("<tr><Th><b>"+ test.getConcept().getBestShortName(Context.getLocale()) + "</b></th>");

                Set<Obs> usedObsThisRow = new HashSet<Obs>();
                for (int k = 0; k <dates.size(); k++){
                Date date = dates.get(k);

                    if (!this.doesSetContainObWithDateWithUsedCheck(headerObs, date, usedObs)) {
                        boolean emptyCellTest = true;
                        Obs oP = this.getObsFromArrayListWithDate(colIndex, date, usedObsThisRow);  
                                if (oP != null){
                                    usedObsThisRow.add(oP);
                                    ret.append("<td>");
                                    Set<Obs> encObsSub = oP.getGroupMembers();
                                    for (Obs obsInnterTmp : encObsSub){
                                        if (obsInnterTmp.isObsGrouping()){  
                                            Set<Obs> encObsChildren = obsInnterTmp.getGroupMembers();
                                            boolean testTmp = false;
                                            for (Obs o:encObsChildren){
                                             
                                                if (o.getValueCoded()!= null && o.getValueCoded().equals(test.getConcept()) && !usedObs.contains(o) && !o.isVoided()) {
                                                    

                                                    if (this.doesSetContainSubString(o.getConcept().getBestName(loc).getName(), greenSet))
                                                        ret = ret.insert(ret.length() -1,  " class='widgetGreen'  ");
                                                    else if (this.doesSetContainSubString(o.getConcept().getBestName(loc).getName(), redSet))
                                                        ret = ret.insert(ret.length() -1,  " class='widgetRed'  ");
                                                    else if (this.doesSetContainSubString(o.getConcept().getBestName(loc).getName(), yellowSet))
                                                        ret = ret.insert(ret.length() -1,  " class='widgetYellow'  ");
                                                    else 
                                                        ret = ret.insert(ret.length() -1,  " class='widgetDefault'  ");
                                                    //HERE
                                                    ret.append("<a class='widgetLinks' style='color:black' href='/openmrs/module/mdrtb/mdrtbEditTestContainer.form?ObsGroupId=" + oP.getObsId() + "'>" + o.getConcept().getBestShortName(loc).getName() + getConcentrationStringForDSTResultObj(o) + "</a>");
                                                    usedObs.add(o);
                                                    emptyCellTest = false;
                                                    testTmp = true;
                                                    break;
                                                }
                                            }
                                            if (testTmp)
                                                break;
                                        }
                                    }
                                    if (emptyCellTest && fillInBlankCells){
                                        if (stringForBlankCells == null)
                                            stringForBlankCells = " ";
                                        ret.append(stringForBlankCells);
                                       }
                                       ret.append("</td>");
                                }
                        
                        colIndexCount++;
                    } else {
                        for (Obs os : headerObs) {
                            if (os.getObsDatetime().getTime() == date.getTime() || (os.getValueDatetime() != null && os.getValueDatetime().getTime() == date.getTime()) && !usedObs.contains(os)) {
                                ret.append("<td rowspan='"
                                        + tests.size() + "' class='widgetHeaderRows'>");
                                
                                ret.append(os.getConcept().getBestShortName(loc).getName());
                                if (os.getValueCoded()!= null)
                                    ret.append(" " + os.getValueCoded().getBestShortName(loc).getName());
                                usedObs.add(os);
                                
                                
                                ret.append("</td>");
                                headerPos.add(Integer.valueOf(k));
                                
                            }
                        }
                    } //end if dst value or header obs
                } //end for each date
                ret.append("</tr>");
                    
                   
                    int subtractFromPos = 0;
                    for (Integer removeDate : headerPos){
                        Date dateTmp = dates.get(removeDate - subtractFromPos);
                        dates.remove(dateTmp);
                        subtractFromPos++;
                    }

                headerPos.clear();
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
            
    
    
    public int doEndTag() {
       obsGroupConcepts = null;
       obsGroupConceptDST = null;
       sputumCollectionDateConcept = null;
       concepts = null;
       observations = null;
       showDateHeader = null;
       cssClass = null;
       fromDate = null;
       toDate = null;
       columnHeaders = null;
       redList = null;
       greenList = null;
       yellowList = null;
       headerDescTop = null;
       headerDescLeft = null;
       //fillInBlankCells = null;
       stringForBlankCells = null;
       resultConceptList = null;
       concentrationLabel = null;
       //concentrationConceptId = null;

        return EVAL_PAGE;
    }
    

    
    public String getObsGroupConcepts() {
        return obsGroupConcepts;
    }
    public void setObsGroupConcepts(String obsGroupConcepts) {
        this.obsGroupConcepts = obsGroupConcepts;
    }
    public String getConcepts() {
        return concepts;
    }
    public void setConcepts(String concepts) {
        if (concepts == null || concepts.length() == 0)
            return;
        this.concepts = concepts;
    }
    public Collection<Obs> getObservations() {
        return observations;
    }
    public void setObservations(Collection<Obs> observations) {
        this.observations = observations;
    }
    public Boolean getShowDateHeader() {
        return showDateHeader;
    }
    public void setShowDateHeader(Boolean showDateHeader) {
        if (showDateHeader == null)
            return;
        this.showDateHeader = showDateHeader;
    }
    public String getCssClass() {
        return cssClass;
    }
    public void setCssClass(String cssClass) {
        if (cssClass == null || cssClass.length() == 0)
            return;
        this.cssClass = cssClass;
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
    public String getColumnHeaders() {
        return columnHeaders;
    }
    public void setColumnHeaders(String columnHeaders) {
        this.columnHeaders = columnHeaders;
    }
    public String getRedList() {
        return redList;
    }
    public void setRedList(String redList) {
        this.redList = redList;
    }
    public String getGreenList() {
        return greenList;
    }
    public void setGreenList(String greenList) {
        this.greenList = greenList;
    }
    public String getHeaderDescTop() {
        return headerDescTop;
    }
    public void setHeaderDescTop(String headerDescTop) {
        this.headerDescTop = headerDescTop;
    }
    public String getHeaderDescLeft() {
        return headerDescLeft;
    }
    public void setHeaderDescLeft(String headerDescLeft) {
        this.headerDescLeft = headerDescLeft;
    }
    public boolean isFillInBlankCells() {
        return fillInBlankCells;
    }
    public void setFillInBlankCells(boolean fillInBlankCells) {
        this.fillInBlankCells = fillInBlankCells;
    }
    public String getStringForBlankCells() {
        return stringForBlankCells;
    }
    public void setStringForBlankCells(String stringForBlankCells) {
        this.stringForBlankCells = stringForBlankCells;
    }
    public String getResultConceptList() {
        return resultConceptList;
    }
    public void setResultConceptList(String resultConceptList) {
        this.resultConceptList = resultConceptList;
    }
    public String getYellowList() {
        return yellowList;
    }
    public void setYellowList(String yellowList) {
        if (yellowList == null)
            return;
        this.yellowList = yellowList;
    }
    public String getObsGroupConceptDST() {
        return obsGroupConceptDST;
    }
    public void setObsGroupConceptDST(String obsGroupConceptDST) {
        if (obsGroupConceptDST == null)
            return;
        this.obsGroupConceptDST = obsGroupConceptDST;
    }
    public String getSputumCollectionDateConcept() {
        return sputumCollectionDateConcept;
    }
    public void setSputumCollectionDateConcept(String sputumCollectionDateConcept) {
        this.sputumCollectionDateConcept = sputumCollectionDateConcept;
    }

    /**
     * Utility functions
     */
    
    
    private boolean doesSetContainSubString(String str, Set<String> sSet) {
        boolean test = false;
        for (String sTest : sSet) {
            if (sTest.equals(str) || sTest.contains(str) || str.contains(sTest)) {
                test = true;
                break;
            }
        }
        return test;
    }
    private List<ConceptName> getColumnHeaderConceptNames(String nameList,
            Locale loc) {
        List<ConceptName> tests = new ArrayList<ConceptName>();
        for (StringTokenizer st = new StringTokenizer(nameList, "|"); st
                .hasMoreTokens();) {
            String conceptString = st.nextToken().trim();

            Concept c = MdrtbUtil.getMDRTBConceptByName(conceptString, new Locale("en", "US"));
            if (c != null) {
                //if (tests.contains(c.getName(loc)) == false)  //we need to support multiples
                tests.add(c.getBestName(loc));
            }
        }
        return tests;
    }


    private boolean doesSetContainObWithDateWithUsedCheck(Set<Obs> encObs, Date date, Set<Obs> usedObs) {
        boolean test = false;
        for (Obs o : encObs) {
            
            if (((o.getValueDatetime() != null && o.getValueDatetime().equals(date)) || o.getObsDatetime().equals(date) )  && !usedObs.contains(o)) {
                test = true;
            }
        }
        return test;
    }
    
    private Obs getObsFromArrayListWithDate(List<Obs> oList, Date date, Set<Obs> usedList){
        Obs o = null;
            for (Obs oTmp:oList){
                if (oTmp.getObsDatetime().equals(date) && oTmp.hasGroupMembers() && !usedList.contains(oTmp)){
                    return oTmp;
                }    
            }
        
        return o;
    }
    
    private String getConcentrationStringForDSTResultObj(Obs o){
        String ret = "";
              Obs oParent = o.getObsGroup();
              Set<Obs> oSiblings = oParent.getGroupMembers();
              for (Obs oInner:oSiblings){
                  if (oInner.getConcept().getConceptId().intValue() == concentrationConceptId
                          && oInner.getValueNumeric() != null){
                      ret = "<br>("+oInner.getValueNumeric()+" "+ concentrationLabel + ")";
                      break;
                  }    
              }
        return ret;
    }


    public int getConcentrationConceptId() {
        return concentrationConceptId;
    }


    public void setConcentrationConceptId(int concentrationConceptId) {
        this.concentrationConceptId = concentrationConceptId;
    }

}
