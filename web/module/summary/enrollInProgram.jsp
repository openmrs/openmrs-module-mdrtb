<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patientId}"/>

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/>
	</c:forEach>
	<br/>
</c:if>

<!-- ENROLLMENT DIV -->
<div align="center" id="enroll">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.enrollment" text="Program Enrollment"/></b>
<div class="box" style="margin:0px">

<form action="summary.form?patientId=${patientId}" method="post">
MDR-TB Program Enrollment Date: <openmrs_tag:dateField formFieldName="dateEnrolled" startValue=""/>
<button type="submit"><spring:message code="mdrtb.enroll" text="Enroll"/></button>
</form>

</div>

</div>