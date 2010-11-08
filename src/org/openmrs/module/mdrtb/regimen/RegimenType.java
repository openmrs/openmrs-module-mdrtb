package org.openmrs.module.mdrtb.regimen;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.order.RegimenSuggestion;

/**
 * Represents the metadata for how to group Drug Orders together to construct Regimens
 */
public class RegimenType {

	//***** PROPERTIES *****

	private String name;
	private Concept drugSet;
	private List<Drug> drugs;
	private Concept typeQuestion;
	private Concept reasonForStoppingQuestion;
	private List<RegimenSuggestion> suggestions;
 
	//***** CONSRUCTORS *****
	
	public RegimenType() {}
	
	public RegimenType(String name) {
		this.name = name;
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegimenType) {
			RegimenType that = (RegimenType)obj;
			return that.getName() != null && that.getName().equals(this.getName());
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the drugSet
	 */
	public Concept getDrugSet() {
		return drugSet;
	}

	/**
	 * @param drugSet the drugSet to set
	 */
	public void setDrugSet(Concept drugSet) {
		this.drugSet = drugSet;
	}

	/**
	 * @return the drugs
	 */
	public List<Drug> getDrugs() {
		if (drugs == null) {
			drugs = new ArrayList<Drug>();
		}
		return drugs;
	}

	/**
	 * @param drugs the drugs to set
	 */
	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}
	
	/**
	 * @param drug the Drug to add
	 */
	public void addDrug(Drug drug) {
		getDrugs().add(drug);
	}

	/**
	 * @return the typeQuestion
	 */
	public Concept getTypeQuestion() {
		return typeQuestion;
	}

	/**
	 * @param typeQuestion the typeQuestion to set
	 */
	public void setTypeQuestion(Concept typeQuestion) {
		this.typeQuestion = typeQuestion;
	}

	/**
	 * @return the reasonForStoppingQuestion
	 */
	public Concept getReasonForStoppingQuestion() {
		return reasonForStoppingQuestion;
	}

	/**
	 * @param reasonForStoppingQuestion the reasonForStoppingQuestion to set
	 */
	public void setReasonForStoppingQuestion(Concept reasonForStoppingQuestion) {
		this.reasonForStoppingQuestion = reasonForStoppingQuestion;
	}

	/**
	 * @return the suggestions
	 */
	public List<RegimenSuggestion> getSuggestions() {
		return suggestions;
	}

	/**
	 * @param suggestions the suggestions to set
	 */
	public void setSuggestions(List<RegimenSuggestion> suggestions) {
		this.suggestions = suggestions;
	}
}