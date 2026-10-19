/**
 * 
 */
package org.openmrs.module.mdrtb.web.dto;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportStatus;
import org.openmrs.module.mdrtb.ReportType;

public class SimpleReportData extends BaseOpenmrsData {
	
	private static Log log = LogFactory.getLog(SimpleReportData.class);
	
	private static final long serialVersionUID = 5041302170784578174L;
	
	private Integer id;
	
	private Location location;
	
	private String reportName;
	
	private String description;
	
	private Integer year;
	
	private Integer quarter;
	
	private Integer month;
	
	private ReportType reportType;
	
	private ReportStatus reportStatus;
	
	private String tableData;
	
	public SimpleReportData() {
		super();
	}
	
	public SimpleReportData(ReportData reportData, boolean attachData) {
		setId(reportData.getId());
		setUuid(reportData.getUuid());
		setLocation(reportData.getLocation());
		setReportName(reportData.getReportName());
		setDescription(reportData.getDescription());
		setYear(reportData.getYear());
		setQuarter(reportData.getQuarter());
		setMonth(reportData.getMonth());
		setReportType(reportData.getReportType());
		setReportStatus(reportData.getReportStatus());
		setDateCreated(reportData.getDateCreated());
		setCreator(reportData.getCreator());
		if (attachData) {
			try {
				String html = reportData.getTableData();
				html = html.replaceAll("\r\n", "");
				html = html.replaceAll("\t", "");
				setTableData(html);
			}
			catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}
	
	public ReportData toReportData() {
		ReportData reportData = new ReportData();
		reportData.setId(id);
		reportData.setUuid(getUuid());
		reportData.setLocation(location);
		reportData.setReportName(reportName);
		reportData.setDescription(description);
		reportData.setYear(year);
		reportData.setQuarter(quarter);
		reportData.setMonth(month);
		reportData.setReportType(reportType);
		reportData.setReportStatus(reportStatus);
		try {
			reportData.setTableData(tableData);
		}
		catch (IOException e) {
			log.error(e.getMessage());
		}
		return reportData;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getQuarter() {
		return quarter;
	}
	
	public void setQuarter(Integer quarter) {
		this.quarter = quarter;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public void setMonth(Integer month) {
		this.month = month;
	}
	
	public ReportType getReportType() {
		return reportType;
	}
	
	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
	public ReportStatus getReportStatus() {
		return reportStatus;
	}
	
	public void setReportStatus(ReportStatus reportStatus) {
		this.reportStatus = reportStatus;
	}
	
	public String getTableData() {
		return tableData;
	}
	
	public void setTableData(String tableData) {
		this.tableData = tableData;
	}
}
