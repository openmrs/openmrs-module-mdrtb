<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="mdrtbPortlets" uri="taglibs/mdrtbPortlets.tld" %>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbOrder.js'></script>

<style>
	#regimenDiv {
		font-size: 100%;
	}
</style> 
<script type="text/javascript">  
 
 	function splitOnCommonDateFormatSepparators(input){
 		var temp = new Array();
 		if (input.indexOf('/') > -1){
 			temp =  input.split('/');
 		} else if (input.indexOf('-') > -1){
 			temp =  input.split('-');
 		} else if (input.indexOf('.') > -1){
 			temp =  input.split('.');
 		} else
 			alert("No Separator Found in System DateFormat");
 		return temp;
 	}
 
 	function buildJavascriptDateObject(inputString){
 		//ok, we're going to expect some combination of d,M,y,/,./-
 		var d,m,y;
		var temp = splitOnCommonDateFormatSepparators('${dateFormat}');
		for (var i = 0; i < temp.length; i ++){
			if (temp[i].indexOf('M') > -1)
				m = i;
			if (temp[i].indexOf('y') > -1)
				y = i;
			if (temp[i].indexOf('d') > -1)
				d = i;		
		}
		var dateArray = splitOnCommonDateFormatSepparators(inputString);
		var date = new Date();
		if (d != null)
			date.setDate(dateArray[d]);
		if (m != null)
			date.setMonth(dateArray[m] - 1);
		if (y != null){
			var year = dateArray[y];
			if (year.length == 2 && year <= 40)
				year = "20" + year;
			if (year.length == 2 && year > 40)
				year = "19" + year;
			date.setFullYear(year);
		}		
		return date;
		
 	}		
 	
 	function clearOrder(){
 		if (action == 1){ 
 			if (orderDateTmp >= stopDateTmp){
				alert('<spring:message code="mdrtb.orderenddatebeforestopdate" />');
			} else {
	 			if (reasonTmp != null && reasonTmp != "" && stopDateTmp != null && stopDateTmp != ""){
	 				MdrtbOrder.discontinueOrder(orderIdTmp, stopDateTmp, reasonTmp ,function(ret){
								if (!ret)
									alert('<spring:message code="mdrtb.DWRunabletodiscontinueorder" />');
								else
									window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${obj.patient.patientId}&view=REG&patientProgramId=${patientProgramId}";
							});
	 				
	 			} else {
	 				alert('<spring:message code="mdrtb.youmustfilloutallfieldstodiscontinue" />');
	 			}
			}
 		} else {
		 		if (reasonTmp != null && reasonTmp != ""){
		 			var trToDelete = document.getElementById("tr_"+orderIdTmp);
						MdrtbOrder.voidOrder(orderIdTmp, reasonTmp, function(ret){
							if (!ret)
								alert("<spring:message code="mdrtb.DWRnotabletovoidorder" />");
							else 
			 					window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${obj.patient.patientId}&view=REG&patientProgramId=${patientProgramId}";
						});
						
				} else {
						$j(".simple_popup_div").remove();
						alert("<spring:message code="mdrtb.youmustgiveareasontodeleteanorder" />");
				} 
		}		 
		reasonTmp = "";
 		orderIdTmp = "";
 		stopDateTmp = "";
 		action = "";
 		orderDateTmp = "";
 		showSubmit = true;
 	}
 	
 	
 	function setStopDate(orderId, date, orderDate){
 		stopDateTmp = date;
		orderIdTmp = orderId;
		orderDateTmp = orderDate;
		action = 1;
 	}
 	function setStopReason(orderId, reason, orderDate){
 		reasonTmp = reason;
		orderIdTmp = orderId;
		orderDateTmp = orderDate;
		action = 1;
 	}
 	
 	function deleteOrder(orderId, reason, orderDate, obj){
		reasonTmp = reason;
		orderIdTmp = orderId;
		orderDateTmp = orderDate;
		action = 0;
 	} 
 	function setAction(orderId, orderDate){
 		action=1;
 		orderIdTmp = orderId;
 		orderDateTmp = orderDate;
 	}
 	   
 	var reasonTmp = "";
 	var orderIdTmp = "";
 	var orderDateTmp = "";
 	var closeDateTmp = "";
 	var action = "";  
 	var newRowsToBeAdded = 0;
 	var showSubmit = true;


	function addOrderRow(){
		var hiddenNewOrderCount = document.getElementById("numberOfNewOrders");
		newRowsToBeAdded++;
		hiddenNewOrderCount.value++;
		var length = $j("#newOrdersTBody").children().length;
		if (length == 0){
			$j("#newOrdersTBody").empty();
			var clonedHeaderRow = $j("#newHeaderTemplate").clone();
			$j("#newOrdersTBody").append(clonedHeaderRow);
		}
		var clonedRow = $j("#newRowTemplate").clone();
		var tds = $j(clonedRow).children();
		for (i = 0; i < tds.length -1; i++){
			var td = tds[i];
			var tdInsides = $j(td).children();
			for (j = 0; j < tdInsides.length; j++){
				var inputItem = tdInsides[j];
				inputItem.name = inputItem.name + hiddenNewOrderCount.value;
				inputItem.id = inputItem.id + hiddenNewOrderCount.value;
			}
		}
		$j("#newOrdersTBody").append(clonedRow);
		if (newRowsToBeAdded > 0)
		$j("#submitSpan").html("<br><input type=submit name='submit' value='<spring:message code="mdrtb.saveneworders" />' id='submit'>");
		$j("#newRowTemplate:first").attr("id", "replace");	
	}

	function remove(obj){
		newRowsToBeAdded--;
		 $j(obj).remove();
	 	var length = $j("#newOrdersTBody").children().length; 
	 	if (length == 1){
	 		$j("#newOrdersTBody").empty();
	 		if (newRowsToBeAdded == 0)
	 		$j("#submitSpan").empty();
	 	}
	}
	
	function cssTables(){
		var rowsTmp = $j("#closedOrdersTBody").children();
		var rowsTwoTmp = $j("#openOrdersTBody").children();
		for (i = 1; i < rowsTmp.length; i++){
			var rowTmp = rowsTmp[i];
			var tdTmp = $j(rowTmp).children()[0];
			var divTmp = $j(tdTmp).children()[0];
			var cssType = divTmp.innerHTML;
			if (i%2 == 0){
				if (cssType == 1)
				$j(rowTmp).addClass('oddRowFirstLine');
				if (cssType == 2)
				$j(rowTmp).addClass('oddRowInjectible');
				if (cssType == 3)
				$j(rowTmp).addClass('oddRowQuinolone');
				if (cssType == 4)
				$j(rowTmp).addClass('oddRowSecondLine');
				if (cssType == 5)
				$j(rowTmp).addClass('oddRowTwo');
			} else {
				if (cssType == 1)
				$j(rowTmp).addClass('evenRowFirstLine');
				if (cssType == 2)
				$j(rowTmp).addClass('evenRowInjectible');
				if (cssType == 3)
				$j(rowTmp).addClass('evenRowQuinolone');
				if (cssType == 4)
				$j(rowTmp).addClass('evenRowSecondLine');
				if (cssType == 5)
				$j(rowTmp).addClass('evenRowTwo');	
			}				
		} 
		for (i = 1; i < rowsTwoTmp.length; i++){
			var rowTmp = rowsTwoTmp[i];
			var tdTmp = $j(rowTmp).children()[0];
			var divTmp = $j(tdTmp).children()[0];
			var cssType = divTmp.innerHTML;
			if (i%2 == 0) {
				if (cssType == 1)
				$j(rowTmp).addClass('oddRowFirstLine');
				if (cssType == 2)
				$j(rowTmp).addClass('oddRowInjectible');
				if (cssType == 3)
				$j(rowTmp).addClass('oddRowQuinolone');
				if (cssType == 4)
				$j(rowTmp).addClass('oddRowSecondLine');
				if (cssType == 5)
				$j(rowTmp).addClass('oddRowTwo');
			} else {
				if (cssType == 1)
				$j(rowTmp).addClass('evenRowFirstLine');
				if (cssType == 2)
				$j(rowTmp).addClass('evenRowInjectible');
				if (cssType == 3)
				$j(rowTmp).addClass('evenRowQuinolone');
				if (cssType == 4)
				$j(rowTmp).addClass('evenRowSecondLine');
				if (cssType == 5)
				$j(rowTmp).addClass('evenRowTwo');	
			}				
		} 
	}
	
	
	//TODO: validate
	function validateForm(){
		var newRows = $j("#newOrdersTBody").children();
		if (newRows.length > 1){
			for (var i = 1; i < newRows.length; i++){
				var rowToValidate = $j(newRows)[i];
				var tdsToValidate = $j(rowToValidate).children();
				var isStandardRegimen = false;
				var startDate = null;
				var discontinueDate = null;
				for (var j = 0; j < tdsToValidate.length-1; j++){
					var tdToValidate = tdsToValidate[j];
					var controls = $j(tdToValidate).children();
					
					for (var k = 0; k < controls.length; k++){
						var control = controls[k];
						if (k == 0 && control.id.indexOf("newDrug") > -1){
							if (isNumeric(control.value) == false)
								isStandardRegimen = true;
						}
						if (control.name.indexOf("stopDate_") >= 0)
							discontinueDate = control.value;
						if 	(control.name.indexOf("startDate_") >= 0)
							startDate = control.value;
						if (isStandardRegimen == false){
							if (control.type != "checkbox" && control.type != "textarea" && control.name.indexOf("stopDate_") < 0 && control.name.indexOf("regimenType_") < 0 && control.name.indexOf("drugSelect_") < 0){
							 if (control.value == ""){
							 	alert("<spring:message code="mdrtb.youcantsaveadrugorderunless" />");
								return false;
							 }	
							}
						
						} else {
							if (control.type != "checkbox" && control.type != "textarea" && control.name.indexOf("stopDate_") < 0 && control.name.indexOf("regimenType_") < 0 && control.name.indexOf("drugSelect_") < 0 && control.name.indexOf("dose_") < 0 && control.name.indexOf("units_") < 0 && control.name.indexOf("perDay_") < 0 && control.name.indexOf("perWeek_") < 0){
							 if (control.value == ""){
							 	alert("<spring:message code="mdrtb.youcantsaveadrugorderunlessSR" />");
								return false;
							 }	
							}
						}
							
					}
				}
				if (startDate != null && discontinueDate != null && discontinueDate != ""){
						var startDateDateObj = buildJavascriptDateObject(startDate);
						var endDateDateObj = buildJavascriptDateObject(discontinueDate);
						if (startDateDateObj.getTime() >= endDateDateObj.getTime()){
							alert("<spring:message code="mdrtb.startDateLaterThanEndDate" />");
							return false;
						}
				}
			}
		}

				//TODO: STANDARDIZED EMPIRIC VALIDATION
		var dateList = "";
		var hiddenRowCount = document.getElementById("numberOfNewOrders"); 
		for (var i = 0; i <= hiddenRowCount.value; i++){
			var startDateId = "startDate_" + i;
			var regimenTypeId = "regimenType_" + i;
			var startDateField = document.getElementById(startDateId);
			var regTypeField = document.getElementById(regimenTypeId);
			if (startDateField != null && regTypeField != null && regTypeField.value != "") {
				if (dateList.indexOf(startDateField.value) >= 0 && dateList.indexOf(startDateField.value + regTypeField.value) == -1){
					alert("<spring:message code='mdrtb.contradictoryStEmpInd' />");
					return false;
				} else {
					dateList += "~" + startDateField.value + regTypeField.value;
				}	
			}
		}
		
		return true;
	}
	
	function isNumeric(inputVal){
		var nums ="0123456789";
		for (i = 0; i < inputVal.length; i++){
	      strChar = inputVal.charAt(i);
	      if (nums.indexOf(strChar) == -1){
	        	return false;
	      }
	    }
		return true;
	}
	
	function populateDrugList(item){
		var sibling = $j(item).next()[0];
		if (isNumeric(item.value)){
			enableNewDrugFields(item);
			for (i = 0; i < sibling.options.length; i++) {
		   			 sibling.options[i] = null;
				}
			sibling.length=0;	
				
			if (item.value != ""){
				sibling.options[sibling.length] = new Option("Generic Order", "");
				for (i = 0; i < allDrugs.length; i++){
					var arrayEntry = allDrugs[i];
					if (arrayEntry[0] == item.value){
					sibling.options[sibling.length] = new Option(arrayEntry[2], arrayEntry[1]);
					}
				}
				$j(sibling).addClass('displayOn');
			}
			if (item.value == ""){
				$j(sibling).removeClass('displayOn');
				$j(sibling).addClass('displayOff');	
			}
		} else {
				
				$j(sibling).removeClass('displayOn');
				$j(sibling).addClass('displayOff');
				disableNewDrugFields(item);
		}
	}
	
	function getRowIdFromInputName(item){
		var pos = item.name.indexOf("_");
		var posInt = item.name.substring(pos + 1);
		return posInt;
	}
	
	function disableNewDrugFields(item){
		var rowNum = getRowIdFromInputName(item);
		var dose = document.getElementById("dose_" + rowNum);
		var units = document.getElementById("units_" + rowNum);
		var perDay = document.getElementById("perDay_" + rowNum);
		var perWeek = document.getElementById("perWeek_" + rowNum);
		var instructions = document.getElementById("instructions_" + rowNum);
		var regimenType = document.getElementById("regimenType_" + rowNum);
		$j(rowNum).attr("disabled", "disabled"); 
		$j(rowNum).val("");
		$j(dose).attr("disabled", "disabled"); 	
		$j(dose).val("");
		$j(units).attr("disabled", "disabled"); 
		$j(units).val("");
		$j(perDay).attr("disabled", "disabled"); 
		$j(perDay).val("");
		$j(perWeek).attr("disabled", "disabled"); 
		$j(instructions).attr("disabled", "disabled");
		$j(instructions).val("");
		$j(regimenType).attr("disabled", "disabled"); 
		$j(regimenType).val("");
	}
	
	function enableNewDrugFields(item){
		var rowNum = getRowIdFromInputName(item);
		var dose = document.getElementById("dose_" + rowNum);
		var units = document.getElementById("units_" + rowNum);
		var perDay = document.getElementById("perDay_" + rowNum);
		var perWeek = document.getElementById("perWeek_" + rowNum);
		var instructions = document.getElementById("instructions_" + rowNum);
		var regimenType = document.getElementById("regimenType_" + rowNum);
		$j(rowNum).removeAttr("disabled");  
		$j(dose).removeAttr("disabled"); 		
		$j(units).removeAttr("disabled");  
		$j(perDay).removeAttr("disabled");  
		$j(perWeek).removeAttr("disabled");  
		$j(instructions).removeAttr("disabled");  
		$j(regimenType).removeAttr("disabled");  
	}
	
	
	//SETUP DRUG ARRAYS each item is  [conceptId, drugId, drugName]
	var allDrugs = new Array();
	<c:set var="drugCount" scope="page" value="0" />
	<c:forEach items="${firstLineDrugs}" var="firstLineDrug" varStatus="varStatus">
			allDrugs[${varStatus.index}] = new Array("${firstLineDrug.concept.conceptId}","${firstLineDrug.drugId}","${firstLineDrug.name}");
			<c:set var="drugCount" scope="page" value="${drugCount + 1}" />
	</c:forEach>
	<c:forEach items="${injectibleDrugs}" var="injectibleDrug" varStatus="varStatus">
			allDrugs[${drugCount}] = new Array("${injectibleDrug.concept.conceptId}","${injectibleDrug.drugId}","${injectibleDrug.name}");
			<c:set var="drugCount" scope="page" value="${drugCount + 1}" />
	</c:forEach>
	<c:forEach items="${quinolones}" var="quinolone" varStatus="varStatus">
			allDrugs[${drugCount}] = new Array("${quinolone.concept.conceptId}","${quinolone.drugId}","${quinolone.name}");
			<c:set var="drugCount" scope="page" value="${drugCount + 1}" />
	</c:forEach>
	<c:forEach items="${secondLineDrugs}" var="secondLineDrug" varStatus="varStatus">
			allDrugs[${drugCount}] = new Array("${secondLineDrug.concept.conceptId}","${secondLineDrug.drugId}","${secondLineDrug.name}");
			<c:set var="drugCount" scope="page" value="${drugCount + 1}" />
	</c:forEach>
	<c:forEach items="${otherDrugs}" var="otherDrug" varStatus="varStatus">
			allDrugs[${drugCount}] = new Array("${otherDrug.concept.conceptId}","${otherDrug.drugId}","${otherDrug.name}");
			<c:set var="drugCount" scope="page" value="${drugCount + 1}" />
	</c:forEach>
	
</script>
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

