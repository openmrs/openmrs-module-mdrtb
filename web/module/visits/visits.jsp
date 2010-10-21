<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->

<div align="center"> <!-- start of page div -->

<!-- VISITS -->

<!-- START LEFT-HAND COLUMN -->
<div id="leftColumn" style="float: left; width:49%;  padding:0px 4px 4px 4px">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.intakeVisit" text="Intake Visit"/></b>
<div class="box" style="margin:0px">
<c:forEach var="encounterStatus" items="${visits.intakeVisits.value}">
<a href="${pageContext.request.contextPath}${encounterStatus.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form%3FpatientProgramId=${patientProgramId}"><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></a>
 - ${encounterStatus.value.location.displayString} - ${encounterStatus.value.provider.personName}<br/>
</c:forEach>
<br/>
<c:if test="${empty visits.intakeVisits.value}">
<button onclick="window.location='${pageContext.request.contextPath}${visits.newIntakeVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addIntakeVisit" text="Add Intake Visit"/></button>
</c:if>
</div>

<br/>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.followUpVisits" text="Follow Up Visits"/></b>
<div class="box" style="margin:0px">
<c:forEach var="encounterStatus" items="${visits.followUpVisits.value}">
<a href="${pageContext.request.contextPath}${encounterStatus.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form%3FpatientProgramId=${patientProgramId}"><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></a>
 - ${encounterStatus.value.location.displayString} - ${encounterStatus.value.provider.personName}<br/>
</c:forEach>
<br/>
<button onclick="window.location='${pageContext.request.contextPath}${visits.newFollowUpVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></button>
</div>

<br/>

<c:if test="${fn:length(visits.scheduledFollowUpVisits.value) > 0}">
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.scheduledFollowUpVisits" text="Future Follow-up Visits Scheduled"/></b>
<div class="box" style="margin:0px">
<c:forEach var="encounterStatus" items="${visits.scheduledFollowUpVisits.value}">
<a href="${pageContext.request.contextPath}${encounterStatus.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form%3FpatientProgramId=${patientProgramId}"><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></a>
 - ${encounterStatus.value.location.displayString} - ${encounterStatus.value.provider.personName}<br/>
</c:forEach>
<br/>
<button onclick="window.location='${pageContext.request.contextPath}${visits.newFollowUpVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></button>
</div>
</c:if>

</div>

<!--  END OF LEFT-HAND COLUMN -->

<!--  START OF RIGHT-HAND COLUMN -->

<div id="rightColumn" style="float:right; width:49%; padding:0px 4px 4px 4px">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.specimenCollections" text="Specimen Collections"/></b>
<div class="box" style="margin:0px">
<c:forEach var="encounterStatus" items="${visits.specimenCollectionVisits.value}">
<a href="${encounterStatus.link}"><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></a>
 - ${encounterStatus.value.location.displayString} - ${encounterStatus.value.provider.personName}</br>
<br/>
</c:forEach>
</div>


</div>
<!--  END OF RIGHT HAND COLUMN -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>