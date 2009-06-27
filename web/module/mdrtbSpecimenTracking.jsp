<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbSpecimenHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>
<style><%@ include file="resources/mdrtb.css"%></style>

<style>
	th {text-align:left;}
	th.patientTable,td.patientTable {text-align:left; white-space:nowrap; padding-right:30px; border: 1px solid black;}
</style>

<div style="font-size:80%">
	<h4><spring:message code="mdrtb.specimenTrackingTitle"/></h4>
	<br/>
	<table id="patientTable" style="border:2px solid black; width:auto;">
		<tr>
			<th class="patientTable"><spring:message code="mdrtb.sampleCollectedOn"/></th>
			<th class="patientTable"><spring:message code="Patient.identifier"/></th>
			<th class="patientTable"><spring:message code="Person.name"/></th>
			<th class="patientTable"><spring:message code="Person.gender"/></th>
			<th class="patientTable"><spring:message code="Person.age"/></th>
			<th class="patientTable"><spring:message code="Encounter.location"/></th>
		</tr>
		<c:forEach items="${encounters}" var="e">
			<tr class="patientRow patientRow${e.patient.patientId}">
				<td class="patientTable"><openmrs:formatDate date="${e.encounterDatetime}"/></td>
				<td class="patientTable">${e.patient.patientIdentifier.identifier}</td>
				<td class="patientTable">${e.patient.personName}</td>
				<td class="patientTable">${e.patient.gender}</td>
				<td class="patientTable">${e.patient.age}</td>
				<td class="patientTable">${e.location.name}</td>
			</tr>
		</c:forEach>
	</table>
</div>

<%@ include file="mdrtbFooter.jsp"%>
