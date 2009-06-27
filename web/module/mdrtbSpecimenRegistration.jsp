<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbSpecimenHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbFindPatient.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script type="text/javascript">
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
    var $j = jQuery.noConflict();
	$j(document).ready(function(){
 	});
 	var numRows = 0;

 	function removePatient(patientId) {
 		$j("#patientRow"+patientId).remove();
 		numRows--;
 		if (numRows == 0) {
 			$j("#patientTable").hide();
 		}
 	}

 	function addPatient(patient) {
		var body = '';
		body += '<tr id="patientRow'+patient.patientId+'">';
		body += '<td class="patientTable"><a href="javascript:removePatient('+patient.patientId+');">[X]</a>'
		body += '<input type="hidden" name="patientId" value="'+ patient.patientId + '"/></td>';
		body += '<td class="patientTable">'+patient.identifier+'</td>';
		body += '<td class="patientTable">'+patient.personName+'</td>';
		body += '<td class="patientTable">'+patient.gender+'</td>';
		body += '<td class="patientTable">'+patient.age+'</td>';
		body += '</tr>';
 	 	var tab = $j("#patientTable").append(body);
 	 	numRows++;
 	 	$j("#patientTable").show();
 	}

 	function validateAndSubmit() {
		if (numRows == 0) {
			alert('You must add at least one patient before submitting form');
			return;
		}
		if ($j("#encounterDateField").val() == '') {
			alert('You must specify a specimen collection date before submitting form');
			return;
		}
		if ($j("#providerField").val() == '') {
			alert('You must specify a provider before submitting form');
			return;
		}
		if ($j("#location").val() == '') {
			alert('You must specify a valid location before submitting form');
			return;
		}
		$j("#registrationForm").submit();
 	}
 	 	
</script>

<style>
	th {text-align:left;}
	th.patientTable,td.patientTable {text-align:left; white-space:nowrap; padding-right:30px; border: 1px solid black;}
</style>

<div style="font-size:80%">
	<h4><spring:message code="mdrtb.specimenRegistrationTitle"/></h4>
	<spring:message code="mdrtb.specimenRegistrationInstructions"/>
	<br/><br/>
	<form id="registrationForm" method="post">
		<table>
			<tr>
				<th><spring:message code="mdrtb.sampleCollectedOn"/>:</th>
				<td><input type="text" value="" id="encounterDateField" name="encounterDate" onMouseDown="$j(this).date_input()" class="dateType"></td>
			</tr>
			<tr>
				<th><spring:message code="Encounter.provider"/>:</th>
				<td>
					<select id="providerField" name="provider">
						<option value=""></option>
						<mdrtb:forEachRecord name="user" filterList="Provider">
							<option value="${record.userId}">${record.personName}</option>
						</mdrtb:forEachRecord>
					</select>
				</td>
			</tr>
			<tr>
				<th><spring:message code="Encounter.location"/>:</th>
				<td><openmrs_tag:locationField formFieldName="location" /></td>
			</tr>
		</table>
		<br/>
		<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=mini|labelCode=mdrtb.addPatient|callback=addPatient|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>	
		<br/>
		<table id="patientTable" style="border:2px solid black; width:auto; display:none;">
			<tr>
				<th class="patientTable">&nbsp;</th>
				<th class="patientTable"><spring:message code="Patient.identifier"/></th>
				<th class="patientTable"><spring:message code="Person.name"/></th>
				<th class="patientTable"><spring:message code="Person.gender"/></th>
				<th class="patientTable"><spring:message code="Person.age"/></th>
			</tr>
		</table>
		<br/>
		<spring:message code="mdrtb.specimensToBeSentForTesting"/><br/><br/>
		<input type="button" value='<spring:message code="general.save"/>' onclick="validateAndSubmit();"/>
	</form>
</div>

<%@ include file="mdrtbFooter.jsp"%>
