<%@ include file="/WEB-INF/template/include.jsp"%>

<c:choose>
	<c:when test="${!empty param.patientId}">
		<openmrs:portlet url="${param.url}" id="${param.id}" moduleId="${param.moduleId}" patientId="${param.patientId}" parameters="${param.parameters}" />
	</c:when>
	<c:when test="${!empty param.patientIds}">
		<openmrs:portlet url="${param.url}" id="${param.id}" moduleId="${param.moduleId}" patientIds="${param.patientIds}" parameters="${param.parameters}" />
	</c:when>
	<c:otherwise>
		<openmrs:portlet url="${param.url}" id="${param.id}" moduleId="${param.moduleId}" parameters="${param.parameters}" />
	</c:otherwise>
</c:choose>
