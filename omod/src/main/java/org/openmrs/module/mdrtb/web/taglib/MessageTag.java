package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

/**
 * Resolves messages through the MDR-TB service.
 */
public class MessageTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String text;
	
	private Object arguments;
	
	private String var;
	
	private boolean javaScriptEscape;
	
	@Override
	public int doStartTag() throws JspException {
		String message = resolveMessage();
		if (javaScriptEscape) {
			message = escapeJavaScript(message);
		}
		
		if (var != null) {
			pageContext.setAttribute(var, message, PageContext.PAGE_SCOPE);
		} else {
			try {
				JspWriter writer = pageContext.getOut();
				writer.write(message);
			}
			catch (IOException e) {
				throw new JspException("Unable to write message " + code, e);
			}
		}
		return SKIP_BODY;
	}
	
	@Override
	public int doEndTag() {
		code = null;
		text = null;
		arguments = null;
		var = null;
		javaScriptEscape = false;
		return EVAL_PAGE;
	}
	
	private String resolveMessage() {
		Object[] resolvedArguments = resolveArguments();
		return Context.getService(MessagePropertyService.class).getMessage(code, resolvedArguments, text);
	}
	
	private Object[] resolveArguments() {
		if (arguments == null) {
			return new Object[0];
		}
		if (arguments instanceof Object[]) {
			return (Object[]) arguments;
		}
		if (arguments instanceof Collection<?>) {
			return ((Collection<?>) arguments).toArray();
		}
		return arguments.toString().split("\\s*,\\s*");
	}
	
	private String escapeJavaScript(String value) {
		StringBuilder escaped = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char character = value.charAt(i);
			switch (character) {
				case '\\':
					escaped.append("\\\\");
					break;
				case '\'':
					escaped.append("\\'");
					break;
				case '\"':
					escaped.append("\\\"");
					break;
				case '\n':
					escaped.append("\\n");
					break;
				case '\r':
					escaped.append("\\r");
					break;
				case '\t':
					escaped.append("\\t");
					break;
				default:
					escaped.append(character);
			}
		}
		return escaped.toString();
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setArguments(Object arguments) {
		this.arguments = arguments;
	}
	
	public void setVar(String var) {
		this.var = var;
	}
	
	public void setJavaScriptEscape(boolean javaScriptEscape) {
		this.javaScriptEscape = javaScriptEscape;
	}
}
