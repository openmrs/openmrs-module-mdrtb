<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.chartCell {border:1px solid #8FABC7; margin:0px; padding-left:2px; padding-right:2px; padding-top:2px; padding-bottom:2px; vertical-align:center}
</style>

<!-- CUSTOM JQUERY  -->
<script type="text/javascript">

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		// add the custom tooltip
	    $j("td").tooltip();

	 	// event handlers to hide and show summary edit box
		$j('#editSummary').click(function(){
			$j('#summary').hide();  // hide the specimen details box
			$j('#edit_summary').show();  // show the edit speciment box
		});

		$j('#cancelSummary').click(function(){
			$j('#edit_summary').hide();  // hide the edit specimen box
			$j('#summary').show();  // show the specimen details box
		});

    });
</script>

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/>
	</c:forEach>
	<br/>
</c:if>

<div align="center"> <!-- start of page div -->

<!-- PATIENT SUMMARY -->

<!-- PATIENT SUMMARY - VIEW -->

<div id="summary">

<b class="boxHeader" style="margin:0px">Patient Overview<span style="position: absolute; right:25px;"><a href="#" id="editSummary">edit</a></span></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">
<tr>
<td>Patient Enrollment Date:</td>
<td>
<c:forEach var="program" items="${mdrtbPatient.mdrtbPrograms}">
	<openmrs:formatDate date="${program.dateEnrolled}"/><br/>
</c:forEach>
</td>
</tr>
</table>

<div align="center">
<a href="<%= request.getContextPath() %>/patientDashboard.form?patientId=${patientId}">View Main Patient Dashboard</a> |
<a href="<%= request.getContextPath() %>/admin/patients/newPatient.form?patientId=${patientId}">Edit Patient Information (Short Form)</a> |
<a href="<%= request.getContextPath() %>/admin/patients/patient.form?patientId=${patientId}">Edit Patient Information (Long Form)</a>
</div>

</div>

</div>

<!-- END PATIENT SUMMARY - VIEW -->

<!-- PATIENT SUMMARY - EDIT -->
<div id="edit_summary" style="display:none">

<b class="boxHeader" style="margin:0px">Patient Overview</b>
<div class="box" style="margin:0px">
<form name="mdrtbPatient" action="summary.form?patientId=${patientId}" method="post">
<table cellspacing="0" cellpadding="0">
<tr>
<td>Patient Enrollment Date:</td>
<td>
<c:forEach var="program" items="${mdrtbPatient.mdrtbPrograms}" varStatus="i">
	<openmrs_tag:dateField formFieldName="mdrtbPrograms[${i.count - 1}].dateEnrolled" startValue="${program.dateEnrolled}"/><br/>
</c:forEach>
</td>
</tr>
</table>
<button type="submit">Save</button><button id="cancelSummary" type="reset">Cancel</button>
</form>

</div>

</div>

<!-- END PATIENT SUMMARY - EDIT -->

<br/><br/>

<!-- END PATIENT SUMMARY -->

<!-- PATIENT CHART -->
<div id="patientChart" align="center">
<table style="border:2px solid #8FABC7; font-size: .9em;">

<!-- START HEADER ROW -->
<thead>
<tr>
<td class="chartCell">Month</td>
<td class="chartCell">Date collected</td>
<td class="chartCell" style="width:100px">Smears</td>
<td class="chartCell" style="width:100px">Cultures</td>
<td class="chartCell">Germ</td>
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

	<c:set var="specimenCount" value="${fn:length(record.value.specimens)}"/>
	
	<c:choose>
		<c:when test="${specimenCount != 0}">
			<c:forEach var="specimen" items="${record.value.specimens}" varStatus="i">
			<tr>
			<c:if test="${i.count == 1}" >
				<td class="chartCell" rowspan="${specimenCount}">${record.key}</td>
			</c:if>
			
			<td class="chartCell"><a href="<%= request.getContextPath() %>/module/mdrtb/specimen/specimen.form?specimenId=${specimen.id}"><openmrs:formatDate date="${specimen.dateCollected}"/></a></td>
			
			<td class="chartCell"><c:if test="${!empty specimen.smears}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:smearCell smears="${specimen.smears}"/>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell"><c:if test="${!empty specimen.cultures}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:cultureCell cultures="${specimen.cultures}"/>
				</tr>
				</table>
			</c:if></td> 
			
			<td class="chartCell" style="font-size:60%">
				<mdrtb:germCell cultures="${specimen.cultures}"/>
			</td>
			
		<!--  	<td class="chartCell" style="border-top:none;border-bottom:none"/>  --> <!-- BLANK COLUMN -->
				
			<!--  dsts -->
			<c:forEach var="drugType" items="${mdrtbPatient.chart.drugTypes}">
				<td class="chartCell"><c:if test="${!empty specimen.dstResultsMap[drugType.id]}">
				<table style="padding:0px; border:0px; margin0px; width:100%">
				<tr>
					<mdrtb:dstResultsCell dstResults="${specimen.dstResultsMap[drugType.id]}"/>	
				</tr>
				</table>
				</c:if></td>
			</c:forEach>
			
			</tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr>
			<td class="chartCell">${record.key}</td>
			<td class="chartCell"/><td class="chartCell"/><td class="chartCell"/><td class="chartCell"/>
			<!-- <td class="chartCell" style="border-top:none;border-bottom:none"/> -->  <!-- BLANK COLUMN -->
			<c:forEach items="${mdrtbPatient.chart.drugTypes}"><td class="chartCell"/></c:forEach>
			</tr>		
		</c:otherwise>
	</c:choose>
	
</c:forEach>

<!-- END ROWS -->
</tbody>

</table>
</div> 

<!-- END PATIENT CHART -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>