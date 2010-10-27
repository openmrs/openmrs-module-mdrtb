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



<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<!--  these are to make sure that the datepicker appears above the popup -->
<style type="text/css">
	#ui-datepicker-div { z-index: 9999; /* must be > than popup editor (950) */}
    .ui-datepicker {z-index: 9999 !important; /* must be > than popup editor (1002) */}
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.ui-dialog-titlebar-close{display: none;}  <!--hides the close button on the pop-ups -->
</style>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#programEditPopup').dialog({
			autoOpen: ${(fn:length(programEditErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
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
			autoOpen: ${(fn:length(programCloseErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.closeProgram" text="Close Program"/>',
			width: '50%',
			position: 'left',
		});

		$j('#programCloseButton').click(function() {
			$j('#programClosePopup').dialog('open');
		});

		$j('#programCloseCancelButton').click(function() {
			$j('#programClosePopup').dialog('close');
		});

		$j('#hospitalizationsEditPopup').dialog({
			autoOpen: false,
			position: 'right',
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.editHospitalization" text="Edit Hospitalization"/>',
			width: '30%',
			position: 'left',
		});

		// opens the pop-up to edit a hospitalization and populates the fill-ins with the appropriate dates
		$j('.hospitalizationsEditLink').click(function() {
			$j('#startDate').val($j(this).closest('tr').find('.admissionDate').html());
			$j('#endDate').val($j(this).closest('tr').find('.dischargeDate').html());
			$j('#hospitalizationStateId').val($j(this).attr('id'));
			$j('#hospitalizationsEditPopup').dialog('open');
		});
		
		$j('#hospitalizationsAddButton').click(function() {
			$j('#startDate').val('');
			$j('#endDate').val('');
			$j('#hospitalizationStateId').val('');
			$j('#hospitalizationsEditPopup').dialog('open');
		});

		$j('#hospitalizationsEditCancelButton').click(function() {
			$j('#hospitalizationsEditPopup').dialog('close');
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

		$j('#startDate').datepicker({		
			dateFormat: 'dd/mm/yy',
		 });

		$j('#endDate').datepicker({		
			dateFormat: 'dd/mm/yy',
		 });
	});
-->
</script>
<!-- END JQUERY -->

<!--  DISPLAY ANY ERROR MESSAGES -->


<div align="center" style="position:relative"> <!-- start of page div -->

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

<!-- START LEFT-HAND COLUMN -->
<div id="leftColumn" style="float: left; width:49%;  padding:0px 4px 4px 4px">

<!--  MDR-TB PROGRAM STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.programStatus" text="Program Status"/></b>
<div class="box" style="margin:0px;">

<table cellpadding="0" cellspacing="0">
<tr><td><spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>:</td><td width="75%"><openmrs:formatDate date="${program.dateEnrolled}"/></td></tr>
<tr><td><spring:message code="mdrtb.enrollment.location" text="Enrollment Location"/>:</td><td>${program.location.displayString}</td></tr>
</table>

<br/>

<table cellpadding="0" cellspacing="0">
<tr><td colspan="2"><spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:<br/>${program.classificationAccordingToPreviousDrugUse.concept.displayString}</td></tr>
<tr><td colspan="2"><spring:message code="mdrtb.previousTreatmentClassification" text="Registration Group - Previous Treatment"/>:<br/>${program.classificationAccordingToPreviousTreatment.concept.displayString}</td></tr>
</table>

<br/>

<table cellpadding="0" cellspacing="0">
<c:if test="${!program.active}">
<tr><td><spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><openmrs:formatDate date="${program.dateCompleted}"/></td></tr>
<tr><td><spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>${program.outcome.concept.displayString}</td></tr>
</c:if>
</table>

<button id="programEditButton"><spring:message code="mdrtb.editProgram" text="Edit Program"/></button> <c:if test="${program.active}"><button id="programCloseButton"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button></c:if>
</div>

<!--  EDIT PROGRAM POPUP -->
<div id="programEditPopup" style="display:none">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(programEditErrors.allErrors) > 0}">
	<c:forEach var="error" items="${programEditErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/>
		<br/>
	</c:forEach>
</c:if>

<form id="programEdit" action="${pageContext.request.contextPath}/module/mdrtb/program/programEdit.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>:</td><td><input id="dateEnrolled" type="text" size="14" tabindex="-1" name="dateEnrolled" value="<openmrs:formatDate date='${program.dateEnrolled}'/>"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.enrollment.Location" text="Enrollment Location"/>:</td><td>
<select id="location" name="location">
<option value=""/>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == program.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td colspan="2">
<spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:<br/>
<select name="classificationAccordingToPreviousDrugUse">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousDrugUse" items="${classificationsAccordingToPreviousDrugUse}">
<option value="${classificationAccordingToPreviousDrugUse.id}" <c:if test="${classificationAccordingToPreviousDrugUse == program.classificationAccordingToPreviousDrugUse}">selected</c:if> >${classificationAccordingToPreviousDrugUse.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td colspan="2">
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
<spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><input id="dateCompleted" type="text" size="14" name="dateCompleted" value="<openmrs:formatDate date='${program.dateCompleted}'/>"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>
<select name="outcome">
<option value=""/>
<c:forEach var="outcome" items="${outcomes}">
<option value="${outcome.id}" <c:if test="${outcome == program.outcome}">selected</c:if> >${outcome.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>
</c:if>
</table>

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> 

<!-- different button depending on whether not there are errors (we need to force a page reload on cancel if there are errors to reset the model object -->
<c:choose>
	<c:when test="${fn:length(programEditErrors.allErrors) == 0}">
		<button type="reset" id="programEditCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	</c:when>
	<c:otherwise>
		<button type="reset" onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	</c:otherwise>
</c:choose>

</form>
</div>

<!-- END EDIT PROGRAM POPUP-->

<!-- CLOSE PROGRAM POPUP -->

<div id="programClosePopup" style="display:none">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(programCloseErrors.allErrors) > 0}">
	<c:forEach var="error" items="${programCloseErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/>
		<br/>
	</c:forEach>
</c:if>

<form id="programClose" action="${pageContext.request.contextPath}/module/mdrtb/program/programClose.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><input id="dateToClose" type="text" size="14" tabindex="-1" name="dateCompleted" value="<openmrs:formatDate date='${program.dateEnrolled}'/>"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>
<select name="outcome">
<c:forEach var="outcome" items="${outcomes}">
<option value="${outcome.id}"  <c:if test="${outcome == program.outcome}">selected</c:if> >${outcome.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>
</table>
<button type="submit"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button> 

<!-- different button depending on whether not there are errors (we need to force a page reload on cancel if there are errors to reset the model object -->
<c:choose>
	<c:when test="${fn:length(programCloseErrors.allErrors) == 0}">
		<button type="reset" id="programCloseCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	</c:when>
	<c:otherwise>
		<button type="reset" onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	</c:otherwise>
</c:choose></form>
</div>

<!-- END CLOSE PROGRAM POPUP -->

<!--  END MDR-TB PROGRAM STATUS BOX -->

<br/>

<!-- TREATMENT STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.treatmentStatus" text="Treatment Status"/>: ${status.treatmentStatus.treatmentState.displayString}</b>
<div class="box" style="margin:0px">

<c:if test="${fn:length(status.treatmentStatus.regimens.value) > 0 }">
<table cellspacing="0" cellpadding="0" border="2px">
<tr>
<td><spring:message code="mdrtb.regimen" text="Regimen"/></td>
<td><spring:message code="mdrtb.startdate" text="Start Date"/></td>
<td><spring:message code="mdrtb.enddate" text="End Date"/></td>
<td><spring:message code="mdrtb.endReason" text="End Reason"/></td>
</tr>
<c:forEach var="regimen" items="${status.treatmentStatus.regimens.value}">
${regimen.displayString}
</c:forEach>
</table>
</c:if>
<br/>
<button onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addRegimens" text="Add Regimens"/></button>

</div>

<!-- END TREATMENT STATUS BOX -->

<br/>

<!--  VISIT STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.visitStatus" text="Visit Status"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">

<tr><td><spring:message code="mdrtb.intake" text="Intake"/>:</td><td>
<c:choose> 
	<c:when test="${! empty status.visitStatus.intakeVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.intakeVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.intakeVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>

<c:if test="${empty status.visitStatus.intakeVisits.value}">
<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newIntakeVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addIntakeVisit" text="Add Intake Visit"/></button>
</td></tr>
</c:if>



<tr><td><spring:message code="mdrtb.mostRecentFollowUp" text="Most Recent Follow-up"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.followUpVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>

<!-- 
<tr><td><spring:message code="mdrtb.mostRecentSpecimenCollection" text="Most Specimen Collection"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.specimenCollectionVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.specimenCollectionVisits.value[fn:length(status.visitStatus.specimenCollectionVisits.value) - 1].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.specimenCollectionVisits.value[fn:length(status.visitStatus.specimenCollectionVisits.value) - 1].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>
-->

<tr><td><spring:message code="mdrtb.nextScheduledFollowUp" text="Next Scheduled Follow-up"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.scheduledFollowUpVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.scheduledFollowUpVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.scheduledFollowUpVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>

<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newFollowUpVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></button>
</td></tr>

</table>

</div>

<!--  END VISIT STATUS BOX -->

<br/>

<!--  START HOSPITALIZATIONS STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.hospitalizations" text="Hospitalizations"/>: 
<c:choose>
	<c:when test="${program.currentlyHospitalized}">
		<spring:message code="mdrtb.currentlyHospitalized" text="Currently hospitalized"/>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.notCurrentlyHospitalized" text="Not currently hospitalized"/>
	</c:otherwise>
</c:choose></b>
<div class="box" style="margin:0px">

<c:if test="${!empty program.allHospitalizations}">

<table cellspacing="0" cellpadding="0" border="2px">
<tr>
<td><spring:message code="mdrtb.admisssionDate" text="Admission Date"/></td>
<td><spring:message code="mdrtb.dischareDate" text="Discharge Date"/></td>
<td><spring:message code="mdrtb.duration" text="Duration"/></td>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>

<c:forEach var="hospitalization" items="${program.allHospitalizations}">
<tr>
<td class="admissionDate"><openmrs:formatDate date="${hospitalization.startDate}"/></td>
<td class="dischargeDate">
<c:choose>
	<c:when test="${!empty hospitalization.endDate}">
		<openmrs:formatDate date="${hospitalization.endDate}"/>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.currentlyHospitalized" text="Currently hospitalized"/>
	</c:otherwise>
</c:choose>
</td>
<td>
<c:if test="${!empty hospitalization.endDate}">
	<mdrtb:duration startDate="${hospitalization.startDate}" endDate="${hospitalization.endDate}" format="days"/> 
	<spring:message code="mdrtb.days" text="days"/>
</c:if>
</td>
<td><a id="${hospitalization.id}" class="hospitalizationsEditLink" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"><spring:message code="mdrtb.edit" text="edit"/></a></td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/program/hospitalizationsDelete.form?patientProgramId=${program.id}&hospitalizationStateId=${hospitalization.id}" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteHospitalization" text="Are you sure you want to delete this hospitalization?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>

<br/>
<button id="hospitalizationsAddButton"><spring:message code="mdrtb.addHospitalization" text="Add Hospitalization"/></button> 

</div>

<!-- END HOSPITALIZATIONS STATUS BOX -->


<!--  EDIT HOSPITALIZATIONS POPUP -->
<div id="hospitalizationsEditPopup">
<form id="hospitalizationsEdit" action="${pageContext.request.contextPath}/module/mdrtb/program/hospitalizationsEdit.form?patientProgramId=${program.id}" method="post" >
<input type="hidden" id="hospitalizationStateId" name="hospitalizationStateId" value=""/>
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.admissionDate" text="Admission Date"/>:</td><td><input id="startDate" type="text" size="14" tabindex="-1" name="startDate"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.dischargeDate" text="Discharge Date"/>:</td><td><input id="endDate" type="text" size="14" tabindex="-1" name="endDate"/>
</td></tr>
</table>
<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> <button type="reset" id="hospitalizationsEditCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>
<!-- END EDIT HOSPITALIZATION POPUP-->

</div>

<!-- END LEFT COLUMN -->

<!--  START RIGHT COLUMN -->

<div id="rightColumn" style="float:right; width:49%; padding:0px 4px 4px 4px">

<!-- MDR-TB DIAGNOSIS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.mdrtbDiagnosis" text="MDR-TB Diagnosis"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.site" text="Site"/>:</td><td>${status.labResultsStatus.anatomicalSite.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.diagnosticSmear" text="Diagnostic Smear"/>:</td><td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.diagnosticSmear.link}">${status.labResultsStatus.diagnosticSmear.displayString}</mdrtb:a></td></tr>
<tr><td><spring:message code="mdrtb.diagnosticCulture" text="Diagnostic Culture"/>:</td><td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.diagnosticCulture.link}">${status.labResultsStatus.diagnosticCulture.displayString}</mdrtb:a></td></tr>

<c:if test="${empty status.labResultsStatus.diagnosticSmear.value || empty status.labResultsStatus.diagnosticCulture.value}">
	<tr><td><button onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addTestResults" text="Add Test Results"/></button></td></tr>
</c:if>

<c:if test="${!empty status.labResultsStatus.tbClassification.value ||  !empty status.labResultsStatus.drugResistanceProfile.value}">
	<tr><td><spring:message code="mdrtb.resistanceType" text="Resistance Type"/>:</td><td>${status.labResultsStatus.tbClassification.displayString}</td></tr>
	<tr><td><spring:message code="mdrtb.resistanceProfile" text="Resistance Profile"/>:</td><td>${status.labResultsStatus.drugResistanceProfile.displayString}</td></tr>
</c:if>

</table>


</div>

<!-- END MDR-TB DIAGNOSIS BOX -->

<br/>

<!-- LAB RESULTS STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.labResultsStatus" text="Bacteriology Status"/></b>
<div class="box" style="margin:0px">

<!--  TODO: get rid of these flags if they aren't being used -->

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.smearStatus" text="Smear Status"/>: ${status.labResultsStatus.smearConversion.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.culturestatus" text="Culture Status"/>: ${status.labResultsStatus.cultureConversion.displayString}</td></tr>
</table>

<table cellspacing="0" cellpadding="0">
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentSmear}"/><spring:message code="mdrtb.mostRecentSmear" text="Most Recent Smear"/>:</td>
<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentSmear.link}">${status.labResultsStatus.mostRecentSmear.displayString}</mdrtb:a></td></tr>
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentCulture}"/><spring:message code="mdrtb.mostRecentCulture" text="Most Recent Culture"/>:</td>
<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentCulture.link}">${status.labResultsStatus.mostRecentCulture.displayString}</mdrtb:a></td></tr>
<tr><td colspan="2"><button onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addTestResults" text="Add Test Results"/></button></td></tr>
</table>

<c:if test="${fn:length(status.labResultsStatus.pendingLabResults.value) > 0}"> 
<br/>
<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.pendingLabResults" text="Pending Lab Results"/></td></tr>
<c:forEach var="pendingLabResult" items="${status.labResultsStatus.pendingLabResults.value}">
<tr><td><a href="${pageContext.request.contextPath}${pendingLabResult.link}">${pendingLabResult.displayString}</a></td></tr>
</c:forEach>
</table>
</c:if>
</div>

<!-- END LAB RESULTS STATUS BOX -->

<br/>

<!-- HIV STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.hivStatus" text="HIV Status"/>: ${status.hivStatus.hivStatus.displayString}</b>
<div class="box" style="margin:0px">

<table cellspacing cellpadding="0">
<tr><td><spring:message code="mdrtb.mostRecentTestResult" text="Most Recent Test Result"/>:</td><td>${status.hivStatus.mostRecentTestResult.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.artTreatment" text="ART Treatment"/>:</td><td>${status.hivStatus.artTreatment.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.currentRegimen" text="Current Regimen"/>:</td><td>${status.hivStatus.currentRegimen.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.mostRecentCd4Count" text="Most Recent CD4 Count"/>:</td><td>${status.hivStatus.mostRecentCd4Count.displayString}</td></tr>
</table>

</div>
<!-- END HIV STATUS BOX -->

</div>

<!-- END OF RIGHT COLUMN -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>