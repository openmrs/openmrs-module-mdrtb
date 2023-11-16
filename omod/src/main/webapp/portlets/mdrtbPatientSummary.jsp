<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>

<style type="text/css">
	.mdrListLabel {
		font-size: .8em
	}
	.mdrListValue {
		font-weight: bold;
	}
	.mdrListBigValue { 
		font-weight: bold;
		font-size: 1.4em;
	}
	.mdrListTable td {
		vertical-align: top;
	}
</style>

<c:choose>
	<c:when test="${empty model.data}">
		<br/>
		<i>Il n'ya pas de patients</i>
	</c:when>
	<c:otherwise>
		<table cellspacing="0" cellpadding="2" border="1" class="mdrListTable" width="100%">	
			<tbody>
				<c:forEach var="patient" items="${model.data}">
		
					<tr bgcolor="#e0e0e0">
						<td colspan="2"><span class="mdrListBigValue"><c:out value="${patient.fullName}"/></span></td>
						<td nowrap><span class="mdrListLabel">Identifier:</span><br/><span class="mdrListValue"><c:out value="${patient.primaryIdentifier}"/></span></td>
						<td><span class="mdrListLabel">Age:</span><br/><span class="mdrListValue"><c:out value="${patient.age}"/></span></td>
						<td><span class="mdrListLabel">Sexe:</span><br/><span class="mdrListValue"><c:out value="${patient.gender}"/></span></td>
						<td><span class="mdrListLabel">Poids:</span><br/><span class="mdrListValue"><c:if test="${!empty patient.weight}"><c:out value="${patient.weight}"/> kgs.</c:if></span></td>
						<td><span class="mdrListLabel">Statut VIH:</span><br/><span class="mdrListValue"><c:out value="${patient.hivStatus}"/><c:if test="${!empty patient.hivStatus}"> (<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.hivStatusDate}"/>)</c:if></span></td>
						<td><span class="mdrListLabel">Statut MDR:</span><br/><span class="mdrListValue"><c:out value="${patient.mdrStatus}"/></span></td>
					</tr>
					<tr>
						<td colspan="2"><span class="mdrListLabel">Regime Actuel:</span><br/><span class="mdrListValue"><c:out value="${patient.currentRegimen}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Crachat Initiale:</span><br/><span class="mdrListValue"><c:out value="${patient.initialSmear}"/> <c:if test="${!empty patient.initialSmearDate}">(<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.initialSmearDate}"/>)</c:if></span></td>
						<td colspan="2"><span class="mdrListLabel">Date 1ere Traitement:</span><br/><span class="mdrListValue"><c:out value="${patient.treatmentStartDate}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Debut d'Hebergement:</span><br/><span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.hospitalizedDate}"/></span></td>
					</tr>
					<tr>
						<td colspan="2"><span class="mdrListLabel">Provenance:</span><br/><span class="mdrListValue"><c:out value="${patient.cityVillage}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Culture Initiale:</span><br/><span class="mdrListValue"><c:out value="${patient.initialCulture}"/> <c:if test="${!empty patient.initialCultureDate}">(<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.initialCultureDate}"/>)</c:if></span></td>
						<td colspan="2"><span class="mdrListLabel">Date 2eme Traitement:</span><br/><span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.empiricDate}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Fin d'Hebergement:</span><br/><span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.ambulatoryDate}"/></span></td>
					</tr>
					<tr>
						<td colspan="2"><span class="mdrListLabel">Antecedents Personnels:</span><br/><span class="mdrListValue"><c:out value="${patient.antecedents}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Bacilloscopie a l'admission:</span><br/><span class="mdrListValue"><c:if test="${!empty patient.smearAtAdmission}"><c:if test="${!empty patient.smearAtAdmission.valueCoded}"><c:out value="${patient.smearAtAdmission.valueCoded.name}"/> (<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.smearAtAdmission.obsDatetime}"/>)</c:if></c:if></span></td>
						<td colspan="2"><span class="mdrListLabel">Date Regime Definitif:</span><br/><span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.individualizedDate}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Derniere X-Ray:</span><br/><span class="mdrListValue"><c:out value="${patient.xRayLast}"/> <c:if test="${!empty patient.xRayLastDate}">(<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.xRayLastDate}"/>)</c:if></span></td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="mdrListLabel">Tel et Adresse #1:</span><span class="mdrListValue">
								<c:if test="${!empty patient.fullAddress }">
									<br/><c:out value="${patient.fullAddress}"/>
								</c:if>
								<c:if test="${!empty patient.telephoneNumber}">
									<br/><c:out value="${patient.telephoneNumber}"/>
								</c:if>
							</span>
						</td>
						<td colspan="4"><span class="mdrListLabel">Profil de Resistance/DST:</span><br/><span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.dstResultParentDate}"/> <c:out value="${patient.resistanceList}"/> </span></td>
						<td colspan="2"><span class="mdrListLabel">Derniere AFB:</span><br/><span class="mdrListValue"><c:out value="${patient.smearLast}"/> <c:if test="${!empty patient.smearLastDate}">(<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.smearLastDate}"/>)</c:if></span></td>
					</tr>
					<tr>
						<td colspan="2"><span class="mdrListLabel">Personne Responsible:</span><br/><span class="mdrListValue"><c:out value="${patient.guardianFirst}"/> <c:out value="${patient.guardianLast}"/> <c:out value="${patient.guardianPhone}"/></span></td>
						<td colspan="4"><span class="mdrListLabel">Traitement Empirique:</span><br/><span class="mdrListValue"><c:out value="${patient.empiricRegimen}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Derniere Culture:</span><br/><span class="mdrListValue"><c:out value="${patient.cultureLast}"/> <c:if test="${!empty patient.cultureLastDate}">(<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.cultureLastDate}"/>)</c:if></span></td>
					</tr>
					<tr>
						<td colspan="2"><span class="mdrListLabel">Tel et Adresse #2:</span></td>
						<td colspan="4"><span class="mdrListLabel">Traitement Personalis�:</span><br/><span class="mdrListValue"><c:out value="${patient.individualizedRegimen}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Derniere Bilan Thyroidien</span><br/><span class="mdrListValue"><c:out value="${patient.thyroidLast} <c:if test="${!empty patient.thyroidLastDate}">(<openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.thyroidLastDate}"/>)</c:if></span></td>
					</tr>
					<tr bgcolor="#f0f0f0">
						<td colspan="4"><span class="mdrListLabel">Effects Secondaires / Type de toxicite:</span></td>
						<td colspan="1"><span class="mdrListLabel">Date:</span></td>
						<td colspan="1"><span class="mdrListLabel">Traitement supportif:</span></td>
						<td colspan="2"><span class="mdrListLabel">Medicament incrimine:</span></td>
					</tr>
					<c:forEach var="allergy" items="${patient.allergies}">
					<tr>
						<td colspan="4"><span class="mdrListLabel"></span> <span class="mdrListValue"><c:out value="${allergy.effect}"/></span></td>
						<td colspan="1"><span class="mdrListLabel"></span> <span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${allergy.date}"/></span></td>
						<td colspan="1"><span class="mdrListLabel"></span> <span class="mdrListValue"><c:out value="${allergy.supportingTreatment}"/></span></td>
						<td colspan="2"><span class="mdrListLabel"></span> <span class="mdrListValue"><c:out value="${allergy.medication}"/></span></td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="4"><span class="mdrListLabel">Localisation:</span><br/><span class="mdrListValue"><c:if test="${!empty patient.pulmonaryDate}">P</c:if><c:if test="${!empty patient.extraPulmonaryDate}">EP</c:if></span></td>
						<td colspan="4"><span class="mdrListLabel">Type de resistance:</span><br/><span class="mdrListValue"><c:out value="${patient.typeOfResistance}"/></span></td>
					</tr>
					<tr bgcolor="#f0f0f0">
						<td colspan="2"><span class="mdrListLabel">Date R�ception</span><br/><span class="mdrListValue"><c:out value="${patient.startDate}"/></span></td>
						<td colspan="2"><span class="mdrListLabel">Date Prochain RV:</span><br/><span class="mdrListValue"><openmrs:formatDate format="${_dateFormatDisplay}" date="${patient.returnVisitDate}"/></span></td>
						<td colspan="4"><span class="mdrListLabel">Sign�:</span><br/></td>
					</tr>
					<tr>
						<td colspan="8" style="background-color:black; height:7px;"> </td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>