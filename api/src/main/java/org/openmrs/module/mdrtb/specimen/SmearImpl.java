package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;

/**
 * An implementaton of a MdrtbSmear. This wraps an ObsGroup and provides access to smear data within
 * the obsgroup.
 */
public class SmearImpl extends TestImpl implements Smear {
	
	public SmearImpl() {
		// test = new Obs(null, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT), null, null);
	}
	
	// set up a smear object, given an existing obs
	/*
	public SmearImpl(Obs smear) {
		if (smear == null
		        || !(smear.getConcept().equals(Context.getService(MdrtbService.class).getConcept(
		            MdrtbConcepts.SMEAR_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize smear: invalid obs used for initialization.");
		}
		test = smear;
	}
	*/
	
	// create a new smear object, given an existing patient
	/*
	public SmearImpl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create smear: encounter can not be null.");
		}
		// note that we are setting the location null--tests don't immediately inherit
		// the location of the parent encounter
		test = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.SMEAR_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	*/
	
	public SmearImpl(LabTest smear) {
		if (smear == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = smear;
	}
	
	@Override
	public String getTestType() {
		return "smear";
	}
	
	public Integer getBacilli() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.BACILLI);
		return (Integer) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BACILLI), test);
		return (obs == null) ? null : obs.getValueNumeric();
		*/
	}
	
	public void setBacilli(Integer bacilli) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.BACILLI);
		attribute.setValue(bacilli);
		test.setAttribute(attribute);
	}
	
	public String getComments() {
		return test.getResultComments();
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_METHOD);
		return (Concept) attribute.getValue();
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_METHOD);
		attribute.setValue(method);
		test.setAttribute(attribute);
	}
	
	public Concept getResult() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_RESULT);
		return (Concept) attribute.getValue();
	}
	
	public void setResult(Concept result) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_RESULT);
		attribute.setValue(result);
		test.setAttribute(attribute);
	}
	
	/**
	 * Utility method for copying a smear
	 */
	public void copyMembersFrom(Test source) {
		super.copyMembersFrom(source);
		this.setBacilli(((Smear) source).getBacilli());
		this.setMethod(((Smear) source).getMethod());
		this.setResult(((Test) source).getResult());
	}
}
