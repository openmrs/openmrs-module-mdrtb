package org.openmrs.module.mdrtb.mdrtbregimens;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.order.RegimenSuggestion;

public class MdrtbRegimenSuggestion extends RegimenSuggestion {
    
    private static final long serialVersionUID = 1345L;
    protected final Log log = LogFactory.getLog(getClass());
    
        private List<MdrtbDrugSuggestion> drugSuggestionList = new ArrayList<MdrtbDrugSuggestion>();
        private String regimenType = "mdrtb.standardized";

        public String getRegimenType() {
            return regimenType;
        }
        public void setRegimenType(String regimenType) {
            this.regimenType = regimenType;
        }
        public List<MdrtbDrugSuggestion> getDrugSuggestionList() {
            return drugSuggestionList;
        }
        public void setDrugSuggestionList(List<MdrtbDrugSuggestion> drugSuggestionList) {
            this.drugSuggestionList = drugSuggestionList;
        }        
        public void addMdrtbDrugSuggestion(MdrtbDrugSuggestion mds){
            if (this.getDrugSuggestionList() == null)
                this.setDrugSuggestionList(new ArrayList<MdrtbDrugSuggestion>());
            this.getDrugSuggestionList().add(mds);
        }
        
        
        
}
