<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbOrder.js'></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>

<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/date_input.css"%></style>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>		

<!-- need to use an "include" to add this so that the javascript files can pick up the model object -->
<!-- and for some reason we may need the regimen javascript as well -->
<script type="text/javascript"> 
<%@ include file="/WEB-INF/view/module/mdrtb/resources/regimen.js"%>
</script>
<script type="text/javascript"> 
<%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtbPatientOverview.js"%>
</script>

<div class="statusContainerDiv">
<script type="text/javascript">

	function removeSupporter(patientId, treatmentSupporterId){
		MdrtbContactsDWRService.removeSupporter(patientId, treatmentSupporterId, function(ret){
			if (!ret)
				alert("<spring:message code='DWRfailedtoremovets' />");
			else {
			var treatmentsupportergivenname = document.getElementById("treatmentsupportergivenname");
			var treatmentsupporterfamilyname = document.getElementById("treatmentsupporterfamilyname");
			var treatmentsupportergender = document.getElementById("treatmentsupportergender");
			var treatmentsupportervillage = document.getElementById("treatmentsupportervillage");
			var treatmentsupporterdob = document.getElementById("treatmentsupporterdob");
			var treatmentsupporterphone = document.getElementById("treatmentsupporterphone");
			var treatmentSupporterId = document.getElementById("treatmentSupporterId");
			
			
			
			
			var removeTSspan = document.getElementById("removeTSspan");
			
			$j(treatmentsupportergivenname).val("");
			$j(treatmentsupporterfamilyname).val("");
			$j(treatmentsupportergender).val("");
			$j(treatmentsupportervillage).val("");
			$j(treatmentsupporterdob).val("");
			$j(treatmentsupporterphone).val("");
			
			$j(removeTSspan).empty();
			$j(removeTSspan).html("<spring:message code='mdrtb.treatmentsupporterremoved' />");
			$j(removeTSspan).attr("style", "color:red;");
			$j(treatmentSupporterId).val("0");
			
			}	
		});
		
		
	}
	
	function checkRequired(){
	
			var treatmentstartdateinput = document.getElementById("treatmentstartdateinput");		
			var prevtreatmentinput = document.getElementById("prevtreatmentinput");		
			var regTypeInput = document.getElementById("regTypeInput");
			var regTypeInputByDrug = document.getElementById("regTypeInputByDrug");
			var pulmonary = document.getElementById("pulmonary");
			var extrapulmonary = document.getElementById("extrapulmonary");		
			var healthCenterInput = document.getElementById("healthCenterInput");
			var detectionDate = document.getElementById("detectionDate");
			
			var showMessage = false;
			if (treatmentstartdateinput.value == null || treatmentstartdateinput.value == ""){
				var reqTreatmentStartDate = document.getElementById("reqTreatmentStartDate");
				$j(reqTreatmentStartDate).attr("style", "display:inline;");
				showMessage = true;
			} 
			
			if ((prevtreatmentinput.value == null || prevtreatmentinput.value == "")){
				var reqRegGroupSub = document.getElementById("reqRegGroupSub");
				$j(reqRegGroupSub).attr("style", "display:inline;");
				showMessage = true;
			}

			
			if (regTypeInput.value == null || regTypeInput.value == ""){
				var reqTypeClassification = document.getElementById("reqTypeClassification");
				$j(reqTypeClassification).attr("style", "display:inline;");
				showMessage = true;
			}
			if (pulmonary.checked == false && extrapulmonary.checked == false){
				var reqTypePull = document.getElementById("reqTypePull");
				$j(reqTypePull).attr("style", "display:inline;");
				showMessage = true;
			}
			if (regTypeInputByDrug.value == null || regTypeInputByDrug.value == ""){
				var regTypeInputByDrugSpan = document.getElementById("regTypeInputByDrugSpan");
				$j(regTypeInputByDrugSpan).attr("style", "display:inline;");
				showMessage = true;
			}
					
			//alert(healthCenterInput.value);
			if ((healthCenterInput.value == null || healthCenterInput.value == "")){
				var reqHealthCenter = document.getElementById("reqHealthCenter");
				$j(reqHealthCenter).attr("style", "display:inline;");
				showMessage = true;
			}
			if ((detectionDate.value == null || detectionDate.value == "")){
				var reqDetectionDate = document.getElementById("reqDetectionDate");
				$j(reqDetectionDate).attr("style", "display:inline;");
				showMessage = true;
			}
			if (showMessage){
				var messageSpan = document.getElementById("requiredMessage");
				$j(messageSpan).attr("style", "display:inline");
			}
			
			
	}
	
	
	var tmpLocation = '<c:if test="${!empty obj.tbLocation}">${obj.tbLocation.valueText}</c:if>';
    function testRadioState(){
    	var pulmonary = document.getElementById("pulmonary");
    	var extrapulmonary = document.getElementById("extrapulmonary");
    	var anatLocation = document.getElementById("anatLocation");
    	var extraPLocation = document.getElementById("extraPLocation");
    	
    	if (pulmonary.checked == true){
    		tmpLocation = anatLocation.value;
    		anatLocation.value = "";
    		anatLocation.disabled = true;
    		$j(extraPLocation).attr("style", "display:none");
    	}    	
    	if (extrapulmonary.checked == true){
    		anatLocation.disabled = false;
    		anatLocation.value = tmpLocation;
    		$j(extraPLocation).attr("style", "display:inline");
    	}   	
    }
    
</script>
					
						<!-- put patient in program -->
						<c:if test="${empty obj.patientProgram}">
							<form method="post" onsubmit="javascript:return validateSubmitData();">
									<table class="portletTable"> 
											<tR>
												<td valign='top'>
													<div>
														<spring:message code="mdrtb.enrollpatientinmdrtbprogram" /> &nbsp;&nbsp; <input type="text" value=""  name="programEnrollmentDate" id="newProgramEnrollmentDate" style="width:90px" onmousedown="javascript:$j(this).date_input()">  <input type="submit" name="submit" value="<spring:message code="mdrtb.enroll" />" />
														<input type='hidden' id='patientId' name='patientId' value='${obj.patient.patientId}'>
													</div>
												</td>
											</tR>
									</table>				
											<bR><br>
							</form>				
						</c:if>	
						
						<c:if test="${!empty obj.patientProgram}">
								<form method="post">
								<!-- Here's the content of the Status tab -->
								
								<span class="dstWarning" id="requiredMessage" style="display:none;">&nbsp;&nbsp;&nbsp;* = <spring:message code="mdrtb.requiredData" /><br><br></span>
								
								<table class="statusTable">
									<tR id="firstTableRow">
										<td class="statusTDs">
											<Table class="statusFillOutTable" id="shortStatusTable">
												
												<tr class="statusTableOdd">
													<td > <spring:message code="mdrtb.mdrtbprogramstartdatebroken" /><bR><Br></td>
													<td><div >
														
														<input type='text' name='programstartdate' value="<openmrs:formatDate date="${obj.patientProgram.dateEnrolled}" format="${dateFormat}" />" style="width:90px" onmousedown="javascript:$j(this).date_input()"/>
														<Br>
													</div></td>
												</tr>
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.treatmentstartdate" />:<bR><Br></td> 
													<td><div >
															
															<span class="dstWarning" id="reqTreatmentStartDate" style="display:none;">*</span><input type='text' name='treatmentstartdate' id='treatmentstartdateinput' value="<openmrs:formatDate date="${obj.treatmentStartDate.valueDatetime}" format="${dateFormat}" />" style="width:90px" onmousedown="javascript:$j(this).date_input()"/>
															<Br>
													</div></td>
												</tr>
												
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.registrationgroup" /><br><Br></td>
												<Td>
															
															<span class="spanFontSize"><span class="spanFontSize"><span class="dstWarning" id="reqRegGroupSub" style="display:none;">* </span><spring:message code="mdrtb.accordingtoprevioustreatment" /></span><br>
															<select name="prevtreatment" id="prevtreatmentinput">
																	<option value=""></option>
																<c:forEach items="${prevTreatment}" var="state" varStatus="varStatus">
																	<option value="${state.answerConcept.conceptId}"
		
																			<c:if test="${!empty obj.patientClassPrevTreatment}">
																			<c:if test="${obj.patientClassPrevTreatment.valueCoded.conceptId == state.answerConcept.conceptId}">
																			SELECTED
																			</c:if>
																			</c:if>
		
																	>${state.answerConcept.name.shortName}</option>
																</c:forEach>
															</select><Br>
															
															<span class="spanFontSize"><span class="dstWarning" id="regTypeInputByDrugSpan" style="display:none;">* </span><spring:message code="mdrtb.accordingtopreviousdrugregimen" /></span><br>
															<select name="prevdruguse" id="regTypeInputByDrug">
																	<option value=""></option>
																<c:forEach items="${prevDrugUse}" var="state" varStatus="varStatus">
																	<option value="${state.answerConcept.conceptId}"
		
																			<c:if test="${!empty obj.patientClassDrugUse}">
																				<c:if test="${obj.patientClassDrugUse.valueCoded.conceptId == state.answerConcept.conceptId}">
																					SELECTED
																				</c:if>
																			</c:if>
		
																	>${state.answerConcept.name.shortName}</option>
																</c:forEach>
															</select><Br>
															<bR>
													
													</Td>
													</tr>
													
													<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.type" />:<br><Br></td>
													<td>
															
															<c:set var="patientDate" scope="page" value="" />
															<span class="spanFontSize"><span class="dstWarning" id="reqTypeClassification" style="display:none;">* </span><spring:message code="mdrtb.tbclassification" />:    &nbsp;&nbsp;</span>
															
															<select name="tbclassification" id="regTypeInput">
																	<option value=""></option>
																<c:forEach items="${tbCaseClass}" var="state" varStatus="varStatus">
																	<option value="${state.answerConcept.conceptId}"
		
																			<c:if test="${!empty obj.tbCaseClassification}">
																			<c:if test="${obj.tbCaseClassification.valueCoded.conceptId == state.answerConcept.conceptId}">
																			SELECTED
																			</c:if>
																			</c:if>
		
																	>${state.answerConcept.name.shortName}</option>
																</c:forEach>
															</select><Br>
															
															<!-- adding detection date for 2008 forms -->
															<span class="spanFontSize"><span class="dstWarning" id="reqDetectionDate" style="display:none;">* </span>
															&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.date" />:&nbsp;
															<input type='text' id='detectionDate' name='detectionDate' value="<openmrs:formatDate date="${obj.tbCaseClassification.obsDatetime}" format="${dateFormat}" />" style="width:90px" onmousedown="javascript:$j(this).date_input()"/><Br>
															
															
															<span class="spanFontSize"><span class="dstWarning" id="reqTypePull" style="display:none;">* </span><spring:message code="mdrtb.pulmonary" /></span><input type="radio" name="pulmonaryradio"  value="0"
																			<c:if test="${!empty obj.pulmonary}">
																				<c:if test="${obj.pulmonary.valueNumeric == 1}">
																				CHECKED
																				</c:if>
																			</c:if>
															id="pulmonary"
															onClick="javascript:testRadioState()"/> 
															&nbsp;&nbsp;<span class="spanFontSize"><spring:message code="mdrtb.extrapulmonary" /></span><input type="radio" name="pulmonaryradio" value="1"
																			<c:if test="${!empty obj.extrapulmonary}">
																				<c:if test="${obj.extrapulmonary.valueNumeric == 1}">
																					CHECKED
																				</c:if>
																			</c:if>
															
															id="extrapulmonary" 
															onClick="javascript:testRadioState()"/> 
															<br>
															<div id="extraPLocation" style="display:none;">&nbsp;&nbsp;&nbsp;&nbsp;
															<span class="spanFontSize"><spring:message code="mdrtb.location" />    &nbsp;&nbsp;</span> &nbsp;&nbsp;<input id="anatLocation" type="text" value="<c:if test="${!empty obj.tbLocation}">${obj.tbLocation.valueText}</c:if>" name="tblocation" disabled=true><br>
															</div>
															<bR>
													
													<script>
														testRadioState();
													</script></td>
												</tr>
												
												
													<tr class="statusTableEven">
													<td ><spring:message code="mdrtb.healthcenter" />:<br><Br></td>
													<td>
														<span class="spanFontSize"><span class="dstWarning" id="reqHealthCenter" style="display:none;">* </span><spring:message code="mdrtb.center" />    &nbsp;&nbsp;</span> <br>
															<select name="healthcenter" id="healthCenterInput">
																<option value=""></option>
																<c:forEach items="${locations}" var="location" varStatus="varStatus">
																	<option value="${location.locationId}"
		
																			<c:if test="${!empty obj.healthCenter}">
																			<c:if test="${obj.healthCenter.value == location.locationId}">
																			SELECTED
																			</c:if>
																			</c:if>
		
																	>${location.name}</option>
																</c:forEach>
															</select><Br>
															<span class="spanFontSize"><spring:message code="mdrtb.district" />    &nbsp;&nbsp;</span> <br>
															<input type="text" value="<c:if test="${!empty obj.healthDistrict}">${obj.healthDistrict.value}</c:if>" name="healthdistrict" id="healthDistrictInput">
															
													</td>
												</tr>
												
												<tR><Td class="statusTableOdd" colspan="2" style="height:3px; padding: 0px 0px 0px 0px;"><Br><hr></Td></tR>
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.hiv" /><BR><Br></td>
		
													<td>
														
														<span class="spanFontSize"><spring:message code="mdrtb.artprogramnumber" />    &nbsp;&nbsp;</span>&nbsp;<c:if test="${!empty obj.artId}">${obj.artId.identifier}</c:if><br>
														<span class="spanFontSize"><spring:message code="mdrtb.onart" /> &nbsp;&nbsp;</span><Br>
															<select name="onart">
																<option value=''></option>
																<option value='0'
																	<c:if test="${!empty obj.onART}">
																	<c:if test="${obj.onART.valueNumeric == 0}">
																		SELECTED
																	</c:if>		
																	</c:if>
																><spring:message code="mdrtb.no" /> </option>
																<option value='1' 
																		<c:if test="${!empty obj.onART}">
																		<c:if test="${obj.onART.valueNumeric == 1}">
																		SELECTED
																		</c:if>		
																		</c:if>
																><spring:message code="mdrtb.yes" /> </option> 
															</select><br>
														<span class="spanFontSize"><spring:message code="mdrtb.testresult" />   &nbsp;&nbsp;</span> <Br>
															<select name="hivstatus">
																<option value=""></option>
																<c:forEach items="${hivStatuses}" var="state" varStatus="varStatus">
																	<option value="${state.answerConcept.conceptId}"
																			<c:if test="${!empty obj.hivStatus}">
																			<c:if test="${obj.hivStatus.valueCoded.conceptId == state.answerConcept.conceptId}">
																			 SELECTED
																			</c:if>
																			</c:if>
																			>${state.answerConcept.name}</option>
																</c:forEach>	
															</select><Br>
															<span class="spanFontSize"><spring:message code="mdrtb.date" />&nbsp;&nbsp;</span> <Br>
															<input type='text' name='dateofhivtest' style="width:90px" value='<c:if test="${!empty obj.hivStatus}"><openmrs:formatDate date="${obj.hivStatus.obsDatetime}" format="${dateFormat}" /></c:if>' onmousedown="javascript:$j(this).date_input()"><br>
														
														
														<span class="spanFontSize"><spring:message code="mdrtb.latestcd4countpercent" />    &nbsp;&nbsp;</span><br>
														<!-- figure out what type of cd4 measurement to show % for kids or count for adults -->
														<c:set var="cd4TypeVal" scope="page" value="" />
														<c:if test="${!empty obj.cd4}">
															<c:set var="cd4TypeVal" scope="page" value="2" />
														</c:if>
														<c:if test='${cd4TypeVal == ""}'>
															<c:if test="${!empty obj.cd4percent}">
																<c:set var="cd4TypeVal" scope="page" value="1" />
															</c:if>
														</c:if>
															<c:if test="${cd4TypeVal == 2}">
																<input type='text' style="width:40px" name='cdfourcount'  value='${fn:replace(obj.cd4.valueNumeric, ".0", "")}' >
															</c:if>
															<c:if test="${cd4TypeVal == 1}">
																<input type='text' style="width:40px" name='cdfourcount'  value='${fn:replace(obj.cd4percent.valueNumeric, ".0","")}%' >
															</c:if>
															<c:if test='${cd4TypeVal == ""}'>
																<input type='text' style="width:40px" name='cdfourcount'  value='' >
															</c:if>
															<Br><span class="spanFontSize"><spring:message code="mdrtb.usepercent" /></span><br><br>
		
														
													</td>
												</tr>
												
												
											</table>
		
										</td>
										<td class="statusTDs" style="background-color:gray;"> </td>
										<Td class="statusTDs">
											<Table class="statusFillOutTable" id="tallStatusTable">
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.patientstate" /><br><Br></td>
													<td>
															
															<c:set var="patientDate" scope="page" value="" />
															<select name="patientStatus">
																	<option value=""></option>
																<c:forEach items="${patientStates}" var="state" varStatus="varStatus">
																	<option value="${state.programWorkflowStateId}"
																	
																		<c:forEach items="${obj.patientProgram.states}" var="patientState" varStatus="varStatusInner">
																			<c:if test="${patientState.state == state && patientState.endDate == null}">SELECTED<c:set var="patientDate" scope="page" value="${patientState.startDate}" /></c:if>
																		</c:forEach>
																	>${state.concept.name.name}</option>
																</c:forEach>
															</select><Br>
															<span class="spanFontSize"><spring:message code="mdrtb.date" />   &nbsp;&nbsp;</span>
															<input type='text' name='patientStatusDate' style="width:90px" value='<openmrs:formatDate date="${patientDate}" format="${dateFormat}" />' onmousedown="javascript:$j(this).date_input()">
															<bR>
													</td>
												</tr>
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.outcome" /><br><Br></td>
													<td>
															
															<c:set var="outcomeDate" scope="page" value="" />				
															<select name="outcomeStatus">
																	<option value=""></option>
																<c:forEach items="${outcomeStates}" var="state" varStatus="varStatus">
																	<option value="${state.programWorkflowStateId}"
																	
																		<c:forEach items="${obj.patientProgram.states}" var="patientState" varStatus="varStatusInner">
																			<c:if test="${patientState.state  == state && patientState.endDate == null}">SELECTED <c:set var="outcomeDate" scope="page" value="${patientState.startDate}" /></c:if>
																		</c:forEach>
																	>${state.concept.name.name}</option>
																</c:forEach>
															</select><bR>
															<span class="spanFontSize"><spring:message code="mdrtb.date" />   &nbsp;&nbsp;</span>
															<input type='text' name='outcomeStatusDate'  style="width:90px" value='<openmrs:formatDate date="${outcomeDate}" format="${dateFormat}" />' onmousedown="javascript:$j(this).date_input()">
															<bR>
													</td>
												</tr>
												
												<tR><Td class="statusTableOdd" colspan="2" style="height:3px; padding: 0px 0px 0px 0px;"><Br><hr></Td></tR>
												
												<tr class="statusTableEven">
													<c:set var="treatSupIdTmp" scope="page" value="0" />
													<c:if test="${!empty obj.treatmentSupporter}">
														<c:set var="treatSupIdTmp" scope="page" value="${obj.treatmentSupporter.personId}" />
													</c:if>
													<td ><spring:message code="mdrtb.treatmentsupporter" /><br><br>
													<input type="hidden" name="treatmentSupporterId" id="treatmentSupporterId" value="${treatSupIdTmp}">
													<input type="hidden" name="treatmentSupporterAction" id="treatmentSupporterAction" value="0"></td>
													<td>
														
														<span class="spanFontSize"><spring:message code="mdrtb.name" />:    &nbsp;&nbsp;</span> <Br>
														<input type='text'  disabled="true" class="disabled" id="treatmentsupportergivenname" name='treatmentsupporter'  value='<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${!empty obj.treatmentSupporter.names}"><c:set var="stop" scope="page" value="0" /><c:forEach items="${obj.treatmentSupporter.names}" var="name" varStatus="varStatus"><c:if test="${stop==0}">${name.givenName}</c:if><c:set var="stop" scope="page" value="1" /></c:forEach></c:if></c:if>' >
														<Br>
														<span class="spanFontSize"><spring:message code="mdrtb.surname" />:    &nbsp;&nbsp;</span> <Br>
														<input type='text'  disabled="true" class="disabled" id="treatmentsupporterfamilyname" name='treatmentsupportersurname'  value='<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${!empty obj.treatmentSupporter.names}"><c:set var="stopfn" scope="page" value="0" /><c:forEach items="${obj.treatmentSupporter.names}" var="name" varStatus="varStatus"><c:if test="${stopfn==0}">${name.familyName}</c:if><c:set var="stopfn" scope="page" value="1" /></c:forEach></c:if></c:if>' >
														<Br>
														<span class="spanFontSize"><spring:message code="mdrtb.gender" />   &nbsp;&nbsp;</span> <br>
														<select id="treatmentsupportergender" disabled="true" class="disabled" name="treatmentsupportergender">
																		<option value=""></option>
																		<option value="M"
																			<c:if test="${!empty obj.treatmentSupporter}">
																			<c:if test='${obj.treatmentSupporter.gender == "M"}'>
																			 SELECTED
																			</c:if>
																			</c:if>
																		><spring:message code="mdrtb.male" /></option>
																		<option value="F"
																			<c:if test="${!empty obj.treatmentSupporter}">
																			<c:if test='${obj.treatmentSupporter.gender == "F"}'>
																			 SELECTED
																			</c:if>
																			</c:if>
																		><spring:message code="mdrtb.female" /></option>
																	</select><br> 
															
														<span class="spanFontSize"><spring:message code="mdrtb.healthcentervillageofsupporter" />:  &nbsp;&nbsp;</span><br>
														<input type='text' disabled="true" class="disabled" name='treatmentsupporteraddress'  id='treatmentsupportervillage' value='<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${!empty obj.treatmentSupporter.addresses}"><c:set var="stopad" scope="page" value="0" /><c:forEach items="${obj.treatmentSupporter.addresses}" var="address" varStatus="varStatus"><c:if test="${stopad==0}">${address.cityVillage}</c:if><c:set var="stopad" scope="page" value="1" /></c:forEach></c:if></c:if>' >
														<br>
														<span class="spanFontSize"><spring:message code="mdrtb.treatmentsupporterbirthdate" />:    &nbsp;&nbsp;</span> <Br>
														<input type='text' disabled="true" class="disabled" id='treatmentsupporterdob' name='treatmentsupporterdob'  value='<c:if test="${!empty obj.treatmentSupporter}"><openmrs:formatDate date="${obj.treatmentSupporter.birthdate}" format="${dateFormat}" /></c:if>' style="width:90px" onmousedown="javascript:$j(this).date_input()">
														<br><spring:message code="mdrtb.phone" />:<br><input type='text' disabled="true" class="disabled" id="treatmentsupporterphone" value="<c:if test="${!empty obj.treatmentSupporterPhone}">${obj.treatmentSupporterPhone.valueText}</c:if>"/>
														<br>
															<a href="#" class="simple_popup" onmousedown="javascript:showSubmit = false; toggleToDisabled(true);$j('#treatmentSupporterAction').val('1');"><spring:message code="mdrtb.searchforapersoninopenmrs" /></a>
															<div class="simple_popup_info">
																<br>
																	<spring:message code="mdrtb.treatmentsupporterlookup" /> : <br><br><input type="text" id="patientLookupPopup" value="" onkeyup="javascript:loadPatients(this);"/><br>
																	<div class="resTableBodyLookup">
																		<Br>
																	</div>
																	
																<br>	
															</div>
															<br>
															<c:if test="${!empty obj.treatmentSupporter}">
																<span id="removeTSspan"><a href="#" onclick="javascript:removeSupporter(${obj.patient.patientId},${obj.treatmentSupporter.personId});return false;"><spring:message code="mdrtb.removetreatmentsupporter" /></a></span>
															</c:if>
														<!-- <br><a href="#" onclick="javascript:createNewPerson();return false;"><spring:message code="mdrtb.createatreatmentsupporter" /></a>
														<c:if test="${!empty obj.treatmentSupporter}">
															<br><a href="#" onclick="javascript:editPerson();return false;"><spring:message code="mdrtb.editthisperson" /></a>	
														</c:if>-->
														
													</td>
		
												</tr>
												
												<tR><Td class="statusTableOdd" colspan="2" style="height:3px; padding: 0px 0px 0px 0px;"><Br><hr></Td></tR>
												
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.treatmentcomment" /><Br><Br></td>
													<td>
														<textarea name="treatmentcomment" class="statusTextArea"><c:if test="${!empty obj.treatmentPlanComment}">${obj.treatmentPlanComment.valueText}</c:if></textarea>
														<br><Br>
													</td>
												</tr>
												<tr class="statusTableOdd">
													<td ><spring:message code="mdrtb.allergyandsideeffectscomment" /><Br><bR></td>
													<td>
														<textarea name="allergycomment" class="statusTextArea"><c:if test="${!empty obj.allergyComment}">${obj.allergyComment.valueText}</c:if></textarea>
														
													</td>
												</tr>
												
												<tR><Td class="statusTableOdd" colspan="2" style="height:3px; padding: 0px 0px 0px 0px;"><Br><hr></Td></tR>
												
												<tr class="statusTableEven">		
													<td ><spring:message code="mdrtb.durationofprevioustreatmentinmonths" /></td> 
													<td><div >
															
															<input type='text' name='durationofprevioustreatment' value='<c:if test="${!empty obj.durationOfPreviousTreatment}">${fn:replace(obj.durationOfPreviousTreatment.valueNumeric, ".0", "")}</c:if>' class="onehundredpx">
															
													</div></td>	
												</tr>
												<tr class="statusTableEven">	
													<td ><spring:message code="mdrtb.previoustreatmentcenter" /></td> 
													<td><div >
															
															<input type='text' name='previoustreatmentcenter' value='<c:if test="${!empty obj.previousTreatmentCenter}">${obj.previousTreatmentCenter.valueText}</c:if>' class="onehundredpx">
														
													</div></td>	
												</tr>
												<tr class="statusTableEven">	
													<td ><spring:message code="mdrtb.previousregistrationnumber" /></td> 
													<td><div >
															
															<input type='text' name='previousregistrationnumber' value='<c:if test="${!empty obj.previousRegistrationNumber}">${obj.previousRegistrationNumber.valueText}</c:if>' class="onehundredpx">
														
													</div></td>	
												</tr>
												<tr class="statusTableEven">
												
																					
													<td ><spring:message code="mdrtb.transferredfrom" /><bR><Br></td> 
													<td><div >
															
															<input type='text' name='transferedfrom' value='<c:if test="${!empty obj.patientTransferredFrom}">${obj.patientTransferredFrom.valueText}</c:if>' class="onehundredpx">
															
													</div></td>	
												</tr>
												<tr class="statusTableEven">									
													<td ><spring:message code="mdrtb.transferredto" /><bR><Br></td> 
													<td><div >
															
															<input type='text' name='transferedto' value='<c:if test="${!empty obj.patientTransferredTo}">${obj.patientTransferredTo.valueText}</c:if>' class="onehundredpx">
															
													</div></td>	
													
												</tr>
												<tr class="statusTableEven">	
													<td ><spring:message code="mdrtb.referredby" /><bR><Br></td> 
													<td><div >
															
															<input type='text' name='referredby' value='<c:if test="${!empty obj.patientReferredBy}">${obj.patientReferredBy.valueText}</c:if>' class="onehundredpx">
															
													</div></td>	
												</tr>
												
											</table>
										</Td>	
									</tr>
								</table>
									<Br>
									<input type="submit" name="submit" value="<spring:message code="mdrtb.save" />" >
									<input type='hidden' id='patientId' name='patientId' value='${obj.patient.patientId}'>
									</form>
									
								<script>
									checkRequired();
								</script>	
						</c:if>
						
						
				
				<br><br>
			</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>