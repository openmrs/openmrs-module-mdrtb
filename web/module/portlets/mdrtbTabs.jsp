<%@ include file="/WEB-INF/template/include.jsp" %>

<!-- TODO create a style that merges first and active? -->
<div align="left">
	<ul id="menu">	
		<li style="border-left-width: 0px;" <c:if test='<%= request.getRequestURI().contains("summary") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/summary/summary.form?patientId=${model.patient.patientId}">Overview</a></li>
		
		<li <c:if test='<%= request.getRequestURI().contains("visits") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${model.patient.patientId}">Visits</a></li>
		
		<li <c:if test='<%= request.getRequestURI().contains("specimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/list.form?patientId=${model.patient.patientId}">Specimens</a></li>
		
		<c:if test='<%= request.getRequestURI().contains("specimen") %>'>
			<li style="font-size:60%;" <c:if test='<%= request.getRequestURI().contains("list") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/list.form?patientId=${model.patient.patientId}">List Specimens</a></li>
			<li style="font-size:60%;" <c:if test='<%= request.getRequestURI().contains("add") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/add.form?patientId=${model.patient.patientId}">Add a Specimen</a></li>
		</c:if>
			
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