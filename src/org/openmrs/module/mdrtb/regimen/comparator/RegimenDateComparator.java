package org.openmrs.module.mdrtb.regimen.comparator;

import java.util.Comparator;
import java.util.Date;

import org.openmrs.module.mdrtb.regimen.Regimen;

/**
 * Comparator used for comparing and sorting Regimens by start date
 */
public class RegimenDateComparator implements Comparator<Regimen> {

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Regimen r1, Regimen r2) {
		Date d1 = (r1 == null ? null : r1.getStartDate());
		Date d2 = (r2 == null ? null : r2.getStartDate());
		if (d1 != null && d2 != null) {
			return d1.compareTo(d2);
		}
		if (d1 == null) {
			return -1;
		}
		return 1;
	}
}
