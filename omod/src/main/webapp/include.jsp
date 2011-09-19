<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ page import="org.openmrs.web.WebConstants" %>

<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>


<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/openmrs.js" />
<openmrs:htmlInclude file="/openmrs.css" />
<openmrs:htmlInclude file="/style.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRAlertService.js" />

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />
		
<!-- set the date format to use throughout the module -->
<c:set var="_dateFormatDisplay" value="dd/MMM/yyyy" scope="request"/>

<script type="text/javascript">
	/* variable used in js to know the context path */
	var openmrsContextPath = '${pageContext.request.contextPath}';
	
	/* js date format (required by openmrs calendar widget) */
	var jsDateFormat = '<openmrs:datePattern localize="false"/>';
	var jsLocale = '<%= org.openmrs.api.context.Context.getLocale() %>';
	
	var $j = jQuery.noConflict();
</script>