package org.openmrs.module.mdrtb.web.controller.specimen;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbSmearObj;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("smear")
@RequestMapping("/module/mdrtb/specimen/editSmear.form")
public class EditSmearController  {

    protected final Log log = LogFactory.getLog(getClass());
	
    // TODO: add validator
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
    }
    
    @ModelAttribute("results")
    Collection<ConceptAnswer> getPossibleSmearResults() {
    	return Context.getService(MdrtbService.class).getPossibleSmearResults();
    }
    
    @ModelAttribute("methods")
    Collection<ConceptAnswer> getPossibleSmearMethods() {
    	return Context.getService(MdrtbService.class).getPossibleSmearMethods();
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showSmear(@RequestParam(required = false, value="obsId") Integer obsId, ModelMap model) {
		
		MdrtbSmearObj smear = null;
		
		// fetch the smear via service method
		if (obsId != null) {
			smear = Context.getService(MdrtbService.class).getSmearObj(obsId);
		}
			
		// create a new smear if we haven't fetched one
		// TODO: this should use the constructor that creates an obs pre-configured for an existing patient?  but need to add encounter?
		if (smear == null) {
			smear = new MdrtbSmearObj();
		}
		
		model.addAttribute("smear", smear);
		
		return new ModelAndView("/module/mdrtb/specimen/editSmear",model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("smear") MdrtbSmearObj smear, BindingResult result, SessionStatus status) {
		
		// TODO: add validation
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/mdrtb/specimen/editSmear");
		}
		
		// do the actual update
		Context.getService(MdrtbService.class).updateSmearObj(smear);
		
		// clears the command object from the session
		status.setComplete();
		
		// TODO: this will become a different redirect
		return new ModelAndView("redirect:/module/mdrtb/mdrtbIndex.form");
		
	}
	
}
