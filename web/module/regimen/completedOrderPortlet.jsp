<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<table width="100%">
	<c:forEach items="${regimenHistoryGroups}" var="group">
		<c:if test="${empty type || type == group.key}">
			<tr>
				<td class="groupStyle" colspan="6" style="margin-top:5px;"><spring:message code="mdrtb.treatment.${group.key}"/></td>
			</tr>
			<c:choose>
				<c:when test="${empty group.value.pastDrugOrders}">
					<tr><td class="cellStyle" style="padding-left:10px;" colspan="6"><spring:message code="mdrtb.none"/></td></tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.dateStart" text="Start Date"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.actualStopDate" text="Stop Date"/></th>
						<th class="headerStyle" width="100%"><spring:message code="mdrtb.discontinuedReason" text="Reason Discontinued"/></th>
					</tr>
					<c:forEach items="${openmrs:sort(group.value.pastDrugOrders, 'discontinuedDate', false)}" var="drugOrder">
						<tr>
							<td style="text-align:left; padding-left:10px; padding-right:10px; white-space:nowrap;">${drugOrder.concept.name.name}</td>
							<td class="cellStyle">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.startDate}" format="dd/MMM/yyyy"/></td>
							<td class="cellStyle"><openmrs:formatDate date="${empty drugOrder.discontinuedDate ? drugOrder.autoExpireDate : drugOrder.discontinuedDate}" format="dd/MMM/yyyy"/></td>
							<td class="cellWrappable"><mdrtb:format obj="${drugOrder.discontinuedReason}"/></td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<tr><td colspan="6">&nbsp;</td></tr>
		</c:if>
	</c:forEach>
</table>
