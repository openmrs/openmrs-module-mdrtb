package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;

public class SimpleDataObject extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -6977541171908933802L;
	
	private Integer id;
	
	private String stringData;
	
	private Integer integerData;
	
	private Double floatData;
	
	public SimpleDataObject(String stringData, Integer integerData, Double floatData) {
		id = (int) (Math.random() * 1000);
		setStringData(stringData);
		setIntegerData(integerData);
		setFloatData(floatData);
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getStringData() {
		return stringData;
	}
	
	public void setStringData(String stringData2) {
		this.stringData = stringData2;
	}
	
	public Integer getIntegerData() {
		return integerData;
	}
	
	public void setIntegerData(Integer integerData2) {
		this.integerData = integerData2;
	}
	
	public Double getFloatData() {
		return floatData;
	}
	
	public void setFloatData(Double floatData) {
		this.floatData = floatData;
	}
}
