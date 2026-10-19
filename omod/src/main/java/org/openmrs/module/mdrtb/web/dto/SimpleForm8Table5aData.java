package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table5aData;

public class SimpleForm8Table5aData extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 5549137578720435225L;
	
	private Integer respBacNew;
	
	private Integer respBacOther;
	
	private Integer respBacNewVillager;
	
	private Integer respBacOtherVillager;
	
	public SimpleForm8Table5aData(Form8Table5aData form8Table5aData) {
		this.respBacNew = form8Table5aData.getRespBacNew();
		this.respBacOther = form8Table5aData.getRespBacOther();
		this.respBacNewVillager = form8Table5aData.getRespBacNewVillager();
		this.respBacOtherVillager = form8Table5aData.getRespBacOtherVillager();
	}
	
	public Integer getRespBacNew() {
		return respBacNew;
	}
	
	public void setRespBacNew(Integer respBacNew) {
		this.respBacNew = respBacNew;
	}
	
	public Integer getRespBacOther() {
		return respBacOther;
	}
	
	public void setRespBacOther(Integer respBacOther) {
		this.respBacOther = respBacOther;
	}
	
	public Integer getRespBacNewVillager() {
		return respBacNewVillager;
	}
	
	public void setRespBacNewVillager(Integer respBacNewVillager) {
		this.respBacNewVillager = respBacNewVillager;
	}
	
	public Integer getRespBacOtherVillager() {
		return respBacOtherVillager;
	}
	
	public void setRespBacOtherVillager(Integer respBacOtherVillager) {
		this.respBacOtherVillager = respBacOtherVillager;
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer integer) {
		
	}
}
