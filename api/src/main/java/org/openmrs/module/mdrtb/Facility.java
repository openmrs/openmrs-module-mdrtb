package org.openmrs.module.mdrtb;

public class Facility extends BaseLocation {
	
	private static final long serialVersionUID = 1L;
	
	public static Integer HIERARCHY_LEVEL = 3;
	
	private BaseLocation parent;
	
	public Facility(BaseLocation baseLocation) {
		super(baseLocation, LocationHierarchy.FACILITY);
	}
	
	/**
	 * @return the parent
	 */
	public BaseLocation getParent() {
		return parent;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(BaseLocation parent) {
		this.parent = parent;
	}
	
}
