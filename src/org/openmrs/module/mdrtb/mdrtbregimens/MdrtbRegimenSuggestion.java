package org.openmrs.module.mdrtb.mdrtbregimens;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MdrtbRegimenSuggestion {

    protected final Log log = LogFactory.getLog(getClass());
    
        private List<MdrtbDrugSuggestion> drugSuggestionList = new ArrayList<MdrtbDrugSuggestion>();
        private String displayName;
        private String codeName;
        private String canReplace;
        private String regimenType = "standardized";

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
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
        public String getCodeName() {
            return codeName;
        }
        public void setCodeName(String codeName) {
            this.codeName = codeName;
        }
        public String getCanReplace() {
            return canReplace;
        }
        public void setCanReplace(String canReplace) {
            this.canReplace = canReplace;
        }
        
        public void addMdrtbDrugSuggestion(MdrtbDrugSuggestion mds){
            if (this.getDrugSuggestionList() == null)
                this.setDrugSuggestionList(new ArrayList<MdrtbDrugSuggestion>());
            this.getDrugSuggestionList().add(mds);
        }
        
        
        
}
