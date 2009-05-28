package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;

public class RegimenHistoryTag extends TagSupport {

    public static final long serialVersionUID = 121341223L;
    private final Log log = LogFactory.getLog(getClass());
    
    private Integer patientId;
    private String drugConceptList;
    private String drugTitleString;
    private String durationTitleString;
    private String cssClass;
    private String graphicResourcePath;
    private boolean invert = true;
    
    
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

    public int doStartTag() {
        String ret = "";
        SimpleDateFormat sdf = Context.getDateFormat();
        Patient patient = Context.getPatientService().getPatient(patientId);
        RegimenHistory history = new RegimenHistory(Context.getOrderService()
                .getDrugOrdersByPatient(patient));
        
        
        //get drugs:
        Set<Concept> drugConcepts = new LinkedHashSet<Concept>();
        if (this.drugConceptList != null && !this.drugConceptList.equals("")){
            for (StringTokenizer st = new StringTokenizer(drugConceptList, "|"); st.hasMoreTokens(); ) {
                String s = st.nextToken().trim();
                Concept c = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en"));
                if (c != null)
                    drugConcepts.add(c);
            }    
        } else {
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
        for (Regimen reg : history.getRegimenList()){
            if (doesRegimenHaveAGenericInDrugConceptSet(reg,drugConcepts))
                    allDates.put(reg.getStartDate(), reg);  
        }
        if (invert){
            ret += "<table class='" + cssClass + "'><tbody>";
            ret += "<tr><th>" + this.drugTitleString + ":</th>";
            for (Concept drugConcept : drugConcepts){
                ret += "<th>" + drugConcept.getName().getShortName() + "</th>";
            }
            ret += "<td><b>" + this.getDurationTitleString() + "</b></td></tr>";
            boolean isEvenRow = false;
            for (Map.Entry<Date, Regimen> e : allDates.entrySet()){
                ret += "<tr ";
                if (isEvenRow){
                    ret+= "class='oddRow'";
                    isEvenRow = false;
                } else {
                    ret+= "class='evenRow'";
                    isEvenRow = true;
                } 
                ret += " ><th>" + sdf.format(e.getKey())+ "</th>";
                for (Concept drugConcept : drugConcepts){
                      Regimen reg = e.getValue();
                      ret += "<td>";
                      if (reg != null && reg.containsDrugConceptOnDate(drugConcept, e.getKey()))
                          ret+= "<img src='" + graphicResourcePath + "' alt='X'>";
                      else
                          ret += " &nbsp;";
                      ret +="</td>";
                }
                if (e.getValue() != null && e.getValue().getDurationInDays() != null)
                    ret += "<td><b>" + e.getValue().getDurationInDays().toString() + "</b></td></tr>";
                else
                    ret += "<td>" + " " + "</td></tr>";
            }
            ret += "</tbody></table>";
        } else {  
          ret += "<table class='" + cssClass + "'><tbody>";
          ret += "<tr><th>" + this.drugTitleString + "</th>";
          for (Map.Entry<Date, Regimen> e : allDates.entrySet()){
             ret += "<th>" + sdf.format(e.getKey()) + "</th>";
          }
          boolean isEvenRow = false;
          ret += "</tr>";
          for (Concept drugConcept : drugConcepts){    
              
               ret += "<tr ";
               if (isEvenRow){
                   ret+= "class='oddRow'";
                   isEvenRow = false;
               } else {
                   ret+= "class='evenRow'";
                   isEvenRow = true;
               }    
               ret += " ><td><b>" + drugConcept.getName().getShortName() + "</b></td>";
               for (Map.Entry<Date, Regimen> e : allDates.entrySet()){
                       Regimen reg = e.getValue();
                       ret += "<td>";
                       if (reg != null && reg.containsDrugConceptOnDate(drugConcept, e.getKey()))
                           ret+= "<img src='" + graphicResourcePath + "' alt='X'>";
                       else
                           ret += " &nbsp;";
                       ret +="</td>";
               }
               ret += "</tr>";
          }
          ret += "<tr ";
          if (isEvenRow){
              ret+= "class='oddRow'";
          } else {
              ret+= "class='evenRow'";
          } 
          ret +=    " ><td><b>" + this.getDurationTitleString() + "</b></td>";
          for (Map.Entry<Date, Regimen> e : allDates.entrySet()){
              if (e.getValue() != null && e.getValue().getDurationInDays() != null)
                  ret += "<td><b>" + e.getValue().getDurationInDays().toString() + "</b></td>";
              else
                  ret += "<td>" + " " + "</td>";
          }
          ret += "</tr></tbody></table>";    
        }    
        
        ret+="<bR><br><hr>";
      //Write out
        try {
            pageContext.getOut().write(ret);
        } catch (IOException e) {
            log.error("Could not write to pageContext", e);
        }
        
        release();
        return SKIP_BODY;
    }
    
    public int doEndTag() {
        patientId = null;
        drugConceptList = null;
        drugTitleString = null;
        durationTitleString = null;
        cssClass = null;
        return EVAL_PAGE;
     }
     
     public void release() {
         super.release();
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
    
    
}
