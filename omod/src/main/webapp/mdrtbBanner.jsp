
<div id="banner">
<a href="<spring:theme code="url.organization" />">
  <div id="logosmall"><img src="<%= request.getContextPath() %><spring:theme code="image.logo.text.small" />" alt="OpenMRS Logo" border="0"/></div>
</a>  
<table id="bannerbar">
  <tr>
    <td id="logocell"> <img src="<%= request.getContextPath() %><spring:theme code="image.logo.small" />" alt="" class="logo-reduced61" />
    </td>
	<td id="barcell">   <!-- need to custom-align this bar since some mdrtb pages set tds to vertical-align: top  -->
        <div class="barsmall">
        <img align="left" src="<%= request.getContextPath() %><spring:theme code="image.logo.bar" />" alt="" class="bar-round-reduced50"/>
        <openmrs:hasPrivilege privilege="View Navigation Menu">
				<%@ include file="mdrtbGutter.jsp" %>
		</openmrs:hasPrivilege>
        </div>
    </td>
  </tr>
</table>
</div>