<%@ include file="/WEB-INF/template/include.jsp"%>
<div id="box" style="position:relative;left:3%;top:8px">
		<table class="patientTable">
			<tr class="patientOverviewHeaderEven">
				<td align='center' style="background-color: whitesmoke;" class="patientOverviewHeaderEven"><b>${obj.givenName}&nbsp;
							${obj.middleName}&nbsp; 
							${obj.familyName}&nbsp;
							${obj.familyNameTwo}
				</b></td>
				<td><spring:message code="mdrtb.birthday" />
				<b><openmrs:formatDate date="${obj.patient.birthdate}" format="${dateFormat}" /></b>
				&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.gender" /> <b>${obj.patient.gender}</b>&nbsp;&nbsp;&nbsp;
				
				</td>			
			</tr>
			<tr>
				<td class="smaller"><spring:message code="mdrtb.patientidentifier" />
				</td>
				<td colspan="3">${obj.patientIdentifier}
				</td>
			</tr>	
			<tr>
				<td class="smaller"><spring:message code="mdrtb.treatmentstartdate" />
				</td>
				<td colspan="3"><openmrs:formatDate date="${obj.treatmentStartDate.valueDatetime}" format="${dateFormat}" />
				</td>
			</tr>
			<c:if test="${!empty obj.patientProgram}">
			<tr>
				<td class="smaller"><spring:message code="mdrtb.culturestatus" />
				</td>
				<td colspan="3">
					
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
					
					
				</td>
			</tr>
			</c:if>
			<c:if test="${!empty obj.cultureConversion}">
			<tr>
				<td class="smaller"><spring:message code="mdrtb.cultureconversiondate" />
				</td>
				<td colspan="3"><openmrs:formatDate date="${obj.cultureConversion.valueDatetime}" format="${dateFormat}" />
				</td>
			</tr>
			</c:if>
			<c:if test="${!empty obj.cultureReconversion}">
			<tr>
				<td class="smaller"><spring:message code="mdrtb.culturereconversiondate" />
				</td>
				<td colspan="3"><openmrs:formatDate date="${obj.cultureReconversion.valueDatetime}" format="${dateFormat}" />
				</td>
			</tr>
			</c:if>
			<tr>
				<td class="smaller"><spring:message code="mdrtb.mdrtbprogramstartdate" />
				</td>
				<td><openmrs:formatDate date="${obj.patientProgram.dateEnrolled}" format="${dateFormat}"/>
				</td>
			</tr>	
			<c:if test="${!empty obj.patientProgram.dateCompleted}">
			<tr>	
				<td class="smaller"><spring:message code="mdrtb.mdrtbprogramstopdate" />
				</td>
				<td><openmrs:formatDate date="${obj.patientProgram.dateCompleted}" format="${dateFormat}"/>
				</td>
			</tr>
			</c:if>
				<tr>	
				<td class="smaller"><spring:message code="mdrtb.provider" />
				</td>
				<td>${obj.provider}
				</td>
			</tr>
			<tr>	
				<td class="smaller"><spring:message code="mdrtb.clinic" />
				</td>
				<td>${obj.location}
				</td>
			</tr>
			<tr>	
				<td class="smaller"><spring:message code="mdrtb.nextvisit" />
				</td>
				<td>
							<input type="text" name="nextscheduledvisit" id="nextscheduledvisit" value='<c:if test="${!empty obj.nextScheduledVisit}"><openmrs:formatDate date="${obj.nextScheduledVisit.valueDatetime}" format="${dateFormat}"/></c:if>' style="width:90px" onmousedown="javascript:$j(this).date_input()" onchange="javascript:showNextVisitDiv(this)"/>
							<div id="nextVisitDivnextscheduledvisit" class="displayOff">
								<input type="button" id="nextvisitbutton" value="<spring:message code="mdrtb.savenextvisitdate" />" onClick="javascript:saveNewNextVisit(this)"/>
								<input type='hidden' id='patientId' name='patientId' value='${obj.patient.patientId}'>
							</div>
				</td>
			</tr>
		</table>
		<span style="">		
		<openmrs:hasPrivilege privilege="Edit Patients">
			<a href="/openmrs/patientDashboard.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.patientdashboardview" /></a>&nbsp;&nbsp;&nbsp;
			<a href="/openmrs/admin/patients/newPatient.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.editpatientshortform" /></a>&nbsp;&nbsp;&nbsp;
			<a href="/openmrs/admin/patients/patient.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.editpatientlongform" /></a>&nbsp;&nbsp;&nbsp;
		</openmrs:hasPrivilege>
		</span>		
</div>