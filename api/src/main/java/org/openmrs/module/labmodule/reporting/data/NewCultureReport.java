package org.openmrs.module.labmodule.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.reporting.DSTReportUtil;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.module.labmodule.specimen.LjCulture;
import org.openmrs.module.labmodule.specimen.MgitCulture;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;


public class NewCultureReport implements ReportSpecification {
	public String getName() {
		return Context.getMessageSourceService().getMessage("labmodule.labTbCultureReport");
	}

	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("labmodule.labTbCultureReport");
	}

	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		return l;
	}

	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/CultureNew" + 
				(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}



	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer)parameters.get("year");
		String quarter = (String) parameters.get("quarter");
		if(parameters.get("oblast") == null)
		{
			throw new IllegalArgumentException("Oblast is missing");
		}
		
		if(year == null)
		{
			//throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleasEnterAYear"));
			throw new IllegalArgumentException("Year is missing");			
		}
			
		//if (quarter == null) {
			// throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleaseEnterAQuarter"));
		//}
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
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	 @SuppressWarnings("unchecked")
	public ReportData evaluateReport(EvaluationContext context) {
		
			Integer district=null,oblast=null,facility=null; 
			Integer year = (Integer) context.getParameterValue("year");

			Map<String,Date>dateMap;
			
			dateMap= ReportUtil.getPeriodDates(year, "1", null);			
		 	Date q1EndDate = (Date)dateMap.get("endDate");
			Date q1StartDate = (Date)dateMap.get("startDate");
			
			dateMap = ReportUtil.getPeriodDates(year, "2", null);
			Date q2EndDate = (Date)dateMap.get("endDate");
			Date q2StartDate = (Date)dateMap.get("startDate");
			
			dateMap = ReportUtil.getPeriodDates(year, "3", null);			
			Date q3EndDate = (Date)dateMap.get("endDate");
			Date q3StartDate = (Date)dateMap.get("startDate");
			
			dateMap = ReportUtil.getPeriodDates(year, "4", null);
			Date q4EndDate = (Date)dateMap.get("endDate");
			Date q4StartDate = (Date)dateMap.get("startDate");
			
			ReportUtil.getPeriodDates(year, null, null);
			Date startDate = dateMap.get("startDate");
			Date endDate = dateMap.get("endDate");
			
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
			
			List<Encounter> q1Tests = Context.getService(TbService.class).getAllPatientTestedAtLocationDuring(q1StartDate, q1EndDate, locList);
			List<Encounter> q2Tests = Context.getService(TbService.class).getAllPatientTestedAtLocationDuring(q2StartDate, q2EndDate, locList);
			List<Encounter> q3Tests = Context.getService(TbService.class).getAllPatientTestedAtLocationDuring(q3StartDate, q3EndDate, locList);
			List<Encounter> q4Tests = Context.getService(TbService.class).getAllPatientTestedAtLocationDuring(q4StartDate, q4EndDate, locList);
			
			Double q1SmearPosMgitPos = 0.0;
			Double q2SmearPosMgitPos = 0.0;
			Double q3SmearPosMgitPos = 0.0;
			Double q4SmearPosMgitPos = 0.0;
			
			Double q1SmearPosMgitPosPercent = 0.0;
			Double q2SmearPosMgitPosPercent = 0.0;
			Double q3SmearPosMgitPosPercent = 0.0;
			Double q4SmearPosMgitPosPercent = 0.0;
			
			Double q1SmearPosMgitNeg = 0.0;
			Double q2SmearPosMgitNeg = 0.0;
			Double q3SmearPosMgitNeg = 0.0;
			Double q4SmearPosMgitNeg = 0.0;
			
			Double q1SmearPosMgitNegPercent = 0.0;
			Double q2SmearPosMgitNegPercent = 0.0;
			Double q3SmearPosMgitNegPercent = 0.0;
			Double q4SmearPosMgitNegPercent = 0.0;
			
			Double q1SmearNegMgitPos = 0.0;
			Double q2SmearNegMgitPos = 0.0;
			Double q3SmearNegMgitPos = 0.0;
			Double q4SmearNegMgitPos = 0.0;
			
			Double q1SmearNegMgitPosPercent = 0.0;
			Double q2SmearNegMgitPosPercent = 0.0;
			Double q3SmearNegMgitPosPercent = 0.0;
			Double q4SmearNegMgitPosPercent = 0.0;
			
			Double q1SmearNegMgitNeg = 0.0;
			Double q2SmearNegMgitNeg = 0.0;
			Double q3SmearNegMgitNeg = 0.0;
			Double q4SmearNegMgitNeg = 0.0;
			
			Double q1SmearNegMgitNegPercent = 0.0;
			Double q2SmearNegMgitNegPercent = 0.0;
			Double q3SmearNegMgitNegPercent = 0.0;
			Double q4SmearNegMgitNegPercent = 0.0;
			
			Double q1TotalMgGerm = 0.0;
			Double q2TotalMgGerm = 0.0;
			Double q3TotalMgGerm = 0.0;
			Double q4TotalMgGerm = 0.0;
			
			Double q1TotalMgGermPercent = 0.0;
			Double q2TotalMgGermPercent = 0.0;
			Double q3TotalMgGermPercent = 0.0;
			Double q4TotalMgGermPercent = 0.0;
			
			Double q1TotalMgit = 0.0;
			Double q2TotalMgit = 0.0;
			Double q3TotalMgit = 0.0;
			Double q4TotalMgit = 0.0;
			
			Double q1TotalMgitNoGrowth = 0.0;
			Double q2TotalMgitNoGrowth = 0.0;
			Double q3TotalMgitNoGrowth = 0.0;
			Double q4TotalMgitNoGrowth = 0.0;
			
			Double q1TotalLj = 0.0;
			Double q2TotalLj = 0.0;
			Double q3TotalLj = 0.0;
			Double q4TotalLj = 0.0;
			
			Double q1TotalLjGrown = 0.0;
			Double q2TotalLjGrown = 0.0;
			Double q3TotalLjGrown = 0.0;
			Double q4TotalLjGrown = 0.0;
			
			Double q1TotalMgitGrown = 0.0;
			Double q2TotalMgitGrown = 0.0;
			Double q3TotalMgitGrown = 0.0;
			Double q4TotalMgitGrown = 0.0;
			
			Double q1SmearPosLjPos = 0.0;
			Double q2SmearPosLjPos = 0.0;
			Double q3SmearPosLjPos = 0.0;
			Double q4SmearPosLjPos = 0.0;
			
			Double q1SmearPosLjPosPercent = 0.0;
			Double q2SmearPosLjPosPercent = 0.0;
			Double q3SmearPosLjPosPercent = 0.0;
			Double q4SmearPosLjPosPercent = 0.0;
			
			Double q1SmearPosLjNeg = 0.0;
			Double q2SmearPosLjNeg = 0.0;
			Double q3SmearPosLjNeg = 0.0;
			Double q4SmearPosLjNeg = 0.0;
			
			Double q1SmearPosLjNegPercent = 0.0;
			Double q2SmearPosLjNegPercent = 0.0;
			Double q3SmearPosLjNegPercent = 0.0;
			Double q4SmearPosLjNegPercent = 0.0;
			
			Double q1SmearNegLjPos = 0.0;
			Double q2SmearNegLjPos = 0.0;
			Double q3SmearNegLjPos = 0.0;
			Double q4SmearNegLjPos = 0.0;
			
			Double q1SmearNegLjPosPercent = 0.0;
			Double q2SmearNegLjPosPercent = 0.0;
			Double q3SmearNegLjPosPercent = 0.0;
			Double q4SmearNegLjPosPercent = 0.0;
			
			Double q1SmearNegLjNeg = 0.0;
			Double q2SmearNegLjNeg = 0.0;
			Double q3SmearNegLjNeg = 0.0;
			Double q4SmearNegLjNeg = 0.0;
			
			Double q1SmearNegLjNegPercent = 0.0;
			Double q2SmearNegLjNegPercent = 0.0;
			Double q3SmearNegLjNegPercent = 0.0;
			Double q4SmearNegLjNegPercent = 0.0;
			Double allSmearNegLjNegPercent = 0.0;
			
			Double q1TotalLjGerm = 0.0;
			Double q2TotalLjGerm = 0.0;
			Double q3TotalLjGerm = 0.0;
			Double q4TotalLjGerm = 0.0;
			
			Double q1TotalLjNotGrown = 0.0;
			Double q2TotalLjNotGrown = 0.0;
			Double q3TotalLjNotGrown = 0.0;
			Double q4TotalLjNotGrown = 0.0;
			
			Double q1TotalLjGermPercent = 0.0;
			Double q2TotalLjGermPercent = 0.0;
			Double q3TotalLjGermPercent = 0.0;
			Double q4TotalLjGermPercent = 0.0;
			
			Concept mgitSubculture = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
			Concept mgitMT = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_MT);
			Concept mgitNoGrowth = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_NO_GROWTH);
			Concept mgitHTM = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_HTM);			
			Concept mgitProGrowth= Context.getService(TbService.class).getConcept(TbConcepts.MGIT_PRO_GROWTH);
			
			Concept ljSubculture = Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT);
			Concept smearTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
			Concept smearNegative = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
			Concept mgitResult;
			Concept ljResult;
			
			/////////////////////////////////////////MGIT////
			
			//Quarter 1
			for(Encounter e : q1Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					for(MgitCulture mgit : c.getMgitCultures())
					{
						System.out.println("MgitCulture: " +  mgit.hashCode());
						//Total Mgits					
						q1TotalMgit ++;
						
						mgitResult  = mgit.getMgitResult();
						
						if (mgitResult == null) {
							continue;
						}
						
						//Germinations
						if (mgit.getMgitResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q1TotalMgGerm++;
						}
						
						//Grown Culture
						else if (mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId())
								|| mgit.getMgitResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q1TotalMgitGrown++;
						} 
						
						
						if(mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q1SmearPosMgitPos ++;
										break;
									}
									//Total Smear- / Mgit +
									else
									{
										q1SmearNegMgitPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / Mgit -
						else if(mgit.getMgitResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							q1TotalMgitNoGrowth ++;
							
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q1SmearPosMgitNeg ++;
										break;
									}
									//Total Smear- / Mgit -
									else
									{
										q1SmearNegMgitNeg++;
										break;
									}
								}							
							}
								
						}	
					}		
						
				}
			}
			
			//For Quarter 2
			for(Encounter e : q2Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					//Total MGIT
					for(MgitCulture mgit : c.getMgitCultures())
					{
						System.out.println("MgitCulture: " +  mgit.hashCode());
						//Total Cultures					
						q2TotalMgit ++;
						
						mgitResult  = mgit.getMgitResult();
						
						if (mgitResult == null) {
							continue;
						}
						
						//Germinations
						if (mgit.getMgitResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q2TotalMgGerm++;
						}
						
						//Grown Culture
						else if (mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId())
								|| mgit.getMgitResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q2TotalMgitGrown++;
						} 
						
						
						if(mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q2SmearPosMgitPos ++;
										break;
									}
									//Total Smear- / Mgit +
									else
									{
										q2SmearNegMgitPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / Mgit -
						else if(mgit.getMgitResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							
							q2TotalMgitNoGrowth ++;
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q2SmearPosMgitNeg ++;
										break;
										
									}
									//Total Smear- / Mgit -
									else
									{
										q2SmearNegMgitNeg++;
										break;
									}
								}							
							}
								
						}	
					}		
						
				}
			}
			//For Quarter 3
			for(Encounter e : q3Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					
					for(MgitCulture mgit : c.getMgitCultures())
					{
						System.out.println("MgitCulture: " +  mgit.hashCode());
						//Total MGIT
						q3TotalMgit ++;
						
						mgitResult  = mgit.getMgitResult();
						
						if (mgitResult== null) {
							continue;
						}
						
						//Germinations
						if (mgit.getMgitResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q3TotalMgGerm++;
						}
						//Grown Culture
						else if (mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId())
								|| mgit.getMgitResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q3TotalMgitGrown++;
						} 
						
						if(mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q3SmearPosMgitPos ++;
										break;
									}
									//Total Smear- / Mgit +
									else
									{
										q3SmearNegMgitPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / Mgit -
						else if(mgit.getMgitResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							q1TotalMgitNoGrowth ++;
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q3SmearPosMgitNeg ++;
										break;
									}
									//Total Smear- / Mgit -
									else
									{
										q3SmearNegMgitNeg++;
										break;
									}
								}							
							}
								
						}							
							
					}		
						
				}
			}
			
			//For Quarter 4
			for(Encounter e : q4Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					for(MgitCulture mgit : c.getMgitCultures())
					{
						System.out.println("MgitCulture: " +  mgit.hashCode());
						//Total MGIT
						q4TotalMgit ++;
						
						mgitResult  = mgit.getMgitResult();
						
						if (mgitResult == null) {
							continue;
						}
							
						//Germinations
						if (mgit.getMgitResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q4TotalMgGerm++;
						}
						
						//Grown Culture
						else if (mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId())
								|| mgit.getMgitResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q4TotalMgitGrown++;
						} 
						
						if(mgit.getMgitResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q4SmearPosMgitPos ++;
										break;
									}
									//Total Smear- / Mgit +
									else
									{
										q4SmearNegMgitPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / Mgit -
						else if(mgit.getMgitResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / Mgit -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q4SmearPosMgitNeg ++;
										break;
									}
									//Total Smear- / Mgit -
									else
									{
										q4SmearNegMgitNeg++;
										break;
									}
								}							
							}
								
						}							
					}		
						
				}
			}
			//////////////////////////////////////////////////////LJ////////////////////////////////////////////////////
			//Quarter 1
			for(Encounter e : q1Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					
					for(LjCulture lj : c.getLjCultures())
					{
						System.out.println("LjCulture: " +  lj.hashCode());
						//Total lj
						q1TotalLj ++;
						
						ljResult  = lj.getLjResult();
						
						if (ljResult == null) {
							continue ;
						}
						
						//Germinations
						if (lj.getLjResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q1TotalLjGerm++;
						}
						
						//Grown Culture
						else if (lj.getLjResult().getConceptId().equals(mgitMT.getConceptId())
								|| lj.getLjResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q1TotalLjGrown++;
						} 
						
						
						if(lj.getLjResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q1SmearPosLjPos ++;
										break;
									}
									//Total Smear- / lj +
									else
									{
										q1SmearNegLjPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / lj -
						else if(lj.getLjResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							
							q1TotalLjNotGrown ++;
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q1SmearPosLjNeg ++;
										break;	
									}
									//Total Smear- / lj -
									else
									{
										q1SmearNegLjNeg++;
										break;
									}
								}							
							}
								
						}
					}		
						
				}
			}
			
			//For Quarter 2
			for(Encounter e : q2Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					for(LjCulture lj : c.getLjCultures())
					{
						System.out.println("LjCulture: " +  lj.hashCode());
						//Total lj
						q2TotalLj ++;
						
						ljResult  = lj.getLjResult();
						
						if (ljResult == null) {
							continue;
						}
						//Germinations
						if (lj.getLjResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q2TotalLjGerm++;
						}
						//Grown Culture
						else if (lj.getLjResult().getConceptId().equals(mgitMT.getConceptId())
								|| lj.getLjResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q2TotalLjGrown++;
						} 
						
						
						if(lj.getLjResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q2SmearPosLjPos ++;
										break;
									}
									//Total Smear- / lj +
									else
									{
										q2SmearNegLjPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / lj -
						else if(lj.getLjResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							q2TotalLjNotGrown ++;
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q2SmearPosLjNeg ++;
										break;
									}
									//Total Smear- / lj -
									else
									{
										q2SmearNegLjNeg++;
										break;
									}
								}							
							}
								
						}	
					}		
						
				}
			}
			//For Quarter 3
			for(Encounter e : q3Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
					
					for(LjCulture lj : c.getLjCultures())
					{
						System.out.println("LjCulture: " +  lj.hashCode());
						//Total lj
						q3TotalLj ++;
						
						ljResult  = lj.getLjResult();
						
						if (ljResult == null) {
							continue;
						}
						
						//Germinations
						if (lj.getLjResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q3TotalLjGerm++;
						}
						//Grown Culture
						else if (lj.getLjResult().getConceptId().equals(mgitMT.getConceptId())
								|| lj.getLjResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q3TotalLjGrown++;
						} 
						
						
						if(lj.getLjResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q3SmearPosLjPos ++;
										break;
									}
									//Total Smear- / lj +
									else
									{
										q3SmearNegLjPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / lj -
						else if(lj.getLjResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							q3TotalLjNotGrown ++;
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q3SmearPosLjNeg ++;
										break;
									}
									//Total Smear- / lj -
									else
									{
										q3SmearNegLjNeg++;
										break;
									}
								}							
							}
								
						}
					}		
						
				}
			}
			
			//For Quarter 4
			for(Encounter e : q4Tests) {
				System.out.println("encounter: " +  e.getId());
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
				for(Culture c: cultures)
				{
					System.out.println("culture: " + c.getId());
				
					for(LjCulture lj : c.getLjCultures())
					{
						System.out.println("LjCulture: " +  lj.hashCode());
						//Total lj
						q4TotalLj ++;
						
						ljResult  = lj.getLjResult();
						
						if (ljResult == null) {
							continue;
						}
						
						//Germinations
						if (lj.getLjResult().getConceptId().equals(mgitProGrowth.getConceptId())) {
							q4TotalLjGerm++;
						}
						
						//Grown Culture
						else if (lj.getLjResult().getConceptId().equals(mgitMT.getConceptId())
								|| lj.getLjResult().getConceptId().equals(mgitHTM.getConceptId())) {
							q4TotalLjGrown++;
						} 
						
						if(lj.getLjResult().getConceptId().equals(mgitMT.getConceptId()))
						{
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj +
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q4SmearPosLjPos ++;
										break;
									}
									//Total Smear- / lj +
									else
									{
										q4SmearNegLjPos++;
										break;
									}
								}							
							}
						}
						
						//Total Smear+ / lj -
						else if(lj.getLjResult().getConceptId().equals(mgitNoGrowth.getConceptId()))
						{
							q4TotalLjNotGrown ++;
							Concept micResult;
							for(Microscopy mic: microscopies)
							{
								System.out.println("microscopy: " +  mic.hashCode());
								micResult = mic.getSampleResult();
								if(micResult!= null)
								{
									Integer resultId = mic.getSampleResult().getConceptId();
									//Total Smear+ / lj -
									if((!resultId .equals(smearNegative.getConceptId())) && (!resultId.equals(smearTestNotDone.getConceptId())))
									{
										q4SmearPosLjNeg ++;
										break;
									}
									//Total Smear- / lj -
									else
									{
										q4SmearNegLjNeg++;
										break;
									}
								}							
							}
								
						}	
					}		
						
				}
			}
			//Calculating percentages 
			/////////////////////Mgit Section ////////////////////
			
			//1.Smear+/MGIT+
			q1SmearPosMgitPosPercent = ((q1SmearPosMgitPos/q1TotalMgitGrown)*100);
			q2SmearPosMgitPosPercent = ((q2SmearPosMgitPos/q2TotalMgitGrown)*100);
			q3SmearPosMgitPosPercent = ((q3SmearPosMgitPos/q3TotalMgitGrown)*100);
			q4SmearPosMgitPosPercent = ((q4SmearPosMgitPos/q4TotalMgitGrown)*100);
			
			//2.Smear-/MGIT+
			q1SmearNegMgitPosPercent =  ((q1SmearNegMgitPos/q1TotalMgitGrown)*100);
			q2SmearNegMgitPosPercent =  ((q2SmearNegMgitPos/q2TotalMgitGrown)*100);
			q3SmearNegMgitPosPercent =  ((q3SmearNegMgitPos/q3TotalMgitGrown)*100);
			q4SmearNegMgitPosPercent =  ((q4SmearNegMgitPos/q4TotalMgitGrown)*100);
			
			//3.Smear-/MGIT-
			q1SmearNegMgitNegPercent =  ((q1SmearNegMgitNeg/q1TotalMgitNoGrowth)*100);
			q2SmearNegMgitNegPercent =  ((q2SmearNegMgitNeg/q2TotalMgitNoGrowth)*100);
			q3SmearNegMgitNegPercent =  ((q3SmearNegMgitNeg/q3TotalMgitNoGrowth)*100);
			q4SmearNegMgitNegPercent =  ((q4SmearNegMgitNeg/q4TotalMgitNoGrowth)*100);
			
			//4.Smear+/MGIT-
			q1SmearPosMgitNegPercent =  ((q1SmearPosMgitNeg/q1TotalMgitNoGrowth)*100);
			q2SmearPosMgitNegPercent =  ((q2SmearPosMgitNeg/q2TotalMgitNoGrowth)*100);
			q3SmearPosMgitNegPercent =  ((q3SmearPosMgitNeg/q3TotalMgitNoGrowth)*100);
			q4SmearPosMgitNegPercent =  ((q4SmearPosMgitNeg/q4TotalMgitNoGrowth)*100);
			
			//5.Germ %
			q1TotalMgGermPercent = ((q1TotalMgGerm/q1TotalMgitGrown)*100);
			q2TotalMgGermPercent = ((q2TotalMgGerm/q2TotalMgitGrown)*100);
			q3TotalMgGermPercent = ((q3TotalMgGerm/q3TotalMgitGrown)*100);
			q4TotalMgGermPercent = ((q4TotalMgGerm/q4TotalMgitGrown)*100);
			
			/////////////// LJ Section ///////////////////////
			//1.Smear+/Lj+
			q1SmearPosLjPosPercent = ((q1SmearPosLjPos/q1TotalLjGrown)*100);
			q2SmearPosLjPosPercent = ((q2SmearPosLjPos/q2TotalLjGrown)*100);
			q3SmearPosLjPosPercent = ((q3SmearPosLjPos/q3TotalLjGrown)*100);
			q4SmearPosLjPosPercent = ((q4SmearPosLjPos/q4TotalLjGrown)*100);
			
			//2.Smear-/Lj+
			q1SmearNegLjPosPercent =  ((q1SmearNegLjPos/q1TotalLjGrown)*100);
			q2SmearNegLjPosPercent =  ((q2SmearNegLjPos/q2TotalLjGrown)*100);
			q3SmearNegLjPosPercent =  ((q3SmearNegLjPos/q3TotalLjGrown)*100);
			q4SmearNegLjPosPercent =  ((q4SmearNegLjPos/q4TotalLjGrown)*100);
			
			//3.Smear-/Lj-
			q1SmearNegLjNegPercent =  ((q1SmearNegLjNeg/q1TotalLjNotGrown)*100);
			q2SmearNegLjNegPercent =  ((q2SmearNegLjNeg/q2TotalLjNotGrown)*100);
			q3SmearNegLjNegPercent =  ((q3SmearNegLjNeg/q3TotalLjNotGrown)*100);
			q4SmearNegLjNegPercent =  ((q4SmearNegLjNeg/q4TotalLjNotGrown)*100);
			
			//4.Smear+/Lj-
			q1SmearPosLjNegPercent =  ((q1SmearPosLjNeg/q1TotalLjNotGrown)*100);
			q2SmearPosLjNegPercent =  ((q2SmearPosLjNeg/q2TotalLjNotGrown)*100);
			q3SmearPosLjNegPercent =  ((q3SmearPosLjNeg/q3TotalLjNotGrown)*100);
			q4SmearPosLjNegPercent =  ((q4SmearPosLjNeg/q4TotalLjNotGrown)*100);
			
			//5.Germ %
			q1TotalLjGermPercent = ((q1TotalLjGerm/q1TotalLj)*100);
			q2TotalLjGermPercent = ((q2TotalLjGerm/q2TotalLj)*100);
			q3TotalLjGermPercent = ((q3TotalLjGerm/q3TotalLj)*100);
			q4TotalLjGermPercent = ((q4TotalLjGerm/q4TotalLj)*100);
			
		ReportData data;
		try {
			context.addParameterValue("q1.total",q1TotalMgit.intValue());
			context.addParameterValue("q2.total",q2TotalMgit.intValue());
			context.addParameterValue("q3.total",q3TotalMgit.intValue());
			context.addParameterValue("q4.total",q4TotalMgit.intValue());
			
			context.addParameterValue("q1.InTotal",q1TotalMgitGrown.intValue());
			context.addParameterValue("q2.InTotal",q2TotalMgitGrown.intValue());
			context.addParameterValue("q3.InTotal",q3TotalMgitGrown.intValue());
			context.addParameterValue("q4.InTotal",q4TotalMgitGrown.intValue());
			
			context.addParameterValue("q1.posMgit",q1SmearPosMgitPos.intValue());
			context.addParameterValue("q2.posMgit",q2SmearPosMgitPos.intValue());
			context.addParameterValue("q3.posMgit",q3SmearPosMgitPos.intValue());
			context.addParameterValue("q4.posMgit",q4SmearPosMgitPos.intValue());
			
			if(Double.isNaN(q1SmearPosMgitPosPercent))
				context.addParameterValue("q1.posMgitPer"," - ");
			else
				context.addParameterValue("q1.posMgitPer",DSTReportUtil.round(q1SmearPosMgitPosPercent,1));
			
			if(Double.isNaN(q2SmearPosMgitPosPercent))
				context.addParameterValue("q2.posMgitPer"," - ");
			else
				context.addParameterValue("q2.posMgitPer",DSTReportUtil.round(q2SmearPosMgitPosPercent,1));
			
			if(Double.isNaN(q3SmearPosMgitPosPercent))
				context.addParameterValue("q3.posMgitPer"," - ");
			else
				context.addParameterValue("q3.posMgitPer",DSTReportUtil.round(q3SmearPosMgitPosPercent,1));
			
			if(Double.isNaN(q4SmearPosMgitPosPercent))
				context.addParameterValue("q4.posMgitPer"," - ");
			else
				context.addParameterValue("q4.posMgitPer",DSTReportUtil.round(q4SmearPosMgitPosPercent,1));
				
			
			context.addParameterValue("q1.negMgit",q1SmearNegMgitPos.intValue());
			context.addParameterValue("q2.negMgit",q2SmearNegMgitPos.intValue());
			context.addParameterValue("q3.negMgit",q3SmearNegMgitPos.intValue());
			context.addParameterValue("q4.negMgit",q4SmearNegMgitPos.intValue());
			
			if(Double.isNaN(q1SmearNegMgitPosPercent))
				context.addParameterValue("q1.negMgitPer"," - ");
			else
				context.addParameterValue("q1.negMgitPer",DSTReportUtil.round(q1SmearNegMgitPosPercent,1));
			
			if(Double.isNaN(q2SmearNegMgitPosPercent))
				context.addParameterValue("q2.negMgitPer"," - ");
			else
				context.addParameterValue("q2.negMgitPer",DSTReportUtil.round(q2SmearNegMgitPosPercent,1));
			
			if(Double.isNaN(q3SmearNegMgitPosPercent))
				context.addParameterValue("q3.negMgitPer"," - ");
			else
				context.addParameterValue("q3.negMgitPer",DSTReportUtil.round(q3SmearNegMgitPosPercent,1));
			
			if(Double.isNaN(q4SmearNegMgitPosPercent))
				context.addParameterValue("q4.negMgitPer"," - ");
			else
				context.addParameterValue("q4.negMgitPer",DSTReportUtil.round(q4SmearNegMgitPosPercent,1));
			
			context.addParameterValue("q1.negMgitNeg",q1SmearNegMgitNeg.intValue());
			context.addParameterValue("q2.negMgitNeg",q2SmearNegMgitNeg.intValue());
			context.addParameterValue("q3.negMgitNeg",q3SmearNegMgitNeg.intValue());
			context.addParameterValue("q4.negMgitNeg",q4SmearNegMgitNeg.intValue());
			
			if(Double.isNaN(q1SmearNegMgitNegPercent))
				context.addParameterValue("q1.negMgitNegPer"," - ");
			else
				context.addParameterValue("q1.negMgitNegPer",DSTReportUtil.round(q1SmearNegMgitNegPercent,1));
			
			if(Double.isNaN(q2SmearNegMgitNegPercent))
				context.addParameterValue("q2.negMgitNegPer"," - ");
			else
				context.addParameterValue("q2.negMgitNegPer",DSTReportUtil.round(q2SmearNegMgitNegPercent,1));
			
			if(Double.isNaN(q3SmearNegMgitNegPercent))
				context.addParameterValue("q3.negMgitNegPer"," - ");
			else
				context.addParameterValue("q3.negMgitNegPer",DSTReportUtil.round(q3SmearNegMgitNegPercent,1));
			
			if(Double.isNaN(q4SmearNegMgitNegPercent))
				context.addParameterValue("q4.negMgitNegPer"," - ");
			else
				context.addParameterValue("q4.negMgitNegPer",DSTReportUtil.round(q4SmearNegMgitNegPercent,1));
			
			context.addParameterValue("q1.posMgitNeg",q1SmearPosMgitNeg.intValue());
			context.addParameterValue("q2.posMgitNeg",q2SmearPosMgitNeg.intValue());
			context.addParameterValue("q3.posMgitNeg",q3SmearPosMgitNeg.intValue());
			context.addParameterValue("q4.posMgitNeg",q4SmearPosMgitNeg.intValue());
	
			if(Double.isNaN(q1SmearPosMgitNegPercent))
				context.addParameterValue("q1.posMgitNegPer"," - ");
			else
				context.addParameterValue("q1.posMgitNegPer",DSTReportUtil.round(q1SmearPosMgitNegPercent,1));
			
			if(Double.isNaN(q2SmearPosMgitNegPercent))
				context.addParameterValue("q2.posMgitNegPer"," - ");
			else
				context.addParameterValue("q2.posMgitNegPer",DSTReportUtil.round(q2SmearPosMgitNegPercent,1));

			if(Double.isNaN(q3SmearPosMgitNegPercent))
				context.addParameterValue("q3.posMgitNegPer"," - ");
			else
				context.addParameterValue("q3.posMgitNegPer",DSTReportUtil.round(q3SmearPosMgitNegPercent,1));

			if(Double.isNaN(q4SmearPosMgitNegPercent))
				context.addParameterValue("q4.posMgitNegPer"," - ");
			else
				context.addParameterValue("q4.posMgitNegPer",DSTReportUtil.round(q4SmearPosMgitNegPercent,1));

			
			context.addParameterValue("q1.Germ",q1TotalMgGerm.intValue());
			context.addParameterValue("q2.Germ",q2TotalMgGerm.intValue());
			context.addParameterValue("q3.Germ",q3TotalMgGerm.intValue());
			context.addParameterValue("q4.Germ",q4TotalMgGerm.intValue());
			
			if(Double.isNaN(q1TotalMgGermPercent))
				context.addParameterValue("q1.GermPer"," - ");
			else
				context.addParameterValue("q1.GermPer",DSTReportUtil.round(q1TotalMgGermPercent,1));

			if(Double.isNaN(q2TotalMgGermPercent))
				context.addParameterValue("q2.GermPer"," - ");
			else
				context.addParameterValue("q2.GermPer",DSTReportUtil.round(q2TotalMgGermPercent,1));

			if(Double.isNaN(q3TotalMgGermPercent))
				context.addParameterValue("q3.GermPer"," - ");
			else
				context.addParameterValue("q3.GermPer",DSTReportUtil.round(q3TotalMgGermPercent,1));

			if(Double.isNaN(q4TotalMgGermPercent))
				context.addParameterValue("q4.GermPer"," - ");
			else
				context.addParameterValue("q4.GermPer",DSTReportUtil.round(q4TotalMgGermPercent,1));
			
			
			//LJ Report Section
			context.addParameterValue("q1.Lj",q1TotalLj.intValue());
			context.addParameterValue("q2.Lj",q2TotalLj.intValue());
			context.addParameterValue("q3.Lj",q3TotalLj.intValue());
			context.addParameterValue("q4.Lj",q4TotalLj.intValue());
			
			context.addParameterValue("q1.LjInTotal",q1TotalLjGrown.intValue());
			context.addParameterValue("q2.LjInTotal",q2TotalLjGrown.intValue());
			context.addParameterValue("q3.LjInTotal",q3TotalLjGrown.intValue());
			context.addParameterValue("q4.LjInTotal",q4TotalLjGrown.intValue());
			
			context.addParameterValue("q1.LjSmearPosLj",q1SmearPosLjPos.intValue());
			context.addParameterValue("q2.LjSmearPosLj",q2SmearPosLjPos.intValue());
			context.addParameterValue("q3.LjSmearPosLj",q3SmearPosLjPos.intValue());
			context.addParameterValue("q4.LjSmearPosLj",q4SmearPosLjPos.intValue());
			
			if(Double.isNaN(q1SmearPosLjPosPercent))
				context.addParameterValue("q1.LjSmearPosLjPer"," - ");
			else
				context.addParameterValue("q1.LjSmearPosLjPer",DSTReportUtil.round(q1SmearPosLjPosPercent,1));
			if(Double.isNaN(q2SmearPosLjPosPercent))
				context.addParameterValue("q2.LjSmearPosLjPer"," - ");
			else
				context.addParameterValue("q2.LjSmearPosLjPer",DSTReportUtil.round(q2SmearPosLjPosPercent,1));
			if(Double.isNaN(q3SmearPosLjPosPercent))
				context.addParameterValue("q3.LjSmearPosLjPer"," - ");
			else
				context.addParameterValue("q3.LjSmearPosLjPer",DSTReportUtil.round(q3SmearPosLjPosPercent,1));
			if(Double.isNaN(q4SmearPosLjPosPercent))
				context.addParameterValue("q4.LjSmearPosLjPer"," - ");
			else
				context.addParameterValue("q4.LjSmearPosLjPer",DSTReportUtil.round(q4SmearPosLjPosPercent,1));
			
			
			
			context.addParameterValue("q1.LjSmearNegLj",q1SmearNegLjPos.intValue());
			context.addParameterValue("q2.LjSmearNegLj",q2SmearNegLjPos.intValue());
			context.addParameterValue("q3.LjSmearNegLj",q3SmearNegLjPos.intValue());
			context.addParameterValue("q4.LjSmearNegLj",q4SmearNegLjPos.intValue());
		
			if(Double.isNaN(q1SmearNegLjPosPercent))
				context.addParameterValue("q1.LjSmearNegLjPer"," - ");
			else
				context.addParameterValue("q1.LjSmearNegLjPer",DSTReportUtil.round(q1SmearNegLjPosPercent,1));
	
			if(Double.isNaN(q2SmearNegLjPosPercent))
				context.addParameterValue("q2.LjSmearNegLjPer"," - ");
			else
				context.addParameterValue("q2.LjSmearNegLjPer",DSTReportUtil.round(q2SmearNegLjPosPercent,1));
	
			if(Double.isNaN(q3SmearPosLjPosPercent))
				context.addParameterValue("q3.LjSmearNegLjPer"," - ");
			else
				context.addParameterValue("q3.LjSmearNegLjPer",DSTReportUtil.round(q3SmearPosLjPosPercent,1));
			
			if(Double.isNaN(q4SmearPosLjPosPercent))
				context.addParameterValue("q4.LjSmearNegLjPer"," - ");
			else
				context.addParameterValue("q4.LjSmearNegLjPer",DSTReportUtil.round(q4SmearPosLjPosPercent,1));
			
			context.addParameterValue("q1.LjSmearPosLjNeg",q1SmearPosLjNeg.intValue());
			context.addParameterValue("q2.LjSmearPosLjNeg",q2SmearPosLjNeg.intValue());
			context.addParameterValue("q3.LjSmearPosLjNeg",q3SmearPosLjNeg.intValue());
			context.addParameterValue("q4.LjSmearPosLjNeg",q4SmearPosLjNeg.intValue());
			
			if(Double.isNaN(q1SmearPosLjNegPercent))
				context.addParameterValue("q1.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q1.LjSmearPosLjNegPer",DSTReportUtil.round(q1SmearPosLjNegPercent,1));
			
			if(Double.isNaN(q2SmearPosLjNegPercent))
				context.addParameterValue("q2.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q2.LjSmearPosLjNegPer",DSTReportUtil.round(q2SmearPosLjNegPercent,1));

			if(Double.isNaN(q3SmearPosLjNegPercent))
				context.addParameterValue("q3.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q3.LjSmearPosLjNegPer",DSTReportUtil.round(q3SmearPosLjNegPercent,1));

			if(Double.isNaN(q4SmearPosLjNegPercent))
				context.addParameterValue("q4.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q4.LjSmearPosLjNegPer",DSTReportUtil.round(q4SmearPosLjNegPercent,1));

			context.addParameterValue("q1.LjSmearNegLjNeg",q1SmearNegLjNeg.intValue());
			context.addParameterValue("q2.LjSmearNegLjNeg",q2SmearNegLjNeg.intValue());
			context.addParameterValue("q3.LjSmearNegLjNeg",q3SmearNegLjNeg.intValue());
			context.addParameterValue("q4.LjSmearNegLjNeg",q4SmearNegLjNeg.intValue());

			if(Double.isNaN(q1SmearPosLjNegPercent))
				context.addParameterValue("q1.LjSmearNegLjNegPer"," - ");
			else
				context.addParameterValue("q1.LjSmearNegLjNegPer",DSTReportUtil.round(q1SmearPosLjNegPercent,1));
			
			if(Double.isNaN(q2SmearPosLjNegPercent))
				context.addParameterValue("q2.LjSmearNegLjNegPer"," - ");
			else
				context.addParameterValue("q2.LjSmearNegLjNegPer",DSTReportUtil.round(q2SmearPosLjNegPercent,1));

			if(Double.isNaN(q3SmearPosLjNegPercent))
				context.addParameterValue("q3.LjSmearNegLjNegPer"," - ");
			else
				context.addParameterValue("q3.LjSmearNegLjNegPer",DSTReportUtil.round(q3SmearPosLjNegPercent,1));

			if(Double.isNaN(q4SmearPosLjNegPercent))
				context.addParameterValue("q4.LjSmearNegLjNegPer"," - ");
			else
				context.addParameterValue("q4.LjSmearNegLjNegPer",DSTReportUtil.round(q4SmearPosLjNegPercent,1));

			
			context.addParameterValue("q1.LjSmearPosLjNeg",q1SmearPosLjNeg.intValue());
			context.addParameterValue("q2.LjSmearPosLjNeg",q2SmearPosLjNeg.intValue());
			context.addParameterValue("q3.LjSmearPosLjNeg",q3SmearPosLjNeg.intValue());
			context.addParameterValue("q4.LjSmearPosLjNeg",q4SmearPosLjNeg.intValue());
	
			if(Double.isNaN(q1SmearPosLjNegPercent))
				context.addParameterValue("q1.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q1.LjSmearPosLjNegPer",DSTReportUtil.round(q1SmearPosLjNegPercent,1));
			
			if(Double.isNaN(q2SmearPosLjNegPercent))
				context.addParameterValue("q2.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q2.LjSmearPosLjNegPer",DSTReportUtil.round(q2SmearPosLjNegPercent,1));

			if(Double.isNaN(q3SmearPosLjNegPercent))
				context.addParameterValue("q3.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q3.LjSmearPosLjNegPer",DSTReportUtil.round(q3SmearPosLjNegPercent,1));

			if(Double.isNaN(q4SmearPosLjNegPercent))
				context.addParameterValue("q4.LjSmearPosLjNegPer"," - ");
			else
				context.addParameterValue("q4.LjSmearPosLjNegPer",DSTReportUtil.round(q4SmearPosLjNegPercent,1));

			context.addParameterValue("q1.LjTotalGerm",q1TotalLjGerm.intValue());
			context.addParameterValue("q2.LjTotalGerm",q2TotalLjGerm.intValue());
			context.addParameterValue("q3.LjTotalGerm",q3TotalLjGerm.intValue());
			context.addParameterValue("q4.LjTotalGerm",q4TotalLjGerm.intValue());
			
			if(Double.isNaN(q1TotalLjGermPercent))
				context.addParameterValue("q1.LjTotalGermPer"," - ");
			else
				context.addParameterValue("q1.LjTotalGermPer",DSTReportUtil.round(q1TotalLjGermPercent,1));

			if(Double.isNaN(q2TotalLjGermPercent))
				context.addParameterValue("q2.LjTotalGermPer"," - ");
			else
				context.addParameterValue("q2.LjTotalGermPer",DSTReportUtil.round(q2TotalLjGermPercent,1));

			if(Double.isNaN(q3TotalLjGermPercent))
				context.addParameterValue("q3.LjTotalGermPer"," - ");
			else
				context.addParameterValue("q3.LjTotalGermPer",DSTReportUtil.round(q3TotalLjGermPercent,1));

			if(Double.isNaN(q4TotalLjGermPercent))
				context.addParameterValue("q4.LjTotalGermPer"," - ");
			else
				context.addParameterValue("q4.LjTotalGermPer",DSTReportUtil.round(q4TotalLjGermPercent,1));
			
			
			CohortDefinition allLabResults = Cohorts.getAllLabResultDuring(startDate,endDate);
			CohortCrossTabDataSetDefinition d = new CohortCrossTabDataSetDefinition(); 
			
			d.addColumn("total", allLabResults, null); 
			report.addDataSetDefinition("all", d, null); 
			data = Context.getService(ReportDefinitionService.class).evaluate(
					report, context);
		} catch (EvaluationException e) {
			throw new MdrtbAPIException("Unable to evaluate Culture report",e);
		}
		return data;
	 }

	 private void traverseTests(List<Encounter> encounters, Date startDate, Date endDate)
	 {
		 for(Encounter e : encounters) {
				
				LabResultImpl lri = new LabResultImpl(e);
				
				List<Culture> cultures = lri.getCultures();
				List<Microscopy> microscopies = lri.getMicroscopies();
				
			} 
	 }
	 
	 private int getCohortSize(CohortDefinition cohortDef)
	 {
		 Cohort cohort;
		 int size=0;
		 try {
				cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDef, new EvaluationContext());
				size = cohort.getSize();
			} catch (EvaluationException e1) {
				e1.printStackTrace();
			}
		 return size;		 
	 }
}


