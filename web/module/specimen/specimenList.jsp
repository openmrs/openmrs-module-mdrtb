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
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.tableCell {border:1px solid #8FABC7; margin:0px; padding-left:2px; padding-right:2px; padding-top:2px; padding-bottom:2px; vertical-align:center}
</style>

<b class="boxHeader"><spring:message code="mdrtb.labs" text="Labs"/></b>
<div class="box">

<div id="specimenList" align="center">
<button onclick="window.location='add.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addANewSpecimen" text="Add a new Specimen"/></button>
<br/><br/>
<table cellspacing="0" cellpadding="0" border="2px">
<tr>
<td class="tableCell"><nobr><spring:message code="mdrtb.dateCollected" text="Date Collected"/></nobr></td>
<td class="tableCell"><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/></nobr></td>
<td class="tableCell"><nobr><spring:message code="mdrtb.smears" text="Smears"/></nobr></td>
<td class="tableCell"><nobr><spring:message code="mdrtb.cultures" text="Cultures"/></nobr></td>
<td class="tableCell"><nobr><spring:message code="mdrtb.dsts" text="DSTs"/></nobr></td>
<td class="tableCell" colspan="2">&nbsp;</td>
</tr>

<c:forEach var="specimen" items="${specimens}">
	<tr>
	<td class="tableCell"><nobr><openmrs:formatDate date="${specimen.dateCollected}"/></nobr></td>
	<td class="tableCell"><nobr><a href="specimen.form?specimenId=${specimen.id}&patientProgramId=${patientProgramId}">${specimen.identifier}</a></nobr></td>
	<td class="tableCell">
		<table style="padding:0px; border:0px; margin0px; width:100%">
			<tr><mdrtb:smearCell smears="${specimen.smears}" parameters="&patientProgramId=${patientProgramId}"/></tr>
		</table>
	</td>
	<td class="tableCell">
		<table style="padding:0px; border:0px; margin0px; width:100%">
			<tr><mdrtb:cultureCell cultures="${specimen.cultures}" parameters="&patientProgramId=${patientProgramId}"/></tr>
		</table>
	</td>	
	<td class="tableCell"><mdrtb:testStatus tests="${specimen.dsts}" type="short"/></td>
	<td class="tableCell"><a href="specimen.form?specimenId=${specimen.id}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.view" text="View"/></a></td>
	<td class="tableCell"><a href="delete.form?specimenId=${specimen.id}&patientId=${patientId}&patientProgramId=${patientProgramId}" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteSpecimen" text="Are you sure you want to delete this specimen?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></td>
	</tr>
</c:forEach>

</table>
</div>

</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>

