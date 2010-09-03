<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb"
	uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css" />

<style>
.formName {
	text-align: center;
	background-color: #e0e0ff;
}

.section {
	border: 1px solid #8FABC7;
	width: 99%;
	padding: 2px;
	text-align: left;
	margin-bottom: 10px;
}

.sectionHeader {
	background-color: #8FABC7;
	color: white;
	display: block;
	padding: 2px;
	font-weight: bold;
}

.greyedout {
	color: #c0c0c0;
}

.highlighted {
	background-color: #FFE87C;
}

table.symptoms th {
	text-align: right;
}

.note {
	color: red;
}

td {
	vertical-align: top;
}
</style>

<div style="width: 100%;">

<div align="center"><span class="sectionHeader">FORMULAIRE
DE DEMANDE D'EXAMENS D'EXPECTORATION</span></div>

<!--  DISPLAY ANY ERROR MESSAGES --> <c:if
	test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}" /></span>
		<br />
	</c:forEach>
	<br />
</c:if>

<form name="msppForm"
	action="MSPPForm.form?encounterId=${encounterId}&patientId=${person.id}"
	method="post">

<table width="100%">
	<tr>
		<td width="50%" class="greyedout">Date: ...</td>
		<td class="highlighted">Nom de l'&eacute;tablissement: <select
			name="location">
			<option value=""></option>
			<c:forEach var="location" items="${locations}">
				<option value="${location.locationId}"
					<c:if test="${location == msppForm.location}">selected</c:if>>${location.displayString}</option>
			</c:forEach>
		</select></td>
	</tr>
</table>

Nom et Pr&eacute;nom du malade: ${person.personName} <br />
<span class="greyedout">Adresse Complete du malade: </span> <br />

<div class="highlighted">(a) Caract&eacute;ristique de l'expectorant <select
	name="appearance">
	<option value=""></option>
	<c:forEach var="appearance" items="${appearances}">
		<option value="${appearance.answerConcept.id}"
			<c:if test="${msppForm.appearance == appearance.answerConcept}">selected</c:if>>${appearance.answerConcept.displayString}</option>
	</c:forEach>
</select>
</td>
</td>
<br />
(b) Microscopie
<table>
	<tr>
		<th>Echantillon</th>
		<th>Resultat et Date</th>
	</tr>
	<tr>
		<td>1</td>
		<td><select name="specimens[0].smears[0].result">
			<option value=""></option>
			<c:forEach var="result" items="${smearResults}">
				<option value="${result.answerConcept.id}"
					<c:if test="${result.answerConcept == msppForm.specimens[0].smears[0].result}">selected</c:if>>${result.answerConcept.displayString}</option>
			</c:forEach>
		</select> <openmrs_tag:dateField formFieldName="specimens[0].dateCollected"
			startValue="${msppForm.specimens[0].dateCollected}" /></td>
	</tr>
	<tr>
		<td>2</td>
		<td><select name="specimens[1].smears[0].result">
			<option value=""></option>
			<c:forEach var="result" items="${smearResults}">
				<option value="${result.answerConcept.id}"
					<c:if test="${result.answerConcept == msppForm.specimens[1].smears[0].result}">selected</c:if>>${result.answerConcept.displayString}</option>
			</c:forEach>
		</select> <openmrs_tag:dateField formFieldName="specimens[1].dateCollected"
			startValue="${msppForm.specimens[1].dateCollected}" /></td>
	</tr>
	<tr>
		<td>3</td>
		<td><select name="specimens[2].smears[0].result">
			<option value=""></option>
			<c:forEach var="result" items="${smearResults}">
				<option value="${result.answerConcept.id}"
					<c:if test="${result.answerConcept == msppForm.specimens[2].smears[0].result}">selected</c:if>>${result.answerConcept.displayString}</option>
			</c:forEach>
		</select> <openmrs_tag:dateField formFieldName="specimens[2].dateCollected"
			startValue="${msppForm.specimens[2].dateCollected}" /></td>
	</tr>
</table>

<table width="100%">
	<tr>
		<td>Date: <openmrs_tag:dateField
			formFieldName="specimens[0].smears[0].resultDate"
			startValue="${msppForm.specimens[0].smears[0].resultDate}" /></td>
		<td>Examen effectu&eacute; par:<select name="provider">
			<option value=""></option>
			<c:forEach var="provider" items="${providers}">
				<option value="${provider.id}"
					<c:if test="${msppForm.provider == provider}">selected</c:if>>${provider.personName}</option>
			</c:forEach>
		</select></td>
	</tr>
</table>
</div>

<br />
<button type="submit"><spring:message code="mdrtb.save"
	text="Save" /></button>
<button type="reset"
	onClick="document.location.href='${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${person.id}'"><spring:message
	code="mdrtb.cancel" /></button>
</form>

</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>