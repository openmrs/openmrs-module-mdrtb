<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>
<%@ taglib prefix="mdrtbPortlets" uri="/WEB-INF/view/module/mdrtb/portlets/taglibs/mdrtbPortlets.tld" %>

<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbOrder.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>

<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/date_input.css"%></style>

<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${obj.patient.patientId}"/>
<openmrs:portlet url="mdrtbTabs" id="mdrtbTabs" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>

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
</style> 

<div style="font-size:70%">
		
<bR>
	<c:if test="${!empty obj.resistanceDrugConcepts}">
			<c:if test="${!empty obj.currentDrugOrders}">
				<c:set var="errorTitleShown" scope="page" value="0" /> 
					<c:forEach items="${obj.resistanceDrugConcepts}" var="resDrugs" varStatus="varStatus">
						<c:forEach items="${obj.currentDrugOrders}" var="order" varStatus="varStatusOrders">
							<c:if test="${resDrugs.conceptId == order.concept.conceptId}">
								<c:if test="${errorTitleShown == 0}">
									<Br><Br>
									<span class="dstWarning"><spring:message code="mdrtb.drugscontraindicated" />: <bR><Br>
								</c:if>
								<c:set var="errorTitleShown" scope="page" value="1" /> 
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${resDrugs.name.name} (${resDrugs.name.shortName})<Br>
							</c:if>
						</c:forEach>	
					</c:forEach>
				</span>
			</c:if>	
			<Br><Br>
		</c:if>

	<spring:message code="mdrtb.durationindays" var="durationindays" />
	<spring:message code="mdrtb.drug" var="drugTitle" />
	<spring:message code="mdrtb.type" var="typeTitle" />
	<spring:message code="mdrtb.standardizedShort" var="standardizedTitle" />
	<spring:message code="mdrtb.empiricShort" var="empiricTitle" />
	<spring:message code="mdrtb.individualizedShort" var="indivTitle" />
	<openmrs:globalProperty key="mdrtb.DST_drug_list" var="dstDrugList"/>
	<mdrtbPortlets:regimenHistory 
				typeString = "${typeTitle}"
				stString = "${standardizedTitle}"
				empString = "${empiricTitle}"
				indString = "${indivTitle}"
				standardizedId = "${standardized.conceptId}"
    			empiricId = "${empiric.conceptId}"
    			individualizedId = "${individualized.conceptId}"
				stEmpIndObs="${obj.stEmpIndObs}"
				patientId="${obj.patient.patientId}" 
				drugTitleString="${drugTitle}" 
				durationTitleString="${durationindays}" 
				drugConceptList="${dstDrugList}|PYRAZINAMIDE|AMOXICILLIN AND CLAVULANIC ACID|KANAMYCIN|LEVOFLOXACIN|RIFABUTINE|CLARITHROMYCIN|THIOACETAZONE|P-AMINOSALICYLIC ACID"
				cssClass="widgetOut"
				invert="true"
				graphicResourcePath="${pageContext.request.contextPath}/moduleResources/mdrtb/greenCheck.gif"/>	
	<Br>
	<c:if test="${!empty obj.currentDrugOrders || !empty obj.futureDrugOrders || !empty obj.completedDrugOrders}">
		<span style="position:relative;left:2%;">	
			<openmrs:portlet url="mdrtbCurrentRegimenType" id="mdrtbCurrentRegimenType" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>		
		</span>	
	</c:if>	
	<br><Br>	
		
	<span style="position:relative;left:2%;"><a href="javascript:addOrderRow();" style="font-size:120%;"><spring:message code="mdrtb.addanewdrugorder" /></a></span>
	<br><Br>
<form method="post" onsubmit="javascript:return validateForm();">

	<table class="regTable" id="regTableNew">
	<tbody id="newOrdersTBody">
	</tbody>
	</table>
			<div style="width:100%; text-align:right;"><span id="submitSpan" style="position:relative; right:10%;"></span><br></div>

	<br>
	<!-- active orders -->
	<b><spring:message code="mdrtb.activeorders" /></b>&nbsp;&nbsp;&nbsp;&nbsp;<span class="evenRowFirstLine">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.firstline" /> &nbsp;&nbsp;&nbsp;<span class="evenRowInjectible">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.injectibles" /> &nbsp;&nbsp;&nbsp; <span class="oddRowQuinolone">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.quinolones" /> &nbsp;&nbsp;&nbsp;<span class="oddRowSecondLine">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.othersecondline" />
	<br><br>
		<c:set var="addEmptyDiv" scope="page" value="1" />
		<c:if test="${empty obj.currentDrugOrders}">
				<c:if test="${empty obj.futureDrugOrders}">
					<div id="noCurrentOrders"><br><br>&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.nocurrentorderstodisplay" /><br><br></div>
					<c:set var="addEmptyDiv" scope="page" value="0" />
				</c:if>			
		</c:if>
		<c:if test="${addEmptyDiv == 1}">
			<div id="noCurrentOrders"></div>
		</c:if>
	<table class="regTable" id="regTableOpen">
		<tbody id="openOrdersTBody">
				<c:if test="${!empty obj.currentDrugOrders || !empty obj.futureDrugOrders}">
						<th><spring:message code="mdrtb.drugincaps" /></th><Th><spring:message code="mdrtb.doseperunits" /></Th><th><spring:message code="mdrtb.frequency" /></th><th><spring:message code="mdrtb.startdate" /></th><th><spring:message code="mdrtb.durationindays" /></th><th><spring:message code="mdrtb.scheduledstopdate" /></th><th><spring:message code="mdrtb.instructions" /></th>
						<th></th>
				</c:if>	

			<c:forEach items="${obj.currentDrugOrders}" var="order" varStatus="varStatus">
				<!--  set color -->
				<c:set value="5" var="colorCode"/>
				<c:forEach items="${firstLineDrugs}" var="firstLineDrug">	
					<c:if test="${ firstLineDrug.concept.name == order.concept.name || firstLineDrug.name == order.drug.name}">
						<c:set value="1" var="colorCode"/>
					</c:if>
				</c:forEach>	
				<c:forEach items="${injectibleDrugs}" var="injectibleDrug">	
					<c:if test="${injectibleDrug.concept.name.name == order.concept.name.name || injectibleDrug.name == order.drug.name}">
						<c:set value="2" var="colorCode"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${quinolones}" var="quinolone">
					<c:if test="${quinolone.concept.name.name == order.concept.name.name || quinolone.name == order.drug.name}">
						<c:set value="3" var="colorCode"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${secondLineDrugs}" var="secondLineDrug">	
					<c:if test="${secondLineDrug.concept.name.name == order.concept.name.name || secondLineDrug.name == order.drug.name}">
						<c:set value="4" var="colorCode"/>
					</c:if>
				</c:forEach>
			
				<tr id="tr_${order.orderId}">
				<c:if test="${!empty order.drug}">
					<td ><div style="display:none">${colorCode}</div>${order.drug.name}</td>
				</c:if>
				<c:if test="${empty order.drug}">
					<td ><div style="display:none">${colorCode}</div>${order.concept.name.name}</td>
				</c:if>
				<td >${order.dose} ${order.units}</td>
				<td >${fn:replace( fn:replace(order.frequency, "<spring:message code='mdrtb.bysevendaysperweek' />", "")," x ", " x<Br>")}</td>
				<td ><openmrs:formatDate date="${order.startDate}" format="${dateFormat}" /></td>
				<td style='text-align:center'> <mdrtbPortlets:dateDiff fromDate="${order.startDate}" format="D" /></td>
				<td ><openmrs:formatDate date="${order.autoExpireDate}" format="${dateFormat}" /></td>
				<td >${order.instructions}</td>
				<td><p><a href="#" class="simple_popup" onmouseup="javascript:setAction(${order.orderId}, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"><spring:message code="mdrtb.discontinue" /></a><span class="simple_popup_info"><spring:message code="mdrtb.discontinueddate" /> <Br>(${dateFormat})  <input type="textbox" value=""  onmousedown="javascript:$j(this).date_input()" onblur="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"  onChange="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"><br><spring:message code="mdrtb.discontinuedreason" /> <select onblur="javascript:setStopReason(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"><option value=""></option><c:forEach items="${discontinueReasons}" var="reason"><option value="${reason.conceptId}">${reason.name.name}</option></c:forEach></select></span></p><p><a href="#" class="simple_popup"><spring:message code="mdrtb.deletelowercase" /></a><span class="simple_popup_info"><spring:message code="mdrtb.pleasegiveareasonfordeletingthisrecord" /><br><br><input type="text" value="" onblur="javascript:deleteOrder(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />',this)"></span></p></td>
				</tr>
			</c:forEach>
			
			<c:forEach items="${obj.futureDrugOrders}" var="order" varStatus="varStatus">
				<!--  set color -->
				<c:set value="5" var="colorCode"/>
				<c:forEach items="${firstLineDrugs}" var="firstLineDrug">	
					<c:if test="${ firstLineDrug.concept.name == order.concept.name || firstLineDrug.name == order.drug.name}">
						<c:set value="1" var="colorCode"/>
					</c:if>
				</c:forEach>	
				<c:forEach items="${injectibleDrugs}" var="injectibleDrug">	
					<c:if test="${injectibleDrug.concept.name.name == order.concept.name.name || injectibleDrug.name == order.drug.name}">
						<c:set value="2" var="colorCode"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${quinolones}" var="quinolone">
					<c:if test="${quinolone.concept.name.name == order.concept.name.name || quinolone.name == order.drug.name}">
						<c:set value="3" var="colorCode"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${secondLineDrugs}" var="secondLineDrug">	
					<c:if test="${secondLineDrug.concept.name.name == order.concept.name.name || secondLineDrug.name == order.drug.name}">
						<c:set value="4" var="colorCode"/>
					</c:if>
				</c:forEach>
				
				<tr id="tr_${order.orderId}">
				<c:if test="${!empty order.drug}">
					<td ><div style="display:none">${colorCode}</div><span style="color:red">*</span>${order.drug.name}</td>
				</c:if>
				<c:if test="${empty order.drug}">
					<td ><div style="display:none">${colorCode}</div><span style="color:red">*</span>${order.concept.name.name}</td>
				</c:if>
				<td >${order.dose} ${order.units}</td>
				<td >${fn:replace( fn:replace(order.frequency, "<spring:message code='mdrtb.bysevendaysperweek' />", "")," x ", " x<Br>")}</td>
				<td ><openmrs:formatDate date="${order.startDate}" format="${dateFormat}" /></td>
				<td style='text-align:center'> <mdrtbPortlets:dateDiff fromDate="${order.startDate}" format="D" /></td>
				<td ><openmrs:formatDate date="${order.autoExpireDate}" format="${dateFormat}" /></td>
				<td >${order.instructions}</td>
				<td><p><a href="#" class="simple_popup" onmouseup="javascript:setAction(${order.orderId}, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"><spring:message code="mdrtb.discontinue" /></a><span class="simple_popup_info"><spring:message code="mdrtb.discontinueddate" /> <Br>(${dateFormat})  <input type="textbox" value=""  onmousedown="javascript:$j(this).date_input()" onblur="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"  onChange="javascript:setStopDate(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"><br><spring:message code="mdrtb.discontinuedreason" /> <select onblur="javascript:setStopReason(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />')"><option value=""></option><c:forEach items="${discontinueReasons}" var="reason"><option value="${reason.conceptId}">${reason.name.name}</option></c:forEach></select></span></p><p><a href="#" class="simple_popup"><spring:message code="mdrtb.deletelowercase" /></a><span class="simple_popup_info"><spring:message code="mdrtb.pleasegiveareasonfordeletingthisrecord" /><br><br><input type="text" value="" onblur="javascript:deleteOrder(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />',this)"></span></p></td>
				</tr>
			</c:forEach>
			
		</tbody>
	</table>
	<div>
		<c:if test="${!empty obj.futureDrugOrders}">
			<div style="display:inline;color:red;position:relative;left:20%">*<span style="color:black"> = future order</span></div>
		</c:if>
	</div>
	<br><br>
	<b><spring:message code="mdrtb.completedorders" /></b>&nbsp;&nbsp;&nbsp;&nbsp;<span class="evenRowFirstLine">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.firstline" /> &nbsp;&nbsp;&nbsp;<span class="evenRowInjectible">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.injectibles" /> &nbsp;&nbsp;&nbsp; <span class="oddRowQuinolone">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.quinolones" /> &nbsp;&nbsp;&nbsp;<span class="oddRowSecondLine">&nbsp;&nbsp;&nbsp;</span> <spring:message code="mdrtb.othersecondline" /> 
	<br><br>
	<!-- completed orders -->
	<c:if test="${empty obj.completedDrugOrders}">
		<div id="completedRegimensNoRowsMessageDiv"><br><br>&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.nocompletedordiscorderstodisplay" /><bR><Br></div>
	</c:if>
	<c:if test="${!empty obj.completedDrugOrders}">
		<div id="completedRegimensNoRowsMessageDiv"></div>
	</c:if>
	<table class="regTable" id="regTableClosed">
		<tbody id="closedOrdersTBody">
			<c:if test="${!empty obj.completedDrugOrders}">
						<th><spring:message code="mdrtb.drugincaps" /></th><Th><spring:message code="mdrtb.doseperunits" /></Th><th><spring:message code="mdrtb.frequency" /></th><th><spring:message code="mdrtb.startdate" /></th><th><spring:message code="mdrtb.durationindays" /></th><th><spring:message code="mdrtb.enddate" /></th><th><spring:message code="mdrtb.reasonforclosure" /></th>
						<th></th>
			</c:if>
				
				<c:forEach items="${obj.completedDrugOrders}" var="order" varStatus="varStatus">
						<!--  set color -->
				<c:set value="5" var="colorCode"/>
				<c:forEach items="${firstLineDrugs}" var="firstLineDrug">	
					<c:if test="${ firstLineDrug.concept.name == order.concept.name || firstLineDrug.name == order.drug.name}">
						<c:set value="1" var="colorCode"/>
					</c:if>
				</c:forEach>	
				<c:forEach items="${injectibleDrugs}" var="injectibleDrug">	
					<c:if test="${injectibleDrug.concept.name.name == order.concept.name.name || injectibleDrug.name == order.drug.name}">
						<c:set value="2" var="colorCode"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${quinolones}" var="quinolone">
					<c:if test="${quinolone.concept.name.name == order.concept.name.name || quinolone.name == order.drug.name}">
						<c:set value="3" var="colorCode"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${secondLineDrugs}" var="secondLineDrug">	
					<c:if test="${secondLineDrug.concept.name.name == order.concept.name.name || secondLineDrug.name == order.drug.name}">
						<c:set value="4" var="colorCode"/>
					</c:if>
				</c:forEach>
							<tr id="tr_${order.orderId}">
								<c:if test="${!empty order.drug}">
									<td ><div style="display:none">${colorCode}</div>${order.drug.name}</td>
								</c:if>
								<c:if test="${empty order.drug}">
									<td ><div style="display:none">${colorCode}</div>${order.concept.name.name}</td>
								</c:if>
								<td >${order.dose} ${order.units}</td>
								<td >${fn:replace( fn:replace(order.frequency, "<spring:message code='mdrtb.bysevendaysperweek' />", "")," x ", " x<Br>")}</td>
								<td ><openmrs:formatDate date="${order.startDate}" format="${dateFormat}" /></td>
								<td style='text-align:center'>
								<c:if test="${!empty order.discontinuedDate}">
									<mdrtbPortlets:dateDiff fromDate="${order.startDate}" toDate="${order.discontinuedDate}" format="D" />
								</c:if>
								<c:if test="${empty order.discontinuedDate}">
									<c:if test="${!empty order.autoExpireDate}">
									<mdrtbPortlets:dateDiff fromDate="${order.startDate}" toDate="${order.autoExpireDate}" format="D" />
									</c:if>
								</c:if>
								</td>
								<td ><c:if test="${!empty order.discontinuedDate}"><openmrs:formatDate date="${order.discontinuedDate}" format="${dateFormat}" /></c:if><c:if test="${empty order.discontinuedDate}"><openmrs:formatDate date="${order.autoExpireDate}" format="${dateFormat}" /></c:if></td>
								<td >${order.discontinuedReason.name.name}</td>	
								<td ><p><a href="#" class="simple_popup"><spring:message code="mdrtb.deletelowercase" /></a><span class="simple_popup_info"><spring:message code="mdrtb.pleasegiveareasonfordeletingthisrecord" /><br><br><input type="text" value="" onblur="javascript:deleteOrder(${order.orderId},this.value, '<openmrs:formatDate date="${order.startDate}" format="${dateFormat}" />',this)"></span></p></td>
							</tr>
				</c:forEach>
				
		</tbody>
	</table>
	<div id="newRowTemplateDisplayDiv" style="display:none">
		<table id="newRowTemplateTable">
			<tr id="newHeaderTemplate">
				<th><spring:message code="mdrtb.drugincaps" /></th>
				<Th><spring:message code="mdrtb.doseperunits" /></Th>
				<th><spring:message code="mdrtb.frequency" /></th>
				<th><spring:message code="mdrtb.startdate" /></th>
				<th><spring:message code="mdrtb.durationindaysorscheduledstopdate" /></th>
				<th colspan="2"><spring:message code="mdrtb.instructions" /></th>
				<th><spring:message code="mdrtb.type" /></th>
				<th colSpan="2"></th>
			</tr>
			<tr id="newRowTemplate">
				<td >
					<select name="newDrug_" id="newDrug_" onChange="javascript:populateDrugList(this)">
					<option value=''></option>
					<c:forEach items="${standardRegimens}" var="standardReg">
						<option value='${standardReg.codeName}'>${standardReg.displayName}</option>
					</c:forEach>
					<option value=''></option>
					<c:forEach items="${firstLineConcepts}" var="firstLineConcept">
						<option value='${firstLineConcept.conceptId}'>${firstLineConcept.name.name} (${firstLineConcept.name.shortName})</option>
					</c:forEach>
					<option value=''></option>
					<c:forEach items="${injectibleConcepts}" var="injectibleConcept">
						<option value='${injectibleConcept.conceptId}'>${injectibleConcept.name.name} (${injectibleConcept.name.shortName})</option>
					</c:forEach>
					<option value=''></option>
					<c:forEach items="${quinolonesConcepts}" var="quinoloneConcept">
						<option value='${quinoloneConcept.conceptId}'>${quinoloneConcept.name.name} (${quinoloneConcept.name.shortName})</option>
					</c:forEach>
					<option value=''></option>
					<c:forEach items="${secondLineConcepts}" var="secondLineConcept">
						<option value='${secondLineConcept.conceptId}'>${secondLineConcept.name.name} (${secondLineConcept.name.shortName})</option>
					</c:forEach>
					<option value=''></option>
					<c:forEach items="${otherDrugsConcepts}" var="otherDrugsConcept">
						<option value='${otherDrugsConcept.conceptId}'>${otherDrugsConcept.name.name} (${otherDrugsConcept.name.shortName})</option>
					</c:forEach>
				</select>
				<select name="drugSelect_" id="drugSelect_" class="displayOff	"><option value=""><spring:message code="mdrtb.selectadrugfromtheformulary" /></option></select>
				</td>
				<td >
					<input type="text" value="" id="dose_" name="dose_" style="width:40px" autocomplete="off">/<select name="units_" id="units_"><option value=""></option>
									<c:forEach items="${drugUnits}" var="unit">
										<option value="${unit}">${unit}</option>
									</c:forEach>
									</select>
				</td>
				<td nowrap>
					<input type="text" value="" name="perDay_" id="perDay_" style="width:40px" autocomplete="off"><spring:message code="mdrtb.perday" /> <bR><select name="perWeek_" id="perWeek_" style="width:40px">
					<option value='1'>1</option>
					<option value='2'>2</option>
					<option value='3'>3</option>
					<option value='4'>4</option>
					<option value='5'>5</option>
					<option value='6'>6</option>
					<option value='7' SELECTED>7</option>
					</select><spring:message code="mdrtb.perweek" />
				</td>
				<td ><input type=text name="startDate_" id="startDate_" value="" style="width:80px;" onmousedown="javascript:$j(this).date_input()"></td>
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
	<bR><Br>
	<input type="hidden" name="numberOfNewOrders" id="numberOfNewOrders" value="0">
	<input type="hidden" name="patientId" value="${obj.patient.patientId}">
</form>
</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>
