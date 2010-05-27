<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>



<openmrs:htmlInclude file="/moduleResources/mdrtb/jmesa.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jmesa.css" /> 
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css" /> 



<form name="patientSummaryTableForm" action="/module/mdrtb/mdrtbPatientSummaryTable.form">
${patientSummaryTable}
</form>

<%@ include file="mdrtbFooter.jsp"%>