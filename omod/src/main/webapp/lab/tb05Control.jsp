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

<html>
<body>
<div id="diagnostics">
<table border="1" cellspacing="0" cellpadding="0" width="103%">
    <tbody>
        <tr>
            <td width="69%" colspan="10" valign="top">
                <p align="center">
                    <strong>
                        <spring:message code="labmodule.tb05.referralControl"/>
                    </strong>
                </p>
            </td>
            <td width="30%" colspan="7" valign="top">
                <p align="center">
                    <strong>Form</strong>
                    <strong> </strong>
                    <strong>ТБ</strong>
                    <strong>05</strong>
                    <strong>k</strong>
                </p>
                <p align="center">
                    <strong> </strong>
                </p>
                <p align="center">
                    <strong>
                        Ministry of Health and Social Protection of Population
                    </strong>
                    <strong> of Republic of Tajikistan</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="11%" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.medFac"/></strong>
                    
                </p>
            </td>
            <td width="58%" colspan="9" valign="top">
                <p>
                    <em> ${medFacCode}</em>
                </p>
            </td>
            <td width="30%" colspan="7" rowspan="2" valign="top">
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" valign="top">
                <p>
                    <strong><strong><spring:message code="labmodule.tb05.dirDate"/></strong>
                </p>
            </td>
            <td width="29%" colspan="4" valign="top">
                <p align="center">
                    <em>${cultureDirectionDate}</em>
                </p>
            </td>
            <td width="21%" colspan="4" valign="top">
            </td>
        </tr>
        <tr>
            <td width="35%" colspan="4" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.passport"/></strong>
                </p>
            </td>
            <td width="34%" colspan="6" valign="top">
            ${passport}
            </td>
            <td width="30%" colspan="7" valign="top">
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.patientName"/></strong>
                </p>
            </td>
            <td width="48%" colspan="7" valign="top">
            ${patientName}
            </td>
            <td width="9%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.DOB"/></strong>
                </p>
            </td>
            <td width="23%" colspan="6" valign="top">
                <p align="center">
                    <em>${dob}</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="48%" colspan="7" valign="top">
            </td>
            <td width="9%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.sex"/></strong>
                </p>
            </td>
            <td width="4%" colspan="6" valign="top">
                <p align="center">  
           			${gender}
                </p>
            </td>
        </tr>
        <tr>
            <td width="28%" colspan="3" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tb03"/></strong>
                </p>
            </td>
            <td width="19%" colspan="3" valign="top">
                <p align="center">
                    <em>${tb03}</em>
                </p>
            </td>
            <td width="28%" colspan="5" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tb03u"/></strong>
                </p>
            </td>
            <td width="23%" colspan="6" valign="top">
                <p align="center">
                    <em></em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tel"/></strong>
                </p>
            </td>
            <td width="35%" colspan="6" valign="top">${phone}
            </td>
            <td width="46%" colspan="9" valign="top">
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.fullAddr"/></strong>
                </p>
            </td>
            <td width="81%" colspan="15" valign="top">
            ${add1Line1}
            </td>
        </tr>
        <tr>
            <td width="81%" colspan="15" valign="top">${add1Line2}
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.actAddr"/></strong>
                </p>
            </td>
            <td width="81%" colspan="15" valign="top">${add2Line1}
            </td>
        </tr>
        <tr>
            <td width="81%" colspan="15" valign="top">${add2Line2}
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" rowspan="3" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.purpose"/></strong>
                </p>
            </td>
            <td width="26%" colspan="3" valign="top">
                <p align="right">
                    <strong><em><spring:message code="labmodule.tb05.regime1"/></em></strong>
                </p>
            </td>
            <td width="5%" colspan="2" valign="top">
                <p>
                    <strong><em>${regime1} </em></strong>
                </p>
            </td>
            <td width="43%" colspan="9" valign="top">
                <p>
                    <strong><em><spring:message code="labmodule.tb05.regime2"/></em></strong>
                </p>
            </td>
            <td width="6%" valign="top">
            ${regime2}
            </td>
        </tr>
        <tr>
            <td width="31%" colspan="5" rowspan="2" valign="top">
                <p>
                    <strong><em> </em></strong>
                </p>
            </td>
            <td width="43%" colspan="9" rowspan="2" valign="top">
                <p align="right">
                    <strong><em>Intensive phase</em></strong>
                </p>
                <p align="right">
                    <strong><em>Supporting phase</em></strong>
                </p>
            </td>
            <td width="6%" valign="top">
            </td>
        </tr>
        
       
    </tbody>
</table>
<table border="1" cellspacing="0" cellpadding="0" width="103%">
    <tbody>
        <tr>
            <td width="30%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.healthCareProvName"/></strong>
                </p>
            </td>
            <td width="69%" valign="top">${referredBy}
            </td>
        </tr>
        <tr>
            <td width="16%" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tel"/></strong>
                </p>
            </td>
            <td width="83%" colspan="2" valign="top">
            ${phone }
            </td>
        </tr>
       
    </tbody>
</table>
<table border="1" cellspacing="0" cellpadding="0" width="110%">
    <tbody>
        <tr>
            <td width="14%" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.sampleTyp"/></strong>
                </p>
            </td>
            <td width="11%" colspan="4" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.sputum"/></strong>
                </p>
            </td>
            <td width="6%" colspan="3" valign="top">
                <p align="center">
                    <strong> ${sampleSputum}</strong>
                </p>
            </td>
            <td width="21%" colspan="12" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.indSputum"/></strong>
                </p>
            </td>
            <td width="5%" colspan="4" valign="top">
                <p>
                    <strong>${sampleInducedSputum} </strong>
                </p>
            </td>
            <td width="15%" colspan="8" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.exp"/></strong>
                </p>
            </td>
            <td width="20%" colspan="8" valign="top">${sampleOther}
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
        <tr>
            <td width="14%" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.collectionDate"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="5%" colspan="2" valign="top">
                <p align="center">
                    <strong>No</strong>
                    <strong> 1</strong>
                    <em></em>
                </p>
            </td>
            <td width="33%" colspan="16" valign="top">
                <p align="center">
                    <em>${micCollectionDate1}</em>
                </p>
            </td>
            <td width="8%" colspan="6" valign="top">
                <p align="center">
                    <strong>No</strong>
                    <strong> 2</strong>
                    <em></em>
                </p>
            </td>
            <td width="33%" colspan="15" valign="top">
                <p align="center">
                    <em>${micCollectionDate1}</em>
                </p>
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
        <tr>
            <td width="94%" colspan="40" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.microscopyResult"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
        <tr>
            <td width="17%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.microscopyDate"/></strong>
                </p>
            </td>
            <td width="13%" colspan="5" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.sampleAppr"/></strong>
                </p>
            </td>
            <td width="23%" colspan="14" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.labNo"/></strong>
                </p>
            </td>
            <td width="14%" colspan="8" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.res"/></strong>
                </p>
            </td>
            <td width="24%" colspan="11" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong>
                </p>
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
        <tr>
            <td width="17%" colspan="2" valign="top">
                <p align="center">
                    <em>${micDate1}</em>
                </p>
            </td>
            <td width="13%" colspan="5" valign="top">
            ${micSample1}
            </td>
            <td width="4%" colspan="14" valign="top">
            ${micLabNo1}
            </td>
            
            <td width="14%" colspan="8" valign="top">
            ${micResult1}
            </td>
            <td width="24%" colspan="11" rowspan="2" valign="top">
            ${micWorker1 }
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
        <tr>
            <td width="17%" colspan="2" valign="top">
                <p align="center">
                    <em>${micDate2}</em>
                </p>
            </td>
            <td width="13%" colspan="5" valign="top">
            ${micSample2}
            </td>
            <td width="4%" colspan="14" valign="top">
            ${micLabNo2}
            </td>
            <td width="14%" colspan="8" valign="top">
            ${micResult2}
            </td>
            <td width="24%" colspan="11" rowspan="2" valign="top">
            ${micWorker2 }
            </td>
            <td width="5%" colspan="3">            
            </td>
        </tr>
        <tr>
            <td width="94%" colspan="40" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.specify"/></strong>
                </p>
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
        <tr>
            <td width="17%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.investigationDate"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="13%" colspan="5" valign="top">
                <p>
                    <em> </em>
                </p>
            </td>
            <td width="15%" colspan="9" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.resDate"/></strong>
                </p>
            </td>
            <td width="14%" colspan="9" valign="top">
            </td>
            <td width="20%" colspan="10" valign="top">
                <p>
                    <strong><strong><spring:message code="labmodule.tb05.recDate"/></strong></strong>
                </p>
            </td>
            <td width="13%" colspan="5" valign="top">
            </td>
            <td width="5%" colspan="3">
            </td>
        </tr>
      
        
    </tbody>
</table>
<table border="1" cellspacing="0" cellpadding="0" width="101%">
    <tbody>
        <tr>
            <td width="49%" colspan="10" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.cultRes"/></strong>
                </p>
            </td>
            <td width="50%" colspan="11" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.labNo"/></strong>
                    <strong>___ ${cultureLabNo}____ </strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="18%" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.medFac"/></strong>
                </p>
            </td>
            <td width="26%" colspan="7" valign="top">
                <p>
                    <em>${medFacCode} </em>
                </p>
            </td>
            <td width="23%" colspan="5" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.dirDate"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="31%" colspan="8" valign="top">
                <p align="center">
                    <em>${cultureDirectionDate}</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="33%" colspan="5" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.passport"/></strong>
                </p>
            </td>
            <td width="66%" colspan="16" valign="top">
            ${passport}
            </td>
        </tr>
        <tr>
            <td width="22%" colspan="2" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.patientName"/></strong>
                    
                </p>
            </td>
            <td width="77%" colspan="19" valign="top">
            ${patientName }
            </td>
        </tr>
        <tr>
            <td width="77%" colspan="19" valign="top">
            </td>
        </tr>
        <tr>
            <td width="22%" colspan="3" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.DOB"/></strong>
                </p>
            </td>
            <td width="33%" colspan="9" valign="top">
                <p align="center">
                    <em>${dob}</em>
                </p>
            </td>
            <td width="15%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.sex"/></strong>
                </p>
            </td>
            <td width="8%" colspan="6" valign="top">
                <p>
                   ${gender}
                </p>
            
            </td>
            <td width="6%" valign="top">
            </td>
        </tr>
        <tr>
            <td width="33%" colspan="5" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tb03"/></strong>
                </p>
            </td>
            <td width="16%" colspan="6" valign="top">
                <p align="center">
                    <em>
                    ${tb03 }
                    </em>
                </p>
            </td>
            <td width="34%" colspan="6" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tb03u"/></strong>
                </p>
            </td>
            <td width="15%" colspan="4" valign="top">
                <p align="center">
                    <em></em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tel"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="76%" colspan="17" valign="top">
            ${phone }
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.actAddr"/></strong>
                </p>
            </td>
            <td width="76%" colspan="17" valign="top">
            ${add2Line1}
            </td>
        </tr>
        <tr>
            <td width="76%" colspan="17" valign="top">
            ${add2Line2}
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.healthCareProvName"/></strong>
                </p>
            </td>
            <td width="76%" colspan="17" valign="top">
            ${referredBy}
            </td>
        </tr>
        <tr>
            <td width="76%" colspan="17" valign="top">
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tel"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="76%" colspan="17" valign="top">${phone }
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.cultDate"/></strong>
                </p>
            </td>
            <td width="20%" colspan="3">
                <p align="center">
                    <strong>L</strong>
                    <strong>-J/MGIT</strong>
                </p>
            </td>
            <td width="29%" colspan="8">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.res"/></strong>
                </p>
            </td>
            <td width="25%" colspan="6">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.resDate"/></strong>
                    <strong> </strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" valign="top">
                <p align="center">
                    <em>${cultureDateMgit}</em>
                </p>
            </td>
            <td width="20%" colspan="3" valign="top">
                <p>
                    <strong>MGIT</strong>
                    <strong><em></em></strong>
                </p>
            </td>
            <td width="29%" colspan="8" valign="top">
                <p>
                    <strong><em>${cultureResultMgit} </em></strong>
                </p>
            </td>
            <td width="25%" colspan="6" valign="top">
                <p align="center">
                    <em>${cultureResultDateMgit}</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" valign="top">
                <p align="center">
                    <em>${cultureDateLj}</em>
                </p>
            </td>
            <td width="20%" colspan="3" valign="top">
                <p>
                    <strong>L</strong>
                    <strong>-J</strong>
                </p>
            </td>
            <td width="29%" colspan="8" valign="top">
            ${cultureResultLj}
            </td>
            <td width="25%" colspan="6" valign="top">
                <p align="center">
                    <em>${cultureResultDateLj}</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="23%" colspan="4" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong>
                </p>
            </td>
            <td width="76%" colspan="17" valign="top">
            ${cultureWorker}
            </td>
        </tr>
        <tr>
            <td width="76%" colspan="17" valign="top">
            </td>
        </tr>
        <tr>
            <td width="34%" colspan="6" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.resDate"/></strong>
                </p>
            </td>
            <td width="14%" colspan="3" valign="top">
            </td>
            <td width="36%" colspan="10" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.recDate"/></strong>
                </p>
            </td>
            <td width="13%" colspan="2" valign="top">
            </td>
        </tr>
        
    </tbody>
</table>
<p>
    
</p>
<table border="1" cellspacing="0" cellpadding="0" width="102%">
    <tbody>
        <tr>
            <td width="49%" colspan="15" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.dstRes"/></strong>
                </p>
            </td>
            <td width="50%" colspan="16" valign="top">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.labNo"/></strong>
                    <strong>${dst1Id} / ${dst2Id}</strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="18%" colspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.medFac"/></strong>
                </p>
            </td>
            <td width="27%" colspan="12" valign="top">
                <p>
                    <em> ${medFacCode}</em>
                </p>
            </td>
            <td width="24%" colspan="8" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.dirDate"/></strong>
                </p>
            </td>
            <td width="29%" colspan="9" valign="top">
                <p align="center">
                    <em>${cultureDirectionDate}</em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="34%" colspan="10" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.passport"/></strong>
                </p>
            </td>
            <td width="65%" colspan="21" valign="top">
            ${passport}
            </td>
        </tr>
        <tr>
            <td width="22%" colspan="3" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.patientName"/></strong>
                </p>
            </td>
            <td width="77%" colspan="28" valign="top">
            ${patientName}
            </td>
        </tr>
        <tr>
            <td width="77%" colspan="28" valign="top">
            </td>
        </tr>
        <tr>
            <td width="22%" colspan="4" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.DOB"/></strong>
                </p>
            </td>
            <td width="34%" colspan="15" valign="top">
                <p align="center">
                    <em>${dob}</em>
                </p>
            </td>
            <td width="15%" colspan="4" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.sex"/></strong>
                </p>
            </td>
            <td width="8%" colspan="2" valign="top">
                <p>
                    ${gender}
                </p>
            </td>
            <td width="5%" colspan="3" valign="top">
            </td>
            <td width="7%" colspan="2" valign="top">
                <p>
                    
                </p>
            </td>
            <td width="4%" valign="top">
            </td>
        </tr>
        <tr>
            <td width="34%" colspan="10" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tb03"/></strong>
                </p>
            </td>
            <td width="16%" colspan="8" valign="top">
                <p align="center">
                    <em>${tb03}</em>
                </p>
            </td>
            <td width="35%" colspan="10" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tb03u"/></strong>
                </p>
            </td>
            <td width="12%" colspan="3" valign="top">
                <p align="center">
                    <em></em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="25%" colspan="6" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tel"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="74%" colspan="25" valign="top">
            ${phone}
            </td>
        </tr>
        <tr>
            <td width="25%" colspan="6" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.actAddr"/></strong>
                </p>
            </td>
            <td width="74%" colspan="25" valign="top">
            ${add2Line1}
            </td>
        </tr>
        <tr>
            <td width="74%" colspan="25" valign="top">
             ${add2Line2}
            </td>
        </tr>
        <tr>
            <td width="25%" colspan="6" rowspan="2" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.healthCareProvName"/></strong>
                </p>
            </td>
            <td width="74%" colspan="25" valign="top">
            	${referredBy}
            </td>
        </tr>
        <tr>
            <td width="74%" colspan="25" valign="top">
            </td>
        </tr>
        <tr>
            <td width="25%" colspan="6" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.tel"/></strong>
                    <strong></strong>
                </p>
            </td>
            <td width="74%" colspan="25" valign="top">
            ${phone}
            </td>
        </tr>
        <tr>
            <td width="16%">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.dstDate"/></strong>
                </p>
            </td>
            <td width="8%" colspan="5">
                <p align="center">
                    <strong>L</strong>
                    <strong>-J</strong>
                </p>
            </td>
            <td width="4%">
                <p align="center">
                    <strong> </strong>
                </p>
            </td>
            <td width="11%" colspan="5">
                <p align="center">
                    <strong>MGIT</strong>
                </p>
            </td>
            <td width="5%" colspan="2">
                <p align="center">
                    <strong> </strong>
                </p>
            </td>
            <td width="37%" colspan="12">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.resDST"/></strong>
                </p>
            </td>
            <td width="16%" colspan="5">
                <p align="center">
                    <strong><spring:message code="labmodule.tb05.resDate"/></strong>
                    <strong></strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="16%" valign="top">
                <p align="center">
                    <em></em>
                </p>${dst1Date}
            </td>
            <td width="7%" colspan="4" valign="top">
                <p>
                    <strong>S</strong>
                    <em></em>
                </p>
            </td>
            <td width="6%" colspan="3" valign="top">
                <p>
                    <strong><em> ${dstS}</em></strong>
                </p>
            </td>
            <td width="7%" colspan="3" valign="top">
                <p align="center">
                    <strong>E</strong>
                    <strong><em></em></strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
                <p align="center">
                    <strong><em> ${dstE}</em></strong>
                </p>
            </td>
            <td width="7%" colspan="4" valign="top">
                <p align="center">
                    <strong>Mfx</strong>
                    <strong><em></em></strong>
                </p>
            </td>
            <td width="5%" colspan="3" valign="top">
                <p align="center">
                    <strong><em> </em></strong>
                </p>${dstMfx}
            </td>
            <td width="7%" valign="top">
                <p align="center">
                    <strong>Am</strong>
                </p>
            </td>
            <td width="6%" valign="top">
                <p align="center">${dstAm}
                    <strong><em> </em></strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
                <p align="center">
                    <strong>Cfz</strong>
                    <strong><em></em></strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
                <p>
                    <strong><em> ${dstCfz}</em></strong>
                </p>
            </td>
            <td width="7%" colspan="3" valign="top">
                <p align="center">
                    <strong>Bdq</strong>
                    <em></em>
                </p>
            </td>
            <td width="8%" colspan="2" valign="top">
                <p align="center">
                    <strong><em>${dstBdq} </em></strong>
                </p>
            </td>
        </tr>
        <tr>
            <td width="16%" valign="top">${dst2Date}
            </td>
            <td width="7%" colspan="4" valign="top">
                <p>
                    <strong>H</strong>
                </p>
            </td>
            <td width="6%" colspan="3" valign="top">
            ${dstH}
            </td>
            <td width="7%" colspan="3" valign="top">
                <p align="center">
                    <strong>Z</strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
            ${dstZ}
            </td>
            <td width="7%" colspan="4" valign="top">
                <p align="center">
                    <strong>Cm</strong>
                </p>
            </td>
            <td width="5%" colspan="3" valign="top">
            ${dstCm}
            </td>
            <td width="7%" valign="top">
                <p align="center">
                    <strong>Pto</strong>
                </p>
            </td>
            <td width="6%" valign="top">${dstPto}
            </td>
            <td width="6%" colspan="2" valign="top">
                <p align="center">
                    <strong>Lzd</strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
            ${dstLzd}
            </td>
            <td width="16%" colspan="5" valign="top">
            </td>
        </tr>
        <tr>
            <td width="16%" valign="top">
                <p align="center">
                    <em></em>
                </p>
            </td>
            <td width="7%" colspan="4" valign="top">
                <p>
                    <strong>R</strong>
                    <em></em>
                </p>
            </td>
            <td width="6%" colspan="3" valign="top">
                <p>
                    <strong>${dstR} </strong>
                </p>
            </td>
            <td width="7%" colspan="3" valign="top">
                <p align="center">
                    <strong>Ofx</strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
            ${dstOfx}
            </td>
            <td width="7%" colspan="4" valign="top">
                <p align="center">
                    <strong>Km</strong>
                </p>
            </td>
            <td width="5%" colspan="3" valign="top">
            ${dstKm}
            </td>
            <td width="7%" valign="top">
                <p align="center">
                    <strong>PAS</strong>
                </p>
            </td>
            <td width="6%" valign="top">${dstPas}
            </td>
            <td width="6%" colspan="2" valign="top">
                <p align="center">
                    <strong>Dlm</strong>
                </p>
            </td>
            <td width="6%" colspan="2" valign="top">
            ${dstDlm}
            </td>
            <td width="16%" colspan="5" valign="top">
                <p align="center">
                    <em></em>
                </p>
            </td>
        </tr>
        <tr>
            <td width="34%" colspan="9" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.nameAndSignofSpecialist"/></strong>
                </p>
            </td>
            <td width="65%" colspan="22" valign="top">
            ${dstWorker}
            </td>
        </tr>
        <tr>
            <td width="34%" colspan="9" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.resDate"/></strong>
                </p>
            </td>
            <td width="15%" colspan="7" valign="top">
                <p>
                    <em>dd</em>
                    <em>/</em>
                    <em>mm</em>
                    <em>/</em>
                    <em>yy</em>
                </p>
            </td>
            <td width="36%" colspan="11" valign="top">
                <p>
                    <strong><spring:message code="labmodule.tb05.recDate"/></strong>
                </p>
            </td>
            <td width="13%" colspan="4" valign="top">
                <p>
                    <em>dd</em>
                    <em>/</em>
                    <em>mm</em>
                    <em>/</em>
                    <em>yy</em>
                </p>
            </td>
        </tr>
        <tr height="0">
            <td width="120">
            </td>
            <td width="16">
            </td>
            <td width="30">
            </td>
            <td width="1">
            </td>
            <td width="6">
            </td>
            <td width="10">
            </td>
            <td width="34">
            </td>
            <td width="3">
            </td>
            <td width="29">
            </td>
            <td width="5">
            </td>
            <td width="18">
            </td>
            <td width="26">
            </td>
            <td width="21">
            </td>
            <td width="17">
            </td>
            <td width="23">
            </td>
            <td width="2">
            </td>
            <td width="10">
            </td>
            <td width="2">
            </td>
            <td width="39">
            </td>
            <td width="1">
            </td>
            <td width="52">
            </td>
            <td width="48">
            </td>
            <td width="13">
            </td>
            <td width="36">
            </td>
            <td width="29">
            </td>
            <td width="16">
            </td>
            <td width="19">
            </td>
            <td width="4">
            </td>
            <td width="34">
            </td>
            <td width="23">
            </td>
            <td width="36">
            </td>
        </tr>
    </tbody>
</table>
</div>

<p>&nbsp;</p>
</body>
</html>

 