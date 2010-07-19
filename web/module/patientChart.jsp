<%@ include file="/WEB-INF/template/include.jsp"%> 

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {border:1px solid #8FABC7; padding-left:2px; padding-right:2px; padding-top:2px; padding-bottom:2px; vertical-align:center}
</style>

<!-- PATIENT CHART -->
<div id="patientChart">
<table style="border:2px solid #8FABC7; font-size: .9em;">

<!-- START HEADER ROW -->

<tr>
<td>Month</td>
<td>Date collected</td>
<td>Smears</td>
<td>Cultures</td>
<td width="30px">&nbsp;</td>
<c:forEach var="drugType" items="${drugTypes}">
	<td style="vertical-align:top">${drugType.drug.name.shortName}<br/>${drugType.concentration}</td>  <!-- TODO: getShortName is depreciated? -->
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
				<td rowspan="${specimenCount}">${record.key}</td>
			</c:if>
			
			<td><a href="specimen/specimen.form?specimenId=${specimen.id}"><openmrs:formatDate date="${specimen.dateCollected}"/></a></td>
			
			<td><c:if test="${!empty specimen.smears}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
				<c:forEach var="smear" items="${specimen.smears}">	
					<mdrtb:smearCell smear="${smear}"/>
				</c:forEach>
				</tr>
				</table>
			</c:if></td> 
			
			<td><c:if test="${!empty specimen.cultures}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
				<c:forEach var="culture" items="${specimen.cultures}">	
					<mdrtb:cultureCell culture="${culture}"/>
				</c:forEach>
				</tr>
				</table>
			</c:if></td> 
			
			<td/>
				
			<!--  dsts -->
			<c:forEach var="drugType" items="${drugTypes}">
				<td style="width:30px">
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
			<td>${record.key}</td>
			<td/><td/><td/><td/>
			<c:forEach items="${drugTypes}"><td/></c:forEach>
			</tr>		
		</c:otherwise>
	</c:choose>
	
</c:forEach>

<!-- END ROWS -->

</table>
</div> 

<!-- END PATIENT CHART -->

</body>
</html>