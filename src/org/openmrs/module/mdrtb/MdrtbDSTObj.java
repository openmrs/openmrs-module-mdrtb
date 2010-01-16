package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

/**
 * A DST parent object.
 */
public class MdrtbDSTObj {
    private Obs drugSensitivityTestComplete = new Obs();
    private Obs dstStartDate = new Obs();
    private Obs dstResultsDate = new Obs();
    private Obs dstDateReceived = new Obs();
    private Obs source = new Obs();
    private Obs dstMethod = new Obs();
    private List<MdrtbDSTResultObj> dstResults = new ArrayList<MdrtbDSTResultObj>();
    private Obs typeOfOrganism = new Obs();
    private Obs typeOfOrganismNonCoded = new Obs();
    private Obs directOrIndirect = new Obs();
    private Obs coloniesInControl = new Obs();
    private Obs dstParentObs = new Obs();
    private Obs sputumCollectionDate = new Obs();
    
    private ConceptService cs = Context.getConceptService();
    
    public MdrtbDSTObj(){}
    
    /**
     * Constructor that initializes the Obs with the correct concepts attached
     * @param STR_DST_COMPLETE
     * @param STR_CULTURE_START_DATE
     * @param STR_RESULT_DATE
     * @param STR_DATE_RECEIVED
     * @param STR_TB_SAMPLE_SOURCE
     * @param STR_DST_METHOD
     * @param STR_TYPE_OF_ORGANISM
     * @param STR_TYPE_OF_ORGANISM_NON_CODED
     * @param STR_DIRECT_INDIRECT
     * @param STR_COLONIES_IN_CONTROL
     */
    public MdrtbDSTObj(Patient patient,
                        User user, MdrtbFactory mu){
        
        this.drugSensitivityTestComplete.setDateCreated(new Date());
        this.drugSensitivityTestComplete.setVoided(false);
        this.drugSensitivityTestComplete.setConcept(mu.getConceptDSTComplete());   
        this.drugSensitivityTestComplete.setCreator(user);
        this.drugSensitivityTestComplete.setPerson(patient);
        this.drugSensitivityTestComplete.setUuid(UUID.randomUUID().toString());
        
        this.dstStartDate.setDateCreated(new Date());
        this.dstStartDate.setVoided(false);
        this.dstStartDate.setConcept(mu.getConceptCultureStartDate());
        this.dstStartDate.setCreator(user);
        this.dstStartDate.setPerson(patient);
        this.dstStartDate.setUuid(UUID.randomUUID().toString());
        
        this.dstResultsDate.setDateCreated(new Date());
        this.dstResultsDate.setVoided(false);
        this.dstResultsDate.setConcept(mu.getConceptResultDate());
        this.dstResultsDate.setCreator(user);
        this.dstResultsDate.setPerson(patient);
        this.dstResultsDate.setUuid(UUID.randomUUID().toString());
        
        this.dstDateReceived.setDateCreated(new Date());
        this.dstDateReceived.setVoided(false);   
        this.dstDateReceived.setConcept(mu.getConceptDateReceived());
        this.dstDateReceived.setCreator(user);
        this.dstDateReceived.setPerson(patient);
        this.dstDateReceived.setUuid(UUID.randomUUID().toString());
        
        this.source.setConcept(mu.getConceptSampleSource());
        this.source.setVoided(false);
        this.source.setDateCreated(new Date());
        this.source.setCreator(user);
        this.source.setPerson(patient);
        this.source.setUuid(UUID.randomUUID().toString());
        
        this.dstMethod.setDateCreated(new Date());
        this.dstMethod.setVoided(false);
        this.dstMethod.setConcept(mu.getConceptDSTMethod());
        this.dstMethod.setCreator(user);
        this.dstMethod.setPerson(patient);
        this.dstMethod.setUuid(UUID.randomUUID().toString());
        
        this.typeOfOrganism.setConcept(mu.getConceptTypeOfOrganism());
        this.typeOfOrganism.setVoided(false);
        this.typeOfOrganism.setDateCreated(new Date());
        this.typeOfOrganism.setCreator(user);
        this.typeOfOrganism.setPerson(patient);
        this.typeOfOrganism.setUuid(UUID.randomUUID().toString());
        
        this.typeOfOrganismNonCoded.setConcept(mu.getConceptTypeOfOrganismNonCoded());
        this.typeOfOrganismNonCoded.setVoided(false);
        this.typeOfOrganismNonCoded.setDateCreated(new Date());
        this.typeOfOrganismNonCoded.setCreator(user);
        this.typeOfOrganismNonCoded.setPerson(patient);
        this.typeOfOrganismNonCoded.setUuid(UUID.randomUUID().toString());
        
        this.directOrIndirect.setDateCreated(new Date());
        this.directOrIndirect.setVoided(false);
        this.directOrIndirect.setConcept(mu.getConceptDirectIndirect());
        this.directOrIndirect.setCreator(user);
        this.directOrIndirect.setPerson(patient);
        this.directOrIndirect.setUuid(UUID.randomUUID().toString());
        
        this.coloniesInControl.setDateCreated(new Date());
        this.coloniesInControl.setVoided(false);
        this.coloniesInControl.setConcept(mu.getConceptColoniesInControl());
        this.coloniesInControl.setCreator(user);
        this.coloniesInControl.setPerson(patient);
        this.coloniesInControl.setUuid(UUID.randomUUID().toString());
        
        this.dstParentObs.setDateCreated(new Date());
        this.dstParentObs.setVoided(false);
        this.dstParentObs.setConcept(mu.getConceptDSTParent());
        this.dstParentObs.setCreator(user);
        this.dstParentObs.setPerson(patient);
        this.dstParentObs.setUuid(UUID.randomUUID().toString());
        
        this.sputumCollectionDate.setDateCreated(new Date());
        this.sputumCollectionDate.setVoided(false); 
        this.sputumCollectionDate.setConcept(mu.getConceptSputumCollectionDate());
        this.sputumCollectionDate.setCreator(user);
        this.sputumCollectionDate.setPerson(patient);
        this.sputumCollectionDate.setUuid(UUID.randomUUID().toString());

        
        AdministrationService as = Context.getAdministrationService();
        String drugList = as.getGlobalProperty("mdrtb.DST_drug_list");
        try {
            
           
                Concept c =  MdrtbUtil.getMDRTBConceptByName(drugList, new Locale("en", "US"), mu);
                
                if (c != null && c.isSet()){
                    List<Concept> drugConceptList = cs.getConceptsByConceptSet(c);
                    for (Concept cChildren:drugConceptList){
                        MdrtbDSTResultObj mdro = new MdrtbDSTResultObj(cChildren, patient, user, mu);
                        dstResults.add(mdro);
                    }
                } else if (c != null){
                    MdrtbDSTResultObj mdro = new MdrtbDSTResultObj(c, patient, user, mu);
                    dstResults.add(mdro);
                } else if (c == null){
                    for (StringTokenizer st = new StringTokenizer(drugList, "|"); st.hasMoreTokens(); ) {
                        String s = st.nextToken().trim();
                        
                    
                        Concept cChildren = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"), mu);
                        if (cChildren != null){
                        MdrtbDSTResultObj mdro = new MdrtbDSTResultObj(cChildren, patient, user, mu);
                        dstResults.add(mdro);
                        }
                    }
                }
        } catch (Exception e){
            throw new RuntimeException("Unable to load drug concepts for DST tests.  Check your global properties values for 'mdrtb.DST_drug_list'.");
        }
        
    }
    
    
    public void setDirectOrIndirect(Obs o){
        this.directOrIndirect = o;
    }
    
    public Obs getDirectOrIndirect(){
        return this.directOrIndirect;
    }
    
    public void setColoniesInControl(Obs o){
        this.coloniesInControl = o;
    }
    
    public Obs getColoniesInControl(){
        return this.coloniesInControl;
    }
    
    public void setDstResults(List<MdrtbDSTResultObj> res){
        this.dstResults = res;
    }
    
    public List<MdrtbDSTResultObj> getDstResults(){
        return this.dstResults;
    }
    
    public void setTypeOfOrganism(Obs o){
        this.typeOfOrganism = o;
    }
    
    public Obs getTypeOfOrganism(){
        return this.typeOfOrganism;
    }
    
    public void setTypeOfOrganismNonCoded(Obs o){
        this.typeOfOrganismNonCoded = o;
    }
    
    public Obs getTypeOfOrganismNonCoded(){
        return this.typeOfOrganismNonCoded;
    }
    
    public void setDrugSensitivityTestComplete(Obs o){
        this.drugSensitivityTestComplete = o;
    }
    
    public Obs getDrugSensitivityTestComplete(){
        return this.drugSensitivityTestComplete;
    }
   
    public void setDstResultsDate(Obs o){
        this.dstResultsDate = o;
    }
    public Obs getDstResultsDate(){
        return this.dstResultsDate;
    }
    public void setDstDateReceived(Obs o){
        this.dstDateReceived = o;
    }
    public Obs getDstDateReceived(){
        return this.dstDateReceived;
    }
    public void setDstMethod(Obs o){
        this.dstMethod = o;
    }
    public Obs getDstMethod(){
        return this.dstMethod;
    }


    public Obs getDstStartDate() {
        return dstStartDate;
    }

    public void setDstStartDate(Obs dstStartDate) {
        this.dstStartDate = dstStartDate;
    }


    public Obs getSource() {
        return source;
    }


    public void setSource(Obs source) {
        this.source = source;
    }


    public Obs getDstParentObs() {
        return dstParentObs;
    }


    public void setDstParentObs(Obs dstParentObs) {
        this.dstParentObs = dstParentObs;
    }


    public Obs getSputumCollectionDate() {
        return sputumCollectionDate;
    }


    public void setSputumCollectionDate(Obs sputumCollectionDate) {
        this.sputumCollectionDate = sputumCollectionDate;
    }
    
    public void addDstResult(MdrtbDSTResultObj mdro){
        this.dstResults.add(mdro);
    }
    
}
