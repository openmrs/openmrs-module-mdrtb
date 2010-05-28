<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="taglibs/mdrtb.tld" %>



<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery-1.2.3.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js" />  <!-- used by tooltip plugin -->
<openmrs:htmlInclude file="/moduleResources/mdrtb/jmesa.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jmesa.css" /> 
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" /> 
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css" /> 

<script type="text/javascript">
// add the custom jquery.tooltip for span elements
$(document).ready(function(){
    $("span").tooltip();
  });
</script>



<!--  try removing this form at some point? -->
<form name="patientSummaryTableForm" action="/module/mdrtb/mdrtbPatientSummaryTable.form">
${patientSummaryTable}
</form>

<%@ include file="mdrtbFooter.jsp"%>