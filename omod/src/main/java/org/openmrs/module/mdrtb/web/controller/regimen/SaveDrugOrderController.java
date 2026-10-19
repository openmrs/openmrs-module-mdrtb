package org.openmrs.module.mdrtb.web.controller.regimen;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller processes a submission of changes to a Drug Order
 */
@Controller
public class SaveDrugOrderController {
	
	protected static final Log log = LogFactory.getLog(SaveDrugOrderController.class);
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		DateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Drug.class, new DrugEditor());
	}
	
	@RequestMapping("/module/mdrtb/regimen/saveDrugOrder")
	public String saveRegimen(@RequestParam(required = true, value = "patientId") Integer patientId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "type") String type,
	        @RequestParam(required = false, value = "orderId") Integer orderId,
	        @RequestParam(required = true, value = "generic") Concept generic,
	        @RequestParam(required = false, value = "drug") Drug drug,
	        @RequestParam(required = false, value = "dose") Double dose,
	        @RequestParam(required = false, value = "units") String units,
	        @RequestParam(required = false, value = "frequency") String frequency,
	        @RequestParam(required = false, value = "perDay") String perDay,
	        @RequestParam(required = false, value = "perWeek") String perWeek,
	        @RequestParam(required = false, value = "instructions") String instructions,
	        @RequestParam(required = true, value = "startDate") Date changeDate,
	        @RequestParam(required = false, value = "autoExpireDate") Date autoExpireDate,
	        @RequestParam(required = false, value = "discontinuedDate") Date discontinuedDate,
	        @RequestParam(required = false, value = "discontinuedReason") Concept discontinuedReason,
	        HttpServletRequest request, ModelMap model) {
		
		DrugOrder drugOrder = null;
		if (ObjectUtil.isNull(orderId)) {
			drugOrder = new DrugOrder();
			drugOrder.setPatient(Context.getPatientService().getPatient(patientId));
			drugOrder.setOrderType(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID));
		} else {
			drugOrder = (DrugOrder) Context.getOrderService().getOrder(orderId);
		}
		String durationUnits = "DAYS";
		if (ObjectUtil.notNull(perWeek)) {
			durationUnits = "WEEKS";
		}
		drugOrder = MdrtbUtil.updateDrugOrder(drugOrder, generic, drug, dose, units, durationUnits, frequency, instructions,
		    changeDate, autoExpireDate, discontinuedDate, discontinuedReason);
		
		return "redirect:/module/mdrtb/regimen/manageDrugOrders.form?patientId=" + patientId + "&patientProgramId="
		        + patientProgramId;
	}
}
