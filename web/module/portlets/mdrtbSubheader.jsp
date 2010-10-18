<%@ include file="/WEB-INF/template/include.jsp" %>

<!-- BANNER FOR DEAD PATIENTS -->
<c:if test="${model.patient.dead}">
	<div id="patientDashboardDeceased" class="retiredMessage">
		<div>
			<spring:message code="Patient.patientDeceased"/>
			<c:if test="${not empty model.patient.deathDate}">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="Person.deathDate"/>: <openmrs:formatDate date="${model.patient.deathDate}"/>
			</c:if>
			<c:if test="${not empty model.causeOfDeath}">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="Person.causeOfDeath"/>: 
				<c:if test="${not empty model.causeOfDeath.valueCoded}"> 
					  &nbsp;&nbsp;<openmrs:format concept="${obj.causeOfDeath.valueCoded}"/>
				</c:if>
				<c:if test="${not empty model.causeOfDeath.valueText}"> 
					  &nbsp;&nbsp;<c:out value="${model.causeOfDeath.valueText}"></c:out>
				</c:if>
			</c:if>
		</div>
	</div>
</c:if>

<!-- TODO create a style that merges first and active? -->
<!-- MENU TABS AND FIND PATIENT SEARCH BOX -->
<table width="100%">
<tr>
<td align="left">
	<ul id="menu">	
		<li style="border-left-width: 0px;" <c:if test='<%= request.getRequestURI().contains("dashboard") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.overview" text="Overview"/></a></li>
		
		
		<li <c:if test='<%= request.getRequestURI().contains("chart") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/chart/chart.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.chart" text="Chart"/></a></li>
		
		<li <c:if test='<%= request.getRequestURI().contains("visits") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.visits" text="Visits"/></a></li>
			
				
		<li <c:if test='<%= request.getRequestURI().contains("regimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.regimens" text="Regimens"/></a></li>
	
		
		<li <c:if test='<%= request.getRequestURI().contains("specimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.labResults" text="Lab Results"/></a></li>
		
		
	<!--
		<c:if test='<%= request.getRequestURI().contains("specimen") %>'>
			<li style="font-size:60%;" <c:if test='<%= request.getRequestURI().contains("list") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.listSpecimens" text="List Specimens"/></a></li>
			<li style="font-size:60%;" <c:if test='<%= request.getRequestURI().contains("add") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/add.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.addASpecimen" text="Add a Specimen"/></a></li>
		</c:if>
		
		-->
	
	<!-- 
		<li <c:if test='<%= request.getRequestURI().contains("contacts") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/contacts/contacts.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.contacts" text="Contacts"/></a></li>
	-->
	
	<!--  
		<li <c:if test='<%= request.getRequestURI().contains("status") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/status/status.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.status" text="Status"/></a></li>
	-->
	
	</ul>
</td>
<!-- patient search box -->
<td align="right">
	<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=mini|resultStyle=right:0|postURL=${pageContext.request.contextPath}/module/mdrtb/program/programList.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
</td>
</tr>
</table>