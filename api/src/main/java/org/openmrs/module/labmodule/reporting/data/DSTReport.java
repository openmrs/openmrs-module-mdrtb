package org.openmrs.module.labmodule.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.reporting.DSTReportUtil;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Dst1;
import org.openmrs.module.labmodule.specimen.Dst2;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
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

public class DSTReport implements ReportSpecification /**
 * @see ReportSpecificationgetName()
 */
{
	
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
	l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
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
//	if (year == null) {
//		throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleasEnterAYear"));		
//	}
	/*Integer quarter = (Integer)parameters.get("quarter");*/
	String quarter = (String) parameters.get("quarter");
	
//	if (quarter == null) {
//		throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleaseEnterAQuarter"));
//	}
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
	 	Integer district=null,oblast=null,facility=null; 
	 	Date endDate = (Date)context.getParameterValue("endDate");
		Date startDate = (Date)context.getParameterValue("startDate");
		if(context.getParameterValue("district")!=null && context.getParameterValue("district")!="")
	 		district = (Integer) context.getParameterValue("district");
	 	if(context.getParameterValue("oblast")!=null && context.getParameterValue("oblast")!="")
		  	oblast = (Integer) context.getParameterValue("oblast");
	 	if(context.getParameterValue("facility")!=null && context.getParameterValue("facility")!="")
		  	facility =(Integer) context.getParameterValue("facility");
		
		ReportDefinition report = new ReportDefinition();
		
		Oblast o = null;
		List<Location> locList = Context.getService(TbService.class).getLocationHierarchy(oblast, district, facility);
		if(locList==null)
			locList=Context.getLocationService().getAllLocations();
		
		List<Encounter> tests = Context.getService(TbService.class).getAllPatientTestedAtLocationDuring(startDate, endDate, locList);
		
		
		int totalEncounters = tests.size();
		Double cultureAbsNew = 0.0;
		Double culturePercentNew = 0.0;
		Double cultureAbsPrev = 0.0;
		Double culturePercentPrev = 0.0;
		Double cultureAbsTotal = 0.0;
		Double	culturePercentTotal = 0.0;
		
		Double firstLineDSTAbsNew = 0.0;
		Double firstLineDSTPercentNew = 0.0;
		Double firstLineDSTAbsPrev = 0.0;
		Double firstLineDSTPercentPrev = 0.0;
		Double firstLineDSTAbsTotal = 0.0;
		Double firstLineDSTPercentTotal = 0.0;
		
		Double fSensitiveAbsNew  = 0.0;
		Double fSensitivePercentNew  = 0.0;	
		Double fSensitiveAbsPrev  = 0.0;	
		Double fSensitivePercentPrev  = 0.0;	
		Double fSensitiveAbsTotal  = 0.0;	
		Double fSensitivePercentTotal  = 0.0;
		
		Double anyResistSAbsNew  = 0.0;
		Double anyResistSPercentNew  = 0.0;
		Double anyResistSAbsPrev  = 0.0;
		Double anyResistSPercentPrev  = 0.0;
		Double anyResistSAbsTotal   = 0.0;
		Double anyResistSPercentTotal   = 0.0;
		
		Double anyResistHAbsNew  = 0.0;
		Double anyResistHPercentNew  = 0.0;
		Double anyResistHAbsPrev  = 0.0;
		Double anyResistHPercentPrev  = 0.0;
		Double anyResistHAbsTotal   = 0.0;
		Double anyResistHPercentTotal   = 0.0;
		
		Double anyResistEAbsNew  = 0.0;
		Double anyResistEPercentNew  = 0.0;
		Double anyResistEAbsPrev  = 0.0;
		Double anyResistEPercentPrev  = 0.0;
		Double anyResistEAbsTotal   = 0.0;
		Double anyResistEPercentTotal   = 0.0;
		
		Double anyResistRAbsNew  = 0.0;
		Double anyResistRPercentNew  = 0.0;
		Double anyResistRAbsPrev  = 0.0;
		Double anyResistRPercentPrev  = 0.0;
		Double anyResistRAbsTotal   = 0.0;
		Double anyResistRPercentTotal   = 0.0;
		
		Double monoResistSAbsNew = 0.0;	
		Double monoResistSPercentNew = 0.0; 	 
		Double monoResistSAbsPrev = 0.0;	
		Double monoResistSPercentPrev = 0.0;	
		Double monoResistSAbsTotal 	= 0.0;
		Double monoResistSPercentTotal = 0.0;
		
		Double monoResistHAbsNew = 0.0;	
		Double monoResistHPercentNew = 0.0; 	 
		Double monoResistHAbsPrev = 0.0;	
		Double monoResistHPercentPrev = 0.0;	
		Double monoResistHAbsTotal 	= 0.0;
		Double monoResistHPercentTotal = 0.0;
		
		Double monoResistRAbsNew = 0.0;	
		Double monoResistRPercentNew = 0.0; 	 
		Double monoResistRAbsPrev = 0.0;	
		Double monoResistRPercentPrev = 0.0;	
		Double monoResistRAbsTotal 	= 0.0;
		Double monoResistRPercentTotal = 0.0;
		
		Double monoResistEAbsNew = 0.0;	
		Double monoResistEPercentNew = 0.0; 	 
		Double monoResistEAbsPrev = 0.0;	
		Double monoResistEPercentPrev = 0.0;	
		Double monoResistEAbsTotal 	= 0.0;
		Double monoResistEPercentTotal = 0.0;
		
		Double mdrHRAbsNew 	= 0.0;
		Double mdrHRPercentNew 	= 0.0;
		Double mdrHRAbsPrev = 0.0;
		Double mdrHRPercentPrev = 0.0;
		Double mdrHRAbsTotal = 0.0;
		Double mdrHRPercentTotal = 0.0;
		
		Double mdrHREAbsNew 	= 0.0;
		Double mdrHREPercentNew 	= 0.0;
		Double mdrHREAbsPrev = 0.0;
		Double mdrHREPercentPrev = 0.0;
		Double mdrHREAbsTotal = 0.0;
		Double mdrHREPercentTotal = 0.0;
		
		Double mdrHRSAbsNew 	= 0.0;
		Double mdrHRSPercentNew 	= 0.0;
		Double mdrHRSAbsPrev = 0.0;
		Double mdrHRSPercentPrev = 0.0;
		Double mdrHRSAbsTotal = 0.0;
		Double mdrHRSPercentTotal = 0.0;
		
		Double mdrHRESAbsNew 	= 0.0;
		Double mdrHRESPercentNew 	= 0.0;
		Double mdrHRESAbsPrev = 0.0;
		Double mdrHRESPercentPrev = 0.0;
		Double mdrHRESAbsTotal = 0.0;
		Double mdrHRESPercentTotal = 0.0;
		

		Double pdrHEAbsNew = 0.0; 	
		Double pdrHEPercentNew = 0.0; 	
		Double pdrHEAbsPrev = 0.0; 	
		Double pdrHEPercentPrev = 0.0; 	
		Double pdrHEAbsTotal = 0.0;	
		Double pdrHEPercentTotal = 0.0;
		
		Double pdrHSAbsNew = 0.0; 	
		Double pdrHSPercentNew = 0.0; 	
		Double pdrHSAbsPrev = 0.0; 	
		Double pdrHSPercentPrev = 0.0; 	
		Double pdrHSAbsTotal = 0.0;	
		Double pdrHSPercentTotal = 0.0;
		
		Double pdrHESAbsNew = 0.0; 	
		Double pdrHESPercentNew = 0.0; 	
		Double pdrHESAbsPrev = 0.0; 	
		Double pdrHESPercentPrev = 0.0; 	
		Double pdrHESAbsTotal = 0.0;	
		Double pdrHESPercentTotal = 0.0;
		
		Double pdrREAbsNew = 0.0; 	
		Double pdrREPercentNew = 0.0; 	
		Double pdrREAbsPrev = 0.0; 	
		Double pdrREPercentPrev = 0.0; 	
		Double pdrREAbsTotal = 0.0;	
		Double pdrREPercentTotal = 0.0;
		
		Double pdrRSAbsNew = 0.0; 	
		Double pdrRSPercentNew = 0.0; 	
		Double pdrRSAbsPrev = 0.0; 	
		Double pdrRSPercentPrev = 0.0; 	
		Double pdrRSAbsTotal = 0.0;	
		Double pdrRSPercentTotal = 0.0;
		
		Double pdrRESAbsNew = 0.0; 	
		Double pdrRESPercentNew = 0.0; 	
		Double pdrRESAbsPrev = 0.0; 	
		Double pdrRESPercentPrev = 0.0; 	
		Double pdrRESAbsTotal = 0.0;	
		Double pdrRESPercentTotal = 0.0;
		
		Double pdrESAbsNew = 0.0; 	
		Double pdrESPercentNew = 0.0; 	
		Double pdrESAbsPrev = 0.0; 	
		Double pdrESPercentPrev = 0.0; 	
		Double pdrESAbsTotal = 0.0;	
		Double pdrESPercentTotal = 0.0;
		
		Double dst2AbsNew  = 0.0;	
		Double dst2PercentNew  = 0.0;	
		Double dst2AbsPrev  = 0.0;	
		Double dst2PercentPrev  = 0.0;	
		Double dst2AbsTotal  = 0.0;	
		Double dst2PercentTotal  = 0.0; 
		
		Double dst2SensitiveAbsNew 	= 0.0; 
		Double dst2SensitivePercentNew 	= 0.0; 
		Double dst2SensitiveAbsPrev = 0.0; 	
		Double dst2SensitivePercentPrev = 0.0; 	
		Double dst2SensitiveAbsTotal = 0.0; 	
		Double dst2SensitivePercentTotal = 0.0; 
		
		Double preXdrHrOfxAbsNew = 0.0;
		Double preXdrHrOfxPercentNew = 0.0;
		Double preXdrHrOfxAbsPrev = 0.0; 
		Double preXdrHrOfxPercentPrev = 0.0;
		Double preXdrHrOfxAbsTotal = 0.0; 
		Double preXdrHrOfxPercentTotal = 0.0; 
		
		Double preXdrHrMoxAbsNew = 0.0;
		Double preXdrHrMoxPercentNew = 0.0;
		Double preXdrHrMoxAbsPrev = 0.0;
		Double preXdrHrMoxPercentPrev = 0.0;
		Double preXdrHrMoxAbsTotal = 0.0;
		Double preXdrHrMoxPercentTotal = 0.0; 
		
		Double preXdrHrLfxAbsNew = 0.0;
		Double preXdrHrLfxPercentNew = 0.0;
		Double preXdrHrLfxAbsPrev = 0.0; 
		Double preXdrHrLfxPercentPrev = 0.0;
		Double preXdrHrLfxAbsTotal = 0.0; 
		Double preXdrHrLfxPercentTotal = 0.0;
		
	 	Double preXdrHrCmAbsNew = 0.0;
	 	Double preXdrHrCmPercentNew = 0.0;
	 	Double preXdrHrCmAbsPrev = 0.0;
	 	Double preXdrHrCmPercentPrev = 0.0;
	 	Double preXdrHrCmAbsTotal = 0.0;
	 	Double preXdrHrCmPercentTotal = 0.0; 
	 	
	 	Double preXdrHrKmAbsNew = 0.0; 	
	 	Double preXdrHrKmPercentNew = 0.0; 	
	 	Double preXdrHrKmAbsPrev = 0.0; 	
	 	Double preXdrHrKmPercentPrev = 0.0; 	
	 	Double preXdrHrKmAbsTotal = 0.0; 	
	 	Double preXdrHrKmPercentTotal = 0.0; 
		
	 	Double preXdrHrAmAbsNew = 0.0; 	
	 	Double preXdrHrAmPercentNew = 0.0; 	
	 	Double preXdrHrAmAbsPrev = 0.0; 	
	 	Double preXdrHrAmPercentPrev = 0.0; 	
	 	Double preXdrHrAmAbsTotal = 0.0; 	
	 	Double preXdrHrAmPercentTotal = 0.0; 
	 	
	 	Double preXdrHrKmAmAbsNew = 0.0;
	 	Double preXdrHrKmAmPercentNew = 0.0;
	 	Double preXdrHrKmAmAbsPrev = 0.0;
	 	Double preXdrHrKmAmPercentPrev = 0.0;
	 	Double preXdrHrKmAmAbsTotal = 0.0; 
	 	Double preXdrHrKmAmPercentTotal = 0.0; 
		
	 	Double preXdrHrCmAmAbsNew = 0.0;
	 	Double preXdrHrCmAmPercentNew = 0.0;
	 	Double preXdrHrCmAmAbsPrev = 0.0; 
	 	Double preXdrHrCmAmPercentPrev = 0.0; 
	 	Double preXdrHrCmAmAbsTotal = 0.0;
	 	Double preXdrHrCmAmPercentTotal = 0.0; 
	 	
	 	Double preXdrHrKmCmAbsNew = 0.0;
	 	Double preXdrHrKmCmPercentNew = 0.0; 
	 	Double preXdrHrKmCmAbsPrev = 0.0;
	 	Double preXdrHrKmCmPercentPrev = 0.0;
	 	Double preXdrHrKmCmAbsTotal = 0.0; 	
	 	Double preXdrHrKmCmPercentTotal = 0.0; 
	 	
	 	Double preXdrHrKmCmAmAbsNew = 0.0;
	 	Double preXdrHrKmCmAmPercentNew = 0.0; 
	 	Double preXdrHrKmCmAmAbsPrev = 0.0;
	 	Double preXdrHrKmCmAmPercentPrev = 0.0;
	 	Double preXdrHrKmCmAmAbsTotal = 0.0;
	 	Double preXdrHrKmCmAmPercentTotal = 0.0;
	 	
	 	
	 	Double  xdrHrFlqKmAbsNew = 0.0;
	 	Double  xdrHrFlqKmPercentNew = 0.0; 
	 	Double  xdrHrFlqKmAbsPrev = 0.0;
	 	Double  xdrHrFlqKmPercentPrev = 0.0;
	 	Double  xdrHrFlqKmAbsTotal = 0.0;
	 	Double  xdrHrFlqKmPercentTotal = 0.0;

	
	 	Double  xdrHrFlqCmAbsNew = 0.0;
	 	Double  xdrHrFlqCmPercentNew = 0.0;
	 	Double  xdrHrFlqCmAbsPrev = 0.0; 
	 	Double  xdrHrFlqCmPercentPrev = 0.0;
	 	Double  xdrHrFlqCmAbsTotal = 0.0; 
	 	Double  xdrHrFlqCmPercentTotal = 0.0;

	
	 	Double  xdrHrFlqAmAbsNew = 0.0;
	 	Double  xdrHrFlqAmPercentNew = 0.0;
	 	Double  xdrHrFlqAmAbsPrev = 0.0;
	 	Double  xdrHrFlqAmPercentPrev = 0.0;
	 	Double  xdrHrFlqAmAbsTotal = 0.0;
	 	Double  xdrHrFlqAmPercentTotal = 0.0;

	
	 	Double  xdrHrFlqKmCmAbsNew = 0.0;
	 	Double  xdrHrFlqKmCmPercentNew = 0.0;
	 	Double  xdrHrFlqKmCmAbsPrev = 0.0;
	 	Double  xdrHrFlqKmCmPercentPrev = 0.0;
	 	Double  xdrHrFlqKmCmAbsTotal = 0.0;
	 	Double  xdrHrFlqKmCmPercentTotal = 0.0;

	
	 	Double  xdrHrFlqKmAmAbsNew = 0.0;
	 	Double  xdrHrFlqKmAmPercentNew = 0.0;
	 	Double  xdrHrFlqKmAmAbsPrev = 0.0;
	 	Double  xdrHrFlqKmAmPercentPrev = 0.0;
	 	Double  xdrHrFlqKmAmAbsTotal = 0.0;
	 	Double  xdrHrFlqKmAmPercentTotal = 0.0;

	
	 	Double  xdrHrFlqCmAmAbsNew = 0.0;
	 	Double  xdrHrFlqCmAmPercentNew = 0.0;
	 	Double  xdrHrFlqCmAmAbsPrev = 0.0; 
	 	Double  xdrHrFlqCmAmPercentPrev = 0.0;
	 	Double  xdrHrFlqCmAmAbsTotal = 0.0; 
	 	Double  xdrHrFlqCmAmPercentTotal = 0.0;

	
	 	Double  xdrHrFlqCmKmAmAbsNew = 0.0;
	 	Double  xdrHrFlqCmKmAmPercentNew = 0.0; 
	 	Double  xdrHrFlqCmKmAmAbsPrev = 0.0;
	 	Double  xdrHrFlqCmKmAmPercentPrev = 0.0;
	 	Double  xdrHrFlqCmKmAmAbsTotal = 0.0;
	 	Double  xdrHrFlqCmKmAmPercentTotal = 0.0;

	 
	 	Double  xdrHrMoxKmAbsNew = 0.0; 	
	 	Double  xdrHrMoxKmPercentNew = 0.0; 	
	 	Double  xdrHrMoxKmAbsPrev = 0.0; 	
	 	Double  xdrHrMoxKmPercentPrev = 0.0; 	
	 	Double  xdrHrMoxKmAbsTotal = 0.0; 	
	 	Double  xdrHrMoxKmPercentTotal = 0.0;

	
	 	Double  xdrHrMoxCmAbsNew = 0.0; 	
	 	Double  xdrHrMoxCmPercentNew = 0.0; 	
	 	Double  xdrHrMoxCmAbsPrev = 0.0; 	
	 	Double  xdrHrMoxCmPercentPrev = 0.0; 	
	 	Double  xdrHrMoxCmAbsTotal = 0.0; 
	 	Double  xdrHrMoxCmPercentTotal = 0.0;

	 	Double  xdrHrMoxAmAbsNew = 0.0; 	
	 	Double  xdrHrMoxAmPercentNew = 0.0; 	
	 	Double  xdrHrMoxAmAbsPrev = 0.0; 	
	 	Double  xdrHrMoxAmPercentPrev = 0.0; 	
	 	Double  xdrHrMoxAmAbsTotal = 0.0; 	
	 	Double  xdrHrMoxAmPercentTotal = 0.0;

	
	 	Double  xdrHrMoxKmCmAbsNew = 0.0; 	
	 	Double  xdrHrMoxKmCmPercentNew = 0.0; 	
	 	Double  xdrHrMoxKmCmAbsPrev = 0.0; 	
	 	Double  xdrHrMoxKmCmPercentPrev = 0.0; 	
	 	Double  xdrHrMoxKmCmAbsTotal = 0.0; 	
	 	Double  xdrHrMoxKmCmPercentTotal = 0.0;

	 
		Double xdrHrMoxKmAmAbsNew = 0.0;
		Double xdrHrMoxKmAmPercentNew = 0.0;
		Double xdrHrMoxKmAmAbsPrev = 0.0;
		Double xdrHrMoxKmAmPercentPrev = 0.0;
		Double xdrHrMoxKmAmAbsTotal = 0.0;
		Double xdrHrMoxKmAmPercentTotal = 0.0;
	 
		Double xdrHrMoxCmAmAbsNew = 0.0;
		Double xdrHrMoxCmAmPercentNew = 0.0;
		Double xdrHrMoxCmAmAbsPrev = 0.0;
		Double xdrHrMoxCmAmPercentPrev = 0.0;
		Double xdrHrMoxCmAmAbsTotal = 0.0;
		Double xdrHrMoxCmAmPercentTotal = 0.0;

		Double xdrHrMoxCmKmAmAbsNew = 0.0;
		Double xdrHrMoxCmKmAmPercentNew = 0.0;
		Double xdrHrMoxCmKmAmAbsPrev = 0.0;
		Double xdrHrMoxCmKmAmPercentPrev = 0.0;
		Double xdrHrMoxCmKmAmAbsTotal = 0.0;
		Double xdrHrMoxCmKmAmPercentTotal = 0.0;

		Double xdrHrLfxKmAbsNew = 0.0;
		Double xdrHrLfxKmPercentNew = 0.0;
		Double xdrHrLfxKmAbsPrev = 0.0;
		Double xdrHrLfxKmPercentPrev = 0.0;
		Double xdrHrLfxKmAbsTotal = 0.0;
		Double xdrHrLfxKmPercentTotal = 0.0;

		Double xdrHrLfxCmAbsNew = 0.0;
		Double xdrHrLfxCmPercentNew = 0.0;
		Double xdrHrLfxCmAbsPrev = 0.0;
		Double xdrHrLfxCmPercentPrev = 0.0;
		Double xdrHrLfxCmAbsTotal = 0.0;
		Double xdrHrLfxCmPercentTotal = 0.0;

		Double xdrHrLfxAmAbsNew = 0.0;
		Double xdrHrLfxAmPercentNew = 0.0;
		Double xdrHrLfxAmAbsPrev = 0.0;
		Double xdrHrLfxAmPercentPrev = 0.0;
		Double xdrHrLfxAmAbsTotal = 0.0;
		Double xdrHrLfxAmPercentTotal = 0.0;

		Double xdrHrLfxKmCmAbsNew = 0.0;
		Double xdrHrLfxKmCmPercentNew = 0.0;
		Double xdrHrLfxKmCmAbsPrev = 0.0;
		Double xdrHrLfxKmCmPercentPrev = 0.0;
		Double xdrHrLfxKmCmAbsTotal = 0.0;
		Double xdrHrLfxKmCmPercentTotal = 0.0;

		Double xdrHrLfxKmAmAbsNew = 0.0;
		Double xdrHrLfxKmAmPercentNew = 0.0;
		Double xdrHrLfxKmAmAbsPrev = 0.0;
		Double xdrHrLfxKmAmPercentPrev = 0.0;
		Double xdrHrLfxKmAmAbsTotal = 0.0;
		Double xdrHrLfxKmAmPercentTotal = 0.0;

		Double xdrHrLfxCmAmAbsNew = 0.0;
		Double xdrHrLfxCmAmPercentNew = 0.0;
		Double xdrHrLfxCmAmAbsPrev = 0.0;
		Double xdrHrLfxCmAmPercentPrev = 0.0;
		Double xdrHrLfxCmAmAbsTotal = 0.0;
		Double xdrHrLfxCmAmPercentTotal = 0.0;

		Double xdrHrLfxCmKmAmAbsNew = 0.0;
		Double xdrHrLfxCmKmAmPercentNew = 0.0;
		Double xdrHrLfxCmKmAmAbsPrev = 0.0;
		Double xdrHrLfxCmKmAmPercentPrev = 0.0;
		Double xdrHrLfxCmKmAmAbsTotal = 0.0;
		Double xdrHrLfxCmKmAmPercentTotal = 0.0; 
		
		Double resistToBDLAbsNew = 0.0;
		Double resistToBDLPercentNew = 0.0;
		Double resistToBDLAbsPrev = 0.0;
		Double resistToBDLPercentPrev = 0.0;
		Double resistToBDLAbsTotal = 0.0;
		Double resistToBDLPercentTotal = 0.0;

		Double resistToDLMAbsNew = 0.0;
		Double resistToDLMPercentNew = 0.0;
		Double resistToDLMAbsPrev = 0.0;
		Double resistToDLMPercentPrev = 0.0;
		Double resistToDLMAbsTotal = 0.0;
		Double resistToDLMPercentTotal = 0.0;

		Double resistToCLOAbsNew = 0.0;
		Double resistToCLOPercentNew = 0.0;
		Double resistToCLOAbsPrev = 0.0;
		Double resistToCLOPercentPrev = 0.0;
		Double resistToCLOAbsTotal = 0.0;
		Double resistToCLOPercentTotal = 0.0;

		Double resistToLINEAbsNew = 0.0;
		Double resistToLINEPercentNew = 0.0;
		Double resistToLINEAbsPrev = 0.0;
		Double resistToLINEPercentPrev = 0.0;
		Double resistToLINEAbsTotal = 0.0;
		Double resistToLINEPercentTotal = 0.0;

		Double resistToPASKAbsNew = 0.0;
		Double resistToPASKPercentNew = 0.0;
		Double resistToPASKAbsPrev = 0.0;
		Double resistToPASKPercentPrev = 0.0;
		Double resistToPASKAbsTotal = 0.0;
		Double resistToPASKPercentTotal = 0.0;
	 	
	 	
		for(Encounter e : tests) {
			
			LabResultImpl lri = new LabResultImpl(e);
			
			List<Culture> cultures = lri.getCultures();
			List<Dst1> dst1s = lri.getDst1s();
			List<Dst2> dst2s = lri.getDst2s();
			Boolean mdr = Boolean.TRUE;
			
			
			//NEW
			if(lri.getInvestigationPurpose().getId().intValue() == Context.getService(TbService.class).getConcept(TbConcepts.NEW_CASE).getId().intValue()) {
				for(Culture c : cultures) {
					cultureAbsNew++;
					cultureAbsTotal++;
				}
				
				for(Dst1 d : dst1s) {
					firstLineDSTAbsNew++;
					firstLineDSTAbsTotal++;
					
					
					if(DSTReportUtil.isFullSensitive(d)) {
						fSensitiveAbsNew++;
						fSensitiveAbsTotal++;
					}
					
					else {
						
						//ANY
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH())) {
							anyResistHAbsNew++;
							anyResistHAbsTotal++;
						}
					
						if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
							anyResistRAbsNew++;
							anyResistRAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE())) {
							anyResistEAbsNew++;
							anyResistEAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
							anyResistSAbsNew++;
							anyResistSAbsTotal++;
						}

						//MONO
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistHAbsNew++;
							monoResistHAbsTotal++;
						}
					
						if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistRAbsNew++;
							monoResistRAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistEAbsNew++;
							monoResistEAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistSAbsNew++;
							monoResistSAbsTotal++;
						}
						
						//MDR
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
							mdr = Boolean.TRUE;	
							//HR
							if(DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
									DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE())){
								 
										mdrHRAbsNew++;
										mdrHRAbsTotal++;
							}
					
							//HRE
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									mdrHREAbsNew++;
									mdrHREAbsTotal++;
							}
							
							//HRS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								mdrHRSAbsNew++;
								mdrHRSAbsTotal++;
							}
						
							//HRES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								mdrHRESAbsNew++;
								mdrHRESAbsTotal++;
							}
						}
						
						//PDR - H
						else if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH())) {
					
							//HE
							if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									pdrHEAbsNew++;
									pdrHEAbsTotal++;
							}
							
							//HS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrHSAbsNew++;
								pdrHSAbsTotal++;
							}
						
							//HES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrHESAbsNew++;
								pdrHESAbsTotal++;
							}
						}
						
						//PDR - R
						else if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
					
							//HE
							if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									pdrREAbsNew++;
									pdrREAbsTotal++;
							}
							
							//HS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrRSAbsNew++;
								pdrRSAbsTotal++;
							}
						
							//HES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrRESAbsNew++;
								pdrRESAbsTotal++;
							}
						}
						
						//PDR - ES
						else if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) && DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrESAbsNew++;
								pdrESAbsTotal++;

						}
					}
				}
				
				for(Dst2 dst : dst2s) {
					
					System.out.println("NEW ID: " + e.getPatient().getId());
					Boolean flq = DSTReportUtil.isFlqResistant(dst);
					System.out.println("flq:"+ flq);
					
					Boolean am = DSTReportUtil.isResistant(dst.getResistanceAm());
					System.out.println("am:"+ am);
					Boolean km = DSTReportUtil.isResistant(dst.getResistanceKm());
					System.out.println("km:"+ km);
					Boolean cm = DSTReportUtil.isResistant(dst.getResistanceCm());
					System.out.println("cm:"+ cm);
					Boolean pto = DSTReportUtil.isResistant(dst.getResistancePto());
					System.out.println("pto:"+ pto);
					Boolean lzd = DSTReportUtil.isResistant(dst.getResistanceLzd());
					System.out.println("lzd:"+ lzd);
					Boolean cfz = DSTReportUtil.isResistant(dst.getResistanceCfz());
					System.out.println("cfz:"+ cfz);
					Boolean bdq = DSTReportUtil.isResistant(dst.getResistanceBdq());
					System.out.println("bdq:"+ bdq);
					Boolean dlm = DSTReportUtil.isResistant(dst.getResistanceDlm());
					System.out.println("dlm:"+ dlm);
					Boolean pas = DSTReportUtil.isResistant(dst.getResistancePas());
					System.out.println("pas:"+ pas);
					Boolean ofx = DSTReportUtil.isResistant(dst.getResistanceOfx());
					System.out.println("ofx:"+ ofx);
					Boolean mfx = DSTReportUtil.isResistant(dst.getResistanceMox());
					System.out.println("mfx:"+ mfx);
					Boolean lfx = DSTReportUtil.isResistant(dst.getResistanceLfx());
					System.out.println("lfx:"+ lfx);
					
					dst2AbsNew++;
					dst2AbsTotal++;
					
					if(DSTReportUtil.isFullSensitive(dst)) {
						dst2SensitiveAbsNew++;
						dst2SensitiveAbsTotal++;
					}
					
					else if(mdr) {
						
						if(flq!=null && flq && cm!=null && cm && km!=null && km && am!=null && am) {
							xdrHrFlqCmKmAmAbsNew++;
							xdrHrFlqCmKmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && cm!=null && cm && am!=null && am) {
							xdrHrFlqCmAmAbsNew++;
							xdrHrFlqCmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km && am!=null && am) {
							xdrHrFlqKmAmAbsNew++;
							xdrHrFlqKmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km && cm!=null && cm) {
							xdrHrFlqKmCmAbsNew++;
							xdrHrFlqKmCmAbsTotal++;
						}
						
						else if(flq!=null && flq && am!=null && am) {
							xdrHrFlqAmAbsNew++;
							xdrHrFlqAmAbsTotal++;
						}
						
						else if(flq!=null && flq && cm!=null && cm) {
							xdrHrFlqCmAbsNew++;
							xdrHrFlqCmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km) {
							xdrHrFlqKmAbsNew++;
							xdrHrFlqKmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm && km!=null && km && am!=null && am) {
							xdrHrMoxCmKmAmAbsNew++;
							xdrHrMoxCmKmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm && am!=null && am) {
							xdrHrMoxCmAmAbsNew++;
							xdrHrMoxCmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km && am!=null && am) {
							xdrHrMoxKmAmAbsNew++;
							xdrHrMoxKmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km && cm!=null && cm) {
							xdrHrMoxKmCmAbsNew++;
							xdrHrMoxKmCmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km) {
							xdrHrMoxKmAbsNew++;
							xdrHrMoxKmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm) {
							xdrHrMoxCmAbsNew++;
							xdrHrMoxCmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && am!=null && am) {
							xdrHrMoxAmAbsNew++;
							xdrHrMoxAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm && km!=null && km && am!=null && am) {
							xdrHrLfxCmKmAmAbsNew++;
							xdrHrLfxCmKmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm && am!=null && am) {
							xdrHrLfxCmAmAbsNew++;
							xdrHrLfxCmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km && am!=null && am) {
							xdrHrLfxKmAmAbsNew++;
							xdrHrLfxKmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km && cm!=null && cm) {
							xdrHrLfxKmCmAbsNew++;
							xdrHrLfxKmCmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km) {
							xdrHrLfxKmAbsNew++;
							xdrHrLfxKmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm) {
							xdrHrLfxCmAbsNew++;
							xdrHrLfxCmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && am!=null && am) {
							xdrHrLfxAmAbsNew++;
							xdrHrLfxAmAbsTotal++;
						}
						
						//PreXDR
						else if(km!=null && km && cm!=null && cm && am!=null && am) {
							preXdrHrKmCmAmAbsNew++;
							preXdrHrKmCmAmAbsTotal++;
						}
						
						else if(km!=null && km && cm!=null && cm) {
							preXdrHrKmCmAbsNew++;
							preXdrHrKmCmAbsTotal++;
						}
						
						else if(am!=null && am && cm!=null && cm) {
							preXdrHrCmAmAbsNew++;
							preXdrHrCmAmAbsTotal++;
						}
						
						else if(km!=null && km && am!=null && am) {
							preXdrHrKmAmAbsNew++;
							preXdrHrKmAmAbsTotal++;
						}
						
						else if(km!=null && km && am!=null && am) {
							preXdrHrKmAmAbsNew++;
							preXdrHrKmAmAbsTotal++;
						}
						
						else if(km!=null && km) {
							preXdrHrKmAbsNew++;
							preXdrHrKmAbsTotal++;
						}
						
						else if(cm!=null && cm) {
							preXdrHrCmAbsNew++;
							preXdrHrCmAbsTotal++;
						}
						
						else if(am!=null && am) {
							preXdrHrAmAbsNew++;
							preXdrHrAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx) {
							preXdrHrLfxAbsNew++;
							preXdrHrLfxAbsTotal++;
						}
						
						else if(mfx!=null && mfx) {
							preXdrHrMoxAbsNew++;
							preXdrHrMoxAbsTotal++;
						}
						
						else if(ofx!=null && ofx) {
							preXdrHrOfxAbsNew++;
							preXdrHrOfxAbsTotal++;
						}
					}
					
					if(bdq!=null && bdq) {
						resistToBDLAbsNew++;
						resistToBDLAbsTotal++;
					}
					
					if(dlm!=null && dlm) {
						resistToDLMAbsNew++;
						resistToDLMAbsTotal++;
					}
					
					if(cfz!=null && cfz) {
						resistToCLOAbsNew++;
						resistToCLOAbsTotal++;
					}
					
					if(lzd!=null && lzd) {
						resistToLINEAbsNew++;
						resistToLINEAbsTotal++;
					}
					
					if(pas!=null && pas) {
						resistToPASKAbsNew++;
						resistToPASKAbsTotal++;
					}
				}
			}
			
			//PREVIOISLY TREATED
			else if(lri.getInvestigationPurpose().getId().intValue() == Context.getService(TbService.class).getConcept(TbConcepts.REPEAT_CASE).getId().intValue()) {
				for(Culture c : cultures) {
					cultureAbsPrev++;
					cultureAbsTotal++;
				}
				
				for(Dst1 d : dst1s) {
					firstLineDSTAbsPrev++;
					firstLineDSTAbsTotal++;
					
					if(DSTReportUtil.isFullSensitive(d)) {
						fSensitiveAbsPrev++;
						fSensitiveAbsTotal++;
					}
					
					else {
						//ANY
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH())) {
							anyResistHAbsPrev++;
							anyResistHAbsTotal++;
						}
					
						if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
							anyResistRAbsPrev++;
							anyResistRAbsTotal++;
						}
					
					
						if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE())) {
							anyResistEAbsPrev++;
							anyResistEAbsTotal++;
						}
										
						if(DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
							anyResistSAbsPrev++;
							anyResistSAbsTotal++;
						}
						
						//MONO
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistHAbsPrev++;
							monoResistHAbsTotal++;
						}
					
						if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistRAbsPrev++;
							monoResistRAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistEAbsPrev++;
							monoResistEAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							monoResistSAbsPrev++;
							monoResistSAbsTotal++;
						}
						
						//MDR
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
							mdr = Boolean.TRUE;	
							//HR
							if(DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
									DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE())){
								 
										mdrHRAbsPrev++;
										mdrHRAbsTotal++;
							}
					
							//HRE
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									mdrHREAbsPrev++;
									mdrHREAbsTotal++;
							}
							
							//HRS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								mdrHRSAbsPrev++;
								mdrHRSAbsTotal++;
							}
						
							//HRES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								mdrHRESAbsPrev++;
								mdrHRESAbsTotal++;
							}
						}
						
						//PDR - H
						else if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH())) {
					
							//HE
							if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									pdrHEAbsPrev++;
									pdrHEAbsTotal++;
							}
							
							//HS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrHSAbsPrev++;
								pdrHSAbsTotal++;
							}
						
							//HES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrHESAbsPrev++;
								pdrHESAbsTotal++;
							}
						}
						
						//PDR - R
						else if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
					
							//HE
							if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									pdrREAbsPrev++;
									pdrREAbsTotal++;
							}
							
							//HS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrRSAbsPrev++;
								pdrRSAbsTotal++;
							}
						
							//HES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrRESAbsPrev++;
								pdrRESAbsTotal++;
							}
						}
						
						//PDR - ES
						else if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) && DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrESAbsPrev++;
								pdrESAbsTotal++;
						
						}
					}
				}
				
				for(Dst2 dst : dst2s) {
					
					System.out.println("PREV ID: " + e.getPatient().getId());
					Boolean flq = DSTReportUtil.isFlqResistant(dst);
					System.out.println("flq:"+ flq);
					
					Boolean am = DSTReportUtil.isResistant(dst.getResistanceAm());
					System.out.println("am:"+ am);
					Boolean km = DSTReportUtil.isResistant(dst.getResistanceKm());
					System.out.println("km:"+ km);
					Boolean cm = DSTReportUtil.isResistant(dst.getResistanceCm());
					System.out.println("cm:"+ cm);
					Boolean pto = DSTReportUtil.isResistant(dst.getResistancePto());
					System.out.println("pto:"+ pto);
					Boolean lzd = DSTReportUtil.isResistant(dst.getResistanceLzd());
					System.out.println("lzd:"+ lzd);
					Boolean cfz = DSTReportUtil.isResistant(dst.getResistanceCfz());
					System.out.println("cfz:"+ cfz);
					Boolean bdq = DSTReportUtil.isResistant(dst.getResistanceBdq());
					System.out.println("bdq:"+ bdq);
					Boolean dlm = DSTReportUtil.isResistant(dst.getResistanceDlm());
					System.out.println("dlm:"+ dlm);
					Boolean pas = DSTReportUtil.isResistant(dst.getResistancePas());
					System.out.println("pas:"+ pas);
					Boolean ofx = DSTReportUtil.isResistant(dst.getResistanceOfx());
					System.out.println("ofx:"+ ofx);
					Boolean mfx = DSTReportUtil.isResistant(dst.getResistanceMox());
					System.out.println("mfx:"+ mfx);
					Boolean lfx = DSTReportUtil.isResistant(dst.getResistanceLfx());
					System.out.println("lfx:"+ lfx);
					
					dst2AbsPrev++;
					dst2AbsTotal++;
					
					if(DSTReportUtil.isFullSensitive(dst)) {
						dst2SensitiveAbsPrev++;
						dst2SensitiveAbsTotal++;
					}
					
					else if(mdr) {
						
						if(flq!=null && flq && cm!=null && cm && km!=null && km && am!=null && am) {
							xdrHrFlqCmKmAmAbsPrev++;
							xdrHrFlqCmKmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && cm!=null && cm && am!=null && am) {
							xdrHrFlqCmAmAbsPrev++;
							xdrHrFlqCmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km && am!=null && am) {
							xdrHrFlqKmAmAbsPrev++;
							xdrHrFlqKmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km && cm!=null && cm) {
							xdrHrFlqKmCmAbsPrev++;
							xdrHrFlqKmCmAbsTotal++;
						}
						
						else if(flq!=null && flq && am!=null && am) {
							xdrHrFlqAmAbsPrev++;
							xdrHrFlqAmAbsTotal++;
						}
						
						else if(flq!=null && flq && cm!=null && cm) {
							xdrHrFlqCmAbsPrev++;
							xdrHrFlqCmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km) {
							xdrHrFlqKmAbsPrev++;
							xdrHrFlqKmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm && km!=null && km && am!=null && am) {
							xdrHrMoxCmKmAmAbsPrev++;
							xdrHrMoxCmKmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm && am!=null && am) {
							xdrHrMoxCmAmAbsPrev++;
							xdrHrMoxCmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km && am!=null && am) {
							xdrHrMoxKmAmAbsPrev++;
							xdrHrMoxKmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km && cm!=null && cm) {
							xdrHrMoxKmCmAbsPrev++;
							xdrHrMoxKmCmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km) {
							xdrHrMoxKmAbsPrev++;
							xdrHrMoxKmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm) {
							xdrHrMoxCmAbsPrev++;
							xdrHrMoxCmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && am!=null && am) {
							xdrHrMoxAmAbsPrev++;
							xdrHrMoxAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm && km!=null && km && am!=null && am) {
							xdrHrLfxCmKmAmAbsPrev++;
							xdrHrLfxCmKmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm && am!=null && am) {
							xdrHrLfxCmAmAbsPrev++;
							xdrHrLfxCmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km && am!=null && am) {
							xdrHrLfxKmAmAbsPrev++;
							xdrHrLfxKmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km && cm!=null && cm) {
							xdrHrLfxKmCmAbsPrev++;
							xdrHrLfxKmCmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km) {
							xdrHrLfxKmAbsPrev++;
							xdrHrLfxKmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm) {
							xdrHrLfxCmAbsPrev++;
							xdrHrLfxCmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && am!=null && am) {
							xdrHrLfxAmAbsPrev++;
							xdrHrLfxAmAbsTotal++;
						}
						//Pre-XDR
						else if(km!=null && km && cm!=null && cm && am!=null && am) {
							preXdrHrKmCmAmAbsPrev++;
							preXdrHrKmCmAmAbsTotal++;
						}
						
						else if(km!=null && km && cm!=null && cm) {
							preXdrHrKmCmAbsPrev++;
							preXdrHrKmCmAbsTotal++;
						}
						
						else if(am!=null && am && cm!=null && cm) {
							preXdrHrCmAmAbsPrev++;
							preXdrHrCmAmAbsTotal++;
						}
						
						else if(km!=null && km && am!=null && am) {
							preXdrHrKmAmAbsPrev++;
							preXdrHrKmAmAbsTotal++;
						}
						
						else if(km!=null && km && am!=null && am) {
							preXdrHrKmAmAbsPrev++;
							preXdrHrKmAmAbsTotal++;
						}
						
						else if(km!=null && km) {
							preXdrHrKmAbsPrev++;
							preXdrHrKmAbsTotal++;
						}
						
						else if(cm!=null && cm) {
							preXdrHrCmAbsPrev++;
							preXdrHrCmAbsTotal++;
						}
						
						else if(am!=null && am) {
							preXdrHrAmAbsPrev++;
							preXdrHrAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx) {
							preXdrHrLfxAbsPrev++;
							preXdrHrLfxAbsTotal++;
						}
						
						else if(mfx!=null && mfx) {
							preXdrHrMoxAbsPrev++;
							preXdrHrMoxAbsTotal++;
						}
						
						else if(ofx!=null && ofx) {
							preXdrHrOfxAbsPrev++;
							preXdrHrOfxAbsTotal++;
						}
					}
					
					//PREV
					if(bdq!=null && bdq) {
						resistToBDLAbsPrev++;
						resistToBDLAbsTotal++;
					}
					
					if(dlm!=null && dlm) {
						resistToDLMAbsPrev++;
						resistToDLMAbsTotal++;
					}
					
					if(cfz!=null && cfz) {
						resistToCLOAbsPrev++;
						resistToCLOAbsTotal++;
					}
					
					if(lzd!=null && lzd) {
						resistToLINEAbsPrev++;
						resistToLINEAbsTotal++;
					}
					
					if(pas!=null && pas) {
						resistToPASKAbsPrev++;
						resistToPASKAbsTotal++;
					}
					
				}
			}
			
			//OTHER
			/*else {
				for(Culture c : cultures) {
					
					cultureAbsTotal++;
				}
				
				for(Dst1 d : dst1s) {
					
					firstLineDSTAbsTotal++;
					
					if(DSTReportUtil.isFullSensitive(d)) {
						fSensitiveAbsTotal++;
					}
					
					else {
						//ANY
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH())) {
							anyResistHAbsTotal++;
						}	
					
						if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
							anyResistRAbsTotal++;
						}
					
					
						if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE())) {
							anyResistEAbsTotal++;
						}
										
						if(DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
							anyResistSAbsTotal++;
						}
						
						//MONO
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							
							monoResistHAbsTotal++;
						}
					
						if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							
							monoResistRAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							
							monoResistEAbsTotal++;
						}
						
						if(DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && !DSTReportUtil.isResistant(d.getResistanceR()) &&
								DSTReportUtil.isResistant(d.getResistanceH())!=null && !DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) && 
								DSTReportUtil.isResistant(d.getResistanceZ())!=null && !DSTReportUtil.isResistant(d.getResistanceZ())) {
							
							monoResistSAbsTotal++;
						}
						
						//MDR
						if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) &&
								DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
							mdr = Boolean.TRUE;	
							//HR
							if(DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS()) &&
									DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE())){
								 
										
										mdrHRAbsTotal++;
							}
					
							//HRE
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
									
									mdrHREAbsTotal++;
							}
							
							//HRS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								
								mdrHRSAbsTotal++;
							}
						
							//HRES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								
								mdrHRESAbsTotal++;
							}
						}
						
						//PDR - H
						else if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH())) {
					
							//HE
							if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {

									pdrHEAbsTotal++;
							}
							
							//HS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								
								pdrHSAbsTotal++;
							}
						
							//HES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrHESAbsTotal++;
							}
						}
						
						//PDR - R
						else if(DSTReportUtil.isResistant(d.getResistanceR())!=null && DSTReportUtil.isResistant(d.getResistanceR())) {
					
							//HE
							if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && !DSTReportUtil.isResistant(d.getResistanceS())) {
								
									pdrREAbsTotal++;
							}
							
							//HS
							else if (DSTReportUtil.isResistant(d.getResistanceE())!=null && !DSTReportUtil.isResistant(d.getResistanceE()) &&
									DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								
								pdrRSAbsTotal++;
							}
						
							//HES
							else if(DSTReportUtil.isResistant(d.getResistanceE())!=null && DSTReportUtil.isResistant(d.getResistanceE()) &&
								DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								
								pdrRESAbsTotal++;
							}
						}
						
						//PDR - ES
						else if(DSTReportUtil.isResistant(d.getResistanceH())!=null && DSTReportUtil.isResistant(d.getResistanceH()) && DSTReportUtil.isResistant(d.getResistanceS())!=null && DSTReportUtil.isResistant(d.getResistanceS())) {
								pdrESAbsTotal++;
						
						}
					}
					
				}
				
				for(Dst2 dst : dst2s) {
					
					System.out.println("OTHER ID: " + e.getPatient().getId());
					Boolean flq = DSTReportUtil.isFlqResistant(dst);
					System.out.println("flq:"+ flq);
					
					Boolean am = DSTReportUtil.isResistant(dst.getResistanceAm());
					System.out.println("am:"+ am);
					Boolean km = DSTReportUtil.isResistant(dst.getResistanceKm());
					System.out.println("km:"+ km);
					Boolean cm = DSTReportUtil.isResistant(dst.getResistanceCm());
					System.out.println("cm:"+ cm);
					Boolean pto = DSTReportUtil.isResistant(dst.getResistancePto());
					System.out.println("pto:"+ pto);
					Boolean lzd = DSTReportUtil.isResistant(dst.getResistanceLzd());
					System.out.println("lzd:"+ lzd);
					Boolean cfz = DSTReportUtil.isResistant(dst.getResistanceCfz());
					System.out.println("cfz:"+ cfz);
					Boolean bdq = DSTReportUtil.isResistant(dst.getResistanceBdq());
					System.out.println("bdq:"+ bdq);
					Boolean dlm = DSTReportUtil.isResistant(dst.getResistanceDlm());
					System.out.println("dlm:"+ dlm);
					Boolean pas = DSTReportUtil.isResistant(dst.getResistancePas());
					System.out.println("pas:"+ pas);
					Boolean ofx = DSTReportUtil.isResistant(dst.getResistanceOfx());
					System.out.println("ofx:"+ ofx);
					Boolean mfx = DSTReportUtil.isResistant(dst.getResistanceMox());
					System.out.println("mfx:"+ mfx);
					Boolean lfx = DSTReportUtil.isResistant(dst.getResistanceLfx());
					System.out.println("lfx:"+ lfx);
					
					
					dst2AbsTotal++;
					
					if(DSTReportUtil.isFullSensitive(dst)) {
						
						dst2SensitiveAbsTotal++;
					}
					
					else if(mdr) {
						
						if(flq!=null && flq && cm!=null && cm && km!=null && km && am!=null && am) {
							
							xdrHrFlqCmKmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && cm!=null && cm && am!=null && am) {
							
							xdrHrFlqCmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km && am!=null && am) {
							
							xdrHrFlqKmAmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km && cm!=null && cm) {
							
							xdrHrFlqKmCmAbsTotal++;
						}
						
						else if(flq!=null && flq && am!=null && am) {
							
							xdrHrFlqAmAbsTotal++;
						}
						
						else if(flq!=null && flq && cm!=null && cm) {
							
							xdrHrFlqCmAbsTotal++;
						}
						
						else if(flq!=null && flq && km!=null && km) {
							
							xdrHrFlqKmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm && km!=null && km && am!=null && am) {
							
							xdrHrMoxCmKmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm && am!=null && am) {
							
							xdrHrMoxCmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km && am!=null && am) {
							
							xdrHrMoxKmAmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km && cm!=null && cm) {
							
							xdrHrMoxKmCmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && km!=null && km) {
							
							xdrHrMoxKmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && cm!=null && cm) {
							
							xdrHrMoxCmAbsTotal++;
						}
						
						else if(mfx!=null && mfx && am!=null && am) {
							
							xdrHrMoxAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm && km!=null && km && am!=null && am) {
							
							xdrHrLfxCmKmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm && am!=null && am) {
							
							xdrHrLfxCmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km && am!=null && am) {
							
							xdrHrLfxKmAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km && cm!=null && cm) {
					
							xdrHrLfxKmCmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && km!=null && km) {
							
							xdrHrLfxKmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && cm!=null && cm) {
							
							xdrHrLfxCmAbsTotal++;
						}
						
						else if(lfx!=null && lfx && am!=null && am) {
							
							xdrHrLfxAmAbsTotal++;
						}
						
						else if(km!=null && km && cm!=null && cm && am!=null && am) {
							
							preXdrHrKmCmAmAbsTotal++;
						}
						
						else if(km!=null && km && cm!=null && cm) {
							
							preXdrHrKmCmAbsTotal++;
						}
						
						else if(am!=null && am && cm!=null && cm) {
							
							preXdrHrCmAmAbsTotal++;
						}
						
						else if(km!=null && km && am!=null && am) {
							
							preXdrHrKmAmAbsTotal++;
						}
						
						else if(km!=null && km && am!=null && am) {
							
							preXdrHrKmAmAbsTotal++;
						}
						
						else if(km!=null && km) {
							
							preXdrHrKmAbsTotal++;
						}
						
						else if(cm!=null && cm) {
							
							preXdrHrCmAbsTotal++;
						}
						
						else if(am!=null && am) {
							
							preXdrHrAmAbsTotal++;
						}
						
						else if(lfx!=null && lfx) {
							
							preXdrHrLfxAbsTotal++;
						}
						
						else if(mfx!=null && mfx) {
							
							preXdrHrMoxAbsTotal++;
						}
						
						else if(ofx!=null && ofx) {
							
							preXdrHrOfxAbsTotal++;
						}
					}
					
					if(bdq!=null && bdq) {
						
						resistToBDLAbsTotal++;
					}
					
					if(dlm!=null && dlm) {
						
						resistToDLMAbsTotal++;
					}
					
					if(cfz!=null && cfz) {
						
						resistToCLOAbsTotal++;
					}
					
					if(lzd!=null && lzd) {
						
						resistToLINEAbsTotal++;
					}
					
					if(pas!=null && pas) {
						
						resistToPASKAbsTotal++;
					}
					
				}
			}*/
			
			
		}
		
		
		//CULTURE
		System.out.println(cultureAbsNew + "," + cultureAbsPrev + "," + cultureAbsTotal);
		
		if(cultureAbsTotal!=0) {
			culturePercentNew = ((cultureAbsNew/cultureAbsTotal)*100);
			culturePercentPrev = ((cultureAbsPrev/cultureAbsTotal)*100);
			culturePercentTotal = (((cultureAbsNew+cultureAbsPrev)/cultureAbsTotal)*100);
			cultureAbsTotal = (cultureAbsNew+cultureAbsPrev);
		}
		
		else {
			culturePercentNew = 0.0;
			culturePercentPrev = 0.0;
			culturePercentTotal = 0.0;
		}
		
		context.addParameterValue("tbl_Culture_AbsNew", cultureAbsNew.intValue());
		context.addParameterValue("tbl_Culture_PercentNew", DSTReportUtil.round(culturePercentNew, 1));
		context.addParameterValue("tbl_Culture_AbsPrev", cultureAbsPrev.intValue());
		context.addParameterValue("tbl_Culture_PercentPrev", DSTReportUtil.round(culturePercentPrev,1));
		context.addParameterValue("tbl_Culture_AbsTotal", cultureAbsTotal.intValue());
		context.addParameterValue("tbl_Culture_PercentTotal", DSTReportUtil.round(culturePercentTotal,1));
		
		//DST1
		if(firstLineDSTAbsTotal!=0) {
			firstLineDSTPercentNew = ((firstLineDSTAbsNew/firstLineDSTAbsTotal)*100);
			firstLineDSTPercentPrev = ((firstLineDSTAbsPrev/firstLineDSTAbsTotal)*100);
			firstLineDSTPercentTotal = (((firstLineDSTAbsNew+firstLineDSTAbsPrev)/firstLineDSTAbsTotal)*100);
			firstLineDSTAbsTotal = (firstLineDSTAbsNew+firstLineDSTAbsPrev);
		}
		
		else {
			firstLineDSTPercentNew = 0.0;
			firstLineDSTPercentPrev = 0.0;
			firstLineDSTPercentTotal = 0.0;
		}
		
		context.addParameterValue("tbl_FirstLineDST_AbsNew", firstLineDSTAbsNew.intValue());
		context.addParameterValue("tbl_FirstLineDST_PercentNew", DSTReportUtil.round(firstLineDSTPercentNew,1));
		context.addParameterValue("tbl_FirstLineDST_AbsPrev", firstLineDSTAbsPrev.intValue());
		context.addParameterValue("tbl_FirstLineDST_PercentPrev", DSTReportUtil.round(firstLineDSTPercentPrev,1));
		context.addParameterValue("tbl_FirstLineDST_AbsTotal", firstLineDSTAbsTotal.intValue());
		context.addParameterValue("tbl_FirstLineDST_PercentTotal", DSTReportUtil.round(firstLineDSTPercentTotal,1));
		
		//Fully Sensitive
		if (fSensitiveAbsTotal != 0) {
			fSensitivePercentNew = ((fSensitiveAbsNew / fSensitiveAbsTotal) * 100);
			fSensitivePercentPrev = ((fSensitiveAbsPrev / fSensitiveAbsTotal) * 100);
			fSensitivePercentTotal = (((fSensitiveAbsNew + fSensitiveAbsPrev) / fSensitiveAbsTotal) * 100);
			fSensitiveAbsTotal = (fSensitiveAbsNew + fSensitiveAbsPrev);
		}

		else {
			fSensitivePercentNew = 0.0;
			fSensitivePercentPrev = 0.0;
			fSensitivePercentTotal = 0.0;
		}

		context.addParameterValue("tbl_FSensitive_AbsNew",fSensitiveAbsNew.intValue());
		context.addParameterValue("tbl_FSensitive_PercentNew",DSTReportUtil.round(fSensitivePercentNew,1));
		context.addParameterValue("tbl_FSensitive_AbsPrev",fSensitiveAbsPrev.intValue());
		context.addParameterValue("tbl_FSensitive_PercentPrev",DSTReportUtil.round(fSensitivePercentPrev,1));
		context.addParameterValue("tbl_FSensitive_AbsTotal",fSensitiveAbsTotal.intValue());
		context.addParameterValue("tbl_FSensitive_PercentTotal",DSTReportUtil.round(fSensitivePercentTotal,1));
		
		//AnyResistH
		if (anyResistHAbsTotal != 0) {
			anyResistHPercentNew = ((anyResistHAbsNew / anyResistHAbsTotal) * 100);
			anyResistHPercentPrev = ((anyResistHAbsPrev / anyResistHAbsTotal) * 100);
			anyResistHPercentTotal = (((anyResistHAbsNew + anyResistHAbsPrev) / anyResistHAbsTotal) * 100);
			anyResistHAbsTotal = (anyResistHAbsNew + anyResistHAbsPrev);
		}

		else {
			anyResistHPercentNew = 0.0;
			anyResistHPercentPrev = 0.0;
			anyResistHPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_AnyResistH_AbsNew",anyResistHAbsNew.intValue());
		context.addParameterValue("tbl_AnyResistH_PercentNew",DSTReportUtil.round(anyResistHPercentNew,1));
		context.addParameterValue("tbl_AnyResistH_AbsPrev",anyResistHAbsPrev.intValue());
		context.addParameterValue("tbl_AnyResistH_PercentPrev",DSTReportUtil.round(anyResistHPercentPrev,1));
		context.addParameterValue("tbl_AnyResistH_AbsTotal",anyResistHAbsTotal.intValue());
		context.addParameterValue("tbl_AnyResistH_PercentTotal",DSTReportUtil.round(anyResistHPercentTotal,1));
		
		//AnyResistR
		if (anyResistRAbsTotal != 0) {
			anyResistRPercentNew = ((anyResistRAbsNew / anyResistRAbsTotal) * 100);
			anyResistRPercentPrev = ((anyResistRAbsPrev / anyResistRAbsTotal) * 100);
			anyResistRPercentTotal = (((anyResistRAbsNew + anyResistRAbsPrev) / anyResistRAbsTotal) * 100);
			anyResistRAbsTotal = (anyResistRAbsNew + anyResistRAbsPrev);
		}

		else {
			anyResistRPercentNew = 0.0;
			anyResistRPercentPrev = 0.0;
			anyResistRPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_AnyResistR_AbsNew",anyResistRAbsNew.intValue());
		context.addParameterValue("tbl_AnyResistR_PercentNew",DSTReportUtil.round(anyResistRPercentNew,1));
		context.addParameterValue("tbl_AnyResistR_AbsPrev",anyResistRAbsPrev.intValue());
		context.addParameterValue("tbl_AnyResistR_PercentPrev",DSTReportUtil.round(anyResistRPercentPrev,1));
		context.addParameterValue("tbl_AnyResistR_AbsTotal",anyResistRAbsTotal.intValue());
		context.addParameterValue("tbl_AnyResistR_PercentTotal",DSTReportUtil.round(anyResistRPercentTotal,1));
		
		//AnyResistE
		if (anyResistEAbsTotal != 0) {
			anyResistEPercentNew = ((anyResistEAbsNew / anyResistEAbsTotal) * 100);
			anyResistEPercentPrev = ((anyResistEAbsPrev / anyResistEAbsTotal) * 100);
			anyResistEPercentTotal = (((anyResistEAbsNew + anyResistEAbsPrev) / anyResistEAbsTotal) * 100);
			anyResistEAbsTotal = (anyResistEAbsNew + anyResistEAbsPrev);
		}

		else {
			anyResistEPercentNew = 0.0;
			anyResistEPercentPrev = 0.0;
			anyResistEPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_AnyResistE_AbsNew",anyResistEAbsNew.intValue());
		context.addParameterValue("tbl_AnyResistE_PercentNew",DSTReportUtil.round(anyResistEPercentNew,1));
		context.addParameterValue("tbl_AnyResistE_AbsPrev",anyResistEAbsPrev.intValue());
		context.addParameterValue("tbl_AnyResistE_PercentPrev",DSTReportUtil.round(anyResistEPercentPrev,1));
		context.addParameterValue("tbl_AnyResistE_AbsTotal",anyResistEAbsTotal.intValue());
		context.addParameterValue("tbl_AnyResistE_PercentTotal",DSTReportUtil.round(anyResistEPercentTotal,1));
		
		
		//AnyResistS
		if (anyResistSAbsTotal != 0) {
			anyResistSPercentNew = ((anyResistSAbsNew / anyResistSAbsTotal) * 100);
			anyResistSPercentPrev = ((anyResistSAbsPrev / anyResistSAbsTotal) * 100);
			anyResistSPercentTotal = (((anyResistSAbsNew + anyResistSAbsPrev) / anyResistSAbsTotal) * 100);
			anyResistSAbsTotal = (anyResistSAbsNew + anyResistSAbsPrev);
		}

		else {
			anyResistSPercentNew = 0.0;
			anyResistSPercentPrev = 0.0;
			anyResistSPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_AnyResistS_AbsNew",anyResistSAbsNew.intValue());
		context.addParameterValue("tbl_AnyResistS_PercentNew",DSTReportUtil.round(anyResistSPercentNew,1));
		context.addParameterValue("tbl_AnyResistS_AbsPrev",anyResistSAbsPrev.intValue());
		context.addParameterValue("tbl_AnyResistS_PercentPrev",DSTReportUtil.round(anyResistSPercentPrev,1));
		context.addParameterValue("tbl_AnyResistS_AbsTotal",anyResistSAbsTotal.intValue());
		context.addParameterValue("tbl_AnyResistS_PercentTotal",DSTReportUtil.round(anyResistSPercentTotal,1));
		
		
		//MONO
		if (monoResistHAbsTotal != 0) {
			monoResistHPercentNew = ((monoResistHAbsNew / monoResistHAbsTotal) * 100);
			monoResistHPercentPrev = ((monoResistHAbsPrev / monoResistHAbsTotal) * 100);
			monoResistHPercentTotal = (((monoResistHAbsNew + monoResistHAbsPrev) / monoResistHAbsTotal) * 100);
			monoResistHAbsTotal = (monoResistHAbsNew + monoResistHAbsPrev);
		}

		else {
			monoResistHPercentNew = 0.0;
			monoResistHPercentPrev = 0.0;
			monoResistHPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MonoResistH_AbsNew",monoResistHAbsNew.intValue());
		context.addParameterValue("tbl_MonoResistH_PercentNew",DSTReportUtil.round(monoResistHPercentNew,1));
		context.addParameterValue("tbl_MonoResistH_AbsPrev",monoResistHAbsPrev.intValue());
		context.addParameterValue("tbl_MonoResistH_PercentPrev",DSTReportUtil.round(monoResistHPercentPrev,1));
		context.addParameterValue("tbl_MonoResistH_AbsTotal",monoResistHAbsTotal.intValue());
		context.addParameterValue("tbl_MonoResistH_PercentTotal",DSTReportUtil.round(monoResistHPercentTotal,1));
		
		//MonoResistR
		if (monoResistRAbsTotal != 0) {
			monoResistRPercentNew = ((monoResistRAbsNew / monoResistRAbsTotal) * 100);
			monoResistRPercentPrev = ((monoResistRAbsPrev / monoResistRAbsTotal) * 100);
			monoResistRPercentTotal = (((monoResistRAbsNew + monoResistRAbsPrev) / monoResistRAbsTotal) * 100);
			monoResistRAbsTotal = (monoResistRAbsNew + monoResistRAbsPrev);
		}

		else {
			monoResistRPercentNew = 0.0;
			monoResistRPercentPrev = 0.0;
			monoResistRPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MonoResistR_AbsNew",monoResistRAbsNew.intValue());
		context.addParameterValue("tbl_MonoResistR_PercentNew",DSTReportUtil.round(monoResistRPercentNew,1));
		context.addParameterValue("tbl_MonoResistR_AbsPrev",monoResistRAbsPrev.intValue());
		context.addParameterValue("tbl_MonoResistR_PercentPrev",DSTReportUtil.round(monoResistRPercentPrev,1));
		context.addParameterValue("tbl_MonoResistR_AbsTotal",monoResistRAbsTotal.intValue());
		context.addParameterValue("tbl_MonoResistR_PercentTotal",DSTReportUtil.round(monoResistRPercentTotal,1));
		
		//MonoResistE
		if (monoResistEAbsTotal != 0) {
			monoResistEPercentNew = ((monoResistEAbsNew / monoResistEAbsTotal) * 100);
			monoResistEPercentPrev = ((monoResistEAbsPrev / monoResistEAbsTotal) * 100);
			monoResistEPercentTotal = (((monoResistEAbsNew + monoResistEAbsPrev) / monoResistEAbsTotal) * 100);
			monoResistEAbsTotal = (monoResistEAbsNew + monoResistEAbsPrev);
		}

		else {
			monoResistEPercentNew = 0.0;
			monoResistEPercentPrev = 0.0;
			monoResistEPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MonoResistE_AbsNew",monoResistEAbsNew.intValue());
		context.addParameterValue("tbl_MonoResistE_PercentNew",DSTReportUtil.round(monoResistEPercentNew,1));
		context.addParameterValue("tbl_MonoResistE_AbsPrev",monoResistEAbsPrev.intValue());
		context.addParameterValue("tbl_MonoResistE_PercentPrev",DSTReportUtil.round(monoResistEPercentPrev,1));
		context.addParameterValue("tbl_MonoResistE_AbsTotal",monoResistEAbsTotal.intValue());
		context.addParameterValue("tbl_MonoResistE_PercentTotal",DSTReportUtil.round(monoResistEPercentTotal,1));
		
		
		//MonoResistS
		if (monoResistSAbsTotal != 0) {
			monoResistSPercentNew = ((monoResistSAbsNew / monoResistSAbsTotal) * 100);
			monoResistSPercentPrev = ((monoResistSAbsPrev / monoResistSAbsTotal) * 100);
			monoResistSPercentTotal = (((monoResistSAbsNew + monoResistSAbsPrev) / monoResistSAbsTotal) * 100);
			monoResistSAbsTotal = (monoResistSAbsNew + monoResistSAbsPrev);
		}

		else {
			monoResistSPercentNew = 0.0;
			monoResistSPercentPrev = 0.0;
			monoResistSPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MonoResistS_AbsNew",monoResistSAbsNew.intValue());
		context.addParameterValue("tbl_MonoResistS_PercentNew",DSTReportUtil.round(monoResistSPercentNew,1));
		context.addParameterValue("tbl_MonoResistS_AbsPrev",monoResistSAbsPrev.intValue());
		context.addParameterValue("tbl_MonoResistS_PercentPrev",DSTReportUtil.round(monoResistSPercentPrev,1));
		context.addParameterValue("tbl_MonoResistS_AbsTotal",monoResistSAbsTotal.intValue());
		context.addParameterValue("tbl_MonoResistS_PercentTotal",DSTReportUtil.round(monoResistSPercentTotal,1));
		
		
		//MDR HR
		if (mdrHRAbsTotal != 0) {
			mdrHRPercentNew = ((mdrHRAbsNew / mdrHRAbsTotal) * 100);
			mdrHRPercentPrev = ((mdrHRAbsPrev / mdrHRAbsTotal) * 100);
			mdrHRPercentTotal = (((mdrHRAbsNew + mdrHRAbsPrev) / mdrHRAbsTotal) * 100);
			mdrHRAbsTotal = (mdrHRAbsNew + mdrHRAbsPrev);
		}

		else {
			mdrHRPercentNew = 0.0;
			mdrHRPercentPrev = 0.0;
			mdrHRPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MdrHR_AbsNew",mdrHRAbsNew.intValue());
		context.addParameterValue("tbl_MdrHR_PercentNew",DSTReportUtil.round(mdrHRPercentNew,1));
		context.addParameterValue("tbl_MdrHR_AbsPrev",mdrHRAbsPrev.intValue());
		context.addParameterValue("tbl_MdrHR_PercentPrev",DSTReportUtil.round(mdrHRPercentPrev,1));
		context.addParameterValue("tbl_MdrHR_AbsTotal",mdrHRAbsTotal.intValue());
		context.addParameterValue("tbl_MdrHR_PercentTotal",DSTReportUtil.round(mdrHRPercentTotal,1));
		
		//MDR HRE
		if (mdrHREAbsTotal != 0) {
			mdrHREPercentNew = ((mdrHREAbsNew / mdrHREAbsTotal) * 100);
			mdrHREPercentPrev = ((mdrHREAbsPrev / mdrHREAbsTotal) * 100);
			mdrHREPercentTotal = (((mdrHREAbsNew + mdrHREAbsPrev) / mdrHREAbsTotal) * 100);
			mdrHREAbsTotal = (mdrHREAbsNew + mdrHREAbsPrev);
		}

		else {
			mdrHREPercentNew = 0.0;
			mdrHREPercentPrev = 0.0;
			mdrHREPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MdrHRE_AbsNew",mdrHREAbsNew.intValue());
		context.addParameterValue("tbl_MdrHRE_PercentNew",DSTReportUtil.round(mdrHREPercentNew,1));
		context.addParameterValue("tbl_MdrHRE_AbsPrev",mdrHREAbsPrev.intValue());
		context.addParameterValue("tbl_MdrHRE_PercentPrev",DSTReportUtil.round(mdrHREPercentPrev,1));
		context.addParameterValue("tbl_MdrHRE_AbsTotal",mdrHREAbsTotal.intValue());
		context.addParameterValue("tbl_MdrHRE_PercentTotal",DSTReportUtil.round(mdrHREPercentTotal,1));
		
		//MDR HRS
		if (mdrHRSAbsTotal != 0) {
			mdrHRSPercentNew = ((mdrHRSAbsNew / mdrHRSAbsTotal) * 100);
			mdrHRSPercentPrev = ((mdrHRSAbsPrev / mdrHRSAbsTotal) * 100);
			mdrHRSPercentTotal = (((mdrHRSAbsNew + mdrHRSAbsPrev) / mdrHRSAbsTotal) * 100);
			mdrHRSAbsTotal = (mdrHRSAbsNew + mdrHRSAbsPrev);
		}

		else {
			mdrHRSPercentNew = 0.0;
			mdrHRSPercentPrev = 0.0;
			mdrHRSPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MdrHRS_AbsNew", mdrHRSAbsNew.intValue());
		context.addParameterValue("tbl_MdrHRS_PercentNew",DSTReportUtil.round(mdrHRSPercentNew,1));
		context.addParameterValue("tbl_MdrHRS_AbsPrev",mdrHRSAbsPrev.intValue());
		context.addParameterValue("tbl_MdrHRS_PercentPrev",DSTReportUtil.round(mdrHRSPercentPrev,1));
		context.addParameterValue("tbl_MdrHRS_AbsTotal",mdrHRSAbsTotal.intValue());
		context.addParameterValue("tbl_MdrHRS_PercentTotal",DSTReportUtil.round(mdrHRSPercentTotal,1));
		
		//MDR HRES
		if (mdrHRESAbsTotal != 0) {
			mdrHRESPercentNew = ((mdrHRESAbsNew / mdrHRESAbsTotal) * 100);
			mdrHRESPercentPrev = ((mdrHRESAbsPrev / mdrHRESAbsTotal) * 100);
			mdrHRESPercentTotal = (((mdrHRESAbsNew + mdrHRESAbsPrev) / mdrHRESAbsTotal) * 100);
			mdrHRESAbsTotal = (mdrHRESAbsNew + mdrHRESAbsPrev);
		}

		else {
			mdrHRESPercentNew = 0.0;
			mdrHRESPercentPrev = 0.0;
			mdrHRESPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_MdrHRES_AbsNew",mdrHRESAbsNew.intValue());
		context.addParameterValue("tbl_MdrHRES_PercentNew",DSTReportUtil.round(mdrHRESPercentNew,1));
		context.addParameterValue("tbl_MdrHRES_AbsPrev",mdrHRESAbsPrev.intValue());
		context.addParameterValue("tbl_MdrHRES_PercentPrev",DSTReportUtil.round(mdrHRESPercentPrev,1));
		context.addParameterValue("tbl_MdrHRES_AbsTotal",mdrHRESAbsTotal.intValue());
		context.addParameterValue("tbl_MdrHRES_PercentTotal",DSTReportUtil.round(mdrHRESPercentTotal,1));
		
		
		//PDR HE
		if (pdrHEAbsTotal != 0) {
			pdrHEPercentNew = ((pdrHEAbsNew / pdrHEAbsTotal) * 100);
			pdrHEPercentPrev = ((pdrHEAbsPrev / pdrHEAbsTotal) * 100);
			pdrHEPercentTotal = (((pdrHEAbsNew + pdrHEAbsPrev) / pdrHEAbsTotal) * 100);
			pdrHEAbsTotal = (pdrHEAbsNew + pdrHEAbsPrev);
		}

		else {
			pdrHEPercentNew = 0.0;
			pdrHEPercentPrev = 0.0;
			pdrHEPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrHE_AbsNew",pdrHEAbsNew.intValue());
		context.addParameterValue("tbl_PdrHE_PercentNew",DSTReportUtil.round(pdrHEPercentNew,1));
		context.addParameterValue("tbl_PdrHE_AbsPrev",pdrHEAbsPrev.intValue());
		context.addParameterValue("tbl_PdrHE_PercentPrev",DSTReportUtil.round(pdrHEPercentPrev,1));
		context.addParameterValue("tbl_PdrHE_AbsTotal",pdrHEAbsTotal.intValue());
		context.addParameterValue("tbl_PdrHE_PercentTotal",DSTReportUtil.round(pdrHEPercentTotal,1));
		
		//PDR HS
		if (pdrHSAbsTotal != 0) {
			pdrHSPercentNew = ((pdrHSAbsNew / pdrHSAbsTotal) * 100);
			pdrHSPercentPrev = ((pdrHSAbsPrev / pdrHSAbsTotal) * 100);
			pdrHSPercentTotal = (((pdrHSAbsNew + pdrHSAbsPrev) / pdrHSAbsTotal) * 100);
			pdrHSAbsTotal = (pdrHSAbsNew + pdrHSAbsPrev);
		}

		else {
			pdrHSPercentNew = 0.0;
			pdrHSPercentPrev = 0.0;
			pdrHSPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrHS_AbsNew",pdrHSAbsNew.intValue());
		context.addParameterValue("tbl_PdrHS_PercentNew",DSTReportUtil.round(pdrHSPercentNew,1));
		context.addParameterValue("tbl_PdrHS_AbsPrev",pdrHSAbsPrev.intValue());
		context.addParameterValue("tbl_PdrHS_PercentPrev",DSTReportUtil.round(pdrHSPercentPrev,1));
		context.addParameterValue("tbl_PdrHS_AbsTotal",pdrHSAbsTotal.intValue());
		context.addParameterValue("tbl_PdrHS_PercentTotal",DSTReportUtil.round(pdrHSPercentTotal,1));
		
		//PDR HES
		if (pdrHESAbsTotal != 0) {
			pdrHESPercentNew = ((pdrHESAbsNew / pdrHESAbsTotal) * 100);
			pdrHESPercentPrev = ((pdrHESAbsPrev / pdrHESAbsTotal) * 100);
			pdrHESPercentTotal = (((pdrHESAbsNew + pdrHESAbsPrev) / pdrHESAbsTotal) * 100);
			pdrHESAbsTotal = (pdrHESAbsNew + pdrHESAbsPrev);
		}

		else {
			pdrHESPercentNew = 0.0;
			pdrHESPercentPrev = 0.0;
			pdrHESPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrHES_AbsNew",pdrHESAbsNew.intValue());
		context.addParameterValue("tbl_PdrHES_PercentNew",DSTReportUtil.round(pdrHESPercentNew,1));
		context.addParameterValue("tbl_PdrHES_AbsPrev",pdrHESAbsPrev.intValue());
		context.addParameterValue("tbl_PdrHES_PercentPrev",DSTReportUtil.round(pdrHESPercentPrev,1));
		context.addParameterValue("tbl_PdrHES_AbsTotal",pdrHESAbsTotal.intValue());
		context.addParameterValue("tbl_PdrHES_PercentTotal",DSTReportUtil.round(pdrHESPercentTotal,1));
		
		//PDR - RE
		if (pdrREAbsTotal != 0) {
			pdrREPercentNew = ((pdrREAbsNew / pdrREAbsTotal) * 100);
			pdrREPercentPrev = ((pdrREAbsPrev / pdrREAbsTotal) * 100);
			pdrREPercentTotal = (((pdrREAbsNew + pdrREAbsPrev) / pdrREAbsTotal) * 100);
			pdrREAbsTotal = (pdrREAbsNew + pdrREAbsPrev);
		}

		else {
			pdrREPercentNew = 0.0;
			pdrREPercentPrev = 0.0;
			pdrREPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrRE_AbsNew",pdrREAbsNew.intValue());
		context.addParameterValue("tbl_PdrRE_PercentNew",DSTReportUtil.round(pdrREPercentNew,1));
		context.addParameterValue("tbl_PdrRE_AbsPrev",pdrREAbsPrev.intValue());
		context.addParameterValue("tbl_PdrRE_PercentPrev",DSTReportUtil.round(pdrREPercentPrev,1));
		context.addParameterValue("tbl_PdrRE_AbsTotal",pdrREAbsTotal.intValue());
		context.addParameterValue("tbl_PdrRE_PercentTotal",DSTReportUtil.round(pdrREPercentTotal,1));
		
		//PDR RS
		if (pdrRSAbsTotal != 0) {
			pdrRSPercentNew = ((pdrRSAbsNew / pdrRSAbsTotal) * 100);
			pdrRSPercentPrev = ((pdrRSAbsPrev / pdrRSAbsTotal) * 100);
			pdrRSPercentTotal = (((pdrRSAbsNew + pdrRSAbsPrev) / pdrRSAbsTotal) * 100);
			pdrRSAbsTotal = (pdrRSAbsNew + pdrRSAbsPrev);
		}

		else {
			pdrRSPercentNew = 0.0;
			pdrRSPercentPrev = 0.0;
			pdrRSPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrRS_AbsNew",pdrRSAbsNew.intValue());
		context.addParameterValue("tbl_PdrRS_PercentNew",DSTReportUtil.round(pdrRSPercentNew,1));
		context.addParameterValue("tbl_PdrRS_AbsPrev",pdrRSAbsPrev.intValue());
		context.addParameterValue("tbl_PdrRS_PercentPrev",DSTReportUtil.round(pdrRSPercentPrev,1));
		context.addParameterValue("tbl_PdrRS_AbsTotal",pdrRSAbsTotal.intValue());
		context.addParameterValue("tbl_PdrRS_PercentTotal",DSTReportUtil.round(pdrRSPercentTotal,1));
		
		//PDR RES
		if (pdrRESAbsTotal != 0) {
			pdrRESPercentNew = ((pdrRESAbsNew / pdrRESAbsTotal) * 100);
			pdrRESPercentPrev = ((pdrRESAbsPrev / pdrRESAbsTotal) * 100);
			pdrRESPercentTotal = (((pdrRESAbsNew + pdrRESAbsPrev) / pdrRESAbsTotal) * 100);
			pdrRESAbsTotal = (pdrRESAbsNew + pdrRESAbsPrev);
		}

		else {
			pdrRESPercentNew = 0.0;
			pdrRESPercentPrev = 0.0;
			pdrRESPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrRES_AbsNew",pdrRESAbsNew.intValue());
		context.addParameterValue("tbl_PdrRES_PercentNew",DSTReportUtil.round(pdrRESPercentNew,1));
		context.addParameterValue("tbl_PdrRES_AbsPrev",pdrRESAbsPrev.intValue());
		context.addParameterValue("tbl_PdrRES_PercentPrev",DSTReportUtil.round(pdrRESPercentPrev,1));
		context.addParameterValue("tbl_PdrRES_AbsTotal",pdrRESAbsTotal.intValue());
		context.addParameterValue("tbl_PdrRES_PercentTotal",DSTReportUtil.round(pdrRESPercentTotal,1));
		
		//PDR ES
		if (pdrESAbsTotal != 0) {
			pdrESPercentNew = ((pdrESAbsNew / pdrESAbsTotal) * 100);
			pdrESPercentPrev = ((pdrESAbsPrev / pdrESAbsTotal) * 100);
			pdrESPercentTotal = (((pdrESAbsNew + pdrESAbsPrev) / pdrESAbsTotal) * 100);
			pdrESAbsTotal = (pdrESAbsNew + pdrESAbsPrev);
		}

		else {
			pdrESPercentNew = 0.0;
			pdrESPercentPrev = 0.0;
			pdrESPercentTotal = 0.0;
		}

		context.addParameterValue("tbl_PdrES_AbsNew",pdrESAbsNew.intValue());
		context.addParameterValue("tbl_PdrES_PercentNew",DSTReportUtil.round(pdrESPercentNew,1));
		context.addParameterValue("tbl_PdrES_AbsPrev",pdrESAbsPrev.intValue());
		context.addParameterValue("tbl_PdrES_PercentPrev",DSTReportUtil.round(pdrESPercentPrev,1));
		context.addParameterValue("tbl_PdrES_AbsTotal",pdrESAbsTotal.intValue());
		context.addParameterValue("tbl_PdrES_PercentTotal",DSTReportUtil.round(pdrESPercentTotal,1));
		
		//DST2
		if (dst2AbsTotal != 0) {
			dst2PercentNew = ((dst2AbsNew /dst2AbsTotal) * 100);
			dst2PercentPrev = ((dst2AbsPrev / dst2AbsTotal) * 100);
			dst2PercentTotal = (((dst2AbsNew + dst2AbsPrev) / dst2AbsTotal) * 100);
			dst2AbsTotal = (dst2AbsNew + dst2AbsPrev);
		}

		else {
			dst2PercentNew = 0.0;
			dst2PercentPrev = 0.0;
			dst2PercentTotal = 0.0;
		}

		context.addParameterValue("tbl_DST2_AbsNew",dst2AbsNew.intValue());
		context.addParameterValue("tbl_DST2_PercentNew",DSTReportUtil.round(dst2PercentNew,1));
		context.addParameterValue("tbl_DST2_AbsPrev",dst2AbsPrev.intValue());
		context.addParameterValue("tbl_DST2_PercentPrev",DSTReportUtil.round(dst2PercentPrev,1));
		context.addParameterValue("tbl_DST2_AbsTotal",dst2AbsTotal.intValue());
		context.addParameterValue("tbl_DST2_PercentTotal",DSTReportUtil.round(dst2PercentTotal,1));
		
		//DST2 - Sensitive
		if (dst2SensitiveAbsTotal != 0) {
			dst2SensitivePercentNew = ((dst2SensitiveAbsNew /dst2SensitiveAbsTotal) * 100);
			dst2SensitivePercentPrev = ((dst2SensitiveAbsPrev / dst2SensitiveAbsTotal) * 100);
			dst2SensitivePercentTotal = (((dst2SensitiveAbsNew + dst2SensitiveAbsPrev) / dst2SensitiveAbsTotal) * 100);
			dst2SensitiveAbsTotal = (dst2SensitiveAbsNew + dst2SensitiveAbsPrev);
		}

		else {
			dst2SensitivePercentNew = 0.0;
			dst2SensitivePercentPrev = 0.0;
			dst2SensitivePercentTotal = 0.0;
		}

		context.addParameterValue("tbl_DST2Sensitive_AbsNew",dst2SensitiveAbsNew.intValue());
		context.addParameterValue("tbl_DST2Sensitive_PercentNew",DSTReportUtil.round(dst2SensitivePercentNew,1));
		context.addParameterValue("tbl_DST2Sensitive_AbsPrev",dst2SensitiveAbsPrev.intValue());
		context.addParameterValue("tbl_DST2Sensitive_PercentPrev",DSTReportUtil.round(dst2SensitivePercentPrev,1));
		context.addParameterValue("tbl_DST2Sensitive_AbsTotal",dst2SensitiveAbsTotal.intValue());
		context.addParameterValue("tbl_DST2Sensitive_PercentTotal",DSTReportUtil.round(dst2SensitivePercentTotal,1));
		
		

		if (preXdrHrOfxAbsTotal != 0) {
			preXdrHrOfxPercentNew = ((preXdrHrOfxAbsNew / preXdrHrOfxAbsTotal) * 100);
			preXdrHrOfxPercentPrev = ((preXdrHrOfxAbsPrev / preXdrHrOfxAbsTotal) * 100);
			preXdrHrOfxPercentTotal = (((preXdrHrOfxAbsNew + preXdrHrOfxAbsPrev)
					/ preXdrHrOfxAbsTotal * 100));
			preXdrHrOfxAbsTotal = preXdrHrOfxAbsNew + preXdrHrOfxAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrOfx_AbsNew",preXdrHrOfxAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrOfx_PercentNew",DSTReportUtil.round(preXdrHrOfxPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrOfx_AbsPrev",preXdrHrOfxAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrOfx_PercentPrev",DSTReportUtil.round(preXdrHrOfxPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrOfx_AbsTotal",preXdrHrOfxAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrOfx_PercentTotal",DSTReportUtil.round(preXdrHrOfxPercentTotal,1));

		if (preXdrHrMoxAbsTotal != 0) {
			preXdrHrMoxPercentNew = ((preXdrHrMoxAbsNew / preXdrHrMoxAbsTotal) * 100);
			preXdrHrMoxPercentPrev = ((preXdrHrMoxAbsPrev / preXdrHrMoxAbsTotal) * 100);
			preXdrHrMoxPercentTotal = (((preXdrHrMoxAbsNew + preXdrHrMoxAbsPrev)
					/ preXdrHrMoxAbsTotal * 100));
			preXdrHrMoxAbsTotal = preXdrHrMoxAbsNew + preXdrHrMoxAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrMox_AbsNew",preXdrHrMoxAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrMox_PercentNew",DSTReportUtil.round(preXdrHrMoxPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrMox_AbsPrev",preXdrHrMoxAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrMox_PercentPrev",DSTReportUtil.round(preXdrHrMoxPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrMox_AbsTotal",preXdrHrMoxAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrMox_PercentTotal",DSTReportUtil.round(preXdrHrMoxPercentTotal,1));

		if (preXdrHrLfxAbsTotal != 0) {
			preXdrHrLfxPercentNew = ((preXdrHrLfxAbsNew / preXdrHrLfxAbsTotal) * 100);
			preXdrHrLfxPercentPrev = ((preXdrHrLfxAbsPrev / preXdrHrLfxAbsTotal) * 100);
			preXdrHrLfxPercentTotal = (((preXdrHrLfxAbsNew + preXdrHrLfxAbsPrev)
					/ preXdrHrLfxAbsTotal * 100));
			preXdrHrLfxAbsTotal = preXdrHrLfxAbsNew + preXdrHrLfxAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrLfx_AbsNew",preXdrHrLfxAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrLfx_PercentNew",DSTReportUtil.round(preXdrHrLfxPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrLfx_AbsPrev",preXdrHrLfxAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrLfx_PercentPrev",DSTReportUtil.round(preXdrHrLfxPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrLfx_AbsTotal",preXdrHrLfxAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrLfx_PercentTotal",DSTReportUtil.round(preXdrHrLfxPercentTotal,1));

		if (preXdrHrCmAbsTotal != 0) {
			preXdrHrCmPercentNew = ((preXdrHrCmAbsNew / preXdrHrCmAbsTotal) * 100);
			preXdrHrCmPercentPrev = ((preXdrHrCmAbsPrev / preXdrHrCmAbsTotal) * 100);
			preXdrHrCmPercentTotal = (((preXdrHrCmAbsNew + preXdrHrCmAbsPrev)
					/ preXdrHrCmAbsTotal * 100));
			preXdrHrCmAbsTotal = preXdrHrCmAbsNew + preXdrHrCmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrCm_AbsNew",preXdrHrCmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrCm_PercentNew",DSTReportUtil.round(preXdrHrCmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrCm_AbsPrev",preXdrHrCmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrCm_PercentPrev",DSTReportUtil.round(preXdrHrCmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrCm_AbsTotal",preXdrHrCmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrCm_PercentTotal",DSTReportUtil.round(preXdrHrCmPercentTotal,1));

		if (preXdrHrKmAbsTotal != 0) {
			preXdrHrKmPercentNew = ((preXdrHrKmAbsNew / preXdrHrKmAbsTotal) * 100);
			preXdrHrKmPercentPrev = ((preXdrHrKmAbsPrev / preXdrHrKmAbsTotal) * 100);
			preXdrHrKmPercentTotal = (((preXdrHrKmAbsNew + preXdrHrKmAbsPrev)
					/ preXdrHrKmAbsTotal * 100));
			preXdrHrKmAbsTotal = preXdrHrKmAbsNew + preXdrHrKmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrKm_AbsNew",preXdrHrKmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrKm_PercentNew",DSTReportUtil.round(preXdrHrKmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrKm_AbsPrev",preXdrHrKmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrKm_PercentPrev",DSTReportUtil.round(preXdrHrKmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrKm_AbsTotal",preXdrHrKmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrKm_PercentTotal",DSTReportUtil.round(preXdrHrKmPercentTotal,1));

		if (preXdrHrAmAbsTotal != 0) {
			preXdrHrAmPercentNew = ((preXdrHrAmAbsNew / preXdrHrAmAbsTotal) * 100);
			preXdrHrAmPercentPrev = ((preXdrHrAmAbsPrev / preXdrHrAmAbsTotal) * 100);
			preXdrHrAmPercentTotal = (((preXdrHrAmAbsNew + preXdrHrAmAbsPrev)
					/ preXdrHrAmAbsTotal * 100));
			preXdrHrAmAbsTotal = preXdrHrAmAbsNew + preXdrHrAmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrAm_AbsNew",preXdrHrAmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrAm_PercentNew",DSTReportUtil.round(preXdrHrAmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrAm_AbsPrev",preXdrHrAmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrAm_PercentPrev",DSTReportUtil.round(preXdrHrAmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrAm_AbsTotal",preXdrHrAmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrAm_PercentTotal",DSTReportUtil.round(preXdrHrAmPercentTotal,1));

		if (preXdrHrKmAmAbsTotal != 0) {
			preXdrHrKmAmPercentNew = ((preXdrHrKmAmAbsNew / preXdrHrKmAmAbsTotal) * 100);
			preXdrHrKmAmPercentPrev = ((preXdrHrKmAmAbsPrev / preXdrHrKmAmAbsTotal) * 100);
			preXdrHrKmAmPercentTotal = (((preXdrHrKmAmAbsNew + preXdrHrKmAmAbsPrev)
					/ preXdrHrKmAmAbsTotal * 100));
			preXdrHrKmAmAbsTotal = preXdrHrKmAmAbsNew + preXdrHrKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrKmAm_AbsNew",preXdrHrKmAmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrKmAm_PercentNew",DSTReportUtil.round(preXdrHrKmAmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrKmAm_AbsPrev",preXdrHrKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrKmAm_PercentPrev",DSTReportUtil.round(preXdrHrKmAmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrKmAm_AbsTotal",preXdrHrKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrKmAm_PercentTotal",DSTReportUtil.round(preXdrHrKmAmPercentTotal,1));

		if (preXdrHrCmAmAbsTotal != 0) {
			preXdrHrCmAmPercentNew = ((preXdrHrCmAmAbsNew / preXdrHrCmAmAbsTotal) * 100);
			preXdrHrCmAmPercentPrev = ((preXdrHrCmAmAbsPrev / preXdrHrCmAmAbsTotal) * 100);
			preXdrHrCmAmPercentTotal = (((preXdrHrCmAmAbsNew + preXdrHrCmAmAbsPrev)
					/ preXdrHrCmAmAbsTotal * 100));
			preXdrHrCmAmAbsTotal = preXdrHrCmAmAbsNew + preXdrHrCmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrCmAm_AbsNew",preXdrHrCmAmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrCmAm_PercentNew",DSTReportUtil.round(preXdrHrCmAmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrCmAm_AbsPrev",preXdrHrCmAmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrCmAm_PercentPrev",DSTReportUtil.round(preXdrHrCmAmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrCmAm_AbsTotal",preXdrHrCmAmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrCmAm_PercentTotal",DSTReportUtil.round(preXdrHrCmAmPercentTotal,1));

		if (preXdrHrKmCmAbsTotal != 0) {
			preXdrHrKmCmPercentNew = ((preXdrHrKmCmAbsNew / preXdrHrKmCmAbsTotal) * 100);
			preXdrHrKmCmPercentPrev = ((preXdrHrKmCmAbsPrev / preXdrHrKmCmAbsTotal) * 100);
			preXdrHrKmCmPercentTotal = (((preXdrHrKmCmAbsNew + preXdrHrKmCmAbsPrev)
					/ preXdrHrKmCmAbsTotal * 100));
			preXdrHrKmCmAbsTotal = preXdrHrKmCmAbsNew + preXdrHrKmCmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrKmCm_AbsNew",preXdrHrKmCmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrKmCm_PercentNew",DSTReportUtil.round(preXdrHrKmCmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrKmCm_AbsPrev",preXdrHrKmCmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrKmCm_PercentPrev",DSTReportUtil.round(preXdrHrKmCmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrKmCm_AbsTotal",preXdrHrKmCmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrKmCm_PercentTotal",DSTReportUtil.round(preXdrHrKmCmPercentTotal,1));

		if (preXdrHrKmCmAmAbsTotal != 0) {
			preXdrHrKmCmAmPercentNew = ((preXdrHrKmCmAmAbsNew / preXdrHrKmCmAmAbsTotal) * 100);
			preXdrHrKmCmAmPercentPrev = ((preXdrHrKmCmAmAbsPrev / preXdrHrKmCmAmAbsTotal) * 100);
			preXdrHrKmCmAmPercentTotal = (((preXdrHrKmCmAmAbsNew + preXdrHrKmCmAmAbsPrev)
					/ preXdrHrKmCmAmAbsTotal * 100));
			preXdrHrKmCmAmAbsTotal = preXdrHrKmCmAmAbsNew
					+ preXdrHrKmCmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_PreXdrHrKmCmAm_AbsNew",preXdrHrKmCmAmAbsNew.intValue());
		context.addParameterValue("tbl_PreXdrHrKmCmAm_PercentNew",DSTReportUtil.round(preXdrHrKmCmAmPercentNew,1));
		context.addParameterValue("tbl_PreXdrHrKmCmAm_AbsPrev",preXdrHrKmCmAmAbsPrev.intValue());
		context.addParameterValue("tbl_PreXdrHrKmCmAm_PercentPrev",DSTReportUtil.round(preXdrHrKmCmAmPercentPrev,1));
		context.addParameterValue("tbl_PreXdrHrKmCmAm_AbsTotal",preXdrHrKmCmAmAbsTotal.intValue());
		context.addParameterValue("tbl_PreXdrHrKmCmAm_PercentTotal",DSTReportUtil.round(preXdrHrKmCmAmPercentTotal,1));

		if (xdrHrFlqKmAbsTotal != 0) {
			xdrHrFlqKmPercentNew = ((xdrHrFlqKmAbsNew / xdrHrFlqKmAbsTotal) * 100);
			xdrHrFlqKmPercentPrev = ((xdrHrFlqKmAbsPrev / xdrHrFlqKmAbsTotal) * 100);
			xdrHrFlqKmPercentTotal = (((xdrHrFlqKmAbsNew + xdrHrFlqKmAbsPrev)
					/ xdrHrFlqKmAbsTotal * 100));
			xdrHrFlqKmAbsTotal = xdrHrFlqKmAbsNew + xdrHrFlqKmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqKm_AbsNew",xdrHrFlqKmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqKm_PercentNew",DSTReportUtil.round(xdrHrFlqKmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqKm_AbsPrev",xdrHrFlqKmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqKm_PercentPrev",DSTReportUtil.round(xdrHrFlqKmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqKm_AbsTotal",xdrHrFlqKmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqKm_PercentTotal",DSTReportUtil.round(xdrHrFlqKmPercentTotal,1));

		if (xdrHrFlqCmAbsTotal != 0) {
			xdrHrFlqCmPercentNew = ((xdrHrFlqCmAbsNew / xdrHrFlqCmAbsTotal) * 100);
			xdrHrFlqCmPercentPrev = ((xdrHrFlqCmAbsPrev / xdrHrFlqCmAbsTotal) * 100);
			xdrHrFlqCmPercentTotal = (((xdrHrFlqCmAbsNew + xdrHrFlqCmAbsPrev)
					/ xdrHrFlqCmAbsTotal * 100));
			xdrHrFlqCmAbsTotal = xdrHrFlqCmAbsNew + xdrHrFlqCmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqCm_AbsNew",xdrHrFlqCmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqCm_PercentNew",DSTReportUtil.round(xdrHrFlqCmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqCm_AbsPrev",xdrHrFlqCmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqCm_PercentPrev",DSTReportUtil.round(xdrHrFlqCmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqCm_AbsTotal",xdrHrFlqCmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqCm_PercentTotal",DSTReportUtil.round(xdrHrFlqCmPercentTotal,1));

		if (xdrHrFlqAmAbsTotal != 0) {
			xdrHrFlqAmPercentNew = ((xdrHrFlqAmAbsNew / xdrHrFlqAmAbsTotal) * 100);
			xdrHrFlqAmPercentPrev = ((xdrHrFlqAmAbsPrev / xdrHrFlqAmAbsTotal) * 100);
			xdrHrFlqAmPercentTotal = (((xdrHrFlqAmAbsNew + xdrHrFlqAmAbsPrev)
					/ xdrHrFlqAmAbsTotal * 100));
			xdrHrFlqAmAbsTotal = xdrHrFlqAmAbsNew + xdrHrFlqAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqAm_AbsNew",xdrHrFlqAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqAm_PercentNew",DSTReportUtil.round(xdrHrFlqAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqAm_AbsPrev",xdrHrFlqAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqAm_PercentPrev",DSTReportUtil.round(xdrHrFlqAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqAm_AbsTotal",xdrHrFlqAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqAm_PercentTotal",DSTReportUtil.round(xdrHrFlqAmPercentTotal,1));

		if (xdrHrFlqKmCmAbsTotal != 0) {
			xdrHrFlqKmCmPercentNew = ((xdrHrFlqKmCmAbsNew / xdrHrFlqKmCmAbsTotal) * 100);
			xdrHrFlqKmCmPercentPrev = ((xdrHrFlqKmCmAbsPrev / xdrHrFlqKmCmAbsTotal) * 100);
			xdrHrFlqKmCmPercentTotal = (((xdrHrFlqKmCmAbsNew + xdrHrFlqKmCmAbsPrev)
					/ xdrHrFlqKmCmAbsTotal * 100));
			xdrHrFlqKmCmAbsTotal = xdrHrFlqKmCmAbsNew + xdrHrFlqKmCmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqKmCm_AbsNew",xdrHrFlqKmCmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqKmCm_PercentNew",DSTReportUtil.round(xdrHrFlqKmCmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqKmCm_AbsPrev",xdrHrFlqKmCmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqKmCm_PercentPrev",DSTReportUtil.round(xdrHrFlqKmCmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqKmCm_AbsTotal",xdrHrFlqKmCmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqKmCm_PercentTotal",DSTReportUtil.round(xdrHrFlqKmCmPercentTotal,1));

		if (xdrHrFlqKmAmAbsTotal != 0) {
			xdrHrFlqKmAmPercentNew = ((xdrHrFlqKmAmAbsNew / xdrHrFlqKmAmAbsTotal) * 100);
			xdrHrFlqKmAmPercentPrev = ((xdrHrFlqKmAmAbsPrev / xdrHrFlqKmAmAbsTotal) * 100);
			xdrHrFlqKmAmPercentTotal = (((xdrHrFlqKmAmAbsNew + xdrHrFlqKmAmAbsPrev)
					/ xdrHrFlqKmAmAbsTotal * 100));
			xdrHrFlqKmAmAbsTotal = xdrHrFlqKmAmAbsNew + xdrHrFlqKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqKmAm_AbsNew",xdrHrFlqKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqKmAm_PercentNew",DSTReportUtil.round(xdrHrFlqKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqKmAm_AbsPrev",xdrHrFlqKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqKmAm_PercentPrev",DSTReportUtil.round(xdrHrFlqKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqKmAm_AbsTotal",xdrHrFlqKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqKmAm_PercentTotal",DSTReportUtil.round(xdrHrFlqKmAmPercentTotal,1));

		if (xdrHrFlqCmAmAbsTotal != 0) {
			xdrHrFlqCmAmPercentNew = ((xdrHrFlqCmAmAbsNew / xdrHrFlqCmAmAbsTotal) * 100);
			xdrHrFlqCmAmPercentPrev = ((xdrHrFlqCmAmAbsPrev / xdrHrFlqCmAmAbsTotal) * 100);
			xdrHrFlqCmAmPercentTotal = (((xdrHrFlqCmAmAbsNew + xdrHrFlqCmAmAbsPrev)
					/ xdrHrFlqCmAmAbsTotal * 100));
			xdrHrFlqCmAmAbsTotal = xdrHrFlqCmAmAbsNew + xdrHrFlqCmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqCmAm_AbsNew",xdrHrFlqCmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqCmAm_PercentNew",DSTReportUtil.round(xdrHrFlqCmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqCmAm_AbsPrev",xdrHrFlqCmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqCmAm_PercentPrev",DSTReportUtil.round(xdrHrFlqCmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqCmAm_AbsTotal",xdrHrFlqCmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqCmAm_PercentTotal",DSTReportUtil.round(xdrHrFlqCmAmPercentTotal,1));

		if (xdrHrFlqCmKmAmAbsTotal != 0) {
			xdrHrFlqCmKmAmPercentNew = ((xdrHrFlqCmKmAmAbsNew / xdrHrFlqCmKmAmAbsTotal) * 100);
			xdrHrFlqCmKmAmPercentPrev = ((xdrHrFlqCmKmAmAbsPrev / xdrHrFlqCmKmAmAbsTotal) * 100);
			xdrHrFlqCmKmAmPercentTotal = (((xdrHrFlqCmKmAmAbsNew + xdrHrFlqCmKmAmAbsPrev)
					/ xdrHrFlqCmKmAmAbsTotal * 100));
			xdrHrFlqCmKmAmAbsTotal = xdrHrFlqCmKmAmAbsNew
					+ xdrHrFlqCmKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrFlqCmKmAm_AbsNew",xdrHrFlqCmKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrFlqCmKmAm_PercentNew",DSTReportUtil.round(xdrHrFlqCmKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrFlqCmKmAm_AbsPrev",xdrHrFlqCmKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrFlqCmKmAm_PercentPrev",DSTReportUtil.round(xdrHrFlqCmKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrFlqCmKmAm_AbsTotal",xdrHrFlqCmKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrFlqCmKmAm_PercentTotal",DSTReportUtil.round(xdrHrFlqCmKmAmPercentTotal,1));

		if (xdrHrMoxKmAbsTotal != 0) {
			xdrHrMoxKmPercentNew = ((xdrHrMoxKmAbsNew / xdrHrMoxKmAbsTotal) * 100);
			xdrHrMoxKmPercentPrev = ((xdrHrMoxKmAbsPrev / xdrHrMoxKmAbsTotal) * 100);
			xdrHrMoxKmPercentTotal = (((xdrHrMoxKmAbsNew + xdrHrMoxKmAbsPrev)
					/ xdrHrMoxKmAbsTotal * 100));
			xdrHrMoxKmAbsTotal = xdrHrMoxKmAbsNew + xdrHrMoxKmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxKm_AbsNew",xdrHrMoxKmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxKm_PercentNew",DSTReportUtil.round(xdrHrMoxKmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxKm_AbsPrev",xdrHrMoxKmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxKm_PercentPrev",DSTReportUtil.round(xdrHrMoxKmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxKm_AbsTotal",xdrHrMoxKmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxKm_PercentTotal",DSTReportUtil.round(xdrHrMoxKmPercentTotal,1));

		if (xdrHrMoxCmAbsTotal != 0) {
			xdrHrMoxCmPercentNew = ((xdrHrMoxCmAbsNew / xdrHrMoxCmAbsTotal) * 100);
			xdrHrMoxCmPercentPrev = ((xdrHrMoxCmAbsPrev / xdrHrMoxCmAbsTotal) * 100);
			xdrHrMoxCmPercentTotal = (((xdrHrMoxCmAbsNew + xdrHrMoxCmAbsPrev)
					/ xdrHrMoxCmAbsTotal * 100));
			xdrHrMoxCmAbsTotal = xdrHrMoxCmAbsNew + xdrHrMoxCmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxCm_AbsNew",xdrHrMoxCmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxCm_PercentNew",DSTReportUtil.round(xdrHrMoxCmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxCm_AbsPrev",xdrHrMoxCmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxCm_PercentPrev",DSTReportUtil.round(xdrHrMoxCmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxCm_AbsTotal",xdrHrMoxCmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxCm_PercentTotal",DSTReportUtil.round(xdrHrMoxCmPercentTotal,1));

		if (xdrHrMoxAmAbsTotal != 0) {
			xdrHrMoxAmPercentNew = ((xdrHrMoxAmAbsNew / xdrHrMoxAmAbsTotal) * 100);
			xdrHrMoxAmPercentPrev = ((xdrHrMoxAmAbsPrev / xdrHrMoxAmAbsTotal) * 100);
			xdrHrMoxAmPercentTotal = (((xdrHrMoxAmAbsNew + xdrHrMoxAmAbsPrev)
					/ xdrHrMoxAmAbsTotal * 100));
			xdrHrMoxAmAbsTotal = xdrHrMoxAmAbsNew + xdrHrMoxAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxAm_AbsNew",xdrHrMoxAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxAm_PercentNew",DSTReportUtil.round(xdrHrMoxAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxAm_AbsPrev",xdrHrMoxAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxAm_PercentPrev",DSTReportUtil.round(xdrHrMoxAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxAm_AbsTotal",xdrHrMoxAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxAm_PercentTotal",DSTReportUtil.round(xdrHrMoxAmPercentTotal,1));

		if (xdrHrMoxKmCmAbsTotal != 0) {
			xdrHrMoxKmCmPercentNew = ((xdrHrMoxKmCmAbsNew / xdrHrMoxKmCmAbsTotal) * 100);
			xdrHrMoxKmCmPercentPrev = ((xdrHrMoxKmCmAbsPrev / xdrHrMoxKmCmAbsTotal) * 100);
			xdrHrMoxKmCmPercentTotal = (((xdrHrMoxKmCmAbsNew + xdrHrMoxKmCmAbsPrev)
					/ xdrHrMoxKmCmAbsTotal * 100));
			xdrHrMoxKmCmAbsTotal = xdrHrMoxKmCmAbsNew + xdrHrMoxKmCmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxKmCm_AbsNew",xdrHrMoxKmCmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxKmCm_PercentNew",DSTReportUtil.round(xdrHrMoxKmCmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxKmCm_AbsPrev",xdrHrMoxKmCmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxKmCm_PercentPrev",DSTReportUtil.round(xdrHrMoxKmCmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxKmCm_AbsTotal",xdrHrMoxKmCmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxKmCm_PercentTotal",DSTReportUtil.round(xdrHrMoxKmCmPercentTotal,1));

		if (xdrHrMoxKmAmAbsTotal != 0) {
			xdrHrMoxKmAmPercentNew = ((xdrHrMoxKmAmAbsNew / xdrHrMoxKmAmAbsTotal) * 100);
			xdrHrMoxKmAmPercentPrev = ((xdrHrMoxKmAmAbsPrev / xdrHrMoxKmAmAbsTotal) * 100);
			xdrHrMoxKmAmPercentTotal = (((xdrHrMoxKmAmAbsNew + xdrHrMoxKmAmAbsPrev)
					/ xdrHrMoxKmAmAbsTotal * 100));
			xdrHrMoxKmAmAbsTotal = xdrHrMoxKmAmAbsNew + xdrHrMoxKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxKmAm_AbsNew",xdrHrMoxKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxKmAm_PercentNew",DSTReportUtil.round(xdrHrMoxKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxKmAm_AbsPrev",xdrHrMoxKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxKmAm_PercentPrev",DSTReportUtil.round(xdrHrMoxKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxKmAm_AbsTotal",xdrHrMoxKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxKmAm_PercentTotal",DSTReportUtil.round(xdrHrMoxKmAmPercentTotal,1));

		if (xdrHrMoxCmAmAbsTotal != 0) {
			xdrHrMoxCmAmPercentNew = ((xdrHrMoxCmAmAbsNew / xdrHrMoxCmAmAbsTotal) * 100);
			xdrHrMoxCmAmPercentPrev = ((xdrHrMoxCmAmAbsPrev / xdrHrMoxCmAmAbsTotal) * 100);
			xdrHrMoxCmAmPercentTotal = (((xdrHrMoxCmAmAbsNew + xdrHrMoxCmAmAbsPrev)
					/ xdrHrMoxCmAmAbsTotal * 100));
			xdrHrMoxCmAmAbsTotal = xdrHrMoxCmAmAbsNew + xdrHrMoxCmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxCmAm_AbsNew",xdrHrMoxCmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxCmAm_PercentNew",DSTReportUtil.round(xdrHrMoxCmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxCmAm_AbsPrev",xdrHrMoxCmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxCmAm_PercentPrev",DSTReportUtil.round(xdrHrMoxCmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxCmAm_AbsTotal",xdrHrMoxCmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxCmAm_PercentTotal",DSTReportUtil.round(xdrHrMoxCmAmPercentTotal,1));

		if (xdrHrMoxCmKmAmAbsTotal != 0) {
			xdrHrMoxCmKmAmPercentNew = ((xdrHrMoxCmKmAmAbsNew / xdrHrMoxCmKmAmAbsTotal) * 100);
			xdrHrMoxCmKmAmPercentPrev = ((xdrHrMoxCmKmAmAbsPrev / xdrHrMoxCmKmAmAbsTotal) * 100);
			xdrHrMoxCmKmAmPercentTotal = (((xdrHrMoxCmKmAmAbsNew + xdrHrMoxCmKmAmAbsPrev)
					/ xdrHrMoxCmKmAmAbsTotal * 100));
			xdrHrMoxCmKmAmAbsTotal = xdrHrMoxCmKmAmAbsNew
					+ xdrHrMoxCmKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrMoxCmKmAm_AbsNew",xdrHrMoxCmKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrMoxCmKmAm_PercentNew",DSTReportUtil.round(xdrHrMoxCmKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrMoxCmKmAm_AbsPrev",xdrHrMoxCmKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrMoxCmKmAm_PercentPrev",DSTReportUtil.round(xdrHrMoxCmKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrMoxCmKmAm_AbsTotal",xdrHrMoxCmKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrMoxCmKmAm_PercentTotal",DSTReportUtil.round(xdrHrMoxCmKmAmPercentTotal,1));

		if (xdrHrLfxKmAbsTotal != 0) {
			xdrHrLfxKmPercentNew = ((xdrHrLfxKmAbsNew / xdrHrLfxKmAbsTotal) * 100);
			xdrHrLfxKmPercentPrev = ((xdrHrLfxKmAbsPrev / xdrHrLfxKmAbsTotal) * 100);
			xdrHrLfxKmPercentTotal = (((xdrHrLfxKmAbsNew + xdrHrLfxKmAbsPrev)
					/ xdrHrLfxKmAbsTotal * 100));
			xdrHrLfxKmAbsTotal = xdrHrLfxKmAbsNew + xdrHrLfxKmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxKm_AbsNew",xdrHrLfxKmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxKm_PercentNew",DSTReportUtil.round(xdrHrLfxKmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxKm_AbsPrev",xdrHrLfxKmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxKm_PercentPrev",DSTReportUtil.round(xdrHrLfxKmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxKm_AbsTotal",xdrHrLfxKmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxKm_PercentTotal",DSTReportUtil.round(xdrHrLfxKmPercentTotal,1));

		if (xdrHrLfxCmAbsTotal != 0) {
			xdrHrLfxCmPercentNew = ((xdrHrLfxCmAbsNew / xdrHrLfxCmAbsTotal) * 100);
			xdrHrLfxCmPercentPrev = ((xdrHrLfxCmAbsPrev / xdrHrLfxCmAbsTotal) * 100);
			xdrHrLfxCmPercentTotal = (((xdrHrLfxCmAbsNew + xdrHrLfxCmAbsPrev)
					/ xdrHrLfxCmAbsTotal * 100));
			xdrHrLfxCmAbsTotal = xdrHrLfxCmAbsNew + xdrHrLfxCmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxCm_AbsNew",xdrHrLfxCmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxCm_PercentNew",DSTReportUtil.round(xdrHrLfxCmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxCm_AbsPrev",xdrHrLfxCmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxCm_PercentPrev",DSTReportUtil.round(xdrHrLfxCmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxCm_AbsTotal",xdrHrLfxCmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxCm_PercentTotal",DSTReportUtil.round(xdrHrLfxCmPercentTotal,1));

		if (xdrHrLfxAmAbsTotal != 0) {
			xdrHrLfxAmPercentNew = ((xdrHrLfxAmAbsNew / xdrHrLfxAmAbsTotal) * 100);
			xdrHrLfxAmPercentPrev = ((xdrHrLfxAmAbsPrev / xdrHrLfxAmAbsTotal) * 100);
			xdrHrLfxAmPercentTotal = (((xdrHrLfxAmAbsNew + xdrHrLfxAmAbsPrev)
					/ xdrHrLfxAmAbsTotal * 100));
			xdrHrLfxAmAbsTotal = xdrHrLfxAmAbsNew + xdrHrLfxAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxAm_AbsNew",xdrHrLfxAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxAm_PercentNew",DSTReportUtil.round(xdrHrLfxAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxAm_AbsPrev",xdrHrLfxAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxAm_PercentPrev",DSTReportUtil.round(xdrHrLfxAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxAm_AbsTotal",xdrHrLfxAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxAm_PercentTotal",DSTReportUtil.round(xdrHrLfxAmPercentTotal,1));

		if (xdrHrLfxKmCmAbsTotal != 0) {
			xdrHrLfxKmCmPercentNew = ((xdrHrLfxKmCmAbsNew / xdrHrLfxKmCmAbsTotal) * 100);
			xdrHrLfxKmCmPercentPrev = ((xdrHrLfxKmCmAbsPrev / xdrHrLfxKmCmAbsTotal) * 100);
			xdrHrLfxKmCmPercentTotal = (((xdrHrLfxKmCmAbsNew + xdrHrLfxKmCmAbsPrev)
					/ xdrHrLfxKmCmAbsTotal * 100));
			xdrHrLfxKmCmAbsTotal = xdrHrLfxKmCmAbsNew + xdrHrLfxKmCmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxKmCm_AbsNew",xdrHrLfxKmCmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxKmCm_PercentNew",DSTReportUtil.round(xdrHrLfxKmCmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxKmCm_AbsPrev",xdrHrLfxKmCmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxKmCm_PercentPrev",DSTReportUtil.round(xdrHrLfxKmCmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxKmCm_AbsTotal",xdrHrLfxKmCmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxKmCm_PercentTotal",DSTReportUtil.round(xdrHrLfxKmCmPercentTotal,1));

		
		if (xdrHrLfxKmAmAbsTotal != 0) {
			xdrHrLfxKmAmPercentNew = ((xdrHrLfxKmAmAbsNew / xdrHrLfxKmAmAbsTotal) * 100);
			xdrHrLfxKmAmPercentPrev = ((xdrHrLfxKmAmAbsPrev / xdrHrLfxKmAmAbsTotal) * 100);
			xdrHrLfxKmAmPercentTotal = (((xdrHrLfxKmAmAbsNew + xdrHrLfxKmAmAbsPrev)
					/ xdrHrLfxKmAmAbsTotal * 100));
			xdrHrLfxKmAmAbsTotal = xdrHrLfxKmAmAbsNew + xdrHrLfxKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxKmAm_AbsNew",xdrHrLfxKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxKmAm_PercentNew",DSTReportUtil.round(xdrHrLfxKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxKmAm_AbsPrev",xdrHrLfxKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxKmAm_PercentPrev",DSTReportUtil.round(xdrHrLfxKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxKmAm_AbsTotal",xdrHrLfxKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxKmAm_PercentTotal",DSTReportUtil.round(xdrHrLfxKmAmPercentTotal,1));

		if (xdrHrLfxCmAmAbsTotal != 0) {
			xdrHrLfxCmAmPercentNew = ((xdrHrLfxCmAmAbsNew / xdrHrLfxCmAmAbsTotal) * 100);
			xdrHrLfxCmAmPercentPrev = ((xdrHrLfxCmAmAbsPrev / xdrHrLfxCmAmAbsTotal) * 100);
			xdrHrLfxCmAmPercentTotal = (((xdrHrLfxCmAmAbsNew + xdrHrLfxCmAmAbsPrev)
					/ xdrHrLfxCmAmAbsTotal * 100));
			xdrHrLfxCmAmAbsTotal = xdrHrLfxCmAmAbsNew + xdrHrLfxCmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxCmAm_AbsNew",xdrHrLfxCmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmAm_PercentNew",DSTReportUtil.round(xdrHrLfxCmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxCmAm_AbsPrev",xdrHrLfxCmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmAm_PercentPrev",DSTReportUtil.round(xdrHrLfxCmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxCmAm_AbsTotal",xdrHrLfxCmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmAm_PercentTotal",DSTReportUtil.round(xdrHrLfxCmAmPercentTotal,1));

		if (xdrHrLfxCmKmAmAbsTotal != 0) {
			xdrHrLfxCmKmAmPercentNew = ((xdrHrLfxCmKmAmAbsNew / xdrHrLfxCmKmAmAbsTotal) * 100);
			xdrHrLfxCmKmAmPercentPrev = ((xdrHrLfxCmKmAmAbsPrev / xdrHrLfxCmKmAmAbsTotal) * 100);
			xdrHrLfxCmKmAmPercentTotal = (((xdrHrLfxCmKmAmAbsNew + xdrHrLfxCmKmAmAbsPrev)
					/ xdrHrLfxCmKmAmAbsTotal * 100));
			xdrHrLfxCmKmAmAbsTotal = xdrHrLfxCmKmAmAbsNew
					+ xdrHrLfxCmKmAmAbsPrev;
		}	
		
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_AbsNew",xdrHrLfxCmKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_PercentNew",DSTReportUtil.round(xdrHrLfxCmKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_AbsPrev",xdrHrLfxCmKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_PercentPrev",DSTReportUtil.round(xdrHrLfxCmKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_AbsTotal",xdrHrLfxCmKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_PercentTotal",DSTReportUtil.round(xdrHrLfxCmKmAmPercentTotal,1));
		
		if (xdrHrLfxCmKmAmAbsTotal != 0) {
			xdrHrLfxCmKmAmPercentNew = ((xdrHrLfxCmKmAmAbsNew / xdrHrLfxCmKmAmAbsTotal) * 100);
			xdrHrLfxCmKmAmPercentPrev = ((xdrHrLfxCmKmAmAbsPrev / xdrHrLfxCmKmAmAbsTotal) * 100);
			xdrHrLfxCmKmAmPercentTotal = (((xdrHrLfxCmKmAmAbsNew + xdrHrLfxCmKmAmAbsPrev)
					/ xdrHrLfxCmKmAmAbsTotal * 100));
			xdrHrLfxCmKmAmAbsTotal = xdrHrLfxCmKmAmAbsNew
					+ xdrHrLfxCmKmAmAbsPrev;
		}
		
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_AbsNew",xdrHrLfxCmKmAmAbsNew.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_PercentNew",DSTReportUtil.round(xdrHrLfxCmKmAmPercentNew,1));
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_AbsPrev",xdrHrLfxCmKmAmAbsPrev.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_PercentPrev",DSTReportUtil.round(xdrHrLfxCmKmAmPercentPrev,1));
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_AbsTotal",xdrHrLfxCmKmAmAbsTotal.intValue());
		context.addParameterValue("tbl_XdrHrLfxCmKmAm_PercentTotal",DSTReportUtil.round(xdrHrLfxCmKmAmPercentTotal,1));

		if (resistToBDLAbsTotal != 0) {
			resistToBDLPercentNew = ((resistToBDLAbsNew / resistToBDLAbsTotal) * 100);
			resistToBDLPercentPrev = ((resistToBDLAbsPrev / resistToBDLAbsTotal) * 100);
			resistToBDLPercentTotal = (((resistToBDLAbsNew + resistToBDLAbsPrev)
					/ resistToBDLAbsTotal * 100));
			resistToBDLAbsTotal = resistToBDLAbsNew + resistToBDLAbsPrev;
		}
		
		context.addParameterValue("tbl_ResistToBDL_AbsNew",resistToBDLAbsNew.intValue());
		context.addParameterValue("tbl_ResistToBDL_PercentNew",DSTReportUtil.round(resistToBDLPercentNew,1));
		context.addParameterValue("tbl_ResistToBDL_AbsPrev",resistToBDLAbsPrev.intValue());
		context.addParameterValue("tbl_ResistToBDL_PercentPrev",DSTReportUtil.round(resistToBDLPercentPrev,1));
		context.addParameterValue("tbl_ResistToBDL_AbsTotal",resistToBDLAbsTotal.intValue());
		context.addParameterValue("tbl_ResistToBDL_PercentTotal",DSTReportUtil.round(resistToBDLPercentTotal,1));

		if (resistToDLMAbsTotal != 0) {
			resistToDLMPercentNew = ((resistToDLMAbsNew / resistToDLMAbsTotal) * 100);
			resistToDLMPercentPrev = ((resistToDLMAbsPrev / resistToDLMAbsTotal) * 100);
			resistToDLMPercentTotal = (((resistToDLMAbsNew + resistToDLMAbsPrev)
					/ resistToDLMAbsTotal * 100));
			resistToDLMAbsTotal = resistToDLMAbsNew + resistToDLMAbsPrev;
		}
		
		context.addParameterValue("tbl_ResistToDLM_AbsNew",resistToDLMAbsNew.intValue());
		context.addParameterValue("tbl_ResistToDLM_PercentNew",DSTReportUtil.round(resistToDLMPercentNew,1));
		context.addParameterValue("tbl_ResistToDLM_AbsPrev",resistToDLMAbsPrev.intValue());
		context.addParameterValue("tbl_ResistToDLM_PercentPrev",DSTReportUtil.round(resistToDLMPercentPrev,1));
		context.addParameterValue("tbl_ResistToDLM_AbsTotal",resistToDLMAbsTotal.intValue());
		context.addParameterValue("tbl_ResistToDLM_PercentTotal",DSTReportUtil.round(resistToDLMPercentTotal,1));

		if (resistToCLOAbsTotal != 0) {
			resistToCLOPercentNew = ((resistToCLOAbsNew / resistToCLOAbsTotal) * 100);
			resistToCLOPercentPrev = ((resistToCLOAbsPrev / resistToCLOAbsTotal) * 100);
			resistToCLOPercentTotal = (((resistToCLOAbsNew + resistToCLOAbsPrev)
					/ resistToCLOAbsTotal * 100));
			resistToCLOAbsTotal = resistToCLOAbsNew + resistToCLOAbsPrev;
		}
		
		context.addParameterValue("tbl_ResistToCLO_AbsNew",resistToCLOAbsNew.intValue());
		context.addParameterValue("tbl_ResistToCLO_PercentNew",DSTReportUtil.round(resistToCLOPercentNew,1));
		context.addParameterValue("tbl_ResistToCLO_AbsPrev",resistToCLOAbsPrev.intValue());
		context.addParameterValue("tbl_ResistToCLO_PercentPrev",DSTReportUtil.round(resistToCLOPercentPrev,1));
		context.addParameterValue("tbl_ResistToCLO_AbsTotal",resistToCLOAbsTotal.intValue());
		context.addParameterValue("tbl_ResistToCLO_PercentTotal",DSTReportUtil.round(resistToCLOPercentTotal,1));

		if (resistToLINEAbsTotal != 0) {
			resistToLINEPercentNew = ((resistToLINEAbsNew / resistToLINEAbsTotal) * 100);
			resistToLINEPercentPrev = ((resistToLINEAbsPrev / resistToLINEAbsTotal) * 100);
			resistToLINEPercentTotal = (((resistToLINEAbsNew + resistToLINEAbsPrev)
					/ resistToLINEAbsTotal * 100));
			resistToLINEAbsTotal = resistToLINEAbsNew + resistToLINEAbsPrev;
		}
		
		context.addParameterValue("tbl_ResistToLINE_AbsNew",resistToLINEAbsNew.intValue());
		context.addParameterValue("tbl_ResistToLINE_PercentNew",DSTReportUtil.round(resistToLINEPercentNew,1));
		context.addParameterValue("tbl_ResistToLINE_AbsPrev",resistToLINEAbsPrev.intValue());
		context.addParameterValue("tbl_ResistToLINE_PercentPrev",DSTReportUtil.round(resistToLINEPercentPrev,1));
		context.addParameterValue("tbl_ResistToLINE_AbsTotal",resistToLINEAbsTotal.intValue());
		context.addParameterValue("tbl_ResistToLINE_PercentTotal",DSTReportUtil.round(resistToLINEPercentTotal,1));

		if (resistToPASKAbsTotal != 0) {
			resistToPASKPercentNew = ((resistToPASKAbsNew / resistToPASKAbsTotal) * 100);
			resistToPASKPercentPrev = ((resistToPASKAbsPrev / resistToPASKAbsTotal) * 100);
			resistToPASKPercentTotal = (((resistToPASKAbsNew + resistToPASKAbsPrev)
					/ resistToPASKAbsTotal * 100));
			resistToPASKAbsTotal = resistToPASKAbsNew + resistToPASKAbsPrev;
		}
		
		context.addParameterValue("tbl_ResistToPASK_AbsNew",resistToPASKAbsNew.intValue());
		context.addParameterValue("tbl_ResistToPASK_PercentNew",DSTReportUtil.round(resistToPASKPercentNew,1));
		context.addParameterValue("tbl_ResistToPASK_AbsPrev",resistToPASKAbsPrev.intValue());
		context.addParameterValue("tbl_ResistToPASK_PercentPrev",DSTReportUtil.round(resistToPASKPercentPrev,1));
		context.addParameterValue("tbl_ResistToPASK_AbsTotal",resistToPASKAbsTotal.intValue());
		context.addParameterValue("tbl_ResistToPASK_PercentTotal",DSTReportUtil.round(resistToPASKPercentTotal,1));
		
		
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
