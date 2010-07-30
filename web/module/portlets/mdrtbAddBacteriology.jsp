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
			var smear = "contentDivSmear_";
			var culture = "contentDivCulture_";
			var smearString = smear + currentTest;
			var cultureString = culture + currentTest;
			var smearDiv = document.getElementById(smearString);
			var cultureDiv = document.getElementById(cultureString);
			$(smearDiv).removeClass();
			$(smearDiv).addClass("displayOn");
			$(cultureDiv).removeClass();
			$(cultureDiv).addClass("displayOn");
			
			resetLinks();
		}
	
		function removeRow(){
			var smear = "contentDivSmear_";
			var culture = "contentDivCulture_";
			var smearString = smear + currentTest;
			var cultureString = culture + currentTest;
			var smearDiv = document.getElementById(smearString);
			var cultureDiv = document.getElementById(cultureString);
			$(smearDiv).removeClass();
			$(smearDiv).addClass("displayOff");
			$(cultureDiv).removeClass();
			$(cultureDiv).addClass("displayOff");
			currentTest --;
			resetLinks();
		}
	
		function resetLinks(){
				if (currentTest == maxTests){
				
			var addAnother = document.getElementById("addAnother");
			$(addAnother).removeClass();
			$(addAnother).addClass("displayOff");
			var addAnotherTop = document.getElementById("addAnotherTop");
			$(addAnotherTop).removeClass();
			$(addAnotherTop).addClass("displayOff");
		} else {
			var addAnother = document.getElementById("addAnother");
			$(addAnother).removeClass();
			$(addAnother).addClass("displayOn");
			
			var addAnotherTop = document.getElementById("addAnotherTop");
			$(addAnotherTop).removeClass();
			$(addAnotherTop).addClass("displayOn");
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
	
	
	var scantyId = ${scantyId};
    function showTrIfScanty(){
    	
   		var smearResultRootString = "smearResult_";
   		var smearBacilliRootString = "numBacilliDiv_";
   		var cultureResultRootString = "cultureResults_";
   		var cultureColoniesRootString = "numColoniesDiv_";	
    	var i = 0;	
    	
    	while (i < 50){
    		var result = document.getElementById(smearResultRootString+i);
    		if (result != null && result != "" && result != "undefined"){
    			var trSmear = document.getElementById(smearBacilliRootString+i);
    			if (result.value == scantyId)
    				$(trSmear).attr("style", "");
    			else {
    				$(trSmear).attr("style", "display:none;");
    				var bacilliVal = document.getElementById("bacilliResult_" + i);
    				$(bacilliVal).val("");
    			}	
    			
    		}
    		
    		
    		var result = document.getElementById(cultureResultRootString+i);
    		if (result != null && result != "" && result != "undefined"){
    			var trCulture = document.getElementById(cultureColoniesRootString+i);
    			if (result.value == scantyId)
    				$(trCulture).attr("style", "");
    			else {
    				$(trCulture).attr("style", "display:none;");
    				var coloniesVal = document.getElementById("coloniesResult_" + i);
    				$(coloniesVal).val("");
    			}	
    		}
    		i++;	
    	}
    }
    
    
</script>
<div><spring:message code="mdrtb.addBacteriology" /><br><br></div>
<div>

<div style="width:100%; font-size:70%">
	<br>
	<table class="portletTable">
		<Tr><th class="patientTableHeader">&nbsp;</th><th class="patientTableHeader"></th></Tr>
		<tr>
			<td><spring:message code="mdrtb.sputumcollectionencounter" /></td>
			<td>
					<select name="encSelect" id="encSelect" onchange="javascript:setEncounterDate(this)">
						<option value=""><spring:message code="mdrtb.none" /></option>
						<c:forEach items="${encounters}" var="encounter">
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
	
	<!--  bacteriology container table -->
	<br>&nbsp;&nbsp;&nbsp;<a href="#" onmousedown="javascript:addAnother()" id="addAnotherTop"><spring:message code="mdrtb.addanother" /></a><Br><br>
	<table class="portletTable">
	<c:forEach var="rowCount" begin="0" end="${maxTests-1}" step="1">
		<tr>	
		<td valign='top'><div id="contentDivSmear_${rowCount}" 
			<c:if test="${rowCount == 0}">
					class="displayOn";
				</c:if>
				<c:if test="${rowCount > 0}">
					class="displayOff";
				</c:if>
			>
			<table>
			<tr>
				<th colSpan='2' style="text-align:left" class="patientTableHeader">&nbsp;&nbsp;<spring:message code="mdrtb.smear" /></th>
			</tr>
			
			<tr>
				<td><spring:message code="mdrtb.samplecollectiondate" /></td>
				<td>
					<spring:bind path="obj.smears[${rowCount}].smearResult.obsDatetime">
						<input type="text" style="width:100px" value="${status.value}" id="sputumCollectionDate_${rowCount}" name="${status.expression}"  onMouseDown="$(this).date_input()" class="dateType">&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.labtestorderedby" /></td>
				<td>
					<select name="smear_provider_${rowCount}" id="smear_provider_${rowCount}" class="labTestOrderedBy">
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
						<input type='text' value='${status.value}' name='${status.expression}' id='sampleIdSmear_${rowCount}'>
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
					<input type="text" style="width:100px" name="${status.expression}" id="resSampleReceivedDateSmear_${rowCount}" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
					<input type="text" style="width:100px" name="${status.expression}" id="resTestCompleteSmearDate_${rowCount}" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
				<td><spring:message code="mdrtb.comment" /></td>
				<spring:bind path="obj.smears[${rowCount}].smearResult.comment">
				<td><textarea name='${status.expression}' id='commentSmear_${rowCount}'>${status.value}</textarea></td>
				<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</tr>	
		</table>
		</div></td>
		<Td></Td>
		<Td><div id="contentDivCulture_${rowCount}"
			<c:if test="${rowCount == 0}">
					class="displayOn";
				</c:if>
				<c:if test="${rowCount > 0}">
					class="displayOff";
				</c:if>
			>
			<table>
			<tr>
				<th colSpan='2' style="text-align:left" class="patientTableHeader">&nbsp;&nbsp;<spring:message code="mdrtb.culture" /></th>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.samplecollectiondate" /></td>
				<td>
					<spring:bind path="obj.cultures[${rowCount}].cultureResult.obsDatetime">
					<input type="text" style="width:100px" value="${status.value}" id="sputumCollectionDateCultures_${rowCount}" name="${status.expression}" onMouseDown="$(this).date_input()" class="dateType">&nbsp;&nbsp;(${dateFormat})
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.labtestorderedby" /></td>
				<td>
					<select name="culture_provider_${rowCount}" id="culture_provider_${rowCount}" class="labTestOrderedBy">
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
							<option value="1" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>1</option>
							<option value="2" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>2</option>
							<option value="3" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>3</option>
							<option value="4" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>4</option>
							<option value="5" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>5</option>
							<option value="6" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>6</option>
							<option value="7">7</option>
							<option value="8" <c:if test="${cultureResult == status.value}">
											SELECTED
										</c:if>>8</option>
							<option value="9" <c:if test="${cultureResult == status.value}">
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
				<td><input type='text' value='${status.value}' name='${status.expression}' id="sampleIdCulture_${rowCount}"></td>
				<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
			</tr>
			<tr>
				<td><spring:message code="mdrtb.anatomicalsite" /></td>
				<td>
					<spring:bind path="obj.cultures[${rowCount}].source.valueCoded">
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
				<td><spring:message code="mdrtb.method" /></td>
				<td>
					<spring:bind path="obj.cultures[${rowCount}].cultureMethod.valueCoded">
						<select name="${status.expression}" id="methodCulture_${rowCount}">
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
				<input type="text" style="width:100px" name="${status.expression}" id="resSampleReceivedDateCulture_${rowCount}" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
					<input type="text" name="${status.expression}" id="resCultureStartDate_${rowCount}"  style="width:100px" value="${status.value}" onMouseDown="$(this).date_input()">&nbsp;&nbsp;(${dateFormat})
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
					<spring:bind path="obj.cultures[${rowCount}].typeOfOrganism.valueCoded">
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

					<spring:bind path="obj.cultures[${rowCount}].typeOfOrganismNonCoded.valueText">
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
					<spring:bind path="obj.cultures[${rowCount}].cultureParentObs.location">
					<select name="${status.expression}" id="labCulture_${rowCount}">
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
				<td><spring:message code="mdrtb.comment" /></td>
				<spring:bind path="obj.cultures[${rowCount}].cultureResult.comment">
				<td><textarea name='${status.expression}' id='commentCulture_${rowCount}'>${status.value}</textarea></td>
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
			</tr>
		</table>
		</div></Td>
		</tr>
	</c:forEach>	
	</table>
	<Br>
	&nbsp;<a href="#" onmousedown="javascript:addAnother()" id="addAnother"><spring:message code="mdrtb.addanother" /></a>
	&nbsp;&nbsp;&nbsp;<a href="#" onmousedown="javascript:removeRow()" id="removeRow" class="displayOff"><spring:message code="mdrtb.remove"/></a>
	





	
</div>
<div style="width:100%; font-size:70%">
	<c:if test="${!empty obj.patient.patientId}">
			<input type="hidden" value="${obj.patient.patientId}" name="patientId" id="patientId">
			<input type="hidden" value="1" name="numRowsShown" id="numRowsShown">
	</c:if>	
	<br><br>
	<input type="submit" name='submit' id='submit' value='<spring:message code="mdrtb.savebacteriology" />'>&nbsp;&nbsp;&nbsp;<input type="submit" value='<spring:message code="mdrtb.cancel" />' id="cancel">
</div>
<br>
</div>