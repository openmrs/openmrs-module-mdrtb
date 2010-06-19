<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

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

<b>Smears</b>

<c:forEach var="smear" items="${specimen.smears}">
<table>
<tr>
<td>Result:</td><td>${smear.result.name.name}</td>
</tr>
<tr>
<td>Bacilli:</td><td>${smear.bacilli}</td>
</tr>
<tr>
<td>Result Date:</td><td>${smear.resultDate}</td>
</tr>
<tr>
<td>Date Received:</td><td>${smear.dateReceived}</td>
</tr>
<tr>
<td>Smear Method:</td><td>${smear.method.name.name}</td>
</tr>
<tr>
<td>Lab:</td><td>${smear.lab}</td>
</tr>
</table>

<br/><br/>

</c:forEach>



<a href="editSmear.form?encounterId=${specimen.specimenId}">Add a Smear</a>

</body>
</html>