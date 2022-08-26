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
	
	public BaseLocation(Integer id, String name) {
		this.name = name;
		this.id = id;
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
}
