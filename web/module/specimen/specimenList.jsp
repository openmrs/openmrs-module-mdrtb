<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patientId}"/>


<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:8px; padding-right:8px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<b class="boxHeader">Specimen List</b>
<div class="box">
<a href="add.form?patientId=${patientId}">Add a new Specimen</a>
<br/><br/>
<table>
<tr>
<td><u><nobr>Date Collected</nobr></u></td>
<td><u><nobr>Sample ID</nobr></u></td>
<td><u><nobr>Location Collected</nobr></u></td>
<td><u><nobr>Collected By</nobr></u></td>
<td colspan="2">&nbsp;</td>
<td width="99%">&nbsp;</td>
</tr>

<c:forEach var="specimen" items="${specimens}">
	<tr>
	<td><nobr><openmrs:formatDate date="${specimen.dateCollected}"/></nobr></td>
	<td><nobr><a href="specimen.form?specimenId=${specimen.id}">${specimen.identifier}</a></nobr></td>
	<td><nobr>${specimen.location.name}</nobr></td>
	<td><nobr>${specimen.provider.personName}</nobr></td>  <!-- TODO: fix/use proper name? -->
	<td><a href="specimen.form?specimenId=${specimen.id}">view</a></td>
	<td><a href="delete.form?specimenId=${specimen.id}&patientId=${patientId}" onclick="return confirm('Are you sure you want to delete this specimen?')">delete</a></td>
	<td width="99%">&nbsp;</td>
	</tr>
</c:forEach>

</table>
</div>


