<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!--  SPECIMEN SECTION -->

<table>
<tr>
<td>Sample Id:</td><td>${specimen.identifier}</td>
</tr>
<tr>
<td>Sample Type:</td><td>${specimen.type.name.name}</td> 
</tr>
<tr>
<td>Date Collected:</td><td>${specimen.dateCollected}</td>
</tr>
<tr>
<td>Collected By:</td><td>${specimen.provider.familyName}</td> <!-- TODO: obviously, need to find out proper way to handle names -->
</tr>
<tr>
<td>Location Collected:</td><td>${specimen.location.name}</td>
</tr>
</table>

<br/><br/><br/>

<c:forEach var="test" items="${specimen.tests}">

<!-- SUMMARY DETAIL SECTION -->
<table>
<tr>
<td>Status:</td><td>${test.status}</td>
</tr>
<tr>
<td>Result:</td><td>${test.result.name.name}</td>
</tr>
<tr>
</table>
<a href="delete.form?testId=${test.id}&specimenId=${specimen.specimenId}">Delete Test</a> 

<br/><br/>

</c:forEach>


<c:forEach var="test" items="${specimen.tests}">
<!--  FULL DETAIL SECTION -->
<table>
<tr>
<td>Laboratory:</td><td>${test.lab}</td>
</tr>

<tr>
<td>Date ordered:</td><td>${test.dateOrdered}</td>
</tr>

<tr>
<td>Date sample received:</td><td>${test.dateReceived}</td>
</tr>

<tr>
<td>Date completed:</td><td>${test.resultDate}</td>
</tr>

</table>

<br/><br/>

<!-- EDIT A TEST SECTION -->

<!--  TODO: how do i bind errors to this? -->
<!-- TODO: form id should be specified based on test type; get rid of enum, just use a String getTestType? -->
<form id="${test.testType}" action="specimen.form?${test.testType}Id=${test.id}&specimenId=${specimen.specimenId}" method="post">

<table>

<tr>
<td>Laboratory:</td>
<td><select id="lab" name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == test.lab}">selected</c:if> >${location.name}</option>
</c:forEach></td>
</select>
</td>
</tr>

<tr>
<td>Date ordered:</td>
<td><openmrs_tag:dateField formFieldName="dateOrdered" startValue="${test.dateOrdered}"/></td>
</tr>

<tr>
<td>Date sample received:</td>
<td><openmrs_tag:dateField formFieldName="dateReceived" startValue="${test.dateReceived}"/></td>
</tr>

<tr>
<td>Date completed:</td>
<td><openmrs_tag:dateField formFieldName="resultDate" startValue="${test.resultDate}"/></td>
</tr>

</table>

<input type="submit" value="Save" />

</form>

<br/><br/>

</c:forEach>

<!-- NEW SMEAR TEST SECTION -->

<!--  TODO: how do i bind errors to this? -->
<!-- TODO: form id should be specified based on test type; get rid of enum, just use a String getTestType? -->
<form id="smear" action="specimen.form?smearId=-1&specimenId=${specimen.specimenId}" method="post">

<table>

<tr>
<td>Laboratory:</td>
<td><select id="lab" name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}">${location.name}</option>
</c:forEach></td>
</select>
</td>
</tr>

<tr>
<td>Date ordered:</td>
<td><openmrs_tag:dateField formFieldName="dateOrdered" startValue=""/></td>
</tr>

<tr>
<td>Date sample received:</td>
<td><openmrs_tag:dateField formFieldName="dateReceived" startValue=""/></td>
</tr>

<tr>
<td>Date completed:</td>
<td><openmrs_tag:dateField formFieldName="resultDate" startValue=""/></td>
</tr>

</table>

<input type="submit" value="Add Smear" />

</form>

</body>
</html>