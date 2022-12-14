<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<%@ taglib prefix="wgt" uri="/WEB-INF/view/module/htmlwidgets/resources/htmlwidgets.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>


<c:if test="${program.patient.voided}">

	<div id="patientDashboardVoided" class="retiredMessage">
		<div><spring:message code="Patient.voidedMessage"/></div>
	</div>
</c:if>



<c:if test="${! empty patientId && patientId != -1}">
	<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
	
</c:if>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.ac_results {  z-index: 9999; }
	.ui-dialog-titlebar-close{display: none;}
</style>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript">

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#programEditPopup').dialog({
			autoOpen: ${(fn:length(programEditErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.editProgram" text="Edit Program"/>',
			width: '50%',
			position: [150,150],
			zIndex:990
		});

		$j('#programEditButton').click(function() {
			$j('#programEditPopup').dialog('open');
		});

		$j('#programEditCancelButton').click(function() {
			if (${fn:length(programEditErrors.allErrors) > 0}) {
				// if we are in the middle of a validation error, we need to do a page reload on cancel
				window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form?patientProgramId=${patientProgramId}";
			} 
			else {
				// otherwise, just close the popup
				$j('#programEditPopup').dialog('close');
			}
		});
		
		$j('#programClosePopup').dialog({
			autoOpen: ${(fn:length(programCloseErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.closeProgram" text="Close Program"/>',
			width: '50%',
			position: [150,150],
			zIndex:990
		});

		$j('#programCloseButton').click(function() {
			$j('#programClosePopup').dialog('open');
		});

		$j('#programCloseCancelButton').click(function() {
			if (${fn:length(programCloseErrors.allErrors) > 0}) {
				// if we are in the middle of a validation error, we need to do a page reload on cancel
				window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form?patientProgramId=${patientProgramId}";
			} 
			else {
				// otherwise, just close the popup
				$j('#programClosePopup').dialog('close');
			}
		});

		$j('#programDeleteButton').click(function() {
			if (confirm('<spring:message code="mdrtb.confirmDeleteProgram" text="Are you sure you want to delete this program?"/>')) {
				window.location="${pageContext.request.contextPath}/module/mdrtb/program/tbprogramDelete.form?patientProgramId=${patientProgramId}";
			}
		});
 		
		$j('#hospitalizationsEditPopup').dialog({
			autoOpen: ${(fn:length(hospitalizationErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.editHospitalization" text="Edit Hospitalization"/>',
			width: '40%',
			position: [150,150],
			zIndex:990
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
			if (${fn:length(hospitalizationErrors.allErrors) > 0}) {
				// if we are in the middle of a validation error, we need to do a page reload on cancel
				window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form?patientProgramId=${patientProgramId}";
			} 
			else {
				// otherwise, just close the popup
				$j('#hospitalizationsEditPopup').dialog('close');
			}
		});

		// event handler to hide/show date of death/cause of death fields
		$j('.outcome').change(function() {
				if ($j(this).attr('value') == ${patientDied.id}) {
					// show patient died fields
					$j(this).closest('div').find('.patientDiedField').show();
				}
				else {
					// hide the patient died fields and reset their values
					$j(this).closest('div').find('.patientDiedField').hide().find('#causeOfDeath').attr('value','');
					$j(this).closest('div').find('.patientDiedField').find('#causeOfDeathTextField').attr('value','');
				}
		});
	});
</script>
<!-- END JQUERY -->

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

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.programStatus" text="Program Status"/>
<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
<span style="position: absolute; left:575px;">
<c:choose>
<c:when test="${! empty program.dateCompleted && !empty program.outcome && program.outcome.concept.id==325}">
<a href="${pageContext.request.contextPath}/module/mdrtb/program/otherEnrollment.form?patientId=${patientId}&patientProgramId=-1&type=mdr&mdrLocation=${program.location.id}&previousProgramId=${program.id }&programStartDate=<openmrs:formatDate date="${program.dateCompleted}" format="${_dateFormatDisplay}"/>" class="delete" onclick="return confirm('<spring:message code="mdrtb.confirmMove" text="Are you sure you want to move this patient to MDR-TB program? The patient's current program will be automatically closed with outcome Moved to SLD Treatment"/>')"><spring:message code="mdrtb.moveToMDR" text="Enroll in MDR Program"/></a>
</c:when>
<c:otherwise>
<a href="${pageContext.request.contextPath}/module/mdrtb/program/otherEnrollment.form?patientId=${patientId}&patientProgramId=-1&type=mdr&mdrLocation=${program.location.id}&previousProgramId=${program.id }" class="delete" onclick="return confirm('<spring:message code="mdrtb.confirmMove" text="Are you sure you want to move this patient to MDR-TB program? The patient's current program will be automatically closed with outcome Moved to SLD Treatment"/>')"><spring:message code="mdrtb.moveToMDR" text="Enroll in MDR Program"/></a>
</c:otherwise>
</c:choose>
</span>
</openmrs:hasPrivilege>
</b>
<div class="box" style="margin:0px;">

<table cellpadding="0" cellspacing="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>:</td><td width="75%"><openmrs:formatDate date="${program.dateEnrolled}" format="${_dateFormatDisplay}"/></td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.enrollment.location" text="Enrollment Location"/>:</td><td>${program.location.displayString}</td></tr>
</table>

<br/>

<table cellpadding="0" cellspacing="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.tb03.patientGroup" text="Registration Group - Previous Tx"/>:</td>
<td>
<c:choose>
	<c:when test="${! empty program.classificationAccordingToPatientGroups.concept.displayString}">
		${program.classificationAccordingToPatientGroups.concept.displayString}
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.unknown"/>
	</c:otherwise>
</c:choose>
</td>
</tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:</td>
<td>
<c:choose>
	<c:when test="${! empty program.classificationAccordingToPreviousDrugUse.concept.displayString}">
		${program.classificationAccordingToPreviousDrugUse.concept.displayString}
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.unknown"/>
	</c:otherwise>
</c:choose>
</td>
</tr>

<tr><td style="font-weight:bold"><spring:message code="mdrtb.transferIn" text="TIN"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.transferInVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.transferInVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}">${status.visitStatus.transferInVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
	<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
		<a href="${pageContext.request.contextPath}${status.visitStatus.newTransferInVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}"><spring:message code="mdrtb.addTransferInVisit" text="Add TransferIn Visit"/></a>
	</openmrs:hasPrivilege>
	</c:otherwise>
</c:choose>
</td></tr>

<tr><td style="font-weight:bold"><spring:message code="mdrtb.transferOut" text="TOUT"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.transferOutVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.transferOutVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}">${status.visitStatus.transferOutVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
	<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
		<a href="${pageContext.request.contextPath}${status.visitStatus.newTransferOutVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}"><spring:message code="mdrtb.addTransferOutVisit" text="Add TransferOut Visit"/></a>
	</openmrs:hasPrivilege>
	</c:otherwise>
</c:choose>
</td></tr>
<%-- <c:if test="${empty status.visitStatus.transferOutVisits.value}">
<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newTransferOutVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}&patientId=${patientId}'"><spring:message code="mdrtb.addTransferOutVisit" text="Add TransferOut Visit"/></button>
</td></tr>
</c:if> --%>

</table>

<br/>

<table cellpadding="0" cellspacing="0">
<c:if test="${!program.active}">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><openmrs:formatDate date="${program.dateCompleted}" format="${_dateFormatDisplay}"/></td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>${program.outcome.concept.displayString}</td></tr>
</c:if>


</table>
<table>
<tr><td>
<a href="${pageContext.request.contextPath}/module/mdrtb/program/showEditEnroll.form?programId=${program.id}&type=tb"><spring:message code="mdrtb.editProgram" text="Edit Program"/></a> 
</td></tr>
</table>
</div>

<!-- CLOSE PROGRAM POPUP -->
</div> --%>

<!-- END CLOSE PROGRAM POPUP -->

<!--  END MDR-TB PROGRAM STATUS BOX -->

<br/>

<!-- <br/> -->

<!--  VISIT STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.forms" text="Forms"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">

<tr><td style="font-weight:bold"><spring:message code="mdrtb.tb03" text="TB03"/>:</td><td>
<c:choose> 
	<c:when test="${! empty status.visitStatus.intakeVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.intakeVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}">${status.visitStatus.intakeVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
	   <openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
		<a href="${pageContext.request.contextPath}${status.visitStatus.newIntakeVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}"><spring:message code="mdrtb.addIntakeVisit" text="Add Intake Visit"/></a>
	   </openmrs:hasPrivilege>
	</c:otherwise>
</c:choose>
</td></tr>
<%-- 
<c:if test="${empty status.visitStatus.intakeVisits.value}">
<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newIntakeVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}&patientId=${patientId}'"><spring:message code="mdrtb.addIntakeVisit" text="Add Intake Visit"/></button>
</td></tr>
</c:if> --%>



<tr><td style="font-weight:bold"><spring:message code="mdrtb.form89" text="Form89"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.followUpVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}&patientId=${patientId}">${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].displayString}</a>
	</c:when>
	<c:otherwise>
		<c:if test="${! empty isNew}">
			<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
				<a href="${pageContext.request.contextPath}${status.visitStatus.newFollowUpVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}%26patientId=${patientId}"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></a>
	  	    </openmrs:hasPrivilege>
	  	 </c:if>
	</c:otherwise>
</c:choose>
</td></tr>
<%-- <c:if test="${empty status.visitStatus.followUpVisits.value}">
<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newFollowUpVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/tbdashboard.form%3FpatientProgramId=${patientProgramId}&patientId=${patientId}'"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></button>
</td></tr>
</c:if> --%>



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

</table>

</div>

<!--  END VISIT STATUS BOX -->

<br/>

</div>

<!-- END LEFT COLUMN -->

<!--  START RIGHT COLUMN -->

<div id="rightColumn" style="float:right; width:49%; padding:0px 4px 4px 4px">

<!-- <br/> -->

<!-- LAB RESULTS STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.labResults" text="Bacteriology Status"/></b>
<div class="box" style="margin:0px">

<!--  TODO: get rid of these flags if they aren't being used -->

<%-- <table cellspacing="0" cellpadding="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.smearStatus" text="Smear Status"/>:</td><td>${status.labResultsStatus.smearConversion.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.culturestatus" text="Culture Status"/>:</td><td>${status.labResultsStatus.cultureConversion.displayString}</td></tr>
</table>

<br/> --%>

<table cellspacing="0" cellpadding="0" border="2px" width="100%">
<tr>
<td>&nbsp;</td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
<!-- <td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCompleted"/> </td>-->
<td style="font-weight:bold" colspan="2"><nobr>&nbsp;</nobr></td>

</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentSmear" text="Most Recent Smear"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentSmear.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentSmear.link}">${status.labResultsStatus.mostRecentSmear.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentSmear.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentSmear.value.lab.displayString}</td>
		<td>${status.labResultsStatus.mostRecentSmear.value.realSpecimenId}</td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/smear.form?encounterId=${status.labResultsStatus.mostRecentSmear.value.specimenId}&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.edit" text="Editz"/></a>
		</openmrs:hasPrivilege>
		</td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/smear.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/smear.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentCulture" text="Most Recent Culture"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentCulture.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentCulture.link}">${status.labResultsStatus.mostRecentCulture.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentCulture.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentCulture.value.lab.displayString}</td>
		<td>${status.labResultsStatus.mostRecentCulture.value.realSpecimenId}</td>
		<%-- <td><openmrs:formatDate date="${status.labResultsStatus.mostRecentCulture.value.resultDate}" format="${_dateFormatDisplay}"/></td> --%>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/culture.form?encounterId=${status.labResultsStatus.mostRecentCulture.value.specimenId}&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.edit" text="Editz"/></a>
		</openmrs:hasPrivilege>
		</td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/culture.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/culture.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentXpert" text="Most Recent Xpert"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentXpert.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentXpert.link}">${status.labResultsStatus.mostRecentXpert.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentXpert.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentXpert.value.lab.displayString}</td>
		<td>${status.labResultsStatus.mostRecentXpert.value.realSpecimenId}</td>
		<%-- <td><openmrs:formatDate date="${status.labResultsStatus.mostRecentXpert.value.resultDate}" format="${_dateFormatDisplay}"/></td> --%>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/xpert.form?encounterId=${status.labResultsStatus.mostRecentXpert.value.specimenId}&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.edit" text="Editz"/></a>
		</openmrs:hasPrivilege>
		</td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/xpert.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/xpert.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentHAIN" text="Most Recent HAIN"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentHAIN.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentHAIN.link}">${status.labResultsStatus.mostRecentHAIN.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentHAIN.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentHAIN.value.lab.displayString}</td>
		<td>${status.labResultsStatus.mostRecentHAIN.value.realSpecimenId}</td>
		<%-- <td><openmrs:formatDate date="${status.labResultsStatus.mostRecentHAIN.value.resultDate}" format="${_dateFormatDisplay}"/></td> --%>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain.form?encounterId=${status.labResultsStatus.mostRecentHAIN.value.specimenId}&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.edit" text="Editz"/></a>
		</openmrs:hasPrivilege>
		</td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentHAIN2" text="Most Recent HAIN2"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentHAIN2.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentHAIN2.link}">${status.labResultsStatus.mostRecentHAIN2.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentHAIN2.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentHAIN2.value.lab.displayString}</td>
		<td>${status.labResultsStatus.mostRecentHAIN2.value.realSpecimenId}</td>
		<%-- <td><openmrs:formatDate date="${status.labResultsStatus.mostRecentHAIN2.value.resultDate}" format="${_dateFormatDisplay}"/></td> --%>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain2.form?encounterId=${status.labResultsStatus.mostRecentHAIN2.value.specimenId}&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.edit" text="Editz"/></a>
		</openmrs:hasPrivilege>
		</td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain2.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain2.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
</table>

<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<td style="font-weight:bold" ><spring:message code="mdrtb.mostRecentDst" text="Most Recent Dst"/></td>
</tr>
</table>

<table cellspacing="0" cellpadding="0" border="2px" width="100%">
<tr>

<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentDst.value}">
		<tr>
		<td><spring:message code="mdrtb.resistant" text="Resistant"/></td>
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentDst.link}">${status.labResultsStatus.mostRecentDst.value.resistantDrugs}</mdrtb:a></td>
		<td rowspan="2"><openmrs:formatDate date="${status.labResultsStatus.mostRecentDst.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td rowspan="2">${status.labResultsStatus.mostRecentDst.value.lab.displayString}</td>
		<td rowspan="2">${status.labResultsStatus.mostRecentDst.value.realSpecimenId}</td>
		<%-- <td><openmrs:formatDate date="${status.labResultsStatus.mostRecentHAIN.value.resultDate}" format="${_dateFormatDisplay}"/></td> --%>
		<td rowspan="2"><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/dst.form?encounterId=${status.labResultsStatus.mostRecentDst.value.specimenId}&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.edit" text="Editz"/></a>
		</openmrs:hasPrivilege>
		</td>
		<td rowspan="2"><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/dst.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
		</tr>
		<tr>
		<td><spring:message code="mdrtb.sensitive" text="Sensitive"/>
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentDst.link}">${status.labResultsStatus.mostRecentDst.value.sensitiveDrugs}</mdrtb:a></td>
		</tr>
	</c:when>
	<c:otherwise>
		
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
		<td><openmrs:hasPrivilege privilege="Edit DOTS-MDR Data"><a href="${pageContext.request.contextPath}/module/mdrtb/form/dst.form?encounterId=-1&patientProgramId=${program.id}" target="_blank"><spring:message code="mdrtb.add" text="Addz"/></a>
		</openmrs:hasPrivilege>
		</td>
	</c:otherwise>
</c:choose>
</tr>
</table>
</div>

<!-- END LAB RESULTS STATUS BOX -->

<br/>

<c:if test="${! empty unlinkedlabs || ! empty unlinkedtb03s }">
<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.Unlinked Forms" text="Unlinked Forms"/>
</b>

<div class="box" style="margin:0px;">
<c:if test="${! empty unlinkedtb03s}">
  <table>
  <tr>
  	<td><spring:message code="mdrtb.formType" text="FormType"/></td>
  	<td><spring:message code="mdrtb.date" text="Datez"/></td>
  	<td><spring:message code="mdrtb.location" text="Location"/></td>
  	<td>&nbsp;</td>
  </tr>
<c:forEach var="form" items="${unlinkedtb03s}">
 <tr>
 <td><a href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${form.id}" target="_blank"><spring:message code="mdrtb.tb03" text="TB03"/></a></td>
 <td><openmrs:formatDate date="${form.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
 <td>${form.location}</td>
 <td><a href="${pageContext.request.contextPath}/module/mdrtb/program/addEncounterTb.form?encounterId=${form.id}&patientProgramId=${program.id}"><spring:message code="mdrtb.linkToProgram" text="AddToProgram"/></a>
 </tr>
  
</c:forEach>


</table>
</c:if>
<c:if test="${! empty unlinkedlabs}">
  <table>
  <tr>
  	<td><spring:message code="mdrtb.formType" text="FormType"/></td>
  	<td><spring:message code="mdrtb.date" text="Datez"/></td>
  	<td><spring:message code="mdrtb.location" text="Location"/></td>
  	<td>&nbsp;</td>
  </tr>
<c:forEach var="form" items="${unlinkedlabs}">
 <tr>
  <c:choose>
  <c:when test="${form.form.id eq xpertFormId}" >
 	<td><a href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${form.id}" target="_blank"><spring:message code="mdrtb.xpert" text="XpertForm"/></a></td>
   </c:when>
   <c:when test="${form.form.id eq smearFormId}" >
 	<td><a href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${form.id}" target="_blank"><spring:message code="mdrtb.smear" text="BacterioscopyForm"/></a></td>
   </c:when>
   <c:when test="${form.form.id eq cultureFormId}" >
 	<td><a href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${form.id}" target="_blank"><spring:message code="mdrtb.culture" text="CultureForm"/></a></td>
   </c:when>
   <c:when test="${form.form.id eq hainFormId}" >
 	<td><a href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${form.id}" target="_blank"><spring:message code="mdrtb.hain" text="HainForm"/></a></td>
   </c:when>
   <c:when test="${form.form.id eq dstFormId}" >
 	<td><a href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${form.id}" target="_blank"><spring:message code="mdrtb.dst" text="DstForm"/></a></td>
   </c:when>
   <c:otherwise>
 	<td><spring:message code="mdrtb.lab" text="labForm"/></td>
   </c:otherwise>
 </c:choose>
 <td><openmrs:formatDate date="${form.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
 <td>${form.location}</td>
 <td><a href="${pageContext.request.contextPath}/module/mdrtb/program/addEncounterTb.form?encounterId=${form.id}&patientProgramId=${program.id}"><spring:message code="mdrtb.linkToProgram" text="AddToProgram"/></a>
 </tr>
  
</c:forEach>


</table>
</c:if>
</div>
</openmrs:hasPrivilege>
</c:if>

</div>
</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>