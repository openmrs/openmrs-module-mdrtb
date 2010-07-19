package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.specimen.MdrtbDstResult;


public class DstResultCellTag extends TagSupport {
	
    private static final long serialVersionUID = -1694368107155244376L;
    
	private final Log log = LogFactory.getLog(getClass());

    private MdrtbDstResult dstResult;

    public int doStartTag() {
    	
    	String ret = null;
    	
    	if(dstResult != null && dstResult.getResult() != null) {
    		String color = MdrtbUtil.getColorForConcept(dstResult.getResult());
    		if (color == null) {
    			color = "black";
    		}
    		
    		ret = "<td style=\"padding:0px;border:0px;margin:0px;background-color:" + color + ";\">&nbsp</td>";
    	}
    	else {
    		ret = "<td style=\"padding:0px;border:0px;margin:0px;\"/>";
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
    	dstResult = null;
    	return EVAL_PAGE;
    }
    
	public void setDstResult(MdrtbDstResult dstResult) {
	    this.dstResult = dstResult;
    }

	public MdrtbDstResult getDstResult() {
	    return dstResult;
    }
    
    
}
