<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/mdrtb/regimen/manageRegimens.form"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/mdrtb/drugOrders.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/drugOrders.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patient.patientId}"/>

<c:set var="tbTxHistory" value="${regimenHistoryGroups['tb']}"/>

<b class="boxHeader">
	<spring:message code="mdrtb.mdrRegimenSummary" text="MDR-TB Treatment Summary"/>
</b>
<div class="box">
	<mdrtb:regimenHistory history="${tbTxHistory}" dateFormat="dd/MMM/yyyy" cssClass="regimenHistory" futureCssClass="future" invert="true" timeDescending="true"/>
</div>
<br/>

<table width="100%"><tr><td valign="top" width="50%">
	<b class="boxHeader"><spring:message code="mdrtb.activeOrders" text="Active Orders"/></b>
	<div class="box">
		<mdrtb:regimenPortlet id="activeOrders" patientId="${patient.patientId}" url="activeOrderPortlet"/>
	</div>
	<br/>
	<b class="boxHeader"><spring:message code="mdrtb.completedOrders" text="Completed Orders"/></b>
	<div class="box">
		<mdrtb:regimenPortlet id="completedOrders" patientId="${patient.patientId}" url="completedOrderPortlet"/>
	</div>
</td>
<td valign="top" width="50%">
	<b class="boxHeader"><spring:message code="mdrtb.orderHistory" text="Order History"/></b>
	<div class="box">
		<mdrtb:regimenPortlet id="orderHistory" patientId="${patient.patientId}" url="orderHistoryPortlet"/>
	</div>
</td></tr></table>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>
