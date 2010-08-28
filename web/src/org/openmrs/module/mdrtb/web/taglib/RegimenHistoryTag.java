package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;

public class RegimenHistoryTag extends TagSupport {

    public static final long serialVersionUID = 121341223L;
    private final Log log = LogFactory.getLog(getClass());
    
    private Integer patientId;
    private String drugConceptList;
    private boolean includeAllOrderedDrugs = false;
    private String drugTitleString;
    private String durationTitleString;
    private String cssClass;
    private String graphicResourcePath;
    private boolean invert = true;
    private List<Obs> stEmpIndObs = new ArrayList<Obs>();
    private Integer standardizedId;
    private Integer empiricId;
    private Integer individualizedId;
    private String typeString;
    private String stString;
    private String empString;
    private String indString;
    private boolean timeDescending;

	public int doStartTag() {
		
        String ret = "";
        SimpleDateFormat sdf = Context.getDateFormat();
        Patient patient = Context.getPatientService().getPatient(patientId);
        RegimenHistory history = new RegimenHistory(Context.getOrderService().getDrugOrdersByPatient(patient));
        String noRegimenLabel = Context.getMessageSourceService().getMessage("mdrtb.noRegimen");
        String drugIndicator = "X";
        if (StringUtils.isNotEmpty(graphicResourcePath)) {
        	drugIndicator = "<img src='" + graphicResourcePath + "' alt='X'>";
		}
        
        //get drugs:
        Set<Concept> drugConcepts = new LinkedHashSet<Concept>();
        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        MdrtbFactory mu = ms.getMdrtbFactory();
        if (StringUtils.isNotEmpty(drugConceptList)){
            for (StringTokenizer st = new StringTokenizer(drugConceptList, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim().split(":")[0];
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en"), mu);
                if (c != null)
                    drugConcepts.add(c);
            }    
        }
        if (includeAllOrderedDrugs || StringUtils.isEmpty(drugConceptList)) {
            List<RegimenComponent> rcs = history.getComponents(null);
            for (RegimenComponent rc : rcs){
                Concept drugConcept = rc.getGeneric();
                if (drugConcept != null)
                    drugConcepts.add(drugConcept);
            }
        }
        
        if (history == null || history.getRegimenList().size() == 0 || !doesHistoryHaveAGenericInDrugConceptSet(history, drugConcepts)){
            release();
            return SKIP_BODY;
        }
        
        //get date map:
        Map<Date, Regimen> allDates = new LinkedHashMap<Date, Regimen>();
        Date lastCloseDate = null;
        for (Regimen reg : history.getRegimenList()) {
            if (doesRegimenHaveAGenericInDrugConceptSet(reg,drugConcepts)) {
            	if (lastCloseDate != null && lastCloseDate.before(reg.getStartDate())) {
            		allDates.put(lastCloseDate, null);
            	}
            	allDates.put(reg.getStartDate(), reg);  
            	lastCloseDate = reg.getEndDate();
            }
        }
        Regimen mostRecentRegimen = history.getRegimenList().get(history.getRegimenList().size()-1);
        if (!mostRecentRegimen.isActive()) {
        	allDates.put(mostRecentRegimen.getEndDate(), null);
        }
        
        List<Date> dateList = new ArrayList<Date>(allDates.keySet());
        if (timeDescending) {
        	Collections.reverse(dateList);
        }
        
        if (invert) { //time goes down
            ret += "<table class='" + cssClass + "'><tbody>";
            ret += "<tr><th>" + drugTitleString + ":</th>";
            for (Concept drugConcept : drugConcepts){
                ret += "<th>" + drugConcept.getBestShortName(Context.getLocale()).getName() + "</th>";
            }
            ret += "<td><b>" + getDurationTitleString() + "</b></td><td><b>" + getTypeString() + "</b></td></tr>";

            for (int i=0; i<dateList.size(); i++) {
            	Date d = dateList.get(i);
            	Regimen reg = allDates.get(d);
            	
            	String rowClass = (i%2 == 0 ? "evenRow" : "oddRow");
            	if ((timeDescending && i==0) || (!timeDescending && i == dateList.size()-1)) {
            		rowClass = "activeRegimenRow";
            	}

                ret += "<tr class='" + rowClass + "'>";
                ret += "<th>" + sdf.format(d)+ "</th>";
                
                if (reg == null) {
                	ret += "<td colspan=\"" + (drugConcepts.size() + 2) + "\">" + noRegimenLabel + "</td><tr>";
                }
                else {
                	for (Concept drugConcept : drugConcepts) {
                		ret += "<td>";
                		if (reg != null && reg.containsDrugConceptOnDate(drugConcept, d)) {
                			ret += drugIndicator;
                		}
                		else {
                			ret += " &nbsp;";
                		}
                		ret +="</td>";
                	}
                	if (reg != null && reg.getDurationInDays() != null) {
                		ret += "<td><b>" + reg.getDurationInDays().toString() + "</b></td>";
                	}
                	else {
                		ret += "<td>" + " " + "</td>";
                	}
                	ret += "<td>" + addStEmpIndMarker(allDates, d) + "</td></tr>";
                }
            }
            ret += "</tbody></table>";
        } 
        else {  //time goes horizontal
          ret += "<table class='" + cssClass + "'><tbody>";
          ret += "<tr><th>" + drugTitleString + "</th>";
          for (int i=0; i<dateList.size(); i++) {
          	Date d = dateList.get(i);
          	ret += "<th>" + sdf.format(d) + "</th>";
          }
          ret += "</tr>";
          
          boolean isEvenRow = false;
          for (Concept drugConcept : drugConcepts) {
        	  isEvenRow = !isEvenRow;
        	  
        	  String rowClass = (isEvenRow ? "oddRow" : "evenRow");
        	  ret += "<tr class='" + rowClass + "'>";
        	  ret += "<td><b>" + drugConcept.getBestShortName(Context.getLocale()).getName() + "</b></td>";
        	  
              for (int i=0; i<dateList.size(); i++) {
              	Date d = dateList.get(i);
              	Regimen reg = allDates.get(d);
              	
              	String cellClass = ((timeDescending && i==0) || (!timeDescending && i == dateList.size()-1)) ? "activeRegimenRow" : rowClass;
              	
              	ret += "<td class=\"" + cellClass + "\">";
              	if (reg != null && reg.containsDrugConceptOnDate(drugConcept, d)) {
            		if (reg != null && reg.containsDrugConceptOnDate(drugConcept, d)) {
            			ret += drugIndicator;
            		}
            		else {
            			ret += " &nbsp;";
            		}
              	}
              	ret +="</td>";
              }
              ret += "</tr>";
          }
          
          ret += "<tr class='" + (isEvenRow ? "oddRow" : "evenRow") + "'>";
          ret += "<td><b>" + getDurationTitleString() + "</b></td>";
          for (int i=0; i<dateList.size(); i++) {
            	Date d = dateList.get(i);
            	Regimen reg = allDates.get(d);
            	if (reg != null && reg.getDurationInDays() != null) {
            		ret += "<td><b>" + reg.getDurationInDays().toString() + "</b></td>";
            	}
            	else {
            		ret += "<td>" + " " + "</td>";
            	}
          }
          ret += "</tr>";
          
          ret += "<tr class='" + (isEvenRow ? "oddRow" : "evenRow") + "'>";
          ret += "<td><b>" + getTypeString() + "</b></td>";
          for (int i=0; i<dateList.size(); i++) {
        	  Date d = dateList.get(i);     
        	  ret += "<td>" + addStEmpIndMarker(allDates, d) + "</td>";
          }
          ret += "</tr></tbody></table>";    
        }    

        try {
            pageContext.getOut().write(ret);
        } 
        catch (IOException e) {
            log.error("Could not write to pageContext", e);
        }
        
        release();
        return SKIP_BODY;
    }
    
    public int doEndTag() {
        patientId = null;
        drugConceptList = null;
        includeAllOrderedDrugs = false;
        drugTitleString = null;
        durationTitleString = null;
        cssClass = null;
        stEmpIndObs = new ArrayList<Obs>();
        graphicResourcePath = null;
        typeString = null;
        stString = null;
        empString = null;
        indString = null;
        invert = true;
        timeDescending = false;
        return EVAL_PAGE;
     }

    private static boolean doesRegimenHaveAGenericInDrugConceptSet(Regimen reg, Set<Concept> drugConcepts){
        for (Concept drugConcept : drugConcepts){
            if (reg.containsDrugConcept(drugConcept))
                return true;
        }
        
        return false;
    }
    
    private static boolean doesHistoryHaveAGenericInDrugConceptSet(RegimenHistory history, Set<Concept> drugConcepts){
        for (Regimen reg : history.getRegimenList()){
            for (Concept drugConcept : drugConcepts){
                if (reg.containsDrugConcept(drugConcept))
                    return true;
            }
        }
        
        return false;
    }



    private String addStEmpIndMarker(Map<Date, Regimen> allDates, Date thisRegimenDate){
        String ret = "<b>";
        if (stEmpIndObs != null){
            for (Obs oTmp: stEmpIndObs){
                if (isDateInRegimenPeriod(allDates, thisRegimenDate, oTmp.getObsDatetime())){

                        if (oTmp.getValueCoded() != null){
                            if (oTmp.getValueCoded().getConceptId().intValue() == this.standardizedId.intValue())
                                ret += this.stString;
                            if (oTmp.getValueCoded().getConceptId().intValue() == this.empiricId.intValue())
                                ret += this.empString;
                            if (oTmp.getValueCoded().getConceptId().intValue() == this.individualizedId.intValue())
                                ret += this.indString;
                        }
                        if (thisRegimenDate.getTime() != oTmp.getObsDatetime().getTime()){
                            SimpleDateFormat sdf = Context.getDateFormat();
                            ret += " (" + sdf.format(oTmp.getObsDatetime()) + ")";
                        } 
                }
            }
        }
        ret += "</b>";
        return ret;
    }
    
    
    private boolean isDateInRegimenPeriod(Map<Date, Regimen> allDates, Date thisRegimenDate, Date obsDate){
        boolean ret = false;
        boolean stopIteratingNextTime = false;
        int size = allDates.size() - 1;
        int counter = 0;
        for (Map.Entry<Date, Regimen> e : allDates.entrySet()){
            if (ret == true && obsDate.getTime() < e.getKey().getTime()){
                return true;
            } else if (stopIteratingNextTime){
                break;
            }
            if (thisRegimenDate.getTime() == e.getKey().getTime() && obsDate.getTime() >= thisRegimenDate.getTime()){
                ret = true;
                stopIteratingNextTime = true;
                if (counter == size) //if there are no more regimen changes
                    return true;
            }    
            counter++;
        }
        return false;
    }
    
    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getStString() {
        return stString;
    }

    public void setStString(String stString) {
        this.stString = stString;
    }

    public String getEmpString() {
        return empString;
    }

    public void setEmpString(String empString) {
        this.empString = empString;
    }

    public String getIndString() {
        return indString;
    }

    public void setIndString(String indString) {
        this.indString = indString;
    }

    public Integer getStandardizedId() {
        return standardizedId;
    }

    public void setStandardizedId(Integer standardizedId) {
        this.standardizedId = standardizedId;
    }

    public Integer getEmpiricId() {
        return empiricId;
    }

    public void setEmpiricId(Integer empiricId) {
        this.empiricId = empiricId;
    }

    public Integer getIndividualizedId() {
        return individualizedId;
    }

    public void setIndividualizedId(Integer individualizedId) {
        this.individualizedId = individualizedId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getDrugConceptList() {
        return drugConceptList;
    }

    public void setDrugConceptList(String drugConceptList) {
        this.drugConceptList = drugConceptList;
    }

	public boolean isIncludeAllOrderedDrugs() {
		return includeAllOrderedDrugs;
	}

	public void setIncludeAllOrderedDrugs(boolean includeAllOrderedDrugs) {
		this.includeAllOrderedDrugs = includeAllOrderedDrugs;
	}
	
    public String getGraphicResourcePath() {
        return graphicResourcePath;
    }

    public void setGraphicResourcePath(String graphicResourcePath) {
        this.graphicResourcePath = graphicResourcePath;
    }

    public boolean isInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public List<Obs> getStEmpIndObs() {
        return stEmpIndObs;
    }

    public void setStEmpIndObs(List<Obs> stEmpIndObs) {
        this.stEmpIndObs = stEmpIndObs;
    }
    
    public String getDrugTitleString() {
        return drugTitleString;
    }

    public void setDrugTitleString(String drugTitleString) {
        this.drugTitleString = drugTitleString;
    }

    public String getDurationTitleString() {
        return durationTitleString;
    }

    public void setDurationTitleString(String durationTitleString) {
        this.durationTitleString = durationTitleString;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

	public boolean isTimeDescending() {
		return timeDescending;
	}

	public void setTimeDescending(boolean timeDescending) {
		this.timeDescending = timeDescending;
	}
}
