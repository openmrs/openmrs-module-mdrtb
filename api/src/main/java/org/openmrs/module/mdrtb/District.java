package org.openmrs.module.mdrtb;

public class District extends BaseLocation {

	public static Integer HIERARCHY_LEVEL = 3;
	
	private BaseLocation parent;

	public District(String name, Integer id) {
		super(id, name);
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