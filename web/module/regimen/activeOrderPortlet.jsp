<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<table width="100%">
	<c:forEach items="${regimenHistoryGroups}" var="group">
		<c:if test="${empty type || type == group.key}">
			<c:set var="regimen" value="${group.value.activeRegimen}"/>
			<tr>
				<td class="groupStyle" colspan="6" style="margin-top:5px;"><spring:message code="mdrtb.treatment.${group.key}"/></td>
			</tr>
			<c:choose>
				<c:when test="${empty regimen.drugOrders && empty group.value.futureDrugOrders}">
					<tr><td class="cellStyle" style="padding-left:10px;" colspan="6"><spring:message code="mdrtb.none"/></td></tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.dateStart" text="Start Date"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.scheduledStopDate" text="Scheduled Stop Date"/></th>
					</tr>
					<c:forEach items="${openmrs:sort(group.value.futureDrugOrders, 'startDate', true)}" var="drugOrder">
						<tr>
							<td class="future" style="text-align:left; padding-left:10px; padding-right:10px; white-space:nowrap;">${drugOrder.concept.name.name}</td>
							<td class="cellStyle future">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle future">
								${drugOrder.frequency}
							</td>
							<td class="cellStyle future"><openmrs:formatDate date="${drugOrder.startDate}" format="dd/MMM/yyyy"/></td>
							<td class="cellStyle future"><openmrs:formatDate date="${drugOrder.autoExpireDate}" format="dd/MMM/yyyy"/></td>
						</tr>
					</c:forEach>
					<c:forEach items="${openmrs:sort(regimen.drugOrders, 'startDate', true)}" var="drugOrder">
						<tr>
							<td style="text-align:left; padding-left:10px; padding-right:10px; white-space:nowrap;">${drugOrder.concept.name.name}</td>
							<td class="cellStyle">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle">
								${drugOrder.frequency}
							</td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.startDate}" format="dd/MMM/yyyy"/></td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.autoExpireDate}" format="dd/MMM/yyyy"/></td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<tr><td colspan="6">&nbsp;</td></tr>
		</c:if>
	</c:forEach>
</table>
