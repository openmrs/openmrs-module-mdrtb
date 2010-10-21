<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->

<br/>

<div align="center"> <!-- start of page div -->

<b class="boxHeader"><spring:message code="mdrtb.followupForm" text="Follow-Up Form"/></b>
<div class="box">

<form name="followup" action="followup.form?patientId=${patientId}&patientProgramId=${patientProgramId}&encounterId=${!empty followup.id ? followup.id : -1}" method="post">
<input type="hidden" name="returnUrl" value="${returnUrl}" />

<table>
 
<tr>
<td><spring:message code="mdrtb.date" text="Date"/>:</td>
<td><openmrs_tag:dateField formFieldName="encounterDatetime" startValue="${followup.encounterDatetime}"/></td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.location" text="Location"/>:</td>
<td>
<select name="location">
<option value=""></option>
<c:forEach var="location" items="${locations}">
	<option value="${location.id}" <c:if test="${followup.location == location}">selected</c:if>>${location.displayString}</option>
</c:forEach>
</select>
</td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.provider" text="Provider"/>:</td>
<td>
<select name="provider">
<option value=""></option>
<c:forEach var="provider" items="${providers}">
	<option value="${provider.id}" <c:if test="${followup.provider == provider}">selected</c:if>>${provider.personName}</option>
</c:forEach>
</select>
</td>
</tr>

</table>

<c:set var="defaultReturnUrl" value="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}"/>
<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button type="reset" onclick="window.location='${!empty returnUrl ? returnUrl : defaultReturnUrl}'"><spring:message code="mdrtb.cancel" text="Cancel"/></button>

</form>

</div>

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>