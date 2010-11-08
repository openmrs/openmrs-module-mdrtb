<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<openmrs:globalProperty var="doseUnits" key="mdrtb.drugDoseUnits" defaultValue="g,mg,ml,tab(s)"/>

<script type="text/javascript">
	var ${id}StandardRegimens = new Array();
	<c:forEach items="${history.type.suggestions}" var="rs">
		${id}StandardRegimens['${rs.codeName}'] = {
			'displayName':'${rs.displayName}',
			'codeName':'${rs.codeName}',
			'canReplace':'${rs.canReplace}',
			'drugComponents': [
				<c:forEach items="${rs.drugComponents}" var="dc" varStatus="dcStatus">
					{
						'drugId':'${dc.drugId}',
						'dose':'${dc.dose}',
						'units':'${dc.units}',
						'frequency':'${dc.frequency}',
						'startDate':'<openmrs:formatDate date="${changeDate}" format="dd/MMM/yyyy"/>',
						'autoExpireDate':'',
						'instructions':'${dc.instructions}'
					}<c:if test="${!dcStatus.last}">,</c:if>
				</c:forEach>
			]
		};
	</c:forEach>

	var ${id}DrugOptions = '<option value=""></option><c:forEach items="${history.type.drugs}" var="d"><option value="${d.drugId}">${d.name}</option</c:forEach>';
	var ${id}UnitOptions = '<option value=""></option><c:forEach items="${doseUnits}" var="u"><option value="${u}">${u}</option</c:forEach>';
	var ${id}Index = 0;

	$j(document).ready(function() {
		$j("#${id}AddStandardRegimen").click(function() {
			var codeName = $j('#${id}standardRegimenSelector').val();
			var regToAdd = ${id}StandardRegimens[codeName];
			for (var i=0; i<regToAdd.drugComponents.length; i++) {
				addDrug('${id}NewOrderMarker', regToAdd.drugComponents[i], ${id}DrugOptions, ${id}UnitOptions, ${id}Index++);
			}
			$j('#${id}standardRegimenSelector').val('');
		});

		$j('#${id}individualDrugSelector').append(${id}DrugOptions);

		$j('#${id}AddIndividualDrug').click(function() {
			addDrug('${id}NewOrderMarker', {
						'drugId':$j('#${id}individualDrugSelector').val(), 
						'dose':'', 
						'units':'', 
						'frequency':'', 
						'instructions':'', 
						'startDate':'<openmrs:formatDate date="${changeDate}" format="dd/MMM/yyyy"/>', 
						'autoExpireDate':''
					}, ${id}DrugOptions, ${id}UnitOptions, ${id}Index++
			);
			$j('#${id}individualDrugSelector').val('')
		});

		<c:forEach items="${change.ordersStarted}" var="d" varStatus="orderStatus">
			addDrug('${id}NewOrderMarker', {
						'drugId':'${d.drug.drugId}', 
						'dose':'${d.dose}', 
						'units':'${d.units}', 
						'frequency':'${d.frequency}', 
						'instructions':'${d.instructions}', 
						'startDate':'<openmrs:formatDate date="${d.startDate}" format="dd/MMM/yyyy"/>', 
						'autoExpireDate':'<openmrs:formatDate date="${d.autoExpireDate}" format="dd/MMM/yyyy"/>'
					}, ${id}DrugOptions, ${id}UnitOptions, ${id}Index++
			);
		</c:forEach>
	});
</script>

<br/>
<form action="saveRegimen.form">
	<input type="hidden" name="patientId" value="${patientId}"/>
	<input type="hidden" name="patientProgramId" value="${patientProgramId}"/>
	<input type="hidden" name="type" value="${type}"/>
	<input type="hidden" name="startingChangeDate" value="${mdrtb:formatDateDefault(changeDate)}"/>
	<b>
		<spring:message code="mdrtb.edit" text="Edit"/>:  
		<spring:message code="mdrtb.treatment.${history.type.name}"/> 
		<spring:message code="mdrtb.changesOn" text="changes on"/> 
		<input type="text" name="changeDate" size="10" tabIndex="-1" value="<openmrs:formatDate date="${changeDate}" />" onFocus="showCalendar(this)" /><span class="datePatternHint"> (<openmrs:datePattern />)</span>
	</b>
	<br/>
	<c:if test="${!empty regimenAtStart.drugOrders}">
		<br/>
		<b class="boxHeader"><spring:message code="mdrtb.changesToExistingOrders" text="Changes to Existing Orders"/></b>
		<div class="box">

			<table>
				<tr>
					<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
					<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
					<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
					<th class="headerStyle"><spring:message code="mdrtb.action" text="Action"/></th>
					<th class="headerStyle"><spring:message code="mdrtb.reasonForStopping" text="Reason for stopping"/></th>
				</tr>
				<c:forEach items="${regimenAtStart.drugOrders}" var="drugOrder" varStatus="orderStatus">
					<c:set var="changeOrderIndex" value="${id}:${orderStatus.index}"/>
					<c:set var="isStopped" value="${mdrtb:collectionContains(change.ordersEnded, drugOrder)}"/>
					<tr>
						<td style="text-align:left; padding-left:10px; padding-right:10px; white-space:nowrap;">${drugOrder.concept.name.name}</td>
						<td class="cellStyle">
							<c:if test="${!empty drugOrder.dose}">
								${drugOrder.dose} ${drugOrder.units}
							</c:if>
						</td>
						<td class="cellStyle">
							${drugOrder.frequency}
						</td>
						<td class="cellStyle">
							<input type="radio" tabIndex="-1" onchange="hideLayer('stopReason${changeOrderIndex}');showLayer('continueReason${changeOrderIndex}');" name="action.${drugOrder.orderId}" value="continue" <c:if test="${!isStopped}">checked</c:if>> 
							<spring:message code="mdrtb.continue" text="Continue"/>
							&nbsp;&nbsp;&nbsp;
							<input type="radio" tabIndex="-1" onchange="showLayer('stopReason${changeOrderIndex}');hideLayer('continueReason${changeOrderIndex}');" name="action.${drugOrder.orderId}" value="stop" <c:if test="${isStopped}">checked</c:if>> 
							<spring:message code="mdrtb.stop" text="Stop"/>
						</td>
						<td class="cellStyle">
							<c:set var="needExtraRow" value="${!empty drugOrder.discontinuedReason}"/>
							<select name="reason.${drugOrder.orderId}" id="stopReason${changeOrderIndex}" <c:if test="${!isStopped}">style="display:none;"</c:if>>
								<option value=""></option>
								<c:forEach items="${history.type.reasonForStoppingQuestion.answers}" var="a">
									<option value="${a.answerConcept.conceptId}" <c:if test="${drugOrder.discontinuedReason.conceptId == a.answerConcept.conceptId}"><c:set var="needExtraRow" value="false"/>selected</c:if>><mdrtb:format obj="${a.answerConcept}"/></option>
								</c:forEach>
								<c:if test="${needExtraRow}">
									<option value="${drugOrder.discontinuedReason}" selected><mdrtb:format obj="${drugOrder.discontinuedReason}"/></option>
								</c:if>
							</select>
							<span id="continueReason${changeOrderIndex}" <c:if test="${isStopped}">style="display:none;"</c:if>>
								<spring:message code="mdrtb.continued" text="Continued"/>
							</span>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</c:if>
	<br/>
	<c:if test="${!empty history.type.typeQuestion}">
		<b class="boxHeader"><spring:message code="mdrtb.changesToTreatmentType" text="Changes to Treatment Type"/></b>
		<div class="box">
			<table width="100%">
				<tr>
					<th class="headerStyle"><spring:message code="mdrtb.startingTreatmentType" text="Starting Treatment Type"/>:</th>
					<td class="cellStyle" valign="top" width="100%">
						<c:choose>
							<c:when test="${empty regimenAtStart.drugOrders}">
								<spring:message code="mdrtb.noOrdersAtStart" text="Not on treatment"/>
							</c:when>
							<c:otherwise>
								<mdrtb:format obj="${regimenAtStart.reasonForStarting.valueCoded}" defaultVal="mdrtb.none"/>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<th class="headerStyle"><spring:message code="mdrtb.changneToTreatmentType" text="Change to Treatment Type"/>:</th>
					<td class="cellStyle" valign="top" width="100%">
						<spring:message code="${empty regimenAtStart.drugOrders ? 'mdrtb.unspecified' : 'mdrtb.noChange'}" var="noChangeMsg"/>
						<openmrs_tag:conceptAnswerField formFieldName="reasonForStarting" concept="${history.type.typeQuestion}" initialValue="${regimenAtEnd.reasonForStarting.valueCoded}" optionHeader="${noChangeMsg}"/>
					</td>
				</tr>
			</table>
		</div>
	</c:if>
	<br/>
	<b class="boxHeader"><spring:message code="mdrtb.newOrdersToStart" text="New Orders to Start"/></b>
	<div class="box">

		<table>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/>/<spring:message code="mdrtb.unit" text="Unit"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.startDate" text="Start Date"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.autoExpireDate" text="Planned Stop Date"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.instructions" text="Instructions"/></th>
			</tr>
			<tr id="${id}NewOrderMarker"><td colspan="6">&nbsp;</td></tr>
		</table>
	</div>
	
	<br/>
	<c:if test="${!empty history.type.suggestions}">
		<spring:message code="mdrtb.startStandardRegimen" text="Start a standard regimen:"/>
		<select id="${id}standardRegimenSelector" name="${id}standardRegimenSelector">
			<option value=""></option>
			<c:forEach items="${history.type.suggestions}" var="suggestion">
				<option value="${suggestion.codeName}">${suggestion.displayName}</option>
			</c:forEach>
		</select>
		<input type="button" id="${id}AddStandardRegimen" value="<spring:message code="mdrtb.add" text="Add"/>"/>
		<br/><b> - <spring:message code="mdrtb.or" text="or"/> - </b><br/>
	</c:if>
	<spring:message code="mdrtb.startIndividualDrugs" text="Start individual drugs:"/>
	<select id="${id}individualDrugSelector" name="${id}individualDrugSelector"></select>
	<input type="button" id="${id}AddIndividualDrug" value="<spring:message code="mdrtb.add" text="Add"/>"/>

	<br/><br/>
	<input type="submit" value="<spring:message code="mdrtb.submit" text="Submit"/>"/>
	<input type="button" value="<spring:message code="mdrtb.cancel" text="Cancel"/>" onclick="javascript:history.go(-1);"/>
</form>