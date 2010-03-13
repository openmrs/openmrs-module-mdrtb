<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script>
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
    var encounterProviderMap = {<c:forEach items="${encounters}" var="encounter" varStatus="varStatus">${encounter.encounterId}:"${encounter.provider.userId}"<c:if test="${!varStatus.last}">,</c:if></c:forEach>};
    

	$(document).ready(function(){
  		resetColorCoding();
  		var typeOfOrganisms = $(".typeOfOrganism");
  		for (i = 0; i < typeOfOrganisms.length; i++){
	  		var inp = typeOfOrganisms[i];
	  		testValue(inp);
  		}
  		var providerInputs = getElementsByClassName(document, "select", "labTestOrderedBy");
			for (i=0; i<providerInputs.length; i++){
				providerInputs[i].value = encounterProviderMap[<c:if test="${fn:length(obj.smears) > 0}">${obj.smears[0].smearParentObs.encounter.encounterId}</c:if><c:if test="${fn:length(obj.cultures) > 0}">${obj.cultures[0].cultureParentObs.encounter.encounterId}</c:if><c:if test="${fn:length(obj.dsts) > 0}">${obj.dsts[0].dstParentObs.encounter.encounterId}</c:if>];
			}
 	});
    
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
   function setEncounterDate(inp){
		var test = inp.value;
		var pipe = test.indexOf("|");
		var length = test.length;
		var dateString = test.substr(pipe+1,length-pipe);
		var encounterId = test.substr(0,pipe);
		var inputs = getElementsByClassName(document, "input", "sputumCollection");
		for (i=0; i<inputs.length; i++){
			inputs[i].value = dateString;
		}
		var providerInputs = getElementsByClassName(document, "select", "labTestOrderedBy");
		for (i=0; i<providerInputs.length; i++){
			providerInputs[i].value = encounterProviderMap[encounterId];
		}
	}
	
	function getElementsByClassName(oElm, strTagName, strClassName){
			var arrElements = (strTagName == "*" && document.all)? document.all : oElm.getElementsByTagName(strTagName);
		    var arrReturnElements = new Array();
		    strClassName = strClassName.replace(/\-/g, "\\-");
		    var oRegExp = new RegExp("(^|\\s)" + strClassName + "(\\s|$)");
		    var oElement;
		    for(var i=0; i<arrElements.length; i++){
		        oElement = arrElements[i];
		        if(oRegExp.test(oElement.className)){
		            arrReturnElements.push(oElement);
		        }
		    }
		    return (arrReturnElements)
		}
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
    
    var scantyId = ${scantyId};
    function showTrIfScanty(){
    	
   		var smearResultRootString = "smearResult_";
   		var smearBacilliRootString = "numBacilliDiv_";
   		var cultureResultRootString = "cultureResults_";
   		var cultureColoniesRootString = "numColoniesDiv_";	
    	var i = 0;	
    	
    	while (i < 1){
    		var result = document.getElementById(smearResultRootString+i);
    		if (result != null && result != "" && result != "undefined"){
    			var trSmear = document.getElementById(smearBacilliRootString+i);
    			if (result.value == scantyId)
    				$(trSmear).attr("style", "");
    			else {
    				$(trSmear).attr("style", "display:none;");
    				//var bacilliVal = document.getElementById("bacilliResult_" + i);
    				//$(bacilliVal).val("");
    			}	
    			
    		}
    		
    		
    		var result = document.getElementById(cultureResultRootString+i);
    		if (result != null && result != "" && result != "undefined"){
    			var trCulture = document.getElementById(cultureColoniesRootString+i);
    			if (result.value == scantyId)
    				$(trCulture).attr("style", "");
    			else {
    				$(trCulture).attr("style", "display:none;");
    				//var coloniesVal = document.getElementById("coloniesResult_" + i);
    				//$(coloniesVal).val("");
    			}	
    		}
    		i++;	
    	}
    }
</script>
<style><%@ include file="resources/mdrtb.css"%></style>
<div class="absolute">
<h2><spring:message code="mdrtb.title" /></h2>
</div>
<form method="post"> 
<c:set var="rowCount" scope="page" value="0" />
<div id="content" style="font-size:80%">

			<br>
	<table class="portletTable">
		<Tr><th class="patientTableHeader">&nbsp;</th><th class="patientTableHeader"></th></Tr>
		<tr>
			<td><spring:message code="mdrtb.sputumcollectionencounter" /></td>
			<td>
					<select name="encSelect" id="encSelect" onchange="javascript:setEncounterDate(this)">
						<option value=""><spring:message code="mdrtb.none" /></option>
						<c:forEach items="${encounters}" var="encounter">
							<option value="${encounter.encounterId}|<openmrs:formatDate date="${encounter.encounterDatetime}" format="${dateFormat}"/>" 
								<c:if test="${fn:length(obj.smears) > 0}">
								<c:if test="${obj.smears[rowCount].smearParentObs.encounter.encounterId == encounter.encounterId}">
								SELECTED
								</c:if>
								</c:if>
								<c:if test="${fn:length(obj.cultures) > 0}">
								<c:if test="${obj.cultures[rowCount].cultureParentObs.encounter.encounterId == encounter.encounterId}">
								SELECTED
								</c:if>
								</c:if>
								<c:if test="${fn:length(obj.dsts) > 0}">
								<c:if test="${obj.dsts[rowCount].dstParentObs.encounter.encounterId == encounter.encounterId}">
								SELECTED
								</c:if>
								</c:if>
							
							><openmrs:formatDate date="${encounter.encounterDatetime}" format="${dateFormat}"/>&nbsp; ${encounter.location}&nbsp; ${encounter.provider}</option>
							
						</c:forEach>	
					</select>
			</td>
		</tr>
	</table> 
	<bR><br>
		<table class="portletTable">
				<c:if test="${fn:length(obj.smears) > 0}">
						<Tr><td>
							<table>
			<tr>
				<th colSpan='2' style="text-align:left" class="patientTableHeader">&nbsp;&nbsp;<spring:message code="mdrtb.smear" /></th>
			</tr>
			
			<tr>
				<td><spring:message code="mdrtb.samplecollectiondate" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearResult.obsDatetime">
						<input type="text" style="width:100px" value="${status.value}" id="sputumCollectionDateSmear" name="${status.expression}"  onMouseDown="$(this).date_input()" class="sputumCollection"/>&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.labtestorderedby" /></td>
				<td>
					<select name="provider_${rowCount}" id="provider_${rowCount}" class="labTestOrderedBy">
						<option value=""></option>
						<c:forEach items="${providers}" var="provider">
							<option value="${provider.userId}">${provider.givenName} ${provider.familyName}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.smearresults" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearResult.valueCoded">
						<select name="${status.expression}" id="smearResult_${rowCount}" onChange="javascript:showTrIfScanty();">
							<option value=""><spring:message code="mdrtb.pleasechoosearesult" /></option>
							<c:forEach items="${smearResults}" var="smearResult">
								<option value="${smearResult}" 
									<c:if test="${smearResult == status.value}">
										SELECTED
									</c:if>
								>${smearResult.name.name}&nbsp;&nbsp;(${smearResult.name.shortName})</option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>	
				</td>
			</tr>
			<tr id="numBacilliDiv_${rowCount}" style="display:none;">
				<td><spring:message code="mdrtb.numberofbacilli" /></td>
				<td>
						<spring:bind path="obj.smears[${rowCount}].bacilli.valueNumeric">
						<select name="${status.expression}" id="bacilliResult_${rowCount}">
								<option value=""><spring:message code="" /></option>
								<option value="1" <c:if test="${1 == status.value}">
											SELECTED
										</c:if>>1</option>
								<option value="2" <c:if test="${2 == status.value}">
											SELECTED
										</c:if>>2</option>
								<option value="3" <c:if test="${3 == status.value}">
											SELECTED
										</c:if>>3</option>
								<option value="4" <c:if test="${4 == status.value}">
											SELECTED
										</c:if>>4</option>
								<option value="5" <c:if test="${5 == status.value}">
											SELECTED
										</c:if>>5</option>
								<option value="6" <c:if test="${6 == status.value}">
											SELECTED
										</c:if>>6</option>
								<option value="7" <c:if test="${7 == status.value}">
											SELECTED
										</c:if>>7</option>
								<option value="8" <c:if test="${8 == status.value}">
											SELECTED
										</c:if>>8</option>
								<option value="9" <c:if test="${9 == status.value}">
											SELECTED
										</c:if>>9</option>
							</select>
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</spring:bind>
				</td>
			</tr>
			<tr><td colSpan='2'>&nbsp;</td>
			<tr>
				<td><spring:message code="mdrtb.sampleid" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearResult.accessionNumber">
						<input type='text' value='${status.value}' name='${status.expression}' id='sampleIdSmear'>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>		
			</tr> 
			<tr>
				<td><spring:message code="mdrtb.anatomicalsite" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].source.valueCoded">
						<select name="${status.expression}">
								<option value=""><spring:message code="mdrtb.pleasechooseanatomicalsite" /></option>
								<c:forEach items="${anatSites}" var="anatSite">
									<option value="${anatSite}"
									<c:if test="${anatSite == status.value}">
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
				<td><spring:message code="mdrtb.method" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearMethod.valueCoded">
							<select name="${status.expression}">
								<option value=""><spring:message code="mdrtb.pleasechooseamethod" /></option>
								<c:forEach items="${smearMethods}" var="method">
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
			<Tr>
				<td><spring:message code="mdrtb.samplereceived" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearDateReceived.valueDatetime">
					<input type="text" style="width:100px" name="${status.expression}" id="resSampleReceivedDateSmear" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
					</spring:bind>
				</td>
			</Tr>	
			<tr>
				<td><spring:message code="mdrtb.smearCompleteDate" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearResultDate.valueDatetime">
					<input type="text" style="width:100px" name="${status.expression}" id="resTestCompleteSmearDate" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
					</c:if>
					</spring:bind>
				</td>	
			</tr>
			<tr>
				<td><spring:message code="mdrtb.lab" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearParentObs.location">
					<select name="${status.expression}" id="labSmear_${rowCount}">
						<option value=""><spring:message code="mdrtb.pleasechoosealab" /></option>
						<c:forEach items="${locations}" var="lab">
									<option value="${lab.locationId}"
										<c:if test="${lab.locationId == status.value}">
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
				<td><spring:message code="mdrtb.comment" /></td>
				<td>
				<spring:bind path="obj.smears[${rowCount}].smearResult.comment">
				<textarea name='${status.expression}' id='commentSmear'>${status.value}</textarea>
				<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
				</td>
			</tr>	
		</table>
						</td></Tr>
				</c:if>
				<c:if test="${fn:length(obj.cultures) > 0}">
							<tr><td>
							<table>
								<tr>
									<th colSpan='2' style="text-align:left" class="patientTableHeader">&nbsp;&nbsp;<spring:message code="mdrtb.culture" /></th>
								</tr>
								<tr>
									<td><spring:message code="mdrtb.samplecollectiondate" /></td>
									<td>
										<spring:bind path="obj.cultures[${rowCount}].cultureResult.obsDatetime">
										<input type="text" style="width:100px" value="${status.value}" id="sputumCollectionDateCulture" name="${status.expression}" onMouseDown="$(this).date_input()" class="sputumCollection"/>&nbsp;&nbsp;(${dateFormat})
											<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
										</spring:bind>
									</td>
								</tr>
								<tr>
									<td><spring:message code="mdrtb.labtestorderedby" /></td>
									<td>
										<select name="provider_${rowCount}" id="provider_${rowCount}" class="labTestOrderedBy">
											<option value=""></option>
											<c:forEach items="${providers}" var="provider">
												<option value="${provider.userId}">${provider.givenName} ${provider.familyName}</option>
											</c:forEach>
										</select>
									</td>
								</tr>
								<tr><td><spring:message code="mdrtb.cultureresults" /></td>
										<td>
											<spring:bind path="obj.cultures[${rowCount}].cultureResult.valueCoded">
											<select name="${status.expression}" id="cultureResults_${rowCount}" onChange="javascript:showTrIfScanty();">
													<option value=""><spring:message code="mdrtb.pleasechoosearesult" /></option>
													<c:forEach items="${cultureResults}" var="cultureResult">
														<option value="${cultureResult}" 
															<c:if test="${cultureResult == status.value}">
																SELECTED
															</c:if>
														>${cultureResult.name.name}&nbsp;&nbsp;(${cultureResult.name.shortName})</option>
													</c:forEach>
											</select>
											<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
											</spring:bind>
										</td>
								</tr>
								<tr id="numColoniesDiv_${rowCount}" style="display:none;">
									<td><spring:message code="mdrtb.numberofcolonies" /></td>
									<td>
									
										<spring:bind path="obj.cultures[${rowCount}].colonies.valueNumeric">
											<select name="${status.expression}" id="coloniesResult_${rowCount}">
												<option value=""><spring:message code="" /></option>
												<option value="1" <c:if test="${1 == status.value}">
											SELECTED
										</c:if>>1</option>
												<option value="2" <c:if test="${2 == status.value}">
											SELECTED
										</c:if>>2</option>
												<option value="3" <c:if test="${3 == status.value}">
											SELECTED
										</c:if>>3</option>
												<option value="4" <c:if test="${4 == status.value}">
											SELECTED
										</c:if>>4</option>
												<option value="5" <c:if test="${5 == status.value}">
											SELECTED
										</c:if>>5</option>
												<option value="6" <c:if test="${6 == status.value}">
											SELECTED
										</c:if>>6</option>
												<option value="7" <c:if test="${7 == status.value}">
											SELECTED
										</c:if>>7</option>
												<option value="8" <c:if test="${8 == status.value}">
											SELECTED
										</c:if>>8</option>
												<option value="9" <c:if test="${9 == status.value}">
											SELECTED
										</c:if>>9</option>
											</select>
											<c:if test="${status.errorMessage != ''}">
												<span class="error">${status.errorMessage}</span>
											</c:if>
										</spring:bind>
								
									</td>
								</tr>
								<tr>
									<td colSpan='2'>&nbsp;</td>
								<tr>
								<tr>
									<td><spring:message code="mdrtb.sampleid" /></td>
									<spring:bind path="obj.cultures[${rowCount}].cultureResult.accessionNumber">
									<td><input type='text' value='${status.value}' name='${status.expression}' id="sampleIdCulture"></td>
									<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
										</spring:bind>
								</tr>
								<tr>
									<td><spring:message code="mdrtb.anatomicalsite" /></td>
									<td>
										<spring:bind path="obj.cultures[${rowCount}].source.valueCoded">
										<select name="${status.expression}" id="anatSiteCulture">
												<option value=""><spring:message code="mdrtb.pleasechooseanatomicalsite" /></option>
												<c:forEach items="${anatSites}" var="anatSite">
													<option value="${anatSite}" 
														<c:if test="${anatSite == status.value}">
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
									<td><spring:message code="mdrtb.method" /></td>
									<td>
										<spring:bind path="obj.cultures[${rowCount}].cultureMethod.valueCoded">
											<select name="${status.expression}" id="methodCulture">
												<option value=""><spring:message code="mdrtb.pleasechooseamethod" /></option>
												<c:forEach items="${cultureMethods}" var="method">
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
								<Tr>
									<td><spring:message code="mdrtb.samplereceived" /></td>
									<td>
									<spring:bind path="obj.cultures[${rowCount}].cultureDateReceived.valueDatetime">
									<input type="text" style="width:100px" name="${status.expression}" id="resSampleReceivedDateSmear" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
									<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
										</spring:bind>
									</td>				
								</Tr>
								<tr>
									<td><spring:message code="mdrtb.culturestart" /></td>
									<td>
									<spring:bind path="obj.cultures[${rowCount}].cultureStartDate.valueDatetime">
										<input type="text" name="${status.expression}" id="resCultureStartDate"  style="width:100px" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
											<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
									</spring:bind>
									</td>
								</tr>	
								<tr>
									<td><spring:message code="mdrtb.culturecomplete" /></td>
									<td>
									<spring:bind path="obj.cultures[${rowCount}].cultureResultsDate.valueDatetime">
										<input type="text" style="width:100px" name="${status.expression}" id="resTestCompleteCultureDate" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
											<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
									</spring:bind>
									</td>
								</tr>
								<Tr>
									<td><spring:message code="mdrtb.typeoforganism" /></td>
									<Td>
										<spring:bind path="obj.cultures[${rowCount}].typeOfOrganism.valueCoded">
											<select name="${status.expression}" id="typeOfOrganism" class="typeOfOrganism" onchange="javascript:testValue(this)">
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
					
										<spring:bind path="obj.cultures[${rowCount}].typeOfOrganismNonCoded.valueText">
										<input type="text" style="width:100px" name="${status.expression}" id="otherType" value="${status.value}" >
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
										<spring:bind path="obj.cultures[${rowCount}].cultureParentObs.location">
										<select name="${status.expression}" id="labCulture_${rowCount}">
											<option value=""><spring:message code="mdrtb.pleasechoosealab" /></option>
											<c:forEach items="${locations}" var="lab">
														<option value="${lab.locationId}"
															<c:if test="${lab.locationId == status.value}">
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
									<td><spring:message code="mdrtb.comment" /></td>
									<spring:bind path="obj.cultures[${rowCount}].cultureResult.comment">
									<td><textarea name='${status.expression}' id='commentCulture'>${status.value}</textarea></td>
											<c:if test="${status.errorMessage != ''}">
													<span class="error">${status.errorMessage}</span>
											</c:if>
									</spring:bind>
								</tr>
							</table>
							</td></tr>
				</c:if>
				<c:if test="${fn:length(obj.dsts) > 0}">
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
					<input type="text" style="width:100px" value="${status.value}" id="sputumCollectionDateDST" name="${status.expression}" onMouseDown="$(this).date_input()" class="sputumCollection">&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.labtestorderedby" /></td>
				<td>
					<select name="provider_${rowCount}" id="provider_${rowCount}" class="labTestOrderedBy">
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
					<select name="${status.expression}" id="anatSiteCulture">
							<option value=""><spring:message code="mdrtb.pleasechooseanatomicalsite" /></option>
							<c:forEach items="${anatSites}" var="anatSite">
								<option value="${anatSite}" 
									<c:if test="${anatSite == status.value}">
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
						<input type='text' value='${status.value}' name='${status.expression}' id='sampleIdSmear'>
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
						<select name="${status.expression}" id="dstCulture">
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
					<input type="text" style="width:50px" value="${status.value}" id="coloniesIncontrol" name="${status.expression}" >
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
				<input type="text" style="width:100px" name="${status.expression}" id="dstDateReceived" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
					<input type="text" name="${status.expression}" id="dstStartDate"  style="width:100px" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
					<input type="text" style="width:100px" name="${status.expression}" id="resTestCompleteCultureDate" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
						<select name="${status.expression}"  class="typeOfOrganism" onchange="javascript:testValue(this)">
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
					<input type="text" style="width:100px" name="${status.expression}" id="otherType" value="${status.value}" >
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
										<c:if test="${lab.locationId == status.value}">
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
				<td><textarea name='${status.expression}' id='commentSmear'>${status.value}</textarea></td>
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
				<Td nowrap>
				<span>&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;
				${command.drug.valueCoded.name.name}</Td>
				<td>
					<spring:bind path="obj.dsts[${rowCount}].dstResults[${counter.index}].drug.concept">
						<select name="${status.expression}" class="selects" onchange="javascript:resetColorCoding()">
							<c:if test='${status.value == ""}'>
							<option value=""><spring:message code="mdrtb.pleasechoosearesult" /></option>
							</c:if>
							<c:if test='${status.value != ""}'>
							<option value="${none}"><spring:message code="mdrtb.removethistest" /></option>
							</c:if>
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
					<input type="text" value="${status.value}" name="${status.expression}" id="concentration" style="width:50px">
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>	
				<td align='center'>
					<spring:bind path="obj.dsts[${rowCount}].dstResults[${counter.index}].colonies.valueNumeric">
					<input type="text" value="${status.value}" name="${status.expression}" id="colonies" style="width:50px">
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
				</tr>
			</c:forEach>
			</table>	
			</td></tr>
				</c:if>
		</table>		 
		

		<br>
		<c:set var="retType" scope="page" value="none" />
		<c:if test="${fn:length(obj.smears) > 0}">
			<c:set var="retType" scope="page" value="smears" />
		</c:if>
		<c:if test="${fn:length(obj.cultures) > 0}">
			<c:set var="retType" scope="page" value="cultures" />
		</c:if>
		<c:if test="${fn:length(obj.dsts) > 0}">
			<c:set var="retType" scope="page" value="dsts" />
		</c:if>
		
		<input type="submit"  name="submit"    value='<spring:message code="mdrtb.save" />'>
		&nbsp;<input type="submit"  name="submit"  value='<spring:message code="mdrtb.cancel" />'>
		&nbsp;<input type="submit" name="submit"  value='<spring:message code="mdrtb.delete" />'>
</div>
<input type="hidden" name="retType" value='${retType}'/>
</form>	
	<script>
		showTrIfScanty();
	</script>
<%@ include file="mdrtbFooter.jsp"%>