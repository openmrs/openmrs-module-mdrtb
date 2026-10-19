package org.openmrs.module.mdrtb;

public class Region extends BaseLocation {
	
	private static final long serialVersionUID = 1L;
	
	public static final Integer HIERARCHY_LEVEL = 0;
	
	private BaseLocation parent;
	
	public Region(BaseLocation baseLocation) {
		super(baseLocation, LocationHierarchy.REGION);
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
