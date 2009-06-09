package org.openmrs.module.mdrtb.mdrtbregimens;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.util.OpenmrsConstants;

public class MdrtbRegimenUtils {

    protected final Log log = LogFactory.getLog(getClass());
    
    public  static List<MdrtbRegimenSuggestion> getMdrtbRegimenSuggestions(){
            List<MdrtbRegimenSuggestion> ret = new ArrayList<MdrtbRegimenSuggestion>();
            
            
            String httpBase = "http://localhost";
            if (httpBase.indexOf("/openmrs") > 0)
                httpBase = httpBase.substring(0, httpBase.indexOf("/openmrs"));
            String XMLlocation = httpBase + "/openmrs/moduleResources/mdrtb/mdrtbRegimenSuggestionTemplate.xml";
            if (!XMLlocation.substring(10).contains(":"))
                XMLlocation = httpBase + Context.getAdministrationService().getGlobalProperty("mdrtb.webserver_port") + "/openmrs/moduleResources/mdrtb/mdrtbRegimenSuggestionTemplate.xml";
            
            try{ 
                
                URL xmlURL = new URL(XMLlocation);
                SAXReader reader = new SAXReader();
                Document xmlDoc = reader.read(xmlURL);
                Element list = xmlDoc.getRootElement();
                ConceptService cs = Context.getConceptService();
                for(Iterator i = list.elements().iterator(); i.hasNext();){
                    Element regSugg = (Element) i.next();
                    //System.out.println(regSugg.getName());
                    if (regSugg.getName().equals("regimenSuggestion")){
                        MdrtbRegimenSuggestion mrs = new MdrtbRegimenSuggestion();
                        for(Iterator j = regSugg.elements().iterator(); j.hasNext();){
                            Element regSugChildren = (Element) j.next();
                            if (regSugChildren.getName().equals("drugComponents")){
                                //HERE:  drug components:
                                for(Iterator k = regSugChildren.elements().iterator(); k.hasNext();){
                                    Element drugSuggestion = (Element) k.next();
                                    if (drugSuggestion.getName().equals("drugSuggestion")){
                                        MdrtbDrugSuggestion mds = new MdrtbDrugSuggestion();
                                        for(Iterator m = drugSuggestion.elements().iterator(); m.hasNext();){
                                            Element drugSugChildren = (Element) m.next();
                                            //System.out.println(drugSugChildren.getName() + " " + drugSugChildren.getText());
                                            if (drugSugChildren.getName().equals("conceptId")){
                                                if (drugSugChildren.getText() != null && !drugSugChildren.getText().equals("")){
                                                    try {
                                                        
                                                        mds.setDrugConcept(cs.getConcept(Integer.valueOf(drugSugChildren.getText())));
                                                        //System.out.println("set drug concept successfully");
                                                        
                                                    } catch (Exception ex){
                                                        System.out.println("Could not parse a conceptId value " + drugSugChildren.getText() + "in the xml into a concept" + ex);
                                                    }
                                                    
                                                }
                                            } else if (drugSugChildren.getName().equals("drugId")){
                                                if (drugSugChildren.getText() != null && !drugSugChildren.getText().equals("")){
                                                    
                                                    try {
                                                        
                                                        mds.setDrug(cs.getDrug(Integer.valueOf(drugSugChildren.getText())));
                                                        //System.out.println("set drug successfully");
                                                        
                                                    } catch (Exception ex){
                                                        System.out.println("Could not parse a drugId value " + drugSugChildren.getText() + "in the xml into a drug" + ex);
                                                    }
                                                    
                                                }
                                            } else if (drugSugChildren.getName().equals("dose")){
                                                if (drugSugChildren.getText() != null && !drugSugChildren.getText().equals("")){
                                                    
                                                    try {
                                                        Double dose = Double.parseDouble(drugSugChildren.getText());
                                                        mds.setDose(dose);
                                                        //System.out.println("set dose successfully " + dose.toString());
                                                        
                                                    } catch (Exception ex){
                                                        System.out.println("Could not parse a dose value " + drugSugChildren.getText() + "in the xml into a Double" + ex);
                                                    }
                                                    
                                                }
                                               
                                            } else if (drugSugChildren.getName().equals("units")){
                                                mds.setUnits(drugSugChildren.getText());
                                            } else if (drugSugChildren.getName().equals("frequency")){
                                                mds.setFrequency(drugSugChildren.getText());
                                            } else if (drugSugChildren.getName().equals("instructions")){
                                                mds.setInstructions(drugSugChildren.getText());
                                            } else if (drugSugChildren.getName().equals("asNeeded")){
                                                if (drugSugChildren.getText().equals("true"))
                                                    mds.setAsNeeded(true);
                                                else
                                                    mds.setAsNeeded(false);
                                            }
                                        }
                                        if (mds.getDrug() == null && mds.getDrugConcept() == null){
                                            System.out.println("You have a drug suggestion with a null drug and concept");
                                        } else {
                                            mrs.addMdrtbDrugSuggestion(mds);
                                        }
                                    }
                                }           
                            } else if (regSugChildren.getName().equals("displayName")){
                                mrs.setDisplayName(regSugChildren.getText());                      
                            } else if (regSugChildren.getName().equals("codeName")){
                                mrs.setCodeName(regSugChildren.getText());   
                            } else if (regSugChildren.getName().equals("canReplace")){
                                mrs.setCanReplace(regSugChildren.getText());
                            } else if (regSugChildren.getName().equals("regimenType")){
                                mrs.setRegimenType(regSugChildren.getText());
                            }
                        }
                        if (mrs.getDisplayName() == null || mrs.getDrugSuggestionList().size() == 0){
                            System.out.println("one of your regimen suggestion lists is empty-- checking for a display name and a non-empty set of drug suggestions");
                        } else {
                            ret.add(mrs);
                        }
                    }
                }
                
            } catch (Exception ex){
                System.out.println("Could not read standard regimen XML. Try accessing your server using the port number in the url.  Or, check the mdrtb.webserver_port global property. " + ex);
            }  
            return ret;
    }
    
    
    
    public static List<DrugOrder> regimenSuggestionToDrugOrders(MdrtbRegimenSuggestion mrs, Patient patient, Date startDate, Date endDate) throws Exception {   
        List<DrugOrder> ret = new ArrayList<DrugOrder>();

            if (startDate == null)
                throw new Exception("You can't pass in a new regimen with a null start date.");
        
            for (MdrtbDrugSuggestion mds : mrs.getDrugSuggestionList()){
                DrugOrder doTmp = new DrugOrder();
                doTmp.setDrug(mds.getDrug());
                if (mds.getDrug() == null)
                    doTmp.setConcept(mds.getDrugConcept());
                else
                doTmp.setConcept(mds.getDrug().getConcept());
                doTmp.setCreator(Context.getAuthenticatedUser());
                doTmp.setDateCreated(new Date());
                if (endDate != null && endDate.before(new Date())){
                    doTmp.setDiscontinuedDate(endDate);
                    doTmp.setDiscontinued(true);
                } else {
                    doTmp.setDiscontinued(false);
                }
                doTmp.setComplex(false);
                doTmp.setDose(mds.getDose());
                doTmp.setFrequency(mds.getFrequency());
                doTmp.setInstructions(mds.getInstructions());
                doTmp.setOrderType(new OrderType(new Integer(OpenmrsConstants.ORDERTYPE_DRUG)));
                doTmp.setPatient(patient);
                doTmp.setPrn(mds.isAsNeeded()); 
                doTmp.setUnits(mds.getUnits());
                doTmp.setVoided(false);
                doTmp.setStartDate(startDate);
                if (endDate != null)
                    doTmp.setAutoExpireDate(endDate);   
                ret.add(doTmp);
            }
        return ret;
    }
    
    public static void reconcileAndSaveDrugOrders(List<DrugOrder> newDOs, String regimenType, Patient p, Date effectiveDate){
           //OrderExtensionService oes = (OrderExtensionService)Context.getService(OrderExtensionService.class);
           Concept reasonForChange = MdrtbUtil.getDefaultDiscontinueReason(); 
           RegimenUtils.setRegimen(p, effectiveDate, newDOs, reasonForChange, null);
    }
    
    
}
