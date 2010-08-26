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
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.chartCell {border:1px solid #8FABC7; margin:0px; padding-left:2px; padding-right:2px; padding-top:2px; padding-bottom:2px; vertical-align:center}
</style>

<!-- CUSTOM JQUERY  -->
<script type="text/javascript">

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		// add the custom tooltip
	    $j("td").tooltip();

	 	// event handlers to hide and show summary edit box
		$j('#editSummary').click(function(){
			$j('#summary').hide();  // hide the specimen details box
			$j('#edit_summary').show();  // show the edit speciment box
		});

		$j('#cancelSummary').click(function(){
			$j('#edit_summary').hide();  // hide the edit specimen box
			$j('#summary').show();  // show the specimen details box
		});

    });
</script>

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/>
	</c:forEach>
	<br/>
</c:if>

<div align="center"> <!-- start of page div -->

<!-- PATIENT SUMMARY -->

<!-- PATIENT SUMMARY - VIEW -->

<div id="summary">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.patientOverview" text="Patient Overview"/><span style="position: absolute; right:25px;"><a href="#" id="editSummary"><spring:message code="mdrtb.edit" text="edit"/></a></span></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">
<tr>
<td><spring:message code="mdrtb.patientEnrollmentDate" text="Patient Enrollment Date"/>:</td>
<td>
<c:forEach var="program" items="${mdrtbPatient.mdrtbPrograms}">
	<openmrs:formatDate date="${program.dateEnrolled}"/><br/>
</c:forEach>
</td>
</tr>
</table>

<div align="center">
<a href="<%= request.getContextPath() %>/patientDashboard.form?patientId=${patientId}"><spring:message code="mdrtb.viewMainPatientDashboard" text="View Main Patient Dashboard"/></a> |
<a href="<%= request.getContextPath() %>/admin/patients/newPatient.form?patientId=${patientId}"><spring:message code="mdrtb.editPatientInformationShortForm" text="Edit Patient Information (Short Form)"/></a> |
<a href="<%= request.getContextPath() %>/admin/patients/patient.form?patientId=${patientId}"><spring:message code="mdrtb.editPatientInformationLongForm" text="Edit Patient Information (Long Form)"/></a>
</div>

</div>

</div>

<!-- END PATIENT SUMMARY - VIEW -->

<!-- PATIENT SUMMARY - EDIT -->
<div id="edit_summary" style="display:none">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.patientOverview" text="Patient Overview"/></b>
<div class="box" style="margin:0px">
<form name="mdrtbPatient" action="summary.form?patientId=${patientId}" method="post">
<table cellspacing="0" cellpadding="0">
<tr>
<td><spring:message code="mdrtb.patientEnrollmentDate" text="Patient Enrollment Date"/>:</td>
<td>
<c:forEach var="program" items="${mdrtbPatient.mdrtbPrograms}" varStatus="i">
	<openmrs_tag:dateField formFieldName="mdrtbPrograms[${i.count - 1}].dateEnrolled" startValue="${program.dateEnrolled}"/><br/>
</c:forEach>
</td>
</tr>
</table>
<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button id="cancelSummary" type="reset"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>

</div>

</div>

<!-- END PATIENT SUMMARY - EDIT -->

<br/><br/>

<!-- END PATIENT SUMMARY -->

<!-- PATIENT CHART -->
<openmrs:portlet url="patientChart" id="patientChart" moduleId="mdrtb" />

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>