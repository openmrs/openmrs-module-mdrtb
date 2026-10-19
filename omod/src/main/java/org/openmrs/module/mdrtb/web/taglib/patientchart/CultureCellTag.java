package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.util.List;

import org.openmrs.module.mdrtb.specimen.Test;

public class CultureCellTag extends AbstractBacteriologyCellTag {
	
	private static final long serialVersionUID = 1L;
	
	private List<Test> cultures;
	
	public int doStartTag() {
		renderCell(cultures);
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		clearParameters();
		cultures = null;
		return EVAL_PAGE;
	}
	
	public List<Test> getCultures() {
		return cultures;
	}
	
	public void setCultures(List<Test> cultures) {
		this.cultures = cultures;
	}
	
}
