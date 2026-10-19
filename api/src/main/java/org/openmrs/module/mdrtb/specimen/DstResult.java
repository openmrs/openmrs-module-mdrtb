package org.openmrs.module.mdrtb.specimen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;

public class DstResult implements Comparable<DstResult> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Concept drug;
	
	private Concept result;
	
	private Integer colonies;
	
	private Double concentration;
	
	public DstResult(Concept drug, Concept result) {
		setDrug(drug);
		setResult(result);
	}
	
	public DstResult(Concept drug, Concept result, Integer colonies, Double concentration) {
		setDrug(drug);
		setResult(result);
		this.colonies = colonies;
		this.concentration = concentration;
	}
	
	public Concept getDrug() {
		return drug;
	}
	
	public void setDrug(Concept drug) {
		this.drug = drug;
	}
	
	public Integer getColonies() {
		return colonies;
	}
	
	public Double getConcentration() {
		return concentration;
	}
	
	public Concept getResult() {
		return result;
	}
	
	public void setResult(Concept result) {
		this.result = result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((drug == null) ? 0 : drug.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DstResult other = (DstResult) obj;
		if (drug == null) {
			if (other.drug != null)
				return false;
		} else if (!drug.equals(other.drug))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(DstResult o) {
		return 0;
	}
}
