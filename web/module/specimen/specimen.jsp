<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!-- TODO: figure out if I should be using concept.name or concept.name.name or whatever -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- JQUERY FOR THIS PAGE -->
<!-- TODO: is this the right link for jquery? -->
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>

<script type="text/javascript"><!--

	function hideAll(){
		$('.addBox').hide();
		$('.editBox').hide();
		$('.detailBox').hide();
	}

	$(document).ready(function(){

		// event handlers to hide and show custom evaluator text box
		$('#editSpecimen').click(function(){
			$('#details_specimen').hide();
			$('#edit_specimen').show();
		});

		$('#cancelSpecimen').click(function(){
			// TODO: add something here to reset all field values
			$('#edit_specimen').hide();
			$('#details_specimen').show();
		});

		// event handlers to display add boxes
		$('#add').click(function(){
			hideAll();
			$('#add_' + $('#addSelect').attr('value')).show()
		});

		$('#cancelAdd').click(function(){
		 	hideAll();
		});
		
		// event handler to display view detail boxes
		$('.view').click(function(){
			hideAll();
			$('#details_' + this.id).show();
		});

		// event handler to display edit detail boxes
		$('.edit').click(function(){
			hideAll();
			$('#edit_' + this.id).show();
		});

		// event handler to cancel an edit
		$('.cancelEdit').click(function(){	
			hideAll();
			$('#details_' + this.id).show();
		});
 	});
-->
</script>
<!-- END JQUERY -->

<!--  SPECIMEN SECTION -->

<div id="details_specimen">

<b class="boxHeader">Sample Details<span id="editSpecimen" style="position: absolute; right:25px;"><u>edit</u></span></b>
<table cellspacing="0" cellpadding="0" class="box">

<tr>
<td><nobr>Sample Id:</nobr></td><td><nobr>${specimen.identifier}</nobr></td>
<td><nobr>Collected By:</nobr></td><td><nobr>${specimen.provider.familyName}</nobr></td> <!-- TODO: obviously, need to find out proper way to handle names -->
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Sample Type:</td><td><nobr>${specimen.type.name.name}</nobr></td> 
<td><nobr>Location Collected:</td><td><nobr>${specimen.location.name}</nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<!-- TODO: how should comments wrap properly??? -->
<tr>
<td><nobr>Date Collected:</td><td><nobr><openmrs:formatDate date="${specimen.dateCollected}"/></nobr></td>
<td><nobr>Comments:</td><td>${specimen.comments}</td>
<td width="100%">&nbsp;</td>
</tr>

</table>

</div>
<!--  END OF SPECIMEN SECTION -->

<!--  EDIT SPECIMEN SECTION -->

<div id="edit_specimen"  style="display:none">

<form:form modelAttribute="specimen">
<form:errors path="*" cssClass="error" />

<b class="boxHeader">Sample Details</b>
<table cellspacing="0" cellpadding="0"  class="box">

<!-- TODO localize all text -->

<!-- TODO is answerConcept.name the correct parameter? -->
<tr>
<td><nobr>Sample ID:</nobr></td>
<td><form:input path="identifier" size="10" /><form:errors path="identifier" cssClass="error" /></td>
<td><nobr>Collected By:</nobr></td>
<td><form:select path="provider" multiple="false">
	<form:options items="${providers}" itemValue="id" itemLabel="names[0].familyName" />
</form:select></td>
<td width="100%">&nbsp;</td>
</tr>
 
<tr>
<td><nobr>Sample Type:</nobr></td>
<td><form:select path="type" multiple="false">
	<form:options items="${types}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
<td><nobr>Location Collected:</nobr></td>
<td><form:select path="location" multiple="false">
	<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
</form:select>	
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Date Collected:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/><form:errors path="dateCollected" cssClass="error" /></nobr></td>
<td><nobr>Comments:</nobr></td>
<td><form:textarea path="comments" cols="100" rows="2"/></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td colspan="5" align="left"><button type="submit">Save</button><button type="reset" id="cancelSpecimen">Cancel</button></td>
</tr>

</table>

</form:form>

</div>
<!-- END OF EDIT SPECIMEN SECTION -->

<br/>

<div id="tests" style="position:relative"> 
<b class="boxHeader">Tests</b>

<!--  TODO: remove this if I don't use it <table cellspacing="0" class="box" border="2"><tr><td> --> <!-- a one-cell table used to create the box around the other data -->


<!-- TEST SUMMARY SECTION -->

<div id="summary" style="position:absolute; left:20px; top:30px; width:400px">
<c:forEach var="test" items="${specimen.tests}">

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/>
<c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>
<span style="position: absolute; right:25px;"><span id="${test.id}" class="view"><u>view</u></span>&nbsp;&nbsp;<span id="${test.id}" class="edit"><u>edit</u></span>&nbsp;&nbsp;<a href="delete.form?testId=${test.id}&specimenId=${specimen.id}">delete</a></span>
</b>

<table style="width:396px;" cellspacing="0" cellpadding="0"  class="box">

<tr>
<td>Status:</td><td>${test.status}</td>
</tr>
<tr>
<td>Result:</td><td>${test.result.name.name}</td>
</tr>
<tr>
</table> 

<br/>

</c:forEach>

Add a new Lab Test:
<span>
<select id="addSelect">
<option value="smear">Smear</option>
<option value="culture">Culture</option>
</select>
<button id="add" type="button">Add</button>
</span>

</div> <!--  end summary div -->

<!-- END OF TEST SUMMARY SECTION -->


<c:forEach var="test" items="${specimen.tests}">

<!--  TEST DETAILS SECTION -->

<div id="details_${test.id}" class="detailBox" style="position:absolute; left:450px; top:30px; display:none">

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/>
<c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Detail View
<span id="${test.id}" class="edit" style="position: absolute; right:25px;"><u>edit</u></span></b>
<table cellpadding="0">
<tr>
<td><nobr>Accession #:</nobr></td><td><nobr>${test.accessionNumber}</nobr></td>
<td><nobr>Date ordered:</nobr></td><td><nobr><openmrs:formatDate date="${test.dateOrdered}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Laboratory:</nobr></td><td><nobr>${test.lab}</nobr></td>
<td><nobr>Date sample received:</nobr></td><td><nobr><openmrs:formatDate date="${test.dateReceived}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Method:</nobr></td><td><nobr>${test.method.name.name}</nobr></td>
<td><nobr>Date started:</nobr></td><td><nobr><openmrs:formatDate date="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Result:</nobr></td><td><nobr>${test.result.name.name}</nobr></td>
<td><nobr>Date completed:</nobr></td><td><nobr><openmrs:formatDate date="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td><td><nobr>${test.organismType.name.name}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr>Comments:</nobr></td><td colspan="3"><nobr>${test.comments}</nobr></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

</div> <!-- end of details div -->

<!-- END OF TEST DETAILS SECTION -->

<!-- EDIT TESTS SECTION -->

<div id="edit_${test.id}" class="editBox" style="position:absolute; left:450px; top:30px; display:none">

<!--  TODO: how do i bind errors to this? -->
<!-- TODO: form id should be specified based on test type; get rid of enum, just use a String getTestType? -->

<form id="${test.testType}" action="specimen.form?${test.testType}Id=${test.id}&specimenId=${specimen.id}" method="post">

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/>
<c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Edit View</b>
<table cellpadding="0">

<tr>
<td><nobr>Accession #:</nobr></td>
<td><input type="text" id="accessionNumber" name="accessionNumber" value="${test.accessionNumber}"/></td>
<td><nobr>Date ordered:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue="${test.dateOrdered}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td>Laboratory:</td>
<td><select id="lab" name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == test.lab}">selected</c:if> >${location.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Date sample received:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateReceived" startValue="${test.dateReceived}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Method:</nobr></td>
<td><select id="method" name="method">
<c:forEach var="method" items="${test.testType eq 'smear'? smearMethods : cultureMethods}">
<option value="${method.answerConcept.id}" <c:if test="${method.answerConcept == test.method}">selected</c:if> >${method.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Date started:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Results:</nobr></td>
<td><select id="result" name="result">
<c:forEach var="result" items="${test.testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td><nobr>Date completed:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select id="organismType" name="organismType">
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}" <c:if test="${organismType.answerConcept == test.organismType}">selected</c:if> >${organismType.answerConcept.name}</option>
</c:forEach></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr>Comments:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" id="comments" name="comments">${test.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<button type="submit">Save</button><button type="reset" id="${test.id}" class="cancelEdit">Cancel</button>

</form>

</div>

</c:forEach>

<!--  END OF EDIT TESTS SECTION -->

<!-- ADD TEST SECTION -->

<c:forEach var="type" items="${testTypes}">

<div id="add_${type}" class="addBox" style="position:absolute; left:450px; top:30px; display:none">

<form id="${type}" action="specimen.form?${type}Id=-1&specimenId=${specimen.id}" method="post">

<b class="boxHeader"><spring:message code="mdrtb.${type}"/>: Add</b>
<table cellpadding="0">

<tr>
<td><nobr>Accession #:</nobr></td>
<td><input type="text" id="accessionNumber" name="accessionNumber"/></td>
<td><nobr>Date ordered:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td>Laboratory:</td>
<td><select id="lab" name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}">${location.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Date sample received:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateReceived" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Method:</nobr></td>
<td><select id="method" name="method">
<c:forEach var="method" items="${type eq 'smear'? smearMethods : cultureMethods}">
<option value="${method.answerConcept.id}">${method.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Date started:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Results:</nobr></td>
<td><select id="result" name="result">
<c:forEach var="result" items="${type eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}">${result.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td><nobr>Date completed:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${type eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select id="organismType" name="organismType">
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}">${organismType.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr>Comments:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" id="comments" name="comments"></textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<button type="submit">Save</button><button id="cancelAdd" type="reset">Cancel</button>
</form>
</div>

</c:forEach> 

<!-- END ADD TEST SECTION -->

<!--  </td></tr></table> -->

</div> <!-- END OF TEST DIV -->

</body>
</html>