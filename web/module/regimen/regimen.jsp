<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtbPortlets" uri="/WEB-INF/view/module/mdrtb/portlets/taglibs/mdrtbPortlets.tld" %>

<!-- TODO: THIS PAGE CAN BE DELETED -->

<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbOrder.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>

<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/date_input.css"%></style>

<openmrs:globalProperty key="mdrtb.dstContradicatesDrugWarningColor" var="contraDstColor"/>
<openmrs:globalProperty key="mdrtb.probableResistanceWarningColor" var="probableResistanceColor"/>
<openmrs:globalProperty key="mdrtb.enableResistanceProbabilityWarning" var="enableResistanceProbabilityWarning"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>

<!-- need to use an "include" to add this so that the javascript files can pick up the model object -->
<script type="text/javascript"> 
<%@ include file="/WEB-INF/view/module/mdrtb/resources/regimen.js"%>
</script>
<script type="text/javascript"> 
<%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtbPatientOverview.js"%>
</script>

<style>
	#regimenDiv {
		font-size: 100%;
	}
	table.widgetOut td {
		white-space:nowrap;
	}
</style> 

<div style="font-size:70%">
	<c:set var="showResistanceProbability" value="false"/>
	<c:if test="${empty obj.extra.dstResults && enableResistanceProbabilityWarning}">
		<c:set var="showResistanceProbability" value="true"/>
		<span style="font-weight:bold; color:red;">
			<spring:message code="mdrtb.probabilityOfResistance" arguments="${obj.extra.resistanceProbability}%" /><br/>
		</span>
		<table style="margin-left:20px; margin-top:5px;">
			<c:forEach items="${obj.extra.resistanceRiskFactors}" var="riskFactorEntry">
				<tr><th align="left">${riskFactorEntry.key.name}:</th><td>${riskFactorEntry.value}</td></tr>
			</c:forEach>
		</table>
	</c:if>
	
	<c:if test="${!empty obj.currentDrugOrders || !empty obj.futureDrugOrders || !empty obj.completedDrugOrders}">	
		<br/>
		<h4><spring:message code="mdrtb.regimenHistory"/></h4>
		<spring:message code="mdrtb.durationindays" var="durationindays" />
		<spring:message code="mdrtb.drug" var="drugTitle" />
		<spring:message code="mdrtb.type" var="typeTitle" />
		<spring:message code="mdrtb.standardizedShort" var="standardizedTitle" />
		<spring:message code="mdrtb.empiricShort" var="empiricTitle" />
		<spring:message code="mdrtb.individualizedShort" var="indivTitle" />
		<openmrs:globalProperty key="mdrtb.DST_drug_list" var="dstDrugList"/>

		<span style="position:relative;left:2%;">	
			<openmrs:portlet url="mdrtbCurrentRegimenType" id="mdrtbCurrentRegimenType" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>		
		</span>	
	</c:if>	
	<br/><br/>	
		
	<a href="javascript:addOrderRow();" style="font-weight:bold;">[+] <spring:message code="mdrtb.addanewdrugorder" /></a>
	<br/>
	
	<form method="post" onsubmit="javascript:return validateForm();">
		<div id="newOrderSection" style="padding-top:10px; margin:5px; border:2px dashed black; display:none;">
			<table class="regTable" id="regTableNew" style="margin:5px;">
				<tbody id="newOrdersTBody">
				</tbody>
			</table>
			<span id="submitSpan"></span><br/>
		</div>
		<br/>
		
		<c:set var="showDstDrugWarning" value="false"/>
		<c:set var="showProbableResistanceWarning" value="false"/>
		<h4><spring:message code="mdrtb.activeorders" /></h4>
		<c:choose>
			<c:when test="${empty obj.currentDrugOrders && empty obj.futureDrugOrders}">
				<div id="noCurrentOrders"><br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.nocurrentorderstodisplay" /><br/><br/></div>
			</c:when>
			<c:otherwise>
				<div id="noCurrentOrders"></div>
				<table class="regTable" id="regTableOpen">
					<tbody id="openOrdersTBody">
						<tr>
							<th><spring:message code="mdrtb.drugincaps" /></th>
							<th><spring:message code="mdrtb.doseperunits" /></th>
							<th><spring:message code="mdrtb.frequency" /></th>
							<th><spring:message code="mdrtb.startdate" /></th>
							<th><spring:message code="mdrtb.durationindays" /></th>
							<th><spring:message code="mdrtb.scheduledstopdate" /></th>
							<th><spring:message code="mdrtb.instructions" /></th>
							<th></th>
						</tr>
						<c:forEach items="${obj.currentDrugOrders}" var="order" varStatus="varStatus">				
							
							<c:set var="rowColor" value="white" />
							<c:if test="${showResistanceProbability && (order.concept.name.name == 'ISONIAZID' || order.concept.name.name == 'RIFAMPICIN')}">
								<c:set var="rowColor" value="${probableResistanceColor}"/>
								<c:set var="showProbableResistanceWarning" value="true"/>
							</c:if>
							<c:if test="${fn:contains(obj.resistanceDrugConcepts, order.concept)}">
								<c:set var="rowColor" value="${contraDstColor}"/>
								<c:set var="showDstDrugWarning" value="true"/>
							</c:if>
						
							<tr id="tr_${order.orderId}" style="background-color:${rowColor};">
								<td>${empty order.drug ? order.concept.name.name : order.drug.name}</td>
								<td>${order.dose} ${order.units}</td>
								<td>${fn:replace( fn:replace(order.frequency, "<spring:message code='mdrtb.bysevendaysperweek' />", "")," x ", " x<br/>")}</td>
								<td><openmrs:formatDate date="${order.startDate}" format="${dateFormat}" /></td>
								<td style='text-align:center'> <mdrtbPortlets:dateDiff fromDate="${order.startDate}" format="D" /></td>
								<td><openmrs:formatDate date="${order.autoExpireDate}" format="${dateFormat}" /></td>
								<td>${order.instructions}</td>
								<td>
									<p>
										<a href="#" class="simple_popup" onmouseup="javascript:setAction(${order.orderId}, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')">
											<spring:message code="mdrtb.discontinue" />
										</a>
										<span class="simple_popup_info">
											<spring:message code="mdrtb.discontinueddate" /> <br/>(${dateFormat})
											<input type="textbox" value="" onmousedown="javascript:$j(this).date_input()" onblur="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')" onChange="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')">
											<br/>
											<spring:message code="mdrtb.discontinuedreason" /> 
											<select onblur="javascript:setStopReason(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')">
												<option value=""></option>
												<c:forEach items="${discontinueReasons}" var="reason">
													<option value="${reason.conceptId}">${reason.name.name}</option>
												</c:forEach>
											</select>
										</span>
									</p>
									<p>
										<a href="#" class="simple_popup">
											<spring:message code="mdrtb.deletelowercase" />
										</a>
										<span class="simple_popup_info">
											<spring:message code="mdrtb.pleasegiveareasonfordeletingthisrecord" /><br/><br/>
											<input type="text" value="" onblur="javascript:deleteOrder(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />',this)">
										</span>
									</p>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${!empty obj.futureDrugOrders}">
							<tr>
								<th colspan="8" align="left" style="background-color:grey;">
									<spring:message code="mdrtb.futureorders"/>
								</th>
							</tr>
						</c:if>
						<c:forEach items="${obj.futureDrugOrders}" var="order" varStatus="varStatus">
							<c:set var="rowColor" value="white" />
							<c:if test="${showResistanceProbability && (order.concept.name.name == 'ISONIAZID' || order.concept.name.name == 'RIFAMPICIN')}">
								<c:set var="rowColor" value="${probableResistanceColor}"/>
								<c:set var="showProbableResistanceWarning" value="true"/>
							</c:if>
							<c:if test="${fn:contains(obj.resistanceDrugConcepts, order.concept)}">
								<c:set var="rowColor" value="${contraDstColor}"/>
								<c:set var="showDstDrugWarning" value="true"/>
							</c:if>
						
							<tr id="tr_${order.orderId}" style="background-color:${rowColor};">
								<td>${empty order.drug ? order.concept.name.name : order.drug.name}</td>
								<td>${order.dose} ${order.units}</td>
								<td>${fn:replace( fn:replace(order.frequency, "<spring:message code='mdrtb.bysevendaysperweek' />", "")," x ", " x<br/>")}</td>
								<td><openmrs:formatDate date="${order.startDate}" format="${dateFormat}" /></td>
								<td style='text-align:center'> <mdrtbPortlets:dateDiff fromDate="${order.startDate}" format="D" /></td>
								<td><openmrs:formatDate date="${order.autoExpireDate}" format="${dateFormat}" /></td>
								<td>${order.instructions}</td>
								<td>
									<p>
										<a href="#" class="simple_popup" onmouseup="javascript:setAction(${order.orderId}, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')">
											<spring:message code="mdrtb.discontinue" />
										</a>
										<span class="simple_popup_info">
											<spring:message code="mdrtb.discontinueddate" /> <br/>(${dateFormat})
											<input type="textbox" value="" onmousedown="javascript:$j(this).date_input()" onblur="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')" onChange="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')">
											<br/>
											<spring:message code="mdrtb.discontinuedreason" /> 
											<select onblur="javascript:setStopReason(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')">
												<option value=""></option>
												<c:forEach items="${discontinueReasons}" var="reason">
													<option value="${reason.conceptId}">${reason.name.name}</option>
												</c:forEach>
											</select>
										</span>
									</p>
									<p>
										<a href="#" class="simple_popup">
											<spring:message code="mdrtb.deletelowercase" />
										</a>
										<span class="simple_popup_info">
											<spring:message code="mdrtb.pleasegiveareasonfordeletingthisrecord" /><br/><br/>
											<input type="text" value="" onblur="javascript:deleteOrder(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />',this)">
										</span>
									</p>
								</td>
							</tr>
						</c:forEach>
	
					</tbody>
				</table>
				<div style="padding-top:10px;">
					<c:if test="${showDstDrugWarning}">
						&nbsp;&nbsp;&nbsp;&nbsp;<span style="background-color:${contraDstColor};">&nbsp;&nbsp;&nbsp;</span> 
						<spring:message code="mdrtb.drugContraIndicatedByDst" />
					</c:if>
					<c:if test="${showProbableResistanceWarning}">
						&nbsp;&nbsp;&nbsp;&nbsp;<span style="background-color:${probableResistanceColor};">&nbsp;&nbsp;&nbsp;</span> 
						<spring:message code="mdrtb.highProbabilityOfResistance" />
					</c:if>
				</div>
			</c:otherwise>
		</c:choose>
	
		<br/><br/>
			
		<h4><spring:message code="mdrtb.completedorders" /></h4>
		<c:choose>
			<c:when test="${empty obj.completedDrugOrders}">
				<div id="completedRegimensNoRowsMessageDiv"><br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.nocompletedordiscorderstodisplay" /><br/><br/></div>
			</c:when>
			<c:otherwise>
				<div id="completedRegimensNoRowsMessageDiv"></div>
				<table class="regTable" id="regTableClosed">
					<tbody id="closedOrdersTBody">
						<tr>
							<th><spring:message code="mdrtb.drugincaps" /></th>
							<th><spring:message code="mdrtb.doseperunits" /></th>
							<th><spring:message code="mdrtb.frequency" /></th>
							<th><spring:message code="mdrtb.startdate" /></th>
							<th><spring:message code="mdrtb.durationindays" /></th>
							<th><spring:message code="mdrtb.enddate" /></th>
							<th><spring:message code="mdrtb.reasonforclosure" /></th>
							<th></th>
						</tr>
						<c:forEach items="${obj.completedDrugOrders}" var="order" varStatus="varStatus">

							<tr id="tr_${order.orderId}" style="background-color:white;">
								<td>${empty order.drug ? order.concept.name.name : order.drug.name}</td>
								<td>${order.dose} ${order.units}</td>
								<td>${fn:replace( fn:replace(order.frequency, "<spring:message code='mdrtb.bysevendaysperweek' />", "")," x ", " x<br/>")}</td>
								<td><openmrs:formatDate date="${order.startDate}" format="${dateFormat}" /></td>
								<td>
									<c:if test="${!empty order.discontinuedDate}">
										<mdrtbPortlets:dateDiff fromDate="${order.startDate}" toDate="${order.discontinuedDate}" format="D" />
									</c:if>
									<c:if test="${empty order.discontinuedDate}">
										<c:if test="${!empty order.autoExpireDate}">
											<mdrtbPortlets:dateDiff fromDate="${order.startDate}" toDate="${order.autoExpireDate}" format="D" />
										</c:if>
									</c:if>
								</td>
								<td><c:if test="${!empty order.discontinuedDate}"><openmrs:formatDate date="${order.discontinuedDate}" format="${dateFormat}" /></c:if><c:if test="${empty order.discontinuedDate}"><openmrs:formatDate date="${order.autoExpireDate}" format="${dateFormat}" /></c:if></td>
								<td>${order.discontinuedReason.name.name}</td>
								<td><p><a href="#" class="simple_popup"><spring:message code="mdrtb.deletelowercase" /></a><span class="simple_popup_info"><spring:message code="mdrtb.pleasegiveareasonfordeletingthisrecord" /><br/><br/><input type="text" value="" onblur="javascript:deleteOrder(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />',this)"></span></p></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:otherwise>
		</c:choose>
		
		<div id="newRowTemplateDisplayDiv" style="display:none">
			<table id="newRowTemplateTable">
				<tr id="newHeaderTemplate">
					<th><spring:message code="mdrtb.drugincaps" /></th>
					<th><spring:message code="mdrtb.doseperunits" /></th>
					<th><spring:message code="mdrtb.frequency" /></th>
					<th><spring:message code="mdrtb.startdate" /></th>
					<th><spring:message code="mdrtb.durationindaysorscheduledstopdate" /></th>
					<th colspan="2"><spring:message code="mdrtb.instructions" /></th>
					<th><spring:message code="mdrtb.type" /></th>
					<th colSpan="2"></th>
				</tr>
				<tr id="newRowTemplate">
					<td>
						<select name="newDrug_" id="newDrug_" onChange="javascript:populateDrugList(this)">
						<option value=""></option>
						<c:forEach items="${standardRegimens}" var="standardReg">
							<option value="${standardReg.codeName}">${standardReg.displayName}</option>
						</c:forEach>
						<option value=""></option>
						<c:forEach items="${firstLineConcepts}" var="firstLineConcept">
							<option value="${firstLineConcept.conceptId}">${firstLineConcept.name.name} (${firstLineConcept.name.shortName})</option>
						</c:forEach>
						<option value=""></option>
						<c:forEach items="${injectibleConcepts}" var="injectibleConcept">
							<option value="${injectibleConcept.conceptId}">${injectibleConcept.name.name} (${injectibleConcept.name.shortName})</option>
						</c:forEach>
						<option value=""></option>
						<c:forEach items="${quinolonesConcepts}" var="quinoloneConcept">
							<option value="${quinoloneConcept.conceptId}">${quinoloneConcept.name.name} (${quinoloneConcept.name.shortName})</option>
						</c:forEach>
						<option value=""></option>
						<c:forEach items="${secondLineConcepts}" var="secondLineConcept">
							<option value="${secondLineConcept.conceptId}">${secondLineConcept.name.name} (${secondLineConcept.name.shortName})</option>
						</c:forEach>
						<option value=""></option>
						<c:forEach items="${otherDrugsConcepts}" var="otherDrugsConcept">
							<option value="${otherDrugsConcept.conceptId}">${otherDrugsConcept.name.name} (${otherDrugsConcept.name.shortName})</option>
						</c:forEach>
					</select>
					<select name="drugSelect_" id="drugSelect_" class="displayOff	"><option value=""><spring:message code="mdrtb.selectadrugfromtheformulary" /></option></select>
					</td>
					<td>
						<input type="text" value="" id="dose_" name="dose_" style="width:40px" autocomplete="off">/<select name="units_" id="units_"><option value=""></option>
										<c:forEach items="${drugUnits}" var="unit">
											<option value="${unit}">${unit}</option>
										</c:forEach>
										</select>
					</td>
					<td nowrap>
						<input type="text" value="" name="perDay_" id="perDay_" style="width:40px" autocomplete="off"><spring:message code="mdrtb.perday" /> <br/><select name="perWeek_" id="perWeek_" style="width:40px">
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="6">6</option>
						<option value="7" SELECTED>7</option>
						</select><spring:message code="mdrtb.perweek" />
					</td>
					<td><input type=text name="startDate_" id="startDate_" value="" style="width:80px;" onmousedown="javascript:$j(this).date_input()"></td>
					<td style='text-align:center' ><input type="text" value="" name="stopDate_" id="stopDate_" style="width:80px;" onmousedown="javascript:$j(this).date_input()"></td>
					<td colSpan='2' ><textarea name="instructions_" id="instructions_"></textarea></td>
					
					<td style="text-align:center;" >
						<select name="regimenType_" id="regimenType_">
							<option value=""></option>
							<option value="${standardized}"><spring:message code="mdrtb.standardized" /></option>
							<option value="${empiric}"><spring:message code="mdrtb.empiric" /></option>
							<option value="${individualized}"><spring:message code="mdrtb.individualized" /></option>
						</select> 
					</td>
					
					<td><a href="#" onClick="javascript:remove(this.parentNode.parentNode); return false;"><spring:message code="mdrtb.cancellowercase" /></a></td>
				</tr>
			</table>
		</div>
		<br/><br/>
		<input type="hidden" name="numberOfNewOrders" id="numberOfNewOrders" value="0">
		<input type="hidden" name="patientId" value="${obj.patient.patientId}">
		<input type="hidden" name="patientProgramId" value="${patientProgramId}"/>
	</form>

</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>
