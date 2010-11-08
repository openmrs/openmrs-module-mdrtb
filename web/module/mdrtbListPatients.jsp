<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
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
			"iDisplayLength": 20,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		});

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

<form method="get">

	<table class="patientTable">
		<tr>
			<td valign="top" class="patientTable">
				<input type="hidden" name="locationMethod" value="${locationMethod}"/>
				<table>
					<tr style="border-bottom:2px solid black;"><td colspan="2" style="background-color:#C0C0C0; font-weight:bold;">
						<table width="100%"><tr>
							<td style="width:100%;">
								<spring:message code="mdrtb.display"/>: 
								<select name="displayMode">
									<option value="basic"<c:if test="${'basic' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.basicDetails"/></option>
									<option value="mdrtbShortSummary"<c:if test="${'mdrtbShortSummary' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.mdrtbShortSummary"/></option>
									<option value="mdrtbSummary"<c:if test="${'mdrtbSummary' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.mdrtbSummary"/></option>
									<option value="mdrtbCustomList"<c:if test="${'mdrtbCustomList' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.mdrtbCustomList"/></option>
									
									<openmrs:extensionPoint pointId="org.openmrs.mdrtb.listPatientDisplayModes" type="html">
										<option value="${extension.key}"<c:if test="${extension.key == param.displayMode}"> selected</c:if>>
											<spring:message code="${extension.label}"/>
										</option>
									</openmrs:extensionPoint>
								</select>
							</td>
							<td align="right" style="white-space:nowrap;">
								<input type="submit" value="<spring:message code="general.search"/>"/>
							</td>
						</tr></table>
					</td></tr>
					<tr><td colspan="2">
						<br/>
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
					
					<tr><th colspan="2"><br/><spring:message code="mdrtb.enrollment"/></th></tr>
					<tr>
						<td><input type="radio" name="enrollment" value="ever"<c:if test="${empty enrollment || enrollment == 'ever'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="mdrtb.enrollment.ever"/></td>
					</tr>
					<tr>
						<td><input type="radio" name="enrollment" value="current"<c:if test="${enrollment == 'current'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="mdrtb.enrollment.current"/></td>
					</tr>
					<tr>
						<td><input type="radio" name="enrollment" value="previous"<c:if test="${enrollment == 'previous'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="mdrtb.enrollment.previous"/></td>
					</tr>
					<tr>
						<td><input type="radio" name="enrollment" value="never"<c:if test="${enrollment == 'never'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="mdrtb.enrollment.never"/></td>
					</tr>
									
					<mdrtb:forEachRecord name="workflow" programName="${mdrProgram}">
						
						<tr><th colspan="2">
							<br/><mdrtb:concept concept="${record.concept}" nameVar="n" mappingVar="m">
								<c:choose>
									<c:when test="${m == 'MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME'}">
										<spring:message code="mdrtb.patientstatusworkflow.outcome"/>
									</c:when>
									<c:when test="${m == 'MULTI-DRUG RESISTANT TUBERCULOSIS CULTURE STATUS'}">
										<spring:message code="mdrtb.patientstatusworkflow.cultureStatus"/>
									</c:when>
									<c:when test="${m == 'MULTI-DRUG RESISTANT TUBERCULOSIS PATIENT STATUS'}">
										<spring:message code="mdrtb.patientstatusworkflow.patientStatus"/>
									</c:when>
									<c:when test="${m == 'CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE'}">
										<spring:message code="mdrtb.previousDrugClassification"/>
									</c:when>
									<c:when test="${m == 'CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT'}">
										<spring:message code="mdrtb.previousTreatmentClassification"/>
									</c:when>		
									<c:when test="${m == 'HOSPITALIZATION WORKFLOW'}">
										<spring:message code="mdrtb.currentlyHospitalized"/>
									</c:when>								
									<c:otherwise>
										${n.name}
									</c:otherwise>
								</c:choose>
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
			</td>
			<td valign="top" width="100%" style="padding-left:10px;">
				<c:set var="extensionFound" value="false"/>
				<openmrs:extensionPoint pointId="org.openmrs.mdrtb.listPatientDisplayModes" type="html">
					<c:if test="${extension.key == param.displayMode}">
						<c:set var="extensionFound" value="true"/>
						<openmrs:portlet moduleId="${extension.moduleId}" url="${extension.portletUrl}" patientIds="${patientIds}" />
					</c:if>
				</openmrs:extensionPoint>
				<c:if test="${extenionFound != 'true'}">
					<c:choose>
						<c:when test="${param.displayMode == 'mdrtbSummary'}">
							<openmrs:portlet moduleId="mdrtb" url="mdrtbPatientSummary" patientIds="${patientIds}" />
						</c:when>
						<c:when test="${param.displayMode == 'mdrtbShortSummary'}">
							<openmrs:portlet moduleId="mdrtb" url="mdrtbShortSummary" patientIds="${patientIds}" />
						</c:when>
						<c:when test="${param.displayMode == 'mdrtbCustomList'}">
							<openmrs:portlet moduleId="mdrtb" url="mdrtbCustomList" patientIds="${patientIds}" />
						</c:when>
						<c:otherwise>
							<table id="patientTable">
								<thead>
									<tr>
										<th>&nbsp;</th>
										<th class="patientTable"><spring:message code="general.id"/></th>
										<th class="patientTable"><spring:message code="general.name"/></th>
										<th class="patientTable"><spring:message code="mdrtb.ageUpper"/></th>
										<th class="patientTable"><spring:message code="mdrtb.gender"/></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${patients}" var="p">
										<tr class="patientRow patientRow${p.patientId}">
											<td class="patientTable" style="white-space:nowrap; width:20px;">
												<a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${p.patientId}">
													<img src="${pageContext.request.contextPath}/images/lookup.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
												</a>						
												<openmrs:extensionPoint pointId="org.openmrs.mdrtb.listPatientDetailPortlets" type="html">
													<a href="javascript:void(0)" onClick="loadUrlIntoDetailsPopup('<spring:message code="${extension.title}"/>', '${pageContext.request.contextPath}/module/mdrtb/viewPortlet.htm?patientId=${p.patientId}&moduleId=${extension.moduleId}&id=${extension.moduleId}&url=${extension.portletUrl}'); return false;">
														<img src="${pageContext.request.contextPath}${extension.imagePath}" title="<spring:message code="${extension.title}"/>" border="0" align="top" />
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
						</c:otherwise>
					</c:choose>
				</c:if>
			</td>
		</tr>
	</table>
</form>

<div id="displayDetailsPopup"> 
	<div id="displayDetailsPopupLoading"><spring:message code="general.loading"/></div>
	<iframe id="displayDetailsPopupIframe" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe> 
</div>

<%@ include file="mdrtbFooter.jsp"%>
