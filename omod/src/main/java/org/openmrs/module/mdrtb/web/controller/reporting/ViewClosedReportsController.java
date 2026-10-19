package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.CompressionUtil;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewClosedReportsController {
	
	private static Log log = LogFactory.getLog(ViewClosedReportsController.class);
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/viewClosedReports")
	public void viewClosedReportsGet(@RequestParam(required = true, value = "type") String reportType, ModelMap model) {
		List<List<Object>> closedReports = Context.getService(MdrtbService.class).getReportsWithoutData(ReportType.valueOf(reportType));
		System.out.println("SIZE:" + closedReports.size());

		List<Integer> reportIds = new ArrayList<>();
		List<Integer> regionIds = new ArrayList<>();
		List<Integer> districtIds = new ArrayList<>();
		List<Integer> facilityIds = new ArrayList<>();
		List<Integer> years = new ArrayList<>();
		List<Integer> quarters = new ArrayList<>();
		List<Integer> months = new ArrayList<>();
		List<Integer> reportDates = new ArrayList<>();
		List<Integer> reportTypes = new ArrayList<>();
		List<Integer> reportStatuses = new ArrayList<>();
		List<Integer> reportNames = new ArrayList<>();
		for (List<Object> row : closedReports) {
			int i = 0;
			reportIds.add((Integer)row.get(i++));
			regionIds.add((Integer)row.get(i++));
			districtIds.add((Integer)row.get(i++));
			facilityIds.add((Integer)row.get(i++));
			reportNames.add((Integer)row.get(i++));
			years.add((Integer)row.get(i++));
			quarters.add((Integer)row.get(i++));
			months.add((Integer)row.get(i++));
			reportDates.add((Integer)row.get(i++));
			reportTypes.add((Integer)row.get(i++));
			reportStatuses.add((Integer)row.get(i++));
		}
		List<Region> regions = new ArrayList<>();
		List<District> districts = new ArrayList<>();
		List<Facility> facilities = new ArrayList<>();
		
		for (Integer regionId : regionIds) {
			regions.add(Context.getService(MdrtbService.class).getRegion(regionId));
		}
		for (Integer districtId : districtIds) {
			if (districtId != null) {
				districts.add(Context.getService(MdrtbService.class).getDistrict(districtId));
			} else {
				districts.add(null);
			}
		}
		
		for (Integer facilityId : facilityIds) {
			if (facilityId != null) {
				facilities.add(Context.getService(MdrtbService.class).getFacility(facilityId));
			} else {
				facilities.add(null);
			}
		}
		
		//List<Location> locations = Context.getLocationService().getAllLocations(false);
		List<Region> o = Context.getService(MdrtbService.class).getRegions();
		List<List<Location>> oblastLocations = new ArrayList<>();
		for (Region oblast : o) {
			List<Location> l = Context.getService(MdrtbService.class).getLocationsFromRegion(oblast);
			oblastLocations.add(l);
		}
		
		model.addAttribute("closedReports", closedReports);
		model.addAttribute("reportIds", reportIds);
		model.addAttribute("oblastIds", regionIds);
		model.addAttribute("districtIds", districtIds);
		model.addAttribute("facilityIds", facilityIds);
		model.addAttribute("years", years);
		model.addAttribute("quarters", quarters);
		model.addAttribute("months", months);
		model.addAttribute("reportDates", reportDates);
		model.addAttribute("reportStatuses", reportStatuses);
		model.addAttribute("reportNames", reportNames);
		
		model.addAttribute("reportOblasts", regions);
		model.addAttribute("reportDistricts", districts);
		model.addAttribute("reportFacilities", facilities);
		//model.addAttribute("reportLocations", locations);
		model.addAttribute("oblasts", o);
		model.addAttribute("oblastLocations", oblastLocations);
		model.addAttribute("reportType", reportType);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/viewClosedReports")
	public ModelAndView viewClosedReportsPost(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("district") Integer districtId,
	        @RequestParam("facility") Integer facilityId, @RequestParam("year") Integer year,
	        @RequestParam("quarter") String q, @RequestParam("month") String m,
	        @RequestParam("reportName") String reportName, @RequestParam("reportDate") String reportDate,
	        @RequestParam("formAction") String formAction, @RequestParam("reportType") String reportType, ModelMap model) {
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		Integer quarter = q == null ? null : Integer.parseInt(q.replace("\"", ""));
		Integer month = (m != null && !m.isEmpty()) ? null : Integer.parseInt(m.replace("\"", ""));
		String html = "";
		String returnStr = "";
		try {
			if (formAction.equals("unlock")) {
				System.out.println("-----UNLOCK-----");
				Context.getService(MdrtbService.class).saveScannedLabReport(null);
				ReportData reportData = new ReportData();
				reportData.setLocation(facility);
				reportData.setYear(year);
				reportData.setMonth(month);
				reportData.setQuarter(quarter);
				reportData.setReportName(reportName);
				
				Context.getService(MdrtbService.class).unlockReport(reportData);
				viewClosedReportsGet(reportType, model);
				returnStr = "/module/mdrtb/reporting/viewClosedReports";
			} else if (formAction.equals("view")) {
				System.out.println("-----VIEW-----");
				List<String> allReports = Context.getService(MdrtbService.class).readTableData(oblastId, districtId,
				    facilityId, year, quarter, month, reportName, null);
				
				if (allReports.isEmpty()) {
					html = "<p>No Data Found</p>";
				} else {
					html = CompressionUtil.decompressCode(allReports.get(0));
				}
				model.addAttribute("html", html);
				model.addAttribute("oblast", oblastId);
				model.addAttribute("district", districtId);
				model.addAttribute("facility", facility);
				model.addAttribute("year", year);
				model.addAttribute("quarter", quarter);
				model.addAttribute("month", month);
				model.addAttribute("reportName", reportName.replaceAll("_", " ").toUpperCase());
				model.addAttribute("reportDate", reportDate);
				model.addAttribute("formAction", formAction);
				returnStr = "/module/mdrtb/reporting/viewClosedReportContent";
			}
		}
		catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("ex", e);
		}
		return new ModelAndView(returnStr, model);
	}
}
