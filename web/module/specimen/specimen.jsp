<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<table>
<tr>
<td>Sample Id:</td><td>${specimen.id.valueText}</td>
</tr>
<tr>
<td>Sample Type:</td><td>${specimen.type.valueCoded.name.name}</td> 
</tr>
<tr>
<td>Date Collected:</td><td>${specimen.encounter.encounterDatetime}</td>
</tr>
<tr>
<td>Collected By:</td><td>${specimen.encounter.provider.familyName}</td> <!-- TODO: obviously, need to find out proper way to handle names -->
</tr>
<tr>
<td>Location Collected:</td><td>${specimen.encounter.location.name}</td>
</tr>
</table>

<b>Smears</b>

<c:forEach var="smear" items="${specimen.smears}">
<table>
<tr>
<td>Result:</td><td>${smear.smearResult.valueCoded.name.name}</td>
</tr>
<tr>
<td>Bacilli:</td><td>${smear.bacilli.valueNumeric}</td>
</tr>
<tr>
<td>Result Date:</td><td>${smear.smearResultDate.valueDatetime}</td>
</tr>
<tr>
<td>Date Received:</td><td>${smear.smearDateReceived.valueDatetime}</td>
</tr>
<tr>
<td>Smear Method:</td><td>${smear.smearMethod.valueCoded.name.name}</td>
</tr>
<tr>
<td>Location:</td><td>${smear.smearParentObs.location}</td>
</tr>
</table>
</c:forEach>

<a href="editSmear.form?encounterId=${specimen.encounter.encounterId}">Add a Smear</a>

</body>
</html>