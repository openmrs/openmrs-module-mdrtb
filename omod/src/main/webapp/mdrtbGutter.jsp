<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>
<ul class="navList">
	<li id="homeNavLink" class="firstChild">
		<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbIndex.form"><mdrtb:message code="mdrtb.title.homepage" /></a>
	</li>
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li><a href="${pageContext.request.contextPath}/admin">
			<mdrtb:message code="Navigation.administration" /></a></li>
	</openmrs:hasPrivilege>
</ul>
