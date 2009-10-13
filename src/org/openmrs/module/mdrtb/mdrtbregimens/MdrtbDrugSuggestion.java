package org.openmrs.module.mdrtb.mdrtbregimens;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.order.DrugSuggestion;

public class MdrtbDrugSuggestion extends DrugSuggestion {
    
    protected final Log log = LogFactory.getLog(getClass());
    
        private Concept drugConcept;
        private Drug drug;
        private boolean asNeeded = false;
        
        
        public boolean isAsNeeded() {
            return asNeeded;
        }
        public void setAsNeeded(boolean asNeeded) {
            this.asNeeded = asNeeded;
        }
        public Concept getDrugConcept() {
            return drugConcept;
        }
        public void setDrugConcept(Concept drugConcept) {
            this.drugConcept = drugConcept;
        }
        public Drug getDrug() {
            return drug;
        }
        public void setDrug(Drug drug) {
            this.drug = drug;
        }        
    
}
