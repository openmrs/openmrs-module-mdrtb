<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<style><%@ include file="/WEB-INF/view/module/mdrtb/resources/mdrtb.css"%></style>
<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->


<form:form modelAttribute="smear">
<form:errors path="*" cssClass="error" />

<table>

<!-- TODO localize all text -->

<!-- TODO is answerConcept.name the correct parameter? -->

<!-- TODO have to create a custom binders!! -->

<tr>
<td>Smear Result:</td>
<td><form:select path="smearResult.valueCoded" multiple="false">
	<form:options items="${results}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
</tr>

<tr>
<td>Bacilli:</td>
<td><form:input path="bacilli.valueNumeric" size="10" /><form:errors path="bacilli.valueNumeric" cssClass="error" /></td>
</tr>

<tr>
<td>Result Date:</td>
<td><openmrs_tag:dateField formFieldName="smearResultDate.valueDatetime" startValue="${smear.smearResultDate.valueDatetime}"/><form:errors path="smearResultDate.valueDatetime" cssClass="error" /></td>
</tr>

<tr>
<td>Date Received:</td>
<td><openmrs_tag:dateField formFieldName="smearDateReceived.valueDatetime" startValue="${smear.smearDateReceived.valueDatetime}"/><form:errors path="smearDateReceived.valueDatetime" cssClass="error" /></td>
</tr>
<tr>
<td>Smear Method:</td>
<td><form:select path="smearMethod.valueCoded" multiple="false">
	<form:options items="${methods}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
</tr>
<tr>
<td>Location:</td>
<td><form:select path="smearParentObs.location" multiple="false">
	<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
</form:select></td>
</tr>

</table>

<input type="submit" value="Save Smear Result" />

</form:form>

</body>
</html>

