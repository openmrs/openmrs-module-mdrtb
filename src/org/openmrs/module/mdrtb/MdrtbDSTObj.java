package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

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
        
        drugSensitivityTestComplete.setDateCreated(new Date());
        drugSensitivityTestComplete.setVoided(false);
        drugSensitivityTestComplete.setConcept(mu.getConceptDSTComplete());   
        drugSensitivityTestComplete.setCreator(user);
        drugSensitivityTestComplete.setPerson(patient);
        dstStartDate.setDateCreated(new Date());
        dstStartDate.setVoided(false);
        dstStartDate.setConcept(mu.getConceptCultureStartDate());
        dstStartDate.setCreator(user);
        dstStartDate.setPerson(patient);
        dstResultsDate.setDateCreated(new Date());
        dstResultsDate.setVoided(false);
        dstResultsDate.setConcept(mu.getConceptResultDate());
        dstResultsDate.setCreator(user);
        dstResultsDate.setPerson(patient);
        dstDateReceived.setDateCreated(new Date());
        dstDateReceived.setVoided(false);
        
        dstDateReceived.setConcept(mu.getConceptDateReceived());
        dstDateReceived.setCreator(user);
        dstDateReceived.setPerson(patient);
        
        source.setConcept(mu.getConceptSampleSource());
        source.setVoided(false);
        source.setDateCreated(new Date());
        source.setCreator(user);
        source.setPerson(patient);
        dstMethod.setDateCreated(new Date());
        dstMethod.setVoided(false);
        dstMethod.setConcept(mu.getConceptDSTMethod());
        dstMethod.setCreator(user);
        dstMethod.setPerson(patient);
        typeOfOrganism.setConcept(mu.getConceptTypeOfOrganism());
        typeOfOrganism.setVoided(false);
        typeOfOrganism.setDateCreated(new Date());
        typeOfOrganism.setCreator(user);
        typeOfOrganism.setPerson(patient);
        
        typeOfOrganismNonCoded.setConcept(mu.getConceptTypeOfOrganismNonCoded());
        typeOfOrganismNonCoded.setVoided(false);
        typeOfOrganismNonCoded.setDateCreated(new Date());
        typeOfOrganismNonCoded.setCreator(user);
        typeOfOrganismNonCoded.setPerson(patient);
        directOrIndirect.setDateCreated(new Date());
        directOrIndirect.setVoided(false);
        
        directOrIndirect.setConcept(mu.getConceptDirectIndirect());
        directOrIndirect.setCreator(user);
        directOrIndirect.setPerson(patient);
        coloniesInControl.setDateCreated(new Date());
        coloniesInControl.setVoided(false);
        
        coloniesInControl.setConcept(mu.getConceptColoniesInControl());
        coloniesInControl.setCreator(user);
        coloniesInControl.setPerson(patient);
        dstParentObs.setDateCreated(new Date());
        dstParentObs.setVoided(false);
        dstParentObs.setConcept(mu.getConceptDSTParent());
        dstParentObs.setCreator(user);
        dstParentObs.setPerson(patient);
        sputumCollectionDate.setDateCreated(new Date());
        sputumCollectionDate.setVoided(false);
        
        sputumCollectionDate.setConcept(mu.getConceptSputumCollectionDate());
        sputumCollectionDate.setCreator(user);
        sputumCollectionDate.setPerson(patient);

        
        AdministrationService as = Context.getAdministrationService();
        String drugList = as.getGlobalProperty("mdrtb.DST_drug_list");
        try {
            
           
                Concept c =  MdrtbUtil.getMDRTBConceptByName(drugList, new Locale("en", "US"));
                
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
                        
                    
                        Concept cChildren = MdrtbUtil.getMDRTBConceptByName(s, new Locale("en", "US"));
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
