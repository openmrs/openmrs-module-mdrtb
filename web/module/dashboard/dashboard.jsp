<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>



<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<!--  these are to make sure that the datepicker appears above the popup -->
<style type="text/css">
	#ui-datepicker-div { z-index: 9999; /* must be > than popup editor (950) */}
    .ui-datepicker {z-index: 9999 !important; /* must be > than popup editor (1002) */}
</style>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#programEditPopup').dialog({
			autoOpen: false,
			modal: true,
			draggable: false,
			title: '<spring:message code="mdrtb.editProgram" text="Edit Program"/>',
			width: '50%',
			position: 'left',
		});

		$j('#programEditButton').click(function() {
			$j('#programEditPopup').dialog('open');
		});

		$j('#programEditCancelButton').click(function() {
			$j('#programEditPopup').dialog('close');
		});
		
		$j('#programClosePopup').dialog({
			autoOpen: false,
			modal: true,
			draggable: false,
			title: '<spring:message code="mdrtb.closeProgram" text="Close Program"/>',
			width: '30%',
			position: 'left'
		});

		$j('#programCloseButton').click(function() {
			$j('#programClosePopup').dialog('open');
		});

		$j('#programCloseCancelButton').click(function() {
			$j('#programClosePopup').dialog('close');
		});

		$j('#visitStatusPopup').dialog({
			autoOpen: false,
			modal: true,
			draggable: false,
			title: '<spring:message code="mdrtb.visits" text="Visits"/>',
			width: '60%',
			position: 'left'
		});

		$j('#visitStatusButton').click(function() {
			$j('#visitStatusPopup').dialog('open');
		});
		
		$j('#dateEnrolled').datepicker({		
			dateFormat: 'dd/mm/yy',
		 });
		
		$j('#dateCompleted').datepicker({		
			dateFormat: 'dd/mm/yy',
		 });

		$j('#dateToClose').datepicker({		
			dateFormat: 'dd/mm/yy',
		 });

	});
-->
</script>
<!-- END JQUERY -->

<!--  DISPLAY ANY ERROR MESSAGES -->


<div align="center"> <!-- start of page div -->

<!--  
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.alerts" text="Alerts"/></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">
<c:forEach var="flag" items="${flags}">
<tr><td>${flag.message}</td></tr>
</c:forEach>
</table>
</div>

<br/>
-->

<!--  MDR-TB PROGRAM STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.programStatus" text="MDR-TB Program Status"/></b>
<div class="box" style="margin:0px;">

<table cellpadding="0" cellspacing="0">
<tr><td><spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>: <openmrs:formatDate date="${program.dateEnrolled}"/></td></tr>
<tr><td><spring:message code="mdrtb.enrollment.location" text="Enrollment Location"/>: ${program.location.displayString}</td></tr>
</table>

<br/>

<table>
<tr><td><spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:<br/>${program.classificationAccordingToPreviousDrugUse.concept.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.previousTreatmentClassification" text="Registration Group - Previous Treatment"/>:<br/>${program.classificationAccordingToPreviousTreatment.concept.displayString}</td></tr>
</table>

<br/>

<table>
<c:if test="${!program.active}">
<tr><td><spring:message code="mdrtb.completionDate" text="Completion Date"/>: <openmrs:formatDate date="${program.dateCompleted}"/></td></tr>
<tr><td><spring:message code="mdrtb.outcome" text="Outcome"/>: ${program.outcome.concept.displayString}</td></tr>
</c:if>
</table>

<button id="programEditButton"><spring:message code="mdrtb.editProgram" text="Edit Program"/></button> <c:if test="${program.active}"><button id="programCloseButton"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button></c:if>
</div>

<!--  EDIT PROGRAM POPUP -->

<div id="programEditPopup">
<form id="programEdit" action="${pageContext.request.contextPath}/module/mdrtb/program/programEdit.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>: <input id="dateEnrolled" type="text" size="14" name="dateEnrolled" value="<openmrs:formatDate date='${program.dateEnrolled}'/>"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.enrollment.Location" text="Enrollment Location"/>:
<select name="location">
<option value=""/>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == program.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td>
<spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:<br/>
<select name="classificationAccordingToPreviousDrugUse">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousDrugUse" items="${classificationsAccordingToPreviousDrugUse}">
<option value="${classificationAccordingToPreviousDrugUse.id}" <c:if test="${classificationAccordingToPreviousDrugUse == program.classificationAccordingToPreviousDrugUse}">selected</c:if> >${classificationAccordingToPreviousDrugUse.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td>
<spring:message code="mdrtb.previousTreatmentClassification" text="Registration Group - Previous Treatment"/>:<br/>
<select name="classificationAccordingToPreviousTreatment">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousTreatment" items="${classificationsAccordingToPreviousTreatment}">
<option value="${classificationAccordingToPreviousTreatment.id}" <c:if test="${classificationAccordingToPreviousTreatment == program.classificationAccordingToPreviousTreatment}">selected</c:if> >${classificationAccordingToPreviousTreatment.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<c:if test="${!program.active}">
<tr><td>
<spring:message code="mdrtb.completionDate" text="Completion Date"/>: <input id="dateCompleted" type="text" size="14" name="dateCompleted" value="<openmrs:formatDate date='${program.dateCompleted}'/>"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.outcome" text="Outcome"/>:
<select name="outcome">
<c:forEach var="outcome" items="${outcomes}">
<option value="${outcome.id}" <c:if test="${outcome == program.outcome}">selected</c:if> >${outcome.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>
</c:if>
</table>

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> <button type="reset" id="programEditCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>

<!-- END EDIT PROGRAM POPUP-->

<!-- CLOSE PROGRAM POPUP -->

<div id="programClosePopup">
<form id="programClose" action="${pageContext.request.contextPath}/module/mdrtb/program/programClose.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.completionDate" text="Completion Date"/>: <input id="dateToClose" type="text" size="14" name="dateCompleted" value=""/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.outcome" text="Outcome"/>:
<select name="outcome">
<c:forEach var="outcome" items="${outcomes}">
<option value="${outcome.id}">${outcome.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>
</table>
<button type="submit"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button> <button type="reset" id="programCloseCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>

<!-- END CLOSE PROGRAM POPUP -->

<!--  END MDR-TB PROGRAM STATUS BOX -->

<br/>

<!-- TREATMENT STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.treatmentStatus" text="Treatment Status"/>: ${status.treatmentStatus.treatmentState.displayString}</b>
<div class="box" style="margin:0px">
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.regimen" text="Regimen"/></td>
<td><spring:message code="mdrtb.startdate" text="Start Date"/></td>
<td><spring:message code="mdrtb.enddate" text="End Date"/></td>
<td><spring:message code="mdrtb.endReason" text="End Reason"/></td>
<td><spring:message code="mdrtb.type" text="Type"/></td>
</tr>
<c:forEach var="regimen" items="${status.treatmentStatus.regimens.value}">
${regimen.displayString}
</c:forEach>
</table>
</div>

<!-- END TREATMENT STATUS BOX -->

<br/>

<!--  VISIT STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.visitStatus" text="Visit Status"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">

<tr><td><spring:message code="mdrtb.intake" text="Intake"/>: 
<c:if test="${! empty status.visitStatus.intakeVisits.value}">
<a href="${status.visitStatus.intakeVisits.value[0].link}">${status.visitStatus.intakeVisits.value[0].displayString}</a>
</c:if>
</td></tr>

<tr><td><spring:message code="mdrtb.mostRecentFollowUp" text="Most Recent Follow-up"/>: 
<c:if test="${! empty status.visitStatus.followUpVisits.value}">
<a href="${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].link}">${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].displayString}</a>
</c:if>
</td></tr>

<tr><td><spring:message code="mdrtb.mostRecentSpecimenCollection" text="Most Specimen Collection"/>: 
<c:if test="${! empty status.visitStatus.specimenCollectionVisits.value}">
<a href="${status.visitStatus.specimenCollectionVisits.value[fn:length(status.visitStatus.specimenCollectionVisits.value) - 1].link}">${status.visitStatus.specimenCollectionVisits.value[fn:length(status.visitStatus.specimenCollectionVisits.value) - 1].displayString}</a>
</c:if>
</td></tr>

<tr><td><spring:message code="mdrtb.nextScheduledFollowUp" text="Next Scheduled Follow-up"/>: 
<c:if test="${! empty status.visitStatus.scheduledFollowUpVisits.value}">
<a href="${status.visitStatus.scheduledFollowUpVisits.value[0].link}">${status.visitStatus.scheduledFollowUpVisits.value[0].displayString}</a>
</c:if>
</td></tr>

</table>

<button id="visitStatusButton"><spring:message code="mdrtb.viewAllVisits" text="View All Visits"/></button>

</div>

<!--  END VISIT STATUS BOX -->

<!-- VISIT STATUS POPUP -->
<div id="visitStatusPopup">

<c:if test="${fn:length(status.visitStatus.intakeVisits.value) > 0}">
<b><spring:message code="mdrtb.intakeVisit" text="Intake Visit"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
</tr>
<c:forEach var="encounterStatus" items="${status.visitStatus.intakeVisits.value}">
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

<c:if test="${fn:length(status.visitStatus.followUpVisits.value) > 0}">
<b><spring:message code="mdrtb.followUpVisits" text="Follow Up Visits"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
</tr>
<c:forEach var="encounterStatus" items="${status.visitStatus.followUpVisits.value}">
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

<c:if test="${fn:length(status.visitStatus.scheduledFollowUpVisits.value) > 0}">
<b><spring:message code="mdrtb.scheduledFollowUpVisits" text="Future Follow-up Visits Scheduled"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
</tr>
<c:forEach var="encounterStatus" items="${status.visitStatus.scheduledFollowUpVisits.value}">
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

<c:if test="${fn:length(status.visitStatus.specimenCollectionVisits.value) > 0}">
<b><spring:message code="mdrtb.specimenCollections" text="Specimen Collections"/></b>
<br/><br/>
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.date" text="Date"/></td>
<td><spring:message code="mdrtb.location" text="Location"/></td>
<td><spring:message code="mdrtb.provider" text="Provider"/></td>
</tr>
<c:forEach var="encounterStatus" items="${status.visitStatus.specimenCollectionVisits.value}">
<tr>
<td><openmrs:formatDate date="${encounterStatus.value.encounterDatetime}"/></td>
<td>${encounterStatus.value.location.displayString}</td>
<td>${encounterStatus.value.provider.personName}</td>
<td><a href="${encounterStatus.link}"><spring:message code="mdrtb.view" text="View"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>

</div>

<!--  END VISIT STATUS POPUP -->


<br/>

<!-- MDR-TB DIAGNOSIS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.mdrtbDiagnosis" text="MDR-TB Diagnosis"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.diagnosticSmear" text="Diagnostic Smear"/>: <a href="${status.labResultsStatus.diagnosticSmear.link}">${status.labResultsStatus.diagnosticSmear.displayString}</a></td></tr>
<tr><td><spring:message code="mdrtb.diagnosticCulture" text="Diagnostic Culture"/>: <a href="${status.labResultsStatus.diagnosticCulture.link}">${status.labResultsStatus.diagnosticCulture.displayString}</a></td></tr>
<tr><td><spring:message code="mdrtb.resistanceType" text="Resistance Type"/>: ${status.labResultsStatus.tbClassification.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.resistanceProfile" text="Resistance Profile"/>: ${status.labResultsStatus.drugResistanceProfile.displayString}</td></tr>
</table>

</div>

<!-- END MDR-TB DIAGNOSIS BOX -->

<br/>

<!-- LAB RESULTS STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.labResultsStatus" text="Lab Results Status"/>: ${status.labResultsStatus.cultureConversion.displayString}</b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentSmear}"/><spring:message code="mdrtb.mostRecentSmear" text="Most Recent Smear"/>: <a href="${status.labResultsStatus.mostRecentSmear.link}">${status.labResultsStatus.mostRecentSmear.displayString}</a></td></tr>
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentCulture}"/><spring:message code="mdrtb.mostRecentCulture" text="Most Recent Culture"/>: <a href="${status.labResultsStatus.mostRecentCulture.link}">${status.labResultsStatus.mostRecentCulture.displayString}</a></td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.pendingLabResults" text="Pending Lab Results"/></td></tr>
<c:forEach var="pendingLabResult" items="${status.labResultsStatus.pendingLabResults.value}">
<tr><td><a href="${pendingLabResult.link}">${pendingLabResult.displayString}</a></td></tr>
</c:forEach>
</table>

</div>

<!-- END LAB RESULTS STATUS BOX -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>