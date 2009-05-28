package org.openmrs.module.mdrtb.mdrtbregimens;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;

public class MdrtbDrugSuggestion {
    
    protected final Log log = LogFactory.getLog(getClass());
    
        private Concept drugConcept;
        private Drug drug;
        private Double dose;
        private String units;
        private String frequency;
        private String instructions;
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
        public Double getDose() {
            return dose;
        }
        public void setDose(Double dose) {
            this.dose = dose;
        }
        public String getUnits() {
            return units;
        }
        public void setUnits(String units) {
            this.units = units;
        }
        public String getFrequency() {
            return frequency;
        }
        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }
        public String getInstructions() {
            return instructions;
        }
        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }
        
        
    
}
