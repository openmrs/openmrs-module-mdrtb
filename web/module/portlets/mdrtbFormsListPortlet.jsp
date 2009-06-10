			<%@ include file="/WEB-INF/template/include.jsp" %>
				
			<c:if test="${!empty mdrtbForms}">
				<table class="portletTable" style="font-size:80%;">
				
					
							
						<c:forEach items="${mdrtbForms}" var="form" varStatus="varStatus">
							<c:set var="rowClass" scope="page">
								<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>
							</c:set>
							<Tr><td class="${rowClass}"><a href='/openmrs/moduleServlet/formentry/formDownload?target=formentry&patientId=${obj.patient.patientId}&formId=${form.formId}'>${form.name}</a></td></Tr>
						</c:forEach>
						
					
				</table><br>
			</c:if>
			<c:if test="${empty mdrtbForms}">
				<span style="font-size:90%;">&nbsp;&nbsp;&nbsp;<i><spring:message code="mdrtb.none" /></i></span>
			</c:if>