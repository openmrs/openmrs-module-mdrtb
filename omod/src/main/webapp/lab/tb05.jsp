<%@page import="org.openmrs.module.labmodule.specimen.LabResult"%>
<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/dotsHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>
<%@ page import="org.openmrs.module.labmodule.TbConcepts, org.openmrs.module.labmodule.service.TbService,org.openmrs.api.context.Context" %>

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<style type="text/css">.reportTable th,td {
		text-align:left; vertical-align:top;
	}
	simple-html-dataset table {
		width:100%; padding:5px; border:1px solid black;
	}
</style>
<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		$j('#reportData').load(function(event){
			var ht = $j("#reportData").height($j(window).height()-110);
		});
		$j('#cancelButton').click(function(event){
			document.location.href = '${pageContext.request.contextPath}/module/labmodule/labIndex.form';
		});
	});
</script>

<div id="page">
<table border="1" cellpadding="0" cellspacing="0" width="102%">
	<tbody>
		<tr>
			<td colspan="23" valign="top" width="68%">
			<p align="center"><strong><spring:message code="labmodule.tb05.labReferral"/> </strong></p>
			</td>
			<td colspan="14" rowspan="4" valign="top" width="31%">
			<p align="center"><strong><spring:message code="labmodule.tb05.ministryOfHealth"/></strong></p>

			</td>
		</tr>
		<tr>
			<td valign="top" width="11%">
			<p><strong><spring:message code="labmodule.tb05.medFac"/></strong></p>
			</td>
			<td colspan="22" valign="top" width="2%">
			<p><em>${medFacCode}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="3" valign="top" width="18%">
			<p><strong><spring:message code="labmodule.tb05.dirDate"/></strong></p>
			</td>
			<td colspan="20" valign="top">
			<p align="center"><em>${cultureDirectionDate}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="10" valign="top" width="34%">
			<p><strong><spring:message code="labmodule.tb05.passport"/></strong></p>
			</td>
			<td colspan="13" valign="top">${passport}</td>
		</tr>
		<tr>
			<td colspan="3" rowspan="2" valign="top">
			<p><strong><spring:message code="labmodule.tb05.patientName"/></strong></p>
			</td>
			<td colspan="14" valign="top" width="3%"><em>${patientName}</em></td>
			<td colspan="7" valign="top" width="12%">
			<p><strong><spring:message code="labmodule.tb05.DOB"/></strong></p>
			</td>
			<td colspan="22" valign="top">
			<p align="center"><em>${dob}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="14" valign="top" width="3%"><em></em></td>
			<td colspan="9" valign="top">
			<p><strong><spring:message code="labmodule.tb05.sex"/></strong></p>
			</td>
			<td colspan="2" valign="top" width="3%">
			<p align="center">${gender}</p>
			</td>
			<td colspan="2" valign="top" width="4%"></td>
			<td colspan="3" valign="top" width="6%"></td>
		</tr>
		<tr>
			<td colspan="3" valign="top">
			<p><strong><spring:message code="labmodule.tb05.tel"/></strong></p>
			</td>
			<td colspan="14" valign="top"></td>
		</tr>
		<tr>
			<td colspan="3" rowspan="2" valign="top">
			<p><strong><spring:message code="labmodule.tb05.fullAddr"/></strong></p>
			</td>
			<td colspan="34" valign="top"><em>${add1Line1}</em></td>
		</tr>
		<tr>
			<td colspan="34" valign="top"><em>${add1Line2}</em></td>
		</tr>
		<tr>
			<td colspan="3" rowspan="2" valign="top">
			<p><strong><spring:message code="labmodule.tb05.actAddr"/></strong></p>
			</td>
			<td colspan="34" valign="top"><em>${add2Line1}</em></td>
		</tr>
		<tr>
			<td colspan="34" valign="top"><em>${add2Line2}</em></td>
		</tr>
		<tr>
			<td colspan="3" valign="top" width="18%">
			<p><strong><spring:message code="labmodule.tb05.purpose"/></strong></p>
			</td>
			<td colspan="13" valign="top" width="31%">
			<p align="right"><strong><em><spring:message code="labmodule.tb05.diagNew"/></em></strong></p>
			</td>
			<td colspan="2" valign="top">
			<p><strong><em>${investigationPurposeNewCase}</em></strong></p>
			</td>
			<td colspan="15" valign="top" width="35%">
			<p align="right"><strong><em><spring:message code="labmodule.tb05.diagRep"/></em></strong></p>
			</td>
			<td colspan="3" valign="top">${investigationPurposeRepeat}</td>
			<td valign="top"></td>
		</tr>
	</tbody>
</table>


<table>
</table>

<table border="1" cellpadding="0" cellspacing="0" width="102%">
	<tbody>
		<tr>
			<td colspan="5" valign="top">
			<p><strong><spring:message code="labmodule.tb05.healthCareProvName"/></strong></p>
			</td>
			<td colspan="32" valign="top"><em>${referredBy}</em></td>
		</tr>
		<tr>
			<td colspan="5" valign="top">
			<p><strong><spring:message code="labmodule.tb05.tel"/></strong></p>
			</td>
			<td colspan="32" valign="top"></td>
		</tr>
	</tbody>
</table>


<table border="1" cellpadding="0" cellspacing="0" width="102%">
	<tbody>
		<tr>
			<td colspan="3" valign="top">
			<p><strong><spring:message code="labmodule.tb05.sampleTyp"/></strong></p>
			</td>
			<td colspan="6" valign="top">
			<p><strong><spring:message code="labmodule.tb05.sputum"/></strong></p>
			</td>
			<td colspan="4" valign="top">
			<p align="center"><strong>${sampleSputum} </strong></p>
			</td>
			<td colspan="6" valign="top">
			<p><strong><spring:message code="labmodule.tb05.indSputum"/></strong></p>
			</td>
			<td colspan="4" valign="top">
			<p><strong>${sampleInducedSputum}</strong></p>
			</td>
			<td colspan="10" valign="top">
			<p><strong><spring:message code="labmodule.tb05.exp"/></strong></p>
			</td>
			<td colspan="4" valign="top" width="22%">${sampleOther }</td>
		</tr>
		<tr>
			<td colspan="3" valign="top" width="12%">
			<p><strong><spring:message code="labmodule.tb05.collectionDate"/></strong></p>
			</td>
			<td colspan="9" valign="top" width="6%">
			<p><strong>No</strong> <strong> 1</strong></p>
			</td>
			<td colspan="6" valign="top" width="3%">
			<p align="center"><em>${micCollectionDate1}</em></p>
			</td>
			<td colspan="10" valign="top" width="8%">
			<p><strong>No</strong> <strong> 2</strong></p>
			</td>
			<td colspan="6" valign="top" width="4%">
			<p align="center"><em><em>${micCollectionDate2}</em></em></p>
			</td>
		</tr>
		<tr>
			<td colspan="37" valign="top">
			<p align="center"><strong><spring:message code="labmodule.tb05.microscopyResult"/></strong></p>
			</td>
		</tr>
		<tr>
			<td colspan="7" valign="top" width="18%">
			<p><strong><spring:message code="labmodule.tb05.microscopyDate"/></strong></p>
			</td>
			<td colspan="7" valign="top" width="13%">
			<p><strong><spring:message code="labmodule.tb05.sampleAppr"/></strong></p>
			</td>
			<td colspan="9" valign="top" width="24%">
			<p align="center"><strong><spring:message code="labmodule.tb05.labNo"/></strong></p>
			</td>
			<td colspan="7" valign="top" width="15%">
			<p align="center"><strong><spring:message code="labmodule.tb05.res"/></strong></p>
			</td>
			<td colspan="7" valign="top" width="26%">
			<p align="center"><strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong></p>
			</td>
		</tr>
		<tr>
			<td colspan="7" valign="top">
			<p align="center"><em>${micDate1}</em></p>
			</td>
			<td colspan="7" valign="top">
			<p align="center"><em>${micSample1}</em></p>
			</td>
			<td colspan="9" valign="top">
			<p align="center"><em>${micLabNo1}</em></p>
			</td>
			<td colspan="7" valign="top">
			<p align="center"><em>${micResult1}</em></p>
			</td>
			<td colspan="7" valign="top">
			<p align="center"></p>
			</td>
		</tr>
		<tr>
			<td colspan="7" valign="top">
			<p align="center"><em>${micDate2}</em></p>
			</td>
			<td colspan="7" valign="top">
			<p align="center"><em>${micSample2}</em></p>
			</td>
			<td colspan="9" valign="top">
			<p align="center"><em>${micLabNo2}</em></p>
			</td>
			<td colspan="7" valign="top">
			<p align="center"><em>${micResult1}</em></p>
			</td>
			<td colspan="7" valign="top">
			<p align="center"></p>
			</td>
		</tr>
		<tr>
			<td colspan="6" valign="top" width="14%">
			<p></p>
			</td>
			<td colspan="31" valign="top" width="84%">
			<p><strong><spring:message code="labmodule.tb05.specify"/></strong></p>
			</td>
		</tr>
		<tr>
			<td colspan="37" valign="top" width="98%">
			<p align="center"><strong><spring:message code="labmodule.tb05.molecularDiagResult"/> </strong></p>
			</td>
		</tr>
		<tr>
		<td colspan="3 valign=" align="center" valign="top" width="16%">
		<p align="center"><strong><spring:message code="labmodule.tb05.test"/></strong></p>
		</td>
		<td colspan="3 valign=" align="center" valign="top" width="4%">
		<p align="center"><strong><spring:message code="labmodule.tb05.mtb"/></strong></p>
		</td>
		<td colspan="3 valign=" align="center" valign="top" width="4%">
		<p align="center"><strong><spring:message code="labmodule.tb05.rif"/></strong></p>
		</td>
		<td colspan="3 valign=" align="center" valign="top" width="4%">
		<p align="center"><strong><spring:message code="labmodule.tb05.h"/></strong></p>
		</td>
		<td colspan="3" valign="top" width="4%">
		<p align="center"><strong><spring:message code="labmodule.tb05.flq"/></strong></p>
		</td>
		<td colspan="3" valign="top" width="9%">
		<p align="center"><strong><spring:message code="labmodule.tb05.kmcmam"/></strong></p>
		</td>
		<td colspan="3" valign="top" width="12%">
		<p align="center"><strong><spring:message code="labmodule.tb05.russ"/></strong></p>
		</td>
		<td colspan="7" valign="top" width="12%">
		<p align="center"><strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong></p>
		</td>
		<td colspan="6" valign="top" width="15%">
		<p><strong><spring:message code="labmodule.tb05.cultureTest"/></strong></p>
		</td>
		</tr>
		<tr>
		<td colspan="3" valign="top" width="16%">
		<p><strong><spring:message code="labmodule.tb05.xpert"/></strong></p>
		</td>
		<td colspan="3" valign="top" width="7%">${xpertMtb}</td>
		<td colspan="3" valign="top" width="9%">${xpertRif}</td>
		<td colspan="3" valign="top" width="4%"></td>
		<td colspan="3" valign="top" width="4%"></td>
		<td colspan="3" valign="top" width="4%"></td>
		<td colspan="3" valign="top" width="9%"></td>
		<td colspan="3" valign="top" width="12%"></td>
		<td colspan="7" valign="top" width="12%"></td>
		<td colspan="6" valign="top" width="15%"></td>
		</tr>
		<tr>
		<td colspan="3" valign="top" width="16%">
		<p><strong><spring:message code="labmodule.tb05.mtbdr"/></strong></p>
		</td>
		<td colspan="3" valign="top" width="7%">${hainMtb}</td>
		<td colspan="3" valign="top" width="9%">${hainRif}</td>
		<td colspan="3" valign="top" width="4%">${hainH}</td>
		<td colspan="3" valign="top" width="4%"></td>
		<td colspan="3" valign="top" width="4%"></td>
		<td colspan="3" valign="top" width="9%"></td>
		<td colspan="3" valign="top" width="12%"></td>
		<td colspan="7" valign="top" width="12%"></td>
		<td colspan="6" valign="top" width="15%"></td>
		</tr>
		<tr>
		<td colspan="3" valign="top" width="16%">
		<p><strong><spring:message code="labmodule.tb05.mtbdrsl"/></strong></p>
		</td>
		<td colspan="3" valign="top" width="7%">${hain2Mtb}</td>
		<td colspan="3" valign="top" width="9%"></td>
		<td colspan="3" valign="top" width="4%"></td>
		<td colspan="3" valign="top" width="4%">${hain2Flq}</td>
		<td colspan="3" valign="top" width="4%">${hain2KmCmAm}</td>
		<td colspan="3" valign="top" width="9%">${hain2E}</td>
		<td colspan="3" valign="top" width="12%"></td>
		<td colspan="7" valign="top" width="12%"></td>
		<td colspan="6" valign="top" width="15%"></td>
		</tr>
		<tr>
		<td colspan="3" valign="top" width="16%">
		<p><strong><spring:message code="labmodule.tb05.investigationDate"/></strong></p>
		</td>
		<td colspan="8" valign="top" width="17%">
		<p><em>dd</em> <em>/</em> <em>mm</em> <em>/</em> <em>yy</em></p>
		</td>
		<td colspan="3" valign="top" width="14%">
		<p><strong><spring:message code="labmodule.tb05.resDate"/></strong></p>
		</td>
		<td colspan="8" valign="top" width="21%">
		<p align="center"><em>dd</em> <em>/</em> <em>mm</em> <em>/</em> <em>yy</em></p>
		</td>
		<td colspan="3" valign="top" width="12%">
		<p><strong><spring:message code="labmodule.tb05.recDate"/></strong></p>
		<p><strong><spring:message code="labmodule.tb05.res"/></strong></p>
		</td>
		<td colspan="12" valign="top" width="15%">
		<p align="center"><em>dd</em> <em>/</em> <em>mm</em> <em>/</em> <em>yy</em></p>
		</td>
		</tr>
		</tbody>
		</table>

	<p></p>
	<br clear="ALL" />

	<table border="1" cellpadding="0" cellspacing="0" width="102%">
	<tbody>
		<tr>
			<td colspan="7" valign="top" width="25%">
			<p align="center"><strong><spring:message code="labmodule.tb05.cultRes"/></strong></p>
			</td>
			<td colspan="9" valign="top" width="23%">
			<p align="center"><strong><spring:message code="labmodule.tb05.labNo"/>;_____${cultureLabNo}________</strong></p>
			</td>
			<td rowspan="22" valign="top" width="3%"></td>
			<td colspan="17" valign="top" width="24%">
			<p align="center"><strong><spring:message code="labmodule.tb05.dstRes"/></strong></p>
			</td>
			<td colspan="15" valign="top" width="22%">
			<p align="center"><strong><spring:message code="labmodule.tb05.labNo"/>;________/________</strong></p>
			</td>
		</tr>
		<tr>
			<td colspan="2" valign="top" width="8%">
			<p><strong><spring:message code="labmodule.tb05.medFac"/></strong></p>
			</td>
			<td colspan="5" valign="top">
			<p><em>${medFacCode}</em></p>
			</td>
			<td colspan="9" valign="top" width="23%"></td>
			<td colspan="5" valign="top" width="9%">
			<p><strong><spring:message code="labmodule.tb05.medFac"/></strong></p>
			</td>
			<td colspan="12" valign="top">
			<p><em>${medFacCode}</em></p>
			</td>
			<td colspan="15" valign="top" width="22%"></td>
		</tr>
		<tr>
			<td colspan="8" valign="top" width="28%">
			<p><strong><spring:message code="labmodule.tb05.dirDate"/></strong></p>
			</td>
			<td colspan="8" valign="top">${cultureDirectionDate}
			<p align="center"></p>
			</td>
			<td colspan="18" valign="top" width="29%">
			<p><strong><spring:message code="labmodule.tb05.dirDate"/></strong></p>
			</td>
			<td colspan="14" valign="top" width="3%">
			<p align="center"><em>${cultureDirectionDate}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="4" valign="top" width="15%">
			<p><strong>P<spring:message code="labmodule.tb05.passport"/></strong></p>
			</td>
			<td colspan="12" valign="top">${passport}</td>
			<td colspan="7" valign="top" width="13%">
			<p><strong><spring:message code="labmodule.tb05.passport"/></strong></p>
			</td>
			<td colspan="25" valign="top">${passport}</td>
		</tr>
		<tr>
			<td colspan="8" valign="top" width="28%">
			<p><strong><spring:message code="labmodule.tb05.patientName"/></strong></p>
			</td>
			<td colspan="8" valign="top"><em>${patientName}</em></td>
			<td colspan="18" valign="top" width="29%">
			<p><strong><spring:message code="labmodule.tb05.patientName"/></strong></p>
			</td>
			<td colspan="14" valign="top" width="3%"><em>${patientName}</em></td>
		</tr>
		<tr>
			<td colspan="4" valign="top" width="15%">
			<p><strong><spring:message code="labmodule.tb05.DOB"/></strong></p>
			</td>
			<td colspan="12" valign="top">
			<p align="center"><em>${dob}</em></p>
			</td>
			<td colspan="8" valign="top" width="16%">
			<p><strong><spring:message code="labmodule.tb05.DOB"/></strong></p>
			</td>
			<td colspan="24" valign="top" width="2%">
			<p align="center"><em>${dob}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="4" valign="top" width="11%">
			<p><strong><spring:message code="labmodule.tb05.sex"/></strong></p>
			</td>
			<td colspan="6" valign="top">
			<p>${gender}</p>
			</td>
			<td colspan="6" valign="top">
			<p></p>
			</td>
			<td colspan="6" valign="top" width="9%">
			<p><strong><spring:message code="labmodule.tb05.sex"/></strong></p>
			</td>
			<td colspan="13" valign="top">
			<p>${gender}</p>
			</td>
			<td colspan="13" valign="top">
			<p></p>
			</td>
		</tr>
		<tr>
			<td colspan="4" valign="top" width="11%">
			<p><strong><spring:message code="labmodule.tb05.tel"/></strong></p>
			</td>
			<td colspan="12" valign="top">${phone}</td>
			<td colspan="6" valign="top" width="9%">
			<p><strong><spring:message code="labmodule.tb05.tel"/></strong></p>
			</td>
			<td colspan="26" valign="top">${phone}</td>
		</tr>
		<tr>
			<td colspan="16" valign="top" width="49%">
			<p><strong><spring:message code="labmodule.tb05.actAddr"/></strong></p>
			</td>
			<td colspan="32" valign="top" width="47%">
			<p><strong><spring:message code="labmodule.tb05.actAddr"/></strong></p>
			</td>
		</tr>
		<tr>
			<td colspan="16" valign="top">
			<p><em>${add2Line1} </em></p>
			</td>
			<td colspan="32" valign="top">
			<p><em>${add2Line1}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="16" valign="top">
			<p><em>${add2Line2}</em></p>
			</td>
			<td colspan="32" valign="top">
			<p><em>${add2Line2}</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="8" valign="top" width="28%">
			<p><strong><spring:message code="labmodule.tb05.healthCareProvName"/></strong></p>
			</td>
			<td colspan="8" valign="top"><em>${referredBy} </em></td>
			<td colspan="18" valign="top" width="35%">
			<p><strong><spring:message code="labmodule.tb05.healthCareProvName"/></strong></p>
			</td>
			<td colspan="14" valign="top"><em>${referredBy} </em></td>
		</tr>
		<tr>
			<td colspan="4" valign="top" width="11%">
			<p><strong><spring:message code="labmodule.tb05.tel"/></strong></p>
			</td>
			<td colspan="12" valign="top">${phone}</td>
			<td colspan="6" valign="top" width="9%">
			<p><strong><spring:message code="labmodule.tb05.tel"/></strong></p>
			</td>
			<td colspan="26" valign="top">${phone}</td>
		</tr>
		<tr>
			<td colspan="3" width="11%">
			<p align="center"><strong><spring:message code="labmodule.tb05.cultDate"/></strong></p>
			</td>
			<td colspan="3" width="10%">
			<p align="center"><strong><spring:message code="labmodule.tb05.ljmgit"/></strong></p>
			</td>
			<td colspan="4" width="13%">
			<p align="center"><strong><spring:message code="labmodule.tb05.res"/></strong></p>
			</td>
			<td colspan="6" width="13%">
			<p align="center"><strong><spring:message code="labmodule.tb05.resDate"/></strong> <strong> </strong></p>
			</td>
			<td colspan="5" width="9%">
			<p align="center"><strong><spring:message code="labmodule.tb05.dstDate"/></strong></p>
			</td>
			<td colspan="21" width="27%">
			<p align="center"><strong><spring:message code="labmodule.tb05.resDST"/></strong></p>
			</td>
			<td colspan="6" width="10%">
			<p align="center"><strong><spring:message code="labmodule.tb05.resDate"/></strong> <strong> </strong></p>
			</td>
		</tr>
		<tr>
			<td colspan="3" valign="top">${cultureDateMgit}
			<p align="center"></p>
			</td>
			<td colspan="3" valign="top">
			<p align="center"><strong style="text-align: -webkit-center;"><spring:message code="labmodule.tb05.mgit"/></strong><span style="text-align: -webkit-center;"></span></p>
			</td>
			<td colspan="4" valign="top">${cultureResultMgit}
			<p align="center"></p>
			</td>
			<td colspan="6" valign="top">${cultureResultDateMgit}
			<p></p>
			</td>
			<td colspan="5"><em>${dst1Date}</em></td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.s"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstS}</p>
			</td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.ofx"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstOfx}</p>
			</td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.pto"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstPto}</p>
			</td>
			<td colspan="3" valign="top">
			<p><em><spring:message code="labmodule.tb05.bdq"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstBdq}</p>
			</td>
		</tr>
		<tr>
			<td colspan="3" valign="top">${cultureDateLj}
			<p align="center"></p>
			</td>
			<td colspan="3" valign="top">
			<p align="center"><strong style="text-align: -webkit-center;"><spring:message code="labmodule.tb05.lj"/></strong></p>
			</td>
			<td colspan="4" valign="top">
			<p align="center"><i>${cultureResultLj}</i></p>
			</td>
			<td colspan="6" valign="top">${cultureResultDateLj}
			<p></p>
			</td>
			<td colspan="5"><em>${dst2Date}</em></td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.h"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstH}</p>
			</td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.mfx"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstMfx}</p>
			</td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.pas"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstPas}</p>
			</td>
			<td colspan="3" valign="top">
			<p></p>
			</td>
			<td colspan="3" valign="top">
			<p></p>
			</td>
		</tr>
		<tr>
			<td colspan="7" valign="top" width="25%">
			<p><strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong></p>
			</td>
			<td colspan="9" valign="top" width="23%"></td>
			<td colspan="5"><em></em></td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.r"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstR}</p>
			</td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.cm"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstCm}</p>
			</td>
			<td colspan="4" valign="top">
			<p><em><spring:message code="labmodule.tb05.cfz"/></em></p>
			</td>
			<td colspan="3" valign="top">
			<p>${dstCfz}</p>
			</td>
			<td colspan="3" valign="top">
			<p></p>
			</td>
			<td colspan="3" valign="top">
			<p></p>
			</td>
		</tr>
		<tr>
			<td colspan="16" valign="top">` <em><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></em></td>
			<td colspan="4" valign="top" width="6%">
			<p><strong><spring:message code="labmodule.tb05.mgit"/></strong></p>
			</td>
			<td valign="top" width="3%"></td>
			<td colspan="4" valign="top">
			<p align="center"></p>
			</td>
			<td colspan="3" valign="top">
			<p align="center"></p>
			</td>
			<td colspan="4" valign="top" width="5%">
			<p><strong><spring:message code="labmodule.tb05.km"/></strong></p>
			</td>
			<td colspan="3" valign="top" width="4%">
			<p>${dstKm}</p>
			</td>
			<td colspan="4" valign="top" width="6%">
			<p><strong><spring:message code="labmodule.tb05.lzd"/></strong></p>
			</td>
			<td colspan="3" valign="top" width="4%">${dstLzd}</td>
			<td colspan="3" valign="top" width="5%">
			<p align="center"><em>ETH</em></p>
			</td>
			<td colspan="3" valign="top" width="5%">
			<p align="center"><em>ETH</em></p>
			</td>
		</tr>
		<tr>
			<td colspan="8" valign="top" width="28%">
			<p><strong><spring:message code="labmodule.tb05.resDate"/></strong></p>
			</td>
			<td colspan="8" valign="top">${cultureResultDate}
			<p align="center"></p>
			</td>
			<td colspan="4" valign="top" width="6%">
			<p><strong><spring:message code="labmodule.tb05.lj"/></strong></p>
			</td>
			<td valign="top" width="3%"></td>
			<td valign="top" width="">
			<p><strong><spring:message code="labmodule.tb05.z"/></strong></p>
			</td>
			<td colspan="4" valign="top" width="4%">
			<p>${dstZ}</p>
			</td>
			<td colspan="4" valign="top" width="5%">
			<p><strong><spring:message code="labmodule.tb05.am"/></strong></p>
			</td>
			<td colspan="6" valign="top" width="4%">
			<p>${dstAm}</p>
			</td>
			<td colspan="4" valign="top" width="6%">
			<p><strong><spring:message code="labmodule.tb05.dlm"/></strong></p>
			</td>
			<td colspan="2" valign="top" width="4%">${dstDlm}</td>
			<td colspan="3" valign="top" width="5%"></td>
			<td colspan="3" valign="top" width="5%"></td>
		</tr>
		<tr>
			<td colspan="8" valign="top" width="28%">
			<p><strong><spring:message code="labmodule.tb05.recDate"/></strong> <strong> </strong> <strong>result</strong></p>
			</td>
			<td colspan="8" valign="top">${cultureResultReceiveingDate}
			<p align="center"></p>
			</td>
			<td colspan="15" valign="top" width="22%">
			<p><strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong></p>
			</td>
			<td colspan="17" valign="top" width="24%"><em> </em></td>
		</tr>
		<tr>
			<td colspan="16" rowspan="2" valign="top" width="49%">
			<p></p>
			</td>
			<td colspan="18" valign="top" width="25%">
			<p><strong><spring:message code="labmodule.tb05.resDate"/></strong></p>
			</td>
			<td colspan="19" valign="top" width="3%">
			<p align="center"><em></em></p>
			</td>
		</tr>
		<tr>
			<td colspan="18" valign="top" width="25%">
			<p><strong><spring:message code="labmodule.tb05.recDate"/></strong> <strong> </strong> <strong><spring:message code="labmodule.tb05.res"/></strong></p>
			</td>
			<td colspan="19" valign="top" width="3%">
			<p align="center"><em><spring:message code="labmodule.tb05.recDate"/></em></p>
			</td>
		</tr>
	</tbody>
</table>
</div>

 