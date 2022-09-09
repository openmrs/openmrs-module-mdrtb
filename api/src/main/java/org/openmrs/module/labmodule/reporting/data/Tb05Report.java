package org.openmrs.module.labmodule.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
public class Tb05Report implements ReportSpecification {
	/**
	 * @see ReportSpecificationgetName()
	 */
	
		
	public String getName() {
		return Context.getMessageSourceService().getMessage("labmodule.labDSTReport");
	}

	/**
	 * @see ReportSpecificationgetDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("labmodule.labDSTReport");
	}

	/**
	 * @see ReportSpecificationgetParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), String.class));
		return l;
	}

	/**
	 * @see ReportSpecificationgetRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/Lab-DST-Underscore" + 
				(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}



	/**
	 * @see ReportSpecificationvalidateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer)parameters.get("year");
		if (year == null) {
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleasEnterAYear"));
		}
		/*Integer quarter = (Integer)parameters.get("quarter");*/
		String quarter = (String) parameters.get("quarter");
		
//		if (quarter == null) {
//			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleaseEnterAQuarter"));
//		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, null));
		
		Map<String,Object>pMap = context.getParameterValues();
		Set<String> keySet = pMap.keySet();
		System.out.println("PARAMS");
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
	        System.out.println(iterator.next());
	 }
		return context;
	}

	/**
	 * ReportSpecificationevaluateReport(EvaluationContext)
	 */
	 @SuppressWarnings("unchecked")
	public ReportData evaluateReport(EvaluationContext context) {
		
			Location location = (Location) context.getParameterValue("location");
			String oblast = (String) context.getParameterValue("oblast");
			Date endDate = (Date)context.getParameterValue("endDate");
			Date startDate = (Date)context.getParameterValue("startDate");
			
			ReportDefinition report = new ReportDefinition();
				
			CohortDefinition allLabResults = Cohorts.getAllLabResultDuring(startDate,endDate);
			CohortCrossTabDataSetDefinition d = new CohortCrossTabDataSetDefinition(); 
			
			d.addColumn("total", allLabResults, null); 
			report.addDataSetDefinition("all", d, null); 
		
			ReportData data;
			
			try {
			data = Context.getService(ReportDefinitionService.class).evaluate(
					report, context);
			} catch (EvaluationException e) {
			throw new MdrtbAPIException("Unable to evaluate Lab DST report",
					e);
			}
			return data;
	 	}
	

}
