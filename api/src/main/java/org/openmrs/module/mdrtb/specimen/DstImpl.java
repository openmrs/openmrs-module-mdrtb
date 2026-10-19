package org.openmrs.module.mdrtb.specimen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.ConceptDatatype;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestType;

/**
 * An implementation of a MdrtbDst. This wraps an ObsGroup and provides access to dst data within
 * the obsgroup.
 */
public class DstImpl extends TestImpl implements Dst {
	
	private static final String VOID_MESSAGE = "Voided by MDRTB module";
	
	DstTestType testMethod;
	
	LabTestType dstTestType;
	
	List<DstResult> resultsList = null;
	
	public DstImpl() {
	}
	
	public DstImpl(LabTest dst) {
		if (dst == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = dst;
		dstTestType = test.getLabTestType();
		if (test.getLabTestType().equals(Context.getService(LabTestService.class).getDstLjTestType())) {
			testMethod = DstTestType.DST_LJ;
		} else if (test.getLabTestType().equals(Context.getService(LabTestService.class).getDstMgitTestType())) {
			testMethod = DstTestType.DST_MGIT;
		}
	}
	
	@Override
	public Concept getMethod() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DST_METHOD, testMethod);
		return (Concept) attribute.getValue();
	}
	
	@Override
	public void setMethod(Concept method) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DST_METHOD, testMethod);
		if (attribute != null) {
			if (method == null) {
				attribute.setVoided(true);
				attribute.setVoidReason(VOID_MESSAGE);
			}
			attribute.setValue(method);
			test.setAttribute(attribute);
			return;
		}
		// Initialize if needed
		attribute = new LabTestAttribute();
		attribute.setAttributeType(Context.getService(LabTestService.class).getLabTestAttributeTypeByTestTypeAndName(
		    dstTestType, MdrtbConcepts.DIRECT_INDIRECT));
		attribute.setValue(method);
		test.addAttribute(attribute);
	}
	
	@Override
	public Concept getResult() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DST_RESULT, testMethod);
		return (Concept) attribute.getValue();
	}
	
	@Override
	public void setResult(Concept result) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DST_METHOD, testMethod);
		attribute.setValue(result);
		test.setAttribute(attribute);
	}
	
	@Override
	public Boolean getDirect() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DIRECT_INDIRECT, testMethod);
		return (Boolean) attribute.getValue();
	}
	
	@Override
	public void setDirect(Boolean direct) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DIRECT_INDIRECT, testMethod);
		if (attribute != null) {
			if (direct == null) {
				attribute.setVoided(true);
				attribute.setVoidReason(VOID_MESSAGE);
			}
			attribute.setValue(direct);
			test.setAttribute(attribute);
			return;
		}
		// Initialize if needed
		attribute = new LabTestAttribute();
		attribute.setAttributeType(Context.getService(LabTestService.class).getLabTestAttributeTypeByTestTypeAndName(
		    dstTestType, MdrtbConcepts.DIRECT_INDIRECT));
		attribute.setValue(direct);
		test.addAttribute(attribute);
	}
	
	@Override
	public Concept getOrganismType() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM, testMethod);
		return (Concept) attribute.getValue();
	}
	
	@Override
	public void setOrganismType(Concept organismType) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM, testMethod);
		if (attribute != null) {
			if (organismType == null) {
				attribute.setVoided(true);
				attribute.setVoidReason(VOID_MESSAGE);
			}
			attribute.setValue(organismType);
			test.setAttribute(attribute);
			return;
		}
		// Initialize if needed
		attribute = new LabTestAttribute();
		attribute.setAttributeType(Context.getService(LabTestService.class).getLabTestAttributeTypeByTestTypeAndName(
		    dstTestType, MdrtbConcepts.TYPE_OF_ORGANISM));
		attribute.setValue(organismType);
		test.addAttribute(attribute);
	}
	
	@Override
	public String getOrganismTypeNonCoded() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED, testMethod);
		return (String) attribute.getValue();
	}
	
	@Override
	public void setOrganismTypeNonCoded(String organismType) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED, testMethod);
		if (attribute != null) {
			if (organismType == null) {
				attribute.setVoided(true);
				attribute.setVoidReason(VOID_MESSAGE);
			}
			attribute.setValue(organismType);
			test.setAttribute(attribute);
			return;
		}
		// Initialize if needed
		attribute = new LabTestAttribute();
		attribute.setAttributeType(Context.getService(LabTestService.class).getLabTestAttributeTypeByTestTypeAndName(
		    dstTestType, MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED));
		attribute.setValue(organismType);
		test.addAttribute(attribute);
	}
	
	@Override
	public Integer getColoniesInControl() {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES_IN_CONTROL, testMethod);
		return (Integer) attribute.getValue();
	}
	
	@Override
	public void setColoniesInControl(Integer coloniesInControl) {
		LabTestAttribute attribute = Context.getService(LabTestService.class).getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES_IN_CONTROL, testMethod);
		if (attribute != null) {
			if (coloniesInControl == null) {
				attribute.setVoided(true);
				attribute.setVoidReason(VOID_MESSAGE);
			}
			attribute.setValue(coloniesInControl);
			test.setAttribute(attribute);
			return;
		}
		// Initialize if needed
		attribute = new LabTestAttribute();
		attribute.setAttributeType(Context.getService(LabTestService.class).getLabTestAttributeTypeByTestTypeAndName(
		    dstTestType, MdrtbConcepts.COLONIES_IN_CONTROL));
		attribute.setValue(coloniesInControl);
		test.addAttribute(attribute);
	}
	
	@Override
	public List<DstResult> getResults() {
		// Check if the resultsList is already initialized
		if (resultsList == null || resultsList.isEmpty()) {
			Collection<Concept> drugs = Context.getService(MdrtbService.class).getAllDrugResistanceConcepts();
			// Initialize resultsList as a new ArrayList
			resultsList = new ArrayList<>();
			// Loop through each DstResult in the list of results
			for (LabTestAttribute attribute : test.getAttributes()) {
				// Detect TB Drug attributes
				Concept concept = new ConceptDatatype().deserialize(attribute.getAttributeType().getDatatypeConfig());
				if (drugs.contains(concept)) {
					Concept drugResult = (Concept) attribute.getValue();
					// Check if the drug in the result is not null
					if (concept != null && drugResult != null) {
						// Directly add the result to resultsList
						DstResult result = new DstResult(concept, drugResult);
						resultsList.add(result);
					}
				}
			}
		}
		// Return the populated resultsList
		return resultsList;
	}
	
	/**
	 * @deprecated use getResultsList instead
	 */
	@Deprecated
	@Override
	public Map<Integer, List<DstResult>> getResultsMap() {
		/* THIS IS THE IMPLEMENTATION BEFORE MOVING TO COMMON LAB MODULE
	    // Check if the resultsMap is already initialized
	    if (resultsMap == null) {
	        // Initialize resultsMap as a new HashMap
	        resultsMap = new HashMap<Integer, List<DstResult>>();
	        // Loop through each DstResult in the list of results
	        for (DstResult result : getResults()) {
	            // Check if the drug in the result is not null
	            if (result.getDrug() != null) {
	                Integer drug = result.getDrug().getId();
	                // Check if this drug ID is already in the map
	                if (resultsMap.containsKey(drug)) {
	                    // If yes, add the result to the existing list and sort it
	                    resultsMap.get(drug).add(result);
	                    Collections.sort(resultsMap.get(drug));
	                } else {
	                    // If no, create a new list for this drug ID and add the result
	                    List<DstResult> drugResults = new LinkedList<DstResult>();
	                    drugResults.add(result);
	                    resultsMap.put(drug, drugResults);
	                }
	            }
	        }
	    }
	    // Return the populated resultsMap
	    return resultsMap;
	    */
		// Check if the resultsMap is already initialized
		Map<Integer, List<DstResult>> resultsMap = new HashMap<>();
		for (DstResult result : getResults()) {
            List<DstResult> drugResults = new LinkedList<>();
            drugResults.add(result);
			resultsMap.put(result.getDrug().getId(), drugResults);
		}
	    // Return the populated resultsMap
	    return resultsMap;
	}
	
	@Override
	public DstResult addResult() {
		//TODO: What to do here? Get a hint from below
		return null;
		/*
		// create a new obs for the result, set to the proper values
		Obs resultObs = new Obs(this.test.getPerson(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.DST_RESULT), this.test.getObsDatetime(), this.test.getLocation());
		resultObs.setEncounter(this.test.getEncounter());
		// add the result to this obs group
		this.test.addGroupMember(resultObs);
		// now create and return a new DstResult
		return new DstResult(resultObs);
		*/
	}
	
	@Override
	public void removeResult(DstResult result) {
		resultsList.remove(result);
		// ((DstResult) result).voidResult();
	}
	
	@Override
	public String getResultsString() {
		StringBuilder results = new StringBuilder();
		List<DstResult> dstResultsList = getResults();
		Collection<Concept> drugs = Context.getService(MdrtbService.class).getAllDrugResistanceConcepts();
		
		for (Concept drug : drugs) {
			for (DstResult result : dstResultsList) {
				if (result.getDrug().getId().equals(drug.getId())) {
					results.append(result.getDrug().getDisplayString()).append(": ")
					        .append(result.getResult().getShortNameInLocale(Context.getLocale())).append("<br/>");
				}
			}
		}
		return results.length() == 0 ? "N/A" : results.toString();
	}
	
	@Override
	public String getResistantDrugs() {
		StringBuilder results = new StringBuilder();
		Collection<Concept> drugs = Context.getService(MdrtbService.class).getAllDrugResistanceConcepts();
		List<DstResult> dstResultsList = getResults();
		Integer resistantConceptId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG)
		        .getId();
		Set<String> resistantDrugNames = new HashSet<>();
		for (Concept drug : drugs) {
			for (DstResult result : dstResultsList) {
				boolean matches = result.getDrug().getId().equals(drug.getId());
				boolean resistant = result.getResult().getId().equals(resistantConceptId);
				if (matches && resistant) {
					ConceptName name = result.getDrug().getShortNameInLocale(Context.getLocale());
					if (name == null) {
						name = result.getDrug().getName(Context.getLocale());
					}
					resistantDrugNames.add(name.getName());
				}
			}
		}
		for (String name : resistantDrugNames) {
			results.append(name);
			results.append(", ");			
		}
		return results.length() == 0 ? "" : results.toString();
	}
	
	@Override
	public String getSensitiveDrugs() {
		StringBuilder results = new StringBuilder();
		Collection<Concept> drugs = Context.getService(MdrtbService.class).getAllDrugResistanceConcepts();
		List<DstResult> dstResultsList = getResults();
		Integer resistantConceptId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG)
		        .getId();
		Set<String> resistantDrugNames = new HashSet<>();
		for (Concept drug : drugs) {
			for (DstResult result : dstResultsList) {
				boolean matches = result.getDrug().getId().equals(drug.getId());
				boolean resistant = result.getResult().getId().equals(resistantConceptId);
				if (matches && resistant) {
					ConceptName name = result.getDrug().getShortNameInLocale(Context.getLocale());
					if (name == null) {
						name = result.getDrug().getName(Context.getLocale());
					}
					resistantDrugNames.add(name.getName());
				}
			}
		}
		for (String name : resistantDrugNames) {
			results.append(name);
			results.append(", ");			
		}
		return results.length() == 0 ? "" : results.toString();
	}
	
	@Override
	public String getTestType() {
		return "dst";
	}
	
	@Override
	public String getComments() {
		return test.getResultComments();
	}
	
	@Override
	public void setComments(String comments) {
		test.setResultComments(comments);
	}
}
