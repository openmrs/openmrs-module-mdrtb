package org.openmrs.module.mdrtb.web.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;

public class SimpleTB03MissingData extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private Integer errorCount;
	
	private Integer totalCases;
	
	private Integer errorPercentage;
	
	private ArrayList<SimpleDQItem> dqItems;
	
	@SuppressWarnings("unchecked")
	public SimpleTB03MissingData(Map<String, Object> map) {
		setErrorCount(0);
		if (map.containsKey("errorCount")) {
			setErrorCount((Integer) map.get("errorCount"));
		}
		setErrorPercentage(0);
		if (map.containsKey("errorPercentage")) {
			setErrorPercentage((Integer) map.get("errorPercentage"));
		}
		if (map.containsKey("num")) {
			setTotalCases((Integer) map.get("num"));
		}
		setDqItems(new ArrayList<>());
		if (map.containsKey("missingTB03")) {
			List<DQItem> patientSet = (List<DQItem>) map.get("missingTB03");
			for (DQItem dqItem : patientSet) {
				getDqItems().add(new SimpleDQItem(dqItem));
			}
		}
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer id) {
	}
	
	public Integer getErrorCount() {
		return errorCount;
	}
	
	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}
	
	public Integer getTotalCases() {
		return totalCases;
	}
	
	public void setTotalCases(Integer totalCases) {
		this.totalCases = totalCases;
	}
	
	public Integer getErrorPercentage() {
		return errorPercentage;
	}
	
	public void setErrorPercentage(Integer errorPercentage) {
		this.errorPercentage = errorPercentage;
	}
	
	public ArrayList<SimpleDQItem> getDqItems() {
		return dqItems;
	}
	
	public void setDqItems(ArrayList<SimpleDQItem> dqItems) {
		this.dqItems = dqItems;
	}
}
