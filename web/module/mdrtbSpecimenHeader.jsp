<%@ include file="mdrtbHeader.jsp"%>
<ul id="menu">
	<li class="first<c:if test='<%= request.getRequestURI().contains("mdrtbSpecimenRegistration") %>'> active</c:if>">
		<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbSpecimenRegistration.form">
			<spring:message code="mdrtb.specimenRegistrationLink" />
		</a>
	</li>
	<li <c:if test='<%= request.getRequestURI().contains("mdrtbSpecimenTracking") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbSpecimenTracking.form">
			<spring:message code="mdrtb.specimenTrackingLink" />
		</a>
	</li>
</ul>