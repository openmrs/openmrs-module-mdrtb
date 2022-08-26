package org.openmrs.module.mdrtb;


public class Country extends BaseLocation {

	public static Integer HIERARCHY_LEVEL = 1;

	public Country(String name, Integer id) {
		super(id, name);
	}
}