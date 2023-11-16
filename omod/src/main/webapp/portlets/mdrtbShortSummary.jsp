<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>

<style type="text/css">
	.mdrListLabel {
		font-size: .8em;
	}
	.mdrListValue {
		font-size: .8em;
		font-weight: bold;
		color:blue;
	}
	.mdrListBigValue { 
		font-weight: bold;
		font-size: 1.0em;
	}
	.mdrListTable td {
		vertical-align: top;
		white-space:nowrap;
	}
</style>

<c:choose>
	<c:when test="${empty model.data}">
		<br/>
		<i>Il n'ya pas de patients</i>
	</c:when>
	<c:otherwise>
		<table cellspacing="0" cellpadding="2" border="1" class="mdrListTable" width="99%">	
			<tbody>
				<c:forEach var="patient" items="${model.data}" varStatus="s">
		
					<tr class="${s.index % 2 == 0 ? 'oddRow' : 'evenRow'}">
						<td>
							<span class="mdrListBigValue">
								<a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${patient.patientId}">
									<c:out value="${patient.fullName}"/>
								</a>
							</span><br/>
							<span class="mdrListLabel">Identifier:</span>
							<span class="mdrListValue"><c:out value="${patient.primaryIdentifier}"/></span><br/>
							<span class="mdrListLabel">Age:</span>
							<span class="mdrListValue"><c:out value="${patient.age}"/></span>&nbsp;|&nbsp;
							<span class="mdrListLabel">Sexe:</span>
							<span class="mdrListValue"><c:out value="${patient.gender}"/></span><br/>
							<span class="mdrListLabel">Statut VIH:</span> 
							<span class="mdrListValue">
								<c:out value="${patient['obs.RESULT OF HIV TEST.latest']}"/>								
								<c:if test="${!empty patient['obs.RESULT OF HIV TEST.latestDate']}">
									(<c:out value="${patient['obs.RESULT OF HIV TEST.latestDate']}"/>)
								</c:if>
							</span>
						</td>
						<td style="white-space:normal;">
							<span class="mdrListLabel">Regime Actuel:</span> 
							<span class="mdrListValue"><br/><c:out value="${patient.currentTbRegimen}"/></span>
							<br/>
							<span class="mdrListLabel">Hebergement:</span>
							<span class="mdrListValue"><br/><c:out value="${patient['state.6']}"/></span>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>