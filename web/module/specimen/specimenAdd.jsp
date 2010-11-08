<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>


<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>
<c:if test="${fn:length(testErrors.allErrors) > 0}">
	<c:forEach var="error" items="${testErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>

<b class="boxHeader">
<!-- display the proper header depending on whether we were adding a specimen, or a smear/culture -->
<c:choose>
	<c:when test="${! empty testType}">
		<spring:message code="mdrtb.${testType}"/> <spring:message code="mdrtb.resultForANewSpecimen" text="result for a new specimen"/>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.addASpecimen" text="Add a Specimen"/>
	</c:otherwise>
</c:choose>

</b>
<div class="box">

<form name="specimen" action="add.form?patientId=${patientId}&patientProgramId=${patientProgramId}" method="post">

<table>

<c:if test="${! empty testType}">
<tr>
<td colspan=2 align="left"><u><spring:message code="mdrtb.specimenDetails" text="Specimen Details"/></u></td>
</tr>
</c:if>

<tr>
<td><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</td>
<td><input type="text" size="10" name="identifier" value="${specimen.identifier}"/></td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.sampleType" text="Sample Type"/>:</td>
<td>
<select name="type">
<option value=""></option>
<c:forEach var="type" items="${types}">
	<option value="${type.answerConcept.id}" <c:if test="${specimen.type == type.answerConcept}">selected</c:if> >${type.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.dateCollected" text="Date Collected"/>:</td>
<td><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/></td>
</tr>

<!--  TODO: need to figure out how to map provider names properly: can we use the custom tags? -->

<tr>
<td><spring:message code="mdrtb.collectedBy" text="Collected By"/>:</td>
<td>
<select name="provider">
<option value=""/>
<c:forEach var="provider" items="${providers}">
	<option value="${provider.id}" <c:if test="${specimen.provider == provider}">selected</c:if> >${provider.personName}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.locationCollected" text="Location Collected"/>:</td>
<td>
<select name="location">
<c:forEach var="location" items="${locations}">
	<option value="${location.locationId}" <c:if test="${location == specimen.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>		
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.appearance" text="Appearance"/>:</td>
<td>
<select name="appearance">
<option value=""></option>
<c:forEach var="appearance" items="${appearances}">
	<option value="${appearance.answerConcept.id}" <c:if test="${specimen.appearance == appearance.answerConcept}">selected</c:if> >${appearance.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<!-- only show the comments if this isn't the "quick entry" version -->

<c:if test="${empty testType}">
<tr>
<td><spring:message code="mdrtb.comments" text="Comments"/>:</td>
<td><textarea name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>
</tr>
</c:if>

</table>

<!-- adds the ability to quick add smears/cultures -->
<c:if test="${! empty testType}">
<input type="hidden" name="testType" value="${testType}"/>

<table>
<tr>
<td colspan="2" align="left"><u><spring:message code="mdrtb.${testType}"/> <spring:message code="mdrtb.details" text="Details"/></u></td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate"  startValue="${resultDate}"/></nobr></td>
</tr>

<tr>
<td><spring:message code="mdrtb.lab" text="Lab"/>:</td>
<td><select name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == lab}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.results" text="Results"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == resultConcept}">selected</c:if> >${result.answerConcept.displayString}</option>

</c:forEach></td>
</select>
</tr>

</table>

</c:if>

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><a style="text-decoration:none" href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}"><button type="button"><spring:message code="mdrtb.cancel" text="Cancel"/></button></a>

</form>
</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>

