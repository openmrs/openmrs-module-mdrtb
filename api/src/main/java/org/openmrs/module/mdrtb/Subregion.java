package org.openmrs.module.mdrtb;

public class Subregion extends BaseLocation {
	
	private static final long serialVersionUID = 1L;
	
	private BaseLocation parent;
	
	public Subregion(BaseLocation baseLocation) {
		super(baseLocation, LocationHierarchy.SUBREGION);
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
