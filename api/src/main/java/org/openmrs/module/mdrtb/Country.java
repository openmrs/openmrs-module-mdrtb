package org.openmrs.module.mdrtb;


public class Country extends BaseLocation {

	public static Integer HIERARCHY_LEVEL = null; //TODO: Shouldn't this be 0?

	public Country(String name, Integer id) {
		super(id, name, HIERARCHY_LEVEL);
	}
}