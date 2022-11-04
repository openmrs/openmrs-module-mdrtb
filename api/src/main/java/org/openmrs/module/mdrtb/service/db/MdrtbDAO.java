package org.openmrs.module.mdrtb.service.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.BaseLocation;

public interface MdrtbDAO {

    /**
     * @return all Locations which have non-voided Patient Programs associated with them
     */
    public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException;
    
    @Deprecated
    public List<String> getAllRayonsTJK();
    
    public PatientIdentifier getPatientIdentifierById(Integer patientIdentifierId);
  
    public int countPDFRows();
    
    public List<List<Integer>> getPDFData(String reportType);
    
    public ArrayList<String> getPDFColumns();
    
    public void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String reportDate, String tableData, boolean reportStatus, String reportName, String reportType);
   
    public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String name, String reportType);
    
    public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String name, String date, String reportType);
    
    public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String name, String date, String reportType);
	
    public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames);
	
    public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate, Date closeDate);
	
	public void evict(Object obj);

	public BaseLocation getAddressHierarchyLocation(Integer locationId);

	public List<BaseLocation> getAddressHierarchyLocationsByHierarchyLevel(Integer level);
	
	public BaseLocation getAddressHierarchyLocationParent(BaseLocation child);

	public List<BaseLocation> getAddressHierarchyLocationsByParent(BaseLocation parent);
}
