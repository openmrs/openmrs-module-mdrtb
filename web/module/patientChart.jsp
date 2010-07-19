<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	.chartCell {border:1px solid #8FABC7; padding-left:2px; padding-right:2px; padding-top:2px; padding-bottom:2px; vertical-align:center}
</style>

<!-- PATIENT CHART -->
<div id="patientChart" align="center">
<table style="border:2px solid #8FABC7; font-size: .9em;">

<!-- START HEADER ROW -->

<tr>
<td class="chartCell">Month</td>
<td class="chartCell">Date collected</td>
<td class="chartCell">Smears</td>
<td class="chartCell">Cultures</td>
<td class="chartCell" width="30px">&nbsp;</td>
<c:forEach var="drugType" items="${drugTypes}">
	<td class="chartCell" style="vertical-align:top">${drugType.drug.name.shortName}<br/>${drugType.concentration}</td>  <!-- TODO: getShortName is depreciated? -->
</c:forEach>
</tr>

<!-- END HEADER ROW -->

<!-- START ROWS -->

<c:forEach var="record" items="${records}">

	<c:set var="specimenCount" value="${fn:length(record.value.specimens)}"/>
	
	<c:choose>
		<c:when test="${specimenCount != 0}">
			<c:forEach var="specimen" items="${record.value.specimens}" varStatus="i">
			<tr>
			<c:if test="${i.count == 1}" >
				<td class="chartCell" rowspan="${specimenCount}">${record.key}</td>
			</c:if>
			
			<td class="chartCell"><a href="specimen/specimen.form?specimenId=${specimen.id}"><openmrs:formatDate date="${specimen.dateCollected}"/></a></td>
			
			<td class="chartCell"><c:if test="${!empty specimen.smears}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
				<c:forEach var="smear" items="${specimen.smears}">	
					<mdrtb:smearCell smear="${smear}"/>
				</c:forEach>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell"><c:if test="${!empty specimen.cultures}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
				<c:forEach var="culture" items="${specimen.cultures}">	
					<mdrtb:cultureCell culture="${culture}"/>
				</c:forEach>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell"/>
				
			<!--  dsts -->
			<c:forEach var="drugType" items="${drugTypes}">
				<td class="chartCell" style="width:30px">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<c:forEach var="dst" items="${specimen.dsts}">
						<mdrtb:dstResultCell dstResult="${dst.resultsMap[drugType.key]}"/>
					</c:forEach>
				</tr>
				</table>
				</td>	
			</c:forEach>
			
			</tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr>
			<td class="chartCell">${record.key}</td>
			<td class="chartCell"/><td class="chartCell"/><td class="chartCell"/><td class="chartCell"/>
			<c:forEach items="${drugTypes}"><td class="chartCell"/></c:forEach>
			</tr>		
		</c:otherwise>
	</c:choose>
	
</c:forEach>

<!-- END ROWS -->

</table>
</div> 

<!-- END PATIENT CHART -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>