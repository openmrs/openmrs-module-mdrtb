
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
 			if (buildJavascriptDateObject(orderDateTmp) >= buildJavascriptDateObject(stopDateTmp)){
				alert('<spring:message code="mdrtb.orderenddatebeforestopdate" />');
			} else {
	 			if (reasonTmp != null && reasonTmp != "" && stopDateTmp != null && stopDateTmp != ""){
	 				MdrtbOrder.discontinueOrder(orderIdTmp, stopDateTmp, reasonTmp ,function(ret){
								if (!ret)
									alert('<spring:message code="mdrtb.DWRunabletodiscontinueorder" />');
								else
									window.location="${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${obj.patient.patientId}";
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
			 					window.location="${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${obj.patient.patientId}";
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
	