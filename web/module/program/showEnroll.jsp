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

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<!--  these are to make sure that the datepicker appears above the popup -->
<style type="text/css">
	#ui-datepicker-div { z-index: 9999; /* must be > than popup editor (950) */}
    .ui-datepicker {z-index: 9999 !important; /* must be > than popup editor (1002) */}
    td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- CUSTOM JQUERY  -->
<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#dateEnrolled').datepicker({		
			dateFormat: 'dd/mm/yy',
		 });
		
 	});
-->
</script>

<!--  DISPLAY ANY ERROR MESSAGES -->

<br/><br/>

<div align="center"> <!-- start of page div -->

<!-- PROGRAM ENROLLMENT BOX-->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.enrollment.enrollMdrtb" text="Enroll in MDR-TB Program"/></b>
<div class="box" style="margin:0px">
<form id="programEnroll" action="${pageContext.request.contextPath}/module/mdrtb/program/programEnroll.form?patientId=${patientId}&patientProgramId=-1" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>: <input id="dateEnrolled" type="text" size="14" tabindex="-1" name="dateEnrolled" />
</td></tr>

<tr><td>
<spring:message code="mdrtb.enrollment.Location" text="Enrollment Location"/>:
Not currently implemented
<!--  <select name="location">
<option value=""/>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == program.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select> -->
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

</table>
<button type="submit"><spring:message code="mdrtb.enrollment.enroll" text="Enroll in Program"/></button><button onclick=window.location='${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${patientId}'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>
<!-- END PROGRAM ENROLLMENT BOX -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>