package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;

/**
 * An implementaton of a MdrtbCulture. This wraps an ObsGroup and provides access to culture data
 * within the obsgroup.
 */
public class CultureImpl extends TestImpl implements Culture {
	
	private static final String CULTURE = "culture";
	
	public CultureImpl() {
	}
	
	// set up a culture object, given an existing obs
	/*
	public CultureImpl(Obs culture) {
		if (culture == null
		        || !(culture.getConcept().equals(Context.getService(MdrtbService.class).getConcept(
		            MdrtbConcepts.CULTURE_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize culture: invalid obs used for initialization.");
		}
		test = culture;
	}
	*/
	
	// create a new culture object, given an existing patient
	/*
	public CultureImpl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create culture: encounter can not be null.");
		}
		test = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CULTURE_CONSTRUCT), encounter.getEncounterDatetime(), null);
		test = new LabTest(order);
	}
	*/
	
	public CultureImpl(LabTest culture) {
		if (culture == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = culture;
	}
	
	@Override
	public String getTestType() {
		return CULTURE;
	}
	
	public Integer getColonies() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES);
		return (Integer) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
			Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES), test);
		return (obs == null) ? null : obs.getValueNumeric();
		*/
	}
	
	public void setColonies(Integer colonies) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES);
		attribute.setValue(colonies);
		test.setAttribute(attribute);
	}
	
	public String getComments() {
		return test.getResultComments();
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
	}
	
	public Integer getDaysToPositivity() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.DAYS_TO_POSITIVITY);
		return (Integer) attribute.getValue();
	}
	
	public void setDaysToPositivity(Integer daysToPositivity) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.DAYS_TO_POSITIVITY);
		attribute.setValue(daysToPositivity);
		test.setAttribute(attribute);
	}
	
	public Concept getOrganismType() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM);
		return (Concept) attribute.getValue();
	}
	
	public void setOrganismType(Concept organismType) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM);
		attribute.setValue(organismType);
		test.setAttribute(attribute);
	}
	
	public String getOrganismTypeNonCoded() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED);
		return (String) attribute.getValue();
	}
	
	public void setOrganismTypeNonCoded(String organismType) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED);
		attribute.setValue(organismType);
		test.setAttribute(attribute);
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES);
		return (Concept) attribute.getValue();
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.CULTURE_METHOD);
		attribute.setValue(method);
		test.setAttribute(attribute);
	}
	
	public Concept getResult() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.CULTURE_RESULT);
		return (Concept) attribute.getValue();
	}
	
	public void setResult(Concept result) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.CULTURE_RESULT);
		attribute.setValue(result);
		test.setAttribute(attribute);
	}
	
	/**
	 * Utility method for copying a culture
	 */
	public void copyMembersFrom(Test source) {
		super.copyMembersFrom(source);
		this.setColonies(((Culture) source).getColonies());
		this.setMethod(((Culture) source).getMethod());
		this.setOrganismType(((Culture) source).getOrganismType());
		this.setOrganismTypeNonCoded(((Culture) source).getOrganismTypeNonCoded());
		this.setDaysToPositivity(((Culture) source).getDaysToPositivity());
		this.setResult(((Test) source).getResult());
	}
}
