<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>

<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>


<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- JQUERY FOR THIS PAGE -->
<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){
		// handle clicking on a row
		$j('.evenRow,.oddRow').click(function () {
			window.location='${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=' + $j(this).children('input').attr('value');
		});

		// handle highlighting a row
		$j('.evenRow,.oddRow').mouseover(function () {
			$j(this).attr('class','searchHighlight');
		});

		// handle the mouse outs
 		$j('.evenRow').mouseout(function () {
			$j(this).attr('class','evenRow');
 		});
 		$j('.oddRow').mouseout(function () {
			$j(this).attr('class','oddRow');
 		});
	});
-->
</script>
<!-- END JQUERY -->

<!-- START PAGE DIV -->
<div align="center">

<!--  EDIT/ADD PATIENT BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.similarPatients" text="Similar Patients"/></b>
<div class="box" style="margin:0px;">

<spring:message code="Person.search.similarPersonInstructions"/>

<br/><br/>

<table cellpadding="3" cellspacing="0">
<tr>
<th><spring:message code="Patient.identifier"/></th>
<th><spring:message code="PersonName.givenName"/></th>
<th><spring:message code="PersonName.middleName"/></th>
<th><spring:message code="PersonName.familyName"/></th>
<th><spring:message code="Person.age"/></th>
<th><spring:message code="Person.gender"/></th>
<th>&nbsp;</th>
<th><spring:message code="Person.birthdate"/></th>
</tr>
<c:forEach var="patient" items="${patients}" varStatus="i">
<tr class="${i.count % 2 == 0 ? 'evenRow' : 'oddRow'}">
<input type="hidden" value="${patient.id}"/>
<td>${patient.patientIdentifier}</td>
<td>${patient.givenName}</td>
<td>${patient.middleName}</td>
<td>${patient.familyName}</td>
<td>${patient.age}</td>
<td>${patient.gender}</td>
<td>${patient.birthdateEstimated ? '~' : ''}</td>
<td><openmrs:formatDate date="${patient.birthdate}" format="${_dateFormatDisplay}"/></td>
</tr>
</c:forEach>
</table>

<br/>

<form action="mdrtbEditPatient.form" method="GET">
<input type="hidden" name="addAge" value="${addAge}"/>
<input type="hidden" name="addName" value="${addName}"/>
<input type="hidden" name="addBirthdate" value="${addBirthdate}"/>
<input type="hidden" name="addGender" value="${addGender}"/>
<input type="hidden" name="successURL" value="${successURL}"/>
<input type="hidden" name="patientId" value="-1"/>
<input type="hidden" name="skipSimilarCheck" value="1"/>

<button type="submit"><spring:message code="Person.search.similarPersonNotOnList"/></button>
<button type="reset" onclick="history.back()"><spring:message code="general.back"/></button>
</form>

</div>

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>