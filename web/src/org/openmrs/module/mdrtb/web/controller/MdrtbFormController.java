/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.birt.BirtReport;
import org.openmrs.module.birt.BirtReportService;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;
/**
 * This controller backs and saves the basic module settings
 * 
 */

public class MdrtbFormController extends SimpleFormController {
	
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
        	   
    private String personType = "patient";
    private String viewType = "shortEdit";
    private String personId = "";
    private String name = "";
    private String birthdate = "";
    private String age = "";
    private String gender = "";

    
    private final String PATIENT_SHORT_EDIT_URL = "module/mdrtb/mdrtbAddPatientForm.form";

    
    @Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {
        
        Map<String,Object> map = new HashMap<String,Object>();
        if (Context.isAuthenticated()){
            
            try { 
                BirtReportService reportService = (BirtReportService) Context.getService(BirtReportService.class);
                String str = Context.getAdministrationService().getGlobalProperty("mdrtb.birt_report_list");
                List<BirtReport> brList = new ArrayList<BirtReport>();
                List<BirtReport> allReports = reportService.getReports();
                for (StringTokenizer st = new StringTokenizer(str, "|"); st.hasMoreTokens(); ) {
                    String s = st.nextToken().trim();
                    for (BirtReport br:allReports){       
                        if (br.getName().equals(s))
                        brList.add(br);
                    }
                }
                if (str.equals(""))
                    brList = reportService.getReports();
                map.put("reports", brList); 
            } catch (Exception ex){
                log.error("Unable to setup birt reports in reference data in MdrtbFormController." + ex);
                map.put("reports", new ArrayList<String>());
            }
            //if blank string was passed in:
             
            String httpBase = request.getRequestURL().toString();
            String httpURI = request.getRequestURI();
            map.put("httpRoot", httpBase.replaceAll(httpURI,"") );
            
            String dateFormat = Context.getDateFormat().toPattern();
            map.put("dateFormat", dateFormat);
            
            MessageSourceAccessor msa = getMessageSourceAccessor();
            map.put("daysOfWeek", "'" + msa.getMessage("mdrtb.sunday")+ "','" + msa.getMessage("mdrtb.monday")+ "','" + msa.getMessage("mdrtb.tuesday") + "','" + msa.getMessage("mdrtb.wednesday")+ "','" + msa.getMessage("mdrtb.thursday")+ "','" + msa.getMessage("mdrtb.friday")+ "','"
                    + msa.getMessage("mdrtb.saturday")+ "','" + msa.getMessage("mdrtb.sun")+ "','" + msa.getMessage("mdrtb.mon")+ "','"+ msa.getMessage("mdrtb.tues")+ "','"+ msa.getMessage("mdrtb.wed")+ "','"+ msa.getMessage("mdrtb.thurs")+ "','"+ msa.getMessage("mdrtb.fri")+ "','" + msa.getMessage("mdrtb.sat") + "'");
            map.put("monthsOfYear", "'" + msa.getMessage("mdrtb.january")+ "','"+ msa.getMessage("mdrtb.february")+ "','"+ msa.getMessage("mdrtb.march")+ "','"+ msa.getMessage("mdrtb.april")+ "','"+ msa.getMessage("mdrtb.may")+ "','"+ msa.getMessage("mdrtb.june")+ "','"+ msa.getMessage("mdrtb.july")+ "','"+ msa.getMessage("mdrtb.august")+ "','"
                    + msa.getMessage("mdrtb.september")+ "','"+ msa.getMessage("mdrtb.october")+ "','"+ msa.getMessage("mdrtb.november")+ "','"+ msa.getMessage("mdrtb.december")+ "','"+ msa.getMessage("mdrtb.jan")+ "','"+ msa.getMessage("mdrtb.feb")+ "','"+ msa.getMessage("mdrtb.mar")+ "','"+ msa.getMessage("mdrtb.ap")+ "','"+ msa.getMessage("mdrtb.may")+ "','"
                    + msa.getMessage("mdrtb.jun")+ "','"+ msa.getMessage("mdrtb.jul")+ "','"+ msa.getMessage("mdrtb.aug")+ "','"+ msa.getMessage("mdrtb.sept")+ "','"+ msa.getMessage("mdrtb.oct")+ "','"+ msa.getMessage("mdrtb.nov")+ "','"+ msa.getMessage("mdrtb.dec")+ "'");
            
        }
		return map ;
	}


	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object, BindException exceptions) throws Exception {		
    	
	    if ("".equals(personId)) {

            
            return new ModelAndView(new RedirectView(getPersonURL("", personType, "shortEdit", request)));
        }
	    
				
    	return new ModelAndView(new RedirectView(getFormView()));
    }


    /**
     * This class returns the form backing object.  This can be a string, a boolean, or a normal
     * java pojo.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception { 
       
        return "";
    }

    private String getPersonURL(String personId, String personType, String viewType, HttpServletRequest request)
    throws ServletException, UnsupportedEncodingException {
        
        if ("shortEdit".equals(viewType))
        return request.getContextPath() + PATIENT_SHORT_EDIT_URL + getParametersForURL(personId, personType);

        throw new ServletException("Undefined personType/viewType combo: " + personType + "/" + viewType);
    
    }
    private String getParametersForURL(String personId, String personType) throws UnsupportedEncodingException {
        if ("".equals(personId))
            return "?addName=" + URLEncoder.encode(name, "UTF-8") + "&addBirthdate=" + birthdate + "&addAge=" + age + "&addGender=" + gender;
        else {
            if ("patient".equals(personType))
                return "?patientId=" + personId;
            else if ("user".equals(personType))
                return "?userId=" + personId;
        }
        return "";
    }
	    
}
