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
<td><form:input path="identifier" size="10" /><form:errors path="identifier" cssClass="error" /></td>
</tr>
 
<tr>
<td>Sample Type:</td>
<td><form:select path="type" multiple="false">
	<form:options items="${types}" itemValue="answerConcept.id" itemLabel="answerConcept.name" />
</form:select></td>
</tr>

<tr>
<td>Date Collected:</td>
<td><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/><form:errors path="dateCollected" cssClass="error" /></td>
</tr>

<!--  TODO: need to figure out how to map provider names properly: can we use the custom tags? -->

<tr>
<td>Collected By:</td>
<td><form:select path="provider" multiple="false">
	<form:options items="${providers}" itemValue="id" itemLabel="names[0].familyName" />
</form:select></td>
</tr>

<tr>
<td>Location Collected:</td>
<td><form:select path="location" multiple="false">
	<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
</form:select>	
</td>
</tr>

<tr>
<td>Comments:</td>
<td><form:textarea path="comments" cols="60" rows="4"/></td>
</tr>

</table>

<input type="submit" value="Save Specimen" />

</form:form>

</body>
</html>
