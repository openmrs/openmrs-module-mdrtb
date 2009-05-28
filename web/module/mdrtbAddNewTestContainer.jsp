<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<style><%@ include file="resources/mdrtb.css"%></style>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script>
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
    var encounterProviderMap = {<c:forEach items="${obj.encounters}" var="encounter" varStatus="varStatus">${encounter.encounterId}:"${encounter.provider.userId}"<c:if test="${!varStatus.last}">,</c:if></c:forEach>};
    
    
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
		
		
</script>
<style><%@ include file="resources/date_input.css"%></style>
<div class="absolute">
<h2><spring:message code="mdrtb.title" /></h2>
</div>
<div id="content">
<form method="post">
		
				<c:if test="${!empty view}">
					<c:if test='${view == "DST"}'>
					<div id="searchTab_DST_content">
						<openmrs:portlet id="mdrtbAddDST" url="mdrtbAddDST" parameters="" moduleId="mdrtb"/>
					</div>
					</c:if>
					
					<c:if test='${view == "BAC"}'>
					<div id="searchTab_Smear_content">
						<openmrs:portlet id="mdrtbAddBacteriology" url="mdrtbAddBacteriology" parameters="" moduleId="mdrtb"/>
					</div>
					</c:if>

						
				</c:if>
</form>		
</div>
	

<%@ include file="mdrtbFooter.jsp"%>