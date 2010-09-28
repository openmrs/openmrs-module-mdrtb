<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->

<!--  DISPLAY ANY ERROR MESSAGES -->

<div align="center"> <!-- start of page div -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.alerts" text="Alerts"/></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">
<c:forEach var="flag" items="${flags}">
<tr><td>${flag.message}</td></tr>
</c:forEach>
</table>
</div>

<br/>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.labResultsStatus" text="Lab Results Status"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.controlSmear" text="Control Smear"/>: ${status.labResultsStatus.controlSmear.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.diagnosticCulture" text="Diagnostic Culture"/>: ${status.labResultsStatus.diagnosticCulture.displayString}</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0">
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentSmear}"/><spring:message code="mdrtb.mostRecentSmear" text="Most Recent Smear"/>: ${status.labResultsStatus.mostRecentSmear.displayString}</td></tr>
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentSmear}"/><spring:message code="mdrtb.mostRecentCulture" text="Most Recent Culture"/>: ${status.labResultsStatus.mostRecentCulture.displayString}</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0">
<tr><td>
<spring:message code="mdrtb.tbclassifcation" text="TB Classification"/>: ${status.labResultsStatus.tbClassification.displayString}
</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0">
<tr><td>
<spring:message code="mdrtb.resistanceProfile" text="Drug Resistance Profile"/>: ${status.labResultsStatus.drugResistanceProfile.displayString}
</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.pendingLabResults" text="Pending Lab Results"/></td></tr>
${status.labResultsStatus.pendingLabResults.displayString}
</table>



</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>