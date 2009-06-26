package org.openmrs.module.mdrtb.web.dwr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenComponent;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;

public class MdrtbPatientSummaryService {

    protected final Log log = LogFactory.getLog(getClass());
    protected final Integer weightId = 5089;
    protected final Integer hivId = 3753;
    
    public MdrtbPatientSummaryObject getSummary(Integer patientId){
        MdrtbPatientSummaryObject mpso = new MdrtbPatientSummaryObject();
        if (Context.isAuthenticated()){
            Patient p = Context.getPatientService().getPatient(patientId);
            SimpleDateFormat sdf = Context.getDateFormat();
            MdrtbFactory mu = MdrtbFactory.getInstance();
            if (p != null){
                //name
                mpso.setPatientName(p.getFamilyName() + "," + p.getGivenName());
                //id
                mpso.setPatientIdentifier(MdrtbUtil.getMdrtbPatientIdentifier(p));
                //age
                String ageString = "";
                if (p.getBirthdateEstimated())
                    ageString = "~";
                mpso.setAge(ageString + p.getAge().toString());
                //gender
                mpso.setGender(p.getGender());

//                private Integer weight;
                Obs o = MdrtbUtil.getMostRecentObs(weightId, p);
                
                mpso.setWeight(formatDouble(o.getValueNumeric()));

//                private String hivStatus;
                mpso.setHivStatus(formatValueCoded(o.getValueCoded()));
//                private String hivDate;
                mpso.setHivDate(sdf.format(o.getObsDatetime()));
//                private String mdrtbPatientStatus;
                Program program = mu.getMDRTBProgram();
                List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, program, null, null, null, null, false);
                PatientProgram ppTmp = null;
                for (PatientProgram pp : pps){
                    if (pp.getDateCompleted() == null && pp.getProgram().equals(program) && !pp.getVoided()){
                        ppTmp = pp;
                    }    
                }
                if (ppTmp == null && pps.size() > 0){
                    ppTmp = pps.get(pps.size()-1);
                }
                if (ppTmp != null){
                    Set<ProgramWorkflowState> pwsSet = mu.getStatesPatientStatus();
                    Set<PatientState> psSet = ppTmp.getStates();
                    for (PatientState ps : psSet){
                        if (pwsSet.contains(ps.getState()) && ps.getEndDate() == null && !ps.getVoided()){
                            mpso.setMdrtbPatientStatus(ps.getState().getConcept().getBestName(Context.getLocale()).getName());
                        }         
                    }
                }
//                
//                private String currentRegimen;
                RegimenHistory rh = RegimenUtils.getRegimenHistory(p);
                Regimen r = rh.getRegimen(new Date());
                Set<RegimenComponent> comps = r.getComponents();
                for (RegimenComponent c:comps){
                    c.getGeneric().getBestName(Context.getLocale()).getName();
                }
                
//                private String initialSmearResult;
//                private String initialSmearDate;
//                private String dateOfFirstTreatment;
//                private String dateOfHospitalization; 
//                
//                private String provenance; //city
//                private String cultureResult;
//                private String cultureDate;
//                private String dateOfSecondTreatment;
//                private String dateOfEndOfHospitalization;
//                
//                private String antecedents;
//                private String originalSmearResult;
//                private String originalSmearDate;
//                private String finalRegimenDate;
//                private String mostRecentXrayResult;
//                private String mostRecentXrayDate;
//                
//                private String patientPhoneAndAddress;
//                private String dstResistanceProfile;
//                private String mostRecentSmearDate;
//                private String mostRecentSmearResult;
//                
//                private String personResponsible;
//                private String empiricalTreatment;
//                private String mostRecentCultureDate;
//                private String mostRecentCultureResult;
//                
//                private String personResponsiblePhoneAndAddress;
//                private String dateOfPersonalizedTreatment;
//                private String mostRecentThyroidDate;
//                private String mostRecentThyroidResult;
//                
//                private List<MdrtbSideEffect> sideEffectsList;
//                private String pulmonaryOrExtrapulmonary;
//                private String resistanceType;  //primary or secondary
                
                
            }    
        }
        return mpso;
    }
    
    private String formatDouble(Double d){
        if (d != null)
            return d.toString();
        else
            return "";
            
    }
    private String formatValueCoded(Concept c){
        if (c != null)
            return c.getBestName(Context.getLocale()).getName();
        else
            return "";
    }
    
    
}
