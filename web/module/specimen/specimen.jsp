<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>

<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${specimen.patient.patientId}"/>
<openmrs:portlet url="mdrtbTabs" id="mdrtbTabs" moduleId="mdrtb" patientId="${specimen.patient.patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->
<!-- TODO: localize all text -->

<!-- TODO: figure out if I should be using concept.name or concept.name.name or whatever -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript"><!--

	// hides all add, edit, and view details boxes
	function hideDisplayBoxes(){
		$('.addBox').hide();
		$('.editBox').hide();
		$('.detailBox').hide();
		$('#details_-1').hide();
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

		// set the add dst count to 1
		var addDstResultCounter = 1;
		
		// show the proper detail windows if it has been specified
		$('#details_' + ${testId}).show();
	
		// event handlers to hide and show specimen edit box
		$('#editSpecimen').click(function(){
			hideViewEditAddLinks();
			$('#details_specimen').hide();  // hide the specimen details box
			$('#edit_specimen').show();  // show the edit speciment box
		});

		$('#cancelSpecimen').click(function(){
			showViewEditAddLinks();
			$('#edit_specimen').hide();  // hide the edit specimen box
			$('#details_specimen').show();  // show the specimen details box
			$('.scannedLabReport').show(); // show any scanned lab reports that may have been deleted
		});

		// event handlers to display add boxes
		$('#addButton').click(function(){
			hideDisplayBoxes();
			hideViewEditAddLinks();
			$('#add_' + $('#addSelect').attr('value')).show(); // show the proper add a test box
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

		// event handler to cancel an edit or add
		$('.cancel').click(function(){	
			hideDisplayBoxes();
			showViewEditAddLinks();
			$('.dstResult').show(); // show any dst results that may have been deleted
			$('#details_' + this.id).show(); // display the details box for the test that was just being edited
			$('.addDstResult').hide().find('input,select').attr('value',''); // hide all the add dst result rows and reset their values
			addDstResultCounter = 1; // reset the add dst result counter
		});

		// event handler to hide/show bacilli and colonies selector, and reset the value if needed
		$('.result').change(function() {
				if ($(this).attr('value') == ${scanty.id}) {
					// show the bacilli or colonies row in the same div as this element
					$(this).closest('div').find('.bacilli').show();
					$(this).closest('div').find('.colonies').show();
				}
				else {
					// hide the bacilli or colonies row in the same div as this element,
					// then find the bacilli/colonies input element and set it's value to empty
					$(this).closest('div').find('.bacilli').hide().find('#bacilli').attr('value','');
					$(this).closest('div').find('.colonies').hide().find('#colonies').attr('value','');
				}
		});
		
		// event handler to reset dst colonies if result is reset to an empty value
		$('.dstResult').change(function() {
			if ($(this).attr('value') == '' || $(this).attr('value') == ${waitingForTestResult.id} || $(this).attr('value') == ${dstTestContaminated.id} ) {
				$(this).closest('tr').find('.dstColonies').hide().attr('value',null);
			}
			else {
				$(this).closest('tr').find('.dstColonies').show();
			}
		});

		//event handler to hide/show organism non-coded selector, and reset the value if needed
		$('.organismType').change(function() {
			if ($(this).attr('value') == ${otherMycobacteriaNonCoded.id}) {
				// show the organism type non-code row in the same div element
				$(this).closest('div').find('.organismTypeNonCoded').show();
			}
			else {
				// hide the organism type non-coded row in the same div as this element
				// then find the organism type non-coded inpout element and set it's value to empty
				$(this).closest('div').find('.organismTypeNonCoded').hide().find('#organismTypeNonCoded').attr('value','');	
			}
		});

		//event handler to handle removing lab reports
		$('.removeScannedLabReport').click(function() {
			// hide the lab report
			$(this).closest('.scannedLabReport').hide();
			// set it's hidden input to the id of this scanned lab report
			$('#removeScannedLabReport' + $(this).attr('value')).attr('value',$(this).attr('value'));
		});

		//event handler to handle removing dst results
		$('.removeDstResult').click(function() {
			// hide the dst result
			$(this).closest('.dstResult').hide();
			// set it's hidden input to the id of this dst report
			$('#removeDstResult' + $(this).attr('value')).attr('value',$(this).attr('value'));
		});

		// event handle to handle adding dst results
		$('#addDstResultRow').click(function() {
			if(addDstResultCounter < 30) {
				// hacky! two things with the same id... also relies on table structure...
				$(this).closest('table').find('#addDstResult' + addDstResultCounter).show();
				addDstResultCounter++;
			}
		});

		// event handle to handle adding dst results in the "add" section
		// TODO: the name "addSectionAddDstResult" makes me cring... have to refactor this better!
		// I've able to repeat all the add dst result functionality between the edit and add sections,
		// except for this because I use ids here
		$('#addSectionAddDstResultRow').click(function() {
			if(addDstResultCounter < 30) {
				$('#addSectionAddDstResult' + addDstResultCounter).show();
				addDstResultCounter++;
			}
		});

		//event handler to handle removing of dst rows that have been added, but not saved
		$('.removeDstResultRow').click(function() {
			// hide the dst result row and reset the value of all the interior elements
			$(this).closest('.addDstResult').hide().find('input,select').attr('value','');
		});
		
 	});
-->
</script>
<!-- END JQUERY -->

<!--  
<div align="left">
<ul id="menu">
<li class="first">
<a style="font-size:70%;" href="${pageContext.request.contextPath}/module/mdrtb/specimen/list.form?patientId=${specimen.patient.patientId}">Return to Specimen List</a>
</li>
</ul>
</div>
-->


<!--  SPECIMEN SECTION -->
<div id="specimen" align="center">

<div id="details_specimen">

<b class="boxHeader" style="margin:0px">Specimen Details<span style="position: absolute; right:25px;"><a href="#" id="editSpecimen">edit</a></span></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">

<tr>
<td><nobr>Sample ID:</nobr></td><td><nobr>${specimen.identifier}</nobr></td>
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
<td><nobr>Appearance:</td><td>${specimen.appearance.name.name}</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Comments:</td><td colspan="3">${specimen.comments}</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Scanned Lab Reports:</nobr></td>
<td colspan="3">
<c:forEach var="report" items="${specimen.scannedLabReports}">
<a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${report.id}&view=download&viewType=download">${report.filename}</a>
</c:forEach>
</td>
<td width="100%">&nbsp;</td>
</tr>

</table>
</div>
</div>
<!--  END OF SPECIMEN SECTION -->

<!--  EDIT SPECIMEN SECTION -->

<div id="edit_specimen"  style="display:none">

<form name="specimen" action="specimen.form?specimenId=${specimen.id}&testId=-1" method="post" enctype="multipart/form-data">

<b class="boxHeader" style="margin:0px">Sample Details</b>
<div class="box" style="margin:0px">
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
<td><nobr>Appearance:</nobr></td>
<td>
<select name="appearance">
<option value=""></option>
<c:forEach var="appearance" items="${appearances}">
<option value="${appearance.answerConcept.id}" <c:if test="${specimen.appearance == appearance.answerConcept}">selected</c:if> >${appearance.answerConcept.name}</option>
</c:forEach>
</select>
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Comments:</nobr></td>
<td colspan="3"><textarea name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr>Scanned Lab Reports:</nobr></td>
<td colspan="3">
<c:forEach var="report" items="${specimen.scannedLabReports}">
<span class="scannedLabReport"><a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${report.id}&view=download&viewType=download">${report.filename}</a>
<button class="removeScannedLabReport" value="${report.id}" type="button">X</button>
<input type="hidden" id="removeScannedLabReport${report.id}" name="removeScannedLabReport" value=""/></span>
<br/>
</c:forEach>
<input type="file" name="addScannedLabReport" size="50" />
</td>
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

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if><span style="position: absolute; right:25px;"><a href="#" id="${test.id}" class="view">view</a></span></b>
<div class="box" style="margin:0px">
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

<!-- BLANK DIV FOR VIEWING/EDITING PANE -->
<div id="details_-1" class="box" style="position:absolute; left:450px; top:30px; height:400px; width: 700px; font-size:0.9em; text-align:center; display:none;">
<br/><br/>
Select a smear, culture, or DST  from the list on the left to view it's details.
</div>

<c:forEach var="test" items="${specimen.tests}">

<!--  TEST DETAILS SECTION -->

<div id="details_${test.id}" class="detailBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Detail View<span style="position: absolute; right:30px;"><a href="#" id="${test.id}" class="edit">edit</a>&nbsp;&nbsp;<a href="delete.form?testId=${test.id}&specimenId=${specimen.id}" class="delete" onclick="return confirm('Are you sure you want to delete this test?')">delete</a></span></b>
<div class="box" style="margin:0px">
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

<c:if test="${test.testType eq 'culture' || test.testType eq 'dst'}">
<tr>
<td><nobr>Organism Type:</nobr></td><td><nobr>${test.organismType.name.name}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${(test.testType eq 'culture' || test.testType eq 'dst') && test.organismType == otherMycobacteriaNonCoded}">
<tr>
<td><nobr>Organism Type Non-Coded:</nobr></td><td><nobr>${test.organismTypeNonCoded}</nobr></td>
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
<!-- fetch the map of test results for this dst -->
<c:set var="resultsMap" value="${test.resultsMap}"/>
<br/>
<table cellpadding="0">
<tr>
<td><u>Drug</u></td><td><u>Concentration</u></td><td><u>Result</u></td><td><u>Colonies</u></td>
</tr>
<c:forEach var="drugType" items="${drugTypes}">
<c:if test="${!empty resultsMap[drugType.id]}">
	<c:forEach var="dstResult" items="${resultsMap[drugType.id]}">
		<tr>
		<td><nobr>${dstResult.drug.name}</nobr></td>
		<td><nobr>${dstResult.concentration}</nobr></td>
		<td><nobr>${dstResult.result.name}</nobr></td>
		<td><nobr>${dstResult.colonies}</nobr></td>
		</tr>
	</c:forEach>
</c:if>
</c:forEach>
</table>
</c:if>
<!-- end of the DST table -->

</div>
</div> <!-- end of details div -->

<!-- END OF TEST DETAILS SECTION -->

<!-- EDIT TESTS SECTION -->

<div id="edit_${test.id}" class="editBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em"">

<!--  TODO: how do i bind errors to this? -->
<!-- TODO: form id should be specified based on test type; get rid of enum, just use a String getTestType? -->

<form name="${test.testType}" action="specimen.form?${test.testType}Id=${test.id}&testId=${test.id}&specimenId=${specimen.id}" method="post">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: Edit View</b>
<div class="box" style="margin:0px">
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
<td><input type="text" name="bacilli" id="bacilli" value="${test.bacilli}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr class="colonies" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr># of Colonies:</nobr></td>
<td><input type="text" name="colonies" id="colonies" value="${test.colonies}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture' || test.testType eq 'dst'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select name="organismType" class="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}" <c:if test="${organismType.answerConcept == test.organismType}">selected</c:if> >${organismType.answerConcept.name}</option>
</c:forEach></td>
<td colspan="2">&nbsp;</td>
</tr>
<tr class="organismTypeNonCoded" <c:if test="${test.organismType != otherMycobacteriaNonCoded}"> style="display:none;"</c:if>>
<td><nobr>Organism Type Non-Coded:</nobr></td>
<td><input type="text" name="organismTypeNonCoded" id="organismTypeNonCoded" value="${test.organismTypeNonCoded}"/></td>
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
<c:forEach var="drugType" items="${drugTypes}">
	<c:if test="${!empty resultsMap[drugType.id]}">
		<c:forEach var="dstResult" items="${resultsMap[drugType.id]}">
			<tr class="dstResult">
			<td><nobr>${dstResult.drug.name}</nobr></td>
			<td><nobr>${dstResult.concentration}</nobr></td>
			<td><nobr>${dstResult.result.name}</nobr></td>
			<td><nobr>${dstResult.colonies}</nobr></td>
			<td><button class="removeDstResult" value="${dstResult.id}" type="button">X</button>
				<input type="hidden" id="removeDstResult${dstResult.id}" name="removeDstResult" value=""/></td>
			</tr>
		</c:forEach>
	</c:if>
	</c:forEach>
	
	<!-- now the rows to add a new table -->
	<!-- note that we just add thirty, blank, hidden rows here, which is kind of hacky, but makes the code simplier! :) -->
	<c:forEach begin="1" end="30" varStatus="i">
		<tr id="addDstResult${i.count}" class="addDstResult" style="display:none">
		<td><select name="addDstResult${i.count}.drug">
			<option value=""></option>
			<c:forEach var="drug" items="${drugTypes}">
				<option value="${drug.id}">${drug.name}</option>
			</c:forEach>
			</select>
		</td>
		<td><input type="input" size="6" name="addDstResult${i.count}.concentration"/></td>
		<td><select name="addDstResult${i.count}.result" class="dstResult">
			<option value=""></option>
			<c:forEach var="possibleResult" items="${dstResults}">
				<option value="${possibleResult.id}">${possibleResult.name}</option>
			</c:forEach></td>
			</select>
		</td>
		<td><input type="text" size="6" name="addDstResult${i.count}.colonies" value="" class="dstColonies" style="display:none"/></td>
		<td><button class="removeDstResultRow" value="${i.count}" type="button">X</button></td>
		</tr>
	</c:forEach>

	<tr>
	<td><button id="addDstResultRow" type="button">Add DST result</button></td>
	<td colspan="4"/>
	</tr>
	
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit">Save</button><button type="reset" id="${test.id}" class="cancel">Cancel</button>

</form>

</div>
</div>

</c:forEach>

<!--  END OF EDIT TESTS SECTION -->

<!-- ADD TEST SECTION -->
<!--  TODO: would really like to combine this with the edit tests section, to avoid all this code repeating... -->


<c:forEach var="type" items="${testTypes}">

<div id="add_${type}" class="addBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em"">

<form name="${type}" action="specimen.form?${type}Id=-1&testId=-1&specimenId=${specimen.id}" method="post">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${type}"/>: Add</b>
<div class="box" style="margin:0px">
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
<td><input type="text" name="bacilli" id="bacilli" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'culture'}">
<tr class="colonies" style="display:none;">
<td><nobr># of Colonies:</nobr></td>
<td><input type="text" name="colonies" id="colonies" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>


<c:if test="${type eq 'culture' || type eq 'dst'}">
<tr>
<td><nobr>Organism Type:</nobr></td>
<td><select name="organismType" class="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}">${organismType.answerConcept.name}</option>
</c:forEach></td>
</select>
</td>
<td colspan="2">&nbsp;</td>
</tr>
<tr class="organismTypeNonCoded" style="display:none;">
<td><nobr>Organism Type Non-Coded:</nobr></td>
<td><input type="text" name="organismTypeNonCoded" id="organismTypeNonCoded" value=""/></td>
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
	
	<!-- now the rows to add a new table -->
	<!-- note that we just add thirty, blank, hidden rows here, which is kind of hacky, but makes the code simplier! :) -->
	<c:forEach begin="1" end="30" varStatus="i">
		<tr id="addSectionAddDstResult${i.count}" class="addDstResult" style="display:none">
		<td><select name="addDstResult${i.count}.drug">
			<option value=""></option>
			<c:forEach var="drug" items="${drugTypes}">
				<option value="${drug.id}">${drug.name}</option>
			</c:forEach>
			</select>
		</td>
		<td><input type="input" size="6" name="addDstResult${i.count}.concentration"/></td>
		<td><select name="addDstResult${i.count}.result" class="dstResult">
			<option value=""></option>
			<c:forEach var="possibleResult" items="${dstResults}">
				<option value="${possibleResult.id}">${possibleResult.name}</option>
			</c:forEach></td>
			</select>
		</td>
		<td><input type="text" size="6" name="addDstResult${i.count}.colonies" value="" class="dstColonies" style="display:none"/></td>
		<td><button class="removeDstResultRow" value="${i.count}" type="button">X</button></td>
		</tr>
	</c:forEach>

	<tr>
	<td><button id="addSectionAddDstResultRow" type="button">Add DST result</button></td>
	<td colspan="4"/>
	</tr>
	
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit">Save</button><button class="cancel" type="reset">Cancel</button>
</form>
</div>
</div>

</c:forEach> 

<!-- END ADD TEST SECTION -->

</div> <!-- END OF TEST DIV -->

</div> <!-- END OF SPECIMEN DIV -->
