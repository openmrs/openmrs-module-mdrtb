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

<!-- CUSTOM JQUERY  -->
<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#programEnrollPopup').dialog({
			autoOpen: false,
			modal: true,
			title: '<spring:message code="mdrtb.enrollmentEnroll" text="Enroll in Program"/>',
			width: '30%',
			position: 'left'
		});
				
		$j('#programEnrollButton').click(function() {
			$j('#programEnrollPopup').dialog('open');
		});

 	});
-->
</script>

<!--  DISPLAY ANY ERROR MESSAGES -->

<br/><br/>

<div align="center"> <!-- start of page div -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.activeProgram" text="Active MDR-TB Program"/></b>
<div class="box" style="margin:0px">
<c:choose>
<c:when test="${programs[0].active}">
<spring:message code="mdrtb.enrollment.enrolledOn" text="Enrolled on" /> <openmrs:formatDate date="${programs[0].dateEnrolled}"/>
<a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${programs[0].id}&patientId=${patientId}"><spring:message code="mdrtb.view" text="View"/></a>
</c:when>
<c:otherwise>
<spring:message code="mdrtb.enrollment.notEnrolled" text="Not currently enrolled" /> <button id="programEnrollButton"><spring:message code="mdrtb.enrollment.enroll" text="Enroll in Program" /></button>
</c:otherwise>
</c:choose>
</div>

<!-- pop up for enrolling in a program -->
<div id="programEnrollPopup">
<form id="programEnroll" action="${pageContext.request.contextPath}/module/mdrtb/program/programEnroll.form?patientId=${patient.id}" method="post" >
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
<button type="submit"><spring:message code="mdrtb.enrollment.enroll" text="Enroll in Program"/></button>
</form>
</div>

<br/>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.previousProrgams" text="Previous MDR-TB Programs"/></b>
<div class="box" style="margin:0px">
<table cellspacing="5" cellpadding="5" border="3">
<tr>
<td><spring:message code="mdrtb.startdate" text="Start Date" /></td>
<td><spring:message code="mdrtb.endDate" text="End Date" /></td>
<td><spring:message code="mdrtb.location" text="Location" /></td>
<td><spring:message code="mdrtb.outcome" text="outcome" /></td>
<td><spring:message code="mdrtb.view" text="view" /></td>
</tr>

<c:forEach var="program" items="${programs}"  varStatus="iteration">
<c:if test="${!program.active || iteration.count > 1}">
<tr>
<td><openmrs:formatDate date="${program.dateEnrolled}"/></td>
<td><openmrs:formatDate date="${program.dateCompleted}"/></td>
<td>${program.location.displayString}</td>
<td>${!empty program.outcome ? program.outcome.displayString : '&nbsp;'}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${program.id}&patientId=${patientId}">
<spring:message code="mdrtb.view" text="view" /></a></td>
</tr>
</c:if>
</c:forEach>

</table>

</div>

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>