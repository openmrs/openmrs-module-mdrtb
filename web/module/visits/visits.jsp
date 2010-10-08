<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->

<div align="center"> <!-- start of page div -->

<!-- VISITS -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.visits" text="Visits"/></b>
<div class="box" style="margin:0px">

<c:if test="${fn:length(visits.intakeVisits.value) > 0}">
<b><spring:message code="mdrtb.intakeVisit" text="Intake Visit"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
<td>&nbsp;</td>
</tr>
<c:forEach var="encounterStatus" items="${visits.intakeVisits.value}">
<tr>
<td><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></td>
<td>${encounterStatus.value.location.displayString}</td>
<td>${encounterStatus.value.provider.personName}</td>
<td><a href="${encounterStatus.link}"><spring:message code="mdrtb.view" text="View"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>

<br/>
<a href="${visits.newIntakeVisit.link}"><spring:message code="mdrtb.addIntakeVisit" text="Add Intake Visit"/></a>
<br/>


<c:if test="${fn:length(visits.followUpVisits.value) > 0}">
<b><spring:message code="mdrtb.followUpVisits" text="Follow Up Visits"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
<td>&nbsp;</td>
</tr>
<c:forEach var="encounterStatus" items="${visits.followUpVisits.value}">
<tr>
<td><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></td>
<td>${encounterStatus.value.location.displayString}</td>
<td>${encounterStatus.value.provider.personName}</td>
<td><a href="${encounterStatus.link}"><spring:message code="mdrtb.view" text="View"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>

<br/>
<a href="${visits.newFollowUpVisit.link}"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></a>
<br/>

<c:if test="${fn:length(visits.scheduledFollowUpVisits.value) > 0}">
<b><spring:message code="mdrtb.scheduledFollowUpVisits" text="Future Follow-up Visits Scheduled"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
<td>&nbsp;</td>
</tr>
<c:forEach var="encounterStatus" items="${visits.scheduledFollowUpVisits.value}">
<tr>
<td><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></td>
<td>${encounterStatus.value.location.displayString}</td>
<td>${encounterStatus.value.provider.personName}</td>
<td><a href="${encounterStatus.link}"><spring:message code="mdrtb.view" text="View"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>

<br/>

<c:if test="${fn:length(visits.specimenCollectionVisits.value) > 0}">
<b><spring:message code="mdrtb.specimenCollections" text="Specimen Collections"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
<td>&nbsp;</td>
</tr>
<c:forEach var="encounterStatus" items="${visits.specimenCollectionVisits.value}">
<tr>
<td><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></td>
<td>${encounterStatus.value.location.displayString}</td>
<td>${encounterStatus.value.provider.personName}</td>
<td><a href="${encounterStatus.link}"><spring:message code="mdrtb.view" text="View"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>
<br/>

</div>

<!--  END VISIT STATUS POPUP -->



</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>