/**
 * 
 */
package org.openmrs.module.mdrtb;


/**
 * @author owais.hussain@esquaredsystems.com
 *
 */
public class BaseLocation {
	
	private String name;
	private Integer id;
	private Integer levelId;
	
	public BaseLocation(Integer id, String name, Integer levelId) {
		this.name = name;
		this.id = id;
		this.levelId = levelId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String toString() {
		return getName();
	}
	/**
	 * @return the levelId
	 */
	public Integer getLevelId() {
		return levelId;
	}
	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}
}
