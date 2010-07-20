<%@ include file="/WEB-INF/template/include.jsp" %>

<div align="left">
	<ul id="menu">	
		<li class="first">
		<a <c:if test='<%= request.getRequestURI().contains("summary") %>'>class="active"</c:if> href="${pageContext.request.contextPath}/module/mdrtb/summary/summary.form?patientId=${model.patient.patientId}">Overview</a></li>
		
		<li <c:if test='<%= request.getRequestURI().contains("visits") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${model.patient.patientId}">Visits</a></li>
		
		<li <c:if test='<%= request.getRequestURI().contains("specimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/list.form?patientId=${model.patient.patientId}">Specimens</a></li>
			
		<li <c:if test='<%= request.getRequestURI().contains("regimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${model.patient.patientId}">Regimen</a></li>
	
	<!-- 
		<li <c:if test='<%= request.getRequestURI().contains("contacts") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/contacts/contacts.form?patientId=${model.patient.patientId}">Contacts</a></li>
	-->
	
		<li <c:if test='<%= request.getRequestURI().contains("status") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/status/status.form?patientId=${model.patient.patientId}">Status</a></li>
	
	</ul>
</div>