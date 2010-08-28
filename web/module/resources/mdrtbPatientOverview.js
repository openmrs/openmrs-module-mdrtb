	var $j = jQuery.noConflict();
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
   
	$j(document).ready(function(){ 				 
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
		window.location='${pageContext.request.contextPath}/module/mdrtb/summary/summary.form?patientId=' + input + '&view=BAC';
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