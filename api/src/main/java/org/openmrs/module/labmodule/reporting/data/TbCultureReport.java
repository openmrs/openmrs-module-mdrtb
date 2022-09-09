package org.openmrs.module.labmodule.reporting.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.reporting.DSTReportUtil;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;


public class TbCultureReport implements ReportSpecification {
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
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), String.class));
		return l;
	}

	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/TbCulture" + 
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
		if(year == null && quarter !=null)
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
		Date endDate = (Date)context.getParameterValue("endDate");
		Date startDate = (Date)context.getParameterValue("startDate");

	 	if(context.getParameterValue("district")!=null && context.getParameterValue("district")!="")
	 		district = (Integer) context.getParameterValue("district");
	 	if(context.getParameterValue("oblast")!=null && context.getParameterValue("oblast")!="")
		  	oblast = (Integer) context.getParameterValue("oblast");
	 	if(context.getParameterValue("facility")!=null && context.getParameterValue("facility")!="")
		  	facility =(Integer) context.getParameterValue("facility");
	 			
		List<Location> locList = Context.getService(TbService.class).getLocationList(oblast, district, facility);
		if(locList==null)
			locList=Context.getLocationService().getAllLocations();

		
		/////////////////////////////////////////COHORT-DEFINITIONS/////////////////////////////////////////////

		Concept microscopy= Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT);
		Concept microscopyResult= Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept culture = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT);
		Concept mgitCultureConstruct = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_RESULT_TEMPLATE);
		Concept mgitCultureResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		Concept inoculationDate= Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_INOCULATION_DATE);
		Concept mgitGrowthDate = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_MGIT_GROWTH);
		Concept typeOfCulture = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_TYPE);
		Concept mgit = Context.getService(TbService.class).getConcept(TbConcepts.MGIT);
		Concept lj = Context.getService(TbService.class).getConcept(TbConcepts.LJ);
		Concept ljSubculture = Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT_TEMPLATE);
		Concept ljCultureResult= Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT);
		Concept ljGrowthDate= Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_LJ_GROWTH);
		Concept mgitSubculture = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_RESULT_TEMPLATE);
		Concept rcSubculture = Context.getService(TbService.class).getConcept(TbConcepts.CONTAMINATED_TUBES_RESULT_TEMPLATE);
		Concept ljCultureType = Context.getService(TbService.class).getConcept(TbConcepts.LJ);
		Concept mtPositive = Context.getService(TbService.class).getConcept(TbConcepts.MT_POSITIVE);
		Concept mtNegative = Context.getService(TbService.class).getConcept(TbConcepts.MT_NEGATIVE);
		Concept mtNotValid= Context.getService(TbService.class).getConcept(TbConcepts.MT_NOT_VALID);
		Concept mgitNoGrowth = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_NO_GROWTH);
		Concept mgitMT= Context.getService(TbService.class).getConcept(TbConcepts.MGIT_MT);
		Concept mgitHTM= Context.getService(TbService.class).getConcept(TbConcepts.MGIT_HTM);
		Concept mgitProGrowth= Context.getService(TbService.class).getConcept(TbConcepts.MGIT_PRO_GROWTH);
		Concept dst1MGit = Context.getService(TbService.class).getConcept(TbConcepts.DST1_MGIT_CONSTRUCT);
		Concept dst2MGit = Context.getService(TbService.class).getConcept(TbConcepts.DST2_MGIT_CONSTRUCT);
		Concept dst1Lj = Context.getService(TbService.class).getConcept(TbConcepts.DST1_LJ_CONSTRUCT);
		Concept dst2Lj = Context.getService(TbService.class).getConcept(TbConcepts.DST2_LJ_CONSTRUCT);
		Concept investigationType=Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE);
		Concept newCase = Context.getService(TbService.class).getConcept(TbConcepts.NEW_CASE);
		Concept previousCases = Context.getService(TbService.class).getConcept(TbConcepts.REPEAT_CASE);

		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		Collection<ConceptAnswer> microscopyAnswers = Context.getService(TbService.class).getPossibleMicroscopyResults();
		for(ConceptAnswer answer: microscopyAnswers)
		{
			if(answer.getConcept().getConceptId()==1)
			{
				//do something here
			}
		}
		
		//List<Concept> mgitPositveResults = new ArrayList<Concept>(Arrays.asList(mgitHTM, mgitMT, mgitProGrowth));
		
		Concept mtid = Context.getService(TbService.class).getConcept(TbConcepts.MT_ID_TEST);
		
		//GET ALL DIAGNOSTICS
		CohortDefinition allDiagnostics = Cohorts.getSimpleCodedCohort(startDate, endDate, investigationType, newCase,previousCases);
				
		//ALL SMEAR			
		CohortDefinition allSmear = Cohorts.getAllByTestType(startDate, endDate, microscopy,true);
		
		//All Culture
		//CohortDefinition allCultures = Cohorts.getAllByTestType(null, null, culture,true);		
				
		//////////////////////////////////		MGIT TABLE		///////////////////////////////////////
		//MGIT Culture
		CohortDefinition allMgits = Cohorts.getAllByTestType(startDate, endDate, mgitSubculture,true);
		
		//MGIT POSITIVE
		CohortDefinition positiveMgits = Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mgitCultureResult, mgitMT);		
		
		//MGIT NEGATIVE
		CohortDefinition negativeMgits = Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mgitCultureResult, mgitNoGrowth);
		
		CohortDefinition progrowthMgits = Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mgitCultureResult, mgitProGrowth);
		
		//MGIT HTM
		CohortDefinition htmMgits = Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mgitCultureResult, mgitHTM);
		
		List<Concept> positiveMicroscopies=new ArrayList<Concept>();
		for(ConceptAnswer answer : microscopyAnswers)
		{
			if(answer.getAnswerConcept().equals(microscopyTestNotDone) &&  answer.getAnswerConcept().equals(microscopyNegative))
			{
				positiveMicroscopies.add(answer.getAnswerConcept());
			}
		}
		//////////////////////////////////LJ TABLE		///////////////////////////////////////
		//MGIT Culture
		CohortDefinition allLjs = Cohorts.getAllByTestType(startDate, endDate, ljSubculture,true);		
		
		//MGIT POSITIVE
		CohortDefinition positiveLjs = Cohorts.getCodedCohortByParentObs(startDate, endDate, ljSubculture, ljCultureResult, mgitMT);		
		
		//MGIT NEGATIVE
		CohortDefinition negativeLjs = Cohorts.getCodedCohortByParentObs(startDate, endDate, ljSubculture, ljCultureResult, microscopyNegative);
		
		CohortDefinition progrowthLjs = Cohorts.getCodedCohortByParentObs(startDate, endDate, ljSubculture, ljCultureResult, mgitProGrowth);
		
		//MGIT HTM
		//CohortDefinition htmLjs = Cohorts.getCodedCohortByParentObs(null, null, ljSubculture, ljCultureResult, mgitHTM);
				
		//get CohortDefs related to Microscopy test
		
		//TODO: GET KUB+ HERE
		//CohortDefinition noMicroscopy = Cohorts.getCodedCohortByParentObs(null, null, microscopy, microscopyResult, microscopyTestNotDone);;
		
		//RCs
		//CohortDefinition allRCs = Cohorts.getAllByTestType(null, null, rcSubculture,true);
				
		//ALL MT IDs
		//CohortDefinition allMTs= Cohorts.getAllByTestType(null, null, mtid,true);
		
		//MT +VE
		CohortDefinition allMtPositive = Cohorts.getCodedCohortByParentObs(startDate, endDate, microscopy, microscopyResult, positiveMicroscopies);
		
		//MT -VE
		CohortDefinition allMtNegative =Cohorts.getCodedCohortByParentObs(startDate, endDate, microscopy, microscopyResult, microscopyNegative);
		
		//MT Not valid ie NO Microsopy
		CohortDefinition allMtNotValid =Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mtid, mtNotValid);
		
		//Tubes:
		CohortDefinition  kubPositiveBacteria = ReportUtil.getCompositionCohort("AND", progrowthMgits, allMtPositive);
		
		//CohortDefinition noGrowth = Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mgitCultureResult, mgitNoGrowth);
		//CohortDefinition nonTBBacteria =  Cohorts.getCodedCohortByParentObs(startDate, endDate, mgitSubculture, mgitCultureResult, mgitHTM);
		
		
		//DST1-MGIT
		Concept dst1 =  Context.getService(TbService.class).getConcept(TbConcepts.DST1_CONSTRUCT);
		CohortDefinition allDST1Mgit= Cohorts.getAllByTestType(startDate, endDate, dst1MGit, true);
		
		//DST2-MGIT
		Concept dst2 =  Context.getService(TbService.class).getConcept(TbConcepts.DST2_CONSTRUCT);
		CohortDefinition allDST2Mgit= Cohorts.getAllByTestType(startDate, endDate, dst2MGit, true);
		
		//DST1-LJ
		CohortDefinition allDST1Lj= Cohorts.getAllByTestType(startDate, endDate, dst1Lj, true);
		
		//DST2-LJ
		CohortDefinition allDST2Lj= Cohorts.getAllByTestType(startDate, endDate, dst2Lj, true);
				
				
		///////////////////////////////			LJ TABLE		////////////////////////////////////////////		
		//ALl LJs
		//allLjs = ReportUtil.getCodedObsCohort(TimeModifier.ANY, typeOfCulture.getConceptId(), startDate, endDate, SetComparator.IN, ljCultureType.getId());
		
		//All Patients
		CohortCrossTabDataSetDefinition cultureTable = new CohortCrossTabDataSetDefinition();
		CohortCrossTabDataSetDefinition smearTable = new CohortCrossTabDataSetDefinition();
		CohortCrossTabDataSetDefinition mgitTable = new CohortCrossTabDataSetDefinition();
		CohortCrossTabDataSetDefinition ljTable = new CohortCrossTabDataSetDefinition();
		
		//Diagnostic Patients
		CohortCrossTabDataSetDefinition diagnosticTable = new CohortCrossTabDataSetDefinition();
		CohortCrossTabDataSetDefinition diagnosticMgitTable = new CohortCrossTabDataSetDefinition();
		CohortCrossTabDataSetDefinition diagnosticLjTable = new CohortCrossTabDataSetDefinition();
		
		smearTable.addColumn("smearCount",allSmear,null);
		smearTable.addColumn("mgitCount",allMgits,null);
		smearTable.addColumn("mgitPositive",positiveMgits,null);
		smearTable.addColumn("mgitNegative",negativeMgits,null);
		smearTable.addColumn("HTM", htmMgits, null);
		smearTable.addColumn("RC", progrowthMgits, null);
		smearTable.addColumn("DST1Mgit", allDST1Mgit, null);
		smearTable.addColumn("DST2Mgit", allDST2Mgit, null);
		
		Cohort allM = null;//
		try {
			allM = Context.getService(CohortDefinitionService.class).evaluate(allMgits, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Cohort contM = null;//
		try {
			contM = Context.getService(CohortDefinitionService.class).evaluate(progrowthMgits, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Double smearSetRC = 0.0;
		Double numAllM = 0.0;
		Double numContM = 0.0;
		if(allM!=null && contM!=null) {
			numAllM = (double) allM.getSize();
			numContM = (double) contM.getSize();
			
			if(numAllM!=0) {
				smearSetRC = (numContM*100)/numAllM;
			}
		}
		
		context.addParameterValue("smearsetPercentageRC", DSTReportUtil.round(smearSetRC, 1));
		
		smearTable.addColumn("ljCount", allLjs, null);
		smearTable.addColumn("ljPositive", positiveLjs, null);
		smearTable.addColumn("ljNegative", negativeLjs, null);
		smearTable.addColumn("DST1Lj", allDST1Lj, null);
		smearTable.addColumn("DST2Lj", allDST2Lj, null);
		
		diagnosticTable.addColumn("smearCount",ReportUtil.getCompositionCohort("AND",allDiagnostics,allSmear),null);
		diagnosticTable.addColumn("mgitCount",ReportUtil.getCompositionCohort("AND",allDiagnostics,allMgits),null);
		diagnosticTable.addColumn("mgitPositive",ReportUtil.getCompositionCohort("AND",allDiagnostics,positiveMgits),null);
		diagnosticTable.addColumn("mgitNegative",ReportUtil.getCompositionCohort("AND",allDiagnostics,negativeMgits),null);
		diagnosticTable.addColumn("HTM", ReportUtil.getCompositionCohort("AND",allDiagnostics,htmMgits), null);
		diagnosticTable.addColumn("RC", ReportUtil.getCompositionCohort("AND",allDiagnostics,progrowthMgits), null);
		diagnosticTable.addColumn("DST1Mgit", ReportUtil.getCompositionCohort("AND",allDiagnostics,allDST1Mgit), null);
		diagnosticTable.addColumn("DST2Mgit", ReportUtil.getCompositionCohort("AND",allDiagnostics,allDST2Mgit), null);
		
		diagnosticTable.addColumn("ljCount", ReportUtil.getCompositionCohort("AND",allDiagnostics,allLjs), null);
		diagnosticTable.addColumn("ljPositive", ReportUtil.getCompositionCohort("AND",allDiagnostics,positiveLjs), null);
		diagnosticTable.addColumn("ljNegative", ReportUtil.getCompositionCohort("AND",allDiagnostics,negativeLjs), null);
		diagnosticTable.addColumn("DST1Lj", ReportUtil.getCompositionCohort("AND",allDiagnostics,allDST1Lj), null);
		diagnosticTable.addColumn("DST2Lj", ReportUtil.getCompositionCohort("AND",allDiagnostics,allDST2Lj), null);
		
		Concept mgitResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		Concept ljResult = Context.getService(TbService.class).getConcept(TbConcepts.LJ_RESULT);
		
		//MGIT Kub + Bacteria
		String kubTotal = Context.getService(TbService.class).getCultureWithMicroscopy(startDate, endDate, mgitResult, mgitMT, locList);
		String kubPos = Context.getService(TbService.class).getCultureWithMicroscopyResultPositive(startDate, endDate,mgitResult, mgitMT, locList);
		String kubNeg = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitMT, microscopyNegative, locList);
		String kubNoMic = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitMT, microscopyTestNotDone, locList);
		
		//MGIT Tubes Negative Results
		String negTotal = Context.getService(TbService.class).getCultureWithMicroscopy(startDate, endDate,mgitResult, mgitNoGrowth, locList);
		String negPos = Context.getService(TbService.class).getCultureWithMicroscopyResultPositive(startDate, endDate,mgitResult, mgitNoGrowth, locList);
		String negNeg = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitNoGrowth, microscopyNegative, locList);
		String negNoMic = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitNoGrowth, microscopyTestNotDone, locList);
		
		//MGIT Mixture of contaminated Tubes
		String mixTotal = Context.getService(TbService.class).getCultureWithMicroscopy(startDate, endDate,mgitResult, mgitProGrowth, locList);
		String mixPos = Context.getService(TbService.class).getCultureWithMicroscopyResultPositive(startDate, endDate,mgitResult, mgitProGrowth, locList);
		String mixNeg = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitProGrowth, microscopyNegative, locList);
		String mixNoMic = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitProGrowth, microscopyTestNotDone, locList);
		
		context.addParameterValue("mgitKubMTPos", kubPos);
		context.addParameterValue("mgitKubMTNeg", kubNeg);
		context.addParameterValue("mgitKubNoMic", kubNoMic);		
		context.addParameterValue("mgitKubTotal", kubTotal);
		
		context.addParameterValue("mgitNegMTPos", negPos);
		context.addParameterValue("mgitNegMTNeg", negNeg);
		context.addParameterValue("mgitNegNoMic", negNoMic);		
		context.addParameterValue("mgitNegTotal", negTotal);
		
		context.addParameterValue("mgitMixMTPos", mixPos);
		context.addParameterValue("mgitMixMTNeg", mixNeg);
		context.addParameterValue("mgitMixNoMic", mixNoMic);		
		context.addParameterValue("mgitMixTotal", mixTotal);
		
		//LJs
		// Kub + Bacteria
		String ljKubTotal = Context.getService(TbService.class).getCultureWithMicroscopy(startDate, endDate, ljResult, mgitMT, locList);
		String ljKubPos = Context.getService(TbService.class).getCultureWithMicroscopyResultPositive(startDate, endDate,ljResult, mgitMT, locList);
		String ljKubNeg = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitMT, microscopyNegative, locList);
		String ljKubNoMic = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitMT, microscopyTestNotDone, locList);

		// Tubes Negative Results
		String ljNegTotal = Context.getService(TbService.class).getCultureWithMicroscopy(startDate, endDate,ljResult, mgitNoGrowth, locList);
		String ljNegPos = Context.getService(TbService.class).getCultureWithMicroscopyResultPositive(startDate, endDate,ljResult, mgitNoGrowth, locList);
		String ljNegNeg = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitNoGrowth, microscopyNegative, locList);
		String ljNegNoMic = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitNoGrowth, microscopyTestNotDone, locList);

		// Mixture of contaminated Tubes
		String ljMixTotal = Context.getService(TbService.class).getCultureWithMicroscopy(startDate, endDate,ljResult, mgitProGrowth, locList);
		String ljMixPos = Context.getService(TbService.class).getCultureWithMicroscopyResultPositive(startDate, endDate,ljResult, mgitProGrowth, locList);
		String ljMixNeg = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitProGrowth, microscopyNegative, locList);
		String ljMixNoMic = Context.getService(TbService.class).getCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitProGrowth, microscopyTestNotDone, locList);

		context.addParameterValue("ljKubMTPos", ljKubPos);
		context.addParameterValue("ljKubMTNeg", ljKubNeg);
		context.addParameterValue("ljKubNoMic", ljKubNoMic);		
		context.addParameterValue("ljKubTotal", ljKubTotal);

		context.addParameterValue("ljNegMTPos", ljNegPos);
		context.addParameterValue("ljNegMTNeg", ljNegNeg);
		context.addParameterValue("ljNegNoMic", ljNegNoMic);		
		context.addParameterValue("ljNegTotal", ljNegTotal);

		context.addParameterValue("ljMixMTPos", ljMixPos);
		context.addParameterValue("ljMixMTNeg", ljMixNeg);
		context.addParameterValue("ljMixNoMic", ljMixNoMic);		
		context.addParameterValue("ljMixTotal", ljMixTotal);
		
		//Diagnostics MGIT Kub + Bacteria
		String dgKubTotal = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopy(startDate, endDate, mgitResult, mgitMT, locList);
		String dgKubPos = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResultPositive(startDate, endDate,mgitResult, mgitMT, locList);
		String dgKubNeg = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitMT, microscopyNegative, locList);
		String dgKubNoMic = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitMT, microscopyTestNotDone, locList);

		//Diagnostics MGIT Tubes Negative Results
		String dgNegTotal = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopy(startDate, endDate,mgitResult, mgitNoGrowth, locList);
		String dgNegPos = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResultPositive(startDate, endDate,mgitResult, mgitNoGrowth, locList);
		String dgNegNeg = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitNoGrowth, microscopyNegative, locList);
		String dgNegNoMic = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitNoGrowth, microscopyTestNotDone, locList);

		//Diagnostics MGIT Mixture of contaminated Tubes
		String dgMixTotal = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopy(startDate, endDate,mgitResult, mgitProGrowth, locList);
		String dgMixPos = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResultPositive(startDate, endDate,mgitResult, mgitProGrowth, locList);
		String dgMixNeg = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitProGrowth, microscopyNegative, locList);
		String dgMixNoMic = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,mgitResult, mgitProGrowth, microscopyTestNotDone, locList);		
		
		context.addParameterValue("dgMgitKubTotal", dgKubTotal);
		context.addParameterValue("dgMgitKubMTPos", dgKubPos);
		context.addParameterValue("dgMgitKubMTNeg", dgKubNeg);
		context.addParameterValue("dgMgitKubNoMic", dgKubNoMic);		

		context.addParameterValue("dgMgitNegTotal", dgNegTotal);
		context.addParameterValue("dgMgitNegMTPos", dgNegPos);
		context.addParameterValue("dgMgitNegMTNeg", dgNegNeg);
		context.addParameterValue("dgMgitNegNoMic", dgNegNoMic);		

		context.addParameterValue("dgMgitMixTotal", dgMixTotal);
		context.addParameterValue("dgMgitMixMTPos", dgMixPos);
		context.addParameterValue("dgMgitMixMTNeg", dgMixNeg);
		context.addParameterValue("dgMgitMixNoMic", dgMixNoMic);		
		
		//Diagnostic LJs
		//Diagnostics MGIT Kub + Bacteria
		String dgLjKubTotal = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopy(startDate, endDate, ljResult, mgitMT, locList);
		String dgLjKubPos = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResultPositive(startDate, endDate,ljResult, mgitMT, locList);
		String dgLjKubNeg = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitMT, microscopyNegative, locList);
		String dgLjKubNoMic = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitMT, microscopyTestNotDone, locList);

		//Diagnostics MGIT Tubes Negative Results
		String dgLjNegTotal = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopy(startDate, endDate,ljResult, mgitNoGrowth, locList);
		String dgLjNegPos = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResultPositive(startDate, endDate,ljResult, mgitNoGrowth, locList);
		String dgLjNegNeg = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitNoGrowth, microscopyNegative, locList);
		String dgLjNegNoMic = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitNoGrowth, microscopyTestNotDone, locList);

		//Diagnostics MGIT Mixture of contaminated Tubes
		String dgLjMixTotal = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopy(startDate, endDate,ljResult, mgitProGrowth, locList);
		String dgLjMixPos = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResultPositive(startDate, endDate,ljResult, mgitProGrowth, locList);
		String dgLjMixNeg = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitProGrowth, microscopyNegative, locList);
		String dgLjMixNoMic = Context.getService(TbService.class).getDiagnosticCultureWithMicroscopyResult(startDate, endDate,ljResult, mgitProGrowth, microscopyTestNotDone, locList);		

		context.addParameterValue("dgLjKubTotal", dgLjKubTotal);
		context.addParameterValue("dgLjKubMTPos", dgLjKubPos);
		context.addParameterValue("dgLjKubMTNeg", dgLjKubNeg);
		context.addParameterValue("dgLjKubNoMic", dgLjKubNoMic);		

		context.addParameterValue("dgLjNegTotal", dgLjNegTotal);
		context.addParameterValue("dgLjNegMTPos", dgLjNegPos);
		context.addParameterValue("dgLjNegMTNeg", dgLjNegNeg);
		context.addParameterValue("dgLjNegNoMic", dgLjNegNoMic);		

		context.addParameterValue("dgLjMixTotal", dgLjMixTotal);
		context.addParameterValue("dgLjMixMTPos", dgLjMixPos);
		context.addParameterValue("dgLjMixMTNeg", dgLjMixNeg);
		context.addParameterValue("dgLjMixNoMic", dgLjMixNoMic);		

		ljTable.addColumn("total", allSmear, null);
		
		Cohort allL = null;
		Cohort contL = null;
		
		/*try {
			contL = Context.getService(CohortDefinitionService.class).evaluate(ReportUtil.getCompositionCohort("AND",allDiagnostics,progrowthLjs), new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			allL = Context.getService(CohortDefinitionService.class).evaluate(ReportUtil.getCompositionCohort("AND",allDiagnostics,allLjs), new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		try {
			contL = Context.getService(CohortDefinitionService.class).evaluate(progrowthLjs, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			allL = Context.getService(CohortDefinitionService.class).evaluate(allLjs, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Double smearSetRCLJ = 0.0;
		Double numAllLJ = 0.0;
		Double numContLJ = 0.0;
		if(allL!=null && contL!=null) {
			numAllLJ = (double) allL.getSize();
			numContLJ = (double) contL.getSize();
			
			if(numAllLJ!=0) {
				smearSetRCLJ = (numContLJ*100)/numAllLJ;
			}
		}
		
		context.addParameterValue("smearsetPercentageRCLJ", DSTReportUtil.round(smearSetRCLJ, 1));
		
		Double dgSetRC  = 0.0;
		Cohort allDiagM = null;
		Cohort contDiagM = null;
		Double numAllDiagM = 0.0;
		Double numContDiagM = 0.0;
		CohortDefinition allDiagMDef = ReportUtil.getCompositionCohort("AND",allDiagnostics,allMgits);
		CohortDefinition contDiagMDef = ReportUtil.getCompositionCohort("AND",allDiagnostics,progrowthMgits);
		try {
			allDiagM = Context.getService(CohortDefinitionService.class).evaluate(allDiagMDef, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			contDiagM = Context.getService(CohortDefinitionService.class).evaluate(contDiagMDef, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(allDiagM!=null && contDiagM!=null) {
			numAllDiagM = (double) allDiagM.getSize();
			numContDiagM = (double) contDiagM.getSize();
			
			if(numAllDiagM!=0) {
				dgSetRC = (numContDiagM*100)/numAllDiagM;
			}
		}
		
		context.addParameterValue("dgsetPercentageRC", DSTReportUtil.round(dgSetRC, 1));
		
		
		//LJ
		Double dgSetRCLJ  = 0.0;
		Cohort allDiagLJ = null;
		Cohort contDiagLJ = null;
		Double numAllDiagLJ = 0.0;
		Double numContDiagLJ = 0.0;
		CohortDefinition allDiagLJDef = ReportUtil.getCompositionCohort("AND",allDiagnostics,allLjs);
		CohortDefinition contDiagLJDef = ReportUtil.getCompositionCohort("AND",allDiagnostics,progrowthLjs);
		try {
			allDiagLJ = Context.getService(CohortDefinitionService.class).evaluate(allDiagLJDef, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			contDiagLJ = Context.getService(CohortDefinitionService.class).evaluate(contDiagLJDef, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(allDiagLJ!=null && contDiagLJ!=null) {
			numAllDiagLJ = (double) allDiagLJ.getSize();
			numContDiagLJ = (double) contDiagLJ.getSize();
			
			if(numAllDiagLJ!=0) {
				dgSetRCLJ = (numContDiagLJ*100)/numAllDiagLJ;
			}
		}
		
		context.addParameterValue("dgsetPercentageRCLJ", DSTReportUtil.round(dgSetRCLJ, 1));
		
		diagnosticLjTable.addColumn("total", ReportUtil.getCompositionCohort("AND",allDiagnostics,allSmear), null);
		diagnosticLjTable.addColumn("MTPositive", ReportUtil.getCompositionCohort("AND",allDiagnostics,allMtPositive,allLjs), null);
		diagnosticLjTable.addColumn("MTNegative", ReportUtil.getCompositionCohort("AND",allDiagnostics,allMtNegative, allLjs), null);
		diagnosticLjTable.addColumn("NoMicrosopy", ReportUtil.getCompositionCohort("AND",allDiagnostics,allMtNotValid, allLjs), null);	
		
		/*diagnosticLjTable.addRow("ljPositive",ReportUtil.getCompositionCohort("AND",allDiagnostics, positiveMgits), null);
		diagnosticLjTable.addRow("ljNegative", ReportUtil.getCompositionCohort("AND",allDiagnostics,negativeMgits), null);*/
		diagnosticLjTable.addRow("ljPositive",ReportUtil.getCompositionCohort("AND",allDiagnostics, positiveLjs), null);
		diagnosticLjTable.addRow("ljNegative", ReportUtil.getCompositionCohort("AND",allDiagnostics,negativeLjs), null);
		diagnosticLjTable.addRow("rcPositive",ReportUtil.getCompositionCohort("AND",allDiagnostics,progrowthLjs), null);
		
		
		////////////////////////////////////////////////////
		
		String avgAllMgit =  Context.getService(TbService.class).getAverageCultureInoculationDays(startDate, endDate, inoculationDate, mgitGrowthDate, null,null);
		String avgAllLj =  Context.getService(TbService.class).getAverageCultureInoculationDays(startDate, endDate, inoculationDate, ljGrowthDate,null,null);
		String avgDiagMgit =  Context.getService(TbService.class).getAverageCultureInoculationDays(startDate, endDate, inoculationDate, mgitGrowthDate,investigationType,newCase,previousCases);
		String avgDiagLj =  Context.getService(TbService.class).getAverageCultureInoculationDays(startDate, endDate, inoculationDate, ljGrowthDate,investigationType,newCase,previousCases);
		
		context.addParameterValue("avgMgitAll", avgAllMgit);
		context.addParameterValue("avgLjAll", avgAllLj);
		context.addParameterValue("avgMgitDg", avgDiagMgit);
		context.addParameterValue("avgLjDg", avgDiagLj);
		
		Parameter param = new Parameter();
		
		ReportDefinition report = new ReportDefinition();
		//String sqlQuery = "Select distinct e.patient_id  from encounter e where encounter_type ='" + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'";
		String sqlQuery = "select encounter.patient_id from encounter where encounter_type ='" + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'";
		
		CohortDefinition allLabPatients = new SqlCohortDefinition(Cohorts.getAllLabPatientTestedDuring(startDate,endDate,locList));
//		CohortDefinition locationFilter = null;
//		if (locList != null) {
//			locationFilter = /*Cohorts.getLocationFilter(location,
//					startDate, endDate)*/ new SqlCohortDefinition(q1Test);
//		}
//		
//		if(locationFilter == null)
//		{
			report.setBaseCohortDefinition(allLabPatients, null);				
//		}
//			
//		else {
//				
//			report.setBaseCohortDefinition(locationFilter, null);	
//		}
		report.setBaseCohortDefinition(allLabPatients, null);		
		report.addDataSetDefinition("tbl", cultureTable, null);
		report.addDataSetDefinition("smearset", smearTable, null);
		report.addDataSetDefinition("mgit", mgitTable, null);
		report.addDataSetDefinition("lj", ljTable, null);
		
		report.addDataSetDefinition("dgset", diagnosticTable, null);
		report.addDataSetDefinition("dgmgit", diagnosticMgitTable, null);
		report.addDataSetDefinition("dglj", diagnosticLjTable, null);
		
		ReportData data;
		try {
			data = Context.getService(ReportDefinitionService.class).evaluate(
					report, context);
		} catch (EvaluationException e) {
			throw new MdrtbAPIException("Unable to evaluate Lab DST report",e);
		}
		return data;
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


