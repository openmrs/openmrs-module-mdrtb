<%@ include file="/WEB-INF/template/include.jsp"%> 

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	table {border:4px solid black}
	td {border:1px solid black; padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:center}
</style>

<div id="patientChart">

<table>
<tr>
<td>Month</td>
<td>Date collected</td>
<td>Smears</td>
<td>Cultures</td>
<td width="20px">&nbsp;</td>
<c:forEach var="drugType" items="${drugTypes}">
<td>${drugType.drug} ${drugType.concentration}</td>
</c:forEach>
</tr>

<!-- TODO: handle prior or baseline -->

<!--  handle the main months-->
<c:forEach var="record" items="${records}">

	<c:set var="specimenCount" value="${fn:length(record.value.specimens)}"/>
	
	<c:choose>
		<c:when test="${specimenCount != 0}">
			<c:forEach var="specimen" items="${record.value.specimens}" varStatus="i">
			<tr>
			<c:if test="${i.count == 1}" >
				<td rowspan="${specimenCount}">${record.key}</td>
			</c:if>
			<td><openmrs:formatDate date="${specimen.dateCollected}"/></td>
			<td><c:if test="${!empty specimen.smears}">${specimen.smears[0].result}</c:if></td>  <!-- TODO: obviously we need to handle multiple smears -->
			<td><c:if test="${!empty specimen.cultures}">${specimen.cultures[0].result}</c:if></td>
			<td/>
			<c:if test="${!empty specimen.dsts}">
				<c:set var="resultsMap" value="${specimen.dsts[0].resultsMap}"/>
			</c:if>
			<c:forEach var="drugType" items="${drugTypes}">
				<td><c:if test="${!empty resultsMap && !empty resultsMap[drugType.key] && !empty resultsMap[drugType.key].result}">${resultsMap[drugType.key].result}</c:if></td>
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


</table>

</div> <!-- end patientChart div -->

</body>
</html>