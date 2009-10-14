<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>
<style><%@ include file="resources/mdrtb.css"%></style>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/mdrtb/mdrtbListPatients.form" />

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<script type="text/javascript">
	var $j = jQuery.noConflict(); 
</script>

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
		
		$j('#patientTable').dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		});

		$j(document).ready(function() { 
			$j('#displayDetailsPopup').dialog({ 
				title: 'dynamic', 
				autoOpen: false, 
				draggable: false, 
				resizable: false, 
				width: '95%', 
				modal: true, 
				open: function(a, b) { $j('#displayDetailsPopupLoading').show(); } 
			});
			$j("#displayDetailsPopupIframe").load(function() { $j('#displayDetailsPopupLoading').hide(); });
		});
		
	});

	function loadUrlIntoDetailsPopup(title, urlToLoad) { 
		$j("#displayDetailsPopupIframe").attr("src", urlToLoad); 
		$j('#displayDetailsPopup').dialog('option', 'title', title).dialog('option', 'height', $j(window).height() - 50).dialog('open'); 
	}

</script>

<style>
	th {text-align:left;}
	th.patientTable,td.patientTable {text-align:left; white-space:nowrap; border: 1px solid black; font-size:small; padding-left:5px; padding-right:5px;}
</style>

<openmrs:globalProperty key="mdrtb.listPatientsLocationMethod" var="locationMethod" defaultValue="PATIENT_HEALTH_CENTER"/>
<openmrs:globalProperty key="mdrtb.location_list" var="locationList"/>
<openmrs:globalProperty key="mdrtb.program_name" var="mdrProgram"/>

<table class="patientTable">
	<tr>
		<td valign="top" class="patientTable">
			<form method="get">
				<input type="hidden" name="locationMethod" value="${locationMethod}"/>
				<table>
					<tr><td colspan="2" align="right"><input type="submit" value="<spring:message code="general.search"/>"/></td></tr>
					<tr><td colspan="2">
						<table>
							<tr>
								<th><spring:message code="general.name"/>&nbsp;</th>
								<td><input type="text" name="name" value="${name}"/></td>
							</tr>
							<tr>
								<th><spring:message code="general.id"/>&nbsp;</th>
								<td><input type="text" name="identifier" value="${identifier}"/></td>
							</tr>
						</table>
						<br/>
					</td></tr>

					<tr><th colspan="2"><spring:message code="mdrtb.locationMethod.${locationMethod}"/></th></tr>
					<mdrtb:forEachRecord name="location" filterList="${locationList}">
						<tr>
							<td><input type="checkbox" name="locations" value="${record.locationId}"<c:if test="${mdrtb:collectionContains(locations, record)}"> checked</c:if>/>&nbsp;</td>
							<td>${record.name}</td>
						</tr>
					</mdrtb:forEachRecord>
					
					<mdrtb:forEachRecord name="workflow" programName="${mdrProgram}">
						
						<tr><th colspan="2">
							<br/><mdrtb:concept concept="${record.concept}" nameVar="n" mappingVar="m">
								<spring:message code="mdrtb.patientstatusworkflow.${m}"/>
							</mdrtb:concept>
						</th></tr>
						
						<mdrtb:forEachRecord name="state" programName="${mdrProgram}" workflowNames="${record.concept.name}">
							<tr>
								<td><input type="checkbox" name="states" value="${record.programWorkflowStateId}"<c:if test="${mdrtb:collectionContains(states, record)}"> checked</c:if>/>&nbsp;</td>
								<td><mdrtb:concept concept="${record.concept}" nameVar="n" mappingVar="m">${n.name}</mdrtb:concept><br/></td>
							</tr>
						</mdrtb:forEachRecord>
					</mdrtb:forEachRecord>
				</table>
			</form>
		</td>
		<td valign="top" class="patientTable" width="100%" style="padding-left:10px;">
			<table id="patientTable">
				<thead>
					<tr>
						<th>&nbsp;</th>
						<th class="patientTable"><spring:message code="general.id"/></th>
						<th class="patientTable"><spring:message code="general.name"/></th>
						<th class="patientTable"><spring:message code="Person.age"/></th>
						<th class="patientTable"><spring:message code="Person.gender"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${patients}" var="p">
						<tr class="patientRow patientRow${p.patientId}">
							<td class="patientTable" style="white-space:nowrap; width:20px;">
								<a href="mdrtbPatientOverview.form?patientId=${p.patientId}">
									<img src="${pageContext.request.contextPath}/images/lookup.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
								</a>						
								<openmrs:extensionPoint pointId="org.openmrs.mdrtb.listPatientDetailPortlets" type="html">
									<a href="javascript:void(0)" onClick="loadUrlIntoDetailsPopup('Test ', '${pageContext.request.contextPath}/module/mdrtb/viewPortlet.htm?patientId=${p.patientId}&moduleId=pihhaiti&id=${extension.moduleId}&url=${extension.portletUrl}'); return false;">
										<img src="${pageContext.request.contextPath}/images/file.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
									</a>
								</openmrs:extensionPoint>
							</td>
							<td class="patientTable" style="white-space:nowrap;">${p.patientIdentifier.identifier}</td>
							<td class="patientTable" >${p.familyName}, ${p.givenName} ${p.middleName}</td>
							<td class="patientTable" style="white-space:nowrap;">${p.age}</td>
							<td class="patientTable" style="white-space:nowrap;">${p.gender}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</td>
	</tr>
</table>

<div id="displayDetailsPopup"> 
	<div id="displayDetailsPopupLoading"><spring:message code="general.loading"/></div>
	<iframe id="displayDetailsPopupIframe" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe> 
</div>

<%@ include file="mdrtbFooter.jsp"%>
