<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${specimen.patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${specimen.patient.patientId}"/>

<!-- TODO: clean up above paths so they use dynamic reference? -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	// hides all add, edit, and view details boxes
	function hideDisplayBoxes(){
		$j('.addBox').hide();
		$j('.editBox').hide();
		$j('.detailBox').hide();
		$j('#details_-1').hide();
	}

	// hides all view, edit, and add elements (used to stop used from navigating away from an edit)
	function hideViewEditAddLinks() {
		$j('.view').fadeOut();  // hide all the view links
		$j('.edit').fadeOut();  // hide all the edit tests links
		$j('.delete').fadeOut(); // hide all the delete links 
		$j('#editSpecimen').fadeOut(); // hide the edit specimen link
		$j('#add').fadeOut(); // hide the "add a test" selector
	}

	// shows all view, edit, and add elements (called when an edit is complete)
	function showViewEditAddLinks() {
		$j('.view').fadeIn();  // show all the view links
		$j('.edit').fadeIn();  // show all the edit tests links
		$j('.delete').fadeIn(); // show all the delete links 
		$j('#editSpecimen').fadeIn(); // show the edit specimen link
		$j('#add').fadeIn(); // show the "add a test" selector
	}
	
	$j(document).ready(function(){

		// set the add dst count to 1
		var addDstResultCounter = 1;
		
		// show the proper detail windows if it has been specified
		// TODO: this does not work when a test is saved as the test id of a test gets changes whenever it is saved since
		// all the obs get voided and recreated;
		$j('#details_${testId}').show();

		// switch to edit specimen if we are here because of specimen validation error
		if(${fn:length(specimenErrors.allErrors) > 0}) {
			hideViewEditAddLinks();
			$j('#details_specimen').hide();  // hide the specimen details box
			$j('#edit_specimen').show();  // show the edit speciment box
			// TODO: rethink this cancel functionality when working on the rest of the validation?
			$j('#cancelSpecimen').hide(); // a user shouldn't be able to cancel, should have to fix the error
						
		}
		
		// event handlers to hide and show specimen edit box
		$j('#editSpecimen').click(function(){
			hideViewEditAddLinks();
			$j('#details_specimen').hide();  // hide the specimen details box
			$j('#edit_specimen').show();  // show the edit speciment box
		});

		$j('#cancelSpecimen').click(function(){
			showViewEditAddLinks();
			$j('#edit_specimen').hide();  // hide the edit specimen box
			$j('#details_specimen').show();  // show the specimen details box
			$j('.scannedLabReport').show(); // show any scanned lab reports that may have been deleted
		});

		// event handlers to display add boxes
		$j('#addButton').click(function(){
			hideDisplayBoxes();
			hideViewEditAddLinks();
			$j('#add_' + $j('#addSelect').attr('value')).show(); // show the proper add a test box
		});

		// event handler to display view detail boxes
		$j('.view').click(function(){
			hideDisplayBoxes();
			$j('#details_' + this.id).show();  // show the selected details box
		});

		// event handler to display edit detail boxes
		$j('.edit').click(function(){
			hideDisplayBoxes();
			hideViewEditAddLinks();
			$j('#edit_' + this.id).show();  // show the selected edit box
		});

		// event handler to cancel an edit or add
		$j('.cancel').click(function(){	
			hideDisplayBoxes();
			showViewEditAddLinks();
			$j('.dstResult').show(); // show any dst results that may have been deleted
			$j('#details_' + this.id).show(); // display the details box for the test that was just being edited
			$j('.addDstResult').hide().find('input,select').attr('value',''); // hide all the add dst result rows and reset their values
			addDstResultCounter = 1; // reset the add dst result counter
		});

		// event handler to hide/show bacilli and colonies selector, and reset the value if needed
		$j('.result').change(function() {
				if ($j(this).attr('value') == ${scanty.id}) {
					// show the bacilli or colonies row in the same div as this element
					$j(this).closest('div').find('.bacilli').show();
					$j(this).closest('div').find('.colonies').show();
				}
				else {
					// hide the bacilli or colonies row in the same div as this element,
					// then find the bacilli/colonies input element and set it's value to empty
					$j(this).closest('div').find('.bacilli').hide().find('#bacilli').attr('value','');
					$j(this).closest('div').find('.colonies').hide().find('#colonies').attr('value','');
				}
		});
		
		// event handler to reset dst colonies if result is reset to an empty value
		$j('.dstResult').change(function() {
			if ($j(this).attr('value') == '' || $j(this).attr('value') == ${waitingForTestResult.id} || $j(this).attr('value') == ${dstTestContaminated.id} ) {
				$j(this).closest('tr').find('.dstColonies').hide().attr('value',null);
			}
			else {
				$j(this).closest('tr').find('.dstColonies').show();
			}
		});

		//event handler to hide/show organism non-coded selector, and reset the value if needed
		$j('.organismType').change(function() {
			if ($j(this).attr('value') == ${otherMycobacteriaNonCoded.id}) {
				// show the organism type non-code row in the same div element
				$j(this).closest('div').find('.organismTypeNonCoded').show();
			}
			else {
				// hide the organism type non-coded row in the same div as this element
				// then find the organism type non-coded inpout element and set it's value to empty
				$j(this).closest('div').find('.organismTypeNonCoded').hide().find('#organismTypeNonCoded').attr('value','');	
			}
		});

		//event handler to handle removing lab reports
		$j('.removeScannedLabReport').click(function() {
			// hide the lab report
			$j(this).closest('.scannedLabReport').hide();
			// set it's hidden input to the id of this scanned lab report
			$j('#removeScannedLabReport' + $j(this).attr('value')).attr('value',$j(this).attr('value'));
		});

		//event handler to handle removing dst results
		$j('.removeDstResult').click(function() {
			// hide the dst result
			$j(this).closest('.dstResult').hide();
			// set it's hidden input to the id of this dst report
			$j('#removeDstResult' + $j(this).attr('value')).attr('value',$j(this).attr('value'));
		});

		// event handle to handle adding dst results
		$j('.addDstResultRow').click(function() {
			if(addDstResultCounter < 30) {
				$j('#addDstResult' + $j(this).attr('value') + "_" + addDstResultCounter).show();
				addDstResultCounter++;
			}
		});

		//event handler to handle removing of dst rows that have been added, but not saved
		$j('.removeDstResultRow').click(function() {
			// hide the dst result row and reset the value of all the interior elements
			$j(this).closest('.addDstResult').hide().find('input,select').attr('value','');
		});
		
 	});
-->
</script>
<!-- END JQUERY -->

<!--  
<div align="left">
<ul id="menu">
<li class="first">
<a style="font-size:70%;" href="${pageContext.request.contextPath}/module/mdrtb/specimen/list.form?patientId=${specimen.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.returnToSpecimenList" text="Return to Specimen List"/></a>
</li>
</ul>
</div>
-->

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(specimenErrors.allErrors) > 0}">
	<c:forEach var="error" items="${specimenErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/>
	</c:forEach>
	<br/>
</c:if>

<!--  SPECIMEN SECTION -->
<div id="specimen" align="center">

<div id="details_specimen">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.specimenDetails" text="Specimen Details"/><span style="position: absolute; right:25px;"><a href="#" id="editSpecimen"><spring:message code="mdrtb.edit" text="edit"/></a></span></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">

<tr>
<td><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</nobr></td><td><nobr>${specimen.identifier}</nobr></td>
<td><nobr><spring:message code="mdrtb.collectedBy" text="Collected By"/>:</nobr></td><td><nobr>${specimen.provider.personName}</nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.sampleType" text="Sample Type"/>:</td><td><nobr>${specimen.type.displayString}</nobr></td> 
<td><nobr><spring:message code="mdrtb.locationCollected" text="Location Collected"/>:</td><td><nobr>${specimen.location.displayString}</nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.dateCollected" text="Date Collected"/>:</td><td><nobr><openmrs:formatDate date="${specimen.dateCollected}"/></nobr></td>
<td><nobr><spring:message code="mdrtb.appearance" text="Appearance"/>:</td><td>${specimen.appearance.displayString}</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</td><td colspan="3">${specimen.comments}</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.scannedLabReports" text="Scanned Lab Reports"/>:</nobr></td>
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

<form id="specimen" action="specimen.form?submissionType=specimen&specimenId=${specimen.id}&patientProgramId=${patientProgramId}&testId=-1" method="post" enctype="multipart/form-data">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.specimenDetails" text="Specimen Details"/></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">

<tr>
<td><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</nobr></td>
<td><input type="text" size="10" name="identifier" value="${specimen.identifier}"/></td>
<td><nobr><spring:message code="mdrtb.collectedBy" text="Collected By"/>:</nobr></td>
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
<td><nobr><spring:message code="mdrtb.sampleType" text="Sample Type"/>:</nobr></td>
<td>
<select name="type">
<option value=""></option>
<c:forEach var="type" items="${types}">
<option value="${type.answerConcept.id}" <c:if test="${specimen.type == type.answerConcept}">selected</c:if> >${type.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
<td><nobr><spring:message code="mdrtb.locationCollected" text="Location Collected"/>:</nobr></td>
<td>
<select name="location">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == specimen.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>	
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.dateCollected" text="Date Collected"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/></nobr></td>
<td><nobr><spring:message code="mdrtb.appearance" text="Appearance"/>:</nobr></td>
<td>
<select name="appearance">
<option value=""></option>
<c:forEach var="appearance" items="${appearances}">
<option value="${appearance.answerConcept.id}" <c:if test="${specimen.appearance == appearance.answerConcept}">selected</c:if> >${appearance.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td>
<td colspan="3"><textarea name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.scannedLabReports" text="Scanned Lab Reports"/>:</nobr></td>
<td colspan="3">
<c:forEach var="report" items="${specimen.scannedLabReports}">
<span class="scannedLabReport"><a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${report.id}&view=download&viewType=download">${report.filename}</a>
<button class="removeScannedLabReport" value="${report.id}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button>
<input type="hidden" id="removeScannedLabReport${report.id}" name="removeScannedLabReport" value=""/></span>
<br/>
</c:forEach>
<input type="file" name="addScannedLabReport" size="50" />
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td colspan="5" align="left"><button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button type="reset" id="cancelSpecimen"><spring:message code="mdrtb.cancel" text="Cancel"/></button></td>
</tr>

</table>

</form>
</div>
</div>
<!-- END OF EDIT SPECIMEN SECTION -->

<br/>

<div id="tests" style="position:relative"> 
<b class="boxHeader"><spring:message code="mdrtb.tests" text="Tests"/></b>

<!-- TEST SUMMARY SECTION -->
<div id="summary" style="position:absolute; left:20px; top:30px; width:400px">

<span id="add"  style="font-size:0.9em">
<spring:message code="mdrtb.addANewLabTest" text="Add a new Lab Test:"/>:
<select id="addSelect">
<c:forEach var="test" items="${testTypes}">
	<option value="${test}"><spring:message code="mdrtb.${test}"/></option>
</c:forEach>
</select>
<button id="addButton" type="button"><spring:message code="mdrtb.add" text="Add"/></button>
</span>

<br/><br/>

<c:forEach var="test" items="${specimen.tests}">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if><span style="position: absolute; right:25px;"><a href="#" id="${test.id}" class="view"><spring:message code="mdrtb.view" text="View"/></a></span></b>
<div class="box" style="margin:0px">
<table style="width:396px;" cellspacing="0" cellpadding="0">

<tr>
<td><spring:message code="mdrtb.status" text="Status"/>:</td><td><mdrtb:testStatus test="${test}"/></td>
</tr>

<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<tr>
<td><spring:message code="mdrtb.result" text="Result"/>:</td><td>${test.result.displayString}</td>
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

<c:forEach var="test" items="${specimen.tests}" varStatus="testIteration">

<!--  TEST DETAILS SECTION -->

<div id="details_${test.id}" class="detailBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: <spring:message code="detailView" text="Detail View"/><span style="position: absolute; right:30px;"><a href="#" id="${test.id}" class="edit"><spring:message code="mdrtb.edit" text="edit"/></a>&nbsp;&nbsp;<a href="delete.form?testId=${test.id}&specimenId=${specimen.id}&patientProgramId=${patientProgramId}" class="delete" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteTest" text="Are you sure you want to delete this test?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></span></b>
<div class="box" style="margin:0px">
<table cellpadding="0">
<tr>
<td><nobr><spring:message code="mdrtb.accessionNumber" text="Accession #"/>:</nobr></td><td><nobr>${test.accessionNumber}</nobr></td>
<td><nobr><spring:message code="mdrtb.dateOrdered" text="Date ordered"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.dateOrdered}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.lab" text="Lab"/>:</nobr></td><td><nobr>${test.lab}</nobr></td>
<td><nobr><spring:message code="mdrtb.dateSampleReceived" text="Date sample received"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.dateReceived}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.method" text="Method"/>:</nobr></td><td><nobr>${test.method.displayString}</nobr></td>
<td><nobr><spring:message code="mdrtb.dateStarted" text="Date started"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>


<tr>
<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<td><nobr><spring:message code="mdrtb.result" text="Result"/>:</nobr></td><td><nobr>${test.result.displayString}</nobr></td>
</c:if>
<c:if test="${test.testType eq 'dst'}">
<td><nobr><spring:message code="mdrtb.directIndirect" text="Direct/Indirect"/>:</nobr></td><td><nobr>
<c:if test="${test.direct == true}">
	<spring:message code="mdrtb.direct" text="Direct"/>	
</c:if>
<c:if test="${test.direct == false}">
	<spring:message code="mdrtb.indirect" text="Indirect"/>
</c:if>
</nobr></td>
</c:if>
<td><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'smear' && test.result == scanty}">
<tr>
<td><nobr><spring:message code="mdrtb.numberofbacilli" text="Number of Bacilli"/>:</nobr></td><td><nobr>${test.bacilli}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture' && test.result == scanty}">
<tr>
<td><nobr><spring:message code="mdrtb.numberofcolonies" text="Number of Colonies"/>:</nobr></td><td><nobr>${test.colonies}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture' || test.testType eq 'dst'}">
<tr>
<td><nobr><spring:message code="mdrtb.organismType" text="Organism Type"/>:</nobr></td><td><nobr>${test.organismType.displayString}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${(test.testType eq 'culture' || test.testType eq 'dst') && test.organismType == otherMycobacteriaNonCoded}">
<tr>
<td><nobr><spring:message code="mdrtb.organismTypeNonCoded" text="Organism Type Non-Coded"/>:</nobr></td><td><nobr>${test.organismTypeNonCoded}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>


<c:if test="${test.testType eq 'dst'}">
<tr>
<td><nobr><spring:message code="mdrtb.coloniesincontrol" text="Colonies in control"/>:</nobr></td><td><nobr>${test.coloniesInControl}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td><td colspan="3">${test.comments}</td>
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
<td><u><spring:message code="mdrtb.drug" text="Drug"/></u></td><td><u><spring:message code="mdrtb.concentration" text="Concentration"/></u></td><td><u><spring:message code="mdrtb.result" text="Result"/></u></td><td><u><spring:message code="mdrtb.colonies" text="Colonies"/></u></td>
</tr>
<c:forEach var="drugType" items="${drugTypes}">
<c:if test="${!empty resultsMap[drugType.id]}">
	<c:forEach var="dstResult" items="${resultsMap[drugType.id]}">
		<tr>
		<td><nobr>${dstResult.drug.displayString}</nobr></td>
		<td><nobr>${dstResult.concentration}</nobr></td>
		<td><nobr>${dstResult.result.displayString}</nobr></td>
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

<div id="edit_${test.id}" class="editBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em">

<!--  TODO: how do i bind errors to this? -->

<form id="${test.testType}" action="specimen.form?submissionType=${test.testType}&${test.testType}Id=${test.id}&testId=${test.id}&specimenId=${specimen.id}&patientProgramId=${patientProgramId}" method="post">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if>: <spring:message code="mdrtb.editView" text="Edit View"/></b>
<div class="box" style="margin:0px">
<table cellpadding="0">

<tr>
<td><nobr><spring:message code="mdrtb.accessionNumber" text="Accession #"/>:</nobr></td>
<td><input type="text" name="accessionNumber" value="${test.accessionNumber}"/></td>
<td><nobr><spring:message code="mdrtb.dateOrdered" text="Date ordered"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue="${test.dateOrdered}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><spring:message code="mdrtb.lab" text="Lab"/>:</td>
<td><select name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == test.lab}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>
</td>
<td><nobr><spring:message code="mdrtb.dateSampleReceived" text="Date sample received"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateReceived" startValue="${test.dateReceived}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.method" text="Method"/>:</nobr></td>
<td><select name="method">
<option value=""></option>
<c:forEach var="method" items="${test.testType eq 'smear'? smearMethods : (test.testType eq 'culture' ? cultureMethods : dstMethods)}">
<option value="${method.answerConcept.id}" <c:if test="${method.answerConcept == test.method}">selected</c:if> >${method.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
<td><nobr><spring:message code="mdrtb.dateStarted" text="Date started"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<td><nobr><spring:message code="mdrtb.results" text="Results"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${test.testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</c:if>

<c:if test="${test.testType eq 'dst'}">
<td><nobr><spring:message code="mdrtb.directIndirect" text="Direct/Indirect"/>:</nobr></td>
<td><select name="direct">
<option value=""></option>
<option <c:if test="${test.direct}">selected </c:if>value="1"><spring:message code="mdrtb.direct" text="Direct"/></option>
<option <c:if test="${!test.direct}">selected </c:if>value="0"><spring:message code="mdrtb.indirect" text="Indirect"/></option>
</select></td>
</c:if>

<td><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'smear'}">
<tr class="bacilli" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr><spring:message code="mdrtb.numberofbacilli" text="Number of Bacilli"/>:</nobr></td>
<td><input type="text" name="bacilli" id="bacilli" value="${test.bacilli}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr class="colonies" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td><nobr><spring:message code="mdrtb.numberofcolonies" text="Number of Colonies"/>:</nobr></td>
<td><input type="text" name="colonies" id="colonies" value="${test.colonies}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture' || test.testType eq 'dst'}">
<tr>
<td><nobr><spring:message code="mdrtb.organismType" text="Organism Type"/>:</nobr></td>
<td><select name="organismType" class="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}" <c:if test="${organismType.answerConcept == test.organismType}">selected</c:if> >${organismType.answerConcept.displayString}</option>
</c:forEach></td>
<td colspan="2">&nbsp;</td>
</tr>
<tr class="organismTypeNonCoded" <c:if test="${test.organismType != otherMycobacteriaNonCoded}"> style="display:none;"</c:if>>
<td><nobr><spring:message code="mdrtb.organismTypeNonCoded" text="Organism Type Non-Coded"/>:</nobr></td>
<td><input type="text" name="organismTypeNonCoded" id="organismTypeNonCoded" value="${test.organismTypeNonCoded}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'dst'}">
<tr>
<td><nobr><spring:message code="mdrtb.coloniesincontrol" text="Colonies in control"/>:</nobr></td>
<td><input type="text" name="coloniesInControl" value="${test.coloniesInControl}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" name="comments">${test.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${test.testType eq 'dst'}">
<br/>
<table cellpadding="0">

<tr>
<td><u><spring:message code="mdrtb.drug" text="Drug"/></u></td><td><u><spring:message code="mdrtb.concentration" text="Concentration"/></u></td><td><u><spring:message code="mdrtb.result" text="Result"/></u></td><td><u><spring:message code="mdrtb.colonies" text="Colonies"/></u></td>
</tr>
<c:forEach var="drugType" items="${drugTypes}">
	<c:if test="${!empty resultsMap[drugType.id]}">
		<c:forEach var="dstResult" items="${resultsMap[drugType.id]}">
			<tr class="dstResult">
			<td><nobr>${dstResult.drug.displayString}</nobr></td>
			<td><nobr>${dstResult.concentration}</nobr></td>
			<td><nobr>${dstResult.result.displayString}</nobr></td>
			<td><nobr>${dstResult.colonies}</nobr></td>
			<td><button class="removeDstResult" value="${dstResult.id}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button>
				<input type="hidden" id="removeDstResult${dstResult.id}" name="removeDstResult" value=""/></td>
			</tr>
		</c:forEach>
	</c:if>
</c:forEach>
	
<!-- now the rows to add a new table -->
<!-- note that we just add thirty, blank, hidden rows here, which is kind of hacky, but makes the code simplier! :) -->
<!-- note that we have to add the test id to the tr so that jquery can find the right tr to hide/show 
	 when there are multiple dsts for a sample, but we don't need to add this count to the actual form fields because
	 we never update more than one test at any time -->
<c:forEach begin="1" end="30" varStatus="i">
	<tr id="addDstResult${test.id}_${i.count}" class="addDstResult" style="display:none">
	<td><select name="addDstResult${i.count}.drug">
		<option value=""></option>
		<c:forEach var="drug" items="${drugTypes}">
			<option value="${drug.id}">${drug.displayString}</option>
		</c:forEach>
		</select>
	</td>
	<td><input type="input" size="6" name="addDstResult${i.count}.concentration"/></td>
	<td><select name="addDstResult${i.count}.result" class="dstResult">
		<option value=""></option>
		<c:forEach var="possibleResult" items="${dstResults}">
			<option value="${possibleResult.id}">${possibleResult.displayString}</option>
		</c:forEach></td>
		</select>
	</td>
	<td><input type="text" size="6" name="addDstResult${i.count}.colonies" value="" class="dstColonies" style="display:none"/></td>
	<td><button class="removeDstResultRow" value="${i.count}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button></td>	
	</tr>
</c:forEach>

	<tr>
	<td><button class="addDstResultRow" value="${test.id}" type="button"><spring:message code="mdrtb.addDSTResult" text="Add DST result"/></button></td>
	<td colspan="4"/>
	</tr>
	
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button type="reset" id="${test.id}" class="cancel"><spring:message code="mdrtb.cancel" text="Cancel"/></button>

</form>

</div>
</div>

</c:forEach>

<!--  END OF EDIT TESTS SECTION -->

<!-- ADD TEST SECTION -->
<!--  TODO: would really like to combine this with the edit tests section, to avoid all this code repeating... -->


<c:forEach var="type" items="${testTypes}">

<div id="add_${type}" class="addBox" style="position:absolute; left:450px; top:30px; display:none; font-size:0.9em"">

<form id="${type}" action="specimen.form?submissionType=${type}&${type}Id=-1&testId=-1&specimenId=${specimen.id}&patientProgramId=${patientProgramId}" method="post">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${type}"/>: <spring:message code="mdrtb.add" text="Add"/></b>
<div class="box" style="margin:0px">
<table cellpadding="0">

<tr>
<td><nobr><spring:message code="mdrtb.accessionNumber" text="Accession #"/>:</nobr></td>
<td><input type="text" name="accessionNumber"/></td>
<td><nobr><spring:message code="mdrtb.dateOrdered" text="Date ordered"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><spring:message code="mdrtb.lab" text="Lab"/>:</td>
<td><select name="lab">
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}">${location.displayString}</option>
</c:forEach>
</select>
</td>
<td><nobr><spring:message code="mdrtb.dateSampleReceived" text="Date sample received"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateReceived" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td><nobr><spring:message code="mdrtb.method" text="Method"/>:</nobr></td>
<td><select name="method">
<option value=""></option>
<c:forEach var="method" items="${type eq 'smear'? smearMethods : (type eq 'culture' ? cultureMethods : dstMethods)}">
<option value="${method.answerConcept.id}">${method.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
<td><nobr><spring:message code="mdrtb.dateStarted" text="Date started"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<c:if test="${type eq 'smear' || type eq 'culture'}">
<td><nobr><spring:message code="mdrtb.results" text="Results"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${type eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</c:if>

<c:if test="${type eq 'dst'}">
<td><nobr><spring:message code="mdrtb.directIndirect" text="Direct/Indirect"/>:</nobr></td>
<td><select name="direct">
<option value=""></option>
<option value="1"><spring:message code="mdrtb.direct" text="Direct"/></option>
<option value="0"><spring:message code="mdrtb.indirect" text="Indirect"/></option>
</select></td>
</c:if>

<td><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue=""/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${type eq 'smear'}">
<tr class="bacilli" style="display:none;">
<td><nobr><spring:message code="mdrtb.numberofbacilli" text="Number of Bacilli"/>:</nobr></td>
<td><input type="text" name="bacilli" id="bacilli" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'culture'}">
<tr class="colonies" style="display:none;">
<td><nobr><spring:message code="mdrtb.numberofcolonies" text="Number of Colonies"/>:</nobr></td>
<td><input type="text" name="colonies" id="colonies" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>


<c:if test="${type eq 'culture' || type eq 'dst'}">
<tr>
<td><nobr><spring:message code="mdrtb.organismType" text="Organism Type"/>:</nobr></td>
<td><select name="organismType" class="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}">${organismType.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
<td colspan="2">&nbsp;</td>
</tr>
<tr class="organismTypeNonCoded" style="display:none;">
<td><nobr><spring:message code="mdrtb.organismTypeNonCoded" text="Organism Type Non-Coded"/>:</nobr></td>
<td><input type="text" name="organismTypeNonCoded" id="organismTypeNonCoded" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'dst'}">
<tr>
<td><nobr><spring:message code="mdrtb.coloniesincontrol" text="Colonies in control"/>:</nobr></td>
<td><input type="text" name="coloniesInControl" value=""/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" name="comments"></textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${type eq 'dst'}">
<br/>
<table cellpadding="0">

<tr>
<td><u><spring:message code="mdrtb.drug" text="Drug"/></u></td><td><u><spring:message code="mdrtb.concentration" text="Concentration"/></u></td><td><u><spring:message code="mdrtb.result" text="Result"/></u></td><td><u><spring:message code="mdrtb.colonies" text="Colonies"/></u></td>
</tr>
	
	<!-- now the rows to add a new table -->
	<!-- note that we just add thirty, blank, hidden rows here, which is kind of hacky, but makes the code simplier! :) -->
	<c:forEach begin="1" end="30" varStatus="i">
		<tr id="addDstResult_${i.count}" class="addDstResult" style="display:none">
		<td><select name="addDstResult${i.count}.drug">
			<option value=""></option>
			<c:forEach var="drug" items="${drugTypes}">
				<option value="${drug.id}">${drug.displayString}</option>
			</c:forEach>
			</select>
		</td>
		<td><input type="input" size="6" name="addDstResult${i.count}.concentration"/></td>
		<td><select name="addDstResult${i.count}.result" class="dstResult">
			<option value=""></option>
			<c:forEach var="possibleResult" items="${dstResults}">
				<option value="${possibleResult.id}">${possibleResult.displayString}</option>
			</c:forEach></td>
			</select>
		</td>
		<td><input type="text" size="6" name="addDstResult${i.count}.colonies" value="" class="dstColonies" style="display:none"/></td>
		<td><button class="removeDstResultRow" value="${i.count}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button></td>
		</tr>
	</c:forEach>

	<tr>
	<td><button class="addDstResultRow" value="" type="button"><spring:message code="mdrtb.addDSTResult" text="Add DST result"/></button></td>
	<td colspan="4"/>
	</tr>
	
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button class="cancel" type="reset"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>
</div>

</c:forEach> 

<!-- END ADD TEST SECTION -->

</div> <!-- END OF TEST DIV -->

</div> <!-- END OF SPECIMEN DIV -->


<!--  TODO: add footer back in once alignment is fixed -->
