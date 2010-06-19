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
<td><form:select path="result" multiple="false">
	<form:options items="${results}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
</tr>

<tr>
<td>Bacilli:</td>
<td><form:input path="bacilli" size="10" /><form:errors path="bacilli" cssClass="error" /></td>
</tr>

<tr>
<td>Result Date:</td>
<td><openmrs_tag:dateField formFieldName="resultDate" startValue="${smear.resultDate}"/><form:errors path="resultDate" cssClass="error" /></td>
</tr>

<tr>
<td>Date Received:</td>
<td><openmrs_tag:dateField formFieldName="dateReceived" startValue="${smear.dateReceived}"/><form:errors path="dateReceived" cssClass="error" /></td>
</tr>
<tr>
<td>Smear Method:</td>
<td><form:select path="method" multiple="false">
	<form:options items="${methods}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
</tr>
<tr>
<td>Lab:</td>
<td><form:select path="lab" multiple="false">
	<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
</form:select></td>
</tr>

</table>

<input type="submit" value="Save Smear Result" />

</form:form>

</body>
</html>

