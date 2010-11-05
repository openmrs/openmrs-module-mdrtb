<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.chartCell {border:1px solid #8FABC7; margin:0px; padding-left:2px; padding-right:2px; padding-top:2px; padding-bottom:2px; vertical-align:center}
	.spacerCell {border:0px; padding:0px; width:1px; background-color: #8FABC7}
</style>


<!-- PATIENT CHART -->
<div id="patientChart" align="center">
<table style="border:2px solid #8FABC7; font-size: .9em; border-spacing:0px;">

<!-- START HEADER ROWS -->
<tr>
<td class="chartCell" colspan="3">&nbsp;</td>
<td class="spacerCell"></td>
<td class="chartCell" colspan="3" align="center"><spring:message code="mdrtb.bacteriologies" text="Bacteriologies"/></td3>
<td class="spacerCell"></td>
<td class="chartCell" colspan="${fn:length(model.chart.drugTypes)}" align="center"><spring:message code="mdrtb.dsts" text="DSTs"/></td>
</tr>

<tr>
<td class="chartCell"><spring:message code="mdrtb.month" text="Month"/></td>
<td class="chartCell"><spring:message code="mdrtb.dateCollected" text="Date collected"/></td>
<td class="chartCell"><spring:message code="mdrtb.lab" text="Lab"/></td>
<td class="spacerCell"></td>
<td class="chartCell" style="width:60px"><spring:message code="mdrtb.smears" text="Smears"/></td>
<td class="chartCell" style="width:60px"><spring:message code="mdrtb.cultures" text="Cultures"/></td>
<td class="chartCell"><spring:message code="mdrtb.bacteria" text="Bacteria"/></td>
<td class="spacerCell"></td>
<!--  <td class="chartCell" style="border-bottom:none;width:10px">&nbsp;</td> --> <!-- BLANK CELL -->
<c:forEach var="drugType" items="${model.chart.drugTypes}">
	<td class="chartCell" style="width:30px;vertical-align:top">${drugType.name.shortName}</td>  <!-- TODO: getShortName is depreciated in 1.8, change to drugType.shortNames[0].name? -->
</c:forEach>
</tr>
<!-- END HEADER ROWS -->

<!-- START ROWS -->
<tbody>
<c:forEach var="record" items="${model.chart.records}">

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
					<a href="<%= request.getContextPath() %>/module/mdrtb/specimen/specimen.form?specimenId=${component.specimen.id}"><openmrs:formatDate date="${component.specimen.dateCollected}" format="${_dateFormatDisplay}"/></a>
				</c:if>
			</td>
			
			<td class="chartCell"><nobr>
				<c:if test="${!empty component.specimen}">
					<mdrtb:labCell specimen="${component.specimen}"/>&nbsp;
				</c:if>
			</nobr></td>
			
			
			<td class="spacerCell">&nbsp;</td>
			
			<td class="chartCell"><c:if test="${!empty component.specimen && !empty component.specimen.smears}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:smearCell smears="${component.specimen.smears}" parameters="&patientProgramId=${patientProgramId}" style="text-align:center;font-style:bold;padding:0px;border:0px;margin:0px;"/>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell"><c:if test="${!empty component.specimen && !empty component.specimen.cultures}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:cultureCell cultures="${component.specimen.cultures}" parameters="&patientProgramId=${patientProgramId}" style="text-align:center;font-style:bold;padding:0px;border:0px;margin:0px;"/>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell" style="font-size:60%">
			<c:if test="${!empty component.specimen && !empty component.specimen.cultures}">
				<mdrtb:germCell cultures="${component.specimen.cultures}"/>
			</c:if>
			</td>
	
			<td class="spacerCell">&nbsp;</td>
	
			<!--  dsts -->
			<c:forEach var="drugType" items="${model.chart.drugTypes}">
				<c:choose>
					<c:when test="${!empty component.specimen.dstResultsMap[drugType.id]}">
						<mdrtb:dstResultsCell dstResults="${component.specimen.dstResultsMap[drugType.id]}" drug="${drugType}" regimens="${component.regimens}" style="width:30px;padding:0px;border:0px;margin:0px;text-align:center;"/>	
					</c:when>
					<c:otherwise>
						<!-- handle any regimen info -->
						<mdrtb:drugCell drug="${drugType}" regimens="${component.regimens}" style=""/>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</c:if>
			
			<!-- HANDLE STATE CHANGE COMPONENTS -->
			<c:if test="${component.type eq 'stateChangeRecordComponent'}">
				<td class="chartCell"><openmrs:formatDate date="${component.date}" format="${_dateFormatDisplay}"/></td>
				<td class="chartCell"/>
				<td class="spacerCell">&nbsp;</td>
				<td class="chartCell" style="background-color:lightgray;text-align:center" colspan="3">${component.text}</td>
				<td class="spacerCell">&nbsp;</td>
				<td class="chartCell" style="background-color:lightgray;text-align:center" colspan="${fn:length(model.chart.drugTypes)}">${component.text}</td>
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