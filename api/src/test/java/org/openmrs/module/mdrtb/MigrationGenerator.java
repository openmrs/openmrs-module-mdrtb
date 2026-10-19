package org.openmrs.module.mdrtb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MigrationGenerator utility to generate TRUNCATE and INSERT INTO statements with explicit column
 * names.
 */
public class MigrationGenerator {
	
	public static void main(String[] args) {
		String baseDir = System.getProperty("user.dir");
		String schemaFile = baseDir + java.io.File.separator + "openmrs_v2_schema.sql";
		String migrationsFile = baseDir + java.io.File.separator + "openmrs_v2_migrations.sql";
		
		System.out.println("Reading schema from: " + schemaFile);
		System.out.println("Generating migrations to: " + migrationsFile);
		
		Map<String, List<String>> tableColumns = new HashMap<String, List<String>>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(schemaFile));
			String line;
			String currentTable = null;
			List<String> currentColumns = null;
			
			// Patterns for table and column extraction
			Pattern tablePattern = Pattern.compile("CREATE\\s+TABLE\\s+[`]?([a-zA-Z0-9_]+)[`]?\\s*\\(",
			    Pattern.CASE_INSENSITIVE);
			Pattern columnPattern = Pattern.compile("^\\s*[`]?([a-zA-Z0-9_]+)[`]?\\s+([a-zA-Z0-9_]+).*",
			    Pattern.CASE_INSENSITIVE);
			Pattern endTablePattern = Pattern.compile("^\\s*\\).*;", Pattern.CASE_INSENSITIVE);
			
			while ((line = reader.readLine()) != null) {
				Matcher tableMatcher = tablePattern.matcher(line);
				if (tableMatcher.find()) {
					currentTable = tableMatcher.group(1);
					currentColumns = new ArrayList<String>();
					tableColumns.put(currentTable, currentColumns);
					continue;
				}
				
				if (currentTable != null) {
					if (endTablePattern.matcher(line).find()) {
						currentTable = null;
						currentColumns = null;
					} else {
						Matcher colMatcher = columnPattern.matcher(line);
						if (colMatcher.find()) {
							String colName = colMatcher.group(1);
							String colType = colMatcher.group(2).toUpperCase();
							
							// Skip SQL keywords that might be mistaken for columns if they are at the start of the line
							if (!colType.equals("KEY") && !colType.equals("PRIMARY") && !colType.equals("UNIQUE")
							        && !colType.equals("CONSTRAINT") && !colName.equalsIgnoreCase("PRIMARY")
							        && !colName.equalsIgnoreCase("KEY") && !colName.equalsIgnoreCase("UNIQUE")
							        && !colName.equalsIgnoreCase("CONSTRAINT")) {
								currentColumns.add(colName);
							}
						}
					}
				}
			}
		}
		catch (IOException e) {
			System.err.println("Error reading schema file: " + e.getMessage());
			return;
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {}
			}
		}
		
		Set<String> allTables = tableColumns.keySet();
		Set<String> sourceTables = new HashSet<String>();
		Set<String> targetTables = new TreeSet<String>();
		
		for (String name : allTables) {
			if (name.startsWith("_")) {
				sourceTables.add(name);
			} else {
				targetTables.add(name);
			}
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(migrationsFile));
			for (String target : targetTables) {
				String source = "_" + target;
				if (sourceTables.contains(source)) {
					List<String> targetCols = tableColumns.get(target);
					List<String> sourceCols = tableColumns.get(source);
					
					List<String> commonCols = new ArrayList<String>();
					for (String col : targetCols) {
						if (sourceCols.contains(col)) {
							commonCols.add(col);
						}
					}
					
					writer.write("TRUNCATE TABLE " + target + ";");
					writer.newLine();
					
					if (commonCols.isEmpty()) {
						writer.write("-- Warning: No common columns found between " + target + " and " + source);
					} else {
						StringBuilder colList = new StringBuilder();
						for (int i = 0; i < commonCols.size(); i++) {
							colList.append(commonCols.get(i));
							if (i < commonCols.size() - 1) {
								colList.append(", ");
							}
						}
						writer.write("INSERT INTO " + target + " (" + colList.toString() + ")");
						writer.newLine();
						writer.write("SELECT " + colList.toString() + " FROM " + source + ";");
					}
					writer.newLine();
				} else {
					writer.write("-- No source counterpart found for table: " + target);
					writer.newLine();
				}
			}
			System.out.println("Generated migrations for " + targetTables.size() + " target tables.");
		}
		catch (IOException e) {
			System.err.println("Error writing migrations file: " + e.getMessage());
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException e) {}
			}
		}
	}
}
