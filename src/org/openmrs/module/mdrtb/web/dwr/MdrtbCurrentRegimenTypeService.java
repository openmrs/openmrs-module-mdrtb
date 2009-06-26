package org.openmrs.module.mdrtb.web.dwr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbUtil;

public class MdrtbCurrentRegimenTypeService {
    protected final Log log = LogFactory.getLog(getClass());
    
    public boolean setCurrentRegimenTypeOnDate(Integer regimenType, String effectiveDateString, Integer pId) {
        
            Patient p = Context.getPatientService().getPatient(pId);
            ObsService os = Context.getObsService();
            SimpleDateFormat sdf = Context.getDateFormat();
            Date effectiveDate = null;
            try{
                effectiveDate = sdf.parse(effectiveDateString);
            } catch (Exception ex){}
            if (regimenType != null && !regimenType.equals("") && effectiveDate != null){
                Integer regTypeInt = null;
                try{
                    regTypeInt = Integer.valueOf(regimenType);
                } catch (Exception ex){
                    System.out.println("error in mdrtb regimen utils: " + regTypeInt + " can't be converted to an integer.");
                }
                if (regTypeInt != null) {
                    MdrtbFactory mu = MdrtbFactory.getInstance();
                    Concept regimenTypeConcept = mu.getConceptCurrentRegimenType();
                    List<Obs> oList = os.getObservationsByPersonAndConcept(p, regimenTypeConcept);
                    boolean needNewObs = true;
                    for (Obs oTmp : oList){
                       if (oTmp.getObsDatetime().getTime() == effectiveDate.getTime() && (oTmp.getValueCoded() == null || oTmp.getValueCoded().getConceptId().intValue() != regTypeInt.intValue())){
                           if (regTypeInt.intValue() != 0){
                               oTmp.setValueCoded(Context.getConceptService().getConcept(regTypeInt));
                               os.saveObs(oTmp, "");
                               needNewObs = false;
                           } else {
                               os.voidObs(oTmp, "");
                               needNewObs = false;
                           }
                       
                       }    
                    }
                    if (needNewObs){
                        Obs o = new Obs();
                        o.setConcept(mu.getConceptCurrentRegimenType());
                        o.setCreator(Context.getAuthenticatedUser());
                        o.setDateCreated(new Date());
                        o.setObsDatetime(effectiveDate);
                        o.setLocation(MdrtbUtil.getDefaultLocation(p));
                        o.setPerson(p);
                        o.setValueCoded(Context.getConceptService().getConcept(regTypeInt));
                        o.setVoided(false);
                        os.saveObs(o, "");
                    }
                    
                } 
            } else {
                return false;
            }
            return true;
        } 

}
