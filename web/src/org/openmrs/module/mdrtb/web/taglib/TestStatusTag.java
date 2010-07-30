package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;


public class TestStatusTag extends TagSupport {

    private static final long serialVersionUID = -447249746150313189L;

	private final Log log = LogFactory.getLog(getClass());
	
	private Test test;
	
	private String type;

	public int doStartTag() {
	
		String ret = "";
		
		if(type == null || type.equals("STANDARD")) {
			ret = TestStatusRenderer.renderStandardStatus(test);
		}
		else if(type.equalsIgnoreCase("SHORT")) {
			ret = TestStatusRenderer.renderShortStatus(test);
		}
		else {
			log.warn("Invalid value for attribute type of testStatus tag. Rendering standard type");
		}
		
		try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting dst result tag", ex);
		}
    	
    	return SKIP_BODY;
	}
	
	public int doEndTag() {
	    test = null;
	    type = null;
	    return EVAL_PAGE;
	}
	
	
	public void setTest(Test test) {
	    this.test = test;
    }

	public Test getTest() {
	    return test;
    }

	public void setType(String type) {
	    this.type = type;
    }

	public String getType() {
	    return type;
    } 	
}
