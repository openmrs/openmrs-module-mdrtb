/**
 * 
 */
package org.openmrs.module.mdrtb;

import org.openmrs.Location;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class BaseLocation extends Location {
	
	private static final long serialVersionUID = 1L;
	
	private LocationHierarchy level;
	
	public BaseLocation(Location location, LocationHierarchy levelName) {
		this.setLocationId(location.getLocationId());
		this.setName(location.getName());
		this.setDescription(location.getDescription());
		this.setAddress1(location.getAddress1());
		this.setAddress2(location.getAddress2());
		this.setCityVillage(location.getCityVillage());
		this.setStateProvince(location.getStateProvince());
		this.setCountry(location.getCountry());
		this.setPostalCode(location.getPostalCode());
		this.setLatitude(location.getLatitude());
		this.setLongitude(location.getLongitude());
		this.setCountyDistrict(location.getCountyDistrict());
		this.setAddress3(location.getAddress3());
		this.setAddress4(location.getAddress4());
		this.setAddress5(location.getAddress5());
		this.setAddress6(location.getAddress6());
		this.setAddress7(location.getAddress7());
		this.setAddress8(location.getAddress8());
		this.setAddress9(location.getAddress9());
		this.setAddress10(location.getAddress10());
		this.setAddress11(location.getAddress11());
		this.setAddress12(location.getAddress12());
		this.setAddress13(location.getAddress13());
		this.setAddress14(location.getAddress14());
		this.setAddress15(location.getAddress15());
		this.setParentLocation(location.getParentLocation());
		this.setChildLocations(location.getChildLocations());
		this.setCreator(location.getCreator());
		this.setDateCreated(location.getDateCreated());
		this.setChangedBy(location.getChangedBy());
		this.setDateChanged(location.getDateChanged());
		this.setRetired(location.getRetired());
		this.setDateRetired(location.getDateRetired());
		this.setRetiredBy(location.getRetiredBy());
		this.setRetireReason(location.getRetireReason());
		this.setUuid(location.getUuid());
		this.level = levelName;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return super.getName();
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return super.getId();
	}
	
	public String toString() {
		return getName();
	}
	
	/**
	 * @return the level
	 */
	public LocationHierarchy getLevel() {
		return level;
	}
	
	/**
	 * @param level the level to set
	 */
	public void setLevelName(LocationHierarchy level) {
		this.level = level;
	}
}
