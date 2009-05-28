<%@ include file="/WEB-INF/template/include.jsp" %>
<c:set var="mdrtbId" scope="page" value="" />
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/index.htm" />

	<%-- Header showing preferred name, id, and treatment status --%>
		<div id="patientHeader" class="boxHeader">
		
		<div id="patientHeaderPatientName">${model.patient.personName}</div>
		<div id="patientHeaderPreferredIdentifier">
			<c:if test="${fn:length(model.patient.activeIdentifiers) > 0}">
				<c:if test="${!empty obj.patientIdentifier}">
					<span class="patientHeaderPatientIdentifier"><span id="patientHeaderPatientIdentifierType">${obj.patientIdentifier.identifierType.name}:</span> ${obj.patientIdentifier.identifier}</span>
					<c:set var="mdrtbId" scope="page" value="${obj.patientIdentifier}" />
				</c:if>
				<c:if test="${empty obj.patientIdentifier}">
					<c:forEach var="identifier" items="${model.patient.activeIdentifiers}" begin="0" end="0">
						<span class="patientHeaderPatientIdentifier"><span id="patientHeaderPatientIdentifierType">${identifier.identifierType.name}<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientIdentifierType" type="html" parameters="identifierLocation=${identifier.location.name}"/>:</span> ${identifier.identifier}</span>
					</c:forEach>
				</c:if>
			</c:if>
		</div>
		<table id="patientHeaderGeneralInfo">
			<tr>
				<td id="patientHeaderPatientGender">
					<c:if test="${model.patient.gender == 'M'}"><img src="${pageContext.request.contextPath}/images/male.gif" alt='<spring:message code="Person.gender.male"/>' id="maleGenderIcon"/></c:if>
					<c:if test="${model.patient.gender == 'F'}"><img src="${pageContext.request.contextPath}/images/female.gif" alt='<spring:message code="Person.gender.female"/>' id="femaleGenderIcon"/></c:if>
				</td>
				<td id="patientHeaderPatientAge">
					<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.beforePatientHeaderPatientAge" type="html" parameters="patientId=${model.patient.patientId}" />
					<c:if test="${model.patient.age > 0}">${model.patient.age} <spring:message code="Person.age.years"/></c:if>
					<c:if test="${model.patient.age == 0}">< 1 <spring:message code="Person.age.year"/></c:if>
					<span id="patientHeaderPatientBirthdate"><c:if test="${not empty model.patient.birthdate}">(<c:if test="${model.patient.birthdateEstimated}">~</c:if><openmrs:formatDate date="${model.patient.birthdate}" type="medium" />)</c:if><c:if test="${empty model.patient.birthdate}"><spring:message code="Person.age.unknown"/></c:if></span>
				</td>
				<openmrs:globalProperty key="use_patient_attribute.tribe" defaultValue="false" var="showTribe"/>
				<c:if test="${showTribe}">
					<td id="patientHeaderPatientTribe">
						<spring:message code="Patient.tribe"/>:
						<b>${model.patient.tribe.name}</b>
						<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientTribe" type="html" parameters="patientId=${model.patient.patientId}" />
					</td>
				</c:if>
				<c:if test="${not empty model.patient.attributeMap['Health Center']}">
					<td id="patientHeaderHealthCenter">
						<spring:message code="PersonAttributeType.HealthCenter"/>:
						<b>${model.patient.attributeMap['Health Center'].hydratedObject}</b>
					</td>
				</c:if>
				<td id="patientDashboardHeaderExtension">
					<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.Header" type="html" parameters="patientId=${model.patient.patientId}" />
				</td>
				<td style="width: 100%;">&nbsp;</td>
				<td id="patientHeaderOtherIdentifiers">
					
					<c:if test="${fn:length(model.patient.activeIdentifiers) > 1}">
						<c:forEach var="identifier" items="${model.patient.activeIdentifiers}" begin="1" end="1">
							<c:if test="${mdrtbId != identifier}">
								<span class="patientHeaderPatientIdentifier">${identifier.identifierType.name}<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientIdentifierType" type="html" parameters="identifierLocation=${identifier.location.name}"/>: ${identifier.identifier}</span>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${fn:length(model.patient.activeIdentifiers) > 2}">
						<div id="patientHeaderMoreIdentifiers">
							<c:forEach var="identifier" items="${model.patient.activeIdentifiers}" begin="2">
								<c:if test="${mdrtbId != identifier}">
									<span class="patientHeaderPatientIdentifier">${identifier.identifierType.name}<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientIdentifierType" type="html" parameters="identifierLocation=${identifier.location.name}"/>: ${identifier.identifier}</span>
								</c:if>
							</c:forEach>
						</div>
					</c:if>
				</td>
				
				<c:if test="${fn:length(model.patient.activeIdentifiers) > 2}">
					<td width="32">
						<small><a id="patientHeaderShowMoreIdentifiers" onclick="return showMoreIdentifiers()" title='<spring:message code="patientDashboard.showMoreIdentifers"/>'><spring:message code="general.nMore" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/></a></small>
					</td>
				</c:if>
			</tr>
		</table>
	</div>

		<div id="patientSubheader" class="box">



		<table id="patientHeaderObs">
			<openmrs:globalProperty key="concept.weight" var="weightConceptId"/>
			<openmrs:globalProperty key="concept.height" var="heightConceptId"/>
		
			
			<tr>
				<th id="patientHeaderObsWeight">
					<spring:message code="Patient.bmi"/>: ${model.patientBmiAsString}
				</th>
				<td> 
					<small>
						(
						<spring:message code="Patient.weight"/>:
						<openmrs_tag:mostRecentObs observations="${model.patientObs}" concept="${weightConceptId}" showUnits="true" locale="${model.locale}" showDate="false" />
						,
						<spring:message code="Patient.height"/>:
						<openmrs_tag:mostRecentObs observations="${model.patientObs}" concept="${heightConceptId}" showUnits="true" locale="${model.locale}" showDate="false" />
						)
					</small>
				</th>
				<td id="patientHeaderObsReturnVisit">
					<spring:message code="Patient.returnVisit"/>:
					<openmrs_tag:mostRecentObs observations="${model.patientObs}" concept="5096" locale="${model.locale}" />
				</td>
				<td id="patientHeaderObsRegimen">
					<spring:message code="Patient.regimen" />:
					<span id="patientHeaderRegimen">
						<c:forEach items="${model.currentDrugOrders}" var="drugOrder" varStatus="drugOrderStatus">
							<c:if test="${!empty drugOrder.drug}">${drugOrder.drug.name}</c:if><c:if test="${empty drugOrder.drug}">${drugOrder.concept.name.name}</c:if>
							<c:if test="${!drugOrderStatus.last}">, </c:if>
						</c:forEach>
					</span>
				</td>
			</tr>
		</table>
		<table><tr>
			<td><spring:message code="Patient.lastEncounter"/>:</td>
			<th>
				<c:forEach items='${openmrs:sort(model.patientEncounters, "encounterDatetime", true)}' var="lastEncounter" varStatus="lastEncounterStatus" end="0">
					${lastEncounter.encounterType.name} @ ${lastEncounter.location.name} | <openmrs:formatDate date="${lastEncounter.encounterDatetime}" type="medium" /> | ${lastEncounter.provider.givenName} ${lastEncounter.provider.familyName}
				</c:forEach>
				<c:if test="${fn:length(model.patientEncounters) == 0}">
					<spring:message code="Encounter.no.previous"/>
				</c:if>	
			</th>
		</tr></table>
		<table>
			<tr>
				<td><spring:message code="mdrtb.mdrtbprogramstartdateLC" />:
				</td>
				<th><openmrs:formatDate date="${obj.patientProgram.dateEnrolled}" type="medium"/>
				</th>
				<c:if test="${!empty obj.patientProgram.dateCompleted}">
					<td><spring:message code="mdrtb.mdrtbprogramstopdate" />:
					</td>
					<th><openmrs:formatDate date="${obj.patientProgram.dateCompleted}" type="medium"/>
					</th>
				</c:if>
				<c:if test="${empty obj.patientProgram.dateCompleted}">
					<td colspan="2"></td>
				</c:if>
			</tr>	
		</table>
		<table>	
			<tr>
				<td><spring:message code="mdrtb.treatmentstartdateLC" />:
				</td>
				<th colspan="3"><openmrs:formatDate date="${obj.treatmentStartDate.valueDatetime}" type="medium" />
				</th>
			</tr>
		</table>
		
			<c:if test="${!empty obj.patientProgram}">
			<table>	
			<tr>
				<td ><spring:message code="mdrtb.culturestatus" />:
				</td>
				<th colspan="3">
					
					<c:set var="winningDate" scope="page" value="0" />
					<c:if test="${!empty obj.cultureConversion}">
							<c:if test="${empty obj.cultureReconversion}">
								<c:set var="winningDate" scope="page" value="1" />
							</c:if>
							<c:if test="${!empty obj.cultureReconversion}">
								<c:if test = "${obj.cultureReconversion.valueDatetime.time < obj.cultureConversion.valueDatetime.time}">
									<c:set var="winningDate" scope="page" value="1" />
								</c:if>
								<c:if test = "${obj.cultureConversion.valueDatetime.time < obj.cultureReconversion.valueDatetime.time }">
									<c:set var="winningDate" scope="page" value="2" />
								</c:if>
							</c:if>
					</c:if>
					<c:if test="${empty obj.cultureConversion}">
						<c:if test="${!empty obj.cultureReconversion}">
							<c:set var="winningDate" scope="page" value="2" />
						</c:if>	
					</c:if>
					
					<c:if test="${winningDate == 0}">
						<c:if test="${fn:contains(obj.cultureStatus.state, 'NONE')}">
							<B><spring:message code="mdrtb.none" /></B>
						</c:if>
						<c:if test="${!fn:contains(obj.cultureStatus.state, 'NONE')}">
							<span style="color:red;"><B><spring:message code="mdrtb.unconverted" /></B>	</span>
						</c:if>
					</c:if>
					<c:if test="${winningDate == 1}">
						<span style="color:green;"><b><spring:message code="mdrtb.converted" /></b>	</span>
					</c:if>
					<c:if test="${winningDate == 2}">
						<span style="color:red;"><b><spring:message code="mdrtb.reconverted" /></b>	</span>
					</c:if>
					
					
				</th>
			</tr>
			</table>
			</c:if>
			<c:if test="${!empty obj.cultureConversion}">
			<table>	
			<tr>
				<td ><spring:message code="mdrtb.cultureconversiondate" />:
				</td>
				<th colspan="3"><openmrs:formatDate date="${obj.cultureConversion.valueDatetime}" type="medium" />
				</th>
			</tr>
			</table>
			</c:if>
			<c:if test="${!empty obj.cultureReconversion}">
			<table>	
			<tr>
				<td ><spring:message code="mdrtb.culturereconversiondate" />:
				</td>
				<th colspan="3"><openmrs:formatDate date="${obj.cultureReconversion.valueDatetime}" type="medium" />
				</th>
			</tr>
			</table>
			</c:if>
			
	</div>
	<div style="" nowrap>		
		<openmrs:hasPrivilege privilege="Edit Patients">
			&nbsp;
			<a href="/openmrs/patientDashboard.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.patientdashboardview" /></a>&nbsp;&nbsp;&nbsp;
			<a href="/openmrs/admin/patients/newPatient.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.editpatientshortform" /></a>&nbsp;&nbsp;&nbsp;
			<a href="/openmrs/admin/patients/patient.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.editpatientlongform" /></a>&nbsp;&nbsp;&nbsp;
		</openmrs:hasPrivilege>
			 | &nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.nextvisit" />:

							<input type="text" name="nextscheduledvisit" id="nextscheduledvisit" value='<c:if test="${!empty obj.nextScheduledVisit}"><openmrs:formatDate date="${obj.nextScheduledVisit.valueDatetime}" format="${dateFormat}"/></c:if>' style="width:90px" onmousedown="javascript:$j(this).date_input()" onchange="javascript:showNextVisitDiv(this)"/>
							<span id="nextVisitDivnextscheduledvisit" class="displayOff">
								<input type="button" id="nextvisitbutton" value="<spring:message code="mdrtb.savenextvisitdate" />" onClick="javascript:saveNewNextVisit(this)"/>
								<input type='hidden' id='patientId' name='patientId' value='${obj.patient.patientId}'>
							</span><span id="nextVisitMessage" style="vertical-align:bottom;color:red;font-size:80%;"></span>

		</div>	
	<script type="text/javascript">
		function showMoreIdentifiers() {
			if (identifierElement.style.display == '') {
				linkElement.innerHTML = '<spring:message code="general.nMore" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/>';
				identifierElement.style.display = "none";
			}
			else {
				linkElement.innerHTML = '<spring:message code="general.nLess" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/>';
				identifierElement.style.display = "";
			}
		}
		
		var identifierElement = document.getElementById("patientHeaderMoreIdentifiers");
		var linkElement = document.getElementById("patientHeaderShowMoreIdentifiers");
		if (identifierElement)
			identifierElement.style.display = "none";
		
	</script>
	