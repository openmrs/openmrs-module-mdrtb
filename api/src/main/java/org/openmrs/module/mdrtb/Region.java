package org.openmrs.module.mdrtb;


public class Region extends BaseLocation {
	
	public static Integer HIERARCHY_LEVEL = 2;

	private BaseLocation parent;
	
	public Region(BaseLocation baseLocation) {
		super(baseLocation.getId(), baseLocation.getName(), baseLocation.getLevelId());		
	}

	public Region(String name, Integer id) {
		super(id, name, HIERARCHY_LEVEL);
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