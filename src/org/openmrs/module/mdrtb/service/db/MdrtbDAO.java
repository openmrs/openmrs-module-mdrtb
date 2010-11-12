package org.openmrs.module.mdrtb.service.db;

import java.util.List;
import java.util.Locale;

import org.openmrs.ConceptName;
import org.openmrs.ConceptWord;
import org.openmrs.Location;
import org.openmrs.api.db.DAOException;

public interface MdrtbDAO {
    
    public List<ConceptName> getMdrtbConceptNamesByNameList(List<String> nameList, boolean removeDuplicates, Locale loc)  throws DAOException ;

    public List<Location> getAllMdrtrbLocations(boolean includeRetired) throws DAOException;
    
    /**
     * @return all Locations which have non-voided Patient Programs associated with them
     */
    public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException;
    
    public List<ConceptWord> getConceptWords(String phrase, List<Locale> locales) throws DAOException;
    
}
