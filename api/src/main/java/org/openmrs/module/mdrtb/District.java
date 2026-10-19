package org.openmrs.module.mdrtb;

public class District extends BaseLocation {
	
	private static final long serialVersionUID = 1L;
	
	private BaseLocation parent;
	
	public District(BaseLocation baseLocation) {
		super(baseLocation, LocationHierarchy.DISTRICT);
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
