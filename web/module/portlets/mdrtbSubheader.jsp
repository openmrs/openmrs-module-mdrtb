<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){
		$j('#programSelector').change(function(){
			// test for the add option--i.e., if the selector has no value	
			if ($j(this).val() == -1) {
				$j(window).attr('location','${pageContext.request.contextPath}/module/mdrtb/program/showEnroll.form?patientId=${model.patient.patientId}');
			}
			else {
				// reload the proper page when the selector changes
				$j(window).attr('location','?patientId=${model.patient.patientId}&patientProgramId=' + $j(this).val());
			}
		});
	});		
	
-->
</script>

<!-- BANNER FOR DEAD PATIENTS -->
<c:if test="${model.patient.dead}">
	<div id="patientDashboardDeceased" class="retiredMessage">
		<div>
			<spring:message code="Patient.patientDeceased"/>
			<c:if test="${not empty model.patient.deathDate}">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="Person.deathDate"/>: <openmrs:formatDate date="${model.patient.deathDate}" format="${_dateFormatDisplay}"/>
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

<!-- PROGRAM SELECTION AND FIND PATIENT SEARCH BOX -->
<table width="100%">
<tr>
<td align="left">
&nbsp;<spring:message code="mdrtb.currentlyViewing" text="Currently viewing"/>:
<select id="programSelector">
<c:forEach var="program" items="${model.patientPrograms}">
	<c:choose>
		<c:when test="${program.active}">
			<option value="${program.id}" <c:if test="${program.id == patientProgramId}">selected</c:if> ><spring:message code="mdrtb.activeMdrtbProgramSelector" text="Active MDR-TB Program, started on"/> <openmrs:formatDate date="${program.dateEnrolled}" format="${_dateFormatDisplay}"/> <c:if test="${!empty program.location}"> <spring:message code="mdrtb.at" text="at"/> ${program.location}</c:if></option>
		</c:when>
		<c:otherwise>
			<option value="${program.id}" <c:if test="${program.id == patientProgramId}">selected</c:if> ><spring:message code="mdrtb.closedMdrtbProgramSelector" text="MDR-TB program from"/> <openmrs:formatDate date="${program.dateEnrolled}" format="${_dateFormatDisplay}"/> <spring:message code="mdrtb.to" text="to"/> <openmrs:formatDate date="${program.dateCompleted}" format="${_dateFormatDisplay}"/><c:if test="${!empty program.location}"> <spring:message code="mdrtb.at" text="at"/> ${program.location}</c:if></option>
		</c:otherwise>
	</c:choose>
</c:forEach>
<option value="-1"><spring:message code="mdrtb.addNewMdrtbProgram" text="Add new MDR-TB program"/></option>
</select>
</td>
<!-- patient search box -->
<td align="right">
	<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=mini|resultStyle=right:0|postURL=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
</td>
</tr>
</table>


<!-- SUB MENU ITEMS -->
<!-- TODO create a style that merges first and active? -->
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
			
				
		<li <c:if test='<%= request.getRequestURI().contains("/regimen/regimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.regimens" text="Regimens"/></a></li>

		<li <c:if test='<%= request.getRequestURI().contains("manageRegimens") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/regimen/manageRegimens.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.drugOrders" text="Drug Orders"/></a></li>	
		
		<li <c:if test='<%= request.getRequestURI().contains("specimen") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.labResults" text="Lab Results"/></a></li>
	
		<li <c:if test='<%= request.getRequestURI().contains("mdrtbEditPatient") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbEditPatient.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.patientDetails" text="Patient Details"/></a></li>
	
	</ul>
</td>
</tr>
</table>