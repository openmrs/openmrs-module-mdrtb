package org.openmrs.module.mdrtb.specimen.custom;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.specimen.TestImpl;

/**
 * An implementaton of a MdrtbSmear. This wraps an ObsGroup and provides access to smear data within
 * the obsgroup.
 */
public class XpertImpl extends TestImpl implements Xpert {
	
	LabTest labTest;
	
	public XpertImpl() {
		// test = new Obs(null, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), null, null);
	}
	
	// set up a xpert object, given an existing obs
	/*
	public XpertImpl(Obs xpert) {
		if (xpert == null
		        || !(xpert.getConcept().equals(Context.getService(MdrtbService.class).getConcept(
		            MdrtbConcepts.XPERT_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize xpert: invalid obs used for initialization.");
		}
		test = xpert;
	}
	*/
	
	// create a new smear object, given an existing patient
	/*
	public XpertImpl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create xpert: encounter can not be null.");
		}
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	*/
	
	public XpertImpl(LabTest xpert) {
		if (xpert == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = xpert;
	}
	
	@Override
	public String getTestType() {
		return "xpert";
	}
	
	public String getComments() {
		return test.getResultComments();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		return (obs == null) ? null : obs.getComment();
		*/
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			        test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		obs.setComment(comments);
		*/
	}
	
	public Concept getResult() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.MTB_RESULT);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setResult(Concept mtbResult) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.MTB_RESULT);
		attribute.setValue(mtbResult);
		test.setAttribute(attribute);
	}
	
	public Concept getRifResistance() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.RIFAMPICIN_RESULT);
		return (Concept) attribute.getValue();
	}
	
	public void setRifResistance(Concept rifResistance) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.RIFAMPICIN_RESULT);
		attribute.setValue(rifResistance);
		test.setAttribute(attribute);
	}
	
	public Concept getMtbBurden() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.XPERT_MTB_BURDEN);
		return (Concept) attribute.getValue();
	}
	
	public void setMtbBurden(Concept mtbBurden) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.XPERT_MTB_BURDEN);
		attribute.setValue(mtbBurden);
		test.setAttribute(attribute);
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.GENEXPERT);
		return (Concept) attribute.getValue();
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.GENEXPERT);
		attribute.setValue(method);
		test.setAttribute(attribute);
	}
}
