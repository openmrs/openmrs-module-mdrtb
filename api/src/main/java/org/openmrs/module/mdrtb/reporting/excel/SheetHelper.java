package org.openmrs.module.mdrtb.reporting.excel;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Utility Class to assist with Excel manipulation
 */
public class SheetHelper {
	
	short rowNum;
	
	short colNum;
	
	HSSFSheet sheet;
	
	HSSFRow currentRow;
	
	public SheetHelper(HSSFSheet sheet) {
		this.sheet = sheet;
		rowNum = (short) 0;
		colNum = (short) 0;
	}
	
	public short getRowNum() {
		return rowNum;
	}
	
	public void skipCell() {
		++colNum;
	}
	
	public void skipCell(HSSFCellStyle style) {
		if (currentRow == null) {
			currentRow = sheet.createRow(rowNum);
		}
		HSSFCell cell = currentRow.createCell(colNum++);
		if (style != null) {
			cell.setCellStyle(style);
		}
	}
	
	public void setColumnWidth(int col, double excelWidth) {
		sheet.setColumnWidth((short) col, (short) (excelWidth * 256));
	}
	
	public void addCell(Object o) {
		addCell(o, null);
	}
	
	public void addCell(Object o, HSSFCellStyle style) {
		if (o == null) {
			skipCell(style);
		} else if (o instanceof BigDecimal || o instanceof Double) {
			addCell((Number) o, style);
		} else if (o instanceof Date) {
			addCell((Date) o, style);
		} else if (o instanceof java.sql.Date) {
			java.sql.Date d = (java.sql.Date) o;
			addCell(new Date(d.getTime()), style);
		} else if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp d = (java.sql.Timestamp) o;
			addCell(new Date(d.getTime()), style);
		} else {
			addCell(o.toString(), style);
		}
	}
	
	public void addCell(Number n) {
		addCell(n, null);
	}
	
	public void addCell(Number n, HSSFCellStyle style) {
		if (n == null) {
			skipCell(style);
		} else {
			addCell(n.doubleValue(), style);
		}
	}
	
	public void addCell(String value) {
		addCell(value, null);
	}
	
	public void addCell(String value, HSSFCellStyle style) {
		if (value == null) {
			skipCell(style);
		} else {
			if (currentRow == null) {
				currentRow = sheet.createRow(rowNum);
			}
			HSSFCell cell = currentRow.createCell(colNum++, CellType.STRING);
			if (style != null) {
				cell.setCellStyle(style);
			}
			cell.setCellValue(value);
		}
	}
	
	public void addCell(Date date) {
		addCell(date, null);
	}
	
	public void addCell(Date date, HSSFCellStyle style) {
		if (currentRow == null) {
			currentRow = sheet.createRow(rowNum);
		}
		HSSFCell cell = currentRow.createCell(colNum++, CellType.NUMERIC);
		if (style != null) {
			cell.setCellStyle(style);
		}
		if (date != null) {
			cell.setCellValue(date);
		}
	}
	
	public void addCell(int value, HSSFCellStyle style) {
		addCell((double) value, style);
	}
	
	public void addCell(int value) {
		addCell((double) value, null);
	}
	
	public void addCell(double value) {
		addCell(value, null);
	}
	
	public void addCell(double value, HSSFCellStyle style) {
		if (currentRow == null) {
			currentRow = sheet.createRow(rowNum);
		}
		HSSFCell cell = currentRow.createCell(colNum++, CellType.NUMERIC);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(value);
	}
	
	public void nextRow() {
		currentRow = null;
		++rowNum;
		colNum = 0;
	}
	
	public void addHeaderRow(String[] headerNames, HSSFCellStyle style) {
		for (int i = 0; i < headerNames.length; i++) {
			addCell(headerNames[i], style);
		}
		nextRow();
	}
	
	public static String getCellContentsAsString(HSSFCell cell) {
		String contents = "";
		try {
			switch (cell.getCellType()) {
				case STRING:
					contents = cell.getStringCellValue();
					break;
				case NUMERIC:
					contents = Double.toString(cell.getNumericCellValue());
					break;
				case BOOLEAN:
					contents = Boolean.toString(cell.getBooleanCellValue());
					break;
				case FORMULA:
					contents = cell.getCellFormula();
					break;
				case ERROR:
					contents = Byte.toString(cell.getErrorCellValue());
					break;
				default:
					break;
			}
		}
		catch (Exception e) {
			contents = cell.getStringCellValue();
		}
		contents = ObjectUtil.nvlStr(contents, "").trim();
		return contents;
	}
	
	public static void setCellContents(HSSFCell cell, Object cellValue) {
		if (cellValue == null) {
			cellValue = "";
		}
		if (!cellValue.equals(getCellContentsAsString(cell))) {
			if (cellValue instanceof Number) {
				cell.setCellValue(((Number) cellValue).doubleValue());
				return;
			}
			if (cellValue instanceof Date) {
				cell.setCellValue(((Date) cellValue));
				return;
			}
			
			String cellValueString = cellValue.toString();
			try {
				if (cell.getCellType() == CellType.BOOLEAN) {
					cell.setCellValue(Boolean.valueOf(cellValueString));
					return;
				}
				if (cell.getCellType() == CellType.FORMULA) {
					cell.setCellFormula(cellValueString);
					return;
				}
				if (cell.getCellType() == CellType.NUMERIC) {
					cell.setCellValue(Double.parseDouble(cellValueString));
					return;
				}
			}
			catch (Exception ignored) {}
			try {
				cell.setCellValue(Integer.toString(Integer.parseInt(cellValueString)));
				return;
			}
			catch (Exception ignored) {}
			try {
				cell.setCellValue(Double.toString(Double.parseDouble(cellValueString)));
				return;
			}
			catch (Exception ignored) {}
			cell.setCellValue(cellValueString);
			return;
		}
		return;
	}
}
