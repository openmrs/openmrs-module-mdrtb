<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbTabs" id="mdrtbTabs" moduleId="mdrtb" patientId="${patientId}"/>


<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>


<b class="boxHeader">Add a Specimen</b>
<div class="box">

<form name="specimen" action="add.form?patientId=${patientId}" method="post">

<table>

<!-- TODO localize all text -->

<!-- TODO is answerConcept.name the correct parameter? -->

<tr>
<td>Sample ID:</td>
<td><input type="text" size="10" name="identifier" value="${specimen.identifier}"/></td>
</tr>
 
<tr>
<td>Sample Type:</td>
<td>
<select name="type">
<option value=""></option>
<c:forEach var="type" items="${types}">
	<option value="${type.answerConcept.id}" <c:if test="${specimen.type == type.answerConcept}">selected</c:if> >${type.answerConcept.name}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td>Date Collected:</td>
<td><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/></td>
</tr>

<!--  TODO: need to figure out how to map provider names properly: can we use the custom tags? -->

<tr>
<td>Collected By:</td>
<td>
<select name="provider">
<c:forEach var="provider" items="${providers}">
	<option value="${provider.id}" <c:if test="${specimen.provider == provider}">selected</c:if> >${provider.personName}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td>Location Collected:</td>
<td>
<select name="location">
<c:forEach var="location" items="${locations}">
	<option value="${location.locationId}" <c:if test="${location == specimen.location}">selected</c:if> >${location.name}</option>
</c:forEach>
</select>		
</td>
</tr>

<tr>
<td>Appearance:</td>
<td>
<select name="appearance">
<option value=""></option>
<c:forEach var="appearance" items="${appearances}">
	<option value="${appearance.answerConcept.id}" <c:if test="${specimen.appearance == appearance.answerConcept}">selected</c:if> >${appearance.answerConcept.name}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td>Comments:</td>
<td><textarea name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>
</tr>

</table>

<button type="submit">Save</button><a style="text-decoration:none" href="${pageContext.request.contextPath}/module/mdrtb/specimen/list.form?patientId=${patientId}"><button type="button">Cancel</button></a>

</form>
</div>


