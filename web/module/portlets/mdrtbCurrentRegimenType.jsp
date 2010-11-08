<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbCurrentRegimenTypeService.js'></script>
<script type="text/javascript">

	
	
	function newCurrentRegimenType(){
		var currentRegimentype = document.getElementById("currentRegimenType");
		var currentRegimenDate = document.getElementById("currentRegimenDate");
		if (currentRegimentype.value != "" && currentRegimenDate.value != ""){
		MdrtbCurrentRegimenTypeService.setCurrentRegimenTypeOnDate(currentRegimentype.value, currentRegimenDate.value, ${obj.patient.patientId}, function(ret){
							if (!ret)
								alert('DWR error');
							else
								window.location="${pageContext.request.contextPath}/module/mdrtb/regimen/regimen.form?patientId=${obj.patient.patientId}&patientProgramId=${patientProgramId}";
						});
			
		}
	}
	
	function testReadyToSubmit(){
		var currentRegimentype = document.getElementById("currentRegimenType");
		var currentRegimenDate = document.getElementById("currentRegimenDate");
		var submitCurrentRegType = document.getElementById("submitCurrentRegType");
		
		if (currentRegimentype.value != "" && currentRegimenDate.value != ""){
			$j(submitCurrentRegType).removeAttr("disabled");
		} else {
			$j(submitCurrentRegType).attr("disabled", "disabled");
			$j(submitCurrentRegType).focus();
		}
		
		
	}
	
	
</script>

<br>				<spring:message code="mdrtb.updateCurrentRegimenType" />: &nbsp;
					<select name="currentRegimenType" id="currentRegimenType" onchange="testReadyToSubmit();" autocomplete="off">
						<option value="" selected></option>
						<option value="${standardized}"><spring:message code="mdrtb.standardized" /></option>
						<option value="${empiric}"><spring:message code="mdrtb.empiric" /></option>
						<option value="${individualized}"><spring:message code="mdrtb.individualized" /></option>
						<option value="0"><spring:message code="mdrtb.none" /></option>
					</select> 
					<input type=text name="currentRegimenDate" id="currentRegimenDate" value="" style="width:80px;" onmousedown="javascript:$j(this).date_input()" onchange="testReadyToSubmit();">
					<input type="button" id="submitCurrentRegType" value="<spring:message code="mdrtb.update" />" onClick="newCurrentRegimenType()" disabled>
<Br>