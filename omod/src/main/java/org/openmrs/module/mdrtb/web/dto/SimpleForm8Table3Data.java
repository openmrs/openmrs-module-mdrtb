package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table3Data;

public class SimpleForm8Table3Data extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -8397746792762958376L;
	
	private Integer group2To1;
	
	private Integer relapse;
	
	public SimpleForm8Table3Data(Form8Table3Data form8Table3Data) {
		this.group2To1 = form8Table3Data.getGroup2To1();
		this.relapse = form8Table3Data.getRelapse();
	}
	
	public Integer getGroup2To1() {
		return group2To1;
	}
	
	public void setGroup2To1(Integer group2To1) {
		this.group2To1 = group2To1;
	}
	
	public Integer getRelapse() {
		return relapse;
	}
	
	public void setRelapse(Integer relapse) {
		this.relapse = relapse;
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer integer) {
		
	}
}
