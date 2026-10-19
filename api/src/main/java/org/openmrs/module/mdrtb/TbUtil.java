package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.Test;

public class TbUtil {
	
	protected static final Log log = LogFactory.getLog(TbUtil.class);
	
	/**
	 * Iterates through all the obs in the test obs group and returns the first one that who concept
	 * matches the specified concept Returns null if obs not found
	 */
	public static Obs getObsFromObsGroup(Concept concept, Obs group) {
		if (group.getGroupMembers() != null) {
			for (Obs obs : group.getGroupMembers()) {
				// need to check for voided obs here because getGroupMembers returns voided obs
				if (!obs.getVoided() && obs.getConcept().equals(concept)) {
					return obs;
				}
			}
		}
		return null;
	}
	
	/**
	 * Iterates through all the top-level obs in the encounter and returns the first one that who
	 * concept matches the specified concept Returns null if obs not found
	 */
	public static Obs getObsFromEncounter(Concept concept, Encounter encounter) {
		Set<Obs> obsSet = encounter.getObsAtTopLevel(false);
		if (obsSet == null) {
			return null;
		}
		for (Obs obs : obsSet) {
			if (Boolean.TRUE.equals(obs.getVoided())) {
				continue;
			}
			if (obs.getConcept().getUuid().equals(concept.getUuid())) {
				if (obs.getValueCoded() != null && obs.getId() != null) {
					try {
						return Context.getObsService().getObs(obs.getId());
					}
					catch (Exception e) {
						return Context.getObsService().getObsByUuid(obs.getUuid());
					}
				}
				return obs;
			}
		}
		return null;
	}
	
	/**
	 * Gets the antiretroviral regimens for a current patient
	 */
	public static List<Regimen> getAntiretroviralRegimens(Patient patient) {
		
		if (patient == null) {
			return null;
		}
		
		return RegimenUtils.getHivRegimenHistory(patient).getAllRegimens();
	}
	
	/**
	 * Returns a set of all encounter types associated with the MDR-TB Program
	 */
	public static Set<EncounterType> getTbEncounterTypes() {
		
		Set<EncounterType> types = new HashSet<>();
		types.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		types.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		types.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		types.add(MdrtbConstants.ET_TRANSFER_OUT);
		types.add(MdrtbConstants.ET_TRANSFER_IN);
		return types;
	}
	
	/**
	 * Gets a specific ProgramWorkflowState, given the concept associated with the state
	 */
	public static ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept) {
		List<ProgramWorkflowState> list = Context.getProgramWorkflowService().getProgramWorkflowStatesByConcept(
		    programWorkflowStateConcept);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * Given a concept, locale, and a string that represents a concept name tag, returns the first
	 * concept name for that concept that matches the language and is tagged with the specified tag
	 */
	public static ConceptName getConceptName(Concept concept, String language, String conceptNameTag) {
		if (concept == null) {
			log.error("No concept provided to findConceptName");
			return null;
		}
		
		ConceptNameTag tag = Context.getConceptService().getConceptNameTagByName(conceptNameTag);
		
		if (tag == null) {
			log.warn("Invalid concept name tag parameter " + conceptNameTag + " passed to findConceptName");
		}
		
		for (ConceptName name : concept.getNames()) {
			if ((language == null || name.getLocale() == null || name.getLocale().getLanguage().equals(language))
			        && (tag == null || name.getTags().contains(tag))) {
				return name;
			}
		}
		
		return null;
	}
	
	/**
	 * Configures the default values for a Test, based on the existing values for other tests in the
	 * specimen Implements the following rule: If this is the first test, and the specimen has a
	 * sample id, set the accession # field with this sample id. If this is not the first test,
	 * then-- If the Accession # on all the existing tests and the sample ID on the specimen are all
	 * the same, set the accession # field with this number. If the Lab, Date Ordered, or Date
	 * Received on all the existing tests are identical, set these fields with these values.
	 */
	public static void setTestDefaults(Specimen specimen, Test test) {
		
		Set<String> accessionNumberSet = new HashSet<>();
		Set<Date> dateOrderedSet = new HashSet<>();
		Set<Date> dateReceivedSet = new HashSet<>();
		Set<Location> labSet = new HashSet<>();
		
		// first add the identifier of the sample to the accession number set
		accessionNumberSet.add(specimen.getIdentifier());
		
		// now loop through all the tests for this sample, excluding the test we want to set the defaults for
		for (Test t : specimen.getTests()) {
			if (t != test) {
				accessionNumberSet.add(t.getAccessionNumber());
				dateOrderedSet.add(t.getDateOrdered());
				dateReceivedSet.add(t.getDateReceived());
				labSet.add(t.getLab());
			}
		}
		
		// test if any of are sets contain exactly one non-null member
		if (accessionNumberSet.size() == 1 && !accessionNumberSet.contains(null)) {
			test.setAccessionNumber(accessionNumberSet.iterator().next());
		}
		if (dateOrderedSet.size() == 1 && !dateOrderedSet.contains(null)) {
			test.setDateOrdered(dateOrderedSet.iterator().next());
		}
		if (dateReceivedSet.size() == 1 && !dateReceivedSet.contains(null)) {
			test.setDateReceived(dateReceivedSet.iterator().next());
		}
		if (labSet.size() == 1 && !labSet.contains(null)) {
			test.setLab(labSet.iterator().next());
		}
	}
	
	/**
	 * Returns true/false if all the fields in the address are empty or null
	 */
	public static Boolean isBlank(PersonAddress address) {
		return StringUtils.isBlank(address.getAddress1()) && StringUtils.isBlank(address.getAddress2())
		        && StringUtils.isBlank(address.getCityVillage()) && StringUtils.isBlank(address.getStateProvince())
		        && StringUtils.isBlank(address.getCountry()) && StringUtils.isBlank(address.getCountyDistrict())
		        && StringUtils.isBlank(address.getAddress3()) && StringUtils.isBlank(address.getPostalCode())
		        && StringUtils.isBlank(address.getAddress4()) && StringUtils.isBlank(address.getLatitude())
		        && StringUtils.isBlank(address.getLongitude()) && StringUtils.isBlank(address.getAddress6())
		        && StringUtils.isBlank(address.getAddress5());
	}
	
}
