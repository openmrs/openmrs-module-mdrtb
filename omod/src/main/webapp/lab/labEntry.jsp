<%@page import="org.openmrs.module.labmodule.web.dwr.LabFindlocation"%>
<%@page import="org.openmrs.module.labmodule.specimen.LabResult"%>
<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/dotsHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>
<%@ page import="org.openmrs.module.labmodule.TbConcepts, org.openmrs.module.labmodule.service.TbService,org.openmrs.api.context.Context" %>

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<style type="text/css">
td
{
    padding:0 10px 0 10px;
}

form {
 margin: 0;
 padding: 0;
}
</style>

<openmrs:portlet url="labPatientHeader" id="labPatientHeader" moduleId="labmodule" patientId="${patientId}"/>

<br>
<br>
<h3> <spring:message code="labmodule.labDataEntry" /> </h3>

<br>

<!-- LEFT-HAND COLUMN START -->
<div id="leftColumn" style=" float: left; padding:0px 4px 4px 4px; width:33%;">
 
	 <b class="boxHeader">
		&nbsp;
		<spring:message code="mdrtb.specimens" text="Specimens"/>
		<span style="float:right">
			<openmrs:hasPrivilege privilege="Add Test Result">
			<img title="Add" class="add" id="quickEntryAddButton" onclick="onClick(this)" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			</openmrs:hasPrivilege>
		</span> 
	</b> 

	<div class="box">
		<div id="specimenList">
		
		<c:choose>
			<c:when test="${fn:length(labResults) > 0}">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
					    <openmrs:hasPrivilege privilege="Edit Test Result">
						<td></td>
						</openmrs:hasPrivilege>
						<openmrs:hasPrivilege privilege="Delete Test Result">
						<td></td>
						</openmrs:hasPrivilege>
						<td class="tableCell" style="font-weight:bold;"><nobr><u><spring:message code="mdrtb.dateCollected" text="Date Collected"/></u></nobr></td>
						<td class="tableCell" style="font-weight:bold;"><nobr><u><spring:message code="labmodule.sampleid" text="Sample ID"/></u></nobr></td>
						<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.tests" text="Tests"/></u></nobr></td>
					
					</tr>
				
					<c:forEach var="specimenListItem" items="${labResults}" varStatus="i">
						<tr 
							<c:if test="${i.count % 2 == 0 }">class="evenRow"</c:if>
							<c:if test="${i.count % 2 != 0 }">class="oddRow"</c:if>
							<c:if test="${specimenListItem.id == labResult.id}"> style="background-color : gold"</c:if>>
						
							<openmrs:hasPrivilege privilege="Edit Test Result">
							<td>
								
									<img title="Edit" src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" onclick="onClick(this,'${labResult.location.displayString}')" alt="edit" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								
							</td>
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Delete Test Result">
							<td> 
								<form id="delete_${i.count}" action="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}&submissionType=delete&testType=labResult" method="post">
									<img title="Delete" id='deleteTest_${i.count}_${specimenListItem.labNumber}' onclick="deleteTest(this)" src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								</form>	
							</td> 
							</openmrs:hasPrivilege>
							
							<td class="tableCell"><nobr><a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}"><openmrs:formatDate date="${specimenListItem.dateCollected}" format="${_dateFormatDisplay}"/></a></nobr></td>
							<td class="tableCell"><nobr><a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}">${specimenListItem.labNumber}</a></nobr></td>
							
							<td class="tableCell"><nobr><a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}">
							
							<c:if test="${fn:length(specimenListItem.microscopies) > 0}">
								-<spring:message code="labmodule.labEntry.bacterioscopy"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.xperts) > 0}">
								-<spring:message code="labmodule.labEntry.xpert"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.HAINS) > 0}">
								-<spring:message code="labmodule.labEntry.hainSplit"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.HAINS2) > 0}">
								-<spring:message code="labmodule.labEntry.hain2Split"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.cultures) > 0}">
								-<spring:message code="labmodule.culture"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.dst1s) > 0}">
								-<spring:message code="labmodule.labEntry.firstLineDst"/> <br>
							</c:if>
							<c:if test="${fn:length(specimenListItem.dst2s) > 0}">
								-<spring:message code="labmodule.labEntry.secondLineDst"/> <br>
							</c:if>
							
							</a></nobr></td>
							
							
						</tr>
					</c:forEach>
				</table>
			</c:when>
			
			<c:otherwise>
				<spring:message code="mdrtb.noSpecimens" text="No specimen information available for this patient program."/>
			</c:otherwise>
			
		</c:choose>
		
		</div>
	</div>
	
	<br> <br>


	<b class="boxHeader">
		&nbsp;
		<spring:message code="labmodule.search.status" text="Status"/>
	</b>
	<div class="box">
		<div style="padding:10px">
			<spring:message code="labmodule.search.status" text="Status"/>:
		    <b>${status}</b>
		</div>
	</div>

</div>
<!-- END OF LEFT-HAND COLUMN -->

<div id="old_column" style="float: right; width:65%;  padding:0px 4px 4px 4px">

	<b class="boxHeader" style="margin:0px">
		&nbsp;
		<spring:message code="mdrtb.specimenDetails" text="Specimen Details"/>
		<span id="specimen_edit_span" name="specimen_edit_span" style="float:right">
		    <A HREF="javascript:window.print()"><spring:message code="labmodule.labEntry.print" text="Print"/></A>
		    &nbsp;&nbsp;
		    <%
			    Integer newCase = Context.getService(TbService.class).getConcept(TbConcepts.NEW_CASE).getConceptId();
		    	Integer  repeatCase = Context.getService(TbService.class).getConcept(TbConcepts.REPEAT_CASE).getConceptId();
			    pageContext.setAttribute("newCase", newCase);
			    pageContext.setAttribute("repeatCase", repeatCase);
			%>
		    <c:choose>
		    	<c:when test="${labResult.investigationPurpose.conceptId ==  repeatCase || labResult.investigationPurpose.conceptId == newCase }">
			    	<A HREF="${pageContext.request.contextPath}/module/labmodule/lab/tb05.form?patientId=${patientId}&labResultId=${labResult.id}&type=diagnostics" target="_blank"><spring:message code="labmodule.tb05ReportDiagnostics" text="TB05 Diagnostics"/></A>
			    	&nbsp;&nbsp;
				</c:when>
				<c:otherwise>
						<A HREF="${pageContext.request.contextPath}/module/labmodule/lab/tb05.form?patientId=${patientId}&labResultId=${labResult.id}&type=control"><spring:message code="labmodule.tb05ReportControl" text="TB05 Control"/></A>
		    			&nbsp;&nbsp;		
				</c:otherwise>
		    </c:choose>
					
			<openmrs:hasPrivilege privilege="Edit Test Result">
				<img title="Edit" id='editSpecimenDetailButton' onclick="onClick(this,'${labResult.location.displayString}')" src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="edit" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			</openmrs:hasPrivilege>
		
		</span> 
	</b>

	<div class="box" id="specimen_view" style="margin:0px">
		
		<table cellspacing="5" cellpadding="0" width="100%">
		
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
					&nbsp;
					${labResult.location.displayString}
					&nbsp;
					${patientLocation}
					&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="Number"/>:</font>
					&nbsp;
					${labResult.labNumber}
				</td>
			</tr>
			<!-- Date of receiveing request -->
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateRequest"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateRequested}" format="${_dateFormatDisplay}"/>
					&nbsp;&nbsp;
				</td>
			</tr>
			
			<!-- Date of receiveing sample -->
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateCollected}" format="${_dateFormatDisplay}"/>
					&nbsp;&nbsp;
				</td>
			</tr>
			
			<!-- Date of sputum collection -->
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.sputumCollectionDate"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.sputumCollectionDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
			
			<!-- Investigation date -->
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.investigationDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
			
			<!-- OMAR FIELDS START -->
			
			<!-- Referring Facility -->
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.referringFacility"/> : </font>
					&nbsp;
					${labResult.referringFacility}
				</td>
			</tr>
			<!-- Referred By -->
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.referredBy"/> : </font>
					&nbsp;
					${labResult.referredBy}
				</td>
			</tr>
			<!-- OMAR FIELDS END -->
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
					&nbsp;
					${labResult.requestingLabName.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
					&nbsp;
					${labResult.investigationPurpose.displayString}
				</td>
			</tr>
			
			<tr>
				<td> 
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					${labResult.biologicalSpecimen.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
					&nbsp;
					${labResult.comments}
				</td>
			</tr>
			
			<%-- <tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabName"/> : </font>
					&nbsp;
					${labResult.peripheralLabName}
				</td>
			</tr>
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
					&nbsp;
					${labResult.peripheralLabNumber}
				</td>
			</tr>
			
			<tr>
				<td> 
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					${labResult.peripheralBiologicalSpecimen.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
					&nbsp;
					${labResult.microscopyResult.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateResult}" format="${_dateFormatDisplay}"/>
				</td>
			</tr> 

			<!-- Omar fields start -->
			
				<tr>
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.tb03"/>:</font>
						&nbsp;
						${labResult.tb03}
					</td>
				</tr>
				
				<tr>
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.year"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${labResult.year}" format="${_dateFormatDisplay}"/>
					</td>
				</tr> 
			<!-- Omar fields end --> --%>
			<!--  
			 	 <tr>
						<td>
							<br>
							<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.regionalLabInfo"/></b>
						</td>
					</tr>
						<tr id="regionalLabViewRow1">
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.regionalLabNo"/>:</font>
							&nbsp;
							${labResult.regionalLabNo}
						</td>
					</tr>
					<tr id="regionalLabViewRow2" >
						<td>
							<font style="font-weight:bold"><spring:message code="labmodule.labEntry.MT"/>:</font>
							&nbsp;
							${labResult.mtResult.displayString}
						<td>
					</tr>
					<tr id="regionalLabViewRow3" >
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.H"/>:</font>
							&nbsp;
							${labResult.regionalhResult.displayString}
						</td>
					</tr>
					<tr id="regionalLabViewRow4" >
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.R"/>:</font>
							&nbsp;
							${labResult.regionalrResult.displayString}
						</td>
					</tr>
					<tr id="regionalLabViewRow5" >
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert"/>:</font>
							&nbsp;
							${labResult.xpertMtbRifResult.displayString}
						</td>
					</tr>
					<tr id="regionalLabViewRow6" >
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.dateObservedGrowth"/>:</font>
							&nbsp;
							<openmrs:formatDate date="${labResult.dateOfObservedGrowth}" format="${_dateFormatDisplay}"/>
						</td>
					</tr>
					<tr id="regionalLabViewRow7" >
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.cultureResult"/>:</font>
							&nbsp;
							${labResult.cultureResult.displayString}
						</td>
					</tr>
					-->
					<!-- Omar Section Ends -->
					
		</table>
	</div>
	
	<div class="box" id="specimen_edit" style="margin:0px">
		
	<form id="updateTestResults" name="updateTestResults" action="labEntry.form?patientId=${patientId}&labResultId=${labResult.id}&submissionType=specimen" method="post">
		
		<table cellspacing="5" cellpadding="0" width="100%">
		
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
				</td>
			</tr>	
				<tr>
				<td>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
					<select name="specimen_oblast" id="specimen_oblast" onchange="getDistrictsInEditTest('specimen')">
						<option value=""></option>
						<c:forEach var="o" items="${oblasts}">
							<option value="${o.id}">${o.name}</option>
						</c:forEach>
					</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
				<select name="specimen_district" id="specimen_district" onchange="getFacilitiesInAddTest('specimen')">
						<option value=""></option>
				</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
						<select name="specimen_facility" id="specimen_facility">
						<option value=""></option>
				</select></td>
			</tr>
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="labNumber_e" name="labNumber_e" value="${labResult.labNumber}">
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateRequest"/> : </font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateRequest_e" startValue="${labResult.dateRequested}"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateRecieve_e" startValue="${labResult.dateCollected}"/>
				</td>
			</tr>
			
			<!-- Sputum collection date -->
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.sputumCollectionDate"/> : </font>
						&nbsp;
						<openmrs_tag:dateField formFieldName="dateSputumCollection_e" startValue="${labResult.sputumCollectionDate}"/>
					</td>
				</tr>
			<tr>
				<td>	
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateInvestigation_e" startValue="${labResult.investigationDate}"/>
				</td>
			</tr>
			
			<!-- OMAR FIELDS START:EDIT -->
				
				<tr>
					
					<td>
						<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.referringFacility"/> : </font>
						&nbsp;
						<input type="text"  size="10" id="referringFacility_e" name="referringFacility_e" value="${labResult.referringFacility}">
					</td>						
				
				</tr>
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.referredBy"/> : </font>
						&nbsp;
						<input type="text"  size="10" id="referredBy_e" name="referredBy_e" value="${labResult.referredBy}">
					</td>
				</tr>				
					
			<!-- OMAR FIELD END:EDIT -->
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
					&nbsp;
					<select id="requestingLabName_e" name="requestingLabName_e">
							<option hidden selected value=""></option>
								<c:forEach var="requestingFacility" items="${requestingFacilities}">
									<option value="${requestingFacility.answerConcept.id}" <c:if test="${requestingFacility.answerConcept == labResult.requestingLabName}">selected</c:if> >${requestingFacility.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
					&nbsp;
					<select id="investigationPurpose_e" name="investigationPurpose_e">
						<c:forEach var="purpose" items="${investigationPurposes}">
							<option value="${purpose.answerConcept.id}" <c:if test="${purpose.answerConcept == labResult.investigationPurpose}">selected</c:if> >${purpose.answerConcept.displayString}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td> 
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					<select id="biologicalSpecimen_e" name="biologicalSpecimen_e">
							<c:forEach var="type" items="${types}">
								<option value="${type.answerConcept.id}" <c:if test="${type.answerConcept == labResult.biologicalSpecimen}">selected</c:if> >${type.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
					&nbsp;
					<textarea rows="4" cols="50" name="comments_e" >${labResult.comments}</textarea>
				</td>
			</tr>
			
			<%-- <tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
				
					<input checked hidden type="checkbox" id="peripheral_e" name="peripheral_e" value="peripheral"/>
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
					&nbsp;
					<input type="text"  size="10" id="peripheralLabNo_e" name="peripheralLabNo_e" value="${labResult.peripheralLabNumber}">
				</td>
			</tr>
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabName"/> : </font>
					&nbsp;
					<input type="text"  size="10" id="peripheralLabName_e" name="peripheralLabName_e" value="${labResult.peripheralLabName}">
				</td>
			</tr>
			<tr>
				<td> 
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					<select id="peripheralBiologicalSpecimen_e" name="peripheralBiologicalSpecimen_e">
							<c:forEach var="type" items="${types}">
								<option value="${type.answerConcept.id}" <c:if test="${type.answerConcept == labResult.peripheralBiologicalSpecimen}">selected</c:if> >${type.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
					&nbsp;
					<select id="microscopyResult_e" name="microscopyResult_e">
							<c:forEach var="result" items="${microscopyResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == labResult.microscopyResult}">selected</c:if> >${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateResult_e" startValue="${labResult.dateResult}"/>
				</td>
			</tr>
			<!-- Omar fields here -->
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.tb03"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="tb03_e" name="tb03_e" value="${labResult.tb03}">
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.year"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="year_e" startValue="${labResult.year}"/>
				</td>
			</tr>
			<tr>
				<td>
					<br>
				</td>
			</tr>
			 --%>
			<tr>
				<td>
					<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateSpecimen" onclick='validateAndSubmit(this)'><spring:message code="mdrtb.save" text="Save"/></button>
					</openmrs:hasPrivilege>
					<button type="reset" id="cancelUpdateSpecimen" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
				</td>
			</tr> 
		
		</table>
		
	</form>	
	</div>
	
	<br>
	
	<form>
				 
		<div id = "labResult_div">
			<font style="font-weight:bold"><spring:message code="labmodule.labEntry.addResultFor"/> : </font>
			&nbsp;
			<select id="test_selected">
				<option hidden selected value> </option>
				<option value="bacterioscopy"><spring:message code="labmodule.labEntry.bacterioscopy"/></option>
				 <option value="xpert"><spring:message code="labmodule.labEntry.xpert"/></option>
				 <option value="hain"><spring:message code="labmodule.labEntry.hain"/></option>
				 <option value="hain2"><spring:message code="labmodule.labEntry.hain2"/></option>
				 <option value="culture"><spring:message code="labmodule.culture"/></option>
				 <option value="dst1"><spring:message code="labmodule.labEntry.firstLineDst"/></option>
				 <option value="dst2"><spring:message code="labmodule.labEntry.secondLineDst"/></option>
				 
			</select>
			&nbsp;
			<img title="Add" class="add" id="addTest" onclick="onClick(this,'')" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			<button hidden id="cancelTest" type=reset text="Cancel"/>
		</div>
		
	</form>
	
	<table cellspacing="5" cellpadding="0" width="100%" id ="add_labs">
			
		<tr>
			<td>
				
				<!-- BacterioScopy Div -->
				<div id = "bacterioscopyDiv">
					
					<b class="boxHeader" style="margin:0px">
						&nbsp;
						<spring:message code="labmodule.labEntry.bacterioscopy" text="Microscopy"/>
					</b>
						
					<form id="addMicroscopyResults" name="addMicroscopyResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=microscopy&microscopyId=0" method="post">
			
						<div class="box">
							<table cellspacing="5" cellpadding="0">
								
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="sampleDate" startValue=""/>
										<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
										&nbsp;
										</td>
								</tr>
								<tr>
									<td>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
									<select name="add_microscopy_oblast" id="add_microscopy_oblast" onchange="getDistrictsInEditTest('add_microscopy')">
										<option value=""></option>
										<c:forEach var="o" items="${oblasts}">
											<option value="${o.id}">${o.name}</option>
										</c:forEach>
									</select>
								<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
									<select name="add_microscopy_district" id="add_microscopy_district" onchange="getFacilitiesInAddTest('add_microscopy')">
										<option value=""></option>
									</select>
								<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
										<select name="add_microscopy_facility" id="add_microscopy_facility">
											<option value=""></option>
										</select>
									</td>
								</tr>
								<tr>
									<td>		
										<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber"/> : </font>
										&nbsp;
										<input type="text"  size="10" id="micrscopyLabNumber" name="micrscopyLabNumber">
									</td>
								</tr>
								<tr>
									<td>		
										<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/> : </font>
										&nbsp;
										<input type="text"  size="10" id="micrscopyLabWorker" name="micrscopyLabWorker">
									</td>
								</tr>		
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:</font>
										&nbsp;
										<select id="sampleAppearance" name="sampleAppearance">
										<option hidden selected value=""></option>
										<c:forEach var="appearance" items="${appearances}">
											<option value="${appearance.answerConcept.id}">${appearance.answerConcept.displayString}</option>
										</c:forEach>
										</select>
									</td>
								</tr>			
										
								<tr>
									<td>			
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.result"/>:</font>
										&nbsp;
										<select id="sampleResult" name=sampleResult>
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${microscopyResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addMicroscopyTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>
										<button type = "reset" id="cancelBacterioscopy" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
									
							</table>
						</div>
								
					</form>
						
				</div>
					<!-- End of BacterioScopy Div -->
				
				<!-- GeneXpert DIV -->	
				<div id="xpertDiv">
				
					<form id="addXpertResults" name="addXpertResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=xpert&xpertId=0" method="post">
					
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.xpert" text="Xpert MTB/Rif"/>
						</b>
					
						<div class="box">
							
							<table cellspacing="5" cellpadding="0">
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
											<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
										&nbsp;
									</td>
								</tr>
								<tr>
									<td>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
									<select name="add_xpert_oblast" id="add_xpert_oblast" onchange="getDistrictsInEditTest('add_xpert')">
										<option value=""></option>
										<c:forEach var="o" items="${oblasts}">
											<option value="${o.id}">${o.name}</option>
										</c:forEach>
									</select>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
									<select name="add_xpert_district" id="add_xpert_district"
									onchange="getFacilitiesInAddTest('add_xpert')">
										<option value=""></option>
									</select>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
										<select name="add_xpert_facility" id="add_xpert_facility">
											<option value=""></option>
										</select>
									</td>
								</tr>
								<tr>
									<td>		
										<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber"/> : </font>
										&nbsp;
										<input type="text"  size="10" id="mtbLabNumber" name="mtbLabNumber">
									</td>
								</tr>
								<tr>
									<td>		
										<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/> : </font>
										&nbsp;
										<input type="text"  size="10" id="xpertLabWorker" name="xpertLabWorker">
									</td>
								</tr>
								<tr>
									<td>								
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="xpertTestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
										&nbsp;
										<select id="mtbXpertResult" name="mtbResult" onChange="onChangeMtb(this)">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${mtbResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
										&nbsp;
										<select id = "rifXpertResult" name = "rifResult" disabled onChange="onChangeRif(this)">
											<option hidden selected value=""></option>
												<c:forEach var="result" items="${rifResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.errorCode"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="xpertError" id="xpertError" disabled>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
										&nbsp;
										<select id="xpertDstSent" name="xpertDstSent" >
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${sentResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="xpertDstSentDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
										&nbsp;
										<select id="xpertCultureSent" name="xpertCultureSent" >
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${sentResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="xpertCultureSentDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addXpertTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelXpert" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
								
							</table>
							
						</div>
					
					</form>
				
				</div>
				<!-- END of GeneXpert DIV -->
				
				<!-- HAIN DIV -->	
				<div id="hainDiv">
				
					<form id="addHAINResults" name="addHAINResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=hain&hainId=0" method="post">

						
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.hain" text="Hain"/>
						</b>
						
						<div class="box">
							
							<table  cellspacing="5" cellpadding="0">
							
								<tr>
									<td>
									
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
									<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
										&nbsp;
									</td>
								</tr>
								<tr>
									<td>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
									<select name="add_hain_oblast" id="add_hain_oblast" onchange="getDistrictsInEditTest('add_hain')">
										<option value=""></option>
										<c:forEach var="o" items="${oblasts}">
											<option value="${o.id}">${o.name}</option>
										</c:forEach>
									</select>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
									<select name="add_hain_district" id="add_hain_district"
									onchange="getFacilitiesInAddTest('add_hain')">
										<option value=""></option>
									</select>
										<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
										<select name="add_hain_facility" id="add_hain_facility">
											<option value=""></option>
										</select>
									</td>
								</tr>
								<tr >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="hainLabWorker" id="hainLabWorker">
									</td>
								</tr>
								<tr>
									<td>		
										<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber"/> : </font>
										&nbsp;
										<input type="text"  size="10" id="inhLabNumber" name="inhLabNumber">
									</td>
								</tr>
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hainTestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
										&nbsp;
										<select id="mtbHainResult" name="mtbResult" onChange="onChangeMtb(this)">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${mtbResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
										&nbsp;
										<select id = "rifHainResult" name = "rifResult" disabled>
											<option hidden selected value=""></option>
												<c:forEach var="result" items="${rifResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.h"/>:</font>
										&nbsp;
										<select id = "inhHainResult" name = "inhResult" disabled>
											<option hidden selected value=""></option>
												<c:forEach var="result" items="${inhResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
										&nbsp;
										<select id="hainDstSent" name="hainDstSent" >
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${sentResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hainDstSentDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
										&nbsp;
										<select id="hainCultureSent" name="hainCultureSent" >
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${sentResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hainCultureSentDate" startValue=""/>
									</td>
								</tr>
								
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addHainTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelHain" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
							
							</table>
							
						</div>
						
					</form>
					
				</div>
				<!-- END OF HAIN DIV -->		
					
					<!-- HAIN 2 DIV -->	
				<div id = "hain2Div">
					
					<form id="addHAIN2Results" name="addHAIN2Results" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=hain2&hain2Id=0" method="post">

						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.hain2" text="Hain 2"/>
						</b>
						
						<div class="box">
							
							<table  cellspacing="5" cellpadding="0">
							
								<tr>
									<td>
									
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
										&nbsp;
									</td>
								</tr>
								<tr>
									<td>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
									<select name="add_hain2_oblast" id="add_hain2_oblast" onchange="getDistrictsInEditTest('add_hain2')">
										<option value=""></option>
										<c:forEach var="o" items="${oblasts}">
											<option value="${o.id}">${o.name}</option>
										</c:forEach>
									</select>
									<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
									<select name="add_hain2_district" id="add_hain2_district" onchange="getFacilitiesInAddTest('add_hain2')">
										<option value=""></option>
									</select>
										<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
										<select name="add_hain2_facility" id="add_hain2_facility">
											<option value=""></option>
										</select>
									</td>
								</tr>
								<tr >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="hain2LabWorker" id="hain2LabWorker">
									</td>
								</tr>
								<tr>
									<td>		
										<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber"/> : </font>
										&nbsp;
										<input type="text"  size="10" id="hain2LabNumber" name="hain2LabNumber">
									</td>
								</tr>	
																
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.analysisDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hain2TestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
										&nbsp;
										<select id="mtbHain2Result" name="mtbResult" onChange="onChangeMtb(this)">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${mtbResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.mox"/>:</font>
										&nbsp;
										<select id="moxHain2Result" name="moxResult" disabled>
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${moxResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.km"/>:</font>
										&nbsp;
										<select id="cmHain2Result" name="cmResult" disabled>
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${cmResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.e"/>:</font>
										&nbsp;
										<select id="erHain2Result" name="erResult" disabled>
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${eResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
										&nbsp;
										<select id="hain2DstSent" name="hain2DstSent" >
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${sentResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hain2DstSentDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
										&nbsp;
										<select id="hain2CultureSent" name="hain2CultureSent" >
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${sentResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hain2CultureSentDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addHain2Test" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelHain2" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
							
							</table>
							
						</div>
						<!-- END OF HAIN2 DIV -->
					</form>
					
				</div>
				
				<!-- Start Culture Div -->
				<div id="cultureDiv">
				
					<form id="addCultureResults" name="addCultureResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=culture&cultureId=0" method="post">
						<input type='hidden' id= "mgit_sub" name="mgit_sub" value='' />
						<input type='hidden' id= "lj_sub" name="lj_sub" value='' />
						<input type='hidden' id= "rc_sub" name="rc_sub" value='' />
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.culture" text="Culture"/>
						</b>
						
						<div class="box">
							
							<table cellspacing="5" cellpadding="0">
							
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.incoculationDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="incoculationDate" startValue=""/>
									</td>
								</tr>
								<%-- 
								<tr >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.culture.result"/>:</font>
										&nbsp;
										<select id="cultureResult" name="cultureResult">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${microscopyResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
									</td>
								</tr>
								--%>
								<!-- REQUIREMENT #10 START -->
								<tr id="cultreLabWorkerName_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="cultureLabWorker" id="cultureLabWorker">
									</td>
								</tr>
								<tr id="cultreLabNo_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="cultureLabNo" id="cultureLabNo">
									</td>
								</tr>
								
								<tr id="placeOfCulture_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.culturePlace"/>:</font>
										&nbsp;
										<select id="culturePlace" name="culturePlace">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${culturePlaces}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								<tr id = "add_culture_type">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.addResultFor"/> : </font>
										&nbsp;
										<select id="culture_selected">
											<option hidden selected value> </option>
											<option value="MGIT"><spring:message code="labmodule.labEntry.mgit"/></option>
											 <option value="LJ"><spring:message code="labmodule.labEntry.lj"/></option>
											 <option value="RE-CULTURE"><spring:message code="labmodule.labEntry.recultureContamintatedTubes"/></option>										 
										</select>
										&nbsp;
										<img title="Add" class="add" id="addCultureType" onclick="onClick(this,'')" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										<button hidden id="cancelTest" type=reset text="Cancel"/>
									</td>
								</tr>
								
								<tr id="mgit_row">
									<td>									
										<div id="mgit_culture" style = "display: none;">
												<table class="box" id="table_mgit_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.mgit"/>:</font>
														</td>
													</tr>
													<tr id="mgit_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.mgitGrowthDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="mgit_growthDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="mgit_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMgitMgit"/>:</font>
															&nbsp;
															<select id="mgit_mgit" name="mgit_mgit" onchange="onChangeMgit(this)">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${mgitResults}">
																			<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																		</c:forEach>
															</select>
															
														</td>
													</tr>
														
													<tr id="mgit_row3">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
															&nbsp;
															<select id="mgit_mtid" name="mgit_mtid" >
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${mtidResults}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
												</table>
										</div>
										
									</td>
								</tr>
							
								<!-- LJ Row -->
								<tr id="lj_row">
									<td>									
										<div id="lj_culture" style = "display: none;">
												<table class="box" id="table_lj_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.lj"/>:</font>
														</td>
													</tr>
													<tr id="lj_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.ljGrowthDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="lj_growthDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="lj_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.ljCultureResult"/>:</font>
															&nbsp;
															<select id="lj_lj" name="lj_lj" onchange="onChangeMgit(this)">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${ljResults}">
																			<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																		</c:forEach>
															</select>
															
														</td>
													</tr>
														
													<tr id="lj_row3">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
															&nbsp;
															<select id="lj_mtid" name="lj_mtid" >
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${mtidResults}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
												</table>
										</div>
										
									</td>
								</tr>
								<!-- RE-CULTURE Row-->
								<tr id="rc_row">
									<td>									
										<div id="rc_culture" style = "display: none;">
												<table class="box" id="table_rc_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.recultureContamintatedTubes"/>:</font>
														</td>
													</tr>
													<tr id="rc_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcContaminationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="rc_inoculation" startValue=""/>
															
														</td>
													</tr>
													<tr id="rc_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcGrowthDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="rc_growthDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="rc_row3">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcContaminationResult"/>:</font>
															&nbsp;
															<select id="rc_rc" name="rc_rc" onchange="onChangeMgit(this)">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${mgitResults}">
																			<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																		</c:forEach>
															</select>
															
														</td>
													</tr>
														
													<tr id=rc_row4">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
															&nbsp;
															<select id="rc_mtid" name="rc_mtid">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${mtidResults}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
												</table>
										</div>
										
									</td>
								</tr>
								
								<tr class="boxHeader" style="margin:0px; width:100%;">
									&nbsp;
									<td><spring:message code="labmodule.reportedCultureResult" text="Reported Culture Result"/><td>
								</tr>
																
								 <tr id="mgitCultureResult">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMgit"/>:</font>
										&nbsp;
										<select id="mgitResults" name="mgitResults" onchange="onChangeMgit(this)">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${mgitResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								
								<tr id="mtTestId" >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureIdentificationBacteria"/>:</font>
										&nbsp;
										<select id="mtidResults" name="mtidResults" disabled="true">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${mtidResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="cultureType">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureTypeReported"/>:</font>
										&nbsp;
										<select id="cultureType" name="cultureType">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${cultureTypes}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="DateOfReportingCulture">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureResultReportingDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="cultureReportingDate" startValue=""/>
										
									</td>
								</tr>
								
								<!-- REQUIREMENT #10 END   -->
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addCultureTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelCulture" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
								
							</table>
							
						</div>
					
					</form>
				
				</div>
				<!-- END of Culture Div -->
				<!-- DST-1 DIV -->
				<div id="dst1div">
					<form id="addDst1Results" name="addDst1Results" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=dst1&dst1Id=0" method="post">
						<input type='hidden' id= "dst1_mgit_sub" name="dst1_mgit_sub" value='' />
						<input type='hidden' id= "dst1_lj_sub" name="dst1_lj_sub" value='' />
					
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.firstLineDsts" text="DST 1st Line"/>
						</b>
						<div class="box">
							<table id="dst1_New" cellspacing="5" cellpadding="0">
							
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
									</td>
								</tr>
								
								<tr id="dstLabNo_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="dst1labNo" id="dst1labNo">
									</td>
								</tr>
								
								<tr id="dstLabWorker_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="dst1LabWorker" id="dst1LabWorker">
									</td>
								</tr>
								
								<tr id="dstLabLocation_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dst1Location"/>:</font>
										&nbsp;
										<select id="dst1LabLocation" name="dst1LabLocation">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${culturePlaces}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
							
								<tr id = "add_dst1_type">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.addResultFor"/> : </font>
										&nbsp;
										<select id="dst1_selected">
											<option hidden selected value> </option>
											<option value="MGIT"><spring:message code="labmodule.labEntry.firstLineDstMgit"/></option>
											 <option value="LJ"><spring:message code="labmodule.labEntry.firstLineDstLj"/></option>
										</select>
										&nbsp;
										<img title="Add" class="add" id="addDst1Type" onclick="onClick(this,'')" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										<button hidden id="cancelTest" type=reset text="Cancel"/>
									</td>
								</tr>
								
								<tr id="dst1_mgit_row">
									<td>									
										<div id="mgit_dst1" style = "display: none;">
												<table class="box" id="table_dst1_mgit_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgit"/>:</font>
														</td>
													</tr>
													<tr id="dst1_mgit_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitInoculationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_mgit_inoculationDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="dst1_mgit_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitReadingDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_mgit_readingDate" startValue=""/>
															
														</td>
													</tr>
														
													<tr id="dst1_mgit_row3">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
															&nbsp;
															<select id="dst1_mgit_s" name="dst1_mgit_s">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													<tr id="dst1_mgit_row4">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
															&nbsp;
															<select id="dst1_mgit_h" name="dst1_mgit_h">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															</td>
													</tr>
													<tr id="dst1_mgit_row5">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
																&nbsp;
																<select id="dst1_mgit_r" name="dst1_mgit_r">
															     <option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																	</c:forEach>
																</select>
														</td>
													</tr>
													<tr id="dst1_mgit_row6">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
															&nbsp;
															<select id="dst1_mgit_e" name="dst1_mgit_e">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
													<tr id="dst1_mgit_row7">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
															&nbsp;
															<select id="dst1_mgit_z" name="dst1_mgit_z">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
													<tr id="dst1_mgit_row8">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
															&nbsp;
															<select id="dst1_mgit_lfx" name="dst1_mgit_lfx">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
												</table>
										</div>
										
									</td>
								</tr>
							
								<!-- LJ Row -->
								<tr id="dst1_lj_row">
									<td>									
										<div id="lj_dst1" style = "display: none;">
												<table class="box" id="table_dst1_lj_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLj"/>:</font>
														</td>
													</tr>
													<tr id="dst1_lj_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjInoculationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_lj_inoculationDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="dst1_lj_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjReadingDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_lj_readingDate" startValue=""/>
															
														</td>
													</tr>
														
													<tr id="dst1_lj_row3">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
															&nbsp;
															<select id="dst1_lj_s" name="dst1_lj_s">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													<tr id="dst1_lj_row4">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
															&nbsp;
															<select id="dst1_lj_h" name="dst1_lj_h">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
													<tr id="dst1_lj_row5">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
															&nbsp;
															<select id="dst1_lj_r" name="dst1_lj_r">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
													<tr id="dst1_lj_row6">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
															&nbsp;
															<select id="dst1_lj_e" name="dst1_lj_e">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
													<tr id="dst1_lj_row7">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
															&nbsp;
															<select id="dst1_lj_z" name="dst1_lj_z">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
													<tr id="dst1_lj_row8">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
															&nbsp;
															<select id="dst1_lj_lfx" name="dst1_lj_lfx">
														     <option hidden selected value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
														</td>	
													</tr>
												</table>
										</div>
										
									</td>
								</tr>
								
								<tr class="boxHeader" style="margin:0px; width:100%;">
									&nbsp;
									<td><spring:message code="labmodule.labEntry.firstLineDstResults" text="Reported DST 1st Line Result"/><td>
								</tr>
																					
								
								<tr id="dst1ResistenceS">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
										&nbsp;
										<select id="dst1S" name="dst1S">
											     <option  value=""></option>
													<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								
								<tr id="dst1ResistenceH">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
										&nbsp;
										<select id="dst1H" name="dst1H">
											     <option value=""> </option>
													<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								
								<tr id="dst1ResistenceR">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
										&nbsp;
										<select id="dst1R" name="dst1R">
											     <option  value=""></option>
													<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="dst1ResistenceE">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
										&nbsp;
										<select id="dst1E" name="dst1E">
											     <option value=""></option>
													<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="dst1ResistenceZ">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
										&nbsp;
										<select id="dst1Z" name="dst1Z">
											     <option value=""></option>
													<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="dst1_rowLfx">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
									&nbsp;
									<select id="dst1Lfx" name="dst1Lfx">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
								</tr>
								<tr id="dst1Type_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dstMethodType"/>:</font>
										&nbsp;
										<select id="dst1Type" name="dst1Type">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${dstTypes}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="DateOfReportingDst1_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstResultsDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="dst1ReportingDate" startValue=""/>
										
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addDst1" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelDst1" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
								
							</table>
						</div>
					</form>
				</div>	
				<!-- END of DST-1 div -->
				<!-- DST-2 DIV -->
				<div id="dst2div">
					<form id="addDst2Results" name="addDst2Results" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=dst2&dst2Id=0" method="post">
						<input type='hidden' id= "dst2_mgit_sub" name="dst2_mgit_sub" value='' />
						<input type='hidden' id= "dst2_lj_sub" name="dst2_lj_sub" value='' />
					
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.firstLineDsts" text="DST 2nd Line"/>
						</b>
						<div class="box">
							<table id="dst2_New" cellspacing="5" cellpadding="0">
							
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="45">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
									</td>
								</tr>
								<tr id="dst2LabNo_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="dst2LabNo" id="dst2LabNo">
									</td>
								</tr>
								
								<tr id="dst2LabWorker_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="dst2LabWorker" id="dst2LabWorker">
									</td>
								</tr>
								
								<tr id="dst2_mgit_labLocation_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLocation"/>:</font>
										&nbsp;
										<select id="dst2LabLocation" name="dst2LabLocation">
									     <option hidden selected value=""></option>
											<c:forEach var="result" items="${culturePlaces}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id = "add_dst2_type">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.addResultFor"/> : </font>
										&nbsp;
										<select id="dst2_selected">
											 <option hidden selected value=""></option>
											<option value="MGIT"><spring:message code="labmodule.labEntry.secondLineDst"/></option>
											<option value="LJ"><spring:message code="labmodule.labEntry.secondLineDstLj"/></option>
										</select>
										&nbsp;
										<img title="Add" class="add" id="addDst2Type" onclick="onClick(this,'')" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										<button hidden id="cancelTest" type=reset text="Cancel"/>
									</td>
								</tr>
								
								<tr id="dst2_mgit_row">
									<td>									
										<div id="mgit_dst2" style = "display: none;">
												<table class="box" id="table_dst2_mgit_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstMgit"/>:</font>
														</td>
													</tr>
													<tr id="dst2_mgit_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstMgitInoculationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst2_mgit_inoculationDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="dst2_mgit_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstMgitReadingDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst2_mgit_readingDate" startValue=""/>
															
														</td>
													</tr>
														
													
													<tr id="dst2_mgit_rowOfx">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
															&nbsp;
															<select id="dst2Ofx_mgit" name="dst2Ofx_mgit">
														    <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													<tr id="dst2_mgit_rowMox">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
															&nbsp;
															<select id="dst2Mox_mgit" name="dst2Mox_mgit">
														    <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
												
													 	
													<%-- <tr id="dst2_mgit_rowLfx">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
															&nbsp;
															<select id="dst2Lfx_mgit" name="dst2Lfx_mgit">
														     <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>  --%>
													
													
													
													<tr id="dst2_mgit_rowPto">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
															&nbsp;
															<select id="dst2Pto_mgit" name="dst2Pto_mgit">
														     <option value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowLzd">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
															&nbsp;
															<select id="dst2Lzd_mgit" name="dst2Lzd_mgit">
														     <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowCfz">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
															&nbsp;
															<select id="dst2Cfz_mgit" name="dst2Cfz_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowBdq">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
															&nbsp;
															<select id="dst2Bdq_mgit" name="dst2Bdq_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowDlm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
															&nbsp;
															<select id="dst2Dlm_mgit" name="dst2Dlm_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowPas">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
															&nbsp;
															<select id="dst2Pas_mgit" name="dst2Pas_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowCm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
															&nbsp;
															<select id="dst2Cm_mgit" name="dst2Cm_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_mgit_rowKm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
															&nbsp;
															<select id="dst2Km_mgit" name="dst2Km_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													<tr id="dst2_mgit_rowAm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
															&nbsp;
															<select id="dst2Am_mgit" name="dst2Am_mgit">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
												</table>
										</div>
										
									</td>
								</tr>
								<tr id="dst2_lj_row">
									<td>									
										<div id="lj_dst2" style = "display: none;">
												<table class="box" id="table_dst2_lj_1">
													<tr>
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLj"/>:</font>
														</td>
													</tr>
													<tr id="dst2_lj_row1">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLjInoculationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst2_lj_inoculationDate" startValue=""/>
															
														</td>
													</tr>
													<tr id="dst2_lj_row2">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLjReadingDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst2_lj_readingDate" startValue=""/>
															
														</td>
													</tr>
														
													
													<tr id="dst2_lj_rowOfx">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
															&nbsp;
															<select id="dst2Ofx_lj" name="dst2Ofx_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													<tr id="dst2_lj_rowMox">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
															&nbsp;
															<select id="dst2Mox_lj" name="dst2Mox_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													 	
													<%-- <tr id="dst2_lj_rowLfx">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
															&nbsp;
															<select id="dst2Lfx_lj" name="dst2Lfx_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>  --%>
													
													
													
													<tr id="dst2_lj_rowPto">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
															&nbsp;
															<select id="dst2Pto_lj" name="dst2Pto_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowLzd">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
															&nbsp;
															<select id="dst2Lzd_lj" name="dst2Lzd_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowCfz">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
															&nbsp;
															<select id="dst2Cfz_lj" name="dst2Cfz_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowBdq">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
															&nbsp;
															<select id="dst2Bdq_lj" name="dst2Bdq_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowDlm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
															&nbsp;
															<select id="dst2Dlm_lj" name="dst2Dlm_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowPas">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
															&nbsp;
															<select id="dst2Pas_lj" name="dst2Pas_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowCm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
															&nbsp;
															<select id="dst2Cm_lj" name="dst2Cm_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													
													<tr id="dst2_lj_rowKm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
															&nbsp;
															<select id="dst2Km_lj" name="dst2Km_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
													<tr id="dst2_lj_rowAm">
														<td>
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
															&nbsp;
															<select id="dst2Am_lj" name="dst2Am_lj">
														      <option  value=""></option>
																<c:forEach var="result" items="${drugResistance}">
																	<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
																</c:forEach>
															</select>
															
														</td>
													</tr>
													
												</table>
										</div>
										
									</td>
								</tr>
								<tr class="boxHeader" style="margin:0px; width:100%;">
									&nbsp;
									<td><spring:message code="labmodule.labEntry.secondLineDst" text="Reported DST 1st Line Result"/><td>
								</tr>
								
	

							<tr id="dst2_rowOfx">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
									&nbsp;
									<select id="dst2Ofx" name="dst2Ofx">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							<tr id="dst2_rowMox">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
									&nbsp;
									<select id="dst2Mox" name="dst2Mox">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
								<%-- 
							<tr id="dst2_rowLfx">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
									&nbsp;
									<select id="dst2Lfx" name="dst2Lfx">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr> --%>
							
							
							
							<tr id="dst2_rowPto">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
									&nbsp;
									<select id="dst2Pto" name="dst2Pto">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							
							<tr id="dst2_rowLzd">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
									&nbsp;
									<select id="dst2Lzd" name="dst2Lzd">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							
							<tr id="dst2_rowCfz">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
									&nbsp;
									<select id="dst2Cfz" name="dst2Cfz">
									  <option  value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>


								<tr id="dst2_rowBdq">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
										&nbsp;
										<select id="dst2Bdq" name="dst2Bdq">
										  <option  value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								
								
								<tr id="dst2_rowDlm">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
										&nbsp;
										<select id="dst2Dlm" name="dst2Dlm">
										  <option  value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								
								
								<tr id="dst2_rowPas">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
										&nbsp;
										<select id="dst2Pas" name="dst2Pas">
										  <option  value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								
								
								<tr id="dst2_rowCm">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
										&nbsp;
										<select id="dst2Cm" name="dst2Cm">
										  <option  value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								
								
								<tr id="dst2_rowKm">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
										&nbsp;
										<select id="dst2Km" name="dst2Km">
										  <option  value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								
								<tr id="dst2_rowAm">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
										&nbsp;
										<select id="dst2Am" name="dst2Am">
										  <option  value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
										</select>
										
									</td>
								</tr>
								<tr id="dst2Type_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dstMethodType"/>:</font>
										&nbsp;
										<select id="dst2Type" name="dst2Type">
												  <option  hiddent selected value=""></option>
													<c:forEach var="result" items="${dstTypes}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
										
									</td>
								</tr>
								
								<tr id="DateOfReportingDst1_new">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstResultsDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="dst2ReportingDate" startValue=""/>
										
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addDst2" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelDst2" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
																
							</table>
					</form>
									
				</div>	
				<!-- END of DST-2 div -->
				
			</td>
		</tr>
			<!-- END OF LAB RESULT -->
	</table>
	
	<br>

	
	<b class="boxHeader" style="margin:0px; width:100%;">
		&nbsp;
		<spring:message code="labmodule.labResults" text="Lab Results"/>
	</b>
	
	<div id="viewLabResults" class="box" style="margin:0px">

	<table cellspacing="5" cellpadding="0" width="100%">
	
	<c:if test="${fn:length(labResult.microscopies) > 0}">
	
		<c:forEach var="microscopy" items="${labResult.microscopies}" varStatus="i">
			<tr>
				<td>
				<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.bacterioscopy"/> ( ${i.count} )
					<span id="microscopy_edit_span_${i.count}" name="specimen_edit_span" style="float:right">
						<openmrs:hasPrivilege privilege="Edit Test Result">
							<img title="Edit" id="editMicroscopySpan_${i.count}" class="edit" onclick='editTest(this,"${microscopy.lab}","${i.count}","microscopy",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="edit" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
						</openmrs:hasPrivilege>
						<openmrs:hasPrivilege privilege="Delete Test Result">
							<img title="Delete" id="deleteMicroscopySpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
						</openmrs:hasPrivilege>
					</span>
					</b>
				</td>
			</tr>
			
			<tr><td>
			
			<form id="deleteMicroscopyResults_${i.count}" name="deleteMicroscopyResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>
				
					<input hidden type="text" name="labResultId" value="${labResult.id}">
					<input hidden type="text" name="id" value="${microscopy.id}">
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${microscopy.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
			
			<tr>
				<td>				
					<font style="font-weight:bold"><spring:message code="mdrtb.labEntry.labWorker" text="Lab"/>:</font>
					&nbsp;
					${microscopy.lab.displayString}
					<span id="microscopy_lab_${i.count}"></span>
					<%-- ${microscopy.lab} --%>
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="Number"/>:</font>
					&nbsp;
					${microscopy.labId}
				</td>
			</tr>					
			 <tr>
				<td>
				<font style="font-weight:bold"><spring:message code="mdrtb.labEntry.labWorker" text="Lab"/>:</font>
					&nbsp;
					${microscopy.labSpecialistName}
					
				</td>
			</tr>
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:</font>
					&nbsp;
					${microscopy.sampleApperence.displayString}
						
					&nbsp;&nbsp;
						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.result"/>:</font>
					&nbsp;
					${microscopy.sampleResult.displayString}
				</td>
			</tr>
			
								
			</table>
			</form>
			
			<form hidden id="editMicroscopyResults_${i.count}" name="editMicroscopyResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=microscopy" method="post">
			<table  cellspacing="5" style="font-size: 13px">
			
			<tr>
				<td>
				
					<input hidden type="text" name="labResultId" value="${labResult.id}">
					<input hidden type="text" name="microscopyId" value="${microscopy.id}">
					<input hidden type="text" name="provider" value="45">
					<input hidden type="text" name="lab" value="${labResult.location.locationId}">
					<input hidden type="text" name="count" value="${i.count}">
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="sampleDate_${i.count}" startValue="${microscopy.resultDate}"/>
				
				</td>
			</tr>
			<tr>
				<td><font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font></td>
			</tr>
			<tr>
				<td width="500">
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
					<select name="microscopy_oblast_${i.count}" id="microscopy_oblast_${i.count}" onchange="getDistrictsInAddTest(${i.count},'microscopy')">
						<option value=""></option>
						<c:forEach var="o" items="${oblasts}">
							<option value="${o.id}">${o.name}</option>
						</c:forEach>
					</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
				<select name="microscopy_district_${i.count}" id="microscopy_district_${i.count}"
					onchange="getFacilitiesInEditTest(${i.count},'microscopy')">
						<option value=""></option>
				</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
						<select name="microscopy_facility_${i.count}" id="microscopy_facility_${i.count}">
						
				</select></td>
			</tr>
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="micrscopyLabNumber_${i.count}" name="micrscopyLabNumber_${i.count}" value="${microscopy.labId}">
				</td>
			</tr>
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labWorker" text="Lab Worker"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="micrscopyLabWorker_${i.count}" name="micrscopyLabWorker_${i.count}" value="${microscopy.labSpecialistName}">
				</td>
			</tr>						
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:</font>
					&nbsp;
					<select id="sampleAppearance_${i.count}" name="sampleAppearance_${i.count}">
					<option hidden selected value=""></option>
						<c:forEach var="appearance" items="${appearances}">
							<option value="${appearance.answerConcept.id}" <c:if test="${appearance.answerConcept == microscopy.sampleApperence}">selected</c:if>>${appearance.answerConcept.displayString}</option>
						</c:forEach>
					</select>
						
					&nbsp;&nbsp;
						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.result"/>:</font>
					&nbsp;
					<select id="sampleResult_${i.count}" name="sampleResult_${i.count}">
						<c:forEach var="result" items="${microscopyResults}">
							<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == microscopy.sampleResult}">selected</c:if>>${result.answerConcept.displayString}</option>
						</c:forEach>
					</select>
					
				</td>
			</tr>
			
			
			<tr> <td>
			   <br>
				<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateMicroscopy_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
				</openmrs:hasPrivilege>
				<button type="reset" id="cancelUpdateMicroscopy_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
			</td></tr>
								
			</table>
			</form>
				
		</c:forEach>
	
	</c:if>
	
	<c:if test="${fn:length(labResult.xperts) > 0}">
	
		<c:forEach var="xpert" items="${labResult.xperts}" varStatus="i">
		   				
				<tr>
					<td>
						<br>
						<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.xpert"/> ( ${i.count} )
						<span id="xpert_edit_span_${i.count}" name="xpert_edit_span" style="float:right">
							<openmrs:hasPrivilege privilege="Edit Test Result">
						    	<img title="Edit" id="editXpertSpan_${i.count}" class="edit" onclick='editTest(this,"${xpert.lab}","${i.count}","xpert",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
						    <openmrs:hasPrivilege privilege="Delete Test Result">
						    	<img title="Delete" id="deleteXpertSpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
						</span>
						</b>
					</td>
				</tr>
				
				<tr><td>
			
				<form id="deleteXpertResults_${i.count}" name="deleteXpertResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

				<table style="font-size: 13px">
				
				<tr>
					<td>
					
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${xpert.id}">
					</td>
				</tr>
				<tr>
					<td>				
						<font style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
						&nbsp;
						${xpert.lab.displayString}
						&nbsp;&nbsp;
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="Number"/>:</font>
						&nbsp;
						${xpert.labId}
					</td>
				</tr>		
				
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
						&nbsp;
						${xpert.result.displayString}
						&nbsp;&nbsp;
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
						&nbsp;
						${xpert.rifResistance.displayString}
					</td>
				</tr>
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.errorCode"/>:</font>
						&nbsp;
						${xpert.errorCode}
					</td>
				</tr>
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
						&nbsp;
						${xpert.sentToDst.displayString}
						&nbsp;&nbsp;
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${xpert.sentToDstDate}" format="${_dateFormatDisplay}"/> 
					</td>
				</tr>		
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
						&nbsp;
						${xpert.sentToCulture.displayString}
						&nbsp;&nbsp;
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${xpert.sentToCultureDate}" format="${_dateFormatDisplay}"/>
					</td>
				</tr>
				
				</table>
				</form>
				
				<form hidden id="editXpertResults_${i.count}" name="editXpertResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=xpert" method="post">

				<table  cellspacing="5" style="font-size: 13px">
				
				<tr>
					<td>
					
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="xpertId" value="${xpert.id}">
						<input hidden type="text" name="provider" value="45">
						<input hidden type="text" name="lab" value="${labResult.location.locationId}">
						<input hidden type="text" name="count" value="${i.count}">
						
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
						&nbsp;
						<openmrs_tag:dateField formFieldName="xpertTestDate_${i.count}" startValue="${xpert.resultDate}"/>
					</td>
				</tr>
				<tr>
				<td><font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font></td>
			</tr>
			<tr>
				<td width="500">
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
					<select name="xpert_oblast_${i.count}" id="xpert_oblast_${i.count}" onchange="getDistrictsInAddTest(${i.count},'xpert')">
						<option value=""></option>
						<c:forEach var="o" items="${oblasts}">
							<option value="${o.id}">${o.name}</option>
						</c:forEach>
					</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
				<select name="xpert_district_${i.count}" id="xpert_district_${i.count}"
					onchange="getFacilitiesInEditTest(${i.count},'xpert')">
						<option value=""></option>
				</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
						<select name="xpert_facility_${i.count}" id="xpert_facility_${i.count}">
						
				</select></td>
			</tr>
					
				<tr>
					<td>		
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
						&nbsp;
						<input type="text"  size="10" id="mtbLabNumber_${i.count}" name="mtbLabNumber_${i.count}" value="${xpert.labId}">
					</td>
				</tr>		
				<tr>
					<td>	
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${xpert.resultDate}" format="${_dateFormatDisplay}"/>
					</td>
				</tr>			
				<tr>
					<td>
					
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
						&nbsp;
						<select id="mtbXpertResult_${i.count}" name="mtbResult_${i.count}" onChange="onChangeMtb(this)">
								<c:forEach var="result" items="${mtbResults}">
									<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == xpert.result}">selected</c:if>>${result.answerConcept.displayString}</option>
								</c:forEach>
						</select>
						
						&nbsp;&nbsp;
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
						&nbsp;
						<select id = "rifXpertResult_${i.count}" name = "rifResult_${i.count}"  <c:if test="${xpert.rifResistance == null}"> disabled </c:if> onChange="onChangeRif(this)">
								<option hidden value=""></option>
								<c:forEach var="result" items="${rifResults}">
									<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == xpert.rifResistance}">selected</c:if> >${result.answerConcept.displayString}</option>
								</c:forEach>
						</select>
					</td>
				</tr>
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.errorCode"/>:</font>
						&nbsp;
						<input type="text"  size="10" name="xpertError_${i.count}" id="xpertError_${i.count}" value="${xpert.errorCode}" <c:if test="${xpert.rifResistance == null}"> disabled </c:if>>
					</td>
				</tr>
				<!-- new fields -->
			<tr>
				
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
					&nbsp;
					<select id="xpertDstSent_${i.count}" name="xpertDstSent_${i.count}" <c:if test="${xpert.sentToDst == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${sentResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == xpert.sentToDst}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
				
				</td>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="xpertDstSentDate_${i.count}" startValue="${xpert.sentToDstDate}"/>
				</td>
			</tr>
			<tr>
				
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
					&nbsp;
					<select id="xpertCultureSent_${i.count}" name="xpertCultureSent_${i.count}" <c:if test="${xpert.sentToDst == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${sentResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == xpert.sentToDst}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
				
				</td>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="xpertCultureSentDate_${i.count}" startValue="${xpert.sentToCultureDate}"/>
				</td>
			</tr>
			
				<tr> <td>
			   <br>
				<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateXpert_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
				</openmrs:hasPrivilege>
				<button type="reset" id="cancelUpdateXpert_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
			</td></tr>
				
				</table>
				</form>
				</td></tr>
					
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.HAINS) > 0}">
	
		<c:forEach var="hain" items="${labResult.HAINS}" varStatus="i">
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.hain"/> ( ${i.count} )
					<span id="hain_edit_span_${i.count}" name="hain_edit_span" style="float:right">
							<openmrs:hasPrivilege privilege="Edit Test Result">
						    	<img title="Edit" id="editHainSpan_${i.count}" class="edit" onclick='editTest(this,"${hains.lab}","${i.count}","hains",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Delete Test Result">
							<img title="Delete" id="deleteHainSpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
						</span>
					</b>
				</td>
			</tr>
			
	<tr><td>
			<form id="deleteHainResults_${i.count}" name="deleteHainResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

		<table style="font-size: 13px">
			
			<tr>
				<td>
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${hain.id}">
				</td>
			</tr>
			<tr>
				<td>				
					<font style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
					&nbsp;
					${hain.lab.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="Number"/>:</font>
					&nbsp;
					${hain.labId}
					&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
					&nbsp;
					${hain.labSpecialistName}
				</td>
			</tr>
			
						
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
						
			<tr>
				<td>
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					${hain.result.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
					&nbsp;
					${hain.rifResistance.displayString}			
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.h"/>:</font>
					&nbsp;
					${hain.inhResistance.displayString}				
				</td>
		  </tr>
		  <tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
					&nbsp;
					${hain.sentToDst.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain.sentToDstDate}" format="${_dateFormatDisplay}"/> 
				</td>
			</tr>		
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
					&nbsp;
					${hain.sentToCulture.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain.sentToCultureDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
		  
		  </table>
		  </form>
		  
		  <form hidden id="editHainResults_${i.count}" name="editHainResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=hain" method="post">

		<table  cellspacing="5"  style="font-size: 13px">
			
			<tr>
				<td>
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="hainId" value="${hain.id}">
						<input hidden type="text" name="provider" value="45">
						<input hidden type="text" name="lab" value="${labResult.location.locationId}">
						<input hidden type="text" name="count" value="${i.count}">
				</td>
			</tr>
			<tr>
				<td><font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font></td>
			</tr>
			<tr>
				<td width="500">
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
					<select name="hain_oblast_${i.count}" id="hain_oblast_${i.count}" onchange="getDistrictsInAddTest(${i.count},'hain')">
						<option value=""></option>
						<c:forEach var="o" items="${oblasts}">
							<option value="${o.id}">${o.name}</option>
						</c:forEach>
					</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
				<select name="hain_district_${i.count}" id="hain_district_${i.count}"
					onchange="getFacilitiesInEditTest(${i.count},'hain')">
						<option value=""></option>
				</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
						<select name="hain_facility_${i.count}" id="hain_facility_${i.count}">
					</select>

				</td>
			</tr>	
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labWorker" text="Lab Worker"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="hainLabWorker_${i.count}" name="hainLabWorker_${i.count}" value="${hain.labSpecialistName}">
				</td>
			</tr>	

			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="inhLabNumber_${i.count}" name="inhLabNumber_${i.count}" value="${hain.labId}">
				</td>
			</tr>		
			
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="hainTestDate_${i.count}" startValue="${hain.resultDate}"/>
				</td>
			</tr>
						
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					<select id="mtbHainResult_${i.count}" name="mtbResult_${i.count}" onChange="onChangeMtb(this)">
							<c:forEach var="result" items="${mtbResults}">
								<option value="${result.answerConcept.id}"  <c:if test="${result.answerConcept == hain.result}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
					&nbsp;
					<select id = "rifHainResult_${i.count}" name = "rifResult_${i.count}" <c:if test="${hain.rifResistance == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${rifResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain.rifResistance}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>		
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.h"/>:</font>
					&nbsp;
					<select id = "inhHainResult_${i.count}" name = "inhResult_${i.count}" <c:if test="${hain.inhResistance == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${inhResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain.inhResistance}">selected</c:if> >${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>			
				</td>
		  </tr>
		  <!-- new fields -->
			<tr>
				
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
					&nbsp;
					<select id="hainDstSent_${i.count}" name="hainDstSent_${i.count}" <c:if test="${hain.sentToDst == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${sentResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain.sentToDst}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
				
				</td>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="hainDstSentDate_${i.count}" startValue="${hain.sentToDstDate}"/>
				</td>
			</tr>
			<tr>
				
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
					&nbsp;
					<select id="hainCultureSent_${i.count}" name="hainCultureSent_${i.count}" <c:if test="${hain.sentToDst == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${sentResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain.sentToDst}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
				
				</td>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="hainCultureSentDate_${i.count}" startValue="${hain.sentToCultureDate}"/>
				</td>
			</tr>
		  
		    <tr> <td>
			   <br>
				<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateHain_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
				</openmrs:hasPrivilege>
				<button type="reset" id="cancelUpdateHain_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
			</td></tr>
		  
		  </table>
		  </form>
		  
		  </td></tr>
			
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.HAINS2) > 0}">
	
		<c:forEach var="hain2" items="${labResult.HAINS2}" varStatus="i">
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.hain2"/> ( ${i.count} )
					<span id="hain2_edit_span_${i.count}" name="hain2_edit_span" style="float:right">
							<openmrs:hasPrivilege privilege="Edit Test Result">
						    	<img title="Edit" id="editHain2Span_${i.count}" class="edit" onclick='editTest(this,"${hain2.lab}","${i.count}","hain2",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Delete Test Result">
							<img title="Delete" id="deleteHain2Span_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
					</span></b>
				</td>
			</tr>
			
			<tr><td>
			<form id="deletehain2Results_${i.count}" name="deletehain2Results_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>				
					<font style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
					&nbsp;
					${hain2.lab.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="Number"/>:</font>
					&nbsp;
					${hain2.labId}
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
					&nbsp;
					${hain2.labSpecialistName}
				</td>
			</tr>
			
			<tr>
				<td>
				
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${hain2.id}">
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.analysisDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain2.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					${hain2.result.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.mox"/>:</font>
					&nbsp;
					${hain2.moxResistance.displayString} 
				</td>
			</tr>
					
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.km"/>:</font>
					&nbsp;
					${hain2.cmResistance.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.e"/>:</font>
					&nbsp;
					${hain2.erResistance.displayString} 
				</td>
			</tr> 
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
					&nbsp;
					${hain2.sentToDst.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain2.sentToDstDate}" format="${_dateFormatDisplay}"/> 
				</td>
			</tr>		
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
					&nbsp;
					${hain2.sentToCulture.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain2.sentToCultureDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
			
			</table>
			</form>
			
			<form hidden id="edithain2Results_${i.count}" name="edithain2Results_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=hain2" method="post">

			<table  cellspacing="5" style="font-size: 13px">
			
			<tr>
				<td>
				
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="hain2Id" value="${hain2.id}">
						<input hidden type="text" name="provider" value="45">
						<input hidden type="text" name="lab" value="${labResult.location.locationId}">
						<input hidden type="text" name="count" value="${i.count}">
				</td>
			</tr>
			<tr>
				<td><font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font></td>
			</tr>
			<tr>
				<td width="500">
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
					<select name="hain2_oblast_${i.count}" id="hain2_oblast_${i.count}" onchange="getDistrictsInAddTest(${i.count},'hain2')">
						<option value=""></option>
						<c:forEach var="o" items="${oblasts}">
							<option value="${o.id}">${o.name}</option>
						</c:forEach>
					</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
				<select name="hain2_district_${i.count}" id="hain2_district_${i.count}"
					onchange="getFacilitiesInEditTest(${i.count},'hain2')">
						<option value=""></option>
				</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
						<select name="hain2_facility_${i.count}" id="hain2_facility_${i.count}">
					
				</select></td>
			</tr>	
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labWorker" text="Lab Worker"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="hain2LabWorker_${i.count}" name="hain2LabWorker_${i.count}" value="${hain2.labSpecialistName}">
				</td>
			</tr>	
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="hain2LabNumber_${i.count}" name="hain2LabNumber_${i.count}" value="${hain2.labId}">
				</td>
			</tr>		
			
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.analysisDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="hain2TestDate_${i.count}" startValue="${hain2.resultDate}"/>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					<select id="mtbHain2Result_${i.count}" name="mtbResult_${i.count}" onChange="onChangeMtb(this)">
							<c:forEach var="result" items="${mtbResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain2.result}">selected</c:if> >${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.mox"/>:</font>
					&nbsp;
					<select id="moxHain2Result_${i.count}" name="moxResult_${i.count}" <c:if test="${hain2.moxResistance == null}"> disabled </c:if> >
							<option hidden value=""></option>
							<c:forEach var="result" items="${moxResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain2.moxResistance}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.km"/>:</font>
					&nbsp;
					<select id="cmHain2Result_${i.count}" name="cmResult_${i.count}" <c:if test="${hain2.cmResistance == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${cmResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain2.cmResistance}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.e"/>:</font>
					&nbsp;
					<select id="erHain2Result_${i.count}" name="erResult_${i.count}" <c:if test="${hain2.erResistance == null}"> disabled </c:if>>
					     <option hidden value=""></option>
							<c:forEach var="result" items="${eResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain2.erResistance}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr> 
			<!-- new fields -->
			<tr>
				
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDst"/>:</font>
					&nbsp;
					<select id="hain2DstSent_${i.count}" name="hain2DstSent_${i.count}" <c:if test="${hain2.sentToDst == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${sentResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain2.sentToDst}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
				
				</td>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToDstDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="hain2DstSentDate_${i.count}" startValue="${hain2.sentToDstDate}"/>
				</td>
			</tr>
			<tr>
				
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCulture"/>:</font>
					&nbsp;
					<select id="hain2CultureSent_${i.count}" name="hain2CultureSent_${i.count}" <c:if test="${hain2.sentToDst == null}"> disabled </c:if>>
							<option hidden value=""></option>
							<c:forEach var="result" items="${sentResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == hain2.sentToDst}">selected</c:if>>${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
				
				</td>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.sentToCultureDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="hain2CultureSentDate_${i.count}" startValue="${hain2.sentToCultureDate}"/>
				</td>
			</tr>  
			 <tr> <td>
			   <br>
				<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateHain2_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
				</openmrs:hasPrivilege>
				<button type="reset" id="cancelUpdateHain2_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
			</td></tr>
			
			</table>
			</form>
			</td></tr>
		
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.cultures) > 0}">
	
		<c:forEach var="culture" items="${labResult.cultures}" varStatus="i">
		
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.culture"/> ( ${i.count} )
						<span id="culture_edit_span_${i.count}" name="culture_edit_span" style="float:right">
								<openmrs:hasPrivilege privilege="Edit Test Result">
						    	<img title="Edit" id="editCultureSpan_${i.count}" class="edit" onclick='editTest(this,"","","",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								</openmrs:hasPrivilege>
								<openmrs:hasPrivilege privilege="Delete Test Result">
								<img title="Delete" id="deleteCultureSpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								</openmrs:hasPrivilege>
						</span>
					</b>
				</td>
			</tr>
			
			<tr>
			<td>
			<form id="deleteSubCultureResults" name="deleteSubCultureResults" action="labEntry.form" method="post"></form>
			
			<form id="deleteCultureResults_${i.count}" name="deleteCultureResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${culture.id}">
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.incoculationDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${culture.inoculationDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
							
			<tr style="display:none;">
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.culture.result"/>:</font>
					&nbsp;
					${culture.result.displayString}
				</td>
			</tr> 
			<!-- REQUIREMENT #10 START -->
			<tr id="cultreLabWorker_view">
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
					&nbsp;
					${culture.labSpecialistName}
				</td>
			</tr>
			<tr id="cultreLabNo_view">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
						&nbsp;
						${culture.labNo}
					</td>
				</tr>
				
				<tr id="placeOfCulture_view">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.culturePlace"/>:</font>
						&nbsp;
						${culture.placeOfCulture.displayString}
					</td>
				</tr>
				<!-- MGIT SUBCULTURE HERE -->
				<tr id="mgit_subculture_view">
					<td>
						<c:if test="${fn:length(culture.mgitCultures) > 0}">
							<c:forEach var="mgit" items="${culture.mgitCultures}" varStatus="k">
							<table id ="mgit_view_subculture" class="box" >
								<tr class="box">
									<td> 
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.mgit"/>:</font>
										<span id="culture_edit_span_${i.count}" name="culture_edit_span" style="float:right">
											<openmrs:hasPrivilege privilege="Delete Test Result">
												<img title="Delete" id="deleteMgitSubculture_${k.count}" class="edit" onclick='deleteSubculture(${mgit.obsGroupId},${culture.id},${patientId},${labResult.id},"mgit" )' 
												src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
											</openmrs:hasPrivilege>
										</span>
									 </td>
								</tr>	
								
								<tr id="mgit_growthDate_view">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.mgitGrowthDate"/>:</font>
										&nbsp;
										<openmrs:formatDate date="${mgit.mgitGrowthDate}" format="${_dateFormatDisplay}"/>
									</td>
								</tr>
								<tr id="mgit_mgit_view" >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMgitMgit"/>:</font>
										&nbsp;
										${mgit.mgitResult.displayString}
										
									</td>
								</tr>
								
								<tr id="mgit_mtid_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
										&nbsp;
										${mgit.mtidTest.displayString}						
									</td>
								</tr>
								
								</table>					
							</c:forEach>
						</c:if>
					</td>
				</tr>
				<!-- LJ SUBCULTURE HERE -->
					<tr id="lj_subculture_view">
					<td>
						<c:if test="${fn:length(culture.ljCultures) > 0}">
							<c:forEach var="lj" items="${culture.ljCultures}" varStatus="y">
							<table id ="lj_view_subculture" class="box" >
								<tr class="box">
									<td> 
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.lj"/>:</font>
										<span id="culture_edit_span_${i.count}" name="culture_edit_span" style="float:right">
											<openmrs:hasPrivilege privilege="Delete Test Result">
												<img title="Delete" id="deleteMgitSubculture_${y.count}" class="edit" onclick='deleteSubculture(${lj.obsGroupId},${culture.id},${patientId},${labResult.id},"lj" )' 
												src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
											</openmrs:hasPrivilege>
										</span>
									 </td>
								</tr>	
								
								<tr id="lj_growthDate_view">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.ljGrowthDate"/>:</font>
										&nbsp;
										<openmrs:formatDate date="${lj.ljGrowthDate}" format="${_dateFormatDisplay}"/>
									</td>
								</tr>
								<tr id="lj_lj_view" >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.ljCultureResult"/>:</font>
										&nbsp;
										${lj.ljResult.displayString}
										
									</td>
								</tr>
								
								<tr id="lj_mtid_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
										&nbsp;
										${lj.mtIdTest.displayString}						
									</td>
								</tr>
								
								</table>					
							</c:forEach>
						</c:if>
					</td>
				</tr>

				<!-- RC SUBCULTURE HERE -->
				<tr id="rc_subculture_view">
					<td>
						<c:if test="${fn:length(culture.contaminatedTubes) > 0}">
							<c:forEach var="rc" items="${culture.contaminatedTubes}" varStatus="x">
							<table id ="rc_view_subculture" class="box" >
								<tr class="box">
									<td> 
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.recultureContamintatedTubes"/>:</font>
										<span id="culture_edit_span_${i.count}" name="culture_edit_span" style="float:right">
											<openmrs:hasPrivilege privilege="Delete Test Result">
												<img title="Delete" id="deleteMgitSubculture_${x.count}" class="edit" onclick='deleteSubculture(${rc.obsGroupId},${culture.id},${patientId},${labResult.id},"rc")' 
												src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
											</openmrs:hasPrivilege>
										</span>
									 </td>
								</tr>	
								
								<tr id="rc_contaminationDate_view">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcContaminationDate"/>:</font>
										&nbsp;
										<openmrs:formatDate date="${rc.repeatedDecontamination}" format="${_dateFormatDisplay}"/>
									</td>
								</tr>
		
								<tr id="rc_growthDate_view">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcGrowthDate"/>:</font>
										&nbsp;
										<openmrs:formatDate date="${rc.growthDate}" format="${_dateFormatDisplay}"/>
									</td>
								</tr>
								<tr id="rc_rc_view" >
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcContaminationResult"/>:</font>
										&nbsp;
										${rc.contaminatedResult.displayString}
										
									</td>
								</tr>
								
								<tr id="rc_mtid_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
										&nbsp;
										${rc.mtidTest.displayString}						
									</td>
								</tr>
								
								</table>					
							</c:forEach>
						</c:if>
					</td>
				</tr>
					
				<b class="boxHeader" style="margin:0px; width:100%;">
					&nbsp;
					<spring:message code="labmodule.labEntry.reportedCultureResult" text="Reported Culture Result"/>					
				</b>
				
				
				<tr id="mgitCultureResult_view">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMgit"/>:</font>
						&nbsp;
						${culture.mgitResult.displayString}
						
					</td>
				</tr>
				
				<tr id="mtTestId_view">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureIdentificationBacteria"/>:</font>
						&nbsp;
						${culture.mtIdTest.displayString}						
					</td>
				</tr>
				
				<tr id="cultureType_view">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureTypeReported"/>:</font>
						&nbsp;
						${culture.typeOfCulture.displayString}
					</td>
				</tr>
				
				<tr id="DateOfReportingCulture_view">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureResultReportingDate"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${culture.cultureResultReportingDate}" format="${_dateFormatDisplay}"/>						
					</td>
				</tr>
			
			<!-- REQUIREMENT #10 END  -->
			</table>
			</form>
			
			<form hidden id="editCultureResults_${i.count}" name="editCultureResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=culture" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="cultureId" value="${culture.id}">
						<input hidden type="text" name="provider" value="45">
						<input hidden type="text" name="lab" value="${labResult.location.locationId}">
						<input hidden type="text" name="count" value="${i.count}">					
					
				</td>
			</tr>
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labWorker" text="Lab Worker"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="cultureLabWorker_${i.count}" name="cultureLabWorker_${i.count}" value="${culture.labSpecialistName}">
				</td>
			</tr>
				<tr id="cultreLabNo_edit">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
						&nbsp;
						<input type="text"  size="10" name="cultureLabNo_${i.count}" id="cultureLabNo_${i.count}" value="${culture.labNo}">
					</td>
				</tr>
				
				<tr id="placeOfCulture_edit">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.culturePlace"/>:</font>
						&nbsp;
						<select id="culturePlace_${i.count}" name="culturePlace_${i.count}">
						     <option hidden selected value=""></option>
								<c:forEach var="result" items="${culturePlaces}">
									<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == culture.placeOfCulture}">selected</c:if>>${result.answerConcept.displayString}</option>
								</c:forEach>
						</select>
					</td>
				</tr>
				
				<!-- MGIT Subtype here -->
				<tr id="mgit_subculture_edit">
				<td>
					<c:if test="${fn:length(culture.mgitCultures) > 0}">
					<input hidden type="text" name="mgit_sub_e" value="${fn:length(culture.mgitCultures)}">
						<c:forEach var="mgit" items="${culture.mgitCultures}" varStatus="j">
						<table id ="mgit_edit_subculture" class="box" >
							<tr class="box">
								<td> <font style="font-weight:bold"><spring:message code="labmodule.labEntry.mgit"/>:</font> </td>
							</tr>	
							
							<tr id="mgit_growthDate_edit">
								<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.mgitGrowthDate"/>:</font>
									&nbsp;
									<openmrs_tag:dateField formFieldName="mgit_growthDate_${j.count}" startValue="${mgit.mgitGrowthDate}"/>
								</td>
							</tr>
							<tr id="mgit_mgit_edit" >
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMgitMgit"/>:</font>
									&nbsp;
									
									<select id="mgit_mgit" name="mgit_mgit_${j.count}" onchange="onChangeMgit(this)">
											<c:forEach var="result" items="${mgitResults}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.mgitResult}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
									
								</td>
							</tr>
							
							<tr id="mgit_mtid_edit">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
									&nbsp;
									<select id="mgit_mtid" name="mgit_mtid_${j.count}">
											<c:forEach var="result" items="${mtidResults}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.mtidTest}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>						
								</td>
								<td>
									<input hidden type="text" name="mgit_obsId_${j.count}" value="${mgit.obsId}">
									<input hidden type="text" name="mgit_obsGroupdId_${j.count}" value="${mgit.obsGroupId}">
								</td>
							</tr>
							
							</table>												
						</c:forEach>
					</c:if>
				</td>
			</tr>
				
				<!-- LJ Subtype here -->
			<tr id="lj_subculture_edit">
			<td>
					<c:if test="${fn:length(culture.ljCultures) > 0}">
					<input hidden type="text" name="lj_sub_e" value="${fn:length(culture.ljCultures)}">
						<c:forEach var="lj" items="${culture.ljCultures}" varStatus="k">
						<table id ="lj_edit_subculture" class="box" >
							<tr class="box">
								<td> <font style="font-weight:bold"><spring:message code="labmodule.labEntry.lj"/>:</font> </td>
							</tr>	
							
							<tr id="lj_growthDate_edit">
								<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.ljGrowthDate"/>:</font>
									&nbsp;
									<openmrs_tag:dateField formFieldName="lj_growthDate_${k.count}" startValue="${lj.ljGrowthDate}"/>
								</td>
							</tr>
							<tr id="lj_lj_edit" >
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.ljCultureResult"/>:</font>
									&nbsp;
									
									<select id="lj_lj" name="lj_lj_${k.count}" onchange="onChangeMgit(this)">
											<c:forEach var="result" items="${mgitResults}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.ljResult}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
									
								</td>
							</tr>
							
							<tr id="lj_mtid_edit">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
									&nbsp;
									<select id="lj_mtid" name="lj_mtid_${k.count}">
											<c:forEach var="result" items="${mtidResults}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.mtIdTest}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>						
								</td>
								<td>
									<input hidden type="text" name="lj_obsId_${k.count}" value="${mgit.obsId}">
									<input hidden type="text" name="lj_obsGroupdId_${k.count}" value="${mgit.obsGroupId}">
								</td>
							</tr>
							
							</table>												
						</c:forEach>
					</c:if>
				</td>
			</tr>	
			
			<!-- RC Subtype here -->
			<tr id="rc_subculture_edit">
			<td>
					<c:if test="${fn:length(culture.contaminatedTubes) > 0}">
					<input hidden type="text" name="rc_sub_e" value="${fn:length(culture.contaminatedTubes)}">
						<c:forEach var="rc" items="${culture.contaminatedTubes}" varStatus="l">
						<table id ="rc_edit_subculture" class="box" >
							<tr class="box">
								<td> <font style="font-weight:bold"><spring:message code="labmodule.labEntry.rc"/>:</font> </td>
							</tr>	
							
							<tr id="rc_contaminationDate_edit">
								<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcContaminationDate"/>:</font>
									&nbsp;
									<openmrs_tag:dateField formFieldName="rc_inoculation_${l.count}" startValue="${rc.repeatedDecontamination}"/>
								</td>
							</tr>
							<tr id="rc_growthDate_edit">
								<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcGrowthDate"/>:</font>
									&nbsp;
									<openmrs_tag:dateField formFieldName="rc_growthDate_${l.count}" startValue="${rc.growthDate}"/>
								</td>
							</tr>
							<tr id="rc_rc_edit" >
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.rcContaminationResult"/>:</font>
									&nbsp;
									
									<select id="rc_rc" name="rc_rc_${l.count}" onchange="onChangeMgit(this)" >
											<c:forEach var="result" items="${mgitResults}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == rc.contaminatedResult}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
									
								</td>
							</tr>
							
							<tr id="rc_mtid_edit">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.cultureMtIdTest"/>:</font>
									&nbsp;
									<select id="rc_mtid" name="rc_mtid_${l.count}" >
											<c:forEach var="result" items="${mtidResults}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == rc.mtidTest}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>						
								</td>
								<td>
									<input hidden type="text" name="rc_obsId_${l.count}" value="${mgit.obsId}">
									<input hidden type="text" name="rc_obsGroupdId_${l.count}" value="${mgit.obsGroupId}">
								</td>
							</tr>
							
							</table>												
						</c:forEach>
					</c:if>
				</td>
			</tr>	
			
			<tr id="inoculcationdate_edit">
				<td>						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.incoculationDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="incoculationDate_${i.count}" startValue="${culture.inoculationDate}"/>
				</td>
			</tr>
						 
			<tr id="mgit_edit">
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.cultureMgit"/>:</font>
					&nbsp;
					<select id="mgitResults_${i.count}" name="mgitResults_${i.count}" onchange="onChangeMgit(this)">
								<c:forEach var="result" items="${mgitResults}">
									<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == culture.mgitResult}">selected</c:if>>${result.answerConcept.displayString}</option>
								</c:forEach>
					</select>
				</td>
			</tr> 
			<tr id="mtid_edit">
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.cultureIdentificationBacteria"/>:</font>
					&nbsp;
					<select id="mtidResults_${i.count}" name="mtidResults_${i.count}">
								<c:forEach var="result" items="${mtidResults}">
									<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == culture.mtIdTest}">selected</c:if>>${result.answerConcept.displayString}</option>
								</c:forEach>
					</select>
				</td>
			</tr> 
			<tr id="culturetype_edit">
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.cultureTypeReported"/>:</font>
					&nbsp;
					<select id="cultureType_${i.count}" name="cultureType_${i.count}">
								<c:forEach var="result" items="${cultureTypes}">
									<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == culture.typeOfCulture}">selected</c:if>>${result.answerConcept.displayString}</option>
								</c:forEach>
					</select>
				</td>
			</tr> 
			<tr id="cultureReportingDate_edit">
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.cultureResultReportingDate"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="cultureReportingDate_${i.count}" startValue="${culture.cultureResultReportingDate}"/>
				</td>
			</tr> 
			<tr>
				 <td>
				   <br>
					<openmrs:hasPrivilege privilege="Edit Test Result">
							<button type="button" id="updateCulture_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
					</openmrs:hasPrivilege>
					<button type="reset" id="cancelUpdateCulture_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
				 </td>
			</tr>
						
			</table>
			</form>
			</td>
			</tr>
		
		</c:forEach>
		
	</c:if>	
	<c:if test="${fn:length(labResult.dst1s) > 0}">
		<c:forEach var="dst1" items="${labResult.dst1s}" varStatus="i">
				<tr>
					<td>
							<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.firstLineDst"/> ( ${i.count} )
								<span id="dst1_edit_span_${i.count}" name="dst1_edit_span" style="float:right">
										<openmrs:hasPrivilege privilege="Edit Test Result">
										<img title="Edit" id="editDst1Span_${i.count}" class="edit" onclick='editTest(this,"","","",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										</openmrs:hasPrivilege>
										<openmrs:hasPrivilege privilege="Delete Test Result">
										<img title="Delete" id="deleteDst1Span_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										</openmrs:hasPrivilege>
								</span>
							</b>
							</span>
					</b>
				</td>
			</tr>
			<tr>
					<td>
							<form id="deleteSubDstResults" name="deleteSubDstResults" action="labEntry.form" method="post"></form>
							
							<form id="deleteDst1_${i.count}" name="deleteDst1_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

								<table style="font-size: 13px">
								
									<tr>
										<td>
											<input hidden type="text" name="labResultId" value="${labResult.id}">
											<input hidden type="text" name="id" value="${culture.id}">											
										</td>
									</tr>														
									
									<tr id="dst1LabNo_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
											&nbsp;
											${dst1.labNo}
										</td>
										
										<td >
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
											&nbsp;
											${dst1.labSpecialistName}
										</td>
									</tr>
									<tr id="dst1LabLocation_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dst1Location"/>:</font>
											&nbsp;
											${dst1.labLocation.displayString}
										</td>
									</tr>
									
									<tr id="mgit_subdst1_view">								
										<td>
											<c:if test="${fn:length(dst1.mgitDsts) > 0}">
												<c:forEach var="mgit" items="${dst1.mgitDsts}" varStatus="j">
												<table id ="mgit_view_subdst1" class="box" >
													<tr class="box">
														<td> 
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgit"/>:</font>
															<span style="display:none" id="dst1_edit_span_${i.count}" name="dst1_edit_span" style="float:right">
																<openmrs:hasPrivilege privilege="Delete Test Result">
																	<img title="Delete" id="deleteMgitSubDst1_${j.count}" class="edit" onclick='deleteSubdst(${mgit.obsGroupId},${patientId},${labResult.id},"mgit","deleteDst1" )' 
																	src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
																</openmrs:hasPrivilege>
															</span>
														 </td>
													</tr>	
													
													<tr id="dst1_mgit_inoculationDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitInoculationDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${mgit.inoculationDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
													<tr id="dst1_mgit_readingDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitReadingDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${mgit.readingDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
														<tr id="dst1_mgit_ResistenceS_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
																&nbsp;
																${mgit.resistanceS.displayString}
															
														</td>
														</tr>
														<tr id="dst1_mgit_ResistenceH_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
																&nbsp;
																${mgit.resistanceH.displayString}
																
															</td>
														</tr>
														
														<tr id="dst1_mgit_ResistenceR_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
																&nbsp;
																${mgit.resistanceR.displayString}
																
															</td>
														</tr>
														<tr id="dst1_mgit_ResistenceE_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
																&nbsp;
																${mgit.resistanceE.displayString}
															</td>
														</tr>
														<tr id="dst1_mgit_ResistenceZ_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
																&nbsp;
																${mgit.resistanceZ.displayString}																
															</td>
														</tr>
														<tr id="dst1_mgit_ResistenceLfx_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
																&nbsp;
																${mgit.resistanceLfx.displayString}																
															</td>
														</tr>
													</table>					
												</c:forEach>
											</c:if>
										</td>
									</tr>
									<tr id="lj_subdst1_view">								
										<td>
											<c:if test="${fn:length(dst1.ljDsts) > 0}">
												<c:forEach var="lj" items="${dst1.ljDsts}" varStatus="k">
												<table id ="lj_view_subdst1" class="box" >
													<tr class="box">
														<td> 
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLj"/>:</font>
															<span style ="display:none" id="dst1_edit_span_${i.count}" name="dst1_edit_span" style="float:right">
																<openmrs:hasPrivilege privilege="Delete Test Result">
																	<img title="Delete" id="deleteLjSubDst1_${k.count}" class="edit" onclick='deleteSubdst(${mgit.obsGroupId},${patientId},${labResult.id},"lj","deleteDst1" )' 
																	src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
																</openmrs:hasPrivilege>
															</span>
														 </td>
													</tr>	
													
													<tr id="dst1_lj_inoculationDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjInoculationDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${lj.inoculationDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
													<tr id="dst1_lj_readingDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjReadingDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${lj.readingDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
														<tr id="dst1_lj_ResistenceS_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
																&nbsp;
																${lj.resistanceS.displayString}
															
														</td>
														</tr>
														<tr id="dst1_lj_ResistenceH_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
																&nbsp;
																${lj.resistanceH.displayString}
																
															</td>
														</tr>
														
														<tr id="dst1_lj_ResistenceR_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
																&nbsp;
																${lj.resistanceR.displayString}
																
															</td>
														</tr>
														<tr id="dst1_lj_ResistenceE_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
																&nbsp;
																${lj.resistanceE.displayString}
															</td>
														</tr>
														<tr id="dst1_lj_ResistenceZ_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
																&nbsp;
																${lj.resistanceZ.displayString}																
															</td>
														</tr>
														<tr id="dst1_lj_ResistenceLfx_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
																	&nbsp;
																	${lj.resistanceLfx.displayString}																
																</td>
															</tr>
													</table>					
												</c:forEach>
											</c:if>
										</td>
									</tr>
									<tr id="dst1ResistenceS_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
										&nbsp;
										${dst1.resistanceS.displayString}
										
									</td>
								</tr>
								<tr id="dst1ResistenceH_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
										&nbsp;
										${dst1.resistanceH.displayString}
										
									</td>
								</tr>
								
								<tr id="dst1ResistenceR_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
										&nbsp;
										${dst1.resistanceR.displayString}
										
									</td>
								</tr>
								<tr id="dst1ResistenceE_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
										&nbsp;
										${dst1.resistanceE.displayString}
									</td>
								</tr>
								<tr id="dst1ResistenceZ_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
										&nbsp;
										${dst1.resistanceZ.displayString}
										
									</td>
								</tr>
								<tr id="dst1ResistenceLfx_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
										&nbsp;
										${dst1.resistanceLfx.displayString}
										
									</td>
								</tr>
									<tr id="dst1Type_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dstMethodType"/>:</font>
											&nbsp;
											${dst1.dstType.displayString}
										</td>
									</tr>
									<tr id="dst1ReportingDate_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstResultsDate"/>:</font>
											&nbsp;
											<openmrs:formatDate date="${dst1.reportingDate}" format="${_dateFormatDisplay}"/>
										</td>
									</tr>
								</table>
							</form>
							<!-- Edit DST-1 Form -->
							<form hidden id="editDst1Results_${i.count}" name="editDst1Results_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=editDst1" method="post">

								<table style="font-size: 13px">
								
										<tr>
											<td>
													<input hidden type="text" name="labResultId" value="${labResult.id}">
													<input hidden type="text" name="dst1Id" value="${dst1.id}">
													<input hidden type="text" name="count" value="${i.count}">					
													<input hidden type="text" name="provider" value="45">
													<input hidden type="text" name="lab" value="${labResult.location.locationId}">
											</td>
										</tr>
								
								<tr id="dst1LabNo_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
											&nbsp;
											<input type="text"  size="10" name="dst1labNo_${i.count}" id="dst1labNo_${i.count}" value="${dst1.labNo}">
											
										</td>
										
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
											&nbsp;
											<input type="text"  size="10" name="dst1LabWorker_${i.count}" id="dst1LabWorker_${i.count}" value="${dst1.labSpecialistName}">
											
										</td>
										
										
									</tr>
									<tr id="dst1LabLocation_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dst1Location"/>:</font>
											&nbsp;
											<select id="dst1LabLocation_${i.count}" name="dst1LabLocation_${i.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${culturePlaces}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.labLocation}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
									</td>
									</tr>
									
									<tr id="mgit_subdst1_edit">								
										<td>
											<c:if test="${fn:length(dst1.mgitDsts) > 0}">
												<input hidden type="text" name="dst1_mgit_sub_e" value="${fn:length(dst1.mgitDsts)}">
												<c:forEach var="mgit" items="${dst1.mgitDsts}" varStatus="l">
												<table id ="mgit_view_subdst1" class="box" >
													<tr class="box">
														<td> 
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgit"/>:</font>
															
														 </td>
													</tr>	
													
													<tr id="dst1_mgit_inoculationDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitInoculationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_mgit_inoculationDate_${l.count}" startValue="${mgit.inoculationDate}"/>
														</td>
													</tr>
													<tr id="dst1_mgit_readingDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitReadingDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_mgit_inoculationDate_${l.count}" startValue="${mgit.readingDate}"/>
															
														</td>
													</tr>
														<tr id="dst1_mgit_ResistenceS_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
																&nbsp;
																<select id="dst1_mgit_s_${l.count}" name="dst1_mgit_s_${l.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceS}">selected</c:if>>${result.answerConcept.displayString}</option>
																	</c:forEach>
															</select>
														</td>
														</tr>
														<tr id="dst1_mgit_ResistenceH_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
																&nbsp;
																<select id="dst1_mgit_h_$lj.count}" name="dst1_mgit_h_${l.count}">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${drugResistance}">
																			<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceH}">selected</c:if>>${result.answerConcept.displayString}</option>
																	</c:forEach>
																</select>
															</td>
														</tr>
														
														<tr id="dst1_mgit_ResistenceR_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
																&nbsp;
																<select id="dst1_mgit_r_${l.count}" name="dst1_mgit_r_${l.count}">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${drugResistance}">
																			<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceR}">selected</c:if>>${result.answerConcept.displayString}</option>
																	</c:forEach>
																</select>
															</td>
														</tr>
														<tr id="dst1_mgit_ResistenceE_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
																&nbsp;
																<select id="dst1_mgit_e_${l.count}" name="dst1_mgit_e_${l.count}">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${drugResistance}">
																			<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceE}">selected</c:if>>${result.answerConcept.displayString}</option>
																	</c:forEach>
																</select>
															</td>
														</tr>
														<tr id="dst1_mgit_ResistenceZ_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
																&nbsp;
																<select id="dst1_mgit_z_${l.count}" name="dst1_mgit_z_${l.count}">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${drugResistance}">
																			<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceZ}">selected</c:if>>${result.answerConcept.displayString}</option>
																	</c:forEach>
																</select>																
															</td>
														</tr>
														<tr id="dst1_mgit_ResistenceLfx_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
																&nbsp;
																<select id="dst1_mgit_lfx_${l.count}" name="dst1_mgit_lfx_${l.count}">
																     <option hidden selected value=""></option>
																		<c:forEach var="result" items="${drugResistance}">
																			<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceLfx}">selected</c:if>>${result.answerConcept.displayString}</option>
																	</c:forEach>
																</select>																
															</td>
														</tr>
													  
													
													</table>					
												</c:forEach>
											</c:if>
										</td>
									</tr>
									<tr id="lj_subdst1_edit">	<!-- Jumah -->							
										<td>
											<c:if test="${fn:length(dst1.ljDsts) > 0}">
												<input hidden type="text" name="dst1_lj_sub_e" value="${fn:length(dst1.ljDsts)}">
												<c:forEach var="lj" items="${dst1.ljDsts}" varStatus="m">
												<table id ="lj_view_subdst1" class="box" >
													<tr class="box">
														<td> 
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLj"/>:</font>															
														 </td>
													</tr>	
													
													<tr id="dst1_lj_inoculationDate_edit">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjInoculationDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_mgit_inoculationDate_${m.count}" startValue="${lj.inoculationDate}"/>
														</td>
													</tr>
													<tr id="dst1_lj_readingDate_edit">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjReadingDate"/>:</font>
															&nbsp;
															<openmrs_tag:dateField formFieldName="dst1_lj_readingDate_${m.count}" startValue="${lj.readingDate}"/>
														</td>
													</tr>
														<tr id="dst1_lj_ResistenceS_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
																&nbsp;
																<select id="dst1_lj_s_${m.count}" name="dst1_lj_s_${m.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceS}">selected</c:if>>${result.answerConcept.displayString}</option>
																</c:forEach>
															
														</td>
														</tr>
														<tr id="dst1_lj_ResistenceH_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
																&nbsp;
																<select id="dst1_lj_h_${m.count}" name="dst1_lj_h_${m.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceH}">selected</c:if>>${result.answerConcept.displayString}</option>
																</c:forEach>
															</td>
														</tr>
														
														<tr id="dst1_lj_ResistenceR_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
																&nbsp;
																<select id="dst1_lj_r_${m.count}" name="dst1_lj_r_${m.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceR}">selected</c:if>>${result.answerConcept.displayString}</option>
																</c:forEach>
															</td>
														</tr>
														<tr id="dst1_lj_ResistenceE_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
																&nbsp;
																<select id="dst1_lj_e_${m.count}" name="dst1_lj_e_${m.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceE}">selected</c:if>>${result.answerConcept.displayString}</option>
																</c:forEach>
															</td>
														</tr>
														<tr id="dst1_lj_ResistenceZ_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
																&nbsp;
																<select id="dst1_lj_z_${m.count}" name="dst1_lj_z_${m.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceZ}">selected</c:if>>${result.answerConcept.displayString}</option>
																</c:forEach>
																																
															</td>
														</tr>
														<tr id="dst1_lj_ResistenceLfx_edit">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
																&nbsp;
																<select id="dst1_lj_lfx_${m.count}" name="dst1_lj_lfx_${m.count}">
																	<option hidden selected value=""></option>
																	<c:forEach var="result" items="${drugResistance}">
																		<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceLfx}">selected</c:if>>${result.answerConcept.displayString}</option>
																</c:forEach>
																																
															</td>
														</tr>
														
													</table>					
												</c:forEach>
											</c:if>
										</td>
									</tr>
									<tr id="dst1ResistenceS_edit">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceS"/>:</font>
										&nbsp;
										<select id="dst1S_${i.count}" name="dst1S_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.resistanceS}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
										
									</td>
								</tr>
								<tr id="dst1ResistenceH_edit">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceH"/>:</font>
										&nbsp;
										<select id="dst1H_${i.count}" name="dst1H_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.resistanceH}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
										
									</td>
								</tr>
								
								<tr id="dst1ResistenceR_edit">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceR"/>:</font>
										&nbsp;
										<select id="dst1R_${i.count}" name="dst1R_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.resistanceR}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</td>
								</tr>
								<tr id="dst1ResistenceE_edit">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceE"/>:</font>
										&nbsp;
										
										<select id="dst1E_${i.count}" name="dst1E_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.resistanceE}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
	
									</td>
								</tr>
								<tr id="dst1ResistenceZ_edit">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceZ"/>:</font>
										&nbsp;
										
										<select id="dst1Z_${i.count}" name="dst1Z_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.resistanceZ}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</td>
								</tr>
								<tr id="dst1ResistenceLfx_edit">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
										&nbsp;
										
										<select id="dst1Lfx_${i.count}" name="dst1Lfx_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${drugResistance}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.resistanceLfx}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</td>
								</tr>
									<tr id="dst1Type_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dstMethodType"/>:</font>
											&nbsp;
											<select id="dst1Type_${i.count}" name="dst1Type_${i.count}">
											<option hidden selected value=""></option>
											<c:forEach var="result" items="${dstTypes}">
												<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst1.dstType}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
										</td>
									</tr>
									<tr id="dst1ReportingDate_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstResultsDate"/>:</font>
											&nbsp;
											<openmrs_tag:dateField formFieldName="dst1ReportingDate_${i.count}" startValue="${dst1.reportingDate}"/>
										</td>
									</tr>
									 <tr>
								   		<td>
											<openmrs:hasPrivilege privilege="Edit Test Result">
											<button type="button" id="updateDst1_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
											</openmrs:hasPrivilege>
											<button type="reset" id="cancelUpdateDst1_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									 	</td>
									 </tr>
						
					</table>
			</c:forEach>
		
		</c:if>	
		<c:if test="${fn:length(labResult.dst2s) > 0}">
		<c:forEach var="dst2" items="${labResult.dst2s}" varStatus="i">
				<tr>
					<td>
							<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.secondLineDst"/> ( ${i.count} )
								<span id="dst2_edit_span_${i.count}" name="dst2_edit_span" style="float:right">
										<openmrs:hasPrivilege privilege="Edit Test Result">
										<img title="Edit" id="editDst2Span_${i.count}" class="edit" onclick='editTest(this,"","","",0)' src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										</openmrs:hasPrivilege>
										<openmrs:hasPrivilege privilege="Delete Test Result">
										<img title="Delete" id="deleteDst2Span_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
										</openmrs:hasPrivilege>
								</span>
							</b>
							</span>
					</b>
				</td>
			</tr>
			<tr>
					<td>
							<form id="deleteSubDstResults" name="deleteSubDstResults" action="labEntry.form" method="post"></form>
							
							<form id="deleteDst2_${i.count}" name="deleteDst2_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

								<table style="font-size: 13px">
								
									<tr>
										<td>
											<input hidden type="text" name="labResultId" value="${labResult.id}">
											<input hidden type="text" name="id" value="${culture.id}">											
										</td>
									</tr>														
									
									<tr id="dst2LabNo_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
											&nbsp;
											${dst2.labNo}
										</td>
										
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
											&nbsp;
											${dst2.labSpecialistName}
										</td>
									</tr>
									<tr id="dst2LabLocation_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLocation"/>:</font>
											&nbsp;
											${dst2.labLocation.displayString}
										</td>
									</tr>
									<tr id="mgit_subdst2_view">								
										<td>
											<c:if test="${fn:length(dst2.mgitDsts) > 0}">
												<c:forEach var="mgit" items="${dst2.mgitDsts}" varStatus="j">
												<table id ="mgit_view_subdst2" class="box" >
													<tr class="box">
														<td> 
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgit"/>:</font>
															<span style="display:none" id="dst2_edit_span_${i.count}" name="dst2_edit_span" style="float:right">
																<openmrs:hasPrivilege privilege="Delete Test Result">
																	<img title="Delete" id="deleteMgitSubDst2_${j.count}" class="edit" onclick='deleteSubdst(${mgit.obsGroupId},${patientId},${labResult.id},"mgit","deleteDst2" )' 
																	src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
																</openmrs:hasPrivilege>
															</span>
														 </td>
													</tr>	
													
													<tr id="dst2_mgit_inoculationDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstMgitInoculationDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${mgit.inoculationDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
													<tr id="dst2_mgit_readingDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstMgitReadingDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${mgit.readingDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
													
													<!--DST2 MGIT RESISTANCE HERE -->
														<tr id="dst2_mgit_rowOfx_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
																&nbsp;
																${mgit.resistanceOfx.displayString}
																
															</td>
														</tr>
														
														<tr id="dst2_mgit_rowMox_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
																&nbsp;
																${mgit.resistanceMox.displayString}
																
															</td>
														</tr>
														
															
														<%-- <tr id="dst2_mgit_rowLfx_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
																&nbsp;
																${mgit.resistanceLfx.displayString}
																
															</td>
														</tr> --%>
														
														
														
														<tr id="dst2_mgit_rowPto_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
																&nbsp;
																${mgit.resistancePto.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowLzd_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
																&nbsp;
																${mgit.resistanceLzd.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowCfz_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
																&nbsp;
																${mgit.resistanceCfz.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowBdq_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
																&nbsp;
																${mgit.resistanceBdq.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowDlm_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
																&nbsp;
																${mgit.resistanceDlm.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowPas_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
																&nbsp;
																${mgit.resistancePas.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowCm_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
																&nbsp;
																${mgit.resistanceCm.displayString}
																
															</td>
														</tr>
														
														
														<tr id="dst2_mgit_rowKm_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
																&nbsp;
																${mgit.resistanceKm.displayString}
																
															</td>
														</tr>
														
														<tr id="dst2_mgit_rowAm_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
																&nbsp;
																${mgit.resistanceAm.displayString}
																
															</td>
														</tr>
													
													</table>					
												</c:forEach>
											</c:if>
										</td>
									</tr>
									<tr id="lj_subdst2_view">								
										<td>
											<c:if test="${fn:length(dst2.ljDsts) > 0}">
												<c:forEach var="lj" items="${dst2.ljDsts}" varStatus="k">
												<table id ="lj_view_subdst2" class="box" >
													<tr class="box">
														<td> 
															<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLj"/>:</font>
															<span style ="display:none" id="dst2_edit_span_${i.count}" name="dst2_edit_span" style="float:right">
																<openmrs:hasPrivilege privilege="Delete Test Result">
																	<img title="Delete" id="deleteLjSubDst2_${k.count}" class="edit" onclick='deleteSubdst(${mgit.obsGroupId},${patientId},${labResult.id},"lj","deleteDst2" )' 
																	src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
																</openmrs:hasPrivilege>
															</span>
														 </td>
													</tr>	
													
													<tr id="dst2_lj_inoculationDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLjInoculationDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${lj.inoculationDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
													<tr id="dst2_lj_readingDate_view">
														<td>
														<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstLjReadingDate"/>:</font>
															&nbsp;
															<openmrs:formatDate date="${lj.readingDate}" format="${_dateFormatDisplay}"/>
														</td>
													</tr>
														
														<!--DST2 LJ RESISTANCE HERE -->
														<tr id="dst2_lj_rowOfx_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
																&nbsp;
																${lj.resistanceOfx.displayString}
															</td>
														</tr>
													
														<tr id="dst2_lj_rowMox_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
																&nbsp;
																${lj.resistanceMox.displayString}
																
															</td>
														</tr>
													
															
														<%-- <tr id="dst2_lj_rowLfx_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
																&nbsp;
																${lj.resistanceLfx.displayString}
																
															</td>
														</tr> --%>
													
													
													
														<tr id="dst2_lj_rowPto_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
																&nbsp;
																${lj.resistancePto.displayString}
																
															</td>
														</tr>
													
													
														<tr id="dst2_lj_rowLzd_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
																&nbsp;
																${lj.resistanceLzd.displayString}
																
															</td>
														</tr>


														<tr id="dst2_lj_rowCfz_view">
															<td>
																<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
																&nbsp;
																${lj.resistanceCfz.displayString}
																
															</td>
														</tr>
											
											
															<tr id="dst2_lj_rowBdq_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
																	&nbsp;
																	${lj.resistanceBdq.displayString}
																	
																</td>
															</tr>
															
															
															<tr id="dst2_lj_rowDlm_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
																	&nbsp;
																	${lj.resistanceDlm.displayString}
																	
																</td>
															</tr>
															
															
															<tr id="dst2_lj_rowPas_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
																	&nbsp;
																	${lj.resistancePas.displayString}
																	
																</td>
															</tr>
															
															
															<tr id="dst2_lj_rowCm_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
																	&nbsp;
																	${lj.resistanceCm.displayString}
																	
																</td>
															</tr>
															
															
															<tr id="dst2_lj_rowKm_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
																	&nbsp;
																	${lj.resistanceKm.displayString}
																	
																</td>
															</tr>
													
															<tr id="dst2_lj_rowAm_view">
																<td>
																	<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
																	&nbsp;
																	${lj.resistanceAm.displayString}
																	
																</td>
															</tr>
													
													</table>					
												</c:forEach>
											</c:if>
										</td>
									</tr>
									
									
									<!--DST2 RESISTANCE HERE -->
									<tr id="dst2_rowOfx_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
											&nbsp;
											${dst2.resistanceOfx.displayString}
											
										</td>
									</tr>
								
									<tr id="dst2_rowMox_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
											&nbsp;
											${dst2.resistanceMox.displayString}
											
										</td>
									</tr>
								
										
									<%-- <tr id="dst2_rowLfx_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
											&nbsp;
											${dst2.resistanceLfx.displayString}
											
										</td>
									</tr> --%>
								
								
								
									<tr id="dst2_rowPto_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
											&nbsp;
											${dst2.resistancePto.displayString}
											
										</td>
									</tr>
								
								
									<tr id="dst2_rowLzd_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
											&nbsp;
											${dst2.resistanceLzd.displayString}
											
										</td>
									</tr>
								

								<tr id="dst2_rowCfz_view">
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
										&nbsp;
										${dst2.resistanceCfz.displayString}
										
									</td>
								</tr>
							
							
									<tr id="dst2_rowBdq_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
											&nbsp;
											${dst2.resistanceBdq.displayString}
											
										</td>
									</tr>
									
									
									<tr id="dst2_rowDlm_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
											&nbsp;
											${dst2.resistanceDlm.displayString}
											
										</td>
									</tr>
									
									
									<tr id="dst2_rowPas_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
											&nbsp;
											${dst2.resistancePas.displayString}
											
										</td>
									</tr>
									
									
									<tr id="dst2_rowCm_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
											&nbsp;
											${dst2.resistanceCm.displayString}
											
										</td>
									</tr>
									
									
									<tr id="dst2_rowKm_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
											&nbsp;
											${dst2.resistanceKm.displayString}
											
										</td>
									</tr>
									
									<tr id="dst2_rowAm_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
											&nbsp;
											${dst2.resistanceAm.displayString}
											
										</td>
									</tr>
								
									<tr id="dst2Type_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dstMethodType"/>:</font>
											&nbsp;
											${dst2.dstType.displayString}
										</td>
									</tr>
									<tr id="dst2ReportingDate_view">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstResultsDate"/>:</font>
											&nbsp;
											<openmrs:formatDate date="${dst2.reportingDate}" format="${_dateFormatDisplay}"/>
										</td>
									</tr>
								</table>
							</form>
								<!-- Edit DST-2 Form -->
			<form hidden id="editDst2Results_${i.count}" name="editDst2Results_${i.count}" action="labEntry.form?patientId=${patientId}&labResultId=${labResult.id}&submissionType=dst2&dst2Id=${dst2.id}" method="post">

			<table style="font-size: 13px">
			
					<tr>
						<td>
<%-- 								<input hidden type="text" name="labResultId" value="${labResult.id}"> --%>
<%-- 								<input hidden type="text" name="dst2Id" value="${dst2.id}"> --%>
								<input hidden type="text" name="count" value="${i.count}">					
								<input hidden type="text" name="provider" value="45">
								<input hidden type="text" name="lab" value="${labResult.location.locationId}">
						</td>
					</tr>
					<tr id="dst2LabNo_edit">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNo"/>:</font>
								&nbsp;
								<input type="text"  size="10" name="dst2LabNo_${i.count}" id="dst2LabNo_${i.count}" value="${dst2.labNo}">
								
							</td>
							
								<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labWorker"/>:</font>
								&nbsp;
								<input type="text"  size="10" name="dst2LabWorker_${i.count}" id="dst2LabWorker_${i.count}" value="${dst2.labSpecialistName}">
								
							</td>
						</tr>
						<tr id="dst2LabLocation_edit">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dst1Location"/>:</font>
								&nbsp;
								<select id="dst2LabLocation_${i.count}" name="dst2LabLocation_${i.count}">
									<option hidden selected value=""></option>
									<c:forEach var="result" items="${culturePlaces}">
										<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.labLocation}">selected</c:if>>${result.answerConcept.displayString}</option>
								</c:forEach>
						</td>
				 </tr>
				<tr id="mgit_subdst2_edit">								
					<td>
						<c:if test="${fn:length(dst2.mgitDsts) > 0}">
							<input hidden type="text" name="dst2_mgit_sub_e" value="${fn:length(dst2.mgitDsts)}">
							<c:forEach var="mgit" items="${dst2.mgitDsts}" varStatus="l">
							<table id ="mgit_edit_subdst2" class="box" >
								<tr class="box">
									<td> 
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgit"/>:</font>
										
									 </td>
								</tr>	
								
								<tr id="dst2_mgit_inoculationDate_edit">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitInoculationDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="dst2_mgit_inoculationDate_${l.count}" startValue="${mgit.inoculationDate}"/>
									</td>
								</tr>
								<tr id="dst2_mgit_readingDate_edit">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstMgitReadingDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="dst2_mgit_inoculationDate_${l.count}" startValue="${mgit.readingDate}"/>
										
									</td>
								</tr>
									
									<!--DST2 MGIT RESISTANCE HERE -->
									<tr id="dst2_rowOfx_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
											&nbsp;
											<select id="dst2Ofx_mgit_${l.count}" name="dst2Ofx_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceOfx}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
											
										</td>
									</tr>

									<tr id="dst2_rowMox_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
											&nbsp;
											<select id="dst2Mox_mgit_${l.count}" name="dst2Mox_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceMox}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
											
										</td>
									</tr>

										
									<%-- <tr id="dst2_rowLfx_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
											&nbsp;
											<select id="dst2Lfx_mgit_${l.count}" name="dst2Lfx_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceLfx}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
											
										</td>
									</tr> --%>



									<tr id="dst2_rowPto_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
											&nbsp;
											<select id="dst2Pto_mgit_${l.count}" name="dst2Pto_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistancePto}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
											
										</td>
									</tr>


									<tr id="dst2_rowLzd_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
											&nbsp;
											<select id="dst2Lzd_mgit_${l.count}" name="dst2Lzd_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceLzd}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
											
										</td>
									</tr>


									<tr id="dst2_rowCfz_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
											&nbsp;
											<select id="dst2Cfz_mgit_${l.count}" name="dst2Cfz_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceCfz}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
											
										</td>
									</tr>


										<tr id="dst2_rowBdq_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
												&nbsp;
												<select id="dst2Bdq_mgit_${l.count}" name="dst2Bdq_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceBdq}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
												
											</td>
										</tr>
										
										
										<tr id="dst2_rowDlm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
												&nbsp;
												<select id="dst2Dlm_mgit_${l.count}" name="dst2Dlm_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceDlm}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
												
											</td>
										</tr>
										
										
										<tr id="dst2_rowPas_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
												&nbsp;
												<select id="dst2Pas_mgit_${l.count}" name="dst2Pas_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistancePas}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
												
											</td>
										</tr>
										
										
										<tr id="dst2_rowCm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
												&nbsp;
												<select id="dst2Cm_mgit_${l.count}" name="dst2Cm_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceCm}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
												
											</td>
										</tr>
										
										
										<tr id="dst2_rowKm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
												&nbsp;
												<select id="dst2Km_mgit_${l.count}" name="dst2Km_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceKm}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
												
											</td>
										</tr>
										
										<tr id="dst2_rowAm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
												&nbsp;
												<select id="dst2Am_mgit_${l.count}" name="dst2Am_mgit_${l.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == mgit.resistanceAm}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											</select>
												
											</td>
										</tr>
									<td>
								  <br>
								  
								
								</table>					
							</c:forEach>
						</c:if>
					</td>
				</tr>
				<tr id="lj_subdst2_edit">						
					<td>
						<c:if test="${fn:length(dst2.ljDsts) > 0}">
							<input hidden type="text" name="dst2_lj_sub_e" value="${fn:length(dst2.ljDsts)}">
							<c:forEach var="lj" items="${dst2.ljDsts}" varStatus="m">
							<table id ="lj_edit_subdst2" class="box" >
								<tr class="box">
									<td> 
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLj"/>:</font>															
									 </td>
								</tr>	
								
								<tr id="dst2_lj_inoculationDate_edit">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjInoculationDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="dst2_mgit_inoculationDate_${m.count}" startValue="${lj.inoculationDate}"/>
									</td>
								</tr>
								<tr id="dst2_lj_readingDate_edit">
									<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.firstLineDstLjReadingDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="dst2_lj_readingDate_${m.count}" startValue="${lj.readingDate}"/>
									</td>
								</tr>
									
									<!--DST2 LJ RESISTANCE HERE -->
										<tr id="dst2_rowOfx_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
											&nbsp;
											<select id="dst2Ofx_lj_${m.count}" name="dst2Ofx_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceOfx}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
										</td>
										</tr>

										<tr id="dst2_rowMox_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
											&nbsp;
											<select id="dst2Mox_lj_${m.count}" name="dst2Mox_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceMox}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
											
										</td>
										</tr>


										<%-- <tr id="dst2_rowLfx_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
											&nbsp;
											<select id="dst2Lfx_lj_${m.count}" name="dst2Lfx_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceLfx}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											
										</td>
										</tr> --%>



										<tr id="dst2_rowPto_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
											&nbsp;
											<select id="dst2Pto_lj_${m.count}" name="dst2Pto_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistancePto}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											
										</td>
										</tr>


										<tr id="dst2_rowLzd_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
											&nbsp;
											<select id="dst2Lzd_lj_${m.count}" name="dst2Lzd_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceLzd}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											
										</td>
										</tr>


										<tr id="dst2_rowCfz_edit">
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
											&nbsp;
											<select id="dst2Cfz_lj_${m.count}" name="dst2Cfz_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceCfz}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
											
										</td>
										</tr>


										<tr id="dst2_rowBdq_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
												&nbsp;
												<select id="dst2Bdq_lj_${m.count}" name="dst2Bdq_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceBdq}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
												
											</td>
										</tr>


										<tr id="dst2_rowDlm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
												&nbsp;
												<select id="dst2Dlm_lj_${m.count}" name="dst2Dlm_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceDlm}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
												
											</td>
										</tr>


										<tr id="dst2_rowPas_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
												&nbsp;
												<select id="dst2Pas_lj_${m.count}" name="dst2Pas_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
													<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistancePas}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
												
											</td>
										</tr>


										<tr id="dst2_rowCm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
												&nbsp;
												<select id="dst2Cm_lj_${m.count}" name="dst2Cm_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceCm}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
												
											</td>
										</tr>


										<tr id="dst2_rowKm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
												&nbsp;
												<select id="dst2Km_lj_${m.count}" name="dst2Km_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceKm}">selected</c:if>>${result.answerConcept.displayString}</option>
											</c:forEach>
												
											</td>
										</tr>

										<tr id="dst2_rowAm_edit">
											<td>
												<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
												&nbsp;
												<select id="dst2Am_lj_${m.count}" name="dst2Am_lj_${m.count}">
												<option hidden selected value=""></option>
												<c:forEach var="result" items="${drugResistance}">
														<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == lj.resistanceAm}">selected</c:if>>${result.answerConcept.displayString}</option>
												</c:forEach>
												
											</td>
										</tr>
									
								</table>					
							</c:forEach>
						</c:if>
					</td>
				</tr>
								<tr id="dst2_rowOfx">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceOfx"/>:</font>
								&nbsp;
								<select id="dst2Ofx_${i.count}" name="dst2Ofx_${i.count}">
								 <option hidden selected value=""></option>
									<c:forEach var="result" items="${drugResistance}">
										<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceOfx}">selected</c:if>>${result.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								
							</td>
						</tr>
						
						<tr id="dst2_rowMox">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceMox"/>:</font>
								&nbsp;
								<select id="dst2Mox_${i.count}" name="dst2Mox_${i.count}">
								 <option hidden selected value=""></option>
									<c:forEach var="result" items="${drugResistance}">
										<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceMox}">selected</c:if>>${result.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								
							</td>
						</tr>
						
							
						<%-- <tr id="dst2_rowLfx">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLfx"/>:</font>
								&nbsp;
								<select id="dst2Lfx_${i.count}" name="dst2Lfx_${i.count}">
								 <option hidden selected value=""></option>
									<c:forEach var="result" items="${drugResistance}">
									<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceLfx}">selected</c:if>>${result.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								
							</td>
						</tr> --%>
						
						
						
						<tr id="dst2_rowPto">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePto"/>:</font>
								&nbsp;
								<select id="dst2Pto_${i.count}" name="dst2Pto_${i.count}">
								 <option hidden selected value=""></option>
									<c:forEach var="result" items="${drugResistance}">
										<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistancePto}">selected</c:if>>${result.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								
							</td>
						</tr>
						
						
						<tr id="dst2_rowLzd">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceLzd"/>:</font>
								&nbsp;
								<select id="dst2Lzd_${i.count}" name="dst2Lzd_${i.count}">
								 <option hidden selected value=""></option>
									<c:forEach var="result" items="${drugResistance}">
										<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceLzd}">selected</c:if>>${result.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								
							</td>
						</tr>
						
						
						<tr id="dst2_rowCfz">
							<td>
								<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCfz"/>:</font>
								&nbsp;
								<select id="dst2Cfz_${i.count}" name="dst2Cfz_${i.count}">
								 <option hidden selected value=""></option>
									<c:forEach var="result" items="${drugResistance}">
									<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceCfz}">selected</c:if>>${result.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								
							</td>
						</tr>


							<tr id="dst2_rowBdq">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceBdq"/>:</font>
									&nbsp;
									<select id="dst2Bdq_${i.count}" name="dst2Bdq_${i.count}">
									 <option hidden selected value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceBdq}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							
							<tr id="dst2_rowDlm">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceDlm"/>:</font>
									&nbsp;
									<select id="dst2Dlm_${i.count}" name="dst2Dlm_${i.count}">
									 <option hidden selected value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceDlm}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							
							<tr id="dst2_rowPas">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistancePas"/>:</font>
									&nbsp;
									<select id="dst2Pas_${i.count}" name="dst2Pas_${i.count}">
									 <option hidden selected value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistancePas}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							
							<tr id="dst2_rowCm">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceCm"/>:</font>
									&nbsp;
									<select id="dst2Cm_${i.count}" name="dst2Cm_${i.count}">
									 <option hidden selected value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceCm}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							
							<tr id="dst2_rowKm">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceKm"/>:</font>
									&nbsp;
									<select id="dst2Km_${i.count}" name="dst2Km_${i.count}">
									 <option hidden selected value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceKm}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
									
								</td>
							</tr>
							
							<tr id="dst2_rowAm">
								<td>
									<font style="font-weight:bold"><spring:message code="labmodule.labEntry.resistanceAm"/>:</font>
									&nbsp;
									<select id="dst2Am_${i.count}" name="dst2Am_${i.count}">
									 <option hidden selected value=""></option>
										<c:forEach var="result" items="${drugResistance}">
											<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.resistanceAm}">selected</c:if>>${result.answerConcept.displayString}</option>
										</c:forEach>
									</select>
		
			<!--DST2 RESISTANCE HERE -->
				<tr id="dst2Type_edit">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dstMethodType"/>:</font>
						&nbsp;
						<select id="dst2Type_${i.count}" name="dst2Type_${i.count}">
						<option hidden selected value=""></option>
						<c:forEach var="result" items="${dstTypes}">
							<option value="${result.answerConcept.id}"<c:if test="${result.answerConcept == dst2.dstType}">selected</c:if>>${result.answerConcept.displayString}</option>
					</c:forEach>
					</td>
				</tr>
				<tr id="dst2ReportingDate_edit">
					<td>
						<font style="font-weight:bold"><spring:message code="labmodule.labEntry.secondLineDstResults"/>:</font>
						&nbsp;
						<openmrs_tag:dateField formFieldName="dst2ReportingDate_${i.count}" startValue="${dst2.reportingDate}"/>
					</td>
				</tr>
				 <tr>
					<td>
						<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateDst2_${i.count}" onclick='validateAndSubmitTest(this)'><spring:message code="mdrtb.save" text="Save"/></button>
						</openmrs:hasPrivilege>
						<button type="reset" id="cancelUpdateDst2_${i.count}" onclick='cancelUpdate(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
					</td>
				 </tr>
	
		</table>
</form>
							
						
			</c:forEach>
		
		</c:if>
	
		</div>
	</table>
	</div>

</div>
	
	

<!-- START 'ADD NEW SPECIMEN' RIGHT-HAND COLUMN -->
<div id="new_column" style="float: right; width:65%;  padding:0px 4px 4px 4px">

	<b class="boxHeader" style="margin:0px">
		&nbsp;
		<spring:message code="mdrtb.specimenDetails" text="Specimen Details"/>
	</b>
 
	<div class="box" id="specimen" style="margin:0px">

		<!-- START OF Specimen Detail DIV -->
		<div id="details_specimen" style="margin:0px">
		
			<form id="addTestResults" name="addTestResults" action="labEntry.form?patientId=${patientId}&submissionType=specimen" method="post">
			
				<table cellspacing="5" cellpadding="0" width="100%" >
			
						
					<tr>
				<td><font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font></td>
			</tr>
			<tr>
				<td><font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.oblast" />:</font>
					<select name="oblast" id="oblast" onchange="getDistrictsInSpecimen()">
						<option value=""></option>
						<c:forEach var="o" items="${oblasts}">
							<option value="${o.id}">${o.name}</option>
						</c:forEach>
					</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.district" />:</font>
				<select name="district" id="district" onchange="getFacilitiesInSpecimen()">
						<option value=""></option>
				</select>
				<font style="font-size:11px; font-weight:bold"><spring:message code="labmodule.facility" />:</font>
						<select name="facility" id="facility">
						<option value=""></option>
				</select></td>
			</tr>
					<tr>
						<td>		
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber"/> : </font>
							&nbsp;
							<input type="text"  size="10" id="labNumber" name="labNumber">
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateRequest"/> : </font>
							&nbsp;
							<openmrs_tag:dateField formFieldName="dateRequest" startValue=""/>
						</td>
					</tr>
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
							&nbsp;
							<openmrs_tag:dateField formFieldName="dateRecieve" startValue=""/>
						</td>
					</tr>
					
						<!-- Sputum collection date -->
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.sputumCollectionDate"/> : </font>
							&nbsp;
							<openmrs_tag:dateField formFieldName="dateSputumCollection" startValue=""/>
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
							&nbsp;
							<openmrs_tag:dateField formFieldName="dateInvestigation" startValue=""/>
						</td>
					</tr>
					<!-- OMAR FIELDS START -->
					
					<tr>
						
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.referringFacility"/> : </font>
							&nbsp;
							<input type="text"  size="10" id="referringFacility" name="referringFacility">
						</td>						
					
					</tr>
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.referredBy"/> : </font>
							&nbsp;
							<input type="text"  size="10" id="referredBy" name="referredBy">
						</td>
					</tr>
														
					<!-- OMAR FIELD END -->
					<tr>
						<td>
							<br>
							<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
							&nbsp;
							<select id="requestingLabName" name="requestingLabName">
								<option hidden selected value=""></option>
									<c:forEach var="requestingFacility" items="${requestingFacilities}">
										<option value="${requestingFacility.answerConcept.id}">${requestingFacility.answerConcept.displayString}</option>
									</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<td>	
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
							&nbsp;
							<select id="investigationPurpose" name="investigationPurpose">
								<option hidden selected value=""></option>
									<c:forEach var="purpose" items="${investigationPurposes}">
										<option value="${purpose.answerConcept.id}">${purpose.answerConcept.displayString}</option>
									</c:forEach>
							</select>
						</td>
					</tr>
				
					<tr>
						<td> 
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
							&nbsp;
							<select id="biologicalSpecimen" name="biologicalSpecimen">
								<option hidden selected value=""></option>
									<c:forEach var="type" items="${types}">
										<option value="${type.answerConcept.id}">${type.answerConcept.displayString}</option>
									</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<td>
							<br>
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
							&nbsp;
							<textarea rows="4" cols="50" name="comments"></textarea>
						</td>
					</tr>
					
					<%-- <tr>
						<td>
							<input type="checkbox" id="peripheral" name="peripheral" value="peripheral" onclick='onClick(this,"")'/> <font size=3> <spring:message code="labmodule.labEntry.peripheralLabInfo"/> </font>
							
							<div id="peripheral_div" >
							
								<table>
								
									<tr>
										<td>
											<br>
											<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
										</td>
									</tr>
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabName"/> : </font>
											&nbsp;
											<input type="text"  size="10" id="peripheralLabName" name="peripheralLabName">
										</td>
									</tr>
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
											&nbsp;
											<input type="text"  size="10" id="peripheralLabNo" name="peripheralLabNo">
										</td>
									</tr>
									<tr>
										<td> 
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
											&nbsp;
											<select id="peripheralBiologicalSpecimen" name="peripheralBiologicalSpecimen">
												<option hidden selected value=""></option>
													<c:forEach var="type" items="${types}">
														<option value="${type.answerConcept.id}">${type.answerConcept.displayString}</option>
													</c:forEach>
											</select>
										</td>
									</tr>
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
											&nbsp;
											<select  id="microscopyResult" name="microscopyResult">
													<option hidden selected value=""></option>
													<c:forEach var="result" items="${microscopyResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
											</select>
										</td>
									</tr>
									
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
											&nbsp;
											<openmrs_tag:dateField formFieldName="dateResult" startValue=""/>
										</td>
									</tr>
									<!-- Omar Fields Start -->
									<tr id="controlRegimentRow1">
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.tb03"/>:</font>
											&nbsp;
											<input type="text"  size="10" id="tb03" name="tb03">
										</td>
									</tr>
									<tr id="controlRegimentRow2" >
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.year"/>:</font>
											&nbsp;
											<openmrs_tag:dateField formFieldName="year" startValue=""/>
										</td>
									</tr>
									<!-- Omar Fields End -->
									<!-- Omar Section Start -->
								 	 <tr>
										<td>
											<br>
											<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.regionalLabInfo"/></b>
										</td>
									</tr>
										<tr id="regionalLabViewRow1">
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.regionalLabNo"/>:</font>
											&nbsp;
											<input type="text"  size="10" id="regionalLabNo" name="regionalLabNo">
										</td>
									</tr>
									<tr id="regionalLabViewRow2" >
										<td>
											<font style="font-weight:bold"><spring:message code="labmodule.labEntry.MT"/>:</font>
											&nbsp;
											<select id="mtbRegionalResult" name="mtbRegionalResult" onChange="onChangeMtb(this)">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${mtbResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
											</select>
										<td>
									</tr>
									<tr id="regionalLabViewRow3" >
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.H"/>:</font>
											&nbsp;
											<select id="hResult" name="hResult" onChange="">
												<option hidden selected value=""></option>
													<c:forEach var="hConcept" items="${rConcepts}">
														<option value="${hConcept.answerConcept.id}">${hConcept.answerConcept.displayString}</option>
													</c:forEach>
											</select>
										</td>
									</tr>
									<tr id="regionalLabViewRow4" >
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.R"/>:</font>
											&nbsp;
											<select id="rResult" name="rResult" onChange="">
													<option hidden selected value=""></option>
													<c:forEach var="rConcept" items="${rConcepts}">
														<option value="${rConcept.answerConcept.id}">${rConcept.answerConcept.displayString}</option>
													</c:forEach>
											</select>

										</td>
									</tr>
									<tr id="regionalLabViewRow5" >
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert"/>:</font>
											&nbsp;
											<select id="xpertMtbRif" name="xpertMtbRif" onChange="">
													<option hidden selected value=""></option>
													<c:forEach var="xpert" items="${xpertMtbRif}">
														<option value="${xpert.answerConcept.id}">${xpert.answerConcept.displayString}</option>
													</c:forEach>
											</select>

										</td>
									</tr>
									<tr id="regionalLabViewRow6" >
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.dateObservedGrowth"/>:</font>
											&nbsp;
											<openmrs_tag:dateField formFieldName="dateObservedGrowth" startValue=""/>
										</td>
									</tr>
									<tr id="regionalLabViewRow7" >
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.cultureResult"/>:</font>
											&nbsp;
											<select id="regionalCultureResult" name="regionalCultureResult" onChange="">
													<option hidden selected value=""></option>
													<c:forEach var="culture" items="${cultureResults}">
														<option value="${culture.answerConcept.id}">${culture.answerConcept.displayString}</option>
													</c:forEach>
											</select>
										</td>
									</tr>
									<!-- Omar Section Ends -->
	
								</table>
							
							</div>
							
						</td>
					</tr> --%>
					
					
					<tr>
						<td>
							<br>
						</td>
					</tr>
					
					<tr>
						<td>
						<openmrs:hasPrivilege privilege="Add Test Result">
							<button type="button" id="saveSpecimen" onclick="validateAndSubmit(this);"><spring:message code="mdrtb.save" text="Save"/></button>
						</openmrs:hasPrivilege>	
							<button type="reset" id="cancelSpecimen" onclick='onClick(this,"")'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
						</td>
					</tr>
				
				</table>
	
			</form>
		
		</div>
	</div>
		<!-- END OF Specimen Detail DIV -->
	</div>

<!-- END RIGHT-HAND COLUMN -->

<br> <br>

<%@ include file="/WEB-INF/view/module/labmodule/dotsFooter.jsp"%>

<script type='text/javascript'>

	var cultureMgitCounter = 0;
	var cultureLjCounter =0;
	var contaminatedTubesCounter =0;
	
	var dst1MgitCounter =0;
	var dst1LjCounter =0;
	
	var dst2MgitCounter =0;
	var dst2LjCounter =0;
	
	
	var baseUrl = "";
	
	document.getElementById('new_column').style.display = "none";
	document.getElementById('old_column').style.display = "none";

	//sortSelect(document.getElementById('microscopyResult'));
	//sortSelect(document.getElementById('microscopyResult_e'));
	sortSelect(document.getElementById('sampleResult'));
	//sortSelect(document.getElementById('cultureResult'));

	if(window.location.href.indexOf("labResultId") > -1) {
		document.getElementById('old_column').style.display = "block";
		document.getElementById("specimen_edit_span").style.display = "block";

		if(window.location.href.indexOf("edit") > -1) {
			document.getElementById("specimen_edit").style.display = "block";
			document.getElementById("specimen_view").style.display = "none";
		}
		else{
			document.getElementById("specimen_edit").style.display = "none";
			document.getElementById("specimen_view").style.display = "block";
		}
		
		document.getElementById('bacterioscopyDiv').style.display = "none";
	 	document.getElementById('xpertDiv').style.display = "none";
	 	document.getElementById('hainDiv').style.display = "none";
	 	document.getElementById('hain2Div').style.display = "none";
	 	document.getElementById('cultureDiv').style.display = "none";
	 	document.getElementById('dst1div').style.display = "none";	 	
	 	document.getElementById('dst2div').style.display = "none";
	 	//document.getElementById('peripheral_div').style.display = "none";
    } 

	function validateAndSubmitTest(obj){

		var theId = obj.id;
		var idArray = theId.split("_");
		
		if(idArray[0] == 'updateMicroscopy'){

			var e = document.getElementById('sampleDate_'+idArray[1]);
			var sampleDate = e.value;
			
			e = document.getElementById('sampleAppearance_'+idArray[1]);
			var sampleAppearance = e.options[e.selectedIndex].text;

			e = document.getElementById('sampleResult_'+idArray[1]);
			var sampleResult = e.options[e.selectedIndex].text;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			var errorText = '';

			if (sampleResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (sampleDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(sampleDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
// 			else if (isDateBefore(resultDate,dateRecieve)){
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
// 			}
			if (sampleAppearance == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noAppearance' text='Please specify Sample Appearance.'/>" + "\n";
			}

			if(errorText == '') {
				document.forms["editMicroscopyResults_"+idArray[1]].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}
			
		}

		else if(idArray[0] == 'updateXpert'){
			
			var e = document.getElementById('xpertTestDate_'+idArray[1]);
			var resultDate = e.value;

			e = document.getElementById('mtbXpertResult_'+idArray[1]);
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('rifXpertResult_'+idArray[1]);
			var rifResult = e.options[e.selectedIndex].value;

			e = document.getElementById('xpertError_'+idArray[1]);
			var error = e.value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			var errorText = '';

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if(!document.getElementById('rifXpertResult_'+idArray[1]).disabled && rifResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRifResult' text='Please specify Rif Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
			if(errorText == '') {
				document.forms["editXpertResults_"+idArray[1]].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}
			
		}

		else if (idArray[0] == 'updateHain'){

			var e = document.getElementById('hainTestDate_'+idArray[1]);
			var resultDate = e.value;

			e = document.getElementById('mtbHainResult_'+idArray[1]);
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('rifHainResult_'+idArray[1]);
			var rifResult = e.options[e.selectedIndex].value;

			e = document.getElementById('inhHainResult_'+idArray[1]);
			var inhResult = e.options[e.selectedIndex].value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			var errorText = '';
			
			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if(!document.getElementById('rifHainResult_'+idArray[1]).disabled && rifResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRifResult' text='Please specify Rif Result.'/>" + "\n";
			}
			if(!document.getElementById('inhHainResult_'+idArray[1]).disabled && inhResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noInhResult' text='Please specify inh Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}

			if(errorText == '') {	
				document.forms["editHainResults_"+idArray[1]].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}	
		}

		else if(idArray[0] == 'updateHain2'){

			var e = document.getElementById('hain2TestDate_'+idArray[1]);
			var resultDate = e.value;

			e = document.getElementById('mtbHain2Result_'+idArray[1]);
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('moxHain2Result_'+idArray[1]);
			var moxResult = e.options[e.selectedIndex].value;

			e = document.getElementById('cmHain2Result_'+idArray[1]);
			var cmResult = e.options[e.selectedIndex].value;

			e = document.getElementById('erHain2Result_'+idArray[1]);
			var erResult = e.options[e.selectedIndex].value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;
			
			var	errorText = '';
			
			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if(!document.getElementById('moxHain2Result_'+idArray[1]).disabled && moxResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMoxResult' text='Please specify MOX/OFX Result.'/>" + "\n";
			}
			if(!document.getElementById('cmHain2Result_'+idArray[1]).disabled && cmResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noCmResult' text='Please specify Km/ Am/ Cm Result.'/>" + "\n";
			}
// 			if(!document.getElementById('erHain2Result_'+idArray[1]).disabled && erResult == ''){
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noErResult' text='Please specify E Result.'/>" + "\n";
// 			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}	

			if(errorText == '') {
				document.forms["edithain2Results_"+idArray[1]].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}	
			
		}

		else if (idArray[0] == 'updateCulture'){

		var errorText = '';
		
		e = document.getElementById('incoculationDate_'+idArray[1]);
		var dateRecieve = e.value;
		
		var e = document.getElementById('cultureReportingDate_'+idArray[1]);
		var resultDate = e.value;
		
		if (resultDate == '') {
			errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
		}
		if (dateRecieve == '') {
			errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateReceivedInFuture' text='Please specify Inoculation Date.'/>" + "\n";
		}
		else if(isFutureDate(resultDate)){
			errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
		}			
		else if (isDateBefore(resultDate,dateRecieve)){
			errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
		}
			
			if(errorText == '') {
			var hidden = document.getElementById('mgit_sub_e');
			if(hidden != null)
			{
				alert(hidden.value);
			}
				document.forms["editCultureResults_"+idArray[1]].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}	
		}
		else if (idArray[0] == 'updateDst1'){
			var errorText = '';
			
			var e = document.getElementById('dst1ReportingDate_'+idArray[1]);
			var resultDate = e.value;
			
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}			
			
			if(errorText == '') {
				document.forms["editDst1Results_"+idArray[1]].submit();
			}
			else{
				
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}	
		}
		else if (idArray[0] == 'updateDst2'){
			var errorText = '';
			var e = document.getElementById('dst2ReportingDate_'+idArray[1]);
			var resultDate = e.value;
			
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			if(errorText == '') {
				document.forms["editDst2Results_"+idArray[1]].submit();
			}
			else{
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}	
		}

	}
	
	function validateAndSubmit(obj) {

		var theId = obj.id;

		if(theId == 'updateSpecimen'){

			//var e = document.getElementById('provider_e');
			//var provider = e.options[e.selectedIndex].value;
	
			e = document.getElementById('specimen_oblast');
			var lab = e.options[e.selectedIndex].value;

			e = document.getElementById('labNumber_e');
			var labNumber = e.value;
	
			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;
	
			e = document.getElementById('dateInvestigation_e');
			var dateInvestigation = e.value;
	
			e = document.getElementById('requestingLabName_e');
			var requestFacility = e.options[e.selectedIndex].text;
	
			e = document.getElementById('investigationPurpose_e');
			var investigationPurpose = e.options[e.selectedIndex].text;
	
			e = document.getElementById('biologicalSpecimen_e');
			var biologicalSpecimen = e.options[e.selectedIndex].text;

			/* e = document.getElementById('peripheralLabNo_e');
			var peripherallabNumber = e.value;

			e = document.getElementById('microscopyResult_e');
			var microscopyResult = e.options[e.selectedIndex].text;

			e = document.getElementById('dateResult_e');
			var dateResult = e.value; */
			
			var errorText = '';	
	
// 			if (provider == '') {
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noCollector' text='Please specify who collected this sample.'/>" + "\n";
// 			}
			if (lab == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLab' text='Please specify a laboratory.'/>" + "\n";
			}
			if (labNumber == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLabNumber' text='Please specify Lab Test Number.'/>" + "\n";
			}
			if (dateRecieve == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateReceived' text='Please specify a Recieve Date.'/>" + "\n";
			}
			else if(isFutureDate(dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateReceivedInFuture' text='The date received must not be in the future.'/>" + "\n";
			}
			if (dateInvestigation == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateInvestigation' text='Please specify a Investigation Date.'/>" + "\n";
			}
			else if(isFutureDate(dateInvestigation)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateInvestigationInFuture' text='The date investigation must not be in the future.'/>" + "\n";
			}
			if (requestFacility == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRequestingFacility' text='Please Specify the requesting medical facility.'/>" + "\n";
			}
			if (investigationPurpose == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noInvestigationPurpose' text='Please Specify the purpose of investigation.'/>" + "\n";
			}
			if (biologicalSpecimen == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noBiologicalSpecimen' text='Please Specify the biological specimen.'/>" + "\n";
			}
			/* if(peripherallabNumber == ''){

				if(microscopyResult != '' || dateResult != '')
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noPeripheralLabNumber' text='Please Specify the Peripheral Lab Number.'/>" + "\n";
			} 
			if(microscopyResult == ''){

				if(peripherallabNumber != '' || dateResult != '')
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please Specify the Microscopy Result.'/>" + "\n";
			}
			if(dateResult == ''){

				if(microscopyResult != '' || peripherallabNumber != '')
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please Specify the Date of result.'/>" + "\n";
			}*/
			
			
			if(errorText == '') {
				document.forms["updateTestResults"].submit();;
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}
			
		}

		else if(theId == 'saveSpecimen'){

// 			var e = document.getElementById('provider');
// 			var provider = e.options[e.selectedIndex].value;
	
			e = document.getElementById("oblast");
			var lab = e.options[e.selectedIndex].value;
	
			e = document.getElementById('dateRecieve');
			var dateRecieve = e.value;

			e = document.getElementById('labNumber');
			var labNumber = e.value;
			
			e = document.getElementById('dateInvestigation');
			var dateInvestigation = e.value;
	
			e = document.getElementById('requestingLabName');
			var requestFacility = e.options[e.selectedIndex].text;
	
			e = document.getElementById('investigationPurpose');
			var investigationPurpose = e.options[e.selectedIndex].text;
	
			e = document.getElementById('biologicalSpecimen');
			var biologicalSpecimen = e.options[e.selectedIndex].text;

			/* e = document.getElementById('peripheralLabNo');
			var peripheralLabNumber = e.value; */

			/* e = document.getElementById('microscopyResult');
			var microscopyResult = e.options[e.selectedIndex].text; */

			/* e = document.getElementById('dateResult');
			var dateResult = e.value;
 */
			var errorText = ''
	
// 			if (provider == '') {
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noCollector' text='Please specify who collected this sample.'/>" + "\n";
// 			}
			if (lab == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLab' text='Please specify a laboratory.'/>" + "\n";
			}
			if (labNumber == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLabNumber' text='Please specify Lab Test Number.'/>" + "\n";
			}
			if (dateRecieve == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateReceived' text='Please specify a Recieve Date.'/>" + "\n";
			}
			else if(isFutureDate(dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateReceivedInFuture' text='The date received must not be in the future.'/>" + "\n";
			}
			if (dateInvestigation == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateInvestigation' text='Please specify a Investigation Date.'/>" + "\n";
			}
			else if(isFutureDate(dateInvestigation)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateInvestigationInFuture' text='The date investigation must not be in the future.'/>" + "\n";
			}
			if (requestFacility == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRequestingFacility' text='Please Specify the requesting medical facility.'/>" + "\n";
			}
			if (investigationPurpose == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noInvestigationPurpose' text='Please Specify the purpose of investigation.'/>" + "\n";
			}
			if (biologicalSpecimen == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noBiologicalSpecimen' text='Please Specify the biological specimen.'/>" + "\n";
			}
			/* if(document.getElementById('peripheral').checked){

				 if (peripheralLabNumber == '') {
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noPeripheralLabNumber' text='Please specify Peripheral Lab Number.'/>" + "\n";
				}
				 if (microscopyResult == '') {
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
				} 
				 if (dateResult == '') {
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
				} 
				 else if(isFutureDate(dateResult)){
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The date result must not be in the future.'/>" + "\n";
				} 
				
			} */
			
			if(errorText == '') {
				document.forms["addTestResults"].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}
		}
	}
	
	function appendNumberToId(table, number)
	{
		var row = document.getElementById(table.id).getElementsByTagName("tr");
		
		for (var i = 0; i < row.length; i++) {
		  var cells = row[i].cells
		  for(var j=0; j < cells.length; j ++)
			{
				var td = cells[j]
				children = td.childNodes;
				for(var k = 0; k < children.length; k ++ )
				{
					var child = children[k];
					if(child != null)
					{
						if(child.id != '')
						{
							child.id =  child.id + "_" + number;
							child.name = child.id;
						}
					}
				}
			}
		}
	}
	
	function cloneElement(container, targetId)
	{
		var elem = document.getElementById(targetId);				    
	    if (elem && container) {
	        var clone = elem.cloneNode(true);	        
	        container.appendChild(clone);
	        return clone;
	    }
	}
	
	// ON CLICK ...
	function onClick(obj,loc){
		
		 var theId = obj.id;

		 // Show Specimen and Test form
		 if(theId == 'quickEntryAddButton'){

			 var r = true;
			 
			 if(document.getElementById('new_column').style.display == 'block'){
				 r = confirm("Are you sure you want to open new 'Add Specimen' form? All unsaved information from previous form will be discarded.");
			 }	 
			
			 if (r == true) {
				 document.getElementById('new_column').style.display = "block";
				 document.getElementById('labResult_div').style.display = "block"; 
				 document.getElementById('old_column').style.display = "none";
				 
				 document.getElementById("cancelSpecimen").click();
				 document.getElementById("cancelBacterioscopy").click();
				 document.getElementById("cancelXpert").click();
				 document.getElementById("cancelHain").click();
				 document.getElementById("cancelHain2").click();
				 document.getElementById("cancelTest").click();
				 document.getElementById("cancelCulture").click();
			 }
			
		 }

		 // Add Test Form a/c to selection
		 else if(theId == 'addTest'){

			 var e = document.getElementById('test_selected');
			 var testResult = e.options[e.selectedIndex].value;	

			 if(testResult != ''){
			 
				 document.getElementById('labResult_div').style.display = "none";
				 document.getElementById('bacterioscopyDiv').style.display = "none";
				 document.getElementById('xpertDiv').style.display = "none";
				 document.getElementById('hainDiv').style.display = "none";
				 document.getElementById('hain2Div').style.display = "none";
				 document.getElementById('cultureDiv').style.display = "none";
				 document.getElementById('dst1div').style.display = "none";
				 document.getElementById('dst2div').style.display = "none";
					
			 }
			 
			 if(testResult == 'bacterioscopy')
				document.getElementById('bacterioscopyDiv').style.display = "block";
			 else if(testResult == 'xpert')
					document.getElementById('xpertDiv').style.display = "block";
			 else if(testResult == 'hain')
					document.getElementById('hainDiv').style.display = "block";
			 else if(testResult == 'hain2')
					document.getElementById('hain2Div').style.display = "block";
			 else if(testResult == 'culture')
					document.getElementById('cultureDiv').style.display = "block";
			 else if(testResult == 'dst1')
					document.getElementById('dst1div').style.display = "block";
			 else if(testResult == 'dst2')
					document.getElementById('dst2div').style.display = "block";
			 
						
		}
		 //DST-2 STARTS HERE
		 else if(theId == 'addDst2Type'){
			 var dst2Type = document.getElementById('dst2_selected');			 
				
			 var dst2Index;
			 if(dst2Type != '')
			 {
			 	dst2Index = dst2Type.selectedIndex;
			 }
			 else
			 {
			 	return;
			 }

			 //Fill DS2-MGIT 
			 if(dst2Index == 1){
			 	dst2MgitCounter ++;				
			 	
			 	//Show UI related to test  
			 	var dst2_mgit =  document.getElementById('mgit_dst2');				
			 	dst2_mgit.style.display = "block";		
			 	
			 	//cloning stuff only if counter is greater than 1				
			 	if(dst2MgitCounter  > 1)
			 	{
			 		var table = cloneElement(dst2_mgit,"table_dst2_mgit_1");					
			 		table.id = "table_dst2_mgit_" + dst2MgitCounter;
			 		//Update IDs of all children to access later in Controller
			 		appendNumberToId(table, dst2MgitCounter);		        
			 		//Show the new table
			 		table.style.display = "block";
			 	}				
			 }
			 //Fill DST2-LJ
			 else if(dst2Index == 2)
			 {
			 	dst2LjCounter ++;				
			 	
			 	//Show UI related to test  
			 	var lj =  document.getElementById('lj_dst2');				
			 	lj.style.display = "block";		
			 	
			 	//cloning stuff only if counter is greater than 1				
			 	if(dst2LjCounter  > 1)
			 	{
			 		var table = cloneElement(lj,"table_dst2_lj_1");					
			 		table.id = "table_dst2_lj_" + dst2LjCounter;
			 		//Update IDs of all children to access later in Controller
			 		appendNumberToId(table, dst2LjCounter);		        
			 		//Show the new table
			 		table.style.display = "block";
			 	}				
			 }

		}
		 else if(theId == 'addCultureType'){
		
			 var testResult = document.getElementById('culture_selected');
			 
			 
			 //var mgitSubcultureId = <%= Context.getService(TbService.class).getConcept(TbConcepts.CONTROL_TREATMENT_REGIMENT_I).getConceptId()%>;
			 //var ljSubcultureId = <%= Context.getService(TbService.class).getConcept(TbConcepts.CONTROL_TREATMENT_REGIMENT_I).getConceptId()%>;
			 //var contaminatedTubesId = <%= Context.getService(TbService.class).getConcept(TbConcepts.CONTROL_TREATMENT_REGIMENT_I).getConceptId()%>;
			 
			var cultureIndex;
			if(testResult != '')
			{
				cultureIndex = testResult.selectedIndex;
			}
			else
			{
				return;
			}

			//Fill MGIT 
			if(cultureIndex == 1){
				cultureMgitCounter ++;				
				
				//Show UI related to test  
				var mgit =  document.getElementById('mgit_culture');				
				mgit.style.display = "block";		
				
				//cloning stuff only if counter is greater than 1				
				if(cultureMgitCounter  > 1)
				{
					var table = cloneElement(mgit,"table_mgit_1");					
					table.id = "table_mgit_" + cultureMgitCounter;
					//Update IDs of all children to access later in Controller
			        appendNumberToId(table, cultureMgitCounter);		        
			        //Show the new table
			        table.style.display = "block";
			 	}				
			}
			//Fill LJ
			else if(cultureIndex == 2)
			{
				cultureLjCounter ++;				
				
				//Show UI related to test  
				var mgit =  document.getElementById('lj_culture');				
				mgit.style.display = "block";		
				
				//cloning stuff only if counter is greater than 1				
				if(cultureLjCounter  > 1)
				{
					var table = cloneElement(mgit,"table_lj_1");					
					table.id = "table_lj_" + cultureLjCounter;
					//Update IDs of all children to access later in Controller
			        appendNumberToId(table, cultureLjCounter);		        
			        //Show the new table
			        table.style.display = "block";
			 	}				
			}
			//Fill ContaminatedTubes
			else if(cultureIndex == 3)
			{
				contaminatedTubesCounter ++;				
				
				//Show UI related to test  
				var mgit =  document.getElementById('rc_culture');				
				mgit.style.display = "block";		
				
				//cloning stuff only if counter is greater than 1				
				if(contaminatedTubesCounter  > 1)
				{
					var table = cloneElement(mgit,"table_rc_1");					
					table.id = "table_rc_" + cultureLjCounter;
					//Update IDs of all children to access later in Controller
			        appendNumberToId(table, contaminatedTubesCounter);		        
			        //Show the new table
			        table.style.display = "block";
			 	}				
			}
			
		 } 

		 else if(theId == 'addDst1Type'){
			 var dst1Type = document.getElementById('dst1_selected');			 
				
			 var dst1Index;
			 if(dst1Type != '')
			 {
			 	dst1Index = dst1Type.selectedIndex;
			 }
			 else
			 {
			 	return;
			 }

			 //Fill DST1-MGIT 
			 if(dst1Index == 1){
			 	dst1MgitCounter ++;				
			 	
			 	//Show UI related to test  
			 	var dst1_mgit =  document.getElementById('mgit_dst1');				
			 	dst1_mgit.style.display = "block";		
			 	
			 	//cloning stuff only if counter is greater than 1				
			 	if(dst1MgitCounter  > 1)
			 	{
			 		var table = cloneElement(dst1_mgit,"table_dst1_mgit_1");					
			 		table.id = "table_dst1_mgit_" + dst1MgitCounter;
			 		//Update IDs of all children to access later in Controller
			 		appendNumberToId(table, dst1MgitCounter);		        
			 		//Show the new table
			 		table.style.display = "block";
			 	}				
			 }
			 //Fill DST1-LJ
			 else if(dst1Index == 2)
			 {
			 	dst1LjCounter ++;				
			 	
			 	//Show UI related to test  
			 	var lj =  document.getElementById('lj_dst1');				
			 	lj.style.display = "block";		
			 	
			 	//cloning stuff only if counter is greater than 1				
			 	if(dst1LjCounter  > 1)
			 	{
			 		var table = cloneElement(lj,"table_dst1_lj_1");					
			 		table.id = "table_dst1_lj_" + dst1LjCounter;
			 		//Update IDs of all children to access later in Controller
			 		appendNumberToId(table, dst1LjCounter);		        
			 		//Show the new table
			 		table.style.display = "block";
			 	}				
			 }

		}
		 

	    else if(theId == 'cancelBacterioscopy'){

			 document.getElementById('bacterioscopyDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();

		}
			
	    else if (theId == 'cancelXpert'){

			 document.getElementById('xpertDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
			
		else if (theId == 'cancelHain'){

			 document.getElementById('hainDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
			
		else if (theId == 'cancelHain2'){

			 document.getElementById('hain2Div').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}

		else if (theId == 'cancelCulture'){

			 document.getElementById('cultureDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
		else if (theId == 'cancelDst1'){

			 document.getElementById('dst1div').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
		else if (theId == 'cancelDst2'){

			 document.getElementById('dst2div').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
		else if (theId == 'cancelSpecimen'){

			 document.getElementById('labResult_div').style.display = "block"; 
			
			 document.getElementById('bacterioscopyDiv').style.display = "none";
			 document.getElementById('xpertDiv').style.display = "none";
			 document.getElementById('hainDiv').style.display = "none";
			 document.getElementById('hain2Div').style.display = "none";
			 document.getElementById('cultureDiv').style.display = "none";
			 //document.getElementById('peripheral_div').style.display = "none";

			 document.getElementById("cancelSpecimen").click();
			 document.getElementById("cancelBacterioscopy").click();
			 document.getElementById("cancelXpert").click();
			 document.getElementById("cancelHain").click();
			 document.getElementById("cancelHain2").click();
			 document.getElementById("cancelTest").click();
			 document.getElementById("cancelCulture").click();
		}

		else if (theId == 'peripheral'){

			if (document.getElementById('peripheral').checked) {
				document.getElementById('peripheral_div').style.display = "block";
				 
				var investigatePurposes = document.getElementById('investigationPurpose');				 
				var selectedConceptId = investigatePurposes.value;
				
				var regiment1= <%= Context.getService(TbService.class).getConcept(TbConcepts.CONTROL_TREATMENT_REGIMENT_I).getConceptId()%>
				var regiment2= <%= Context.getService(TbService.class).getConcept(TbConcepts.CONTROL_TREATMENT_REGIMENT_II).getConceptId()%>
				 
				if(selectedConceptId == regiment1 || selectedConceptId == regiment2) //We only want show TB03 and Year question when Control Regiment options are selected.
				{					
					document.getElementById('controlRegimentRow1').style.display = "block";
					document.getElementById('controlRegimentRow2').style.display = "block";
				}
				else
				{
					document.getElementById('controlRegimentRow1').style.display = "none";
					document.getElementById('controlRegimentRow2').style.display = "none";
				}
				
	        } else {
	        	//document.getElementById('peripheral_div').style.display = "none";
	        }
			
		}

		else if (theId == 'editSpecimenDetailButton'){

			document.getElementById("specimen_edit_span").style.display = "none";
			document.getElementById("specimen_edit").style.display = "block";
			document.getElementById("specimen_view").style.display = "none";
			console.log(loc)
			
			if(loc!="")
			{
			LabFindlocation.getlocation(loc, function(data) {
				$("#specimen_district").children('option').remove();
				LabFindlocation.getDistricts(data.oblastId, function(data1) {
				for(var i=0; i<data1.length; i++) {
					if(data1[i].id===data.districtId)
						$("#specimen_district").append("<option id='"+data1[i].id+"' value='"+data1[i].id+"' selected>"+data1[i].name+"</option>");
					else
						$("#specimen_district").append("<option id='"+data1[i].id+"' value='"+data1[i].id+"'>"+data1[i].name+"</option>");
					}
		    });
			$("#specimen_facility").children('option').remove();
			LabFindlocation.getfacilities(data.districtId, function(data1) {
				for(var i=0; i<data1.length; i++) {
					if(data1[i].id===data.facilityId)
						$("#specimen_facility").append("<option id='"+data1[i].id+"' value='"+data1[i].id+"' selected>"+data1[i].name+"</option>");
					else
						$("#specimen_facility").append("<option id='"+data1[i].id+"' value='"+data1[i].id+"'>"+data1[i].name+"</option>");
				}
			});
			$("#specimen_oblast").val(data.oblastId);
			});
			}
		}

		else if (theId == 'cancelUpdateSpecimen'){

			document.getElementById("specimen_edit_span").style.display = "block";
			document.getElementById("specimen_edit").style.display = "none";
			document.getElementById("specimen_view").style.display = "block";
			
		}	

  	}
	
	function stringToDate(_date,_format,_delimiter)
	{
	            var formatLowerCase=_format.toLowerCase();
	            var formatItems=formatLowerCase.split(_delimiter);
	            var dateItems=_date.split(_delimiter);
	            var monthIndex=formatItems.indexOf("mm");
	            var dayIndex=formatItems.indexOf("dd");
	            var yearIndex=formatItems.indexOf("yyyy");
	            var month=parseInt(dateItems[monthIndex]);
	            month-=1;
	            var formatedDate = new Date(dateItems[yearIndex],month,dateItems[dayIndex]);
	            return formatedDate;
	}

	function isFutureDate(dateRecieve)
	{
		var dateR;
		if(dateRecieve.includes("/")){
			dateR = stringToDate(dateRecieve,"mm/dd/yyyy","/");	
		}	
		else if (dateRecieve.includes(".")){
			dateR = stringToDate(dateRecieve,"dd.mm.yyyy",".");	
		}	
			
		var now = new Date();
		
		if(dateR > now)
			return true;
		else 
			return false;	

	}

	function isDateBefore(dateRecieve, dateCompare)
	{
		var dateR;
		if(dateRecieve.includes("/")){
			dateR = stringToDate(dateRecieve,"mm/dd/yyyy","/");	
		}	
		else if (dateRecieve.includes(".")){
			dateR = stringToDate(dateRecieve,"dd.mm.yyyy",".");	
		}	

		var dateC;
		if(dateCompare.includes("/")){
			dateC = stringToDate(dateCompare,"mm/dd/yyyy","/");	
		}	
		else if (dateCompare.includes(".")){
			dateC = stringToDate(dateCompare,"dd.mm.yyyy",".");	
		}	
		
		if(dateC > dateR)
			return true;
		else 
			return false;	

	}

	function deleteSubculture(obsId, cultureId, patientId,labId,cultureType)
	{
		var flag = confirm("Are you sure you want to delete this Culture? This data would not be recovered later");
		if(flag == true)
		{
			var submissionType="deleteSubculture";
			var form = document.getElementById("deleteSubCultureResults");
			var params = "?obsId=" +obsId+ "&cultureId=" +cultureId+ "&patientId=" +patientId+ "&labId=" + labId+ "&submissionType="+submissionType+ "&cultureType="+ cultureType;
			
			if(baseUrl == "")
			{
				baseUrl = form.action; 
			}			
			form.action = baseUrl + params;
			alert(form.action);
			form.submit();	
		}
	}
	
	function deleteSubdst(obsId,patientId,labId,submissionType,methodMapping)
	{
		var flag = confirm("Are you sure you want to delete this Sub-test? This data would not be recovered later");
		if(flag == true)
		{
			var form = document.getElementById("deleteSubDstResults");
			var params = "?obsId=" +obsId+ "&patientId=" +patientId+ "&submissionType="+submissionType+ "&methodMapping="+ methodMapping;
			
			if(baseUrl == "")
			{
				baseUrl = form.action; 
			}			
			form.action = baseUrl + params;
			alert(form.action);
			form.submit();	
		}
	}

	function editTest(obj,location,id,type,r){

		var theId = obj.id;
		console.log("#"+type+"_district_"+id)
		console.log("#"+type+"_oblast_"+id)
		
		var idArray = theId.split("_");
		
		if(type!="")
		{
		LabFindlocation.getlocation(location, function(data) {
			$("#"+type+"_district_"+id).children('option').remove();
			$("#"+type+"_district_"+id).append("<option value=''></option>");
			LabFindlocation.getDistricts(data.oblastId, function(data1) {
			for(var i=0; i<data1.length; i++) {
				if(data1[i].id===data.districtId)
					$("#"+type+"_district_"+id).append("<option id='"+data1[i].id+"' value='"+data1[i].id+"' selected>"+data1[i].name+"</option>");
				else
					$("#"+type+"_district_"+id).append("<option id='"+data1[i].id+"' value='"+data1[i].id+"'>"+data1[i].name+"</option>");
				
			}
	    });
		$("#"+type+"_facility_"+id).children('option').remove();
		$("#"+type+"_facility_"+id).append("<option value=''></option>");
		LabFindlocation.getfacilities(data.districtId, function(data1) {
			for(var i=0; i<data1.length; i++) {
				if(data1[i].id===data.facilityId)
						$("#"+type+"_facility_"+id).append("<option id='"+data1[i].id+"' value='"+data1[i].id+"' selected>"+data1[i].name+"</option>");
				else
					$("#"+type+"_facility_"+id).append("<option id='"+data1[i].id+"' value='"+data1[i].id+"'>"+data1[i].name+"</option>");
					
			}
		});
		$("#"+type+"_oblast_"+id).val(data.oblastId);
		});
		}
		if(idArray[0] == 'editMicroscopySpan'){

			document.getElementById("deleteMicroscopyResults_"+idArray[1]).style.display = "none";
			document.getElementById("editMicroscopyResults_"+idArray[1]).style.display = "block";

		//	sortSelect(document.getElementById('sampleResult_'+idArray[i]));
		}
		else if(idArray[0] == 'editXpertSpan'){

			document.getElementById("deleteXpertResults_"+idArray[1]).style.display = "none";
			document.getElementById("editXpertResults_"+idArray[1]).style.display = "block";
		}
		else if(idArray[0] == 'editHainSpan'){

			document.getElementById("deleteHainResults_"+idArray[1]).style.display = "none";
			document.getElementById("editHainResults_"+idArray[1]).style.display = "block";
		}
		else if(idArray[0] == 'editHain2Span'){
			document.getElementById("deletehain2Results_"+idArray[1]).style.display = "none";
			document.getElementById("edithain2Results_"+idArray[1]).style.display = "block";
		}
		else if(idArray[0] == 'editCultureSpan'){
			document.getElementById("deleteCultureResults_"+idArray[1]).style.display = "none";
			document.getElementById("editCultureResults_"+idArray[1]).style.display = "block";

			//sortSelect(document.getElementById('cultureResult_'+idArray[i]));
		}
		else if(idArray[0] == 'editDst1Span'){
			document.getElementById("deleteDst1_"+idArray[1]).style.display = "none";
			document.getElementById("editDst1Results_"+idArray[1]).style.display = "block";

			//sortSelect(document.getElementById('cultureResult_'+idArray[i]));
		}
		else if(idArray[0] == 'editDst2Span'){
			document.getElementById("deleteDst2_"+idArray[1]).style.display = "none";
			document.getElementById("editDst2Results_"+idArray[1]).style.display = "block";

			//sortSelect(document.getElementById('cultureResult_'+idArray[i]));
		}
		
		
	}

	/* function setTest(id,type,location)
	{
		LabFindlocation.getlocation(location, function(data) {
			
			if($("#"+type+"_oblast_"+id))
				$("#"+type+"_oblast_"+id).val(data.oblastId);
			if($("#"+type+"_district_"+id))
				$("#"+type+"_district_"+id).val(data.districtId);
			if($("#"+type+"_facility_"+id))
				$("#"+type+"_facility_"+id).val(data.facilityId)
		});
	}
	
	function setSpecimen(data)
	{ console.log(data)
		if($("#specimen_oblast"))
				$("#specimen_oblast").val(data.oblastId);
		if($("#specimen_district"))
				$("#specimen_district").val(data.districtId);
		if($("#specimen_facility"))
				$("#specimen_facility").val(data.facilityId)
	}
	 */
	function cancelUpdate(obj){

		var theId = obj.id;

		var idArray = theId.split("_");

		if(idArray[0] == 'cancelUpdateMicroscopy'){
			document.getElementById("deleteMicroscopyResults_"+idArray[1]).style.display = "block";
			document.getElementById("editMicroscopyResults_"+idArray[1]).style.display = "none";
		}
		else if(idArray[0] == 'cancelUpdateXpert'){
			document.getElementById("deleteXpertResults_"+idArray[1]).style.display = "block";
			document.getElementById("editXpertResults_"+idArray[1]).style.display = "none";
		}
		else if(idArray[0] == 'cancelUpdateHain'){
			document.getElementById("deleteHainResults_"+idArray[1]).style.display = "block";
			document.getElementById("editHainResults_"+idArray[1]).style.display = "none";
		}
		else if(idArray[0] == 'cancelUpdateHain2'){
			document.getElementById("deletehain2Results_"+idArray[1]).style.display = "block";
			document.getElementById("edithain2Results_"+idArray[1]).style.display = "none";
		}
		else if(idArray[0] == 'cancelUpdateCulture'){
			document.getElementById("deleteCultureResults_"+idArray[1]).style.display = "block";
			document.getElementById("editCultureResults_"+idArray[1]).style.display = "none";
		}
		else if(idArray[0] == 'cancelUpdateDst1'){
			document.getElementById("deleteDst1_"+idArray[1]).style.display = "block";
			document.getElementById("editDst1Results_"+idArray[1]).style.display = "none";
		}
		else if(idArray[0] == 'cancelUpdateDst2'){
			document.getElementById("deleteDst2_"+idArray[1]).style.display = "block";
			document.getElementById("editDst2Results_"+idArray[1]).style.display = "none";
		}

	}
		
	function deleteTest(obj){

		var theId = obj.id;

		var idArray = theId.split("_");
		
		if(idArray[0] == 'deleteMicroscopySpan'){

			var flag = confirm("Are you sure you want to delete the Microscopy test Record?");
			
			if(flag == true)
				document.forms["deleteMicroscopyResults_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteXpertSpan'){

			var flag = confirm("Are you sure you want to delete the Xpert test Record?");
			
			if(flag == true)
				document.forms["deleteXpertResults_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteHainSpan'){

			var flag = confirm("Are you sure you want to delete the Hain test Record?");
			
			if(flag == true)
				document.forms["deleteHainResults_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteHain2Span'){

		var flag = confirm("Are you sure you want to delete the Hain-2 test Record?");
			
			if(flag == true)
				document.forms["deletehain2Results_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteCultureSpan'){

		var flag = confirm("Are you sure you want to delete the Culture test Record?");
			
			if(flag == true)
				document.forms["deleteCultureResults_"+idArray[1]].submit();
			
		}

		else if(idArray[0] == 'deleteTest'){

			var flag = confirm("Are you sure you want to delete the whole specimen Record for lab test #:"+idArray[2]);
			
			if(flag == true)
				document.getElementById("delete_"+idArray[1]).submit();
	
		}
	}

	function saveTest(obj){

		var theId = obj.id;
		var formName = '';

		var errorText = '';
		
		if (theId == 'addMicroscopyTest'){
			formName = "addMicroscopyResults";

			var e = document.getElementById('sampleResult');
			var result = e.options[e.selectedIndex].value;
	
			e = document.getElementById('sampleDate');
			var resultDate = e.value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			e = document.getElementById('sampleAppearance');
			var appearance = e.options[e.selectedIndex].value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;
			
			if (result == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			if (appearance == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noAppearance' text='Please specify Sample Appearance.'/>" + "\n";
			}
			
		}
		else if (theId == 'addXpertTest'){
			formName = "addXpertResults";

			var e = document.getElementById('xpertTestDate');
			var resultDate = e.value;

			e = document.getElementById('mtbXpertResult');
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('rifXpertResult');
			var rifResult = e.options[e.selectedIndex].value;

			e = document.getElementById('xpertError');
			var error = e.value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMtbResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
// 			if(!document.getElementById('rifXpertResult').disabled && rifResult == ''){
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRifResult' text='Please specify Rif Result.'/>" + "\n";
// 			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
		}
		else if (theId == 'addHainTest'){
			formName = "addHAINResults";

			var e = document.getElementById('hainTestDate');
			var resultDate = e.value;

			e = document.getElementById('mtbHainResult');
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('rifHainResult');
			var rifResult = e.options[e.selectedIndex].value;

			e = document.getElementById('inhHainResult');
			var inhResult = e.options[e.selectedIndex].value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if(!document.getElementById('rifHainResult').disabled && rifResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRifResult' text='Please specify Rif Result.'/>" + "\n";
			}
			if(!document.getElementById('inhHainResult').disabled && inhResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noInhResult' text='Please specify inh Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}	
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}		
		}
		else if (theId == 'addHain2Test'){
			formName = "addHAIN2Results";

			var e = document.getElementById('hain2TestDate');
			var resultDate = e.value;

			e = document.getElementById('mtbHain2Result');
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('moxHain2Result');
			var moxResult = e.options[e.selectedIndex].value;

			e = document.getElementById('cmHain2Result');
			var cmResult = e.options[e.selectedIndex].value;

			e = document.getElementById('erHain2Result');
			var erResult = e.options[e.selectedIndex].value;

			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if(!document.getElementById('moxHain2Result').disabled && moxResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMoxResult' text='Please specify MOX/OFX Result.'/>" + "\n";
			}
			if(!document.getElementById('cmHain2Result').disabled && cmResult == ''){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noCmResult' text='Please specify Km/ Am/ Cm Result.'/>" + "\n";
			}
// 			if(!document.getElementById('erHain2Result').disabled && erResult == ''){
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noErResult' text='Please specify E Result.'/>" + "\n";
// 			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}	
// 			else if (isDateBefore(resultDate,dateRecieve)){
// 				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
// 			}
			
		}
		else if (theId == 'addCultureTest'){
			formName = "addCultureResults";
				
			var hiddenMgit = document.getElementById('mgit_sub');
			var hiddenLj = document.getElementById('lj_sub');
			var hiddenRc = document.getElementById('rc_sub');
			
			
			e = document.getElementById('incoculationDate');
			var dateRecieve = e.value;
			
			var e = document.getElementById('cultureReportingDate');
			var resultDate = e.value;
			
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			
			if (dateRecieve == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateReceivedInFuture' text='Please specify Inoculation Date.'/>" + "\n";
			}
			
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}			
			else if (isDateBefore(resultDate,dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
			if(hiddenMgit != null)
			{
				hiddenMgit.value = cultureMgitCounter;
			}
			if(hiddenLj != null)
			{
				hiddenLj.value = cultureLjCounter;
			}
			if(hiddenRc != null)
			{
				hiddenRc.value = contaminatedTubesCounter;
			}			
		}
		else if(theId == 'addDst1'){
			formName = "addDst1Results";
			
			var hiddenDst1Mgit = document.getElementById('dst1_mgit_sub');
			var hiddenDst1Lj = document.getElementById('dst1_lj_sub');
			
			var e = document.getElementById('dst1ReportingDate');			
			var resultDate = e.value;
			
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
			if(hiddenDst1Mgit != null)
			{
				hiddenDst1Mgit.value = dst1MgitCounter;
			}
			if(hiddenDst1Lj != null)
			{
				hiddenDst1Lj.value = dst1LjCounter;
			}
		}
		
		else if(theId == 'addDst2'){
			formName = "addDst2Results";
			
			var hiddenDst2Mgit = document.getElementById('dst2_mgit_sub');
			var hiddenDst2Lj = document.getElementById('dst2_lj_sub');
			
			var e = document.getElementById('dst2ReportingDate');			
			var resultDate = e.value;
			
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
			if(hiddenDst2Mgit != null)
			{
				hiddenDst2Mgit.value = dst2MgitCounter;
			}
			if(hiddenDst2Lj != null)
			{
				hiddenDst2Lj.value = dst2LjCounter;
			}
		}


		if(errorText == '') {
			document.forms[formName].submit();
		}
		else{

			errorText = "Fix following error(s) to continue:\n" + errorText;
			confirm(errorText); 
			return false;
		}
		
	}

	function sortSelect(selElem) {
	    var tmpAry = new Array();
	    for (var i=0;i<selElem.options.length;i++) {
	        tmpAry[i] = new Array();
	        tmpAry[i][0] = selElem.options[i].value;
	        tmpAry[i][1] = selElem.options[i].text;
	    }
	    tmpAry.sort();
	    while (selElem.options.length > 0) {
	        selElem.options[0] = null;
	    }
	    for (var i=0;i<tmpAry.length;i++) {
	        var op = new Option(tmpAry[i][1], tmpAry[i][0]);
	        selElem.options[i] = op;
	    }
	    return;
	}
	

	function onChangeMgit(elem){
		var theId = elem.id;
		
		var htm = <%= Context.getService(TbService.class).getConcept(TbConcepts.MGIT_HTM).getConceptId()%>;
		var mt = <%= Context.getService(TbService.class).getConcept(TbConcepts.MGIT_MT).getConceptId()%>;
		
		//Dynamically added elements contain "_" to differntiate and select resutls individually
		if(elem.id.includes('_')){
			
			var idArray = theId.split("_");
			//Main Culture MGIT
			if(idArray[0] == 'mgitResults'){			
				var elemMtid = document.getElementById('mtidResults'+idArray[1]);
				if(elem.value == htm || elem.value == mt)
				{
					elemMtid.disabled=false;
				}
			}
			//Mgit Subclture's MGIT
// 			else if(idArray[0] == 'mgit_mgit'){
// 				var elemMtid = document.getElementById('mgit_mtid_'+idArray[1]);
// 				if(elem.value == htm || elem.value == mt)
// 				{
// 					elemMtid.disabled=false;
// 				}
// 			}
// 			//LJ Subculture MGIT
// 			else if(idArray[0] == 'lj_lj'){
// 				var elemMtid = document.getElementById('lj_mtid_'+idArray[1]);
// 				if(elem.value == htm || elem.value == mt)
// 				{
// 					elemMtid.disabled=false;
// 				}
// 			}
// 			//RC Contamintated tubes MGIT
// 			else if(idArray[0] == 'rc_rc'){
// 				var elemMtid = document.getElementById('rc_mtid_'+idArray[1]);
// 				if(elem.value == htm || elem.value == mt)
// 				{
// 					elemMtid.disabled=false;
// 				}
// 			}
		}
		//This is from the default Culture table
		else
		{
			if(elem.id == 'mgitResults'){			
				var elemMtid = document.getElementById('mtidResults');
				if(elem.value == htm || elem.value == mt)
				{
					elemMtid.disabled=false;
				}
			}
			//Mgit Subclture's MGIT
// 			else if(elem.id == 'mgit_mgit'){
// 				var elemMtid = document.getElementById('mgit_mtid');
// 				if(elem.value == htm || elem.value == mt)
// 				{
// 					elemMtid.disabled=false;
// 				}
// 			}
// 			//LJ Subculture MGIT
// 			else if(elem.id == 'lj_lj'){
// 				var elemMtid = document.getElementById('lj_mtid');
// 				if(elem.value == htm || elem.value == mt)
// 				{
// 					elemMtid.disabled=false;
// 				}
// 			}
// 			//RC Contamintated tubes MGIT
// 			else if(elem.id == 'rc_rc'){
// 				var elemMtid = document.getElementById('rc_mtid');
// 				if(elem.value == htm || elem.value == mt)
// 				{
// 					elemMtid.disabled=false;
// 				}
// 			}			
		}

	}
	function onChangeMtb(elem){

		if(elem.id.includes('_')){

			var theId = elem.id;
			var idArray = theId.split("_");

			if (elem.options[elem.selectedIndex].text.includes('(+)') ){

				if(idArray[0] == 'mtbXpertResult'){
				
					enable('rifXpertResult_'+idArray[1]);
	
					var elemMtb = document.getElementById('rifXpertResult_'+idArray[1]);
					if(elemMtb.options[elemMtb.selectedIndex].text == 'ERROR' || elemMtb.options[elemMtb.selectedIndex].text.includes('\u0431')){
						enable('xpertError_'+idArray[1]);
					}
					else{
						disable('xpertError_'+idArray[1]);
						document.getElementById('xpertError_'+idArray[1]).value = '';
					}
				}
				else if(idArray[0] == 'mtbHainResult'){

					enable('rifHainResult_'+idArray[1]);
					enable('inhHainResult_'+idArray[1]);
				}
				else if (idArray[0] == 'mtbHain2Result'){
					enable('moxHain2Result_'+idArray[1]);
					enable('cmHain2Result_'+idArray[1]);
					enable('erHain2Result_'+idArray[1]);
				}
				
			}
			else if (elem.options[elem.selectedIndex].text.includes('(-)')){
				if(idArray[0] == 'mtbXpertResult'){
					disable('rifXpertResult_'+idArray[1]);
					disable('xpertError_'+idArray[1]);
	
					document.getElementById('xpertError_'+idArray[1]).value = '';
					var element = document.getElementById('rifXpertResult_'+idArray[1]);
					element.value = '';
				}	
				else if(idArray[0] == 'mtbHainResult'){
					disable('rifHainResult_'+idArray[1]);
					disable('inhHainResult_'+idArray[1]);

					var element = document.getElementById('rifHainResult_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('inhHainResult_'+idArray[1]);
					element.value = '';
				}
				else if (idArray[0] == 'mtbHain2Result'){
					disable('moxHain2Result_'+idArray[1]);
					disable('cmHain2Result_'+idArray[1]);
					disable('erHain2Result_'+idArray[1]);

					var element = document.getElementById('moxHain2Result_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('cmHain2Result_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('erHain2Result_'+idArray[1]);
					element.value = '';
				}
			}
			else if (elem.options[elem.selectedIndex].text == 'ERROR' || elem.options[elem.selectedIndex].text.includes('\u0431')){
				if(idArray[0] == 'mtbXpertResult'){
					disable('rifXpertResult_'+idArray[1]);
					disable('xpertError_'+idArray[1]);
	
					var element = document.getElementById('rifXpertResult_'+idArray[1]);
					element.value = '';
				}
				else if(idArray[0] == 'mtbHainResult'){
					disable('rifHainResult_'+idArray[1]);
					disable('inhHainResult_'+idArray[1]);

					var element = document.getElementById('rifHainResult_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('inhHainResult_'+idArray[1]);
					element.value = '';
				}
				else if (idArray[0] == 'mtbHain2Result'){
					disable('moxHain2Result_'+idArray[1]);
					disable('cmHain2Result_'+idArray[1]);
					disable('erHain2Result_'+idArray[1]);

					var element = document.getElementById('moxHain2Result_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('cmHain2Result_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('erHain2Result_'+idArray[1]);
					element.value = '';
				}
			}
			else{
				if(idArray[0] == 'mtbXpertResult'){
					disable('rifXpertResult_'+idArray[1]);
					disable('xpertError_'+idArray[1]);
	
					document.getElementById('xpertError_'+idArray[1]).value = '';
					var element = document.getElementById('rifXpertResult_'+idArray[1]);
					element.value = '';
				}
				else if(idArray[0] == 'mtbHainResult'){
					disable('rifHainResult_'+idArray[1]);
					disable('inhHainResult_'+idArray[1]);

					var element = document.getElementById('rifHainResult_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('inhHainResult_'+idArray[1]);
					element.value = '';
				}

				else if (idArray[0] == 'mtbHain2Result'){
					disable('moxHain2Result_'+idArray[1]);
					disable('cmHain2Result_'+idArray[1]);
					disable('erHain2Result_'+idArray[1]);

					var element = document.getElementById('moxHain2Result_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('cmHain2Result_'+idArray[1]);
					element.value = '';

					var element = document.getElementById('erHain2Result_'+idArray[1]);
					element.value = '';
				}
			}
			
		}
		else{
			if (elem.options[elem.selectedIndex].text.includes('(+)') ){

				if(elem.id == 'mtbXpertResult'){
					enable('rifXpertResult');
	
					var elemMtb = document.getElementById('rifXpertResult');
					if(elemMtb.options[elemMtb.selectedIndex].text == 'ERROR' || elemMtb.options[elemMtb.selectedIndex].text.includes('\u0431')){
						enable('xpertError');
					}
					else{
						disable('xpertError');
						document.getElementById('xpertError').value = '';
					}
				}
				else if (elem.id == 'mtbHainResult'){
					enable('rifHainResult');
					enable('inhHainResult');
				}
				else if (elem.id == 'mtbHain2Result'){
					enable('moxHain2Result');
					enable('cmHain2Result');
					enable('erHain2Result');
				}
				
			}
			else if (elem.options[elem.selectedIndex].text.includes('(-)')){

				if(elem.id == 'mtbXpertResult'){
					disable('rifXpertResult');
					disable('xpertError');
	
					document.getElementById('xpertError').value = '';
					var element = document.getElementById('rifXpertResult');
					element.value = '';
				}
				else if (elem.id == 'mtbHainResult'){
					disable('rifHainResult');
					disable('inhHainResult');

					var element = document.getElementById('rifHainResult');
					element.value = '';

					var element = document.getElementById('inhHainResult');
					element.value = '';
				}
				else if (elem.id == 'mtbHain2Result'){
					disable('moxHain2Result');
					disable('cmHain2Result');
					disable('erHain2Result');

					var element = document.getElementById('moxHain2Result');
					element.value = '';

					var element = document.getElementById('cmHain2Result');
					element.value = '';

					var element = document.getElementById('erHain2Result');
					element.value = '';
				}
			}
			else if (elem.options[elem.selectedIndex].text == 'ERROR' || elem.options[elem.selectedIndex].text.includes('\u0431')){

				if(elem.id == 'mtbXpertResult'){
					disable('rifXpertResult');
					enable('xpertError');
	
					 var element = document.getElementById('rifXpertResult');
					 element.value = '';
				}
				else if (elem.id == 'mtbHainResult'){
					disable('rifHainResult');
					disable('inhHainResult');

					var element = document.getElementById('rifHainResult');
					element.value = '';

					var element = document.getElementById('inhHainResult');
					element.value = '';
				}
				else if (elem.id == 'mtbHain2Result'){
					disable('moxHain2Result');
					disable('cmHain2Result');
					disable('erHain2Result');

					var element = document.getElementById('moxHain2Result');
					element.value = '';

					var element = document.getElementById('cmHain2Result');
					element.value = '';

					var element = document.getElementById('erHain2Result');
					element.value = '';
				}
			}
			else{
				if(elem.id == 'mtbXpertResult'){
					disable('rifXpertResult');
					disable('xpertError');
	
					document.getElementById('xpertError').value = '';
					var element = document.getElementById('rifXpertResult');
					element.value = '';
				}
				else if (elem.id == 'mtbHainResult'){
					disable('rifHainResult');
					disable('inhHainResult');

					var element = document.getElementById('rifHainResult');
					element.value = '';

					var element = document.getElementById('inhHainResult');
					element.value = '';
				}
				else if (elem.id == 'mtbHain2Result'){
					disable('moxHain2Result');
					disable('cmHain2Result');
					disable('erHain2Result');

					var element = document.getElementById('moxHain2Result');
					element.value = '';

					var element = document.getElementById('cmHain2Result');
					element.value = '';

					var element = document.getElementById('erHain2Result');
					element.value = '';
				}
			}
		}
	}

	function onChangeRif(elem){

		if(elem.id.includes('_')){

			var theId = elem.id;
			var idArray = theId.split("_");

			if (elem.options[elem.selectedIndex].text == 'ERROR' || elem.options[elem.selectedIndex].text.includes('\u0431')){
				enable('xpertError_'+idArray[1]);
			}
			else{

				var elemRif = document.getElementById('mtbXpertResult_'+idArray[1]);

				if(elemRif.options[elemRif.selectedIndex].text == 'ERROR' || elemRif.options[elemRif.selectedIndex].text.includes('\u0431')){
					enable('xpertError_'+idArray[1]);
				}
				else{
					disable('xpertError_'+idArray[1]);
					document.getElementById('xpertError_'+idArray[1]).value = '';
				}
			}
			
		}
		else{
			if (elem.options[elem.selectedIndex].text == 'ERROR' || elem.options[elem.selectedIndex].text.includes('\u0431')){
				enable('xpertError');
			}
			else{
				var elemRif = document.getElementById('mtbXpertResult');

				if(elemRif.options[elemRif.selectedIndex].text == 'ERROR' || elemRif.options[elemRif.selectedIndex].text.includes('\u0431')){
					enable('xpertError');
				}
				else{
					disable('xpertError');
					document.getElementById('xpertError').value = '';
				}
			}
		}
	}

	function disable(elementId) {
	    document.getElementById(elementId).disabled=true;
	}
	function enable(elementId) {
	    document.getElementById(elementId).disabled=false;
	}


</script>
<script src='<%= request.getContextPath() %>/dwr/interface/LabFindlocation.js'></script>
<script type="text/javascript">
function getDistrictsInAddTest(count,type)
{
	var e = document.getElementById(type+"_oblast_"+count);
	var val = e.options[e.selectedIndex].value;
	console.log(e);
	console.log(val);
	$("#"+type+"_district_"+count).children('option').remove();
	$("#"+type+"_facility_"+count).children('option').remove();
	LabFindlocation.getDistricts(val, function(data) {
		for(var i=0; i<data.length; i++) {
			console.log(data[i])
			$("#"+type+"_district_"+count).append("<option id='"+data[i].id+"' value='"+data[i].id+"'>"+data[i].name+"</option>");
       		}
    });
}

function getFacilitiesInEditTest(count,type)
{
	var e = document.getElementById(type+"_district_"+count);
	var val2 = e.options[e.selectedIndex].value;
	console.log(e);
	console.log(val2);
	$("#"+type+"_facility_"+count).children('option').remove();
	LabFindlocation.getfacilities(val2, function(data) {
		for(var i=0; i<data.length; i++) {
			$("#"+type+"_facility_"+count).append("<option id='"+data[i].id+"' value='"+data[i].id+"'>"+data[i].name+"</option>");
       		}
	});
}

function getDistrictsInSpecimen()
{
	var e = document.getElementById("oblast");
	var val = e.options[e.selectedIndex].value;
	$("#district").children('option').remove();
	$("#facility").children('option').remove();
	LabFindlocation.getDistricts(val, function(data) {
		console.log(data)
		for(var i=0; i<data.length; i++) {
			console.log(data[i])
			$("#district").append("<option id='"+data[i].id+"' value='"+data[i].id+"'>"+data[i].name+"</option>");
       		}
    });
}

function getFacilitiesInSpecimen()
{
	var e = document.getElementById("oblast");
	var val1 = e.options[e.selectedIndex].value;
	var e = document.getElementById("district");
	var val2 = e.options[e.selectedIndex].value;
	$("#facility").children('option').remove();
	LabFindlocation.getfacilities(val2, function(data) {
		console.log(data)
		for(var i=0; i<data.length; i++) {
			$("#facility").append("<option id='"+data[i].id+"' value='"+data[i].id+"'>"+data[i].name+"</option>");
       		}
	});
}

function getDistrictsInEditTest(type)
{
	var e = document.getElementById(type+"_oblast");
	var val = e.options[e.selectedIndex].value;
	$("#"+type+"_district").children('option').remove();
	$("#"+type+"_facility").children('option').remove();
	LabFindlocation.getDistricts(val, function(data) {
		console.log(data)
		$("#"+type+"_district").append("<option value=''></option>");
       	for(var i=0; i<data.length; i++) {
			console.log(data[i])
			$("#"+type+"_district").append("<option id='"+data[i].id+"' value='"+data[i].id+"'>"+data[i].name+"</option>");
       		}
    });
}

function getFacilitiesInAddTest(type)
{
	var e = document.getElementById(type+"_oblast");
	var val1 = e.options[e.selectedIndex].value;
	var e = document.getElementById(type+"_district");
	var val2 = e.options[e.selectedIndex].value;
	$("#"+type+"_facility").children('option').remove();
	LabFindlocation.getfacilities(val2, function(data) {
		console.log(data)
		for(var i=0; i<data.length; i++) {
			$("#"+type+"_facility").append("<option id='"+data[i].id+"' value='"+data[i].id+"'>"+data[i].name+"</option>");
       		}
	});
}

jQuery( document ).ready(function() {
	/* DWREngine.setAsync(false);
	for(i=1;i<=20;i++)
    {
    	if($("#hidden_microscopy_lab_"+i)!=null)
    	{
    	var microscopy=$("#hidden_microscopy_lab_"+i).val()
        LabFindlocation.getlocation(microscopy, function(data) {
    		$("microscopy_lab_"+i).append("<p>"+data.oblastName+", "+data.districtName+", "+data.facilityName+"</p>");	
    		console.log(data.oblastName+", "+data.districtName+", "+data.facilityName)
    		console.log("microscopy_lab_"+i)
    	});
    	}
    } */
    
});
</script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
