<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.specimenReports"/></b>
<div class="box" style="margin:0px;">

<form action="specimenReportsOverview.form" method="POST">
<table cellpadding="0" cellspacing="0" border="0">
<tr>
<td>Report on specimens collected between <openmrs_tag:dateField formFieldName="startDateCollected" startValue="${query.startDateCollected}"/> and <openmrs_tag:dateField formFieldName="endDateCollected" startValue="${query.endDateCollected}"/></td>
</tr>
<tr>
<td>Show specimens with positive smear results more than <input type="text" size="5" name="daysSinceSmear" value="${query.daysSinceSmear}"/> days old, but no culture results</td>
</tr>
<tr>
<td>Show specimens with positive culture results more than <input type="text" size="5" name="daysSinceCulture" value="${query.daysSinceCulture}"/> days old, but no DST results</td>
</tr>
<tr>
<td>Show specimens tested at:
<select name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == query.lab}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>
</td>
</tr>
<tr>
<td><button type="submit"><spring:message code="mdrtb.submitQuery"/></button></td>
<td>&nbsp;</td>
</tr>
</table>
</form>

</div>

<br/><br/>

<!-- START LEFT-HAND COLUMN -->
<div id="leftColumn" style="float: left; width:49%;  padding:0px 4px 4px 4px">

<openmrs:portlet url="specimenReport" id="specimenReport" moduleId="mdrtb" parameters="report=specimensWithPositiveSmearButNoCultureResults|lab=${query.lab.id}|startDateCollected=${query.startDateCollectedAsString}|endDateCollected=${query.endDateCollectedAsString}|daysSinceSmear=${query.daysSinceSmear}" />  
 

<br/><br/>

<openmrs:portlet url="specimenReport" id="specimenReport" moduleId="mdrtb" parameters="report=specimensWithPositiveCultureButNoDstResults|lab=${query.lab.id}|startDateCollected=${query.startDateCollectedAsString}|endDateCollected=${query.endDateCollectedAsString}|daysSinceCulture=${query.daysSinceCulture}" />  
 

</div>


<!--  START RIGHT COLUMN -->

<div id="rightColumn" style="float:right; width:49%; padding:0px 4px 4px 4px">

<openmrs:portlet url="specimenReport" id="specimenReport" moduleId="mdrtb" parameters="report=specimensWithContaminatedTest|lab=${query.lab.id}|startDateCollected=${query.startDateCollectedAsString}|endDateCollected=${query.endDateCollectedAsString}" />  

<br/><br/>

<openmrs:portlet url="specimenReport" id="specimenReport" moduleId="mdrtb" parameters="report=specimensWithNoResults|lab=${query.lab.id}|startDateCollected=${query.startDateCollectedAsString}|endDateCollected=${query.endDateCollectedAsString}" /> 

</div>



<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>