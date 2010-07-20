<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<h2><table><tr><td><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/who_logo.bmp" alt="logo WHO" style="height:50px; width:50px;" border="0"/></td><td>&nbsp;<spring:message code="mdrtb.title" /></td></tr></table></h2>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />
<spring:message var="pageTitle" code="findPatient.title" scope="page"/>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<br />
<table class="indexTable">
	<tr>
		<td width=60% valign='top'>
			<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=full|postURL=${pageContext.request.contextPath}/module/mdrtb/mdrtbPatientOverview.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
			<openmrs:hasPrivilege privilege="Add Patients">
				<br/><br/>
					<openmrs:portlet id="mdrtbAddPatient" url="mdrtbAddPatient" parameters="personType=patient|postURL=mdrtbAddPatientForm.form|viewType=shortEdit" moduleId="mdrtb" />
			</openmrs:hasPrivilege>
			
			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.bottomLeft" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div class="box">
						
						<b class="boxHeader"><spring:message code="${extension.title}"/></b>
						<ul id="menu">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<li><a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a></li>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<li><a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			
		</td>
		<td>&nbsp;&nbsp;&nbsp;
		</td>
		<td valign='top'>
			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.upperRight" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div class="box">
						
						<b class="boxHeader"><spring:message code="${extension.title}"/></b>
						<ul id="menu">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<li><a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a></li>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<li><a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			
			<openmrs:globalProperty key="mdrtb.enable_specimen_tracking" var="enableSpecimenTracking"/>

			<table class="indexInner" style="border-collapse:collapse;">
			
				<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="mdrtb.patientLists"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr class="${rowClass}"><td>
					<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbListPatients.form">
						<spring:message code="mdrtb.viewListPatientPage"/>
					</a>
					<br/><br/>
					<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.listPatientLinks" type="html">
						<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
							<spring:message code="${extension.title}"/>:
							<c:forEach items="${extension.links}" var="link">
								<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>&nbsp;&nbsp;
							</c:forEach>
							<br/>
						</openmrs:hasPrivilege>
					</openmrs:extensionPoint>

					
				</td></tr>
			
		<!--  this old specimen tracking is defunct -->
		<!--
				<c:if test="${enableSpecimenTracking == 'true'}">
					<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
						<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
							<spring:message code="mdrtb.specimenTrackingLink"/>&nbsp;&nbsp;
						</b>
					</td></tr>
					<tr class="${rowClass}"><td>
						<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbSpecimenRegistration.form">
							<spring:message code="mdrtb.specimenRegistrationLink"/>
						</a>
						<br/>
						<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbSpecimenTracking.form">
							<spring:message code="mdrtb.specimenTrackingLink"/>
						</a>
					</td></tr>
				</c:if>
			-->
				
				<tr><td style = "background-color:#8FABC7;padding:2px 2px 2px 2px;"><b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;<spring:message code="mdrtb.viewreports"/>&nbsp;&nbsp;</b></td></tr>
				<c:forEach var="report" items="${reports}" varStatus="varStatus">
					<c:set var="rowClass" scope="page">
						<c:choose><c:when test="${varStatus.index % 2 == 0}">oddRow</c:when><c:otherwise>evenRow</c:otherwise></c:choose>
					</c:set>
					
						
					<tr class="${rowClass}"><td><a href="${pageContext.request.contextPath}/module/birt/generateReport.form?reportId=${report.reportDefinition.reportObjectId}">
								${report.reportDefinition.name}
							</a></td></tr>
					
					
				</c:forEach>
				<c:if test="${fn:length(reports) == 0}">
				<tr><td><i> &nbsp; <spring:message code="mdrtb.noReports"/></i><br/></td></tr>
				</c:if>
					
				<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;"><b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;<spring:message code="mdrtb.viewdrugrequirements" />&nbsp;&nbsp;</b></td></tr>
				<tr><td>
					<a href="drugforecast/simpleUsage.list"><spring:message code="mdrtb.simpleDrugUsage"/></a><br/>
					<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="mdrtb.numberofpatientstakingeachdrug" /></a>
				</td></tr>
			
			</table>
		</td>
	</tr>
</table>
<br>&nbsp;<br>

<%@ include file="mdrtbFooter.jsp"%>
