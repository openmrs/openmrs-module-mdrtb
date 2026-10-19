package org.openmrs.module.mdrtb.specimen;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class SpecimenUtil {
	
	public static void groupSpecimensByDay(List<Specimen> specimens) {
		
		if (specimens == null || specimens.isEmpty()) {
			return;
		}
		
		// first make sure the specimens are in order
		Collections.sort(specimens);
		
		ListIterator<Specimen> i = specimens.listIterator();
		
		SpecimenGroupImpl specimenGroup = null;
		
		while (i.hasNext()) {
			specimenGroup = groupSpecimensByDayHelper(i, null);
			
			// if we've gotten a specimen group back, add it
			if (specimenGroup != null) {
				i.add(specimenGroup);
			}
		}
	}
	
	private static SpecimenGroupImpl groupSpecimensByDayHelper(ListIterator<Specimen> i, SpecimenGroupImpl specimenGroup) {
		// compare if the next two methods in the the list are equal, if they are, we need to merge them
		Specimen specimen = i.next();
		if (i.hasNext()) {
			if (specimen.getDateCollected().equals(i.next().getDateCollected())) {
				
				// create the specimen group if need be
				if (specimenGroup == null) {
					specimenGroup = new SpecimenGroupImpl();
				}
				
				// step back and make the recursive call to check the next set
				i.previous();
				specimenGroup = groupSpecimensByDayHelper(i, specimenGroup);
			} else {
				i.previous();
			}
		}
		
		// if the specimen group have been created, it means we have some merging to perform
		if (specimenGroup != null) {
			specimenGroup.addSpecimen(i.previous());
			i.remove();
		}
		
		return specimenGroup;
	}
	
}
