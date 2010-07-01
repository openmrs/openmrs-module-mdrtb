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

	// hides all add, edit, and view details boxes
	function hideDisplayBoxes(){
		$('.addBox').hide();
		$('.editBox').hide();
		$('.detailBox').hide();
	}

	// hides all view, edit, and add elements (used to stop used from navigating away from an edit)
	function hideViewEditAddLinks() {
		$('.view').fadeOut();  // hide all the view links
		$('.edit').fadeOut();  // hide all the edit tests links
		$('.delete').fadeOut(); // hide all the delete links 
		$('#editSpecimen').fadeOut(); // hide the edit specimen link
		$('#add').fadeOut(); // hide the "add a test" selector
	}

	// shows all view, edit, and add elements (called when an edit is complete)
	function showViewEditAddLinks() {
		$('.view').fadeIn();  // show all the view links
		$('.edit').fadeIn();  // show all the edit tests links
		$('.delete').fadeIn(); // show all the delete links 
		$('#editSpecimen').fadeIn(); // show the edit specimen link
		$('#add').fadeIn(); // show the "add a test" selector
	}
	
	$(document).ready(function(){

		// show the proper detail windows if it has been specified
		if ("${testId}" != "-1") {
			$('#details_' + ${testId}).show();
		}
		
		// event handlers to hide and show custom evaluator text box
		$('#editSpecimen').click(function(){
			hideViewEditAddLinks();
			$('#details_specimen').hide();  // hide the specimen details box
			$('#edit_specimen').show();  // show the edit speciment box
		});

		$('#cancelSpecimen').click(function(){
			showViewEditAddLinks();
			$('#edit_specimen').hide();  // hide the edit specimen box
			$('#details_specimen').show();  // show the specimen details box
		});

		// event handlers to display add boxes
		$('#addButton').click(function(){
			hideDisplayBoxes();
			hideViewEditAddLinks();
			$('#add_' + $('#addSelect').attr('value')).show(); // show the proper add a test box
		});

		// TODO: figure out why "cancelAdd" as an id name isn't working
		// TODO: some sort of precendence issue?
		$('.cancelAdd').click(function(){
			hideDisplayBoxes();
			showViewEditAddLinks();
		});

		// event handler to display view detail boxes
		$('.view').click(function(){
			hideDisplayBoxes();
			$('#details_' + this.id).show();  // show the selected details box
		});

		// event handler to display edit detail boxes
		$('.edit').click(function(){
			hideDisplayBoxes();
			hideViewEditAddLinks();
			$('#edit_' + this.id).show();  // show the selected edit box
		});

		// event handler to cancel an edit
		$('.cancelEdit').click(function(){	
			hideDisplayBoxes();
			showViewEditAddLinks();
			$('#details_' + this.id).show(); // display the details box for the test that was just being edited
		});

		// event handles to hide/show bacilli and colonies selector, and reset the value if needed
		$('.result').change(function() {
				if ($(this).attr('value') == ${scanty.id}) {
					// show the bacilli or colonies row in the same div as this element
					$(this).closest('div').find('.bacilli').show();
					$(this).closest('div').find('.colonies').show();
				}
				else {
					// hide the bacilli or colonies row in the same div as this element,
					// then find the bacilli/colonies input element and it's value to empty
					$(this).closest('div').find('.bacilli').hide().find('#bacilli').attr('value','');
					$(this).closest('div').find('.colonies').hide().find('#colonies').attr('value','');
				}
		});
 	});
-->
</script>
<!-- END JQUERY -->

<!--  SPECIMEN SECTION -->

<div id="details_specimen">

<b class="boxHeader">Sample Details<span style="position: absolute; right:25px;"><a href="#" id="editSpecimen">edit</a></span></b>
<div class="box">
<table cellspacing="0" cellpadding="0">

<tr>
<td><nobr>Sample Id:</nobr></td><td><nobr>${specimen.identifier}</nobr></td>
<td><nobr>Collected By:</nobr></td><td><nobr>${specimen.provider.personName}</nobr></td> <!-- TODO: obviously, need to find out proper way to handle names -->
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
</div>
<!--  END OF SPECIMEN SECTION -->

<!--  EDIT SPECIMEN SECTION -->

<div id="edit_specimen"  style="display:none">

<form name="specimen" action="specimen.form?specimenId=${specimen.id}" method="post">

<b class="boxHeader">Sample Details</b>
<div class="box">
<table cellspacing="0" cellpadding="0">

<!-- TODO localize all text -->

<!-- TODO is answerConcept.name the correct parameter? -->
<tr>
<td><nobr>Sample ID:</nobr></td>
<td><input type="text" size="10" name="identifier" value="${specimen.identifier}"/></td>
<td><nobr>Collected By:</nobr></td>
<td>
<select name="provider">
<c:forEach var="provider" items="${providers}">
<option value="${provider.id}" <c:if test="${specimen.provider == provider}">selected</c:if> >${provider.personName}</option>
</c:forEach>
</select>
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Sample Type:</nobr></td>
<td>
<select name="type">
<option value=""></option>
<c:forEach var="type" items="${types}">
<option value="${type.answerConcept.id}" <c:if test="${specimen.type == type.answerConcept}">selected</c:if> >${type.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Location Collected:</nobr></td>
<td>
<select name="location">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == specimen.location}">selected</c:if> >${location.name}</option>
</c:forEach>
</select>	
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Date Collected:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/></nobr></td>
<td><nobr>Comments:</nobr></td>
<td><textarea name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td colspan="5" align="left"><button type="submit">Save</button><button type="reset" id="cancelSpecimen">Cancel</button></td>
</tr>

</table>

</form>
</div>
</div>
<!-- END OF EDIT SPECIMEN SECTION -->

<br/>

<div id="tests" style="position:relative"> 
<b class="boxHeader">Tests</b>

<!-- TEST SUMMARY SECTION -->
<div id="summary" style="position:absolute; left:20px; top:30px; width:400px">

<span id="add"  style="font-size:0.9em">
Add a new Lab Test:
<select id="addSelect">
<c:forEach var="test" items="${testTypes}">
<option value="${test}"><spring:message code="mdrtb.${test}"/></option>
</c:forEach>
</select>
<button id="addButton" type="button">Add</button>
</span>

<br/><br/>

<c:forEach var="test" items="${specimen.tests}">

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if><span style="position: absolute; right:25px;"><a href="#" id="${test.id}" class="view">view</a></span></b>
<div class="box">
<table style="width:396px;" cellspacing="0" cellpadding="0">

<tr>
<td>Status:</td><td>${test.status}</td>
</tr>

<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<tr>
<td>Result:</td><td>${test.result.name.name}</td>
</tr>
</c:if>

</table> 
</div>

<br/>

</c:forEach>

</div> <!--  end summary div -->

<!-- END OF TEST SUMMARY SECTION -->


<c:forEach var="test" items="${specimen.tests}">

<!--  TEST DETAILS SECTION -->

<div id="details_${test.id}" class="detailBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em">

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Detail View<span style="position: absolute; right:30px;"><a href="#" id="${test.id}" class="edit">edit</a>&nbsp;&nbsp;<a href="delete.form?testId=${test.id}&specimenId=${specimen.id}" class="delete" onclick="return confirm('Are you sure you want to delete this test?')">delete</a></span></b>
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
<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<td><nobr>Result:</nobr></td><td><nobr>${test.result.name.name}</nobr></td>
</c:if>
<c:if test="${test.testType eq 'dst'}">
<td><nobr>Direct/Indirect:</nobr></td><td><nobr>${test.direct ? 'Direct' : 'Indirect'}</nobr></td>
</c:if>
<td><nobr>Date completed:</nobr></td><td><nobr><openmrs:formatDate date="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'smear' && test.result == scanty}">
<tr>
<td><nobr># of Bacilli:</nobr></td><td><nobr>${test.bacilli}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture' && test.result == scanty}">
<tr>
<td><nobr># of Colonies</nobr></td><td><nobr>${test.colonies}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td><td><nobr>${test.organismType.name.name}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'dst'}">
<tr>
<td><nobr>Colonies in control:</nobr></td><td><nobr>${test.coloniesInControl}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr>Comments:</nobr></td><td colspan="3"><nobr>${test.comments}</nobr></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${test.testType eq 'dst'}">
<br/>
<table cellpadding="0">
<tr>
<td><u>Drug</u></td><td><u>Concentration</u></td><td><u>Result</u></td><td><u>Colonies</u></td>
</tr>
<c:forEach var="drugType" items="${drugTypes}">
<tr>
<td>${drugType.drug.name}</td>
<td>${drugType.concentration}</td>
<c:set var="flag" value="1"/>
<c:forEach var="result" items="${test.results}">
<c:if test="${result.drug == drugType.drug && result.concentration == drugType.concentration}">
<td>${result.result.name}</td>
<td>${result.colonies}</td>
<c:set var="flag" value="0"/> <!-- so that we know we don't need to print the empty cells -->
</c:if>
</c:forEach>
<c:if test="${flag == 1}">
<td colspan="2">&nbsp;</td>
</c:if>
</tr>
</c:forEach>
</table>
</c:if>
<!-- end of the DST table -->

</div> <!-- end of details div -->

<!-- END OF TEST DETAILS SECTION -->

<!-- EDIT TESTS SECTION -->

<div id="edit_${test.id}" class="editBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em"">

<!--  TODO: how do i bind errors to this? -->
<!-- TODO: form id should be specified based on test type; get rid of enum, just use a String getTestType? -->

<form name="${test.testType}" action="specimen.form?${test.testType}Id=${test.id}&specimenId=${specimen.id}" method="post">

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Edit View</b>
<table cellpadding="0">

<tr>
<td><nobr>Accession #:</nobr></td>
<td><input type="text" name="accessionNumber" value="${test.accessionNumber}"/></td>
<td><nobr>Date ordered:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue="${test.dateOrdered}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td>Laboratory:</td>
<td><select name="lab">
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
<td><select name="method">
<option value=""></option>
<c:forEach var="method" items="${test.testType eq 'smear'? smearMethods : (test.testType eq 'culture' ? cultureMethods : dstMethods)}">
<option value="${method.answerConcept.id}" <c:if test="${method.answerConcept == test.method}">selected</c:if> >${method.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Date started:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<td><nobr>Results:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${test.testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
</c:if>

<c:if test="${test.testType eq 'dst'}">
<td><nobr>Direct/Indirect:</nobr></td>
<td><select name="direct">
<option value=""></option>
<option <c:if test="${test.direct}">selected </c:if>value="1">Direct</option>
<option <c:if test="${!test.direct}">selected </c:if>value="0">Indirect</option>
</select></td>
</c:if>

<td><nobr>Date completed:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'smear'}">
<tr class="bacilli" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr># of Bacilli:</nobr></td>
<td><input type="text" name="bacilli" value="${test.bacilli}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr class="colonies" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr># of Colonies:</nobr></td>
<td><input type="text" name="colonies" value="${test.colonies}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select name="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}" <c:if test="${organismType.answerConcept == test.organismType}">selected</c:if> >${organismType.answerConcept.name}</option>
</c:forEach></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'dst'}">
<tr>
<td><nobr>Colonies in control:</nobr></td>
<td><input type="text" name="coloniesInControl" value="${test.coloniesInControl}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr>Comments:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" name="comments">${test.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${test.testType eq 'dst'}">
<br/>
<table cellpadding="0">

<tr>
<td><u>Drug</u></td><td><u>Concentration</u></td><td><u>Result</u></td><td><u>Colonies</u></td>
</tr>

<c:set var="i" value="0"/>
<c:forEach var="drugType" items="${drugTypes}">

<c:set var="flag" value="1"/>
<tr>
<c:forEach var="result" items="${test.results}" varStatus="j">
<c:if test="${result.drug == drugType.drug && result.concentration == drugType.concentration}">
<td>${drugType.drug.name}</td>
<td>${drugType.concentration}</td>
<td><select name="results[${j.count-1}].result">
<option value=""></option>
<c:forEach var="possibleResult" items="${dstResults}">
<option value="${possibleResult.id}" <c:if test="${possibleResult == test.results[j.count-1].result}">selected</c:if> >${possibleResult.name}</option>
</c:forEach></td>
</select>
</td>
<td><input type="text" name="results[${j.count-1}].colonies" value="${result.colonies}"/></td>
<c:set var="flag" value="0"/> <!-- so that we know we don't need to print the empty inputs -->
</c:if>
</c:forEach>

<c:if test="${flag == 1}">
<td>${drugType.drug.name}<input type="hidden" name="addDst${i}.drug" value="${drugType.drug.id}"/></td>
<td>${drugType.concentration}<input type="hidden" name="addDst${i}.concentration" value="${drugType.concentration}"/></td>
<td><select name="addDst${i}.result">
<option value=""></option>
<c:forEach var="possibleResult" items="${dstResults}">
<option value="${possibleResult.id}">${possibleResult.name}</option>
</c:forEach></td>
</select>
</td>
<td><input type="text" name="addDst${i}.colonies" value=""/></td>
<c:set var="i" value="${i+1}"/>
</c:if>

</tr>
</c:forEach>
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit">Save</button><button type="reset" id="${test.id}" class="cancelEdit">Cancel</button>

</form>

</div>

</c:forEach>

<!--  END OF EDIT TESTS SECTION -->

<!-- ADD TEST SECTION -->

<c:forEach var="type" items="${testTypes}">

<div id="add_${type}" class="addBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em"">

<form name="${type}" action="specimen.form?${type}Id=-1&specimenId=${specimen.id}" method="post">

<b class="boxHeader"><spring:message code="mdrtb.${type}"/>: Add</b>
<table cellpadding="0">

<tr>
<td><nobr>Accession #:</nobr></td>
<td><input type="text" name="accessionNumber"/></td>
<td><nobr>Date ordered:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td>Laboratory:</td>
<td><select name="lab">
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
<td><select name="method">
<option value=""></option>
<c:forEach var="method" items="${type eq 'smear'? smearMethods : (type eq 'culture' ? cultureMethods : dstMethods)}">
<option value="${method.answerConcept.id}">${method.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Date started:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<c:if test="${type eq 'smear' || type eq 'culture'}">
<td><nobr>Results:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${type eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}">${result.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
</c:if>

<c:if test="${type eq 'dst'}">
<td><nobr>Direct/Indirect:</nobr></td>
<td><select name="direct">
<option value=""></option>
<option value="1">Direct</option>
<option value="0">Indirect</option>
</select></td>
</c:if>

<td><nobr>Date completed:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${type eq 'smear'}">
<tr class="bacilli" style="display:none;">
<td><nobr># of Bacilli:</nobr></td>
<td><input type="text" name="bacilli" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'culture'}">
<tr class="colonies" style="display:none;">
<td><nobr># of Colonies:</nobr></td>
<td><input type="text" name="colonies" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>


<c:if test="${type eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select name="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}">${organismType.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'dst'}">
<tr>
<td><nobr>Colonies in control:</nobr></td>
<td><input type="text" name="coloniesInControl" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr>Comments:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" name="comments"></textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${type eq 'dst'}">
<br/>
<table cellpadding="0">

<tr>
<td><u>Drug</u></td><td><u>Concentration</u></td><td><u>Result</u></td><td><u>Colonies</u></td>
</tr>

<c:set var="i" value="0"/>
<c:forEach var="drugType" items="${drugTypes}">
<tr>
<td>${drugType.drug.name}<input type="hidden" name="addDst${i}.drug" value="${drugType.drug.id}"/></td>
<td>${drugType.concentration}<input type="hidden" name="addDst${i}.concentration" value="${drugType.concentration}"/></td>
<td><select name="addDst${i}.result">
<option value=""></option>
<c:forEach var="possibleResult" items="${dstResults}">
<option value="${possibleResult.id}">${possibleResult.name}</option>
</c:forEach></td>
</select>
</td>
<td><input type="text" name="addDst${i}.colonies" value=""/></td>
<c:set var="i" value="${i+1}"/>
</tr>
</c:forEach>
</table>
<br/>
</c:if>
<!-- end of the DST table -->


<!--  TODO: figure out why "cancelAdd" as an id (instead of class) isn't working -->
<button type="submit">Save</button><button class="cancelAdd" type="reset">Cancel</button>
</form>
</div>

</c:forEach> 

<!-- END ADD TEST SECTION -->

</div> <!-- END OF TEST DIV -->

</body>
</html>