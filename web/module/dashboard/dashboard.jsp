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

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#programEditPopup').dialog({
			autoOpen: false,
			modal: true,
			title: '<spring:message code="mdrtb.editProgram" text="Edit Program"/>',
			width: '30%',
			position: 'left'
		});

		$j('#programClosePopup').dialog({
			autoOpen: false,
			modal: true,
			title: '<spring:message code="mdrtb.closeProgram" text="Close Program"/>',
			width: '30%',
			position: 'left'
		});
				
		$j('#programEditButton').click(function() {
			$j('#programEditPopup').dialog('open');
		});

		$j('#programCloseButton').click(function() {
			$j('#programClosePopup').dialog('open');
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

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.programStatus" text="MDR-TB Program Status"/></b>
<div class="box" style="margin:0px">
<table cellpadding="0" cellspacing="0">
<tr><td><spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>: <openmrs:formatDate date="${program.dateEnrolled}"/></td></tr>
<tr><td><spring:message code="mdrtb.enrollment.location" text="Enrollment Location"/>: ${program.location.displayString}</td></tr>
</table>

<button id="programEditButton"><spring:message code="mdrtb.edit" text="Edit"/></button>  <button id="programCloseButton"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button>
</div>

<div id="programEditPopup">
<form id="programEdit" action="editProgram.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>: <openmrs_tag:dateField formFieldName="dateEnrolled" startValue="${program.dateEnrolled}"/>
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
</table>
<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button>
</form>
</div>

<div id="programClosePopup">

</div>

<br/>

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

<br/>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.mdrtbDiagnosis" text="MDR-TB Diagnosis"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.diagnosticSmear" text="Diagnostic Smear"/>: ${status.labResultsStatus.diagnosticSmear.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.diagnosticCulture" text="Diagnostic Culture"/>: ${status.labResultsStatus.diagnosticCulture.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.resistanceType" text="Resistance Type"/>: ${status.labResultsStatus.tbClassification.displayString}</td></tr>
<tr><td><spring:message code="mdrtb.resistanceProfile" text="Resistance Profile"/>: ${status.labResultsStatus.drugResistanceProfile.displayString}</td></tr>
</table>

</div>

<br/>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.labResultsStatus" text="Lab Results Status"/>: ${status.labResultsStatus.cultureConversion.displayString}</b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentSmear}"/><spring:message code="mdrtb.mostRecentSmear" text="Most Recent Smear"/>: ${status.labResultsStatus.mostRecentSmear.displayString}</td></tr>
<tr><td><mdrtb:flag item="${status.labResultsStatus.mostRecentCulture}"/><spring:message code="mdrtb.mostRecentCulture" text="Most Recent Culture"/>: ${status.labResultsStatus.mostRecentCulture.displayString}</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0">
<tr><td><spring:message code="mdrtb.pendingLabResults" text="Pending Lab Results"/></td></tr>
${status.labResultsStatus.pendingLabResults.displayString}
</table>

</div>

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>