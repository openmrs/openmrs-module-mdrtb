package org.openmrs.module.mdrtb;

public class District extends BaseLocation {

	public static Integer HIERARCHY_LEVEL = 3;
	
	private BaseLocation parent;
	
	public District(BaseLocation baseLocation) {
		super(baseLocation.getId(), baseLocation.getName(), baseLocation.getLevelId());		
	}

	public District(String name, Integer id, Integer levelId) {
		super(id, name, levelId);
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