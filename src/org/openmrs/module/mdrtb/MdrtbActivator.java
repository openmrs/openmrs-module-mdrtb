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
package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.mdrtb.mdrtbregimens.MdrtbRegimenUtils;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class MdrtbActivator implements Activator, Runnable {

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * @see org.openmrs.module.Activator#startup()
     */
    public final void shutdown() {
        onShutdown();
    }
    
    
    


    /**
     * @see org.openmrs.module.Activator#shutdown()
     */
    public final void startup() {
        onStartup();
        
        Thread contextChecker = new Thread(this);
        contextChecker.start();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public final void run() {
        // Wait for context refresh to finish
        MdrtbService ms = null;
        try {
        Thread.sleep(30000);
        while (ms == null) {
            
                Thread.sleep(4000);
                try{
                    ms = Context.getService(MdrtbService.class);
                } catch (APIException apiEx){}
            
        }
        } catch (InterruptedException ex) {}
        
        try {
            // Start new OpenMRS session on this thread
            Context.openSession();
            Context.addProxyPrivilege("View Concept Classes");
            Context.addProxyPrivilege("View Concepts");
            Context.addProxyPrivilege("Manage Concepts");
            Context.addProxyPrivilege("View Global Properties");
            Context.addProxyPrivilege("Manage Global Properties");
            Context.addProxyPrivilege("SQL Level Access");
            onLoad(ms);
        } catch (Exception ex) {
            throw new RuntimeException("Could not pre-load concepts" + ex, ex);
        } finally {
            Context.removeProxyPrivilege("SQL Level Access");
            Context.removeProxyPrivilege("View Concept Classes");
            Context.removeProxyPrivilege("View Concepts");
            Context.removeProxyPrivilege("Manage Concepts");
            Context.removeProxyPrivilege("View Global Properties");
            Context.removeProxyPrivilege("Manage Global Properties");
            Context.closeSession();
        }   
    }
    
    /**
     * Called when module is being started
     */
    protected void onStartup() {        
    }
    
    /**
     * Called after module application context has been loaded. There is no authenticated
     * user so all required privileges must be added as proxy privileges
     */
    protected void onLoad(MdrtbService ms) {     
        
//add concepts to concept map:
        
        MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(1453), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "MULTIDRUG-RESISTANT TB TREATMENT START DATE");
        MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(730), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "CD4 PERCENT");
        MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(2169), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "RESULT OF HIV TEST");
        MdrtbUtil.addConceptMapForConcept(Context.getConceptService().getConcept(1391), Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"), "TREATMENT PLAN OTHER REMARKS");
        
        
        MdrtbFactory mu = MdrtbFactory.getInstance();
        ms.setMdrtbFactory(mu);
        ms.setStandardRegimens(MdrtbRegimenUtils.getMdrtbRegimenSuggestions());
        List<Locale> locales = new ArrayList<Locale>();
        List<List<Object>> rows = Context.getAdministrationService().executeSQL("select distinct locale from concept_word", true);
        
        //get all used locales in ConceptWord table
        for (List<Object> row:rows){
            for (Object o : row){
                String oTmp = (String) o;
                if (oTmp != null && oTmp != "")
                    locales.add(new Locale(oTmp));
            }
        }
        ms.setLocaleSetUsedInDB(locales);
        
        
        log.info("Finished loading mdrtb metadata.");
    }
    
    /**
     * Called when module is being shutdown
     */
    protected void onShutdown() {       
    }

    /**
     *  utility method if failure at startup, or you have reason to run again.  Waiting for 
     *  the 'afterContextLoad' method in trunk...
     */
    public void load() {  
        MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
 
        MdrtbFactory mu = ms.getMdrtbFactory();
        ms.setMdrtbFactory(mu);
        ms.setStandardRegimens(MdrtbRegimenUtils.getMdrtbRegimenSuggestions());
        List<Locale> locales = new ArrayList<Locale>();
        List<List<Object>> rows = Context.getAdministrationService().executeSQL("select distinct locale from concept_word", true);
        
        //get all used locales in ConceptWord table
        for (List<Object> row:rows){
            for (Object o : row){
                String oTmp = (String) o;
                if (oTmp != null && oTmp != "")
                    locales.add(new Locale(oTmp));
            }
        }
        ms.setLocaleSetUsedInDB(locales);
        log.info("Finished loading mdrtb metadata.");
    }
   

    /**
     * Provides a convenient way of calling load() from another
     * class, e.g. 
     * <code>UsageStatsActivator.getInstance().load();</code>
     * @return the instance of this activator created by OpenMRS
     */
    public static MdrtbActivator getInstance() {
     return (MdrtbActivator) ModuleFactory.getModuleById("mdrtb").getActivator();
    }

    
}
