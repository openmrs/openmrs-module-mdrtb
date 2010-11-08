<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<style>
	.cellStyle {
		text-align:left; padding-right:20px; white-space:nowrap;
	}
	.cellWrappable {
		text-align:left; padding-right:20px;
	}
	.headerStyle {
		text-align:left; padding-right:20px; white-space:nowrap; 
	}
	.groupStyle {
		font-weight:bold; text-align:left; padding-right:20px; white-space:nowrap; border-bottom:1px solid black; background-color:lightgrey;
	}
	.future {
		font-style:italic; color:grey; font-weight:normal;
	}
	.regimenHistory {
		border:1px solid grey; white-space:nowrap; background-color:#EEEEDD;
	}
	.regimenHistory th {
		padding:5px; background-color:white; vertical-align:middle;
	}
	.regimenHistory td {
		padding:5px; text-align: center;
	}
	#ui-datepicker-div {
		z-index: 9999; /* must be > than popup editor (950) */
	}
	.ui-datepicker {
		z-index: 9999 !important; /* must be > than popup editor (1002) */
	}
</style>

<c:set var="cd" value="${empty param.changeDate ? '' : mdrtb:parseDate(param.changeDate, 'yyyy-MM-dd')}"/>
<mdrtb:regimenPortlet id="${param.id}" patientId="${param.patientId}" type="${param.type}" changeDate="${cd}" url="${param.url}"/>
