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

<b class="boxHeader"><spring:message code="mdrtb.labs" text="Labs"/></b>
<div class="box">
<a href="add.form?patientId=${patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.addANewSpecimen" text="Add a new Specimen"/></a>
<br/><br/>
<table border="1" cellspacing="3">
<tr>
<td><nobr><spring:message code="mdrtb.dateCollected" text="Date Collected"/></nobr></td>
<td><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/></nobr></td>
<td><nobr><spring:message code="mdrtb.smears" text="Smears"/></nobr></td>
<td><nobr><spring:message code="mdrtb.cultures" text="Cultures"/></nobr></td>
<td colspan="2">&nbsp;</td>
</tr>

<c:forEach var="specimen" items="${specimens}">
	<tr>
	<td><nobr><openmrs:formatDate date="${specimen.dateCollected}"/></nobr></td>
	<td><nobr><a href="specimen.form?specimenId=${specimen.id}&patientProgramId=${patientProgramId}">${specimen.identifier}</a></nobr></td>
	<mdrtb:smearCell smears="${specimen.smears}" parameters="&patientProgramId=${patientProgramId}"/>
	<mdrtb:cultureCell cultures="${specimen.cultures}" parameters="&patientProgramId=${patientProgramId}"/>
	<td><a href="specimen.form?specimenId=${specimen.id}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.view" text="View"/></a></td>
	<td><a href="delete.form?specimenId=${specimen.id}&patientId=${patientId}&patientProgramId=${patientProgramId}" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteSpecimen" text="Are you sure you want to delete this specimen?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></td>
	</tr>
</c:forEach>

</table>
</div>


