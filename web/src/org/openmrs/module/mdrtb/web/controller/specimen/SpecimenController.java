package org.openmrs.module.mdrtb.web.controller.specimen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.validators.SpecimenValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/specimen.form")
public class SpecimenController extends AbstractSpecimenController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Returns the smear that should be used to bind a form posting to
	 * 
	 * @param smearId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("smear")
	public Smear getSmear(@RequestParam(required = false, value="smearId") Integer smearId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Smear smear = null;
		
		// only do something here if the smear id has been set
		if (smearId != null) {
			// smearId != -1 is means "this is a new smear"
			if (smearId != -1) {
				smear = Context.getService(MdrtbService.class).getSmear(smearId);
			}
			
			// create the new smear if needed
			if (smear == null) {
				smear = Context.getService(MdrtbService.class).createSmear(Context.getEncounterService().getEncounter(specimenId));
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return smear;
	}
	
	/**
	 * Returns the culture that should be used to bind a form posting to
	 * 
	 * @param cultureId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("culture")
	public Culture getCulture(@RequestParam(required = false, value="cultureId") Integer cultureId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Culture culture = null;
		
		// only do something here if the culture id has been set
		if (cultureId != null) {
			// cultureId != -1 is means "this is a new culture"
			if (cultureId != -1) {
				culture = Context.getService(MdrtbService.class).getCulture(cultureId);
			}
			
			// create the new culture if needed
			if (culture == null) {
				culture = Context.getService(MdrtbService.class).createCulture(Context.getEncounterService().getEncounter(specimenId));
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return culture;
	}
	
	/**
	 * Returns the dst that should be used to bind a form posting to
	 * 
	 * @param dstId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("dst")
	public Dst getDst(@RequestParam(required = false, value="dstId") Integer dstId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Dst dst = null;
		
		// only do something here if the dst id has been set
		if (dstId != null) {
			// dstId != -1 is means "this is a new dst"
			if (dstId != -1) {
				dst = Context.getService(MdrtbService.class).getDst(dstId);
			}
			
			// create the new dst if needed
			if (dst == null) {
				dst = Context.getService(MdrtbService.class).createDst(Context.getEncounterService().getEncounter(specimenId));
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return dst;
	}
	
	
	/**
	 * Returns the specimen that should be used to bind a form posting to
	 * 
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("specimen")
	public Specimen getSpecimen(@RequestParam(required = true, value="specimenId") Integer specimenId) {
		return Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
	}
		
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimen(@RequestParam(required = false, value = "testId") String testId, ModelMap map) {
		
		// add the testId to the model map if there is one (this is used to make sure the proper detail page is shown after an edit)
		if (StringUtils.isEmpty(testId)) {
			testId = "-1";
		}
		map.put("testId", testId);
		
		return new ModelAndView("/module/mdrtb/specimen/specimen", map);
	}
	
	
	/** 
	 * Handles the submission of a specimen form
	 * 
	 * @param specimen
	 * @param specimenErrors
	 * @param status
	 * @param request
	 * @param map
	 * @param testId
	 * @param scannedLabReport
	 * @param removeScannedLabReports
	 * @return
	 */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=specimen")
	public ModelAndView processSubmit(@ModelAttribute("specimen") Specimen specimen, BindingResult specimenErrors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = false, value = "testId") String testId, 
	                                  @RequestParam(required = false, value = "addScannedLabReport") MultipartFile scannedLabReport,
	                                  @RequestParam(required = false, value = "removeScannedLabReport") String [] removeScannedLabReports) {
	  
		// validate
    	// TODO: add validation of other model objects
    	if(specimen != null) {
    		new SpecimenValidator().validate(specimen, specimenErrors);
    	}
		
		if (specimenErrors.hasErrors()) {
			map.put("testId", testId);
			map.put("specimenErrors", specimenErrors);
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
		
		// remove scanned lab reports if necessary
		if (removeScannedLabReports != null) {
			Set<String> removeScannedLabReportSet = new HashSet<String>(Arrays.asList(removeScannedLabReports));
			
			for (ScannedLabReport report : specimen.getScannedLabReports()) {
				if (report.getId() != null && removeScannedLabReportSet.contains(report.getId())) {
					specimen.removeScannedLabReport(report);
					// TODO: we don't actually remove the underlying PDF file--is this a problem?
				}
			}
		}
		
		// save the actual update
		Context.getService(MdrtbService.class).saveSpecimen(specimen);
		
		// (somewhat) hacky way to manually handle the addition of new scanned lab report
		// (this has to happen after the specimen is saved for some reason)
		if (scannedLabReport != null && !scannedLabReport.isEmpty()) {
			// create the new result
			ScannedLabReport report = specimen.addScannedLabReport();
			
			// TODO: hack for now, we are assuming that the location must be MSLI!
			report.setLab(Context.getLocationService().getLocation("MSLI"));
			report.setFile(scannedLabReport);
		
			// need to save this explicitly for the obs handler to pick it up and handle it properly
			Context.getService(MdrtbService.class).saveScannedLabReport(report);
		}
			
		// clears the command object from the session
		status.setComplete();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimen.getId() + "&testId=" + testId);
		
	}
    
    /**
     * Handles the submission of a smear form
     *
     * @param smear
     * @param smearErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=smear")
	public ModelAndView processSubmit(@ModelAttribute("smear") Smear smear, BindingResult smearErrors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") String specimenId,
	                                  @RequestParam(required = false, value = "testId") String testId) {
	                     
		// validate
    	if(smear != null) {
    		// TODO: add smear validation
    	}
				
		// save the actual update
		Context.getService(MdrtbService.class).saveSmear(smear);
			
		// clears the command object from the session
		status.setComplete();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + testId);
		
	}
    
    /**
     * Handles the submission of a culture form
     * 
     * @param culture
     * @param cultureErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=culture")
	public ModelAndView processSubmit(@ModelAttribute("culture") Culture culture, BindingResult cultureErrors,
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") String specimenId,
	                                  @RequestParam(required = false, value = "testId") String testId) {
	                     
		// validate
    	if(culture != null) {
    		// TODO: add culture validation
    	}
		
		// save the actual update
		Context.getService(MdrtbService.class).saveCulture(culture);
			
		// clears the command object from the session
		status.setComplete();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + testId);
		
	}
    
    /**
     * Handles the submission of a DST form
     * 
     * @param dst
     * @param dstErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @param removeDstResults
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=dst")
	public ModelAndView processSubmit(@ModelAttribute("dst") Dst dst, BindingResult dstErrors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") String specimenId,
	                                  @RequestParam(required = false, value = "testId") String testId, 
	                                  @RequestParam(required = false, value = "removeDstResult") String [] removeDstResults) {
	                     
		// validate
    	if(dst != null) {
    		// TODO: add DST validation
    	}
		
		// hacky way to manually handle the addition of new dsts
		int i = 1;
		while(i<=30) {
			
			if(StringUtils.isNotEmpty(request.getParameter("addDstResult" + i + ".drug")) ) {
				// create the new result
				DstResult dstResult = dst.addResult();
			
				// pull the values from the request
				String colonies = request.getParameter("addDstResult" + i + ".colonies");
				String concentration = request.getParameter("addDstResult" + i + ".concentration");
				String resultType = request.getParameter("addDstResult" + i + ".result");
				String drug = request.getParameter("addDstResult" + i + ".drug");
				
				// assign them if they exist
				if (StringUtils.isNotEmpty(colonies)) {
					dstResult.setColonies(Integer.valueOf(colonies));
				}
				if (StringUtils.isNotEmpty(concentration)) {
					dstResult.setConcentration(Double.valueOf(concentration));
				}
				// although the DstResult obj should handle it, still a good idea to set the result before the drug because of the wonky way result/drugs are stored
				if (StringUtils.isNotEmpty(resultType)) {
					dstResult.setResult(Context.getConceptService().getConcept(Integer.valueOf(resultType)));
				}
				if (StringUtils.isNotEmpty(drug)) {
					dstResult.setDrug(Context.getConceptService().getConcept(Integer.valueOf(drug)));
				}
			}
			i++;
		} 
		
		// remove dst results
		if(removeDstResults != null) {
			Set<String> removeDstResultSet = new HashSet<String>(Arrays.asList(removeDstResults));
			
			for(DstResult result : dst.getResults()) {
				if(result.getId() != null && removeDstResultSet.contains(result.getId())) {
					dst.removeResult(result);
				}
			}
		}
			
		// save the actual update
		Context.getService(MdrtbService.class).saveDst(dst);
		
		// clears the command object from the session
		status.setComplete();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + testId);
		
	}
    
    
    
	
}
