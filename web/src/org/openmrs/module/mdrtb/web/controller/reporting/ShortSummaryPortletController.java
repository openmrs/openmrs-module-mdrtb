package org.openmrs.module.mdrtb.web.controller.reporting;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class ShortSummaryPortletController extends PatientSummaryPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Return the columns appropriate for this Patient Summary
	 * @return
	 */
	@Override
	protected String[] getSummaryColumns(HttpServletRequest request) {
		String[] columns = request.getParameterValues("columns");
		if (columns == null || columns.length == 0) {
			columns = new String[] {
				"patientId", "fullName", "mdrtbId", "age", "gender", "hivStatus", "hivStatusDate", 
				"currentRegimen", "hospitalizedDate", "ambulatoryDate"
			};
		}
		return columns;
	}
}
