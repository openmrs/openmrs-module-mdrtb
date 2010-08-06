package org.openmrs.module.mdrtb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;

@Deprecated
public class MdrtbDstDrugConcentrationPairing {
    private Log log = LogFactory.getLog(this.getClass());
    
    private Concept concept;
    private Integer concentration;
    public Concept getConcept() {
        return concept;
    }
    public void setConcept(Concept concept) {
        this.concept = concept;
    }
    public Integer getConcentration() {
        return concentration;
    }
    public void setConcentration(Integer concentration) {
        this.concentration = concentration;
    }
    
    
}
