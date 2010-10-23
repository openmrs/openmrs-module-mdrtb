package org.openmrs.module.mdrtb;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.springframework.util.StringUtils;

public class MdrtbCohortUtil {
    
	/**
	 * Utility method to return patients matching passed criteria
	 * @return Cohort
	 */
	public static Cohort getMdrPatients(String identifier, String name, String enrollment, List<Location> locations,
										PatientLocationMethod locationMethod, List<ProgramWorkflowState> states) {
		
		Cohort cohort = Context.getPatientSetService().getAllPatients();
		
		MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
        MdrtbFactory mu = ms.getMdrtbFactory();
		Date now = new Date();
		Program mdrtbProgram = mu.getMDRTBProgram();
		
		if ("current".equals(enrollment)) {
			Cohort current = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, now, now);
			cohort = Cohort.intersect(cohort, current);
		}
		else {
			Cohort ever = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, null, null);
			if ("previous".equals(enrollment)) {
				Cohort current = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, now, now);
				Cohort previous = Cohort.subtract(ever, current);
				cohort = Cohort.intersect(cohort, previous);   			
			}
			else if ("never".equals(enrollment)) {
				cohort = Cohort.subtract(cohort, ever);
			}
			else {
				cohort = Cohort.intersect(cohort, ever);
			}	
		}
		
		if (StringUtils.hasText(name) || StringUtils.hasText(identifier)) {
			name = "".equals(name) ? null : name;
			identifier = "".equals(identifier) ? null : identifier;
			Cohort nameIdMatches = new Cohort(Context.getPatientService().getPatients(name, identifier, null, false));
			cohort = Cohort.intersect(cohort, nameIdMatches);
		}
		
		if (locations != null) {
			
			if (locationMethod == null) {
				locationMethod = PatientLocationMethod.PATIENT_HEALTH_CENTER;
			}
			Map<Location, Cohort> locationCache = new HashMap<Location, Cohort>();
			String unknownLocationName = Context.getAdministrationService().getGlobalProperty("mdrtb.unknownLocationName");
			
			Set<Integer> startingLocationMemberIds = cohort.getMemberIds();
			Cohort locationCohort = new Cohort();
			for (Location l : locations) {
				locationCohort = Cohort.union(locationCohort, getLocationCohort(l, locationMethod, locationCache));
				if (l.getName().equals(unknownLocationName)) {
					Cohort noLocations = new Cohort(startingLocationMemberIds);
					for (Location ul : Context.getLocationService().getAllLocations()) {
						noLocations = Cohort.subtract(noLocations, getLocationCohort(ul, locationMethod, locationCache));
					}
					locationCohort = Cohort.union(locationCohort, noLocations);
				}
			}
			
			cohort = Cohort.intersect(cohort, locationCohort);
		}
		
		if (states != null) {
			Cohort inStates = Context.getPatientSetService().getPatientsByProgramAndState(null, states, now, now);
			cohort = Cohort.intersect(cohort, inStates);
		}
		
		return cohort;
	}
	
    /**
     * Utility method to return a list of patients at a given location
     */
    public static Cohort getLocationCohort(Location location, PatientLocationMethod locationMethod, Map<Location, Cohort> cache) {
    	if (cache.containsKey(location)) {
    		return cache.get(location);
    	}
    	Cohort c = Context.getPatientSetService().getPatientsHavingLocation(location, locationMethod);
    	cache.put(location, c);
    	return c;
    }
}
