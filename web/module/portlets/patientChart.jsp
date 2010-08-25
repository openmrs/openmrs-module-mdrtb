<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>


<!-- PATIENT CHART -->
<div id="patientChart" align="center">
<table style="border:2px solid #8FABC7; font-size: .9em;">

<!-- START HEADER ROW -->
<thead>
<tr>
<td class="chartCell"><spring:message code="mdrtb.month" text="Month"/></td>
<td class="chartCell"><spring:message code="mdrtb.dateCollected" text="Date collected"/></td>
<td class="chartCell" style="width:100px"><spring:message code="mdrtb.smears" text="Smears"/></td>
<td class="chartCell" style="width:100px"><spring:message code="mdrtb.cultures" text="Cultures"/></td>
<td class="chartCell"><spring:message code="mdrtb.bacteria" text="Bacteria"/></td>
<!--  <td class="chartCell" style="border-bottom:none;width:10px">&nbsp;</td> --> <!-- BLANK CELL -->
<c:forEach var="drugType" items="${mdrtbPatient.chart.drugTypes}">
	<td class="chartCell" style="width:30px;vertical-align:top">${drugType.name.shortName}</td>  <!-- TODO: getShortName is depreciated? -->
</c:forEach>
</tr>
</thead>

<!-- END HEADER ROW -->

<!-- START ROWS -->
<tbody>
<c:forEach var="record" items="${mdrtbPatient.chart.records}">

	<c:set var="componentCount" value="${fn:length(record.components)}"/>
	
		<c:forEach var="component" items="${record.components}" varStatus="i">
		<tr height="20">
		<c:if test="${i.count == 1}" >
			<td class="chartCell" rowspan="${componentCount}">${record.label}</td>
		</c:if>
			
		<!-- HANDLE SPECIMEN COMPONENTS -->
		<c:if test="${component.type eq 'specimenRecordComponent'}">
			<td class="chartCell">
				<c:if test="${!empty component.specimen}">
					<a href="<%= request.getContextPath() %>/module/mdrtb/specimen/specimen.form?specimenId=${component.specimen.id}"><openmrs:formatDate date="${component.specimen.dateCollected}"/></a>
				</c:if>
			</td>
			
			<td class="chartCell"><c:if test="${!empty component.specimen && !empty component.specimen.smears}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:smearCell smears="${component.specimen.smears}"/>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell"><c:if test="${!empty component.specimen && !empty component.specimen.cultures}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:cultureCell cultures="${component.specimen.cultures}"/>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell" style="font-size:60%">
			<c:if test="${!empty component.specimen && !empty component.specimen.cultures}">
				<mdrtb:germCell cultures="${component.specimen.cultures}"/>
			</c:if>
			</td>
	
			<!--  dsts -->
			<c:forEach var="drugType" items="${mdrtbPatient.chart.drugTypes}">
				<c:choose>
					<c:when test="${!empty component.specimen.dstResultsMap[drugType.id]}">
						<mdrtb:dstResultsCell dstResults="${component.specimen.dstResultsMap[drugType.id]}" drug="${drugType}" regimens="${component.regimens}"/>	
					</c:when>
					<c:otherwise>
						<!-- handle any regimen info -->
						<mdrtb:drugCell drug="${drugType}" regimens="${component.regimens}" patientId="${mdrtbPatient.patient.id}"/>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</c:if>
			
			<!-- HANDLE STATE CHANGE COMPONENTS -->
			<c:if test="${component.type eq 'stateChangeRecordComponent'}">
				<td class="chartCell"><openmrs:formatDate date="${component.date}"/></td>
				<td class="chartCell" style="background-color:lightgray;text-align:center" colspan="${fn:length(mdrtbPatient.chart.drugTypes) + 3}">${component.text}</td>
			</c:if>
			<!-- END OF HANDLING STATE CHANGE COMPONENTS -->
			</tr>
	</c:forEach>
</c:forEach>

<!-- END ROWS -->
</tbody>

</table>
</div> 

<!-- END PATIENT CHART -->