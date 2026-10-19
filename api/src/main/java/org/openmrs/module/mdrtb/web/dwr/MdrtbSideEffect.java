package org.openmrs.module.mdrtb.web.dwr;

@Deprecated
public class MdrtbSideEffect {
	
	private String type;
	
	private String agent;
	
	private String date;
	
	private String traitmentSupportif;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTraitmentSupportif() {
		return traitmentSupportif;
	}
	
	public void setTraitmentSupportif(String traitmentSupportif) {
		this.traitmentSupportif = traitmentSupportif;
	}
	
}
