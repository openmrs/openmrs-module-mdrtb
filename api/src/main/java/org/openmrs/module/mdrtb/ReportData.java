/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb;

import java.io.IOException;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;

/**
 * Please note that a corresponding table schema must be created in liquibase.xml.
 */
@Entity(name = "mdrtb.ReportData")
@Table(name = "report_data")
public class ReportData extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1348372808239298611L;
	
	@Id
	@GeneratedValue
	@Column(name = "report_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location location;
	
	@Basic
	@Column(name = "report_name", length = 255)
	private String reportName;
	
	@Basic
	@Column(name = "description", length = 255)
	private String description;
	
	@Basic
	@Column(name = "year")
	private Integer year;
	
	@Basic
	@Column(name = "quarter")
	private Integer quarter;
	
	@Basic
	@Column(name = "month")
	private Integer month;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "report_type", length = 50)
	private ReportType reportType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "report_status", length = 50)
	private ReportStatus reportStatus;
	
	@Column(name = "table_data", length = 65536)
	private String tableData;
	
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
	
	public String getTableData() throws IOException {
		if (tableData != null) {
			return CompressionUtil.decompressCode(tableData);
		}
		return null;
	}
	
	public void setTableData(String tableData) throws IOException {
		String compressCode = CompressionUtil.compressCode(tableData);
		this.tableData = compressCode;
	}
}
