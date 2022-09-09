package org.openmrs.module.labmodule.specimen;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a culture
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface Culture extends Bacteriology {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * colonies: the # of colonies present
	 * method: the Concept that represents the method used when performing the test
	 * organism: the type of organism present
	 * daysToPosiivity: the number of days for a culture to show a positive result
	 * 
	 */
	
	public Integer getColonies();
	public void setColonies(Integer colonies);
	
	public Concept getMethod();
	public void setMethod(Concept method);
	
	public Concept getOrganismType();
	public void setOrganismType(Concept organismType);

	public String getOrganismTypeNonCoded();
	public void setOrganismTypeNonCoded(String organismType);

	public Integer getDaysToPositivity();
	public void setDaysToPositivity(Integer daysToPositivity);

	//Requirement #10
	public Integer getLabNo();
	public void setLabNo(Integer daysToPositivity);

	public Concept getPlaceOfCulture();
	public void setPlaceOfCulture(Concept place);
	
	public List<MgitCulture> getMgitCultures();
	public void setMgitCultures(List<MgitCulture> mgit);

	public List<LjCulture> getLjCultures();
	public void setLjCultures(List<LjCulture> lj);

	public List<ContaminatedTubes> getContaminatedTubes();
	public void setContaminatedTubes(List<ContaminatedTubes> tubes);

	public Date getInoculationDate();
	public void setInoculationDate(Date inoculationDate);

	public Concept getMgitResult();
	public void setMgitResult(Concept mgit);
	
	public Concept getMtIdTest();
	public void setMtIdTest(Concept mtId);
	
	public Concept getTypeOfCulture();
	public void setTypeOfCulture(Concept cultureType);
	
	public Date getCultureResultReportingDate();
	public void setCultureResultReportingDate(Date reportingDate);
	
}
