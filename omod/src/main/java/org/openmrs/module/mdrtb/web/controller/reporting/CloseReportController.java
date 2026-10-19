package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportStatus;
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

@Controller
public class CloseReportController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/closeReport")
	public void closeReportGet(ModelMap model) {
		System.out.println("-----Close Report GET-----");
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		List<Region> oblasts = Context.getService(MdrtbService.class).getRegions();
		model.addAttribute("locations", locations);
		model.addAttribute("oblasts", oblasts);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/closeReport")
	public String closeReportPost(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("district") Integer districtId,
	        @RequestParam("facility") Integer facilityId, @RequestParam("year") Integer year,
	        @RequestParam("quarter") String q, @RequestParam("month") String m,
	        @RequestParam("reportDate") String reportDateStr, @RequestParam("table") String table,
	        @RequestParam("reportName") String reportName, @RequestParam("formPath") String formPath, ModelMap model) {
		System.out.println("-----Close Report POST-----");
		
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		Integer quarter = q == null ? null : Integer.parseInt(q.replace("\"", ""));
		Integer month = (m != null && !m.isEmpty()) ? null : Integer.parseInt(m.replace("\"", ""));
		Date reportDate = null;
		
		ReportStatus reportStatus = ReportStatus.UNLOCKED;
		
		try {
			if (!(reportDateStr.equals(""))) {
				try {
					reportDate = Context.getDateTimeFormat().parse(reportDateStr);
				}
				catch (ParseException e) {
					reportDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(reportDateStr);
				}
			}
			reportStatus = ReportStatus.LOCKED;
			
			StringBuffer sb = new StringBuffer().append("Oblast: ").append(region).append("District: ").append(district)
			        .append("Facility: ").append(facility).append("Year: ").append(year).append("Quarter: ").append(quarter)
			        .append("Month: ").append(month).append("Report Name: ").append(reportName).append("Report Status: ")
			        .append(reportStatus).append("Report Date: ").append(reportDate).append("Form Path: ").append(formPath);
			System.out.println(sb);
			
			ReportData reportData = new ReportData();
			reportData.setLocation(facility);
			reportData.setYear(year);
			reportData.setMonth(month);
			reportData.setQuarter(quarter);
			reportData.setReportStatus(reportStatus);
			reportData.setReportName(reportName);
			reportData.setTableData(table);
			
			if (formPath.equals("tb08uResults") || formPath.equals("tb07uResults") || formPath.equals("tb03uResults")
			        || formPath.equals("dquResults")) {
				reportData.setReportType(ReportType.MDRTB);
				Context.getService(MdrtbService.class).lockReport(reportData);
			} else {
				try {
					reportData.setReportType(ReportType.DOTSTB);
					Context.getService(MdrtbService.class).lockReport(reportData);
				}
				catch (Exception ee) {
					System.out.println("Caught in inner catch:" + ee.getMessage());
					model.addAttribute("ex", ee);
					model.addAttribute("reportStatus", reportStatus);
				}
			}
			model.addAttribute("reportStatus", reportStatus);
			request.getSession().setAttribute("reportStatus", reportStatus);
			
			System.out.println("---POST CLOSE-----");
		}
		catch (Exception e) {
			System.out.println("Caught in outer catch:" + e.getMessage());
			model.addAttribute("ex", e);
			model.addAttribute("reportStatus", false);
		}
		
		String url = "";
		switch (formPath) {
			case "tb08uResults":
				url = TB08uController.doTB08(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				break;
			case "tb07uResults":
				url = TB07uController.doTB07u(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				break;
			case "dquResults":
				url = MDRDQController.doDQ(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				break;
			case "dqResults":
				url = DOTSDQController.doDQ(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				break;
			case "tb07Results":
				url = TB07ReportController.doTB07(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				break;
			case "tb08Results":
				url = TB08ReportController.doTB08(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				System.out.println("URL:" + url);
				break;
			case "tb03Results":
				url = TB03ExportController.doTB03(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				System.out.println("URL:" + url);
				break;
			case "tb03uResults":
				url = TB03uController.doTB03(district.getId(), region.getId(), facility.getId(), year, q, m, model);
				System.out.println("URL:" + url);
				break;
		}
		System.out.println("url: " + url);
		return url;
	}
}
