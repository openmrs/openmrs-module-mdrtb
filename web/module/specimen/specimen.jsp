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
		$('.view').hide();  // hide all the view links
		$('.edit').hide();  // hide all the edit tests links
		$('.delete').hide(); // hide all the delete links 
		$('#editSpecimen').hide(); // hide the edit specimen link
		$('#add').hide(); // hide the "add a test" selector
	}

	// shows all view, edit, and add elements (called when an edit is complete)
	function showViewEditAddLinks() {
		$('.view').show();  // show all the view links
		$('.edit').show();  // show all the edit tests links
		$('.delete').show(); // show all the delete links 
		$('#editSpecimen').show(); // show the edit specimen link
		$('#add').show(); // show the "add a test" selector
	}
	
	$(document).ready(function(){
		
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

<form id="specimen" action="specimen.form?specimenId=${specimen.id}" method="post">

<b class="boxHeader">Sample Details</b>
<div class="box">
<table cellspacing="0" cellpadding="0">

<!-- TODO localize all text -->

<!-- TODO is answerConcept.name the correct parameter? -->
<tr>
<td><nobr>Sample ID:</nobr></td>
<td><input type="text" size="10" id="identifier" name="identifier" value="${specimen.identifier}"/></td>
<td><nobr>Collected By:</nobr></td>
<td>
<select id="provider" name="provider">
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
<select id="type" name="type">
<option value=""></option>
<c:forEach var="type" items="${types}">
<option value="${type.answerConcept.id}" <c:if test="${specimen.type == type.answerConcept}">selected</c:if> >${type.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td><nobr>Location Collected:</nobr></td>
<td>
<select id="location" name="location">
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
<td><textarea id="comments" name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>
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

<span id="add">
Add a new Lab Test:
<select id="addSelect">
<option value="smear">Smear</option>
<option value="culture">Culture</option>
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
<tr>
<td>Result:</td><td>${test.result.name.name}</td>
</tr>
<tr>
</table> 
</div>

<br/>

</c:forEach>

</div> <!--  end summary div -->

<!-- END OF TEST SUMMARY SECTION -->


<c:forEach var="test" items="${specimen.tests}">

<!--  TEST DETAILS SECTION -->

<div id="details_${test.id}" class="detailBox" style="position:absolute; left:450px; top:30px; display:none">

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
<td><nobr>Result:</nobr></td><td><nobr>${test.result.name.name}</nobr></td>
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

<b class="boxHeader"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Edit View</b>
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
<option value=""></option>
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
<td><select id="result" name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${test.testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td><nobr>Date completed:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'smear'}">
<tr class="bacilli" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr># of Bacilli:</nobr></td>
<td><input type="text" id="bacilli" name="bacilli" value="${test.bacilli}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr class="colonies" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr># of Colonies:</nobr></td>
<td><input type="text" id="colonies" name="colonies" value="${test.colonies}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select id="organismType" name="organismType">
<option value=""></option>
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
<option value=""></option>
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
<td><select id="result" name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${type eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}">${result.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td><nobr>Date completed:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${type eq 'smear'}">
<tr class="bacilli" style="display:none;">
<td><nobr># of Bacilli:</nobr></td>
<td><input type="text" id="bacilli" name="bacilli" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'culture'}">
<tr class="colonies" style="display:none;">
<td><nobr># of Colonies:</nobr></td>
<td><input type="text" id="colonies" name="colonies" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>


<c:if test="${type eq 'culture'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select id="organismType" name="organismType">
<option value=""></option>
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

<!--  TODO: figure out why "cancelAdd" as an id (instead of class) isn't working -->
<button type="submit">Save</button><button class="cancelAdd" type="reset">Cancel</button>
</form>
</div>

</c:forEach> 

<!-- END ADD TEST SECTION -->

</div> <!-- END OF TEST DIV -->

</body>
</html>