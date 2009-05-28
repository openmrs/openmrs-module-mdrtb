/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb;

import java.util.Date;

import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.formentry.FormEntryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * This test validates the AdminList extension class
 */
public class AdminListExtensionTest extends BaseModuleContextSensitiveTest {

    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }
    
//	public void testGenericTestSpace() throws Exception {
//	    this.authenticate();
//	    Context.openSession();
//	    MdrtbUtil mu = new MdrtbUtil();
//	    Concept c = mu.getDSTParentConcept();
//	    assertTrue(c.getConceptId() != null);
//	    
//	    Concept cSmear = mu.getSmearParentConcept();
//	    Concept cCulture = mu.getCultureParentConcept();
//	    assertNotNull(cSmear);
//	    assertNotNull(cCulture);
//	    
//	    Map<Obs, Date> obsListSmear = mu.getSmears(Context.getPatientService().getPatient(9400));
//	    Map<Obs, Date> obsListCulture = mu.getCultures(Context.getPatientService().getPatient(9400));
//	    
//	    assertTrue(obsListSmear.size() == 2);
//	//    assertTrue(obsListCulture.size() == 2);
//	    
//	    for (Map.Entry<Obs, Date> e : obsListSmear.entrySet()){
//	    mu.getSputumCollectionDateSmear(e.getKey());
//	    assertTrue(e.getValue() != null);
//	    System.out.println(e.getValue());
//	    }
//	    
//	    for (Map.Entry<Obs, Date> e : obsListCulture.entrySet()){
//	        mu.getSputumCollectionDateCulture(e.getKey());
//	        assertTrue(e.getValue() != null);
//	        System.out.println(e.getValue());
//	        System.out.println(e.getKey().getObsId() + " isNegative is " + mu.isNegativeCulture(e.getKey()));
//	        }
//	    
//	    
//	    ObsService os = Context.getObsService();
//	    Obs o = os.getObs(154);
//	    Date date = mu.getSputumCollectionDateDST(o);
//	    assertTrue(date != null);
//	    Map<Obs, Date> obsList =  mu.getDSTs(Context.getPatientService().getPatient(9400));
//	    System.out.println("size " + obsList.size());
//
//	   Date testDate = mu.getReconversionDate(Context.getPatientService().getPatient(9400));
//	   System.out.println("reconversionDate " +testDate);
//	   this.setComplete();
//	}
//	
//
//	public void testCultureConversion() throws Exception {
//	    this.authenticate();
//	    MdrtbUtil mu = new MdrtbUtil();
//	    Concept ccConcept = mu.getCultureConverstionConcept();
//	    assertNotNull(ccConcept.getConceptId());
//	    Concept c2 = mu.getCultureReconversionConcept();
//	    assertNotNull(c2.getConceptId());
//	    
//	    
////	   ProgramWorkflowService pws = Context.getProgramWorkflowService();
////	   List<PatientProgram> pp = pws.getPatientPrograms(Context.getPatientService().getPatient(9400), new Program(), null, null, null, null, true);
////	   for (PatientProgram p:pp){
////	       ProgramWorkflow pw = p.getProgram().getWorkflowByName("");
////	       ProgramWorkflowState pxws = pw.getStateByName("");
////	       p.transitionToState(pxws, new Date());
////	   }
//	}
//	
//	public void testWorkflowStates() throws Exception {
//	    this.authenticate();
//        MdrtbUtil mu = new MdrtbUtil();
//        Set<ProgramWorkflowState> s = mu.getStatesOutcomes();
//        assertTrue(s.size() > 0);
//        Set<ProgramWorkflowState> s2 =mu.getStatesPatientStatus();
//       assertTrue(s.size() > 0);
//       Set<ProgramWorkflowState> s3 =mu.getStatesCultureStatus();
//       assertTrue(s.size() > 0 );
//
//       mu.fixCultureConversions(Context.getPatientService().getPatient(9400));
//       
//       Concept c = Context.getConceptService().getConceptByName("MODERATELY POSITIVE");
//       assertNotNull(c);
//       Patient patient = Context.getPatientService().getPatient(9411);
//       Program program = mu.getMDRTBProgram();
//       PatientProgram ppTmp = null;
//       List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(patient, program, null, null, null, null, true);
//      System.out.println("pps size " + pps.size());
//       for (PatientProgram pp : pps){
//           System.out.println("dateCompleted " + pp.getDateCompleted());
//           if (pp.getDateCompleted() == null && pp.getProgram().equals(program) && !pp.getVoided())
//               ppTmp = pp;
//               break;
//       }
//       assertNotNull(ppTmp);
//   
//	}
	
	public void testStuff() throws Exception {
	    this.authenticate();
	    
	    Order o = new Order();
	    o.setConcept(Context.getConceptService().getConcept(1));
	    o.setCreator(Context.getAuthenticatedUser());
	    o.setDateCreated(new Date());
	    o.setDiscontinued(false);
	    o.setOrderer(Context.getAuthenticatedUser());
	    o.setOrderType(Context.getOrderService().getOrderType(1));
	    o.setPatient(Context.getPatientService().getPatient(9401));
	    o.setVoided(false);
	    
	    Context.getOrderService().saveOrder(o);
	    
	    System.out.println("OrderId" + o.getOrderId());
	    
	    OrderExtension oe = new OrderExtension(o, "test");
	    OrderExtensionService oes = (OrderExtensionService)Context.getService(OrderExtensionService.class);
	    oes.saveOrderExtension(oe);
	    System.out.println("test  " + oe.getOrderExtensionId());
        
	}
	
}
