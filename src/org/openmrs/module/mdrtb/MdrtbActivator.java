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
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
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
        while (ms == null) {
            try {
                Thread.sleep(1000);
                try{
                    ms = Context.getService(MdrtbService.class);
                } catch (APIException apiEx){}
            } catch (InterruptedException ex) {}
        }
        
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
            throw new RuntimeException("Could not pre-load concepts" + ex);
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
        
        //TODO:  add Mdrtb standard regimens
        
        log.info("Finished loading mdrtb metadata.");
    }
    
    /**
     * Called when module is being shutdown
     */
    protected void onShutdown() {       
    }

	
}
