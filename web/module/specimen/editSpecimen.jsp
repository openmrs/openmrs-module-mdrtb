<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->


<form:form modelAttribute="specimen">
<form:errors path="*" cssClass="error" />

<table>

<!-- TODO localize all text -->

<!-- TODO is answerConcept.name the correct parameter? -->
<tr>
<td>Sample ID:</td>
<td><form:input path="id.valueNumeric" size="10" /><form:errors path="id.valueNumeric" cssClass="error" /></td>
</tr>
 
<tr>
<td>Sample Type:</td>
<td><form:select path="type.valueCoded" multiple="false">
	<form:options items="${types}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
</tr>

<tr>
<td>Date Collected:</td>
<td><openmrs_tag:dateField formFieldName="encounter.encounterDatetime" startValue="${specimen.encounter.encounterDatetime}"/><form:errors path="encounter.encounterDatetime" cssClass="error" /></td>
</tr>

<!--  TODO: need to figure out how to map provider names properly: can we use the custom tags? -->

<tr>
<td>Collected By:</td>
<td><form:select path="encounter.provider" multiple="false">
	<form:options items="${providers}" itemValue="id" itemLabel="names[0].familyName" />
</form:select></td>
</tr>

<tr>
<td>Location Collected:</td>
<td><form:select path="encounter.location" multiple="false">
	<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
</form:select>	
</td>
</tr>

</table>

<input type="submit" value="Save Specimen" />

</form:form>

</body>
</html>

