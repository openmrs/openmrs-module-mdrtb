<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:globalProperty key="mdrtb.max_num_bacteriologies_or_dsts_to_add_at_once" var="maxTests"/>
<script type="text/javascript">
	
	var currentTest = 0;
	var maxTests = ${maxTests} - 1;

 function testValue(inp){
		var parent = inp.parentNode.parentNode;
		var typeOther = $(parent).next()[0];
		var typeOtherChildren = $(typeOther).children()[0];
		var div = $(typeOtherChildren).children()[0];
			if (inp.value == "${OtherNonCodedId}"){
				$(div).removeClass("displayOff");
				$(div).addClass("displayOn");
			}	
			else {
				$(div).removeClass("displayOn");
				$(div).addClass("displayOff");
				var divChildren = $(div).children();
				for (i = 0; i < divChildren.length; i++){
					var divChild = divChildren[i];
					if (divChild.type == "text"){
						divChild.value="";
					}
				}
			}	
	}


	
	function addAnother(){
		currentTest++;
		var dst = "contentDiv_";
		var dstString = dst + currentTest;
		var dstDiv = document.getElementById(dstString);
		$(dstDiv).removeClass();
		$(dstDiv).addClass("displayOn");
		resetLinks();
	}
	
	function removeRow(){
		var dst = "contentDiv_";
		var dstString = dst + currentTest;
		var dstDiv = document.getElementById(dstString);
		$(dstDiv).removeClass();
		$(dstDiv).addClass("displayOff");
		currentTest --;
		resetLinks();
	}
	
	function resetLinks(){
				if (currentTest == maxTests){
			var addAnother = document.getElementById("addAnother");
			$(addAnother).removeClass();
			$(addAnother).addClass("displayOff");
		} else {
			var addAnother = document.getElementById("addAnother");
			$(addAnother).removeClass();
			$(addAnother).addClass("displayOn");
		}
		if (currentTest > 0){
			var addAnother = document.getElementById("removeRow");
			$(addAnother).removeClass();
			$(addAnother).addClass("displayOn");
		} else {
			var addAnother = document.getElementById("removeRow");
			$(addAnother).removeClass();
			$(addAnother).addClass("displayOff");
		}
		var numRowsShown = document.getElementById("numRowsShown");
		numRowsShown.value = currentTest + 1;
	}
	
	$(document).ready(function(){
  		resetColorCoding();
 	});
    
    function resetColorCoding(){
    	var selects = $(".selects");
    	for (i = 0; i < selects.length; i ++){
	    	var select = selects[i];
	    	var selectedIndex = select.selectedIndex;
	    	var option = select.options[selectedIndex];
	    	if (option.text == "${yellow}"){
	    		var tdTmp = $(select).parent();
	    		var td = tdTmp[0];
	    		var tdTwoTmp = $(td).prev("td");
	    		var tdTwo = tdTwoTmp[0];
	    		var spanList = $(tdTwo).children();
				var span=spanList[0];
				$(span).removeClass();
				$(span).addClass("widgetYellow");
	    	}
	    	 else if (option.text == "${red}"){
	    		var tdTmp = $(select).parent();
	    		var td = tdTmp[0];
	    		var tdTwoTmp = $(td).prev("td");
	    		var tdTwo = tdTwoTmp[0];
	    		var spanList = $(tdTwo).children();
				var span=spanList[0];
				$(span).removeClass();
				$(span).addClass("widgetRed");
	    	}
	    	else if (option.text == "${green}"){
	    		var tdTmp = $(select).parent();
	    		var td = tdTmp[0];
	    		var tdTwoTmp = $(td).prev("td");
	    		var tdTwo = tdTwoTmp[0];
	    		var spanList = $(tdTwo).children();
				var span=spanList[0];
				$(span).removeClass();
				$(span).addClass("widgetGreen");
	    	}
	    	else {
	    		var tdTmp = $(select).parent();
	    		var td = tdTmp[0];
	    		var tdTwoTmp = $(td).prev("td");
	    		var tdTwo = tdTwoTmp[0];
	    		var spanList = $(tdTwo).children();
				var span=spanList[0];
				$(span).removeClass();
	    	}	
    	}
    }
    
</script>
<div style="font-size:68%">
	<spring:message code="mdrtb.dstresults" /><br>
	<br>
	<table class="portletTable">
		<Tr><th class="patientTableHeader">&nbsp;</th><th class="patientTableHeader"></th></Tr>
		<tr>
			<td><spring:message code="mdrtb.sputumcollectionencounter" /></td>
			<td>
					<select name="encSelect" onchange="javascript:setEncounterDate(this)">
						<option value=""><spring:message code="mdrtb.none" /></option>
						<c:forEach items="${obj.encounters}" var="encounter">
							<option value="${encounter.encounterId}|<openmrs:formatDate date="${encounter.encounterDatetime}" format="${dateFormat}"/>">
								${encounter.encounterType.name}&nbsp;-&nbsp;
								<openmrs:formatDate date="${encounter.encounterDatetime}" format="${dateFormat}"/>&nbsp;
								${encounter.location}&nbsp; ${encounter.provider}
							</option>
						</c:forEach>	
					</select>
			</td>
		</tr>
	</table> 
	<bR><br>
	
	<!-- DST container table -->
	<Br>&nbsp;&nbsp;&nbsp;<a href="#" onmousedown="javascript:addAnother()" id="addAnother"><spring:message code="mdrtb.addanother" /></a><Br><br>
	<table>
		<c:forEach var="rowCount" begin="0" end="${maxTests-1}" step="1">
			<tr>
			<td><div id="contentDiv_${rowCount}" 
				<c:if test="${rowCount == 0}">
					class="displayOn";
				</c:if>
				<c:if test="${rowCount > 0}">
					class="displayOff";
				</c:if>
				>
				<table class="portletTable">	
		<tr><td colSpan='2' valign='top'>
		<!--  left column -->	
			<table>
			<tr>
				<th colSpan='2' style="text-align:left" class="patientTableHeader">&nbsp;&nbsp;<spring:message code="mdrtb.dst" /></th>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.samplecollectiondate" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].sputumCollectionDate.valueDatetime">
					<input type="text" style="width:100px" value="${status.value}" id="sputumCollectionDate_${rowCount}" name="${status.expression}" onMouseDown="$(this).date_input()" class="sputumCollection">&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.labtestorderedby" /></td>
				<td>
					<select name="dst_provider_${rowCount}" id="dst_provider_${rowCount}" class="labTestOrderedBy">
						<option value=""></option>
						<c:forEach items="${providers}" var="provider">
							<option value="${provider.userId}">${provider.givenName} ${provider.familyName}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.anatomicalsite" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].source.valueCoded">
					<select name="${status.expression}" id="anatSiteCulture_${rowCount}">
							<option value=""><spring:message code="mdrtb.pleasechooseanatomicalsite" /></option>
							<c:forEach items="${anatSites}" var="anatSite">
								<option value="${anatSite}" 
									<c:if test="${anatSite == status.value || anatSite.name.name == 'SPUTUM'}">
										SELECTED
									</c:if>
								>${anatSite.name.name}</option>
							</c:forEach>
					</select>
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.sampleid" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].sputumCollectionDate.accessionNumber">
						<input type='text' value='${status.value}' name='${status.expression}' id='sampleIdSmear_${rowCount}'>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>		
			</tr> 
			<tr>
				<td><spring:message code="mdrtb.directorindirect" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].directOrIndirect.valueNumeric">
					<select name="${status.expression}">
					<option value=""></option>
					<option value="1" <c:if test="${status.value == '1'}">SELECTED</c:if>
									><spring:message code="mdrtb.direct" /></option>
									<option value="0" <c:if test="${status.value == '0' || status.value == ''}">SELECTED</c:if>
									><spring:message code="mdrtb.indirect" /></option>
							
					</select>
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>				
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.method" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].dstMethod.valueCoded">
						<select name="${status.expression}" id="dstCulture_${rowCount}">
							<option value=""><spring:message code="mdrtb.pleasechooseamethod" /></option>
							<c:forEach items="${dstMethods}" var="method">
								<option value="${method}"
									<c:if test="${method == status.value}">
										SELECTED
									</c:if>
								>${method.name.name}</option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.coloniesincontrol" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].coloniesInControl.valueNumeric">
					<input type="text" style="width:50px" value="${status.value}" id="coloniesIncontrol_${rowCount}" name="${status.expression}" >
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<Tr>
				<td><spring:message code="mdrtb.samplereceived" /></td>
				<td>
				<spring:bind path="obj.dsts[${rowCount}].dstDateReceived.valueDatetime">
				<input type="text" style="width:100px" name="${status.expression}" id="dstDateReceived_${rowCount}" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
				<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>				
			</Tr>
			<tr>
				<td><spring:message code="mdrtb.dststart" /></td>
				<td>
				<spring:bind path="obj.dsts[${rowCount}].dstStartDate.valueDatetime">
					<input type="text" name="${status.expression}" id="dstStartDate_${rowCount}"  style="width:100px" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
				</td>
			</tr>	
			<tr>
				<td><spring:message code="mdrtb.dstresultdate" /></td>
				<td>
				<spring:bind path="obj.dsts[${rowCount}].dstResultsDate.valueDatetime">
					<input type="text" style="width:100px" name="${status.expression}" id="resTestCompleteCultureDate_${rowCount}" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
				</td>
			</tr>
			<Tr>
				<td><spring:message code="mdrtb.typeoforganism" /></td>
				<Td>
					<spring:bind path="obj.dsts[${rowCount}].typeOfOrganism.valueCoded">
						<select name="${status.expression}" id="typeOfOrganism_${rowCount}" onchange="javascript:testValue(this)">
							<option value=""><spring:message code="mdrtb.pleasechooseatype" /></option>
							<c:forEach items="${organismTypes}" var="organismType">
								<option value="${organismType}"
									<c:if test="${organismType == status.value}">
										SELECTED
									</c:if>
								>${organismType.name.name}</option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</Td>
			</Tr>
			<Tr><td colSpan="2">
				
				<div class="displayOff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="mdrtb.othertype" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

					<spring:bind path="obj.dsts[${rowCount}].typeOfOrganismNonCoded.valueText">
					<input type="text" style="width:100px" name="${status.expression}" id="otherType_${rowCount}" value="${status.value}" >
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
				</div>
				</td>
			</Tr>
			<tr>
				<td><spring:message code="mdrtb.lab" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].dstParentObs.location">
					<select name="${status.expression}" id="labDST_${rowCount}">
						<option value=""><spring:message code="mdrtb.pleasechoosealab" /></option>
						<c:forEach items="${locations}" var="lab">
									<option value="${lab.locationId}"
										<c:if test="${lab == status.value}">
											SELECTED
										</c:if>
									>${lab}</option>
								</c:forEach>
					</select>
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
					</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.complete" /></td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].drugSensitivityTestComplete.valueNumeric">
					<select name="${status.expression}">
					<option value="1" <c:if test="${status.value == '1' || status.value == ''}">SELECTED</c:if>
									><spring:message code="mdrtb.yes" /></option>
									<option value="0" <c:if test="${status.value == '0'}">SELECTED</c:if>
									><spring:message code="mdrtb.no" /></option>
							
					</select>
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>	
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.comment" /></td>
				<spring:bind path="obj.dsts[${rowCount}].sputumCollectionDate.comment">
				<td><textarea name='${status.expression}' id='commentSmear_${rowCount}'>${status.value}</textarea></td>
				<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</tr>
			</table>
		</td>
		<!-- left column -->
		<td colSpan='2' valign='top'>
			<table>
			<th class="patientTableHeader"><spring:message code="mdrtb.drug" /></th><th class="patientTableHeader"><spring:message code="mdrtb.result" /></th><th class="patientTableHeader"><spring:message code="mdrtb.concentration" /><br>&nbsp;<spring:message code="mdrtb.mcg/ml" /></th><th class="patientTableHeader"><spring:message code="mdrtb.colonies" /></th>
			<c:forEach items="${obj.dsts[0].dstResults}" var="command" varStatus="counter">
				<tr>
				<Td nowrap><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;
				${command.drug.valueCoded.name.name}</Td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].dstResults[${counter.index}].drug.concept">
					<select name="${status.expression}" class="selects" onchange="javascript:resetColorCoding()">
					<option value=""><spring:message code="mdrtb.pleasechoosearesult" /></option>
							<c:forEach items="${dstResults}" var="dstresult">					
									<option value="${dstresult}"
										<c:if test="${dstresult == status.value}">
											SELECTED
										</c:if>
									>${dstresult.name.name}</option>
							</c:forEach>	
					</select>
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>				
					</spring:bind>
				</td>
				<td align='center'>
					<spring:bind path="obj.dsts[${rowCount}].dstResults[${counter.index}].concentration.valueNumeric">
					<input type="text" value="${status.value}" name="${status.expression}" id="concentration_${counter.index}" style="width:50px">
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>	
				<td align='center'>
					<spring:bind path="obj.dsts[${rowCount}].dstResults[${counter.index}].colonies.valueNumeric">
					<input type="text" value="${status.value}" name="${status.expression}" id="colonies_${counter.index}" style="width:50px">
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
				</tr>
			</c:forEach>
			</table>	
			</td></tr>
	</table>
	
	
			</div></Td>
			</tr>
		</c:forEach>
	</table>

	&nbsp;&nbsp;&nbsp;<a href="#" onmousedown="javascript:addAnother()" id="addAnother"><spring:message code="mdrtb.addanother" /></a>
	&nbsp;&nbsp;&nbsp;<a href="#" onmousedown="javascript:removeRow()" id="removeRow" class="displayOff"><spring:message code="mdrtb.remove"/></a>
	
	<Br>
	<br>
	<c:if test="${!empty obj}">
			<input type="hidden" value="${obj}" name="patientId" id="patientId">
			<input type="hidden" value="1" name="numRowsShown" id="numRowsShown">
	</c:if>	
	<input type='submit' name='submit' value='<spring:message code="mdrtb.savedst" />' id='submit'> &nbsp;&nbsp;&nbsp;<input type="submit" value='<spring:message code="mdrtb.cancel" />' id="cancel">
</div>

