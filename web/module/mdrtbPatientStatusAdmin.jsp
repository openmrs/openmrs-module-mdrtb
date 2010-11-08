<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script type="text/javascript">
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
</script>

<br>
<form method="post" style="font-size:85%">
<b>Patient Cleaning</b><br><Br>
<table class="portletTable" style="font-size:80%;">
	<tr>
		<td>ID</td>
		<td>Given Name</td>
		<td>Middle Name</td>
		<td>Family Name</td>
		<Td>Health Center</Td>
		<td>Program Stop Date</td>
		<td>Patient Outcome/Outcome Date</td>
		<td>Patient Status/Status Date</td>
		<td>Treatment Start Date</td>
		<td>Treatment Stop Date</td>
		<td width="500px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|</td>
		<td width="500px">|</td>
	</tr>
	<c:forEach items="${obj}" var="moo" varStatus="varStatus">
			<c:set var="rowClass" scope="page">
				<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>
			</c:set>
		<tr class="${rowClass}">
			<td>${moo.patientIdentifier}
			</td>
			<td>${moo.givenName}
			</td>
			<td>${moo.middleName}
			</td>
			<td>${moo.familyName}
			</td>
			<Td>
				<select name="healthCenter_${varStatus.index}" id="healthCenter_${varStatus.index}">
					<option value=""></option>
					<c:forEach items="${locations}" var="location" varStatus="locCount">
						<option value="${location.locationId}"
							<c:if test="${moo.healthCenter.value == location.locationId}">SELECTED</c:if>
						>${location}</option>
					</c:forEach>
				</select>
			</Td>
			<td>
				<input type="text" style="width:100px" value='<openmrs:formatDate date="${moo.patientProgram.dateCompleted}" format="${dateFormat}" />' id="patientProgramEndDate_${varStatus.index}" name="patientProgramEndDate_${varStatus.index}" onMouseDown="$(this).date_input()" class="dateType">
			</td>
			<td>
				<select name="outcome_${varStatus.index}" id="outcome_${varStatus.index}">
					<option value=""></option>
					<c:forEach items="${outcomeStates}" var="outcome" varStatus="stateCount">
						<option value="${outcome.programWorkflowStateId}"
							<c:if test="${moo.outcome.state == outcome}">SELECTED</c:if>
						>${outcome.concept.name}</option>
					</c:forEach>
				</select><br>
				<input type="text" style="width:100px" value='<openmrs:formatDate date="${moo.outcome.startDate}" format="${dateFormat}" />' id="outcomeStartDate_${varStatus.index}" name="outcomeStartDate_${varStatus.index}" onMouseDown="$(this).date_input()" class="dateType">
			</td>
			<td>
				<select name="state_${varStatus.index}" id="state_${varStatus.index}">
					<option value=""></option>
					<c:forEach items="${patientStates}" var="state" varStatus="stateCount">
						<option value="${state.programWorkflowStateId}"
							<c:if test="${moo.status.state == state}">SELECTED</c:if>
						>${state.concept.name}</option>
					</c:forEach>
				</select><br>
				<input type="text" style="width:100px" value='<openmrs:formatDate date="${moo.status.startDate}" format="${dateFormat}" />' id="stateStartDate_${varStatus.index}" name="stateStartDate_${varStatus.index}" onMouseDown="$(this).date_input()" class="dateType">
			</td>
			<td>
					<input type="text" style="width:100px" value='<openmrs:formatDate date="${moo.treatmentStartDate.valueDatetime}" format="${dateFormat}" />' id="treatmentStartDate_${varStatus.index}" name="treatmentStartDate_${varStatus.index}" onMouseDown="$(this).date_input()" class="dateType">
			</td>
			<td>
					<input type="text" style="width:100px" value='<openmrs:formatDate date="${moo.treatmentStopDate.valueDatetime}" format="${dateFormat}" />'  id="treatmentEndDate_${varStatus.index}" name="treatmentEndDate_${varStatus.index}" onMouseDown="$(this).date_input()" class="dateType">
			</td>
			<td width="500px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|</td>
			<td width="500px">|</td>
			
		</tr>		
	</c:forEach>
</table><bR><BR>
<input type="submit" value="submit" name="submit">
</form>
<Br><br>



<%@ include file="mdrtbFooter.jsp"%>