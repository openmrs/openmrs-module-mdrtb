<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbNextVisit.js'></script>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbFindPatient.js'></script>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbContactsDWRService.js'></script>
<script type="text/javascript"> 
	var xmlDoc;
	var currentSearchTab;
	function changeSearchTab(tabObj, focusToId) {
		if (typeof tabObj == 'string')
			tabObj = document.getElementById(tabObj);

		if (tabObj) { 
			var tabs = tabObj.parentNode.parentNode.getElementsByTagName('a');
			for (var i = 0; i < tabs.length; ++i) {
				if (tabs[i].className.indexOf('current') != -1) {
					manipulateClass('remove', tabs[i], 'current');
				}
				var tabContentId = tabs[i].id + '_content';
				if (tabs[i].id == tabObj.id){
					currentSearchTab=i;

					showLayer(tabContentId);
					}
				else
					hideLayer(tabContentId);
			}
			addClass(tabObj, 'current');
			
			if (focusToId)
				document.getElementById(focusToId).focus();
			else
				tabObj.blur();
		}
	}
	
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
    
    function validateSubmitData(){
    	var enrollDate = document.getElementById("newProgramEnrollmentDate");
    	if (enrollDate != null && enrollDate.value == ""){
    		alert('<spring:message code="mdrtb.youmustenteravalidprogramenrollmentdate" />');
    		return false;
    	}
    	return true;
    }
    
    
    function showNextVisitDiv(input){
    	var divToShow = document.getElementById("nextVisitDivnextscheduledvisit");
    	$j(divToShow).removeClass("displayOff");
    	$j(divToShow).addClass("displayOn");
    	var nextVisitMessage = document.getElementById("nextVisitMessage");
    	$j(nextVisitMessage).text("");
    }
    
    function saveNewNextVisit(input){
    	var newValTextbox = document.getElementById("nextscheduledvisit");
    	if (newValTextbox.value != ""){
    		var divToShow = document.getElementById("nextVisitDivnextscheduledvisit");
    		MdrtbNextVisit.setNextVisitDate(newValTextbox.value,'${obj.patient.patientId}','${obj.location.locationId}', function(ret){
    			if (!ret){
    				alert("<spring:message code='DWRfailedtosetnextvisit' />");
    			} else {
    				$j(divToShow).removeClass("displayOn");
					$j(divToShow).addClass("displayOff");
					var nextVisitMessage = document.getElementById("nextVisitMessage");
					$j(nextVisitMessage).text("<spring:message code='mdrtb.successfullyupdated' />");
    			}
			});
    		
    	}
    }
    
    //this is a hack for IE css
    function setHeights(){
    	var tr = document.getElementById("firstTableRow");
		var trHeight = $j(tr).height() -50;
		var short = document.getElementById("shortStatusTable");
		var tall = document.getElementById("tallStatusTable");
		$j(short).height(trHeight);
		$j(tall).height(trHeight);
    }
    
    //Simple popup:
    
    	var $j = jQuery.noConflict();
	$j(document).ready(function(){ 		
		cssTables();		 
  		 var viewport = {
        o: function() {
            if (self.innerHeight) {
    			this.pageYOffset = self.pageYOffset;
    			this.pageXOffset = self.pageXOffset;
    			this.innerHeight = self.innerHeight;
    			this.innerWidth = self.innerWidth;
    		} else if (document.documentElement && document.documentElement.clientHeight) {
    			this.pageYOffset = document.documentElement.scrollTop;
    			this.pageXOffset = document.documentElement.scrollLeft;
    			this.innerHeight = document.documentElement.clientHeight;
    			this.innerWidth = document.documentElement.clientWidth;
    		} else if (document.body) {
    			this.pageYOffset = document.body.scrollTop;
    			this.pageXOffset = document.body.scrollLeft;
    			this.innerHeight = document.body.clientHeight;
    			this.innerWidth = document.body.clientWidth;
    		}
    		return this;
        },
        init: function(el) {
            $j(el).css("left",Math.round(viewport.o().innerWidth/2) + viewport.o().pageXOffset - Math.round($j(el).width()/2));
            $j(el).css("top",Math.round(viewport.o().innerHeight/2) + viewport.o().pageYOffset - Math.round($j(el).height()/2));
        }
    };
    $j(".simple_popup_info").each(function(){
        $j(this).css("display","none").siblings(".simple_popup").click(function(){
            $j(".simple_popup_div").remove();
            var strSimple = "<div class='simple_popup_div'><div class='simple_popup_inner'>";
            strSimple += $j(this).siblings(".simple_popup_info").html();
            strSimple += "<Br><Br><table class='popupTable'><tr>";
            if (showSubmit == true)
            strSimple += "<td nowrap ><p class='simple_close' nowrap>[ x ] <a href='#' onmouseup='javascript:clearOrder()'><spring:message code="mdrtb.submit" /></a>&nbsp;&nbsp;&nbsp;</p></td>";
            strSimple += "<td nowrap><p class='simple_close' nowrap>[ x ] <a href='#'><spring:message code="mdrtb.close" /></a><Br><br>&nbsp;</p></td></tr></table>";
            strSimple += "<Br></div></div>";
            $j("body").append(strSimple);
            viewport.init(".simple_popup_div");
            $j(".simple_close").click(function(){
                $j(".simple_popup_div").remove();
                $j(".resTableBodyLookup").empty();
                reasonTmp = "";
 				orderIdTmp = "";
 				closeDateTmp = "";
 				action = "";
 				showSubmit = true;
                return false;
           	 });
            	return false;
        	});
    	});
 	});	
    
    //simple popup cont... The following is for patient lookups:
	var savedRet;
    function loadPatients(input){
    	if (input.value.length > 3){
	    	MdrtbFindPatient.findPeople(input.value, dateFormat, false, function(ret){
   						savedRet = ret;
   						drawPatientTable(savedRet);
   			});
    	} else {
    		$j(".resTableBodyLookup").html("<Br>");
    	}
    }
    
    function drawPatientTable(savedRet){
    $j(".resTableBodyLookup").html("<Br>");
    	var retString = "<br>"; 
    	if (savedRet.length > 0)
    		retString += "<table class='findpatientpopup'><thead><Th><spring:message code="mdrtb.name" /></th><Th><spring:message code="mdrtb.surname" /></th><Th><spring:message code="mdrtb.treatmentsupporterbirthdate" /></th><Th><spring:message code="mdrtb.healthcentervillageofsupporter" /></th><Th><spring:message code="mdrtb.gender" /></th></thead><tbody id='findpatientpopupbody'>";
    	for (var i = 0; i < savedRet.length; i++){
    		var thisPatient = savedRet[i];
    		retString += "<tr><td><span class='displayOff'>" + i + "</span>" + thisPatient.givenName + "</td><td>" + thisPatient.familyName + "</Td><td>" + thisPatient.birthdateString + "</td><Td>" + thisPatient.village + "</Td><td>" + thisPatient.gender + "</td></tr>";
    	}
    	if (savedRet.lenght > 0) 
    		retString += "</tbody></table>"
    	$j(".resTableBodyLookup").html(retString);
    	//add click and onmouseover events and striping to tr's
    	addRowEvents();
    	
    }
    
    	var classTmp = "";
    function addRowEvents(){
		var tbody = document.getElementById('findpatientpopupbody');
		var trs = tbody.getElementsByTagName("tr");
		$j('table.findpatientpopup tbody tr:odd').addClass('oddRow');
  		$j('table.findpatientpopup tbody tr:even').addClass('evenRow');
   		$j('table.findpatientpopup tbody tr').mouseover(function(){
				mouseOver(this);refresh(this);
			});
		$j('table.findpatientpopup tbody tr').mouseout(function(){
			mouseOut(this); refresh(this);
		});
		$j('table.findpatientpopup tbody tr').click(function(){
			choosePatient(this);
		});
	}
	
	function choosePatient(input){
		if (input.firstChild.firstChild.innerHTML != null && input.firstChild.firstChild.innerHTML != 'undefined' && input.firstChild.firstChild.innerHTML !=''){
			var index = input.firstChild.firstChild.innerHTML;
			
			$j("#treatmentsupportergivenname").val(savedRet[index].givenName);
			$j("#treatmentsupporterfamilyname").val(savedRet[index].familyName);
			$j("#treatmentsupportergender").val(savedRet[index].gender);
			$j("#treatmentsupportervillage").val(savedRet[index].village);
			$j("#treatmentsupporterdob").val(savedRet[index].birthdateString);
			$j("#treatmentSupporterId").val(savedRet[index].personId);
		}
		$j(".simple_close").click();
	}
	
	function selectPatient(input){
		window.location='/openmrs/module/mdrtb/mdrtbPatientOverview.form?patientId=' + input + '&view=BAC';
	}
	function mouseOver(input){
		classTmp = this.className;
		input.className="rowMouseOver";
		refresh(this);	
	}
	function mouseOut(input){
		input.className = classTmp;
		$j('table.findpatientpopup tbody tr:odd').addClass('oddRow');
  		$j('table.findpatientpopup tbody tr:even').addClass('evenRow');
  		$j('table.findpatientpopup tbody tr:odd').addClass('oddRow');
  		$j('table.findpatientpopup tbody tr:even').addClass('evenRow');
		refresh(this);
    }
    	function refresh(input){
		if (input.className) input.className = input.className;
	}
    
    //treatment supporter
    var supportergivenname = '<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${!empty obj.treatmentSupporter.names}"><c:set var="stop" scope="page" value="0" /><c:forEach items="${obj.treatmentSupporter.names}" var="name" varStatus="varStatus"><c:if test="${stop==0}">${name.givenName}</c:if><c:set var="stop" scope="page" value="1" /></c:forEach></c:if></c:if>';
    var supporterfamilyname = '<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${!empty obj.treatmentSupporter.names}"><c:set var="stopfn" scope="page" value="0" /><c:forEach items="${obj.treatmentSupporter.names}" var="name" varStatus="varStatus"><c:if test="${stopfn==0}">${name.familyName}</c:if><c:set var="stopfn" scope="page" value="1" /></c:forEach></c:if></c:if>';
    var supportergender = '<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${obj.treatmentSupporter.gender == 'M'}">M</c:if><c:if test="${obj.treatmentSupporter.gender == 'F'}">F</c:if></c:if>';
    
    var supportervillage = '<c:if test="${!empty obj.treatmentSupporter}"><c:if test="${!empty obj.treatmentSupporter.addresses}"><c:set var="stopad" scope="page" value="0" /><c:forEach items="${obj.treatmentSupporter.addresses}" var="address" varStatus="varStatus"><c:if test="${stopad==0}">${address.cityVillage}</c:if><c:set var="stopad" scope="page" value="1" /></c:forEach></c:if></c:if>';
    var supporterdob = '<c:if test="${!empty obj.treatmentSupporter}"><openmrs:formatDate date="${obj.treatmentSupporter.birthdate}" format="${dateFormat}" /></c:if>';
    
    function toggleToDisabled(direction){
    		if (!direction){
	    		$j("#treatmentsupportergivenname").removeClass("disabled");
				$j("#treatmentsupporterfamilyname").removeClass("disabled");
				$j("#treatmentsupportergender").removeClass("disabled");
				$j("#treatmentsupportervillage").removeClass("disabled");
				$j("#treatmentsupporterdob").removeClass("disabled");
			} else {
				$j("#treatmentsupportergivenname").addClass("disabled");
				$j("#treatmentsupporterfamilyname").addClass("disabled");
				$j("#treatmentsupportergender").addClass("disabled");
				$j("#treatmentsupportervillage").addClass("disabled");
				$j("#treatmentsupporterdob").addClass("disabled");
			}
   			$j("#treatmentsupportergivenname").attr("disabled", direction);
			$j("#treatmentsupporterfamilyname").attr("disabled", direction);
			$j("#treatmentsupportergender").attr("disabled", direction);
			$j("#treatmentsupportervillage").attr("disabled", direction);
			$j("#treatmentsupporterdob").attr("disabled", direction);	
	}		
	
	function createNewPerson() {
		toggleToDisabled(false);
		$j('#treatmentSupporterAction').val("2");
        
        $j("#treatmentsupportergivenname").val("");
         $j("#treatmentsupporterfamilyname").val("");
          $j("#treatmentsupportergender").val("");
           $j("#treatmentsupportervillage").val("");
            $j("#treatmentsupporterdob").val("");
            
	}
	
	function editPerson() {
		toggleToDisabled(false);
		$j('#treatmentSupporterAction').val("3");
		$j("#treatmentsupportergivenname").val(supportergivenname);
         $j("#treatmentsupporterfamilyname").val(supporterfamilyname);
          $j("#treatmentsupportergender").val(supportergender);
        	
           $j("#treatmentsupportervillage").val(supportervillage);
            $j("#treatmentsupporterdob").val(supporterdob);
	}
    
</script>

<span style="text-align:right;">
	<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=mini|resultStyle=right:0|postURL=/openmrs/module/mdrtb/mdrtbPatientOverview.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
</span>
<div style="padding:2px;"></div>
<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>
<!-- <openmrs:portlet url="mdrtbHeaderBox" id="mdrtbHeaderBox" moduleId="mdrtb"  patientId="${obj.patient.patientId}"/>-->
<br><br>
		
		
<div id="R01SearchTabs">
			<ul>
			<li>&nbsp;</li>
			<li><a id="searchTab_status" href="#"
					onClick="changeSearchTab(this)"><spring:message
					code="mdrtb.status" /></a></li>
					
					<li><a id="searchTab_formEntry" href="#"
					onClick="changeSearchTab(this)"><spring:message
					code="mdrtb.formentry" /></a></li>
					
			<li><a id="searchTab_patientRegimen" href="#"
					onClick="changeSearchTab(this)"><spring:message
					code="mdrtb.patientregimen" /></a></li>
					
			<li><a id="searchTab_BAC" href="#"
					onClick="changeSearchTab(this)"><spring:message
					code="mdrtb.bacteriologies" /></a></li>
					
					<li><a id="searchTab_DST" href="#"
					onClick="changeSearchTab(this)"><spring:message
					code="mdrtb.dsts" /></a></li>

					<li><a id="searchTab_contacts" href="#"
					onClick="changeSearchTab(this)"><spring:message
					code="mdrtb.contacts" /></a></li>
					
					
					<openmrs:extensionPoint pointId="org.openmrs.mdrtb.mdrtbDashboardTab" type="html">
							<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
								<li>
									<a id="searchTab_${extension.tabId}" href="#" onclick="changeSearchTab(this)"><spring:message code="${extension.tabName}"/></a>
								</li>
							</openmrs:hasPrivilege>
					</openmrs:extensionPoint>

			</ul>
</div>
	
<div id="content" style="border-top: 1px grey solid; border-top: none; padding: 4px 5px 2px 10px;">
		
		<div  style="display:inline" id="searchTab_status_content"><bR>
			<openmrs:portlet url="mdrtbStatus" id="mdrtbStatus" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>		
		</div>
		
		
			<div  style="display:inline" id="searchTab_formEntry_content"><bR>
				<openmrs:portlet url="mdrtbFormsListPortlet" id="mdrtbFormsListPortlet" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>		
			</div>
		
		
		<div  style="display:inline" id="searchTab_patientRegimen_content"><bR>
			<openmrs:portlet url="mdrtbRegimen" id="mdrtbRegimen" moduleId="mdrtb"  patientId="${obj.patient.patientId}"/>
		</div>
		
		
		<div  style="display:inline;font-size:65%" id="searchTab_BAC_content"><br>
		&nbsp;&nbsp;&nbsp;&nbsp;<a style="font-size:125%;" href="mdrtbAddNewTestContainer.form?action=BAC&patientId=${obj.patient.patientId}"><spring:message code="mdrtb.addnewbacteriology" /></a>
		<bR><br>
		<spring:message code='mdrtb.mdrtbprogramstartdate' var='progString'/>
		<openmrs:globalProperty key="mdrtb.red_list" var="redList"/>
		<openmrs:globalProperty key="mdrtb.green_list" var="greenList"/>
		<openmrs:globalProperty key="mdrtb.yellow_list" var="yellowList"/>
		<spring:message code='mdrtb.samplecollectiondateLN' var='sampleCollectionDateStrLN'/>
		<c:if test="${!empty obj.obs}">
						<mdrtb:bacteriology
						observations="${obj.obs}"
						concepts="TUBERCULOSIS TREATMENT STARTED|TUBERCULOSIS PROPHYLAXIS STARTED|REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED|TUBERCULOSIS DRUG TREATMENT START DATE"
						obsGroupConcepts="TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT|TUBERCULOSIS CULTURE CONSTRUCT"
						resultConceptList="TUBERCULOSIS SMEAR RESULT|TUBERCULOSIS CULTURE RESULT"
						cssClass="widgetOut"
						columnHeaders="TUBERCULOSIS SMEAR RESULT|TUBERCULOSIS CULTURE RESULT"
						redList="${redList}"
						greenList="${greenList}"
						headerDescLeft="${sampleCollectionDateStrLN}"
						fillInBlankCells="true"
						stringForBlankCells=" "
						programWorkflowStatesToNotShow="NOT CONVERTED|NONE|STILL ON TREATMENT|ON TREATMENT"
						programNameReplacementString="${progString}"
						scanty="SCANTY"
						/>
		</c:if>	<br>
		</div>

		
		<div id="searchTab_DST_content" style="font-size:65%"><Br>
		&nbsp;&nbsp;&nbsp;&nbsp;<a style="font-size:125%;" href="mdrtbAddNewTestContainer.form?action=DST&patientId=${obj.patient.patientId}"><spring:message code="mdrtb.addnewdst" /></a>
		<bR><br>
		<c:if test="${!empty obj.obs}">
					<openmrs:globalProperty key="mdrtb.DST_drug_list" var="left_column_headers"/>
					<spring:message code='mdrtb.mcg/ml' var='concentrationLabelStr'/>
					<spring:message code='mdrtb.samplecollectiondate' var='sampleCollectionDateStr'/>
					<mdrtb:dst
						observations="${obj.obs}"
						concepts="TUBERCULOSIS TREATMENT STARTED|TUBERCULOSIS PROPHYLAXIS STARTED|REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED|TUBERCULOSIS DRUG TREATMENT START DATE"
						obsGroupConceptDST="TUBERCULOSIS DRUG SENSITIVITY TEST CONSTRUCT" 
						sputumCollectionDateConcept="SPUTUM COLLECTION DATE"
						obsGroupConcepts="TUBERCULOSIS DRUG SENSITIVITY TEST RESULT"
						resultConceptList="RESISTANT TO TUBERCULOSIS DRUG|SUSCEPTIBLE TO TUBERCULOSIS DRUG|INTERMEDIATE TO TUBERCULOSIS DRUG"
						columnHeaders="${left_column_headers}"
						cssClass="widgetOut" 
						redList="${redList}"
						greenList="${greenList}"
						yellowList="${yellowList}"
						headerDescTop="${sampleCollectionDateStr}"
						fillInBlankCells="false"
						concentrationLabel="${concentrationLabelStr}" 
						concentrationConceptId="${concentrationConceptId}"
					/>
		</c:if><br>
		</div>
		
		<div id="searchTab_contacts_content" style="font-size:80%"><Br>
			<a  href="/openmrs/module/mdrtb/mdrtbManageContacts.form?patientId=${obj.patient.patientId}"><spring:message code="mdrtb.managecontacts" /></a>
			<br>
			<Br>	
			<c:if test="${obj.contacts != null}">
			<table class="contactsTable" style="font-size:80%">
			<tr >
				<th></th>
				<th><spring:message code="mdrtb.name" /></th>
				<th><spring:message code="mdrtb.relationship" /></th>
				<th><spring:message code="mdrtb.latesttestresult" /></th>
				<th><spring:message code="mdrtb.address" /></th>
				<th><spring:message code="mdrtb.patienttype" /></th>		
			</tr>
			<c:if test="${empty obj.contacts}">
				<tr><td colspan="6" style="text-align:center"><i><spring:message code="mdrtb.none" /></i></td></tr>
			</c:if>
			<c:forEach items="${obj.contacts}" var="contact" varStatus="varStatus">
				<c:set var="rowClass" scope="page">
					<c:choose>
					<c:when test="${varStatus.index % 2 == 0}">contactsTableOdd</c:when><c:otherwise>contactsTableEven</c:otherwise></c:choose>
				</c:set>
				
				<Tr class="${rowClass}">
					<td>
						
					</td>
					<Td>
						<c:if test="${contact.isTBPatient}">
							<a href="/openmrs/module/mdrtb/mdrtbPatientOverview.form?patientId=${contact.person.personId}&view=STATUS">${contact.person.givenName} ${contact.person.familyName}</a>
						</c:if>
						<c:if test="${!contact.isTBPatient}">
							<c:if test="${contact.isPatient}">
								<a href="/openmrs/patientDashboard.form?patientId=${contact.person.personId}">${contact.person.givenName} ${contact.person.familyName}</a>
							</c:if>
						</c:if>
						<c:if test="${!contact.isTBPatient}">
							<c:if test="${!contact.isPatient}">
								${contact.person.givenName} ${contact.person.familyName}
							</c:if>
						</c:if>
						
						<c:if test="${!empty contact.mdrtbContactId}">
							<br>${contact.mdrtbContactId.value}	
						</c:if>
					</Td>
					
					<td>
						<c:if test="${contact.relationship.personA.personId == obj.patient.patientId}">
							${contact.relationship.relationshipType.bIsToA}
						</c:if>
						<c:if test="${contact.relationship.personB.personId == obj.patient.patientId}">
							${contact.relationship.relationshipType.aIsToB}
						</c:if>
					</td>
					
					<td>
						<c:if test="${!empty contact.testResult}">
							<spring:message code="mdrtb.result" />: ${contact.testResult.valueCoded.name.name}
							<Br><spring:message code="mdrtb.datelowercase" />: <openmrs:formatDate date="${contact.testResult.valueDatetime}" format="${dateFormat}" />
							<c:if test="${!empty contact.testType}">
								<br><spring:message code="mdrtb.typelowercase" />: ${contact.testType.valueCoded.name.shortName}
							</c:if>
						</c:if>
						<c:if test="${empty contact.testResult}">
							<spring:message code="mdrtb.notests" />
						</c:if>
					</td>
					<Td>
						<c:if test="${!empty contact.address.address1}">${contact.address.address1} </c:if> 
						<c:if test="${!empty contact.address.address2}">${contact.address.address2} <Br></c:if> 
						${contact.address.townshipDivision} ${contact.address.cityVillage}
						<Br>${contact.address.countyDistrict} ${contact.address.region}
						<Br>${contact.phone.valueText}
					</Td>
					<td>
						<table>
							<c:if test="${!empty contact.isTBPatient}">
								<c:if test="${contact.isTBPatient}">
									<Tr><Td><spring:message code="mdrtb.istbpatient" /></Td><td><spring:message code="mdrtb.yes" /></td></Tr>
								</c:if>
								<c:if test="${contact.isTBPatient == false}">
									<tr><Td><spring:message code="mdrtb.istbpatient" /></Td><td><spring:message code="mdrtb.no" /></td></tr>
								</c:if>
							</c:if>
							<c:if test="${!empty contact.isPatient}">
								<c:if test="${contact.isPatient}">
									<tr><td><spring:message code="mdrtb.ispatient" /></td><td><spring:message code="mdrtb.yes" /></td></tr>
								</c:if>
								<c:if test="${contact.isPatient == false}">
									<tr><td><spring:message code="mdrtb.ispatient" /></td><td><spring:message code="mdrtb.no" /></td></tr>
								</c:if>
							</c:if>
							<c:if test="${!empty contact.knownMdrtbContact}">
								<c:if test="${contact.knownMdrtbContact.valueNumeric == 0}">
									<tr><td><spring:message code="mdrtb.knownmdr" />?</td><td id="isKnownMdr_${contact.person.personId}"><spring:message code="mdrtb.no" /></td></tr>
								</c:if>
								<c:if test="${contact.knownMdrtbContact.valueNumeric == 1}">
									<tr><td><spring:message code="mdrtb.knownmdr" />?</td><td id="isKnownMdr_${contact.person.personId}"><spring:message code="mdrtb.yes" /></td></tr>
								</c:if>
							</c:if>
						</table>
					</td>
				</Tr>
			</c:forEach>
			</table>
			</c:if>
			<bR><Br>
		</div>
		
		<openmrs:extensionPoint pointId="org.openmrs.mdrtb.mdrtbDashboardTab" type="html">
		<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
			<div id="searchTab_${extension.tabId}_content" style="display:none;">
				<c:choose>
					<c:when test="${extension.portletUrl == '' || extension.portletUrl == null}">
						portletId is null: '${extension.extensionId}'
					</c:when>
					<c:otherwise>
					
						<openmrs:extensionPoint pointId="org.openmrs.mdrtb.mdrtbDashboardTab.${extension.tabId}TabHeader" type="html" parameters="patientId=${patient.patientId}" />
						<openmrs:portlet url="${extension.portletUrl}" id="${extension.tabId}" moduleId="${extension.moduleId}"/>
						
					</c:otherwise>
				</c:choose>
			</div>
		</openmrs:hasPrivilege>
		</openmrs:extensionPoint>
		
		
</div>

<bR><br>
<script>
		<c:if test="${empty view}">
			changeSearchTab('searchTab_status');
		</c:if>	
		<c:if test="${!empty view}">
			<c:if test='${view == "DST"}'>
				changeSearchTab('searchTab_DST');
			</c:if>
			<c:if test='${view == "BAC"}'>
				changeSearchTab('searchTab_BAC');
			</c:if>
			<c:if test='${view == "REG"}'>
				changeSearchTab('searchTab_patientRegimen');
			</c:if>
			<c:if test='${view == "STATUS"}'>
				changeSearchTab('searchTab_status');
			</c:if>
			<c:if test='${view == "CONTACTS"}'>
				changeSearchTab('searchTab_contacts');
			</c:if>
			<c:if test='${view == "FORM"}'>
				changeSearchTab('searchTab_formEntry');
			</c:if>
		</c:if>
			setHeights();
</script>






<%@ include file="mdrtbFooter.jsp"%>