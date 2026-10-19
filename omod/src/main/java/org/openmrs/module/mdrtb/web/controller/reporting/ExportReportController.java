package org.openmrs.module.mdrtb.web.controller.reporting;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.PDFHelper;
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
public class ExportReportController {
	
	private static Log log = LogFactory.getLog(ExportReportController.class);
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/exportReport")
	public void exportReportGet(ModelMap model) {
		System.out.println("-----Export Report GET-----");
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		List<Region> oblasts = Context.getService(MdrtbService.class).getRegions();
		model.addAttribute("locations", locations);
		model.addAttribute("oblasts", oblasts);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/exportReport")
	public String exportReportPost(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("district") Integer districtId,
	        @RequestParam("facility") Integer facilityId, @RequestParam("year") Integer year,
	        @RequestParam("quarter") String quarter, @RequestParam("month") String month,
	        @RequestParam("reportDate") String reportDate, @RequestParam("table") String table,
	        @RequestParam("reportName") String reportName, @RequestParam("formPath") String formPath, ModelMap model) {
		System.out.println("-----Export Report POST-----");
		
		Integer oblast = null;
		Integer location = null;
		ByteArrayOutputStream baos = null;
		
		String date = reportDate;
		
		try {
			if (!(reportDate.equals(""))) {
				date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new SimpleDateFormat("dd.MM.yyyy")
				        .parse(reportDate));
			}
			if (!(table.equals(""))) {
				table = table.replaceAll("<br>", " ");
				table = table.replaceAll("<br/>", " ");
				table = table.replaceAll("\"", "'");
				
				String html = "<html><body>" + table + "</body></html>";
				//System.out.println(html);
				baos = new PDFHelper().createAndDownloadPdf(html);
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=download.pdf");
				OutputStream os = response.getOutputStream();
				baos.writeTo(os);
				os.flush();
				os.close();
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("---POST EXPORT-----\n");
			sb.append("oblast: ").append(oblast).append("\n");
			sb.append("location: ").append(location).append("\n");
			sb.append("year: ").append(year).append("\n");
			sb.append("quarter: ").append(quarter).append("\n");
			sb.append("month: ").append(month).append("\n");
			sb.append("reportDate: ").append(date).append("\n");
			sb.append("formPath: ").append(formPath).append("\n");
			sb.append("reportName: ").append(reportName).append("\n\n\n");
			System.out.println(sb);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("ex", e);
		}
		return TB08uController.doTB08(oblastId, districtId, facilityId, year, quarter, month, model);
	}
	
}
