package org.openmrs.module.labmodule.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.labmodule.MdrtbConceptMap;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.program.TbPatientProgram;
import org.openmrs.module.labmodule.service.db.MdrtbDAO;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.CultureImpl;
import org.openmrs.module.labmodule.specimen.Dst;
import org.openmrs.module.labmodule.specimen.Dst1;
import org.openmrs.module.labmodule.specimen.Dst1Impl;
import org.openmrs.module.labmodule.specimen.Dst2;
import org.openmrs.module.labmodule.specimen.Dst2Impl;
import org.openmrs.module.labmodule.specimen.DstImpl;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.HAINImpl;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.MicroscopyImpl;
import org.openmrs.module.labmodule.specimen.ScannedLabReport;
import org.openmrs.module.labmodule.specimen.Smear;
import org.openmrs.module.labmodule.specimen.SmearImpl;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.SpecimenImpl;
import org.openmrs.module.labmodule.specimen.Xpert;
import org.openmrs.module.labmodule.specimen.XpertImpl;
import org.openmrs.module.labmodule.specimen.reporting.District;
import org.openmrs.module.labmodule.specimen.reporting.Facility;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

public class TbServiceImpl extends BaseOpenmrsService implements TbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected MdrtbDAO dao;
	
	private MdrtbConceptMap conceptMap = new MdrtbConceptMap(); // TODO: should this be a bean?	
	
	// caches
	private Map<Integer,String> colorMapCache = null;

	public void setDao(MdrtbDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see TbService#getLocationsWithAnyProgramEnrollments()
	 */
	public List<Location> getLocationsWithAnyProgramEnrollments() {
		return dao.getLocationsWithAnyProgramEnrollments();
	}

	public Concept getConcept(String... conceptMapping) {
		return conceptMap.lookup(conceptMapping);
	}
	
	public Concept getConcept(String conceptMapping) {
		//System.out.println ("CONC MAP:" + conceptMapping);
		return conceptMap.lookup(conceptMapping);
	}
	
	/**
	 * @see TbService#findMatchingConcept(String)
	 */
	public Concept findMatchingConcept(String lookup) {
    	if (ObjectUtil.notNull(lookup)) {
    		// First try MDR-TB module's known concept mappings
    		try {
    			return Context.getService(TbService.class).getConcept(new String[] {lookup});
    		}
    		catch (Exception e) {}
    		// Next try id/name
    		try {
    			Concept c = Context.getConceptService().getConcept(lookup);
    			if (c != null) {
    				return c;
    			}
    		}
    		catch (Exception e) {}
    		// Next try uuid 
        	try {
        		Concept c = Context.getConceptService().getConceptByUuid(lookup);
    			if (c != null) {
    				return c;
    			}
        	}
        	catch (Exception e) {}
    	}
    	return null;
	}

	public void resetConceptMapCache() {
		this.conceptMap.resetCache();
	}
	
	public List<Encounter> getTbEncounters(Patient patient) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, null, TbUtil.getTbEncounterTypes(), null, false);
	}
	
	
	
	public List<TbPatientProgram> getAllTbPatientPrograms() {
		return getAllTbPatientProgramsInDateRange(null, null);
	}
	
	public District getDistrict(Integer districtId){
		District district = null;
				
		List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 3 and address_hierarchy_entry_id = " +  districtId, true);
		for (List<Object> temp : result) {
			Integer id = 0;
			String name = "";
	        for (int i = 0; i < temp.size(); i++) {
	        	Object value = temp.get(i);
	            if (value != null) {
	            	
	            	if(i == 0)
	            		id = (Integer) value;
	            	else if (i == 1)
	            		name = (String) value;
	            }
	        }
	        district = new District(name, id);
	        break;
	    }		
		
		return district;
	}
	
	public District getDistrict(String dName){
		District district = null;
		String query="Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 3 and name = '"+ dName+"'";
		System.out.println(query);
		List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 3 and name = '" +dName+"'", true);
		for (List<Object> temp : result) {
			Integer id = 0;
			String name = "";
	        for (int i = 0; i < temp.size(); i++) {
	        	Object value = temp.get(i);
	            if (value != null) {
	            	
	            	if(i == 0)
	            		id = (Integer) value;
	            	else if (i == 1)
	            		name = (String) value;
	            }
	        }
	        district = new District(name, id);
	        break;
	    }
		return district;
	}

	public ArrayList<Location> getLocationList(Integer oblastId, Integer districtId, Integer facilityId) {
		ArrayList<Location> locList = new ArrayList<Location>();
    	Location location = null;
    	if(oblastId==null && districtId==null && facilityId==null)
    	return null;	
    	else if(districtId == null) { //means they stopped at oblast
    		List<District> distList = getDistricts(oblastId);
    		for(District d : distList) {
    			location = getLocation(oblastId, d.getId(), null);
    			//if(location == null) {
    			if(location != null)
    				locList.add(location);
    			
				List<Facility> facs = getFacilities(d.getId().intValue());
				for(Facility f : facs) {
					location = null;
					location = getLocation(oblastId,d.getId(),f.getId());
    				if(location!=null) {
    					locList.add(location);
    				}
    			}
    			//}
    			
    			/*else {
    				locList.add(location);
    			}*/
    		}
    	}
    	
    	else if(facilityId == null) {//means they stopped at district either a single district or a set of facilities
    		location = Context.getService(TbService.class).getLocation(oblastId, districtId, null);
    		
    		if(location==null) { // district that has a set of facilities under it
    			List<Facility> facs = Context.getService(TbService.class).getFacilities(districtId);
    			for(Facility f : facs) {
    				location = Context.getService(TbService.class).getLocation(oblastId,districtId,f.getId());
    				if(location!=null) {
    					locList.add(location);
    				}
    			}
    		}
    		
    		else {
    			locList.add(location);
    		}
    	}
    	
    	else { //single location
    		location = Context.getService(TbService.class).getLocation(oblastId, districtId, facilityId);
    		locList.add(location);
    	}
    	
       	return locList;
	}
	
	//shakeeb function: for lab module and loction hierarchy
	public ArrayList<Location> getLocationHierarchy(Integer oblastId, Integer districtId, Integer facilityId) {
		ArrayList<Location> locList = new ArrayList<Location>();
    	Location location = null;
    	if(oblastId==null && districtId==null && facilityId==null)
    	return null;	
    	else if(districtId == null) { //means they stopped at oblast
    		List<District> distList = getDistricts(oblastId);
    		for(District d : distList) {
    			location = getLocation(oblastId, d.getId(), null);
    			if(location!=null)
    			{
    				List<Facility> facs = getFacilities(d.getId().intValue());
    				if(facs.isEmpty())
    					locList.add(location);  				
    				for(Facility f : facs) {
    					location = getLocation(oblastId,d.getId(),f.getId());
    					locList.add(location);
    				}
    			}
    		}
    	}
    	
    	else if(facilityId == null) {//means they stopped at district either a single district or a set of facilities
    		location = Context.getService(TbService.class).getLocation(oblastId, districtId, null);
    		locList.add(location);
    		if(location!=null) { // district that has a set of facilities under it
    			List<Facility> facs = Context.getService(TbService.class).getFacilities(districtId);
    			for(Facility f : facs) {
    				location = Context.getService(TbService.class).getLocation(oblastId,districtId,f.getId());
    				if(location!=null) {
    					locList.add(location);
    				}
    			}
    		}
    		
    		else {
    			locList.add(location);
    		}
    	}
    	
    	else { //single location
    		location = Context.getService(TbService.class).getLocation(oblastId, districtId, facilityId);
    		locList.add(location);
    	}
    	
    	return locList;
	}
	
	@Override
public List<District> getDistricts(Integer parentId){
		
		List<District> districtList = new ArrayList<District>();
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 3 and parent_id="+parentId, true);
		for (List<Object> temp : result) {
			Integer id = 0;
			String name = "";
	        for (int i = 0; i < temp.size(); i++) {
	        	Object value = temp.get(i);
	            if (value != null) {
	            	
	            	if(i == 0)
	            		id = (Integer) value;
	            	else if (i == 1)
	            		name = (String) value;
	            }
	        }
	        districtList.add(new District(name, id));
	    }
		return districtList;
	
	}
	
	@Override
	 public List<Facility> getFacilities(Integer parentId)
	    {
	    	List<Facility> facilityList = new ArrayList<Facility>();
	    	
			List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 6 and parent_id="+parentId + ";", true);
			for (List<Object> temp : result) {
				Integer id = 0;
				String name = "";
		        for (int i = 0; i < temp.size(); i++) {
		        	Object value = temp.get(i);
		            if (value != null) {
		            	
		            	if(i == 0)
		            		id = (Integer) value;
		            	else if (i == 1)
		            		name = (String) value;
		            }
		        }
		        facilityList.add(new Facility(name, id));
		    }
			return facilityList;
	    }
	
	 public Facility getFacility(Integer facilityId)
	    {
	    	Facility facility = null;
			
			List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 6 and address_hierarchy_entry_id = " +  facilityId + ";", true);
			for (List<Object> temp : result) {
				Integer id = 0;
				String name = "";
		        for (int i = 0; i < temp.size(); i++) {
		        	Object value = temp.get(i);
		            if (value != null) {
		            	
		            	if(i == 0)
		            		id = (Integer) value;
		            	else if (i == 1)
		            		name = (String) value;
		            }
		        }
		        facility = new Facility(name, id);
		        break;
		    }
	    	
			return facility;
	    }
	 
	 public Map<String, Object> getLocation(Location location,String type)
	 {
		Map<String, Object> map=new HashMap<String, Object>();
		List<Oblast> oblasts = Context.getService(TbService.class).getOblasts();
     	for(Oblast o : oblasts) {
     		if(o.getName().equals(location.getStateProvince())) {
     			List<District>districts = Context.getService(TbService.class).getDistricts(o.getId());
     			for(District d : districts) {
     				if(d.getName().equals(location.getCountyDistrict())) {
     					List<Facility>facilities = Context.getService(TbService.class).getFacilities(d.getId());
     					if(facilities != null ) {
     						for(Facility f : facilities) {
     							if(f.getName().equals(location.getRegion())) {
     								map.put("facilityName", f.getName());
     				     			map.put("facilityId", f.getId());
     				     			break;
     							}
     						}
     					}
     					map.put("districtName", d.getName());
     	     			map.put("districtId", d.getId());
     	     			break;
     				}
     			}
     			map.put("oblastName", o.getName());
     			map.put("oblastId", o.getId());
     			break; 
     		}
     	}
		return map;
	 }
	
	 public Location getAnyLocation(Integer oblastId, Integer districtId, Integer facilityId) {
	    	Oblast o=null;
	    	District d=null;
	    	Facility f = null;
	    	if(oblastId!=null)
	    		o = getOblast(oblastId);
	    	if(districtId!=null)
		    	d = getDistrict(districtId);
	    	if(facilityId!=null)
	    		f = getFacility(facilityId);
	    	
	    	
	    	Location location = null;
	    	
	    	List<Location> locations = Context.getLocationService().getAllLocations(false);
	    	
	    	if(d==null) {
	    		for(Location loc : locations){ 
	    				if(loc.getName().equals(o.getName())) {
	    				location = loc;
	    				return location;
	    			}
	    		}
	    	}
	    	else if(f==null)
	    	{
	    		for(Location loc : locations){ 
	    			if(loc.getName().equals(d.getName())) {
	    				location = loc;
	    				return location;
	    			}
	    		}
	    	}
	    	else{
	    		for(Location loc : locations){ 
	    			if(loc.getStateProvince()!=null && loc.getStateProvince().equals(o.getName()) && loc.getCountyDistrict()!=null && loc.getCountyDistrict().equals(d.getName()) && loc.getRegion()!=null && loc.getRegion().equals(f.getName()) ) {
	    				location = loc;
	    				return location;
	    			}
	    		}
	    	}
	    	return location;
	    }
			
	 
public Location getLocation(Integer oblastId, Integer districtId, Integer facilityId) {
    	
    	if(oblastId==null || districtId==null)
    		return null;
    	
    	Oblast o = getOblast(oblastId);
    	District d = getDistrict(districtId);
    	Facility f = null;
    	
    	if(facilityId!=null)
    		f = getFacility(facilityId);
    	
    	Location location = null;
    	
    	
    	List<Location> locations = Context.getLocationService().getAllLocations(true); //to include parent results as well who are voided later on
    	
    	if(f!=null) {
    		for(Location loc : locations){ 
    			if(loc.getStateProvince()!=null && loc.getStateProvince().equals(o.getName()) && loc.getCountyDistrict()!=null && loc.getCountyDistrict().equals(d.getName()) && loc.getRegion()!=null && loc.getRegion().equals(f.getName()) ) {
    				location = loc;
    				break;
    			}
    		}
    	}
    	
    	else {
    		for(Location loc : locations){ 
    			if(loc.getStateProvince()!=null && loc.getStateProvince().equals(o.getName()) && loc.getCountyDistrict()!=null && loc.getCountyDistrict().equals(d.getName())) {
    				location = loc;
    				break;
    			}
    		}
    	}
    	
    	return location;
    }
		
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(), startDate, endDate, null, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}
	
	public List<TbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(), startDate, endDate, null, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}


	public List<TbPatientProgram> getAllTbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(), null, endDate, startDate, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}
public List<TbPatientProgram> getTbPatientPrograms(Patient patient) {
    	
    	List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getTbProgram(), null, null, null, null, false);
    	
    	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
    }

	
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient) {
    	List<TbPatientProgram> programs = getTbPatientPrograms(patient);
    	
    	if (programs.size() > 0) {
    		return programs.get(programs.size() - 1);
    	} 
    	else {
    		return null;
    	}
    }
	
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<TbPatientProgram> programs = new LinkedList<TbPatientProgram>();
		
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if( (endDate == null || program.getDateEnrolled().before(endDate)) &&
	    			(program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(startDate)) ) {
	    			programs.add(program);
	    	}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	public TbPatientProgram getTbPatientProgramOnDate(Patient patient, Date date) {
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
				return program;
			}
		}

		return null;
	}
	
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		}
		else if (patientProgramId == -1) {
			return null;
		}
		else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			
			if (program == null || !program.getProgram().equals(getTbProgram())) {
				throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			}
			
			else {
				return new TbPatientProgram(program);
			}
		}
	}
	
	public LabResult getLabResult(Integer labResultId) {
		return getLabResult(Context.getEncounterService().getEncounter(labResultId));
	}
	
	public LabResult getLabResult(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong type
		if(encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"))) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new LabResultImpl(encounter);
	}
	
	public List<LabResult> getLabResults(Patient patient) {
		return getLabResults(patient, null, null, null);
	}
	
	public List<LabResult> getLabResults(Patient patient, Date startDate, Date endDate) {	
		return getLabResults(patient, startDate, endDate, null);
	}
	 
	public List<LabResult> getLabResults(Patient patient, Date startDateCollected, Date endDateCollected, Location locationCollected) {
		List<LabResult> labResults = new LinkedList<LabResult>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(specimenEncounterType);
		
		specimenEncounters = Context.getEncounterService().getEncounters(patient, locationCollected, startDateCollected, endDateCollected, null, specimenEncounterTypes, null, false);
		
		for(Encounter encounter : specimenEncounters) {	
			LabResult results= new LabResultImpl(encounter);
			labResults.add(results);
		}
		Collections.sort(labResults);
		return labResults;
	}
	
	public Specimen getSpecimen(Integer specimenId) {
		return getSpecimen(Context.getEncounterService().getEncounter(specimenId));
	}
	
	public Specimen getSpecimen(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong type
		if(encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"))) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(encounter);
	}
	
	public List<Specimen> getSpecimens(Patient patient) {
		return getSpecimens(patient, null, null, null);
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDate, Date endDate) {	
		return getSpecimens(patient, startDate, endDate, null);
	}
	 
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected, Location locationCollected) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(specimenEncounterType);
		
		specimenEncounters = Context.getEncounterService().getEncounters(patient, locationCollected, startDateCollected, endDateCollected, null, specimenEncounterTypes, null, false);
		
		for(Encounter encounter : specimenEncounters) {	
			specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
	}
	
	public Smear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new SmearImpl(obs);
	}

	public Smear getSmear(Integer obsId) {
		return getSmear(Context.getObsService().getObs(obsId));
	}
	
	public Culture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new CultureImpl(obs);
	}

	public Culture getCulture(Integer obsId) {
		return getCulture(Context.getObsService().getObs(obsId));
	}
	
	public Dst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new DstImpl(obs);
	}
	public Dst1 getDst1(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new Dst1Impl(obs);
	}

	public Dst getDst(Integer obsId) {
		return getDst(Context.getObsService().getObs(obsId));
	}
	
	public Dst1 getDst1(Integer obsId) {
		return getDst1(Context.getObsService().getObs(obsId));
	}
	
	public Dst2 getDst2(Integer obsId) {
		return getDst2(Context.getObsService().getObs(obsId));
	}
	
	public Program getTbProgram() {
    	return Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"));
    }
	
	public Program getMdrtbProgram() {
    	return Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
    }
	
   public Collection<Person> getProviders() {
		// TODO: this should be customizable, so that other installs can define there own provider lists?
		Role provider = Context.getUserService().getRole("Provider");
		Collection<User> providers = Context.getUserService().getUsersByRole(provider);
		
		// add all the persons to a sorted set sorted by name
		SortedSet<Person> persons = new TreeSet<Person>(new PersonByNameComparator());
		
		for (User user : providers) {
			persons.add(user.getPerson());
		}
		
		return persons;
	}
    
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return this.getConcept(TbConcepts.SMEAR_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return this.getConcept(TbConcepts.SMEAR_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return this.getConcept(TbConcepts.CULTURE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return this.getConcept(TbConcepts.CULTURE_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return this.getConcept(TbConcepts.DST_METHOD).getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		List<Concept> results = new LinkedList<Concept>();
		results.add(this.getConcept(TbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(TbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(TbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(TbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(TbConcepts.WAITING_FOR_TEST_RESULTS));
		
		return results;
	}
	
	public Collection<Location> getPossibleLabs(){
		Collection<Location> allLocations =  getAllLocations();//Context.getLocationService().getAllLocations();
		Collection<Location> allLabs = new ArrayList<Location>();
		
		for(Location loc : allLocations){
			
			String locName = loc.getName();
			if(locName.length() > 2 && TbUtil.areRussianStringsEqual(locName.substring(0, 2), "БЛ"))
				allLabs.add(loc);
			else if(locName.length() > 2 && TbUtil.areRussianStringsEqual(locName.substring(0, 2),"ГЦ"))
				allLabs.add(loc);
			else if(locName.length() > 4 && TbUtil.areRussianStringsEqual(locName.substring(0, 4), "ОЦБТ"))
				allLabs.add(loc);	
			else if (TbUtil.areRussianStringsEqual(locName,"РЦЗНТ Душанбе"))
				allLabs.add(loc);
			else if (TbUtil.areRussianStringsEqual(locName,"НРЛ"))
				allLabs.add(loc);
			else if (TbUtil.areRussianStringsEqual(locName,"НЛОЗ"))
				allLabs.add(loc);
		}
		
		return allLabs;
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return this.getConcept(TbConcepts.TYPE_OF_ORGANISM).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {	
		return this.getConcept(TbConcepts.SAMPLE_SOURCE).getAnswers();
	}
	
	
	public Collection<ConceptAnswer> getPossibleInvestigationPurposes() {	
		return this.getConcept(TbConcepts.INVESTIGATION_PURPOSE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return this.getConcept(TbConcepts.SPECIMEN_APPEARANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRequestingFacilities() {
		return this.getConcept(TbConcepts.REQUESTING_MEDICAL_FACILITY).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleMicroscopyResults() {
		return this.getConcept(TbConcepts.MICROSCOPY_RESULT).getAnswers();
	}
	 
    public Collection<ConceptAnswer> getPossibleAnatomicalSites() {
    	return this.getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
    }
    
    /**
     * @return the List of Concepts that represent the Drugs within the passed Drug Set
     */
    public List<Concept> getDrugsInSet(String... conceptMapKey) {
    	return getDrugsInSet(Context.getService(TbService.class).getConcept(conceptMapKey));
    }
    
    /**
     * @return the List of Concepts that represent the Drugs within the passed Drug Set
     */
    public List<Concept> getDrugsInSet(Concept concept) {
    	List<Concept> drugs = new LinkedList<Concept>();
    	if (concept != null) {
    		List<ConceptSet> drugSet = Context.getConceptService().getConceptSetsByConcept(concept);
    		if (drugSet != null) {
				for (ConceptSet drug : drugSet) {
					drugs.add(drug.getConcept());
				}
    		}
    	}
    	return drugs;    	
    }
	
    public List<Concept> getMdrtbDrugs() {
    	return getDrugsInSet(TbConcepts.TUBERCULOSIS_DRUGS);
    }
    
    public List<Concept> getAntiretrovirals() {
    	return getDrugsInSet(TbConcepts.ANTIRETROVIRALS);
    }
    
    public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes() {
    	return getPossibleWorkflowStates(Context.getService(TbService.class).getConcept(TbConcepts.TB_TX_OUTCOME));
    }

    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups() {
    	return getPossibleWorkflowStates(Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_GROUP));
    }
  
    /*public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
    	return getPossibleWorkflowStates(Context.getService(TbService.class).getConcept(TbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX));
    }  */  
    
    public String getColorForConcept(Concept concept) {
    	if(concept == null) {
    		log.error("Cannot fetch color for null concept");
    		return "";
    	}
    	
    	// initialize the cache if need be
    	if(colorMapCache == null) {
    		colorMapCache = loadCache(Context.getAdministrationService().getGlobalProperty("mdrtb.colorMap"));
    	}
    	
    	String color = "";
    	
    	try {
    		color = colorMapCache.get(concept.getId());
    	}
    	catch(Exception e) {
    		log.error("Unable to get color for concept " + concept.getId());
    		color = "white";
    	}
    	
    	return color;
    }
	
    public void resetColorMapCache() {
    	this.colorMapCache = null;
    }
    
	/**
	 * Utility functions
	 */
    
    private Set<ProgramWorkflowState> getPossibleWorkflowStates(Concept workflowConcept) {
    	// get the mdrtb program via the name listed in global properties
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"));
    	
    	// get the workflow via the concept name
    	for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
    		if (workflow.getConcept().equals(workflowConcept)) {
    			return workflow.getStates(false);
    		}
    	}
    	return null;
    }
    
    
    private Map<Integer,String> loadCache(String mapAsString) {
    	Map<Integer,String> map = new HashMap<Integer,String>();
    	
    	if(StringUtils.isNotBlank(mapAsString)) {    	
    		for(String mapping : mapAsString.split("\\|")) {
    			String[] mappingFields = mapping.split(":");
    			
    			Integer conceptId = null;
    			
    			// if this is a mapping code, need to convert it to the concept id
    			if(!TbUtil.isInteger(mappingFields[0])) {
    				Concept concept = getConcept(mappingFields[0]);
    				if (concept != null) {
    					conceptId = concept.getConceptId();
    				}
    				else {
    					throw new MdrtbAPIException("Invalid concept mapping value in the the colorMap global property.");
    				}
    			}
    			// otherwise, assume this is a concept id
    			else {
    				conceptId = Integer.valueOf(mappingFields[0]);
    			}
    			
    			map.put(conceptId, mappingFields[1]);
    		}
    	}
    	else {
    		// TODO: make this error catching a little more elegant?
    		throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
    	}
    	
    	return map;
    }
    
    public List<String> getAllRayonsTJK() {  	
    	return dao.getAllRayonsTJK();
    }

    public Collection<ConceptAnswer> getPossibleMtbResults() {
		return this.getConcept(TbConcepts.MTB_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return this.getConcept(TbConcepts.RIFAMPICIN_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleMoxResults() {
		return this.getConcept(TbConcepts.MOX).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCmResults() {
		return this.getConcept(TbConcepts.CM).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleEResults() {
		return this.getConcept(TbConcepts.E).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return this.getConcept(TbConcepts.ISONIAZID_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return this.getConcept(TbConcepts.XPERT_MTB_BURDEN).getAnswers();
	}

	@Override
	public TbPatientProgram getMdrtbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		}
		else if (patientProgramId == -1) {
			return null;
		}
		else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			
			if (program == null || !program.getProgram().equals(getMdrtbProgram())) {
				throw new MdrtbAPIException(patientProgramId + " does not reference an MDR-TB patient program");
			}
			
			else {
				return new TbPatientProgram(program);
			}
		}
	}

	@Override
	public void saveSpecimen(Specimen specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return;
		}
		
		// make sure getSpecimen returns the right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should an encounter)
		if(!(specimen.getSpecimen() instanceof Encounter)){
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
		//We need the specimen encounters to potentially be viewable by a bacteriology htmlform:
		Encounter enc = (Encounter) specimen.getSpecimen();
		String formIdWithWhichToViewEncounter = Context.getAdministrationService().getGlobalProperty("mdrtb.formIdToAttachToBacteriologyEntry");
		try {
		    if (formIdWithWhichToViewEncounter != null && !formIdWithWhichToViewEncounter.equals(""))
		        enc.setForm(Context.getFormService().getForm(Integer.valueOf(formIdWithWhichToViewEncounter)));
		} catch (Exception ex){
		    log.error("Invalid formId found in global property mdrtb.formIdToAttachToBacteriologyEntry");
		}
		
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter(enc);
	}

	@Override
	public Specimen createSpecimen(Patient patient) {
		// return null if the patient is null
		if(patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(patient);
	}
	
	@Override
	public int saveLabResult(LabResult labResult) {
		if (labResult == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return 0;
		}
		
		if(!(labResult.getLabResult() instanceof Encounter)){
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
		
		Encounter enc = (Encounter) labResult.getLabResult();
		
		// otherwise, go ahead and do the save
		Encounter e = Context.getEncounterService().saveEncounter(enc);
		return e.getEncounterId();
	}
	
	@Override
	public LabResult createLabResult(Patient patient) {
		// return null if the patient is null
		if(patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new LabResultImpl(patient);
	}
	
	
	
	@Override
	public int saveXpert(Xpert xpert) {
		if (xpert == null) {
			log.warn("Unable to save xpert: xpert object is null");
			return 0;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(xpert.getTest() instanceof Obs)) {
			throw new APIException("Not a valid xpert implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) xpert.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
		return obs.getEncounter().getId();
	}

	@Override
	public Xpert createXpert(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addXpert();
	}

	public Xpert getXpert(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new XpertImpl(obs);
	}

	public Xpert getXpert(Integer obsId) {
		return getXpert(Context.getObsService().getObs(obsId));
	}

	@Override
	public int saveHAIN(HAIN hain) {
		if (hain == null) {
			log.warn("Unable to save hain: hain object is null");
			return 0;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(hain.getTest() instanceof Obs)) {
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) hain.getTest(), "voided by Mdr-tb module specimen tracking UI");
				
		return obs.getEncounter().getId();
	}
	
	@Override
	public int saveHAIN2(HAIN2 hain2) {
		if (hain2 == null) {
			log.warn("Unable to save hain: hain object is null");
			return 0;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(hain2.getTest() instanceof Obs)) {
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) hain2.getTest(), "voided by Mdr-tb module specimen tracking UI");
				
		return obs.getEncounter().getId();
	}

	@Override
	public HAIN createHAIN(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addHAIN();
	}
	
	@Override
	public Culture createCulture(LabResult labResult) {
		if (labResult == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return labResult.addCulture();
	}
	
	@Override
	public HAIN2 createHAIN2(LabResult labResult) {
		if (labResult == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return labResult.addHAIN2();
	}

	public HAIN getHAIN(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new HAINImpl(obs);
	}

	public HAIN getHAIN(Integer obsId) {
		return getHAIN(Context.getObsService().getObs(obsId));
	}

	@Override
	public Smear createSmear(LabResult labResult) {
		if (labResult == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return labResult.addSmear();
	}

	@Override
	public void saveSmear(Smear smear) {
		if (smear == null) {
			log.warn("Unable to save smear: smear object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(smear.getTest() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}

	@Override
	public Culture createCulture(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addCulture();
	}

	
	@Override
	public int saveCulture(Culture culture) {
		if (culture == null) {
			log.warn("Unable to save culture: culture object is null");
			return 0;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(culture.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) culture.getTest(), "voided by Mdr-tb module specimen tracking UI");
		return obs.getEncounter().getId();
	}

	@Override
	public Dst createDst(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create dst: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst();
	}

	@Override
	public void saveDst(Dst dst) {
		if (dst == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(dst.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	@Override
	public void deleteDstResult(Integer dstResultId) {
		Obs obs = Context.getObsService().getObs(dstResultId);
		
		// the id must refer to a valid obs, which is a dst result
		if (obs == null || ! obs.getConcept().equals(this.getConcept(TbConcepts.DST_RESULT)) ) {
			throw new APIException ("Unable to delete dst result: invalid dst result id " + dstResultId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}

	@Override
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(report.getScannedLabReport() instanceof Obs)) {
			throw new APIException("Not a valid scanned lab report implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), "voided by Mdr-tb module specimen tracking UI");
	}

	
	@Override
	public void deleteSpecimen(Integer specimenId) {

		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		}
		else {
			Context.getEncounterService().voidEncounter(encounter, "voided by Mdr-tb module specimen tracking UI");
		}
		
	}

	@Override
	public void deleteTest(Integer testId) {

		Obs obs = Context.getObsService().getObs(testId);
		
		// the id must refer to a valid obs, which is a smear, culture, or dst construct
		if (obs == null) {
			throw new APIException ("Unable to delete specimen test: invalid test id " + testId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
		
	}

	
	@Override
	public Microscopy getMicroscopy(Integer obsId) {
		return getMicroscopy(Context.getObsService().getObs(obsId));
	}
	
	public Microscopy getMicroscopy(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new MicroscopyImpl(obs);
	}


	@Override
	public Microscopy createMicroscopy(LabResult labresult) {
		if (labresult == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return labresult.addMicroscopy();
	}
	
	@Override
	public Xpert createXpert(LabResult labresult) {
		if (labresult == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return labresult.addXpert();
	}
	
	@Override
	public HAIN createHAIN(LabResult labresult) {
		if (labresult == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return labresult.addHAIN();
	}

	
	@Override
	public int saveMicroscopy(Microscopy microscopy) {
		if (microscopy == null) {
			log.warn("Unable to save xpert: xpert object is null");
			return 0;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(microscopy.getTest() instanceof Obs)) {
			throw new APIException("Not a valid xpert implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) microscopy.getTest(), "voided by Mdr-tb module specimen tracking UI");

		return obs.getEncounter().getId();
		
	}
	
	
	@Override
	public void updateMicroscopy(Microscopy microscopy) {
		if (microscopy == null) {
			log.warn("Unable to save xpert: xpert object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(microscopy.getTest() instanceof Obs)) {
			throw new APIException("Not a valid xpert implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		List<Obs> obsArray = Context.getObsService().findObsByGroupId(Integer.parseInt(microscopy.getId()));
		
		for(Obs o : obsArray)
			Context.getObsService().saveObs(o, "Update Microscopy from Labmodule UI...");
		
	}
	
	@Override
	public List<Oblast> getOblasts(){
		
		List<Oblast> oblastList = new ArrayList<Oblast>();
		try{
		List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 2", true);
		for (List<Object> temp : result) {
			Integer id = 0;
			String name = "";
	        for (int i = 0; i < temp.size(); i++) {
	        	Object value = temp.get(i);
	            if (value != null) {
	            	
	            	if(i == 0)
	            		id = (Integer) value;
	            	else if (i == 1)
	            		name = (String) value;
	            }
	        }
	        oblastList.add(new Oblast(name, id));
	    }
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally{
		return oblastList;			
	}
	}
	
	public Oblast getOblast(Integer oblastId){
		Oblast oblast = null;
				
		List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 2 and address_hierarchy_entry_id = " +  oblastId, true);
		for (List<Object> temp : result) {
			Integer id = 0;
			String name = "";
	        for (int i = 0; i < temp.size(); i++) {
	        	Object value = temp.get(i);
	            if (value != null) {
	            	
	            	if(i == 0)
	            		id = (Integer) value;
	            	else if (i == 1)
	            		name = (String) value;
	            }
	        }
	        oblast = new Oblast(name, id);
	        break;
	    }

		return oblast;
	}
	    public List<Location> getLocationsFromOblastName(Oblast oblast){
    	List<Location> locationList = new ArrayList<Location>();
    	
    	List<Location> locations = Context.getLocationService().getAllLocations(false);
    	
    	for(Location loc : locations){
    		    		
    		if(loc.getStateProvince() != null){
	    		if(loc.getStateProvince().equals(oblast.getName()))
	    			locationList.add(loc);
    		}
    	}
    	return locationList;
    }
    
    public String getAllPatientTestedDuring(Date startDate, Date endDate, List<Location> locList){
		StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id) ");
		q.append("from encounter e ");
		if(!locList.isEmpty())
			q.append(" , location l ");
		q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
				
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		q.append("and e.voided = 0 ");
		
		if(!locList.isEmpty()){
			q.append("and e.location_id = l.location_id and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) and l.retired = 0");
		}
		
		System.out.println("PATIENT TESTED -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getAllPositivePatientDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id)");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o on e.encounter_id = o.encounter_id and o.voided = 0 and ");
    	q.append("((o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT) + " and o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE_PLUS) + ") or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ")) or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + "))) ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
    	System.out.println("POSITIVE PATIENT -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
    	
    }
    
    public String getAllDiagnosticPatientTestedDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id)  ");
		q.append("from encounter e, obs o ");
		if(!locList.isEmpty())
			q.append(" , location l ");
		q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
		q.append("and o.encounter_id = e.encounter_id and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) +" ");
		q.append(" and (o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA)  + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");		
		q.append("and e.voided = 0 and o.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		if(!locList.isEmpty()){
			q.append("and e.location_id = l.location_id and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) and l.retired = 0 ");
		}
		
		System.out.println("DIAGNOSTIC PATIENT TESTED -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getPositiveDiagnosticPatientDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id) ");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o on e.encounter_id = o.encounter_id and o.voided = 0 and ");
    	q.append("((o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT) + " and o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE_PLUS) + ") or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ")) or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + "))) ");
    	q.append("INNER JOIN obs o1 on o.encounter_id = o1.encounter_id and o1.voided = 0 and ");
    	q.append("o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ( o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
    	System.out.println("DIAGNOSTIC POSITIVE PATIENT -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
    	
    }
    
    public String getAllFollowupPatientTestedDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id)  ");
		q.append("from encounter e, obs o ");
		if(!locList.isEmpty())
			q.append(" , location l ");
		q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
		q.append("and o.encounter_id = e.encounter_id and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) +" ");
		q.append(" and (o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_123)  + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_4) + ") ");		
		q.append("and e.voided = 0 and o.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		if(!locList.isEmpty()){
			q.append("and e.location_id = l.location_id and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) and l.retired = 0 ");
		}
		
		System.out.println("FOLLOW-UP PATIENT TESTED -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getAllPatientTestedDuring(Date startDate, Date endDate, List<Location> locList, Concept... concepts){
		StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id)  ");
		//q.append("select count(o.obs_id) ");
		q.append("from encounter e ");
		if(!locList.isEmpty())
			q.append(" , location l ");
		if(concepts.length > 0)
			q.append(" , obs o ");
		q.append(" where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
		if(concepts.length > 0)
		{
			q.append(" and e.patient_id = o.person_id  ");
			q.append(" and o.encounter_id = e.encounter_id  ");
			q.append( " and o.concept_id in ( ");
			for (int i=0; i<concepts.length; i++) {
				q.append( " " + concepts[i].getConceptId() + " ");
				
				if ((i+1)<concepts.length) {
					q.append(" , ");
				}
			}	
	    	q.append(" ) ");
		}
		
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		q.append(" and e.voided = 0 ");
		
		if(!locList.isEmpty()){
			q.append(" and e.location_id = l.location_id and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) and l.retired = 0");
		}
		
		
		System.out.println("PATIENT TESTED -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
    }
    
    public String getPositiveFollowupPatientDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id) ");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o on e.encounter_id = o.encounter_id and o.voided = 0 and ");
    	q.append("((o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT) + " and o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE_PLUS) + ") or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ")) or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + "))) ");
    	q.append("INNER JOIN obs o1 on o.encounter_id = o1.encounter_id and o1.voided = 0 and ");
    	q.append("o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ( o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_123) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_4) + ") ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
    	System.out.println("DIAGNOSTIC ALL PATIENT TESTED -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
    	
    }
    
    public String getAllMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
		StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id) ");
		q.append("from encounter e, obs o ");
		if(!locList.isEmpty())
			q.append(" , location l ");
		q.append("where		e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " ");
		q.append("and o.encounter_id = e.encounter_id and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) +" ");
				
		q.append("and e.voided = 0 and o.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		if(!locList.isEmpty()){
			q.append("and e.location_id = l.location_id and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) and l.retired = 0 ");
		}
		
		System.out.println("MICROSCOPY RESULT -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getAllPositiveMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id) ");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o on e.encounter_id = o.encounter_id and o.voided = 0 and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
    	q.append("INNER JOIN obs o1 on o.obs_id = o1.obs_group_id and o1.voided = 0 and o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and ");
    	q.append("!(o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ") ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
    	System.out.println("ALL POSITIVE MICROSCOPY -> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
    	
    }
    
    public String getDiagnosticMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id)  ");
		q.append("from encounter e ");
		q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 and o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ");
		q.append("( o1.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");
		q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
		if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
		q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println("DIAGNOSTIC MICROSCOPY RESULT ->" + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getDiagnosticPositiveMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id) ");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 ");
    	q.append("and o1.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ( o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");
    	q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
    	q.append("INNER JOIN obs o3 on o2.obs_id = o3.obs_group_id and o3.voided = 0 and o3.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and ");
    	q.append("!(o3.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o3.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ") ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
    	System.out.println("DIAGNOSTIC POSITIVE MICROSCOPY RESULT ->" + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
  
    }
    
    public String getFollowupMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id) ");
		q.append("from encounter e ");
		q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 and o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ");
		q.append("( o1.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_123) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_4) + ") ");
		q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
		if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}

		q.append("where e.encounter_type =  " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "  and e.voided = 0 ");
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println("FOLLOWUP MICROSCOPY RESULT ->"+ q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    
    
    public String getFollowupPositiveMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id) ");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 ");
    	q.append("and o1.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ( o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_123) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_4) + ") ");
    	q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
    	q.append("INNER JOIN obs o3 on o2.obs_id = o3.obs_group_id and o3.voided = 0 and o3.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and ");
    	q.append("!(o3.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o3.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ") ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
    	System.out.println("FOLLOWUP POSITIVE MICROSCOPY RESULT ->" + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
  
    }
    
    public String getAllPHCResultDuring(Date startDate, Date endDate, List<Location> locList){
		StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id)  ");
		q.append("from encounter e ");
    	q.append("INNER JOIN obs o on e.encounter_id = o.encounter_id and o.voided = 0 and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.REQUESTING_MEDICAL_FACILITY) +" and o.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.PHC) +" ");
    	q.append("INNER JOIN obs o1 on o.encounter_id = o1.encounter_id and o1.voided and (o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " || o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT) + " ");
    	q.append("|| o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT) + " ||o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT) + " || o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT) + ") ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println("PHC ALL RESULTS" + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getPositivePHCResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
    	q.append("select distinct(e.encounter_id) ");
    	q.append("from encounter e ");
    	q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 and o1.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.REQUESTING_MEDICAL_FACILITY) +" and o1.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.PHC) +" ");
    	q.append("INNER JOIN obs o on o1.encounter_id = o.encounter_id and o.voided = 0 and ");
    	q.append("((o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT) + " and o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE_PLUS) + ") or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + ")) or ");
    	q.append("(o.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT) + " and !(o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE) + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE) + "))) ");
    	if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
    	q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
    	if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
    	
		System.out.println("PHC POSITIVE RESULTS" + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getRatioDiagnosticMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select count(distinct(o1.encounter_id))/count(e.patient_id)  ");
		q.append("from encounter e ");
		q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 and o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ");
		q.append("( o1.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");
		q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
		if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
		q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println("Ratio Microscopy ---- >> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		String res = "";
		
		for(List<Object> r : result){
			
			res =  String.format("%.2f", r.get(0));			
			break;
			
		}
		
		if(res.contains("nu"))
			res = "0";
		
		return res;
	}
    
    public String getRateFollowupMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select count(distinct(e.encounter_id))/count(e.patient_id)  ");
		q.append("from encounter e ");
		q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 and o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ");
		q.append("( o1.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_123) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CONTROL_CAT_4) + ") ");
		q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
		if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
		q.append("where e.encounter_type = " + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println("Ratio Microscopy ---- >> " + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		String res = "";
		
		for(List<Object> r : result){
			
			res =  String.format("%.2f", r.get(0));			
			break;
			
		}
			
		if(res.contains("nu"))
			res = "0";
		
		return res;
	}
    
    public String getSalivaDiagnosticMicroscopyResultDuring(Date startDate, Date endDate, List<Location> locList){
    	StringBuilder q = new StringBuilder();
		q.append("select distinct(e.encounter_id)  ");
		q.append("from encounter e ");
		q.append("INNER JOIN obs o1 on e.encounter_id = o1.encounter_id and o1.voided = 0 and o1.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) + " and ");
		q.append("( o1.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA) + " or o1.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");
		q.append("INNER JOIN obs o2 on o1.encounter_id = o2.encounter_id and o2.voided = 0 and o2.concept_id = " + Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT) + " ");
		q.append("INNER JOIN obs o3 on o2.encounter_id = o3.encounter_id and o3.voided = 0 and ");
		q.append("o3.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_APPEARANCE) + " and o3.value_coded = "+ Context.getService(TbService.class).getConcept(TbConcepts.SALIVA) + " ");
		if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
		q.append("where e.encounter_type =  "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() +" and e.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println(q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		return String.valueOf(result.size());
	}
    
    public String getAverageWeeklyLoadPerLabTechnician(Date startDate, Date endDate, List<Location> locList, int noOfWeeks){
    	StringBuilder q = new StringBuilder();
		q.append("select u.user_id, count(distinct(e.encounter_id)) ");
		q.append("from users u ");
		q.append("INNER JOIN openmrspih.user_role ur on u.user_id = ur.user_id and ur.role = 'Lab Technician' || ur.role = 'Lab Supervisor' ");
		q.append("INNER JOIN openmrspih.encounter e on u.user_id = e.creator ");
		if(!locList.isEmpty()){
			q.append("INNER JOIN location l on e.location_id = l.location_id and l.retired = 0 and ( ");
			
			for(int i = 0; i<locList.size(); i++){
				
				Location loc = locList.get(i);
				
				if(i != 0)
					q.append(" or ");
				
				q.append(" l.name = '" + loc.getName() + "'");
				
			}
			
			q.append(" ) ");
		}
		q.append("where e.encounter_type = " +Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + " and e.voided = 0 ");
		
		if (startDate != null) {
			q.append("and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println(q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("size", String.valueOf(result.size()));
		
		Long sumOfLoad = new Long("0");
		for(List<Object> obj : result){
			
			sumOfLoad = sumOfLoad + (Long)obj.get(1);
			
		}
		
		Double res = (double) ((sumOfLoad) / noOfWeeks ) / result.size();
		
		return String.format("%.2f", res);
	}
    
    public Collection<Location> getAllLocations(){
    	List<Location> locations = Context.getLocationService().getAllLocations();
    	List<Location> noLabs = new ArrayList<Location>();  
//    	for(Location l : locations)
//		{
//			if(l.getName().startsWith("БЛ") || l.getName().startsWith("BL"))
//			{
//				System.out.println(l.getName());
//				locations.remove(l);
//			}
//			else
//			{
//				noLabs.add(l);
//			}
//		}

		return Context.getLocationService().getAllLocations(false);

	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleR() {
		return this.getConcept(TbConcepts.R).getAnswers();
		}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleH() {
		return this.getConcept(TbConcepts.H).getAnswers();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleXpertMtbRif() {
		return this.getConcept(TbConcepts.XPERT_MTB_RIF).getAnswers();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleMgitResults() {
		return this.getConcept(TbConcepts.MGIT_CULTURE).getAnswers();	
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleMtIdTest() {
		return this.getConcept(TbConcepts.MT_ID_TEST).getAnswers();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCultureTypes() {
		return this.getConcept(TbConcepts.CULTURE_TYPE).getAnswers();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getAllPlacesOfCulture() {
		return this.getConcept(TbConcepts.PLACE_OF_CULTURE).getAnswers();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleLjResults() {
		return this.getConcept(TbConcepts.LJ_RESULT).getAnswers();	
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSentResults() {
		return this.getConcept(TbConcepts.SENT_TO_CULTURE).getAnswers();	
	}
	
	@Override
	@Transactional
	public int saveDst1(Dst1 dst1) {
		if (dst1 == null) {
			log.warn("Unable to save dst1: dst1 object is null");
			return 0;
		}
		
		// make sure getDst1 returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(dst1.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst1 implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) dst1.getTest(), "voided by Mdr-tb module specimen tracking UI");
		return obs.getEncounter().getId();
	}

	@Override
	@Transactional
	public int saveDst2(Dst2 dst2) {
		if (dst2 == null) {
			log.warn("Unable to save dst1: dst1 object is null");
			return 0;
		}
		
		// make sure getDst2 returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(dst2.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Obs obs = Context.getObsService().saveObs((Obs) dst2.getTest(), "voided by Mdr-tb module specimen tracking UI");
		return obs.getEncounter().getId();
	}

	@Override
	public Dst1 createDst1(LabResult labResult) {
		if (labResult == null) {
			log.error("Unable to create xpert: specimen is null");
			return null;
		}
		
		// add the culture to the specimen
		return labResult.addDst1();
	}

	@Override
	public Dst2 createDst2(LabResult labResult) {
		if (labResult == null) {
			log.error("Unable to create xpert: specimen is null");
			return null;
		}
		
		// add the culture to the specimen
		return labResult.addDst2();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleDrugResistance() {
		return this.getConcept(TbConcepts.DST_RESISTANCE).getAnswers();	

	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleDstTypes() {
		return this.getConcept(TbConcepts.TYPE_OF_DEST_REPORTED).getAnswers();	

	}

	@Override
	public Dst2 getDst2(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new Dst2Impl(obs);			
	}

	@Override
	public Dst1 createDst1(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst1();
	}

	@Override
	public Dst2 createDst2(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst2();	}

	@Override
	public String getAverageCultureInoculationDays(Date startDate,
			Date endDate, Concept inoculateDate, Concept growthDate,Concept conditionalConcept, Concept... codedValues) {
		StringBuilder cultureDays =  new StringBuilder();
		cultureDays.append("SELECT  round(abs(avg(datediff(o1.value_datetime,o2.value_datetime))),1) as diff FROM obs o1 inner join obs o2 ");
		cultureDays.append(" on o1.encounter_id = o2.encounter_id and ");
		cultureDays.append(" o1.concept_id = '"  + inoculateDate.getConceptId() + "'");
		cultureDays.append(" and o2.concept_id = '" + growthDate.getConceptId()+ "'");
		if(conditionalConcept!= null && codedValues.length>0)
		{
			cultureDays.append(" inner join obs o3 on o3.concept_id = 425 ");
			cultureDays.append(" and o3.value_coded in (") ;
			for (int i=0; i<codedValues.length; i++) {
				cultureDays.append(codedValues[i].getConceptId());
				if ((i+1)<codedValues.length) {
					cultureDays.append(",");
				}
			}
			cultureDays.append(") ");
			cultureDays.append(" and o3.encounter_id = o1.encounter_id");
		}
		if(startDate!= null)
			cultureDays.append(" and o1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		if(endDate!= null)
			cultureDays.append(" and o1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		
		System.out.println("AVERAGE CULTURE DAYS -> " + cultureDays.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(cultureDays.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String average = values.get(0).toString();
			return average;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getCodedCohortByParentObs(Date startDate, Date endDate,Concept parent, Concept questionConcept, Concept... answers) {
		StringBuilder q = new StringBuilder();
		 q.append("	select childObs.obs_id ");
		 q.append("	 from obs childObs ");
		 q.append("	inner join ");
		 q.append("	 obs parentObs on childObs.obs_group_id = parentObs.obs_id and parentObs.concept_id = " + parent.getConceptId());
		 q.append("	and childObs.concept_id  = " + questionConcept.getConceptId());
		 if(answers.length >0 )
		 {
			 q.append("	and childObs.value_coded  in( "); 
			 for(int i =0; i < answers.length;i ++)
			 {
				 q.append( answers[i].getConceptId());
				 if((i+1) < answers.length)
				 {
					 q.append(",");
				 }
			 }
			 q.append("	)");
		 }
		 q.append("	inner join encounter e on e.encounter_id = parentObs.encounter_id");
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		System.out.println("PARENT OBS -> " + parent.getName().getName() + "");
		return String.valueOf(result.size());
	}

	@Override
	public String getHainAllSensitive(Date startDate, Date endDate, List<Location> locList) {
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT);
		Concept rif = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept inh = Context.getService(TbService.class).getConcept(TbConcepts.ISONIAZID_RESISTANCE);
		Concept notDetected = Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2 , obs as obs3, encounter as e");
		q.append(" where obs1.concept_id = '" + hain1.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id");
		q.append(" and obs3.obs_group_id = obs1.obs_id");
		q.append(" and obs2.obs_group_id = obs1.obs_id");
		q.append(" and obs2.concept_id = '" + rif.getConceptId() + "'" );
		q.append(" and obs2.value_coded = '" + notDetected.getConceptId() + "'");
		q.append(" and obs3.concept_id = '" + inh.getConceptId() + "'");
		q.append(" and obs3.value_coded = '" + notDetected.getConceptId() + "'");
		q.append(" and obs1.encounter_id = e.encounter_id");
		if(!locList.isEmpty()){
			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());
				Location loc = locList.get(i);	
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		q.append(" and e.encounter_type= ' "  + /*LAB_ENCOUNTER_TYPE_ID*/ Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}
	
	@Override
	public String getHainAllIndefinite(Date startDate, Date endDate, List<Location> locList) {
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT);
		Concept rif = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept inh = Context.getService(TbService.class).getConcept(TbConcepts.ISONIAZID_RESISTANCE);
		Concept undetermined = Context.getService(TbService.class).getConcept(TbConcepts.UNDETERMINED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2 , obs as obs3, encounter as e");
		q.append(" where obs1.concept_id = '" + hain1.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id");
		q.append(" and obs3.obs_group_id = obs1.obs_id");
		q.append(" and obs2.obs_group_id = obs1.obs_id");
		q.append(" and obs2.concept_id = '" + rif.getConceptId() + "'" );
		q.append(" and obs2.value_coded = '" + undetermined.getConceptId() + "'");
		q.append(" and obs3.concept_id = '" + inh.getConceptId() + "'");
		q.append(" and obs3.value_coded = '" + undetermined.getConceptId() + "'");
		q.append(" and obs1.encounter_id = e.encounter_id");
		if(!locList.isEmpty()){
			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());
				Location loc = locList.get(i);	
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		q.append(" and e.encounter_type= ' "  + /*LAB_ENCOUNTER_TYPE_ID*/ Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getHainAllResistant(Date startDate, Date endDate , List<Location> locList) {
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT);
		Concept rif = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept inh = Context.getService(TbService.class).getConcept(TbConcepts.ISONIAZID_RESISTANCE);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2 , obs as obs3 , encounter as e ");
		q.append(" where obs1.concept_id = '" + hain1.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id");
		q.append(" and obs3.obs_group_id = obs1.obs_id");
		q.append(" and obs2.obs_group_id = obs1.obs_id");
		q.append(" and obs2.concept_id = '" + rif.getConceptId() + "'" );
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");
		q.append("and obs3.concept_id = '" + inh.getConceptId() + "'");
		q.append("and obs3.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and obs1.encounter_id = e.encounter_id ");
		q.append("and e.encounter_type= ' "  + /*LAB_ENCOUNTER_TYPE_ID*/ Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if(!locList.isEmpty()){
			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());
				Location loc = locList.get(i);	
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getHainResistantByDrug(Date startDate, Date endDate, Concept drug , List<Location> locList) {
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2, encounter as e");
		q.append(" where obs1.concept_id = '" + hain1.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs2.obs_group_id = obs1.obs_id");
		q.append(" and obs2.concept_id = '" + drug.getConceptId() + "'" );
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and obs1.encounter_id = e.encounter_id ");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){
			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getAllPatientByTestTypeDuring(Date startDate, Date endDate,
			List<Location> locList, Concept test) {

	 	EncounterType labEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
		StringBuilder q = new StringBuilder();
		q.append("Select distinct(e.encounter_id) from encounter e");
		q.append(" inner join obs o on e.patient_id = o.person_id");
		q.append(" 	and e.encounter_type = " + labEncounterType.getId());
		q.append(" and o.concept_id = " + test.getConceptId() );
				
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return null;
	}

	@Override
	public String getHain1MTB(Date startDate, Date endDate, List<Location> locList) {
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT);
		Concept mtb_resistance = Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
		Concept mtb_positive = Context.getService(TbService.class).getConcept(TbConcepts.MT_POSITIVE);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED); //this was used before so used for calculating older records
		Concept not_detected = Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED); //this was used before so used for calculating older records
		Concept mtb_negative = Context.getService(TbService.class).getConcept(TbConcepts.MT_NEGATIVE);
		
		StringBuilder q = new StringBuilder();
		q.append(" select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2, encounter as e");
		q.append(" where obs1.concept_id = '" + hain1.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs2.obs_group_id = obs1.obs_id");
		q.append(" and obs2.concept_id = '" + mtb_resistance.getConceptId() + "'" );
		q.append(" and obs2.value_coded in( '" + mtb_negative.getConceptId() + "','" + not_detected.getConceptId() + "')");
		q.append(" and obs1.encounter_id = e.encounter_id ");
		
		if(!locList.isEmpty()){
			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());
				Location loc = locList.get(i);	
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '"  + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		System.out.println("MTB NEGATIVE  - >" + q.toString());
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
		
	}
	
	@Override
	public String getHain2MTB(Date startDate, Date endDate, List<Location> locList) {
		Concept hain1 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept mtb_resistance = Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
		Concept mtb_positive = Context.getService(TbService.class).getConcept(TbConcepts.MT_POSITIVE);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED); //this was used before so used for calculating older records
		Concept not_detected = Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED); //this was used before so used for calculating older records
		Concept mtb_negative = Context.getService(TbService.class).getConcept(TbConcepts.MT_NEGATIVE);
		
		StringBuilder q = new StringBuilder();
		q.append(" select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2, encounter as e");
		q.append(" where obs1.concept_id = '" + hain1.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs2.obs_group_id = obs1.obs_id");
		q.append(" and obs2.concept_id = '" + mtb_resistance.getConceptId() + "'" );
		q.append(" and obs2.value_coded in( '" + mtb_negative.getConceptId() + "','" + not_detected.getConceptId() + "')");
		q.append(" and obs1.encounter_id = e.encounter_id ");
		
		if(!locList.isEmpty()){
			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());
				Location loc = locList.get(i);	
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '"  + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
		
	}
	
	@Override
	public String getHain2AllSensitive(Date startDate, Date endDate,	List<Location> locList) {
		Concept cm  = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		Concept emb  = Context.getService(TbService.class).getConcept(TbConcepts.E);
		Concept mox = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept notDetected = Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED);
	
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2 , obs as obs3, obs as obs4,encounter e");
		q.append(" where obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id");
		q.append(" and obs1.encounter_id = obs4.encounter_id");
		
//		q.append(" and obs2.obs_group_id = obs1.obs_id");
//		q.append(" and obs3.obs_group_id = obs1.obs_id");
//		q.append(" and obs4.obs_group_id = obs1.obs_id");
		
		q.append(" and obs2.concept_id = '" + cm.getConceptId() + "'" );
		q.append(" and obs2.value_coded = '" + notDetected.getConceptId() + "'");
		q.append(" and obs3.concept_id = '" + mox.getConceptId() + "'");
		q.append(" and obs3.value_coded = '" + notDetected.getConceptId() + "'");
		q.append(" and obs4.concept_id = '" + emb.getConceptId() + "'");
		q.append(" and obs4.value_coded = '" + notDetected.getConceptId() + "'");
		q.append(" and obs1.encounter_id = e.encounter_id ");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getHain2ResistanceByDrug(Date startDate, Date endDate, List<Location> locList, Concept... drug) {
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		if(drug.length<1)
			return "0";
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2");
		q.append(" where obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		//q.append(" and obs2.obs_group_id = obs1.obs_id");
		
		q.append(" and obs2.concept_id in (" );
			for(int i=0; i < drug.length; i ++)
			{
				q.append(drug[i].getConceptId());
				if(i+1 < drug.length)
				{
					q.append(",");
				}
			}
		q.append(")");	
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");	 		
		q.append(" and obs1.encounter_id IN (select encounter_id from encounter where encounter_type = ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId()  + "'" );
		if(!locList.isEmpty()){			
			q.append(" and encounter.location_id IN ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(" )");
		}
		q.append(" )");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getHain2AllResistant(Date startDate, Date endDate,	List<Location> locList) {
		Concept cm  = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		Concept emb  = Context.getService(TbService.class).getConcept(TbConcepts.E);
		Concept mox = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
	
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2 , obs as obs3, obs as obs4,encounter e");
		q.append(" where obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id");
		q.append(" and obs1.encounter_id = obs4.encounter_id");
		
//		q.append(" and obs2.obs_group_id = obs1.obs_id");
//		q.append(" and obs3.obs_group_id = obs1.obs_id");
//		q.append(" and obs4.obs_group_id = obs1.obs_id");
		
		q.append(" and obs2.concept_id = '" + cm.getConceptId() + "'" );
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and obs3.concept_id = '" + mox.getConceptId() + "'");
		q.append(" and obs3.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and obs4.concept_id = '" + emb.getConceptId() + "'");
		q.append(" and obs4.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and obs1.encounter_id = e.encounter_id ");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertMTPositive(Date startDate, Date endDate, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded not in ('" + microscopyTestNotDone.getConceptId() + "','" + microscopyNegative.getConceptId() + "')");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertMTNegative(Date startDate, Date endDate,List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded = '" + microscopyNegative.getConceptId() + "'");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + 
		Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}

	}

	@Override
	public String getXpertPositiveMtPositiveSusceptible(Date startDate,Date endDate, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept rifResistance = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept not_detected= Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED);
		
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded not in ('" + microscopyTestNotDone.getConceptId() + "','" + microscopyNegative.getConceptId() + "')");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join obs gxresult  on gxresult.concept_id = '" + rifResistance.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + not_detected.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}

	}

	@Override
	public String getXpertPositiveMtPositiveResistant(Date startDate,Date endDate, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept rifResistance = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept detected= Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded not in ('" + microscopyTestNotDone.getConceptId() + "','" + microscopyNegative.getConceptId() + "')");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join obs gxresult  on gxresult.concept_id = '" + rifResistance.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertPositiveMtNegativeSusceptible(Date startDate,Date endDate, List<Location> locList) {
		
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept rifResistance = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept not_detected= Context.getService(TbService.class).getConcept(TbConcepts.NOT_DETECTED);
		
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded in ('" + microscopyTestNotDone.getConceptId() + "','" + microscopyNegative.getConceptId() + "')");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join obs gxresult  on gxresult.concept_id = '" + rifResistance.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + not_detected.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}

	}

	@Override
	public String getXpertPositiveMtNegativeResistant(Date startDate,Date endDate, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept rifResistance = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept detected= Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded in ('" + microscopyTestNotDone.getConceptId() + "','" + microscopyNegative.getConceptId() + "')");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join obs gxresult  on gxresult.concept_id = '" + rifResistance.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}

	}

	@Override
	public String getXpertNegative(Date startDate, Date endDate,List<Location> locList) {
	
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept mtbResult= Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
		Concept negative  = Context.getService(TbService.class).getConcept(TbConcepts.MTB_NEGATIVE);
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);

		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(xpert.encounter_id)) from obs xpert ");
		q.append(" inner join obs gxresult on gxresult.concept_id = '" + mtbResult.getConceptId() + "'");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + negative.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join obs mic on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertOnly(Date startDate, Date endDate,List<Location> locList) {
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept rifResistance = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept negative  = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopy= Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT);

		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(xpert.encounter_id)) from encounter e inner join obs xpert on xpert.encounter_id  = e.encounter_id");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId()  + "'");
		q.append(" and e.encounter_type= ' "  + 
		Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" left join obs missing on missing.encounter_id = e.encounter_id ");
		q.append(" and missing.concept_id = '" + microscopy.getConceptId() + "'");
		q.append(" where missing.obs_id is null");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}

	}

	@Override
	public String getXpertOnlyByResult(Date startDate, Date endDate,List<Location> locList, Concept result) {
		
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept rifResistance = Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE);
		Concept microscopy= Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT);
		Concept mtbResult = Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(xpert.encounter_id)) from encounter e inner join obs xpert on xpert.encounter_id  = e.encounter_id");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId()  + "'");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" inner join obs result on result.concept_id  = '"  + rifResistance.getConceptId() + "'" );
		q.append(" and result.value_coded = '" + result.getConceptId() + "'");
		q.append(" and result.obs_group_id = xpert.obs_id" );		
		q.append(" left join obs missing on missing.encounter_id = e.encounter_id ");
		q.append(" and missing.concept_id = '" + microscopy.getConceptId() + "'");
		q.append(" where missing.obs_id is null");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> r = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(r.size()==1)
		{
			List<Object>values = r.get(0);
			String s = values.get(0).toString();
			return s;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertError(Date startDate, Date endDate,
			List<Location> locList) {
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept mtbResult= Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
		Concept error  = Context.getService(TbService.class).getConcept(TbConcepts.ERROR);
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);

		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(xpert.encounter_id)) from obs xpert ");
		q.append(" inner join obs gxresult on gxresult.concept_id = '" + mtbResult.getConceptId() + "'");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + error.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join obs mic on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertNoResults(Date startDate, Date endDate,
			List<Location> locList) {
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		Concept mtbResult= Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
		Concept test_not_done  = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);

		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(xpert.encounter_id)) from obs xpert ");
		q.append(" inner join obs gxresult on gxresult.concept_id = '" + mtbResult.getConceptId() + "'");
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" and gxresult.value_coded = '" + test_not_done.getConceptId() + "'");
		q.append(" and gxresult.obs_group_id = xpert.obs_id");
		q.append(" and gxresult.encounter_id = xpert.encounter_id");
		q.append(" inner join obs mic on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	//////////ADDED BY ALI FOR DST REPORT
	
	 public List<Encounter> getAllPatientTestedAtLocationDuring(Date startDate, Date endDate, List<Location> locList){

		 HashSet<Location> locSet = new HashSet<Location>();
		 
		 if(locList!=null) {
		 	for(Location l : locList) {
		 		locSet.add(l);
			 }
		 }
		 
		 EncounterType eType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
		 List<EncounterType> typeList = new ArrayList<EncounterType> ();
		 typeList.add(eType);
		 
		 List<Encounter> labTestList = Context.getEncounterService().getEncounters(null, null, startDate, endDate, null, typeList, null, false);
		 
		if(locList!=null) {
			ArrayList<Encounter> ret = new ArrayList<Encounter>();
			
			for(Encounter e : labTestList) {
				if(locSet.contains(e.getLocation())) {
				ret.add(e);
				}
			}
		 
			return ret;
		}

		else
			return labTestList;

	}

	@Override
	public String getXpertWithMicroscopy(Date startDate, Date endDate,List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept xpert = Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs xpert on xpert.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");		
		q.append(" and xpert.concept_id = '" + xpert.getConceptId() + "'");
		q.append(" inner join encounter e on e.encounter_id  = xpert.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getXpertWithoutMicroscopy(Date starDate, Date endDate,
			List<Location> locList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHain2ResistanceFlqAgE(Date startDate, Date endDate, List<Location> locList) {
		
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept flq = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		Concept ag = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		Concept emb = Context.getService(TbService.class).getConcept(TbConcepts.E);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs obs1 inner join obs obs2 on  ");
		
		q.append(" obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs2.concept_id = '" + flq.getConceptId() +"'");	
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");		
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		
		q.append(" inner join obs obs3 on obs2.encounter_id = obs3.encounter_id ");
		q.append(" and obs3.concept_id = '" + ag.getConceptId() +"'");	
		q.append(" and obs3.value_coded = '" + detected.getConceptId() + "'");	 		
		
		q.append(" inner join obs as obs4 on obs3.encounter_id = obs4.encounter_id ");				
		q.append(" and obs4.concept_id = '" + emb.getConceptId() +"'");	
		q.append(" and obs4.value_coded = '" + detected.getConceptId() + "'");	 		
		
		q.append(" where obs1.encounter_id IN (select encounter_id from encounter where encounter_type = ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId()  + "'" );
		if(!locList.isEmpty()){			
			q.append(" and encounter.location_id IN ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(" )");
		}
		q.append(" )");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}
	
	@Override
	public String getHain2IndefiniteFlqAgE(Date startDate, Date endDate, List<Location> locList) {
		
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept flq = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		Concept ag = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		Concept emb = Context.getService(TbService.class).getConcept(TbConcepts.E);
		Concept undetermined = Context.getService(TbService.class).getConcept(TbConcepts.UNDETERMINED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs obs1 inner join obs obs2 on  ");
		
		q.append(" obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs2.concept_id = '" + flq.getConceptId() +"'");	
		q.append(" and obs2.value_coded = '" + undetermined.getConceptId() + "'");		
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		
		q.append(" inner join obs obs3 on obs2.encounter_id = obs3.encounter_id ");
		q.append(" and obs3.concept_id = '" + ag.getConceptId() +"'");	
		q.append(" and obs3.value_coded = '" + undetermined.getConceptId() + "'");	 		
		
		q.append(" inner join obs as obs4 on obs3.encounter_id = obs4.encounter_id ");				
		q.append(" and obs4.concept_id = '" + emb.getConceptId() +"'");	
		q.append(" and obs4.value_coded = '" + undetermined.getConceptId() + "'");	 		
		
		q.append(" where obs1.encounter_id IN (select encounter_id from encounter where encounter_type = ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId()  + "'" );
		if(!locList.isEmpty()){			
			q.append(" and encounter.location_id IN ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(" )");
		}
		q.append(" )");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getHain2ResistanceFlqE(Date startDate, Date endDate, List<Location> locList) {
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept flq = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		Concept emb = Context.getService(TbService.class).getConcept(TbConcepts.E);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2, obs as obs3");
		q.append(" where obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id ");
		
//		q.append(" and obs2.obs_group_id = obs1.obs_id");
//		q.append(" and obs3.obs_group_id = obs1.obs_id");
		
		q.append(" and obs2.concept_id = '" + flq.getConceptId() +"'");	
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");	 		
		
		q.append(" and obs3.concept_id = '" + emb.getConceptId() +"'");	
		q.append(" and obs3.value_coded = '" + detected.getConceptId() + "'");	 				
		
		q.append(" and obs1.encounter_id IN (select encounter_id from encounter where encounter_type = ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId()  + "'" );
		if(!locList.isEmpty()){			
			q.append(" and encounter.location_id IN ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(" )");
		}
		q.append(" )");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}
	
	@Override
	public String getHain2ResistanceAgE(Date startDate, Date endDate, List<Location> locList) {
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept cm = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		Concept emb = Context.getService(TbService.class).getConcept(TbConcepts.E);
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(obs1.encounter_id)) from obs as obs1, obs as obs2, obs as obs3");
		q.append(" where obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id ");
		
//		q.append(" and obs2.obs_group_id = obs1.obs_id");
//		q.append(" and obs3.obs_group_id = obs1.obs_id");
		
		q.append(" and obs2.concept_id = '" + cm.getConceptId() +"'");	
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");	 		
		
		q.append(" and obs3.concept_id = '" + emb.getConceptId() +"'");	
		q.append(" and obs3.value_coded = '" + detected.getConceptId() + "'");	 				
		
		q.append(" and obs1.encounter_id IN (select encounter_id from encounter where encounter_type = ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId()  + "'" );
		if(!locList.isEmpty()){			
			q.append(" and encounter.location_id IN ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(" )");
		}
		q.append(" )");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}
	

	@Override
	public String getHain2ResistanceFlqAg(Date startDate, Date endDate, List<Location> locList) {
		
		Concept hain2 = Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT);
		Concept flq = Context.getService(TbService.class).getConcept(TbConcepts.MOX);
		Concept ag = Context.getService(TbService.class).getConcept(TbConcepts.CM);
		
		Concept detected = Context.getService(TbService.class).getConcept(TbConcepts.DETECTED);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(obs1.obs_id) from obs as obs1, obs as obs2, obs as obs3");
		q.append(" where obs1.concept_id = '" + hain2.getConceptId() + "'");
		q.append(" and obs1.encounter_id = obs2.encounter_id ");
		q.append(" and obs1.encounter_id = obs3.encounter_id ");
		q.append(" and obs1.voided = 0 ");
		
		q.append(" and obs2.concept_id = '" + flq.getConceptId() +"'");	
		q.append(" and obs2.value_coded = '" + detected.getConceptId() + "'");	 		
		q.append(" and obs2.voided = 0");
		
		q.append(" and obs3.concept_id = '" + ag.getConceptId() +"'");	
		q.append(" and obs3.value_coded = '" + detected.getConceptId() + "'");
		q.append(" and obs3.voided = 0");
		
		q.append(" and obs1.encounter_id IN (select encounter_id from encounter where encounter_type = ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId()  + "'" );		
		q.append(" and encounter.voided = 0");
		if(!locList.isEmpty()){			
			q.append(" and encounter.location_id IN ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(" )");
		}
		q.append(" )");
		
		if (startDate != null) {
			q.append(" and	obs1.obs_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	obs1.obs_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getCultureWithMicroscopy(Date startDate, Date endDate,Concept cultureType, Concept cultureResult, List<Location> locList) {
		
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept culture = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT);
		Concept mgitResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		
		
		Concept microscopyPositive = Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE);
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs culture on culture.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.voided = 0");
		q.append(" and culture.concept_id = '" + cultureType.getConceptId() + "'");
		q.append(" and culture.value_coded = '" + cultureResult.getConceptId() + "'");
		q.append(" and culture.voided = 0 ");
		q.append(" and culture.encounter_id = mic.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = culture.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" and e.voided = 0 ");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getCultureWithMicroscopyResult(Date startDate, Date endDate,Concept cultureType,Concept cultureResult, Concept micResult, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
			
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs culture on culture.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded = '" + micResult.getConceptId() + "'");
		q.append(" and mic.voided = 0 ");		
		q.append(" and culture.concept_id = '" + cultureType.getConceptId() + "'");
		q.append(" and culture.value_coded = '" + cultureResult.getConceptId() + "'");
		q.append(" and culture.voided = 0 ");
		q.append(" and culture.encounter_id = mic.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = culture.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" and e.voided = 0 ");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}

	}

	@Override
	public String getCultureWithMicroscopyResultPositive(Date startDate,Date endDate, Concept cultureType,Concept cultureResult,List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs culture on culture.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded not in ('" + microscopyNegative.getConceptId() + "', '" + microscopyTestNotDone.getConceptId() + "')");
		q.append(" and mic.voided = 0");
		q.append(" and culture.concept_id = '" + cultureType.getConceptId() + "'");
		q.append(" and culture.value_coded = '" + cultureResult.getConceptId() + "'");
		q.append(" and culture.voided = 0");		
		q.append(" and culture.encounter_id = mic.encounter_id");
		q.append(" inner join encounter e on e.encounter_id  = culture.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" and e.voided = 0 ");
		
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getDiagnosticCultureWithMicroscopy(Date startDate,
			Date endDate, Concept cultureType, Concept cultureResult,
			List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept mgitResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs culture on culture.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.voided = 0");
		q.append(" and culture.concept_id = '" + cultureType.getConceptId() + "'");
		q.append(" and culture.value_coded = '" + cultureResult.getConceptId() + "'");
		q.append(" and culture.voided = 0 ");
		q.append(" and culture.encounter_id = mic.encounter_id");
		q.append(" inner join obs o on ");
		q.append(" o.encounter_id = culture.encounter_id and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) +" ");
		q.append(" and (o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA)  + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");			
		q.append(" and o.voided = 0 ");
		q.append(" inner join encounter e on e.encounter_id  = culture.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" and e.voided = 0 ");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getDiagnosticCultureWithMicroscopyResult(Date startDate,
			Date endDate, Concept cultureType, Concept cultureResult,
			Concept micResult, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept mgitResult = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_CULTURE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs culture on culture.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded = '" + micResult.getConceptId() + "'");
		q.append(" and mic.voided = 0 ");		
		q.append(" and culture.concept_id = '" + cultureType.getConceptId() + "'");
		q.append(" and culture.value_coded = '" + cultureResult.getConceptId() + "'");
		q.append(" and culture.voided = 0 ");
		q.append(" and culture.encounter_id = mic.encounter_id");
		q.append(" inner join obs o on ");
		q.append(" o.encounter_id = culture.encounter_id and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) +" ");
		q.append(" and (o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA)  + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");			
		q.append(" and o.voided = 0 ");
		q.append(" inner join encounter e on e.encounter_id  = culture.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" and e.voided = 0 ");
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	@Override
	public String getDiagnosticCultureWithMicroscopyResultPositive(
			Date startDate, Date endDate, Concept cultureType,
			Concept cultureResult, List<Location> locList) {
		Concept microscopyResult = Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT);
		Concept microscopyNegative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
		Concept microscopyTestNotDone = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE);
		
		StringBuilder q = new StringBuilder();
		q.append("select count(distinct(mic.encounter_id)) from obs mic inner join obs culture on culture.encounter_id  = mic.encounter_id");
		q.append(" and mic.concept_id = '" + microscopyResult.getConceptId() + "'");
		q.append(" and mic.value_coded not in ('" + microscopyNegative.getConceptId() + "', '" + microscopyTestNotDone.getConceptId() + "')");
		q.append(" and mic.voided = 0");
		q.append(" and culture.concept_id = '" + cultureType.getConceptId() + "'");
		q.append(" and culture.value_coded = '" + cultureResult.getConceptId() + "'");
		q.append(" and culture.voided = 0");		
		q.append(" and culture.encounter_id = mic.encounter_id");
		q.append(" inner join obs o on ");
		q.append(" o.encounter_id = culture.encounter_id and o.concept_id = "+ Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE) +" ");
		q.append(" and (o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_NA)  + " or o.value_coded = " + Context.getService(TbService.class).getConcept(TbConcepts.DIAGNOSTICS_REPEATED) + ") ");			
		q.append(" and o.voided = 0 ");
		q.append(" inner join encounter e on e.encounter_id  = culture.encounter_id");
		q.append(" and e.encounter_type= ' "  + Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")).getId() + "'");
		q.append(" and e.voided = 0 ");
		
		if(!locList.isEmpty()){			
			q.append(" and e.location_id in ( ");
			
			for(int i = 0; i<locList.size(); i++){
				q.append(locList.get(i).getLocationId());				
				if(i+1 < locList.size())
				{
					q.append(",");
				}
			}
			q.append(")");
		}
		if (startDate != null) {
			q.append(" and	e.encounter_datetime > '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append(" and	e.encounter_datetime < '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		
		List<List<Object>> result = Context.getAdministrationService().executeSQL(q.toString(), true);
		if(result.size()==1)
		{
			List<Object>values = result.get(0);
			String r = values.get(0).toString();
			return r;
		}
		else
		{
			return "0";
		}
	}

	public List<Facility> getFacilities() {
		
		List<Facility> facilityList = new ArrayList<Facility>();
		try{
		List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 6", true);
		for (List<Object> temp : result) {
			Integer id = 0;
			String name = "";
	        for (int i = 0; i < temp.size(); i++) {
	        	Object value = temp.get(i);
	            if (value != null) {
	            	
	            	if(i == 0)
	            		id = (Integer) value;
	            	else if (i == 1)
	            		name = (String) value;
	            }
	        }
	        facilityList.add(new Facility(name, id));
	    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			return facilityList;			
		}
	}

	public List<District> getDistricts() {
		List<District> districtList = new ArrayList<District>();
		
		try {
			List<List<Object>> result = Context.getAdministrationService().executeSQL("Select address_hierarchy_entry_id, name from address_hierarchy_entry where level_id = 3", true);
			for (List<Object> temp : result) {
				Integer id = 0;
				String name = "";
			    for (int i = 0; i < temp.size(); i++) {
			    	Object value = temp.get(i);
			        if (value != null) {
			        	
			        	if(i == 0)
			        		id = (Integer) value;
			        	else if (i == 1)
			        		name = (String) value;
			        }
			    }
			    districtList.add(new District(name, id));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			return districtList;			
		}
		
	}


}
	

