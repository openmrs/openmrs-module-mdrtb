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
package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsUtil;

public class FormatTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	protected static final Log log = LogFactory.getLog(FormatTag.class);
	
	//***** PROPERTIES *****
	
	private Object obj;
	private String separator;
	private String defaultVal;
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Does the actual formatting of each object
	 */
	public String formatObject(Object o) {
		if (ObjectUtil.notNull(o)) {
			if (o instanceof Regimen) {
				Regimen r = (Regimen)o;
				return RegimenUtils.formatRegimenGenerics(r, separator, ObjectUtil.nvlStr(defaultVal, "mdrtb.none"));
			}
			else if (o instanceof Concept) {
				return ((Concept)o).getBestShortName(Context.getLocale()).getName();
			}
			else if (o instanceof Obs) {
				Obs obs = (Obs)o;
				return obs.getValueAsString(Context.getLocale());
			}
			else {
				return o.toString();
			}
		}
		return MessageUtil.translate(defaultVal);
	}
	
    /**
     * @see Tag#doStartTag()
     */
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		
		String ret = "";
		if (obj != null) {
			if (obj instanceof Collection<?>) {
				Collection<?> l = (Collection<?>) obj;
				if (!l.isEmpty()) {
					Object o = l.iterator().next();
					if (o instanceof Concept) {
						ret = RegimenUtils.formatConcepts((Collection<Concept>)l, separator, defaultVal);
					}
					else if (o instanceof DrugOrder) {
						ret = RegimenUtils.formatDrugOrders(((Collection<DrugOrder>)l), separator, defaultVal);
					}
					else {
						List<String> s = new ArrayList<String>();
						for (Object item : l) {
							s.add(formatObject(item));
						}
						ret = OpenmrsUtil.join(s, separator);
					}
				}
				else {
					ret = defaultVal;
				}
			}
			else {
				ret = formatObject(obj);
			}
		}
		try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting duration tag", ex);
		}
		return SKIP_BODY;
	}
	
    /**
     * @see Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
	    obj = null;
	    separator = null;
	    defaultVal = null;
	    return EVAL_PAGE;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the obj
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * @param obj the obj to set
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return the defaultVal
	 */
	public String getDefaultVal() {
		return defaultVal;
	}

	/**
	 * @param defaultVal the defaultVal to set
	 */
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
}
