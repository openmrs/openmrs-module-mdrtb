<%@ include file="/WEB-INF/template/include.jsp"%>
<h3>${param.title}</h3>
<openmrs:portlet id="summaryPortlet" moduleId="mdrtb" url="mdrtbShortSummary" patientIds="${param.patientIds}" parameters="" />

