package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;


public class DstResultCellTag extends TagSupport {
	
    private static final long serialVersionUID = -1694368107155244376L;
    
	private final Log log = LogFactory.getLog(getClass());

	private Dst dst;
	
    private DstResult dstResult;

    public int doStartTag() {
    	
    	String ret = null;
    	
    	if(dstResult != null && dstResult.getResult() != null) {
    		String color = Context.getService(MdrtbService.class).getColorForConcept(dstResult.getResult());
    		if (color == null) {
    			color = "black";
    		}
    		
    		ret = "<td onmouseover=\"document.body.style.cursor = \'pointer\'\" onmouseout=\"document.body.style.cursor = \'default\'\" " +
    				"onclick=\"window.location = \'../specimen/specimen.form?specimenId=" + dst.getSpecimenId() + "&testId=" + dst.getId() +
    				"\'\" title=\"Result: " + dstResult.getResult().getBestShortName(Context.getLocale())  
    				+ "\" style=\"padding:0px;border:0px;margin:0px;background-color:" + color + ";\">&nbsp</td>";
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
    
	public void setDstResult(DstResult dstResult) {
	    this.dstResult = dstResult;
    }

	public DstResult getDstResult() {
	    return dstResult;
    }


	public void setDst(Dst dst) {
	    this.dst = dst;
    }


	public Dst getDst() {
	    return dst;
    }
    
    
}
